import Foundation

public enum LLSDNotationError: Error, LocalizedError {
    case unexpectedCharacter(Character, Int)
    case unexpectedEOF
    case invalidUUID(String)
    case invalidDate(String)
    case invalidNumber(String)
    case invalidBinaryEncoding

    public var errorDescription: String? {
        switch self {
        case .unexpectedCharacter(let char, let pos): return "Unexpected character '\(char)' at index \(pos)."
        case .unexpectedEOF: return "Unexpected end of file while parsing notation."
        case .invalidUUID(let str): return "Invalid UUID string in notation: \(str)."
        case .invalidDate(let str): return "Invalid date string in notation: \(str)."
        case .invalidNumber(let str): return "Invalid number string in notation: \(str)."
        case .invalidBinaryEncoding: return "Invalid binary encoding in notation."
        }
    }
}

public struct LLSDNotation {
    public static let header = "<?llsd/notation?>\n"

    // MARK: - Serializer
    public static func serialize(_ value: LLSDValue, includeHeader: Bool = false) -> String {
        var str = includeHeader ? header : ""
        encode(value, into: &str)
        return str
    }

    private static func encode(_ value: LLSDValue, into str: inout String) {
        switch value {
        case .undefined:
            str.append("!")
        case .boolean(let boolVal):
            str.append(boolVal ? "1" : "0")
        case .integer(let intVal):
            str.append("i\(intVal)")
        case .real(let doubleVal):
            str.append("r\(doubleVal)")
        case .uuid(let uuidVal):
            str.append("u\(uuidVal.uuidString.lowercased())")
        case .date(let dateVal):
            let formatter = ISO8601DateFormatter()
            formatter.formatOptions = [.withInternetDateTime, .withFractionalSeconds]
            str.append("d\"\(formatter.string(from: dateVal))\"")
        case .string(let strVal):
            let escaped = strVal
                .replacingOccurrences(of: "\\", with: "\\\\")
                .replacingOccurrences(of: "'", with: "\\'")
            str.append("'\(escaped)'")
        case .uri(let urlVal):
            str.append("l\"\(urlVal.absoluteString)\"")
        case .binary(let binVal):
            str.append("b64\"\(binVal.base64EncodedString())\"")
        case .array(let arrVal):
            str.append("[")
            for (i, elem) in arrVal.enumerated() {
                if i > 0 { str.append(",") }
                encode(elem, into: &str)
            }
            str.append("]")
        case .map(let mapVal):
            str.append("{")
            let keys = mapVal.keys.sorted()
            for (i, key) in keys.enumerated() {
                if i > 0 { str.append(",") }
                let escapedKey = key
                    .replacingOccurrences(of: "\\", with: "\\\\")
                    .replacingOccurrences(of: "'", with: "\\'")
                str.append("'\(escapedKey)':")
                if let val = mapVal[key] {
                    encode(val, into: &str)
                }
            }
            str.append("}")
        }
    }

    // MARK: - Parser
    public static func parse(_ text: String) throws -> LLSDValue {
        var trimmed = text.trimmingCharacters(in: .whitespacesAndNewlines)
        if trimmed.hasPrefix(header) {
            trimmed = String(trimmed.dropFirst(header.count)).trimmingCharacters(in: .whitespacesAndNewlines)
        }
        var parser = LLSDNotationParser(text: trimmed)
        return try parser.parseValue()
    }
}

// MARK: - Notation Parser Engine
public struct LLSDNotationParser {
    private let text: String
    private let utf8: String.UTF8View
    private var index: String.UTF8View.Index

    public init(text: String) {
        self.text = text
        self.utf8 = text.utf8
        self.index = text.utf8.startIndex
    }

    private var isAtEnd: Bool { index >= utf8.endIndex }

    private mutating func skipWhitespace() {
        while !isAtEnd {
            let b = utf8[index]
            if b == 0x20 || b == 0x09 || b == 0x0A || b == 0x0D {
                index = utf8.index(after: index)
            } else {
                break
            }
        }
    }

    private func peek() -> UInt8? {
        if isAtEnd { return nil }
        return utf8[index]
    }

    private mutating func advance() -> UInt8 {
        let byte = utf8[index]
        index = utf8.index(after: index)
        return byte
    }

