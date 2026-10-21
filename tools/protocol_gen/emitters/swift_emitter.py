import os
from typing import Dict

from tools.protocol_gen.emitters.base import BaseEmitter
from tools.protocol_gen.proto_ast.models import ProtocolAST


class SwiftEmitter(BaseEmitter):
    def __init__(self):
        super().__init__("Swift", ".swift")

    def emit(self, ast: ProtocolAST, out_dir: str) -> Dict[str, str]:
        os.makedirs(out_dir, exist_ok=True)

        proto_code = self._generate_swift_protocol(ast)
        llsd_code = self._generate_swift_llsd(ast)

        proto_file = os.path.join(out_dir, "GeneratedProtocol.swift")
        llsd_file = os.path.join(out_dir, "GeneratedLLSDCapabilities.swift")

        results = {
            proto_file: proto_code,
            llsd_file: llsd_code,
        }

        for path, content in results.items():
            with open(path, "w", encoding="utf-8", newline="\n") as f:
                f.write(content)

        return results

    def _generate_swift_protocol(self, ast: ProtocolAST) -> str:
        out = []
        out.append(self.get_header_warning("//"))
        out.append("import Foundation\n")

        out.append("public struct GeneratedProtocol {")
        out.append(f'    public static let templateVersion: String = "{ast.version}"')
        out.append("    public static let registeredMessages: [String: UInt32] = [")
        for msg in ast.messages:
            out.append(f'        "{msg.name}": {msg.message_number},')
        out.append("    ]\n")

        # Zero-coded decompression
        out.append("""    public static func decompressZerocoded(_ src: Data) -> Data {
        var dest = Data()
        let bytes = [UInt8](src)
        var i = 0
        let len = bytes.count
        while i < len {
            let b = bytes[i]
            if b == 0 {
                if i + 1 < len {
                    let count = Int(bytes[i + 1])
                    if count == 0 {
                        dest.append(0)
                        i += 2
                    } else {
                        dest.append(contentsOf: [UInt8](repeating: 0, count: count))
                        i += 2
                    }
                } else {
                    dest.append(0)
                    i += 1
                }
            } else {
                dest.append(b)
                i += 1
            }
        }
        return dest
    }

    public static func decompressZerocoded(_ src: [UInt8]) -> [UInt8] {
        var dest: [UInt8] = []
        var i = 0
        let len = src.count
        while i < len {
            let b = src[i]
            if b == 0 {
                if i + 1 < len {
                    let count = Int(src[i + 1])
                    if count == 0 {
                        dest.append(0)
                        i += 2
                    } else {
                        dest.append(contentsOf: [UInt8](repeating: 0, count: count))
                        i += 2
                    }
                } else {
                    dest.append(0)
                    i += 1
                }
            } else {
                dest.append(b)
                i += 1
            }
        }
        return dest
    }
}
""")

        # UDP Message structs
        out.append("// Generated UDP Messages")
        for msg in ast.messages:
            out.append(f"// Message: {msg.name} ({msg.frequency} {msg.message_number})")
            out.append(f"public struct {msg.name}Packet {{")
            out.append(f'    public var messageName: String = "{msg.name}"')
            out.append(f"    public var messageNumber: UInt32 = {msg.message_number}")
            out.append(f'    public var frequency: String = "{msg.frequency}"')
            out.append(
                f"    public var isZerocoded: Bool = {'true' if msg.encoding == 'Zerocoded' else 'false'}\n"
            )
            out.append("    public init(")
            out.append(f'        messageName: String = "{msg.name}",')
            out.append(f"        messageNumber: UInt32 = {msg.message_number},")
            out.append(f'        frequency: String = "{msg.frequency}",')
            out.append(
                f"        isZerocoded: Bool = {'true' if msg.encoding == 'Zerocoded' else 'false'}"
            )
            out.append("    ) {")
            out.append("        self.messageName = messageName")
            out.append("        self.messageNumber = messageNumber")
            out.append("        self.frequency = frequency")
            out.append("        self.isZerocoded = isZerocoded")
            out.append("    }\n")
            out.append("    public func serialize() -> Data {")
            out.append("        var data = Data()")
            out.append("        var num = messageNumber.littleEndian")
            out.append("        data.append(Data(bytes: &num, count: MemoryLayout<UInt32>.size))")
            out.append("        return data")
            out.append("    }")
            out.append("}\n")

        return "\n".join(out)

    def _generate_swift_llsd(self, ast: ProtocolAST) -> str:
        out = []
        out.append(self.get_header_warning("//"))
        out.append("import Foundation\n")

        for schema in ast.llsd_schemas:
            out.append(f"// LLSD Schema: {schema.title}")
            out.append(f"public struct {schema.title}Capabilities: Codable {{")

            init_params = []
            init_assigns = []

            for p_name, prop in schema.properties.items():
                swift_type = self._map_swift_type(prop)
                out.append(f"    public var {p_name}: {swift_type}")

                if prop.required:
                    init_params.append(f"        {p_name}: {swift_type}")
                else:
                    init_params.append(f"        {p_name}: {swift_type} = nil")
                init_assigns.append(f"        self.{p_name} = {p_name}")

            out.append("")
            if init_params:
                out.append("    public init(")
                out.append(",\n".join(init_params))
                out.append("    ) {")
                out.append("\n".join(init_assigns))
                out.append("    }")
            else:
                out.append("    public init() {}")
            out.append("}\n")

        return "\n".join(out)

    def _map_swift_type(self, prop) -> str:
        if prop.data_type == "integer":
            base_type = "Int"
        elif prop.data_type == "number":
            base_type = "Double"
        elif prop.data_type == "boolean":
            base_type = "Bool"
        elif prop.data_type == "array":
            base_type = "[String]"
        else:
            base_type = "String"

        if prop.required:
            return base_type
        return f"{base_type}?"
