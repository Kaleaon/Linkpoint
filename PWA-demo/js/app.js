/**
 * Linkpoint PWA - Main Application
 */

class LinkpointApp {
  constructor() {
    this.protocol = null;
    this.auth = null;
    this.world = null;
    this.chat = null;
    this.currentView = 'login';
    this.deferredPrompt = null;
    this.serviceWorkerRegistration = null;
  }

  /**
   * Initialize application
   */
  async init() {
    console.log('🔗 Linkpoint PWA Starting...');

    // Register service worker
    await this.registerServiceWorker();

    // Initialize managers
    this.protocol = new ProtocolManager();
    this.auth = new AuthManager(this.protocol);
    this.world = new WorldViewer(this.protocol);
    this.chat = new ChatManager(this.protocol, this.auth);

    // Initialize modules
    this.auth.init();
    this.world.init();
    this.chat.init();

    // Setup UI
    this.setupUI();
    this.setupEventListeners();

    // Setup PWA install prompt
    this.setupInstallPrompt();

    // Check for updates
    this.checkForUpdates();

    // Handle query parameters
    this.handleQueryParams();

    console.log('✅ Linkpoint PWA Ready');
    Utils.showToast('Welcome to Linkpoint!', 'success');
  }

  /**
   * Register service worker
   */
  async registerServiceWorker() {
    if (!('serviceWorker' in navigator)) {
      console.warn('Service Worker not supported');
      return;
    }

    try {
```suggestion
      this.serviceWorkerRegistration = await navigator.serviceWorker.register('service-worker.js');
      console.log('✅ Service Worker registered');

      // Listen for updates
      this.serviceWorkerRegistration.addEventListener('updatefound', () => {
        const newWorker = this.serviceWorkerRegistration.installing;
        newWorker.addEventListener('statechange', () => {
          if (newWorker.state === 'installed' && navigator.serviceWorker.controller) {
            // New version available
            this.showUpdateNotification();
          }
        });
      });
    } catch (error) {
      console.error('Service Worker registration failed:', error);
    }
  }

  /**
   * Setup UI components
   */
  setupUI() {
    // Setup navigation
    const navLinks = document.querySelectorAll('.nav-link');
    navLinks.forEach(link => {
      link.addEventListener('click', (e) => {
        e.preventDefault();
        const view = link.dataset.view;
        if (view) {
          this.switchView(view);
        }
      });
    });

    // Setup menu button
    const menuBtn = document.getElementById('menu-btn');
    const sidebar = document.getElementById('sidebar');
    const sidebarCloseBtn = document.getElementById('sidebar-close-btn');

    menuBtn?.addEventListener('click', () => {
      sidebar?.classList.add('active');
    });

    sidebarCloseBtn?.addEventListener('click', () => {
      sidebar?.classList.remove('active');
    });

    // Close sidebar when clicking outside
    document.addEventListener('click', (e) => {
      if (sidebar?.classList.contains('active') && 
          !sidebar.contains(e.target) && 
          !menuBtn?.contains(e.target)) {
        sidebar.classList.remove('active');
      }
    });

    // Setup settings button
    const settingsBtn = document.getElementById('settings-btn');
    settingsBtn?.addEventListener('click', () => {
      this.showSettings();
    });

    // Setup notifications button
    const notificationsBtn = document.getElementById('notifications-btn');
    notificationsBtn?.addEventListener('click', () => {
      this.showNotifications();
    });
  }

  /**
   * Setup event listeners
   */
  setupEventListeners() {
    // Auth events
    this.auth.on('login_success', (user) => {
      console.log('User logged in:', user);
      this.updateNotificationCount(0);
    });

    this.auth.on('logout', () => {
      console.log('User logged out');
      this.chat.clearHistory();
    });

    // Protocol events
    this.protocol.on('websocket_connected', () => {
      console.log('WebSocket connected');
      this.updateConnectionStatus('Connected');
    });

    this.protocol.on('websocket_disconnected', () => {
      console.log('WebSocket disconnected');
      this.updateConnectionStatus('Disconnected');
    });

    // Chat events
    this.chat.on('message_received', (message) => {
      // Show notification if not in chat view
      if (this.currentView !== 'chat') {
        this.incrementNotificationCount();
        this.showNotificationToast(message);
      }
    });

    // Online/offline detection
    window.addEventListener('online', () => {
      Utils.showToast('Connection restored', 'success');
      this.updateConnectionStatus('Online');
    });

    window.addEventListener('offline', () => {
      Utils.showToast('Connection lost', 'warning');
      this.updateConnectionStatus('Offline');
    });
  }

  /**
   * Switch view
   */
  switchView(viewName) {
    // Update nav links
    document.querySelectorAll('.nav-link').forEach(link => {
      link.classList.toggle('active', link.dataset.view === viewName);
    });

    // Update views
    document.querySelectorAll('.view').forEach(view => {
      view.classList.toggle('active', view.id === `view-${viewName}`);
    });

    // Close sidebar on mobile
    const sidebar = document.getElementById('sidebar');
    sidebar?.classList.remove('active');

    // Reset notification count if switching to chat
    if (viewName === 'chat') {
      this.updateNotificationCount(0);
    }

    this.currentView = viewName;
    this.emit('view_changed', viewName);
  }

  /**
   * Show settings
   */
  showSettings() {
    Utils.showToast('Settings (coming soon)', 'info');
  }

  /**
   * Show notifications
   */
  showNotifications() {
    Utils.showToast('Notifications (coming soon)', 'info');
  }

  /**
   * Update notification count
   */
  updateNotificationCount(count) {
    const badge = document.getElementById('notification-count');
    if (badge) {
      badge.textContent = count;
      badge.style.display = count > 0 ? 'block' : 'none';
    }
  }

  /**
   * Increment notification count
   */
  incrementNotificationCount() {
    const badge = document.getElementById('notification-count');
    if (badge) {
      const current = parseInt(badge.textContent) || 0;
      this.updateNotificationCount(current + 1);
    }
  }

  /**
   * Show notification toast
   */
  showNotificationToast(message) {
    Utils.showToast(`${message.sender}: ${message.text.substring(0, 50)}...`, 'info', 5000);
  }

  /**
   * Update connection status
   */
  updateConnectionStatus(status) {
    const statusEl = document.getElementById('connection-status');
    if (statusEl) {
      statusEl.textContent = status;
      
      // Update color based on status
      if (status === 'Connected' || status === 'Online') {
        statusEl.style.color = 'var(--success-color)';
      } else if (status === 'Disconnected' || status === 'Offline') {
        statusEl.style.color = 'var(--error-color)';
      } else {
        statusEl.style.color = 'var(--warning-color)';
      }
    }
  }

  /**
   * Setup PWA install prompt
   */
  setupInstallPrompt() {
    window.addEventListener('beforeinstallprompt', (e) => {
      e.preventDefault();
      this.deferredPrompt = e;
      this.showInstallPrompt();
    });

    // Install button
    const installBtn = document.getElementById('install-btn');
    installBtn?.addEventListener('click', async () => {
      if (!this.deferredPrompt) return;

      this.deferredPrompt.prompt();
      const result = await this.deferredPrompt.userChoice;
      
      if (result.outcome === 'accepted') {
        Utils.showToast('App installed successfully!', 'success');
      }
      
      this.deferredPrompt = null;
      this.hideInstallPrompt();
    });

    // Dismiss button
    const dismissBtn = document.getElementById('dismiss-install');
    dismissBtn?.addEventListener('click', () => {
      this.hideInstallPrompt();
    });
  }

  /**
   * Show install prompt
   */
  showInstallPrompt() {
    const prompt = document.getElementById('install-prompt');
    if (prompt) {
      prompt.style.display = 'block';
    }
  }

  /**
   * Hide install prompt
   */
  hideInstallPrompt() {
    const prompt = document.getElementById('install-prompt');
    if (prompt) {
      prompt.style.display = 'none';
    }
  }

  /**
   * Show update notification
   */
  showUpdateNotification() {
    const updateAvailable = confirm('A new version of Linkpoint is available. Update now?');
    
    if (updateAvailable && this.serviceWorkerRegistration) {
      const newWorker = this.serviceWorkerRegistration.waiting;
      if (newWorker) {
        newWorker.postMessage({ type: 'SKIP_WAITING' });
        window.location.reload();
      }
    }
  }

  /**
   * Check for updates
   */
  async checkForUpdates() {
    if (this.serviceWorkerRegistration) {
      try {
        await this.serviceWorkerRegistration.update();
      } catch (error) {
        console.error('Update check failed:', error);
      }
    }
  }

  /**
   * Handle query parameters
   */
  handleQueryParams() {
    const params = Utils.parseQueryString();
    
    if (params.action === 'login') {
      this.switchView('login');
    } else if (params.action === 'world') {
      this.switchView('world');
    }
  }

  /**
   * Emit event (using Utils.EventEmitter pattern)
   */
  emit(event, data) {
    window.dispatchEvent(new CustomEvent(`linkpoint:${event}`, { detail: data }));
  }
}

// Initialize app when DOM is ready
if (document.readyState === 'loading') {
  document.addEventListener('DOMContentLoaded', () => {
    window.app = new LinkpointApp();
    window.app.init();
  });
} else {
  window.app = new LinkpointApp();
  window.app.init();
}
