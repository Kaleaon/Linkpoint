# Linkpoint Quick Start Guide

**Linkpoint** - Modern Android Second Life Viewer  
**Version**: 3.4.3 (Modernization Phase 1 Complete)  
**Last Updated**: November 2, 2025

---

## 🚀 What is Linkpoint?

Linkpoint is a modern Android application for connecting to Second Life and OpenSimulator virtual worlds. Built on the Lumiya codebase with extensive modernization, it features:

- ✅ **Modern Kotlin architecture** (1,954 Kotlin files)
- ✅ **PBR rendering** with Filament engine
- ✅ **WebRTC voice chat** (modern replacement for Vivox)
- ✅ **HTTP/2 + WebSocket** protocol support
- ✅ **Material Design 3** UI components
- ✅ **Advanced avatar system** (133-bone skeleton, 56 attachments)

---

## 📋 Prerequisites

### Development Requirements
- **Android Studio**: Arctic Fox or newer
- **JDK**: Java 17+ (Eclipse Temurin recommended)
- **Android SDK**: API 34 (compile), API 24+ (minimum)
- **Gradle**: 8.5 (included via wrapper)
- **Git**: For version control

### System Requirements
- **Memory**: 8GB+ RAM (16GB recommended)
- **Storage**: 10GB+ free space
- **OS**: Linux, macOS, or Windows with WSL2

---

## ⚡ Quick Start (5 Minutes)

### 1. Clone the Repository
```bash
git clone https://github.com/Kaleaon/Linkpoint.git
cd Linkpoint
```

### 2. Open in Android Studio
1. Launch Android Studio
2. Select **File → Open**
3. Navigate to the Linkpoint directory
4. Click **OK**

### 3. Sync Gradle
Android Studio will automatically:
- Download Gradle 8.5
- Sync dependencies
- Configure Android SDK

Wait for "Gradle sync complete" notification.

### 4. Build the App
```bash
# Debug build
./gradlew assembleDebug

# Release build (for testing)
./gradlew assembleRelease
```

**Build time**: ~2 minutes (first build), ~30 seconds (subsequent builds)

### 5. Run on Device/Emulator
1. Connect Android device (API 24+) or start emulator
2. Click **Run** (green play button) in Android Studio
3. Select your device
4. App will install and launch

---

## 📱 App Structure

### Main Features (Current Status)

**✅ Implemented**:
- Login system (CleanLoginActivity - launcher)
- World rendering (OpenGL ES + Filament)
- Avatar system (mesh, rigging, animation)
- Modern UI framework
- Network layer (UDP, HTTP/2, WebSocket)
- Voice chat (WebRTC)

**🔄 In Progress**:
- Second Life grid connectivity testing
- Inventory management
- Chat & IM
- Object interaction
- Settings & preferences

### Application Flow
```
Launch → CleanLoginActivity
          ↓
       Login to Grid
          ↓
    WorldViewActivity
          ↓
   (Render Second Life world)
```

---

## 🛠️ Development Commands

### Building
```bash
# Clean build
./gradlew clean

# Debug APK (for testing)
./gradlew assembleDebug
# Output: app/build/outputs/apk/debug/app-debug.apk

# Release APK (unsigned)
./gradlew assembleRelease
# Output: app/build/outputs/apk/release/app-release-unsigned.apk
```

### Testing
```bash
# Run all unit tests
./gradlew test

# Run specific test
./gradlew test --tests LLSDProtocolTests

# Generate test report
./gradlew test --info
# Report: app/build/reports/tests/test/index.html
```

### Code Quality
```bash
# Lint check
./gradlew lint

# Format code (ktlint - to be added)
./gradlew ktlintFormat

# Generate API docs (Dokka - to be added)
./gradlew dokkaHtml
```

### Troubleshooting
```bash
# Check Gradle version
./gradlew --version

# Check architecture support
./gradlew archInfo

# Clean everything (including caches)
./gradlew clean --no-daemon
rm -rf .gradle build app/build
```

---

## 📂 Project Structure

