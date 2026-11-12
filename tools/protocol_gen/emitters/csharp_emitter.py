import os
from typing import Dict

from tools.protocol_gen.emitters.base import BaseEmitter
from tools.protocol_gen.proto_ast.models import ProtocolAST


class CSharpEmitter(BaseEmitter):
    def __init__(self):
        super().__init__("CSharp", ".cs")

    def emit(self, ast: ProtocolAST, out_dir: str) -> Dict[str, str]:
        os.makedirs(out_dir, exist_ok=True)

        proto_code = self._generate_csharp_protocol(ast)
        llsd_code = self._generate_csharp_llsd(ast)

        proto_file = os.path.join(out_dir, "GeneratedProtocol.cs")
        llsd_file = os.path.join(out_dir, "GeneratedLLSDCapabilities.cs")

        results = {
            proto_file: proto_code,
            llsd_file: llsd_code,
        }

        for path, content in results.items():
            with open(path, "w", encoding="utf-8", newline="\n") as f:
                f.write(content)

        return results

    def _generate_csharp_protocol(self, ast: ProtocolAST) -> str:
        out = []
        out.append(self.get_header_warning("//"))
        out.append("using System;")
        out.append("using System.Collections.Generic;")
        out.append("using System.IO;\n")

        out.append("namespace Linkpoint.Protocol.Generated")
        out.append("{")
        out.append("    public static class GeneratedProtocol")
        out.append("    {")
        out.append(f'        public const string TemplateVersion = "{ast.version}";')
        out.append(
            "        public static readonly Dictionary<string, uint> RegisteredMessages = new Dictionary<string, uint>"
        )
        out.append("        {")
        for msg in ast.messages:
            out.append(f'            {{ "{msg.name}", {msg.message_number} }},')
        out.append("        };\n")

        # Zero-coded decompression
        out.append("""        public static byte[] DecompressZerocoded(byte[] src)
        {
            if (src == null) return Array.Empty<byte>();
            List<byte> dest = new List<byte>(src.Length * 2);
            int i = 0;
            while (i < src.Length)
            {
                byte b = src[i];
                if (b == 0)
                {
                    if (i + 1 < src.Length)
                    {
                        int count = src[i + 1];
                        if (count == 0)
                        {
                            dest.Add(0);
                            i += 2;
                        }
                        else
                        {
                            for (int k = 0; k < count; k++)
                            {
                                dest.Add(0);
                            }
                            i += 2;
                        }
                    }
                    else
                    {
                        dest.Add(0);
                        i += 1;
                    }
                }
                else
                {
                    dest.Add(b);
                    i += 1;
                }
            }
            return dest.ToArray();
        }

        public static byte[] decompressZerocoded(byte[] src) => DecompressZerocoded(src);
    }
""")

        # Message Packet classes
        out.append("    // Generated UDP Messages")
        for msg in ast.messages:
            out.append(f"    public class {msg.name}Packet")
            out.append("    {")
            out.append(
                f'        public string MessageName {{ get; set; }} = "{msg.name}";'
            )
            out.append(
                f"        public uint MessageNumber {{ get; set; }} = {msg.message_number};"
            )
            out.append(
                f'        public string Frequency {{ get; set; }} = "{msg.frequency}";'
            )
            out.append(
                f"        public bool IsZerocoded {{ get; set; }} = {'true' if msg.encoding == 'Zerocoded' else 'false'};\n"
            )
            out.append("        public byte[] Serialize()")
            out.append("        {")
            out.append("            using (MemoryStream ms = new MemoryStream())")
            out.append("            using (BinaryWriter writer = new BinaryWriter(ms))")
            out.append("            {")
            out.append("                writer.Write(MessageNumber);")
            out.append("                return ms.ToArray();")
            out.append("            }")
            out.append("        }\n")
            out.append("        public byte[] serialize() => Serialize();")
            out.append("    }\n")

        out.append("}")
        return "\n".join(out)

    def _generate_csharp_llsd(self, ast: ProtocolAST) -> str:
        out = []
        out.append(self.get_header_warning("//"))
        out.append("using System;")
        out.append("using System.Collections.Generic;\n")

        out.append("namespace Linkpoint.Protocol.Generated")
        out.append("{")
        for schema in ast.llsd_schemas:
            out.append(f"    // LLSD Schema: {schema.title}")
            out.append(f"    public class {schema.title}Capabilities")
            out.append("    {")
            for p_name, prop in schema.properties.items():
                cs_type = self._map_csharp_type(prop)
                init_val = ""
                if prop.data_type == "array" and prop.required:
                    init_val = " = new List<string>();"
                out.append(
                    f"        public {cs_type} {p_name} {{ get; set; }}{init_val}"
                )
            out.append("    }\n")

        out.append("}")
        return "\n".join(out)

    def _map_csharp_type(self, prop) -> str:
        if prop.data_type == "integer":
            return "int" if prop.required else "int?"
        elif prop.data_type == "number":
            return "double" if prop.required else "double?"
        elif prop.data_type == "boolean":
            return "bool" if prop.required else "bool?"
        elif prop.data_type == "array":
            return "List<string>"
        else:
            return "string"
