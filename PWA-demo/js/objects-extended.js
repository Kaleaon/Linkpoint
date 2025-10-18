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
    
    // Feature 18: Object permissions
    this.permissions = new Map();
    
    // Feature 19: Object selection
    this.selectedObjects = new Set();
    this.selectionCallbacks = [];
    
    // Feature 20: Parent-child relationships
    this.parentChildMap = new Map();
    this.childrenMap = new Map();
    
    // TODO: Implement object caching
    // TODO: Implement permission checks
    // TODO: Connect to protocol layer
    // TODO: Integrate with 3D rendering
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
}

// Export singleton instance
const objectManagerExtended = new ObjectManagerExtended();
export default objectManagerExtended;
export { ObjectManagerExtended };
