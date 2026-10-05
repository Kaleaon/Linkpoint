import Foundation

#if canImport(FoundationXML)
import FoundationXML
#endif

public enum LLSDXMLError: Error, LocalizedError {
    case parseFailed(String)
    case unexpectedRootElement(String)
    case missingKey

    public var errorDescription: String? {
        switch self {
        case .parseFailed(let msg): return "XML parse error: \(msg)"
        case .unexpectedRootElement(let el): return "Unexpected root element: \(el)"
        case .missingKey: return "Missing <key> element in LLSD map."
        }
    }
}

public struct LLSDXML {
    public static let xmlHeader = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"

    // MARK: - Serializer
    public static func serialize(_ value: LLSDValue, includeXMLHeader: Bool = true) -> String {
        var xml = includeXMLHeader ? xmlHeader : ""
        xml.append("<llsd>")
        encode(value, into: &xml)
        xml.append("</llsd>")
        return xml
    }

    private static func encode(_ value: LLSDValue, into xml: inout String) {
        switch value {
        case .undefined:
            xml.append("<undef/>")

        case .boolean(let boolVal):
            xml.append("<boolean>\(boolVal ? "1" : "0")</boolean>")

        case .integer(let intVal):
            xml.append("<integer>\(intVal)</integer>")

        case .real(let doubleVal):
            xml.append("<real>\(doubleVal)</real>")

        case .uuid(let uuidVal):
            xml.append("<uuid>\(uuidVal.uuidString.lowercased())</uuid>")

        case .date(let dateVal):
            let formatter = ISO8601DateFormatter()
            formatter.formatOptions = [.withInternetDateTime, .withFractionalSeconds]
            xml.append("<date>\(formatter.string(from: dateVal))</date>")

        case .string(let strVal):
            xml.append("<string>\(escapeXML(strVal))</string>")

        case .uri(let urlVal):
            xml.append("<uri>\(escapeXML(urlVal.absoluteString))</uri>")

        case .binary(let binVal):
            xml.append("<binary encoding=\"base64\">\(binVal.base64EncodedString())</binary>")

        case .array(let arrVal):
            xml.append("<array>")
            for elem in arrVal {
                encode(elem, into: &xml)
            }
            xml.append("</array>")

        case .map(let mapVal):
            xml.append("<map>")
            let sortedKeys = mapVal.keys.sorted()
            for key in sortedKeys {
                xml.append("<key>\(escapeXML(key))</key>")
                if let val = mapVal[key] {
                    encode(val, into: &xml)
                }
            }
            xml.append("</map>")
        }
    }

    private static func escapeXML(_ text: String) -> String {
        text.replacingOccurrences(of: "&", with: "&amp;")
            .replacingOccurrences(of: "<", with: "&lt;")
            .replacingOccurrences(of: ">", with: "&gt;")
            .replacingOccurrences(of: "\"", with: "&quot;")
            .replacingOccurrences(of: "'", with: "&apos;")
    }

    // MARK: - Parser
    public static func parse(_ xmlData: Data) throws -> LLSDValue {
        let delegate = LLSDXMLParserDelegate()
        let parser = XMLParser(data: xmlData)
        parser.delegate = delegate
        guard parser.parse() else {
            let err = parser.parserError?.localizedDescription ?? "Unknown XML error"
            throw LLSDXMLError.parseFailed(err)
        }
        guard let result = delegate.rootValue else {
            throw LLSDXMLError.parseFailed("Empty LLSD document")
        }
        return result
    }

    public static func parse(_ xmlString: String) throws -> LLSDValue {
        guard let data = xmlString.data(using: .utf8) else {
            throw LLSDXMLError.parseFailed("Invalid UTF-8 string")
        }
        return try parse(data)
    }
}

// MARK: - XML Parser Delegate
final class LLSDXMLParserDelegate: NSObject, XMLParserDelegate {
    var rootValue: LLSDValue?

