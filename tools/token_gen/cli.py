#!/usr/bin/env python3
import sys
import os
import argparse
import json
import re
from pathlib import Path

# Add project root to sys.path
PROJECT_ROOT = Path(__file__).resolve().parent.parent.parent
sys.path.insert(0, str(PROJECT_ROOT))

try:
    import jinja2
except ImportError:
    print("Error: Jinja2 package is required. Run 'pip install jinja2'")
    sys.exit(1)

DEFAULT_TOKENS_PATH = "packages/design-system/src/tokens/tokens.json"

DEFAULT_TARGET_FILES = {
    "typescript": "packages/design-system/src/tokens/tokens.ts",
    "kotlin": "Linkpoint/ui-components/src/main/java/com/linkpoint/ui/components/linkpoint2/tokens/GeneratedTokens.kt",
    "dart": "packages/linkpoint_dart/lib/src/generated_tokens.dart",
    "rust": "crates/linkpoint-core/src/generated_tokens.rs",
}

TEMPLATE_FILES = {
    "typescript": "tokens.ts.j2",
    "kotlin": "GeneratedTokens.kt.j2",
    "dart": "generated_tokens.dart.j2",
    "rust": "generated_tokens.rs.j2",
}

DEFAULT_OUTPUT_FILENAMES = {
    "typescript": "tokens.ts",
    "kotlin": "GeneratedTokens.kt",
    "dart": "generated_tokens.dart",
    "rust": "generated_tokens.rs",
}


def to_pascal_case(s: str) -> str:
    if not s:
        return ""
    # Replace underscores/hyphens or convert camelCase
    s = re.sub(r"[-_\s]+", " ", s)
    words = s.split()
    if len(words) > 1:
        return "".join(w.capitalize() for w in words)
    return s[0].upper() + s[1:]


def to_camel_case(s: str) -> str:
    pascal = to_pascal_case(s)
    if not pascal:
        return ""
    return pascal[0].lower() + pascal[1:]


def to_snake_case(s: str) -> str:
    if not s:
        return ""
    s = re.sub(r"[-_\s]+", "_", s)
    s = re.sub(r"(?<!^)(?=[A-Z])", "_", s)
    return s.lower()


def parse_hex_color(hex_str: str):
    if not hex_str or not isinstance(hex_str, str):
        return (0, 0, 0, 255)
    clean = hex_str.replace("#", "").strip()
    if len(clean) == 6:
        r = int(clean[0:2], 16)
        g = int(clean[2:4], 16)
        b = int(clean[4:6], 16)
        a = 255
        return (r, g, b, a)
    elif len(clean) == 8:
        r = int(clean[0:2], 16)
        g = int(clean[2:4], 16)
        b = int(clean[4:6], 16)
        a = int(clean[6:8], 16)
        return (r, g, b, a)
    return (0, 0, 0, 255)


def to_kotlin_color(hex_str: str) -> str:
    r, g, b, a = parse_hex_color(hex_str)
    return f"Color(0x{a:02X}{r:02X}{g:02X}{b:02X})"


def to_dart_color(hex_str: str) -> str:
    r, g, b, a = parse_hex_color(hex_str)
    return f"Color(0x{a:02X}{r:02X}{g:02X}{b:02X})"


def fmt_f32(val: float) -> str:
    rounded = round(val, 6)
    formatted = f"{rounded:g}"
    if "." not in formatted and "e" not in formatted:
        formatted += ".0"
    return formatted


def to_rust_color(hex_str: str) -> str:
    r, g, b, a = parse_hex_color(hex_str)
    rf, gf, bf, af = r / 255.0, g / 255.0, b / 255.0, a / 255.0
    return f"RgbaColor::new({fmt_f32(rf)}, {fmt_f32(gf)}, {fmt_f32(bf)}, {fmt_f32(af)})"


def get_jinja_env() -> jinja2.Environment:
    template_dir = Path(__file__).resolve().parent / "templates"
    env = jinja2.Environment(
        loader=jinja2.FileSystemLoader(str(template_dir)),
        trim_blocks=True,
        lstrip_blocks=True,
        keep_trailing_newline=True,
    )
    env.filters["to_pascal_case"] = to_pascal_case
    env.filters["to_camel_case"] = to_camel_case
    env.filters["to_snake_case"] = to_snake_case
    env.filters["to_kotlin_color"] = to_kotlin_color
    env.filters["to_dart_color"] = to_dart_color
    env.filters["to_rust_color"] = to_rust_color
    return env


