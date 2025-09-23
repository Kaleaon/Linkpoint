# Lumiya Static Analysis Pipeline

This repository includes a reproducible pipeline to analyze a Lumiya APK (or a ZIP containing the APK).

## CI (GitHub Actions)

- Workflow: `.github/workflows/lumiya-static-analysis.yml`
- Triggers: push, pull_request, and manual runs via "Run workflow".
- Input `artifact_path` (optional for manual runs): path (relative to repo root) to the APK or ZIP. If omitted, the workflow will attempt default detection in this order:
  1. `Lumiya_3.4.2.zip`
  2. `Lumiya_3.4.2.apk`
  3. First `*.apk` or `*.zip` at repo root (non-recursive)

### Outputs

The workflow uploads an artifact named `lumiya-analysis` containing:
- `out/jadx/` — Decompiled Java sources via JADX
- `out/apktool/` — Resources, manifest, and smali from apktool
- `out/dex2jar/` — Alternate Java decompilation via dex2jar + CFR
- `out/native/` — Extracted `.so` files (if present)
- `out/ghidra/reports/*.functions.json` — Function lists per native library
- `out/meta/` — File type and zip listing

## Local usage

1. Ensure `apktool` and `jadx` are installed (optionally `dex2jar` + `CFR`).
2. Run:

```bash
./tools/run_apk_decompile.sh path/to/lumiya.apk
```

Or if the APK was renamed as `.zip`:

```bash
./tools/run_apk_decompile.sh path/to/renamed_lumiya.zip
```

3. Results will be in `out/`.

## Notes

- The CI job is resource-intensive; prefer manual runs when not needed on every push.
- Keep the repository private if it contains proprietary binaries.
- Avoid committing very large binaries without Git LFS.