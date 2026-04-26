#!/usr/bin/env python3
"""Offline classification audit of message_template.msg vs MessageTemplateCatalog.kt.

This is a JDK-free counterpart to the Gradle test
``com.linkpoint.protocol.messages.MessageTemplateProtocolConformanceTest``.
It is designed for fast local runs and pre-commit gating: it walks the
committed message template, walks the Kotlin catalog source, and asserts
that every template message has exactly one classification (supported,
declared-only, or deprecated).

Why duplicate the JVM test?
* The CI pipeline runs ``run_conformance.sh`` long before the Android
  SDK / NDK have finished provisioning, so a no-JDK gate produces a
  fast actionable failure on classification regressions.
* It catches drift (e.g. someone adding a message to the template
  without classifying it) without needing the Android toolchain at all.
"""

from __future__ import annotations

import argparse
import re
import sys
from pathlib import Path
from typing import Iterable

SCRIPT_DIR = Path(__file__).resolve().parent
PROJECT_ROOT = SCRIPT_DIR.parent.parent
TEMPLATE_PATH = PROJECT_ROOT / "src/test/resources/protocol/message_template.msg"
CATALOG_PATH = (
    PROJECT_ROOT
    / "src/main/java/com/linkpoint/protocol/messages/MessageTemplateCatalog.kt"
)
APP_PATH = PROJECT_ROOT / "src/main/java/com/linkpoint/LinkpointApp.kt"

# Same regex as MessageTemplateProtocolConformanceTest.kt
TEMPLATE_RE = re.compile(
    r"^\s*([A-Za-z][A-Za-z0-9_]*)\s+(Low|Medium|High|Fixed)\s+(\d+)\s+([^\n]+)$",
    re.MULTILINE,
)

KOTLIN_SET_RE = re.compile(
    r"\b(?:private\s+|public\s+)?val\s+(\w+)\s*:\s*Set<String>\s*=\s*setOf\(\s*(.*?)\)",
    re.DOTALL,
)
KOTLIN_MAP_RE = re.compile(
    r"\b(?:private\s+|public\s+)?val\s+(\w+)\s*:\s*Map<String,\s*String>\s*=\s*mapOf\(\s*(.*?)\s*\)",
    re.DOTALL,
)
QUOTED_NAME_RE = re.compile(r'"([A-Za-z][A-Za-z0-9_]*)"')
MAP_ENTRY_RE = re.compile(
    r'"([A-Za-z][A-Za-z0-9_]*)"\s+to\s+"([^"]*)"',
)


def parse_template_messages(text: str) -> list[tuple[str, bool]]:
    """Return (name, is_deprecated) per first-seen template message."""
    seen: set[str] = set()
    out: list[tuple[str, bool]] = []
    for m in TEMPLATE_RE.finditer(text):
        name = m.group(1)
        trailer = m.group(4)
        if name in seen:
            continue
        seen.add(name)
        out.append((name, "Deprecated" in trailer))
    return out


def parse_kotlin_set(name: str, source: str) -> set[str]:
    for match in KOTLIN_SET_RE.finditer(source):
        if match.group(1) == name:
            return {n for n in QUOTED_NAME_RE.findall(match.group(2))}
    return set()


def parse_kotlin_map(name: str, source: str) -> dict[str, str]:
    for match in KOTLIN_MAP_RE.finditer(source):
        if match.group(1) == name:
            return {k: v for k, v in MAP_ENTRY_RE.findall(match.group(2))}
    return {}


def write_report(path: Path, lines: Iterable[str]) -> None:
    path.parent.mkdir(parents=True, exist_ok=True)
    path.write_text("\n".join(lines) + "\n")


