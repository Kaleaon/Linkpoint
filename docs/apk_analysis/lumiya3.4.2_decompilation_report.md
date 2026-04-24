# Lumiya 3.4.2 APK Decompilation + Full-Extraction Validation Report

## Input Artifact
- APK URL: https://tmpfiles.org/dl/34983673/lumiya3.4.2.apk
- Downloaded file: `/tmp/lumiya_task/lumiya3.4.2.apk`
- SHA256: `cc4bac60dc2df24f5e4e98be293ba4b9061ac237156afab6629793fa2ffc0c5d`

## Tooling Used
1. **JADX** (`jadx`) for Java decompilation.
2. **Ghidra** (`analyzeHeadless`) for DEX import/analysis.
3. **smali/baksmali** (`baksmali` from `libsmali-java`, version `2.5.2.git2771eae`) for bytecode-level class verification.
4. **smali assembler** (`smali assemble`) for round-trip DEX validation.
5. **radare2/rabin2** (`radare2 5.5.0`) for native `.so` inventory and string-focused binary triage.
6. **ZIP container checks** (`unzip -tqq`) and optional signing/alignment checks (`apksigner`, `zipalign`).

### References Used
- smali project: https://github.com/JesusFreke/smali
- smali wiki: https://github.com/JesusFreke/smali/wiki
- Ghidra workflow article: https://remyhax.xyz/posts/android-with-ghidra/
- Appknox radare2 article: https://www.appknox.com/blog/binary-exploitation-with-radare2

## Workflow Updates
### 1) Extract all directly packaged code artifacts
- Extract `classes*.dex` for primary Dalvik payload.
- Extract `lib/**/*.so` for native payload.
- Extract potential dynamic code containers under assets (`assets/*.jar`, `assets/*.zip`, `assets/*.dex`) to avoid missing late-loaded modules.

### 2) Corruption/integrity gates
- ZIP structure check: `unzip -tqq`.
- DEX magic check and per-file SHA256 in summary.
- smali round-trip check (`baksmali` → `smali assemble`) to ensure disassembly/reassembly validity.
- Optional signing and alignment checks when tooling exists (`apksigner verify`, `zipalign -c`).

### 3) Multi-lens semantic checks
- JADX Java classes vs smali class declarations.
- DEX string class signatures as a Ghidra-like lens.
- Native library triage with `rabin2 -I` and `rabin2 -z` keyword scan.

## Commands Executed
```bash
# Download APK
curl -L --fail -o /tmp/lumiya_task/lumiya3.4.2.apk \
  https://tmpfiles.org/dl/34983673/lumiya3.4.2.apk

# Basic APK container integrity
unzip -tqq /tmp/lumiya_task/lumiya3.4.2.apk

# Decompile Java
jadx -d /tmp/lumiya_task/jadx_out /tmp/lumiya_task/lumiya3.4.2.apk

# Extract code-bearing payloads
unzip -q -o /tmp/lumiya_task/lumiya3.4.2.apk 'classes*.dex' -d /tmp/lumiya_task/dex
unzip -q -o /tmp/lumiya_task/lumiya3.4.2.apk 'lib/*' -d /tmp/lumiya_task/native_libs
unzip -q -o /tmp/lumiya_task/lumiya3.4.2.apk 'assets/*.jar' 'assets/*.zip' 'assets/*.dex' -d /tmp/lumiya_task/extra_code

# smali disassemble + reassemble
baksmali disassemble /tmp/lumiya_task/dex/classes.dex -o /tmp/lumiya_task/smali_out/classes
smali assemble /tmp/lumiya_task/smali_out/classes -o /tmp/lumiya_task/roundtrip/classes.roundtrip.dex

# radare2/rabin2 examples used by automation
rabin2 -I /tmp/lumiya_task/native_libs/lib/x86/libopenjpeg.so
rabin2 -z /tmp/lumiya_task/native_libs/lib/x86/libopenjpeg.so
```

## Results Summary
From `docs/apk_analysis/lumiya3.4.2_triple_check_summary.json`:
- `apk_integrity.zip_structure_ok`: **true**
- `apk_integrity.apksigner_status`: **not_run** (tool unavailable in environment)
- `apk_integrity.zipalign_status`: **not_run** (tool unavailable in environment)
- `dex_files_processed`: **[`classes.dex`]**
- `dex_integrity[0].valid_dex_magic`: **true**
- `roundtrip_dex_status[0].valid_dex_magic`: **true**
- `extra_code_artifacts`: **[]** (no additional JAR/ZIP/DEX in assets root)
- `java_total`: **2996**
- `smali_total`: **8240**
- `missing_java_in_smali_count`: **2** (`com.google.gson.internal.C$Gson$Preconditions`, `com.google.gson.internal.C$Gson$Types`)
- `lumiya_missing_in_smali_count`: **0** ✅
- `lumiya_missing_in_ghidra_strings_count`: **2**
- `radare2_native_library_count`: **17**
- `radare2_sensitive_string_hit_count`: **0**

From `docs/apk_analysis/lumiya3.4.2_radare2_summary.json`:
- Complete cross-architecture `.so` inventory with SHA256 per native library.
- No keyword-based sensitive string hits in current scan configuration.

## What still needs to be done for “all code extracted” and “not corrupted” confidence
1. **Recursive asset code discovery**
   - Current extraction pattern checks `assets/*.jar|*.zip|*.dex` at root; extend to recursive `assets/**` and nested archives.
2. **Resource-level decompilation pass (`apktool d`)**
   - Decode Android resources + manifest merge behavior, then diff against JADX XML/resource output.
3. **Ghidra full run**
   - Install/provide `analyzeHeadless` path and process all DEX/native artifacts inside one project for symbol-level review.
4. **Independent decompiler parity**
   - Add CFR/fernflower or JADX fallback modes and compare class/method deltas to detect decompiler corruption.
5. **DEX verifier pass**
   - Run `dexdump`/ART verifier-oriented checks per DEX to catch malformed but parseable bytecode.
6. **Round-trip equivalence checks**
   - Beyond magic/header validation, compare class-def counts and method ID ranges between original vs round-tripped DEX.
7. **Signature-chain validation**
   - Run `apksigner verify --print-certs` to validate signer metadata and detect tamper scenarios.
8. **Native binary deep RE**
   - For suspicious libs, run `r2 -A`, `afl`, `izz`, and selected `pdf` function reviews rather than string-only triage.

## Environment Notes
- JADX finished with 2 recoverable errors (`exit code 3`) while still producing usable sources.
- `analyzeHeadless` was unavailable at `/tmp/ghidra/ghidra_12.0.4_PUBLIC` in this run.
- `apksigner` and `zipalign` were not present, so those checks were recorded as `not_run`.
