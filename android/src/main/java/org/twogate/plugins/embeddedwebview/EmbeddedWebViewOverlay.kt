package org.twogate.plugins.embeddedwebview

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.res.Resources
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.widget.Button
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson

class EmbeddedWebViewOverlay {
    data class EmbeddedWebViewAlertThemeValue(
        val background: String,
        val text: String
    )
    data class EmbeddedWebViewAlertThemeText(val text: String)
    data class EmbeddedWebViewAlertActionTheme(
        val cancel: EmbeddedWebViewAlertThemeValue,
        val default: EmbeddedWebViewAlertThemeText,
        val destructive: EmbeddedWebViewAlertThemeText,
    )
    data class EmbeddedWebViewAlertTheme (
        val view: EmbeddedWebViewAlertThemeValue,
        val action: EmbeddedWebViewAlertActionTheme
    )
    data class EmbeddedWebViewContentAlertAction (
        val title: String,
        val value: String,
        val role: Int
    )
    data class EmbeddedWebViewContentAlertOptions (
        val title: String,
        val message: String,
        val actions: Array<EmbeddedWebViewContentAlertAction>,
        val name: String,
        val theme: EmbeddedWebViewAlertTheme
    ) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as EmbeddedWebViewContentAlertOptions

            if (!actions.contentEquals(other.actions)) return false

