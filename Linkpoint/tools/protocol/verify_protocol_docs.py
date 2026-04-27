#!/usr/bin/env python3
"""Validate that AGENT_PARSABLE_MODERNIZATION_TASKLIST.md remains parseable.

The tasklist exposes a Parse Contract (task_id, title, priority, status,
labels, depends_on, owner, repo_paths, external_refs, deliverables,
acceptance_criteria). Autonomous agents depend on that contract being
machine-parseable, so this script enforces:

* every ``task_id`` is unique
* every task has the full required field set
* every status is one of {todo, in_progress, blocked, done}
* every priority is one of {P0, P1, P2}
* every depends_on entry references a known task_id
* every label is in the documented Label Taxonomy

The script writes a deterministic report under
``build/reports/protocol/protocol-docs-audit.txt`` and exits non-zero
on any contract violation.
"""

from __future__ import annotations

import argparse
import re
import sys
from pathlib import Path

SCRIPT_DIR = Path(__file__).resolve().parent
PROJECT_ROOT = SCRIPT_DIR.parent.parent
# tasklist lives at repo root (one level above the Linkpoint project root)
DEFAULT_TASKLIST = (
    PROJECT_ROOT.parent / "docs/AGENT_PARSABLE_MODERNIZATION_TASKLIST.md"
)

REQUIRED_FIELDS = (
    "title",
    "priority",
    "status",
    "labels",
    "depends_on",
    "owner",
    "repo_paths",
    "external_refs",
    "deliverables",
    "acceptance_criteria",
)
ALLOWED_STATUS = {"todo", "in_progress", "blocked", "done"}
ALLOWED_PRIORITY = {"P0", "P1", "P2"}
ALLOWED_LABELS = {
    "PROTO-TEMPLATE",
    "PROTO-UDP",
    "PROTO-CAPS",
    "PROTO-LLSD-XML",
    "PROTO-LLSD-BINARY",
    "PROTO-LLSD-NOTATION",
    "PROTO-CONFORMANCE",
    "RUNTIME-CONNECTIVITY",
    "RUNTIME-WORLD",
    "RUNTIME-RENDER",
    "ANDROID",
    "TESTING",
    "OBSERVABILITY",
    "DOCUMENTATION",
}


def parse_tasks(text: str) -> list[dict]:
    """Naive but deterministic tasklist parser.

    Tasks are demarcated by ``### task_id:`` headings; subsequent lines
    of the form ``- key: value`` populate scalar fields, and ``- key:``
    on its own line followed by ``  - item`` lines populate list fields.
    """
    tasks: list[dict] = []
    current: dict | None = None
    current_list_key: str | None = None
    inside_block = False

    for raw in text.splitlines():
        line = raw.rstrip()
        m = re.match(r"^### task_id:\s*(\S+)\s*$", line)
        if m:
            if current is not None:
                tasks.append(current)
            current = {"task_id": m.group(1)}
            current_list_key = None
            inside_block = True
            continue

        if not inside_block or current is None:
            continue

        if line.startswith("---") or line.startswith("## "):
            tasks.append(current)
            current = None
            current_list_key = None
            inside_block = False
            continue

        m = re.match(r"^-\s*([a-z_]+):\s*(.*)$", line)
        if m:
            key, value = m.group(1), m.group(2).strip()
            if value:
                current[key] = value
                current_list_key = None
            else:
                current[key] = []
                current_list_key = key
            continue

        m = re.match(r"^\s+-\s*(.*)$", line)
        if m and current_list_key is not None:
            current.setdefault(current_list_key, []).append(m.group(1).strip())

    if current is not None:
        tasks.append(current)
    return tasks


def lint(tasks: list[dict]) -> list[str]:
    errors: list[str] = []
    seen_ids: set[str] = set()
    known_ids: set[str] = {t.get("task_id", "") for t in tasks}

    for t in tasks:
        tid = t.get("task_id", "<unknown>")
        if tid in seen_ids:
            errors.append(f"duplicate task_id: {tid}")
        seen_ids.add(tid)

        for f in REQUIRED_FIELDS:
            if f not in t:
                errors.append(f"{tid}: missing required field '{f}'")

        prio = t.get("priority")
        if prio is not None and prio not in ALLOWED_PRIORITY:
            errors.append(f"{tid}: invalid priority '{prio}'")

        status = t.get("status")
        if status is not None and status not in ALLOWED_STATUS:
            errors.append(f"{tid}: invalid status '{status}'")

        labels = t.get("labels")
        if isinstance(labels, str):
            # support inline form: labels: [A, B, C]
            inner = labels.strip().lstrip("[").rstrip("]")
            label_iter = [l.strip() for l in inner.split(",") if l.strip()]
        elif isinstance(labels, list):
            label_iter = labels
        else:
            label_iter = []
        for lbl in label_iter:
            if lbl not in ALLOWED_LABELS:
                errors.append(f"{tid}: unknown label '{lbl}'")

        deps = t.get("depends_on")
        if isinstance(deps, str):
            inner = deps.strip().lstrip("[").rstrip("]")
            dep_iter = [d.strip() for d in inner.split(",") if d.strip()]
        elif isinstance(deps, list):
            dep_iter = deps
        else:
            dep_iter = []
        for d in dep_iter:
            if d not in known_ids:
                errors.append(f"{tid}: depends_on '{d}' is not a declared task_id")

    return errors


def main() -> int:
    ap = argparse.ArgumentParser(description=__doc__)
    ap.add_argument("--tasklist", type=Path, default=DEFAULT_TASKLIST)
    ap.add_argument(
        "--report",
        type=Path,
        default=PROJECT_ROOT / "build/reports/protocol/protocol-docs-audit.txt",
    )
    args = ap.parse_args()

    if not args.tasklist.exists():
        print(f"tasklist not found: {args.tasklist}", file=sys.stderr)
        return 2

    tasks = parse_tasks(args.tasklist.read_text())
    errors = lint(tasks)

    args.report.parent.mkdir(parents=True, exist_ok=True)
    out = ["Protocol docs parse-contract audit", f"tasklist={args.tasklist}"]
    out.append(f"task_count={len(tasks)}")
    out.append(f"error_count={len(errors)}")
    out.append("")
    out.append("[TASK_IDS]")
    for t in tasks:
        out.append(f"- {t.get('task_id')} :: {t.get('priority', '?')} :: {t.get('status', '?')}")
    out.append("")
    out.append("[ERRORS]")
    if errors:
        out.extend(f"- {e}" for e in errors)
    else:
        out.append("none")
    args.report.write_text("\n".join(out) + "\n")

    if errors:
        print(f"protocol docs audit failed ({len(errors)} errors); see {args.report}", file=sys.stderr)
        return 1
    print(f"protocol docs audit OK ({len(tasks)} tasks); report at {args.report}")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
