package org.twogate.plugins.embeddedwebview

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.util.Log
import android.view.Gravity
import android.view.View
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.widget.Button
import android.widget.LinearLayout
import android.widget.RelativeLayout
import com.getcapacitor.JSObject
import com.getcapacitor.Plugin
import com.getcapacitor.PluginCall
import com.getcapacitor.PluginMethod
import com.getcapacitor.annotation.CapacitorPlugin
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson



@CapacitorPlugin(name = "EmbeddedWebView")
class EmbeddedWebViewPlugin : Plugin() {
    private lateinit var webView: WebView
    private lateinit var containerLayout: RelativeLayout

    private class JSEventListener(
        private val activity: Activity,
        private val context: Context,
        private val webView: WebView
    ) {
        private val defaultLayoutParams: RelativeLayout.LayoutParams = RelativeLayout.LayoutParams(this.webView.layoutParams)

        data class EmbeddedWebViewContentAlertAction (
            val title: String,
            val value: String,
            val role: Int
        )
        data class EmbeddedWebViewContentAlertOptions (
            val title: String,
            val message: String,
            val actions: Array<EmbeddedWebViewContentAlertAction>,
            val name: String
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


        @JavascriptInterface
        fun showAlert(data: String?) {
            val options = Gson().fromJson(data, EmbeddedWebViewContentAlertOptions::class.java)
            val alert = AlertDialog.Builder(context)
            alert.setTitle(options.title)
            alert.setMessage(options.message)
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
            alert.show()

            Log.i("EmbeddedWebView", "Log Title of Options ")
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
                layout.addView(button)
            }

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

    @PluginMethod
    fun create(call: PluginCall) {
        val url = call.getString("url")
        if (url == null) {
            call.reject("url is undefined.")
            return
        }

        val rawConfiguration = call.getObject("configuration")
        if (rawConfiguration == null) {
            call.reject("configuration is undefined.")
            return
        }

        val configuration: EmbeddedWebViewConfiguration = try {
            EmbeddedWebViewConfiguration(rawConfiguration)
        } catch(exception: Exception) {
            call.reject(exception.message)
            return
        }

        activity.runOnUiThread(Runnable {
            // Note: initialize webView here
            this.webView = EmbeddedWebView(getBridge().context, configuration).webView
            this.webView.loadUrl(url)

            print("webView Initialized")
            this.webView.addJavascriptInterface(JSEventListener(activity, this.webView.context, this.webView), "AndroidWebView")

            this.containerLayout = RelativeLayout(getBridge().context)
            this.containerLayout.layoutParams =  RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
            )
            this.containerLayout.addView(this.webView)

            activity.addContentView(this.containerLayout, this.containerLayout.layoutParams)

            call.resolve()
            return@Runnable
        })
    }

    @PluginMethod
    fun show(call: PluginCall) {
        activity.runOnUiThread(Runnable {
            if (!this::webView.isInitialized) {
                call.resolve(null)
                return@Runnable
            }

            this.webView.visibility = View.VISIBLE

//            val layoutParams: RelativeLayout.LayoutParams = RelativeLayout.LayoutParams(
//                1080,
//                2000
//            )
//            this.webView.layoutParams = layoutParams

            val ret = JSObject()
            ret.put("visibility", true)
            call.resolve(ret)
        })
    }

    @PluginMethod
    fun hide(call: PluginCall) {
        activity.runOnUiThread(Runnable {
            if (!this::webView.isInitialized) {
                call.resolve(null)
                return@Runnable
            }

            this.webView.visibility = View.INVISIBLE

            val ret = JSObject()
            ret.put("visibility", false)
            call.resolve(ret)
        })
    }

    @PluginMethod
    fun destroy(call: PluginCall) {
        activity.runOnUiThread(Runnable {
            this.webView.destroy()
            call.resolve()
            return@Runnable
        })
    }
}