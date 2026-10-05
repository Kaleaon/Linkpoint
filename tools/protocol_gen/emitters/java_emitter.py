import os
from typing import Dict
from tools.protocol_gen.proto_ast.models import ProtocolAST
from tools.protocol_gen.emitters.base import BaseEmitter

class JavaEmitter(BaseEmitter):
    def __init__(self):
        super().__init__("Java", ".java")

    def emit(self, ast: ProtocolAST, out_dir: str) -> Dict[str, str]:
        os.makedirs(out_dir, exist_ok=True)

        proto_code = self._generate_java_protocol(ast)
        llsd_code = self._generate_java_llsd(ast)

        proto_file = os.path.join(out_dir, "GeneratedProtocol.java")
        llsd_file = os.path.join(out_dir, "GeneratedLLSDCapabilities.java")

        results = {
            proto_file: proto_code,
            llsd_file: llsd_code
        }

        for path, content in results.items():
            with open(path, "w", encoding="utf-8") as f:
                f.write(content)

        return results

    def _generate_java_protocol(self, ast: ProtocolAST) -> str:
        out = []
        out.append(self.get_header_warning("//"))
        out.append("package com.linkpoint.protocol.java.generated;\n")
        out.append("import java.nio.ByteBuffer;")
        out.append("import java.nio.ByteOrder;")
        out.append("import java.util.HashMap;")
        out.append("import java.util.Map;\n")

        out.append("public class GeneratedProtocol {")
        out.append(f'    public static final String TEMPLATE_VERSION = "{ast.version}";')
        out.append("    public static final Map<String, Integer> REGISTERED_MESSAGES = new HashMap<>();\n")
        out.append("    static {")
        for msg in ast.messages:
            num_val = f"(int) {msg.message_number}L" if msg.message_number > 2147483647 or msg.message_number < -2147483648 else str(msg.message_number)
            out.append(f'        REGISTERED_MESSAGES.put("{msg.name}", {num_val});')
        out.append("    }\n")

        out.append("""    public static byte[] decompressZerocoded(byte[] src) {
        ByteBuffer dest = ByteBuffer.allocate(src.length * 4);
        int i = 0;
        while (i < src.length) {
            byte b = src[i];
            if (b == 0) {
                if (i + 1 < src.length) {
                    int count = src[i + 1] & 0xFF;
                    if (count == 0) {
                        dest.put((byte) 0);
                        i += 2;
                    } else {
                        for (int k = 0; k < count; k++) {
                            dest.put((byte) 0);
                        }
                        i += 2;
                    }
                } else {
                    dest.put((byte) 0);
                    i++;
                }
            } else {
                dest.put(b);
                i++;
            }
        }
        byte[] result = new byte[dest.position()];
        dest.flip();
        dest.get(result);
        return result;
    }
}
""")
        return "\n".join(out)

    def _generate_java_llsd(self, ast: ProtocolAST) -> str:
        out = []
        out.append(self.get_header_warning("//"))
        out.append("package com.linkpoint.protocol.java.generated;\n")

        out.append("public class GeneratedLLSDCapabilities {")
        for schema in ast.llsd_schemas:
            out.append(f"    public static class {schema.title}Capabilities {{")
            for p_name, prop in schema.properties.items():
                j_type = "String"
                if prop.data_type == "integer":
                    j_type = "Integer"
                elif prop.data_type == "number":
                    j_type = "Double"
                elif prop.data_type == "boolean":
                    j_type = "Boolean"
                out.append(f"        public {j_type} {p_name};")
            out.append("    }\n")
        out.append("}")

        return "\n".join(out)
