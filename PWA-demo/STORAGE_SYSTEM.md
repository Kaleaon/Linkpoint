# Multi-Gigabyte Storage System for Second Life Assets

## Overview

The Linkpoint PWA implements a comprehensive storage system capable of caching multiple gigabytes of Second Life assets (textures, meshes, sounds, animations) to provide a smooth, responsive user experience.

## Storage Architecture

### Three-Layer Storage System

1. **Service Worker Cache API** (Primary binary asset storage)
   - CACHE_SL_TEXTURES: ~2-5GB (10,000 textures)
   - CACHE_SL_MESHES: ~1-2GB (5,000 meshes)
   - CACHE_SL_SOUNDS: ~500MB (1,000 sounds)
   - CACHE_SL_ANIMATIONS: ~100MB (2,000 animations)
   - **Total Target**: 5-10GB

2. **IndexedDB** (Structured metadata and small assets)
   - Asset metadata (UUIDs, timestamps, references)
   - Notecards and scripts
   - Wearable definitions
   - Landmarks

3. **Memory Cache** (JavaScript objects)
   - Active scene objects
   - Currently loaded textures
   - Temporary data

## Features

### Persistent Storage

The PWA requests **persistent storage** from the browser to prevent eviction:

```javascript
await navigator.storage.persist();
```

This ensures that cached Second Life assets are not deleted when:
- Browser storage is low
- User hasn't visited in a while
- Browser needs space

### Storage Quota Management

**Default Browser Quotas** (varies by browser/platform):
- Chrome Desktop: 60% of available disk space (potentially 100s of GB)
- Chrome Mobile: 6-50% of available disk space
- Firefox: 50% of available disk space (up to 2GB default, can request more)
- Safari: 1GB default, user can approve more

**PWA Requests**:
- Persistent storage prevents eviction
- Quota monitoring tracks usage
- Auto-eviction when 90% full (LRU strategy)
- Target usage: 70% after eviction

### LRU (Least Recently Used) Eviction

When storage reaches 90% quota:

1. **Identify** least recently accessed assets
2. **Delete** oldest assets first
3. **Continue** until usage drops to 70%
4. **Log** eviction statistics

This ensures:
- Most frequently used assets stay cached
- Storage doesn't exceed quota
- Automatic management (no user intervention)

## Storage Manager API

### Initialization

```javascript
const storage = new StorageManager();
await storage.init();

// Check storage info
const info = await storage.updateStorageInfo();
console.log(`Usage: ${info.usage} / ${info.quota}`);
console.log(`Persisted: ${info.persisted}`);
```

### Storing Assets

```javascript
// Store a texture
await storage.storeAsset('textures', textureUUID, textureData, {
  format: 'JPEG2000',
  width: 1024,
  height: 1024
});

// Store a mesh
await storage.storeAsset('meshes', meshUUID, meshData, {
  vertices: 5000,
  faces: 10000
});

// Store a sound
await storage.storeAsset('sounds', soundUUID, audioBuffer, {
  duration: 5.2,
  format: 'ogg'
});
```

### Retrieving Assets

```javascript
// Get a texture (updates last accessed time)
const texture = await storage.getAsset('textures', textureUUID);

if (texture) {
  loadTexture(texture.data);
}
```

### Storage Statistics

```javascript
// Get stats for all stores
const stats = await storage.getAllStats();

console.log('Textures:', stats.textures.count, stats.textures.sizeFormatted);
console.log('Meshes:', stats.meshes.count, stats.meshes.sizeFormatted);
console.log('Total usage:', stats.total.usageFormatted, '/', stats.total.quotaFormatted);
console.log('Usage percent:', stats.total.usagePercent.toFixed(1) + '%');
```

### Manual Cache Management

```javascript
// Clear specific asset type
await storage.clearStore('textures');

// Manually trigger LRU eviction
await storage.evictLRU('textures', 70); // Target 70% usage

// Delete specific asset
await storage.deleteAsset('meshes', meshUUID);
```

## Service Worker Integration

### Asset Caching

Service worker automatically caches SL assets based on URL patterns:

```javascript
// Textures
if (url.pathname.includes('/texture/') || url.searchParams.has('texture_id')) {
  event.respondWith(cacheSLAsset(request, CACHE_SL_TEXTURES, MAX_SL_TEXTURE_CACHE));
}

// Meshes
if (url.pathname.includes('/mesh/') || url.searchParams.has('mesh_id')) {
  event.respondWith(cacheSLAsset(request, CACHE_SL_MESHES, MAX_SL_MESH_CACHE));
}
```

