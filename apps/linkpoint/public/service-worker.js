// Linkpoint PWA Service Worker
const CACHE_VERSION = 'linkpoint-v1.2.0';
const CACHE_STATIC = `${CACHE_VERSION}-static`;
const CACHE_DYNAMIC = `${CACHE_VERSION}-dynamic`;
const CACHE_ASSETS = `${CACHE_VERSION}-assets`;
const CACHE_SL_TEXTURES = `${CACHE_VERSION}-sl-textures`;
const CACHE_SL_MESHES = `${CACHE_VERSION}-sl-meshes`;
const CACHE_SL_SOUNDS = `${CACHE_VERSION}-sl-sounds`;
const CACHE_SL_ANIMATIONS = `${CACHE_VERSION}-sl-animations`;

// Static files to cache on install
const STATIC_FILES = [
  './',
  './index.html',
  './manifest.webmanifest',
  './icons/icon.svg',
  './icons/icon-maskable.svg'
];

const MAX_CACHE_SIZE = 50;
const MAX_SL_TEXTURE_CACHE = 10000;
const MAX_SL_MESH_CACHE = 5000;
const MAX_SL_SOUND_CACHE = 1000;
const MAX_SL_ANIMATION_CACHE = 2000;

self.addEventListener('install', (event) => {
  event.waitUntil(
    caches.open(CACHE_STATIC)
      .then((cache) => cache.addAll(STATIC_FILES))
      .then(() => self.skipWaiting())
  );
});

self.addEventListener('activate', (event) => {
  event.waitUntil(
    Promise.all([
      caches.keys().then((cacheNames) => {
        return Promise.all(
          cacheNames
            .filter((cacheName) => cacheName.startsWith('linkpoint-') && !Object.values({CACHE_STATIC, CACHE_DYNAMIC, CACHE_ASSETS, CACHE_SL_TEXTURES, CACHE_SL_MESHES, CACHE_SL_SOUNDS, CACHE_SL_ANIMATIONS}).includes(cacheName))
            .map((cacheName) => caches.delete(cacheName))
        );
      }),
      self.clients.claim()
    ])
  );
});

self.addEventListener('fetch', (event) => {
  const { request } = event;
  if (request.method !== 'GET') return;
  const url = new URL(request.url);

  if (url.protocol === 'chrome-extension:') return;

  // Never persist authenticated grid traffic, capability URLs, or cross-origin
  // responses: those URLs commonly contain bearer-equivalent secrets.
  if (url.origin !== self.location.origin || url.pathname.includes('/api/') || url.pathname.includes('/caps/')) {
    event.respondWith(fetch(request, { cache: 'no-store' }));
    return;
  }

  if (url.pathname.includes('/texture/')) {
    event.respondWith(cacheSLAsset(request, CACHE_SL_TEXTURES, MAX_SL_TEXTURE_CACHE));
    return;
  }

  if (url.pathname.includes('/mesh/')) {
    event.respondWith(cacheSLAsset(request, CACHE_SL_MESHES, MAX_SL_MESH_CACHE));
    return;
  }

  if (request.mode === 'navigate') {
    event.respondWith(appShellStrategy(request));
    return;
  }

  event.respondWith(staleWhileRevalidateStrategy(request));
});

async function appShellStrategy(request) {
  try {
    return await fetch(request);
  } catch (error) {
    const cache = await caches.open(CACHE_STATIC);
    return cache.match('./index.html');
  }
}

async function networkFirstStrategy(request) {
  try {
    const networkResponse = await fetch(request);
    if (networkResponse.ok) {
      const cache = await caches.open(CACHE_DYNAMIC);
      cache.put(request, networkResponse.clone());
    }
    return networkResponse;
  } catch (error) {
    return caches.match(request);
  }
}

async function staleWhileRevalidateStrategy(request) {
  const cachedResponse = await caches.match(request);
  const fetchPromise = fetch(request).then(async (networkResponse) => {
    if (networkResponse.ok) {
      const cache = await caches.open(CACHE_DYNAMIC);
      cache.put(request, networkResponse.clone());
    }
    return networkResponse;
  });
  return cachedResponse || fetchPromise;
}

async function cacheSLAsset(request, cacheName, maxSize) {
  const cache = await caches.open(cacheName);
  const cachedResponse = await cache.match(request);
  if (cachedResponse) return cachedResponse;

  try {
    const networkResponse = await fetch(request);
    if (networkResponse.ok) {
      cache.put(request, networkResponse.clone());
      limitCacheSize(cacheName, maxSize);
    }
    return networkResponse;
  } catch (error) {
    return caches.match(request);
  }
}

async function limitCacheSize(cacheName, maxSize) {
  const cache = await caches.open(cacheName);
  const keys = await cache.keys();
  if (keys.length > maxSize) {
    await cache.delete(keys[0]);
  }
}
