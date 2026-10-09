import XCTest
import SwiftUI
@testable import Ktheme

final class KthemeTests: XCTestCase {
    func testGeneratedTokensExist() {
        _ = GeneratedTokens.Color.Status.online
        _ = GeneratedTokens.Color.Status.offline
        _ = GeneratedTokens.Color.Editor.background
        _ = GeneratedTokens.Color.Scene.background
        _ = GeneratedTokens.Color.Brand.primary
        _ = GeneratedTokens.Color.Ui.text
    }

    func testKthemeConvenienceAccessors() {
        XCTAssertNotNil(Ktheme.brandPrimary)
        XCTAssertNotNil(Ktheme.brandSecondary)
        XCTAssertNotNil(Ktheme.textPrimary)
        XCTAssertNotNil(Ktheme.textMuted)
        XCTAssertNotNil(Ktheme.error)
        XCTAssertNotNil(Ktheme.surface)
        XCTAssertNotNil(Ktheme.sceneBackground)
    }
}
