/**
 * Linkpoint PWA - Inventory Special Types (Features 31-35)
 * 
 * Phase 2: Core Protocol Extensions - Priority 2
 * Roadmap: PWA-demo/ANDROID_PORT_ROADMAP.md (Lines 65-70)
 * Android Source: app/src/main/java/com/lumiyaviewer/lumiya/slproto/modules/inventory/
 * 
 * Handles special inventory item types (gestures, animations, scripts, sounds, textures).
 * 
 * Features:
 * - Feature 31: Gestures
 * - Feature 32: Animations
 * - Feature 33: Scripts
 * - Feature 34: Sounds
 * - Feature 35: Textures
 * 
 * @module phase2/inventory-types
 */

/**
 * Inventory Special Types Manager
 * Handles special asset types in inventory
 */
class InventorySpecialTypes {
  constructor() {
    this.gestures = new Map();
    this.animations = new Map();
    this.scripts = new Map();
    this.sounds = new Map();
    this.textures = new Map();
  }

  /**
   * Feature 31: Gestures
   * Register and manage gesture assets
   * 
   * @param {string} gestureId - Gesture asset UUID
   * @param {Object} gestureData - Gesture configuration
   * 
   * TODO: Implement gesture sequence parsing (see Android GestureAsset)
   * TODO: Add trigger word support
   */
  registerGesture(gestureId, gestureData) {
    if (!gestureId || typeof gestureId !== 'string') {
      throw new Error('Valid gesture ID required');
    }
    if (!gestureData || typeof gestureData !== 'object') {
      throw new Error('Valid gesture data required');
    }
    
    const gesture = {
      id: gestureId,
      name: gestureData.name || 'Gesture',
      trigger: gestureData.trigger || '',
      animations: gestureData.animations || [],
      sounds: gestureData.sounds || [],
      active: gestureData.active || false,
      ...gestureData
    };
    
    this.gestures.set(gestureId, gesture);
    console.log(`[InventoryTypes] Registered gesture: ${gesture.name}`);
  }

  /**
   * Activate/deactivate gesture
   * @param {string} gestureId - Gesture UUID
   * @param {boolean} active - Active state
   */
  setGestureActive(gestureId, active = true) {
    const gesture = this.gestures.get(gestureId);
    if (gesture) {
      gesture.active = active;
      console.log(`[InventoryTypes] Gesture ${gestureId} ${active ? 'activated' : 'deactivated'}`);
    }
  }

  /**
   * Feature 32: Animations
   * Register animation asset
   * 
   * @param {string} animId - Animation asset UUID
   * @param {Object} animData - Animation configuration
   * 
   * TODO: Implement animation keyframe parsing
   * TODO: Add animation priority and blending
   */
  registerAnimation(animId, animData) {
    if (!animId || typeof animId !== 'string') {
      throw new Error('Valid animation ID required');
    }
    if (!animData || typeof animData !== 'object') {
      throw new Error('Valid animation data required');
    }
    
    const animation = {
      id: animId,
      name: animData.name || 'Animation',
      duration: animData.duration || 0,
      loop: animData.loop || false,
      priority: animData.priority || 0,
      ...animData
    };
    
    this.animations.set(animId, animation);
    console.log(`[InventoryTypes] Registered animation: ${animation.name}`);
  }

  /**
   * Get animation data
   * @param {string} animId - Animation UUID
   * @returns {Object|null}
   */
  getAnimation(animId) {
    return this.animations.get(animId) || null;
  }

  /**
   * Feature 33: Scripts
   * Register script asset
   * 
   * @param {string} scriptId - Script asset UUID
   * @param {Object} scriptData - Script configuration
   * 
   * TODO: Implement script compilation state tracking
   * TODO: Add script memory and execution stats
   */
  registerScript(scriptId, scriptData) {
    if (!scriptId || typeof scriptId !== 'string') {
      throw new Error('Valid script ID required');
    }
    if (!scriptData || typeof scriptData !== 'object') {
      throw new Error('Valid script data required');
    }
    
    const script = {
      id: scriptId,
      name: scriptData.name || 'Script',
      state: scriptData.state || 'stopped', // stopped, running, error
      compiled: scriptData.compiled || false,
      errors: scriptData.errors || [],
      ...scriptData
    };
    
    this.scripts.set(scriptId, script);
    console.log(`[InventoryTypes] Registered script: ${script.name}`);
  }

