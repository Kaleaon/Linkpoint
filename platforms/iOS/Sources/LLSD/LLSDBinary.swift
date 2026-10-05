import Foundation

public enum LLSDBinaryError: Error, LocalizedError {
    case invalidHeader
    case unexpectedEOF
    case invalidTag(UInt8)
    case stringDecodingFailed
    case missingEndMarker(UInt8)

    public var errorDescription: String? {
        switch self {
        case .invalidHeader: return "Invalid LLSD binary header."
        case .unexpectedEOF: return "Unexpected end of data while parsing LLSD binary."
        case .invalidTag(let tag): return "Invalid LLSD binary tag byte: \(UnicodeScalar(tag))."
        case .stringDecodingFailed: return "Failed to decode UTF-8 string from binary data."
        case .missingEndMarker(let tag): return "Missing expected container end marker tag: \(UnicodeScalar(tag))."
        }
    }
}

public struct LLSDBinary {
    public static let header = Data("<?llsd/binary?>\n".utf8)

    // MARK: - Serializer
    public static func serialize(_ value: LLSDValue, includeHeader: Bool = true) -> Data {
        var data = Data()
        if includeHeader {
            data.append(header)
        }
        encode(value, into: &data)
        return data
    }

    private static func encode(_ value: LLSDValue, into data: inout Data) {
        switch value {
        case .undefined:
            data.append(0x21) // '!'

        case .boolean(let boolVal):
            data.append(boolVal ? 0x31 : 0x30) // '1' or '0'

        case .integer(let intVal):
            data.append(0x69) // 'i'
            var bigEndian = intVal.bigEndian
            withUnsafeBytes(of: &bigEndian) { data.append(contentsOf: $0) }

        case .real(let doubleVal):
            data.append(0x72) // 'r'
            var bigEndian = doubleVal.bitPattern.bigEndian
            withUnsafeBytes(of: &bigEndian) { data.append(contentsOf: $0) }

        case .uuid(let uuidVal):
            data.append(0x75) // 'u'
            let uuidTuple = uuidVal.uuid
            let bytes = [
                uuidTuple.0, uuidTuple.1, uuidTuple.2, uuidTuple.3,
                uuidTuple.4, uuidTuple.5, uuidTuple.6, uuidTuple.7,
                uuidTuple.8, uuidTuple.9, uuidTuple.10, uuidTuple.11,
                uuidTuple.12, uuidTuple.13, uuidTuple.14, uuidTuple.15
            ]
            data.append(contentsOf: bytes)

        case .date(let dateVal):
            data.append(0x64) // 'd'
            let seconds = dateVal.timeIntervalSince1970
            var bigEndian = seconds.bitPattern.bigEndian
            withUnsafeBytes(of: &bigEndian) { data.append(contentsOf: $0) }

        case .string(let strVal):
            data.append(0x73) // 's'
            let utf8 = Data(strVal.utf8)
            var count = UInt32(utf8.count).bigEndian
            withUnsafeBytes(of: &count) { data.append(contentsOf: $0) }
            data.append(utf8)

        case .uri(let urlVal):
            data.append(0x6C) // 'l'
            let utf8 = Data(urlVal.absoluteString.utf8)
            var count = UInt32(utf8.count).bigEndian
            withUnsafeBytes(of: &count) { data.append(contentsOf: $0) }
            data.append(utf8)

        case .binary(let binVal):
            data.append(0x62) // 'b'
            var count = UInt32(binVal.count).bigEndian
            withUnsafeBytes(of: &count) { data.append(contentsOf: $0) }
            data.append(binVal)

        case .array(let arrVal):
            data.append(0x5B) // '['
            var count = UInt32(arrVal.count).bigEndian
            withUnsafeBytes(of: &count) { data.append(contentsOf: $0) }
            for elem in arrVal {
                encode(elem, into: &data)
            }
            data.append(0x5D) // ']'

        case .map(let mapVal):
            data.append(0x7B) // '{'
            var count = UInt32(mapVal.count).bigEndian
            withUnsafeBytes(of: &count) { data.append(contentsOf: $0) }
            for (key, val) in mapVal {
                // Key string format
                data.append(0x73) // 's'
                let keyUtf8 = Data(key.utf8)
                var keyCount = UInt32(keyUtf8.count).bigEndian
                withUnsafeBytes(of: &keyCount) { data.append(contentsOf: $0) }
                data.append(keyUtf8)

                encode(val, into: &data)
            }
            data.append(0x7D) // '}'
        }
    }

    // MARK: - Parser
    public static func parse(_ data: Data) throws -> LLSDValue {
        var reader = LLSDBinaryReader(data: data)
        if reader.hasPrefix(header) {
            reader.advance(by: header.count)
        }
        return try reader.readValue()
    }
}

// MARK: - Binary Reader
public struct LLSDBinaryReader {
    private let data: Data
    private var offset: Int = 0

    public init(data: Data) {
        self.data = data
    }

    public var isAtEnd: Bool {
        offset >= data.count
    }

