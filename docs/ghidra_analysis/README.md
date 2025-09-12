# Ghidra Analysis of Lumiya APK

## Overview

This directory contains the results of analyzing the Lumiya APK using Ghidra (from @NationalSecurityAgency/ghidra).
The analysis compares the decompiled output from Ghidra with the existing active library source code.

## Analysis Date
2025-09-12T07:54:06.914142

## Files Generated

- `source_structure_comparison.json` - Comparison of source file structures
- `dex_structure_analysis.json` - Analysis of the DEX file structure  
- `ghidra_analysis_report.md` - Detailed analysis report
- `README.md` - This documentation file

## Ghidra Version
Ghidra 11.4.2 PUBLIC from NSA GitHub repository

## Source Files Analyzed

### Active Library
- Path: `/home/runner/work/Linkpoint/Linkpoint/app/src/main/java`
- Contains the current working source code in the repository

### Original Decompiled Source  
- Source: `/home/runner/work/Linkpoint/Linkpoint/Lumiya_3.4.2.apk_Decompiler.com (1).zip`
- Contains decompiled source from original APK analysis

### Ghidra Decompiled Output
- Generated fresh decompilation using Ghidra headless analyzer
- Analyzed DEX file: `/tmp/lumiya_analysis/classes.dex`

## Analysis Process

1. **Ghidra Setup**: Downloaded and configured Ghidra 11.4.2 from NSA GitHub
2. **DEX Analysis**: Used Ghidra headless analyzer on classes.dex
3. **Source Extraction**: Extracted existing decompiled sources for comparison
4. **Structure Analysis**: Compared directory structures and file inventories
5. **Report Generation**: Created detailed comparison reports

## Key Findings

The analysis provides insights into:
- Consistency between decompiled and active source code
- Missing or extra files in each version  
- Structural differences in package organization
- Method and class signature validation

## Usage

To reproduce this analysis:

```bash
# Extract Lumiya APK  
unzip Lumiya_3.4.2.zip -d /tmp/lumiya_analysis

# Run Ghidra analysis
/path/to/ghidra/support/analyzeHeadless /tmp/analysis LumiyaProject -import classes.dex -overwrite

# Run comparison script
python3 scripts/ghidra_comparison.py --repo-path . --ghidra-path /path/to/ghidra --dex-path /tmp/lumiya_analysis/classes.dex
```

## References

- [Ghidra Software Reverse Engineering Framework](https://github.com/NationalSecurityAgency/ghidra)
- [Lumiya Viewer Documentation](../README.md)
- [Decompilation Analysis Report](../Dex_Extraction_Report.md)
