/**
 * Linkpoint PWA - World Viewer Module
 * 3D rendering and world interaction
 */

class WorldViewer extends Utils.EventEmitter {
  constructor(protocolManager) {
    super();
    this.protocol = protocolManager;
    this.canvas = null;
    this.graphics3d = null;
    this.camera3d = null;
    this.scene3d = null;
    this.animationId = null;
    this.fps = 0;
    this.lastFrameTime = 0;
    this.frameCount = 0;
    this.use3D = true; // Toggle between 2D and 3D
    
    // Legacy 2D context (fallback)
    this.ctx = null;

    // World state
    this.region = {
      name: 'Simulated Region',
      x: 256000,
      y: 256000
    };

    this.objects = [];
    this.avatars = [];
    
    // 3D mode
    this.objectMeshes = new Map();
  }

  /**
   * Initialize world viewer
   */
  async init() {
    this.canvas = document.getElementById('world-canvas');
    if (!this.canvas) {
      console.error('World canvas not found');
      return;
    }

    // Try to initialize 3D graphics
    try {
      await this.init3D();
      this.use3D = true;
      console.log('✅ 3D graphics initialized');
    } catch (error) {
      console.warn('3D initialization failed, falling back to 2D:', error);
      this.use3D = false;
      this.ctx = this.canvas.getContext('2d');
    }

    this.resizeCanvas();

    // Setup event listeners
    window.addEventListener('resize', () => this.resizeCanvas());
    this.setupControls();

    // Setup protocol listeners
    this.protocol.on('object_update', (data) => this.handleObjectUpdate(data));
    this.protocol.on('avatar_update', (data) => this.handleAvatarUpdate(data));

    // Add demo 3D objects
    if (this.use3D) {
      this.addDemo3DObjects();
    }

    // Start rendering
    this.startRendering();
  }

  /**
   * Initialize 3D graphics
   */
  async init3D() {
    // Create 3D systems
    this.graphics3d = new Graphics3D(this.canvas);
    await this.graphics3d.init();

    this.camera3d = new Camera3D();
    this.camera3d.setPosition(128, 138, 35);
    this.camera3d.setRotation(-0.3, -Math.PI / 2, 0);
    this.camera3d.setMode('orbit');
    this.camera3d.setOrbitTarget(128, 128, 25);

    this.scene3d = new Scene3D(this.graphics3d, this.camera3d);
    await this.scene3d.init();

    // Listen to camera events
    this.camera3d.on('position_changed', (pos) => {
      this.emit('camera_moved', { x: pos[0], y: pos[1], z: pos[2] });
    });
  }

  /**
   * Add demo 3D objects
   */
  addDemo3DObjects() {
    // Add ground plane
    this.scene3d.addObject('ground', {
      mesh: 'plane',
      position: [128, 128, 0],
      scale: [25.6, 25.6, 1],
      color: [0.2, 0.4, 0.2, 1],
      material: 'basic'
    });

    // Add some cubes
    for (let i = 0; i < 5; i++) {
      const x = 128 + (Math.random() - 0.5) * 50;
      const y = 128 + (Math.random() - 0.5) * 50;
      const size = 2 + Math.random() * 3;
      
      this.scene3d.addObject(`cube_${i}`, {
        mesh: 'cube',
        position: [x, y, size / 2],
        scale: [size, size, size],
        color: [Math.random(), Math.random(), Math.random(), 1],
        material: 'basic'
      });
    }

    // Add spheres
    for (let i = 0; i < 3; i++) {
      const x = 128 + (Math.random() - 0.5) * 40;
      const y = 128 + (Math.random() - 0.5) * 40;
      const size = 1.5 + Math.random() * 2;
      
      this.scene3d.addObject(`sphere_${i}`, {
        mesh: 'sphere',
        position: [x, y, size + 5],
        scale: [size, size, size],
        color: [Math.random(), Math.random(), Math.random(), 1],
        material: 'basic'
      });
    }
  }

  /**
   * Resize canvas to fill viewport
   */
  resizeCanvas() {
    if (!this.canvas) return;

    const viewport = this.canvas.parentElement;
    if (viewport) {
      this.canvas.width = viewport.clientWidth;
      this.canvas.height = viewport.clientHeight;
    }
  }

