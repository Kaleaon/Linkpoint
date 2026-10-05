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
    private let chars: [Character]
    private var index: Int = 0

    public init(text: String) {
        self.chars = Array(text)
    }

    private var isAtEnd: Bool { index >= chars.count }

    private mutating func skipWhitespace() {
        while !isAtEnd {
            let c = chars[index]
            if c == " " || c == "\t" || c == "\n" || c == "\r" {
                index += 1
            } else {
                break
            }
        }
    }

    private mutating func peek() -> Character? {
        if isAtEnd { return nil }
        return chars[index]
    }

    private mutating func advance() -> Character {
        let c = chars[index]
        index += 1
        return c
    }

    public mutating func parseValue() throws -> LLSDValue {
        skipWhitespace()
        guard let c = peek() else { throw LLSDNotationError.unexpectedEOF }

        switch c {
        case "!":
            _ = advance()
            return .undefined

        case "1", "t", "T":
            if peekString("true") || peekString("TRUE") {
                advance(by: 4)
            } else {
                _ = advance()
            }
            return .boolean(true)

        case "0", "f", "F":
            if peekString("false") || peekString("FALSE") {
                advance(by: 5)
            } else {
                _ = advance()
            }
            return .boolean(false)

        case "i":
            _ = advance()
            let numStr = readUntilDelimiter()
            guard let val = Int32(numStr) else { throw LLSDNotationError.invalidNumber(numStr) }
            return .integer(val)

        case "r":
            _ = advance()
            let numStr = readUntilDelimiter()
            guard let val = Double(numStr) else { throw LLSDNotationError.invalidNumber(numStr) }
            return .real(val)

        case "u":
            _ = advance()
            if peek() == "\"" || peek() == "'" {
                let uuidStr = try readQuotedString()
                guard let u = UUID(uuidString: uuidStr) else { throw LLSDNotationError.invalidUUID(uuidStr) }
                return .uuid(u)
            } else {
                let uuidStr = readFixedLength(36)
                guard let u = UUID(uuidString: uuidStr) else { throw LLSDNotationError.invalidUUID(uuidStr) }
                return .uuid(u)
            }

        case "d":
            _ = advance()
            if peek() == "\"" || peek() == "'" {
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

        case "l":
            _ = advance()
            let uriStr = try readQuotedString()
            guard let url = URL(string: uriStr) else {
                return .uri(URL(string: "about:blank")!)
            }
            return .uri(url)

        case "s":
            if peekString("s(") {
                return try parseSizedString()
            } else {
                _ = advance()
                let str = try readQuotedString()
                return .string(str)
            }

        case "b":
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

        case "'", "\"":
            let str = try readQuotedString()
            return .string(str)

        case "[":
            _ = advance()
            var elements: [LLSDValue] = []
            skipWhitespace()
            if peek() == "]" {
                _ = advance()
                return .array([])
            }
            while !isAtEnd {
                let elem = try parseValue()
                elements.append(elem)
                skipWhitespace()
                if peek() == "," {
                    _ = advance()
                    skipWhitespace()
                }
                if peek() == "]" {
                    _ = advance()
                    break
                }
            }
            return .array(elements)

        case "{":
            _ = advance()
            var map: [String: LLSDValue] = [:]
            skipWhitespace()
            if peek() == "}" {
                _ = advance()
                return .map([:])
            }
            while !isAtEnd {
                skipWhitespace()
                let key: String
                if peek() == "'" || peek() == "\"" {
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
                guard peek() == ":" || peek() == "=" else {
                    throw LLSDNotationError.unexpectedCharacter(peek() ?? " ", index)
                }
                _ = advance() // skip ':' or '='
                let val = try parseValue()
                map[key] = val
                skipWhitespace()
                if peek() == "," {
                    _ = advance()
                    skipWhitespace()
                }
                if peek() == "}" {
                    _ = advance()
                    break
                }
            }
            return .map(map)

        default:
            throw LLSDNotationError.unexpectedCharacter(c, index)
        }
    }

    private mutating func peekString(_ prefix: String) -> Bool {
        let arr = Array(prefix)
        guard index + arr.count <= chars.count else { return false }
        for i in 0..<arr.count {
            if chars[index + i] != arr[i] { return false }
        }
        return true
    }

    private mutating func advance(by count: Int) {
        index += count
    }

    private mutating func readQuotedString() throws -> String {
        guard let quote = peek(), quote == "'" || quote == "\"" else {
            throw LLSDNotationError.unexpectedCharacter(peek() ?? " ", index)
        }
        _ = advance() // skip initial quote
        var result = ""
        while !isAtEnd {
            let c = advance()
            if c == quote {
                return result
            } else if c == "\\" {
                if !isAtEnd {
                    let escaped = advance()
                    switch escaped {
                    case "n": result.append("\n")
                    case "r": result.append("\r")
                    case "t": result.append("\t")
                    case "\\": result.append("\\")
                    case "'": result.append("'")
                    case "\"": result.append("\"")
                    default: result.append(escaped)
                    }
                }
            } else {
                result.append(c)
            }
        }
        throw LLSDNotationError.unexpectedEOF
    }

    private mutating func parseSizedString() throws -> LLSDValue {
        advance(by: 2) // skip 's('
        var lenStr = ""
        while !isAtEnd && peek() != ")" {
            lenStr.append(advance())
        }
        guard peek() == ")" else { throw LLSDNotationError.unexpectedEOF }
        _ = advance() // skip ')'
        guard let len = Int(lenStr) else { throw LLSDNotationError.invalidNumber(lenStr) }
        guard let quote = peek(), quote == "'" || quote == "\"" else {
            throw LLSDNotationError.unexpectedCharacter(peek() ?? " ", index)
        }
        _ = advance()
        let str = readFixedLength(len)
        if !isAtEnd && peek() == quote { _ = advance() }
        return .string(str)
    }

    private mutating func parseSizedBinary() throws -> LLSDValue {
        advance(by: 2) // skip 'b('
        var lenStr = ""
        while !isAtEnd && peek() != ")" {
            lenStr.append(advance())
        }
        guard peek() == ")" else { throw LLSDNotationError.unexpectedEOF }
        _ = advance() // skip ')'
        guard let len = Int(lenStr) else { throw LLSDNotationError.invalidNumber(lenStr) }
        guard let quote = peek(), quote == "'" || quote == "\"" else {
            throw LLSDNotationError.unexpectedCharacter(peek() ?? " ", index)
        }
        _ = advance()
        let str = readFixedLength(len)
        if !isAtEnd && peek() == quote { _ = advance() }
        return .binary(str.data(using: .utf8) ?? Data())
    }

    private mutating func readUntilDelimiter() -> String {
        var res = ""
        while !isAtEnd {
            guard let c = peek() else { break }
            if c == " " || c == "\t" || c == "\n" || c == "\r" || c == "," || c == "]" || c == "}" {
                break
            }
            res.append(advance())
        }
        return res
    }

    private mutating func readFixedLength(_ count: Int) -> String {
        var res = ""
        for _ in 0..<count {
            if isAtEnd { break }
            res.append(advance())
        }
        return res
    }

    private mutating func readKeyName() -> String {
        var res = ""
        while !isAtEnd {
            guard let c = peek() else { break }
            if c == ":" || c == "=" || c == " " || c == "\t" || c == "\n" || c == "\r" {
                break
            }
            res.append(advance())
        }
        return res
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
