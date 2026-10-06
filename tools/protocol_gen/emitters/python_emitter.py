import os
from typing import Dict
from tools.protocol_gen.proto_ast.models import ProtocolAST
from tools.protocol_gen.emitters.base import BaseEmitter

class PythonEmitter(BaseEmitter):
    def __init__(self):
        super().__init__("Python", ".py")

    def emit(self, ast: ProtocolAST, out_dir: str) -> Dict[str, str]:
        os.makedirs(out_dir, exist_ok=True)

        code = self._generate_python_code(ast)
        out_file = os.path.join(out_dir, "generated.py")

        with open(out_file, "w", encoding="utf-8", newline="\n") as f:
            f.write(code)

        return {out_file: code}

    def _generate_python_code(self, ast: ProtocolAST) -> str:
        out = []
        out.append(self.get_header_warning("#"))
        out.append("import struct")
        out.append("from dataclasses import dataclass, field")
        out.append("from typing import Optional, List, Dict, Any\n")

        out.append(f'TEMPLATE_VERSION = "{ast.version}"\n')

        out.append("REGISTERED_MESSAGES: Dict[str, int] = {")
        for msg in ast.messages:
            out.append(f'    "{msg.name}": {msg.message_number},')
        out.append("}\n")

        out.append("""def decompress_zerocoded(src: bytes) -> bytes:
    dest = bytearray()
    i = 0
    src_len = len(src)
    while i < src_len:
        b = src[i]
        if b == 0:
            if i + 1 < src_len:
                count = src[i + 1]
                if count == 0:
                    dest.append(0)
                    i += 2
                else:
                    dest.extend(b'\\x00' * count)
                    i += 2
            else:
                dest.append(0)
                i += 1
        else:
            dest.append(b)
            i += 1
    return bytes(dest)
""")

        out.append("# Generated UDP Messages")
        for msg in ast.messages:
            out.append("@dataclass")
            out.append(f"class {msg.name}Packet:")
            out.append(f'    message_name: str = "{msg.name}"')
            out.append(f"    message_number: int = {msg.message_number}")
            out.append(f'    frequency: str = "{msg.frequency}"')
            out.append(f'    is_zerocoded: bool = {"True" if msg.encoding == "Zerocoded" else "False"}\n')
            out.append("    def serialize(self) -> bytes:")
            out.append(f"        return struct.pack('<I', {msg.message_number})\n")

        out.append("# Generated LLSD Capability Schemas")
        for schema in ast.llsd_schemas:
            out.append("@dataclass")
            out.append(f"class {schema.title}Capabilities:")
            for p_name, prop in schema.properties.items():
                py_type = "Optional[str]"
                default_val = "None"
                if prop.data_type == "integer":
                    py_type = "Optional[int]"
                elif prop.data_type == "number":
                    py_type = "Optional[float]"
                elif prop.data_type == "boolean":
                    py_type = "Optional[bool]"
                elif prop.data_type == "array":
                    py_type = "List[str]"
                    default_val = "field(default_factory=list)"
                out.append(f"    {p_name}: {py_type} = {default_val}")
            out.append("")

        return "\n".join(out)