    public func hasPrefix(_ prefix: Data) -> Bool {
        guard data.count - offset >= prefix.count else { return false }
        let sub = data.subdata(in: offset..<(offset + prefix.count))
        return sub == prefix
    }

    public mutating func advance(by count: Int) {
        offset += count
    }

    public mutating func readByte() throws -> UInt8 {
        guard offset < data.count else { throw LLSDBinaryError.unexpectedEOF }
        let byte = data[data.startIndex + offset]
        offset += 1
        return byte
    }

    public mutating func readData(length: Int) throws -> Data {
        guard offset + length <= data.count else { throw LLSDBinaryError.unexpectedEOF }
        let start = data.startIndex + offset
        let sub = data.subdata(in: start..<(start + length))
        offset += length
        return sub
    }

    public mutating func readUInt32() throws -> UInt32 {
        let bytes = try readData(length: 4)
        let raw = bytes.withUnsafeBytes { $0.load(as: UInt32.self) }
        return UInt32(bigEndian: raw)
    }

    public mutating func readInt32() throws -> Int32 {
        let bytes = try readData(length: 4)
        let raw = bytes.withUnsafeBytes { $0.load(as: Int32.self) }
        return Int32(bigEndian: raw)
    }

    public mutating func readDouble() throws -> Double {
        let bytes = try readData(length: 8)
        let raw = bytes.withUnsafeBytes { $0.load(as: UInt64.self) }
        let bitPattern = UInt64(bigEndian: raw)
        return Double(bitPattern: bitPattern)
    }

    public mutating func readUUID() throws -> UUID {
        let bytesData = try readData(length: 16)
        let bytes = [UInt8](bytesData)
        let tuple: uuid_t = (
            bytes[0], bytes[1], bytes[2], bytes[3],
            bytes[4], bytes[5], bytes[6], bytes[7],
            bytes[8], bytes[9], bytes[10], bytes[11],
            bytes[12], bytes[13], bytes[14], bytes[15]
        )
        return UUID(uuid: tuple)
    }

    public mutating func readString() throws -> String {
        let len = Int(try readUInt32())
        if len == 0 { return "" }
        let bytes = try readData(length: len)
        guard let str = String(data: bytes, encoding: .utf8) else {
            throw LLSDBinaryError.stringDecodingFailed
        }
        return str
    }

    public mutating func readValue() throws -> LLSDValue {
        let tag = try readByte()
        switch tag {
        case 0x21: // '!'
            return .undefined

        case 0x31, 0x74: // '1' or 't'
            return .boolean(true)

        case 0x30, 0x66: // '0' or 'f'
            return .boolean(false)

        case 0x69: // 'i'
            return .integer(try readInt32())

        case 0x72: // 'r'
            return .real(try readDouble())

        case 0x75: // 'u'
            return .uuid(try readUUID())

        case 0x64: // 'd'
            let seconds = try readDouble()
            return .date(Date(timeIntervalSince1970: seconds))

        case 0x73: // 's'
            return .string(try readString())

        case 0x6C: // 'l'
            let str = try readString()
            return .uri(URL(string: str) ?? URL(string: "about:blank")!)

        case 0x62: // 'b'
            let len = Int(try readUInt32())
            let data = try readData(length: len)
            return .binary(data)

        case 0x5B: // '['
            let count = Int(try readUInt32())
            var elements: [LLSDValue] = []
            elements.reserveCapacity(count)
            for _ in 0..<count {
                elements.append(try readValue())
            }
            let endTag = try readByte()
            guard endTag == 0x5D else { // ']'
                throw LLSDBinaryError.missingEndMarker(0x5D)
            }
            return .array(elements)

        case 0x7B: // '{'
            let count = Int(try readUInt32())
            var map: [String: LLSDValue] = [:]
            for _ in 0..<count {
                // Key can be 's' + length + bytes or length + bytes
                let nextTag = try readByte()
                let key: String
                if nextTag == 0x73 { // 's'
                    key = try readString()
                } else {
                    // Put tag back mentally: nextTag was first byte of length
                    let b1 = UInt32(nextTag)
                    let remaining = try readData(length: 3)
                    let b2 = UInt32(remaining[remaining.startIndex])
                    let b3 = UInt32(remaining[remaining.startIndex + 1])
                    let b4 = UInt32(remaining[remaining.startIndex + 2])
                    let len = Int((b1 << 24) | (b2 << 16) | (b3 << 8) | b4)
                    let keyBytes = try readData(length: len)
                    guard let decoded = String(data: keyBytes, encoding: .utf8) else {
                        throw LLSDBinaryError.stringDecodingFailed
                    }
                    key = decoded
                }
                let val = try readValue()
                map[key] = val
            }
            let endTag = try readByte()
            guard endTag == 0x7D else { // '}'
                throw LLSDBinaryError.missingEndMarker(0x7D)
            }
            return .map(map)

        default:
            throw LLSDBinaryError.invalidTag(tag)
        }
    }
}
