# Lumiya APK Files - Decompilation Instructions

This directory contains the original Lumiya APK files for reverse engineering and analysis.

## Files

- **Lumiya_3.4.2.apk** - Main Lumiya Second Life viewer application
- **Lumiya Cloud Plugin_1.0.apk** - Cloud storage plugin
- **Lumiya Voice Plugin_1.4.apk** - Voice communication plugin

## Decompilation with Ghidra

### Quick Start

To decompile all APKs automatically:

```bash
# From repository root
./scripts/decompile_lumiya_with_ghidra.sh
```

### Manual Decompilation

For detailed step-by-step instructions, see:
- **docs/GHIDRA_DECOMPILATION_GUIDE.md** - Complete guide following https://remyhax.xyz/posts/android-with-ghidra/

### Important Notes

**Critical Workflow Requirements** (from the guide):

1. ✅ **DO**: Launch Ghidra Code Browser directly
2. ✅ **DO**: Import APK using File > Import File (Single file mode)
3. ✅ **DO**: Click "No" when prompted to analyze immediately
4. ✅ **DO**: Set External Name Associations for all DEX files
5. ✅ **DO**: Verify all DEX files appear in Listings
6. ✅ **DO**: Then run "Analyze All Open"

**Common Mistakes to Avoid:**

- ❌ **DON'T**: Import from Ghidra main screen
- ❌ **DON'T**: Click "Yes" on immediate analysis
- ❌ **DON'T**: Skip External Name Association setup
- ❌ **DON'T**: Analyze DEX files separately

Following these steps ensures proper multi-DEX analysis with working cross-references.

## Analysis Results

After decompilation, reports are generated in:
```
docs/ghidra_analysis/
├── LUMIYA_APKS_DECOMPILATION_SUMMARY.md
├── Lumiya_3.4.2_analysis_report.md
├── Lumiya Cloud Plugin_1.0_analysis_report.md
└── Lumiya Voice Plugin_1.4_analysis_report.md
```

## Resources

- **Guide**: https://remyhax.xyz/posts/android-with-ghidra/
- **Ghidra**: https://github.com/NationalSecurityAgency/ghidra
- **Local Guide**: ../docs/GHIDRA_DECOMPILATION_GUIDE.md

---

*These APKs are stored for reverse engineering analysis to understand the Lumiya viewer implementation and support modernization efforts.*
