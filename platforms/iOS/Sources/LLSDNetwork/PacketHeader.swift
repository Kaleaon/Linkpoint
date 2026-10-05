import Foundation

public struct PacketHeaderFlags: OptionSet {
    public let rawValue: UInt8

    public init(rawValue: UInt8) {
        self.rawValue = rawValue
    }

    public static let ack       = PacketHeaderFlags(rawValue: 0x80)
    public static let resent    = PacketHeaderFlags(rawValue: 0x40)
    public static let reliable  = PacketHeaderFlags(rawValue: 0x20)
    public static let zerocoded = PacketHeaderFlags(rawValue: 0x10)
}

public struct PacketHeader {
    public var flags: PacketHeaderFlags
    public var sequenceNumber: UInt32
    public var extraBytesCount: Int

    public init(flags: PacketHeaderFlags, sequenceNumber: UInt32, extraBytesCount: Int = 0) {
        self.flags = flags
        self.sequenceNumber = sequenceNumber
        self.extraBytesCount = extraBytesCount
    }

    public static func parse(from data: Data) throws -> (PacketHeader, Data) {
        guard data.count >= 6 else {
            throw PacketHeaderError.invalidLength(data.count)
        }

        let flags = PacketHeaderFlags(rawValue: data[data.startIndex])
        let seqBytes = data.subdata(in: (data.startIndex + 1)..<(data.startIndex + 5))
        let rawSeq = seqBytes.withUnsafeBytes { $0.load(as: UInt32.self) }
        let seq = UInt32(bigEndian: rawSeq)
        let extra = Int(data[data.startIndex + 5])

        let header = PacketHeader(flags: flags, sequenceNumber: seq, extraBytesCount: extra)
        let payloadStart = data.startIndex + 6 + extra
        guard payloadStart <= data.endIndex else {
            throw PacketHeaderError.invalidLength(data.count)
        }
        let payload = data.subdata(in: payloadStart..<data.endIndex)
        return (header, payload)
    }

    public func serialize() -> Data {
        var data = Data()
        data.append(flags.rawValue)
        var seqBig = sequenceNumber.bigEndian
        withUnsafeBytes(of: &seqBig) { data.append(contentsOf: $0) }
        data.append(UInt8(min(extraBytesCount, 255)))
        if extraBytesCount > 0 {
            data.append(Data(repeating: 0, count: extraBytesCount))
        }
        return data
    }
}

public enum PacketHeaderError: Error, LocalizedError {
    case invalidLength(Int)

    public var errorDescription: String? {
        switch self {
        case .invalidLength(let len): return "Packet length too short for header (\(len) bytes)."
        }
    }
}
