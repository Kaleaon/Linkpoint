# Ghidra Decompilation Guide for Lumiya APKs

## Overview

This guide provides step-by-step instructions for properly decompiling the Lumiya APK files using Ghidra, following best practices from the comprehensive guide at https://remyhax.xyz/posts/android-with-ghidra/.

## Why This Guide Matters

The referenced guide highlights critical workflow issues that many users encounter when decompiling Android APKs with Ghidra. The most important insight is:

> **The singular core mistake I see so many people do with Android RE in Ghidra** is importing APKs incorrectly, which causes them to miss entire DEX files (like classes2.dex, classes3.dex, etc.).

This guide ensures you avoid these pitfalls and get complete, accurate decompilation results.

## Quick Start (Automated)

For automated decompilation of all Lumiya APKs:

```bash
# Run the automated decompilation script
./scripts/decompile_lumiya_with_ghidra.sh
```

This script will:
1. Check and install prerequisites (including Ghidra if needed)
2. Process all APK files in the `Lumiya/` directory
3. Follow the proper multi-DEX workflow
4. Generate comprehensive analysis reports
5. Create a summary of all decompiled APKs

## Manual Decompilation (GUI Method)

If you prefer to use the Ghidra GUI for interactive analysis, follow these steps carefully:

### Prerequisites

1. **Ghidra 11.4.2+** - Download from https://github.com/NationalSecurityAgency/ghidra/releases
2. **Java 17+** - Required to run Ghidra
3. **Lumiya APK Files** - Located in `Lumiya/` directory

### Step 1: Launch Ghidra Code Browser

**IMPORTANT**: Do NOT import the APK from Ghidra's main screen. This is a common mistake.

✅ **DO**: Launch the Code Browser directly
- Open Ghidra
- Click the "Dragon" icon to launch Code Browser
- Or: File > New Project, then click the Code Browser icon

❌ **DON'T**: Use File > Import File from the main Ghidra window

### Step 2: Import APK File

From within the Code Browser:

1. Go to **File > Import File...**
2. Select your Lumiya APK file (e.g., `Lumiya_3.4.2.apk`)
3. Set Import mode to **"Single file"**
4. Ghidra should auto-detect the format as **"Android APK"**
5. Click **OK**

### Step 3: DO NOT Analyze Yet!

When prompted "Would you like to analyze now?":

❌ **Click "No"**

This is critical! If you click "Yes", you'll only get `classes.dex` analyzed and miss `classes2.dex` and any other DEX files.

### Step 4: Set Up External Name Associations

This is the key step that most people skip:

1. Go to **Window > External Programs**
2. You'll see entries for all DEX files (classes.dex, classes2.dex, etc.)
3. For each DEX file:
   - Right-click on the entry
   - Select **"Set External Name Association"**
   - Point it to itself (e.g., classes2.dex → classes2.dex)
   
This seems redundant, but it's necessary for Ghidra to properly resolve paths when working with APK archives.

### Step 5: Verify All DEX Files Are Listed

1. Go to **Window > Listings**
2. You should see all DEX files listed (classes.dex, classes2.dex, etc.)
3. If any are missing:
   - Go to **File > Open**
   - Select the missing DEX file
   - Repeat Step 4 for the newly opened file

### Step 6: NOW Analyze!

With all DEX files properly set up:

1. Go to **Analysis > Analyze All Open...**
2. In the Analysis Options dialog, ensure these are enabled:
   - ✅ **Decompiler Parameter ID**
   - ✅ **External Entry Points**
   - ✅ **Non-Returning Functions - Discovered**
   - ✅ **Android DEX Cross-References** (if available)
3. Click **Analyze**

Ghidra will now properly analyze all DEX files with cross-references working correctly between them.

### Step 7: Verify Cross-DEX XREFs Work

After analysis completes:

1. Navigate to a class in classes.dex
2. Look for references to classes in classes2.dex
3. Double-click on an external reference
4. You should automatically jump to the correct location in classes2.dex

If this works, your setup is correct!

## Understanding the Analysis Output

### Window Layout Recommendation

The guide recommends this layout for effective Android RE:

**Top Row:**
- **Listing** - Shows the bytecode/assembly
- **Decompile** - Shows Java-like pseudo-code
- **Function Call Graph** - Visualizes call relationships

**Bottom Row:**
- **References** - Shows XREFs to/from current location
- **Function Call Tree** - Shows complete call hierarchy

### Key Features Available

With proper setup, you get:

