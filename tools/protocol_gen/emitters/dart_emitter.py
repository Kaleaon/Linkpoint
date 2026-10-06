import os
from typing import Dict
from tools.protocol_gen.proto_ast.models import ProtocolAST
from tools.protocol_gen.emitters.base import BaseEmitter

class DartEmitter(BaseEmitter):
    def __init__(self):
        super().__init__("Dart", ".dart")

    def emit(self, ast: ProtocolAST, out_dir: str) -> Dict[str, str]:
        os.makedirs(out_dir, exist_ok=True)

        code = self._generate_dart_code(ast)
        out_file = os.path.join(out_dir, "generated.dart")

        with open(out_file, "w", encoding="utf-8", newline="\n") as f:
            f.write(code)

        return {out_file: code}

    def _generate_dart_code(self, ast: ProtocolAST) -> str:
        out = []
        out.append(self.get_header_warning("//"))
        out.append("import 'dart:typed_data';\n")

        out.append("class GeneratedProtocolCatalog {")
        out.append(f'  static const String templateVersion = "{ast.version}";')
        out.append("  static const Map<String, int> registeredMessages = {")
        for msg in ast.messages:
            out.append(f'    "{msg.name}": {msg.message_number},')
        out.append("  };\n")

        out.append("""  static Uint8List decompressZerocoded(Uint8List src) {
    final dest = <int>[];
    int i = 0;
    while (i < src.length) {
      final b = src[i];
      if (b == 0) {
        if (i + 1 < src.length) {
          final count = src[i + 1];
          if (count == 0) {
            dest.add(0);
            i += 2;
          } else {
            for (int k = 0; k < count; k++) {
              dest.add(0);
            }
            i += 2;
          }
        } else {
          dest.add(0);
          i += 1;
        }
      } else {
        dest.add(b);
        i += 1;
      }
    }
    return Uint8List.fromList(dest);
  }
}
""")

        for msg in ast.messages:
            out.append(f"class {msg.name}Packet {{")
            out.append(f'  final String name = "{msg.name}";')
            out.append(f"  final int messageNumber = {msg.message_number};")
            out.append(f'  final String frequency = "{msg.frequency}";')
            out.append(f'  final bool isZerocoded = {"true" if msg.encoding == "Zerocoded" else "false"};\n')
            out.append("  Uint8List serialize() {")
            out.append("    final buffer = ByteData(8);")
            out.append(f"    buffer.setUint32(0, {msg.message_number}, Endian.little);")
            out.append("    return buffer.buffer.asUint8List();")
            out.append("  }")
            out.append("}\n")

        for schema in ast.llsd_schemas:
            out.append(f"class {schema.title}Capabilities {{")
            for p_name, prop in schema.properties.items():
                dart_type = "String?"
                if prop.data_type == "integer":
                    dart_type = "int?"
                elif prop.data_type == "number":
                    dart_type = "double?"
                elif prop.data_type == "boolean":
                    dart_type = "bool?"
                elif prop.data_type == "array":
                    dart_type = "List<String>?"
                out.append(f"  final {dart_type} {p_name};")

            out.append(f"\n  {schema.title}Capabilities({{")
            for p_name in schema.properties.keys():
                out.append(f"    this.{p_name},")
            out.append("  });")
            out.append("}\n")

        return "\n".join(out)
