# 🔗 Linkpoint

**Modern Second Life Viewer for Android**

Linkpoint is a next-generation Second Life viewer built from the ground up with modern technologies and standards for 2025 and beyond.

> ⚠️ **Important Disclaimer:** This software is not provided or supported by Linden Lab, the makers of Second Life. Linkpoint is an independent, community-developed third-party viewer.

## Third-Party Viewer Compliance

Linkpoint complies with [Linden Lab's Third-Party Viewer Policy](https://secondlife.com/corporate/third-party-viewers).

- 📄 [Privacy Policy](../../PRIVACY_POLICY.md)
- ✅ [TPV Policy Compliance Documentation](../../THIRD_PARTY_VIEWER_POLICY_COMPLIANCE.md)
- 💬 **Customer Support:** Community support via [GitHub Issues](https://github.com/Kaleaon/Linkpoint/issues) - no official support provided

## ✨ Features

### 🎙️ Modern Voice Chat
- **WebRTC-based voice communication** replacing legacy Vivox
- Real-time spatial audio with proximity-based attenuation
- Full support for group voice channels
- Hardware echo cancellation and noise suppression
- Bluetooth and wired headset support
- Low-latency audio streaming

### 🎨 Advanced Graphics
- **OpenGL ES 3.2** with modern shader-based rendering
- **PBR (Physically Based Rendering)** materials
- HDR lighting and tone mapping
- Real-time shadows and ambient occlusion
- Post-processing effects
- Optimized for mobile GPUs

### 🔐 Modern Authentication
- OAuth2 authentication support
- LLSD protocol implementation
- Secure credential storage
- Multi-grid support (Agni, Aditi, OpenSim)

### 📱 Native Android Experience
- **Kotlin-first** codebase with coroutines
- Material Design 3 UI
- Dark mode support
- Gesture navigation
- Optimized battery usage
- Adaptive layouts for tablets

### 🌐 Protocol Implementation
- HTTP/2 support for improved performance
- Modern capabilities (caps) system
- WebSocket event streaming
- LLSD serialization/deserialization
- Connection pooling and retry logic

## 🏗️ Architecture

Linkpoint is built using modern Android architecture patterns:

```
Linkpoint/
├── src/main/kotlin/com/linkpoint/
│   ├── core/              # Application core
│   │   └── LinkpointApplication.kt
│   ├── auth/              # Authentication
│   │   └── LinkpointAuthManager.kt
│   ├── voice/             # Voice chat (WebRTC)
│   │   └── LinkpointVoiceManager.kt
│   ├── graphics/          # Rendering pipeline
│   │   └── LinkpointRenderPipeline.kt
│   ├── protocol/          # SL protocol
│   │   └── LinkpointProtocolManager.kt
│   └── ui/                # User interface
│       └── LinkpointMainActivity.kt
├── src/main/res/          # Resources
│   ├── values/
│   │   ├── strings.xml
│   │   ├── colors.xml
│   │   └── themes.xml
│   └── layout/
└── src/main/AndroidManifest.xml
```

## 🚀 Getting Started

### Prerequisites

- Android Studio Hedgehog (2023.1.1) or later
- Android SDK 34
- Kotlin 1.9+
- Gradle 8.2+
- JDK 17

### Building

1. Clone the repository:
```bash
git clone https://github.com/yourusername/linkpoint.git
cd linkpoint
```

2. Open in Android Studio:
```bash
studio Linkpoint/
```

3. Sync Gradle and build:
```bash
./gradlew build
```

4. Run on device or emulator:
```bash
./gradlew installDebug
```

## 📦 Dependencies

### Core Libraries
- **Kotlin Coroutines** - Asynchronous programming
- **AndroidX** - Modern Android components
- **Material Design 3** - UI components

### Networking
- **OkHttp 4.12** - HTTP/2 client
- **Gson** - JSON serialization

### Voice
- **Stream WebRTC Android** - Modern WebRTC implementation
- **AndroidX Media** - Audio processing

### Graphics
- **OpenGL ES 3.2** - 3D rendering
- Native graphics libraries

## 🔧 Configuration

### Voice Settings

Configure voice chat in `LinkpointVoiceManager.kt`:

```kotlin
// Enable echo cancellation
.setUseHardwareAcousticEchoCanceler(true)

// Enable noise suppression
.setUseHardwareNoiseSuppressor(true)
```

### Graphics Settings

Adjust graphics quality in `LinkpointRenderPipeline.kt`:

```kotlin
// Enable/disable features
GLES32.glEnable(GLES32.GL_DEPTH_TEST)
GLES32.glEnable(GLES32.GL_CULL_FACE)
```

## 🧪 Testing

Run unit tests:
```bash
./gradlew test
```

Run instrumentation tests:
```bash
./gradlew connectedAndroidTest
```

## 📱 Permissions

Linkpoint requires the following permissions:

- **INTERNET** - Network communication
- **RECORD_AUDIO** - Voice chat
- **MODIFY_AUDIO_SETTINGS** - Audio configuration
- **BLUETOOTH** - Bluetooth headset support
- **ACCESS_NETWORK_STATE** - Connection monitoring

## 🔐 Privacy & Security

- All credentials are stored securely using Android Keystore
- Voice data is encrypted end-to-end
- No telemetry or analytics by default
- HTTPS/TLS 1.3 for all network communication

## 🛠️ Development Roadmap

### Phase 1: Core Features (Q1 2025) ✅
- [x] Modern Kotlin architecture
- [x] WebRTC voice integration
- [x] PBR graphics pipeline
- [x] Authentication system
- [x] LLSD protocol support

### Phase 2: Enhanced Features (Q2 2025)
- [ ] Inventory management
- [ ] Chat system
- [ ] Avatar customization
- [ ] World navigation
- [ ] Gesture support

### Phase 3: Advanced Features (Q3 2025)
- [ ] Building tools
- [ ] Scripting support (LSL)
- [ ] Advanced graphics (raytracing)
- [ ] VR/AR support
- [ ] Mesh upload

### Phase 4: Polish (Q4 2025)
- [ ] Performance optimization
- [ ] Localization
- [ ] Accessibility features
- [ ] Extensive testing
- [ ] Documentation

## 🤝 Contributing

Contributions are welcome! Please read our [Contributing Guide](CONTRIBUTING.md) for details.

### Development Guidelines

1. **Code Style**: Follow Kotlin coding conventions
2. **Commits**: Use conventional commit messages
3. **Testing**: Add tests for new features
4. **Documentation**: Update docs for API changes

## 📄 License

This project is licensed under the GPL v2 License - see [LICENSE](LICENSE) file for details.

## ⚖️ Legal & Trademarks

- Second Life is a trademark of Linden Lab.
- This project is compatible with Second Life™ protocols but is **not affiliated with or endorsed by Linden Lab**.
- This software is not provided or supported by Linden Lab, the makers of Second Life.
- Linkpoint complies with [Linden Lab's Third-Party Viewer Policy](https://secondlife.com/corporate/third-party-viewers).

## 🙏 Acknowledgments

- **Linden Lab** - Second Life protocol and grid infrastructure
- **libopenmv** - OpenMetaverse library inspiration
- **Lumiya Viewer** - Reference implementation
- **WebRTC Project** - Modern voice technology
- **Android Open Source Project** - Platform foundation

## 📧 Contact

- **Project Lead**: Your Name
- **Email**: your.email@example.com
- **Discord**: [Linkpoint Community](https://discord.gg/linkpoint)
- **Website**: https://linkpoint.app

## 🔗 Links

- [Second Life](https://secondlife.com)
- [Second Life Open Source Portal](https://wiki.secondlife.com/wiki/Open_Source_Portal)
- [OpenSimulator](http://opensimulator.org)
- [WebRTC](https://webrtc.org)

---

**Made with ❤️ for the Second Life community**

*Bringing modern technology to virtual worlds since 2025*