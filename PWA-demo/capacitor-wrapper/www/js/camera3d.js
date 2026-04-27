/**
 * Linkpoint PWA - 3D Camera System
 */

class Camera3D extends Utils.EventEmitter {
  constructor() {
    super();
    
    // Position and rotation
    this.position = [128, 128, 25];
    this.rotation = [0, 0, 0]; // pitch, yaw, roll
    this.target = [128, 128, 0];
    
    // Projection
    this.fov = 60; // degrees
    this.aspect = 16 / 9;
    this.near = 0.1;
    this.far = 1000;
    
    // Movement
    this.moveSpeed = 10.0;
    this.rotateSpeed = 0.002;
    this.zoomSpeed = 1.0;
    
    // Matrices
    this.viewMatrix = this.createMatrix4();
    this.projectionMatrix = this.createMatrix4();
    this.viewProjectionMatrix = this.createMatrix4();
    
    // Camera mode
    this.mode = 'orbit'; // 'orbit', 'first-person', 'third-person'
    this.orbitDistance = 10;
    this.orbitTarget = [128, 128, 25];
    
    this.updateMatrices();
  }

  /**
   * Set position
   */
  setPosition(x, y, z) {
    this.position = [x, y, z];
    this.updateMatrices();
    this.emit('position_changed', this.position);
  }

  /**
   * Set rotation
   */
  setRotation(pitch, yaw, roll = 0) {
    this.rotation = [pitch, yaw, roll];
    this.updateMatrices();
    this.emit('rotation_changed', this.rotation);
  }

  /**
   * Look at target
   */
  lookAt(target) {
    this.target = target;
    this.updateMatrices();
  }

  /**
   * Move camera
   */
  move(forward, right, up) {
    const [pitch, yaw] = this.rotation;
    
    // Calculate movement vectors
    const forwardVec = [
      Math.sin(yaw) * Math.cos(pitch),
      Math.cos(yaw) * Math.cos(pitch),
      Math.sin(pitch)
    ];
    
    const rightVec = [
      Math.sin(yaw - Math.PI / 2),
      Math.cos(yaw - Math.PI / 2),
      0
    ];
    
    const upVec = [0, 0, 1];
    
    this.position[0] += forwardVec[0] * forward + rightVec[0] * right + upVec[0] * up;
    this.position[1] += forwardVec[1] * forward + rightVec[1] * right + upVec[1] * up;
    this.position[2] += forwardVec[2] * forward + rightVec[2] * right + upVec[2] * up;
    
    this.updateMatrices();
    this.emit('moved', this.position);
  }

  /**
   * Rotate camera
   */
  rotate(deltaPitch, deltaYaw, deltaRoll = 0) {
    this.rotation[0] = Utils.clamp(this.rotation[0] + deltaPitch, -Math.PI / 2 + 0.01, Math.PI / 2 - 0.01);
    this.rotation[1] += deltaYaw;
    this.rotation[2] += deltaRoll;
    
    // Normalize yaw to 0-2π
    this.rotation[1] = this.rotation[1] % (Math.PI * 2);
    
    this.updateMatrices();
    this.emit('rotated', this.rotation);
  }

  /**
   * Zoom (change FOV or orbit distance)
   */
  zoom(delta) {
    if (this.mode === 'orbit') {
      this.orbitDistance = Utils.clamp(this.orbitDistance * (1 - delta * this.zoomSpeed), 1, 500);
      this.updateMatrices();
    } else {
      this.fov = Utils.clamp(this.fov - delta * 10, 10, 120);
      this.updateProjectionMatrix();
    }
    this.emit('zoomed', this.mode === 'orbit' ? this.orbitDistance : this.fov);
  }

  /**
   * Set camera mode
   */
  setMode(mode) {
    this.mode = mode;
    this.updateMatrices();
    this.emit('mode_changed', mode);
  }

  /**
   * Set orbit target
   */
  setOrbitTarget(x, y, z) {
    this.orbitTarget = [x, y, z];
    if (this.mode === 'orbit') {
      this.updateMatrices();
    }
  }

  /**
   * Update view matrix
   */
  updateViewMatrix() {
    if (this.mode === 'orbit') {
      // Orbit camera
      const [pitch, yaw] = this.rotation;
      
      this.position[0] = this.orbitTarget[0] + this.orbitDistance * Math.sin(yaw) * Math.cos(pitch);
      this.position[1] = this.orbitTarget[1] + this.orbitDistance * Math.cos(yaw) * Math.cos(pitch);
      this.position[2] = this.orbitTarget[2] + this.orbitDistance * Math.sin(pitch);
      
      this.viewMatrix = this.mat4LookAt(this.position, this.orbitTarget, [0, 0, 1]);
    } else {
      // First-person camera
      const [pitch, yaw] = this.rotation;
      
      this.target[0] = this.position[0] + Math.sin(yaw) * Math.cos(pitch);
      this.target[1] = this.position[1] + Math.cos(yaw) * Math.cos(pitch);
      this.target[2] = this.position[2] + Math.sin(pitch);
      
      this.viewMatrix = this.mat4LookAt(this.position, this.target, [0, 0, 1]);
    }
  }

