copilot/fix-16
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
                     # Capabilities system
│   │   ├── chat/                     # Chat protocols
│   │   ├── inventory/                # Inventory system
│   │   ├── llsd/                     # LLSD data format
│   │   └── [15+ more modules]
│   ├── ui/                           # User interface components
│   └── openjpeg/                     # JPEG2000 + Basis Universal integration
├── cpp/                              # Native C++ code
│   ├── CMakeLists.txt               # CMake build configuration  
│   ├── basis_universal/             # Basis Universal texture codec
│   └── jni/                         # JNI wrapper functions
└── resources/                       # Android resources and manifest
    ├── AndroidManifest.xml          # App manifest (has known issues)
    └── res/                         # UI resources (conflicting with AndroidX)
```

## Development Workflow

### Code Exploration and Navigation
Since builds fail, use these approaches for code analysis:

- **Search specific functionality**: `grep -r "TextureCache" app/src/main/java/`
- **Find protocol handlers**: `find app/src/main/java -name "*SLAgent*"`
- **Locate rendering code**: `ls app/src/main/java/com/lumiyaviewer/lumiya/render/`
- **Check native integration**: `find app/src/main/cpp -name "*.cpp" -o -name "*.h"`

### Key Classes and Components
Focus on these core systems when making changes:

- `ResourceManager` - Central resource management system
- `SLAgentCircuit` - Main Second Life protocol handler
- `TextureCache`, `GeometryCache`, `AnimationCache` - Asset caching systems  
- `TerrainPatchGeometry` - Terrain rendering
- `ModernTextureManager` - Basis Universal texture integration
- `OpenJPEG` class - JPEG2000 and KTX2 texture decoding

### Native Code (C++)
The project includes Basis Universal integration:
- **Build requirements**: NDK with C++17 support, CMake 3.10.2+
- **Location**: `app/src/main/cpp/`
- **JNI integration**: Functions in `jni/openjpeg_basis_integration.cpp`
- **Texture conversion**: Use `scripts/convert_textures.sh` for asset pipeline

## Validation and Testing

### Manual Validation Approaches
Since automated testing fails, use these manual validation methods:

**Code Quality Checks:**
- Review Java files for proper null checks and exception handling
- Check JNI function signatures match Java native method declarations  
- Verify resource references in XML files are valid
- Look for potential memory leaks in native code

**Architecture Validation:**
- Ensure new classes follow existing package structure
- Check that protocol handlers extend appropriate base classes
- Verify texture management code integrates with existing cache systems
- Confirm UI components use proper Android lifecycle methods

### Documentation Validation
The project includes extensive documentation in `docs/`:
- Check `docs/CPP_Integration_Guide.md` for native code requirements
- Review `docs/Lumiya_Modernization_Guide.md` for architecture patterns
- Reference `CONTRIBUTING.md` for code style guidelines

## Common Development Tasks

### Adding New Features
When implementing new functionality:

1. **Find the relevant subsystem** in the directory structure above
2. **Check existing patterns** by examining similar classes in the same package
3. **Follow the protocol architecture** - most features integrate with `slproto/` modules
4. **Consider resource management** - use existing cache systems where appropriate
5. **Test manually** by code review since builds fail

### Texture and Asset Work
For texture-related changes:

- **Modern textures**: Work with `ModernTextureManager` class
- **Legacy textures**: Check `TextureCache` and related systems
- **Asset conversion**: Use the working `scripts/convert_textures.sh`
- **Native integration**: JNI functions in `cpp/jni/` directory

### Protocol and Network Changes
For Second Life protocol work:

- **Start with**: `slproto/` package - contains all protocol modules
- **Authentication**: Check `slproto/auth/` 
- **Object management**: Look at `slproto/objects/`
- **Chat/messaging**: Review `slproto/chat/`

## Known Limitations and Workarounds

### Resource Conflicts (Critical Issue)
The project contains pre-AndroidX Android Support Library resources that conflict with modern AndroidX dependencies. This prevents all build operations.

**Affected areas:**
- All build commands fail
- Cannot run automated tests
- Cannot generate APK files
- Cannot use Android lint tools

**Workarounds:**
- Use code analysis and manual review instead of builds
- Reference the extensive documentation for validation
- Use search and grep commands for code exploration

### Development Environment
**Working environment requirements:**
- Java 17+ (confirmed working with Eclipse Adoptium 17.0.16+8)
- Gradle 8.0 (auto-downloads via wrapper)
- Android SDK with API 34 support
- NDK for native builds (if fixing C++ components)

**Commands that work reliably:**
- All `grep`, `find`, and file exploration commands
- `./gradlew clean` and `./gradlew tasks`
- Asset conversion scripts
- Documentation and file editing

## Critical Reminders

- **NEVER attempt builds** - they will always fail due to resource conflicts
- **Use manual validation** - rely on code review and documentation
- **Focus on architecture** - the extensive documentation provides implementation guidance  
- **Test logic, not compilation** - validate code correctness through review
- **Follow existing patterns** - the codebase has consistent architectural approaches

## Quick Reference Commands

```bash
# Environment check
./gradlew --version

# Clean workspace  
./gradlew clean

# Explore codebase structure
find app/src/main/java -name "*.java" | head -20
ls -la app/src/main/java/com/lumiyaviewer/lumiya/

# Search for specific functionality
grep -r "class.*Manager" app/src/main/java/
find app/src/main/java -name "*Cache*"

# Asset pipeline (this works)
chmod +x scripts/convert_textures.sh
./scripts/convert_textures.sh

# Documentation review
ls docs/
cat CONTRIBUTING.md
```

Remember: This is a sophisticated 3D graphics application with complex architecture. The build issues do not reflect code quality - focus on the architectural patterns and extensive documentation for development guidance.
