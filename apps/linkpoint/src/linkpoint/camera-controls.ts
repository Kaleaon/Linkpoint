import { Camera3D } from './camera-3d';
import { isMotionKey, isTypingTarget, resolveKeyMotion, TURN_RATE, type KeyMotion } from './keyboard-motion';

type PointerSample = { x: number; y: number; time: number };

/**
 * Pointer, touch, wheel and keyboard controls modelled after Firestorm.
 * Keyboard shortcuts listen on the window, not the canvas: on desktop the
 * canvas sits behind floaters and the chrome, so it is rarely the focused
 * element. Typing in any text field still takes priority.
 */
export class CameraControls {
  private pointers = new Map<number, PointerSample>();
  private keys = new Set<string>();
  private pinchDistance = 0;
  private previousMidpoint: { x: number; y: number } | null = null;
  private lastFrame = 0;
  private frame: number | null = null;
  private pointerStart = new Map<number, PointerSample>();
  private velocityThreshold = 0.05; // px/ms
  private displacementThreshold = 3; // px
  private panMode = false;

  constructor(private canvas: HTMLCanvasElement, private camera: Camera3D, private changed: () => void = () => undefined, private picked: (x: number, y: number) => void = () => undefined, private avatarMotion: (motion: KeyMotion, run: boolean) => boolean = () => false) {
    canvas.style.touchAction = 'none';
    canvas.tabIndex = 0;
    canvas.addEventListener('pointerdown', this.onPointerDown);
    canvas.addEventListener('pointermove', this.onPointerMove);
    canvas.addEventListener('pointerup', this.onPointerUp);
    canvas.addEventListener('pointercancel', this.onPointerUp);
    canvas.addEventListener('wheel', this.onWheel, { passive: false });
    canvas.addEventListener('contextmenu', this.preventMenu);
    canvas.addEventListener('touchstart', this.onTouchStart, { passive: false });
    canvas.addEventListener('touchmove', this.onTouchMove, { passive: false });
    canvas.addEventListener('gesturestart', this.preventMenu);
    canvas.addEventListener('gesturechange', this.preventMenu);
    window.addEventListener('keydown', this.onKeyDown);
    window.addEventListener('keyup', this.onKeyUp);
    window.addEventListener('blur', this.onBlur);
    document.addEventListener('visibilitychange', this.onBlur);
  }

  public setVelocityThreshold(threshold: number) { this.velocityThreshold = Math.max(0, threshold); }
  public getVelocityThreshold() { return this.velocityThreshold; }
  public setDisplacementThreshold(threshold: number) { this.displacementThreshold = Math.max(0, threshold); }
  public getDisplacementThreshold() { return this.displacementThreshold; }
  public setPanMode(enabled: boolean) { this.panMode = enabled; }
  public getPanMode() { return this.panMode; }
  public togglePanMode() { this.panMode = !this.panMode; return this.panMode; }

  private preventMenu = (event: Event) => event.preventDefault();
  private onTouchStart = (event: TouchEvent) => {
    if (event.touches.length > 1) {
      event.preventDefault();
    }
  };
  private onTouchMove = (event: TouchEvent) => {
    // Prevent mobile browser page zoom / pull-to-refresh during 3D touch interaction
    event.preventDefault();
  };

  private sampleTime(event: PointerEvent) {
    if ((event as any).timeStampOverride !== undefined) return (event as any).timeStampOverride;
    return event.timeStamp && event.timeStamp > 0
      ? event.timeStamp
      : (typeof performance !== 'undefined' ? performance.now() : Date.now());
  }

  private onPointerDown = (event: PointerEvent) => {
    this.canvas.focus({ preventScroll: true });
    try { this.canvas.setPointerCapture?.(event.pointerId); } catch { /* ignore */ }
    const time = this.sampleTime(event);
    const sample = { x: event.clientX, y: event.clientY, time };
    this.pointers.set(event.pointerId, sample);
    this.pointerStart.set(event.pointerId, sample);
    if (this.pointers.size > 1) {
      this.pinchDistance = this.distance();
      this.previousMidpoint = this.midpoint();
    }
  };