  /**
   * Setup movement controls
   */
  setupControls() {
    const moveForward = document.getElementById('move-forward');
    const moveBack = document.getElementById('move-back');
    const moveLeft = document.getElementById('move-left');
    const moveRight = document.getElementById('move-right');
    const flyBtn = document.getElementById('fly-btn');

    moveForward?.addEventListener('click', () => this.moveCamera(0, -5, 0));
    moveBack?.addEventListener('click', () => this.moveCamera(0, 5, 0));
    moveLeft?.addEventListener('click', () => this.moveCamera(-5, 0, 0));
    moveRight?.addEventListener('click', () => this.moveCamera(5, 0, 0));
    flyBtn?.addEventListener('click', () => this.toggleFly());

    // Keyboard controls
    document.addEventListener('keydown', (e) => this.handleKeyboard(e));

    // Mouse controls
    this.canvas?.addEventListener('mousedown', (e) => this.handleMouseDown(e));
    this.canvas?.addEventListener('mousemove', (e) => this.handleMouseMove(e));
    this.canvas?.addEventListener('mouseup', (e) => this.handleMouseUp(e));
    this.canvas?.addEventListener('wheel', (e) => this.handleWheel(e));
  }

  /**
   * Move camera
   */
  moveCamera(dx, dy, dz) {
    if (this.use3D && this.camera3d) {
      this.camera3d.move(dy, dx, dz);
      const pos = this.camera3d.position;
      this.updateLocationDisplay();
      this.emit('camera_moved', { x: pos[0], y: pos[1], z: pos[2] });
    } else {
      // 2D fallback
      this.camera.x = Utils.clamp(this.camera.x + dx, 0, 256);
      this.camera.y = Utils.clamp(this.camera.y + dy, 0, 256);
      this.camera.z = Utils.clamp(this.camera.z + dz, 0, 4096);
      
      this.updateLocationDisplay();
      this.emit('camera_moved', this.camera);
    }
  }

  /**
   * Toggle fly mode
   */
  toggleFly() {
    // Fly toggle logic
    Utils.showToast('Fly mode toggled', 'info');
  }

  /**
   * Handle keyboard input
   */
  handleKeyboard(e) {
    const view = document.getElementById('view-world');
    if (!view || !view.classList.contains('active')) return;

    switch (e.key) {
      case 'w':
      case 'ArrowUp':
        this.moveCamera(0, -5, 0);
        break;
      case 's':
      case 'ArrowDown':
        this.moveCamera(0, 5, 0);
        break;
      case 'a':
      case 'ArrowLeft':
        this.moveCamera(-5, 0, 0);
        break;
      case 'd':
      case 'ArrowRight':
        this.moveCamera(5, 0, 0);
        break;
      case 'e':
        this.moveCamera(0, 0, 5);
        break;
      case 'c':
        this.moveCamera(0, 0, -5);
        break;
    }
  }

  /**
   * Handle mouse down
   */
  handleMouseDown(e) {
    this.isDragging = true;
    this.lastMouseX = e.clientX;
    this.lastMouseY = e.clientY;
  }

  /**
   * Handle mouse move
   */
  handleMouseMove(e) {
    if (!this.isDragging) return;

    const dx = e.clientX - this.lastMouseX;
    const dy = e.clientY - this.lastMouseY;

    if (this.use3D && this.camera3d) {
      this.camera3d.rotate(-dy * this.camera3d.rotateSpeed, -dx * this.camera3d.rotateSpeed);
    } else {
      this.camera.rotation += dx * 0.01;
    }
    
    this.lastMouseX = e.clientX;
    this.lastMouseY = e.clientY;
  }

  /**
   * Handle mouse up
   */
  handleMouseUp(e) {
    this.isDragging = false;
  }

