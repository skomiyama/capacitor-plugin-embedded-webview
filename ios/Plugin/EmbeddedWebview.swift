import Foundation

@objc public class EmbeddedWebview: NSObject {
    @objc public func echo(_ value: String) -> String {
        print(value)
        return value
    }
}
