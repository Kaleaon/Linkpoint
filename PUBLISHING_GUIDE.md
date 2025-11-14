# Publishing Guide for Linkpoint

This guide explains how to prepare Linkpoint for publishing to Google Play Store and other distribution channels.

## Prerequisites

- Android Studio or Android SDK installed
- Java Development Kit (JDK) 17 or higher
- A Google Play Console developer account (for Play Store publishing)

## Building the App

### Debug Build (Testing)

For testing purposes, you can build a debug APK:

```bash
./gradlew assembleDebug
```

The debug APK will be located at: `app/build/outputs/apk/debug/app-debug.apk`

### Release Build (Production)

#### Step 1: Create a Signing Keystore

If you don't already have a keystore, create one:

```bash
keytool -genkey -v -keystore linkpoint-release.keystore -alias linkpoint -keyalg RSA -keysize 2048 -validity 10000
```

You will be prompted for:
- Keystore password
- Key password
- Your name and organization details

**IMPORTANT**: Keep your keystore file and passwords secure. You cannot update your app on Play Store without the original keystore.

#### Step 2: Create keystore.properties

Create a file named `keystore.properties` in the project root directory:

```properties
storeFile=/absolute/path/to/linkpoint-release.keystore
storePassword=your_keystore_password
keyAlias=linkpoint
keyPassword=your_key_password
```

**SECURITY NOTE**: 
- Never commit `keystore.properties` to version control
- The file is already in `.gitignore`
- Store keystore credentials securely (e.g., password manager, CI/CD secrets)

#### Step 3: Build Release APK

```bash
./gradlew assembleRelease
```

The release APK will be located at: `app/build/outputs/apk/release/app-release.apk`

If `keystore.properties` is not found, the build will use debug signing (suitable for testing but NOT for Play Store).

## Publishing to Google Play Store

### 1. Prepare Store Listing

Before uploading, prepare:
- App title: "Linkpoint"
- Short description (80 characters)
- Full description (4000 characters)
- Screenshots (at least 2, recommended 4-8)
- Feature graphic (1024 x 500)
- App icon (512 x 512)
- Privacy policy URL

### 2. Upload APK

1. Log in to [Google Play Console](https://play.google.com/console)
2. Select your app or create a new app
3. Go to "Release" → "Production" (or "Internal testing" for beta)
4. Click "Create new release"
5. Upload your signed `app-release.apk`
6. Fill in release notes
7. Review and roll out

### 3. Content Rating

Complete the content rating questionnaire in Play Console:
- Navigate to "Policy" → "App content"
- Complete the questionnaire honestly
- Generate rating certificate

### 4. Required Declarations

Ensure you've completed:
- Target audience and content
- Privacy policy
- Data safety form
- Ads declaration (if applicable)

## Publishing to Alternative Stores

### F-Droid

For F-Droid publishing:
1. Ensure all dependencies are open source
2. Fork the [F-Droid metadata repository](https://gitlab.com/fdroid/fdroiddata)
3. Add metadata for Linkpoint
4. Submit merge request

### Amazon Appstore

1. Create an [Amazon Developer Account](https://developer.amazon.com/apps-and-games)
2. Upload the signed APK
3. Complete product listing similar to Play Store

### Direct APK Distribution

You can distribute APKs directly:
- Host on your website
- Use GitHub Releases (already configured in `.github/workflows/deploy.yml`)
- Provide installation instructions for users to enable "Install from unknown sources"

## Continuous Integration / Deployment

The project includes automated workflows:

### Build Workflow (`.github/workflows/build-linkpoint.yml`)
- Triggers on push to main/develop branches
- Builds both debug and release APKs
- Runs tests and lint checks
- Uploads artifacts

### Release Workflow (`.github/workflows/build-release.yml`)
- Triggers on push to main branch
- Builds release APK
- Runs quality checks

### Deploy Workflow (`.github/workflows/deploy.yml`)
- Triggers on version tags (e.g., `v1.0.0`)
- Creates GitHub releases
- Optionally deploys to Firebase App Distribution

### Setting up CI/CD Secrets

For automated builds with proper signing, add these secrets to your GitHub repository:

1. Go to repository Settings → Secrets and variables → Actions
2. Add the following secrets:
   - `KEYSTORE_BASE64`: Base64-encoded keystore file
   - `KEYSTORE_PASSWORD`: Your keystore password
   - `KEY_ALIAS`: Your key alias
   - `KEY_PASSWORD`: Your key password

To encode your keystore:
```bash
base64 linkpoint-release.keystore | tr -d '\n' > keystore_base64.txt
```

## Version Management

### Updating Version

Edit `app/build.gradle`:

```gradle
versionCode 68      // Increment by 1 for each release
versionName "3.4.4" // Semantic version
```

Version code must always increase for Play Store updates.

### Creating a Release Tag

After building and testing:

```bash
git tag -a v3.4.4 -m "Release version 3.4.4"
git push origin v3.4.4
```

This triggers the deploy workflow automatically.

## Pre-Release Checklist

Before publishing to production:

- [ ] All tests pass (`./gradlew test`)
- [ ] Lint checks pass (`./gradlew lint`)
- [ ] App builds successfully with release signing
- [ ] Version code incremented
- [ ] Version name updated
- [ ] CHANGELOG.md updated
- [ ] Release notes prepared
- [ ] Keystore and passwords secured
- [ ] Screenshots updated (if UI changed)
- [ ] Privacy policy reviewed
- [ ] All required Play Console declarations completed

## Troubleshooting

### Issue: "keystore.properties not found"

**Solution**: Create the file as described in Step 2 above, or the build will use debug signing.

### Issue: "Keystore was tampered with, or password was incorrect"

**Solution**: Verify your keystore password in `keystore.properties` is correct.

### Issue: "Build fails with resource errors"

**Solution**: Run `./gradlew clean` first, then rebuild.

### Issue: "APK not optimized for Play Store"

**Solution**: This is expected as minification is disabled. The larger APK size is a known trade-off for this project.

## Security Best Practices

1. **Never commit sensitive files**:
   - `keystore.properties`
   - `*.keystore`, `*.jks`
   - Any files with passwords

2. **Secure keystore backup**:
   - Keep encrypted backups
   - Store in multiple secure locations
   - Document keystore details securely

3. **Use environment variables for CI/CD**:
   - Store secrets in GitHub Secrets
   - Never hardcode credentials

4. **Regular security updates**:
   - Keep dependencies updated
   - Monitor security advisories
   - Run security scans

## Resources

- [Google Play Console](https://play.google.com/console)
- [Android App Signing](https://developer.android.com/studio/publish/app-signing)
- [Play Store Publishing Guide](https://developer.android.com/distribute/best-practices/launch)
- [Semantic Versioning](https://semver.org/)

## Support

For issues or questions:
- Open an issue on [GitHub](https://github.com/Kaleaon/Linkpoint/issues)
- Check existing documentation in the `docs/` directory
- Review GitHub Actions workflow logs for CI/CD issues
