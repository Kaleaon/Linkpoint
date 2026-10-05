import Foundation
import LLSD

#if canImport(FoundationXML)
import FoundationXML
#endif

public enum XMLRPCValue: Equatable {
    case int(Int)
    case double(Double)
    case boolean(Bool)
    case string(String)
    case dateTime(Date)
    case base64(Data)
    case array([XMLRPCValue])
    case structure([String: XMLRPCValue])

    public var asString: String? {
        if case .string(let s) = self { return s }
        return nil
    }

    public var asStructure: [String: XMLRPCValue]? {
        if case .structure(let s) = self { return s }
        return nil
    }

    public var asLLSDValue: LLSDValue {
        switch self {
        case .int(let i): return .integer(Int32(i))
        case .double(let d): return .real(d)
        case .boolean(let b): return .boolean(b)
        case .string(let s):
            if let uuid = UUID(uuidString: s) {
                return .uuid(uuid)
            }
            return .string(s)
        case .dateTime(let d): return .date(d)
        case .base64(let d): return .binary(d)
        case .array(let arr): return .array(arr.map { $0.asLLSDValue })
        case .structure(let dict):
            var map: [String: LLSDValue] = [:]
            for (k, v) in dict { map[k] = v.asLLSDValue }
            return .map(map)
        }
    }

    public func toXML() -> String {
        switch self {
        case .int(let val):
            return "<value><int>\(val)</int></value>"
        case .double(let val):
            return "<value><double>\(val)</double></value>"
        case .boolean(let val):
            return "<value><boolean>\(val ? "1" : "0")</boolean></value>"
        case .string(let val):
            return "<value><string>\(escapeXML(val))</string></value>"
        case .dateTime(let val):
            let formatter = ISO8601DateFormatter()
            return "<value><dateTime.iso8601>\(formatter.string(from: val))</dateTime.iso8601></value>"
        case .base64(let val):
            return "<value><base64>\(val.base64EncodedString())</base64></value>"
        case .array(let arr):
            var xml = "<value><array><data>"
            for item in arr {
                xml.append(item.toXML())
            }
            xml.append("</data></array></value>")
            return xml
        case .structure(let dict):
            var xml = "<value><struct>"
            let keys = dict.keys.sorted()
            for k in keys {
                xml.append("<member><name>\(escapeXML(k))</name>")
                if let v = dict[k] {
                    xml.append(v.toXML())
                }
                xml.append("</member>")
            }
            xml.append("</struct></value>")
            return xml
        }
    }

    private func escapeXML(_ text: String) -> String {
        text.replacingOccurrences(of: "&", with: "&amp;")
            .replacingOccurrences(of: "<", with: "&lt;")
            .replacingOccurrences(of: ">", with: "&gt;")
            .replacingOccurrences(of: "\"", with: "&quot;")
            .replacingOccurrences(of: "'", with: "&apos;")
    }
}
