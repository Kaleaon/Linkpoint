import XCTest
@testable import LinkpointiOS
@testable import LLSDXMLRPC
@testable import LLSD

final class AuthViewModelTests: XCTestCase {

    func testAuthViewModelStateAndLogout() async {
        await MainActor.run {
            let viewModel = AuthViewModel()
            XCTAssertFalse(viewModel.isAuthenticated)
            XCTAssertNil(viewModel.currentUser)

            let mockUser = User(
                agentId: "agent-123",
                sessionId: "session-456",
                firstName: "Test",
                lastName: "User"
            )

            viewModel.currentUser = mockUser
            viewModel.isAuthenticated = true
            XCTAssertTrue(viewModel.isAuthenticated)

            viewModel.logout()
            XCTAssertFalse(viewModel.isAuthenticated)
            XCTAssertNil(viewModel.currentUser)
        }
    }

    func testCustomGridAddition() async {
        let viewModel = await AuthViewModel()
        let initialCount = await viewModel.availableGrids.count

        await viewModel.addCustomGrid(name: "TestGrid", loginURL: "http://testgrid.org/login")
        let updatedCount = await viewModel.availableGrids.count

        XCTAssertEqual(updatedCount, initialCount + 1)
    }
}