  /**
   * Handle mouse wheel
   */
  handleWheel(e) {
    e.preventDefault();
    
    if (this.use3D && this.camera3d) {
      const delta = e.deltaY > 0 ? 0.1 : -0.1;
      this.camera3d.zoom(delta);
    } else {
      const delta = e.deltaY > 0 ? 0.9 : 1.1;
      this.camera.zoom = Utils.clamp(this.camera.zoom * delta, 0.5, 3);
    }
  }

  /**
   * Update location display
   */
  updateLocationDisplay() {
    const regionName = document.getElementById('region-name');
    const coordinates = document.getElementById('coordinates');

    if (regionName) {
      regionName.textContent = this.region.name;
    }

    if (coordinates) {
      let x, y, z;
      if (this.use3D && this.camera3d) {
        [x, y, z] = this.camera3d.position;
      } else {
        x = this.camera.x;
        y = this.camera.y;
        z = this.camera.z;
      }
      coordinates.textContent = `${Math.floor(x)}, ${Math.floor(y)}, ${Math.floor(z)}`;
    }
  }

  /**
   * Handle object update from protocol
   */
  handleObjectUpdate(data) {
    // Update or add object
    const existingIndex = this.objects.findIndex(obj => obj.id === data.id);
    if (existingIndex >= 0) {
      this.objects[existingIndex] = { ...this.objects[existingIndex], ...data };
    } else {
      this.objects.push(data);
    }
  }

  /**
   * Handle avatar update from protocol
   */
  handleAvatarUpdate(data) {
    const existingIndex = this.avatars.findIndex(av => av.id === data.id);
    if (existingIndex >= 0) {
      this.avatars[existingIndex] = { ...this.avatars[existingIndex], ...data };
    } else {
      this.avatars.push(data);
    }
  }

  /**
   * Update region info from protocol
   */
  updateRegionInfo(data) {
    console.log('[World] Updating region info:', data);
    if (data.name) {
      this.region.name = data.name;
    }
    if (data.x !== undefined) {
      this.region.x = data.x;
    }
    if (data.y !== undefined) {
      this.region.y = data.y;
    }
    this.updateLocationDisplay();
    this.emit('region_updated', this.region);
  }

  /**
   * Start rendering loop
   */
  startRendering() {
    const render = (timestamp) => {
      this.render(timestamp);
      this.animationId = requestAnimationFrame(render);
    };
    this.animationId = requestAnimationFrame(render);
  }

  /**
   * Stop rendering
   */
  stopRendering() {
    if (this.animationId) {
      cancelAnimationFrame(this.animationId);
      this.animationId = null;
    }
  }

  /**
   * Main render loop
   */
  render(timestamp) {
    // Calculate FPS
    this.frameCount++;
    if (timestamp - this.lastFrameTime >= 1000) {
      this.fps = this.frameCount;
      this.frameCount = 0;
      this.lastFrameTime = timestamp;
      
      const fpsCounter = document.getElementById('fps-counter');
      if (fpsCounter) {
        fpsCounter.textContent = this.fps;
      }
    }

    if (this.use3D && this.scene3d) {
      // 3D rendering
      this.render3D(timestamp);
    } else if (this.ctx) {
      // 2D fallback rendering
      this.render2D(timestamp);
    }
  }

  /**
   * 3D render
   */
  render3D(timestamp) {
    // Update camera matrices
    this.camera3d.updateMatrices();

    // Render scene
    this.scene3d.render();

    // Get and display stats
    const stats = this.graphics3d.getStats();
    // console.log('Draw calls:', stats.drawCalls, 'Triangles:', stats.triangles);
  }

  /**
   * 2D render (fallback)
   */
  render2D(timestamp) {
    // Clear canvas
    this.ctx.fillStyle = '#0a0e27';
    this.ctx.fillRect(0, 0, this.canvas.width, this.canvas.height);

    // Draw grid
    this.drawGrid();

    // Draw objects
    this.drawObjects();

    // Draw avatars
    this.drawAvatars();

    // Draw camera position marker
    this.drawCameraMarker();
  }

