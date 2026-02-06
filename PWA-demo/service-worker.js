// Linkpoint PWA Service Worker
const CACHE_VERSION = 'linkpoint-v1.0.0';
const CACHE_STATIC = `${CACHE_VERSION}-static`;
const CACHE_DYNAMIC = `${CACHE_VERSION}-dynamic`;
const CACHE_ASSETS = `${CACHE_VERSION}-assets`;
const CACHE_SL_TEXTURES = `${CACHE_VERSION}-sl-textures`;
const CACHE_SL_MESHES = `${CACHE_VERSION}-sl-meshes`;
const CACHE_SL_SOUNDS = `${CACHE_VERSION}-sl-sounds`;
const CACHE_SL_ANIMATIONS = `${CACHE_VERSION}-sl-animations`;

// Static files to cache on install (relative paths for subpath hosting)
const STATIC_FILES = [
  './',
  './index.html',
  './css/styles.css',
  './js/app.js',
  './js/auth.js',
  './js/world.js',
  './js/chat.js',
  './js/protocol.js',
  './js/utils.js',
  './js/voice.js',
  './js/inventory.js',
  './js/friends.js',
  './js/teleport.js',
  './js/search.js',
  './js/preferences.js',
  './js/notifications.js',
  './js/graphics3d.js',
  './js/camera3d.js',
  './js/primitives3d.js',
  './js/scene3d.js',
  './js/sl-xmlrpc.js',
  './js/sl-messages.js',
  './js/sl-circuit.js',
  './js/sl-protocol-real.js',
  './js/sl-connection-full.js',
  './js/sl-mesh-loader.js',
  './js/sl-object-manager.js',
  './js/enhanced-notifications.js',
  './js/filesystem.js',
  './js/network-status.js',
  './js/device-info.js',
  './js/secure-storage.js',
  './js/background-sync.js',
  './js/haptics.js',
  './js/badge.js',
  './js/pwa-integration.js',
  './js/pwa-demo.js',
  './manifest.json'
];

// Maximum cache sizes (in number of items)
const MAX_CACHE_SIZE = 50; // General dynamic content
const MAX_SL_TEXTURE_CACHE = 10000; // ~2-5GB for textures
const MAX_SL_MESH_CACHE = 5000; // ~1-2GB for meshes
const MAX_SL_SOUND_CACHE = 1000; // ~500MB for sounds
const MAX_SL_ANIMATION_CACHE = 2000; // ~100MB for animations

// Total target: 5-10GB of SL asset storage

// Install event - cache static files
self.addEventListener('install', (event) => {
  console.log('[ServiceWorker] Installing...');
  event.waitUntil(
    caches.open(CACHE_STATIC)
      .then((cache) => {
        console.log('[ServiceWorker] Caching static files');
        return cache.addAll(STATIC_FILES);
      })
      .then(() => self.skipWaiting())
  );
});

// Activate event - clean up old caches and request persistent storage
self.addEventListener('activate', (event) => {
  console.log('[ServiceWorker] Activating...');
  event.waitUntil(
    Promise.all([
      // Clean up old caches
      caches.keys().then((cacheNames) => {
        return Promise.all(
          cacheNames
            .filter((cacheName) => {
              return cacheName.startsWith('linkpoint-') && 
                     cacheName !== CACHE_STATIC && 
                     cacheName !== CACHE_DYNAMIC &&
                     cacheName !== CACHE_ASSETS &&
                     cacheName !== CACHE_SL_TEXTURES &&
                     cacheName !== CACHE_SL_MESHES &&
                     cacheName !== CACHE_SL_SOUNDS &&
                     cacheName !== CACHE_SL_ANIMATIONS;
            })
            .map((cacheName) => {
              console.log('[ServiceWorker] Deleting old cache:', cacheName);
              return caches.delete(cacheName);
            })
        );
      }),
      // Request persistent storage for multi-GB cache
      requestPersistentStorage(),
      // Claim clients
      self.clients.claim()
    ])
  );
});

// Request persistent storage to prevent browser eviction
async function requestPersistentStorage() {
  try {
    if (navigator.storage && navigator.storage.persist) {
      const isPersisted = await navigator.storage.persist();
      console.log(`[ServiceWorker] Persistent storage ${isPersisted ? 'granted' : 'denied'}`);
      
      if (isPersisted && navigator.storage.estimate) {
        const estimate = await navigator.storage.estimate();
        const usageMB = (estimate.usage / 1024 / 1024).toFixed(2);
        const quotaMB = (estimate.quota / 1024 / 1024).toFixed(2);
        console.log(`[ServiceWorker] Storage: ${usageMB}MB / ${quotaMB}MB (${(estimate.usage / estimate.quota * 100).toFixed(1)}%)`);
      }
    }
  } catch (error) {
    console.error('[ServiceWorker] Persistent storage request failed:', error);
  }
}

