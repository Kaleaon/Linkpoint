import os

from tools.protocol_gen.emitters.base import BaseEmitter
from tools.protocol_gen.proto_ast.models import ProtocolAST


class SwiftEmitter(BaseEmitter):
    def __init__(self):
        super().__init__("Swift", ".swift")

    def emit(self, ast: ProtocolAST, out_dir: str) -> dict[str, str]:
        os.makedirs(out_dir, exist_ok=True)

        code = self._generate_swift_code(ast)
        out_file = os.path.join(out_dir, "GeneratedProtocol.swift")

        with open(out_file, "w", encoding="utf-8", newline="\n") as f:
            f.write(code)

        return {out_file: code}

    def _generate_swift_code(self, ast: ProtocolAST) -> str:
        out = []
        out.append(self.get_header_warning("//"))
        out.append("import Foundation\n")

        out.append("public struct GeneratedProtocolCatalog {")
        out.append(f'    public static let templateVersion: String = "{ast.version}"')
        out.append("    public static let registeredMessages: [String: UInt32] = [")
        for msg in ast.messages:
            out.append(f'        "{msg.name}": {msg.message_number},')
        out.append("    ]\n")

        out.append("""    public static func decompressZerocoded(_ src: Data) -> Data {
        var dest = Data()
        var i = 0
        let srcArray = Array(src)
        while i < srcArray.count {
            let b = srcArray[i]
            if b == 0 {
                if i + 1 < srcArray.count {
                    let count = Int(srcArray[i + 1])
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

        out.append("// Generated UDP Messages")
        for msg in ast.messages:
            out.append(f"public struct {msg.name}Packet {{")
            out.append(f'    public let name: String = "{msg.name}"')
            out.append(f"    public let messageNumber: UInt32 = {msg.message_number}")
            out.append(f'    public let frequency: String = "{msg.frequency}"')
            out.append(
                f"    public let isZerocoded: Bool = {'true' if msg.encoding == 'Zerocoded' else 'false'}\n"
            )
            out.append("    public init() {}\n")
            out.append("    public func serialize() -> Data {")
            out.append("        var num = messageNumber.littleEndian")
            out.append("        return Data(bytes: &num, count: MemoryLayout<UInt32>.size)")
            out.append("    }")
            out.append("}\n")

        out.append("// Generated LLSD Capability Schemas")
        for schema in ast.llsd_schemas:
            out.append(f"public struct {schema.title}Capabilities: Codable {{")
            for p_name, prop in schema.properties.items():
                swift_type = "String?"
                if prop.data_type == "integer":
                    swift_type = "Int?"
                elif prop.data_type == "number":
                    swift_type = "Double?"
                elif prop.data_type == "boolean":
                    swift_type = "Bool?"
                elif prop.data_type == "array":
                    swift_type = "[String]?"
                out.append(f"    public var {p_name}: {swift_type}")
            out.append("\n    public init(")
            init_params = []
            for p_name, prop in schema.properties.items():
                swift_type = "String?"
                if prop.data_type == "integer":
                    swift_type = "Int?"
                elif prop.data_type == "number":
                    swift_type = "Double?"
                elif prop.data_type == "boolean":
                    swift_type = "Bool?"
                elif prop.data_type == "array":
                    swift_type = "[String]?"
                init_params.append(f"{p_name}: {swift_type} = nil")
            out.append("        " + ",\n        ".join(init_params))
            out.append("    ) {")
            for p_name in schema.properties.keys():
                out.append(f"        self.{p_name} = {p_name}")
            out.append("    }")
            out.append("}\n")

        return "\n".join(out)
