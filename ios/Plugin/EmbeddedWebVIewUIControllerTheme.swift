//
//  EmbeddedWebVIewUIControllerTheme.swift
//  Plugin
//
//  Created by Shota Komi on 2022/08/17.
//  Copyright © 2022 Max Lynch. All rights reserved.
//

import Foundation
import Capacitor

struct EmbeddedWebViewUIControllerTheme {
    enum ThemeErrors: Error {
        case notEnoughThemeParameter(name: String)
    }
    
    struct ThemeValue: Decodable {
        let background: String
        let text: String
        init(value: JSObject, parent: String) throws {
            guard let background = value["background"] as? String else {
                throw ThemeErrors.notEnoughThemeParameter(name: "configuration.styles.theme.\(parent).background")
            }
            self.background = background

            guard let text = value["text"] as? String else {
                throw ThemeErrors.notEnoughThemeParameter(name: "configuration.styles.theme.\(parent).text")
            }
            self.text = text
        }
    }
    
    struct ThemeText: Decodable {
        let text: String
        init (value: JSObject, parent: String) throws {
            guard let text = value["text"] as? String else {
                throw ThemeErrors.notEnoughThemeParameter(name: "configuration.styles.theme.\(parent).text")
            }
            self.text = text
        }
    }
    
    struct ActionTheme: Decodable {
        let cancel: ThemeValue
        let `default`: ThemeText
        let destructive: ThemeText
        
        init(action: JSObject) throws {
            guard let cancel = action["cancel"] as? JSObject else {
                throw ThemeErrors.notEnoughThemeParameter(name: "configuration.styles.theme.action.cancel")
            }
            self.cancel = try ThemeValue(value: cancel, parent: "action.cancel")
            guard let defaultTheme = action["default"] as? JSObject else {
                throw ThemeErrors.notEnoughThemeParameter(name: "configuration.styles.theme.action.default")
            }
            self.default = try ThemeText(value: defaultTheme, parent: "action.default")
            guard let destructive = action["destructive"] as? JSObject else {
                throw ThemeErrors.notEnoughThemeParameter(name: "configuration.styles.theme.action.destructive")
            }
            self.destructive = try ThemeText(value: destructive, parent: "action.destructive")
        }
    }
    struct Theme: Decodable {
        let view: ThemeValue
        let action: ActionTheme
        init(theme: JSObject) throws {
            guard let view = theme["view"] as? JSObject else {
                throw ThemeErrors.notEnoughThemeParameter(name: "configuration.styles.theme.view")
            }
            self.view = try ThemeValue(value: view, parent: "view")
            guard let action = theme["action"] as? JSObject else {
                throw ThemeErrors.notEnoughThemeParameter(name: "configuration.styles.theme.action.action")
            }
            self.action = try ActionTheme(action: action)
        }
    }
}

extension UIColor {
    convenience init(hex: String) {
        var cString:String = hex.trimmingCharacters(in: .whitespacesAndNewlines).uppercased()

        if (cString.hasPrefix("#")) {
            cString.remove(at: cString.startIndex)
        }

        var rgbValue:UInt64 = 0
        Scanner(string: cString).scanHexInt64(&rgbValue)

        self.init(
            red: CGFloat((rgbValue & 0xFF0000) >> 16) / 255.0,
            green: CGFloat((rgbValue & 0x00FF00) >> 8) / 255.0,
            blue: CGFloat(rgbValue & 0x0000FF) / 255.0,
            alpha: CGFloat(1.0)
        )
    }
}

extension UIAlertAction {
    var titleTextColor: UIColor? {
        get {
            return self.value(forKey: "titleTextColor") as? UIColor
        } set {
            self.setValue(newValue, forKey: "titleTextColor")
        }
    }
}
