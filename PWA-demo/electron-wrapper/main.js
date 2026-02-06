/**
 * Linkpoint PWA - Electron Wrapper with Built-in Proxy
 * This bundles a CORS proxy server directly in the desktop app
 */

const { app, BrowserWindow, ipcMain } = require('electron');
const express = require('express');
const path = require('path');
const fetch = require('node-fetch');

// Configuration
const PROXY_PORT = 13337;
const PROXY_HOST = '127.0.0.1';

/**
 * Create local proxy server to bypass CORS
 */
function startProxyServer() {
  const proxyApp = express();
  
  // Enable JSON and text parsing
  proxyApp.use(express.json());
  proxyApp.use(express.text({ type: 'text/xml' }));
  proxyApp.use(express.urlencoded({ extended: true }));

  // CORS headers for local requests
  proxyApp.use((req, res, next) => {
    res.header('Access-Control-Allow-Origin', '*');
    res.header('Access-Control-Allow-Methods', 'GET, POST, OPTIONS');
    res.header('Access-Control-Allow-Headers', 'Content-Type');
    if (req.method === 'OPTIONS') {
      res.sendStatus(200);
    } else {
      next();
    }
  });

  // SL Login Proxy
  proxyApp.post('/sl-login', async (req, res) => {
    try {
      console.log('[Proxy] SL Login request received');
      
      const response = await fetch(
        'https://login.agni.lindenlab.com/cgi-bin/login.cgi',
        {
          method: 'POST',
          headers: {
            'Content-Type': 'text/xml',
            'User-Agent': 'Linkpoint-PWA-Electron/1.0'
          },
          body: req.body
        }
      );

      const text = await response.text();
      console.log('[Proxy] SL Login response received:', text.length, 'bytes');
      
      res.type('text/xml').send(text);
    } catch (error) {
      console.error('[Proxy] SL Login error:', error);
      res.status(500).send(error.message);
    }
  });

  // Capability proxy (for any HTTPS capability)
  proxyApp.post('/sl-capability', async (req, res) => {
    try {
      const { url, body, method = 'POST' } = req.body;
      
      console.log(`[Proxy] Capability request: ${method} ${url}`);
      
      const response = await fetch(url, {
        method,
        headers: {
          'Content-Type': 'application/llsd+xml',
          'Accept': 'application/llsd+xml, application/llsd+binary'
        },
        body: body || undefined
      });

      const text = await response.text();
      res.send(text);
    } catch (error) {
      console.error('[Proxy] Capability error:', error);
      res.status(500).send(error.message);
    }
  });

  // Asset fetch proxy
  proxyApp.get('/sl-asset/:assetId', async (req, res) => {
    try {
      const { assetId } = req.params;
      const { server } = req.query;
      
      console.log(`[Proxy] Asset fetch: ${assetId} from ${server}`);
      
      const response = await fetch(`${server}/${assetId}`);
      const buffer = await response.buffer();
      
      res.type(response.headers.get('content-type')).send(buffer);
    } catch (error) {
      console.error('[Proxy] Asset error:', error);
      res.status(500).send(error.message);
    }
  });

  // Start server
  proxyApp.listen(PROXY_PORT, PROXY_HOST, () => {
    console.log(`[Proxy] Server running on http://${PROXY_HOST}:${PROXY_PORT}`);
    console.log('[Proxy] Ready to proxy SL requests');
  });

  return proxyApp;
}

/**
 * Create main application window
 */
function createWindow() {
  const mainWindow = new BrowserWindow({
    width: 1200,
    height: 800,
    minWidth: 800,
    minHeight: 600,
    webPreferences: {
      nodeIntegration: false,
      contextIsolation: true,
      preload: path.join(__dirname, 'preload.js')
    },
    icon: path.join(__dirname, '../assets/icons/icon-512x512.png'),
    title: 'Linkpoint PWA',
    backgroundColor: '#1a1a1a'
  });

  // Load PWA
  mainWindow.loadFile(path.join(__dirname, '../index.html'));

  // Open DevTools in development
  if (process.env.NODE_ENV === 'development') {
    mainWindow.webContents.openDevTools();
  }

  // Inject proxy URL into page
  mainWindow.webContents.on('did-finish-load', () => {
    mainWindow.webContents.executeJavaScript(`
      window.ELECTRON_PROXY_URL = 'http://${PROXY_HOST}:${PROXY_PORT}';
      window.IS_ELECTRON = true;
      console.log('Electron mode: Proxy available at', window.ELECTRON_PROXY_URL);
    `);
  });

  return mainWindow;
}

/**
 * App lifecycle
 */
app.whenReady().then(() => {
  // Start proxy server
  startProxyServer();

  // Create window
  createWindow();

  app.on('activate', () => {
    if (BrowserWindow.getAllWindows().length === 0) {
      createWindow();
    }
  });
});

app.on('window-all-closed', () => {
  if (process.platform !== 'darwin') {
    app.quit();
  }
});

// Handle app quit
app.on('before-quit', () => {
  console.log('[App] Shutting down...');
});
