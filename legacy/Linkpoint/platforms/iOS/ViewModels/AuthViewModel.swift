import Foundation
import Combine
import Security

final class AuthViewModel: ObservableObject {
    @Published var firstName: String = ""
    @Published var lastName: String = ""
    @Published var password: String = ""
    @Published var isLoading: Bool = false
    @Published var errorMessage: String?
    @Published var isAuthenticated: Bool = false
    @Published var currentUser: User?

    private let authService = AuthService()
    private var cancellables = Set<AnyCancellable>()
    private let tokenKey = "linkpoint_auth_token"
    private let userDefaults = UserDefaults.standard

    init() {
        restoreSession()
    }

    var isFormValid: Bool {
        validateFirstName(firstName) == nil &&
        validateLastName(lastName) == nil &&
        validatePassword(password) == nil
    }

    var firstNameError: String? { validateFirstName(firstName) }
    var lastNameError: String? { validateLastName(lastName) }
    var passwordError: String? { validatePassword(password) }

    func login() {
        guard isFormValid else {
            errorMessage = "Please correct the highlighted issues."
            return
        }

        let request = LoginRequest(
            firstName: firstName.trimmingCharacters(in: .whitespacesAndNewlines),
            lastName: lastName.trimmingCharacters(in: .whitespacesAndNewlines),
            password: password
        )

        isLoading = true
        errorMessage = nil

        authService.authenticate(loginRequest: request)
            .receive(on: DispatchQueue.main)
            .sink { [weak self] completion in
                guard let self else { return }
                self.isLoading = false
                if case let .failure(error) = completion {
                    self.errorMessage = error.localizedDescription
                }
            } receiveValue: { [weak self] response in
                guard let self else { return }
                guard response.success, let user = response.user else {
                    self.errorMessage = response.message ?? "Login failed."
                    return
                }

                self.persistSession(token: response.token, user: user)
                self.currentUser = user
                self.isAuthenticated = true
            }
            .store(in: &cancellables)
    }

    func logout() {
        deletePersistedSession()
        firstName = ""
        lastName = ""
        password = ""
        errorMessage = nil
        currentUser = nil
        isAuthenticated = false
    }

    func clearError() {
        errorMessage = nil
    }

    // MARK: - Validation

    private func validateFirstName(_ name: String) -> String? {
        let trimmed = name.trimmingCharacters(in: .whitespacesAndNewlines)
        guard !trimmed.isEmpty else { return "First name is required" }
        guard trimmed.count >= 2 else { return "First name must be at least 2 characters" }
        guard trimmed.range(of: "^[A-Za-z'-]+$", options: .regularExpression) != nil else {
            return "Letters, apostrophes or hyphens only"
        }
        return nil
    }

    private func validateLastName(_ name: String) -> String? {
        let trimmed = name.trimmingCharacters(in: .whitespacesAndNewlines)
        guard !trimmed.isEmpty else { return "Last name is required" }
        guard trimmed.count >= 2 else { return "Last name must be at least 2 characters" }
        guard trimmed.range(of: "^[A-Za-z'-]+$", options: .regularExpression) != nil else {
            return "Letters, apostrophes or hyphens only"
        }
        return nil
    }

    private func validatePassword(_ password: String) -> String? {
        guard !password.isEmpty else { return "Password is required" }
        guard password.count >= 6 else { return "Password must contain at least 6 characters" }
        return nil
    }

    // MARK: - Session persistence

    private func persistSession(token: String?, user: User) {
        if let token, let data = token.data(using: .utf8) {
            saveTokenToKeychain(data)
            userDefaults.set(token, forKey: tokenKey)
        }

        if let encoded = try? JSONEncoder().encode(user) {
            userDefaults.set(encoded, forKey: "linkpoint_user")
        }
    }

    private func restoreSession() {
        guard let token = userDefaults.string(forKey: tokenKey) else { return }

        authService.validate(token: token)
            .receive(on: DispatchQueue.main)
            .sink { [weak self] completion in
                if case .failure = completion {
                    self?.deletePersistedSession()
                }
            } receiveValue: { [weak self] response in
                guard response.success, let user = response.user else {
                    self?.deletePersistedSession()
                    return
                }
                self?.currentUser = user
                self?.isAuthenticated = true
            }
            .store(in: &cancellables)
    }

    private func deletePersistedSession() {
        userDefaults.removeObject(forKey: tokenKey)
        userDefaults.removeObject(forKey: "linkpoint_user")

        let query: [String: Any] = [
            kSecClass as String: kSecClassGenericPassword,
            kSecAttrAccount as String: tokenKey
        ]
        SecItemDelete(query as CFDictionary)
    }

    private func saveTokenToKeychain(_ data: Data) {
        let query: [String: Any] = [
            kSecClass as String: kSecClassGenericPassword,
            kSecAttrAccount as String: tokenKey,
            kSecValueData as String: data
        ]

        SecItemDelete(query as CFDictionary)
        SecItemAdd(query as CFDictionary, nil)
    }
}

// MARK: - Network Layer

final class AuthService {
    private let session = URLSession(configuration: .default)
    private let baseURL = URL(string: "https://login.agni.lindenlab.com/cgi-bin")!

    func authenticate(loginRequest: LoginRequest) -> AnyPublisher<AuthResponse, Error> {
        var request = URLRequest(url: baseURL.appendingPathComponent("login.cgi"))
        request.httpMethod = "POST"
        request.setValue("application/json", forHTTPHeaderField: "Content-Type")

        do {
            request.httpBody = try JSONEncoder().encode(loginRequest)
        } catch {
            return Fail(error: error).eraseToAnyPublisher()
        }

        return session.dataTaskPublisher(for: request)
            .map(\.$data)
            .decode(type: AuthResponse.self, decoder: JSONDecoder())
            .mapError { error -> Error in
                if case .decodingFailure = error as? DecodingError {
                    return AuthError.invalidCredentials
                }
                return error
            }
            .eraseToAnyPublisher()
    }

    func validate(token: String) -> AnyPublisher<AuthResponse, Error> {
        // Stub validation for shared data set – in production this would
        // call a validation endpoint and refresh the session if necessary.
        let response = AuthResponse(success: true, message: nil, user: User.sample, token: token)
        return Just(response)
            .setFailureType(to: Error.self)
            .eraseToAnyPublisher()
    }
}

enum AuthError: LocalizedError {
    case invalidCredentials

    var errorDescription: String? {
        switch self {
        case .invalidCredentials:
            return "Invalid username or password."
        }
    }
}
