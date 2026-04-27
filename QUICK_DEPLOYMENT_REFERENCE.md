# Linkpoint Quick Deployment Reference

## 🚀 Quick Commands

### Local Build
```bash
cd Linkpoint
./gradlew clean assembleDebug
```

### Local Release (with signing)
```bash
cd Linkpoint
# First, configure keystore.properties
./gradlew clean assembleRelease
```

### Deploy via Tag
```bash
# Create and push version tag
git tag v1.0.0
git push origin v1.0.0
# GitHub Actions will automatically build and deploy
```

### Manual Deployment
1. Go to: https://github.com/YOUR_ORG/YOUR_REPO/actions/workflows/deploy.yml
2. Click "Run workflow"
3. Enter version (e.g., v1.0.0) and environment
4. Click "Run workflow"

## 🔐 Required GitHub Secrets

### For Signed Releases
```bash
# Generate base64 of keystore
base64 -i release.keystore | pbcopy  # macOS
base64 release.keystore              # Linux

# Add to GitHub: Settings > Secrets > Actions
KEYSTORE_BASE64      = <paste base64 output>
KEYSTORE_PASSWORD    = your_keystore_password
KEY_ALIAS           = your_key_alias
KEY_PASSWORD        = your_key_password
```

### For Firebase (Optional)
```bash
FIREBASE_APP_ID              = 1:1234567890:android:abcdef
FIREBASE_SERVICE_ACCOUNT     = <base64 of service account JSON>
```

## 📋 Pre-Deployment Checklist

- [ ] All tests passing: `./gradlew test`
- [ ] Lint checks passing: `./gradlew lintDebug`
- [ ] Version updated in build.gradle.kts
- [ ] CHANGELOG.md updated (if exists)
- [ ] GitHub Secrets configured
- [ ] Tested on device/emulator

## 🎯 Build Variants

### Debug
```bash
./gradlew assembleDebug
# Output: build/outputs/apk/debug/linkpoint-debug.apk
```

### Release
```bash
./gradlew assembleRelease
# Output: build/outputs/apk/release/linkpoint-release.apk
```

## 📦 APK Locations

| Variant | Location | Size |
|---------|----------|------|
| Debug | `build/outputs/apk/debug/` | ~40-60 MB |
| Release | `build/outputs/apk/release/` | ~30-50 MB |

## 🔍 Quick Checks

### Verify Build Setup
```bash
cd Linkpoint
./gradlew --version
./gradlew tasks --group build
```

### Check Dependencies
```bash
./gradlew dependencies
```

### Run Tests
```bash
./gradlew testDebugUnitTest
```

### Run Lint
```bash
./gradlew lintDebug
```

## 🐛 Quick Troubleshooting

### Build Fails
```bash
./gradlew clean
./gradlew --refresh-dependencies
./gradlew assembleDebug --stacktrace
```

### Signing Issues
1. Check keystore.properties exists
2. Verify keystore file location
3. Check passwords are correct
4. Ensure GitHub Secrets are set

### Memory Issues
```bash
export GRADLE_OPTS="-Xmx4g -XX:MaxMetaspaceSize=1g"
./gradlew clean assembleDebug
```

## 📊 Workflow Status

Check build status:
```
https://github.com/YOUR_ORG/YOUR_REPO/actions
```

View releases:
```
https://github.com/YOUR_ORG/YOUR_REPO/releases
```

## 🔄 Version Bump

1. Update `build.gradle.kts`:
   ```kotlin
   versionCode = 2
   versionName = "1.1.0"
   ```

2. Create tag:
   ```bash
   git tag v1.1.0
   git push origin v1.1.0
   ```

## 🎊 First Deployment

### Step 1: Configure Signing
```bash
cd Linkpoint
cp keystore.properties.template keystore.properties
# Edit keystore.properties with your values
```

### Step 2: Add GitHub Secrets
- Go to repository Settings > Secrets
- Add all required secrets (see above)

### Step 3: Push Changes
```bash
git add .
git commit -m "Setup deployment configuration"
git push origin main
```

### Step 4: Create First Release
```bash
git tag v1.0.0
git push origin v1.0.0
```

### Step 5: Monitor
- Check Actions tab for build progress
- Download APK from Releases when complete

## 📚 Documentation Links

- Full Guide: `.github/DEPLOYMENT_GUIDE.md`
- Pipeline Details: `.github/BUILD_PIPELINE.md`
- Complete Summary: `DEPLOYMENT_READY_SUMMARY.md`

## ⚡ Pro Tips

1. **Use caching**: Subsequent builds are much faster
2. **Run lint locally**: Fix issues before pushing
3. **Test on device**: Always test APK before releasing
4. **Keep secrets safe**: Never commit keystore files
5. **Version consistently**: Follow semantic versioning
6. **Monitor builds**: Check Actions tab regularly

## 🔐 Security Reminders

- ❌ Never commit keystore files
- ❌ Never commit passwords
- ❌ Never commit keystore.properties
- ✅ Always use GitHub Secrets
- ✅ Always check .gitignore
- ✅ Always use secure passwords

---

**Need help?** See the full deployment guide at `.github/DEPLOYMENT_GUIDE.md`