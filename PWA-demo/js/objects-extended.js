/**
 * Linkpoint PWA - Object Manager Extended (Features 17-20)
 * Ported from Android slproto/objects/ and slproto/prims/
 * 
 * Android Source: 
 *   - app/src/main/java/com/lumiyaviewer/lumiya/slproto/objects/
 *   - app/src/main/java/com/lumiyaviewer/lumiya/slproto/prims/
 * See: PWA-demo/ANDROID_PORT_ROADMAP.md Phase 2, Priority 1 (Features 17-20)
 * 
 * Handles extended object functionality including prim parameters, object
 * permissions, object selection, and parent-child relationships.
 */

/**
 * ObjectManagerExtended class
 * Extends basic object management with advanced prim and relationship features
 */
class ObjectManagerExtended {
  constructor() {
    // Feature 17: Prim parameters (shape, material, texture)
    this.objects = new Map();
    this.primParams = new Map();
    this.objectProperties = new Map();
    
    // Feature 18: Object permissions
    this.permissions = new Map();
    
    // Feature 19: Object selection
    this.selectedObjects = new Set();
    this.selectionCallbacks = [];
    this.selectionMode = 'single'; // 'single' or 'multiple'
    
    // Feature 20: Parent-child relationships
    this.parentChildMap = new Map();
    this.childrenMap = new Map();
    
    // Object caching
    this.objectCache = new Map();
    this.cacheTimeout = 300000; // 5 minutes
    
    // Material types
    this.MATERIALS = {
      STONE: 0,
      METAL: 1,
      GLASS: 2,
      WOOD: 3,
      FLESH: 4,
      PLASTIC: 5,
      RUBBER: 6
    };
    
    // Prim types
    this.PRIM_TYPES = {
      BOX: 0,
      CYLINDER: 1,
      PRISM: 2,
      SPHERE: 3,
      TORUS: 4,
      TUBE: 5,
      RING: 6,
      SCULPT: 7
    };
  }
  
  /**
   * Set prim parameters for an object
   * @param {string} objectUUID - UUID of the object
   * @param {Object} params - Prim parameters (shape, material, texture, etc.)
   */
  setPrimParams(objectUUID, params) {
    this.primParams.set(objectUUID, {
      // Shape parameters
      pathCurve: params.pathCurve || 0,
      profileCurve: params.profileCurve || 0,
      pathBegin: params.pathBegin || 0,
      pathEnd: params.pathEnd || 1.0,
      pathScaleX: params.pathScaleX || 1.0,
      pathScaleY: params.pathScaleY || 1.0,
      pathShearX: params.pathShearX || 0,
      pathShearY: params.pathShearY || 0,
      pathTwist: params.pathTwist || 0,
      pathTwistBegin: params.pathTwistBegin || 0,
      pathRadiusOffset: params.pathRadiusOffset || 0,
      pathTaperX: params.pathTaperX || 0,
      pathTaperY: params.pathTaperY || 0,
      pathRevolutions: params.pathRevolutions || 1.0,
      pathSkew: params.pathSkew || 0,
      
      // Profile parameters
      profileBegin: params.profileBegin || 0,
      profileEnd: params.profileEnd || 1.0,
      profileHollow: params.profileHollow || 0,
      
      // Material
      material: params.material || 0, // 0=Stone, 1=Metal, 2=Glass, 3=Wood, 4=Flesh, 5=Plastic, 6=Rubber
      
      // Textures (per face)
      textures: params.textures || [],
      
      // Physics shape type
      physicsShapeType: params.physicsShapeType || 0
    });
    
    // TODO: Notify 3D rendering of shape change
    // TODO: Update physics if needed
  }
  
  /**
   * Get prim parameters for an object
   * @param {string} objectUUID - UUID of the object
   * @returns {Object|null} Prim parameters or null if not found
   */
  getPrimParams(objectUUID) {
    return this.primParams.get(objectUUID) || null;
  }
  