  /**
   * Update script state
   * @param {string} scriptId - Script UUID
   * @param {string} state - New state
   */
  setScriptState(scriptId, state) {
    const script = this.scripts.get(scriptId);
    if (script) {
      script.state = state;
      console.log(`[InventoryTypes] Script ${scriptId} state: ${state}`);
    }
  }

  /**
   * Feature 34: Sounds
   * Register sound asset
   * 
   * @param {string} soundId - Sound asset UUID
   * @param {Object} soundData - Sound configuration
   * 
   * TODO: Implement sound preloading
   * TODO: Add spatial audio support
   */
  registerSound(soundId, soundData) {
    if (!soundId || typeof soundId !== 'string') {
      throw new Error('Valid sound ID required');
    }
    if (!soundData || typeof soundData !== 'object') {
      throw new Error('Valid sound data required');
    }
    
    const sound = {
      id: soundId,
      name: soundData.name || 'Sound',
      duration: soundData.duration || 0,
      preloaded: false,
      volume: soundData.volume || 1.0,
      ...soundData
    };
    
    this.sounds.set(soundId, sound);
    console.log(`[InventoryTypes] Registered sound: ${sound.name}`);
  }

  /**
   * Preload sound for playback
   * @param {string} soundId - Sound UUID
   * @returns {Promise<void>}
   */
  async preloadSound(soundId) {
    const sound = this.sounds.get(soundId);
    if (sound) {
      // TODO: Implement actual preloading
      sound.preloaded = true;
      console.log(`[InventoryTypes] Preloaded sound: ${soundId}`);
    }
    return Promise.resolve();
  }

  /**
   * Feature 35: Textures
   * Register texture asset
   * 
   * @param {string} textureId - Texture asset UUID
   * @param {Object} textureData - Texture configuration
   * 
   * TODO: Implement texture caching and priority
   * TODO: Add J2K decode support
   */
  registerTexture(textureId, textureData) {
    if (!textureId || typeof textureId !== 'string') {
      throw new Error('Valid texture ID required');
    }
    if (!textureData || typeof textureData !== 'object') {
      throw new Error('Valid texture data required');
    }
    
    const texture = {
      id: textureId,
      name: textureData.name || 'Texture',
      width: textureData.width || 0,
      height: textureData.height || 0,
      cached: false,
      priority: textureData.priority || 0,
      ...textureData
    };
    
    this.textures.set(textureId, texture);
    console.log(`[InventoryTypes] Registered texture: ${texture.name}`);
  }

  /**
   * Set texture cache status
   * @param {string} textureId - Texture UUID
   * @param {boolean} cached - Cache status
   */
  setTextureCached(textureId, cached = true) {
    const texture = this.textures.get(textureId);
    if (texture) {
      texture.cached = cached;
    }
  }

  /**
   * Get statistics for all special types
   * @returns {Object}
   */
  getStats() {
    return {
      gestures: {
        total: this.gestures.size,
        active: Array.from(this.gestures.values()).filter(g => g.active).length
      },
      animations: this.animations.size,
      scripts: {
        total: this.scripts.size,
        running: Array.from(this.scripts.values()).filter(s => s.state === 'running').length
      },
      sounds: {
        total: this.sounds.size,
        preloaded: Array.from(this.sounds.values()).filter(s => s.preloaded).length
      },
      textures: {
        total: this.textures.size,
        cached: Array.from(this.textures.values()).filter(t => t.cached).length
      }
    };
  }
}

// Export for use in other modules
if (typeof module !== 'undefined' && module.exports) {
  module.exports = { InventorySpecialTypes };
}

/**
 * Example Usage:
 * 
 * const specialTypes = new InventorySpecialTypes();
 * 
 * // Register gesture
 * specialTypes.registerGesture('gesture-123', {
 *   name: 'Wave',
 *   trigger: '/wave',
 *   animations: ['anim-456'],
 *   active: true
 * });
 * 
 * // Register animation
 * specialTypes.registerAnimation('anim-456', {
 *   name: 'Wave Animation',
 *   duration: 2.5,
 *   loop: false
 * });
 * 
 * // Register script
 * specialTypes.registerScript('script-789', {
 *   name: 'Hello Script',
 *   state: 'running',
 *   compiled: true
 * });
 */
