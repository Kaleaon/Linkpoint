/**
 * Worn HUDs: attachments on the HUD attachment points (31-38).
 *
 * Second Life draws a HUD in screen space and maps its prims onto the real
 * screen. A phone or a small window cannot honour that mapping, so, like Lumiya,
 * each HUD is fitted to a panel: its extents are measured, centred, and scaled
 * so the largest side fills a chosen part of the view. Behaviour and constants
 * come from Lumiya's recovered WorldViewRenderer and DrawableHUD, checked
 * against the original smali:
 *
 *  - the HUD view is orthographic, x from -aspect to +aspect and y from -1 to 1;
 *  - HUD space is rotated 90 degrees about Y then -90 about X to reach the
 *    screen, which makes SL x the depth axis, SL y screen-left and SL z up;
 *  - the extents come from every prim's box corners in HUD space, and a HUD
 *    smaller than 0.001 in both screen directions is left unscaled.
 *
 * Differences from Lumiya, on purpose: the fit scales all three axes equally (it
 * scaled only y and z), so a deep HUD keeps its proportions, and panning is in
 * screen units after the fit.
 */

/** HUD attachment point ids and names. */
export const HUD_POINTS: Readonly<Record<number, string>> = {
  31: 'Center 2', 32: 'Top Right', 33: 'Top', 34: 'Top Left', 35: 'Center', 36: 'Bottom Left', 37: 'Bottom', 38: 'Bottom Right',
};

export const isHudPoint = (id: unknown): boolean => typeof id === 'number' && Object.prototype.hasOwnProperty.call(HUD_POINTS, id);

export interface Box { min: [number, number, number]; max: [number, number, number] }

/** A prim's placement relative to its HUD root (position, rotation quaternion xyzw, scale). */
export interface HudPrim { position: number[]; rotation: number[]; scale: number[] }

const rotateByQuaternion = (v: number[], q: number[]): number[] => {
  const [x, y, z] = v;
  const [qx, qy, qz, qw] = q;
  const ix = qw * x + qy * z - qz * y;
  const iy = qw * y + qz * x - qx * z;
  const iz = qw * z + qx * y - qy * x;
  const iw = -qx * x - qy * y - qz * z;
  return [
    ix * qw + iw * -qx + iy * -qz - iz * -qy,
    iy * qw + iw * -qy + iz * -qx - ix * -qz,
    iz * qw + iw * -qz + ix * -qy - iy * -qx,
  ];
};

/** Bounding box, in HUD space, of every prim's oriented box. Null when there are no prims. */
export function hudExtents(prims: HudPrim[]): Box | null {
  let min: number[] | null = null;
  let max: number[] | null = null;
  for (const prim of prims) {
    const scale = prim.scale.map((value) => Math.abs(Number(value)) || 0);
    const rotation = prim.rotation.length === 4 ? prim.rotation : [0, 0, 0, 1];
    for (const sx of [-0.5, 0.5]) for (const sy of [-0.5, 0.5]) for (const sz of [-0.5, 0.5]) {
      const corner = rotateByQuaternion([sx * scale[0], sy * scale[1], sz * scale[2]], rotation).map((value, axis) => value + (prim.position[axis] === undefined ? 0 : Number(prim.position[axis])));
      if (!corner.every(Number.isFinite)) continue;
      min = min ? min.map((value, axis) => Math.min(value, corner[axis])) : [...corner];
      max = max ? max.map((value, axis) => Math.max(value, corner[axis])) : [...corner];
    }
  }
  return min && max ? { min: min as Box['min'], max: max as Box['max'] } : null;
}

/** Column-major matrix taking SL HUD space to screen space: (x, y, z) -> (-y, z, -x). */
export function hudToScreenMatrix(): Float32Array {
  return new Float32Array([
    0, 0, -1, 0, // SL x (depth) -> screen -z, away from the viewer
    -1, 0, 0, 0, // SL y (left) -> screen -x
    0, 1, 0, 0, // SL z (up) -> screen +y
    0, 0, 0, 1,
  ]);
}

const multiply = (a: ArrayLike<number>, b: ArrayLike<number>): Float32Array => {
  const out = new Float32Array(16);
  for (let c = 0; c < 4; c++) for (let r = 0; r < 4; r++) {
    let sum = 0;
    for (let k = 0; k < 4; k++) sum += a[k * 4 + r] * b[c * 4 + k];
    out[c * 4 + r] = sum;
  }
  return out;
};

export interface HudFit {
  /** HUD-space to screen matrix including the fit and pan. */
  matrix: Float32Array;
  /** Uniform scale applied by the fit. */
  scale: number;
}

/**
 * Fit a HUD to the view. `size` is the fraction of the view height the HUD's
 * largest side should fill (Lumiya's default is 1, meaning half the screen
 * height in its -1..1 view), `pan` offsets it on screen in view units.
 */
export function fitHud(extents: Box | null, size = 1, pan: [number, number] = [0, 0]): HudFit {
  const toScreen = hudToScreenMatrix();
  if (!extents) return { matrix: toScreen, scale: 1 };
  const [minX] = extents.min;
  const centreY = (extents.min[1] + extents.max[1]) / 2;
  const centreZ = (extents.min[2] + extents.max[2]) / 2;
  const largest = Math.max(extents.max[1] - extents.min[1], extents.max[2] - extents.min[2]);
  const scale = largest > 0.001 ? size / largest : 1;
  // Move the HUD so its nearest face sits at depth 0 and it is centred, then scale.
  const translate = new Float32Array([1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, -minX, -centreY, -centreZ, 1]);
  const uniform = new Float32Array([scale, 0, 0, 0, 0, scale, 0, 0, 0, 0, scale, 0, 0, 0, 0, 1]);
  const panning = new Float32Array([1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, pan[0], pan[1], 0, 1]);
  return { matrix: multiply(panning, multiply(toScreen, multiply(uniform, translate))), scale };
}

/** Orthographic projection for the HUD view: x in [-aspect, aspect], y in [-1, 1], depth +-far. */
export function hudProjection(aspect: number, far = 100): Float32Array {
  const a = Number.isFinite(aspect) && aspect > 0 ? aspect : 1;
  return new Float32Array([
    1 / a, 0, 0, 0,
    0, 1, 0, 0,
    0, 0, -1 / far, 0,
    0, 0, 0, 1,
  ]);
}

export interface HudInfo {
  /** The HUD's root object id. */
  id: string;
  name: string;
  attachmentPoint: number;
  pointName: string;
  /** Ids of the root and every prim linked to it. */
  memberIds: string[];
}

/** Largest HUD zoom and the step the picker uses. */
export const HUD_SIZE = { min: 0.4, max: 1.9, step: 0.15, initial: 1 } as const;
