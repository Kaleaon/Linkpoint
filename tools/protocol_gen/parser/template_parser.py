import re
from typing import List, Optional
from tools.protocol_gen.proto_ast.models import MessageSpec, BlockSpec, FieldSpec, ProtocolAST

class TemplateParser:
    """
    Parser for Linden Lab message_template.msg grammar into ProtocolAST MessageSpecs.
    """

    def parse(self, content: str, ast: Optional[ProtocolAST] = None) -> ProtocolAST:
        if ast is None:
            ast = ProtocolAST()

        # Remove single line comments
        lines = []
        for line in content.splitlines():
            line_str = line.split("//")[0].strip()
            if line_str:
                lines.append(line_str)

        full_text = " ".join(lines)

        # Parse version
        version_match = re.search(r"version\s+([\d\.]+)", full_text, re.IGNORECASE)
        if version_match:
            ast.version = version_match.group(1)

        # Tokenize by nested block braces
        top_blocks = self._extract_braced_blocks(full_text)
        for block_text in top_blocks:
            msg = self._parse_message(block_text)
            if msg:
                ast.messages.append(msg)

        return ast

    def _extract_braced_blocks(self, text: str) -> List[str]:
        blocks = []
        stack = 0
        start = -1
        for i, char in enumerate(text):
            if char == '{':
                if stack == 0:
                    start = i + 1
                stack += 1
            elif char == '}':
                stack -= 1
                if stack == 0 and start != -1:
                    blocks.append(text[start:i].strip())
                    start = -1
        return blocks

    def _parse_message(self, text: str) -> Optional[MessageSpec]:
        # Split message header from inner blocks
        first_brace = text.find('{')
        if first_brace == -1:
            header_text = text
            inner_text = ""
        else:
            header_text = text[:first_brace].strip()
            inner_text = text[first_brace:]

        tokens = header_text.split()
        if not tokens or len(tokens) < 5:
            return None

        msg_name = tokens[0]
        frequency = tokens[1]
        msg_num_str = tokens[2]
        
        # Parse message number (decimal or hex)
        if msg_num_str.lower().startswith("0x"):
            msg_num = int(msg_num_str, 16)
        else:
            msg_num = int(msg_num_str)

        trust = tokens[3]
        encoding = tokens[4]
        flags = tokens[5:] if len(tokens) > 5 else []

        message_spec = MessageSpec(
            name=msg_name,
            frequency=frequency,
            message_number=msg_num,
            trust_level=trust,
            encoding=encoding,
            flags=flags,
            blocks=[]
        )

        if inner_text:
            block_strings = self._extract_braced_blocks(inner_text)
            for b_str in block_strings:
                blk = self._parse_block(b_str)
                if blk:
                    message_spec.blocks.append(blk)

        return message_spec

    def _parse_block(self, text: str) -> Optional[BlockSpec]:
        first_brace = text.find('{')
        if first_brace == -1:
            header_text = text
            inner_text = ""
        else:
            header_text = text[:first_brace].strip()
            inner_text = text[first_brace:]

        tokens = header_text.split()
        if not tokens or len(tokens) < 2:
            return None

        block_name = tokens[0]
        block_type = tokens[1]
        count = int(tokens[2]) if len(tokens) > 2 and tokens[2].isdigit() else 1

        block_spec = BlockSpec(
            name=block_name,
            block_type=block_type,
            count=count,
            fields=[]
        )

        if inner_text:
            field_strings = self._extract_braced_blocks(inner_text)
            for f_str in field_strings:
                fld = self._parse_field(f_str)
                if fld:
                    block_spec.fields.append(fld)

        return block_spec

    def _parse_field(self, text: str) -> Optional[FieldSpec]:
        tokens = text.split()
        if not tokens or len(tokens) < 2:
            return None

        field_name = tokens[0]
        field_type = tokens[1]
        count = int(tokens[2]) if len(tokens) > 2 and tokens[2].isdigit() else 1

        return FieldSpec(
            name=field_name,
            type_name=field_type,
            count=count
        )
