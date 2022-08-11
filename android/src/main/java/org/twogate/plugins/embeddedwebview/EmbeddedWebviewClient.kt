package org.twogate.plugins.embeddedwebview

import android.net.http.SslError
import android.util.Log
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
        Log.i("EmbeddedWebView", "onPageFinished()")
        if (this.globalVariables != null) {
            view?.evaluateJavascript("window.embedded_webview = ${this.globalVariables.toString()}", null)
        }
    }

    override fun onReceivedSslError(view: WebView?, handler: SslErrorHandler?, error: SslError?) {
        super.onReceivedSslError(view, handler, error)
        Log.i("EmbeddedWebview", "onReceivedSssError")
        if (handler !== null) {
            Log.i("EmbeddedWebview", "handler exists")
            handler.proceed()
        }
    }
}