# Android to JavaScript Mapping

This document maps the new JavaScript modules to their Android/Kotlin counterparts, showing how essential features from the Android app have been ported to the PWA.

## Overview

Based on analysis of the Android codebase (5,365 Java and Kotlin files), four critical missing subsystems were identified and ported to JavaScript:

1. **Terrain System** - Handles Second Life terrain decompression and rendering
2. **Windlight/Environment** - Manages atmospheric rendering, lighting, and day cycles
3. **Display Names** - Resolves and caches user display names
4. **Assets** - Handles wearables, notecards, and landmarks

## Module Mappings

### 1. Terrain System

**JavaScript**: `js/terrain.js` (300+ lines)

**Android Source**:
- `com.lumiyaviewer.lumiya.slproto.terrain.TerrainPatch.kt`
- `com.lumiyaviewer.lumiya.slproto.terrain.TerrainData.kt`
- `com.lumiyaviewer.lumiya.slproto.terrain.TerrainPatchHeightMap.kt`

**Key Classes Ported**:
- `TerrainPatch` - Decompresses terrain patches using DCT (Discrete Cosine Transform)
- `TerrainManager` - Manages multiple terrain patches for a region
- `BitBuffer` - Reads compressed terrain data from bit streams

**Features Implemented**:
- ✅ Terrain patch decompression (16x16 patches)
- ✅ DCT coefficient dequantization
- ✅ Inverse DCT for height map reconstruction
- ✅ Zigzag matrix ordering
- ✅ Height interpolation (bilinear)
- ✅ Water height management
- ✅ Per-position height queries

**Usage Example**:
```javascript
const terrainManager = new TerrainManager();
const bitBuffer = new BitBuffer(compressedData);
const patch = new TerrainPatch();
const decompressed = patch.decompressPatch(bitBuffer, 16);

terrainManager.setPatch(0, 0, decompressed);
const height = terrainManager.getHeightAt(128, 128);
```

### 2. Windlight/Environment System

**JavaScript**: `js/windlight.js` (350+ lines)

**Android Source**:
- `com.lumiyaviewer.lumiya.slproto.windlight.WindlightPreset.kt`
- `com.lumiyaviewer.lumiya.slproto.windlight.WindlightDay.kt`

**Key Classes Ported**:
- `WindlightPreset` - Atmospheric rendering parameters
- `WindlightDay` - Day cycle management with time-based interpolation

**Features Implemented**:
- ✅ Atmospheric color parameters (ambient, blue density, blue horizon)
- ✅ Sun/moon lighting and positioning
- ✅ Cloud rendering parameters
- ✅ Haze and fog settings
- ✅ Underwater lighting adjustments
- ✅ Gamma correction
- ✅ Day cycle with preset interpolation
- ✅ Time-of-day sun direction calculation

**Windlight Parameters**:
```javascript
{
  ambient: [r, g, b, a],           // Ambient light color
  blue_density: [r, g, b, a],      // Sky blue density
  blue_horizon: [r, g, b, a],      // Horizon blue tint
  sunlight_color: [r, g, b, a],    // Sun light color
  cloud_color: [r, g, b, a],       // Cloud color
  haze_density: [r, g, b, a],      // Atmospheric haze
  lightnorm: [x, y, z, w],         // Sun direction vector
  star_brightness: float            // Star visibility
}
```

**Usage Example**:
```javascript
const windlight = new WindlightPreset();
windlight.setSunDirectionFromTime(12.0); // Noon

const daycycle = new WindlightDay();
daycycle.setTimeOfDay(18.0); // 6 PM
dayycle.update(deltaTime); // Updates with time progression
```

### 3. Display Names System

**JavaScript**: `js/displaynames.js` (320+ lines)

**Android Source**:
- `com.lumiyaviewer.lumiya.slproto.dispnames.SLDisplayNameFetcher.kt`

**Key Classes Ported**:
- `DisplayNameCache` - Caches display names with expiration
- `DisplayNameFetcher` - Fetches names via capabilities, supports batching

**Features Implemented**:
- ✅ Display name caching with TTL
- ✅ Batch fetching (up to 100 UUIDs per request)
- ✅ Capability-based fetching
- ✅ Fallback to legacy names
- ✅ Request deduplication
- ✅ Automatic cache size management
- ✅ Display name formatting

