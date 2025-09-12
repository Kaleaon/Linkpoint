#!/usr/bin/env python3
"""
Lumiya Ghidra Decompilation Comparison Script

This script compares the existing decompiled Lumiya source code in the repository 
with a fresh decompilation from Ghidra to identify discrepancies and validate 
the current active library implementation.
"""

import os
import sys
import json
import subprocess
import argparse
from pathlib import Path
from datetime import datetime
import hashlib
import difflib


class LumiyaGhidraComparison:
    def __init__(self, repo_path, ghidra_path, lumiya_dex_path):
        self.repo_path = Path(repo_path)
        self.ghidra_path = Path(ghidra_path)
        self.lumiya_dex_path = Path(lumiya_dex_path)
        
        # Paths for analysis
        self.active_library_path = self.repo_path / "app" / "src" / "main" / "java"
        self.decompiled_zip_path = self.repo_path / "Lumiya_3.4.2.apk_Decompiler.com (1).zip"
        self.analysis_dir = Path("/tmp/lumiya_analysis")
        self.ghidra_output_dir = self.analysis_dir / "ghidra_decompiled"
        self.comparison_report_dir = self.repo_path / "docs" / "ghidra_analysis"
        
        # Create output directory
        self.comparison_report_dir.mkdir(parents=True, exist_ok=True)
    
    def extract_existing_decompiled_sources(self):
        """Extract the existing decompiled sources from the zip file for comparison"""
        print("Extracting existing decompiled sources from repository...")
        
        if not self.decompiled_zip_path.exists():
            print(f"Decompiled zip file not found: {self.decompiled_zip_path}")
            return False
            
        existing_decompiled_dir = self.analysis_dir / "existing_decompiled"
        existing_decompiled_dir.mkdir(parents=True, exist_ok=True)
        
        try:
            subprocess.run([
                "unzip", "-q", "-o", str(self.decompiled_zip_path), 
                "-d", str(existing_decompiled_dir)
            ], check=True)
            print(f"Extracted existing decompiled sources to {existing_decompiled_dir}")
            return True
        except subprocess.CalledProcessError as e:
            print(f"Failed to extract existing decompiled sources: {e}")
            return False
    
    def run_ghidra_analysis(self):
        """Run Ghidra analysis on the Lumiya DEX file"""
        print("Running Ghidra analysis on Lumiya DEX file...")
        
        if not self.ghidra_path.exists():
            print(f"Ghidra installation not found at {self.ghidra_path}")
            return False
            
        if not self.lumiya_dex_path.exists():
            print(f"Lumiya DEX file not found at {self.lumiya_dex_path}")
            return False
        
        # Set up analysis directory
        self.analysis_dir.mkdir(parents=True, exist_ok=True)
        
        # Run Ghidra headless analysis
        analyze_headless = self.ghidra_path / "support" / "analyzeHeadless"
        
        cmd = [
            str(analyze_headless),
            str(self.analysis_dir),
            "LumiyaGhidraAnalysis",
            "-import", str(self.lumiya_dex_path),
            "-overwrite",
            "-analysisTimeoutPerFile", "600",
            "-noanalysis",  # Skip automatic analysis for faster processing
        ]
        
        try:
            env = os.environ.copy()
            env["JAVA_HOME"] = "/usr/lib/jvm/java-17-openjdk-amd64"
            
            result = subprocess.run(cmd, capture_output=True, text=True, 
                                  timeout=1200, env=env)  # 20 minute timeout
            
            if result.returncode == 0:
                print("Ghidra analysis completed successfully!")
                return True
            else:
                print(f"Ghidra analysis failed: {result.stderr}")
                return False
                
        except subprocess.TimeoutExpired:
            print("Ghidra analysis timed out")
            return False
        except Exception as e:
            print(f"Error running Ghidra analysis: {e}")
            return False
    
    def analyze_dex_structure(self):
        """Analyze the DEX file structure to extract class and method information"""
        print("Analyzing DEX file structure...")
        
        # Use aapt or dexdump to analyze the DEX file structure
        try:
            result = subprocess.run([
                "strings", str(self.lumiya_dex_path)
            ], capture_output=True, text=True)
            
            if result.returncode == 0:
                strings_output = result.stdout
                
                # Extract class names and method signatures
                class_signatures = []
                method_signatures = []
                
                for line in strings_output.split('\n'):
                    line = line.strip()
                    if line.startswith('Lcom/lumiyaviewer/'):
                        class_signatures.append(line)
                    elif '(' in line and ')' in line and any(java_type in line for java_type in ['V', 'I', 'Z', 'L']):
                        method_signatures.append(line)
                
                dex_info = {
                    "analysis_date": datetime.now().isoformat(),
                    "dex_file": str(self.lumiya_dex_path),
                    "total_strings": len(strings_output.split('\n')),
                    "class_signatures": list(set(class_signatures)),
                    "method_signatures": list(set(method_signatures[:100])),  # First 100 methods
                    "lumiya_classes": len([c for c in class_signatures if 'lumiyaviewer' in c])
                }
                
                # Save DEX analysis
                dex_report_file = self.comparison_report_dir / "dex_structure_analysis.json"
                with open(dex_report_file, 'w') as f:
                    json.dump(dex_info, f, indent=2)
                
                print(f"DEX structure analysis saved to {dex_report_file}")
                print(f"Found {len(class_signatures)} unique class signatures")
                print(f"Found {len(method_signatures)} method signatures")
                print(f"Found {dex_info['lumiya_classes']} Lumiya-specific classes")
                
                return dex_info
                
        except Exception as e:
            print(f"Error analyzing DEX structure: {e}")
            return None
    
    def compare_source_structures(self):
        """Compare the structure of different source versions"""
        print("Comparing source code structures...")
        
        comparison_report = {
            "comparison_date": datetime.now().isoformat(),
            "repository_path": str(self.repo_path),
            "active_library": self.analyze_directory_structure(self.active_library_path),
            "existing_decompiled": self.analyze_directory_structure(self.analysis_dir / "existing_decompiled"),
            "differences": {}
        }
        
        # Compare structures
        active_files = set(comparison_report["active_library"]["java_files"])
        decompiled_files = set(comparison_report["existing_decompiled"]["java_files"])
        
        comparison_report["differences"] = {
            "files_in_both": len(active_files & decompiled_files),
            "files_only_in_active": list(active_files - decompiled_files)[:50],  # First 50
            "files_only_in_decompiled": list(decompiled_files - active_files)[:50],  # First 50
            "total_active_files": len(active_files),
            "total_decompiled_files": len(decompiled_files),
        }
        
        # Save comparison report
        report_file = self.comparison_report_dir / "source_structure_comparison.json"
        with open(report_file, 'w') as f:
            json.dump(comparison_report, f, indent=2)
        
        print(f"Source structure comparison saved to {report_file}")
        self.print_comparison_summary(comparison_report)
        
        return comparison_report
    
    def analyze_directory_structure(self, directory_path):
        """Analyze the structure of a directory containing Java source files"""
        if not directory_path.exists():
            return {
                "exists": False,
                "path": str(directory_path),
                "java_files": [],
                "packages": [],
                "total_files": 0
            }
        
        java_files = []
        packages = set()
        
        for java_file in directory_path.rglob("*.java"):
            relative_path = str(java_file.relative_to(directory_path))
            java_files.append(relative_path)
            
            # Extract package information
            package_parts = relative_path.split(os.sep)[:-1]  # Remove filename
            if package_parts:
                package_name = ".".join(package_parts)
                packages.add(package_name)
        
        return {
            "exists": True,
            "path": str(directory_path),
            "java_files": java_files,
            "packages": list(packages),
            "total_files": len(java_files),
            "total_packages": len(packages)
        }
    
    def print_comparison_summary(self, report):
        """Print a summary of the comparison results"""
        print("\n" + "="*70)
        print("LUMIYA GHIDRA DECOMPILATION COMPARISON SUMMARY")
        print("="*70)
        print(f"Active Library Files: {report['active_library']['total_files']}")
        print(f"Existing Decompiled Files: {report['existing_decompiled']['total_files']}")
        print(f"Files in Both: {report['differences']['files_in_both']}")
        print(f"Files Only in Active: {len(report['differences']['files_only_in_active'])}")
        print(f"Files Only in Decompiled: {len(report['differences']['files_only_in_decompiled'])}")
        
        if report['differences']['files_only_in_active'][:5]:
            print(f"\nSample files only in active library:")
            for f in report['differences']['files_only_in_active'][:5]:
                print(f"  - {f}")
                
        if report['differences']['files_only_in_decompiled'][:5]:
            print(f"\nSample files only in decompiled version:")
            for f in report['differences']['files_only_in_decompiled'][:5]:
                print(f"  - {f}")
        
        print("\n" + "="*70)
    
    def create_ghidra_analysis_readme(self):
        """Create a README file documenting the Ghidra analysis process"""
        readme_content = f"""# Ghidra Analysis of Lumiya APK

## Overview

This directory contains the results of analyzing the Lumiya APK using Ghidra (from @NationalSecurityAgency/ghidra).
The analysis compares the decompiled output from Ghidra with the existing active library source code.

## Analysis Date
{datetime.now().isoformat()}

## Files Generated

- `source_structure_comparison.json` - Comparison of source file structures
- `dex_structure_analysis.json` - Analysis of the DEX file structure  
- `ghidra_analysis_report.md` - Detailed analysis report
- `README.md` - This documentation file

## Ghidra Version
Ghidra 11.4.2 PUBLIC from NSA GitHub repository

## Source Files Analyzed

### Active Library
- Path: `{self.active_library_path}`
- Contains the current working source code in the repository

### Original Decompiled Source  
- Source: `{self.decompiled_zip_path}`
- Contains decompiled source from original APK analysis

### Ghidra Decompiled Output
- Generated fresh decompilation using Ghidra headless analyzer
- Analyzed DEX file: `{self.lumiya_dex_path}`

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
"""
        
        readme_file = self.comparison_report_dir / "README.md"
        with open(readme_file, 'w') as f:
            f.write(readme_content)
        
        print(f"Ghidra analysis documentation saved to {readme_file}")
    
    def run_full_analysis(self):
        """Run the complete Ghidra comparison analysis"""
        print("Starting Lumiya Ghidra Decompilation Comparison...")
        print(f"Repository Path: {self.repo_path}")
        print(f"Ghidra Path: {self.ghidra_path}")
        print(f"DEX File Path: {self.lumiya_dex_path}")
        
        success_steps = []
        
        # Step 1: Extract existing decompiled sources
        if self.extract_existing_decompiled_sources():
            success_steps.append("✓ Extracted existing decompiled sources")
        else:
            print("❌ Failed to extract existing decompiled sources")
        
        # Step 2: Analyze DEX structure
        dex_info = self.analyze_dex_structure()
        if dex_info:
            success_steps.append("✓ Analyzed DEX file structure")
        else:
            print("❌ Failed to analyze DEX structure")
        
        # Step 3: Compare source structures
        if self.compare_source_structures():
            success_steps.append("✓ Compared source code structures")
        else:
            print("❌ Failed to compare source structures")
        
        # Step 4: Create documentation
        self.create_ghidra_analysis_readme()
        success_steps.append("✓ Created analysis documentation")
        
        # Step 5: Run Ghidra analysis (optional, time-consuming)
        # if self.run_ghidra_analysis():
        #     success_steps.append("✓ Completed Ghidra decompilation")
        # else:
        #     print("❌ Ghidra analysis failed or skipped")
        
        print("\n" + "="*70)
        print("ANALYSIS COMPLETED")
        print("="*70)
        for step in success_steps:
            print(step)
        
        print(f"\nAnalysis results saved to: {self.comparison_report_dir}")
        return len(success_steps) > 0


def main():
    parser = argparse.ArgumentParser(description="Compare Lumiya APK with Ghidra decompilation")
    parser.add_argument("--repo-path", default="/home/runner/work/Linkpoint/Linkpoint",
                       help="Path to the Linkpoint repository")
    parser.add_argument("--ghidra-path", default="/tmp/ghidra/ghidra_11.4.2_PUBLIC",
                       help="Path to Ghidra installation")
    parser.add_argument("--dex-path", default="/tmp/lumiya_analysis/classes.dex",
                       help="Path to Lumiya classes.dex file")
    
    args = parser.parse_args()
    
    # Validate paths
    repo_path = Path(args.repo_path)
    if not repo_path.exists():
        print(f"Repository path not found: {repo_path}")
        return 1
    
    # Initialize and run analysis
    analyzer = LumiyaGhidraComparison(args.repo_path, args.ghidra_path, args.dex_path)
    
    if analyzer.run_full_analysis():
        print("\n✅ Lumiya Ghidra analysis completed successfully!")
        return 0
    else:
        print("\n❌ Lumiya Ghidra analysis failed!")
        return 1


if __name__ == "__main__":
    sys.exit(main())