    private mutating func advance(by count: Int) {
        index = utf8.index(index, offsetBy: count, limitedBy: utf8.endIndex) ?? utf8.endIndex
    }

    private func peekString(_ prefix: String) -> Bool {
        let prefixBytes = prefix.utf8
        var cur = index
        for byte in prefixBytes {
            if cur >= utf8.endIndex || utf8[cur] != byte {
                return false
            }
            cur = utf8.index(after: cur)
        }
        return true
    }

    public mutating func parseValue() throws -> LLSDValue {
        skipWhitespace()
        guard let c = peek() else { throw LLSDNotationError.unexpectedEOF }

        switch c {
        case UInt8(ascii: "!"):
            _ = advance()
            return .undefined

        case UInt8(ascii: "1"), UInt8(ascii: "t"), UInt8(ascii: "T"):
            if peekString("true") || peekString("TRUE") {
                advance(by: 4)
            } else {
                _ = advance()
            }
            return .boolean(true)

        case UInt8(ascii: "0"), UInt8(ascii: "f"), UInt8(ascii: "F"):
            if peekString("false") || peekString("FALSE") {
                advance(by: 5)
            } else {
                _ = advance()
            }
            return .boolean(false)

        case UInt8(ascii: "i"):
            _ = advance()
            let numStr = readUntilDelimiter()
            guard let val = Int32(numStr) else { throw LLSDNotationError.invalidNumber(numStr) }
            return .integer(val)

        case UInt8(ascii: "r"):
            _ = advance()
            let numStr = readUntilDelimiter()
            guard let val = Double(numStr) else { throw LLSDNotationError.invalidNumber(numStr) }
            return .real(val)

        case UInt8(ascii: "u"):
            _ = advance()
            if peek() == UInt8(ascii: "\"") || peek() == UInt8(ascii: "'") {
                let uuidStr = try readQuotedString()
                guard let u = UUID(uuidString: uuidStr) else { throw LLSDNotationError.invalidUUID(uuidStr) }
                return .uuid(u)
            } else {
                let uuidStr = readFixedLength(36)
                guard let u = UUID(uuidString: uuidStr) else { throw LLSDNotationError.invalidUUID(uuidStr) }
                return .uuid(u)
            }

        case UInt8(ascii: "d"):
            _ = advance()
            if peek() == UInt8(ascii: "\"") || peek() == UInt8(ascii: "'") {
                let dateStr = try readQuotedString()
                let formatter = ISO8601DateFormatter()
                formatter.formatOptions = [.withInternetDateTime, .withFractionalSeconds]
                if let date = formatter.date(from: dateStr) { return .date(date) }
                formatter.formatOptions = [.withInternetDateTime]
                if let date = formatter.date(from: dateStr) { return .date(date) }
                throw LLSDNotationError.invalidDate(dateStr)
            } else {
                let numStr = readUntilDelimiter()
                guard let sec = Double(numStr) else { throw LLSDNotationError.invalidDate(numStr) }
                return .date(Date(timeIntervalSince1970: sec))
            }

        case UInt8(ascii: "l"):
            _ = advance()
            let uriStr = try readQuotedString()
            guard let url = URL(string: uriStr) else {
                return .uri(URL(string: "about:blank")!)
            }
            return .uri(url)

        case UInt8(ascii: "s"):
            if peekString("s(") {
                return try parseSizedString()
            } else {
                _ = advance()
                let str = try readQuotedString()
                return .string(str)
            }

        case UInt8(ascii: "b"):
            if peekString("b64\"") || peekString("b64'") {
                advance(by: 3)
                let base64 = try readQuotedString()
                guard let data = Data(base64Encoded: base64) else { throw LLSDNotationError.invalidBinaryEncoding }
                return .binary(data)
            } else if peekString("b16\"") || peekString("b16'") {
                advance(by: 3)
                let hex = try readQuotedString()
                let data = try dataFromHex(hex)
                return .binary(data)
            } else if peekString("b(") {
                return try parseSizedBinary()
            } else {
                _ = advance()
                let str = try readQuotedString()
                return .binary(str.data(using: .utf8) ?? Data())
            }

        case UInt8(ascii: "'"), UInt8(ascii: "\""):
            let str = try readQuotedString()
            return .string(str)

        case UInt8(ascii: "["):
            _ = advance()
            var elements: [LLSDValue] = []
            skipWhitespace()
            if peek() == UInt8(ascii: "]") {
                _ = advance()
                return .array([])
            }
            while !isAtEnd {
                let elem = try parseValue()
                elements.append(elem)
                skipWhitespace()
                if peek() == UInt8(ascii: ",") {
                    _ = advance()
                    skipWhitespace()
                }
                if peek() == UInt8(ascii: "]") {
                    _ = advance()
                    break
                }
            }
            return .array(elements)

        case UInt8(ascii: "{"):
            _ = advance()
            var map: [String: LLSDValue] = [:]
            skipWhitespace()
            if peek() == UInt8(ascii: "}") {
                _ = advance()
                return .map([:])
            }
            while !isAtEnd {
                skipWhitespace()
                let key: String
                let pk = peek()
                if pk == UInt8(ascii: "'") || pk == UInt8(ascii: "\"") {
                    key = try readQuotedString()
                } else if peekString("s(") {
                    if case .string(let s) = try parseSizedString() {
                        key = s
                    } else {
                        key = ""
                    }
                } else {
                    key = readKeyName()
                }
                skipWhitespace()
                let nextByte = peek()
                guard nextByte == UInt8(ascii: ":") || nextByte == UInt8(ascii: "=") else {
                    let char: Character = index < utf8.endIndex ? text[index] : " "
                    let charOffset = text[..<index].count
                    throw LLSDNotationError.unexpectedCharacter(char, charOffset)
                }
                _ = advance() // skip ':' or '='
                let val = try parseValue()
                map[key] = val
                skipWhitespace()
                if peek() == UInt8(ascii: ",") {
                    _ = advance()
                    skipWhitespace()
                }
                if peek() == UInt8(ascii: "}") {
                    _ = advance()
                    break
                }
            }
            return .map(map)

        default:
            let char: Character = index < utf8.endIndex ? text[index] : " "
            let charOffset = text[..<index].count
            throw LLSDNotationError.unexpectedCharacter(char, charOffset)
        }
    }

