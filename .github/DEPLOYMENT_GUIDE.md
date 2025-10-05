# Linkpoint Deployment Guide

This guide explains how to deploy the Linkpoint Android application using GitHub Actions.

## Table of Contents

1. [Prerequisites](#prerequisites)
2. [Setting Up Signing](#setting-up-signing)
3. [GitHub Secrets Configuration](#github-secrets-configuration)
4. [Deployment Workflows](#deployment-workflows)
5. [Deployment Targets](#deployment-targets)
6. [Versioning](#versioning)
7. [Troubleshooting](#troubleshooting)

## Prerequisites

- GitHub repository with appropriate permissions
- Android keystore for signing release builds
- (Optional) Firebase project for App Distribution
- (Optional) Google Play Console account

## Setting Up Signing

### Generate a Keystore

If you don't have a keystore, generate one:

```bash
keytool -genkey -v -keystore release.keystore -alias linkpoint -keyalg RSA -keysize 2048 -validity 10000
```

**Important:** Store your keystore and passwords securely! Never commit them to the repository.

### Configure Local Signing

1. Copy the keystore template:
   ```bash
   cd Linkpoint
   cp keystore.properties.template keystore.properties
   ```

2. Edit `keystore.properties` with your actual values:
   ```properties
   storeFile=release.keystore
   storePassword=YOUR_STORE_PASSWORD
   keyAlias=YOUR_KEY_ALIAS
   keyPassword=YOUR_KEY_PASSWORD
   ```

3. Place your `release.keystore` file in the `Linkpoint/` directory

4. Verify `.gitignore` excludes these files:
   ```
   keystore.properties
   *.keystore
   *.jks
   ```

## GitHub Secrets Configuration

Configure the following secrets in your GitHub repository settings (Settings > Secrets and variables > Actions):

### Required for Release Builds

1. **KEYSTORE_BASE64**
   - Base64-encoded keystore file
   - Generate with: `base64 -i release.keystore | pbcopy` (macOS) or `base64 release.keystore | xclip` (Linux)

2. **KEYSTORE_PASSWORD**
   - Password for the keystore

3. **KEY_ALIAS**
   - Alias of the key in the keystore

4. **KEY_PASSWORD**
   - Password for the key

### Optional for Firebase App Distribution

5. **FIREBASE_APP_ID**
   - Firebase App ID from Firebase Console
   - Format: `1:1234567890:android:abcdef`

6. **FIREBASE_SERVICE_ACCOUNT**
   - Firebase service account JSON (base64 encoded)
   - Download from Firebase Console > Project Settings > Service Accounts

## Deployment Workflows

### 1. Build Linkpoint (`build-linkpoint.yml`)

**Trigger:** Automatic on push to main/develop or cursor branches

**Purpose:** Continuous integration and testing

**Outputs:**
- Debug APK
- Release APK (if configured)
- Build reports
- Test results
- Lint reports

**Usage:** Automatically runs on every commit

### 2. Deploy (`deploy.yml`)

**Trigger:** Manual or on version tags

**Purpose:** Production deployment

**Outputs:**
- Signed release APK
- GitHub Release
- Firebase App Distribution (if configured)
- Checksums (SHA256, MD5)

#### Manual Deployment

1. Go to Actions > Deploy Linkpoint
2. Click "Run workflow"
3. Fill in the parameters:
   - **Version:** e.g., `v1.0.0`
   - **Environment:** production, staging, or beta
4. Click "Run workflow"

#### Tag-Based Deployment

```bash
# Create and push a version tag
git tag v1.0.0
git push origin v1.0.0
```

The deployment workflow will automatically:
1. Build signed release APK
2. Run tests and quality checks
3. Create GitHub Release
4. Deploy to Firebase (if configured)
5. Generate checksums

## Deployment Targets

### GitHub Releases

**Always enabled** - No additional setup required

- APKs are uploaded to GitHub Releases
- Automatic release notes generation
- Checksums included
- Download links available to all users

### Firebase App Distribution

**Optional** - Requires Firebase setup

Benefits:
- Beta testing distribution
- Targeted tester groups
- In-app update notifications
- Crash reporting integration

Setup:
1. Create Firebase project at https://console.firebase.google.com
2. Add Android app to Firebase
3. Configure GitHub Secrets (see above)
4. Specify tester groups in workflow

### Google Play Store

**Coming soon** - Not yet implemented

Future features:
- Automated Play Store uploads
- Staged rollouts
- Release track management

## Versioning

### Version Format

Linkpoint uses semantic versioning: `MAJOR.MINOR.PATCH`

Examples:
- `v1.0.0` - Initial release
- `v1.1.0` - New features
- `v1.1.1` - Bug fixes
- `v2.0.0` - Breaking changes

### Version Code

Automatically generated from timestamp in deployment workflow.

### Updating Version

#### For Tagged Releases

The deployment workflow automatically updates version from the tag:
```bash
git tag v1.2.0
git push origin v1.2.0
```

#### For Manual Releases

Update in `Linkpoint/build.gradle.kts`:
```kotlin
versionCode = 2
versionName = "1.2.0"
```

## Build Variants

### Debug

- **Suffix:** `.debug`
- **Signing:** Debug keystore (automatic)
- **Minification:** Disabled
- **Use case:** Development and testing

### Release

- **Suffix:** None
- **Signing:** Release keystore (configured)
- **Minification:** Optional (currently disabled)
- **Use case:** Production deployment

## Deployment Checklist

Before deploying a new version:

- [ ] All tests passing locally
- [ ] Code reviewed and merged to main
- [ ] CHANGELOG.md updated (if exists)
- [ ] Version number decided
- [ ] Keystore and secrets configured
- [ ] Tested on multiple devices/emulators
- [ ] Release notes prepared

## Troubleshooting

### Build Fails with Signing Error

**Problem:** Release build fails with keystore/signing errors

**Solution:**
1. Verify all secrets are set correctly in GitHub
2. Check KEYSTORE_BASE64 is properly encoded
3. Ensure passwords match keystore
4. Verify key alias exists in keystore

### APK Not Signed

**Problem:** Release APK is unsigned

**Solution:**
1. Check `keystore.properties` exists and is configured
2. Verify `build.gradle.kts` signing configuration
3. Ensure workflow has access to secrets
4. Check workflow logs for signing steps

### Firebase Upload Fails

**Problem:** Firebase App Distribution step fails

**Solution:**
1. Verify FIREBASE_APP_ID is correct
2. Check FIREBASE_SERVICE_ACCOUNT has proper permissions
3. Ensure app exists in Firebase Console
4. Verify tester groups exist

### Version Conflict

**Problem:** Version tag already exists

**Solution:**
```bash
# Delete local and remote tag
git tag -d v1.0.0
git push origin :refs/tags/v1.0.0

# Create new tag
git tag v1.0.1
git push origin v1.0.1
```

### Build Timeout

**Problem:** Build exceeds 45-minute timeout

**Solution:**
1. Check for hanging tests
2. Verify Gradle daemon is disabled
3. Review build configuration
4. Consider splitting into multiple jobs

## Monitoring

### Build Status

Monitor builds at: `https://github.com/YOUR_ORG/YOUR_REPO/actions`

### Release Status

Check releases at: `https://github.com/YOUR_ORG/YOUR_REPO/releases`

### Firebase Distribution

View distributions in Firebase Console > App Distribution

## Security Best Practices

1. **Never commit sensitive files:**
   - Keystore files
   - keystore.properties
   - Service account JSONs

2. **Use GitHub Secrets for:**
   - All passwords
   - API keys
   - Service accounts

3. **Rotate credentials regularly:**
   - Update keystore passwords
   - Regenerate service accounts
   - Update GitHub secrets

4. **Limit access:**
   - Restrict who can trigger workflows
   - Use environment protection rules
   - Enable required reviews

## Support

For deployment issues:
1. Check GitHub Actions logs
2. Review this guide
3. Consult BUILD_PIPELINE.md
4. Open an issue on GitHub

## References

- [Android App Signing](https://developer.android.com/studio/publish/app-signing)
- [GitHub Actions Documentation](https://docs.github.com/en/actions)
- [Firebase App Distribution](https://firebase.google.com/docs/app-distribution)
- [Semantic Versioning](https://semver.org/)

---

**Last Updated:** October 5, 2025  
**Status:** ✅ Production Ready