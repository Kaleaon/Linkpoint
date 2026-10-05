/**
 * Linkpoint PWA - 3D Camera System
 */

import { Utils } from './utils';
import { multiplyMat4 } from './frustum';
import { invertMat4, rayFromNDC } from './ray-pick';

export class Camera3D extends Utils.EventEmitter {
  public position: number[] = [128, 128, 25];
  public rotation: number[] = [0, 0, 0]; // pitch, yaw, roll
  public target: number[] = [128, 128, 0];
  
  // Projection
  public fov: number = 60; // degrees
  public aspect: number = 16 / 9;
  public near: number = 0.1;
  public far: number = 1000;
  
  // Movement
  public moveSpeed: number = 10.0;
  public rotateSpeed: number = 0.002;
  public zoomSpeed: number = 1.0;
  
  // Matrices
  public viewMatrix: Float32Array;
  public projectionMatrix: Float32Array;
  public viewProjectionMatrix: Float32Array;
  
  // Camera mode
  public mode: string = 'orbit'; // 'orbit', 'first-person', 'third-person'
  public orbitDistance: number = 10;
  public orbitTarget: number[] = [128, 128, 25];

  /** Firestorm-style named camera positions. */
  public preset: 'rear' | 'front' | 'first-person' | 'free' = 'rear';

  constructor() {
    super();
    this.viewMatrix = this.createMatrix4();
    this.projectionMatrix = this.createMatrix4();
    this.viewProjectionMatrix = this.createMatrix4();
    this.updateMatrices();
  }

  /**
   * Set position
   */
  setPosition(x: number, y: number, z: number) {
    this.position = [x, y, z];
    this.updateMatrices();
    this.emit('position_changed', this.position);
  }

  /**
   * Set rotation
   */
  setRotation(pitch: number, yaw: number, roll: number = 0) {
    this.rotation = [pitch, yaw, roll];
    this.updateMatrices();
    this.emit('rotation_changed', this.rotation);
  }

  /**
   * Look at target
   */
  lookAt(target: number[]) {
    this.target = target;
    this.updateMatrices();
  }

  /**
   * Unit horizontal direction the camera is looking along. In orbit mode the
   * rotation describes where the camera sits relative to its target, so the
   * view direction is the opposite of the first-person heading.
   */
  private horizontalHeading(): [number, number] {
    const yaw = this.rotation[1];
    const sign = this.mode === 'orbit' ? -1 : 1;
    return [sign * Math.sin(yaw), sign * Math.cos(yaw)];
  }

  /**
   * Move camera. `forward` and `right` are relative to what is on screen:
   * positive forward moves into the view, positive right moves to the right of
   * it. Orbit mode moves the focus point along the ground (pitch is ignored so
   * looking down does not sink the camera); first-person flies along the view.
   */
  move(forward: number, right: number, up: number) {
    const pitch = this.rotation[0];
    const [hx, hy] = this.horizontalHeading();
    const climb = this.mode === 'orbit' ? 0 : Math.sin(pitch);
    const reach = this.mode === 'orbit' ? 1 : Math.cos(pitch);

    const destination = this.mode === 'orbit' ? this.orbitTarget : this.position;
    destination[0] += hx * reach * forward + hy * right;
    destination[1] += hy * reach * forward - hx * right;
    destination[2] += climb * forward + up;

    this.updateMatrices();
    this.emit('moved', this.position);
  }

  /**
   * Where the camera is actually looking, as a compass heading (0 = north/+Y,
   * clockwise) and a pitch in degrees (positive looks up). Orbit mode's rotation
   * describes the camera's position around its target, so the view direction is
   * derived rather than read straight from `rotation`.
   */
  viewAngles(): { heading: number; pitch: number } {
    const [hx, hy] = this.horizontalHeading();
    const heading = ((Math.atan2(hx, hy) * 180) / Math.PI + 360) % 360;
    const pitch = ((this.mode === 'orbit' ? -this.rotation[0] : this.rotation[0]) * 180) / Math.PI;
    return { heading, pitch };
  }

  /** Turn the view left/right on screen (positive = right), whichever mode is active. */
  turn(amount: number) {
    this.rotate(0, this.mode === 'orbit' ? -amount : amount);
  }