```
Linkpoint/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/lumiyaviewer/lumiya/
│   │   │   │   ├── modern/          # Modern components (30 implementations)
│   │   │   │   │   ├── auth/        # OAuth2 + legacy auth
│   │   │   │   │   ├── avatar/      # Advanced avatar system
│   │   │   │   │   ├── graphics/    # PBR rendering pipeline
│   │   │   │   │   ├── protocol/    # HTTP/2, WebSocket, UDP
│   │   │   │   │   ├── llsd/        # LLSD codec
│   │   │   │   │   ├── voice/       # WebRTC voice
│   │   │   │   │   └── ...
│   │   │   │   ├── ui/              # UI activities (20+)
│   │   │   │   │   ├── login/       # Login screens
│   │   │   │   │   ├── render/      # World rendering
│   │   │   │   │   ├── chat/        # Chat UI
│   │   │   │   │   └── ...
│   │   │   │   ├── animesh/         # Avatar core (133 bones)
│   │   │   │   ├── slproto/         # Second Life protocol
│   │   │   │   └── ...
│   │   │   ├── res/                 # Android resources
│   │   │   └── AndroidManifest.xml
│   │   └── test/                    # Unit tests
│   └── build.gradle                 # App module config
├── docs/                            # Documentation (50+ files)
├── scripts/                         # Build scripts
├── builds/                          # Build artifacts
├── MASTER_PLAN_MODERNIZATION.md    # 16-week roadmap
├── FEATURE_STATUS_INVENTORY.md     # Feature tracking
├── QUICKSTART.md                    # This file
├── README.md                        # Main README
├── build.gradle                     # Root build config
└── settings.gradle                  # Gradle settings
```

---

## 🎯 Key Features Showcase

### 1. Modern Authentication
```kotlin
val authManager = ModernAuthManager(context)
val result = authManager.login(
    firstName = "John",
    lastName = "Doe",
    password = "secure_password",
    grid = SecondLifeGrid.MAIN_GRID
)
```

### 2. PBR Rendering
```kotlin
val renderPipeline = ModernRenderPipeline(context)
renderPipeline.enablePBR(true)
renderPipeline.setQualityLevel(QualityLevel.HIGH)
```

### 3. WebRTC Voice
```kotlin
val voiceManager = WebRTCManager()
voiceManager.connect(channel = "spatial_audio")
voiceManager.enableSpatialAudio(true)
```

### 4. LLSD Protocol
```kotlin
val agentData = LLSD.Map(mapOf(
    "agent_id" to LLSD.UUID(uuid),
    "name" to LLSD.String("Avatar Name"),
    "position" to LLSD.Array(listOf(
        LLSD.Real(128.0),
        LLSD.Real(128.0),
        LLSD.Real(23.5)
    ))
))
```

---

## 🧪 Testing

### Running Tests
```bash
# All tests
./gradlew test

# View results
open app/build/reports/tests/test/index.html
```

### Current Test Coverage
- **LLSD Protocol**: 20+ tests ✅
- **Authentication**: In progress
- **Network Layer**: In progress
- **Graphics**: In progress
- **Target**: 80%+ coverage

### Writing Tests
```kotlin
class MyFeatureTest {
    @Test
    fun `test feature works correctly`() {
        // Arrange
        val feature = MyFeature()
        
        // Act
        val result = feature.doSomething()
        
        // Assert
        assertEquals(expected, result)
    }
}
```

---

## 📚 Documentation

### User Guides
- **[Master Plan](MASTER_PLAN_MODERNIZATION.md)** - 16-week roadmap
- **[Feature Status](FEATURE_STATUS_INVENTORY.md)** - Component tracking
- **[Implementation Roadmap](docs/Implementation_Roadmap.md)** - Technical details
- **[Lumiya Modernization Guide](docs/Lumiya_Modernization_Guide.md)** - Architecture

### Developer Guides
- **[AI Knowledge Base](LINKPOINT_AI_KNOWLEDGE_BASE.md)** - LLSD, RLV specs
- **[Graphics Roadmap](docs/Graphics_Engine_Roadmap.md)** - Rendering pipeline
- **[API Documentation](docs/Modern_Components_API_Documentation.md)** - Modern APIs
- **[CPP Integration](docs/CPP_Integration_Guide.md)** - Native components

### API Reference
```bash
# Generate API docs (to be implemented)
./gradlew dokkaHtml
# Output: build/dokka/html/index.html
```

---

## 🐛 Troubleshooting

### Build Fails
**Problem**: Gradle sync fails  
**Solution**:
```bash
./gradlew clean
rm -rf .gradle build
./gradlew build
```

