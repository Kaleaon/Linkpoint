import Foundation
#if canImport(Network)
import Network
#endif
#if canImport(Combine)
import Combine
#endif

public struct IncomingPacket: Sendable {
    public let header: PacketHeader
    public let messageID: UInt32
    public let payload: Data
    public let rawData: Data
    public let timestamp: Date

    public init(header: PacketHeader, messageID: UInt32, payload: Data, rawData: Data, timestamp: Date = Date()) {
        self.header = header
        self.messageID = messageID
        self.payload = payload
        self.rawData = rawData
        self.timestamp = timestamp
    }
}

public final class VirtualCircuitManager: @unchecked Sendable {
    public private(set) var currentSequenceNumber: UInt32 = 0
    public private(set) var unackedPackets: [UInt32: Data] = [:]
    public private(set) var receivedSequenceNumbers: Set<UInt32> = []
    public private(set) var pendingAcks: Set<UInt32> = []

    #if canImport(Combine)
    private let packetSubject = PassthroughSubject<IncomingPacket, Never>()
    public var packetPublisher: AnyPublisher<IncomingPacket, Never> {
        packetSubject.eraseToAnyPublisher()
    }
    #endif

    private var streamContinuation: AsyncStream<IncomingPacket>.Continuation?
    public lazy var packetStream: AsyncStream<IncomingPacket> = {
        AsyncStream { continuation in
            self.streamContinuation = continuation
        }
    }()

    #if canImport(Network)
    private var connection: NWConnection?
    #endif

    public init() {}

    #if canImport(Network)
    public func connect(host: String, port: UInt16) {
        let nwHost = NWEndpoint.Host(host)
        let nwPort = NWEndpoint.Port(rawValue: port)!
        let conn = NWConnection(host: nwHost, port: nwPort, using: .udp)
        self.connection = conn

        conn.stateUpdateHandler = { state in
            switch state {
            case .ready:
                break
            case .failed(let err):
                print("VirtualCircuit UDP connection failed: \(err)")
            default:
                break
            }
        }
        conn.start(queue: .global())
        receiveFromConnection(conn)
    }

    public func disconnect() {
        connection?.cancel()
        connection = nil
    }

    private func receiveFromConnection(_ conn: NWConnection) {
        conn.receive(minimumIncompleteLength: 1, maximumLength: 65535) { [weak self] content, _, _, error in
            if let data = content, !data.isEmpty {
                self?.processIncomingData(data)
            }
            if error == nil {
                self?.receiveFromConnection(conn)
            }
        }
    }
    #endif

    public func processIncomingData(_ rawData: Data) {
        guard let (header, payloadData) = try? PacketHeader.parse(from: rawData) else {
            return
        }

        // Decompress zerocode if flagged
        let decompressedPayload: Data
        if header.flags.contains(.zerocoded) {
            decompressedPayload = Zerocode.decompress(payloadData)
        } else {
            decompressedPayload = payloadData
        }

        let messageID = header.messageId
        let body = decompressedPayload

        // Reliability tracking
        receivedSequenceNumbers.insert(header.sequenceNumber)
        if header.flags.contains(.reliable) {
            pendingAcks.insert(header.sequenceNumber)
        }

        let packet = IncomingPacket(
            header: header,
            messageID: messageID,
            payload: body,
            rawData: rawData
        )

        #if canImport(Combine)
        packetSubject.send(packet)
        #endif

        streamContinuation?.yield(packet)
    }

    public func sendPacket(messageID: UInt32, payload: Data, reliable: Bool = true) -> Data {
        currentSequenceNumber += 1
        var flags: PacketHeaderFlags = []
        if reliable { flags.insert(.reliable) }

        let compressedBody = Zerocode.compress(payload)
        let useZerocode = compressedBody.count < payload.count
        if useZerocode { flags.insert(.zerocoded) }

        let header = PacketHeader(flags: flags, sequenceNumber: currentSequenceNumber, messageId: messageID)

        var packetData = header.serialize()
        packetData.append(useZerocode ? compressedBody : payload)

        #if canImport(Network)
        if let conn = connection {
            conn.send(content: packetData, completion: .contentProcessed({ _ in }))
        }
        #endif

        if reliable {
            unackedPackets[currentSequenceNumber] = packetData
        }

        return packetData
    }

    public func acknowledgePacket(sequenceNumber: UInt32) {
        unackedPackets.removeValue(forKey: sequenceNumber)
    }
}
