package org.twogate.plugins.embeddedwebview

import android.content.res.Configuration
import android.content.res.Resources
import android.util.Log
import android.view.View
import android.webkit.WebView
import android.widget.LinearLayout
import org.json.JSONObject

import com.getcapacitor.annotation.CapacitorPlugin
import com.getcapacitor.PluginMethod
import com.getcapacitor.PluginCall
import com.getcapacitor.JSObject
import com.getcapacitor.Plugin

import org.twogate.plugins.embeddedwebview.EmbeddedWebview

@CapacitorPlugin(name = "EmbeddedWebview")
class EmbeddedWebviewPlugin : Plugin() {
    private lateinit var webView: WebView

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
            this.webView = EmbeddedWebview(getBridge().context, configuration).webView
            this.webView.loadUrl(url)

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