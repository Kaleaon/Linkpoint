/**
 * Electron Preload Script
 * Exposes safe APIs to renderer process
 */

const { contextBridge } = require('electron');

contextBridge.exposeInMainWorld('electronAPI', {
  isElectron: true,
  platform: process.platform,
  version: process.versions.electron
});