    private mutating func readQuotedString() throws -> String {
        guard let quote = peek(), quote == UInt8(ascii: "'") || quote == UInt8(ascii: "\"") else {
            let char: Character = index < utf8.endIndex ? text[index] : " "
            let charOffset = text[..<index].count
            throw LLSDNotationError.unexpectedCharacter(char, charOffset)
        }
        _ = advance() // skip initial quote

        let sliceStart = index
        var cur = index
        var hasEscapes = false

        while cur < utf8.endIndex {
            let b = utf8[cur]
            if b == quote {
                break
            }
            if b == UInt8(ascii: "\\") {
                hasEscapes = true
                break
            }
            cur = utf8.index(after: cur)
        }

        if !hasEscapes {
            if cur >= utf8.endIndex {
                throw LLSDNotationError.unexpectedEOF
            }
            let result = String(decoding: utf8[sliceStart..<cur], as: UTF8.self)
            index = utf8.index(after: cur) // skip closing quote
            return result
        }

        // Slow path for escaped characters
        var result = String(decoding: utf8[sliceStart..<cur], as: UTF8.self)
        index = cur

        while !isAtEnd {
            let b = advance()
            if b == quote {
                return result
            } else if b == UInt8(ascii: "\\") {
                if !isAtEnd {
                    let escaped = advance()
                    switch escaped {
                    case UInt8(ascii: "n"): result.append("\n")
                    case UInt8(ascii: "r"): result.append("\r")
                    case UInt8(ascii: "t"): result.append("\t")
                    case UInt8(ascii: "\\"): result.append("\\")
                    case UInt8(ascii: "'"): result.append("'")
                    case UInt8(ascii: "\""): result.append("\"")
                    default:
                        if escaped < 128 {
                            result.append(Character(UnicodeScalar(escaped)))
                        } else {
                            let prevIndex = utf8.index(before: index)
                            let charEnd = text.index(after: prevIndex)
                            let charStr = String(decoding: utf8[prevIndex..<charEnd], as: UTF8.self)
                            result.append(charStr)
                            index = charEnd
                        }
                    }
                }
            } else {
                let byteStart = utf8.index(before: index)
                var nextCur = index
                while nextCur < utf8.endIndex {
                    let nb = utf8[nextCur]
                    if nb == quote || nb == UInt8(ascii: "\\") {
                        break
                    }
                    nextCur = utf8.index(after: nextCur)
                }
                result.append(String(decoding: utf8[byteStart..<nextCur], as: UTF8.self))
                index = nextCur
            }
        }
        throw LLSDNotationError.unexpectedEOF
    }

