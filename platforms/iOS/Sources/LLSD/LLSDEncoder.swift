import Foundation

public enum LLSDFormat {
    case xml
    case binary
    case notation
    case auto
}

public class LLSDEncoder {
    public var format: LLSDFormat

    public init(format: LLSDFormat = .xml) {
        self.format = format
    }

    public func encode<T: Encodable>(_ value: T) throws -> Data {
        let llsdValue: LLSDValue
        if let direct = value as? LLSDValue {
            llsdValue = direct
        } else {
            let jsonEncoder = JSONEncoder()
            jsonEncoder.dateEncodingStrategy = .iso8601
            let jsonData = try jsonEncoder.encode(value)
            let jsonDecoder = JSONDecoder()
            jsonDecoder.dateDecodingStrategy = .iso8601
            llsdValue = try jsonDecoder.decode(LLSDValue.self, from: jsonData)
        }

        switch format {
        case .binary:
            return LLSDBinary.serialize(llsdValue, includeHeader: true)
        case .notation:
            let str = LLSDNotation.serialize(llsdValue, includeHeader: true)
            return Data(str.utf8)
        case .xml, .auto:
            let str = LLSDXML.serialize(llsdValue, includeXMLHeader: true)
            return Data(str.utf8)
        }
    }
}

public class LLSDDecoder {
    public var format: LLSDFormat

    public init(format: LLSDFormat = .auto) {
        self.format = format
    }

    public func decode<T: Decodable>(_ type: T.Type, from data: Data) throws -> T {
        let detectedFormat = detectFormat(for: data)
        let llsdValue: LLSDValue

        switch detectedFormat {
        case .binary:
            llsdValue = try LLSDBinary.parse(data)
        case .notation:
            guard let str = String(data: data, encoding: .utf8) else {
                throw LLSDBinaryError.stringDecodingFailed
            }
            llsdValue = try LLSDNotation.parse(str)
        case .xml, .auto:
            llsdValue = try LLSDXML.parse(data)
        }

        if T.self == LLSDValue.self {
            return llsdValue as! T
        }

        let jsonEncoder = JSONEncoder()
        jsonEncoder.dateEncodingStrategy = .iso8601
        let jsonData = try jsonEncoder.encode(llsdValue)
        let jsonDecoder = JSONDecoder()
        jsonDecoder.dateDecodingStrategy = .iso8601
        return try jsonDecoder.decode(T.self, from: jsonData)
    }

    private func detectFormat(for data: Data) -> LLSDFormat {
        if format != .auto { return format }

        if data.starts(with: LLSDBinary.header) {
            return .binary
        }
        if let str = String(data: data.prefix(32), encoding: .utf8) {
            let trimmed = str.trimmingCharacters(in: .whitespacesAndNewlines)
            if trimmed.hasPrefix("<?llsd/notation?>") {
                return .notation
            }
            if trimmed.hasPrefix("<?xml") || trimmed.hasPrefix("<llsd") || trimmed.hasPrefix("<map") {
                return .xml
            }
            if trimmed.hasPrefix("{") || trimmed.hasPrefix("[") || trimmed.hasPrefix("!") {
                return .notation
            }
        }
        // Default check byte tag for binary
        if let first = data.first {
            if first == 0x7B || first == 0x5B || first == 0x73 || first == 0x75 || first == 0x69 || first == 0x72 {
                return .binary
            }
        }
        return .xml
    }
}