def main() -> int:
    ap = argparse.ArgumentParser(description=__doc__)
    ap.add_argument(
        "--report",
        type=Path,
        default=PROJECT_ROOT
        / "build/reports/protocol/message-template-audit.txt",
    )
    args = ap.parse_args()

    for required in (TEMPLATE_PATH, CATALOG_PATH, APP_PATH):
        if not required.exists():
            print(f"missing required file: {required}", file=sys.stderr)
            return 2

    template_messages = parse_template_messages(TEMPLATE_PATH.read_text())
    catalog_src = CATALOG_PATH.read_text()
    app_src = APP_PATH.read_text()

    # Match the JVM merge in MessageIds.kt:
    #   supportedTemplateMessages   = declaredParserOrWriterMessages
    #                               + LinkpointApp.parserSupportedMessageNamesForConformance
    #   declaredOnlyTemplateMessages = declaredOnlyBacklogMessages.keys
    #                               - implementedDeclaredMessageNames
    #   deprecatedTemplateMessages  = deprecatedMessagesWithRationale.keys
    declared_pw = parse_kotlin_set("declaredParserOrWriterMessages", catalog_src)
    parser_supported = parse_kotlin_set("parserSupportedMessageNamesForConformance", app_src)
    backlog = parse_kotlin_map("declaredOnlyBacklogMessages", catalog_src)
    implemented_declared = parse_kotlin_set("implementedDeclaredMessageNames", catalog_src)
    deprecated_map = parse_kotlin_map("deprecatedMessagesWithRationale", catalog_src)

    supported: set[str] = declared_pw | parser_supported
    declared_only_keys = backlog.keys() - implemented_declared
    declared_only: dict[str, str] = {k: backlog[k] for k in declared_only_keys}
    deprecated: dict[str, str] = deprecated_map

    template_names = [n for n, _ in template_messages]
    deprecated_in_template = [n for n, dep in template_messages if dep]

    duplicate = sorted(
        n for n in template_names
        if (int(n in supported) + int(n in declared_only) + int(n in deprecated)) > 1
    )
    missing = sorted(
        n for n in template_names
        if n not in supported and n not in declared_only and n not in deprecated
    )
    blank_declared = sorted(k for k, v in declared_only.items() if not v.strip())
    blank_deprecated = sorted(k for k, v in deprecated.items() if not v.strip())
    template_dep_without_table = sorted(
        n for n in deprecated_in_template if n not in deprecated
    )

    def section(title: str, items: list[str]) -> list[str]:
        body = [f"- {n}" for n in items] if items else ["none"]
        return [title, *body, ""]

    lines = [
        "Linkpoint Message Template Classification Audit (offline)",
        f"template_path={TEMPLATE_PATH.relative_to(PROJECT_ROOT)}",
        f"catalog_path={CATALOG_PATH.relative_to(PROJECT_ROOT)}",
        f"template_count={len(template_names)}",
        f"supported_count={sum(1 for n in template_names if n in supported)}",
        f"declared_only_count={sum(1 for n in template_names if n in declared_only)}",
        f"deprecated_count={sum(1 for n in template_names if n in deprecated)}",
        f"duplicate_classification_count={len(duplicate)}",
        f"missing_classification_count={len(missing)}",
        f"blank_declared_rationales={len(blank_declared)}",
        f"blank_deprecated_rationales={len(blank_deprecated)}",
        f"template_deprecated_without_table={len(template_dep_without_table)}",
        "",
        *section("[DUPLICATE_CLASSIFICATION]", duplicate),
        *section("[MISSING_CLASSIFICATION]", missing),
        *section("[BLANK_DECLARED_RATIONALES]", blank_declared),
        *section("[BLANK_DEPRECATED_RATIONALES]", blank_deprecated),
        *section("[TEMPLATE_DEPRECATED_WITHOUT_TABLE]", template_dep_without_table),
    ]
    write_report(args.report, lines)

    failed = (
        bool(duplicate)
        or bool(missing)
        or bool(blank_declared)
        or bool(blank_deprecated)
        or bool(template_dep_without_table)
    )
    if failed:
        print(f"classification audit failed; see {args.report}", file=sys.stderr)
        return 1
    print(f"classification audit OK ({len(template_names)} messages); report at {args.report}")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
