#!/usr/bin/env bash
set -euo pipefail

OUTPUT_JSON="${1:-docs/apk_analysis/code_repair_issue_list.json}"
OUTPUT_MD="${2:-docs/apk_analysis/code_repair_issue_list.md}"

OUTPUT_JSON="$OUTPUT_JSON" OUTPUT_MD="$OUTPUT_MD" python3 - <<'PY'
from __future__ import annotations
import json
import os
import re
from pathlib import Path

repo = Path('.')
output_json = Path(os.environ['OUTPUT_JSON'])
output_md = Path(os.environ['OUTPUT_MD'])

issues = []

# 1) Java decompilation stubs that require smali reconstruction.
stub_re = re.compile(r'Method not decompiled: ([^(]+)\((.*?)\):([^\"]+)')
for java_file in sorted((repo / 'file_bundle').rglob('*.java')):
    text = java_file.read_text(errors='ignore')
    for m in stub_re.finditer(text):
        method_qual = m.group(1)
        class_name, _, method_name = method_qual.rpartition('.')
        issues.append({
            'category': 'java_smali_reconstruction',
            'severity': 'high',
            'file': str(java_file),
            'symbol': f'{class_name}.{method_name}',
            'details': f'Unresolved decompilation stub; rebuild from smali. params=({m.group(2)}) return={m.group(3)}'
        })

# 2) Kotlin conversion review heuristics.
kotlin_files = sorted((repo / 'src/main/java').rglob('*.kt'))
for kt_file in kotlin_files:
    text = kt_file.read_text(errors='ignore')

    if 'ByteArray(0)' in text:
        issues.append({
            'category': 'kotlin_logic_placeholder',
            'severity': 'medium',
            'file': str(kt_file),
            'symbol': 'ByteArray(0)',
            'details': 'Potential placeholder packet/body content; verify against smali/original protocol behavior.'
        })

    if 'Channel.UNLIMITED' in text:
        issues.append({
            'category': 'kotlin_backpressure_risk',
            'severity': 'medium',
            'file': str(kt_file),
            'symbol': 'Channel.UNLIMITED',
            'details': 'Unbounded channel can cause memory growth; verify expected throughput and add backpressure strategy.'
        })

    # simple constant-use heuristic
    constants = re.findall(r'private const val ([A-Z0-9_]+)\s*=.*', text)
    for c in constants:
        count = len(re.findall(rf'\b{re.escape(c)}\b', text))
        if count <= 1:
            issues.append({
                'category': 'kotlin_unused_constant',
                'severity': 'low',
                'file': str(kt_file),
                'symbol': c,
                'details': 'Constant appears unused; verify if dead code or missing implementation linkage.'
            })

# 3) Existing smali-gap report context if present.
gap_report = repo / 'docs/apk_analysis/java_smali_gap_report.json'
if gap_report.exists():
    gap_data = json.loads(gap_report.read_text())
    if gap_data.get('stub_method_count', 0) > 0:
        issues.append({
            'category': 'smali_artifact_availability',
            'severity': 'high',
            'file': str(gap_report),
            'symbol': 'smali_class_count',
            'details': 'Smali class inventory missing/incomplete for current pass; rerun extraction with local APK to unblock stub reconstruction.'
        })

summary = {
    'issue_count': len(issues),
    'high': sum(1 for i in issues if i['severity'] == 'high'),
    'medium': sum(1 for i in issues if i['severity'] == 'medium'),
    'low': sum(1 for i in issues if i['severity'] == 'low'),
    'issues': issues,
}

output_json.parent.mkdir(parents=True, exist_ok=True)
output_json.write_text(json.dumps(summary, indent=2) + '\n')

lines = [
    '# Code Repair Issue List (Java/Kotlin + Smali)',
    '',
    f"Total issues: **{summary['issue_count']}**",
    f"- High: **{summary['high']}**",
    f"- Medium: **{summary['medium']}**",
    f"- Low: **{summary['low']}**",
    '',
    '| Severity | Category | File | Symbol | Details |',
    '|---|---|---|---|---|',
]
for i in issues:
    lines.append(
        f"| {i['severity']} | {i['category']} | `{i['file']}` | `{i['symbol']}` | {i['details']} |"
    )
output_md.write_text('\n'.join(lines) + '\n')
print(json.dumps(summary, indent=2))
PY
