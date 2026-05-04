#!/usr/bin/env python3
from pathlib import Path
import sys
import xml.etree.ElementTree as ET
RULE_FILES=[Path('Linkpoint/src/main/res/xml/backup_rules.xml'),Path('Linkpoint/src/main/res/xml/data_extraction_rules.xml')]
REQ={("sharedpref","com_linkpoint_mfa_hashes.xml"),("sharedpref","com_linkpoint_mfa_hashes_encrypted.xml"),("sharedpref","device.xml"),("file","crash_logs/"),("file","Linkpoint Logs/"),("database","inventory.db"),("database","inventory.db-shm"),("database","inventory.db-wal"),("root","cache/")}
ALLOW={("sharedpref","com.linkpoint_preferences.xml"),("sharedpref","theme_manager.xml"),("sharedpref","cache_settings.xml"),("sharedpref","destination_guide.xml")}
def pairs(p,t):
 r=ET.parse(p).getroot();return {(e.attrib.get('domain',''),e.attrib.get('path','')) for e in r.iter(t)}
fail=False
for f in RULE_FILES:
 ex=pairs(f,'exclude');inc=pairs(f,'include');miss=sorted(REQ-ex);unsafe=sorted(x for x in inc if x not in ALLOW)
 if miss or unsafe: fail=True
 if miss:
  print(f'ERROR: {f} missing excludes:');[print(f'  - domain={d} path={p}') for d,p in miss]
 if unsafe:
  print(f'ERROR: {f} has non-allowlisted includes:');[print(f'  - domain={d} path={p}') for d,p in unsafe]
 if not miss and not unsafe: print(f'OK: {f}')
sys.exit(1 if fail else 0)
