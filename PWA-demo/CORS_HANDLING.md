# 🌐 CORS Handling in Linkpoint PWA

## Overview

The Linkpoint PWA needs to connect to Second Life/OpenSimulator servers, which typically don't allow direct browser connections due to CORS (Cross-Origin Resource Sharing) restrictions. This document explains how the app handles CORS in different environments.

## 📱 Environments & CORS Solutions

### 1. **Web Browser (Standard)** ⚠️
- **CORS Status:** Blocked by default
- **Solution:** Public CORS proxy (corsproxy.io)
- **Performance:** May be slower, depends on proxy availability
- **Reliability:** Good, but not guaranteed 100% uptime

**User Experience:**
- Works out of the box
- May experience occasional delays
- Best for quick testing

**Recommendation:** Install as PWA or use desktop app for better experience

---

### 2. **Installed PWA (Progressive Web App)** ⚠️
- **CORS Status:** Still blocked (runs in browser context)
- **Solution:** Public CORS proxy (corsproxy.io)
- **Performance:** Same as web browser
- **Reliability:** Good, but not guaranteed

**User Experience:**
- Can be installed to home screen/desktop
- Works offline after first load
- Still uses public proxy for SL connections

**Recommendation:** Consider desktop app for production use

---

### 3. **Electron Desktop App** ✅ **OPTIMAL**
- **CORS Status:** Bypassed via local proxy
- **Solution:** Built-in Express proxy server (port 13337)
- **Performance:** Excellent - direct connection
- **Reliability:** Excellent - no external dependencies

**User Experience:**
- Native desktop application
- Zero CORS issues
- Fast and reliable connections
- ~100MB installer

**Location:** `/app/PWA-demo/electron-wrapper/`

**How to Use:**
```bash
cd /app/PWA-demo/electron-wrapper
npm install
npm run dev        # Development
npm run build:win  # Build Windows installer
npm run build:mac  # Build macOS app
npm run build:linux # Build Linux AppImage
```

---

### 4. **Tauri Desktop App** ✅ **OPTIMAL**
- **CORS Status:** Bypassed via local proxy
- **Solution:** Built-in Actix-Web proxy server (port 13338)
- **Performance:** Excellent - native Rust performance
- **Reliability:** Excellent - no external dependencies

**User Experience:**
- Native desktop application
- Zero CORS issues
- Fast and reliable connections
- ~5MB installer (95% smaller than Electron!)

**Location:** `/app/PWA-demo/tauri-wrapper/`

**How to Use:**
```bash
cd /app/PWA-demo/tauri-wrapper
npm install
npm run dev     # Development
npm run build   # Build installer
```

---

### 5. **Capacitor Mobile App** ✅ **OPTIMAL**
- **CORS Status:** Bypassed via native HTTP
- **Solution:** Capacitor native HTTP plugin
- **Performance:** Excellent - native mobile HTTP
- **Reliability:** Excellent - no external dependencies

**User Experience:**
- Native iOS/Android app
- Zero CORS issues
- Direct connections
- App Store/Play Store ready

**Location:** `/app/PWA-demo/capacitor-wrapper/`

**How to Use:**
```bash
cd /app/PWA-demo/capacitor-wrapper
npm install
npm run android:open  # Open in Android Studio
npm run ios:open      # Open in Xcode
```

---

## 🔍 How CORS Detection Works

The app automatically detects the environment and chooses the best method:

```javascript
// Detection order:
1. Check for Capacitor (mobile)
   → Use native HTTP

2. Check for Electron
   → Use window.ELECTRON_PROXY_URL

3. Check for Tauri
   → Use window.TAURI_PROXY_URL or window.__TAURI__

4. Check if installed as PWA
   → Use public CORS proxy

5. Browser fallback
   → Use public CORS proxy
```

## 📊 CORS Status Indicator

The login screen shows current CORS status:

### Status Indicators:
- ✅ **Optimal** - No proxy needed (desktop/mobile apps)
- ⚠️ **Using Proxy** - Public CORS proxy in use (browser/PWA)
- ❌ **Failed** - Connection issues

### Checking Status:
1. Open the app
2. Look at the login screen
3. Click "Connection Status ▼" to expand details
4. See environment type and recommendations

