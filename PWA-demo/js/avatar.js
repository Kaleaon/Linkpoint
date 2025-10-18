/**
 * Linkpoint PWA - Avatar Manager (Features 13-16)
 * Ported from Android slproto/avatar/
 * 
 * Android Source: app/src/main/java/com/lumiyaviewer/lumiya/slproto/avatar/
 * See: PWA-demo/ANDROID_PORT_ROADMAP.md Phase 2, Priority 1 (Features 13-16)
 * 
 * Handles avatar appearance parameters, attachment points, visual parameters,
 * and basic avatar skeleton functionality for the Second Life protocol.
 */

/**
 * AvatarManager class
 * Manages avatar appearance, attachments, and visual parameters
 */
class AvatarManager {
  constructor() {
    // Feature 13: Avatar appearance parameters
    this.appearanceParams = new Map();
    this.visualParams = new Map();
    this.wearables = new Map(); // Type -> { assetId, itemId }
    this.bakedTextures = new Map(); // Index -> texture UUID
    this.serialNumber = 0;
    
    // Feature 14: Attachment points
    this.attachmentPoints = this.initAttachmentPoints();
    this.activeAttachments = new Map();
    
    // Feature 15: Visual parameters (shape, skin settings)
    this.shapeParams = new Map();
    this.skinParams = new Map();
    this.visualParamDefinitions = this.initVisualParamDefinitions();
    
    // Feature 16: Avatar skeleton basics
    this.skeleton = null;
    this.boneHierarchy = new Map();
    this.joints = new Map();
    this.initSkeleton();
    
    // Callbacks
    this.appearanceChangeCallbacks = [];
    this.attachmentChangeCallbacks = [];
  }
  