  private onPointerMove = (event: PointerEvent) => {
    const previous = this.pointers.get(event.pointerId);
    if (!previous) return;
    const time = this.sampleTime(event);
    const dt = Math.max(time - previous.time, 0.001);
    const dx = event.clientX - previous.x;
    const dy = event.clientY - previous.y;
    const stepDistance = Math.hypot(dx, dy);
    const velocity = stepDistance / dt;

    const start = this.pointerStart.get(event.pointerId);
    const totalDisplacement = start ? Math.hypot(event.clientX - start.x, event.clientY - start.y) : stepDistance;

    // Filter touch/pointer drag gestures using velocity and displacement thresholds for single pointer
    if (this.pointers.size === 1 && velocity < this.velocityThreshold && totalDisplacement < this.displacementThreshold) {
      return;
    }

    this.pointers.set(event.pointerId, { x: event.clientX, y: event.clientY, time });

    if (this.pointers.size > 1) {
      // Multi-touch: Pinch-to-zoom (pinch to zoom out, spread to zoom in) & Two-finger Panning
      const currentDistance = this.distance();
      const currentMidpoint = this.midpoint();

      // 1. Pinch to zoom out, spread fingers to zoom in
      if (this.pinchDistance > 0 && currentDistance > 0) {
        const delta = currentDistance - this.pinchDistance;
        // Spreading fingers (delta > 0) zooms in; pinching fingers (delta < 0) zooms out
        this.camera.zoom(delta / 180);
        this.pinchDistance = currentDistance;
      } else {
        this.pinchDistance = currentDistance;
      }

      // 2. Two-finger Panning: moving both fingers together pans the camera smoothly
      if (this.previousMidpoint && currentMidpoint) {
        const midDx = currentMidpoint.x - this.previousMidpoint.x;
        const midDy = currentMidpoint.y - this.previousMidpoint.y;
        if (Math.hypot(midDx, midDy) > 0.5) {
          this.camera.pan(-midDx * 0.015, midDy * 0.015);
        }
        this.previousMidpoint = currentMidpoint;
      } else {
        this.previousMidpoint = currentMidpoint;
      }
    } else if (this.panMode || event.shiftKey || event.button === 1 || event.buttons === 4) {
      // Single-pointer Pan (Pan mode on mobile or Shift-drag / middle-drag on desktop)
      this.camera.pan(-dx * 0.015, dy * 0.015);
    } else {
      // Single-pointer Orbit / Rotate
      this.camera.rotate(-dy * this.camera.rotateSpeed, -dx * this.camera.rotateSpeed);
    }
    this.changed();
  };

  private onPointerUp = (event: PointerEvent) => {
    const start = this.pointerStart.get(event.pointerId);
    if (start && Math.hypot(event.clientX - start.x, event.clientY - start.y) < 6) {
      const bounds = this.canvas.getBoundingClientRect();
      this.picked(event.clientX - bounds.left, event.clientY - bounds.top);
    }
    try { this.canvas.releasePointerCapture?.(event.pointerId); } catch { /* ignore */ }
    this.pointers.delete(event.pointerId);
    this.pointerStart.delete(event.pointerId);
    if (this.pointers.size > 1) {
      this.pinchDistance = this.distance();
      this.previousMidpoint = this.midpoint();
    } else {
      this.pinchDistance = 0;
      this.previousMidpoint = null;
    }
  };

  private onWheel = (event: WheelEvent) => {
    event.preventDefault();
    this.camera.zoom(-event.deltaY / 700);
    this.changed();
  };
  private shift = false;
  private onKeyDown = (event: KeyboardEvent) => {
    this.shift = event.shiftKey;
    if (!isMotionKey(event.code)) return;
    // Leave browser/OS shortcuts and text entry alone.
    if (event.ctrlKey || event.metaKey || event.altKey || isTypingTarget(event.target)) return;
    // A focused button or link would otherwise also activate on Space.
    event.preventDefault();
    this.keys.add(event.code);
    this.startKeys();
  };
  private onKeyUp = (event: KeyboardEvent) => {
    this.shift = event.shiftKey;
    this.keys.delete(event.code);
  };
  private onBlur = () => { this.keys.clear(); this.shift = false; };

  private distance(): number {
    const points = [...this.pointers.values()];
    return points.length < 2 ? 0 : Math.hypot(points[0].x - points[1].x, points[0].y - points[1].y);
  }

  private midpoint(): { x: number; y: number } | null {
    const points = [...this.pointers.values()];
    if (points.length < 2) return null;
    return {
      x: (points[0].x + points[1].x) / 2,
      y: (points[0].y + points[1].y) / 2,
    };
  }

  private startKeys() {
    if (this.frame !== null) return;
    const tick = (time: number) => {
      const seconds = Math.min((time - (this.lastFrame || time)) / 1000, .05);
      this.lastFrame = time;
      const motion = resolveKeyMotion(this.keys, this.shift);
      const avatarMoved = this.avatarMotion(motion, this.shift);
      const step = seconds * this.camera.moveSpeed;
      let moved = false;
      if (!avatarMoved && motion.turn) { this.camera.turn(motion.turn * TURN_RATE * seconds); moved = true; }
      if (!avatarMoved && (motion.forward || motion.right || motion.up)) {
        this.camera.move(motion.forward * step, motion.right * step, motion.up * step);
        moved = true;
      }
      if (moved) this.changed();
      if (this.keys.size) this.frame = requestAnimationFrame(tick);
      else { this.frame = null; this.lastFrame = 0; }
    };
    this.frame = requestAnimationFrame(tick);
  }

  destroy() {
    this.canvas.removeEventListener('pointerdown', this.onPointerDown);
    this.canvas.removeEventListener('pointermove', this.onPointerMove);
    this.canvas.removeEventListener('pointerup', this.onPointerUp);
    this.canvas.removeEventListener('pointercancel', this.onPointerUp);
    this.canvas.removeEventListener('wheel', this.onWheel);
    this.canvas.removeEventListener('contextmenu', this.preventMenu);
    this.canvas.removeEventListener('touchstart', this.onTouchStart);
    this.canvas.removeEventListener('touchmove', this.onTouchMove);
    this.canvas.removeEventListener('gesturestart', this.preventMenu);
    this.canvas.removeEventListener('gesturechange', this.preventMenu);
    window.removeEventListener('keydown', this.onKeyDown);
    window.removeEventListener('keyup', this.onKeyUp);
    window.removeEventListener('blur', this.onBlur);
    document.removeEventListener('visibilitychange', this.onBlur);
    this.keys.clear();
    if (this.frame !== null) cancelAnimationFrame(this.frame);
  }
}
