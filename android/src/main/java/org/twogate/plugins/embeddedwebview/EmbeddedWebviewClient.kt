package org.twogate.plugins.embeddedwebview

import android.net.http.SslError
import android.webkit.SslErrorHandler
import android.webkit.WebView
import android.webkit.WebViewClient

class EmbeddedWebViewClient(configuration: EmbeddedWebViewConfiguration?, listener: KeyboardListener) : WebViewClient() {
    private val configuration: EmbeddedWebViewConfiguration?
    private val keyboardListener: KeyboardListener

    init {
        this.configuration = configuration
        this.keyboardListener = listener
    }

    @Deprecated("Deprecated in Java")
    override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
        view?.loadUrl(url!!)
        return true
    }

    override fun onPageFinished(view: WebView?, url: String?) {
        super.onPageFinished(view, url)
        if (this.configuration != null) {
            view?.evaluateJavascript("window.embedded_webview = ${this.configuration.globalVaribles.toString()}", null)
            view?.evaluateJavascript("document.documentElement.style.setProperty('--embedded-content-height', '${this.configuration.styles.height}px')", null)
        }
    }

    override fun onReceivedSslError(view: WebView?, handler: SslErrorHandler?, error: SslError?) {
        super.onReceivedSslError(view, handler, error)
        if (handler !== null) {
            handler.proceed()
        }
    }
}