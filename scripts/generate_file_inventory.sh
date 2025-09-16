#!/bin/bash

# File Inventory Generator for Linkpoint Repository
# Generates comprehensive listings of all files for automated copilot review

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[0;33m'
NC='\033[0m' # No Color

PROJECT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
OUTPUT_DIR="$PROJECT_ROOT/file_inventory"
TIMESTAMP=$(date +"%Y%m%d_%H%M%S")

echo -e "${BLUE}Linkpoint File Inventory Generator${NC}"
echo -e "${BLUE}===================================${NC}"
echo "Project Root: $PROJECT_ROOT"
echo "Output Directory: $OUTPUT_DIR"
echo "Timestamp: $TIMESTAMP"
echo

# Create output directory
mkdir -p "$OUTPUT_DIR"

# Function to count files by extension
count_by_extension() {
    echo "Analyzing file types..."
    find "$PROJECT_ROOT" -type f -not -path "*/.*" -not -path "*/file_inventory/*" | \
        sed 's/.*\.//' | \
        sort | uniq -c | sort -nr > "$OUTPUT_DIR/file_types_count.txt"
}

# Function to generate comprehensive file list
generate_comprehensive_list() {
    echo "Generating comprehensive file list..."
    find "$PROJECT_ROOT" -type f -not -path "*/.*" -not -path "*/file_inventory/*" | \
        sort > "$OUTPUT_DIR/all_files.txt"
}

# Function to categorize files
categorize_files() {
    echo "Categorizing files..."
    
    # Java source files
    find "$PROJECT_ROOT" -name "*.java" -type f | sort > "$OUTPUT_DIR/java_files.txt"
    
    # Kotlin files (if any)
    find "$PROJECT_ROOT" -name "*.kt" -type f | sort > "$OUTPUT_DIR/kotlin_files.txt"
    
    # C++ source files
    find "$PROJECT_ROOT" \( -name "*.cpp" -o -name "*.h" -o -name "*.hpp" -o -name "*.c" \) -type f | sort > "$OUTPUT_DIR/cpp_files.txt"
    
    # Configuration files
    find "$PROJECT_ROOT" \( -name "*.gradle" -o -name "*.properties" -o -name "*.xml" -o -name "*.json" \) -type f | sort > "$OUTPUT_DIR/config_files.txt"
    
    # Documentation files
    find "$PROJECT_ROOT" \( -name "*.md" -o -name "*.txt" -o -name "README*" -o -name "CHANGELOG*" \) -type f | sort > "$OUTPUT_DIR/documentation_files.txt"
    
    # Script files
    find "$PROJECT_ROOT" \( -name "*.sh" -o -name "*.py" -o -name "*.pl" \) -type f | sort > "$OUTPUT_DIR/script_files.txt"
    
    # Android resource files
    find "$PROJECT_ROOT" -path "*/res/*" -name "*.xml" -type f | sort > "$OUTPUT_DIR/android_resource_files.txt"
    
    # Binary/Asset files
    find "$PROJECT_ROOT" \( -name "*.apk" -o -name "*.jar" -o -name "*.so" -o -name "*.zip" -o -name "*.png" -o -name "*.jpg" -o -name "*.jpeg" \) -type f | sort > "$OUTPUT_DIR/binary_asset_files.txt"
}

# Function to generate detailed file information
generate_detailed_info() {
    echo "Generating detailed file information..."
    
    # File with metadata
    find "$PROJECT_ROOT" -type f -not -path "*/.*" -not -path "*/file_inventory/*" -exec ls -la {} \; | \
        awk '{print $9 "|" $5 "|" $6 " " $7 " " $8}' | \
        sort > "$OUTPUT_DIR/files_with_metadata.txt"
    
    # Large files (>1MB)
    find "$PROJECT_ROOT" -type f -not -path "*/.*" -not -path "*/file_inventory/*" -size +1M -exec ls -lh {} \; | \
        awk '{print $9 "|" $5}' | \
        sort > "$OUTPUT_DIR/large_files.txt"
}

# Function to analyze directory structure
analyze_directory_structure() {
    echo "Analyzing directory structure..."
    
    # Directory tree
    tree "$PROJECT_ROOT" -I ".git|file_inventory" > "$OUTPUT_DIR/directory_tree.txt" 2>/dev/null || \
        find "$PROJECT_ROOT" -type d -not -path "*/.*" -not -path "*/file_inventory/*" | sort > "$OUTPUT_DIR/directories.txt"
    
    # File count per directory
    find "$PROJECT_ROOT" -type d -not -path "*/.*" -not -path "*/file_inventory/*" | while read dir; do
        count=$(find "$dir" -maxdepth 1 -type f | wc -l)
        echo "$dir|$count"
    done | sort -t'|' -k2 -nr > "$OUTPUT_DIR/files_per_directory.txt"
}

# Function to generate review-ready summaries
generate_review_summaries() {
    echo "Generating review summaries..."
    
    cat > "$OUTPUT_DIR/inventory_summary.md" << EOF
# Linkpoint File Inventory Summary
Generated: $(date)

## Repository Statistics
- **Total Files**: $(cat "$OUTPUT_DIR/all_files.txt" | wc -l)
- **Total Java Files**: $(cat "$OUTPUT_DIR/java_files.txt" | wc -l)
- **Total Documentation Files**: $(cat "$OUTPUT_DIR/documentation_files.txt" | wc -l)
- **Total Configuration Files**: $(cat "$OUTPUT_DIR/config_files.txt" | wc -l)
- **Total Script Files**: $(cat "$OUTPUT_DIR/script_files.txt" | wc -l)

## File Types Distribution
\`\`\`
$(head -20 "$OUTPUT_DIR/file_types_count.txt")
\`\`\`

## Largest Files
\`\`\`
$(head -10 "$OUTPUT_DIR/large_files.txt")
\`\`\`

## Key Directories with File Counts
\`\`\`
$(head -20 "$OUTPUT_DIR/files_per_directory.txt")
\`\`\`

## Files by Category

### Java Source Files ($(cat "$OUTPUT_DIR/java_files.txt" | wc -l) total)
For detailed list see: [java_files.txt](java_files.txt)

### Documentation Files ($(cat "$OUTPUT_DIR/documentation_files.txt" | wc -l) total)  
For detailed list see: [documentation_files.txt](documentation_files.txt)

### Configuration Files ($(cat "$OUTPUT_DIR/config_files.txt" | wc -l) total)
For detailed list see: [config_files.txt](config_files.txt)

### Android Resource Files ($(cat "$OUTPUT_DIR/android_resource_files.txt" | wc -l) total)
For detailed list see: [android_resource_files.txt](android_resource_files.txt)

### Script Files ($(cat "$OUTPUT_DIR/script_files.txt" | wc -l) total)
For detailed list see: [script_files.txt](script_files.txt)

## Review Recommendations

### High Priority for Review
1. **Java Source Files** - Core application logic and business rules
2. **Configuration Files** - Build settings, dependencies, and app configuration  
3. **Android Manifests and Resources** - UI definitions and app permissions
4. **Documentation** - Project guides and API documentation

### Medium Priority for Review  
1. **Script Files** - Build automation and tooling
2. **C++ Source Files** - Native components and performance-critical code

### Low Priority for Review
1. **Binary Assets** - APK files, JARs, and media assets
2. **Generated Files** - Build outputs and temporary files

## Automated Review Suggestions

### Code Quality Focus Areas
- Java files in \`app/src/main/java/com/lumiyaviewer/\` (core application code)
- Build configuration in \`build.gradle\` and \`app/build.gradle\`
- Android manifest and resource definitions
- Documentation completeness and accuracy

### Security Review Areas  
- Network communication code
- Authentication and authorization logic
- File I/O and data storage implementations
- Native JNI interfaces

### Performance Review Areas
- Memory management in caching systems
- Graphics rendering pipeline efficiency  
- Network protocol optimization
- Resource loading and asset management
EOF
}

# Function to create copilot-ready file lists
generate_copilot_lists() {
    echo "Generating copilot-ready file lists..."
    
    # Create individual file review checklist
    echo "# Linkpoint Files for Automated Review" > "$OUTPUT_DIR/copilot_review_list.md"
    echo "Generated: $(date)" >> "$OUTPUT_DIR/copilot_review_list.md"
    echo "" >> "$OUTPUT_DIR/copilot_review_list.md"
    echo "## All Files to Review ($(cat "$OUTPUT_DIR/all_files.txt" | wc -l) total)" >> "$OUTPUT_DIR/copilot_review_list.md"
    echo "" >> "$OUTPUT_DIR/copilot_review_list.md"
    
    # Add each file as a checkbox item
    cat "$OUTPUT_DIR/all_files.txt" | while read file; do
        relative_path=${file#$PROJECT_ROOT/}
        file_size=$(stat -f%z "$file" 2>/dev/null || stat -c%s "$file" 2>/dev/null || echo "0")
        echo "- [ ] \`$relative_path\` (${file_size} bytes)" >> "$OUTPUT_DIR/copilot_review_list.md"
    done
}

# Main execution
echo -e "${GREEN}Starting file inventory generation...${NC}"

count_by_extension
generate_comprehensive_list  
categorize_files
generate_detailed_info
analyze_directory_structure
generate_review_summaries
generate_copilot_lists

echo -e "${GREEN}File inventory generation complete!${NC}"
echo -e "${YELLOW}Output files generated in: $OUTPUT_DIR${NC}"
echo
echo "Generated files:"
ls -la "$OUTPUT_DIR/"

echo
echo -e "${BLUE}Summary:${NC}"
echo "Total files found: $(cat "$OUTPUT_DIR/all_files.txt" | wc -l)"
echo "Java files: $(cat "$OUTPUT_DIR/java_files.txt" | wc -l)"
echo "Documentation files: $(cat "$OUTPUT_DIR/documentation_files.txt" | wc -l)"
echo "Configuration files: $(cat "$OUTPUT_DIR/config_files.txt" | wc -l)"
echo
echo -e "${GREEN}Review the files in $OUTPUT_DIR/ for comprehensive file listings!${NC}"