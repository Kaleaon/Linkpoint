#!/bin/bash

# Individual File Review Generator for Copilot
# Creates individual "issues" or review items for each file in the repository
# This addresses the requirement to "List every file in Linkpoint as its own issue"

set -e

# Colors
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[0;33m'
NC='\033[0m'

PROJECT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
OUTPUT_DIR="$PROJECT_ROOT/copilot_review_files"
TIMESTAMP=$(date +"%Y%m%d_%H%M%S")

echo -e "${BLUE}Individual File Review Generator for Copilot${NC}"
echo -e "${BLUE}=============================================${NC}"
echo "Project Root: $PROJECT_ROOT"
echo "Output Directory: $OUTPUT_DIR"
echo "Timestamp: $TIMESTAMP"
echo

# Create output directory
mkdir -p "$OUTPUT_DIR"

# Function to determine file category and priority
get_file_category() {
    local file="$1"
    local basename=$(basename "$file")
    local extension="${basename##*.}"
    
    case "$extension" in
        java) echo "HIGH_PRIORITY_SOURCE" ;;
        kt) echo "HIGH_PRIORITY_SOURCE" ;;
        cpp|h|hpp|c) echo "MEDIUM_PRIORITY_NATIVE" ;;
        xml) 
            if [[ "$file" == *"/res/"* ]]; then
                echo "HIGH_PRIORITY_ANDROID_RESOURCE"
            elif [[ "$basename" == "AndroidManifest.xml" ]]; then
                echo "CRITICAL_ANDROID_MANIFEST"
            else
                echo "MEDIUM_PRIORITY_CONFIG"
            fi
            ;;
        gradle) echo "HIGH_PRIORITY_BUILD_CONFIG" ;;
        properties) echo "MEDIUM_PRIORITY_CONFIG" ;;
        md) echo "MEDIUM_PRIORITY_DOCUMENTATION" ;;
        sh|py|pl) echo "MEDIUM_PRIORITY_SCRIPT" ;;
        json) echo "MEDIUM_PRIORITY_CONFIG" ;;
        png|jpg|jpeg|tga|lbm) echo "LOW_PRIORITY_ASSET" ;;
        apk|jar|zip) echo "LOW_PRIORITY_BINARY" ;;
        *) echo "LOW_PRIORITY_OTHER" ;;
    esac
}

# Function to generate review complexity estimate
get_complexity_estimate() {
    local file="$1"
    local file_size=$(stat -f%z "$file" 2>/dev/null || stat -c%s "$file" 2>/dev/null || echo "0")
    local lines=0
    
    # Try to count lines for text files
    if file "$file" | grep -q text; then
        lines=$(wc -l < "$file" 2>/dev/null || echo "0")
    fi
    
    if [ "$lines" -gt 1000 ] || [ "$file_size" -gt 50000 ]; then
        echo "HIGH"
    elif [ "$lines" -gt 200 ] || [ "$file_size" -gt 10000 ]; then
        echo "MEDIUM"
    else
        echo "LOW"
    fi
}

# Function to generate review focus areas for a file
get_review_focus() {
    local file="$1"
    local basename=$(basename "$file")
    local extension="${basename##*.}"
    
    case "$extension" in
        java)
            if [[ "$file" == *"/test/"* ]]; then
                echo "Test Coverage, Test Quality, Mock Usage"
            elif [[ "$file" == *"/slproto/"* ]]; then
                echo "Protocol Implementation, Network Security, Error Handling"
            elif [[ "$file" == *"/render/"* ]]; then
                echo "Performance, Memory Management, Graphics Optimization"
            elif [[ "$file" == *"/ui/"* ]]; then
                echo "User Experience, Accessibility, UI Performance"
            else
                echo "Code Quality, Design Patterns, Error Handling"
            fi
            ;;
        xml)
            if [[ "$file" == *"/layout/"* ]]; then
                echo "UI Design, Accessibility, Performance"
            elif [[ "$file" == *"/values/"* ]]; then
                echo "Resource Management, Localization, Consistency"
            elif [[ "$basename" == "AndroidManifest.xml" ]]; then
                echo "Security Permissions, App Configuration, Compatibility"
            else
                echo "Configuration Correctness, Schema Compliance"
            fi
            ;;
        gradle) echo "Build Configuration, Dependencies, Security" ;;
        md) echo "Documentation Quality, Completeness, Accuracy" ;;
        sh) echo "Script Security, Error Handling, Portability" ;;
        *) echo "General Code Quality, Standards Compliance" ;;
    esac
}

