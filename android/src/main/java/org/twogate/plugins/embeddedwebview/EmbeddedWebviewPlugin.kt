package org.twogate.plugins.embeddedwebview

import android.view.View
import android.webkit.WebView
import android.widget.RelativeLayout
import com.getcapacitor.JSObject
import com.getcapacitor.Plugin
import com.getcapacitor.PluginCall
import com.getcapacitor.PluginMethod
import com.getcapacitor.annotation.CapacitorPlugin


@CapacitorPlugin(name = "EmbeddedWebView")
class EmbeddedWebViewPlugin : Plugin() {
    private lateinit var webView: WebView
    private lateinit var containerLayout: RelativeLayout

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

            this.webView.addJavascriptInterface(EmbeddedWebViewOverlay.JSEventListener(activity, this.webView.context, this.webView), "AndroidWebView")

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
}