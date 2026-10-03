#!/usr/bin/env python3
import argparse
import json
import os
import sys
import xml.etree.ElementTree as ET
from pathlib import Path


def parse_jacoco_xml(xml_path: Path):
    if not xml_path.exists():
        print(f"Warning: JaCoCo XML file not found at {xml_path}")
        return None

    try:
        tree = ET.parse(xml_path)
        root = tree.getroot()
        for counter in root.findall("counter"):
            if counter.attrib.get("type") == "LINE":
                covered = int(counter.attrib.get("covered", 0))
                missed = int(counter.attrib.get("missed", 0))
                total = covered + missed
                pct = (covered / total * 100.0) if total > 0 else 0.0
                return {
                    "covered": covered,
                    "missed": missed,
                    "total": total,
                    "percentage": round(pct, 2)
                }
        # Fallback to INSTRUCTION if LINE counter is missing
        for counter in root.findall("counter"):
            if counter.attrib.get("type") == "INSTRUCTION":
                covered = int(counter.attrib.get("covered", 0))
                missed = int(counter.attrib.get("missed", 0))
                total = covered + missed
                pct = (covered / total * 100.0) if total > 0 else 0.0
                return {
                    "covered": covered,
                    "missed": missed,
                    "total": total,
                    "percentage": round(pct, 2)
                }
    except Exception as e:
        print(f"Error parsing JaCoCo report {xml_path}: {e}")

    return None


def parse_lcov(lcov_path: Path):
    if not lcov_path.exists():
        print(f"Warning: LCOV file not found at {lcov_path}")
        return None

    try:
        lines_found = 0
        lines_hit = 0
        with open(lcov_path, "r", encoding="utf-8") as f:
            for line in f:
                line = line.strip()
                if line.startswith("LF:"):
                    lines_found += int(line.split(":")[1])
                elif line.startswith("LH:"):
                    lines_hit += int(line.split(":")[1])

        missed = lines_found - lines_hit
        pct = (lines_hit / lines_found * 100.0) if lines_found > 0 else 0.0
        return {
            "covered": lines_hit,
            "missed": missed,
            "total": lines_found,
            "percentage": round(pct, 2)
        }
    except Exception as e:
        print(f"Error parsing LCOV report {lcov_path}: {e}")

    return None


