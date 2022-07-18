package org.twogate.plugins.embeddedwebview

import android.net.http.SslError
import android.util.Log
import android.webkit.SslErrorHandler
import android.webkit.WebView
import android.webkit.WebViewClient

class EmbeddedWebviewClient : WebViewClient() {
    override fun shouldOverrideUrlLoading(view: WebView, url: String?): Boolean {
        view.loadUrl(url!!)
        return true
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