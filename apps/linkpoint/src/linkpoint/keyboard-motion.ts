/**
 * Keyboard bindings for the 3D camera, kept free of DOM and camera state so
 * they can be tested directly.
 *
 *  W / Up         forward           S / Down      backward
 *  A / D          strafe left/right Left / Right  turn left/right
 *  E / Space / PageUp   up          Q / C / PageDown  down
 *  Shift          move faster
 */

export const MOTION_KEYS = [
  'KeyW', 'KeyA', 'KeyS', 'KeyD', 'KeyQ', 'KeyE', 'KeyC', 'Space',
  'ArrowUp', 'ArrowDown', 'ArrowLeft', 'ArrowRight', 'PageUp', 'PageDown',
] as const;

const MOTION_KEY_SET = new Set<string>(MOTION_KEYS);
export const isMotionKey = (code: string) => MOTION_KEY_SET.has(code);

export interface KeyMotion {
  forward: number;
  right: number;
  up: number;
  /** Positive turns the view to the right. */
  turn: number;
}

export const WALK_SPEED_MULTIPLIER = 1;
export const RUN_SPEED_MULTIPLIER = 3;
export const TURN_RATE = 1.8; // radians per second

const axis = (keys: ReadonlySet<string>, positive: string[], negative: string[]) =>
  Number(positive.some((code) => keys.has(code))) - Number(negative.some((code) => keys.has(code)));

/** Resolve the held keys into a motion vector. Diagonals are normalized so they are no faster. */
export function resolveKeyMotion(keys: ReadonlySet<string>, shift = false): KeyMotion {
  let forward = axis(keys, ['KeyW', 'ArrowUp'], ['KeyS', 'ArrowDown']);
  let right = axis(keys, ['KeyD'], ['KeyA']);
  const up = axis(keys, ['KeyE', 'Space', 'PageUp'], ['KeyQ', 'KeyC', 'PageDown']);
  const turn = axis(keys, ['ArrowRight'], ['ArrowLeft']);
  const planar = Math.hypot(forward, right);
  if (planar > 1) { forward /= planar; right /= planar; }
  const speed = shift ? RUN_SPEED_MULTIPLIER : WALK_SPEED_MULTIPLIER;
  return { forward: forward * speed, right: right * speed, up: up * speed, turn: turn * (shift ? 1.5 : 1) };
}

/** True when typing should win over camera shortcuts. */
export function isTypingTarget(target: EventTarget | null): boolean {
  const element = target as HTMLElement | null;
  if (!element || typeof element !== 'object' || !('tagName' in element)) return false;
  const tag = element.tagName;
  if (tag === 'INPUT' || tag === 'TEXTAREA' || tag === 'SELECT') return true;
  if (element.isContentEditable) return true;
  const role = element.getAttribute?.('role');
  return role === 'textbox' || role === 'combobox' || role === 'searchbox';
}
