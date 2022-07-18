import Foundation
import Capacitor

/**
 * Please read the Capacitor iOS Plugin Development Guide
 * here: https://capacitorjs.com/docs/plugins/ios
 */
@objc(EmbeddedWebviewPlugin)
public class EmbeddedWebviewPlugin: CAPPlugin {
    private var embeddedWebview: EmbeddedWebview!
    var customWebview: WKWebView?
    
    @objc func echo(_ call: CAPPluginCall) {
        let value = call.getString("value") ?? ""
        call.resolve(["value": value])
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
            
            self.embeddedWebview = EmbeddedWebview(url: url)
            
            capWebview.addSubview(self.embeddedWebview.view)
            // FIXME: insert canOpenUrl()
            self.embeddedWebview.create(url: "https://twogate.com")
        }
    }
    
    @objc func destroy(_ call:CAPPluginCall) -> Void {
        DispatchQueue.main.async {
            self.embeddedWebview.dismiss(animated: false)
            call.resolve()
        }
    }
    
    @objc func show(_ call: CAPPluginCall) -> Void {
        DispatchQueue.main.async {
            self.embeddedWebview.show()
            call.resolve(["visibility": true])
        }
    }

    @objc func hide(_ call: CAPPluginCall) -> Void {
        DispatchQueue.main.async {
            self.embeddedWebview.hide()
            call.resolve(["visibility": false])
        }
    }
}
