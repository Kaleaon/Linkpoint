import Foundation
import LLSD

#if canImport(FoundationXML)
import FoundationXML
#endif

#if canImport(FoundationNetworking)
import FoundationNetworking

extension URLSession {
    func data(for request: URLRequest) async throws -> (Data, URLResponse) {
        try await withCheckedThrowingContinuation { continuation in
            let task = self.dataTask(with: request) { data, response, error in
                if let error = error {
                    continuation.resume(throwing: error)
                } else if let data = data, let response = response {
                    continuation.resume(returning: (data, response))
                } else {
                    continuation.resume(throwing: URLError(.unknown))
                }
            }
            task.resume()
        }
    }
}
#endif

public enum XMLRPCError: Error, LocalizedError {
    case invalidURL(String)
    case httpError(Int)
    case faultResponse(Int, String)
    case parseError(String)

    public var errorDescription: String? {
        switch self {
        case .invalidURL(let u): return "Invalid URL: \(u)"
        case .httpError(let code): return "HTTP server returned error status code \(code)."
        case .faultResponse(let code, let msg): return "XMLRPC fault (\(code)): \(msg)"
        case .parseError(let msg): return "Failed to parse XMLRPC response: \(msg)"
        }
    }
}

public class XMLRPCClient {
    private let session: URLSession

    public init(session: URLSession = .shared) {
        self.session = session
    }

    public func executeCall(endpointURL: String, methodName: String, params: [XMLRPCValue]) async throws -> XMLRPCValue {
        guard let url = URL(string: endpointURL) else {
            throw XMLRPCError.invalidURL(endpointURL)
        }

        let bodyXML = buildRequestXML(methodName: methodName, params: params)
        guard let bodyData = bodyXML.data(using: .utf8) else {
            throw XMLRPCError.parseError("Failed to encode request XML to UTF-8")
        }

        var request = URLRequest(url: url)
        request.httpMethod = "POST"
        request.setValue("text/xml; charset=utf-8", forHTTPHeaderField: "Content-Type")
        request.setValue("Linkpoint-iOS/1.0", forHTTPHeaderField: "User-Agent")
        request.httpBody = bodyData

        let (data, response) = try await session.data(for: request)

        if let httpResponse = response as? HTTPURLResponse, !(200...299).contains(httpResponse.statusCode) {
            throw XMLRPCError.httpError(httpResponse.statusCode)
        }

        return try parseResponseXML(data)
    }

    public func loginToSimulator(
        gridURL: String,
        firstName: String,
        lastName: String,
        password: String,
        startLocation: String = "last",
        channel: String = "Linkpoint",
        version: String = "1.0.0"
    ) async throws -> [String: LLSDValue] {
        let paramsDict: [String: XMLRPCValue] = [
            "first": .string(firstName),
            "last": .string(lastName),
            "passwd": .string(password),
            "start": .string(startLocation),
            "channel": .string(channel),
            "version": .string(version),
            "platform": .string("iOS"),
            "mac": .string("00:00:00:00:00:00"),
            "id0": .string("00000000000000000000000000000000"),
            "agree_to_tos": .boolean(true),
            "read_critical": .boolean(true)
        ]

        let responseValue = try await executeCall(
            endpointURL: gridURL,
            methodName: "login_to_simulator",
            params: [.structure(paramsDict)]
        )

        if case .map(let map) = responseValue.asLLSDValue {
            return map
        }
        throw XMLRPCError.parseError("Expected map/struct in login_to_simulator response")
    }

    private func buildRequestXML(methodName: String, params: [XMLRPCValue]) -> String {
        var xml = "<?xml version=\"1.0\"?>\n<methodCall>\n<methodName>\(methodName)</methodName>\n<params>\n"
        for param in params {
            xml.append("<param>\n\(param.toXML())\n</param>\n")
        }
        xml.append("</params>\n</methodCall>")
        return xml
    }

    public func parseResponseXML(_ data: Data) throws -> XMLRPCValue {
        let delegate = XMLRPCResponseParserDelegate()
        let parser = XMLParser(data: data)
        parser.delegate = delegate
        guard parser.parse() else {
            let err = parser.parserError?.localizedDescription ?? "XML parse failed"
            throw XMLRPCError.parseError(err)
        }
        if let fault = delegate.faultValue {
            let code = fault["faultCode"]?.asLLSDValue.asInt ?? 0
            let msg = fault["faultString"]?.asLLSDValue.asString ?? "Unknown fault"
            throw XMLRPCError.faultResponse(Int(code), msg)
        }
        guard let result = delegate.resultValue else {
            throw XMLRPCError.parseError("Empty or invalid methodResponse")
        }
        return result
    }
}