    private mutating func parseSizedString() throws -> LLSDValue {
        advance(by: 2) // skip 's('
        let start = index
        while !isAtEnd && peek() != UInt8(ascii: ")") {
            _ = advance()
        }
        guard peek() == UInt8(ascii: ")") else { throw LLSDNotationError.unexpectedEOF }
        let lenStr = String(decoding: utf8[start..<index], as: UTF8.self)
        _ = advance() // skip ')'
        guard let len = Int(lenStr) else { throw LLSDNotationError.invalidNumber(lenStr) }
        guard let quote = peek(), quote == UInt8(ascii: "'") || quote == UInt8(ascii: "\"") else {
            let char: Character = index < utf8.endIndex ? text[index] : " "
            let charOffset = text[..<index].count
            throw LLSDNotationError.unexpectedCharacter(char, charOffset)
        }
        _ = advance()
        let str = readFixedLength(len)
        if !isAtEnd && peek() == quote { _ = advance() }
        return .string(str)
    }

    private mutating func parseSizedBinary() throws -> LLSDValue {
        advance(by: 2) // skip 'b('
        let start = index
        while !isAtEnd && peek() != UInt8(ascii: ")") {
            _ = advance()
        }
        guard peek() == UInt8(ascii: ")") else { throw LLSDNotationError.unexpectedEOF }
        let lenStr = String(decoding: utf8[start..<index], as: UTF8.self)
        _ = advance() // skip ')'
        guard let len = Int(lenStr) else { throw LLSDNotationError.invalidNumber(lenStr) }
        guard let quote = peek(), quote == UInt8(ascii: "'") || quote == UInt8(ascii: "\"") else {
            let char: Character = index < utf8.endIndex ? text[index] : " "
            let charOffset = text[..<index].count
            throw LLSDNotationError.unexpectedCharacter(char, charOffset)
        }
        _ = advance()
        let str = readFixedLength(len)
        if !isAtEnd && peek() == quote { _ = advance() }
        return .binary(str.data(using: .utf8) ?? Data())
    }

    private mutating func readUntilDelimiter() -> String {
        let start = index
        while !isAtEnd {
            guard let b = peek() else { break }
            if b == UInt8(ascii: " ") || b == UInt8(ascii: "\t") || b == UInt8(ascii: "\n") || b == UInt8(ascii: "\r") || b == UInt8(ascii: ",") || b == UInt8(ascii: "]") || b == UInt8(ascii: "}") {
                break
            }
            _ = advance()
        }
        return String(decoding: utf8[start..<index], as: UTF8.self)
    }

    private mutating func readFixedLength(_ count: Int) -> String {
        let start = index
        for _ in 0..<count {
            if isAtEnd { break }
            index = text.index(after: index)
        }
        return String(decoding: utf8[start..<index], as: UTF8.self)
    }

    private mutating func readKeyName() -> String {
        let start = index
        while !isAtEnd {
            guard let b = peek() else { break }
            if b == UInt8(ascii: ":") || b == UInt8(ascii: "=") || b == UInt8(ascii: " ") || b == UInt8(ascii: "\t") || b == UInt8(ascii: "\n") || b == UInt8(ascii: "\r") {
                break
            }
            _ = advance()
        }
        return String(decoding: utf8[start..<index], as: UTF8.self)
    }

    private func dataFromHex(_ hex: String) throws -> Data {
        var data = Data()
        var temp = ""
        for char in hex {
            if char.isHexDigit {
                temp.append(char)
                if temp.count == 2 {
                    if let byte = UInt8(temp, radix: 16) {
                        data.append(byte)
                    }
                    temp = ""
                }
            }
        }
        return data
    }
}