### Cache Limits

```javascript
const MAX_SL_TEXTURE_CACHE = 10000; // ~2-5GB for textures
const MAX_SL_MESH_CACHE = 5000;     // ~1-2GB for meshes
const MAX_SL_SOUND_CACHE = 1000;    // ~500MB for sounds
const MAX_SL_ANIMATION_CACHE = 2000; // ~100MB for animations
```

### Messaging with Service Worker

```javascript
// Get storage info from service worker
const channel = new MessageChannel();
navigator.serviceWorker.controller.postMessage(
  { type: 'GET_STORAGE_INFO' },
  [channel.port2]
);

channel.port1.onmessage = (event) => {
  console.log('Storage info:', event.data);
};

// Clear SL caches
const channel2 = new MessageChannel();
navigator.serviceWorker.controller.postMessage(
  { type: 'CLEAR_SL_CACHE', cacheType: 'textures' },
  [channel2.port2]
);

channel2.port1.onmessage = (event) => {
  console.log('Cache cleared:', event.data.success);
};
```

## Asset Types and Storage

### Textures (~2-5GB)

**Formats**: JPEG2000, TGA, PNG, J2K
**Average Size**: 200KB - 500KB per texture
**Cache Size**: 10,000 textures = ~3GB

**Storage Priority**:
1. Avatar textures (high frequency)
2. Environment textures (sky, water, ground)
3. Object textures (buildings, items)
4. Temporary textures (snapshots, generated)

### Meshes (~1-2GB)

**Formats**: Binary mesh data, vertex/face arrays
**Average Size**: 50KB - 500KB per mesh
**Cache Size**: 5,000 meshes = ~1.5GB

**Storage Priority**:
1. Avatar meshes
2. Common building components
3. Furniture and objects
4. Temporary meshes

### Sounds (~500MB)

**Formats**: OGG, WAV
**Average Size**: 100KB - 1MB per sound
**Cache Size**: 1,000 sounds = ~500MB

**Storage Priority**:
1. UI sounds
2. Ambient sounds
3. Avatar gestures
4. Environmental sounds

### Animations (~100MB)

**Formats**: BVH, animation keyframes
**Average Size**: 10KB - 100KB per animation
**Cache Size**: 2,000 animations = ~100MB

**Storage Priority**:
1. Walking/running animations
2. Avatar poses
3. Object animations
4. Gesture animations

## Performance Optimization

### Lazy Loading

```javascript
// Load textures only when needed
async function loadTextureWhenVisible(textureUUID) {
  // Check if already cached
  let texture = await storage.getAsset('textures', textureUUID);
  
  if (!texture) {
    // Fetch from SL server
    texture = await fetchTexture(textureUUID);
    await storage.storeAsset('textures', textureUUID, texture);
  }
  
  return texture;
}
```

### Batch Loading

```javascript
// Prefetch nearby region assets
async function prefetchRegionAssets(regionUUID) {
  const manifest = await fetchRegionManifest(regionUUID);
  
  // Batch load in background
  for (const assetUUID of manifest.textures.slice(0, 100)) {
    loadTextureWhenVisible(assetUUID);
  }
}
```

### Progressive Loading

```javascript
// Load low-res first, then high-res
async function progressiveLoadTexture(textureUUID) {
  // Load 128x128 thumbnail first
  const thumbnail = await fetchTexture(textureUUID, 128);
  displayTexture(thumbnail);
  
  // Load full resolution in background
  const fullRes = await fetchTexture(textureUUID, 1024);
  await storage.storeAsset('textures', textureUUID, fullRes);
  displayTexture(fullRes);
}
```

## Browser Compatibility

### Chrome/Edge (Chromium)
- ✅ Persistent storage supported
- ✅ Quota: 60% of available disk space
- ✅ IndexedDB: Unlimited (within quota)
- ✅ Cache API: Unlimited (within quota)

### Firefox
- ✅ Persistent storage supported
- ✅ Quota: Up to 50% of disk space
- ✅ IndexedDB: 2GB default, can request more
- ✅ Cache API: Shared with IndexedDB quota

