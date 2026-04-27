#!/usr/bin/env python3
"""Generate Kotlin parser scaffolding from template metadata.

This script turns entries from ``tools/parser-templates/message-parser-templates.json``
into concrete parser forwarding methods in
``src/main/java/com/linkpoint/protocol/messages/generated/GeneratedParserScaffolding.kt``.

The generated methods intentionally delegate to existing domain parser objects (for
example ``ObjectMessageParsers`` and ``TeleportMessageParsers``) so that scaffolding
is immediately usable and does not emit TODO placeholders.
"""

from __future__ import annotations

import argparse
import json
from pathlib import Path
from typing import Any

ROOT = Path(__file__).resolve().parents[1]
DEFAULT_TEMPLATE = ROOT / "tools/parser-templates/message-parser-templates.json"
DEFAULT_OUTPUT = ROOT / "src/main/java/com/linkpoint/protocol/messages/generated/GeneratedParserScaffolding.kt"

# Domain-to-parser mapping follows conventions in src/main/java/com/linkpoint/protocol/messages/*Parsers.kt
PARSER_OBJECT_BY_DOMAIN: dict[str, str] = {
    "object": "MessageParser",  # parseObjectUpdate currently lives in MessageParser
    "avatar": "AvatarMessageParsers",
    "teleport": "TeleportMessageParsers",
    "inventory": "AdditionalMessageParsers",
    "chat": "ChatMessageParsers",
}


def parse_args() -> argparse.Namespace:
    parser = argparse.ArgumentParser(
        description=(
            "Generate GeneratedParserScaffolding.kt by wiring template messages to "
            "existing parser objects (no TODO stubs)."
        )
    )
    parser.add_argument(
        "--template",
        type=Path,
        default=DEFAULT_TEMPLATE,
        help="Path to message-parser-templates.json (default: %(default)s)",
    )
    parser.add_argument(
        "--output",
        type=Path,
        default=DEFAULT_OUTPUT,
        help="Path to write GeneratedParserScaffolding.kt (default: %(default)s)",
    )
    return parser.parse_args()


def load_template(path: Path) -> list[dict[str, Any]]:
    data = json.loads(path.read_text())
    messages = data.get("messages")
    if not isinstance(messages, list):
        raise ValueError(f"Expected top-level 'messages' array in {path}")
    return messages


def parser_object_for(message: dict[str, Any]) -> str:
    explicit_parser = message.get("parserObject")
    if isinstance(explicit_parser, str) and explicit_parser:
        return explicit_parser

    domain = message.get("domain")
    if not isinstance(domain, str) or domain not in PARSER_OBJECT_BY_DOMAIN:
        known = ", ".join(sorted(PARSER_OBJECT_BY_DOMAIN))
        raise ValueError(
            f"Message '{message.get('name')}' has unknown domain '{domain}'. "
            f"Add parserObject in template or extend domain map ({known})."
        )
    return PARSER_OBJECT_BY_DOMAIN[domain]


def generate(messages: list[dict[str, Any]]) -> str:
    lines = [
        "package com.linkpoint.protocol.messages.generated",
        "",
        "import com.linkpoint.protocol.messages.*",
        "",
        "/** Auto-generated parser scaffolding from message templates. */",
        "object GeneratedParserScaffolding {",
    ]

    for message in messages:
        name = message["name"]
        msg_id = message["id"]
        domain = message["domain"]
        return_type = message["returnType"]
        parser_object = parser_object_for(message)

        lines.append(f"    const val {name.upper()}_ID: Int = {msg_id}")
        lines.append(f"    // domain={domain} returnType={return_type}")
        lines.append(
            f"    fun parse{name}(payload: ByteArray): {return_type} = "
            f"{parser_object}.parse{name}(payload)"
        )
        lines.append("")

    lines.append("}")
    return "\n".join(lines)


def main() -> None:
    args = parse_args()
    template_path = args.template if args.template.is_absolute() else ROOT / args.template
    output_path = args.output if args.output.is_absolute() else ROOT / args.output

    messages = load_template(template_path)
    output_path.parent.mkdir(parents=True, exist_ok=True)
    output_path.write_text(generate(messages) + "\n")
    print(f"Wrote {output_path}")


if __name__ == "__main__":
    main()
