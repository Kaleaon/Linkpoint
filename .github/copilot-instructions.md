# Linkpoint (Lumiya Second Life Viewer)

Linkpoint is an Android application for connecting to Second Life virtual worlds. It's a mobile client with 3D rendering, native C++ components, and complex resource management systems.

**ALWAYS reference these instructions first and fallback to search or bash commands only when you encounter unexpected information that does not match the info here.**

## Working Effectively

### Known Build Issues - CRITICAL
**The build currently FAILS due to documented resource conflicts. Do NOT expect it to build successfully without applying fixes first.**

- `./gradlew build` - FAILS in ~2-45 seconds with resource conflicts
- `./gradlew assembleDebug` - FAILS in ~2-45 seconds with resource conflicts  
- `./gradlew lintDebug` - FAILS in ~2 seconds with resource conflicts

**Expected Error Messages:**
```
Resource compilation failed. Can not add resource to table.
Duplicate value for resource 'attr/fontStyle'
android:exported needs to be explicitly specified
Can't process attribute android:pathData="@string/path_password_eye"
```

**The project requires fixes from docs/Broken_Code_Analysis_and_Fixes.md before building.**

### Environment Setup
- **Java**: Requires Java 8+ (Java 17 available in environment)
- **Android SDK**: Located at `/usr/local/lib/android/sdk`
- **NDK**: Available versions 26.3, 27.3, 28.2 in `$ANDROID_HOME/ndk/`
- **Gradle**: Use `./gradlew` (v8.0) not system gradle (v9.0) - version mismatch causes issues
- **CMake**: Required for native C++ components (Basis Universal transcoder)

### Build System Commands
```bash
# Clean (works) - takes ~1 minute 20 seconds. NEVER CANCEL.
./gradlew clean
# Timeout: Set to 300+ seconds

# Check available tasks
./gradlew tasks --all | grep -E "(build|lint|test)"

# View build configuration  
./gradlew dependencies --configuration debugRuntimeClasspath
```

### Development Structure
```
app/
├── src/main/java/com/lumiyaviewer/lumiya/
│   ├── render/          # 3D rendering system  
│   ├── res/             # Resource management
│   ├── slproto/         # Second Life protocol
│   ├── ui/              # User interface
│   └── utils/           # Utilities
├── src/main/cpp/        # Native C++ code
│   └── CMakeLists.txt   # CMake build config
└── resources/           # Android resources (causes conflicts)
    ├── AndroidManifest.xml
    └── res/             # Resource conflicts with dependencies
```

### Scripts and Tools
```bash
# Texture conversion (requires basisu tool - currently missing)
chmod +x scripts/convert_textures.sh
./scripts/convert_textures.sh
# Expected: "Error: basisu command not found" - install from https://github.com/BinomialLLC/basis_universal

# Explore project structure  
find . -name "*.gradle" -o -name "CMakeLists.txt" -o -name "*.md"
```

## Validation & Testing

**CRITICAL: Since builds fail, focus on code analysis and applying documented fixes.**

### Manual Code Review Process
1. **Analyze resource conflicts**: Check `app/resources/res/` vs dependency resources
2. **Review manifest issues**: Check `app/resources/AndroidManifest.xml` for missing `android:exported`
3. **Study documentation**: Read `docs/Broken_Code_Analysis_and_Fixes.md` for specific fixes
4. **Apply suggested fixes**: Use fixes from documentation before attempting builds

### Code Quality Checks (Currently Fail)
```bash
# Lint analysis - FAILS due to resource conflicts (~2 seconds)
./gradlew lintDebug --continue
# Expected: Same resource conflicts as build

# Check code style (when fixed)
# ./gradlew checkJetifier
```

### Testing Strategy (Post-Fix)
After applying resource conflict fixes from documentation:
1. **Build verification**: `./gradlew assembleDebug` - should take 30+ minutes with native components. **NEVER CANCEL.**
2. **APK validation**: `unzip -l app/build/outputs/apk/debug/app-debug.apk | grep -E "(libopenjpeg|classes.dex)"`
3. **Native library check**: Verify C++ components compiled correctly

## Common Tasks & Patterns

