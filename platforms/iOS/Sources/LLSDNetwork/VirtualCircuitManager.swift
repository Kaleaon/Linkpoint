import Foundation

#if canImport(Combine)
import Combine
#endif

#if canImport(Network)
import Network
#endif

public struct IncomingPacket: Identifiable {
    public let id = UUID()
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

public class VirtualCircuitManager {
    public var currentSequenceNumber: UInt32 = 0
    private var pendingAcks: Set<UInt32> = []
    private var receivedSequenceNumbers: Set<UInt32> = []

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
    private var listener: NWListener?
    #endif

    public init() {}

    public func connect(host: String, port: UInt16) {
        #if canImport(Network)
        let endpoint = NWEndpoint.hostPort(host: NWEndpoint.Host(host), port: NWEndpoint.Port(integerLiteral: port))
        let nwConn = NWConnection(to: endpoint, using: .udp)
        self.connection = nwConn

        nwConn.stateUpdateHandler = { state in
            switch state {
            case .ready:
                self.startReceiving()
            default:
                break
            }
        }
        nwConn.start(queue: .global())
        #endif
    }

    public func startListener(port: UInt16) throws {
        #if canImport(Network)
        guard let nwPort = NWEndpoint.Port(rawValue: port) else { return }
        let nwListener = try NWListener(using: .udp, on: nwPort)
        self.listener = nwListener

        nwListener.newConnectionHandler = { [weak self] newConn in
            newConn.start(queue: .global())
            self?.receiveFromConnection(newConn)
        }
        nwListener.start(queue: .global())
        #endif
    }

    private func startReceiving() {
        #if canImport(Network)
        guard let conn = connection else { return }
        receiveFromConnection(conn)
        #endif
    }

    #if canImport(Network)
    private func receiveFromConnection(_ conn: NWConnection) {
        conn.receiveMessage { [weak self] content, context, isComplete, error in
            if let data = content {
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

        // Extract message ID (first 1-4 bytes depending on frequency)
        let messageID: UInt32
        if decompressedPayload.count >= 4 {
            let msgBytes = decompressedPayload.subdata(in: 0..<4)
            let rawMsg = msgBytes.withUnsafeBytes { $0.load(as: UInt32.self) }
            messageID = UInt32(bigEndian: rawMsg)
        } else if decompressedPayload.count >= 1 {
            messageID = UInt32(decompressedPayload[0])
        } else {
            messageID = 0
        }

        let body: Data
        if decompressedPayload.count >= 4 {
            body = decompressedPayload.subdata(in: 4..<decompressedPayload.endIndex)
        } else {
            body = Data()
        }

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

        var header = PacketHeader(flags: flags, sequenceNumber: currentSequenceNumber)

        let compressedBody = Zerocode.compress(payload)
        if compressedBody.count < payload.count {
            header.flags.insert(.zerocoded)
        }

        var packetData = header.serialize()
        var msgBig = messageID.bigEndian
        withUnsafeBytes(of: &msgBig) { packetData.append(contentsOf: $0) }
        packetData.append(header.flags.contains(.zerocoded) ? compressedBody : payload)

        #if canImport(Network)
        if let conn = connection {
            conn.send(content: packetData, completion: .contentProcessed({ _ in }))
        }
        #endif

        return packetData
    }

    public func pendingAckCount() -> Int {
        pendingAcks.count
    }

    public func popPendingAcks() -> [UInt32] {
        let acks = Array(pendingAcks)
        pendingAcks.removeAll()
        return acks
    }
}