**Problem**: Out of memory  
**Solution**: Increase heap size in `gradle.properties`
```properties
org.gradle.jvmargs=-Xmx4g
```

### App Crashes
**Problem**: App crashes on launch  
**Check**:
1. Device API level (24+)
2. LogCat for stack traces
3. Permissions in AndroidManifest

**Problem**: Performance issues  
**Solution**: 
- Reduce quality settings
- Enable hardware acceleration
- Check device specifications

### Connection Issues
**Problem**: Can't connect to Second Life  
**Check**:
1. Internet connection
2. Firewall settings (UDP port 13000-13050)
3. Grid status (https://status.secondlifegrid.net)

---

## 🤝 Contributing

### Getting Started
1. Fork the repository
2. Create feature branch: `git checkout -b feature/amazing-feature`
3. Make changes
4. Write tests
5. Commit: `git commit -m 'Add amazing feature'`
6. Push: `git push origin feature/amazing-feature`
7. Open Pull Request

### Code Style
- **Kotlin**: Follow Kotlin conventions
- **Naming**: PascalCase for classes, camelCase for functions
- **Documentation**: KDoc for public APIs
- **Tests**: Write tests for new features

### Commit Messages
```
Type: Brief description

Detailed explanation if needed.

- List of changes
- More details

Closes #issue-number
```

Types: `feat`, `fix`, `docs`, `style`, `refactor`, `test`, `chore`

---

## 📞 Support

### Getting Help
- **Issues**: [GitHub Issues](https://github.com/Kaleaon/Linkpoint/issues)
- **Discussions**: [GitHub Discussions](https://github.com/Kaleaon/Linkpoint/discussions)
- **Wiki**: [Project Wiki](https://github.com/Kaleaon/Linkpoint/wiki) (coming soon)

### Reporting Bugs
1. Check existing issues
2. Create new issue with:
   - Device info (model, Android version)
   - Steps to reproduce
   - Expected vs actual behavior
   - Screenshots/logs

### Feature Requests
1. Check feature status inventory
2. Open discussion in GitHub Discussions
3. If accepted, create issue

---

## 🎉 Quick Win - Run the Demo

### 1. Build and Install
```bash
./gradlew assembleDebug
adb install app/build/outputs/apk/debug/app-debug.apk
```

### 2. Launch App
- Find "Linkpoint" icon
- Tap to launch
- You'll see CleanLoginActivity

### 3. Explore Modern Components
The app currently has:
- Modern login UI
- World view activity
- Performance monitoring
- Connection diagnostics

### 4. Check Logs
```bash
adb logcat | grep -i linkpoint
```

---

## 📊 Status Dashboard

### Overall Progress: 60%
- ✅ Core Protocol: 70%
- ✅ Graphics Pipeline: 75%
- ✅ Network Layer: 70%
- ✅ Avatar System: 85%
- 🔄 UI Framework: 65%
- 🔄 Features: 50%
- 🔴 Testing: 10%

### Current Phase
**Phase 1**: Architecture Assessment ✅ COMPLETE  
**Phase 2**: Core Protocol & Connectivity (Starting)

### Next Milestones
- [ ] Week 2: Second Life authentication working
- [ ] Week 4: Modern UI framework complete
- [ ] Week 8: PBR rendering optimized
- [ ] Week 12: Core features complete
- [ ] Week 16: Production release

---

## 🚀 What's Next?

### This Week (Week 2)
1. Test Second Life authentication
2. Verify network connectivity
3. Complete LLSD protocol tests
4. Begin UI modernization
5. Set up CI/CD

### This Month (Weeks 2-5)
1. Complete network layer
2. Modern UI foundation
3. Graphics optimization
4. Feature implementation starts

### This Quarter (Weeks 2-16)
1. All core features complete
2. 80%+ test coverage
3. Production-ready release
4. Google Play submission

---

## 🌟 Success So Far

✅ **1,954 Kotlin files** - Full migration complete  
✅ **30 modern components** - Framework in place  
✅ **Build system working** - APK generation successful  
✅ **Architecture solid** - Modern patterns established  
✅ **Documentation complete** - 50+ docs, 70KB+  

**We're 60% there! Let's finish strong!** 💪

---

*Last Updated: November 2, 2025*  
*Version: 1.0*  
*Status: Active Development*
