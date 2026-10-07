#!/usr/bin/env python3
import glob
import os
import sys

def main():
    repo_dir = os.path.abspath(os.path.join(os.path.dirname(__file__), ".."))
    workflows_dir = os.path.join(repo_dir, ".github", "workflows")
    
    if not os.path.exists(workflows_dir):
        print(f"Error: Workflows directory not found at {workflows_dir}")
        sys.exit(1)

    violations = []
    workflow_files = glob.glob(os.path.join(workflows_dir, "*.yml")) + glob.glob(os.path.join(workflows_dir, "*.yaml"))
    
    for path in sorted(workflow_files):
        with open(path, "r", encoding="utf-8") as f:
            content = f.read()
        
        lines = content.splitlines()
        in_run = False
        run_indent = 0
        filename = os.path.basename(path)
        
        for i, line in enumerate(lines, 1):
            stripped = line.lstrip()
            indent = len(line) - len(stripped)
            
            if stripped.startswith("run:"):
                in_run = True
                run_indent = indent
                if "${{" in line:
                    violations.append((filename, i, line.strip()))
                continue
            
            if in_run:
                if stripped and indent <= run_indent and not stripped.startswith("#"):
                    in_run = False
                else:
                    if "${{" in line:
                        violations.append((filename, i, line.strip()))

    if violations:
        print("❌ Found inline expression violations in runner execution blocks:")
        for f, line_num, text in violations:
            print(f"  {f}:{line_num}: {text}")
        sys.exit(1)
    else:
        print("✅ SUCCESS: Zero direct expression expansions found in runner execution blocks across all workflows!")
        sys.exit(0)

if __name__ == "__main__":
    main()
