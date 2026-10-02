/**
 * Second Life keyframe animation (.anim) parsing, sampling and timing.
 *
 * Behaviour follows Lumiya's `AnimationData` (checked against the recovered
 * Java/smali); the code is a fresh TypeScript implementation.
 */

export type Vec3 = [number, number, number];
export type Quat = [number, number, number, number];

export interface RotKeyframe { time: number; value: Quat }
export interface PosKeyframe { time: number; value: Vec3 }
export interface JointAnimation { name: string; priority: number; rotations: RotKeyframe[]; positions: PosKeyframe[] }
export interface KeyframeAnimation {
  priority: number;
  length: number;
  expression: string;
  inPoint: number;
  outPoint: number;
  loop: boolean;
  easeIn: number;
  easeOut: number;
  handPose: number;
  joints: JointAnimation[];
}

const MAX_PELVIS_OFFSET = 5;
const MAX_KEYFRAMES = 10000;
const U16 = 1 / 65535;

/** Quantised uint16 -> float in [lo, hi]; values within one step of zero snap to zero. */
function dequantise(value: number, lo: number, hi: number): number {
  const span = hi - lo;
  const result = value * U16 * span + lo;
  return Math.abs(result) < span * U16 ? 0 : result;
}

/** Rebuild a unit quaternion from its x/y/z parts (w is always the non-negative root). */
export function unpackQuaternion(x: number, y: number, z: number): Quat {
  const rest = 1 - (x * x + y * y + z * z);
  return [x, y, z, rest > 0 ? Math.sqrt(rest) : 0];
}

export function parseAnimation(data: ArrayBuffer | Uint8Array): KeyframeAnimation {
  const bytes = data instanceof Uint8Array ? data : new Uint8Array(data);
  const view = new DataView(bytes.buffer, bytes.byteOffset, bytes.byteLength);
  let pos = 4; // version + sub-version (two uint16)
  const need = (count: number) => { if (pos + count > bytes.length) throw new Error('Truncated animation'); };
  const i32 = () => { need(4); const v = view.getInt32(pos, true); pos += 4; return v; };
  const f32 = () => { need(4); const v = view.getFloat32(pos, true); pos += 4; return v; };
  const u16 = () => { need(2); const v = view.getUint16(pos, true); pos += 2; return v; };
  const cstr = () => {
    let end = pos;
    while (end < bytes.length && bytes[end] !== 0) end++;
    if (end >= bytes.length) throw new Error('Truncated animation');
    const text = new TextDecoder().decode(bytes.subarray(pos, end));
    pos = end + 1;
    return text;
  };
  need(4);
  const priority = i32();
  const length = f32();
  const expression = cstr();
  const inPoint = f32();
  const outPoint = f32();
  const loop = i32() !== 0;
  const easeIn = f32();
  const easeOut = f32();
  const handPose = i32();
  const jointCount = i32();
  if (jointCount < 0 || jointCount > 1000 || !(length >= 0)) throw new Error('Invalid animation header');
  const joints: JointAnimation[] = [];
  for (let j = 0; j < jointCount; j++) {
    const name = cstr();
    const jointPriority = i32();
    let rotCount = i32(); if (rotCount < 0 || rotCount > MAX_KEYFRAMES) rotCount = 0;
    const rotations: RotKeyframe[] = [];
    for (let k = 0; k < rotCount; k++) {
      const time = dequantise(u16(), 0, length);
      rotations.push({ time, value: unpackQuaternion(dequantise(u16(), -1, 1), dequantise(u16(), -1, 1), dequantise(u16(), -1, 1)) });
    }
    let posCount = i32(); if (posCount < 0 || posCount > MAX_KEYFRAMES) posCount = 0;
    const positions: PosKeyframe[] = [];
    for (let k = 0; k < posCount; k++) {
      const time = dequantise(u16(), 0, length);
      positions.push({ time, value: [dequantise(u16(), -MAX_PELVIS_OFFSET, MAX_PELVIS_OFFSET), dequantise(u16(), -MAX_PELVIS_OFFSET, MAX_PELVIS_OFFSET), dequantise(u16(), -MAX_PELVIS_OFFSET, MAX_PELVIS_OFFSET)] });
    }
    joints.push({ name, priority: jointPriority, rotations, positions });
  }
  return { priority, length, expression, inPoint, outPoint, loop, easeIn, easeOut, handPose, joints };
}

/**
 * Linear sample of keyframes at `time`. Before the first key the first value
 * is held; between keys it interpolates; the segment wrapping past the last key
 * back to the first (time < first key) treats the first key as `length` later.
 */
function sampleKeys<T extends { time: number; value: number[] }>(keys: T[], time: number, length: number): number[] | null {
  if (!keys.length) return null;
  if (keys.length === 1) return keys[0].value;
  for (let i = 0; i < keys.length; i++) {
    if (time > keys[i].time) continue;
    if (time === keys[i].time || i === 0) return keys[i].value;
    const next = keys[i], prev = keys[i - 1];
    let prevTime = prev.time;
    if (prevTime > next.time) prevTime -= length;
    const span = next.time - prevTime;
    if (span === 0) return next.value;
    const t = (time - prevTime) / span;
    return next.value.map((v, c) => prev.value[c] * (1 - t) + v * t);
  }
  return keys[keys.length - 1].value; // past the last key: hold it
}

