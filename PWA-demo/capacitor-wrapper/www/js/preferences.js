/**
 * Linkpoint PWA - Preferences/Settings Manager
 */

class PreferencesManager extends Utils.EventEmitter {
  constructor() {
    super();
    this.preferences = this.getDefaultPreferences();
  }

  /**
   * Get default preferences
   */
  getDefaultPreferences() {
    return {
      // Graphics
      graphics: {
        quality: 'medium',
        fov: 60,
        drawDistance: 128,
        shadows: true,
        antialiasing: true,
        vsync: true,
        fps: 60
      },
      
      // Audio
      audio: {
        masterVolume: 1.0,
        uiVolume: 0.8,
        ambientVolume: 0.6,
        voiceVolume: 1.0,
        mediaVolume: 0.8,
        spatialAudio: true
      },
      
      // Voice
      voice: {
        enabled: false,
        pushToTalk: false,
        inputDevice: 'default',
        outputDevice: 'default',
        echoCancellation: true,
        noiseSuppression: true,
        autoGainControl: true
      },
      
      // Chat
      chat: {
        fontSize: 14,
        timestamps: true,
        showJoinLeave: true,
        maxHistory: 1000,
        playSound: true,
        toastNotifications: true,
        translateChat: false
      },
      
      // Privacy
      privacy: {
        showOnlineStatus: true,
        allowTeleportOffers: true,
        allowInventoryOffers: true,
        showInSearch: true,
        allowGroupInvites: true,
        blockUnknownCallers: false
      },
      
      // Notifications
      notifications: {
        enabled: true,
        friendOnline: true,
        friendOffline: false,
        groupNotices: true,
        inventoryReceived: true,
        teleportOffers: true,
        soundEnabled: true,
        desktopNotifications: true
      },
      
      // Interface
      interface: {
        theme: 'dark',
        language: 'en',
        compactMode: false,
        showMinimap: true,
        showRadar: true,
        showFPS: true,
        tooltips: true
      },
      
      // Controls
      controls: {
        invertMouse: false,
        mouseSensitivity: 1.0,
        cameraSmooth: true,
        doubleClickTeleport: false,
        autoRun: false
      },
      
      // Network
      network: {
        bandwidth: 'auto',
        maxConcurrentDownloads: 4,
        cacheSize: 500, // MB
        offlineMode: false
      }
    };
  }

  /**
   * Initialize preferences
   */
  async init() {
    this.load();
    this.applyPreferences();
  }

  /**
   * Get preference value
   */
  get(category, key) {
    if (key) {
      return this.preferences[category]?.[key];
    }
    return this.preferences[category];
  }

  /**
   * Set preference value
   */
  set(category, key, value) {
    if (!this.preferences[category]) {
      this.preferences[category] = {};
    }

    const oldValue = this.preferences[category][key];
    this.preferences[category][key] = value;

    this.save();
    this.emit('preference_changed', { category, key, value, oldValue });

    // Apply specific preferences
    this.applyPreference(category, key, value);
  }

  /**
   * Set multiple preferences
   */
  setMultiple(category, values) {
    Object.entries(values).forEach(([key, value]) => {
      this.set(category, key, value);
    });
  }

  /**
   * Reset category to defaults
   */
  resetCategory(category) {
    const defaults = this.getDefaultPreferences();
    this.preferences[category] = { ...defaults[category] };
    this.save();
    this.applyCategory(category);
    this.emit('category_reset', category);
  }

  /**
   * Reset all to defaults
   */
  resetAll() {
    const confirmed = confirm('Reset all preferences to defaults?');
    if (!confirmed) return;

    this.preferences = this.getDefaultPreferences();
    this.save();
    this.applyPreferences();
    Utils.showToast('All preferences reset to defaults', 'success');
    this.emit('all_reset');
  }

  /**
   * Apply all preferences
   */
  applyPreferences() {
    Object.keys(this.preferences).forEach(category => {
      this.applyCategory(category);
    });
  }