  /**
   * Draw grid
   */
  drawGrid() {
    const { ctx, canvas, camera } = this;
    const gridSize = 16;
    const offsetX = (canvas.width / 2) - (camera.x * camera.zoom);
    const offsetY = (canvas.height / 2) - (camera.y * camera.zoom);

    ctx.strokeStyle = '#1a2332';
    ctx.lineWidth = 1;

    // Vertical lines
    for (let x = 0; x <= 256; x += gridSize) {
      const screenX = offsetX + (x * camera.zoom);
      if (screenX >= 0 && screenX <= canvas.width) {
        ctx.beginPath();
        ctx.moveTo(screenX, 0);
        ctx.lineTo(screenX, canvas.height);
        ctx.stroke();
      }
    }

    // Horizontal lines
    for (let y = 0; y <= 256; y += gridSize) {
      const screenY = offsetY + (y * camera.zoom);
      if (screenY >= 0 && screenY <= canvas.height) {
        ctx.beginPath();
        ctx.moveTo(0, screenY);
        ctx.lineTo(canvas.width, screenY);
        ctx.stroke();
      }
    }

    // Draw region boundaries
    ctx.strokeStyle = '#00d4ff';
    ctx.lineWidth = 2;
    ctx.strokeRect(offsetX, offsetY, 256 * camera.zoom, 256 * camera.zoom);
  }

  /**
   * Draw objects
   */
  drawObjects() {
    const { ctx, canvas, camera } = this;
    const offsetX = (canvas.width / 2) - (camera.x * camera.zoom);
    const offsetY = (canvas.height / 2) - (camera.y * camera.zoom);

    this.objects.forEach(obj => {
      const x = offsetX + (obj.x || 128) * camera.zoom;
      const y = offsetY + (obj.y || 128) * camera.zoom;
      
      ctx.fillStyle = '#3fb950';
      ctx.beginPath();
      ctx.arc(x, y, 5 * camera.zoom, 0, Math.PI * 2);
      ctx.fill();
    });
  }

  /**
   * Draw avatars
   */
  drawAvatars() {
    const { ctx, canvas, camera } = this;
    const offsetX = (canvas.width / 2) - (camera.x * camera.zoom);
    const offsetY = (canvas.height / 2) - (camera.y * camera.zoom);

    this.avatars.forEach(avatar => {
      const x = offsetX + (avatar.x || 128) * camera.zoom;
      const y = offsetY + (avatar.y || 128) * camera.zoom;
      
      ctx.fillStyle = '#e94560';
      ctx.beginPath();
      ctx.arc(x, y, 8 * camera.zoom, 0, Math.PI * 2);
      ctx.fill();
      
      // Draw name
      if (camera.zoom > 0.8) {
        ctx.fillStyle = '#ffffff';
        ctx.font = '12px sans-serif';
        ctx.textAlign = 'center';
        ctx.fillText(avatar.name || 'Avatar', x, y - 15);
      }
    });
  }

  /**
   * Draw camera position marker
   */
  drawCameraMarker() {
    const { ctx, canvas } = this;
    const centerX = canvas.width / 2;
    const centerY = canvas.height / 2;

    // Draw crosshair
    ctx.strokeStyle = '#00d4ff';
    ctx.lineWidth = 2;
    
    ctx.beginPath();
    ctx.moveTo(centerX - 10, centerY);
    ctx.lineTo(centerX + 10, centerY);
    ctx.moveTo(centerX, centerY - 10);
    ctx.lineTo(centerX, centerY + 10);
    ctx.stroke();

    // Draw circle
    ctx.beginPath();
    ctx.arc(centerX, centerY, 20, 0, Math.PI * 2);
    ctx.stroke();
  }

  /**
   * Toggle between 2D and 3D
   */
  toggle3D() {
    if (!this.graphics3d) {
      Utils.showToast('3D graphics not available', 'warning');
      return;
    }

    this.use3D = !this.use3D;
    Utils.showToast(`Switched to ${this.use3D ? '3D' : '2D'} mode`, 'info');
  }

  /**
   * Cleanup
   */
  destroy() {
    this.stopRendering();
    window.removeEventListener('resize', () => this.resizeCanvas());
    
    if (this.graphics3d) {
      this.graphics3d.destroy();
    }
  }
}

// Make available globally
window.WorldViewer = WorldViewer;
