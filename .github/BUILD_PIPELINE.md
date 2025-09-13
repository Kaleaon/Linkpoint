# Automated Build Pipeline for Linkpoint

This repository is configured with GitHub Actions to automatically build release-ready APK files for testing whenever code is pushed to the `main` branch.

## 🚀 Automated Builds

### How It Works
- **Trigger**: Every push to `main` branch or manual workflow dispatch
- **Build Time**: ~3-5 minutes for release APK
- **Outputs**: Both debug and release APK files
- **Retention**: APK artifacts kept for 30 days (7 days for quick builds)

### Available Workflows

#### 1. `build-release.yml` - Complete Build Pipeline
- Builds both debug and release APKs
- Includes comprehensive artifact naming with version info
- Provides detailed build summary with installation instructions
- Triggered on: pushes to `main`, PRs to `main`, manual dispatch

#### 2. `quick-release.yml` - Fast Release Build
- Builds only release APK for immediate testing
- Optimized for speed and simplicity
- Triggered on: pushes to `main` only

## 📱 Getting the APK

### After Each Push to Main:
1. Go to the **Actions** tab in the GitHub repository
2. Click on the latest workflow run
3. Scroll down to **Artifacts** section
4. Download the APK file (e.g., `linkpoint-release-20240913_054400`)
5. Extract the ZIP file to get the APK

### APK File Naming:
- **Release**: `Linkpoint-release-v3.4.3-abc1234.apk`
- **Debug**: `Linkpoint-debug-v3.4.3-abc1234.apk`
- Format: `Linkpoint-[type]-v[version]-[commit].apk`

## 🛠️ Build Configuration

### Release Build Features:
- ✅ **Code Minification**: Enabled with ProGuard/R8
- ✅ **Resource Shrinking**: Removes unused resources
- ✅ **Debug Signing**: Uses debug keystore for testing (not production)
- ✅ **Lint Bypassing**: Configured to build despite lint warnings
- ✅ **APK Optimization**: ~6.8MB (vs ~13.6MB debug)

### Build Environment:
- **OS**: Ubuntu Latest
- **Java**: OpenJDK 17 (Temurin distribution)
- **Android SDK**: Latest available
- **Gradle**: 8.0 (wrapper version)
- **Build Tools**: Auto-installed as needed

## 📋 Installation on Device

### Prerequisites:
1. Android device with API 21+ (Android 5.0+)
2. Enable "Install from Unknown Sources" in device settings

### Steps:
1. Download APK from GitHub Actions artifacts
2. Transfer APK to your Android device
3. Tap the APK file and install
4. Launch "Linkpoint Modern Demo" from app drawer

### What You'll See:
- Modern Material Design 3 interface
- Feature overview and component status
- Testing options for OAuth2, graphics, streaming, etc.
- Performance benchmarking tools

## 🔧 Development Workflow

### Making Changes:
1. Push code to `main` branch
2. Workflow automatically triggers
3. Wait ~3-5 minutes for build completion
4. Download and test the generated APK

### Manual Builds:
```bash
# Local release build
./gradlew assembleRelease

# Local debug build
./gradlew assembleDebug

# Clean build
./gradlew clean assembleRelease
```

### Troubleshooting:
- **Build fails**: Check Actions tab for error details
- **APK not installing**: Verify "Unknown Sources" is enabled
- **App crashes**: Use debug APK for better error reporting
- **Missing artifacts**: Check if workflow completed successfully

## 🔐 Security Notes

### Current Signing:
- **Debug keystore** used for testing releases
- Not suitable for Google Play Store distribution
- Fine for sideloading and testing purposes

### For Production:
- Configure proper release signing keystore
- Store signing keys securely in GitHub Secrets
- Update workflow to use production signing for releases

## 📊 Build Metrics

### Typical Build Times:
- **Clean + Release**: ~5 minutes
- **Release (cached)**: ~2-3 minutes
- **Debug**: ~2 minutes

### APK Sizes:
- **Release**: ~6.8MB (optimized)
- **Debug**: ~13.6MB (unoptimized)

### Cache Strategy:
- Gradle dependencies cached between runs
- Significant speed improvement for subsequent builds
- Cache invalidated when build files change

## 🎯 Future Enhancements

- [ ] Add automated testing before APK generation
- [ ] Implement proper release signing for distribution
- [ ] Add APK size monitoring and reporting
- [ ] Create automatic draft releases for version tags
- [ ] Add build notifications (Slack, email, etc.)
- [ ] Implement branch protection with required builds

## 📞 Support

If you encounter issues with the automated builds:
1. Check the workflow logs in GitHub Actions
2. Verify your changes don't break the build configuration
3. Test locally with `./gradlew assembleRelease` first
4. Create an issue with build logs if needed