#!/usr/bin/env python3
import argparse
import json
import os
import sys

# Ensure parent directory is in path
sys.path.insert(0, os.path.abspath(os.path.join(os.path.dirname(__file__), "../..")))

from tools.protocol_gen.emitters import EMITTERS
from tools.protocol_gen.parser.llsd_schema_parser import LLSDSchemaParser
from tools.protocol_gen.parser.template_parser import TemplateParser
from tools.protocol_gen.proto_ast.models import ProtocolAST

DEFAULT_TEMPLATE_PATH = "schemas/protocol/message_template.msg"
DEFAULT_LLSD_DIR = "schemas/protocol/llsd"

DEFAULT_TARGET_OUT_DIRS = {
    "kotlin": "legacy/Linkpoint/src/main/java/com/linkpoint/protocol/generated"
    if os.path.exists("legacy/Linkpoint")
    else "Linkpoint/src/main/java/com/linkpoint/protocol/generated",
    "rust": "crates/linkpoint-protocol/src/generated",
    "dart": "packages/viewer-dart/lib/src/generated",
    "typescript": "packages/viewer-types/src/generated",
    "python": "tools/protocol_gen/generated/python",
    "c": "crates/linkpoint-protocol/c_include/generated",
    "java": "legacy/Linkpoint/src/main/java/com/linkpoint/protocol/java/generated"
    if os.path.exists("legacy/Linkpoint")
    else "Linkpoint/src/main/java/com/linkpoint/protocol/java/generated",
}


def load_ast(template_path: str, llsd_dir: str) -> ProtocolAST:
    ast = ProtocolAST()

    if os.path.exists(template_path):
        with open(template_path, "r", encoding="utf-8") as f:
            content = f.read()
        TemplateParser().parse(content, ast)
    else:
        print(f"Warning: Template file {template_path} not found.")

    if os.path.exists(llsd_dir):
        LLSDSchemaParser().parse_directory(llsd_dir, ast)
    else:
        print(f"Warning: LLSD schema directory {llsd_dir} not found.")

    return ast


def cmd_parse(args):
    ast = load_ast(args.template, args.llsd_dir)
    ast_dict = ast.to_dict()
    if args.json:
        print(json.dumps(ast_dict, indent=2))
    else:
        print(f"Parsed Protocol AST (Version {ast.version}):")
        print(f"  - Messages parsed: {len(ast.messages)}")
        print(f"  - LLSD Schemas parsed: {len(ast.llsd_schemas)}")


def cmd_generate(args):
    ast = load_ast(args.template, args.llsd_dir)

    targets = [t.strip().lower() for t in args.target.split(",")]
    if "all" in targets:
        targets = list(EMITTERS.keys())

    generated_count = 0
    for target in targets:
        if target not in EMITTERS:
            print(
                f"Error: Unknown target language '{target}'. Valid options: {list(EMITTERS.keys())}"
            )
            sys.exit(1)

        emitter = EMITTERS[target]
        out_dir = (
            args.out_dir
            if args.out_dir
            else DEFAULT_TARGET_OUT_DIRS.get(target, f"generated/{target}")
        )

        results = emitter.emit(ast, out_dir)
        for path in results.keys():
            print(f"[{target.upper()}] Generated {path}")
            generated_count += 1

    print(
        f"\nSuccessfully generated {generated_count} source files across {len(targets)} targets."
    )


def cmd_validate(args):
    ast = load_ast(args.template, args.llsd_dir)
    if len(ast.messages) == 0:
        print("Validation Failed: No messages parsed from template.")
        sys.exit(1)

    print(
        f"Validation Passed: Canonical specs parsed cleanly. ({len(ast.messages)} messages, {len(ast.llsd_schemas)} LLSD schemas)"
    )


def main():
    parser = argparse.ArgumentParser(
        description="Linkpoint Unified Protocol Code Generator CLI"
    )
    subparsers = parser.add_subparsers(dest="command", required=True)

    # Parse subcommand
    p_parse = subparsers.add_parser("parse", help="Parse canonical specs into AST")
    p_parse.add_argument(
        "--template", default=DEFAULT_TEMPLATE_PATH, help="Path to message_template.msg"
    )
    p_parse.add_argument(
        "--llsd-dir",
        default=DEFAULT_LLSD_DIR,
        help="Directory containing LLSD JSON schemas",
    )
    p_parse.add_argument("--json", action="store_true", help="Output AST as JSON")
    p_parse.set_defaults(func=cmd_parse)

    # Generate subcommand
    p_gen = subparsers.add_parser("generate", help="Generate source code for targets")
    p_gen.add_argument(
        "--template", default=DEFAULT_TEMPLATE_PATH, help="Path to message_template.msg"
    )
    p_gen.add_argument(
        "--llsd-dir",
        default=DEFAULT_LLSD_DIR,
        help="Directory containing LLSD JSON schemas",
    )
    p_gen.add_argument(
        "--target",
        default="all",
        help="Target languages (kotlin,rust,dart,typescript,python,c,java,all)",
    )
    p_gen.add_argument("--out-dir", default=None, help="Custom output directory root")
    p_gen.set_defaults(func=cmd_generate)

    # Validate subcommand
    p_val = subparsers.add_parser("validate", help="Validate canonical specs")
    p_val.add_argument(
        "--template", default=DEFAULT_TEMPLATE_PATH, help="Path to message_template.msg"
    )
    p_val.add_argument(
        "--llsd-dir",
        default=DEFAULT_LLSD_DIR,
        help="Directory containing LLSD JSON schemas",
    )
    p_val.set_defaults(func=cmd_validate)

    args = parser.parse_args()
    args.func(args)


if __name__ == "__main__":
    main()
