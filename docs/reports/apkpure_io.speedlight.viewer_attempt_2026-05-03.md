# APK Retrieval + Decompilation Study: io.speedlight.viewer

Date: 2026-05-03 (UTC)
Primary URL requested: `https://d.apkpure.com/b/APK/io.speedlight.viewer?version=latest`
Fallback provided by reviewer: `https://drive.google.com/file/d/1DwPnZLgyMVhvvQqO2FULV9pUiimHBrND/view?usp=drivesdk`

## Retrieval outcome
- APKPure direct download remained blocked by Cloudflare challenge HTML.
- Google Drive fallback succeeded after following the virus-scan confirmation flow.
- Retrieved artifact: `speedlight-viewer.apk` (8,717,702 bytes), valid ZIP/APK (`PK\x03\x04`), 605 entries.

## Decompilation outcome
- Decompiled with `jadx` into `speedlight_decompiled/`.
- `jadx` completed with 3 non-fatal errors (`finished with errors, count: 3`).
- Primary app namespace present: `io.speedlight.viewer`.

## High-level app profile
- Package: `io.speedlight.viewer`
- Version: `30.190.1616` (`versionCode` 324576)
- App framework: Cordova (hybrid app), launcher activity extends `CordovaActivity`.
- App label/identity in config: `SpeedLight`, described as "Web and mobile viewer for Second Life".

## Security/network-relevant observations
- `config.xml` content URL: `http://localhost:12224/` (local web app server in Cordova container).
- `config.xml` includes broad navigation/access rules (`allow-navigation href="*"`, `access origin="http://*"`, `access origin="https://*"`).
- Manifest/config indicates cleartext traffic enabled via merged application config.
- Integrations/plugins observed: Firebase messaging, PushNotification, InAppBrowser, AppRate, analytics endpoints, Sentry endpoint, and multiple speedlight domains.

## Key files reviewed
- `speedlight_decompiled/resources/AndroidManifest.xml`
- `speedlight_decompiled/resources/res/xml/config.xml`
- `speedlight_decompiled/sources/io/speedlight/viewer/MainActivity.java`
- `speedlight_decompiled/sources/io/speedlight/viewer/BuildConfig.java`

## Commands used
```bash
# 1) Initial Google Drive response (virus-scan warning HTML)
curl -L -c /tmp/gc.txt \
  'https://drive.google.com/uc?export=download&id=1DwPnZLgyMVhvvQqO2FULV9pUiimHBrND' \
  -o /tmp/gdrive_resp

# 2) Confirmed download URL from hidden form inputs
curl -L \
  'https://drive.usercontent.google.com/download?id=1DwPnZLgyMVhvvQqO2FULV9pUiimHBrND&export=download&confirm=t&uuid=6e75110b-f947-4d07-932b-488bcbc749a5' \
  -o speedlight-viewer.apk

# 3) Validate APK structure
python - <<'PY'
from pathlib import Path
import zipfile
p=Path('speedlight-viewer.apk')
print('size',p.stat().st_size)
print('magic',p.read_bytes()[:4])
z=zipfile.ZipFile(p)
print('zip entries',len(z.namelist()))
PY

# 4) Decompile
jadx -d speedlight_decompiled speedlight-viewer.apk
```