### Repository Navigation
```bash
# Key documentation (READ FIRST)
ls docs/
# Focus on: Broken_Code_Analysis_and_Fixes.md, Implementation_Roadmap.md

# Main source areas
find app/src/main/java -name "*.java" | head -10
ls app/src/main/cpp/

# Resource investigation 
find app/resources/res -name "*.xml" | head -5
grep -r "attr/fontStyle" app/resources/
```

### Resource Conflict Analysis
```bash
# Check for duplicate resources (root cause of build failures)
grep -r "fontStyle\|passwordToggle\|buttonGravity" app/resources/res/
find app/resources/res -name "values.xml" -exec grep -l "attr/" {} \;

# AndroidX dependencies check
grep -A 5 "implementation.*androidx" app/build.gradle
```

### Native Code Analysis
```bash
# CMake configuration
cat app/src/main/cpp/CMakeLists.txt

# Native source files
find app/src/main/cpp -name "*.cpp" -o -name "*.h"

# NDK configuration
grep -A 5 "ndk\|cmake" app/build.gradle
```

## Critical Information

### Build Timings & Timeouts
- **Clean**: ~1 min 20 sec - Set timeout to 300+ seconds
- **Resource merge**: Fails in ~2-45 seconds (conflicts)
- **Full build**: Would be 30+ minutes with native compilation - Set timeout to 3600+ seconds
- **NEVER CANCEL**: Native builds may appear hung but are processing

### Expected Working Commands
```bash
# These work reliably:
./gradlew clean                    # ~1 min 20 sec
./gradlew tasks                    # ~10 seconds  
./gradlew dependencies             # ~15 seconds
./scripts/convert_textures.sh      # Fails gracefully - missing basisu
grep -r "class.*Activity" app/src/main/java/  # Code exploration
```

### Do NOT Attempt (Will Fail)
```bash
# These WILL fail until resource conflicts are fixed:
./gradlew build
./gradlew assembleDebug  
./gradlew assembleRelease
./gradlew lint
./gradlew test
```

### Fix Application Order
1. **Apply resource conflict fixes** from `docs/Broken_Code_Analysis_and_Fixes.md`
2. **Update AndroidManifest.xml** - add `android:exported` attributes  
3. **Resolve dependency conflicts** - use packagingOptions in build.gradle
4. **Test incremental builds** - start with `./gradlew assembleDebug`
5. **Validate native compilation** - ensure CMake builds C++ components

## Key Files Reference

### Critical Configuration Files
- `build.gradle` - Top-level project config
- `app/build.gradle` - Main app module (contains resource conflict issues)
- `app/resources/AndroidManifest.xml` - Missing android:exported declarations
- `app/src/main/cpp/CMakeLists.txt` - Native build configuration

### Essential Documentation  
- `docs/Broken_Code_Analysis_and_Fixes.md` - **MUST READ** - Contains exact fixes for build failures
- `docs/Implementation_Roadmap.md` - Step-by-step fix implementation
- `docs/Lumiya_Modernization_Guide.md` - Architecture modernization plan  
- `README.md` - Project overview and structure

### Important Source Areas
- `app/src/main/java/com/lumiyaviewer/lumiya/render/` - 3D graphics system
- `app/src/main/java/com/lumiyaviewer/lumiya/slproto/` - Second Life protocol  
- `app/src/main/java/com/lumiyaviewer/lumiya/res/` - Resource management
- `app/src/main/cpp/` - Native Basis Universal integration

## Emergency Procedures

### If Build Appears Hung
- **DO NOT CANCEL** - Native compilation can take 30+ minutes
- Monitor with: `ps aux | grep -E "(gradle|cmake|ndk-build)"`
- Wait minimum 45 minutes before considering cancellation

### If New Errors Appear  
1. Check if resource conflicts are resolved: `grep -r "Duplicate.*resource" build/`
2. Verify Android SDK version: `ls $ANDROID_HOME/platforms/`
3. Compare with documented fixes in `docs/Broken_Code_Analysis_and_Fixes.md`

**Remember: This project has documented build failures. Focus on applying the comprehensive fixes in the documentation rather than expecting immediate build success.**