  /**
   * Initialize standard Second Life attachment points
   * @returns {Map} Attachment point definitions
   */
  initAttachmentPoints() {
    const points = new Map();
    
    // Standard SL attachment points with proper positions
    // Based on Second Life avatar skeleton
    points.set(1, { name: 'ATTACH_CHEST', position: [0.15, 0, 0], parent: 'mChest' });
    points.set(2, { name: 'ATTACH_HEAD', position: [0.08, 0, 0.03], parent: 'mHead' });
    points.set(3, { name: 'ATTACH_LSHOULDER', position: [0.08, 0.22, 0], parent: 'mCollarLeft' });
    points.set(4, { name: 'ATTACH_RSHOULDER', position: [0.08, -0.22, 0], parent: 'mCollarRight' });
    points.set(5, { name: 'ATTACH_LHAND', position: [0.08, 0, 0], parent: 'mWristLeft' });
    points.set(6, { name: 'ATTACH_RHAND', position: [0.08, 0, 0], parent: 'mWristRight' });
    points.set(7, { name: 'ATTACH_LFOOT', position: [0, 0, -0.08], parent: 'mFootLeft' });
    points.set(8, { name: 'ATTACH_RFOOT', position: [0, 0, -0.08], parent: 'mFootRight' });
    points.set(9, { name: 'ATTACH_BACK', position: [-0.15, 0, 0], parent: 'mChest' });
    points.set(10, { name: 'ATTACH_PELVIS', position: [0, 0, 0], parent: 'mPelvis' });
    points.set(11, { name: 'ATTACH_MOUTH', position: [0.11, 0, 0], parent: 'mHead' });
    points.set(12, { name: 'ATTACH_CHIN', position: [0.08, 0, -0.04], parent: 'mHead' });
    points.set(13, { name: 'ATTACH_LEAR', position: [0.08, 0.08, 0.025], parent: 'mHead' });
    points.set(14, { name: 'ATTACH_REAR', position: [0.08, -0.08, 0.025], parent: 'mHead' });
    points.set(15, { name: 'ATTACH_LEYE', position: [0.11, 0.03, 0.03], parent: 'mHead' });
    points.set(16, { name: 'ATTACH_REYE', position: [0.11, -0.03, 0.03], parent: 'mHead' });
    points.set(17, { name: 'ATTACH_NOSE', position: [0.12, 0, 0.015], parent: 'mHead' });
    points.set(18, { name: 'ATTACH_RUARM', position: [0.08, 0, 0], parent: 'mElbowRight' });
    points.set(19, { name: 'ATTACH_RLARM', position: [0.08, 0, 0], parent: 'mWristRight' });
    points.set(20, { name: 'ATTACH_LUARM', position: [0.08, 0, 0], parent: 'mElbowLeft' });
    points.set(21, { name: 'ATTACH_LLARM', position: [0.08, 0, 0], parent: 'mWristLeft' });
    points.set(22, { name: 'ATTACH_RHIP', position: [0, -0.1, 0], parent: 'mPelvis' });
    points.set(23, { name: 'ATTACH_RULEG', position: [0, 0, -0.15], parent: 'mKneeRight' });
    points.set(24, { name: 'ATTACH_RLLEG', position: [0, 0, -0.15], parent: 'mAnkleRight' });
    points.set(25, { name: 'ATTACH_LHIP', position: [0, 0.1, 0], parent: 'mPelvis' });
    points.set(26, { name: 'ATTACH_LULEG', position: [0, 0, -0.15], parent: 'mKneeLeft' });
    points.set(27, { name: 'ATTACH_LLLEG', position: [0, 0, -0.15], parent: 'mAnkleLeft' });
    points.set(28, { name: 'ATTACH_BELLY', position: [0.08, 0, 0], parent: 'mPelvis' });
    points.set(29, { name: 'ATTACH_LEFT_PEC', position: [0.08, 0.07, 0.02], parent: 'mChest' });
    points.set(30, { name: 'ATTACH_RIGHT_PEC', position: [0.08, -0.07, 0.02], parent: 'mChest' });
    points.set(31, { name: 'ATTACH_HUD_CENTER_2', position: [0, 0, 0], parent: 'HUD' });
    points.set(32, { name: 'ATTACH_HUD_TOP_RIGHT', position: [0, 0, 0], parent: 'HUD' });
    points.set(33, { name: 'ATTACH_HUD_TOP_CENTER', position: [0, 0, 0], parent: 'HUD' });
    points.set(34, { name: 'ATTACH_HUD_TOP_LEFT', position: [0, 0, 0], parent: 'HUD' });
    points.set(35, { name: 'ATTACH_HUD_CENTER_1', position: [0, 0, 0], parent: 'HUD' });
    points.set(36, { name: 'ATTACH_HUD_BOTTOM_LEFT', position: [0, 0, 0], parent: 'HUD' });
    points.set(37, { name: 'ATTACH_HUD_BOTTOM', position: [0, 0, 0], parent: 'HUD' });
    points.set(38, { name: 'ATTACH_HUD_BOTTOM_RIGHT', position: [0, 0, 0], parent: 'HUD' });
    points.set(39, { name: 'ATTACH_NECK', position: [0.08, 0, 0], parent: 'mNeck' });
    points.set(40, { name: 'ATTACH_AVATAR_CENTER', position: [0, 0, 0], parent: 'mRoot' });
    
    return points;
  }
  
  /**
   * Initialize visual parameter definitions
   * Common Second Life visual parameters
   */
  initVisualParamDefinitions() {
    const params = new Map();
    
    // Shape parameters
    params.set(33, { name: 'Body Height', group: 'shape', min: 0, max: 255, default: 128 });
    params.set(34, { name: 'Body Thickness', group: 'shape', min: 0, max: 255, default: 128 });
    params.set(35, { name: 'Body Fat', group: 'shape', min: 0, max: 255, default: 0 });
    params.set(36, { name: 'Head Size', group: 'shape', min: 0, max: 255, default: 128 });
    params.set(37, { name: 'Head Stretch', group: 'shape', min: 0, max: 255, default: 128 });
    params.set(38, { name: 'Torso Length', group: 'shape', min: 0, max: 255, default: 128 });
    
    // Skin parameters
    params.set(108, { name: 'Rainbow Color', group: 'skin', min: 0, max: 255, default: 0 });
    params.set(110, { name: 'Red Skin', group: 'skin', min: 0, max: 255, default: 0 });
    params.set(111, { name: 'Pigment', group: 'skin', min: 0, max: 255, default: 128 });
    params.set(112, { name: 'Freckles', group: 'skin', min: 0, max: 255, default: 0 });
    
    // Hair parameters
    params.set(180, { name: 'Hair Volume', group: 'hair', min: 0, max: 255, default: 160 });
    params.set(181, { name: 'Hair Front', group: 'hair', min: 0, max: 255, default: 140 });
    params.set(182, { name: 'Hair Sides', group: 'hair', min: 0, max: 255, default: 140 });
    params.set(183, { name: 'Hair Back', group: 'hair', min: 0, max: 255, default: 170 });
    
    return params;
  }
  
