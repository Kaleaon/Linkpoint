/**
 * Linkpoint PWA - Main Application
 */

class LinkpointApp {
  constructor() {
    this.protocol = null;
    this.auth = null;
    this.world = null;
    this.chat = null;
    this.voice = null;
    this.inventory = null;
    this.friends = null;
    this.groups = null;
    this.teleport = null;
    this.search = null;
    this.preferences = null;
    this.notifications = null;
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
    // Use real SL protocol implementation
    this.protocol = window.SLConnectionFull ? new SLConnectionFull() : new ProtocolManager();
    this.preferences = new PreferencesManager();
    this.auth = new AuthManager(this.protocol);
    this.world = new WorldViewer(this.protocol);
    this.chat = new ChatManager(this.protocol, this.auth);
    this.voice = new VoiceManager(this.protocol, this.auth);
    this.inventory = new InventoryManager(this.protocol, this.auth);
    this.friends = new FriendsManager(this.protocol, this.auth);
    this.groups = new GroupsUIManager(this.protocol, this.auth);
    this.teleport = new TeleportManager(this.protocol, this.auth, this.world);
    this.search = new SearchManager(this.protocol);
    this.notifications = new NotificationsManager(this.protocol, this.auth, this.preferences);
    
    // Real SL components
    this.meshLoader = null;
    this.objectManager = null;

    // Initialize modules
    await this.preferences.init();
    this.auth.init();
    await this.world.init();
    
    // Initialize SL-specific components if 3D is available
    if (this.world.graphics3d && window.SLMeshLoader && window.SLObjectManager) {
      this.meshLoader = new SLMeshLoader(this.world.graphics3d);
      this.objectManager = new SLObjectManager(this.world.scene3d, this.meshLoader);
      
      // Connect protocol events to object manager
      this.protocol.on('object_update', (msg) => this.objectManager.handleObjectUpdate(msg));
      this.protocol.on('kill_object', (msg) => this.objectManager.handleKillObject(msg));
      
      console.log('✅ SL Mesh and Object systems initialized');
    }
    
    this.chat.init();
    await this.voice.init();
    await this.inventory.init();
    await this.friends.init();
    this.groups.init();
    await this.teleport.init();
    await this.search.init();
    await this.notifications.init();

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
      // Use relative path for service worker to support subpath hosting
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
    // Setup navigation (support both .nav-link and .nav-item)
    const navLinks = document.querySelectorAll('.nav-link, .nav-item');
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
    
    // Setup voice controls
    this.setupVoiceControls();
    
    // Setup groups loading when view is switched
    this.on('view_changed', (view) => {
      if (view === 'groups' && this.groups) {
        this.groups.loadGroups();
      }
    });
  }
  