**Display Name Structure**:
```javascript
{
  uuid: string,                    // Agent UUID
  username: string,                // username.resident
  displayName: string,             // Custom display name
  legacyFirstName: string,         // FirstName
  legacyLastName: string,          // LastName
  isDefaultName: boolean,          // Using default name
  displayNameExpires: timestamp    // Expiration time
}
```

**Usage Example**:
```javascript
const fetcher = new DisplayNameFetcher(protocol);

// Single fetch
const displayName = await fetcher.fetchDisplayName(agentUUID);
console.log(fetcher.formatDisplayName(displayName, true));

// Batch fetch
const names = await fetcher.fetchDisplayNames([uuid1, uuid2, uuid3]);

// Queue for batching (efficient for many requests)
const name = await fetcher.queueForBatch(agentUUID);
```

### 4. Assets System

**JavaScript**: `js/assets.js` (400+ lines)

**Android Source**:
- `com.lumiyaviewer.lumiya.slproto.assets.SLWearable.kt`
- `com.lumiyaviewer.lumiya.slproto.assets.SLWearableType.kt`
- `com.lumiyaviewer.lumiya.slproto.assets.SLNotecard.kt`
- `com.lumiyaviewer.lumiya.slproto.assets.SLLandmark.kt`

**Key Classes Ported**:
- `SLWearable` - Parses and manages wearable assets
- `SLNotecard` - Handles notecard text and embedded items
- `SLLandmark` - Parses landmark location data
- `AssetManager` - Unified asset fetching and caching
- `WearableType` - Enum for wearable categories

**Features Implemented**:
- ✅ Wearable parsing (LLWearable format)
- ✅ Visual parameters (shape, skin, etc.)
- ✅ Texture entries
- ✅ Notecard text extraction
- ✅ Embedded inventory items
- ✅ Landmark position parsing
- ✅ SLURL generation
- ✅ Asset caching

**Wearable Types**:
```javascript
WearableType = {
  SHAPE: 0, SKIN: 1, HAIR: 2, EYES: 3,
  SHIRT: 4, PANTS: 5, SHOES: 6, SOCKS: 7,
  JACKET: 8, GLOVES: 9, UNDERSHIRT: 10, UNDERPANTS: 11,
  SKIRT: 12, ALPHA: 13, TATTOO: 14, PHYSICS: 15, UNIVERSAL: 16
}
```

**Usage Example**:
```javascript
// Wearable
const wearable = new SLWearable(assetData);
console.log(`Wearable: ${wearable.name}`);
console.log(`Type: ${WearableType.toString(wearable.type)}`);
const param = wearable.getParameter(33); // Get parameter value
const texture = wearable.getTexture(0);  // Get texture UUID

// Notecard
const notecard = new SLNotecard(assetData);
console.log(notecard.getText());

// Landmark
const landmark = new SLLandmark(assetData);
console.log(landmark.getSLURL());
const pos = landmark.getPosition(); // {x, y, z}

// Asset Manager
const assetMgr = new AssetManager(protocol);
const asset = await assetMgr.fetchAsset(assetUUID, 'wearable');
```

## Integration Points

### How These Modules Integrate with Existing PWA Code

1. **Terrain + World Viewer** (`js/world.js`):
   ```javascript
   // In world.js
   this.terrainManager = new TerrainManager();
   
   // When terrain data arrives
   handleTerrainData(patchData) {
     const bitBuffer = new BitBuffer(patchData);
     const patch = new TerrainPatch();
     const decompressed = patch.decompressPatch(bitBuffer, 16);
     this.terrainManager.setPatch(patchX, patchY, decompressed);
   }
   ```

2. **Windlight + Graphics** (`js/graphics3d.js`):
   ```javascript
   // In graphics3d.js
   this.windlight = new WindlightPreset();
   this.dayCircle = new WindlightDay();
   
   // Update lighting
   updateLighting() {
     this.dayCircle.update(deltaTime);
     const preset = this.dayCircle.currentPreset;
     this.setAmbientLight(preset.ambient);
     this.setSunLight(preset.sunlight_color);
     this.setSunDirection(preset.getSunDirection());
   }
   ```

