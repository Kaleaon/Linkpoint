# 🚀 Linkpoint PWA - Quick Start Guide

Get up and running with Linkpoint PWA in 5 minutes!

## ⚡ Fastest Way to Start

### Option 1: Local File (No Server)
1. Download or clone this repository
2. Open `index.html` in your browser
3. Done! (Note: Some PWA features require HTTPS)

### Option 2: Local Web Server (Recommended)
```bash
# Using Python (most common)
cd PWA-demo
python3 -m http.server 8000

# Or using Node.js
npx http-server -p 8000

# Or using PHP
php -S localhost:8000
```

Then open: `http://localhost:8000`

## 📱 First Time Usage

### 1. Login
- **Grid**: Select "Second Life (Main Grid)" or "OSGrid"
- **Username**: `FirstName LastName` (or `FirstName.LastName`)
- **Password**: Your password
- Click **Login**

> 💡 **Demo Mode**: For testing, the app simulates a successful login

### 2. Explore Views
Use the menu (☰) to navigate:
- 🌍 **World Viewer** - See the virtual world
- 💬 **Chat** - Send messages
- 📦 **Inventory** - Browse items
- 👥 **Friends** - Manage contacts
- 🗺️ **Map** - Navigate regions

### 3. World Controls
**Keyboard**:
- `W/A/S/D` or `Arrow Keys` - Move
- `E` - Move up
- `C` - Move down
- `Mouse Drag` - Rotate view
- `Mouse Wheel` - Zoom

**Mobile**:
- Use on-screen control buttons
- Pinch to zoom
- Drag to look around

### 4. Chat
- Type message in chat box
- Press `Enter` to send
- Try commands:
  - `/help` - Show all commands
  - `/me waves` - Send emote
  - `/clear` - Clear chat

## 🔧 Configuration

### Change Grid
Edit `js/protocol.js`:
```javascript
static GRIDS = {
  mygrid: {
    name: 'My Custom Grid',
    loginUrl: 'https://login.mygrid.com/cgi-bin/login.cgi'
  }
};
```

### Customize Theme
Edit `css/styles.css`:
```css
:root {
  --primary-color: #0f3460;
  --accent-color: #00d4ff;
  --background: #0d1117;
}
```

## 📦 Install as PWA

### Desktop (Chrome/Edge)
1. Click the install icon in address bar
2. Or: Menu → Install Linkpoint
3. App opens in its own window

### Mobile (Android)
1. Menu (⋮) → Add to Home Screen
2. Icon appears on home screen
3. Opens like native app

### Mobile (iOS)
1. Share button → Add to Home Screen
2. Icon appears on home screen
3. Opens in full screen

## 🐛 Troubleshooting

### Can't Login?
- Check username format: `FirstName LastName`
- Verify internet connection
- Try different grid
- Check browser console (F12) for errors

### PWA Not Installing?
- Ensure using HTTPS (or localhost)
- Check manifest.json is valid
- Service worker must be registered
- Try different browser

### Slow Performance?
- Clear browser cache
- Close other tabs
- Try incognito mode
- Update browser

### Chat Not Working?
- Login first
- Check connection status (bottom bar)
- WebSocket might be blocked
- Try different network

## 📊 Check Status

Open browser DevTools (F12):

### Application Tab
- ✅ **Manifest**: Should show app info
- ✅ **Service Workers**: Should be "activated"
- ✅ **Cache Storage**: Should have cached files
- ✅ **Local Storage**: Should have settings

### Console
- No red errors
- `✅ Linkpoint PWA Ready` message
- `✅ Service Worker registered` message

### Network Tab
- Service Worker should intercept requests
- Static files served from cache
- API calls go to network

## 🎯 Features Checklist

After starting, verify these work:

- [ ] App loads without errors
- [ ] Can see login screen
- [ ] Can enter credentials
- [ ] Login button works
- [ ] Can switch between views
- [ ] World viewer shows grid
- [ ] Chat input accepts text
- [ ] Can send chat messages
- [ ] FPS counter shows in status bar
- [ ] Menu button opens sidebar
- [ ] PWA install prompt shows (Chrome)

## 🌐 Deploy Online

### GitHub Pages
1. Push to GitHub repo
2. Settings → Pages → Select branch
3. Access at `username.github.io/repo-name`

### Netlify (Easiest)
```bash
# Install Netlify CLI
npm install -g netlify-cli

# Deploy
cd PWA-demo
netlify deploy --prod
```

### Vercel
```bash
# Install Vercel CLI
npm install -g vercel

# Deploy
cd PWA-demo
vercel --prod
```

## 🔗 Useful Links

- [Full Documentation](README.md)
- [Technical Details](TECHNICAL.md)
- [Second Life](https://secondlife.com)
- [OpenSimulator](http://opensimulator.org)

## 💡 Tips

1. **Use WASD** for faster navigation
2. **Enable "Remember Me"** for quicker login
3. **Install as PWA** for better experience
4. **Use WiFi** for initial load
5. **Clear cache** if updates don't appear
6. **Check DevTools** for debugging
7. **Export chat logs** before clearing

## 🆘 Get Help

- **Issues**: Check [README.md](README.md) troubleshooting
- **Questions**: Open GitHub issue
- **Community**: Join discussions
- **Docs**: Read [TECHNICAL.md](TECHNICAL.md)

## 🎉 Next Steps

1. ✅ Start the app
2. ✅ Login successfully
3. ✅ Explore all views
4. ✅ Send a chat message
5. ✅ Move around the world
6. ✅ Install as PWA
7. ✅ Customize theme
8. ✅ Deploy online

---

**Ready to explore virtual worlds? Start now! 🚀**

*Questions? Check the [full documentation](README.md)*
