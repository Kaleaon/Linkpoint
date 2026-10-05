import Foundation

public enum LLSDValue: Equatable, Hashable, Codable {
    case undefined
    case boolean(Bool)
    case integer(Int32)
    case real(Double)
    case string(String)
    case uuid(UUID)
    case date(Date)
    case uri(URL)
    case binary(Data)
    case array([LLSDValue])
    case map([String: LLSDValue])

    // MARK: - Convenient Accessors
    public var isUndefined: Bool {
        if case .undefined = self { return true }
        return false
    }

    public var asBool: Bool? {
        switch self {
        case .boolean(let val): return val
        case .integer(let val): return val != 0
        case .real(let val): return val != 0
        case .string(let val):
            let lower = val.lowercased().trimmingCharacters(in: .whitespaces)
            return lower == "true" || lower == "1" || lower == "t"
        default: return nil
        }
    }

    public var asInt: Int32? {
        switch self {
        case .integer(let val): return val
        case .boolean(let val): return val ? 1 : 0
        case .real(let val): return Int32(val)
        case .string(let val): return Int32(val)
        default: return nil
        }
    }

    public var asReal: Double? {
        switch self {
        case .real(let val): return val
        case .integer(let val): return Double(val)
        case .boolean(let val): return val ? 1.0 : 0.0
        case .string(let val): return Double(val)
        default: return nil
        }
    }

    public var asString: String? {
        switch self {
        case .string(let val): return val
        case .uuid(let val): return val.uuidString.lowercased()
        case .uri(let val): return val.absoluteString
        case .boolean(let val): return val ? "true" : "false"
        case .integer(let val): return String(val)
        case .real(let val): return String(val)
        case .binary(let val): return val.base64EncodedString()
        default: return nil
        }
    }

    public var asUUID: UUID? {
        switch self {
        case .uuid(let val): return val
        case .string(let val): return UUID(uuidString: val)
        case .binary(let val) where val.count == 16:
            let bytes = [UInt8](val)
            let tuple: uuid_t = (
                bytes[0], bytes[1], bytes[2], bytes[3],
                bytes[4], bytes[5], bytes[6], bytes[7],
                bytes[8], bytes[9], bytes[10], bytes[11],
                bytes[12], bytes[13], bytes[14], bytes[15]
            )
            return UUID(uuid: tuple)
        default: return nil
        }
    }

    public var asDate: Date? {
        switch self {
        case .date(let val): return val
        case .real(let val): return Date(timeIntervalSince1970: val)
        case .integer(let val): return Date(timeIntervalSince1970: Double(val))
        case .string(let val):
            let formatter = ISO8601DateFormatter()
            formatter.formatOptions = [.withInternetDateTime, .withFractionalSeconds]
            if let d = formatter.date(from: val) { return d }
            formatter.formatOptions = [.withInternetDateTime]
            return formatter.date(from: val)
        default: return nil
        }
    }

    public var asURL: URL? {
        switch self {
        case .uri(let val): return val
        case .string(let val): return URL(string: val)
        default: return nil
        }
    }

    public var asData: Data? {
        switch self {
        case .binary(let val): return val
        case .string(let val): return Data(base64Encoded: val) ?? val.data(using: .utf8)
        default: return nil
        }
    }

    public var asArray: [LLSDValue]? {
        if case .array(let val) = self { return val }
        return nil
    }

    public var asMap: [String: LLSDValue]? {
        if case .map(let val) = self { return val }
        return nil
    }

    public subscript(key: String) -> LLSDValue? {
        if case .map(let map) = self {
            return map[key]
        }
        return nil
    }

    public subscript(index: Int) -> LLSDValue? {
        if case .array(let array) = self, index >= 0 && index < array.count {
            return array[index]
        }
        return nil
    }

    // MARK: - Codable Implementation
    public init(from decoder: Decoder) throws {
        let container = try decoder.singleValueContainer()
        if container.decodeNil() {
            self = .undefined
        } else if let b = try? container.decode(Bool.self) {
            self = .boolean(b)
        } else if let i = try? container.decode(Int32.self) {
            self = .integer(i)
        } else if let r = try? container.decode(Double.self) {
            self = .real(r)
        } else if let s = try? container.decode(String.self) {
            if let uuid = UUID(uuidString: s) {
                self = .uuid(uuid)
            } else {
                self = .string(s)
            }
        } else if let u = try? container.decode(UUID.self) {
            self = .uuid(u)
        } else if let d = try? container.decode(Date.self) {
            self = .date(d)
        } else if let url = try? container.decode(URL.self) {
            self = .uri(url)
        } else if let data = try? container.decode(Data.self) {
            self = .binary(data)
        } else if let arr = try? container.decode([LLSDValue].self) {
            self = .array(arr)
        } else if let dict = try? container.decode([String: LLSDValue].self) {
            self = .map(dict)
        } else {
            self = .undefined
        }
    }

    public func encode(to encoder: Encoder) throws {
        var container = encoder.singleValueContainer()
        switch self {
        case .undefined: try container.encodeNil()
        case .boolean(let b): try container.encode(b)
        case .integer(let i): try container.encode(i)
        case .real(let r): try container.encode(r)
        case .string(let s): try container.encode(s)
        case .uuid(let u): try container.encode(u.uuidString.lowercased())
        case .date(let d): try container.encode(d)
        case .uri(let u): try container.encode(u.absoluteString)
        case .binary(let data): try container.encode(data)
        case .array(let arr): try container.encode(arr)
        case .map(let dict): try container.encode(dict)
        }
    }
}

// MARK: - ExpressibleBy Literals
extension LLSDValue: ExpressibleByNilLiteral {
    public init(nilLiteral: ()) { self = .undefined }
}

extension LLSDValue: ExpressibleByBooleanLiteral {
    public init(booleanLiteral value: Bool) { self = .boolean(value) }
}

extension LLSDValue: ExpressibleByIntegerLiteral {
    public init(integerLiteral value: Int) { self = .integer(Int32(value)) }
}

extension LLSDValue: ExpressibleByFloatLiteral {
    public init(floatLiteral value: Double) { self = .real(value) }
}

extension LLSDValue: ExpressibleByStringLiteral {
    public init(stringLiteral value: String) {
        if let u = UUID(uuidString: value) {
            self = .uuid(u)
        } else {
            self = .string(value)
        }
    }
}

extension LLSDValue: ExpressibleByArrayLiteral {
    public init(arrayLiteral elements: LLSDValue...) { self = .array(elements) }
}

extension LLSDValue: ExpressibleByDictionaryLiteral {
    public init(dictionaryLiteral elements: (String, LLSDValue)...) {
        var map: [String: LLSDValue] = [:]
        for (k, v) in elements { map[k] = v }
        self = .map(map)
    }
}
