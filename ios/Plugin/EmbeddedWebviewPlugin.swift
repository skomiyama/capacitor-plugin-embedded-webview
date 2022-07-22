import Foundation
import Capacitor

/**
 * Please read the Capacitor iOS Plugin Development Guide
 * here: https://capacitorjs.com/docs/plugins/ios
 */
@objc(EmbeddedWebviewPlugin)
public class EmbeddedWebviewPlugin: CAPPlugin {
    private var embeddedWebview: EmbeddedWebview!

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
            
            guard let webviewConfig = call.getObject("webviewConfiguration") else { return }
            let width: Int = webviewConfig["width"] as! Int
            let height: Int = webviewConfig["height"] as! Int
            let frame = CGRect(x: 0, y: 0, width: width, height: height)
            
            // FIXME: insert canOpenUrl()
            self.embeddedWebview.create(url: url, frame: frame)
            call.resolve()
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
}