  /**
   * Set object permissions
   * @param {string} objectUUID - UUID of the object
   * @param {Object} perms - Permission flags
   */
  setPermissions(objectUUID, perms) {
    this.permissions.set(objectUUID, {
      base: {
        canTransfer: perms.baseCanTransfer ?? true,
        canCopy: perms.baseCanCopy ?? true,
        canModify: perms.baseCanModify ?? true,
        canMove: perms.baseCanMove ?? true
      },
      owner: {
        canTransfer: perms.ownerCanTransfer ?? true,
        canCopy: perms.ownerCanCopy ?? true,
        canModify: perms.ownerCanModify ?? true,
        canMove: perms.ownerCanMove ?? true
      },
      group: {
        canTransfer: perms.groupCanTransfer ?? false,
        canCopy: perms.groupCanCopy ?? false,
        canModify: perms.groupCanModify ?? false,
        canMove: perms.groupCanMove ?? false
      },
      everyone: {
        canTransfer: perms.everyoneCanTransfer ?? false,
        canCopy: perms.everyoneCanCopy ?? false,
        canModify: perms.everyoneCanModify ?? false,
        canMove: perms.everyoneCanMove ?? false
      },
      nextOwner: {
        canTransfer: perms.nextOwnerCanTransfer ?? true,
        canCopy: perms.nextOwnerCanCopy ?? true,
        canModify: perms.nextOwnerCanModify ?? true
      }
    });
  }
  
  /**
   * Check if operation is permitted
   * @param {string} objectUUID - UUID of the object
   * @param {string} operation - Operation to check (transfer, copy, modify, move)
   * @param {string} scope - Permission scope (base, owner, group, everyone, nextOwner)
   * @returns {boolean} True if operation is permitted
   */
  checkPermission(objectUUID, operation, scope = 'owner') {
    const perms = this.permissions.get(objectUUID);
    if (!perms || !perms[scope]) {
      return false;
    }
    
    const opKey = `can${operation.charAt(0).toUpperCase()}${operation.slice(1)}`;
    return perms[scope][opKey] ?? false;
  }
  
  /**
   * Select an object
   * @param {string} objectUUID - UUID of the object to select
   * @param {boolean} addToSelection - If true, add to selection; if false, replace selection
   */
  selectObject(objectUUID, addToSelection = false) {
    if (!addToSelection) {
      this.selectedObjects.clear();
    }
    
    this.selectedObjects.add(objectUUID);
    this.notifySelectionChange();
    // TODO: Send ObjectSelect message to server
    // TODO: Highlight in 3D view
  }
  
  /**
   * Deselect an object
   * @param {string} objectUUID - UUID of the object to deselect
   */
  deselectObject(objectUUID) {
    this.selectedObjects.delete(objectUUID);
    this.notifySelectionChange();
    // TODO: Send ObjectDeselect message to server
    // TODO: Remove highlight in 3D view
  }
  
  /**
   * Clear all selections
   */
  clearSelection() {
    this.selectedObjects.clear();
    this.notifySelectionChange();
  }
  
  /**
   * Get selected objects
   * @returns {Set} Set of selected object UUIDs
   */
  getSelection() {
    return new Set(this.selectedObjects);
  }
  
  /**
   * Register callback for selection changes
   * @param {Function} callback - Callback function(selectedObjects)
   */
  onSelectionChange(callback) {
    this.selectionCallbacks.push(callback);
  }
  
  /**
   * Notify all listeners of selection change
   */
  notifySelectionChange() {
    const selected = this.getSelection();
    this.selectionCallbacks.forEach(cb => cb(selected));
  }
  
  /**
   * Set parent-child relationship
   * @param {string} childUUID - UUID of child object
   * @param {string} parentUUID - UUID of parent object (null for root)
   */
  setParent(childUUID, parentUUID) {
    // Remove from old parent if exists
    const oldParent = this.parentChildMap.get(childUUID);
    if (oldParent) {
      const siblings = this.childrenMap.get(oldParent);
      if (siblings) {
        siblings.delete(childUUID);
      }
    }
    
    // Set new parent
    if (parentUUID) {
      this.parentChildMap.set(childUUID, parentUUID);
      
      if (!this.childrenMap.has(parentUUID)) {
        this.childrenMap.set(parentUUID, new Set());
      }
      this.childrenMap.get(parentUUID).add(childUUID);
    } else {
      this.parentChildMap.delete(childUUID);
    }
    
    // TODO: Update object transforms based on parent
    // TODO: Send LinkObject or DelinkObject message
  }
  
  /**
   * Get parent of an object
   * @param {string} objectUUID - UUID of the object
   * @returns {string|null} UUID of parent or null if root
   */
  getParent(objectUUID) {
    return this.parentChildMap.get(objectUUID) || null;
  }
  
  /**
   * Get children of an object
   * @param {string} objectUUID - UUID of the object
   * @returns {Set} Set of child object UUIDs
   */
  getChildren(objectUUID) {
    return new Set(this.childrenMap.get(objectUUID) || []);
  }
  
