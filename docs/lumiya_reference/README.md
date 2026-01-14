# Lumiya Reference Code

This directory contains decompiled Lumiya APK source code for reference and study purposes.

## Purpose

The decompiled code here is used to understand how Lumiya implements:

- **Login Flow** (`slproto/SLGridConnection.java`, `slproto/SLAgentCircuit.java`)
- **Module System** (`slproto/modules/SLModules.java`)
- **Avatar Appearance/Skeleton** (`slproto/modules/SLAvatarAppearance.java`)
- **Texture Fetching** (`slproto/modules/texfetcher/SLTextureFetcher.java`)
- **Rendering** (`render/` directory)
- **Asset Management** (`res/` directory)

## Key Files for Study

### Connection & Login
- `lumiyaviewer/lumiya/slproto/SLGridConnection.java` - Main grid connection handler
- `lumiyaviewer/lumiya/slproto/SLAgentCircuit.java` - Agent circuit management
- `lumiyaviewer/lumiya/slproto/auth/SLAuth.java` - Authentication
- `lumiyaviewer/lumiya/slproto/auth/SLAuthReply.java` - Login response data

### Modules (initialized after login)
- `lumiyaviewer/lumiya/slproto/modules/SLModules.java` - Module registry
- `lumiyaviewer/lumiya/slproto/modules/SLAvatarAppearance.java` - Avatar appearance/baking
- `lumiyaviewer/lumiya/slproto/modules/SLAvatarControl.java` - Avatar movement
- `lumiyaviewer/lumiya/slproto/modules/SLInventory.java` - Inventory management

### Textures & Assets
- `lumiyaviewer/lumiya/slproto/modules/texfetcher/SLTextureFetcher.java` - Texture downloading
- `lumiyaviewer/lumiya/res/textures/TextureCache.java` - Texture caching

### Rendering
- `lumiyaviewer/lumiya/render/` - OpenGL ES rendering code

## Note

This code is for **reference only** and should not be directly copied. Use it to understand patterns and implement equivalent functionality in Kotlin for Linkpoint.

The decompiled `.java` files are excluded from git via `.gitignore` to keep the repository size manageable. Only this README is committed.

## How to Regenerate

If you need to study the decompiled code locally:

```bash
# Download and decompile Lumiya APK
pip install gdown
gdown "https://drive.google.com/uc?id=1dbEbqIZdLTDoAfFl7ybdI2Syb9wri-qD" -O lumiya.apk

# Install jadx (Java decompiler)
wget https://github.com/skylot/jadx/releases/download/v1.5.0/jadx-1.5.0.zip
unzip jadx-1.5.0.zip -d jadx

# Decompile
./jadx/bin/jadx -d lumiya_decompiled lumiya.apk

# Copy to this directory
cp -r lumiya_decompiled/sources/com/lumiyaviewer docs/lumiya_reference/
```
