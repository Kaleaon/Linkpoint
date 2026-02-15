#!/usr/bin/env python3
import json
from pathlib import Path

root = Path(__file__).resolve().parents[1]
tpl = json.loads((root / "tools/parser-templates/message-parser-templates.json").read_text())
out = root / "src/main/java/com/linkpoint/protocol/messages/generated/GeneratedParserScaffolding.kt"

lines = [
    "package com.linkpoint.protocol.messages.generated",
    "",
    "import com.linkpoint.protocol.messages.*",
    "",
    "/** Auto-generated parser scaffolding from message templates. */",
    "object GeneratedParserScaffolding {",
]
for msg in tpl["messages"]:
    lines.append(f"    const val {msg['name'].upper()}_ID: Int = {msg['id']}")
    lines.append(f"    // domain={msg['domain']} returnType={msg['returnType']}")
    lines.append(f"    fun parse{msg['name']}(payload: ByteArray): {msg['returnType']} = TODO(\"Implement {msg['name']} parser\")")
    lines.append("")
lines.append("}")
out.write_text("\n".join(lines))
print(f"Wrote {out}")
