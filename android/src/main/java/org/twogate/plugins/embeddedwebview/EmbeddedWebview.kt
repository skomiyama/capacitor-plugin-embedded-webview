package org.twogate.plugins.embeddedwebview

import android.content.Context
import android.content.res.Resources
import android.util.Log
import android.webkit.WebView
import android.widget.LinearLayout
import org.json.JSONObject

import com.getcapacitor.JSObject

class EmbeddedWebview {
    val webView: WebView
    val configuration: EmbeddedWebViewConfiguration

    fun layoutParams(): LinearLayout.LayoutParams {
        val density = Resources.getSystem().displayMetrics.density

        val width = (this.configuration.styles.width.toInt() * density).toInt()
        val height = (this.configuration.styles.height.toInt() * density).toInt()
        return LinearLayout.LayoutParams(width, height)
    }

    constructor(context: Context, configuration: EmbeddedWebViewConfiguration) {
        this.configuration = configuration

        webView = WebView(context)

        webView.settings.domStorageEnabled = true
        webView.settings.javaScriptEnabled = true
        webView.settings.javaScriptCanOpenWindowsAutomatically = true

        webView.webViewClient = EmbeddedWebviewClient(configuration.globalVaribles)

        webView.layoutParams = this.layoutParams()
    }
}
