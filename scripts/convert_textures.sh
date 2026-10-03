#!/bin/bash

# Basis Universal Asset Conversion Script
# This script converts texture assets to KTX2 format for use with ModernTextureManager

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ASSETS_DIR="$SCRIPT_DIR/../assets/textures"
OUTPUT_DIR="$SCRIPT_DIR/../assets/textures_ktx2"
BASISU_TOOL="basisu"

# Color codes for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo -e "${GREEN}Basis Universal Asset Conversion Script${NC}"
echo "========================================"

# Check if basisu tool is available
if ! command -v $BASISU_TOOL &> /dev/null; then
    echo -e "${RED}Error: $BASISU_TOOL command not found${NC}"
    echo "Please install Basis Universal tools from:"
    echo "https://github.com/BinomialLLC/basis_universal"
    exit 1
fi

# Check if input directory exists
if [ ! -d "$ASSETS_DIR" ]; then
    echo -e "${YELLOW}Warning: Input directory $ASSETS_DIR does not exist${NC}"
    echo "Creating example directory structure..."
    mkdir -p "$ASSETS_DIR"
    echo "Place your PNG, TGA, or other texture files in $ASSETS_DIR"
    exit 0
fi

# Create output directory
mkdir -p "$OUTPUT_DIR"

echo "Input directory: $ASSETS_DIR"
echo "Output directory: $OUTPUT_DIR"
echo ""

# Function to convert a single file
convert_texture() {
    local input_file="$1"
    local relative_path="$2"
    local output_file="$OUTPUT_DIR/${relative_path%.*}.ktx2"
    local output_dir="$(dirname "$output_file")"
    
    mkdir -p "$output_dir"
    
    echo -e "Converting: ${YELLOW}$relative_path${NC}"
    
    # Use UASTC format for high quality
    if $BASISU_TOOL -ktx2 -uastc -uastc_level 2 -mipmap "$input_file" -output_file "$output_file"; then
        local input_size=$(stat -c%s "$input_file")
        local output_size=$(stat -c%s "$output_file")
        local compression_ratio=$(( input_size / output_size ))
        echo -e "  ${GREEN}✓${NC} Success (${compression_ratio}:1 compression)"
    else
        echo -e "  ${RED}✗${NC} Failed"
        return 1
    fi
}

# Find and convert all supported texture files
converted_count=0
failed_count=0

while IFS= read -r -d '' file; do
    # Get relative path from assets directory
    relative_path="${file#$ASSETS_DIR/}"
    
    if convert_texture "$file" "$relative_path"; then
        ((converted_count++))
    else
        ((failed_count++))
    fi
done < <(find "$ASSETS_DIR" -type f \( -iname "*.png" -o -iname "*.jpg" -o -iname "*.jpeg" -o -iname "*.tga" -o -iname "*.bmp" \) -print0)

echo ""
echo "========================================"
echo -e "Conversion complete:"
echo -e "  ${GREEN}Converted: $converted_count files${NC}"
if [ $failed_count -gt 0 ]; then
    echo -e "  ${RED}Failed: $failed_count files${NC}"
fi

if [ $converted_count -gt 0 ]; then
    echo ""
    echo "KTX2 files generated in: $OUTPUT_DIR"
    echo "Update your Android assets directory to include these files."
    echo ""
    echo "Usage in code:"
    echo "  InputStream ktx2Stream = assets.open(\"textures_ktx2/texture.ktx2\");"
    echo "  TextureData textureData = textureManager.loadKTX2Texture(ktx2Stream);"
fi