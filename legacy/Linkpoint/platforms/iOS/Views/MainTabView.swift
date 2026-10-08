import SwiftUI

struct MainTabView: View {
    @EnvironmentObject private var authViewModel: AuthViewModel
    @State private var selectedTab = 0

    var body: some View {
        TabView(selection: $selectedTab) {
            WorldView()
                .tabItem { Label("World", systemImage: "globe.americas") }
                .tag(0)

            ChatView()
                .tabItem { Label("Chat", systemImage: "message") }
                .tag(1)

            FriendsView()
                .tabItem { Label("Friends", systemImage: "person.2") }
                .tag(2)

            InventoryView()
                .tabItem { Label("Inventory", systemImage: "square.grid.2x2") }
                .tag(3)

            ProfileView(user: authViewModel.currentUser, logoutAction: authViewModel.logout)
                .tabItem { Label("Profile", systemImage: "person.circle") }
                .tag(4)
        }
    }
}

// MARK: - World View

struct WorldView: View {
    var body: some View {
        NavigationView {
            VStack(spacing: 18) {
                MetalWorldView()
                    .frame(height: 280)
                    .clipShape(RoundedRectangle(cornerRadius: 20))
                    .shadow(radius: 6)

                Text("Real-time rendering via Metal")
                    .font(.headline)

                WorldControls()
            }
            .padding()
            .navigationTitle("World")
        }
    }
}

private struct WorldControls: View {
    var body: some View {
        VStack(alignment: .leading, spacing: 12) {
            Text("Controls").font(.headline)
            LazyVGrid(columns: Array(repeating: GridItem(.flexible(), spacing: 12), count: 3), spacing: 12) {
                ForEach(Control.allCases, id: \.self) { control in
                    VStack(spacing: 4) {
                        Image(systemName: control.icon)
                        Text(control.title)
                    }
                    .frame(maxWidth: .infinity, minHeight: 60)
                    .background(Color(.secondarySystemBackground))
                    .cornerRadius(10)
                }
            }
        }
    }

    private enum Control: CaseIterable {
        case forward, back, left, right, camera, voice

        var title: String {
            switch self {
            case .forward: return "Forward"
            case .back: return "Back"
            case .left: return "Left"
            case .right: return "Right"
            case .camera: return "Camera"
            case .voice: return "Voice"
            }
        }

        var icon: String {
            switch self {
            case .forward: return "arrow.up"
            case .back: return "arrow.down"
            case .left: return "arrow.left"
            case .right: return "arrow.right"
            case .camera: return "camera"
            case .voice: return "mic"
            }
        }
    }
}

// MARK: - Chat View

struct ChatView: View {
    @State private var messages: [ChatMessage] = [
        .sample,
        ChatMessage(id: UUID().uuidString, senderId: "you", senderName: "You", content: "Hi there!", timestamp: Date(), isFromCurrentUser: true)
    ]
    @State private var newMessage = ""

    var body: some View {
        NavigationView {
            VStack(spacing: 0) {
                ScrollViewReader { proxy in
                    ScrollView {
                        LazyVStack(spacing: 12) {
                            ForEach(messages) { message in
                                ChatBubble(message: message)
                                    .id(message.id)
                            }
                        }
                        .padding()
                    }
                    .onChange(of: messages.count) { _ in
                        if let last = messages.last {
                            proxy.scrollTo(last.id, anchor: .bottom)
                        }
                    }
                }

                HStack(spacing: 12) {
                    TextField("Message", text: $newMessage)
                        .textFieldStyle(.roundedBorder)
                        .onSubmit(send)
                    Button("Send", action: send)
                        .disabled(newMessage.trimmingCharacters(in: .whitespaces).isEmpty)
                }
                .padding()
            }
            .navigationTitle("Chat")
        }
    }

    private func send() {
        let trimmed = newMessage.trimmingCharacters(in: .whitespacesAndNewlines)
        guard !trimmed.isEmpty else { return }
        messages.append(ChatMessage(id: UUID().uuidString, senderId: "you", senderName: "You", content: trimmed, timestamp: Date(), isFromCurrentUser: true))
        newMessage = ""
    }
}

private struct ChatBubble: View {
    let message: ChatMessage

    var body: some View {
        HStack {
            if message.isFromCurrentUser { Spacer() }
            VStack(alignment: message.isFromCurrentUser ? .trailing : .leading, spacing: 6) {
                Text(message.senderName).font(.caption).foregroundColor(.secondary)
                Text(message.content)
                    .padding(12)
                    .background(message.isFromCurrentUser ? Color.accentColor : Color(.secondarySystemBackground))
                    .foregroundColor(message.isFromCurrentUser ? .white : .primary)
                    .cornerRadius(12)
            }
            if !message.isFromCurrentUser { Spacer() }
        }
    }
}

// MARK: - Friends View