export function sampleRotation(joint: JointAnimation, time: number, length: number): Quat | null {
  const q = sampleKeys(joint.rotations, time, length) as Quat | null;
  if (!q) return null;
  const n = Math.hypot(q[0], q[1], q[2], q[3]);
  return n > 1e-9 ? [q[0] / n, q[1] / n, q[2] / n, q[3] / n] : [0, 0, 0, 1];
}

export function samplePosition(joint: JointAnimation, time: number, length: number): Vec3 | null {
  return sampleKeys(joint.positions, time, length) as Vec3 | null;
}

const smooth = (v: number) => { const c = Math.max(0, Math.min(1, v)); return (3 - 2 * c) * c * c; };

export interface AnimationTiming { time: number; weight: number }

/**
 * Where to sample and how strongly to apply an animation.
 * @param elapsed seconds since the animation started
 * @param stoppedFor seconds since it was told to stop, or -1 while running
 */
export function animationTiming(anim: KeyframeAnimation, elapsed: number, stoppedFor = -1): AnimationTiming {
  const { length, inPoint, outPoint, loop, easeIn, easeOut } = anim;
  // sample time
  let time: number;
  if (!loop) time = Math.min(elapsed, length);
  else if (elapsed < inPoint) time = elapsed;
  else if (outPoint > inPoint) {
    const loopLen = outPoint - inPoint;
    if (stoppedFor < 0) time = inPoint + ((elapsed - inPoint) % loopLen);
    else {
      // finish the current pass, then run on to the end of the animation
      const loopStart = elapsed - stoppedFor;
      const passes = Math.floor((loopStart - inPoint) / loopLen);
      time = Math.min(elapsed - passes * loopLen, length);
    }
  } else time = stoppedFor < 0 ? inPoint : Math.min(outPoint + stoppedFor, length);
  // ease in
  const inFactor = elapsed >= easeIn || easeIn < 0.001 ? 1 : smooth(elapsed / easeIn);
  // ease out
  let outFactor = 1;
  const easeOutAt = (since: number) => (easeOut < 0.001 ? 0 : smooth(1 - since / easeOut));
  if (stoppedFor >= 0) {
    // A stopped non-looping animation also eases out if it was already inside its natural tail.
    const naturalTail = elapsed - (length - easeOut);
    outFactor = easeOutAt(!loop && naturalTail > 0 ? Math.max(stoppedFor, naturalTail) : stoppedFor);
  } else if (!loop) {
    const into = elapsed - (length - easeOut);
    if (into >= 0) outFactor = easeOutAt(into);
  }
  return { time, weight: inFactor * outFactor };
}

export interface RunningAnimation { anim: KeyframeAnimation; startedAt: number; stoppedAt?: number | null }
export interface JointPose { rotation: Quat; position: Vec3 | null }

/**
 * Blend running animations into one pose per joint name. Higher priority wins
 * the joint; within equal priority, later-started animations are applied
 * first and each takes its weight from what remains (as Lumiya's weight
 * budget does), so the result never exceeds a unit blend.
 */
export function blendAnimations(running: RunningAnimation[], now: number): Map<string, JointPose> {
  const entries = running
    .map((r) => ({ r, t: animationTiming(r.anim, now - r.startedAt, r.stoppedAt == null ? -1 : now - r.stoppedAt) }))
    .filter((e) => e.t.weight > 0);
  type Contribution = { priority: number; started: number; rotation: Quat | null; position: Vec3 | null; weight: number };
  const byJoint = new Map<string, Contribution[]>();
  for (const { r, t } of entries) {
    for (const joint of r.anim.joints) {
      const rotation = sampleRotation(joint, t.time, r.anim.length);
      const position = samplePosition(joint, t.time, r.anim.length);
      if (!rotation && !position) continue;
      const list = byJoint.get(joint.name) || [];
      list.push({ priority: joint.priority, started: r.startedAt, rotation, position, weight: t.weight });
      byJoint.set(joint.name, list);
    }
  }
  const out = new Map<string, JointPose>();
  for (const [name, list] of byJoint) {
    list.sort((a, b) => b.priority - a.priority || b.started - a.started);
    let rotBudget = 1, posBudget = 1;
    const rot: Quat = [0, 0, 0, 0];
    const pos: Vec3 = [0, 0, 0];
    let hasPos = false;
    for (const c of list) {
      if (c.rotation && rotBudget > 0) {
        const w = Math.min(c.weight, rotBudget);
        // keep quaternions in one hemisphere so weighted sums do not cancel
        const dot = rot[0] * c.rotation[0] + rot[1] * c.rotation[1] + rot[2] * c.rotation[2] + rot[3] * c.rotation[3];
        const sign = dot < 0 ? -1 : 1;
        for (let i = 0; i < 4; i++) rot[i] += c.rotation[i] * w * sign;
        rotBudget -= w;
      }
      if (c.position && posBudget > 0) {
        const w = Math.min(c.weight, posBudget);
        for (let i = 0; i < 3; i++) pos[i] += c.position[i] * w;
        posBudget -= w;
        hasPos = true;
      }
    }
    const n = Math.hypot(rot[0], rot[1], rot[2], rot[3]);
    out.set(name, { rotation: n > 1e-9 ? [rot[0] / n, rot[1] / n, rot[2] / n, rot[3] / n] : [0, 0, 0, 1], position: hasPos ? pos : null });
  }
  return out;
}
