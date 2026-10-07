import Foundation

public struct PacketHeaderFlags: OptionSet {
    public let rawValue: UInt8

    public init(rawValue: UInt8) {
        self.rawValue = rawValue
    }

    public static let zerocoded    = PacketHeaderFlags(rawValue: 0x80)
    public static let reliable     = PacketHeaderFlags(rawValue: 0x40)
    public static let resent       = PacketHeaderFlags(rawValue: 0x20)
    public static let appendedAcks = PacketHeaderFlags(rawValue: 0x10)
    public static let ack          = PacketHeaderFlags(rawValue: 0x10)
}

public enum PacketFrequency {
    case high
    case medium
    case low
    case fixed
}

public struct PacketHeader {
    public var flags: PacketHeaderFlags
    public var sequenceNumber: UInt32
    public var extraBytesCount: Int
    public var messageId: UInt32
    public var frequency: PacketFrequency
    public var extraData: Data
    public var acks: [UInt32]
    public var bodyOffset: Int

    public init(
        flags: PacketHeaderFlags,
        sequenceNumber: UInt32,
        extraBytesCount: Int = 0,
        messageId: UInt32 = 0,
        frequency: PacketFrequency = .high,
        extraData: Data = Data(),
        acks: [UInt32] = [],
        bodyOffset: Int = 7
    ) {
        self.flags = flags
        self.sequenceNumber = sequenceNumber
        self.extraBytesCount = extraBytesCount
        self.messageId = messageId
        self.frequency = frequency
        self.extraData = extraData
        self.acks = acks
        self.bodyOffset = bodyOffset
    }

    public static func parse(from data: Data) throws -> (PacketHeader, Data) {
        guard data.count >= 7 else {
            throw PacketHeaderError.invalidLength(data.count)
        }

        let flags = PacketHeaderFlags(rawValue: data[data.startIndex])
        let seqBytes = data.subdata(in: (data.startIndex + 1)..<(data.startIndex + 5))
        let rawSeq = seqBytes.withUnsafeBytes { $0.load(as: UInt32.self) }
        let seq = UInt32(bigEndian: rawSeq)
        let extra = Int(data[data.startIndex + 5])

        var offset = data.startIndex + 6 + extra
        guard offset < data.endIndex else {
            throw PacketHeaderError.invalidLength(data.count)
        }

        let extraData = extra > 0 ? data.subdata(in: (data.startIndex + 6)..<offset) : Data()

        let first = data[offset]
        offset += 1

        var freq: PacketFrequency = .high
        var msgId: UInt32 = 0

        if first != 0xFF {
            freq = .high
            msgId = UInt32(first)
        } else {
            guard offset < data.endIndex else { throw PacketHeaderError.invalidLength(data.count) }
            let second = data[offset]
            offset += 1
            if second != 0xFF {
                freq = .medium
                msgId = UInt32(second)
            } else {
                guard offset < data.endIndex else { throw PacketHeaderError.invalidLength(data.count) }
                let high = data[offset]
                offset += 1
                if high == 0xFF {
                    guard offset < data.endIndex else { throw PacketHeaderError.invalidLength(data.count) }
                    freq = .fixed
                    msgId = UInt32(data[offset])
                    offset += 1
                } else {
                    guard offset < data.endIndex else { throw PacketHeaderError.invalidLength(data.count) }
                    let low = data[offset]
                    offset += 1
                    freq = .low
                    msgId = (UInt32(high) << 8) | UInt32(low)
                }
            }
        }

        var acks: [UInt32] = []
        var payloadEnd = data.endIndex
        if flags.contains(.appendedAcks) && !data.isEmpty {
            let ackCount = Int(data[data.endIndex - 1])
            let acksLength = ackCount * 4 + 1
            if data.count >= offset + acksLength {
                payloadEnd = data.endIndex - acksLength
                for i in 0..<ackCount {
                    let ackStart = payloadEnd + (i * 4)
                    let ackBytes = data.subdata(in: ackStart..<(ackStart + 4))
                    let rawAck = ackBytes.withUnsafeBytes { $0.load(as: UInt32.self) }
                    acks.append(UInt32(bigEndian: rawAck))
                }
            }
        }

        let header = PacketHeader(
            flags: flags,
            sequenceNumber: seq,
            extraBytesCount: extra,
            messageId: msgId,
            frequency: freq,
            extraData: extraData,
            acks: acks,
            bodyOffset: offset
        )

        let payload = data.subdata(in: offset..<payloadEnd)
        return (header, payload)
    }

    public func serialize() -> Data {
        var data = Data()
        data.append(flags.rawValue)
        var seqBig = sequenceNumber.bigEndian
        withUnsafeBytes(of: &seqBig) { data.append(contentsOf: $0) }
        data.append(UInt8(min(extraBytesCount, 255)))
        if extraBytesCount > 0 {
            if extraData.count == extraBytesCount {
                data.append(extraData)
            } else {
                data.append(Data(repeating: 0, count: extraBytesCount))
            }
        }
        switch frequency {
        case .high:
            data.append(UInt8(messageId & 0xFF))
        case .medium:
            data.append(0xFF)
            data.append(UInt8(messageId & 0xFF))
        case .low:
            data.append(0xFF)
            data.append(0xFF)
            data.append(UInt8((messageId >> 8) & 0xFF))
            data.append(UInt8(messageId & 0xFF))
        case .fixed:
            data.append(0xFF)
            data.append(0xFF)
            data.append(0xFF)
            data.append(UInt8(messageId & 0xFF))
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