  /**
   * Rotate camera
   */
  rotate(deltaPitch: number, deltaYaw: number, deltaRoll: number = 0) {
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
  zoom(delta: number) {
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
  setMode(mode: string) {
    this.mode = mode;
    this.updateMatrices();
    this.emit('mode_changed', mode);
  }

  setPreset(preset: 'rear' | 'front' | 'first-person' | 'free') {
    this.preset = preset;
    if (preset === 'first-person') {
      this.mode = 'first-person';
      this.position = [...this.orbitTarget];
      this.position[2] += 1.65;
    } else {
      this.mode = preset === 'free' ? 'first-person' : 'orbit';
      if (preset === 'rear') this.rotation = [-0.28, Math.PI, 0];
      if (preset === 'front') this.rotation = [-0.18, 0, 0];
      if (preset !== 'free') this.orbitDistance = 7.5;
    }
    this.updateMatrices();
    this.emit('preset_changed', preset);
  }

  reset(target?: number[]) {
    if (target && Array.isArray(target) && target.length >= 3) {
      this.orbitTarget = [Number(target[0]) || 128, Number(target[1]) || 128, Number(target[2]) || 25];
    }
    this.mode = 'orbit';
    this.preset = 'rear';
    this.rotation = [-0.28, Math.PI, 0];
    this.orbitDistance = 7.5;
    this.fov = 60;
    this.updateMatrices();
    this.emit('preset_changed', this.preset);
    this.emit('mode_changed', this.mode);
    this.emit('zoomed', this.orbitDistance);
    this.emit('rotated', this.rotation);
    this.emit('reset');
  }

  /** Pan parallel to the view plane, as Firestorm's Alt+Ctrl+Shift drag does. */
  pan(horizontal: number, vertical: number) {
    const [hx, hy] = this.horizontalHeading();
    // Screen-right is the heading rotated a quarter turn clockwise.
    const delta = [hy * horizontal, -hx * horizontal, vertical];
    const destination = this.mode === 'orbit' ? this.orbitTarget : this.position;
    for (let index = 0; index < 3; index++) destination[index] += delta[index];
    this.updateMatrices();
    this.emit('panned', [...destination]);
  }

  /**
   * Set orbit target
   */
  setOrbitTarget(x: number, y: number, z: number) {
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
  setAspect(aspect: number) {
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
  createMatrix4(): Float32Array {
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
  mat4Perspective(fov: number, aspect: number, near: number, far: number): Float32Array {
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
  mat4LookAt(eye: number[], center: number[], up: number[] = [0, 0, 1]): Float32Array {
    const diff = [
      eye[0] - center[0],
      eye[1] - center[1],
      eye[2] - center[2]
    ];
    const dist = Math.hypot(diff[0], diff[1], diff[2]);
    const z = dist > 0.00001 ? [diff[0] / dist, diff[1] / dist, diff[2] / dist] : [0, 0, 1];

    let x = this.vec3Cross(up, z);
    if (Math.hypot(x[0], x[1], x[2]) < 0.0001) {
      // Degenerate/gimbal lock: up and z are collinear (e.g. looking straight down/up along z)
      x = this.vec3Cross([0, 1, 0], z);
      if (Math.hypot(x[0], x[1], x[2]) < 0.0001) {
        x = this.vec3Cross([1, 0, 0], z);
      }
    }
    x = this.vec3Normalize(x);
    const y = this.vec3Cross(z, x);

    return new Float32Array([
      x[0], y[0], z[0], 0,
      x[1], y[1], z[1], 0,
      x[2], y[2], z[2], 0,
      -this.vec3Dot(x, eye), -this.vec3Dot(y, eye), -this.vec3Dot(z, eye), 1
    ]);
  }

  /**
   * Multiply two column-major matrices: returns `a * b`, so `P * V` is
   * `mat4Multiply(P, V)`. (The previous row-major indexing silently returned
   * `b * a` for WebGL-layout data.)
   */
  mat4Multiply(a: Float32Array, b: Float32Array): Float32Array {
    return multiplyMat4(a, b);
  }

  /**
   * Vector operations
   */
  vec3Normalize(v: number[]): number[] {
    const len = Math.sqrt(v[0] * v[0] + v[1] * v[1] + v[2] * v[2]);
    return len > 0 ? [v[0] / len, v[1] / len, v[2] / len] : [0, 0, 0];
  }

  vec3Cross(a: number[], b: number[]): number[] {
    return [
      a[1] * b[2] - a[2] * b[1],
      a[2] * b[0] - a[0] * b[2],
      a[0] * b[1] - a[1] * b[0]
    ];
  }

  vec3Dot(a: number[], b: number[]): number {
    return a[0] * b[0] + a[1] * b[1] + a[2] * b[2];
  }

  /**
   * Screen to world ray. Unprojects through the inverse view-projection matrix,
   * so it honours aspect ratio, field of view, and both orbit and first-person
   * modes. Direction has unit length and origin is the camera position.
   */
  screenToWorldRay(screenX: number, screenY: number, width: number, height: number) {
    const fallback = { origin: [...this.position], direction: [0, 1, 0] };
    if (!(width > 0) || !(height > 0)) return fallback;
    const inverse = invertMat4(multiplyMat4(this.projectionMatrix, this.viewMatrix));
    if (!inverse) return fallback;
    const ndcX = (2.0 * screenX) / width - 1.0;
    const ndcY = 1.0 - (2.0 * screenY) / height;
    const ray = rayFromNDC(inverse, ndcX, ndcY);
    // Every perspective ray passes through the eye, so start there.
    return ray ? { origin: [...this.position], direction: ray.direction } : fallback;
  }
}
