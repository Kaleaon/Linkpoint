# Linkpoint PWA - Tauri Desktop App

## 🎯 What This Is

A **lightweight Rust-based wrapper** for the Linkpoint PWA using Tauri. Much smaller than Electron (~5MB vs ~100MB) with built-in CORS proxy.

## ✅ Advantages Over Electron

- **90% smaller** - ~5MB vs ~100MB
- **Lower memory** - ~50MB RAM vs ~150MB
- **Faster startup** - Native Rust backend
- **Better security** - Rust memory safety
- **Cross-platform** - Same as Electron

## 🚀 Quick Start

### Prerequisites
```bash
# Install Rust
curl --proto '=https' --tlsv1.2 -sSf https://sh.rustup.rs | sh

# On Windows, also need:
# - Microsoft Visual C++ Build Tools
# - WebView2 (usually pre-installed on Windows 10/11)

# On Linux, install dependencies:
sudo apt update
sudo apt install libwebkit2gtk-4.0-dev \
    build-essential \
    curl \
    wget \
    libssl-dev \
    libgtk-3-dev \
    libayatana-appindicator3-dev \
    librsvg2-dev
```

### Install & Run
```bash
cd tauri-wrapper
npm install
npm run dev
```

### Build Distributable
```bash
# Current platform
npm run build

# Specific platform
npm run build:win     # Windows .msi (~5MB)
npm run build:mac     # macOS .app (~5MB)
npm run build:linux   # Linux .AppImage (~5MB)
```

## 📦 Build Artifacts

**Windows:**
- `src-tauri/target/release/bundle/msi/Linkpoint PWA_1.0.0_x64_en-US.msi`

**macOS:**
- `src-tauri/target/release/bundle/dmg/Linkpoint PWA_1.0.0_x64.dmg`
- `src-tauri/target/release/bundle/macos/Linkpoint PWA.app`

**Linux:**
- `src-tauri/target/release/bundle/appimage/linkpoint-pwa_1.0.0_amd64.AppImage`
- `src-tauri/target/release/bundle/deb/linkpoint-pwa_1.0.0_amd64.deb`

## 🔧 How It Works

```
Tauri App Starts
    ↓
Rust main.rs
    ├── Spawns Actix-Web server (port 13338)
    │   ├── /sl-login → Proxies to SL
    │   ├── /sl-capability → Proxies caps
    │   └── /sl-asset/:id → Proxies assets
    └── Opens WebView window
        └── Loads PWA (index.html)
            ↓
        window.IS_TAURI = true
        window.TAURI_PROXY_URL = 'http://127.0.0.1:13338'
            ↓
        Full SL connectivity! ✅
```

## 📊 Size Comparison

| Package | Electron | Tauri |
|---------|----------|-------|
| Windows | ~100MB | ~5MB |
| macOS | ~100MB | ~5MB |
| Linux | ~100MB | ~5MB |

## ⚡ Performance Comparison

| Metric | Electron | Tauri |
|--------|----------|-------|
| Startup | ~2s | ~0.5s |
| Memory | ~150MB | ~50MB |
| CPU | Higher | Lower |

## 🔒 Security

- Rust backend (memory safe)
- Minimal attack surface
- Sandboxed WebView
- No Node.js runtime exposure

## 🛠️ Development

### Hot Reload
```bash
npm run dev
# Changes to PWA reload automatically
# Changes to Rust require restart
```

### Debugging
```bash
# Rust backend logs
RUST_LOG=debug npm run dev

# Open DevTools
Right-click in app → Inspect
```

### Custom Port
Edit `src-tauri/src/main.rs`:
```rust
let proxy_port = 13338; // Change this
```

## 📝 Configuration

All settings in `src-tauri/tauri.conf.json`:

```json
{
  "tauri": {
    "windows": [{
      "width": 1200,    // Change window size
      "height": 800,
      "minWidth": 800,
      "minHeight": 600
    }]
  }
}
```

## 🐛 Troubleshooting

**Build fails:**
```bash
# Update Rust
rustup update

# Clear cache
cargo clean
```

**Proxy won't start:**
```bash
# Check logs
RUST_LOG=actix_web=debug npm run dev
```

**Windows WebView2 missing:**
```powershell
# Download WebView2 Runtime
# https://developer.microsoft.com/en-us/microsoft-edge/webview2/
```

## 🚀 Distribution

### Code Signing

**Windows:**
```bash
# Set certificate
set TAURI_PRIVATE_KEY=path/to/cert.pfx
set TAURI_KEY_PASSWORD=password
npm run build
```

**macOS:**
```bash
# Sign with Apple cert
export APPLE_SIGNING_IDENTITY="Developer ID Application: Your Name"
npm run build
```

## 🆚 When to Use Tauri vs Electron

**Use Tauri if:**
- Want smaller bundle size
- Care about performance
- Comfortable with Rust
- Need modern security

**Use Electron if:**
- Need maximum compatibility
- Have Node.js dependencies
- Need Chromium-specific features
- Larger community/plugins

## 📚 Learn More

- [Tauri Docs](https://tauri.app/)
- [Actix-Web](https://actix.rs/)
- [Reqwest](https://docs.rs/reqwest/)

---

**Result:** A tiny 5MB desktop app with full SL connectivity! 🎉
