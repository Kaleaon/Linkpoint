/**
 * Linkpoint PWA - Avatar Manager (Features 13-16)
 * 
 * Phase 2: Core Protocol Extensions - Priority 1
 * Roadmap: PWA-demo/ANDROID_PORT_ROADMAP.md (Lines 35-40)
 * Android Source: app/src/main/java/com/lumiyaviewer/lumiya/slproto/users/
 * 
 * Manages avatar appearance, attachments, and visual parameters.
 * 
 * Features:
 * - Feature 13: Avatar appearance parameters
 * - Feature 14: Attachment points
 * - Feature 15: Visual parameters (shape, skin settings)
 * - Feature 16: Avatar skeleton basics
 * 
 * @module phase2/avatar
 */

/**
 * Avatar Manager
 * Manages avatar appearance and configuration
 */
class AvatarManager {
  constructor() {
    this.avatarId = null;
    this.appearanceParams = new Map();
    this.attachments = new Map();
    this.visualParams = new Map();
    this.skeletonData = null;
  }

  /**
   * Feature 13: Avatar appearance parameters
   * Set avatar appearance parameter
   * 
   * @param {string} paramName - Parameter name (e.g., 'texture', 'wearable')
   * @param {any} value - Parameter value
   * 
   * TODO: Implement full appearance serialization (see Android AvatarAppearance)
   * TODO: Add baked texture support
   */
  setAppearanceParam(paramName, value) {
    if (!paramName || typeof paramName !== 'string') {
      throw new Error('Valid parameter name required');
    }
    
    this.appearanceParams.set(paramName, value);
    console.log(`[Avatar] Set appearance: ${paramName}`);
  }

  /**
   * Get appearance parameter
   * @param {string} paramName - Parameter name
   * @returns {any}
   */
  getAppearanceParam(paramName) {
    return this.appearanceParams.get(paramName);
  }

  /**
   * Feature 14: Attachment points
   * Attach an object to avatar attachment point
   * 
   * @param {number} attachmentPoint - Attachment point ID
   * @param {Object} objectData - Object to attach
   * 
   * TODO: Implement attachment point validation
   * TODO: Add support for multiple attachments per point
   */
  attachObject(attachmentPoint, objectData) {
    if (typeof attachmentPoint !== 'number') {
      throw new Error('Valid attachment point ID required');
    }
    if (!objectData || typeof objectData !== 'object') {
      throw new Error('Valid object data required');
    }
    
    this.attachments.set(attachmentPoint, objectData);
    console.log(`[Avatar] Attached object to point ${attachmentPoint}`);
  }

  /**
   * Detach object from attachment point
   * @param {number} attachmentPoint - Attachment point ID
   */
  detachObject(attachmentPoint) {
    if (this.attachments.delete(attachmentPoint)) {
      console.log(`[Avatar] Detached object from point ${attachmentPoint}`);
    }
  }

  /**
   * Get all attachments
   * @returns {Map}
   */
  getAttachments() {
    return new Map(this.attachments);
  }

  /**
   * Feature 15: Visual parameters (shape, skin settings)
   * Set visual parameter (body shape, skin, etc.)
   * 
   * @param {number} paramId - Visual parameter ID
   * @param {number} value - Parameter value (typically 0-1 range)
   * 
   * TODO: Implement visual param bounds checking
   * TODO: Add param group support (body/skin/hair/etc)
   */
  setVisualParam(paramId, value) {
    if (typeof paramId !== 'number' || typeof value !== 'number') {
      throw new Error('Valid parameter ID and value required');
    }
    
    // Clamp value to 0-1 range
    const clampedValue = Math.max(0, Math.min(1, value));
    this.visualParams.set(paramId, clampedValue);
    console.log(`[Avatar] Set visual param ${paramId}: ${clampedValue}`);
  }

  /**
   * Get visual parameter value
   * @param {number} paramId - Visual parameter ID
   * @returns {number|null}
   */
  getVisualParam(paramId) {
    return this.visualParams.get(paramId) ?? null;
  }

  /**
   * Feature 16: Avatar skeleton basics
   * Initialize avatar skeleton structure
   * 
   * @param {Object} skeletonConfig - Skeleton configuration
   * 
   * TODO: Implement bone hierarchy (see Android skeleton data)
   * TODO: Add joint rotation support
   */
  initializeSkeleton(skeletonConfig) {
    if (!skeletonConfig || typeof skeletonConfig !== 'object') {
      throw new Error('Valid skeleton configuration required');
    }
    
    this.skeletonData = {
      bones: skeletonConfig.bones || [],
      joints: skeletonConfig.joints || [],
      initialized: true,
      timestamp: Date.now()
    };
    
    console.log(`[Avatar] Skeleton initialized with ${this.skeletonData.bones.length} bones`);
  }

  /**
   * Get skeleton data
   * @returns {Object|null}
   */
  getSkeleton() {
    return this.skeletonData;
  }

  /**
   * Export avatar configuration
   * @returns {Object}
   */
  exportConfig() {
    return {
      avatarId: this.avatarId,
      appearance: Object.fromEntries(this.appearanceParams),
      attachments: Object.fromEntries(this.attachments),
      visualParams: Object.fromEntries(this.visualParams),
      skeleton: this.skeletonData
    };
  }

  /**
   * Reset avatar to defaults
   */
  reset() {
    this.appearanceParams.clear();
    this.attachments.clear();
    this.visualParams.clear();
    this.skeletonData = null;
    console.log('[Avatar] Reset to defaults');
  }
}

// Export for use in other modules
if (typeof module !== 'undefined' && module.exports) {
  module.exports = { AvatarManager };
}

/**
 * Example Usage:
 * 
 * const avatar = new AvatarManager();
 * 
 * // Set visual parameters
 * avatar.setVisualParam(33, 0.5); // Body height
 * avatar.setVisualParam(34, 0.8); // Body thickness
 * 
 * // Attach objects
 * avatar.attachObject(3, { objectId: 'abc123', name: 'Hat' });
 * 
 * // Initialize skeleton
 * avatar.initializeSkeleton({
 *   bones: ['pelvis', 'spine', 'head'],
 *   joints: ['hip', 'knee', 'ankle']
 * });
 */
