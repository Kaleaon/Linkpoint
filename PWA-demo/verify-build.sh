#!/bin/bash
# Linkpoint PWA - Build Verification Script
# This script verifies that all required files exist for Vercel deployment

# set -e  <-- Removed to allow full report generation despite failures

echo "🔍 Verifying Linkpoint PWA Build..."
echo ""

# Color codes
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Counters
PASSED=0
FAILED=0
WARNINGS=0

# Function to check file exists
check_file() {
    if [ -f "$1" ]; then
        echo -e "${GREEN}✓${NC} $1"
        ((PASSED++))
        return 0
    else
        echo -e "${RED}✗${NC} $1 (MISSING)"
        ((FAILED++))
        return 1
    fi
}

# Function to check directory exists
check_dir() {
    if [ -d "$1" ]; then
        echo -e "${GREEN}✓${NC} $1/"
        ((PASSED++))
        return 0
    else
        echo -e "${RED}✗${NC} $1/ (MISSING)"
        ((FAILED++))
        return 1
    fi
}

# Function to validate JSON
check_json() {
    if python3 -m json.tool "$1" > /dev/null 2>&1; then
        echo -e "${GREEN}✓${NC} $1 (valid JSON)"
        ((PASSED++))
        return 0
    else
        echo -e "${RED}✗${NC} $1 (invalid JSON)"
        ((FAILED++))
        return 1
    fi
}

echo "📋 Core Files:"
check_file "index.html"
check_file "manifest.json"
check_file "service-worker.js"
check_file "vercel.json"
check_file "package.json"
echo ""

echo "📋 Configuration Files Validation:"
check_json "manifest.json"
check_json "vercel.json"
check_json "package.json"
echo ""

echo "📁 Directory Structure:"
check_dir "js"
check_dir "css"
check_dir "assets"
check_dir "assets/icons"
check_dir "assets/images"
echo ""

echo "🎨 Stylesheets:"
check_file "css/styles.css"
echo ""

echo "📜 JavaScript Modules:"
# Core utilities
check_file "js/utils.js"

# Protocol layer
check_file "js/protocol.js"
check_file "js/sl-xmlrpc.js"
check_file "js/sl-messages.js"
check_file "js/sl-circuit.js"
check_file "js/sl-protocol-real.js"
check_file "js/sl-connection-full.js"

# 3D Graphics
check_file "js/primitives3d.js"
check_file "js/camera3d.js"
check_file "js/graphics3d.js"
check_file "js/scene3d.js"
check_file "js/sl-mesh-loader.js"
check_file "js/sl-object-manager.js"

# Advanced SL features (ported from Android) - Phase 1
check_file "js/terrain.js"
check_file "js/windlight.js"
check_file "js/displaynames.js"
check_file "js/assets.js"

# Protocol extensions (ported from Android) - Phase 2
check_file "js/eventqueue.js"
check_file "js/capabilities.js"

# Feature managers
check_file "js/preferences.js"
check_file "js/auth.js"
check_file "js/world.js"
check_file "js/chat.js"
check_file "js/voice.js"
check_file "js/inventory.js"
check_file "js/friends.js"
check_file "js/teleport.js"
check_file "js/search.js"
check_file "js/notifications.js"

# Main app
check_file "js/app.js"
echo ""

echo "🎨 PWA Icons:"
check_file "assets/icons/icon-72x72.png"
check_file "assets/icons/icon-96x96.png"
check_file "assets/icons/icon-128x128.png"
check_file "assets/icons/icon-144x144.png"
check_file "assets/icons/icon-152x152.png"
check_file "assets/icons/icon-192x192.png"
check_file "assets/icons/icon-384x384.png"
check_file "assets/icons/icon-512x512.png"
echo ""

echo "📸 Screenshots:"
check_file "assets/images/screenshot1.png"
check_file "assets/images/screenshot2.png"
echo ""

echo "🔍 Verifying HTML References..."
# Check that all JS files referenced in index.html exist
JS_FILES=$(grep -oP 'src="js/[^"]+\.js"' index.html | sed 's/src="//;s/"//')
MISSING_JS=0
for js_file in $JS_FILES; do
    if [ ! -f "$js_file" ]; then
        echo -e "${RED}✗${NC} $js_file referenced in index.html but missing"
        ((MISSING_JS++))
        ((FAILED++))
    fi
done

if [ $MISSING_JS -eq 0 ]; then
    echo -e "${GREEN}✓${NC} All JavaScript files referenced in index.html exist"
    ((PASSED++))
fi
echo ""

# Check CSS files
CSS_FILES=$(grep -oP 'href="[^"]+\.css"' index.html | sed 's/href="//;s/"//')
MISSING_CSS=0
for css_file in $CSS_FILES; do
    if [ ! -f "$css_file" ]; then
        echo -e "${RED}✗${NC} $css_file referenced in index.html but missing"
        ((MISSING_CSS++))
        ((FAILED++))
    fi
done

if [ $MISSING_CSS -eq 0 ]; then
    echo -e "${GREEN}✓${NC} All CSS files referenced in index.html exist"
    ((PASSED++))
fi
echo ""

echo "🔧 Vercel Configuration Check:"
# Check if vercel.json uses correct builder
if grep -q '"@vercel/static"' vercel.json; then
    echo -e "${GREEN}✓${NC} Using @vercel/static builder (correct for PWA)"
    ((PASSED++))
else
    echo -e "${YELLOW}⚠${NC} Not using @vercel/static builder"
    ((WARNINGS++))
fi

# Check if index.html is set as destination for SPA routing
if grep -q '"destination": "/index.html"' vercel.json; then
    echo -e "${GREEN}✓${NC} SPA routing configured (redirects to index.html)"
    ((PASSED++))
else
    echo -e "${YELLOW}⚠${NC} SPA routing not configured"
    ((WARNINGS++))
fi
echo ""

echo "📱 PWA Manifest Check:"
# Check manifest has required fields
if grep -q '"name"' manifest.json && grep -q '"start_url"' manifest.json && grep -q '"icons"' manifest.json; then
    echo -e "${GREEN}✓${NC} Manifest has required PWA fields"
    ((PASSED++))
else
    echo -e "${RED}✗${NC} Manifest missing required PWA fields"
    ((FAILED++))
fi

# Check for icon type correctness
if grep -q '"type": "image/png"' manifest.json; then
    echo -e "${GREEN}✓${NC} Icon types correctly set to image/png"
    ((PASSED++))
else
    echo -e "${RED}✗${NC} Icon types may be incorrect (should be image/png)"
    ((FAILED++))
fi
echo ""

echo "📊 Summary:"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo -e "  ${GREEN}Passed:${NC}    $PASSED"
echo -e "  ${RED}Failed:${NC}    $FAILED"
echo -e "  ${YELLOW}Warnings:${NC}  $WARNINGS"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo ""

if [ $FAILED -eq 0 ]; then
    echo -e "${GREEN}✅ Build verification PASSED!${NC}"
    echo "   Ready for Vercel deployment!"
    echo ""
    echo "   To deploy, run:"
    echo "   $ cd PWA-demo"
    echo "   $ vercel --prod"
    exit 0
else
    echo -e "${RED}❌ Build verification FAILED!${NC}"
    echo "   Please fix the errors above before deploying."
    exit 1
fi
