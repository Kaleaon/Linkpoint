import SwiftUI
import SafariServices

struct LoginView: View {
    @EnvironmentObject private var authViewModel: AuthViewModel
    @State private var isShowingCreateAccount = false
    @FocusState private var focusedField: Field?

    private enum Field: Hashable {
        case firstName, lastName, password
    }

    var body: some View {
        NavigationView {
            ScrollView {
                VStack(spacing: 28) {
                    header
                    form
                    actions
                    footer
                }
                .padding(.horizontal, 24)
                .padding(.vertical, 36)
            }
            .background(Color(.systemGroupedBackground))
            .navigationTitle("Linkpoint")
            .navigationBarTitleDisplayMode(.large)
            .alert("Login Error", isPresented: Binding<Bool>(
                get: { authViewModel.errorMessage != nil },
                set: { _ in authViewModel.clearError() }
            )) {
                Button("OK", role: .cancel) {}
            } message: {
                Text(authViewModel.errorMessage ?? "Unknown error")
            }
        }
    }

    private var header: some View {
        VStack(spacing: 16) {
            Image(systemName: "globe.americas")
                .font(.system(size: 80))
                .foregroundColor(.accentColor)

            Text("Welcome to Linkpoint")
                .font(.largeTitle).bold()
                .multilineTextAlignment(.center)

            Text("Connect to Second Life with a modern SwiftUI experience.")
                .font(.subheadline)
                .foregroundColor(.secondary)
                .multilineTextAlignment(.center)
        }
    }

    private var form: some View {
        VStack(spacing: 20) {
            inputField(
                title: "First Name",
                text: $authViewModel.firstName,
                field: .firstName,
                error: authViewModel.firstNameError,
                submitNext: .lastName
            )

            inputField(
                title: "Last Name",
                text: $authViewModel.lastName,
                field: .lastName,
                error: authViewModel.lastNameError,
                submitNext: .password
            )

            VStack(alignment: .leading, spacing: 8) {
                Text("Password").font(.headline)
                SecureField("••••••••", text: $authViewModel.password)
                    .textFieldStyle(.roundedBorder)
                    .focused($focusedField, equals: .password)
                    .onSubmit { authViewModel.login() }
                if let error = authViewModel.passwordError {
                    validationLabel(error)
                }
            }
        }
    }

    private func inputField(
        title: String,
        text: Binding<String>,
        field: Field,
        error: String?,
        submitNext: Field?
    ) -> some View {
        VStack(alignment: .leading, spacing: 8) {
            Text(title).font(.headline)
            TextField("", text: text)
                .textFieldStyle(.roundedBorder)
                .textInputAutocapitalization(.words)
                .focused($focusedField, equals: field)
                .onSubmit {
                    if let next = submitNext {
                        focusedField = next
                    } else {
                        authViewModel.login()
                    }
                }
            if let error {
                validationLabel(error)
            }
        }
    }

    private func validationLabel(_ text: String) -> some View {
        Text(text)
            .font(.caption)
            .foregroundColor(.red)
    }

    private var actions: some View {
        VStack(spacing: 12) {
            if authViewModel.isLoading {
                ProgressView().scaleEffect(1.2).padding(.vertical)
            }

            Button(action: authViewModel.login) {
                HStack {
                    Image(systemName: "person.fill.badge.plus")
                    Text("Sign In")
                }
                .font(.headline)
                .foregroundColor(.white)
                .frame(maxWidth: .infinity)
                .frame(height: 50)
                .background(authViewModel.isFormValid ? Color.accentColor : Color.gray)
                .cornerRadius(12)
            }
            .disabled(!authViewModel.isFormValid || authViewModel.isLoading)

            Button("Forgot Password?", action: {})
                .font(.subheadline)
                .foregroundColor(.accentColor)
        }
    }

    private var footer: some View {
        VStack(spacing: 16) {
            Divider()

            Text("Don't have a Second Life account?")
                .font(.subheadline)
                .foregroundColor(.secondary)

            Button {
                isShowingCreateAccount = true
            } label: {
                HStack {
                    Image(systemName: "person.crop.circle.badge.plus")
                    Text("Create Free Account")
                }
                .font(.headline)
                .foregroundColor(.accentColor)
                .frame(maxWidth: .infinity)
                .frame(height: 50)
                .background(Color.accentColor.opacity(0.12))
                .cornerRadius(12)
            }
            .sheet(isPresented: $isShowingCreateAccount) {
                SafariView(url: URL(string: "https://join.secondlife.com/")!)
            }

            HStack(spacing: 12) {
                Link("Terms of Service", destination: URL(string: "https://www.lindenlab.com/tos")!)
                Link("Privacy", destination: URL(string: "https://www.lindenlab.com/privacy")!)
            }
            .font(.caption)
        }
    }
}

// MARK: - Preview

struct LoginView_Previews: PreviewProvider {
    static var previews: some View {
        LoginView()
            .environmentObject(AuthViewModel())
    }
}

// MARK: - Safari Wrapper

struct SafariView: UIViewControllerRepresentable {
    let url: URL

    func makeUIViewController(context: Context) -> SFSafariViewController {
        SFSafariViewController(url: url)
    }

    func updateUIViewController(_ controller: SFSafariViewController, context: Context) {}
}
