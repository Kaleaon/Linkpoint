#!/usr/bin/env python3
"""
Batch Java to Kotlin Converter
Processes all remaining Java files in the project
"""

import os
import sys
import subprocess
from pathlib import Path
from concurrent.futures import ThreadPoolExecutor, as_completed
import time

class BatchConverter:
    """Batch conversion manager"""
    
    def __init__(self, workspace_dir: str):
        self.workspace_dir = Path(workspace_dir)
        self.java_dir = self.workspace_dir / "app/src/main/java"
        self.backup_dir = self.workspace_dir / "app/src/main/java.backup"
        self.converter_script = self.workspace_dir / "scripts/advanced_java2kotlin.py"
        self.ktlint = self.workspace_dir / "tools/ktlint"
        
        self.total_files = 0
        self.converted_files = 0
        self.failed_files = []
        self.skipped_files = []
        
    def find_java_files(self):
        """Find all Java files that need conversion"""
        java_files = []
        
        for java_file in self.java_dir.rglob("*.java"):
            # Check if Kotlin version already exists
            kotlin_file = java_file.with_suffix('.kt')
            
            if not kotlin_file.exists():
                java_files.append(java_file)
            else:
                self.skipped_files.append(str(java_file))
        
        return java_files
    
    def convert_file(self, java_file: Path) -> tuple:
        """Convert a single file"""
        kotlin_file = java_file.with_suffix('.kt')
        
        try:
            # Run converter
            result = subprocess.run(
                [sys.executable, str(self.converter_script), str(java_file), str(kotlin_file)],
                capture_output=True,
                text=True,
                timeout=30
            )
            
            if result.returncode == 0:
                # Format with ktlint
                subprocess.run(
                    [str(self.ktlint), "--format", str(kotlin_file)],
                    capture_output=True,
                    timeout=10
                )
                
                # Delete Java file after successful conversion
                java_file.unlink()
                
                return (True, str(java_file), None)
            else:
                return (False, str(java_file), result.stderr)
                
        except Exception as e:
            return (False, str(java_file), str(e))
    
    def convert_batch(self, java_files: list, max_workers: int = 4):
        """Convert files in parallel"""
        print(f"\n🚀 Starting batch conversion of {len(java_files)} files...")
        print(f"Using {max_workers} parallel workers\n")
        
        start_time = time.time()
        
        with ThreadPoolExecutor(max_workers=max_workers) as executor:
            futures = {executor.submit(self.convert_file, f): f for f in java_files}
            
            for future in as_completed(futures):
                success, file_path, error = future.result()
                
                if success:
                    self.converted_files += 1
                    print(f"✓ [{self.converted_files}/{len(java_files)}] {Path(file_path).name}")
                else:
                    self.failed_files.append((file_path, error))
                    print(f"✗ [{self.converted_files}/{len(java_files)}] {Path(file_path).name} - {error}")
        
        elapsed_time = time.time() - start_time
        
        print(f"\n{'='*60}")
        print(f"Conversion Complete!")
        print(f"{'='*60}")
        print(f"Total files processed: {len(java_files)}")
        print(f"Successfully converted: {self.converted_files}")
        print(f"Failed: {len(self.failed_files)}")
        print(f"Skipped (already converted): {len(self.skipped_files)}")
        print(f"Time elapsed: {elapsed_time:.2f} seconds")
        print(f"Average: {elapsed_time/len(java_files):.2f} seconds per file")
        
        if self.failed_files:
            print(f"\n❌ Failed conversions:")
            for file_path, error in self.failed_files[:10]:  # Show first 10
                print(f"  - {Path(file_path).name}: {error[:100]}")
            if len(self.failed_files) > 10:
                print(f"  ... and {len(self.failed_files) - 10} more")
    
    def run(self):
        """Run the batch conversion"""
        print("="*60)
        print("Linkpoint Java to Kotlin Batch Converter")
        print("="*60)
        
        # Find files
        print("\n📁 Scanning for Java files...")
        java_files = self.find_java_files()
        
        if not java_files:
            print("✓ No Java files need conversion. All done!")
            return
        
        print(f"Found {len(java_files)} Java files to convert")
        print(f"Skipped {len(self.skipped_files)} files (already converted)")
        
        # Confirm
        response = input(f"\nConvert {len(java_files)} files? (yes/no): ")
        if response.lower() not in ['yes', 'y']:
            print("Cancelled.")
            return
        
        # Convert
        self.convert_batch(java_files)
        
        # Save report
        self.save_report()
    
    def save_report(self):
        """Save conversion report"""
        report_file = self.workspace_dir / "CONVERSION_REPORT.md"
        
        with open(report_file, 'w') as f:
            f.write("# Batch Conversion Report\n\n")
            f.write(f"**Date:** {time.strftime('%Y-%m-%d %H:%M:%S')}\n\n")
            f.write(f"## Summary\n\n")
            f.write(f"- Total files processed: {self.converted_files + len(self.failed_files)}\n")
            f.write(f"- Successfully converted: {self.converted_files}\n")
            f.write(f"- Failed: {len(self.failed_files)}\n")
            f.write(f"- Skipped: {len(self.skipped_files)}\n\n")
            
            if self.failed_files:
                f.write(f"## Failed Conversions\n\n")
                for file_path, error in self.failed_files:
                    f.write(f"### {Path(file_path).name}\n")
                    f.write(f"**Path:** `{file_path}`\n\n")
                    f.write(f"**Error:**\n```\n{error}\n```\n\n")
        
        print(f"\n📄 Report saved to: {report_file}")

def main():
    if len(sys.argv) < 2:
        print("Usage: batch_convert_all.py <workspace_dir>")
        sys.exit(1)
    
    workspace_dir = sys.argv[1]
    
    if not os.path.exists(workspace_dir):
        print(f"Error: Directory not found: {workspace_dir}")
        sys.exit(1)
    
    converter = BatchConverter(workspace_dir)
    converter.run()

if __name__ == '__main__':
    main()