  /**
   * Get all descendants of an object (recursive)
   * @param {string} objectUUID - UUID of the object
   * @returns {Set} Set of all descendant object UUIDs
   */
  getAllDescendants(objectUUID) {
    const descendants = new Set();
    const children = this.getChildren(objectUUID);
    
    children.forEach(child => {
      descendants.add(child);
      const childDescendants = this.getAllDescendants(child);
      childDescendants.forEach(d => descendants.add(d));
    });
    
    return descendants;
  }
  
  /**
   * Check if object is root (has no parent)
   * @param {string} objectUUID - UUID of the object
   * @returns {boolean} True if object is root
   */
  isRoot(objectUUID) {
    return !this.parentChildMap.has(objectUUID);
  }
  
  /**
   * Get root object for a given object
   * @param {string} objectUUID - UUID of the object
   * @returns {string} UUID of root object
   */
  getRootObject(objectUUID) {
    let current = objectUUID;
    let parent = this.getParent(current);
    
    while (parent) {
      current = parent;
      parent = this.getParent(current);
    }
    
    return current;
  }
  
  /**
   * Get link set (root and all children)
   * @param {string} objectUUID - UUID of any object in the link set
   * @returns {Set} Set of all object UUIDs in the link set
   */
  getLinkSet(objectUUID) {
    const root = this.getRootObject(objectUUID);
    const linkSet = new Set([root]);
    const descendants = this.getAllDescendants(root);
    descendants.forEach(d => linkSet.add(d));
    return linkSet;
  }
  
  /**
   * Get link number (position in link set, 1-indexed)
   * @param {string} objectUUID - UUID of the object
   * @returns {number} Link number (1 = root, 2+ = children)
   */
  getLinkNumber(objectUUID) {
    const root = this.getRootObject(objectUUID);
    if (root === objectUUID) return 1;
    
    const linkSet = Array.from(this.getLinkSet(root));
    return linkSet.indexOf(objectUUID) + 1;
  }
  
  /**
   * Set object property
   * @param {string} objectUUID - UUID of the object
   * @param {Object} properties - Object properties
   */
  setObjectProperties(objectUUID, properties) {
    this.objectProperties.set(objectUUID, {
      name: properties.name || '',
      description: properties.description || '',
      position: properties.position || [0, 0, 0],
      rotation: properties.rotation || [0, 0, 0, 1],
      scale: properties.scale || [0.5, 0.5, 0.5],
      velocity: properties.velocity || [0, 0, 0],
      angularVelocity: properties.angularVelocity || [0, 0, 0],
      clickAction: properties.clickAction || 0,
      flags: properties.flags || 0,
      timestamp: Date.now()
    });
    
    this.cacheObject(objectUUID, properties);
  }
  
  /**
   * Get object properties
   * @param {string} objectUUID - UUID of the object
   * @returns {Object|null} Object properties or null
   */
  getObjectProperties(objectUUID) {
    return this.objectProperties.get(objectUUID) || null;
  }
  
  /**
   * Cache object data
   * @param {string} objectUUID - UUID of the object
   * @param {Object} data - Object data to cache
   */
  cacheObject(objectUUID, data) {
    this.objectCache.set(objectUUID, {
      data,
      timestamp: Date.now(),
      expires: Date.now() + this.cacheTimeout
    });
  }
  
  /**
   * Get cached object
   * @param {string} objectUUID - UUID of the object
   * @returns {Object|null} Cached object data or null
   */
  getCachedObject(objectUUID) {
    const cached = this.objectCache.get(objectUUID);
    
    if (!cached) return null;
    
    if (Date.now() > cached.expires) {
      this.objectCache.delete(objectUUID);
      return null;
    }
    
    return cached.data;
  }
  
  /**
   * Clear cache for object
   * @param {string} objectUUID - UUID of the object
   */
  clearCache(objectUUID) {
    this.objectCache.delete(objectUUID);
  }
  
  /**
   * Clear all expired cache entries
   */
  clearExpiredCache() {
    const now = Date.now();
    for (const [uuid, cached] of this.objectCache) {
      if (now > cached.expires) {
        this.objectCache.delete(uuid);
      }
    }
  }
  