  /**
   * Apply category preferences
   */
  applyCategory(category) {
    const prefs = this.preferences[category];
    if (!prefs) return;

    Object.entries(prefs).forEach(([key, value]) => {
      this.applyPreference(category, key, value);
    });
  }

  /**
   * Apply specific preference
   */
  applyPreference(category, key, value) {
    switch (category) {
      case 'interface':
        this.applyInterfacePreference(key, value);
        break;
      case 'graphics':
        this.applyGraphicsPreference(key, value);
        break;
      case 'audio':
        this.applyAudioPreference(key, value);
        break;
      case 'chat':
        this.applyChatPreference(key, value);
        break;
      case 'notifications':
        this.applyNotificationPreference(key, value);
        break;
    }
  }

  /**
   * Apply interface preference
   */
  applyInterfacePreference(key, value) {
    switch (key) {
      case 'theme':
        document.body.dataset.theme = value;
        break;
      case 'showFPS':
        const fpsCounter = document.getElementById('fps-counter');
        if (fpsCounter) {
          fpsCounter.parentElement.style.display = value ? 'flex' : 'none';
        }
        break;
    }
  }

  /**
   * Apply graphics preference
   */
  applyGraphicsPreference(key, value) {
    // Graphics preferences would affect WorldViewer
    if (window.app?.world) {
      window.app.world.emit('graphics_setting_changed', { key, value });
    }
  }

  /**
   * Apply audio preference
   */
  applyAudioPreference(key, value) {
    // Audio preferences would affect VoiceManager
    if (window.app?.voice) {
      if (key === 'voiceVolume') {
        window.app.voice.setVolume(value);
      }
    }
  }

  /**
   * Apply chat preference
   */
  applyChatPreference(key, value) {
    // Chat preferences would affect ChatManager
    if (window.app?.chat) {
      window.app.chat.emit('preference_changed', { key, value });
    }
  }

  /**
   * Apply notification preference
   */
  applyNotificationPreference(key, value) {
    if (key === 'desktopNotifications' && value) {
      this.requestNotificationPermission();
    }
  }

  /**
   * Request notification permission
   */
  async requestNotificationPermission() {
    if (!('Notification' in window)) {
      console.warn('Notifications not supported');
      return false;
    }

    if (Notification.permission === 'granted') {
      return true;
    }

    if (Notification.permission !== 'denied') {
      const permission = await Notification.requestPermission();
      return permission === 'granted';
    }

    return false;
  }

  /**
   * Export preferences
   */
  export() {
    const json = JSON.stringify(this.preferences, null, 2);
    const blob = new Blob([json], { type: 'application/json' });
    const url = URL.createObjectURL(blob);
    
    const a = document.createElement('a');
    a.href = url;
    a.download = `linkpoint-preferences-${Date.now()}.json`;
    a.click();
    
    URL.revokeObjectURL(url);
    Utils.showToast('Preferences exported', 'success');
  }

  /**
   * Import preferences
   */
  async import(file) {
    try {
      const text = await file.text();
      const imported = JSON.parse(text);
      
      // Validate structure
      if (typeof imported !== 'object') {
        throw new Error('Invalid preferences file');
      }

      this.preferences = { ...this.getDefaultPreferences(), ...imported };
      this.save();
      this.applyPreferences();
      
      Utils.showToast('Preferences imported successfully', 'success');
      this.emit('preferences_imported');
      
    } catch (error) {
      console.error('Failed to import preferences:', error);
      Utils.showToast('Failed to import preferences', 'error');
    }
  }

  /**
   * Save preferences to storage
   */
  save() {
    Utils.storage.set('linkpoint_preferences', this.preferences);
  }

  /**
   * Load preferences from storage
   */
  load() {
    const saved = Utils.storage.get('linkpoint_preferences');
    if (saved) {
      this.preferences = { ...this.getDefaultPreferences(), ...saved };
    }
  }

  /**
   * Get all preferences
   */
  getAll() {
    return this.preferences;
  }
}

// Make available globally
window.PreferencesManager = PreferencesManager;
