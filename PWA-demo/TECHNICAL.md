# Linkpoint PWA - Technical Documentation

## Architecture Overview

### System Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                         PWA Layer                            │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐      │
│  │   Manifest   │  │Service Worker│  │  Cache API   │      │
│  └──────────────┘  └──────────────┘  └──────────────┘      │
└─────────────────────────────────────────────────────────────┘
                              │
┌─────────────────────────────────────────────────────────────┐
│                     Application Layer                        │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐      │
│  │  AuthManager │  │ ChatManager  │  │ WorldViewer  │      │
│  └──────┬───────┘  └──────┬───────┘  └──────┬───────┘      │
│         │                 │                  │               │
│         └─────────────────┴──────────────────┘               │
│                           │                                  │
│                  ┌────────┴────────┐                        │
│                  │ ProtocolManager │                        │
│                  └────────┬────────┘                        │
└───────────────────────────┼─────────────────────────────────┘
                            │
┌───────────────────────────┼─────────────────────────────────┐
│                  Network Layer                               │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐      │
│  │  WebSocket   │  │  HTTP/HTTPS  │  │   REST API   │      │
│  └──────────────┘  └──────────────┘  └──────────────┘      │
└─────────────────────────────────────────────────────────────┘
```

## Core Components

### 1. Service Worker (`service-worker.js`)

**Purpose**: Enables offline functionality and caching

**Key Features**:
- Static file caching
- Dynamic content caching
- Network-first strategy for API calls
- Cache-first strategy for assets
- Stale-while-revalidate for HTML
- Background sync
- Push notifications

**Caching Strategies**:

```javascript
// Cache-First: Assets (images, fonts, styles, scripts)
async function cacheFirstStrategy(request) {
  const cache = await caches.match(request);
  if (cache) return cache;
  const response = await fetch(request);
  caches.open(CACHE_ASSETS).then(c => c.put(request, response.clone()));
  return response;
}

// Network-First: API calls
async function networkFirstStrategy(request) {
  try {
    const response = await fetch(request);
    caches.open(CACHE_DYNAMIC).then(c => c.put(request, response.clone()));
    return response;
  } catch {
    return caches.match(request);
  }
}

// Stale-While-Revalidate: HTML pages
async function staleWhileRevalidateStrategy(request) {
  const cache = await caches.match(request);
  const fetchPromise = fetch(request).then(response => {
    caches.open(CACHE_DYNAMIC).then(c => c.put(request, response.clone()));
    return response;
  });
  return cache || fetchPromise;
}
```

### 2. Protocol Manager (`js/protocol.js`)

**Purpose**: Handles LLSD protocol and grid communication

**Key Classes**:
```javascript
class ProtocolManager extends Utils.EventEmitter {
  // LLSD Serialization
  serializeLLSD(data)     // Convert JS object to LLSD XML
  parseLLSD(xml)          // Parse LLSD XML to JS object
  
  // Authentication
  login(grid, user, pass) // Login to grid
  logout()                // Logout and cleanup
  
  // Communication
  connectWebSocket(url)   // Establish WebSocket connection
  sendMessage(type, data) // Send message to simulator
  handleMessage(data)     // Process incoming messages
  
  // Session Management
  startHeartbeat()        // Keep connection alive
  getCapabilities(url)    // Fetch grid capabilities
}
```

**LLSD Protocol Implementation**:
```javascript
// LLSD XML Structure
<?xml version="1.0" encoding="UTF-8"?>
<llsd>
  <map>
    <key>first</key>
    <string>FirstName</string>
    <key>last</key>
    <string>LastName</string>
    <key>passwd</key>
    <string>$1$hash...</string>
  </map>
