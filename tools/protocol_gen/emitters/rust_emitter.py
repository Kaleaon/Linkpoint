import os
import subprocess
from typing import Dict
from tools.protocol_gen.proto_ast.models import ProtocolAST
from tools.protocol_gen.emitters.base import BaseEmitter

class RustEmitter(BaseEmitter):
    def __init__(self):
        super().__init__("Rust", ".rs")

    def emit(self, ast: ProtocolAST, out_dir: str) -> Dict[str, str]:
        os.makedirs(out_dir, exist_ok=True)

        code = self._generate_rust_code(ast)
        out_file = os.path.join(out_dir, "mod.rs")

        with open(out_file, "w", encoding="utf-8") as f:
            f.write(code)

        try:
            subprocess.run(["rustfmt", out_file], check=False, stdout=subprocess.DEVNULL, stderr=subprocess.DEVNULL)
            with open(out_file, "r", encoding="utf-8") as f:
                code = f.read()
        except Exception:
            pass

        return {out_file: code}

    def _generate_rust_code(self, ast: ProtocolAST) -> str:
        out = []
        out.append(self.get_header_warning("//").rstrip())
        out.append("")
        out.append("#![allow(dead_code)]")
        out.append("#![allow(non_camel_case_types)]")
        out.append("#![allow(clippy::all)]")
        out.append("")

        out.append(f'pub const TEMPLATE_VERSION: &str = "{ast.version}";')
        out.append("")

        # Zerocoded decompression
        out.append("""/// Decompresses zero-coded byte sequence into unencoded payload
pub fn decompress_zerocoded(src: &[u8]) -> Vec<u8> {
    let mut dest = Vec::with_capacity(src.len() * 2);
    let mut i = 0;
    while i < src.len() {
        let b = src[i];
        if b == 0 {
            if i + 1 < src.len() {
                let count = src[i + 1] as usize;
                if count == 0 {
                    dest.push(0);
                    i += 2;
                } else {
                    dest.extend(std::iter::repeat(0).take(count));
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
    dest
}
""")

        # Enums for Message ID
        out.append("#[derive(Debug, Clone, Copy, PartialEq, Eq)]")
        out.append("pub enum MessageFrequency {")
        out.append("    High,")
        out.append("    Medium,")
        out.append("    Low,")
        out.append("    Fixed,")
        out.append("}")
        out.append("")

        # Message Structs
        out.append("// Generated UDP Messages")
        for msg in ast.messages:
            struct_name = f"{msg.name}Packet"
            out.append("#[derive(Debug, Clone)]")
            out.append(f"pub struct {struct_name} {{")
            out.append("    pub message_id: u32,")
            out.append("    pub zerocoded: bool,")
            out.append("}")

            out.append(f"impl {struct_name} {{")
            out.append(f"    pub const MESSAGE_ID: u32 = {msg.message_number};")
            out.append(f"    pub const FREQUENCY: MessageFrequency = MessageFrequency::{msg.frequency};")
            out.append(f'    pub const ZEROCODED: bool = {"true" if msg.encoding == "Zerocoded" else "false"};')
            out.append("    pub fn new() -> Self {")
            out.append("        Self {")
            out.append(f"            message_id: {msg.message_number},")
            out.append(f'            zerocoded: {"true" if msg.encoding == "Zerocoded" else "false"},')
            out.append("        }")
            out.append("    }")
            out.append("}")
            out.append("")

        # LLSD capability structs
        out.append("// Generated LLSD Capability Schemas")
        for schema in ast.llsd_schemas:
            struct_name = f"{schema.title}Capabilities"
            out.append("#[derive(Debug, Clone, Default)]")
            out.append(f"pub struct {struct_name} {{")
            for p_name, prop in schema.properties.items():
                rs_type = "Option<String>"
                if prop.data_type == "integer":
                    rs_type = "Option<i32>"
                elif prop.data_type == "number":
                    rs_type = "Option<f64>"
                elif prop.data_type == "boolean":
                    rs_type = "Option<bool>"
                elif prop.data_type == "array":
                    rs_type = "Vec<String>"
                out.append(f"    pub {p_name}: {rs_type},")
            out.append("}")
            out.append("")

        return "\n".join(out)
