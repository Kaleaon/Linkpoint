/**
 * Linkpoint PWA - 3D Scene Manager
 */

class Scene3D extends Utils.EventEmitter {
  constructor(graphics, camera) {
    super();
    this.graphics = graphics;
    this.camera = camera;
    
    // Scene objects
    this.objects = new Map();
    this.lights = [];
    this.skybox = null;
    
    // Transform helpers
    this.modelMatrix = new Float32Array(16);
    this.normalMatrix = new Float32Array(9);
    
    // Grid
    this.showGrid = true;
    this.gridSize = 256;
    this.gridDivisions = 16;
  }

  /**
   * Initialize scene
   */
  async init() {
    // Create default primitives
    this.createDefaultPrimitives();
    
    // Create grid
    if (this.showGrid) {
      this.createGrid();
    }
    
    // Add default light
    this.addLight({
      type: 'directional',
      position: [100, 100, 200],
      color: [1, 1, 1],
      intensity: 1.0
    });
    
    this.emit('initialized');
  }

  /**
   * Create default primitive meshes
   */
  createDefaultPrimitives() {
    // Cube
    const cube = Primitives3D.createCube(1);
    this.graphics.createMesh('cube', cube.vertices, cube.indices, cube.normals, cube.texCoords);
    
    // Sphere
    const sphere = Primitives3D.createSphere(1, 32, 16);
    this.graphics.createMesh('sphere', sphere.vertices, sphere.indices, sphere.normals, sphere.texCoords);
    
    // Plane
    const plane = Primitives3D.createPlane(10, 10, 10, 10);
    this.graphics.createMesh('plane', plane.vertices, plane.indices, plane.normals, plane.texCoords);
    
    // Cylinder
    const cylinder = Primitives3D.createCylinder(1, 1, 2, 32);
    this.graphics.createMesh('cylinder', cylinder.vertices, cylinder.indices, cylinder.normals, cylinder.texCoords);
    
    // Cone
    const cone = Primitives3D.createCone(1, 2, 32);
    this.graphics.createMesh('cone', cone.vertices, cone.indices, cone.normals, cone.texCoords);
  }

  /**
   * Create grid
   */
  createGrid() {
    const grid = Primitives3D.createGrid(this.gridSize, this.gridDivisions);
    this.graphics.createMesh('grid', grid.vertices, grid.indices, grid.normals, grid.texCoords);
  }

  /**
   * Add object to scene
   */
  addObject(id, config) {
    const object = {
      id,
      mesh: config.mesh || 'cube',
      position: config.position || [0, 0, 0],
      rotation: config.rotation || [0, 0, 0],
      scale: config.scale || [1, 1, 1],
      color: config.color || [1, 1, 1, 1],
      material: config.material || 'basic',
      visible: config.visible !== false,
      castShadow: config.castShadow !== false,
      receiveShadow: config.receiveShadow !== false
    };
    
    this.objects.set(id, object);
    this.emit('object_added', object);
    return object;
  }

  /**
   * Remove object from scene
   */
  removeObject(id) {
    const object = this.objects.get(id);
    if (object) {
      this.objects.delete(id);
      this.emit('object_removed', object);
    }
  }

  /**
   * Update object
   */
  updateObject(id, updates) {
    const object = this.objects.get(id);
    if (object) {
      Object.assign(object, updates);
      this.emit('object_updated', object);
    }
  }

  /**
   * Get object
   */
  getObject(id) {
    return this.objects.get(id);
  }

  /**
   * Add light
   */
  addLight(config) {
    const light = {
      id: config.id || Utils.generateUUID(),
      type: config.type || 'point',
      position: config.position || [0, 0, 0],
      color: config.color || [1, 1, 1],
      intensity: config.intensity || 1.0,
      range: config.range || 100
    };
    
    this.lights.push(light);
    this.emit('light_added', light);
    return light;
  }

  /**
   * Render scene
   */
  render() {
    const gl = this.graphics.gl;
    
    // Clear
    this.graphics.clear();
    
    // Get matrices
    const viewMatrix = this.camera.getViewMatrix();
    const projectionMatrix = this.camera.getProjectionMatrix();
    
    // Render grid first
    if (this.showGrid) {
      this.renderGrid(viewMatrix, projectionMatrix);
    }
    
    // Render all objects
    this.objects.forEach(object => {
      if (object.visible) {
        this.renderObject(object, viewMatrix, projectionMatrix);
      }
    });
  }

  /**
   * Render grid
   */
  renderGrid(viewMatrix, projectionMatrix) {
    const modelMatrix = this.mat4Identity();
    const normalMatrix = this.mat3FromMat4(modelMatrix);
    
    const light = this.lights[0] || { position: [100, 100, 200], color: [1, 1, 1] };
    
    this.graphics.drawMesh('grid', 'basic', {
      uModelMatrix: modelMatrix,
      uViewMatrix: viewMatrix,
      uProjectionMatrix: projectionMatrix,
      uNormalMatrix: normalMatrix,
      uLightPos: new Float32Array(light.position),
      uLightColor: new Float32Array(light.color),
      uAmbientColor: new Float32Array([0.3, 0.3, 0.3]),
      uColor: new Float32Array([0.5, 0.5, 0.5, 0.3]),
      uUseTexture: false
    });
  }

