package org.twogate.plugins.embeddedwebview

import android.content.res.Resources
import android.util.Log
import android.view.View
import android.webkit.WebView
import android.widget.LinearLayout

import com.getcapacitor.annotation.CapacitorPlugin
import com.getcapacitor.PluginMethod
import com.getcapacitor.PluginCall
import com.getcapacitor.JSObject
import com.getcapacitor.Plugin

import org.twogate.plugins.embeddedwebview.EmbeddedWebview

@CapacitorPlugin(name = "EmbeddedWebview")
class EmbeddedWebviewPlugin : Plugin() {
    private lateinit var webView: WebView
    private val implementation = EmbeddedWebview()

    @PluginMethod
    fun echo(call: PluginCall) {
        val value = call.getString("value")
        val ret = JSObject()
        Log.i("Echo", value!!)
        ret.put("value", value)
        call.resolve(ret)
    }

    @PluginMethod
    fun create(call: PluginCall) {
        val url = call.getString("url")
        if (url == null) {
            call.reject("url is undefined")
            return
        }

        fun createWebView(url: String): WebView {
            val webView = WebView(getBridge().context)

            val displayMetrics = Resources.getSystem().displayMetrics
            val width = displayMetrics.widthPixels
            val height = displayMetrics.heightPixels - (80 * displayMetrics.density)
            val viewLayoutParams = LinearLayout.LayoutParams(width.toInt(), height.toInt())
            webView.layoutParams = viewLayoutParams

            webView.settings.domStorageEnabled = true
            webView.settings.javaScriptEnabled = true
            webView.settings.javaScriptCanOpenWindowsAutomatically = true

            webView.webViewClient = EmbeddedWebviewClient()

            webView.loadUrl(url)

            return webView
        }

        activity.runOnUiThread(Runnable {
            // Note: initialize webView here
            this.webView = createWebView(url)
            activity.addContentView(this.webView, this.webView.layoutParams)

            call.resolve()
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
        })
    }
}