  /**
   * Set texture for specific face
   * @param {string} objectUUID - UUID of the object
   * @param {number} face - Face index (-1 for all faces)
   * @param {Object} texture - Texture properties
   */
  setFaceTexture(objectUUID, face, texture) {
    const params = this.primParams.get(objectUUID);
    if (!params) return;
    
    if (!params.textures) {
      params.textures = [];
    }
    
    const textureEntry = {
      textureId: texture.textureId || '00000000-0000-0000-0000-000000000000',
      color: texture.color || [1, 1, 1, 1],
      repeatU: texture.repeatU || 1.0,
      repeatV: texture.repeatV || 1.0,
      offsetU: texture.offsetU || 0,
      offsetV: texture.offsetV || 0,
      rotation: texture.rotation || 0,
      glow: texture.glow || 0,
      material: texture.material || 0,
      mediaFlags: texture.mediaFlags || 0
    };
    
    if (face === -1) {
      // Set for all faces
      for (let i = 0; i < 8; i++) {
        params.textures[i] = { ...textureEntry };
      }
    } else {
      params.textures[face] = textureEntry;
    }
  }
  
  /**
   * Get texture for specific face
   * @param {string} objectUUID - UUID of the object
   * @param {number} face - Face index
   * @returns {Object|null} Texture properties or null
   */
  getFaceTexture(objectUUID, face) {
    const params = this.primParams.get(objectUUID);
    if (!params || !params.textures) return null;
    return params.textures[face] || null;
  }
  
  /**
   * Set material type
   * @param {string} objectUUID - UUID of the object
   * @param {number} material - Material type (0-6)
   */
  setMaterial(objectUUID, material) {
    const params = this.primParams.get(objectUUID);
    if (params) {
      params.material = material;
    }
  }
  
  /**
   * Get material name
   * @param {number} materialType - Material type
   * @returns {string} Material name
   */
  getMaterialName(materialType) {
    const names = Object.keys(this.MATERIALS);
    for (const name of names) {
      if (this.MATERIALS[name] === materialType) {
        return name;
      }
    }
    return 'UNKNOWN';
  }
  
  /**
   * Calculate bounding box for object
   * @param {string} objectUUID - UUID of the object
   * @returns {Object} Bounding box {min, max}
   */
  getBoundingBox(objectUUID) {
    const props = this.getObjectProperties(objectUUID);
    const params = this.getPrimParams(objectUUID);
    
    if (!props || !params) {
      return { min: [0, 0, 0], max: [0, 0, 0] };
    }
    
    const scale = props.scale;
    const halfScale = scale.map(s => s / 2);
    
    return {
      min: props.position.map((p, i) => p - halfScale[i]),
      max: props.position.map((p, i) => p + halfScale[i])
    };
  }
  
  /**
   * Check if point is inside object bounding box
   * @param {string} objectUUID - UUID of the object
   * @param {Array} point - Point [x, y, z]
   * @returns {boolean} True if point is inside
   */
  containsPoint(objectUUID, point) {
    const bbox = this.getBoundingBox(objectUUID);
    
    return point[0] >= bbox.min[0] && point[0] <= bbox.max[0] &&
           point[1] >= bbox.min[1] && point[1] <= bbox.max[1] &&
           point[2] >= bbox.min[2] && point[2] <= bbox.max[2];
  }
  
  /**
   * Get all objects in region
   * @returns {Array} Array of object UUIDs
   */
  getAllObjects() {
    return Array.from(this.objects.keys());
  }
  
  /**
   * Get object count
   * @returns {number} Total number of objects
   */
  getObjectCount() {
    return this.objects.size;
  }
  
  /**
   * Remove object and clean up
   * @param {string} objectUUID - UUID of the object
   */
  removeObject(objectUUID) {
    this.objects.delete(objectUUID);
    this.primParams.delete(objectUUID);
    this.permissions.delete(objectUUID);
    this.objectProperties.delete(objectUUID);
    this.objectCache.delete(objectUUID);
    this.selectedObjects.delete(objectUUID);
    
    // Clean up parent-child relationships
    const children = this.getChildren(objectUUID);
    children.forEach(child => {
      this.parentChildMap.delete(child);
    });
    this.childrenMap.delete(objectUUID);
    
    const parent = this.getParent(objectUUID);
    if (parent) {
      const siblings = this.childrenMap.get(parent);
      if (siblings) {
        siblings.delete(objectUUID);
      }
      this.parentChildMap.delete(objectUUID);
    }
  }
  
  /**
   * Clear all objects
   */
  clearAll() {
    this.objects.clear();
    this.primParams.clear();
    this.permissions.clear();
    this.objectProperties.clear();
    this.objectCache.clear();
    this.selectedObjects.clear();
    this.parentChildMap.clear();
    this.childrenMap.clear();
  }
}

// Export singleton instance
const objectManagerExtended = new ObjectManagerExtended();
export default objectManagerExtended;
export { ObjectManagerExtended };
