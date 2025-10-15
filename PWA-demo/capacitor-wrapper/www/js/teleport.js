/**
 * Linkpoint PWA - Teleport System
 */

class TeleportManager extends Utils.EventEmitter {
  constructor(protocolManager, authManager, worldViewer) {
    super();
    this.protocol = protocolManager;
    this.auth = authManager;
    this.world = worldViewer;
    this.history = [];
    this.landmarks = new Map();
    this.teleporting = false;
  }

  /**
   * Initialize teleport manager
   */
  async init() {
    // Setup protocol listeners
    this.protocol.on('teleport_progress', (data) => this.handleTeleportProgress(data));
    this.protocol.on('teleport_finished', (data) => this.handleTeleportFinished(data));
    this.protocol.on('teleport_failed', (data) => this.handleTeleportFailed(data));

    // Load landmarks
    this.loadLandmarks();
  }

  /**
   * Teleport to coordinates
   */
  async teleportTo(region, x, y, z) {
    if (this.teleporting) {
      Utils.showToast('Teleport in progress...', 'warning');
      return;
    }

    if (!this.auth.isLoggedIn()) {
      Utils.showToast('Please login first', 'warning');
      return;
    }

    this.teleporting = true;
    
    try {
      // Send teleport request
      this.protocol.sendMessage('TeleportLocationRequest', {
        region_name: region,
        position_x: x,
        position_y: y,
        position_z: z
      });

      Utils.showToast(`Teleporting to ${region}...`, 'info');
      this.emit('teleport_started', { region, x, y, z });

      // Add to history
      this.addToHistory({ region, x, y, z, timestamp: Date.now() });

      // Simulate teleport for demo
      await Utils.sleep(2000);
      this.handleTeleportFinished({ region, x, y, z });

    } catch (error) {
      console.error('Teleport failed:', error);
      this.teleporting = false;
      Utils.showToast('Teleport failed', 'error');
      this.emit('teleport_failed', error);
    }
  }

  /**
   * Teleport by SLURL
   */
  async teleportBySLURL(slurl) {
    // Parse SLURL: secondlife://Region/X/Y/Z
    const match = slurl.match(/secondlife:\/\/([^\/]+)\/(\d+)\/(\d+)\/(\d+)/);
    
    if (!match) {
      Utils.showToast('Invalid SLURL format', 'error');
      return;
    }

    const [, region, x, y, z] = match;
    await this.teleportTo(
      decodeURIComponent(region),
      parseInt(x),
      parseInt(y),
      parseInt(z)
    );
  }

  /**
   * Teleport home
   */
  async teleportHome() {
    const user = this.auth.getUser();
    if (!user) return;

    // Get home location from user data
    const home = user.home || { region: 'Home', x: 128, y: 128, z: 25 };
    await this.teleportTo(home.region, home.x, home.y, home.z);
  }

  /**
   * Teleport to landmark
   */
  async teleportToLandmark(landmarkId) {
    const landmark = this.landmarks.get(landmarkId);
    if (!landmark) {
      Utils.showToast('Landmark not found', 'error');
      return;
    }

    await this.teleportTo(
      landmark.region,
      landmark.x,
      landmark.y,
      landmark.z
    );
  }

  /**
   * Create landmark at current location
   */
  createLandmark(name, description = '') {
    const camera = this.world.camera;
    const region = this.world.region;

    const landmark = {
      id: Utils.generateUUID(),
      name,
      description,
      region: region.name,
      x: Math.floor(camera.x),
      y: Math.floor(camera.y),
      z: Math.floor(camera.z),
      created: Date.now()
    };

    this.landmarks.set(landmark.id, landmark);
    this.saveLandmarks();

    Utils.showToast(`Landmark created: ${name}`, 'success');
    this.emit('landmark_created', landmark);

    return landmark;
  }

  /**
   * Delete landmark
   */
  deleteLandmark(landmarkId) {
    const landmark = this.landmarks.get(landmarkId);
    if (!landmark) return;

    this.landmarks.delete(landmarkId);
    this.saveLandmarks();

    Utils.showToast(`Landmark deleted: ${landmark.name}`, 'info');
    this.emit('landmark_deleted', landmarkId);
  }

  /**
   * Get all landmarks
   */
  getLandmarks() {
    return Array.from(this.landmarks.values());
  }

  /**
   * Add to teleport history
   */
  addToHistory(location) {
    this.history.unshift(location);
    
    // Keep only last 50 teleports
    if (this.history.length > 50) {
      this.history = this.history.slice(0, 50);
    }

    this.saveHistory();
  }

  /**
   * Get teleport history
   */
  getHistory() {
    return this.history;
  }

  /**
   * Clear history
   */
  clearHistory() {
    this.history = [];
    this.saveHistory();
    Utils.showToast('Teleport history cleared', 'info');
  }

  /**
   * Handle teleport progress
   */
  handleTeleportProgress(data) {
    Utils.showToast(`Teleport progress: ${data.progress}%`, 'info');
    this.emit('teleport_progress', data);
  }

  /**
   * Handle teleport finished
   */
  handleTeleportFinished(data) {
    this.teleporting = false;

    // Update world viewer
    if (this.world) {
      this.world.region.name = data.region;
      this.world.camera.x = data.x;
      this.world.camera.y = data.y;
      this.world.camera.z = data.z;
      this.world.updateLocationDisplay();
    }

    Utils.showToast(`Arrived at ${data.region}`, 'success');
    this.emit('teleport_finished', data);
  }

  /**
   * Handle teleport failed
   */
  handleTeleportFailed(data) {
    this.teleporting = false;
    Utils.showToast(`Teleport failed: ${data.reason}`, 'error');
    this.emit('teleport_failed', data);
  }

  /**
   * Save landmarks to storage
   */
  saveLandmarks() {
    const landmarksArray = Array.from(this.landmarks.values());
    Utils.storage.set('linkpoint_landmarks', landmarksArray);
  }

  /**
   * Load landmarks from storage
   */
  loadLandmarks() {
    const landmarksArray = Utils.storage.get('linkpoint_landmarks', []);
    this.landmarks.clear();
    
    landmarksArray.forEach(landmark => {
      this.landmarks.set(landmark.id, landmark);
    });
  }

  /**
   * Save history to storage
   */
  saveHistory() {
    Utils.storage.set('linkpoint_teleport_history', this.history);
  }

  /**
   * Load history from storage
   */
  loadHistory() {
    this.history = Utils.storage.get('linkpoint_teleport_history', []);
  }

  /**
   * Check if teleporting
   */
  isTeleporting() {
    return this.teleporting;
  }
}

// Make available globally
window.TeleportManager = TeleportManager;
