/**
 * Linkpoint PWA - SL Object Manager
 * Based on Linkpoint SLObjectInfo.kt and object handling
 */

class SLObjectManager extends Utils.EventEmitter {
  constructor(scene3d, meshLoader) {
    super();
    this.scene = scene3d;
    this.meshLoader = meshLoader;
    this.objects = new Map();
    this.avatars = new Map();
    this.pendingUpdates = [];
  }

  /**
   * Handle ObjectUpdate message
   */
  handleObjectUpdate(message) {
    // Based on ObjectUpdate.kt structure
    const regionHandle = message.regionHandle;
    const timeDilation = message.timeDilation;

    for (const objectData of message.objectList) {
      this.processObjectData(objectData);
    }

    this.emit('objects_updated', { count: message.objectList.length });
  }

  /**
   * Process individual object data
   */
  processObjectData(data) {
    const objectId = data.fullId || data.id.toString();
    
    let object = this.objects.get(objectId);
    
    if (!object) {
      // New object
      object = this.createObject(objectId, data);
      this.objects.set(objectId, object);
    } else {
      // Update existing object
      this.updateObject(object, data);
    }
  }

  /**
   * Create new object
   */
  createObject(id, data) {
    const object = {
      id,
      localId: data.id,
      fullId: data.fullId,
      
      // Position and rotation from objectData
      position: this.extractPosition(data.objectData),
      rotation: this.extractRotation(data.objectData),
      scale: data.scale ? [data.scale.x, data.scale.y, data.scale.z] : [1, 1, 1],
      
      // Object properties
      pCode: data.pCode, // Primitive code (9 = prim, 47 = avatar)
      material: data.material,
      clickAction: data.clickAction,
      flags: data.updateFlags,
      
      // Parent/child
      parentId: data.parentId,
      children: [],
      
      // Appearance
      textureEntry: data.textureEntry,
      color: [1, 1, 1, 1],
      
      // Names
      name: data.name || 'Object',
      description: data.description || '',
      
      // State
      visible: true,
      selected: false,
      
      // 3D mesh reference
      meshName: null
    };

    // Determine mesh to use based on pCode
    object.meshName = this.selectMeshForPCode(object.pCode, data);

    // Add to 3D scene
    if (this.scene && object.meshName) {
      this.scene.addObject(id, {
        mesh: object.meshName,
        position: object.position,
        rotation: object.rotation,
        scale: object.scale,
        color: object.color,
        material: 'basic'
      });
    }

    this.emit('object_created', object);
    return object;
  }

  /**
   * Update existing object
   */
  updateObject(object, data) {
    // Update position if changed
    if (data.objectData) {
      const newPos = this.extractPosition(data.objectData);
      const newRot = this.extractRotation(data.objectData);
      
      if (newPos) object.position = newPos;
      if (newRot) object.rotation = newRot;
    }

    // Update scale
    if (data.scale) {
      object.scale = [data.scale.x, data.scale.y, data.scale.z];
    }

    // Update 3D scene object
    if (this.scene) {
      this.scene.updateObject(object.id, {
        position: object.position,
        rotation: object.rotation,
        scale: object.scale
      });
    }

    this.emit('object_updated', object);
  }

  /**
   * Extract position from object data
   * Object data contains packed position, velocity, rotation, angular velocity
   */
  extractPosition(objectData) {
    if (!objectData || objectData.length < 12) return null;

    const view = new DataView(objectData.buffer || new ArrayBuffer(objectData.length));
    
    try {
      // Position is first 12 bytes (3 floats)
      const x = view.getFloat32(0, false); // Big endian
      const y = view.getFloat32(4, false);
      const z = view.getFloat32(8, false);
      
      return [x, y, z];
    } catch (error) {
      return null;
    }
  }

