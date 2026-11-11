import SwiftUI

@main
struct LinkpointApp: App {
    @StateObject private var authViewModel = AuthViewModel()
    @StateObject private var appState = AppState()

    var body: some Scene {
        WindowGroup {
            ContentView()
                .environmentObject(authViewModel)
                .environmentObject(appState)
                .onAppear(perform: configureAppearance)
        }
    }

    private func configureAppearance() {
        UINavigationBar.appearance().tintColor = UIColor.systemPurple
        UITabBar.appearance().tintColor = UIColor.systemPurple
        UITabBar.appearance().unselectedItemTintColor = UIColor.secondaryLabel
    }
}

// MARK: - Global App State
final class AppState: ObservableObject {
    @Published var isLoading: Bool = false
    @Published var errorMessage: String?
    @Published var currentUser: User?

    func update(user: User) {
        currentUser = user
    }

    func clearError() {
        errorMessage = nil
    }
}

// MARK: - Root Content View
struct ContentView: View {
    @EnvironmentObject private var authViewModel: AuthViewModel

    var body: some View {
        Group {
            if authViewModel.isAuthenticated {
                MainTabView()
            } else {
                LoginView()
            }
        }
        .animation(.easeInOut(duration: 0.2), value: authViewModel.isAuthenticated)
    }
}
