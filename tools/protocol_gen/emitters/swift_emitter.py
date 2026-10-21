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

        out.append("public enum GeneratedProtocolCatalog {")
        out.append(f'    public static let templateVersion = "{ast.version}"')
        out.append("    public static let registeredMessages: [String: Int] = [")
        for msg in ast.messages:
            out.append(f'        "{msg.name}": {msg.message_number},')
        out.append("    ]\n")

        out.append("""    public static func decompressZerocoded(_ src: [UInt8]) -> [UInt8] {
        var dest = [UInt8]()
        var i = 0
        while i < src.count {
            let b = src[i]
            if b == 0 {
                if i + 1 < src.count {
                    let count = Int(src[i + 1])
                    if count == 0 {
                        dest.append(0)
                        i += 2
                    } else {
                        dest.append(contentsOf: Array(repeating: UInt8(0), count: count))
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
            out.append(f'    public let messageName: String = "{msg.name}"')
            out.append(f"    public let messageNumber: Int = {msg.message_number}")
            out.append(f'    public let frequency: String = "{msg.frequency}"')
            out.append(
                f"    public let isZerocoded: Bool = {'true' if msg.encoding == 'Zerocoded' else 'false'}\n"
            )
            out.append("    public init() {}\n}")
            out.append("")

        out.append("// Generated LLSD Capability Schemas")
        for schema in ast.llsd_schemas:
            out.append(f"public struct {schema.title}Capabilities {{")
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
            init_args = []
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
                init_args.append(f"{p_name}: {swift_type} = nil")
            out.append("        " + ",\n        ".join(init_args))
            out.append("    ) {")
            for p_name in schema.properties.keys():
                out.append(f"        self.{p_name} = {p_name}")
            out.append("    }\n}")
            out.append("")

        return "\n".join(out)