</llsd>
```

**Supported Data Types**:
- `undef` - Null/undefined
- `boolean` - true/false
- `integer` - Whole numbers
- `real` - Floating point
- `string` - Text
- `uuid` - UUIDs
- `date` - ISO 8601 dates
- `uri` - URLs
- `binary` - Base64 encoded
- `array` - Arrays
- `map` - Objects/dictionaries

### 3. Authentication Manager (`js/auth.js`)

**Purpose**: User authentication and session management

**Features**:
- Grid selection (Agni, Aditi, OSGrid, custom)
- Username/password validation
- Remember me functionality
- Session persistence
- Auto-login support

**Authentication Flow**:
```
┌──────────┐
│ User     │
│ Input    │
└────┬─────┘
     │
     ▼
┌────────────────┐
│ Validate       │
│ Credentials    │
└────┬───────────┘
     │
     ▼
┌────────────────┐
│ Protocol       │
│ Login Request  │
└────┬───────────┘
     │
     ▼
┌────────────────┐
│ Parse          │
│ Response       │
└────┬───────────┘
     │
     ▼
┌────────────────┐
│ Store Session  │
│ Update UI      │
└────────────────┘
```

### 4. World Viewer (`js/world.js`)

**Purpose**: 2D/3D world visualization

**Components**:
- Canvas renderer
- Camera system
- Object management
- Avatar tracking
- Input handling

**Rendering Pipeline**:
```javascript
render(timestamp) {
  // 1. Clear canvas
  ctx.fillRect(0, 0, width, height);
  
  // 2. Apply camera transform
  const offsetX = (width/2) - (camera.x * camera.zoom);
  const offsetY = (height/2) - (camera.y * camera.zoom);
  
  // 3. Draw grid
  drawGrid();
  
  // 4. Draw objects (sorted by depth)
  objects.sort((a, b) => a.z - b.z);
  objects.forEach(drawObject);
  
  // 5. Draw avatars
  avatars.forEach(drawAvatar);
  
  // 6. Draw UI overlay
  drawOverlay();
  
  // 7. Calculate FPS
  updateFPS(timestamp);
}
```

**Camera Controls**:
- WASD / Arrow keys - Movement
- E/C - Vertical movement
- Mouse drag - Rotation
- Mouse wheel - Zoom

### 5. Chat Manager (`js/chat.js`)

**Purpose**: Real-time messaging system

**Features**:
- Local chat
- Private messages
- Chat commands
- Message history
- Offline queueing
- Export functionality

**Message Structure**:
```javascript
{
  id: 'uuid',              // Unique message ID
  sender: 'FirstName LastName',
  senderId: 'agent-uuid',
  text: 'message content',
  timestamp: 1234567890,   // Unix timestamp
  type: 'local',           // local, whisper, shout, etc.
  channel: 'local'         // Chat channel
}
```

**Chat Commands**:
- `/help` - Show commands
- `/clear` - Clear history
- `/me <action>` - Emote
- `/shout <msg>` - Shout (100m)
- `/whisper <msg>` - Whisper (10m)

### 6. Utility Functions (`js/utils.js`)

**Purpose**: Shared utility functions

**Key Utilities**:
```javascript
Utils.generateUUID()                 // Generate UUID v4
Utils.formatTime(timestamp)          // Format timestamps
Utils.showToast(msg, type, duration) // Show notifications
Utils.storage.get/set/remove(key)    // LocalStorage wrapper
Utils.debounce(fn, delay)            // Debounce function
Utils.throttle(fn, limit)            // Throttle function
Utils.EventEmitter                   // Event system
```

## Data Flow

### Login Flow
```
User → Form Input
  ↓
Validation
  ↓
ProtocolManager.login()
  ↓
LLSD Serialization
  ↓
HTTP POST to Grid
  ↓
LLSD Response Parse
  ↓
AuthManager.user = {...}
  ↓
Update UI
  ↓
Switch to World View
```

### Chat Message Flow
```
User Types Message
  ↓
ChatManager.sendMessage()
  ↓
Validate & Format
  ↓
ProtocolManager.sendMessage()
  ↓
WebSocket.send()
  ↓
Server Processes
  ↓
WebSocket.onmessage
  ↓
ProtocolManager.handleMessage()
  ↓
ChatManager.handleIncomingMessage()
  ↓
Display in UI
  ↓
Save to History
```

### World Update Flow
```
WebSocket Message
  ↓
