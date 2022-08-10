package org.twogate.plugins.embeddedwebview

import com.getcapacitor.JSObject

class EmbeddedWebviewConfigurationStyles(width: Int, height: Int)  {
    val width: Int
    val height: Int

    init {
        this.width = width
        this.height = height
    }
}

class EmbeddedWebViewConfiguration (configuration: JSObject) {
    val styles: EmbeddedWebviewConfigurationStyles
    val globalVaribles: JSObject?
    val enableCooklie: Boolean?
    val css: String?

    init {
        val rawStyles = configuration.getJSObject("styles") ?: throw Exception("configuration.styles is undefined")
        val width = rawStyles.getInteger("width") ?: throw Exception("configuration.styles.width is undefined")
        val height = rawStyles.getInteger("height") ?: throw Exception("configuration.styles.height is undefined")
        val styles = EmbeddedWebviewConfigurationStyles(width, height)
        this.styles = styles

        this.globalVaribles = configuration.getJSObject("global")
        this.enableCooklie = configuration.getBool("enableCookie")
        this.css = configuration.getString("css")
    }
}