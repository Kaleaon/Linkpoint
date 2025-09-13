# Linkpoint (Lumiya Viewer)

Linkpoint is an Android application for connecting to Second Life virtual worlds. This project contains the source code for the Lumiya viewer, a mobile client that allows users to access Second Life from their Android devices.

## Features

- **Virtual World Access**: Connect to Second Life virtual worlds
- **3D Rendering**: Advanced 3D graphics rendering for mobile devices
- **Asset Management**: Efficient handling of textures, animations, and geometry
- **Network Protocol**: Implementation of Second Life's communication protocols
- **Resource Caching**: Optimized caching system for improved performance

## Project Structure

The project follows standard Android application structure:

```
app/
├── src/main/java/com/lumiyaviewer/lumiya/
│   ├── render/          # 3D rendering system
│   │   ├── avatar/      # Avatar rendering and animation
│   │   ├── glres/       # OpenGL resource management
│   │   ├── programs/    # Shader programs
│   │   ├── spatial/     # Spatial indexing and culling
│   │   ├── terrain/     # Terrain rendering
│   │   └── tex/         # Texture management
│   ├── res/             # Resource management system
│   │   ├── anim/        # Animation cache
│   │   ├── collections/ # Custom collections and queues
│   │   ├── executors/   # Thread pool executors
│   │   ├── geometry/    # Geometry cache
│   │   ├── mesh/        # Mesh cache
│   │   ├── terrain/     # Terrain resources
│   │   ├── text/        # Text rendering
│   │   └── textures/    # Texture cache
│   ├── slproto/         # Second Life protocol implementation
│   ├── ui/              # User interface components
│   └── utils/           # Utility classes
└── resources/           # Android resources and manifest
```

## Architecture

The application is structured with the following main components:

- **Resource Management**: Handles textures, animations, geometry, and other 3D assets
- **Network Layer**: Implements Second Life protocol communication (`SLCircuit`, `SLConnection`)
- **Rendering System**: 3D graphics rendering for terrain, objects, and avatars
- **Asset Pipeline**: Efficient loading and caching of virtual world resources

## Key Components

- `ResourceManager` - Central resource management system
- `SLAgentCircuit` - Main Second Life protocol handler  
- `AnimationCache`, `TextureCache`, `GeometryCache` - Asset caching systems
- `TerrainPatchGeometry` - Terrain rendering
- `DrawableText*` - Text rendering systems

## Building

This is an Android application. To build:

1. Ensure you have Android Studio and the Android SDK installed
2. Open the project in Android Studio
3. Build using the standard Android build process with Gradle

### Build Requirements

- **Android Studio**: Latest stable version
- **Android SDK**: API Level 14+ (minimum), API Level 34+ (target)
- **Java**: Version 8 or higher
- **Gradle**: Included with Android Studio

## Contributing

When contributing to this project:

1. Follow standard Java coding conventions
2. Ensure proper error handling and logging
3. Add appropriate documentation for new features
4. Test on various Android devices when possible

See [CONTRIBUTING.md](CONTRIBUTING.md) for detailed contribution guidelines.

## Dependencies

This project uses several key libraries:

- **Google Guava** - Collections and utilities
- **OkHttp** - HTTP networking
- **AndroidX** - Android support libraries
- **Custom OpenJPEG binding** - JPEG2000 image decoding

## Documentation

For detailed technical information about the project architecture and improvements:

### Second Life Open Source Integration
- **[Second Life Open Source Portal Integration Guide](docs/Second_Life_Open_Source_Portal_Integration_Guide.md)** - **PRIMARY GUIDE** - Comprehensive documentation for using Linkpoint with Second Life's open source ecosystem
- **[OpenSimulator Compatibility](docs/OpenSimulator_Compatibility.md)** - Complete guide for connecting to and using OpenSimulator grids
- **[LibreMetaverse Integration](docs/LibreMetaverse_Integration.md)** - Java implementation of LibreMetaverse patterns and protocols
- **[Troubleshooting Second Life Integration](docs/Troubleshooting_Second_Life_Integration.md)** - Comprehensive troubleshooting guide for common SL integration issues

### Core Architecture Documentation
- **[API Analysis and Improvements](docs/API_Analysis_and_Improvements.md)** - Comprehensive analysis of LibreMetaverse API implementation and graphics engine architecture
- **[API Usage Catalog](docs/API_Usage_Catalog.md)** - Complete mapping of all APIs, libraries, and frameworks used in the project  
- **[Graphics Engine Roadmap](docs/Graphics_Engine_Roadmap.md)** - Detailed improvement plan for graphics and performance enhancements
- **[Research Summary](docs/Research_Summary.md)** - Executive summary of OpenMetaverse/LibreMetaverse research and improvement opportunities

### Modernization Documentation
- **[Lumiya Modernization Guide](docs/Lumiya_Modernization_Guide.md)** - Comprehensive guide for modernizing Lumiya Viewer to work with Second Life using contemporary technologies
- **[Broken Code Analysis and Fixes](docs/Broken_Code_Analysis_and_Fixes.md)** - Detailed analysis of broken code areas with specific fixes using modern virtual world technologies
- **[Second Life Integration Guide](docs/Second_Life_Integration_Guide.md)** - Specific implementation details for integrating modern technologies with Second Life protocols
- **[Implementation Roadmap](docs/Implementation_Roadmap.md)** - Practical step-by-step implementation guide with specific code changes and testing procedures

### Key Modernization Features

The modernization effort integrates insights from leading virtual world and metaverse projects:

#### Referenced Technologies
- **@webaverse-studios/webaverse** - Modern WebXR/3D web technologies and asset pipeline
- **OMI Group (github.com/omigroup)** - Open Metaverse Interoperability standards
- **@cinderblocks/libremetaverse** - Contemporary C# Second Life protocol implementation  
- **Second Life OpenMetaverse Community** - Protocol modernization insights

#### Key Improvements
- **Modern Protocol Stack**: HTTP/2 CAPS, WebSocket events, OAuth2 authentication
- **Advanced Graphics Pipeline**: Vulkan rendering, PBR materials, Basis Universal textures
- **Interoperability Standards**: glTF 2.0, VRM avatars, OMI extensions
- **Performance Optimization**: Multi-threaded rendering, GPU memory pools, adaptive quality
- **Build System Fixes**: Resolved Android resource conflicts and native compilation issues

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

*Note: This is a Second Life viewer implementation. Second Life is a trademark of Linden Lab.*