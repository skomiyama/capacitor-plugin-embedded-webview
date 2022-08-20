import Foundation
import Capacitor
import Combine

public struct EmbeddedWebviewConfigurationStyles {
    let height: Int;
    let width: Int;
}

public struct EmbeddedWebviewConfiguration {
    let styles: EmbeddedWebviewConfigurationStyles
    let enableCookie: Bool?
    let globalVariables: JSObject?
}

/**
 * Please read the Capacitor iOS Plugin Development Guide
 * here: https://capacitorjs.com/docs/plugins/ios
 */
@objc(EmbeddedWebviewPlugin)
public class EmbeddedWebviewPlugin: CAPPlugin {
    private var embeddedWebview: EmbeddedWebView!

    @objc func echo(_ call: CAPPluginCall) {
        let value = call.getString("value") ?? ""
        call.resolve(["value": value])
    }
    
    func embeddedWebViewConfiguration(call: CAPPluginCall) -> EmbeddedWebviewConfiguration? {
        // Fixme following nil check codes. Can I write in struct or class??
        guard let configuration = call.getObject("configuration") else {
            call.reject("[EmbeddedWebView] configuration is undefined.")
            return nil
        }
        guard let styles = configuration["styles"] as? JSObject else {
            call.reject("[EmbeddedWebView] configuration.styles is undefined.")
            return nil
        }
        guard let width = styles["width"] as? Int else {
            call.reject("[EmbeddedWebView] configuration.styles.width is undefined.")
            return nil
        }
        guard let height = styles["height"] as? Int else {
            call.reject("[EmbeddedWebView] configuration.styles.height is undefined.")
            return nil
        }

        let enableCookie = configuration["enableCookie"] as? Bool
        let globalVariables = configuration["global"] as? JSObject
        
        return EmbeddedWebviewConfiguration(
            styles: EmbeddedWebviewConfigurationStyles(height: height, width: width),
            enableCookie: enableCookie,
            globalVariables: globalVariables
        )
    }

    
    @objc func create(_ call: CAPPluginCall) -> Void {
        DispatchQueue.main.async {
            guard let capWebview = self.bridge?.webView else {
                call.reject("capacitor webview is null")
                return
            }
            guard let url = call.getString("url") else {
                call.reject("url is undefined")
                return
            }
            let path = call.getString("path")
            guard let configuration = self.embeddedWebViewConfiguration(call: call) else {
                call.reject("Failed to initialize configuration")
                return
            }

            self.embeddedWebview = EmbeddedWebView(url: url, configuration: configuration)
        
            capWebview.addSubview(self.embeddedWebview.view)
            
            self.embeddedWebview.create {
                if (path == nil) {
                    call.resolve()
                }
                self.embeddedWebview.pushTo(path: path!) {
                    call.resolve()
                }
            }
        }
    }
    
    @objc func destroy(_ call:CAPPluginCall) -> Void {
        DispatchQueue.main.async {
            guard (self.embeddedWebview != nil) else {
                call.resolve()
                return
            }
            self.embeddedWebview.dismiss(animated: false)
            call.resolve()
        }
    }
    
    @objc func show(_ call: CAPPluginCall) -> Void {
        DispatchQueue.main.async {
            guard (self.embeddedWebview != nil) else {
                call.resolve()
                return
            }
            self.bridge?.triggerWindowJSEvent(eventName: "show_embedded_view", data: "{ 'data': 'SHOW_EMBEDDED_     VIEW' }")
            self.embeddedWebview.show()
            call.resolve(["visibility": true])
        }
    }

    @objc func hide(_ call: CAPPluginCall) -> Void {
        DispatchQueue.main.async {
            guard (self.embeddedWebview != nil) else {
                call.resolve()
                return
            }
            self.embeddedWebview.hide()
            call.resolve(["visibility": false])
        }
    }
    
    @objc func pushTo(_ call: CAPPluginCall) -> Void {
        DispatchQueue.main.async {
            guard let path = call.getString("path") as String? else {
                call.reject("path is undeinfed");
                return;
            }
            
            if (self.embeddedWebview == nil) {
                call.reject("EmbeddedWebView is not initialized")
                return;
            }

            self.embeddedWebview.pushTo(path: path) {
                call.resolve()
            }
        }
        
    }
}
