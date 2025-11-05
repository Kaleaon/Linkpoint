import Foundation
import Combine

@MainActor
class AuthViewModel: ObservableObject {
    @Published var isAuthenticated = false
    @Published var currentUser: User?
    @Published var isLoading = false
    @Published var errorMessage: String?
    @Published var selectedGrid: Grid = .secondLife
    @Published var availableGrids: [Grid] = Grid.defaultGrids
    
    private var cancellables = Set<AnyCancellable>()
    
    init() {
        checkAuthStatus()
    }
    
    func login(username: String, password: String, firstName: String, lastName: String) async {
        isLoading = true
        errorMessage = nil
        
        do {
            // Simulate network delay
            try await Task.sleep(nanoseconds: 1_000_000_000)
            
            // Create user object
            let user = User(
                username: username,
                firstName: firstName,
                lastName: lastName,
                gridURL: selectedGrid.loginURL,
                isLoggedIn: true
            )
            
            // In a real app, this would make an XMLRPC call to the grid
            // For now, we'll simulate a successful login
            currentUser = user
            isAuthenticated = true
            
            // Save to UserDefaults
            saveUserSession(user)
            
        } catch {
            errorMessage = "Login failed: \(error.localizedDescription)"
        }
        
        isLoading = false
    }
    
    func logout() {
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
    
    func addCustomGrid(name: String, loginURL: String) {
        let newGrid = Grid(name: name, loginURL: loginURL)
        availableGrids.append(newGrid)
    }
}