  /**
   * Setup voice controls UI
   */
  setupVoiceControls() {
    const voiceToggleBtn = document.getElementById('voice-toggle-btn');
    const voiceMuteBtn = document.getElementById('voice-mute-btn');
    const voiceStatus = document.getElementById('voice-status');
    const voiceIcon = document.getElementById('voice-icon');
    const voiceText = document.getElementById('voice-text');
    const muteIcon = document.getElementById('mute-icon');
    
    // Voice toggle button
    if (voiceToggleBtn) {
      voiceToggleBtn.addEventListener('click', async () => {
        if (this.voice.isEnabled()) {
          this.voice.disable();
          voiceToggleBtn.innerHTML = '<span>🎤</span>';
          voiceToggleBtn.title = 'Enable Voice';
          if (voiceMuteBtn) voiceMuteBtn.style.display = 'none';
          if (voiceIcon) voiceIcon.textContent = '🔇';
          if (voiceText) voiceText.textContent = 'Voice: Off';
        } else {
          const enabled = await this.voice.enable();
          if (enabled) {
            voiceToggleBtn.innerHTML = '<span>🎤</span>';
            voiceToggleBtn.title = 'Disable Voice';
            if (voiceMuteBtn) voiceMuteBtn.style.display = 'inline-block';
            if (voiceIcon) voiceIcon.textContent = '🔊';
            if (voiceText) voiceText.textContent = 'Voice: On';
          }
        }
      });
    }
    
    // Voice mute button
    if (voiceMuteBtn) {
      voiceMuteBtn.addEventListener('click', () => {
        const muted = this.voice.toggleMute();
        if (muteIcon) {
          muteIcon.textContent = muted ? '🔇' : '🔊';
        }
        if (voiceIcon) {
          voiceIcon.textContent = muted ? '🔇' : '🔊';
        }
        voiceMuteBtn.title = muted ? 'Unmute' : 'Mute';
      });
    }
    
    // Voice events
    if (this.voice) {
      this.voice.on('voice_enabled', () => {
        if (voiceStatus) voiceStatus.classList.add('active');
        Utils.showToast('Voice chat enabled', 'success');
      });
      
      this.voice.on('voice_disabled', () => {
        if (voiceStatus) voiceStatus.classList.remove('active');
        Utils.showToast('Voice chat disabled', 'info');
      });
      
      this.voice.on('voice_error', (error) => {
        Utils.showToast(`Voice error: ${error.message || error}`, 'error');
      });
    }
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
   * Activate post-login features
   * This connects the real SL protocol features after successful authentication
   */
  activatePostLoginFeatures() {
    console.log('[App] Activating post-login features...');
    
    try {
      // 1. Setup protocol event handlers for chat
      if (this.protocol.on) {
        // Handle incoming chat messages
        this.protocol.on('chat_from_simulator', (data) => {
          console.log('[App] Chat message from simulator:', data);
          if (this.chat && this.chat.handleIncomingMessage) {
            this.chat.handleIncomingMessage(data);
          }
          
          // Show notification
          if (this.currentView !== 'chat') {
            this.incrementNotificationCount();
            Utils.showToast(`${data.fromName || 'Unknown'}: ${data.message}`, 'info');
          }
        });
        
        // Handle object updates for 3D rendering
        this.protocol.on('object_update', (data) => {
          console.log('[App] Object update received');
          if (this.objectManager) {
            this.objectManager.handleObjectUpdate(data);
          }
        });
        
        // Handle region handshake
        this.protocol.on('region_handshake', (data) => {
          console.log('[App] Region handshake:', data);
          if (this.world && this.world.updateRegionInfo) {
            this.world.updateRegionInfo(data);
          }
        });
        
        console.log('[App] Protocol event handlers registered');
      }
      
      // 2. Start event queue if using SLConnectionFull
      if (this.protocol.eventQueueRunning === false && this.protocol.startEventQueue) {
        console.log('[App] Starting event queue...');
        this.protocol.startEventQueue();
      }
      
      // 3. Activate 3D rendering if available
      if (this.world && this.world.startRendering) {
        console.log('[App] Starting 3D rendering...');
        this.world.startRendering();
      }
      
      // 4. Subscribe to capabilities-based services
      if (this.protocol.capabilities) {
        console.log('[App] Available capabilities:', Object.keys(this.protocol.capabilities));
        
        // Start fetching display names if available
        if (this.protocol.capabilities.GetDisplayNames && window.DisplayNameManager) {
          const displayNameManager = new DisplayNameManager(this.protocol);
          console.log('[App] Display name manager initialized');
        }
      }
      
      // 5. Show status
      Utils.showToast('Connected to Second Life', 'success');
      console.log('[App] ✅ Post-login features activated');
      
    } catch (error) {
      console.error('[App] Error activating post-login features:', error);
      Utils.showToast('Warning: Some features may not be fully active', 'warning');
    }
  }

  /**
   * Switch view
   */
  switchView(viewName) {
    console.log(`[App] Switching to view: ${viewName}`);
    
    // Update nav links (support both .nav-link and .nav-item)
    document.querySelectorAll('.nav-link, .nav-item').forEach(link => {
      link.classList.toggle('active', link.dataset.view === viewName);
    });

    // Update views (support both #view-{name} and #{name}-view patterns)
    document.querySelectorAll('.view, .view-container').forEach(view => {
      const isActive = view.id === `view-${viewName}` || view.id === `${viewName}-view`;
      view.classList.toggle('active', isActive);
      
      if (isActive) {
        console.log(`[App] Activated view: ${view.id}`);
      }
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
    console.log(`[App] Current view is now: ${this.currentView}`);
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