# Function to generate individual file review items
generate_individual_reviews() {
    echo "Generating individual file review items..."
    
    # Create master review list
    echo "# Linkpoint Individual File Reviews" > "$OUTPUT_DIR/master_review_list.md"
    echo "Generated: $(date)" >> "$OUTPUT_DIR/master_review_list.md"
    echo "" >> "$OUTPUT_DIR/master_review_list.md"
    echo "This document lists every file in the Linkpoint repository as an individual review item for automated copilot analysis." >> "$OUTPUT_DIR/master_review_list.md"
    echo "" >> "$OUTPUT_DIR/master_review_list.md"
    
    # Counters
    local total_files=0
    local critical_count=0
    local high_count=0
    local medium_count=0
    local low_count=0
    
    # Process each file
    find "$PROJECT_ROOT" -type f -not -path "*/.*" -not -path "*/file_inventory/*" -not -path "*/copilot_review_files/*" | sort | while read file; do
        total_files=$((total_files + 1))
        
        relative_path=${file#$PROJECT_ROOT/}
        file_size=$(stat -f%z "$file" 2>/dev/null || stat -c%s "$file" 2>/dev/null || echo "0")
        category=$(get_file_category "$file")
        complexity=$(get_complexity_estimate "$file")
        focus_areas=$(get_review_focus "$file")
        
        # Update counters based on category
        case "$category" in
            CRITICAL_*) critical_count=$((critical_count + 1)) ;;
            HIGH_*) high_count=$((high_count + 1)) ;;
            MEDIUM_*) medium_count=$((medium_count + 1)) ;;
            LOW_*) low_count=$((low_count + 1)) ;;
        esac
        
        # Create individual review file
        review_file="$OUTPUT_DIR/review_$(echo "$relative_path" | sed 's/[^a-zA-Z0-9._-]/_/g').md"
        
        cat > "$review_file" << EOF
# File Review: $relative_path

**Generated:** $(date)  
**Category:** $category  
**Complexity:** $complexity  
**Size:** $file_size bytes

## Review Checklist

- [ ] **Code Quality**: Review for coding standards, best practices, and maintainability
- [ ] **Security**: Check for security vulnerabilities and sensitive data exposure  
- [ ] **Performance**: Analyze for performance bottlenecks and optimization opportunities
- [ ] **Documentation**: Verify code comments and documentation quality
- [ ] **Testing**: Assess test coverage and test quality (if applicable)

## Specific Focus Areas
$focus_areas