3. **Display Names + Chat** (`js/chat.js`):
   ```javascript
   // In chat.js
   this.displayNameFetcher = new DisplayNameFetcher(protocol);
   
   // When displaying chat message
   async displayMessage(agentUUID, message) {
     const displayName = await this.displayNameFetcher.fetchDisplayName(agentUUID);
     const name = this.displayNameFetcher.formatDisplayName(displayName);
     this.addChatLine(name, message);
   }
   ```

4. **Assets + Inventory** (`js/inventory.js`):
   ```javascript
   // In inventory.js
   this.assetManager = new AssetManager(protocol);
   
   // When viewing wearable
   async viewWearable(assetUUID) {
     const wearable = await this.assetManager.fetchAsset(assetUUID, 'wearable');
     this.displayWearableInfo(wearable);
   }
   ```

## Code Quality & Compatibility

### Differences from Android Implementation

1. **Language Features**:
   - Kotlin nullable types → JavaScript null checks
   - Kotlin data classes → JavaScript objects
   - Kotlin coroutines → JavaScript async/await
   - Kotlin sealed classes → JavaScript enums/objects

2. **Platform APIs**:
   - Android BitBuffer → Custom JavaScript BitBuffer
   - Android LLSD parsing → JavaScript object parsing
   - Android asset loading → Fetch API

3. **Optimizations**:
   - Float32Array for terrain data (native performance)
   - Map for efficient lookups
   - Batch fetching to reduce network requests
   - LRU-style cache management

### Testing Recommendations

1. **Terrain System**:
   ```javascript
   // Test terrain decompression
   const patch = new TerrainPatch();
   const testData = new Uint8Array([/* compressed data */]);
   const result = patch.decompressPatch(new BitBuffer(testData), 16);
   console.assert(result.heightMap.length === 256);
   ```

2. **Windlight System**:
   ```javascript
   // Test time-of-day transitions
   const day = new WindlightDay();
   day.setTimeOfDay(6.0);  // Dawn
   const preset = day.currentPreset;
   console.assert(preset.sunlight_color[0] > 0);
   ```

3. **Display Names**:
   ```javascript
   // Test caching
   const cache = new DisplayNameCache();
   cache.set('uuid-123', {displayName: 'Test User'});
   const cached = cache.get('uuid-123');
   console.assert(cached.displayName === 'Test User');
   ```

## Statistics

### Code Ported

| Module | JavaScript Lines | Android Source Files | Key Features |
|--------|-----------------|---------------------|--------------|
| Terrain | 300+ | 5 Kotlin files | DCT, decompression, height queries |
| Windlight | 350+ | 2 Kotlin files | Atmosphere, day cycle, lighting |
| Display Names | 320+ | 1 Kotlin file | Caching, batch fetching |
| Assets | 400+ | 4 Kotlin files | Wearables, notecards, landmarks |
| **Total** | **1,370+ lines** | **12 Android files** | **30+ features** |

### Coverage

From the Android app's 22 major packages in `slproto`, we've now ported essential functionality from:
- ✅ `terrain` - Complete terrain system
- ✅ `windlight` - Complete environment system
- ✅ `dispnames` - Display name resolution
- ✅ `assets` - Core asset types (wearables, notecards, landmarks)

## Future Enhancements

Additional Android packages that could be ported:

1. **Avatar System** (`avatar/`):
   - Visual parameters (SLAvatarParams.kt)
   - Skeleton and bones (SLSkeleton.kt)
   - Attachment points (SLAttachmentPoint.kt)

2. **Mesh System** (`mesh/`):
   - Advanced mesh loading
   - LOD management

3. **Baker System** (`baker/`):
   - Avatar baking
   - Texture compositing

4. **Events System** (`events/`):
   - Event queue processing
   - Server-sent events

## Conclusion

These four new modules bring essential Second Life functionality from the Android app to the PWA, enhancing:

- **Visual Fidelity**: Terrain and windlight for realistic environments
- **User Experience**: Display names for proper identity
- **Feature Completeness**: Asset handling for inventory interaction

The PWA now has **28 JavaScript modules** (up from 24), with critical functionality ported from the Android codebase while maintaining web platform compatibility.
