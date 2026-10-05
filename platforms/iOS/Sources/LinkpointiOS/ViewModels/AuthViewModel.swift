import Foundation
import LLSD
import LLSDXMLRPC

#if canImport(Combine)
import Combine
#else
public protocol ObservableObject: AnyObject {}

@propertyWrapper
public struct Published<Value> {
    public var wrappedValue: Value
    public init(wrappedValue: Value) {
        self.wrappedValue = wrappedValue
    }
}
#endif

@MainActor
public class AuthViewModel: ObservableObject {
    @Published public var isAuthenticated = false
    @Published public var currentUser: User?
    @Published public var isLoading = false
    @Published public var errorMessage: String?
    @Published public var selectedGrid: Grid = .secondLife
    @Published public var availableGrids: [Grid] = Grid.defaultGrids

    private let xmlrpcClient: XMLRPCClient

    #if canImport(Combine)
    private var cancellables = Set<AnyCancellable>()
    #endif

    public init(xmlrpcClient: XMLRPCClient = XMLRPCClient()) {
        self.xmlrpcClient = xmlrpcClient
        checkAuthStatus()
    }

    public func login(username: String, password: String, firstName: String, lastName: String) async {
        isLoading = true
        errorMessage = nil

        do {
            let responseMap = try await xmlrpcClient.loginToSimulator(
                gridURL: selectedGrid.loginURL,
                firstName: firstName,
                lastName: lastName,
                password: password
            )

            let loginSuccess = responseMap["login"]?.asString?.lowercased() == "true" || responseMap["login"]?.asBool == true

            if loginSuccess {
                let agentId = responseMap["agent_id"]?.asString ?? UUID().uuidString
                let sessionId = responseMap["session_id"]?.asString ?? UUID().uuidString
                let fName = responseMap["first_name"]?.asString ?? firstName
                let lName = responseMap["last_name"]?.asString ?? lastName

                let user = User(
                    agentId: agentId,
                    sessionId: sessionId,
                    firstName: fName,
                    lastName: lName,
                    displayName: "\(fName) \(lName)"
                )

                self.currentUser = user
                self.isAuthenticated = true
                saveUserSession(user)
            } else {
                let reason = responseMap["message"]?.asString ?? responseMap["reason"]?.asString ?? "Authentication rejected by grid."
                self.errorMessage = "Login failed: \(reason)"
            }

        } catch {
            self.errorMessage = "Login error: \(error.localizedDescription)"
        }

        isLoading = false
    }

    public func logout() {
        currentUser = nil
        isAuthenticated = false
        clearUserSession()
    }

    private func checkAuthStatus() {
        if let userData = UserDefaults.standard.data(forKey: "currentUser"),
           let user = try? JSONDecoder().decode(User.self, from: userData) {
            currentUser = user
            isAuthenticated = true
        }
    }

    private func saveUserSession(_ user: User) {
        if let encoded = try? JSONEncoder().encode(user) {
            UserDefaults.standard.set(encoded, forKey: "currentUser")
        }
    }

    private func clearUserSession() {
        UserDefaults.standard.removeObject(forKey: "currentUser")
    }

    public func addCustomGrid(name: String, loginURL: String) {
        let newGrid = Grid(name: name, loginURL: loginURL)
        availableGrids.append(newGrid)
    }
}
