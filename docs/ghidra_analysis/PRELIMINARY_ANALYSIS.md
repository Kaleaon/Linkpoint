# Lumiya APK Analysis - Preliminary Report

## Overview

This preliminary report documents the initial analysis of the Lumiya APK files prior to full Ghidra decompilation. The analysis validates the APK structure and identifies key components for reverse engineering.

**Analysis Date**: 2025-10-29
**Method**: Manual APK inspection and DEX extraction
**Guide Reference**: https://remyhax.xyz/posts/android-with-ghidra/

## APK Files Examined

### 1. Lumiya_3.4.2.apk

**File Size**: 10.5 MB (11,234,005 bytes)

**Structure Analysis**:
- ✅ **DEX Files**: 1 file (classes.dex - 7.6 MB)
- ✅ **Native Libraries**: Multiple architectures detected
- ✅ **AndroidManifest**: Present

**DEX Content**:
```
classes.dex - 7.6 MB
  └─ Lumiya-specific classes: 6 identified
```

**Native Libraries**:
```
ARM64 (arm64-v8a):
  - libgvr.so (1.7 MB) - Google VR support
  - libopenjpeg.so (192 KB) - JPEG2000 codec
  - librawbuf.so (9.6 KB) - Raw buffer handling

ARM (armeabi-v7a):
  - libgvr.so (1.0 MB)
  - libopenjpeg.so (143 KB)
  - librawbuf.so (13 KB)

x86/x86_64:
  - libgvr.so, libopenjpeg.so, librawbuf.so

MIPS/MIPS64:
  - libopenjpeg.so, librawbuf.so
```

**Lumiya Classes Found** (sample):
```
Lcom/lumiyaviewer/lumiya/Debug;
Lcom/lumiyaviewer/lumiya/R$id;
Lcom/lumiyaviewer/lumiya/R$raw;
Lcom/lumiyaviewer/lumiya/R$xml;
Lcom/lumiyaviewer/lumiya/R;
Lcom/lumiyaviewer/rawbuffers/R;
```

### 2. Lumiya Cloud Plugin_1.0.apk

**File Size**: 2.7 MB (2,797,429 bytes)
**Analysis**: Pending full Ghidra decompilation

### 3. Lumiya Voice Plugin_1.4.apk

**File Size**: 4.9 MB (5,132,788 bytes)
**Analysis**: Pending full Ghidra decompilation

## Multi-DEX Analysis Requirements

### Lumiya_3.4.2.apk: Single DEX

This APK contains only one DEX file (`classes.dex`), which simplifies the analysis process. However, following the guide's best practices is still important:

**Workflow for Single-DEX APKs**:
1. ✅ Launch Code Browser directly (not main screen import)
2. ✅ Import APK as "Single file"
3. ✅ Click "No" on immediate analysis
4. ✅ Verify classes.dex appears in Listings
5. ✅ Set External Name Association (even for single DEX)
6. ✅ Then run "Analyze All Open"

**Why This Matters**:
Even with a single DEX file, the proper workflow ensures:
- Correct symbol resolution
- Proper JNI linkage to native libraries
- Clean decompilation output
- Ability to cross-reference with native code

## Native Code Integration

The presence of multiple native libraries indicates JNI (Java Native Interface) usage:

**libopenjpeg.so** - JPEG2000 image decoder
- Multiple architectures (ARM, x86, MIPS)
- Critical for texture decoding in Second Life
- Will require separate Ghidra analysis alongside DEX

**libgvr.so** - Google VR/Cardboard support
- Only in ARM and x86 architectures
- Virtual reality functionality
- Complex C++ codebase

**librawbuf.so** - Raw buffer handling
- All architectures
- Low-level memory management
- May have JNI calls from Java code

## Decompilation Strategy

### Phase 1: DEX Analysis (Current Focus)

Following https://remyhax.xyz/posts/android-with-ghidra/:

1. **Import Setup**
   - Use Code Browser > File > Import File
   - Select "Single file" mode
   - Auto-detect as "Android APK"

2. **Pre-Analysis Configuration**
   - Do NOT analyze immediately
   - Set External Name Associations
   - Verify all files in Listings

3. **Comprehensive Analysis**
   - Analysis > Analyze All Open
   - Enable all DEX-specific analyzers
   - Allow full timeout for complete analysis

### Phase 2: Native Library Analysis

After DEX analysis:

1. Import each .so file separately into Ghidra
2. Analyze for each architecture (ARM64 recommended)
3. Identify JNI function signatures
4. Cross-reference with DEX JNI calls
5. Document native method implementations

### Phase 3: Integration Analysis

Combine DEX and native analysis:

1. Map JNI function calls from Java to native code
2. Create unified call graphs
3. Document complete execution flow
4. Identify security-critical paths

## Key Findings

### APK Structure

✅ **Single DEX Design**: Simplifies analysis (no multi-DEX complexity)
✅ **Multi-Architecture Support**: 5 architectures (ARM, ARM64, x86, x86_64, MIPS)
✅ **VR Integration**: Google VR/Cardboard support included
✅ **Image Decoding**: JPEG2000 codec for Second Life textures

### Reverse Engineering Targets

**Primary Analysis**:
- `classes.dex` - Main application logic (7.6 MB)
- JNI interface definitions
- Android resource bindings (R.* classes)

**Secondary Analysis**:
- Native libraries (architecture-specific)
- VR integration code
- Image codec implementation

## Next Steps

### Automated Decompilation

Run the automated decompilation script:
```bash
./scripts/decompile_lumiya_with_ghidra.sh
```

This will:
1. Download and install Ghidra (if needed)
2. Process all 3 APK files
3. Follow proper multi-DEX workflow
4. Generate comprehensive reports
5. Extract all symbols and signatures

### Manual GUI Analysis

For interactive exploration:
```bash
# Launch Ghidra GUI
$GHIDRA_HOME/ghidraRun

# Follow steps in docs/GHIDRA_DECOMPILATION_GUIDE.md
```

### Expected Outputs

After full decompilation:
- DEX structure analysis (JSON)
- Class hierarchy diagrams
- Method signature listings
- Native library analysis
- JNI cross-reference maps
- Security vulnerability reports

## Critical Workflow Reminders

Based on https://remyhax.xyz/posts/android-with-ghidra/:

### ✅ DO:
- Launch Code Browser directly
- Import as "Single file"
- Say NO to immediate analysis
- Set External Name Associations
- Analyze All Open when ready

### ❌ DON'T:
- Import from main Ghidra screen
- Click YES on analysis prompt
- Skip External Name Association
- Analyze files separately
- Forget to check Listings window

## References

- **Analysis Guide**: https://remyhax.xyz/posts/android-with-ghidra/
- **Ghidra Project**: https://github.com/NationalSecurityAgency/ghidra
- **DEX Format**: https://source.android.com/devices/tech/dalvik/dex-format
- **JNI Specification**: https://docs.oracle.com/javase/8/docs/technotes/guides/jni/
- **Local Guide**: ./GHIDRA_DECOMPILATION_GUIDE.md

## Conclusion

The Lumiya APK structure is well-suited for Ghidra analysis:
- Single DEX file reduces complexity
- Native libraries are properly architected
- Standard Android APK format
- Clear separation of Java and native code

Following the guide's workflow will ensure complete and accurate decompilation with proper symbol resolution and cross-references.

---

*Preliminary analysis completed. Full Ghidra decompilation pending.*
