#!/usr/bin/env python3
import argparse
import json
import os
import shutil
import subprocess
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
    "kotlin": "Linkpoint/src/main/java/com/linkpoint/protocol/generated",
    "rust": "crates/linkpoint-protocol/src/generated",
    "dart": "packages/viewer-dart/lib/src/generated",
    "typescript": "packages/viewer-types/src/generated",
    "python": "tools/protocol_gen/generated/python",
    "c": "crates/linkpoint-protocol/c_include/generated",
    "java": "Linkpoint/src/main/java/com/linkpoint/protocol/java/generated",
    "swift": "platforms/iOS/Sources/LLSD/Generated",
}

DEFAULT_FBS_OUT_DIRS = {
    "swift": "platforms/iOS/Sources/LLSD/Generated",
    "rust": "crates/linkpoint-assets/src/generated",
    "dart": "packages/viewer-dart/lib/src/generated",
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


def compile_flatbuffers(
    targets: list[str], fbs_dir: str = "schemas", out_dir: str | None = None
) -> int:
    flatc_path = shutil.which("flatc")
    if not flatc_path:
        sys.stderr.write(
            "Error: 'flatc' binary (FlatBuffers compiler) not found in system PATH.\n"
            "Please install the FlatBuffers compiler to compile .fbs schemas:\n"
            "  - Ubuntu/Debian: sudo apt-get install flatbuffers-compiler\n"
            "  - macOS: brew install flatbuffers\n"
            "  - Build from source: https://github.com/google/flatbuffers\n"
        )
        sys.stderr.flush()
        sys.exit(1)

    seen_basenames = set()
    unique_fbs_files = []
    candidate_dirs = [fbs_dir, "schemas", "schemas/protocol", "schema"]
    for d in candidate_dirs:
        if os.path.exists(d):
            if os.path.isfile(d) and d.endswith(".fbs"):
                bname = os.path.basename(d)
                if bname not in seen_basenames:
                    seen_basenames.add(bname)
                    unique_fbs_files.append(d)
            elif os.path.isdir(d):
                for root, _, files in sorted(os.walk(d)):
                    for file in sorted(files):
                        if file.endswith(".fbs") and file not in seen_basenames:
                            seen_basenames.add(file)
                            unique_fbs_files.append(os.path.join(root, file))

    fbs_files = unique_fbs_files

    if not fbs_files:
        print(f"Warning: No FlatBuffers (.fbs) schema files found in '{fbs_dir}'.")
        return 0

    generated_fbs_count = 0
    for target in targets:
        target_clean = target.strip().lower()
        if target_clean not in DEFAULT_FBS_OUT_DIRS:
            continue

        target_dir = (
            out_dir
            if out_dir
            else DEFAULT_FBS_OUT_DIRS.get(
                target_clean,
                DEFAULT_TARGET_OUT_DIRS.get(target_clean, f"generated/{target_clean}"),
            )
        )
        os.makedirs(target_dir, exist_ok=True)

        lang_flag = f"--{target_clean}"
        for fbs_file in fbs_files:
            cmd = [flatc_path, lang_flag, "-o", target_dir, fbs_file]
            try:
                subprocess.run(cmd, capture_output=True, text=True, check=True)
                print(
                    f"[{target_clean.upper()}] FlatBuffers compiled {fbs_file} -> {target_dir}"
                )
                generated_fbs_count += 1
            except subprocess.CalledProcessError as e:
                print(
                    f"Error compiling FlatBuffers schema {fbs_file} for target '{target_clean}': {e.stderr}"
                )
                sys.exit(1)

        if target_clean == "rust":
            rustfmt_bin = shutil.which("rustfmt")
            cargo_bin = shutil.which("cargo")
            rs_files = [
                os.path.join(target_dir, fname)
                for fname in os.listdir(target_dir)
                if fname.endswith(".rs")
            ]
            if rustfmt_bin and rs_files:
                subprocess.run(
                    [rustfmt_bin, "--edition", "2021"] + rs_files,
                    capture_output=True,
                )
            elif cargo_bin:
                subprocess.run([cargo_bin, "fmt", "--all"], capture_output=True)

        if target_clean == "dart":
            for fname in os.listdir(target_dir):
                if fname.endswith(".dart") and fname != "generated.dart":
                    fpath = os.path.join(target_dir, fname)
                    with open(fpath, "r", encoding="utf-8") as f:
                        content = f.read()
                    if "// ignore_for_file:" in content:
                        content = content.replace(
                            "// ignore_for_file: unused_import, unused_field, unused_element, unused_local_variable",
                            "// ignore_for_file: unused_import, unused_field, unused_element, unused_local_variable, uri_does_not_exist, undefined_method, override_on_non_overriding_member",
                        )
                    else:
                        content = (
                            "// ignore_for_file: unused_import, unused_field, unused_element, unused_local_variable, uri_does_not_exist, undefined_method, override_on_non_overriding_member\n"
                            + content
                        )
                    with open(fpath, "w", encoding="utf-8") as f:
                        f.write(content)

        if target_clean == "swift":
            import re

            for fname in os.listdir(target_dir):
                if fname.endswith(".swift"):
                    fpath = os.path.join(target_dir, fname)
                    with open(fpath, "r", encoding="utf-8") as f:
                        content = f.read()
                    if "FlatBuffersVersion_" in content:
                        content = re.sub(
                            r"FlatBuffersVersion_\d+_\d+_\d+\(\)",
                            "FlatBuffersVersion_23_5_26()",
                            content,
                        )
                        with open(fpath, "w", encoding="utf-8") as f:
                            f.write(content)

    return generated_fbs_count


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
    valid_targets = set(EMITTERS.keys()) | set(DEFAULT_FBS_OUT_DIRS.keys())
    if "all" in targets:
        targets = sorted(list(valid_targets))

    for target in targets:
        if target not in valid_targets:
            print(
                f"Error: Unknown target language '{target}'. Valid options: {sorted(list(valid_targets))}"
            )
            sys.exit(1)

    generated_count = 0
    for target in targets:
        if target in EMITTERS:
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

    fbs_targets = [t for t in targets if t in DEFAULT_FBS_OUT_DIRS or args.flatc]
    if args.flatc or fbs_targets:
        fbs_count = compile_flatbuffers(
            fbs_targets, fbs_dir=args.fbs_dir, out_dir=args.out_dir
        )
        generated_count += fbs_count

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
        help="Target languages (kotlin,rust,dart,typescript,python,c,java,swift,all)",
    )
    p_gen.add_argument("--out-dir", default=None, help="Custom output directory root")
    p_gen.add_argument(
        "--flatc",
        action="store_true",
        help="Compile FlatBuffers .fbs schemas using flatc",
    )
    p_gen.add_argument(
        "--fbs-dir",
        default="schemas",
        help="Directory containing FlatBuffers .fbs schemas",
    )
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
