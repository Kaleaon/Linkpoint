import { Camera3D } from './camera-3d';
import { isMotionKey, isTypingTarget, resolveKeyMotion, TURN_RATE, type KeyMotion } from './keyboard-motion';

type PointerSample = { x: number; y: number };

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
  private lastFrame = 0;
  private frame: number | null = null;
  private pointerStart = new Map<number, PointerSample>();

  constructor(private canvas: HTMLCanvasElement, private camera: Camera3D, private changed: () => void = () => undefined, private picked: (x: number, y: number) => void = () => undefined, private avatarMotion: (motion: KeyMotion, run: boolean) => boolean = () => false) {
    canvas.style.touchAction = 'none';
    canvas.tabIndex = 0;
    canvas.addEventListener('pointerdown', this.onPointerDown);
    canvas.addEventListener('pointermove', this.onPointerMove);
    canvas.addEventListener('pointerup', this.onPointerUp);
    canvas.addEventListener('pointercancel', this.onPointerUp);
    canvas.addEventListener('wheel', this.onWheel, { passive: false });
    canvas.addEventListener('contextmenu', this.preventMenu);
    window.addEventListener('keydown', this.onKeyDown);
    window.addEventListener('keyup', this.onKeyUp);
    window.addEventListener('blur', this.onBlur);
    document.addEventListener('visibilitychange', this.onBlur);
  }

  private preventMenu = (event: Event) => event.preventDefault();
  private onPointerDown = (event: PointerEvent) => {
    this.canvas.focus({ preventScroll: true });
    this.canvas.setPointerCapture?.(event.pointerId);
    this.pointers.set(event.pointerId, { x: event.clientX, y: event.clientY });
    this.pointerStart.set(event.pointerId, { x: event.clientX, y: event.clientY });
    this.pinchDistance = this.distance();
  };
  private onPointerMove = (event: PointerEvent) => {
    const previous = this.pointers.get(event.pointerId);
    if (!previous) return;
    const dx = event.clientX - previous.x;
    const dy = event.clientY - previous.y;
    this.pointers.set(event.pointerId, { x: event.clientX, y: event.clientY });
    if (this.pointers.size > 1) {
      const distance = this.distance();
      if (this.pinchDistance) this.camera.zoom((distance - this.pinchDistance) / 240);
      this.pinchDistance = distance;
      this.camera.pan(-dx * .012, dy * .012);
    } else if (event.shiftKey || event.button === 1 || event.buttons === 4) {
      this.camera.pan(-dx * .012, dy * .012);
    } else {
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
    this.pointers.delete(event.pointerId);
    this.pointerStart.delete(event.pointerId);
    this.pinchDistance = this.distance();
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
  private distance() {
    const points = [...this.pointers.values()];
    return points.length < 2 ? 0 : Math.hypot(points[0].x - points[1].x, points[0].y - points[1].y);
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
    window.removeEventListener('keydown', this.onKeyDown);
    window.removeEventListener('keyup', this.onKeyUp);
    window.removeEventListener('blur', this.onBlur);
    document.removeEventListener('visibilitychange', this.onBlur);
    this.keys.clear();
    if (this.frame !== null) cancelAnimationFrame(this.frame);
  }
}
