# Linkpoint (Lumiya Viewer) Development Instructions

Always reference these instructions first and fallback to search or bash commands only when you encounter unexpected information that does not match the info here.

## Working Effectively

### Bootstrap and Setup Commands
Execute these commands in order to set up your environment:

- `chmod +x ./gradlew` - Make Gradle wrapper executable
- `./gradlew --version` - Verify Gradle 8.0 and Java 17+ are working
- `./gradlew clean` - Clean any previous build artifacts (takes ~1 minute)

### Build Status: ❌ CRITICAL - BUILD CURRENTLY FAILS
**DO NOT attempt to build this project** - it fails due to resource conflicts between bundled Android Support Library resources and modern AndroidX dependencies.

**Known Build Issues:**
- Resource compilation failures with duplicate AndroidX/Support Library attributes
- Nine-patch PNG compilation errors (`abc_list_divider_mtrl_alpha.9.png`)
- Vector drawable references to string resources not supported

**Failed Commands (documented for reference):**
- `./gradlew build` - FAILS after 3 seconds with resource conflicts  
- `./gradlew assembleDebug` - FAILS after 3 seconds with resource conflicts
- `./gradlew test` - FAILS due to dependency on resource compilation
- `./gradlew lint` - FAILS due to dependency on resource compilation

### Working Commands
These commands work and should be used for development tasks:

- `./gradlew --help` - Show available Gradle options
- `./gradlew tasks --all` - List all available tasks (despite build issues)
- `./gradlew clean` - Clean build directory (works independently)
- `chmod +x scripts/convert_textures.sh && ./scripts/convert_textures.sh` - Convert textures to KTX2 format

## Project Architecture

### Project Type
Android Second Life viewer application using:
- **Languages**: Java (primary) + C++ (native components via JNI)
- **Build System**: Gradle 8.0 with Android Gradle Plugin 8.0.0
- **Target**: Android API 14+ (minimum), API 34 (target)
- **NDK**: C++17 with CMake 3.10.2 for native builds

### Key Directory Structure
```
app/src/main/
├── java/com/lumiyaviewer/lumiya/     # Main Java application code
│   ├── render/                       # 3D rendering engine (OpenGL ES)
│   │   ├── avatar/                   # Avatar rendering and animation
│   │   ├── glres/                    # OpenGL resource management  
│   │   ├── programs/                 # Shader programs
│   │   ├── spatial/                  # Spatial indexing and culling
│   │   ├── terrain/                  # Terrain rendering
│   │   └── tex/                      # Texture management
│   ├── res/                          # Resource management system
│   │   ├── anim/                     # Animation cache
│   │   ├── geometry/                 # Geometry cache
│   │   ├── textures/                 # Texture cache
│   │   └── [8+ more subsystems]
│   ├── slproto/                      # Second Life protocol implementation
│   │   ├── auth/                     # Authentication
│   │   ├── avatar/                   # Avatar management  
│   │   ├── caps/                     # Capabilities system
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