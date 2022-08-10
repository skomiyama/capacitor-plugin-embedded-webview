import Foundation
import Capacitor
import WebKit

@objc class EmbeddedWebview: UIViewController, WKNavigationDelegate, WKUIDelegate {
    private var url: URL!
    private var webViewConfiguration: WKWebViewConfiguration!
    private var webViewFrame: CGRect!
    private var webView: WKWebView!
    
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
        self.webViewConfiguration = self.createWebViewConfiguration(configuration: configuration)
        self.webViewFrame = CGRect(x: 0, y: 0, width: configuration.styles.width, height: configuration.styles.height)
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
                let scriptSource = "window.embedded_webview = " + jsonData
                let script = WKUserScript(source: scriptSource, injectionTime: .atDocumentEnd, forMainFrameOnly: true)
                let userContentController = WKUserContentController()
                userContentController.addUserScript(script)
                webViewConfiguration.userContentController = userContentController
            }
        }
        return webViewConfiguration
    }
    
    override func loadView() {
        webView = WKWebView(frame: self.webViewFrame, configuration: self.webViewConfiguration)
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
        view.isHidden = false
    }
    public func hide() {
        view.isHidden = true
    }
}