// Fetch event - serve from cache or network
self.addEventListener('fetch', (event) => {
  const { request } = event;
  const url = new URL(request.url);

  // Skip chrome extension requests
  if (url.protocol === 'chrome-extension:') {
    return;
  }

  // Network-first for API calls
  if (url.pathname.includes('/api/') || url.pathname.includes('/caps/')) {
    event.respondWith(networkFirstStrategy(request));
    return;
  }

  // Cache-first for static assets and SL assets
  if (request.destination === 'image' || 
      request.destination === 'font' || 
      request.destination === 'style' || 
      request.destination === 'script') {
    event.respondWith(cacheFirstStrategy(request));
    return;
  }
  
  // Special handling for Second Life assets
  if (url.pathname.includes('/texture/') || url.searchParams.has('texture_id')) {
    event.respondWith(cacheSLAsset(request, CACHE_SL_TEXTURES, MAX_SL_TEXTURE_CACHE));
    return;
  }
  
  if (url.pathname.includes('/mesh/') || url.searchParams.has('mesh_id')) {
    event.respondWith(cacheSLAsset(request, CACHE_SL_MESHES, MAX_SL_MESH_CACHE));
    return;
  }
  
  if (url.pathname.includes('/sound/') || url.searchParams.has('sound_id')) {
    event.respondWith(cacheSLAsset(request, CACHE_SL_SOUNDS, MAX_SL_SOUND_CACHE));
    return;
  }
  
  if (url.pathname.includes('/animation/') || url.searchParams.has('anim_id')) {
    event.respondWith(cacheSLAsset(request, CACHE_SL_ANIMATIONS, MAX_SL_ANIMATION_CACHE));
    return;
  }

  // Stale-while-revalidate for HTML pages
  event.respondWith(staleWhileRevalidateStrategy(request));
});

// Cache-first strategy
async function cacheFirstStrategy(request) {
  const cachedResponse = await caches.match(request);
  if (cachedResponse) {
    return cachedResponse;
  }

  try {
    const networkResponse = await fetch(request);
    if (networkResponse && networkResponse.status === 200) {
      const cache = await caches.open(CACHE_ASSETS);
      cache.put(request, networkResponse.clone());
      limitCacheSize(CACHE_ASSETS, MAX_CACHE_SIZE);
    }
    return networkResponse;
  } catch (error) {
    console.error('[ServiceWorker] Fetch failed:', error);
    return new Response('Network error', {
      status: 408,
      headers: { 'Content-Type': 'text/plain' }
    });
  }
}

// Network-first strategy
async function networkFirstStrategy(request) {
  try {
    const networkResponse = await fetch(request);
    if (networkResponse && networkResponse.status === 200) {
      const cache = await caches.open(CACHE_DYNAMIC);
      cache.put(request, networkResponse.clone());
      limitCacheSize(CACHE_DYNAMIC, MAX_CACHE_SIZE);
    }
    return networkResponse;
  } catch (error) {
    const cachedResponse = await caches.match(request);
    if (cachedResponse) {
      return cachedResponse;
    }
    return new Response(JSON.stringify({ error: 'Network unavailable' }), {
      status: 503,
      headers: { 'Content-Type': 'application/json' }
    });
  }
}

// Stale-while-revalidate strategy
async function staleWhileRevalidateStrategy(request) {
  const cachedResponse = await caches.match(request);
  
  const fetchPromise = fetch(request).then((networkResponse) => {
    if (networkResponse && networkResponse.status === 200) {
      const cache = caches.open(CACHE_DYNAMIC);
      cache.then((c) => c.put(request, networkResponse.clone()));
    }
    return networkResponse;
  });

  return cachedResponse || fetchPromise;
}

// Cache SL assets with LRU eviction
async function cacheSLAsset(request, cacheName, maxSize) {
  try {
    const cache = await caches.open(cacheName);
    const cachedResponse = await cache.match(request);
    
    if (cachedResponse) {
      // Update access time by re-caching (LRU)
      cache.put(request, cachedResponse.clone());
      return cachedResponse;
    }
    
    // Fetch from network
    const networkResponse = await fetch(request);
    if (networkResponse && networkResponse.status === 200) {
      cache.put(request, networkResponse.clone());
      limitCacheSize(cacheName, maxSize);
    }
    return networkResponse;
  } catch (error) {
    console.error('[ServiceWorker] SL asset fetch failed:', error);
    const cachedResponse = await caches.match(request);
    if (cachedResponse) {
      return cachedResponse;
    }
    return new Response('Asset unavailable', {
      status: 503,
      headers: { 'Content-Type': 'text/plain' }
    });
  }
}

