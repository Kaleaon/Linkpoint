import os

from tools.protocol_gen.emitters.base import BaseEmitter
from tools.protocol_gen.proto_ast.models import ProtocolAST


class TypeScriptEmitter(BaseEmitter):
    def __init__(self):
        super().__init__("TypeScript", ".ts")

    def emit(self, ast: ProtocolAST, out_dir: str) -> dict[str, str]:
        os.makedirs(out_dir, exist_ok=True)

        code = self._generate_ts_code(ast)
        out_file = os.path.join(out_dir, "generated.ts")

        with open(out_file, "w", encoding="utf-8", newline="\n") as f:
            f.write(code)

        return {out_file: code}

    def _generate_ts_code(self, ast: ProtocolAST) -> str:
        out = []
        out.append(self.get_header_warning("//"))

        out.append(f'export const TEMPLATE_VERSION = "{ast.version}";\n')

        out.append("export const REGISTERED_MESSAGES: Record<string, number> = {")
        for msg in ast.messages:
            out.append(f'  "{msg.name}": {msg.message_number},')
        out.append("};\n")

        out.append("""export function decompressZerocoded(src: Uint8Array): Uint8Array {
  const dest: number[] = [];
  let i = 0;
  while (i < src.length) {
    const b = src[i];
    if (b === 0) {
      if (i + 1 < src.length) {
        const count = src[i + 1];
        if (count === 0) {
          dest.push(0);
          i += 2;
        } else {
          for (let k = 0; k < count; k++) {
            dest.push(0);
          }
          i += 2;
        }
      } else {
        dest.push(0);
        i += 1;
      }
    } else {
      dest.push(b);
      i += 1;
    }
  }
  return new Uint8Array(dest);
}
""")

        out.append("// Generated UDP Messages")
        for msg in ast.messages:
            out.append(f"export interface {msg.name}Packet {{")
            out.append(f'  messageName: "{msg.name}";')
            out.append(f"  messageNumber: {msg.message_number};")
            out.append(f'  frequency: "{msg.frequency}";')
            out.append(
                f"  isZerocoded: {'true' if msg.encoding == 'Zerocoded' else 'false'};"
            )
            out.append("}\n")

        out.append("// Generated LLSD Capability Schemas")
        for schema in ast.llsd_schemas:
            out.append(f"export interface {schema.title}Capabilities {{")
            for p_name, prop in schema.properties.items():
                ts_type = "string"
                if prop.data_type == "integer" or prop.data_type == "number":
                    ts_type = "number"
                elif prop.data_type == "boolean":
                    ts_type = "boolean"
                elif prop.data_type == "array":
                    ts_type = "string[]"
                opt = "?" if not prop.required else ""
                out.append(f"  {p_name}{opt}: {ts_type};")
            out.append("}\n")

        return "\n".join(out)
