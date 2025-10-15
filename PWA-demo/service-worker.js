// Linkpoint PWA Service Worker
const CACHE_VERSION = 'linkpoint-v1.0.0';
const CACHE_STATIC = `${CACHE_VERSION}-static`;
const CACHE_DYNAMIC = `${CACHE_VERSION}-dynamic`;
const CACHE_ASSETS = `${CACHE_VERSION}-assets`;

// Static files to cache on install
const STATIC_FILES = [
  '/',
  '/index.html',
  '/css/styles.css',
  '/js/app.js',
  '/js/auth.js',
  '/js/world.js',
  '/js/chat.js',
  '/js/protocol.js',
  '/js/utils.js',
  '/manifest.json'
];

// Maximum cache size for dynamic content
const MAX_CACHE_SIZE = 50;

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

// Activate event - clean up old caches
self.addEventListener('activate', (event) => {
  console.log('[ServiceWorker] Activating...');
  event.waitUntil(
    caches.keys()
      .then((cacheNames) => {
        return Promise.all(
          cacheNames
            .filter((cacheName) => {
              return cacheName.startsWith('linkpoint-') && 
                     cacheName !== CACHE_STATIC && 
                     cacheName !== CACHE_DYNAMIC &&
                     cacheName !== CACHE_ASSETS;
            })
            .map((cacheName) => {
              console.log('[ServiceWorker] Deleting old cache:', cacheName);
              return caches.delete(cacheName);
            })
        );
      })
      .then(() => self.clients.claim())
  );
});

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

  // Cache-first for static assets
  if (request.destination === 'image' || 
      request.destination === 'font' || 
      request.destination === 'style' || 
      request.destination === 'script') {
    event.respondWith(cacheFirstStrategy(request));
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

// Limit cache size
async function limitCacheSize(cacheName, maxSize) {
  const cache = await caches.open(cacheName);
  const keys = await cache.keys();
  if (keys.length > maxSize) {
    await cache.delete(keys[0]);
    limitCacheSize(cacheName, maxSize);
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
});

console.log('[ServiceWorker] Loaded successfully');
