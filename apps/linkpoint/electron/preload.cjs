const { contextBridge, ipcRenderer } = require('electron');

contextBridge.exposeInMainWorld('linkpointDesktop', {
  allowLoginEndpoint: (url) => ipcRenderer.invoke('linkpoint:allow-login', url),
  request: (request) => ipcRenderer.invoke('linkpoint:request', request),
  connectViewer: (request) => ipcRenderer.invoke('linkpoint:viewer-connect', request),
  /** Any session operation by name; the allow-list lives in core/viewer-api.cjs. */
  call: (method, params) => ipcRenderer.invoke('linkpoint:viewer-call', method, params),
  fetchProfilePhoto: (request) => ipcRenderer.invoke('linkpoint:viewer-profile-photo', request),
  disconnectViewer: () => ipcRenderer.invoke('linkpoint:viewer-disconnect'),
  onViewerEvent: (listener) => {
    const handler = (_event, payload) => listener(payload);
    ipcRenderer.on('linkpoint:viewer-event', handler);
    return () => ipcRenderer.removeListener('linkpoint:viewer-event', handler);
  },
});
