import SwiftUI

public struct Ktheme {
    public typealias Tokens = GeneratedTokens

    // Convenient token accessors for iOS views
    public static var brandPrimary: Color { GeneratedTokens.Color.Brand.primary }
    public static var brandSecondary: Color { GeneratedTokens.Color.Brand.secondary }
    public static var brandAccent: Color { GeneratedTokens.Color.Brand.accent }

    public static var textPrimary: Color { GeneratedTokens.Color.Ui.text }
    public static var textMuted: Color { GeneratedTokens.Color.Ui.textMuted }
    public static var textSecondary: Color { GeneratedTokens.Color.Ui.textSecondary }

    public static var background: Color { GeneratedTokens.Color.Ui.background }
    public static var surface: Color { GeneratedTokens.Color.Ui.surface }
    public static var cardBackground: Color { GeneratedTokens.Color.Ui.cardBackground }

    public static var error: Color { GeneratedTokens.Color.Ui.error }
    public static var disabled: Color { GeneratedTokens.Color.Ui.disabled }
    public static var shadow: Color { GeneratedTokens.Color.Ui.shadow }
    public static var viewportPlaceholder: Color { GeneratedTokens.Color.Ui.viewportPlaceholder }

    public static var sceneBackground: Color { GeneratedTokens.Color.Scene.background }
    public static var sceneShadow: Color { GeneratedTokens.Color.Scene.shadow }

    public static var statusOnline: Color { GeneratedTokens.Color.Status.online }
    public static var statusOffline: Color { GeneratedTokens.Color.Status.offline }
    public static var statusDegraded: Color { GeneratedTokens.Color.Status.degraded }
    public static var statusUnknown: Color { GeneratedTokens.Color.Status.unknown }
}

public extension Color {
    static var ktheme: Ktheme.Type { Ktheme.self }
}