  /**
   * Render object
   */
  renderObject(object, viewMatrix, projectionMatrix) {
    // Calculate model matrix
    const modelMatrix = this.calculateModelMatrix(
      object.position,
      object.rotation,
      object.scale
    );
    
    const normalMatrix = this.mat3FromMat4(modelMatrix);
    
    // Get primary light
    const light = this.lights[0] || { position: [100, 100, 200], color: [1, 1, 1] };
    
    // Draw
    this.graphics.drawMesh(object.mesh, object.material, {
      uModelMatrix: modelMatrix,
      uViewMatrix: viewMatrix,
      uProjectionMatrix: projectionMatrix,
      uNormalMatrix: normalMatrix,
      uLightPos: new Float32Array(light.position),
      uLightColor: new Float32Array(light.color),
      uAmbientColor: new Float32Array([0.2, 0.2, 0.2]),
      uColor: new Float32Array(object.color),
      uUseTexture: false
    });
  }

  /**
   * Calculate model matrix from transform
   */
  calculateModelMatrix(position, rotation, scale) {
    const matrix = this.mat4Identity();
    
    // Translate
    this.mat4Translate(matrix, position);
    
    // Rotate
    if (rotation[0] !== 0) this.mat4RotateX(matrix, rotation[0]);
    if (rotation[1] !== 0) this.mat4RotateY(matrix, rotation[1]);
    if (rotation[2] !== 0) this.mat4RotateZ(matrix, rotation[2]);
    
    // Scale
    this.mat4Scale(matrix, scale);
    
    return matrix;
  }

  /**
   * Matrix operations
   */
  mat4Identity() {
    return new Float32Array([
      1, 0, 0, 0,
      0, 1, 0, 0,
      0, 0, 1, 0,
      0, 0, 0, 1
    ]);
  }

  mat4Translate(m, v) {
    m[12] += v[0];
    m[13] += v[1];
    m[14] += v[2];
  }

  mat4RotateX(m, angle) {
    const c = Math.cos(angle);
    const s = Math.sin(angle);
    const m1 = m[1], m2 = m[2];
    const m5 = m[5], m6 = m[6];
    const m9 = m[9], m10 = m[10];
    const m13 = m[13], m14 = m[14];
    
    m[1] = m1 * c + m2 * s;
    m[2] = m2 * c - m1 * s;
    m[5] = m5 * c + m6 * s;
    m[6] = m6 * c - m5 * s;
    m[9] = m9 * c + m10 * s;
    m[10] = m10 * c - m9 * s;
    m[13] = m13 * c + m14 * s;
    m[14] = m14 * c - m13 * s;
  }

  mat4RotateY(m, angle) {
    const c = Math.cos(angle);
    const s = Math.sin(angle);
    const m0 = m[0], m2 = m[2];
    const m4 = m[4], m6 = m[6];
    const m8 = m[8], m10 = m[10];
    const m12 = m[12], m14 = m[14];
    
    m[0] = m0 * c - m2 * s;
    m[2] = m0 * s + m2 * c;
    m[4] = m4 * c - m6 * s;
    m[6] = m4 * s + m6 * c;
    m[8] = m8 * c - m10 * s;
    m[10] = m8 * s + m10 * c;
    m[12] = m12 * c - m14 * s;
    m[14] = m12 * s + m14 * c;
  }

  mat4RotateZ(m, angle) {
    const c = Math.cos(angle);
    const s = Math.sin(angle);
    const m0 = m[0], m1 = m[1];
    const m4 = m[4], m5 = m[5];
    const m8 = m[8], m9 = m[9];
    const m12 = m[12], m13 = m[13];
    
    m[0] = m0 * c + m1 * s;
    m[1] = m1 * c - m0 * s;
    m[4] = m4 * c + m5 * s;
    m[5] = m5 * c - m4 * s;
    m[8] = m8 * c + m9 * s;
    m[9] = m9 * c - m8 * s;
    m[12] = m12 * c + m13 * s;
    m[13] = m13 * c - m12 * s;
  }

  mat4Scale(m, v) {
    m[0] *= v[0];
    m[1] *= v[0];
    m[2] *= v[0];
    m[3] *= v[0];
    m[4] *= v[1];
    m[5] *= v[1];
    m[6] *= v[1];
    m[7] *= v[1];
    m[8] *= v[2];
    m[9] *= v[2];
    m[10] *= v[2];
    m[11] *= v[2];
  }

  mat3FromMat4(m4) {
    return new Float32Array([
      m4[0], m4[1], m4[2],
      m4[4], m4[5], m4[6],
      m4[8], m4[9], m4[10]
    ]);
  }

  /**
   * Ray casting for object picking
   */
  raycast(ray, objects = null) {
    const testObjects = objects || Array.from(this.objects.values());
    const hits = [];

    testObjects.forEach(object => {
      if (!object.visible) return;

      // Simple bounding sphere test
      const distance = this.vec3Distance(ray.origin, object.position);
      const maxScale = Math.max(...object.scale);
      
      if (distance < maxScale * 2) {
        hits.push({
          object,
          distance
        });
      }
    });

    return hits.sort((a, b) => a.distance - b.distance);
  }

  vec3Distance(a, b) {
    const dx = b[0] - a[0];
    const dy = b[1] - a[1];
    const dz = b[2] - a[2];
    return Math.sqrt(dx * dx + dy * dy + dz * dz);
  }

  /**
   * Clear scene
   */
  clear() {
    this.objects.clear();
    this.lights = [];
  }

  /**
   * Get statistics
   */
  getStats() {
    return {
      objects: this.objects.size,
      lights: this.lights.length,
      visibleObjects: Array.from(this.objects.values()).filter(o => o.visible).length
    };
  }
}

// Make available globally
window.Scene3D = Scene3D;
