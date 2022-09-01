package org.twogate.plugins.embeddedwebview

import android.content.Context
import android.content.res.Resources
import android.graphics.Rect
import android.view.Window
import android.webkit.WebView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import kotlin.properties.Delegates

class EmbeddedWebView: WebView {
    val keyboardListener: KeyboardListener

    constructor(context: Context, activity: AppCompatActivity) : super(context) {
        this.keyboardListener = KeyboardListener(activity, this)
        keyboardListener.listen()
    }
}

class EmbeddedWebViewContainer {
    val webView: EmbeddedWebView
    val configuration: EmbeddedWebViewConfiguration
    val activity: AppCompatActivity

    fun layoutParams(): LinearLayout.LayoutParams {
        val density = Resources.getSystem().displayMetrics.density

        val width = (this.configuration.styles.width * density).toInt()
        val height = ((this.configuration.styles.height) * density).toInt()
        return LinearLayout.LayoutParams(width, height)
    }

    constructor(
        activity: AppCompatActivity,
        context: Context,
        configuration: EmbeddedWebViewConfiguration,
    ) {
        this.activity = activity
        this.configuration = configuration

        webView = EmbeddedWebView(context, activity)

        webView.settings.domStorageEnabled = true
        webView.settings.javaScriptEnabled = true
        webView.settings.javaScriptCanOpenWindowsAutomatically = true

        webView.layoutParams = this.layoutParams()
    }
}