struct FriendsView: View {
    @State private var friends: [Friend] = [.sample, Friend.sample]
    @State private var search = ""

    var filteredFriends: [Friend] {
        guard !search.isEmpty else { return friends }
        return friends.filter { $0.fullName.localizedCaseInsensitiveContains(search) }
    }

    var body: some View {
        NavigationView {
            List(filteredFriends) { friend in
                HStack(spacing: 12) {
                    Circle()
                        .fill(Color.accentColor.opacity(0.2))
                        .frame(width: 44, height: 44)
                        .overlay(Text(String(friend.firstName.prefix(1) + friend.lastName.prefix(1))).fontWeight(.medium))
                    VStack(alignment: .leading, spacing: 4) {
                        Text(friend.fullName).font(.headline)
                        Text(friend.status ?? "Online").font(.caption).foregroundColor(.secondary)
                    }
                    Spacer()
                    Circle()
                        .fill(friend.isOnline ? Color.green : Color.gray)
                        .frame(width: 10, height: 10)
                }
                .padding(.vertical, 4)
            }
            .listStyle(.insetGrouped)
            .navigationTitle("Friends")
            .searchable(text: $search)
        }
    }
}

// MARK: - Inventory View

struct InventoryView: View {
    @State private var items: [InventoryItem] = [.sample]
    @State private var selectedType: InventoryItem.ItemType = .clothing

    var body: some View {
        NavigationView {
            VStack(spacing: 16) {
                Picker("Type", selection: $selectedType) {
                    ForEach(InventoryItem.ItemType.allCases, id: \.self) { type in
                        Text(type.rawValue.capitalized).tag(type)
                    }
                }
                .pickerStyle(.segmented)

                ScrollView {
                    LazyVGrid(columns: [GridItem(.flexible()), GridItem(.flexible())], spacing: 16) {
                        ForEach(items.filter { $0.type == selectedType }) { item in
                            VStack(alignment: .leading, spacing: 8) {
                                RoundedRectangle(cornerRadius: 12)
                                    .fill(Color(.secondarySystemBackground))
                                    .frame(height: 80)
                                    .overlay(Image(systemName: item.type.iconName).font(.title))
                                Text(item.name).font(.headline)
                                Text(item.description).font(.caption).foregroundColor(.secondary)
                            }
                            .padding()
                            .background(Color(.systemBackground))
                            .cornerRadius(16)
                            .shadow(color: Color.black.opacity(0.05), radius: 4, x: 0, y: 2)
                        }
                    }
                    .padding(.horizontal)
                }
            }
            .padding(.top)
            .navigationTitle("Inventory")
        }
    }
}

// MARK: - Profile View

struct ProfileView: View {
    let user: User?
    let logoutAction: () -> Void

    var body: some View {
        NavigationView {
            ScrollView {
                VStack(spacing: 24) {
                    profileHeader
                    stats
                    Button(role: .destructive, action: logoutAction) {
                        Label("Logout", systemImage: "arrow.right.square")
                            .frame(maxWidth: .infinity)
                    }
                    .buttonStyle(.borderedProminent)
                }
                .padding()
            }
            .navigationTitle("Profile")
        }
    }

    private var profileHeader: some View {
        VStack(spacing: 12) {
            Circle()
                .fill(Color.accentColor.opacity(0.15))
                .frame(width: 100, height: 100)
                .overlay(Text(initials).font(.largeTitle).fontWeight(.bold).foregroundColor(.accentColor))
            Text(user?.displayName ?? "Resident")
                .font(.title2).fontWeight(.semibold)
            Text("Agent ID: \(user?.agentId ?? "—")").font(.caption).foregroundColor(.secondary)
        }
        .frame(maxWidth: .infinity)
        .padding()
        .background(Color(.secondarySystemBackground))
        .cornerRadius(16)
    }

    private var stats: some View {
        VStack(spacing: 12) {
            statRow(title: "Balance", value: "L$ \(String(format: "%.2f", user?.balance ?? 0))")
            statRow(title: "Status", value: user?.isOnline == true ? "Online" : "Offline")
            statRow(title: "Member Since", value: formattedDate(user?.lastSeen))
        }
        .padding()
        .background(Color(.secondarySystemBackground))
        .cornerRadius(16)
    }

    private func statRow(title: String, value: String) -> some View {
        HStack {
            Text(title).foregroundColor(.secondary)
            Spacer()
            Text(value).fontWeight(.medium)
        }
    }

    private func formattedDate(_ date: Date?) -> String {
        guard let date else { return "—" }
        let formatter = DateFormatter()
        formatter.dateStyle = .medium
        return formatter.string(from: date)
    }

    private var initials: String {
        guard let user else { return "LR" }
        let first = user.firstName.first.map(String.init) ?? "L"
        let last = user.lastName.first.map(String.init) ?? "R"
        return first + last
    }
}
