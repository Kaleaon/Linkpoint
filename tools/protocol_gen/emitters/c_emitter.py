import os
from typing import Dict
from tools.protocol_gen.proto_ast.models import ProtocolAST
from tools.protocol_gen.emitters.base import BaseEmitter

class CEmitter(BaseEmitter):
    def __init__(self):
        super().__init__("C", ".h")

    def emit(self, ast: ProtocolAST, out_dir: str) -> Dict[str, str]:
        os.makedirs(out_dir, exist_ok=True)

        header_code = self._generate_c_header(ast)
        source_code = self._generate_c_source(ast)

        header_file = os.path.join(out_dir, "generated_protocol.h")
        source_file = os.path.join(out_dir, "generated_protocol.c")

        results = {
            header_file: header_code,
            source_file: source_code
        }

        for path, content in results.items():
            with open(path, "w", encoding="utf-8", newline="\n") as f:
                f.write(content)

        return results

    def _generate_c_header(self, ast: ProtocolAST) -> str:
        out = []
        out.append(self.get_header_warning("/*"))
        out.append("#ifndef GENERATED_PROTOCOL_H")
        out.append("#define GENERATED_PROTOCOL_H\n")
        out.append("#include <stdint.h>")
        out.append("#include <stddef.h>")
        out.append("#include <stdbool.h>\n")

        out.append(f'#define PROTOCOL_TEMPLATE_VERSION "{ast.version}"\n')

        # Functions
        out.append("size_t decompress_zerocoded(const uint8_t* src, size_t src_len, uint8_t* dest, size_t dest_capacity);\n")

        # Message Structs
        for msg in ast.messages:
            out.append(f"// Message: {msg.name}")
            out.append(f"#define MSG_ID_{msg.name.upper()} {msg.message_number}")
            out.append(f"typedef struct {msg.name}Packet {{")
            out.append("    uint32_t message_id;")
            out.append("    bool is_zerocoded;")
            out.append(f"}} {msg.name}Packet;\n")

        out.append("#endif /* GENERATED_PROTOCOL_H */")
        return "\n".join(out)

    def _generate_c_source(self, ast: ProtocolAST) -> str:
        out = []
        out.append(self.get_header_warning("/*"))
        out.append('#include "generated_protocol.h"')
        out.append("#include <string.h>\n")

        out.append("""size_t decompress_zerocoded(const uint8_t* src, size_t src_len, uint8_t* dest, size_t dest_capacity) {
    size_t i = 0;
    size_t out_len = 0;
    while (i < src_len && out_len < dest_capacity) {
        uint8_t b = src[i];
        if (b == 0) {
            if (i + 1 < src_len) {
                uint8_t count = src[i + 1];
                if (count == 0) {
                    dest[out_len++] = 0;
                    i += 2;
                } else {
                    for (uint8_t k = 0; k < count && out_len < dest_capacity; k++) {
                        dest[out_len++] = 0;
                    }
                    i += 2;
                }
            } else {
                dest[out_len++] = 0;
                i += 1;
            }
        } else {
            dest[out_len++] = b;
            i += 1;
        }
    }
    return out_len;
}
""")
        return "\n".join(out)