## File Information
- **Full Path:** \`$file\`
- **Relative Path:** \`$relative_path\`
- **File Size:** $file_size bytes
- **Review Priority:** $category
- **Estimated Complexity:** $complexity

## Automated Analysis Suggestions

### For Source Code Files
- Check for proper error handling and exception management
- Verify null safety and defensive programming practices
- Review for memory leaks and resource management
- Assess algorithm efficiency and data structure usage

### For Configuration Files  
- Validate configuration syntax and schema compliance
- Check for hardcoded secrets or credentials
- Verify environment-specific settings are properly externalized
- Review dependency versions for known vulnerabilities

### For Documentation Files
- Assess completeness and accuracy of information
- Check for outdated references or broken links  
- Verify examples are working and up-to-date
- Review for clarity and readability

### For Resource Files
- Validate resource naming conventions
- Check for unused or duplicate resources
- Verify accessibility compliance for UI resources
- Review for proper localization support

## Review Status
- [ ] Initial Review Completed
- [ ] Security Review Completed  
- [ ] Performance Review Completed
- [ ] Final Approval

## Notes
_Add review notes and findings here_

---
**Review File Generated by Linkpoint File Inventory System**
EOF

        # Add to master list
        echo "## File $(($total_files)): \`$relative_path\`" >> "$OUTPUT_DIR/master_review_list.md"
        echo "" >> "$OUTPUT_DIR/master_review_list.md"
        echo "- **Category:** $category" >> "$OUTPUT_DIR/master_review_list.md"
        echo "- **Complexity:** $complexity" >> "$OUTPUT_DIR/master_review_list.md"
        echo "- **Size:** $file_size bytes" >> "$OUTPUT_DIR/master_review_list.md"
        echo "- **Focus Areas:** $focus_areas" >> "$OUTPUT_DIR/master_review_list.md"
        echo "- **Review File:** [$(basename "$review_file")]($(basename "$review_file"))" >> "$OUTPUT_DIR/master_review_list.md"
        echo "" >> "$OUTPUT_DIR/master_review_list.md"
        echo "**Review Checklist:**" >> "$OUTPUT_DIR/master_review_list.md"
        echo "- [ ] Code Quality Review" >> "$OUTPUT_DIR/master_review_list.md"
        echo "- [ ] Security Review" >> "$OUTPUT_DIR/master_review_list.md"
        echo "- [ ] Performance Review" >> "$OUTPUT_DIR/master_review_list.md"
        echo "- [ ] Documentation Review" >> "$OUTPUT_DIR/master_review_list.md"
        echo "" >> "$OUTPUT_DIR/master_review_list.md"
        echo "---" >> "$OUTPUT_DIR/master_review_list.md"
        echo "" >> "$OUTPUT_DIR/master_review_list.md"
        
        # Progress indicator
        if [ $((total_files % 100)) -eq 0 ]; then
            echo "Processed $total_files files..."
        fi
    done
    
    # Add summary to master list
    cat >> "$OUTPUT_DIR/master_review_list.md" << EOF

## Review Summary Statistics

- **Total Files to Review:** $total_files
- **Critical Priority:** $critical_count files
- **High Priority:** $high_count files  
- **Medium Priority:** $medium_count files
- **Low Priority:** $low_count files

## Review Workflow Recommendations

### Phase 1: Critical and High Priority ($(($critical_count + $high_count)) files)
Focus on Android manifests, build configurations, and core Java source files.

### Phase 2: Medium Priority ($(medium_count) files)  
Review documentation, scripts, and supporting configuration files.

### Phase 3: Low Priority ($(low_count) files)
Review assets, binaries, and generated files for completeness.

## Automated Review Integration

Each file has been created as an individual review item with:
- Specific review checklist tailored to file type
- Focus areas relevant to the file's purpose
- Priority classification for review scheduling
- Structured format for automated copilot analysis

Use the individual review files in this directory for detailed per-file analysis.
EOF
}

# Function to create review priority lists
create_priority_lists() {
    echo "Creating priority-based review lists..."
    
    # Critical Priority Files
    find "$PROJECT_ROOT" -name "AndroidManifest.xml" -type f > "$OUTPUT_DIR/critical_priority_files.txt"
    
    # High Priority Files  
    find "$PROJECT_ROOT" \( -name "*.java" -o -name "*.gradle" -o -path "*/res/*.xml" \) -type f > "$OUTPUT_DIR/high_priority_files.txt"
    
    # Medium Priority Files
    find "$PROJECT_ROOT" \( -name "*.cpp" -o -name "*.h" -o -name "*.md" -o -name "*.sh" -o -name "*.py" -o -name "*.json" -o -name "*.properties" \) -type f > "$OUTPUT_DIR/medium_priority_files.txt"
    
    # Low Priority Files  
    find "$PROJECT_ROOT" \( -name "*.png" -o -name "*.jpg" -o -name "*.apk" -o -name "*.jar" -o -name "*.zip" \) -type f > "$OUTPUT_DIR/low_priority_files.txt"
}

# Main execution
echo -e "${GREEN}Generating individual file reviews...${NC}"

generate_individual_reviews
create_priority_lists

echo -e "${GREEN}Individual file review generation complete!${NC}"
echo -e "${YELLOW}Output files generated in: $OUTPUT_DIR${NC}"
echo
echo "Generated files:"
ls -la "$OUTPUT_DIR/" | head -20
echo "... and many more individual review files"
echo
echo -e "${BLUE}Summary:${NC}"
echo "Total individual review files created: $(find "$OUTPUT_DIR" -name "review_*.md" | wc -l)"
echo "Master review list: $OUTPUT_DIR/master_review_list.md"
echo "Priority lists created for targeted reviews"
echo
echo -e "${GREEN}Each file in Linkpoint now has its own review item for copilot analysis!${NC}"