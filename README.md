# Linkpoint (Lumiya Viewer)

Linkpoint is an Android application for connecting to Second Life virtual worlds. This project contains the source code for the Lumiya viewer, a mobile client that allows users to access Second Life from their Android devices.

## Features

- **Virtual World Access**: Connect to Second Life virtual worlds
- **3D Rendering**: Advanced 3D graphics rendering for mobile devices
- **Asset Management**: Efficient handling of textures, animations, and geometry
- **Network Protocol**: Implementation of Second Life's communication protocols
- **Resource Caching**: Optimized caching system for improved performance

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
3. Build using the standard Android build process

## Contributing

When contributing to this project:

1. Follow standard Java coding conventions
2. Ensure proper error handling and logging
3. Add appropriate documentation for new features
4. Test on various Android devices when possible

## License

[License information to be added]

## Third-Party Libraries

This project uses several third-party libraries for functionality such as:
- Google Guava for collections and utilities
- OkHttp for networking
- Various Android support libraries

---

*Note: This is a Second Life viewer implementation. Second Life is a trademark of Linden Lab.*