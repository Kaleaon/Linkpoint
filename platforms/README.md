# Linkpoint Cross-Platform Implementations

This directory contains the cross-platform implementations of the Linkpoint Second Life/OpenSimulator viewer.

## Overview

Linkpoint is a modern, cross-platform viewer for Second Life and OpenSimulator virtual worlds. This directory contains three platform-specific implementations:

1. **Android** - Native Android app (see main `app/` directory)
2. **iOS** - Native iOS app with SwiftUI
3. **PWA** - Progressive Web App with Next.js

## Platform Comparison

| Feature | Android | iOS | PWA |
|---------|---------|-----|-----|
| **Framework** | Jetpack Compose | SwiftUI | Next.js + React |
| **Language** | Kotlin | Swift | TypeScript |
| **Architecture** | MVVM + Hilt | MVVM | React Hooks |
| **3D Rendering** | OpenGL ES | Metal | WebGL/Three.js |
| **Min Version** | Android 7.0+ | iOS 15.0+ | Modern Browsers |
| **Offline Support** | ✅ | ✅ | ✅ (PWA) |
| **Install Size** | ~50MB | ~40MB | ~2MB (cached) |

## Shared Features

All three platforms share the following core features:

### Authentication
- Multi-grid support (Second Life, OSGrid, custom grids)
- XMLRPC authentication protocol
- Session persistence
- Secure credential storage

### World View
- 3D rendering of virtual environments
- Avatar movement and controls
- Camera controls
- Mini-map
- Region information

### Chat System
- Local chat
- Group chat
- Instant messaging (IM)
- System messages
- Chat history

### Inventory Management
- Folder-based organization
- Search functionality
- Item categorization
- Drag-and-drop support (desktop)

### Profile Management
- User information display
- Avatar customization
- Settings and preferences
- Statistics dashboard

## Architecture

### Common Design Patterns

All platforms follow similar architectural patterns:

1. **MVVM Architecture**: Separation of UI, business logic, and data
2. **Repository Pattern**: Abstraction of data sources
3. **Dependency Injection**: Loose coupling of components
4. **Reactive Programming**: State management with observables

### Protocol Implementation

All platforms implement the same protocols for backend communication:

- **XMLRPC**: Authentication and initial connection
- **LLSD**: Structured data exchange
- **UDP**: Real-time world updates
- **HTTP/REST**: Asset downloads and API calls
- **WebSocket**: Real-time messaging (PWA)

## Getting Started

### Android
```bash
# Open in Android Studio
# Build and run on device/emulator
./gradlew assembleDebug
```

### iOS
```bash
cd platforms/iOS
# Open in Xcode
open Linkpoint.xcodeproj
# Build and run (⌘R)
```

### PWA
```bash
cd platforms/PWA
npm install
npm run dev
# Open http://localhost:3000
```

## Development Roadmap

### Phase 1: Core Features ✅
- [x] Basic UI and navigation
- [x] Login system
- [x] Chat interface
- [x] Inventory view
- [x] Profile management

### Phase 2: Backend Integration (In Progress)
- [ ] XMLRPC client implementation
- [ ] Real authentication with grids
- [ ] Inventory synchronization
- [ ] Chat protocol implementation

### Phase 3: 3D Rendering
- [ ] Basic 3D scene rendering
- [ ] Avatar rendering
- [ ] Object loading and display
- [ ] Texture management
- [ ] Lighting and shadows

### Phase 4: Advanced Features
- [ ] Voice chat (WebRTC)
- [ ] Group management
- [ ] LSL scripting support
- [ ] Marketplace integration
- [ ] Friends list
- [ ] Teleportation

### Phase 5: Optimization
- [ ] Performance improvements
- [ ] Memory optimization
- [ ] Network efficiency
- [ ] Battery optimization (mobile)
- [ ] Offline mode enhancements

## Integration with OpenSimulator

All platforms are designed to work with existing OpenSimulator/Second Life backends:

### Supported Grids
- **Second Life**: Official Linden Lab grid
- **OSGrid**: Open source grid
- **Custom Grids**: Any OpenSimulator-based grid

### Protocol Compatibility
- XMLRPC login protocol
- LLSD data format
- UDP message protocol
- Capabilities system
- Asset system

### No Backend Required
These are **viewer clients only** - they connect to existing OpenSimulator/Second Life servers. No custom backend development is needed.

## Code Organization

```
platforms/
├── iOS/                    # iOS implementation
│   ├── App.swift          # Main app entry
│   ├── Models/            # Data models
│   ├── ViewModels/        # Business logic
│   ├── Views/             # UI components
│   └── README.md          # iOS-specific docs
│
├── PWA/                   # Progressive Web App
│   ├── app/               # Next.js app directory
│   ├── components/        # React components
│   ├── public/            # Static assets
│   └── README.md          # PWA-specific docs
│
└── README.md              # This file
```

## Testing

### Android
```bash
./gradlew test
./gradlew connectedAndroidTest
```

### iOS
```bash
# In Xcode
⌘U (Run tests)
```

### PWA
```bash
cd platforms/PWA
npm test
npm run test:e2e
```

## Deployment

### Android
- Google Play Store
- APK direct distribution
- F-Droid (open source)

### iOS
- Apple App Store
- TestFlight (beta)

### PWA
- Web hosting (Vercel, Netlify, etc.)
- Self-hosted
- CDN distribution

## Contributing

Contributions are welcome! Please:

1. Follow the existing code style
2. Write tests for new features
3. Update documentation
4. Submit pull requests to the main repository

## Resources

### Documentation
- [OpenSimulator Documentation](http://opensimulator.org/wiki/)
- [Second Life Protocol](http://wiki.secondlife.com/wiki/Protocol)
- [LLSD Format](http://wiki.secondlife.com/wiki/LLSD)

### Related Projects
- [Firestorm Viewer](https://www.firestormviewer.org/)
- [Singularity Viewer](https://www.singularityviewer.org/)
- [OpenSimulator](http://opensimulator.org/)

## License

See LICENSE file in the main repository.

## Support

For questions and support:
- GitHub Issues: [Linkpoint Repository](https://github.com/Kaleaon/Linkpoint)
- Documentation: See individual platform READMEs
- Community: OpenSimulator forums and Discord

---

**Note**: This is an active development project. Features and documentation are continuously being updated.