import Foundation
import Capacitor
import WebKit

@objc class EmbeddedWebview: UIViewController, WKNavigationDelegate, WKUIDelegate {
    private func deviceRect(x: CGFloat, y: CGFloat) -> CGRect {
        let width = UIScreen.main.bounds.width
        let height = UIScreen.main.bounds.height
        return CGRect(x: x, y: y, width: width, height: height - 84)
    }
    
    private var url: URL!
    private var css: String?
    private var webView: WKWebView!
    
    override init(nibName nibNameOrNil: String?, bundle nibBundleOrNil: Bundle?) {
        super.init(nibName: nibNameOrNil, bundle: nibBundleOrNil)
    }
    convenience init(url: String, css: String = "") {
        self.init(nibName:nil, bundle:nil)
        self.url = URL(string:url)
        self.css = css
    }
    required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
    }
    
    override func loadView() {
        let webConfiguration = WKWebViewConfiguration()
        let frame = deviceRect(x: 0, y: 0)
        webView = WKWebView(frame: frame, configuration: webConfiguration)
        webView.uiDelegate = self
        view = webView
    }

    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        self.webView.uiDelegate = self
        self.webView.navigationDelegate = self
    }
    
    // target is _blank
    func webView(_ webView: WKWebView, createWebViewWith configuration: WKWebViewConfiguration, for navigationAction: WKNavigationAction, windowFeatures: WKWindowFeatures) -> WKWebView? {
       if navigationAction.targetFrame == nil {
           if  let url = navigationAction.request.url {
               UIApplication.shared.open(url, options: [:], completionHandler: nil)
           }
       }
       return nil
    }
    
    public func create(url: String) {
        let URL = URL(string:url)
        let request = URLRequest(url: URL!)
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