✅ **Cross-DEX XREFs** - References work across all DEX files
✅ **Control Flow Graphs** - Complete CFG for DEX bytecode
✅ **Function Call Graphs** - Full call hierarchies
✅ **Decompilation** - Java-like code from Dalvik bytecode
✅ **Symbol Resolution** - Proper naming across all DEX files

## Common Issues and Solutions

### Issue: Only classes.dex appears in Listings

**Cause**: You analyzed immediately without setting up External Name Associations

**Solution**: 
1. Close the current analysis
2. Re-import the APK following Steps 1-6 carefully
3. Do NOT skip the External Name Association step

### Issue: XREFs don't work between DEX files

**Cause**: External Name Associations not properly configured

**Solution**:
1. Go to Window > External Programs
2. Verify each DEX file has its External Name Association set
3. Re-run "Analyze All Open"

### Issue: Ghidra crashes or hangs during analysis

**Cause**: Insufficient memory or timeout issues

**Solution**:
1. Increase Ghidra's max memory in `ghidraRun` script
2. Use longer analysis timeout: `-analysisTimeoutPerFile 1200`
3. For headless mode, use `-max-cpu` to limit CPU cores

## Lumiya APK Files in This Repository

The `Lumiya/` directory contains:

1. **Lumiya_3.4.2.apk** - Main Lumiya viewer application
2. **Lumiya Cloud Plugin_1.0.apk** - Cloud functionality plugin
3. **Lumiya Voice Plugin_1.4.apk** - Voice chat plugin

Each APK should be analyzed separately following this guide.

## Analysis Reports

After running the automated script or manual analysis, reports are generated in:

```
docs/ghidra_analysis/
├── LUMIYA_APKS_DECOMPILATION_SUMMARY.md
├── Lumiya_3.4.2_analysis_report.md
├── Lumiya Cloud Plugin_1.0_analysis_report.md
└── Lumiya Voice Plugin_1.4_analysis_report.md
```

Each report contains:
- DEX file structure analysis
- Class and method counts
- Native library information
- Decompilation workflow details
- References to generated artifacts

## Advanced Analysis

### Native Library Analysis

If the APK contains native libraries (`.so` files):

1. Locate them in the APK: `lib/[arch]/lib*.so`
2. Import them separately into Ghidra
3. Set up External Program references between DEX and native code
4. Analyze JNI function calls to link Java and native code

### Scripting and Automation

Ghidra supports Python and Java scripting for automated analysis:

```python
# Example: List all classes in DEX file
from ghidra.program.model.symbol import *

symbol_table = currentProgram.getSymbolTable()
for symbol in symbol_table.getAllSymbols(True):
    if symbol.getSymbolType() == SymbolType.CLASS:
        print(f"Class: {symbol.getName()}")
```

### Headless Analysis

For batch processing, use Ghidra's headless mode:

```bash
$GHIDRA_HOME/support/analyzeHeadless \
    /tmp/ghidra_projects \
    LumiyaProject \
    -import Lumiya_3.4.2.apk \
    -overwrite \
    -analysisTimeoutPerFile 1200 \
    -max-cpu 4
```

## Best Practices Summary

Following the guide from https://remyhax.xyz/posts/android-with-ghidra/:

1. ✅ **Launch Code Browser directly** - Don't import from main screen
2. ✅ **Use "Single file" import** - Proper APK handling
3. ✅ **Say NO to immediate analysis** - Critical step!
4. ✅ **Set External Name Associations** - For all DEX files
5. ✅ **Verify all DEX files in Listings** - Before analyzing
6. ✅ **Use "Analyze All Open"** - For multi-DEX analysis
7. ✅ **Check XREFs work** - Verify cross-DEX references

## Resources

- **Ghidra Official**: https://github.com/NationalSecurityAgency/ghidra
- **Analysis Guide**: https://remyhax.xyz/posts/android-with-ghidra/
- **DEX Format**: https://source.android.com/devices/tech/dalvik/dex-format
- **APK Structure**: https://developer.android.com/guide/components/fundamentals
- **Ghidra Documentation**: https://ghidra-sre.org/

## Troubleshooting

For issues not covered here:

1. Check Ghidra's log file: `~/.ghidra/.ghidra_11.4.2_PUBLIC/application.log`
2. Verify Java version: `java -version` (needs 17+)
3. Check memory settings in Ghidra launch scripts
4. Review the guide: https://remyhax.xyz/posts/android-with-ghidra/
5. Consult Ghidra GitHub issues: https://github.com/NationalSecurityAgency/ghidra/issues

---

*This guide ensures proper decompilation of Android APKs following industry best practices for reverse engineering with Ghidra.*
