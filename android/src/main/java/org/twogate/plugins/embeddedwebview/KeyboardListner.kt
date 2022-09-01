package org.twogate.plugins.embeddedwebview

import android.content.res.Resources
import android.graphics.Rect
import android.view.View
import android.view.ViewTreeObserver
import android.view.WindowManager
import android.view.WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
import android.webkit.WebView
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import kotlin.properties.Delegates

class KeyboardListener(activity: AppCompatActivity, webView: EmbeddedWebView) {
    enum class ScrollBehaviour(val value: Int) {
        ScrollUp(0), None(1);
        companion object {
            fun fromInt(value: Int) = ScrollBehaviour.values().first { it.value == value }
        }
    }

    private val activity: AppCompatActivity
    private val webView: EmbeddedWebView
    private var defaultWebViewHeight by Delegates.notNull<Int>()
    private val rootView: View
    private var previousVisibilityHeight: Int
    private var scrollBehaviour: ScrollBehaviour
    private val defaultSoftInputMode: Int

    init {
        this.activity = activity
        this.rootView = activity.window.decorView.findViewById<FrameLayout>(android.R.id.content).rootView
        previousVisibilityHeight = activity.window.decorView.height
        this.webView = webView

        this.defaultSoftInputMode = activity.window.attributes.softInputMode

        val webViewOnLayoutChangeListener = View.OnLayoutChangeListener { view: View, _: Int, _: Int, _: Int, _: Int, _: Int, _: Int, _: Int, _: Int ->
            this.defaultWebViewHeight = view.height
        }
        this.webView.addOnLayoutChangeListener(webViewOnLayoutChangeListener)

        this.scrollBehaviour = ScrollBehaviour.ScrollUp
    }


    fun listen() {
        val density = Resources.getSystem().displayMetrics.density

        val globalLayoutListener = ViewTreeObserver.OnGlobalLayoutListener {
            activity.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)

            val rect = Rect()
            activity.window.decorView.getWindowVisibleDisplayFrame(rect)

            if (previousVisibilityHeight > rect.height()) {
                // show keyboard
                val visibilityHeightDiff = this.defaultWebViewHeight - rect.height()
                val script = "window.dispatchEvent(new CustomEvent('embedded_webview_keyboard_will_show', { detail: { keyboardHeight: { current: 0, next: ${(visibilityHeightDiff / density).toInt()} }, type: 'show'  }}));"
                webView.evaluateJavascript(script, null)
            }

            if (previousVisibilityHeight < rect.height()) {
                // hide keyboard
                val script = "window.dispatchEvent(new CustomEvent('embedded_webview_keyboard_will_hide', { detail: { keyboardHeight: { current: 0, next: 0 }, type: 'hide'  }}));"
                webView.evaluateJavascript(script, null)
            }
            previousVisibilityHeight = rect.height()
        }

        rootView.viewTreeObserver.addOnGlobalLayoutListener(globalLayoutListener)
    }
}