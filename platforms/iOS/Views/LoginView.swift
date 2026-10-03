import SwiftUI

struct LoginView: View {
    @EnvironmentObject var authViewModel: AuthViewModel
    @State private var username = ""
    @State private var password = ""
    @State private var firstName = ""
    @State private var lastName = ""
    @State private var showingGridPicker = false
    
    var body: some View {
        NavigationView {
            ZStack {
                // Background gradient
                LinearGradient(
                    gradient: Gradient(colors: [Color.blue.opacity(0.6), Color.purple.opacity(0.6)]),
                    startPoint: .topLeading,
                    endPoint: .bottomTrailing
                )
                .ignoresSafeArea()
                
                ScrollView {
                    VStack(spacing: 25) {
                        // Logo and title
                        VStack(spacing: 10) {
                            Image(systemName: "link.circle.fill")
                                .resizable()
                                .frame(width: 100, height: 100)
                                .foregroundColor(.white)
                            
                            Text("Linkpoint")
                                .font(.system(size: 42, weight: .bold))
                                .foregroundColor(.white)
                            
                            Text("Connect to Virtual Worlds")
                                .font(.subheadline)
                                .foregroundColor(.white.opacity(0.9))
                        }
                        .padding(.top, 50)
                        .padding(.bottom, 30)
                        
                        // Login form
                        VStack(spacing: 20) {
                            // Grid selector
                            Button(action: { showingGridPicker = true }) {
                                HStack {
                                    Text("Grid: \(authViewModel.selectedGrid.name)")
                                        .foregroundColor(.primary)
                                    Spacer()
                                    Image(systemName: "chevron.down")
                                        .foregroundColor(.secondary)
                                }
                                .padding()
                                .background(Color(.systemBackground))
                                .cornerRadius(10)
                            }
                            
                            // Username
                            TextField("Username", text: $username)
                                .textFieldStyle(RoundedTextFieldStyle())
                                .autocapitalization(.none)
                            
                            // Password
                            SecureField("Password", text: $password)
                                .textFieldStyle(RoundedTextFieldStyle())
                            
                            // First name
                            TextField("First Name", text: $firstName)
                                .textFieldStyle(RoundedTextFieldStyle())
                            
                            // Last name
                            TextField("Last Name", text: $lastName)
                                .textFieldStyle(RoundedTextFieldStyle())
                            
                            // Error message
                            if let error = authViewModel.errorMessage {
                                Text(error)
                                    .foregroundColor(.red)
                                    .font(.caption)
                                    .padding(.horizontal)
                            }
                            
                            // Login button
                            Button(action: login) {
                                if authViewModel.isLoading {
                                    ProgressView()
                                        .progressViewStyle(CircularProgressViewStyle(tint: .white))
                                } else {
                                    Text("Login")
                                        .font(.headline)
                                        .foregroundColor(.white)
                                        .frame(maxWidth: .infinity)
                                }
                            }
                            .frame(height: 50)
                            .background(isFormValid ? Color.blue : Color.gray)
                            .cornerRadius(10)
                            .disabled(!isFormValid || authViewModel.isLoading)
                        }
                        .padding(.horizontal, 30)
                        
                        Spacer()
                    }
                }
            }
            .navigationBarHidden(true)
            .sheet(isPresented: $showingGridPicker) {
                GridPickerView(selectedGrid: $authViewModel.selectedGrid, grids: authViewModel.availableGrids)
            }
        }
    }
    
    private var isFormValid: Bool {
        !username.isEmpty && !password.isEmpty && !firstName.isEmpty && !lastName.isEmpty
    }
    
    private func login() {
        Task {
            await authViewModel.login(
                username: username,
                password: password,
                firstName: firstName,
                lastName: lastName
            )
        }
    }
}

struct RoundedTextFieldStyle: TextFieldStyle {
    func _body(configuration: TextField<Self._Label>) -> some View {
        configuration
            .padding()
            .background(Color(.systemBackground))
            .cornerRadius(10)
            .shadow(color: Color.black.opacity(0.1), radius: 5, x: 0, y: 2)
    }
}

struct GridPickerView: View {
    @Binding var selectedGrid: Grid
    let grids: [Grid]
    @Environment(\.dismiss) var dismiss
    
    var body: some View {
        NavigationView {
            List(grids) { grid in
                Button(action: {
                    selectedGrid = grid
                    dismiss()
                }) {
                    HStack {
                        VStack(alignment: .leading) {
                            Text(grid.name)
                                .font(.headline)
                            Text(grid.loginURL)
                                .font(.caption)
                                .foregroundColor(.secondary)
                        }
                        Spacer()
                        if grid.id == selectedGrid.id {
                            Image(systemName: "checkmark")
                                .foregroundColor(.blue)
                        }
                    }
                }
            }
            .navigationTitle("Select Grid")
            .navigationBarItems(trailing: Button("Done") { dismiss() })
        }
    }
}

struct LoginView_Previews: PreviewProvider {
    static var previews: some View {
        LoginView()
            .environmentObject(AuthViewModel())
    }
}