import Foundation
import UIKit

struct User: Codable, Identifiable {
    let id: String
    let agentId: String
    let sessionId: String
    let firstName: String
    let lastName: String
    let displayName: String
    let email: String?
    let avatarURL: URL?
    let isOnline: Bool
    let lastSeen: Date?
    let balance: Double

    var fullName: String {
        "\(firstName) \(lastName)"
    }

    static var sample: User {
        User(
            id: UUID().uuidString,
            agentId: "agent-demo",
            sessionId: "session-demo",
            firstName: "Demo",
            lastName: "Resident",
            displayName: "Demo Resident",
            email: "demo@example.com",
            avatarURL: nil,
            isOnline: true,
            lastSeen: Date(),
            balance: 1024.0
        )
    }
}

struct AuthResponse: Codable {
    let success: Bool
    let message: String?
    let user: User?
    let token: String?
}

struct LoginRequest: Codable {
    let firstName: String
    let lastName: String
    let password: String
    let startLocation: String
    let channel: String
    let version: String
    let platform: String
    let mac: String
    let id0: String
    let agreeToTos: Bool
    let readCritical: Bool

    init(firstName: String, lastName: String, password: String) {
        self.firstName = firstName
        self.lastName = lastName
        self.password = password
        self.startLocation = "last"
        self.channel = "Linkpoint"
        self.version = "1.0.0"
        self.platform = "iOS"
        let deviceId = UIDevice.current.identifierForVendor?.uuidString ?? UUID().uuidString
        self.mac = deviceId
        self.id0 = deviceId
        self.agreeToTos = true
        self.readCritical = true
    }
}

struct ChatMessage: Codable, Identifiable {
    let id: String
    let senderId: String
    let senderName: String
    let content: String
    let timestamp: Date
    let isFromCurrentUser: Bool

    static var sample: ChatMessage {
        ChatMessage(
            id: UUID().uuidString,
            senderId: "system",
            senderName: "System",
            content: "Welcome to Linkpoint!",
            timestamp: Date(),
            isFromCurrentUser: false
        )
    }
}

struct Friend: Codable, Identifiable {
    let id: String
    let agentId: String
    let firstName: String
    let lastName: String
    let displayName: String
    let isOnline: Bool
    let status: String?

    var fullName: String {
        "\(firstName) \(lastName)"
    }

    static var sample: Friend {
        Friend(
            id: UUID().uuidString,
            agentId: "friend-001",
            firstName: "Jane",
            lastName: "Doe",
            displayName: "Jane Doe",
            isOnline: true,
            status: "Available"
        )
    }
}

struct InventoryItem: Codable, Identifiable {
    struct Permissions: Codable {
        let canModify: Bool
        let canCopy: Bool
        let canTransfer: Bool
    }

    enum ItemType: String, Codable, CaseIterable {
        case clothing, object, texture, sound, gesture, notecard, script, landmark, callingCard

        var iconName: String {
            switch self {
            case .clothing: return "tshirt"
            case .object: return "cube"
            case .texture: return "photo"
            case .sound: return "speaker.wave.2"
            case .gesture: return "hand.wave"
            case .notecard: return "note.text"
            case .script: return "terminal"
            case .landmark: return "mappin.and.ellipse"
            case .callingCard: return "person.crop.rectangle"
            }
        }
    }

    let id: String
    let name: String
    let description: String
    let type: ItemType
    let createdAt: Date
    let permissions: Permissions

    static var sample: InventoryItem {
        InventoryItem(
            id: UUID().uuidString,
            name: "Starter Jacket",
            description: "Classic Linkpoint jacket",
            type: .clothing,
            createdAt: Date(),
            permissions: Permissions(canModify: true, canCopy: true, canTransfer: false)
        )
    }
}
