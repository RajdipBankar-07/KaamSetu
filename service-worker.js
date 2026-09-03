// 🌾 KaamSetu PWA Service Worker — Offline-First Resiliency
const CACHE_NAME = 'kaamsetu-v1-static';
const DYNAMIC_CACHE = 'kaamsetu-v1-dynamic';

const ASSETS_TO_CACHE = [
  './',
  './index.html',
  './manifest.json',
  './css/design-system.css',
  './css/components.css',
  './js/i18n.js',
  './js/api.js',
  './js/auth.js',
  './js/security.js',
  './js/pilot.js',
  './js/data.js',
  './js/state.js',
  './js/app.js'
];

// Install Event: Pre-cache core application shell
self.addEventListener('install', event => {
  event.waitUntil(
    caches.open(CACHE_NAME).then(cache => {
      console.log('🌾 [ServiceWorker] Pre-caching application shell');
      return cache.addAll(ASSETS_TO_CACHE);
    }).then(() => self.skipWaiting())
  );
});

// Activate Event: Cleanup stale caches
self.addEventListener('activate', event => {
  event.waitUntil(
    caches.keys().then(cacheNames => {
      return Promise.all(
        cacheNames.map(cache => {
          if (cache !== CACHE_NAME && cache !== DYNAMIC_CACHE) {
            console.log('🌾 [ServiceWorker] Removing old cache:', cache);
            return caches.delete(cache);
          }
        })
      );
    }).then(() => self.clients.claim())
  );
});

// Fetch Event: Cache-First for static assets, Network-First with fallback for API
self.addEventListener('fetch', event => {
  const requestUrl = new URL(event.request.url);

  // Skip caching for backend API requests to allow graceful offline mock fallback
  if (requestUrl.pathname.startsWith('/api') || requestUrl.pathname.startsWith('/auth')) {
    event.respondWith(
      fetch(event.request).catch(() => {
        return new Response(JSON.stringify({
          success: false,
          offline: true,
          message: 'Offline mode active. Local data served.'
        }), {
          headers: { 'Content-Type': 'application/json' }
        });
      })
    );
    return;
  }

  // Cache-First strategy with dynamic cache update for UI assets
  event.respondWith(
    caches.match(event.request).then(cachedResponse => {
      if (cachedResponse) {
        return cachedResponse;
      }
      return fetch(event.request).then(networkResponse => {
        if (networkResponse && networkResponse.status === 200) {
          const responseClone = networkResponse.clone();
          caches.open(DYNAMIC_CACHE).then(cache => {
            cache.put(event.request, responseClone);
          });
        }
        return networkResponse;
      }).catch(() => {
        if (event.request.headers.get('accept')?.includes('text/html')) {
          return caches.match('./index.html');
        }
      });
    })
  );
});