Or check the browser console for detailed logs:
```
═══════════════════════════════════════════════
🌐 CORS Handler Status
═══════════════════════════════════════════════
Environment: Web Browser
Type: browser
CORS Support: public-proxy
Needs Proxy: Yes
...
```

## 🛠️ Implementation Details

### CORSHandler Class (`cors-handler.js`)
Centralized CORS management:
- Automatic environment detection
- Proxy selection and fallback
- Error handling with helpful messages
- Status reporting

### XMLRPCClient Updates (`sl-xmlrpc.js`)
Uses CORSHandler for all requests:
```javascript
const response = await window.corsHandler.makeRequest(url, {
  method: 'POST',
  headers: {
    'Content-Type': 'text/xml',
    'Accept': 'text/xml, application/xml'
  },
  body: xmlRequest
});
```

## 🚀 Public CORS Proxies

Primary: `https://corsproxy.io/`
- Fast and reliable
- Good uptime
- Free tier available

Fallbacks:
1. `https://api.allorigins.win/raw?url=`
2. `https://cors-anywhere.herokuapp.com/`

**Note:** Public proxies are shared resources and may have rate limits or occasional downtime.

## 💡 Recommendations by Use Case

### For Development:
- ✅ Use web browser with public proxy
- ✅ Quick testing and iteration

### For Regular Users:
- ✅ Install as PWA (one-click install)
- ✅ Good for casual use
- ⚠️ May experience occasional delays

### For Power Users:
- ✅✅ **Electron Desktop App** (best compatibility)
- ✅✅ **Tauri Desktop App** (smallest size)
- ✅✅ Direct connections, zero issues

### For Mobile:
- ✅✅ **Capacitor Mobile App**
- ✅✅ Native performance
- ✅✅ App Store ready

## 🔧 Troubleshooting

### Connection Failed
**Symptom:** "Unable to connect to Second Life servers"

**Solutions:**
1. Check internet connection
2. Try installing as PWA
3. Try desktop app (Electron/Tauri)
4. Check if public proxy is accessible

### Slow Connections
**Symptom:** Long loading times

**Solutions:**
1. Use desktop app for direct connections
2. Check internet speed
3. Try different time of day (proxy load)

### CORS Errors in Console
**Symptom:** CORS policy errors in browser console

**Expected Behavior:** This is normal for browser/PWA
- App automatically uses proxy
- Connection should still work

**If connections fail:**
- Install desktop app for zero CORS issues

## 📈 Performance Comparison

| Environment | Connection Type | Speed | Reliability | Setup |
|-------------|----------------|-------|-------------|-------|
| Browser | Public Proxy | ⭐⭐⭐ | ⭐⭐⭐⭐ | None |
| PWA | Public Proxy | ⭐⭐⭐ | ⭐⭐⭐⭐ | 1-click |
| Electron | Local Proxy | ⭐⭐⭐⭐⭐ | ⭐⭐⭐⭐⭐ | Install |
| Tauri | Local Proxy | ⭐⭐⭐⭐⭐ | ⭐⭐⭐⭐⭐ | Install |
| Capacitor | Native HTTP | ⭐⭐⭐⭐⭐ | ⭐⭐⭐⭐⭐ | App Store |

## 🎯 Best Practices

### For Developers:
1. Always test in browser first (fastest iteration)
2. Test in all environments before release
3. Monitor public proxy status
4. Provide desktop apps for production

### For Users:
1. Try web version first
2. If experience is good, use as-is
3. If experiencing delays, install PWA
4. For best experience, use desktop/mobile app

### For Distribution:
1. Provide web version for discovery
2. Offer PWA install for convenience
3. Distribute desktop apps for power users
4. Submit mobile apps to stores

## 📝 Summary

**The Good News:** 🎉
- ✅ App works in ALL environments
- ✅ Automatic detection and adaptation
- ✅ Multiple fallback options
- ✅ Clear user guidance

**Optimal Setup:** 🚀
- Desktop: Electron or Tauri wrapper
- Mobile: Capacitor app
- Web: PWA with public proxy (acceptable for most users)

**No Configuration Needed:** ⚡
- Everything is automatic
- Users get best available option
- Graceful degradation
- Helpful error messages

---

**Last Updated:** 2025
**Linkpoint PWA Team**