ProtocolManager.emit('object_update')
  ↓
WorldViewer.handleObjectUpdate()
  ↓
Update objects array
  ↓
Next render() call
  ↓
Draw updated scene
```

## Performance Optimizations

### 1. Rendering
- RequestAnimationFrame for smooth 60fps
- Canvas double buffering
- Object culling (only draw visible)
- Depth sorting for correct layering
- FPS counter for monitoring

### 2. Caching
- Service Worker caches static assets
- LocalStorage for user preferences
- IndexedDB for large data sets
- LRU cache eviction policy
- Max cache size limits

### 3. Network
- HTTP/2 multiplexing
- WebSocket for real-time data
- Request debouncing
- Retry logic with exponential backoff
- Connection pooling

### 4. Memory
- Object pooling for frequent allocations
- Weak references for caches
- Periodic garbage collection hints
- Max message history limit
- Lazy loading of assets

## Security Considerations

### 1. Input Validation
```javascript
// XSS Prevention
function escapeHtml(text) {
  const div = document.createElement('div');
  div.textContent = text;
  return div.innerHTML;
}

// URL Validation
function isValidUrl(url) {
  try {
    new URL(url);
    return true;
  } catch {
    return false;
  }
}
```

### 2. Credential Storage
- LocalStorage for non-sensitive data
- Never store passwords
- Clear credentials on logout
- Session timeout handling

### 3. Network Security
- HTTPS required for PWA
- Certificate pinning for production
- CORS policy compliance
- CSP headers recommended

### 4. Content Security
```html
<!-- Recommended CSP Header -->
<meta http-equiv="Content-Security-Policy" 
      content="default-src 'self'; 
               script-src 'self'; 
               style-src 'self' 'unsafe-inline'; 
               img-src 'self' data: https:; 
               connect-src 'self' wss: https:;">
```

## Testing

### Unit Tests
```javascript
// Example test structure
describe('ProtocolManager', () => {
  describe('LLSD Serialization', () => {
    it('should serialize string', () => {
      const result = protocol.serializeLLSD('test');
      expect(result).toContain('<string>test</string>');
    });
  });
});
```

### Integration Tests
- Login flow
- Message sending
- World rendering
- Cache behavior
- Offline mode

### PWA Tests
- Manifest validation
- Service Worker registration
- Cache strategies
- Offline functionality
- Install prompt

## Browser Compatibility

### Required Features
- ES6+ JavaScript
- Service Workers
- Canvas API
- LocalStorage
- WebSocket
- Promises/Async-Await

### Polyfills
```javascript
// Promise polyfill for older browsers
if (!window.Promise) {
  import('promise-polyfill');
}

// Fetch polyfill
if (!window.fetch) {
  import('whatwg-fetch');
}
```

## Build & Deployment

### Development
```bash
# No build step required - vanilla JavaScript
# Simply serve files with HTTP server
python -m http.server 8000
```

### Production
```bash
# Minify JavaScript
terser js/*.js --compress --mangle -o dist/app.min.js

# Minify CSS
csso css/styles.css -o dist/styles.min.css

# Optimize images
imagemin assets/**/* --out-dir=dist/assets

# Update service worker cache version
sed -i 's/v1.0.0/v1.0.1/' service-worker.js
```

### Monitoring
```javascript
// Performance monitoring
window.addEventListener('load', () => {
  const perfData = performance.getEntriesByType('navigation')[0];
  console.log('Load time:', perfData.loadEventEnd - perfData.fetchStart);
});

// Error tracking
window.addEventListener('error', (event) => {
  console.error('Error:', event.error);
  // Send to error tracking service
});
```

## API Reference

See individual module documentation:
- [Protocol API](js/protocol.js)
- [Auth API](js/auth.js)
- [World API](js/world.js)
- [Chat API](js/chat.js)
- [Utils API](js/utils.js)

## Contributing

See [README.md](README.md) for contribution guidelines.

## License

GPL v2 - See LICENSE file

---

**Last Updated**: 2025-10-15