  /**
   * Feature 16: Initialize avatar skeleton
   * Basic skeleton structure for Second Life avatars
   */
  initSkeleton() {
    // Simplified skeleton hierarchy
    const bones = [
      { name: 'mPelvis', parent: null, offset: [0, 0, 0.84] },
      { name: 'mTorso', parent: 'mPelvis', offset: [0, 0, 0.08] },
      { name: 'mChest', parent: 'mTorso', offset: [0, 0, 0.15] },
      { name: 'mNeck', parent: 'mChest', offset: [0, 0, 0.19] },
      { name: 'mHead', parent: 'mNeck', offset: [0, 0, 0.06] },
      { name: 'mCollarLeft', parent: 'mChest', offset: [0, 0.1, 0.18] },
      { name: 'mShoulderLeft', parent: 'mCollarLeft', offset: [0, 0.09, 0] },
      { name: 'mElbowLeft', parent: 'mShoulderLeft', offset: [0, 0.24, 0] },
      { name: 'mWristLeft', parent: 'mElbowLeft', offset: [0, 0.21, 0] },
      { name: 'mCollarRight', parent: 'mChest', offset: [0, -0.1, 0.18] },
      { name: 'mShoulderRight', parent: 'mCollarRight', offset: [0, -0.09, 0] },
      { name: 'mElbowRight', parent: 'mShoulderRight', offset: [0, -0.24, 0] },
      { name: 'mWristRight', parent: 'mElbowRight', offset: [0, -0.21, 0] },
      { name: 'mHipLeft', parent: 'mPelvis', offset: [0, 0.09, 0] },
      { name: 'mKneeLeft', parent: 'mHipLeft', offset: [0, 0, -0.42] },
      { name: 'mAnkleLeft', parent: 'mKneeLeft', offset: [0, 0, -0.41] },
      { name: 'mFootLeft', parent: 'mAnkleLeft', offset: [0.08, 0, -0.03] },
      { name: 'mHipRight', parent: 'mPelvis', offset: [0, -0.09, 0] },
      { name: 'mKneeRight', parent: 'mHipRight', offset: [0, 0, -0.42] },
      { name: 'mAnkleRight', parent: 'mKneeRight', offset: [0, 0, -0.41] },
      { name: 'mFootRight', parent: 'mAnkleRight', offset: [0.08, 0, -0.03] },
    ];
    
    this.skeleton = bones;
    
    // Build bone hierarchy
    bones.forEach(bone => {
      this.boneHierarchy.set(bone.name, {
        parent: bone.parent,
        children: [],
        offset: bone.offset,
        rotation: [0, 0, 0, 1], // Quaternion
        scale: [1, 1, 1]
      });
    });
    
    // Link children to parents
    bones.forEach(bone => {
      if (bone.parent) {
        const parent = this.boneHierarchy.get(bone.parent);
        if (parent) {
          parent.children.push(bone.name);
        }
      }
    });
  }
  
  /**
   * Set avatar appearance parameter
   * @param {number} paramId - Visual parameter ID
   * @param {number} value - Parameter value (0-255 typically)
   */
  setAppearanceParam(paramId, value) {
    this.appearanceParams.set(paramId, value);
    
    // Update specific parameter groups
    const paramDef = this.visualParamDefinitions.get(paramId);
    if (paramDef) {
      if (paramDef.group === 'shape') {
        this.shapeParams.set(paramId, value);
      } else if (paramDef.group === 'skin') {
        this.skinParams.set(paramId, value);
      }
    }
    
    this.serialNumber++;
    this.notifyAppearanceChange();
  }
  
  /**
   * Get appearance parameter value
   * @param {number} paramId - Visual parameter ID
   * @returns {number|null} Parameter value or null if not set
   */
  getAppearanceParam(paramId) {
    return this.appearanceParams.get(paramId) ?? null;
  }
  
  /**
   * Set multiple appearance parameters at once
   * @param {Object} params - Map of paramId -> value
   */
  setAppearanceParams(params) {
    for (const [paramId, value] of Object.entries(params)) {
      this.setAppearanceParam(parseInt(paramId), value);
    }
  }
  