  /**
   * Extract rotation from object data
   */
  extractRotation(objectData) {
    if (!objectData || objectData.length < 60) return null;

    const view = new DataView(objectData.buffer || new ArrayBuffer(objectData.length));
    
    try {
      // Rotation is at offset 36 (after position + velocity + acceleration)
      const x = view.getFloat32(36, false);
      const y = view.getFloat32(40, false);
      const z = view.getFloat32(44, false);
      const w = view.getFloat32(48, false);
      
      // Convert quaternion to Euler angles
      return this.quaternionToEuler(x, y, z, w);
    } catch (error) {
      return [0, 0, 0];
    }
  }

  /**
   * Convert quaternion to Euler angles
   */
  quaternionToEuler(x, y, z, w) {
    // Roll (x-axis)
    const sinr_cosp = 2 * (w * x + y * z);
    const cosr_cosp = 1 - 2 * (x * x + y * y);
    const roll = Math.atan2(sinr_cosp, cosr_cosp);

    // Pitch (y-axis)
    const sinp = 2 * (w * y - z * x);
    const pitch = Math.abs(sinp) >= 1 
      ? Math.sign(sinp) * Math.PI / 2 
      : Math.asin(sinp);

    // Yaw (z-axis)
    const siny_cosp = 2 * (w * z + x * y);
    const cosy_cosp = 1 - 2 * (y * y + z * z);
    const yaw = Math.atan2(siny_cosp, cosy_cosp);

    return [pitch, yaw, roll];
  }

  /**
   * Select mesh based on pCode
   * pCode meanings:
   * 9 = Primitive (prim)
   * 47 = Avatar
   * 49 = Grass
   * 50 = Tree
   * etc.
   */
  selectMeshForPCode(pCode, data) {
    switch (pCode) {
      case 9: // Primitive
        return this.selectPrimMesh(data);
      case 47: // Avatar
        return 'sphere'; // Use sphere for avatars temporarily
      case 49: // Grass
        return 'plane';
      case 50: // Tree
        return 'cylinder';
      default:
        return 'cube'; // Default
    }
  }

  /**
   * Select primitive mesh type
   */
  selectPrimMesh(data) {
    // Based on PathCurve and ProfileCurve
    const pathCurve = data.pathCurve || 0;
    const profileCurve = data.profileCurve || 0;

    // Simplified mapping
    if ((profileCurve & 0x0F) === 0) return 'cube'; // Box
    if ((profileCurve & 0x0F) === 1) return 'cylinder'; // Cylinder
    if ((profileCurve & 0x0F) === 2) return 'sphere'; // Sphere/half-sphere
    if ((profileCurve & 0x0F) === 3) return 'cylinder'; // Torus
    
    return 'cube'; // Default to cube
  }

  /**
   * Remove object
   */
  removeObject(objectId) {
    const object = this.objects.get(objectId);
    if (!object) return;

    // Remove from 3D scene
    if (this.scene) {
      this.scene.removeObject(objectId);
    }

    this.objects.delete(objectId);
    this.emit('object_removed', objectId);
  }

  /**
   * Handle KillObject message
   */
  handleKillObject(message) {
    if (message.objectIds) {
      for (const id of message.objectIds) {
        this.removeObject(id);
      }
    }
  }

  /**
   * Get object by ID
   */
  getObject(id) {
    return this.objects.get(id);
  }

  /**
   * Get all objects
   */
  getAllObjects() {
    return Array.from(this.objects.values());
  }

  /**
   * Get object count
   */
  getObjectCount() {
    return this.objects.size;
  }

  /**
   * Clear all objects
   */
  clearAllObjects() {
    this.objects.forEach((obj, id) => {
      if (this.scene) {
        this.scene.removeObject(id);
      }
    });
    this.objects.clear();
    this.emit('all_objects_cleared');
  }

  /**
   * Get statistics
   */
  getStats() {
    return {
      objects: this.objects.size,
      avatars: this.avatars.size,
      pendingUpdates: this.pendingUpdates.length,
      meshesLoaded: this.meshLoader.getCacheStats().meshCount
    };
  }
}

// Make available globally
window.SLObjectManager = SLObjectManager;
