#!/usr/bin/env bash
set -euo pipefail

JAVA_ROOT="${1:-file_bundle}"
SMALI_ROOT="${2:-/tmp/lumiya_task/smali_out}"
OUTPUT="${3:-docs/apk_analysis/java_smali_gap_report.json}"

JAVA_ROOT="$JAVA_ROOT" SMALI_ROOT="$SMALI_ROOT" OUTPUT="$OUTPUT" python3 - <<'PY'
from __future__ import annotations
import json
import os
import re
from pathlib import Path

stub_re = re.compile(r'Method not decompiled: ([^(]+)\((.*?)\):([^\"]+)')

java_root = Path(os.environ['JAVA_ROOT'])
smali_root = Path(os.environ['SMALI_ROOT'])
out = Path(os.environ['OUTPUT'])

smali_classes = set()
if smali_root.exists():
    for smali_file in smali_root.rglob('*.smali'):
        rel = smali_file.relative_to(smali_root).with_suffix('')
        class_name = '.'.join(rel.parts)
        if class_name.startswith('classes.'):
            class_name = class_name[len('classes.'):]
        smali_classes.add(class_name)

stubs = []
for java_file in sorted(java_root.rglob('*.java')):
    text = java_file.read_text(errors='ignore')
    for m in stub_re.finditer(text):
        method_qual = m.group(1)
        class_name, _, method_name = method_qual.rpartition('.')
        stubs.append({
            'java_file': str(java_file),
            'class': class_name,
            'method': method_name,
            'params': m.group(2),
            'return_type': m.group(3),
            'smali_class_present': class_name in smali_classes,
        })

summary = {
    'stub_method_count': len(stubs),
    'smali_class_count': len(smali_classes),
    'stubs_with_missing_smali_class_count': len([s for s in stubs if not s['smali_class_present']]),
    'stubs': stubs,
}

out.parent.mkdir(parents=True, exist_ok=True)
out.write_text(json.dumps(summary, indent=2) + '\n')
print(json.dumps(summary, indent=2))
PY