  /**
   * Get all appearance parameters
   * @returns {Object} All appearance parameters
   */
  getAllAppearanceParams() {
    return Object.fromEntries(this.appearanceParams);
  }
  
  /**
   * Feature 14: Attach object to attachment point
   * @param {number} pointId - Attachment point ID
   * @param {string} objectUUID - UUID of object to attach
   * @param {Object} options - Additional attachment options
   */
  attachObject(pointId, objectUUID, options = {}) {
    if (!this.attachmentPoints.has(pointId)) {
      console.warn(`Invalid attachment point: ${pointId}`);
      return false;
    }
    
    const point = this.attachmentPoints.get(pointId);
    
    this.activeAttachments.set(pointId, {
      objectUUID,
      attachedAt: Date.now(),
      position: options.position || point.position,
      rotation: options.rotation || [0, 0, 0, 1],
      scale: options.scale || [1, 1, 1],
      parentBone: point.parent
    });
    
    this.notifyAttachmentChange('attach', pointId, objectUUID);
    return true;
  }
  
  /**
   * Detach object from attachment point
   * @param {number} pointId - Attachment point ID
   */
  detachObject(pointId) {
    const attachment = this.activeAttachments.get(pointId);
    if (!attachment) {
      return false;
    }
    
    const objectUUID = attachment.objectUUID;
    this.activeAttachments.delete(pointId);
    
    this.notifyAttachmentChange('detach', pointId, objectUUID);
    return true;
  }
  
  /**
   * Get all active attachments
   * @returns {Map} Map of attachment point ID to object UUID
   */
  getAttachments() {
    return new Map(this.activeAttachments);
  }
  
  /**
   * Get attachment at specific point
   * @param {number} pointId - Attachment point ID
   * @returns {Object|null} Attachment info or null
   */
  getAttachment(pointId) {
    return this.activeAttachments.get(pointId) || null;
  }
  
  /**
   * Find which attachment point an object is attached to
   * @param {string} objectUUID - UUID of the object
   * @returns {number|null} Attachment point ID or null
   */
  findAttachmentPoint(objectUUID) {
    for (const [pointId, attachment] of this.activeAttachments) {
      if (attachment.objectUUID === objectUUID) {
        return pointId;
      }
    }
    return null;
  }
  
  /**
   * Feature 15: Set visual parameter (shape, skin, etc.)
   * @param {number} paramId - Visual parameter ID
   * @param {number} weight - Parameter weight/value
   */
  setVisualParam(paramId, weight) {
    this.visualParams.set(paramId, weight);
    this.setAppearanceParam(paramId, weight);
  }
  
  /**
   * Get visual parameter
   * @param {number} paramId - Visual parameter ID
   * @returns {number|null} Parameter weight or null
   */
  getVisualParam(paramId) {
    return this.visualParams.get(paramId) ?? null;
  }
  
  /**
   * Set wearable for a specific type
   * @param {number} type - Wearable type
   * @param {string} assetId - Asset UUID
   * @param {string} itemId - Inventory item UUID
   */
  setWearable(type, assetId, itemId) {
    this.wearables.set(type, { assetId, itemId });
    this.serialNumber++;
    this.notifyAppearanceChange();
  }
  
  /**
   * Remove wearable of specific type
   * @param {number} type - Wearable type
   */
  removeWearable(type) {
    const removed = this.wearables.delete(type);
    if (removed) {
      this.serialNumber++;
      this.notifyAppearanceChange();
    }
    return removed;
  }
  
  /**
   * Get all wearables
   * @returns {Map} Map of type -> {assetId, itemId}
   */
  getWearables() {
    return new Map(this.wearables);
  }
  
  /**
   * Set baked texture for index
   * @param {number} index - Baked texture index (0-4)
   * @param {string} textureUUID - Texture UUID
   */
  setBakedTexture(index, textureUUID) {
    this.bakedTextures.set(index, textureUUID);
    this.notifyAppearanceChange();
  }
  
  /**
   * Get baked textures
   * @returns {Map} Map of index -> texture UUID
   */
  getBakedTextures() {
    return new Map(this.bakedTextures);
  }
  