// Limit cache size with LRU eviction
async function limitCacheSize(cacheName, maxSize) {
  const cache = await caches.open(cacheName);
  const keys = await cache.keys();
  if (keys.length > maxSize) {
    // Delete oldest entries (first in keys array)
    const deleteCount = Math.min(100, keys.length - maxSize);
    for (let i = 0; i < deleteCount; i++) {
      await cache.delete(keys[i]);
    }
    console.log(`[ServiceWorker] Evicted ${deleteCount} items from ${cacheName}`);
  }
}

// Background sync for offline actions
self.addEventListener('sync', (event) => {
  console.log('[ServiceWorker] Background sync:', event.tag);
  if (event.tag === 'sync-messages') {
    event.waitUntil(syncMessages());
  }
});

async function syncMessages() {
  // Sync offline messages when connection is restored
  console.log('[ServiceWorker] Syncing offline messages...');
  // Implementation would send queued messages to server
}

// Push notifications
self.addEventListener('push', (event) => {
  const data = event.data ? event.data.json() : {};
  const title = data.title || 'Linkpoint';
  const options = {
    body: data.body || 'New notification',
    icon: '/assets/icons/icon-192x192.png',
    badge: '/assets/icons/icon-72x72.png',
    vibrate: [200, 100, 200],
    tag: data.tag || 'default',
    data: data.data || {}
  };

  event.waitUntil(
    self.registration.showNotification(title, options)
  );
});

// Notification click handler
self.addEventListener('notificationclick', (event) => {
  event.notification.close();
  
  event.waitUntil(
    clients.openWindow(event.notification.data.url || '/')
  );
});

// Message handler for communication with main app
self.addEventListener('message', (event) => {
  if (event.data && event.data.type === 'SKIP_WAITING') {
    self.skipWaiting();
  }
  
  if (event.data && event.data.type === 'CACHE_URLS') {
    event.waitUntil(
      caches.open(CACHE_DYNAMIC).then((cache) => {
        return cache.addAll(event.data.urls);
      })
    );
  }
  
  // Get storage info for SL asset management
  if (event.data && event.data.type === 'GET_STORAGE_INFO') {
    event.waitUntil(
      getStorageInfo().then((info) => {
        event.ports[0].postMessage(info);
      })
    );
  }
  
  // Clear SL asset caches
  if (event.data && event.data.type === 'CLEAR_SL_CACHE') {
    event.waitUntil(
      clearSLCaches(event.data.cacheType).then(() => {
        event.ports[0].postMessage({ success: true });
      })
    );
  }
});

// Get detailed storage information
async function getStorageInfo() {
  const info = {
    persisted: false,
    usage: 0,
    quota: 0,
    caches: {}
  };
  
  try {
    if (navigator.storage && navigator.storage.persisted) {
      info.persisted = await navigator.storage.persisted();
    }
    
    if (navigator.storage && navigator.storage.estimate) {
      const estimate = await navigator.storage.estimate();
      info.usage = estimate.usage;
      info.quota = estimate.quota;
    }
    
    // Get size of each cache
    const cacheNames = [CACHE_STATIC, CACHE_DYNAMIC, CACHE_ASSETS, 
                        CACHE_SL_TEXTURES, CACHE_SL_MESHES, 
                        CACHE_SL_SOUNDS, CACHE_SL_ANIMATIONS];
    
    for (const cacheName of cacheNames) {
      const cache = await caches.open(cacheName);
      const keys = await cache.keys();
      info.caches[cacheName] = keys.length;
    }
  } catch (error) {
    console.error('[ServiceWorker] Failed to get storage info:', error);
  }
  
  return info;
}

// Clear specific SL cache types
async function clearSLCaches(cacheType) {
  const cachesToClear = [];
  
  if (cacheType === 'all' || cacheType === 'textures') {
    cachesToClear.push(CACHE_SL_TEXTURES);
  }
  if (cacheType === 'all' || cacheType === 'meshes') {
    cachesToClear.push(CACHE_SL_MESHES);
  }
  if (cacheType === 'all' || cacheType === 'sounds') {
    cachesToClear.push(CACHE_SL_SOUNDS);
  }
  if (cacheType === 'all' || cacheType === 'animations') {
    cachesToClear.push(CACHE_SL_ANIMATIONS);
  }
  
  for (const cacheName of cachesToClear) {
    await caches.delete(cacheName);
    console.log(`[ServiceWorker] Cleared cache: ${cacheName}`);
  }
}

console.log('[ServiceWorker] Loaded successfully');
