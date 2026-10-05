import Foundation

public struct User: Codable, Identifiable {
    public let id: String
    public let agentId: String
    public let sessionId: String
    public let firstName: String
    public let lastName: String
    public let displayName: String
    public let email: String?
    public var avatarURL: URL?
    public var isOnline: Bool
    public var lastSeen: Date?
    public var balance: Double

    public init(
        id: String = UUID().uuidString,
        agentId: String,
        sessionId: String,
        firstName: String,
        lastName: String,
        displayName: String? = nil,
        email: String? = nil,
        avatarURL: URL? = nil,
        isOnline: Bool = true,
        lastSeen: Date? = Date(),
        balance: Double = 0.0
    ) {
        self.id = id
        self.agentId = agentId
        self.sessionId = sessionId
        self.firstName = firstName
        self.lastName = lastName
        self.displayName = displayName ?? "\(firstName) \(lastName)"
        self.email = email
        self.avatarURL = avatarURL
        self.isOnline = isOnline
        self.lastSeen = lastSeen
        self.balance = balance
    }

    public var fullName: String {
        "\(firstName) \(lastName)"
    }

    public static var sample: User {
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

public struct Grid: Codable, Identifiable, Hashable {
    public var id: String { name }
    public let name: String
    public let loginURL: String

    public init(name: String, loginURL: String) {
        self.name = name
        self.loginURL = loginURL
    }

    public static let secondLife = Grid(name: "Second Life Main Grid", loginURL: "https://login.agni.lindenlab.com/cgi-bin/login.cgi")
    public static let secondLifeBeta = Grid(name: "Second Life Beta Grid (Aditi)", loginURL: "https://login.aditi.lindenlab.com/cgi-bin/login.cgi")
    public static let osgrid = Grid(name: "OSGrid", loginURL: "http://login.osgrid.org/")

    public static var defaultGrids: [Grid] {
        [.secondLife, .secondLifeBeta, .osgrid]
    }
}