  /**
   * Get bone from skeleton
   * @param {string} boneName - Name of the bone
   * @returns {Object|null} Bone data or null
   */
  getBone(boneName) {
    return this.boneHierarchy.get(boneName) || null;
  }
  
  /**
   * Set bone rotation
   * @param {string} boneName - Name of the bone
   * @param {Array} rotation - Quaternion [x, y, z, w]
   */
  setBoneRotation(boneName, rotation) {
    const bone = this.boneHierarchy.get(boneName);
    if (bone) {
      bone.rotation = rotation;
    }
  }
  
  /**
   * Get bone world position (including parent transforms)
   * @param {string} boneName - Name of the bone
   * @returns {Array} World position [x, y, z]
   */
  getBoneWorldPosition(boneName) {
    const bone = this.boneHierarchy.get(boneName);
    if (!bone) return [0, 0, 0];
    
    let position = [...bone.offset];
    let currentBone = bone;
    
    // Walk up the hierarchy
    while (currentBone.parent) {
      const parent = this.boneHierarchy.get(currentBone.parent);
      if (!parent) break;
      
      position[0] += parent.offset[0];
      position[1] += parent.offset[1];
      position[2] += parent.offset[2];
      
      currentBone = parent;
    }
    
    return position;
  }
  
  /**
   * Update avatar from appearance message
   * @param {Object} appearanceData - Appearance data from protocol
   */
  updateAppearance(appearanceData) {
    if (appearanceData.visualParams) {
      for (const [paramId, value] of Object.entries(appearanceData.visualParams)) {
        this.setVisualParam(parseInt(paramId), value);
      }
    }
    
    if (appearanceData.wearables) {
      for (const [type, wearable] of Object.entries(appearanceData.wearables)) {
        this.setWearable(parseInt(type), wearable.assetId, wearable.itemId);
      }
    }
    
    if (appearanceData.bakedTextures) {
      for (const [index, textureUUID] of Object.entries(appearanceData.bakedTextures)) {
        this.setBakedTexture(parseInt(index), textureUUID);
      }
    }
    
    if (appearanceData.serialNumber !== undefined) {
      this.serialNumber = appearanceData.serialNumber;
    }
  }
  
  /**
   * Export appearance to protocol format
   * @returns {Object} Appearance data for protocol
   */
  exportAppearance() {
    return {
      serialNumber: this.serialNumber,
      visualParams: Object.fromEntries(this.visualParams),
      wearables: Object.fromEntries(this.wearables),
      bakedTextures: Object.fromEntries(this.bakedTextures),
      attachments: Array.from(this.activeAttachments.entries()).map(([pointId, attachment]) => ({
        pointId,
        objectUUID: attachment.objectUUID
      }))
    };
  }
  
  /**
   * Register callback for appearance changes
   * @param {Function} callback - Callback function
   */
  onAppearanceChange(callback) {
    this.appearanceChangeCallbacks.push(callback);
  }
  
  /**
   * Register callback for attachment changes
   * @param {Function} callback - Callback function
   */
  onAttachmentChange(callback) {
    this.attachmentChangeCallbacks.push(callback);
  }
  
  /**
   * Notify listeners of appearance change
   */
  notifyAppearanceChange() {
    this.appearanceChangeCallbacks.forEach(cb => {
      try {
        cb(this.exportAppearance());
      } catch (error) {
        console.error('Error in appearance change callback:', error);
      }
    });
  }
  
  /**
   * Notify listeners of attachment change
   * @param {string} action - 'attach' or 'detach'
   * @param {number} pointId - Attachment point ID
   * @param {string} objectUUID - Object UUID
   */
  notifyAttachmentChange(action, pointId, objectUUID) {
    this.attachmentChangeCallbacks.forEach(cb => {
      try {
        cb(action, pointId, objectUUID);
      } catch (error) {
        console.error('Error in attachment change callback:', error);
      }
    });
  }
  
  /**
   * Reset avatar to default state
   */
  reset() {
    this.appearanceParams.clear();
    this.visualParams.clear();
    this.wearables.clear();
    this.bakedTextures.clear();
    this.activeAttachments.clear();
    this.shapeParams.clear();
    this.skinParams.clear();
    this.serialNumber = 0;
    this.notifyAppearanceChange();
  }
}

// Export singleton instance
const avatarManager = new AvatarManager();
export default avatarManager;
export { AvatarManager };
