#!/usr/bin/env python3
import re,sys
from pathlib import Path
root=Path(__file__).resolve().parents[1]/'src/main/java/com/linkpoint/protocol/messages/ids'
pat=re.compile(r'const val\s+(\w+)\s*=\s*(.+)')
vals={}
for f in sorted(root.glob('*Messages.kt')):
    for i,l in enumerate(f.read_text().splitlines(),1):
        m=pat.search(l)
        if not m: continue
        name,expr=m.groups()
        expr=expr.split('//')[0].strip()
        if '.toInt()' in expr: expr=expr.replace('.toInt()','')
        try: v=eval(expr,{}, {})
        except: continue
        vals.setdefault(v,[]).append((name,f.name,i))
errs=[(v,a) for v,a in vals.items() if len(a)>1]
if errs:
    print('Duplicate message IDs found:')
    for v,a in errs:
        print(v,a)
    sys.exit(1)
print(f'Validated {sum(len(v) for v in vals.values())} message IDs across domain files with no duplicates.')
