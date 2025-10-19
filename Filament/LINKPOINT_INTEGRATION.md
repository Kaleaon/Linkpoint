# Filament Integration with Linkpoint

## Overview

This directory contains Google Filament, a real-time physically based rendering (PBR) engine for Android, iOS, Linux, macOS, Windows, and WebGL. It has been integrated into the Linkpoint project to provide modern rendering capabilities.

## About Filament

Filament is a real-time physically based rendering engine developed by Google. It is designed to be as small as possible and as efficient as possible on Android, while providing high-quality rendering across multiple platforms.

**Source Repository:** https://github.com/google/filament

**Key Features:**
- Real-time physically based rendering (PBR)
- Cross-platform support (Android, iOS, Windows, macOS, Linux, WebGL)
- Efficient mobile rendering
- Modern graphics API support (OpenGL ES, Vulkan, Metal, DirectX)
- glTF 2.0 support
- Material system with runtime compilation
- Advanced lighting and shadow techniques

## Version Information

- **Cloned Date:** October 19, 2025
- **Source:** https://github.com/google/filament (latest main branch at time of clone)

## Integration with Linkpoint

This Filament copy is integrated into Linkpoint to enhance the graphics rendering capabilities of the viewer. Linkpoint is a Second Life viewer for Android that benefits from Filament's:

1. **Modern PBR Rendering** - Enhanced visual quality with physically based materials
2. **Mobile Optimization** - Better performance on Android devices
3. **Advanced Lighting** - Improved lighting and shadow systems
4. **Asset Support** - glTF 2.0 compatibility for modern 3D assets

## Directory Structure

```
Filament/
├── android/          # Android-specific code and samples
├── assets/           # Sample assets and materials
├── build/            # Build system and scripts
├── docs/             # Documentation
├── filament/         # Core Filament rendering engine
├── libs/             # Supporting libraries
├── samples/          # Sample applications
├── shaders/          # Shader sources
├── third_party/      # Third-party dependencies
└── tools/            # Build tools and utilities
```

## Building Filament

For detailed build instructions, see the [BUILDING.md](BUILDING.md) file in this directory.

### Android Quick Start

To use Filament in an Android project, add these dependencies to your `build.gradle`:

```gradle
repositories {
    mavenCentral()
}

dependencies {
    implementation 'com.google.android.filament:filament-android:1.66.0'
    implementation 'com.google.android.filament:gltfio-android:1.66.0'
    implementation 'com.google.android.filament:filament-utils-android:1.66.0'
}
```

## Documentation

- [README.md](README.md) - Main Filament documentation
- [BUILDING.md](BUILDING.md) - Build instructions
- [docs/](docs/) - Comprehensive documentation
- [samples/](samples/) - Example applications

## License

Filament is licensed under the Apache License 2.0. See [LICENSE](LICENSE) for details.

## Related Linkpoint Documentation

For information on how Filament is integrated with Linkpoint's rendering pipeline, see:
- `/docs/Graphics_Engine_Modernization_Plan.md`
- `/docs/Graphics_Engine_Roadmap.md`

## Maintenance Notes

This is a snapshot of the Google Filament repository integrated into Linkpoint. For the latest Filament updates, refer to the official repository at https://github.com/google/filament.

To update to a newer version:
1. Clone the latest Filament repository
2. Remove the old Filament directory (excluding any Linkpoint-specific modifications)
3. Copy the new Filament repository (without the .git folder)
4. Update this document with the new version information
5. Test compatibility with Linkpoint's rendering pipeline
