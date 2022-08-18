import Foundation
import Capacitor
import WebKit

struct EmbeddedWebViewContentAlertAction: Decodable {
    let title: String
    let value: String
    let role: Int
}
struct EmbeddedWebViewContentAlertOptions: Decodable {
    let title: String?
    let message: String?
    let style: Int
    let theme: EmbeddedWebViewUIControllerTheme.Theme
    let name: String
    let actions: [EmbeddedWebViewContentAlertAction]
}
enum OverlayDiaplay: Int {
    case fullscreen = 0
    case contain = 1
}
struct EmbeddedWebViewContentModalOptions: Decodable {
    let display: Int
}

class FulscreenWebView: WKWebView {
    override var safeAreaInsets: UIEdgeInsets {
        return UIEdgeInsets(top: 0, left: 0, bottom: 0, right: 0)
    }
}


@objc class EmbeddedWebView: UIViewController, WKScriptMessageHandler, WKNavigationDelegate, WKUIDelegate {
    private var url: URL!
    private var webViewConfiguration: WKWebViewConfiguration!
    private var webViewFrame: CGRect!
    private var webView: WKWebView!
    
    func userContentController(_ userContentController: WKUserContentController, didReceive message: WKScriptMessage) {
        let body = String(describing: message.body)
        let jsonData = body.data(using: .utf8)
        
        switch message.name {
        case "dismissOverlay":
            self.webView.frame = self.webViewFrame
            self.webView.isOpaque = true
            break
        case "showOverlay":
            self.webView.isOpaque = false
            
            if let options = try? JSONDecoder().decode(EmbeddedWebViewContentModalOptions.self, from: jsonData!) {
                if (OverlayDiaplay(rawValue: options.display) == .fullscreen) {
                    let frame = CGRect(x: 0, y: 0, width: UIScreen.main.bounds.width, height: UIScreen.main.bounds.height)
                    self.webView.frame = frame
                }
                let script = """
                window.dispatchEvent(new CustomEvent('set_native_modal_layout', null))
                """
                self.webView.evaluateJavaScript(script)
            }
            break
        case "showActionSheet", "showAlert":
            print(body)
            if let options = try? JSONDecoder().decode(EmbeddedWebViewContentAlertOptions.self, from: jsonData!) {
                let alert = createAlert(webView: self.webView, options: options)
                self.present(alert, animated: true)
            }
            break
        default:
            print("default")
        }
    }
    
    // for target=_blank
    func webView(_ webView: WKWebView, createWebViewWith configuration: WKWebViewConfiguration, for navigationAction: WKNavigationAction, windowFeatures: WKWindowFeatures) -> WKWebView? {
       if navigationAction.targetFrame == nil {
           if  let url = navigationAction.request.url {
               UIApplication.shared.open(url, options: [:], completionHandler: nil)
           }
       }
       return nil
    }

    override init(nibName nibNameOrNil: String?, bundle nibBundleOrNil: Bundle?) {
        super.init(nibName: nibNameOrNil, bundle: nibBundleOrNil)
    }
    convenience init(url: String, configuration: EmbeddedWebviewConfiguration) {
        self.init(nibName:nil, bundle:nil)
        self.url = URL(string:url)
        self.webViewFrame = CGRect(x: 0, y: 0, width: configuration.styles.width, height: configuration.styles.height)
        self.webViewConfiguration = self.createWebViewConfiguration(configuration: configuration)
    }
    required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
    }
    
    private func encodeToJson(variables: JSObject) -> String? {
        if let jsonData = try? JSONSerialization.data(withJSONObject: variables) {
            if let jsonText = String(data: jsonData, encoding: .utf8) {
                return jsonText
            }
        }
        return nil
    }
    
    private func createWebViewConfiguration(configuration: EmbeddedWebviewConfiguration) -> WKWebViewConfiguration {
        let webViewConfiguration = WKWebViewConfiguration()
        if (configuration.globalVariables != nil) {
            if let jsonData = self.encodeToJson(variables: configuration.globalVariables!) {
                var scriptSource = "window.embedded_webview = " + jsonData + ";"
                let height = Int(self.webViewFrame.height)
                print(height)
                scriptSource = scriptSource + """
                    document.documentElement.style.setProperty('--embedded-content-height', '\(height)px');
                    window.addEventListener('send_message_to_webview', ($event) => {
                        window.webkit.messageHandlers[$event.detail.function].postMessage($event.detail.options)
                    });
                """
                let script = WKUserScript(source: scriptSource, injectionTime: .atDocumentEnd, forMainFrameOnly: true)
                let userContentController = WKUserContentController()
                userContentController.add(self, name: "showOverlay")
                userContentController.add(self, name: "dismissOverlay")
                userContentController.add(self, name: "showAlert")
                userContentController.add(self, name: "showActionSheet")
                userContentController.addUserScript(script)
                webViewConfiguration.userContentController = userContentController
            }
        }
        return webViewConfiguration
    }
    
    override func loadView() {
        webView = FulscreenWebView(frame: self.webViewFrame, configuration: self.webViewConfiguration)
        webView.uiDelegate = self
        view = webView
    }

    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        self.webView.uiDelegate = self
        self.webView.navigationDelegate = self
    }
    
    public func create() {
        let request = URLRequest(url: self.url)
        webView.load(request)
    }
    public func destroy() {
        self.dismiss(animated: false)
    }

    public func show() {
        self.webView.evaluateJavaScript("console.log('run from swfit')")
        view.isHidden = false
    }
    public func hide() {
        view.isHidden = true
    }
}
