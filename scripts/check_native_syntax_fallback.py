#!/usr/bin/env python3
"""
Native fallback JSON and YAML syntax validator.
Used when Python pre-commit CLI is not installed.
"""

import json
import os
import re
import subprocess
import sys

try:
    import yaml
    HAS_PYYAML = True
except ImportError:
    HAS_PYYAML = False

EXCLUDE_DIRS = {
    "android-sdk",
    "lumiya_extracted",
    "lumiya_decompiled_source",
    "secondlife_decompiled",
    "disassembled-apps",
    "Gauss",
    "LLSD-KOTLIN",
    "vendor",
    "file_bundle",
    "apk_analysis",
    "kotlin-translations",
    "cmdline-temp",
    "platforms",
    "builds",
    "ktheme-pr",
    "node_modules",
    ".git",
    "target",
}

EXCLUDE_PREFIXES = (
    "Linkpoint/src/test/resources",
    "crates/quick-xml",
)


def is_excluded(rel_path):
    norm_path = rel_path.replace("\\", "/")
    parts = norm_path.split("/")
    if parts[0] in EXCLUDE_DIRS:
        return True
    for prefix in EXCLUDE_PREFIXES:
        if norm_path == prefix or norm_path.startswith(prefix + "/"):
            return True
    return False


def validate_json(path):
    try:
        with open(path, "r", encoding="utf-8") as f:
            json.load(f)
        return True, None
    except Exception as e:
        return False, str(e)


def fallback_yaml_check(content, path):
    # Standard JSON is valid YAML
    try:
        json.loads(content)
        return True, None
    except Exception:
        pass

    lines = content.splitlines()
    stack = []
    block_scalar_indent = None

    for line_num, line in enumerate(lines, 1):
        stripped_spaces = line.lstrip(" ")
        if stripped_spaces.startswith("\t"):
            return False, f"Line {line_num}: Tab used for YAML indentation"

        stripped = line.strip()
        if not stripped:
            continue

        indent = len(line) - len(stripped_spaces)

        # Skip content inside YAML block scalar literals (| or >)
        if block_scalar_indent is not None:
            if indent > block_scalar_indent:
                continue
            else:
                block_scalar_indent = None

        if stripped.startswith("#"):
            continue

        line_no_comment = line.split(" #")[0] if " #" in line else line
        trimmed = line_no_comment.rstrip()
        if trimmed.endswith(("|", ">", "|-", ">-", "|+", ">+")):
            block_scalar_indent = indent
            continue

        # Check bracket/brace balance
        in_quote = None
        escaped = False
        for i, char in enumerate(line_no_comment):
            if escaped:
                escaped = False
                continue
            if char == "\\":
                escaped = True
                continue
            if in_quote:
                if char == in_quote:
                    in_quote = None
            else:
                if char in ('"', "'"):
                    prev = line_no_comment[i - 1] if i > 0 else " "
                    if prev in (" ", "\t", ":", "-", "[", "{", "(", ","):
                        in_quote = char
                elif char in ("(", "[", "{"):
                    stack.append((char, line_num))
                elif char in (")", "]", "}"):
                    if not stack:
                        return False, f"Line {line_num}: Unmatched closing '{char}'"
                    top, _ = stack.pop()
                    expected = {"(": ")", "[": "]", "{": "}"}[top]
                    if char != expected:
                        return False, f"Line {line_num}: Mismatched closing '{char}', expected '{expected}'"

    if stack:
        char, line_num = stack[-1]
        return False, f"Line {line_num}: Unclosed '{char}'"

    return True, None


def validate_yaml(path):
    try:
        with open(path, "r", encoding="utf-8") as f:
            content = f.read()

        if HAS_PYYAML:
            try:
                yaml.safe_load(content)
                return True, None
            except Exception as e:
                return False, str(e)
        else:
            return fallback_yaml_check(content, path)
    except Exception as e:
        return False, str(e)


def get_staged_files():
    try:
        output = subprocess.check_output(
            ["git", "diff", "--cached", "--name-only", "--diff-filter=ACM"],
            text=True,
            stderr=subprocess.DEVNULL,
        )
        files = [line.strip() for line in output.splitlines() if line.strip()]
        return [f for f in files if f.endswith((".json", ".yaml", ".yml"))]
    except Exception:
        return []


def get_all_repo_files(root_dir):
    matched = []
    for root, dirs, files in os.walk(root_dir):
        dirs[:] = [d for d in dirs if d not in EXCLUDE_DIRS]
        rel_root = os.path.relpath(root, root_dir)
        if rel_root != "." and is_excluded(rel_root):
            continue

        for f in files:
            if f.endswith((".json", ".yaml", ".yml")):
                full_path = os.path.join(root, f)
                rel_path = os.path.relpath(full_path, root_dir)
                if not is_excluded(rel_path):
                    matched.append(rel_path)
    return matched


def main():
    root_dir = os.path.abspath(
        subprocess.check_output(
            ["git", "rev-parse", "--show-toplevel"], text=True
        ).strip()
        if os.path.exists(".git")
        else os.getcwd()
    )
    os.chdir(root_dir)

    args = sys.argv[1:]
    files_to_check = []

    if "--all-files" in args:
        files_to_check = get_all_repo_files(root_dir)
    elif "--staged" in args:
        files_to_check = get_staged_files()
    elif args:
        files_to_check = [a for a in args if not a.startswith("--")]
    else:
        staged = get_staged_files()
        if staged:
            files_to_check = staged
        else:
            files_to_check = get_all_repo_files(root_dir)

    files_to_check = [f for f in files_to_check if not is_excluded(f)]

    if not files_to_check:
        print("[native-syntax-fallback] No JSON or YAML files to check.")
        sys.exit(0)

    errors = 0
    checked = 0

    for rel_path in files_to_check:
        if not os.path.isfile(rel_path):
            continue

        checked += 1
        if rel_path.endswith(".json"):
            ok, err_msg = validate_json(rel_path)
        else:
            ok, err_msg = validate_yaml(rel_path)

        if not ok:
            print(f"[native-syntax-fallback] ERROR in {rel_path}:", file=sys.stderr)
            print(f"  {err_msg}", file=sys.stderr)
            errors += 1

    if errors > 0:
        print(
            f"[native-syntax-fallback] Failed syntax check for {errors} file(s).",
            file=sys.stderr,
        )
        sys.exit(1)

    print(
        f"[native-syntax-fallback] Successfully validated syntax in {checked} file(s)."
    )
    sys.exit(0)


if __name__ == "__main__":
    main()
