import SwiftUI
import Ktheme

enum MainTabItem: String, CaseIterable, Identifiable {
    case world = "World"
    case inventory = "Inventory"
    case chat = "Chat"
    case profile = "Profile"

    var id: String { rawValue }

    var icon: String {
        switch self {
        case .world: return "globe"
        case .inventory: return "folder"
        case .chat: return "message"
        case .profile: return "person"
        }
    }

    var tag: Int {
        switch self {
        case .world: return 0
        case .inventory: return 1
        case .chat: return 2
        case .profile: return 3
        }
    }
}

struct MainTabView: View {
    @EnvironmentObject var authViewModel: AuthViewModel
    @Environment(\.horizontalSizeClass) var horizontalSizeClass
    @State private var selectedTab = 0
    @State private var selectedSidebarItem: MainTabItem? = .world

    var body: some View {
        if horizontalSizeClass == .regular {
            NavigationSplitView {
                List(MainTabItem.allCases, selection: $selectedSidebarItem) { item in
                    NavigationLink(value: item) {
                        Label(item.rawValue, systemImage: item.icon)
                    }
                }
                .navigationTitle("Linkpoint")
            } detail: {
                detailView(for: selectedSidebarItem ?? .world)
            }
        } else {
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

    @ViewBuilder
    private func detailView(for item: MainTabItem) -> some View {
        switch item {
        case .world:
            WorldView()
        case .inventory:
            InventoryView()
        case .chat:
            ChatView()
        case .profile:
            ProfileView()
        }
    }
}

struct WorldView: View {
    var body: some View {
        NavigationView {
            ZStack {
                Ktheme.sceneBackground.ignoresSafeArea()

                VStack {
                    Text("3D World View")
                        .font(.title)
                        .foregroundColor(Ktheme.textPrimary)

                    Text("OpenGL/Metal rendering will be implemented here")
                        .foregroundColor(Ktheme.textSecondary)
                        .padding()

                    // Placeholder for 3D rendering
                    Rectangle()
                        .fill(Ktheme.viewportPlaceholder)
                        .overlay(
                            VStack {
                                Image(systemName: "cube.transparent")
                                    .resizable()
                                    .frame(width: 100, height: 100)
                                    .foregroundColor(Ktheme.textMuted)
                                Text("3D Viewport")
                                    .foregroundColor(Ktheme.textMuted)
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
                .foregroundColor(Ktheme.brandPrimary)
                .frame(width: 30)

            VStack(alignment: .leading) {
                Text(name)
                    .font(.body)
                Text(type)
                    .font(.caption)
                    .foregroundColor(Ktheme.textSecondary)
            }

            Spacer()

            Image(systemName: "chevron.right")
                .foregroundColor(Ktheme.textSecondary)
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
                            .foregroundColor(Ktheme.brandPrimary)
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
                    .foregroundColor(Ktheme.brandPrimary)

                Text(message.timestamp, style: .time)
                    .font(.caption2)
                    .foregroundColor(Ktheme.textSecondary)
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
                            .foregroundColor(Ktheme.brandPrimary)

                        VStack(alignment: .leading) {
                            Text(authViewModel.currentUser?.fullName ?? "User")
                                .font(.title2)
                                .fontWeight(.bold)
                            Text("@\(authViewModel.currentUser?.username ?? "username")")
                                .font(.subheadline)
                                .foregroundColor(Ktheme.textSecondary)
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
                            .foregroundColor(Ktheme.textSecondary)
                    }
                }

                Section {
                    Button(action: { authViewModel.logout() }) {
                        HStack {
                            Spacer()
                            Text("Logout")
                                .foregroundColor(Ktheme.error)
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
