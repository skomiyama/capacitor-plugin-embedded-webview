package org.twogate.plugins.embeddedwebview

import android.net.http.SslError
import android.webkit.SslErrorHandler
import android.webkit.WebView
import android.webkit.WebViewClient
import com.getcapacitor.JSObject

class EmbeddedWebviewClient(globalVariables: JSObject?) : WebViewClient() {
    private val globalVariables: JSObject?

    init {
        this.globalVariables = globalVariables
    }

    override fun shouldOverrideUrlLoading(view: WebView, url: String?): Boolean {
        view.loadUrl(url!!)
        return true
    }

    override fun onPageFinished(view: WebView?, url: String?) {
        super.onPageFinished(view, url)
        if (this.globalVariables != null) {
            view?.evaluateJavascript("window.embedded_webview = ${this.globalVariables.toString()}", null)
        }
    }

    override fun onReceivedSslError(view: WebView?, handler: SslErrorHandler?, error: SslError?) {
        super.onReceivedSslError(view, handler, error)
        if (handler !== null) {
            handler.proceed()
        }
    }
}