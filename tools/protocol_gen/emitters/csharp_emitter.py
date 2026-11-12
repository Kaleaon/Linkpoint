import os

from tools.protocol_gen.emitters.base import BaseEmitter
from tools.protocol_gen.proto_ast.models import ProtocolAST


class CSharpEmitter(BaseEmitter):
    def __init__(self):
        super().__init__("CSharp", ".cs")

    def emit(self, ast: ProtocolAST, out_dir: str) -> dict[str, str]:
        os.makedirs(out_dir, exist_ok=True)

        code = self._generate_csharp_code(ast)
        out_file = os.path.join(out_dir, "GeneratedProtocol.cs")

        with open(out_file, "w", encoding="utf-8", newline="\n") as f:
            f.write(code)

        return {out_file: code}

    def _generate_csharp_code(self, ast: ProtocolAST) -> str:
        out = []
        out.append(self.get_header_warning("//"))
        out.append("using System;")
        out.append("using System.Collections.Generic;")
        out.append("using System.IO;\n")

        out.append("namespace Linkpoint.Protocol")
        out.append("{")
        out.append("    public static class GeneratedProtocolCatalog")
        out.append("    {")
        out.append(f'        public const string TemplateVersion = "{ast.version}";')
        out.append("        public static readonly IReadOnlyDictionary<string, uint> RegisteredMessages = new Dictionary<string, uint>")
        out.append("        {")
        for msg in ast.messages:
            out.append(f'            {{ "{msg.name}", {msg.message_number} }},')
        out.append("        };\n")

        out.append("""        public static byte[] DecompressZerocoded(byte[] src)
        {
            using var dest = new MemoryStream();
            int i = 0;
            while (i < src.Length)
            {
                byte b = src[i];
                if (b == 0)
                {
                    if (i + 1 < src.Length)
                    {
                        byte count = src[i + 1];
                        if (count == 0)
                        {
                            dest.WriteByte(0);
                            i += 2;
                        }
                        else
                        {
                            for (int k = 0; k < count; k++)
                            {
                                dest.WriteByte(0);
                            }
                            i += 2;
                        }
                    }
                    else
                    {
                        dest.WriteByte(0);
                        i += 1;
                    }
                }
                else
                {
                    dest.WriteByte(b);
                    i += 1;
                }
            }
            return dest.ToArray();
        }
    }
""")

        out.append("    // Generated UDP Messages")
        for msg in ast.messages:
            out.append(f"    public class {msg.name}Packet")
            out.append("    {")
            out.append(f'        public string Name => "{msg.name}";')
            out.append(f"        public uint MessageNumber => {msg.message_number};")
            out.append(f'        public string Frequency => "{msg.frequency}";')
            out.append(
                f"        public bool IsZerocoded => {'true' if msg.encoding == 'Zerocoded' else 'false'};\n"
            )
            out.append("        public byte[] Serialize()")
            out.append("        {")
            out.append("            return BitConverter.GetBytes(MessageNumber);")
            out.append("        }")
            out.append("    }\n")

        out.append("    // Generated LLSD Capability Schemas")
        for schema in ast.llsd_schemas:
            out.append(f"    public class {schema.title}Capabilities")
            out.append("    {")
            for p_name, prop in schema.properties.items():
                cs_type = "string?"
                if prop.data_type == "integer":
                    cs_type = "int?"
                elif prop.data_type == "number":
                    cs_type = "double?"
                elif prop.data_type == "boolean":
                    cs_type = "bool?"
                elif prop.data_type == "array":
                    cs_type = "List<string>?"
                out.append(f"        public {cs_type} {p_name} {{ get; set; }}")
            out.append("    }\n")

        out.append("}")

        return "\n".join(out)