def main():
    parser = argparse.ArgumentParser(description="Evaluate Component Coverage & Quality Gates")
    parser.add_argument("--android-report", type=Path, default=Path("Linkpoint/build/reports/jacoco/jacocoTestReport/jacocoTestReport.xml"))
    parser.add_argument("--web-report", type=Path, default=Path("coverage/lcov.info"))
    parser.add_argument("--rust-report", type=Path, default=Path("lcov.info"))
    parser.add_argument("--baseline-file", type=Path, default=Path("coverage-baseline.json"))
    parser.add_argument("--allow-negative-delta", action="store_true", help="Allow negative delta coverage for urgent fixes")
    parser.add_argument("--update-baseline", action="store_true", help="Update baseline file with current coverage")
    args = parser.parse_args()

    # Load baseline config
    baseline_data = {}
    if args.baseline_file.exists():
        with open(args.baseline_file, "r", encoding="utf-8") as f:
            baseline_data = json.load(f)

    results = {}

    # 1. Android
    android_data = parse_jacoco_xml(args.android_report)
    if android_data is None:
        # Search fallback locations
        for fallback in Path(".").glob("**/jacoco*.xml"):
            android_data = parse_jacoco_xml(fallback)
            if android_data:
                break

    results["android"] = android_data

    # 2. Web
    web_data = parse_lcov(args.web_report)
    if web_data is None:
        for fallback in Path(".").glob("**/coverage/lcov.info"):
            web_data = parse_lcov(fallback)
            if web_data:
                break

    results["web"] = web_data

    # 3. Rust
    rust_data = parse_lcov(args.rust_report)
    if rust_data is None:
        for fallback in [Path("coverage-rust.lcov"), Path("rust-lcov.info")]:
            rust_data = parse_lcov(fallback)
            if rust_data:
                break

    results["rust"] = rust_data

    # Update baseline if requested
    if args.update_baseline:
        for comp, res in results.items():
            if res:
                if comp not in baseline_data:
                    baseline_data[comp] = {}
                baseline_data[comp]["baseline"] = res["percentage"]
                if "min_threshold" not in baseline_data[comp]:
                    baseline_data[comp]["min_threshold"] = max(0.0, round(res["percentage"] - 5.0, 2))
        with open(args.baseline_file, "w", encoding="utf-8") as f:
            json.dump(baseline_data, f, indent=2)
        print(f"Updated baseline file {args.baseline_file}")

    # Evaluate Quality Gates
    all_passed = True
    gate_evaluations = []

    components = [
        ("android", "🤖 Android", "Android Unit Tests (JaCoCo)"),
        ("web", "🌐 Web", "Web Workspace Tests (Vitest V8)"),
        ("rust", "🦀 Rust", "Rust Workspace Tests (LLVM-cov)"),
    ]

    table_rows = []

    for key, display_name, description in components:
        comp_res = results.get(key)
        comp_base = baseline_data.get(key, {})
        base_pct = comp_base.get("baseline", 0.0)
        min_thresh = comp_base.get("min_threshold", 0.0)

        if comp_res is None:
            status_str = "❌ FAIL (Missing Report)"
            all_passed = False
            table_rows.append(f"| {display_name} | N/A | N/A | {base_pct:.2f}% | N/A | {min_thresh:.2f}% | ❌ MISSING |")
            gate_evaluations.append(f"- **{display_name}**: ❌ Missing report file")
            continue

        curr_pct = comp_res["percentage"]
        covered = comp_res["covered"]
        total = comp_res["total"]
        delta = curr_pct - base_pct
        delta_str = f"{delta:+.2f}%"

        reasons = []
        if curr_pct < min_thresh:
            reasons.append(f"Coverage {curr_pct:.2f}% below minimum threshold {min_thresh:.2f}%")
        if delta < -0.01 and not args.allow_negative_delta:
            reasons.append(f"Negative delta coverage ({delta_str})")

        if reasons:
            comp_passed = False
            all_passed = False
            status_icon = "❌ FAIL"
            gate_evaluations.append(f"- **{display_name}**: ❌ Failed ({'; '.join(reasons)}) [Current: {curr_pct:.2f}%, Baseline: {base_pct:.2f}%, Threshold: {min_thresh:.2f}%]")
        else:
            comp_passed = True
            status_icon = "✅ PASS"
            gate_evaluations.append(f"- **{display_name}**: ✅ Passed [Current: {curr_pct:.2f}%, Baseline: {base_pct:.2f}%, Threshold: {min_thresh:.2f}%, Delta: {delta_str}]")

        table_rows.append(f"| {display_name} | {curr_pct:.2f}% | {covered:,} / {total:,} | {base_pct:.2f}% | {delta_str} | {min_thresh:.2f}% | {status_icon} |")

    # Build Markdown Summary
    markdown_lines = []
    markdown_lines.append("## 📊 Continuous Integration Code Coverage & Quality Gate Summary\n")
    markdown_lines.append("| Component | Coverage | Lines Hit / Total | Baseline | Delta | Minimum Threshold | Status |")
    markdown_lines.append("| :--- | :--- | :--- | :--- | :--- | :--- | :--- |")
    for row in table_rows:
        markdown_lines.append(row)

    markdown_lines.append("\n### Quality Gate Assessment")
    for eval_line in gate_evaluations:
        markdown_lines.append(eval_line)

    if all_passed:
        markdown_lines.append("\n**Overall Quality Gate Status**: ✅ **PASSED**")
    else:
        markdown_lines.append("\n**Overall Quality Gate Status**: ❌ **FAILED**")
        markdown_lines.append("\n> ⚠️ **Action Required for Quality Gate Failure**:")
        markdown_lines.append("> One or more components failed coverage quality gate requirements.")
        markdown_lines.append("> - Please add unit tests covering new or modified code logic.")
        markdown_lines.append("> - To run coverage locally:")
        markdown_lines.append(">   - Android: `./gradlew testDebugUnitTest jacocoTestReport`")
        markdown_lines.append(">   - Web: `npm run test:coverage`")
        markdown_lines.append(">   - Rust: `cargo llvm-cov --workspace --lcov --output-path lcov.info`")
        markdown_lines.append(">   - Evaluate: `python3 scripts/evaluate_coverage.py`")

    summary_content = "\n".join(markdown_lines)
    print(summary_content)

    # Write to GITHUB_STEP_SUMMARY if present
    step_summary_path = os.environ.get("GITHUB_STEP_SUMMARY")
    if step_summary_path:
        with open(step_summary_path, "a", encoding="utf-8") as f:
            f.write(summary_content + "\n")

    if not all_passed:
        sys.exit(1)
    else:
        sys.exit(0)


if __name__ == "__main__":
    main()
