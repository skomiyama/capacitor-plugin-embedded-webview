//
//  EmbeddedWebViewUIController.swift
//  Plugin
//
//  Created by Shota Komi on 2022/08/17.
//  Copyright © 2022 Max Lynch. All rights reserved.
//

import Foundation
import Capacitor

extension UIView {
    private struct AssociatedKey {
        static var subviewsBackgroundColor = "subviewsBackgroundColor"
    }

    @objc dynamic var subviewsBackgroundColor: UIColor? {
        get {
            return objc_getAssociatedObject(self, &AssociatedKey.subviewsBackgroundColor) as? UIColor
        }

        set {
            objc_setAssociatedObject(self,  &AssociatedKey.subviewsBackgroundColor, newValue, .OBJC_ASSOCIATION_RETAIN_NONATOMIC)
            subviews.forEach { $0.backgroundColor = newValue }
        }
    }
}

func createAlert(webView: WKWebView, options: EmbeddedWebViewContentAlertOptions) -> UIAlertController {
    let alertStyle = UIAlertController.Style(rawValue: options.style)
    let alert = UIAlertController(title: options.title ?? nil, message: options.message ?? nil, preferredStyle: alertStyle ?? .alert)
    alert.view.subviews.forEach { v in
       v.subviews.forEach { v in
            v.subviews.forEach { v in
                v.subviews.forEach { v in
                    v.backgroundColor = UIColor(hex: options.theme.view.background)
                }
            }
        }
    }

    if (alertStyle == .alert) {
        // set title
        let titleAttributes = [NSAttributedString.Key.foregroundColor: UIColor(hex: options.theme.view.text)]
        let titleString = NSAttributedString(string: options.title!, attributes: titleAttributes)
        alert.setValue(titleString, forKey: "attributedTitle")

        // set message
        let messageAttributes = [NSAttributedString.Key.foregroundColor: UIColor(hex: options.theme.view.text)]
        let messageString = NSAttributedString(string: options.message!, attributes: messageAttributes)
        alert.setValue(messageString, forKey: "attributedMessage")
    }
    
    if (alertStyle == .actionSheet) {
        if let cancelBackgroundViewType = NSClassFromString("_UIAlertControlleriOSActionSheetCancelBackgroundView") as? UIView.Type {
            cancelBackgroundViewType.appearance().subviewsBackgroundColor = UIColor(hex: options.theme.action.cancel.background)
        }
    }
    
    for action in options.actions {
        let role = UIAlertAction.Style(rawValue: action.role)
        let action = UIAlertAction(
            title: action.title,
            style: role ?? .default,
            handler: { _ in
                let script = """
                window.dispatchEvent(new CustomEvent('on_did_dismiss_\(options.name)', { detail: '\(action.value)' }));
                """
                webView.evaluateJavaScript(script)
            }
        )

        if (role == .destructive) {
            action.titleTextColor = UIColor(hex: options.theme.action.destructive.text)
        }
        if (role == .cancel) {
            action.titleTextColor = UIColor(hex: options.theme.action.cancel.text)
        }
        if (role == .default) {
            action.titleTextColor = UIColor(hex: options.theme.action.default.text)
        }

        alert.addAction(action)
    }
    return alert
}