            return true
        }

        override fun hashCode(): Int {
            return actions.contentHashCode()
        }
    }
    enum class EmbeddedWebViewContentAlertActionRole(val v: Int) {
        Default(0),
        Cancel(1),
        Destructive(2);
        companion object {
            fun valueOf(value: Int): EmbeddedWebViewContentAlertActionRole? = values().find { it.v == value }
        }
    }

    class JSEventListener(
        private val activity: Activity,
        private val context: Context,
        private val webView: WebView
    ) {
        private val defaultLayoutParams: RelativeLayout.LayoutParams = RelativeLayout.LayoutParams(this.webView.layoutParams)


        private fun generateButton(context: Context, text: String, role: EmbeddedWebViewContentAlertActionRole?, handler: () -> Any): Button {
            val barButton = Button(context)
            var verticalPadding = 20
            if (role != null && role == EmbeddedWebViewContentAlertActionRole.Cancel) {
                verticalPadding = 24
            }
            barButton.setPadding(20, verticalPadding, 20, verticalPadding)
            barButton.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
            barButton.gravity = Gravity.LEFT or Gravity.CENTER_VERTICAL
            barButton.text = text
            barButton.stateListAnimator = null
            barButton.setBackgroundColor(Color.TRANSPARENT)
            barButton.setTextColor(Color.parseColor("#111111"))
            barButton.setBackgroundResource(R.drawable.ripple)
            barButton.includeFontPadding = false

            val listener = View.OnClickListener { handler() }
            barButton.setOnClickListener(listener)

            return barButton
        }

        private fun generateLayout(
            orientation: Int = LinearLayout.VERTICAL,
            gravity: Int = Gravity.NO_GRAVITY
        ): LinearLayout {
            val layout = LinearLayout(this.context)
            layout.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            val shape = GradientDrawable()
            shape.cornerRadius = 12.0F
            shape.setColor(Color.WHITE)
            layout.background = shape
            layout.gravity = gravity
            layout.orientation = orientation
            return layout
        }


        fun createDialogTextView(text: String, textColor: String, fontSize: Float): TextView {
            val textView = TextView(context)
            textView.setText(text)
            textView.setTextColor(Color.parseColor(textColor))
            textView.setTextSize(fontSize)
            textView.setTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL))
            textView.gravity = Gravity.CENTER_VERTICAL or Gravity.LEFT
            return textView
        }

        @JavascriptInterface
        fun showAlert(data: String?) {
            val options = Gson().fromJson(data, EmbeddedWebViewContentAlertOptions::class.java)
            val alert = AlertDialog.Builder(context, R.style.EmbeddedContentAlertDialogStyle)

            val typedValue = TypedValue()
            context.theme.resolveAttribute(R.attr.dialogPreferredPadding, typedValue, true)
            val dialogPreferredPadding = TypedValue.complexToDimensionPixelSize(typedValue.data, context.resources.displayMetrics)

            val typedShapeValue = TypedValue()
            context.theme.resolveAttribute(R.attr.shapeAppearanceMediumComponent, typedShapeValue, true)
            println(typedShapeValue.data);

            val layout = LinearLayout(context)
            layout.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            layout.setPadding(dialogPreferredPadding, dialogPreferredPadding, dialogPreferredPadding, 0)
            layout.orientation = LinearLayout.VERTICAL

            val title = createDialogTextView(options.title,  options.theme.view.text,  20.0F,)
            title.setPadding(0, 0, 0,dialogPreferredPadding)
            val message = createDialogTextView(options.message,  options.theme.view.text,  16.0F)
            message.setPadding(0, 0, 0,dialogPreferredPadding)
            layout.addView(title)
            layout.addView(message)

            alert.setView(layout)

            for(action in options.actions) {
                val listener = DialogInterface.OnClickListener { _, _ ->
                    this.activity.runOnUiThread(Runnable {
                        this.webView.evaluateJavascript("window.dispatchEvent(new CustomEvent('on_did_dismiss_${options.name}', { detail: '${action.value}' }));", null)
                        return@Runnable
                    })
                }
                if (action.role > 0) {
                    alert.setNegativeButton(action.title, listener)
                } else {
                    alert.setPositiveButton(action.title, listener)
                }
            }

            val dialog = alert.create()
            dialog.show()
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.parseColor(options.theme.view.background)))
            for(action in options.actions) {
                when (EmbeddedWebViewContentAlertActionRole.valueOf(action.role)) {
                    EmbeddedWebViewContentAlertActionRole.Cancel -> {
                        val negativeButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                        negativeButton.setTextColor(Color.parseColor(options.theme.action.cancel.text))
                    }
                    EmbeddedWebViewContentAlertActionRole.Destructive -> {
                        val negativeButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                        negativeButton.setTextColor(Color.parseColor(options.theme.action.destructive.text))
                    }
                    EmbeddedWebViewContentAlertActionRole.Default -> {
                        val negativeButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                        negativeButton.setTextColor(Color.parseColor(options.theme.action.default.text))
                    }
                    else -> {}
                }
            }
        }


        @JavascriptInterface
        fun showActionSheet(data: String) {
            val options = Gson().fromJson(data, EmbeddedWebViewContentAlertOptions::class.java)

            val bottomSheetDialog = BottomSheetDialog(this.context)

            val layout = this.generateLayout()

            for (action in options.actions) {
                val button: Button = this.generateButton(
                    layout.context,
                    action.title,
                    EmbeddedWebViewContentAlertActionRole.valueOf(action.role)
                ) {
                    this.activity.runOnUiThread(Runnable {
                        this.webView.evaluateJavascript(
                            "window.dispatchEvent(new CustomEvent('on_did_dismiss_${options.name}', { detail: '${action.value}' }));",
                            null
                        )
                        bottomSheetDialog.dismiss()
                        return@Runnable
                    })
                }
                when (EmbeddedWebViewContentAlertActionRole.valueOf(action.role)) {
                    EmbeddedWebViewContentAlertActionRole.Cancel -> {
                        button.setBackgroundColor(Color.parseColor(options.theme.action.cancel.background))
                        button.setTextColor(Color.parseColor(options.theme.action.cancel.text))
                    }
                    EmbeddedWebViewContentAlertActionRole.Destructive -> {
                        button.setTextColor(Color.parseColor(options.theme.action.destructive.text))
                    }
                    EmbeddedWebViewContentAlertActionRole.Default -> {
                        button.setTextColor(Color.parseColor(options.theme.action.default.text))
                    }
                    else -> {}
                }
                layout.addView(button)
            }
            layout.setBackgroundColor(Color.parseColor(options.theme.view.background))

            bottomSheetDialog.setContentView(layout)
            bottomSheetDialog.show()
        }

        @JavascriptInterface
        fun showModal(data: String) {
            this.activity.runOnUiThread( Runnable {
                val width = Resources.getSystem().displayMetrics.widthPixels
                val height = Resources.getSystem().displayMetrics.heightPixels
                this.webView.layoutParams = RelativeLayout.LayoutParams(width, height)
                return@Runnable
            })
        }

        @JavascriptInterface
        fun dismissModal(data: String) {
            this.activity.runOnUiThread(Runnable {
                this.webView.layoutParams = this.defaultLayoutParams
                return@Runnable
            })
        }
    }
}