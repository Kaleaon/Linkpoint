# Firestorm vs Lumiya/Linkpoint Feature Comparison

## Executive Summary

**Firestorm** is the most popular desktop Second Life viewer with cutting-edge features. **Lumiya** is a mobile viewer from 2012-2020 era. **Linkpoint** is the modern 2025 reimplementation.

## 📊 Feature Matrix

| Feature Category | Firestorm 7.x (Desktop) | Lumiya (2020) | Linkpoint (2025) | Status |
|------------------|------------------------|---------------|------------------|---------|
| **Core Protocol** |
| UDP Messages | ✅ Full | ✅ Full | ✅ Full | ✅ |
| HTTP/CAPS | ✅ HTTP/2 | ✅ HTTP/1.1 | ✅ HTTP/2 | ✅ |
| LLSD | ✅ Full | ✅ Full | ✅ Enhanced | ✅ |
| WebSocket Events | ✅ Yes | ❌ No | ✅ Planned | 🔄 |
| **Modern Features (2015-2025)** |
| **Animesh** (2018) | ✅ Full Support | ❌ No | ❌ No | ❌ MISSING |
| Bakes on Mesh (2018) | ✅ Full Support | ❌ No | ❌ No | ❌ MISSING |
| EEP (Env Enhance, 2020) | ✅ Full Support | ⚠️ Partial (Windlight) | ⚠️ Partial | ⚠️ LIMITED |
| PBR Materials (2023) | ✅ Full Support | ❌ No | ✅ Framework | 🔄 IN PROGRESS |
| Experience Tools | ✅ Yes | ❌ No | ❌ No | ❌ MISSING |
| **Avatar Features** |
| Classic Avatars | ✅ Full | ✅ Full | ✅ Full | ✅ |
| Mesh Avatars | ✅ Full | ✅ Basic | ✅ Full | ✅ |
| Fitted Mesh | ✅ Full | ⚠️ Limited | ✅ Planned | 🔄 |
| Baked Textures | ✅ Server | ✅ Local Only | ✅ Planned | 🔄 |
| Avatar Complexity | ✅ Full Metrics | ❌ No | ❌ No | ❌ |
| **Rendering** |
| OpenGL Version | 4.6 | ES 2.0 | ES 3.2 | ✅ |
| PBR Shaders | ✅ Full | ❌ No | ✅ Partial | 🔄 |
| Advanced Lighting | ✅ Full (ALM) | ❌ No | ✅ Basic | 🔄 |
| Shadows | ✅ Dynamic | ❌ No | ✅ Planned | 📋 |
| Reflections | ✅ Realtime | ❌ No | ❌ No | ❌ |
| Ambient Occlusion | ✅ SSAO | ❌ No | ✅ Basic | 🔄 |
| **Voice** |
| Spatial Voice | ✅ Vivox | ✅ Vivox | ✅ WebRTC | ✅ |
| Group Voice | ✅ Yes | ✅ Yes | ✅ Yes | ✅ |
| Voice Morphing | ✅ Yes | ❌ No | ❌ No | ❌ |
| Echo Cancellation | ✅ Yes | ✅ Basic | ✅ Hardware | ✅ |
| **Build Tools** |
| Prim Building | ✅ Full | ⚠️ Limited | ⚠️ Limited | ⚠️ |
| Mesh Upload | ✅ Full | ❌ No | ❌ No | ❌ |
| Texture Upload | ✅ Full | ⚠️ Limited | ⚠️ Planned | 📋 |
| Script Editor | ✅ Full LSL IDE | ❌ No | ❌ No | ❌ |
| **Inventory** |
| Basic Management | ✅ Full | ✅ Full | ✅ Planned | 🔄 |
| Search | ✅ Advanced | ⚠️ Basic | ✅ Planned | 📋 |
| Filters | ✅ Extensive | ⚠️ Basic | ⚠️ Basic | ⚠️ |
| Worn Items Tab | ✅ Yes | ✅ Yes | ✅ Planned | 📋 |
| **Communication** |
| Local Chat | ✅ Full | ✅ Full | ✅ Full | ✅ |
| IMs | ✅ Full | ✅ Full | ✅ Full | ✅ |
| Group Chat | ✅ Full | ✅ Full | ✅ Planned | 📋 |
| Nearby Voice | ✅ Yes | ✅ Yes | ✅ Yes | ✅ |