  /**
   * Update projection matrix
   */
  updateProjectionMatrix() {
    this.projectionMatrix = this.mat4Perspective(
      this.fov * Math.PI / 180,
      this.aspect,
      this.near,
      this.far
    );
  }

  /**
   * Update all matrices
   */
  updateMatrices() {
    this.updateViewMatrix();
    this.updateProjectionMatrix();
    
    // Calculate view-projection matrix
    this.viewProjectionMatrix = this.mat4Multiply(this.projectionMatrix, this.viewMatrix);
  }

  /**
   * Set aspect ratio
   */
  setAspect(aspect) {
    this.aspect = aspect;
    this.updateProjectionMatrix();
  }

  /**
   * Get view matrix
   */
  getViewMatrix() {
    return this.viewMatrix;
  }

  /**
   * Get projection matrix
   */
  getProjectionMatrix() {
    return this.projectionMatrix;
  }

  /**
   * Get view-projection matrix
   */
  getViewProjectionMatrix() {
    return this.viewProjectionMatrix;
  }

  /**
   * Create identity matrix
   */
  createMatrix4() {
    return new Float32Array([
      1, 0, 0, 0,
      0, 1, 0, 0,
      0, 0, 1, 0,
      0, 0, 0, 1
    ]);
  }

  /**
   * Create perspective projection matrix
   */
  mat4Perspective(fov, aspect, near, far) {
    const f = 1.0 / Math.tan(fov / 2);
    const nf = 1 / (near - far);

    return new Float32Array([
      f / aspect, 0, 0, 0,
      0, f, 0, 0,
      0, 0, (far + near) * nf, -1,
      0, 0, (2 * far * near) * nf, 0
    ]);
  }

  /**
   * Create look-at view matrix
   */
  mat4LookAt(eye, center, up) {
    const z = this.vec3Normalize([
      eye[0] - center[0],
      eye[1] - center[1],
      eye[2] - center[2]
    ]);

    const x = this.vec3Normalize(this.vec3Cross(up, z));
    const y = this.vec3Cross(z, x);

    return new Float32Array([
      x[0], y[0], z[0], 0,
      x[1], y[1], z[1], 0,
      x[2], y[2], z[2], 0,
      -this.vec3Dot(x, eye), -this.vec3Dot(y, eye), -this.vec3Dot(z, eye), 1
    ]);
  }

  /**
   * Multiply two matrices
   */
  mat4Multiply(a, b) {
    const result = new Float32Array(16);
    
    for (let i = 0; i < 4; i++) {
      for (let j = 0; j < 4; j++) {
        result[i * 4 + j] = 
          a[i * 4 + 0] * b[0 * 4 + j] +
          a[i * 4 + 1] * b[1 * 4 + j] +
          a[i * 4 + 2] * b[2 * 4 + j] +
          a[i * 4 + 3] * b[3 * 4 + j];
      }
    }
    
    return result;
  }

  /**
   * Vector operations
   */
  vec3Normalize(v) {
    const len = Math.sqrt(v[0] * v[0] + v[1] * v[1] + v[2] * v[2]);
    return len > 0 ? [v[0] / len, v[1] / len, v[2] / len] : [0, 0, 0];
  }

  vec3Cross(a, b) {
    return [
      a[1] * b[2] - a[2] * b[1],
      a[2] * b[0] - a[0] * b[2],
      a[0] * b[1] - a[1] * b[0]
    ];
  }

  vec3Dot(a, b) {
    return a[0] * b[0] + a[1] * b[1] + a[2] * b[2];
  }

  /**
   * Screen to world ray
   */
  screenToWorldRay(screenX, screenY, width, height) {
    // Normalized device coordinates
    const ndcX = (2.0 * screenX) / width - 1.0;
    const ndcY = 1.0 - (2.0 * screenY) / height;

    // Ray in world space (simplified)
    const [pitch, yaw] = this.rotation;
    const direction = [
      Math.sin(yaw + ndcX * this.fov * Math.PI / 360) * Math.cos(pitch + ndcY * this.fov * Math.PI / 360),
      Math.cos(yaw + ndcX * this.fov * Math.PI / 360) * Math.cos(pitch + ndcY * this.fov * Math.PI / 360),
      Math.sin(pitch + ndcY * this.fov * Math.PI / 360)
    ];

    return {
      origin: [...this.position],
      direction: this.vec3Normalize(direction)
    };
  }
}

// Make available globally
window.Camera3D = Camera3D;