// MARK: - XMLRPC Response Parser Delegate
final class XMLRPCResponseParserDelegate: NSObject, XMLParserDelegate {
    var resultValue: XMLRPCValue?
    var faultValue: [String: XMLRPCValue]?

    private enum StackType: Equatable {
        case value
        case param
        case fault
        case member
        case name
        case structContainer
        case arrayContainer
        case dataContainer
        case scalar(String)
        case other
    }

    private struct ElementNode {
        let name: String
        let type: StackType
        var text: String = ""
        var currentMemberName: String?
        var structMembers: [String: XMLRPCValue] = [:]
        var arrayElements: [XMLRPCValue] = []
        var childValue: XMLRPCValue?
    }

    private var stack: [ElementNode] = []

    func parser(_ parser: XMLParser, didStartElement elementName: String, namespaceURI: String?, qualifiedName qName: String?, attributes attributeDict: [String : String] = [:]) {
        let type: StackType
        switch elementName.lowercased() {
        case "value": type = .value
        case "param": type = .param
        case "fault": type = .fault
        case "member": type = .member
        case "name": type = .name
        case "struct": type = .structContainer
        case "array": type = .arrayContainer
        case "data": type = .dataContainer
        case "int", "i4", "boolean", "string", "double", "datetime.iso8601", "base64":
            type = .scalar(elementName.lowercased())
        default:
            type = .other
        }
        stack.append(ElementNode(name: elementName, type: type))
    }

    func parser(_ parser: XMLParser, foundCharacters string: String) {
        guard !stack.isEmpty else { return }
        stack[stack.count - 1].text.append(string)
    }

    func parser(_ parser: XMLParser, didEndElement elementName: String, namespaceURI: String?, qualifiedName qName: String?) {
        guard !stack.isEmpty else { return }
        let node = stack.removeLast()
        let trimmed = node.text.trimmingCharacters(in: .whitespacesAndNewlines)

        var createdValue: XMLRPCValue? = node.childValue

        switch node.type {
        case .scalar(let tag):
            switch tag {
            case "int", "i4":
                createdValue = .int(Int(trimmed) ?? 0)
            case "boolean":
                createdValue = .boolean(trimmed == "1" || trimmed.lowercased() == "true")
            case "double":
                createdValue = .double(Double(trimmed) ?? 0.0)
            case "string":
                createdValue = .string(node.text)
            case "datetime.iso8601":
                let formatter = ISO8601DateFormatter()
                formatter.formatOptions = [.withInternetDateTime, .withFractionalSeconds]
                let date = formatter.date(from: trimmed) ?? Date()
                createdValue = .dateTime(date)
            case "base64":
                let data = Data(base64Encoded: trimmed) ?? Data()
                createdValue = .base64(data)
            default:
                createdValue = .string(node.text)
            }

        case .name:
            if !stack.isEmpty {
                stack[stack.count - 1].currentMemberName = node.text
            }

        case .structContainer:
            createdValue = .structure(node.structMembers)

        case .arrayContainer, .dataContainer:
            createdValue = .array(node.arrayElements)

        case .value:
            if createdValue == nil && !node.text.isEmpty {
                createdValue = .string(node.text)
            }

        case .member:
            if !stack.isEmpty {
                let parentIdx = stack.count - 1
                for (k, v) in node.structMembers {
                    stack[parentIdx].structMembers[k] = v
                }
            }

        case .fault:
            if case .structure(let dict) = createdValue {
                faultValue = dict
            }

        case .param:
            if let val = createdValue {
                resultValue = val
            }

        default:
            break
        }

        if let val = createdValue, !stack.isEmpty {
            let parentIdx = stack.count - 1
            if stack[parentIdx].type == .value {
                stack[parentIdx].childValue = val
            } else if stack[parentIdx].type == .member {
                if let key = stack[parentIdx].currentMemberName {
                    stack[parentIdx].structMembers[key] = val
                }
            } else if stack[parentIdx].type == .arrayContainer || stack[parentIdx].type == .dataContainer {
                stack[parentIdx].arrayElements.append(val)
            } else if stack[parentIdx].type == .structContainer {
                if let key = stack[parentIdx].currentMemberName {
                    stack[parentIdx].structMembers[key] = val
                }
            } else if stack[parentIdx].type == .param {
                stack[parentIdx].childValue = val
                resultValue = val
            } else if stack[parentIdx].type == .fault {
                if case .structure(let dict) = val {
                    faultValue = dict
                }
            } else if stack[parentIdx].type == .other {
                stack[parentIdx].childValue = val
                if resultValue == nil {
                    resultValue = val
                }
            }
        }
    }
}
