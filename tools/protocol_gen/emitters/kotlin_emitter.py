import os
from typing import Dict
from tools.protocol_gen.proto_ast.models import ProtocolAST
from tools.protocol_gen.emitters.base import BaseEmitter

class KotlinEmitter(BaseEmitter):
    def __init__(self):
        super().__init__("Kotlin", ".kt")

    def emit(self, ast: ProtocolAST, out_dir: str) -> Dict[str, str]:
        os.makedirs(out_dir, exist_ok=True)

        proto_code = self._generate_protocol_code(ast)
        llsd_code = self._generate_llsd_code(ast)

        proto_file = os.path.join(out_dir, "GeneratedProtocol.kt")
        llsd_file = os.path.join(out_dir, "GeneratedLLSDCapabilities.kt")

        results = {
            proto_file: proto_code,
            llsd_file: llsd_code
        }

        for path, content in results.items():
            with open(path, "w", encoding="utf-8") as f:
                f.write(content)

        return results

    def _generate_protocol_code(self, ast: ProtocolAST) -> str:
        out = []
        out.append(self.get_header_warning("//"))
        out.append("package com.linkpoint.protocol.generated\n")
        out.append("import java.nio.ByteBuffer\nimport java.nio.ByteOrder\n")

        out.append("object GeneratedProtocolCatalog {")
        out.append(f'    const val TEMPLATE_VERSION = "{ast.version}"')
        out.append("    val REGISTERED_MESSAGES = mapOf(")
        for msg in ast.messages:
            out.append(f'        "{msg.name}" to {msg.message_number},')
        out.append("    )\n")

        # Zero-coding decompression helper
        out.append("""    fun decompressZerocoded(src: ByteArray): ByteArray {
        val dest = mutableListOf<Byte>()
        var i = 0
        while (i < src.size) {
            val b = src[i]
            if (b == 0.toByte()) {
                if (i + 1 < src.size) {
                    val count = src[i + 1].toInt() and 0xFF
                    if (count == 0) {
                        dest.add(0.toByte())
                        i += 2
                    } else {
                        repeat(count) { dest.add(0.toByte()) }
                        i += 2
                    }
                } else {
                    dest.add(0.toByte())
                    i++
                }
            } else {
                dest.add(b)
                i++
            }
        }
        return dest.toByteArray()
    }
}
""")

        # Message data classes
        for msg in ast.messages:
            msg_num = f"{msg.message_number}L.toInt()" if msg.message_number > 2147483647 else str(msg.message_number)
            out.append(f"// Message: {msg.name} ({msg.frequency} {msg.message_number})")
            out.append(f"data class {msg.name}Packet(")
            out.append(f'    val messageName: String = "{msg.name}",')
            out.append(f"    val messageNumber: Int = {msg_num},")
            out.append(f'    val frequency: String = "{msg.frequency}",')
            out.append(f'    val isZerocoded: Boolean = {"true" if msg.encoding == "Zerocoded" else "false"}')
            out.append(") {\n")
            out.append("    fun serialize(): ByteArray {")
            out.append("        val buffer = ByteBuffer.allocate(2048).order(ByteOrder.LITTLE_ENDIAN)")
            out.append(f"        buffer.putInt({msg_num})")
            out.append("        return buffer.array().copyOf(buffer.position())")
            out.append("    }\n")
            out.append("}\n")

        return "\n".join(out)

    def _generate_llsd_code(self, ast: ProtocolAST) -> str:
        out = []
        out.append(self.get_header_warning("//"))
        out.append("package com.linkpoint.protocol.generated\n")

        for schema in ast.llsd_schemas:
            out.append(f"// LLSD Schema: {schema.title}")
            out.append(f"data class {schema.title}Capabilities(")
            fields_def = []
            for p_name, prop in schema.properties.items():
                kt_type = "String"
                if prop.data_type == "integer":
                    kt_type = "Int"
                elif prop.data_type == "number":
                    kt_type = "Double"
                elif prop.data_type == "boolean":
                    kt_type = "Boolean"
                elif prop.data_type == "array":
                    kt_type = "List<String>"

                opt = "?" if not prop.required else "?"
                fields_def.append(f"    val {p_name}: {kt_type}{opt} = null")
            out.append(",\n".join(fields_def))
            out.append(")\n")

        return "\n".join(out)