def load_tokens(tokens_path: str) -> dict:
    abs_path = Path(tokens_path)
    if not abs_path.is_absolute():
        abs_path = PROJECT_ROOT / tokens_path
    if not abs_path.exists():
        print(f"Error: Tokens file not found at {abs_path}")
        sys.exit(1)
    with open(abs_path, "r", encoding="utf-8") as f:
        return json.load(f)


def resolve_targets(target_arg: str) -> list[str]:
    raw_targets = [t.strip().lower() for t in target_arg.split(",")]
    if "all" in raw_targets:
        return list(DEFAULT_TARGET_FILES.keys())
    valid_targets = set(DEFAULT_TARGET_FILES.keys())
    for t in raw_targets:
        if t not in valid_targets:
            print(f"Error: Unknown target '{t}'. Valid targets: {list(valid_targets) + ['all']}")
            sys.exit(1)
    return raw_targets


def get_out_path(target: str, output_dir: str | None) -> Path:
    if output_dir:
        out_d = Path(output_dir)
        if not out_d.is_absolute():
            out_d = PROJECT_ROOT / output_dir
        return out_d / DEFAULT_OUTPUT_FILENAMES[target]
    else:
        rel_path = DEFAULT_TARGET_FILES[target]
        return PROJECT_ROOT / rel_path


def render_target(target: str, tokens_data: dict, env: jinja2.Environment) -> str:
    template_name = TEMPLATE_FILES[target]
    template = env.get_template(template_name)
    return template.render(tokens=tokens_data)


def cmd_generate(args):
    tokens_data = load_tokens(args.tokens)
    targets = resolve_targets(args.target)
    env = get_jinja_env()

    generated_count = 0
    for target in targets:
        content = render_target(target, tokens_data, env)
        out_path = get_out_path(target, args.output_dir)
        out_path.parent.mkdir(parents=True, exist_ok=True)
        with open(out_path, "w", encoding="utf-8", newline="\n") as f:
            f.write(content)
        print(f"[{target.upper()}] Generated {out_path.relative_to(PROJECT_ROOT) if out_path.is_relative_to(PROJECT_ROOT) else out_path}")
        generated_count += 1

    print(f"\nSuccessfully generated tokens for {generated_count} target(s).")


def cmd_check(args):
    tokens_data = load_tokens(args.tokens)
    targets = resolve_targets(args.target)
    env = get_jinja_env()

    mismatches = []
    for target in targets:
        rendered = render_target(target, tokens_data, env)
        out_path = get_out_path(target, getattr(args, "output_dir", None))
        if not out_path.exists():
            mismatches.append(f"[{target.upper()}] Missing output file: {out_path}")
            continue
        with open(out_path, "r", encoding="utf-8", newline="") as f:
            on_disk = f.read()
        if on_disk != rendered:
            mismatches.append(f"[{target.upper()}] File out of sync: {out_path}")

    if mismatches:
        print("Check failed! Generated token files are out of sync with tokens.json:")
        for m in mismatches:
            print(f"  - {m}")
        sys.exit(1)
    else:
        print("Check passed! All generated token files match source tokens.json.")


def main():
    parser = argparse.ArgumentParser(description="Linkpoint Workspace Token Generator CLI")
    subparsers = parser.add_subparsers(dest="command", required=True)

    # Generate subcommand
    p_gen = subparsers.add_parser("generate", help="Generate token bindings for target languages")
    p_gen.add_argument("--tokens", default=DEFAULT_TOKENS_PATH, help="Path to source tokens.json")
    p_gen.add_argument("--output-dir", default=None, help="Custom output directory")
    p_gen.add_argument("--target", default="all", help="Target languages (typescript,kotlin,dart,rust,all)")
    p_gen.set_defaults(func=cmd_generate)

    # Check subcommand
    p_chk = subparsers.add_parser("check", help="Verify generated token bindings against source JSON")
    p_chk.add_argument("--tokens", default=DEFAULT_TOKENS_PATH, help="Path to source tokens.json")
    p_chk.add_argument("--output-dir", default=None, help="Custom output directory")
    p_chk.add_argument("--target", default="all", help="Target languages (typescript,kotlin,dart,rust,all)")
    p_chk.set_defaults(func=cmd_check)

    args = parser.parse_args()
    args.func(args)


if __name__ == "__main__":
    main()
