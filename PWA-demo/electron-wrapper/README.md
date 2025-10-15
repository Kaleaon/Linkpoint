# Linkpoint PWA - Electron Desktop App

## 🎯 What This Does

Wraps the Linkpoint PWA in an Electron desktop application with a **built-in CORS proxy server**. This allows the PWA to connect to Second Life grids without needing an external proxy.

## ✅ How It Works

1. **Electron app starts** → Launches Express server on `localhost:13337`
2. **Proxy intercepts SL requests** → Routes through local server
3. **Local server fetches from SL** → No CORS restrictions
4. **Returns data to PWA** → Full SL connectivity works!

## 🚀 Quick Start

### Install Dependencies
```bash
cd electron-wrapper
npm install
```

### Run in Development
```bash
npm run dev
```

### Build Distributable App

**Windows:**
```bash
npm run build:win
# Creates: dist/Linkpoint PWA Setup.exe (~100MB)
```

**macOS:**
```bash
npm run build:mac
# Creates: dist/Linkpoint PWA.dmg (~100MB)
```

**Linux:**
```bash
npm run build:linux
# Creates: dist/Linkpoint PWA.AppImage (~100MB)
```

## 📦 What Gets Built

- **Windows**: `.exe` installer (NSIS)
- **macOS**: `.dmg` disk image
- **Linux**: `.AppImage` portable

All include:
- ✅ Bundled proxy server (Express)
- ✅ Full PWA code
- ✅ Node.js runtime
- ✅ Complete offline support

## 🔧 Architecture

```
Electron App
    ↓
┌─────────────────────────────────────┐
│  Main Process (Node.js)             │
│  ├── Express Proxy (port 13337)     │
│  │   ├── /sl-login → SL login       │
│  │   ├── /sl-capability → caps      │
│  │   └── /sl-asset/:id → assets     │
│  └── BrowserWindow                  │
│      └── loads PWA (index.html)     │
└─────────────────────────────────────┘
         ↓
    PWA detects Electron
         ↓
    Uses http://localhost:13337 for SL
         ↓
    Full SL connectivity! ✅
```

## 🌐 Proxy Endpoints

### `/sl-login` (POST)
Proxies XML-RPC login requests to SL

**Usage in PWA:**
```javascript
fetch('http://localhost:13337/sl-login', {
  method: 'POST',
  body: xmlRpcRequest
});
```

### `/sl-capability` (POST)
Proxies capability requests

**Usage:**
```javascript
fetch('http://localhost:13337/sl-capability', {
  method: 'POST',
  body: JSON.stringify({
    url: 'https://sim3015.agni.lindenlab.com/cap/...',
    body: llsdRequest,
    method: 'POST'
  })
});
```

### `/sl-asset/:assetId` (GET)
Fetches assets (textures, meshes)

**Usage:**
```javascript
fetch('http://localhost:13337/sl-asset/UUID?server=https://...');
```

## 📊 Size Comparison

| Version | Size | CORS Bypass | Platform |
|---------|------|-------------|----------|
| Web PWA | <1MB | ❌ No | All browsers |
| Electron App | ~100MB | ✅ Yes | Desktop (Win/Mac/Linux) |

## ✅ What Works Now

With the Electron app:
- ✅ Login to Second Life (Agni, Aditi)
- ✅ Login to OpenSimulator grids
- ✅ Fetch capabilities
- ✅ Event queue polling
- ✅ Object updates
- ✅ Mesh loading
- ✅ Chat messages
- ✅ All PWA features

## 🔐 Security

- Proxy only listens on `127.0.0.1` (localhost)
- Not accessible from network
- No external connections except to SL
- Runs in user space (no admin required)

## 🎨 Customization

### Change Proxy Port
Edit `main.js`:
```javascript
const PROXY_PORT = 13337; // Change this
```

### Add More Grids
Edit proxy routes in `main.js`:
```javascript
proxyApp.post('/osgrid-login', async (req, res) => {
  const response = await fetch('http://login.osgrid.org/', ...);
  // ...
});
```

### Custom Window Size
Edit `main.js`:
```javascript
const mainWindow = new BrowserWindow({
  width: 1600,  // Change
  height: 900   // Change
});
```

## 🐛 Troubleshooting

**Proxy won't start:**
- Check port 13337 not in use: `lsof -i :13337`
- Try different port in `main.js`

**Can't build app:**
- Ensure all dependencies installed: `npm install`
- For macOS builds, need to be on Mac
- For Windows, need Windows or Wine

**App won't connect to SL:**
- Check DevTools console (View → Toggle Developer Tools)
- Look for "Electron mode: Proxy available" message
- Check proxy logs in terminal

## 📝 Development

### Enable DevTools
Already enabled in dev mode (`npm run dev`)

### View Proxy Logs
All proxy requests logged to console:
```
[Proxy] Server running on http://127.0.0.1:13337
[Proxy] SL Login request received
[Proxy] SL Login response received: 5432 bytes
```

### Hot Reload
Changes to PWA code (HTML/CSS/JS) require app restart:
1. Close app
2. `npm run dev`

Changes to `main.js` require:
1. Close app
2. `npm run dev`

## 🚀 Distribution

### Code Signing (Optional)

**Windows:**
```bash
# Get code signing certificate
# Set environment variables:
export CSC_LINK=/path/to/cert.pfx
export CSC_KEY_PASSWORD=your_password

npm run build:win
```

**macOS:**
```bash
# Sign with Apple Developer cert
export APPLE_ID=your@email.com
export APPLE_ID_PASSWORD=app-specific-password

npm run build:mac
```

### Auto-Updates (Advanced)

Add to `package.json`:
```json
{
  "build": {
    "publish": {
      "provider": "github",
      "owner": "your-username",
      "repo": "linkpoint-pwa"
    }
  }
}
```

## 📚 Further Reading

- [Electron Documentation](https://www.electronjs.org/docs)
- [Electron Builder](https://www.electron.build/)
- [Express.js](https://expressjs.com/)
- [PWA Best Practices](https://web.dev/progressive-web-apps/)

---

**Result:** A fully self-contained desktop app that bypasses CORS and connects to Second Life! 🎉
