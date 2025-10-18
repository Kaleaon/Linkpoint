/**
 * Linkpoint PWA - Object Manager Extensions (Features 17-20)
 * 
 * Phase 2: Core Protocol Extensions - Priority 1
 * Roadmap: PWA-demo/ANDROID_PORT_ROADMAP.md (Lines 42-47)
 * Android Source: app/src/main/java/com/lumiyaviewer/lumiya/slproto/objects/
 * 
 * Extends object management with prim parameters, permissions, and relationships.
 * 
 * Features:
 * - Feature 17: Prim parameters (shape, material, texture)
 * - Feature 18: Object permissions
 * - Feature 19: Object selection
 * - Feature 20: Parent-child relationships
 * 
 * @module phase2/objects-extended
 */

/**
 * Extended Object Manager
 * Manages complex object properties and relationships
 */
class ObjectManagerExtended {
  constructor() {
    this.objects = new Map();
    this.selectedObjects = new Set();
    this.parentChildMap = new Map();
  }

  /**
   * Feature 17: Prim parameters (shape, material, texture)
   * Set prim parameters for an object
   * 
   * @param {string} objectId - Object UUID
   * @param {Object} primParams - Prim parameter data
   * 
   * TODO: Implement full prim parameter validation (see Android PrimData)
   * TODO: Add support for sculpt maps and flexible prims
   */
  setPrimParams(objectId, primParams) {
    if (!objectId || typeof objectId !== 'string') {
      throw new Error('Valid object ID required');
    }
    if (!primParams || typeof primParams !== 'object') {
      throw new Error('Valid prim parameters required');
    }
    
    const obj = this.objects.get(objectId) || {};
    obj.primParams = {
      shape: primParams.shape || 'box',
      material: primParams.material || 'wood',
      texture: primParams.texture || null,
      color: primParams.color || [1, 1, 1, 1],
      scale: primParams.scale || [1, 1, 1],
      ...primParams
    };
    
    this.objects.set(objectId, obj);
    console.log(`[Objects] Set prim params for: ${objectId}`);
  }

  /**
   * Get prim parameters
   * @param {string} objectId - Object UUID
   * @returns {Object|null}
   */
  getPrimParams(objectId) {
    return this.objects.get(objectId)?.primParams || null;
  }

  /**
   * Feature 18: Object permissions
   * Set object permissions
   * 
   * @param {string} objectId - Object UUID
   * @param {Object} permissions - Permission flags
   * 
   * TODO: Implement permission bit flags (copy/modify/transfer)
   * TODO: Add permission inheritance for linksets
   */
  setPermissions(objectId, permissions) {
    if (!objectId || typeof objectId !== 'string') {
      throw new Error('Valid object ID required');
    }
    if (!permissions || typeof permissions !== 'object') {
      throw new Error('Valid permissions object required');
    }
    
    const obj = this.objects.get(objectId) || {};
    obj.permissions = {
      baseMask: permissions.baseMask || 0,
      ownerMask: permissions.ownerMask || 0,
      groupMask: permissions.groupMask || 0,
      everyoneMask: permissions.everyoneMask || 0,
      nextOwnerMask: permissions.nextOwnerMask || 0,
      ...permissions
    };
    
    this.objects.set(objectId, obj);
    console.log(`[Objects] Set permissions for: ${objectId}`);
  }

  /**
   * Check if object has specific permission
   * @param {string} objectId - Object UUID
   * @param {string} permType - Permission type
   * @returns {boolean}
   */
  hasPermission(objectId, permType) {
    const perms = this.objects.get(objectId)?.permissions;
    return perms ? Boolean(perms[permType]) : false;
  }

  /**
   * Feature 19: Object selection
   * Select or deselect an object
   * 
   * @param {string} objectId - Object UUID
   * @param {boolean} selected - Whether to select or deselect
   * 
   * TODO: Add multi-select support with shift/ctrl
   * TODO: Implement selection highlighting
   */
  selectObject(objectId, selected = true) {
    if (!objectId || typeof objectId !== 'string') {
      throw new Error('Valid object ID required');
    }
    
    if (selected) {
      this.selectedObjects.add(objectId);
      console.log(`[Objects] Selected: ${objectId}`);
    } else {
      this.selectedObjects.delete(objectId);
      console.log(`[Objects] Deselected: ${objectId}`);
    }
  }

  /**
   * Get all selected objects
   * @returns {Set<string>}
   */
  getSelectedObjects() {
    return new Set(this.selectedObjects);
  }

  /**
   * Clear all selections
   */
  clearSelection() {
    const count = this.selectedObjects.size;
    this.selectedObjects.clear();
    console.log(`[Objects] Cleared ${count} selections`);
  }

  /**
   * Feature 20: Parent-child relationships
   * Link object as child to parent
   * 
   * @param {string} parentId - Parent object UUID
   * @param {string} childId - Child object UUID
   * 
   * TODO: Implement linkset management (see Android ObjectUpdate)
   * TODO: Add support for link/unlink operations
   */
  linkChild(parentId, childId) {
    if (!parentId || !childId) {
      throw new Error('Valid parent and child IDs required');
    }
    
    if (!this.parentChildMap.has(parentId)) {
      this.parentChildMap.set(parentId, new Set());
    }
    
    this.parentChildMap.get(parentId).add(childId);
    
    // Store parent reference in child object
    const childObj = this.objects.get(childId) || {};
    childObj.parentId = parentId;
    this.objects.set(childId, childObj);
    
    console.log(`[Objects] Linked ${childId} to parent ${parentId}`);
  }

  /**
   * Unlink child from parent
   * @param {string} childId - Child object UUID
   */
  unlinkChild(childId) {
    const obj = this.objects.get(childId);
    if (obj?.parentId) {
      const parent = this.parentChildMap.get(obj.parentId);
      if (parent) {
        parent.delete(childId);
      }
      delete obj.parentId;
      console.log(`[Objects] Unlinked ${childId}`);
    }
  }

  /**
   * Get children of a parent object
   * @param {string} parentId - Parent object UUID
   * @returns {Set<string>}
   */
  getChildren(parentId) {
    return this.parentChildMap.get(parentId) || new Set();
  }

  /**
   * Get parent of a child object
   * @param {string} childId - Child object UUID
   * @returns {string|null}
   */
  getParent(childId) {
    return this.objects.get(childId)?.parentId || null;
  }
}

// Export for use in other modules
if (typeof module !== 'undefined' && module.exports) {
  module.exports = { ObjectManagerExtended };
}

/**
 * Example Usage:
 * 
 * const objMgr = new ObjectManagerExtended();
 * 
 * // Set prim parameters
 * objMgr.setPrimParams('obj-123', {
 *   shape: 'sphere',
 *   material: 'metal',
 *   scale: [2, 2, 2]
 * });
 * 
 * // Set permissions
 * objMgr.setPermissions('obj-123', {
 *   ownerMask: 0x7FFFFFFF,
 *   everyoneMask: 0x00080000
 * });
 * 
 * // Select object
 * objMgr.selectObject('obj-123');
 * 
 * // Link objects
 * objMgr.linkChild('parent-123', 'child-456');
 */