    private enum ElementType {
        case llsd
        case undef
        case boolean
        case integer
        case real
        case uuid
        case date
        case string
        case uri
        case binary
        case array
        case map
        case key
        case unknown
    }

    private struct Node {
        let type: ElementType
        var attributes: [String: String]
        var text: String = ""
        var currentKey: String?
        var mapValues: [String: LLSDValue] = [:]
        var arrayValues: [LLSDValue] = []
    }

    private var stack: [Node] = []

    func parser(_ parser: XMLParser, didStartElement elementName: String, namespaceURI: String?, qualifiedName qName: String?, attributes attributeDict: [String : String] = [:]) {
        let type = elementType(for: elementName)
        let node = Node(type: type, attributes: attributeDict)
        stack.append(node)
    }

    func parser(_ parser: XMLParser, foundCharacters string: String) {
        guard !stack.isEmpty else { return }
        stack[stack.count - 1].text.append(string)
    }

    func parser(_ parser: XMLParser, didEndElement elementName: String, namespaceURI: String?, qualifiedName qName: String?) {
        guard !stack.isEmpty else { return }
        let node = stack.removeLast()
        let trimmedText = node.text.trimmingCharacters(in: .whitespacesAndNewlines)

        let value: LLSDValue?
        switch node.type {
        case .undef:
            value = .undefined
        case .boolean:
            let lower = trimmedText.lowercased()
            value = .boolean(lower == "true" || lower == "1" || lower == "t")
        case .integer:
            value = .integer(Int32(trimmedText) ?? 0)
        case .real:
            value = .real(Double(trimmedText) ?? 0.0)
        case .uuid:
            if let uuid = UUID(uuidString: trimmedText) {
                value = .uuid(uuid)
            } else {
                value = .uuid(UUID(uuidString: "00000000-0000-0000-0000-000000000000")!)
            }
        case .date:
            let formatter = ISO8601DateFormatter()
            formatter.formatOptions = [.withInternetDateTime, .withFractionalSeconds]
            if let d = formatter.date(from: trimmedText) {
                value = .date(d)
            } else {
                formatter.formatOptions = [.withInternetDateTime]
                let d = formatter.date(from: trimmedText) ?? Date(timeIntervalSince1970: 0)
                value = .date(d)
            }
        case .string:
            value = .string(node.text)
        case .uri:
            let url = URL(string: trimmedText) ?? URL(string: "about:blank")!
            value = .uri(url)
        case .binary:
            if let data = Data(base64Encoded: trimmedText) {
                value = .binary(data)
            } else {
                value = .binary(node.text.data(using: .utf8) ?? Data())
            }
        case .array:
            value = .array(node.arrayValues)
        case .map:
            value = .map(node.mapValues)
        case .key:
            if !stack.isEmpty {
                stack[stack.count - 1].currentKey = node.text
            }
            value = nil
        case .llsd:
            if !node.arrayValues.isEmpty {
                rootValue = node.arrayValues.first
            } else if !node.mapValues.isEmpty {
                rootValue = .map(node.mapValues)
            }
            value = nil
        case .unknown:
            value = nil
        }

        if let val = value {
            if stack.isEmpty {
                rootValue = val
            } else {
                let parentIdx = stack.count - 1
                if stack[parentIdx].type == .map {
                    if let k = stack[parentIdx].currentKey {
                        stack[parentIdx].mapValues[k] = val
                        stack[parentIdx].currentKey = nil
                    }
                } else if stack[parentIdx].type == .array || stack[parentIdx].type == .llsd {
                    stack[parentIdx].arrayValues.append(val)
                }
            }
        }
    }

    private func elementType(for name: String) -> ElementType {
        switch name.lowercased() {
        case "llsd": return .llsd
        case "undef": return .undef
        case "boolean": return .boolean
        case "integer": return .integer
        case "real": return .real
        case "uuid": return .uuid
        case "date": return .date
        case "string": return .string
        case "uri": return .uri
        case "binary": return .binary
        case "array": return .array
        case "map": return .map
        case "key": return .key
        default: return .unknown
        }
    }
}