### Safari (iOS/macOS)
- ⚠️ Persistent storage limited
- ⚠️ Quota: 1GB default
- ⚠️ May evict after 7 days of inactivity
- ✅ Can request quota increase

### Installed PWA
- ✅ Better persistence guarantees
- ✅ Larger quotas
- ✅ Less likely to be evicted

## Monitoring and Debugging

### Console Logging

The storage manager provides detailed logging:

```
[Storage] Initializing storage manager...
[Storage] ✅ Storage already persisted
[Storage] ✅ Storage manager initialized
[Storage] Quota: 50.5 GB
[Storage] Usage: 2.3 GB (4.6%)
[Storage] Persisted: true
```

### Storage Inspector

Access storage inspector in browser DevTools:
- Chrome: DevTools → Application → Storage
- Firefox: DevTools → Storage
- Safari: Web Inspector → Storage

### Runtime Statistics

```javascript
// Display storage stats in UI
async function displayStorageStats() {
  const stats = await storage.getAllStats();
  
  console.log('=== Storage Statistics ===');
  console.log(`Textures: ${stats.textures.count} (${stats.textures.sizeFormatted})`);
  console.log(`Meshes: ${stats.meshes.count} (${stats.meshes.sizeFormatted})`);
  console.log(`Sounds: ${stats.sounds.count} (${stats.sounds.sizeFormatted})`);
  console.log(`Total: ${stats.total.usageFormatted} / ${stats.total.quotaFormatted}`);
  console.log(`Usage: ${stats.total.usagePercent.toFixed(1)}%`);
}
```

## Security Considerations

### Same-Origin Policy

Storage is isolated per origin:
- Data cannot be accessed by other sites
- Secure HTTPS required for service workers
- PWA must be served over HTTPS

### Data Privacy

- No user data sent to third parties
- Assets cached locally only
- Cleared when user clears browser data

### Quota Management

- Browser enforces quota limits
- User can approve additional storage
- App respects browser eviction policies

## Best Practices

1. **Request Persistent Storage Early**
   - Call `requestPersistentStorage()` on first load
   - Show UI to explain benefits

2. **Monitor Usage**
   - Check quota regularly
   - Warn users when approaching limit
   - Provide manual cache management

3. **Implement LRU**
   - Delete least used assets first
   - Keep frequently accessed assets
   - Log eviction for debugging

4. **Batch Operations**
   - Load assets in batches
   - Avoid blocking UI thread
   - Use Web Workers for processing

5. **Error Handling**
   - Handle quota exceeded errors
   - Gracefully degrade when offline
   - Provide fallback for failed loads

## Troubleshooting

### Storage Not Persisted

**Symptom**: Assets deleted after browser restart

**Solution**:
1. Check if user has site engagement (visits, interactions)
2. Request persistent storage explicitly
3. For PWA: Install as app for better persistence

### Quota Exceeded

**Symptom**: Cannot store new assets

**Solution**:
1. Check current usage: `await storage.updateStorageInfo()`
2. Run LRU eviction: `await storage.evictLRU('textures', 70)`
3. Clear unused caches: `await storage.clearStore('sounds')`

### Assets Not Loading

**Symptom**: Cached assets not being retrieved

**Solution**:
1. Check IndexedDB in DevTools
2. Verify asset IDs match
3. Check service worker cache
4. Clear and re-cache if corrupted

## Future Enhancements

- [ ] Compression for stored assets
- [ ] Delta updates for mesh/texture versions
- [ ] Smart prefetching based on user location
- [ ] P2P asset sharing between nearby users
- [ ] Automatic quality adjustment based on quota
- [ ] Cloud backup for installed PWA
- [ ] Asset prioritization algorithm
- [ ] Background sync for large downloads

## Summary

The Linkpoint PWA storage system provides:

✅ **Multi-gigabyte capacity** (5-10GB target)
✅ **Persistent storage** (prevents eviction)
✅ **Automatic management** (LRU eviction)
✅ **Type-specific caching** (textures, meshes, sounds, animations)
✅ **Performance optimization** (lazy loading, prefetching)
✅ **Quota monitoring** (usage tracking)
✅ **Browser compatibility** (Chrome, Firefox, Safari)
✅ **Developer tools** (stats, debugging, manual control)

This ensures a smooth Second Life experience with minimal loading times and robust offline support.
