/**
 * Linkpoint PWA - Inventory Special Types (Features 31-35)
 *
 * Phase 2: Core Protocol Extensions - Priority 2
 * Roadmap: PWA-demo/ANDROID_PORT_ROADMAP.md (Lines 65-70)
 * Android Source: app/src/main/java/com/lumiyaviewer/lumiya/slproto/modules/inventory/
 *
 * Handles special inventory item types (gestures, animations, scripts, sounds, textures).
 */

export class InventorySpecialTypes {
  private gestures: Map<string, any> = new Map();
  private animations: Map<string, any> = new Map();
  private scripts: Map<string, any> = new Map();
  private sounds: Map<string, any> = new Map();
  private textures: Map<string, any> = new Map();

  /**
   * Feature 31: Gestures
   * Register and manage gesture assets
   */
  registerGesture(gestureId: string, gestureData: any) {
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

  setGestureActive(gestureId: string, active: boolean = true) {
    const gesture = this.gestures.get(gestureId);
    if (gesture) {
      gesture.active = active;
      console.log(`[InventoryTypes] Gesture ${gestureId} ${active ? 'activated' : 'deactivated'}`);
    }
  }

  /**
   * Feature 32: Animations
   * Register animation asset
   */
  registerAnimation(animId: string, animData: any) {
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

  getAnimation(animId: string) {
    return this.animations.get(animId) || null;
  }

  /**
   * Feature 33: Scripts
   * Register script asset
   */
  registerScript(scriptId: string, scriptData: any) {
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

  setScriptState(scriptId: string, state: string) {
    const script = this.scripts.get(scriptId);
    if (script) {
      script.state = state;
      console.log(`[InventoryTypes] Script ${scriptId} state: ${state}`);
    }
  }

  /**
   * Feature 34: Sounds
   * Register sound asset
   */
  registerSound(soundId: string, soundData: any) {
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

  async preloadSound(soundId: string): Promise<void> {
    const sound = this.sounds.get(soundId);

    if (!sound) {
      return Promise.resolve();
    }

    if (sound.preloaded) {
      return Promise.resolve();
    }

    return new Promise((resolve) => {
      // If there's no URL, we can't actually preload it via Audio
      if (!sound.url) {
        sound.preloaded = true;
        console.log(`[InventoryTypes] Preloaded sound (no URL): ${soundId}`);
        resolve();
        return;
      }

      try {
        const audio = new Audio(sound.url);

        audio.oncanplaythrough = () => {
          sound.preloaded = true;
          console.log(`[InventoryTypes] Preloaded sound: ${soundId}`);
          resolve();
        };

        audio.onerror = (e) => {
          console.error(`[InventoryTypes] Failed to preload sound ${soundId}: `, e);
          // Even if it fails, we resolve to avoid blocking the queue, but don't mark as preloaded
          resolve();
        };

        // For some browsers, we might need to explicitly load
        audio.load();
      } catch (err) {
        console.error(`[InventoryTypes] Error creating Audio for sound ${soundId}: `, err);
        resolve();
      }
    });
  }

  playSound(soundId: string) {
    const sound = this.sounds.get(soundId);
    if (!sound) {
      console.warn(`[InventoryTypes] Cannot play unknown sound: ${soundId}`);
      return;
    }

    if (!sound.url) {
      console.warn(`[InventoryTypes] Cannot play sound without URL: ${soundId}`);
      return;
    }

    try {
      if (!sound.audioElement) {
        sound.audioElement = new Audio(sound.url);
      }

      sound.audioElement.volume = sound.volume || 1.0;
      sound.audioElement.currentTime = 0;

      sound.audioElement.play().catch((err) => {
        console.error(`[InventoryTypes] Error playing sound ${soundId}:`, err);
      });
    } catch (err) {
      console.error(`[InventoryTypes] Error creating Audio for playing sound ${soundId}:`, err);
    }
  }

  stopSound(soundId: string) {
    const sound = this.sounds.get(soundId);
    if (sound && sound.audioElement) {
      try {
        sound.audioElement.pause();
        sound.audioElement.currentTime = 0;
      } catch (err) {
        console.error(`[InventoryTypes] Error stopping sound ${soundId}:`, err);
      }
    }
  }

  /**
   * Feature 35: Textures
   * Register texture asset
   */
  registerTexture(textureId: string, textureData: any) {
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

  setTextureCached(textureId: string, cached: boolean = true) {
    const texture = this.textures.get(textureId);
    if (texture) {
      texture.cached = cached;
    }
  }

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