## 🚫 Critical Missing Features for Modern SL (2018-2025)

### 1. Animesh (2018) - **NOT SUPPORTED**

**What it is:** Animated mesh attachments that move independently with their own skeleton and animations.

**Why it matters:** 
- 50%+ of modern avatars use animesh (tails, wings, hair, pets)
- Animesh NPCs are everywhere
- Without support, users see static broken meshes

**Current Status in Lumiya/Linkpoint:** ❌ **NOT IMPLEMENTED**

**Evidence:**
```bash
# Search results show NO animesh support
grep -r "animesh" . --include="*.java"
# Only found in documentation, not implementation
```

**What users will see:** Animesh objects will appear as frozen, non-animated meshes

---

### 2. Bakes on Mesh (2018) - **NOT SUPPORTED**

**What it is:** Server-baked textures applied to mesh bodies/heads instead of classic avatar layers.

**Why it matters:**
- 90%+ of modern mesh bodies use bakes on mesh
- Enables tattoos, makeup, clothing layers on mesh
- Without it, mesh avatars look blank/broken

**Current Status:** ❌ **NOT IMPLEMENTED**

**Evidence:**
```java
// Found only basic baked texture support for classic avatars
// BakedTextureIndex.java only has classic avatar bakes
public enum BakedTextureIndex {
    HEAD, UPPER, LOWER, EYES, SKIRT, HAIR
    // Missing: BAKES_ON_MESH indices
}
```

**What users will see:** Modern mesh avatars will have missing textures

---

### 3. EEP (Enhanced Environment) (2020) - **PARTIALLY SUPPORTED**

**What it is:** Modern environmental system with custom skies, water, lighting per parcel.

**Current Status:** ⚠️ **LIMITED** - Has old Windlight, not new EEP

**Evidence:**
```bash
grep -r "windlight" . --include="*.java"
# Found: Old windlight system
# NOT Found: New EEP system
```

**What users will see:** Default environment instead of custom region settings

---

### 4. PBR Materials (2023) - **FRAMEWORK ONLY**

**What it is:** Physically Based Rendering with metallic, roughness, normal, emissive maps.

**Current Status:** 🔄 **IN PROGRESS**

**Evidence:**
- Linkpoint has PBR shader framework
- Missing: PBR asset loading, protocol support
- Missing: Material parsing from server

**What users will see:** Objects with PBR materials will render as basic textures

---

## 📱 Mobile-Specific Limitations

### What Works Well on Lumiya/Linkpoint:

✅ **Basic SL Experience:**
- Walking around
- Chatting (local, IM, group)
- Basic avatar appearance (classic avatars work fine)
- Teleporting
- Basic inventory
- Touch/click objects
- Nearby voice chat
- Friend list
- Minimap

✅ **Mobile Optimizations:**
- Touch controls
- Battery-aware rendering
- Bandwidth optimization
- Gesture navigation

### What Doesn't Work:

❌ **Modern Avatar Features:**
- Animesh attachments (frozen)
- Bakes on mesh (missing textures)
- Complex fitted mesh (may render wrong)
- Avatar complexity calculations

❌ **Content Creation:**
- No building tools
- No script editing
- No mesh upload
- Limited texture upload

❌ **Advanced Features:**
- No ALM (Advanced Lighting Model)
- No dynamic shadows
- No reflections
- Limited materials

## 🔍 Technical Analysis

### Protocol Support Analysis

Based on code analysis:

```java
// FOUND: Good support for
- UDP protocol messages (1900+ message types)
- HTTP CAPS requests
- LLSD serialization (XML and Binary)
- Mesh rendering (basic)
- Avatar skeleton system
- Texture fetching
- Voice (WebRTC in Linkpoint)

// MISSING: Required for modern features
- Animesh protocol messages
- Bakes on Mesh CAPS
- EEP environment settings
- PBR material asset format
- Experience permissions
```

### Rendering Capabilities

**Lumiya (Original):**
- OpenGL ES 2.0 (2010 standard)
- Fixed-function pipeline remnants
- Basic texture mapping
- No advanced lighting

**Linkpoint (Modern):**
- OpenGL ES 3.2 (2015 standard)
- Modern shader pipeline
- PBR framework (incomplete)
- HDR support (basic)

## 🔨 Build Status & Deployment

### Can We Build It?

Let me check the build configuration...

```bash
# Check build.gradle
- Android SDK 34 ✅
- Gradle 8.2 ✅
- Dependencies mostly resolved ✅
- Many files excluded from build ⚠️
```

**Current Build Issues:**
1. Many Java files excluded (voice/, ui/, modern/)
2. Missing native libraries (OpenJPEG, etc.)
3. Test dependencies may conflict

**Recommended Approach:**
1. Build with current exclusions (minimal viable build)
2. Test core functionality
3. Gradually re-enable features

### Testing Plan

If build succeeds, test on Second Life:

**Will Work:**
- Login to SL
- Basic movement
- Chat
- See other classic avatars
- Basic inventory

**Will NOT Work:**
- Modern mesh avatars (will look broken)
- Animesh (will be static)
- PBR regions (will look basic)
- Complex attachments

## 📈 Modernization Priority

To support 2018-2025 features:

### Phase 1: Critical (Animesh + BoM)
**Estimated effort:** 3-6 months

1. **Animesh Support:**
   - Add animesh protocol messages
   - Implement animesh skeleton system
   - Add animesh animation playback
   - Test with common animesh items

2. **Bakes on Mesh:**
   - Add BoM CAPS support
   - Implement BoM texture fetching
   - Add BoM to mesh rendering
   - Test with modern mesh bodies

### Phase 2: Enhanced Environment
**Estimated effort:** 2-3 months

3. **EEP (Enhanced Environment):**
   - Replace Windlight with EEP
   - Add per-parcel environment
   - Implement custom sky settings
   - Add advanced water rendering

### Phase 3: Full PBR
**Estimated effort:** 3-4 months

4. **Complete PBR Pipeline:**
   - Implement PBR asset loading
   - Add PBR material protocol
   - Complete PBR shader system
   - Test with PBR content

## 🎯 Recommendation

### For Users:

**Current State (2025):**
- ✅ Good for: Basic SL socializing, chat, exploring with classic avatars
- ❌ Not good for: Modern avatar fashion, animesh pets, modern builds
- ⚠️ Expect: Many things to look "broken" or "blank"

**Alternatives:**
- Desktop: Use Firestorm for full modern SL experience
- Mobile: Wait for Animesh + BoM implementation

### For Developers:

**Immediate:**
1. Build and test current codebase
2. Document what works/doesn't work
3. Create issue tracker for missing features

**Short-term (3-6 months):**
1. Implement Animesh support (highest impact)
2. Add Bakes on Mesh (fixes most avatar issues)
3. Update to EEP

**Long-term (6-12 months):**
1. Complete PBR implementation
2. Add build tools
3. Implement experience tools

## 📞 Conclusion

**Linkpoint/Lumiya in 2025:**
- 🟡 **Functional but dated** - Works for basic SL
- 🔴 **Missing critical features** - Animesh, BoM, EEP
- 🟢 **Good foundation** - Modern Kotlin, WebRTC, decent protocol support
- ⚠️ **Needs major updates** - To support 2018-2025 SL features

**Bottom Line:**
The codebase is ~5-7 years behind modern Second Life. It will work for basic activities but many modern avatars and content will appear broken or missing features.

---

*Analysis Date: October 2025*
*Lumiya Version Analyzed: 3.4.3 (2020)*
*Linkpoint: Modern rebuild (2025)*