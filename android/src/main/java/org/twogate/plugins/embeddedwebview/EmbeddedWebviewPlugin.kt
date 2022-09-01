package org.twogate.plugins.embeddedwebview

import android.content.Context
import android.content.res.Resources
import android.graphics.Rect
import android.inputmethodservice.Keyboard
import android.view.View
import android.view.ViewTreeObserver
import android.view.WindowManager
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.widget.FrameLayout
import android.widget.RelativeLayout
import com.getcapacitor.JSObject
import com.getcapacitor.Plugin
import com.getcapacitor.PluginCall
import com.getcapacitor.PluginMethod
import com.getcapacitor.annotation.CapacitorPlugin


@CapacitorPlugin(name = "EmbeddedWebView")
class EmbeddedWebViewPlugin : Plugin() {
    private lateinit var webView: EmbeddedWebView
    private lateinit var containerLayout: RelativeLayout
    private lateinit var jsListener: EmbeddedWebViewJSListener.JSEventListener

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
            this.webView = EmbeddedWebViewContainer(activity, getBridge().context, configuration).webView
            this.webView.webViewClient = EmbeddedWebViewClient(configuration, this.webView.keyboardListener)
            this.webView.loadUrl(url)   

            this.jsListener = EmbeddedWebViewJSListener.JSEventListener(activity, this.webView.context, this.webView)
            this.webView.addJavascriptInterface(this.jsListener, "AndroidWebView")

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

    @JavascriptInterface
    fun completedPushTo(call: PluginCall) {
        activity.runOnUiThread(Runnable {
            call.resolve()
        })
    }

    @PluginMethod
    fun pushTo(call: PluginCall) {
        activity.runOnUiThread( Runnable {
            if (this.webView == null) {
                call.reject("EmbeddedWebView is not initialized")
                return@Runnable
            }

            val path = call.getString("path")
            if (path == null) {
                call.reject("path is undefined")
                return@Runnable
            }

            val script = "window.dispatchEvent(new CustomEvent('embedded_content_navigation', { detail: { path: '$path' } } ))"
            this.webView.evaluateJavascript(script, null)

            this.jsListener.completedEventMethod = {
                call.resolve()
            }
            return@Runnable
        })
    }

}

