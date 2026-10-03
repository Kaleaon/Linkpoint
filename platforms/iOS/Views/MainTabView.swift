import SwiftUI

struct MainTabView: View {
    @EnvironmentObject var authViewModel: AuthViewModel
    @State private var selectedTab = 0
    
    var body: some View {
        TabView(selection: $selectedTab) {
            WorldView()
                .tabItem {
                    Label("World", systemImage: "globe")
                }
                .tag(0)
            
            InventoryView()
                .tabItem {
                    Label("Inventory", systemImage: "folder")
                }
                .tag(1)
            
            ChatView()
                .tabItem {
                    Label("Chat", systemImage: "message")
                }
                .tag(2)
            
            ProfileView()
                .tabItem {
                    Label("Profile", systemImage: "person")
                }
                .tag(3)
        }
    }
}

struct WorldView: View {
    var body: some View {
        NavigationView {
            ZStack {
                Color.black.ignoresSafeArea()
                
                VStack {
                    Text("3D World View")
                        .font(.title)
                        .foregroundColor(.white)
                    
                    Text("OpenGL/Metal rendering will be implemented here")
                        .foregroundColor(.gray)
                        .padding()
                    
                    // Placeholder for 3D rendering
                    Rectangle()
                        .fill(Color.gray.opacity(0.3))
                        .overlay(
                            VStack {
                                Image(systemName: "cube.transparent")
                                    .resizable()
                                    .frame(width: 100, height: 100)
                                    .foregroundColor(.white.opacity(0.5))
                                Text("3D Viewport")
                                    .foregroundColor(.white.opacity(0.7))
                            }
                        )
                }
            }
            .navigationTitle("World")
        }
    }
}

struct InventoryView: View {
    @State private var searchText = ""
    
    var body: some View {
        NavigationView {
            List {
                Section(header: Text("Recent Items")) {
                    InventoryItemRow(name: "My Outfit", icon: "tshirt", type: "Clothing")
                    InventoryItemRow(name: "Favorite Place", icon: "map", type: "Landmark")
                    InventoryItemRow(name: "Photo Album", icon: "photo", type: "Texture")
                }
                
                Section(header: Text("Folders")) {
                    InventoryItemRow(name: "Animations", icon: "figure.walk", type: "Folder")
                    InventoryItemRow(name: "Body Parts", icon: "person.fill", type: "Folder")
                    InventoryItemRow(name: "Clothing", icon: "tshirt.fill", type: "Folder")
                    InventoryItemRow(name: "Gestures", icon: "hand.raised.fill", type: "Folder")
                    InventoryItemRow(name: "Landmarks", icon: "map.fill", type: "Folder")
                    InventoryItemRow(name: "Notecards", icon: "doc.text.fill", type: "Folder")
                    InventoryItemRow(name: "Objects", icon: "cube.fill", type: "Folder")
                    InventoryItemRow(name: "Scripts", icon: "doc.plaintext.fill", type: "Folder")
                    InventoryItemRow(name: "Sounds", icon: "speaker.wave.2.fill", type: "Folder")
                    InventoryItemRow(name: "Textures", icon: "photo.fill", type: "Folder")
                }
            }
            .navigationTitle("Inventory")
            .searchable(text: $searchText, prompt: "Search inventory")
        }
    }
}

struct InventoryItemRow: View {
    let name: String
    let icon: String
    let type: String
    
    var body: some View {
        HStack {
            Image(systemName: icon)
                .foregroundColor(.blue)
                .frame(width: 30)
            
            VStack(alignment: .leading) {
                Text(name)
                    .font(.body)
                Text(type)
                    .font(.caption)
                    .foregroundColor(.secondary)
            }
            
            Spacer()
            
            Image(systemName: "chevron.right")
                .foregroundColor(.secondary)
                .font(.caption)
        }
    }
}

struct ChatView: View {
    @State private var messages: [ChatMessage] = [
        ChatMessage(sender: "System", content: "Welcome to Linkpoint!", timestamp: Date()),
        ChatMessage(sender: "Local Chat", content: "Connected to region", timestamp: Date())
    ]
    @State private var newMessage = ""
    
    var body: some View {
        NavigationView {
            VStack {
                // Messages list
                ScrollView {
                    LazyVStack(alignment: .leading, spacing: 12) {
                        ForEach(messages) { message in
                            ChatMessageView(message: message)
                        }
                    }
                    .padding()
                }
                
                // Input bar
                HStack {
                    TextField("Type a message...", text: $newMessage)
                        .textFieldStyle(RoundedBorderTextFieldStyle())
                    
                    Button(action: sendMessage) {
                        Image(systemName: "paperplane.fill")
                            .foregroundColor(.blue)
                    }
                    .disabled(newMessage.isEmpty)
                }
                .padding()
            }
            .navigationTitle("Chat")
        }
    }
    
    private func sendMessage() {
        guard !newMessage.isEmpty else { return }
        
        let message = ChatMessage(
            sender: "You",
            content: newMessage,
            timestamp: Date()
        )
        messages.append(message)
        newMessage = ""
    }
}

struct ChatMessage: Identifiable {
    let id = UUID()
    let sender: String
    let content: String
    let timestamp: Date
}

struct ChatMessageView: View {
    let message: ChatMessage
    
    var body: some View {
        VStack(alignment: .leading, spacing: 4) {
            HStack {
                Text(message.sender)
                    .font(.caption)
                    .fontWeight(.bold)
                    .foregroundColor(.blue)
                
                Text(message.timestamp, style: .time)
                    .font(.caption2)
                    .foregroundColor(.secondary)
            }
            
            Text(message.content)
                .font(.body)
        }
        .padding(.vertical, 4)
    }
}

struct ProfileView: View {
    @EnvironmentObject var authViewModel: AuthViewModel
    
    var body: some View {
        NavigationView {
            List {
                Section {
                    HStack {
                        Image(systemName: "person.circle.fill")
                            .resizable()
                            .frame(width: 60, height: 60)
                            .foregroundColor(.blue)
                        
                        VStack(alignment: .leading) {
                            Text(authViewModel.currentUser?.fullName ?? "User")
                                .font(.title2)
                                .fontWeight(.bold)
                            Text("@\(authViewModel.currentUser?.username ?? "username")")
                                .font(.subheadline)
                                .foregroundColor(.secondary)
                        }
                        .padding(.leading, 8)
                    }
                    .padding(.vertical, 8)
                }
                
                Section(header: Text("Account")) {
                    NavigationLink(destination: Text("Edit Profile")) {
                        Label("Edit Profile", systemImage: "pencil")
                    }
                    NavigationLink(destination: Text("Settings")) {
                        Label("Settings", systemImage: "gear")
                    }
                    NavigationLink(destination: Text("Preferences")) {
                        Label("Preferences", systemImage: "slider.horizontal.3")
                    }
                }
                
                Section(header: Text("Grid")) {
                    HStack {
                        Text("Current Grid")
                        Spacer()
                        Text(authViewModel.selectedGrid.name)
                            .foregroundColor(.secondary)
                    }
                }
                
                Section {
                    Button(action: { authViewModel.logout() }) {
                        HStack {
                            Spacer()
                            Text("Logout")
                                .foregroundColor(.red)
                            Spacer()
                        }
                    }
                }
            }
            .navigationTitle("Profile")
        }
    }
}

struct MainTabView_Previews: PreviewProvider {
    static var previews: some View {
        MainTabView()
            .environmentObject(AuthViewModel())
    }
}