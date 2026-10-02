import { describe, expect, it } from 'vitest';
import { animationTiming, blendAnimations, parseAnimation, sampleRotation, samplePosition, unpackQuaternion, type KeyframeAnimation } from '../avatar-animation';

function build(opts: { length: number; loop?: boolean; joints: { name: string; priority: number; rot: [number, number[]][]; pos: [number, number[]][] }[] }): Uint8Array {
  const out: number[] = [];
  const dv = new DataView(new ArrayBuffer(4));
  const i32 = (v: number) => { dv.setInt32(0, v, true); out.push(...new Uint8Array(dv.buffer)); };
  const f32 = (v: number) => { dv.setFloat32(0, v, true); out.push(...new Uint8Array(dv.buffer)); };
  const u16 = (v: number) => { out.push(v & 255, v >> 8); };
  const str = (s: string) => { for (const c of s) out.push(c.charCodeAt(0)); out.push(0); };
  out.push(1, 0, 0, 0);
  i32(3); f32(opts.length); str('');
  f32(0); f32(opts.length); i32(opts.loop ? 1 : 0); f32(0.25); f32(0.25); i32(0);
  i32(opts.joints.length);
  for (const j of opts.joints) {
    str(j.name); i32(j.priority);
    i32(j.rot.length);
    for (const [t, q] of j.rot) { u16(Math.round((t / opts.length) * 65535)); for (const c of q) u16(Math.round(((c + 1) / 2) * 65535)); }
    i32(j.pos.length);
    for (const [t, p] of j.pos) { u16(Math.round((t / opts.length) * 65535)); for (const c of p) u16(Math.round(((c + 5) / 10) * 65535)); }
  }
  return new Uint8Array(out);
}

describe('avatar animation', () => {
  it('parses header, joints and dequantised keyframes', () => {
    const anim = parseAnimation(build({ length: 2, loop: true, joints: [{ name: 'mPelvis', priority: 4, rot: [[0, [0, 0, 0]], [1, [0.5, 0, 0]]], pos: [[0, [1, 2, -3]]] }] }));
    expect(anim.priority).toBe(3);
    expect(anim.length).toBeCloseTo(2);
    expect(anim.loop).toBe(true);
    expect(anim.easeIn).toBeCloseTo(0.25);
    expect(anim.joints).toHaveLength(1);
    const [joint] = anim.joints;
    expect(joint.name).toBe('mPelvis');
    expect(joint.rotations[1].time).toBeCloseTo(1, 3);
    expect(joint.rotations[1].value[0]).toBeCloseTo(0.5, 3);
    expect(joint.rotations[1].value[3]).toBeCloseTo(Math.sqrt(0.75), 3);
    expect(joint.positions[0].value[1]).toBeCloseTo(2, 3);
    expect(joint.positions[0].value[2]).toBeCloseTo(-3, 3);
  });

  it('rejects truncated data', () => {
    expect(() => parseAnimation(build({ length: 1, joints: [{ name: 'a', priority: 0, rot: [[0, [0, 0, 0]]], pos: [] }] }).subarray(0, 40))).toThrow();
  });

  it('keeps negative quantised values signed', () => {
    const anim = parseAnimation(build({ length: 1, joints: [{ name: 'a', priority: 0, rot: [], pos: [[0, [-4.5, -0.5, 4.5]]] }] }));
    expect(anim.joints[0].positions[0].value[0]).toBeCloseTo(-4.5, 3);
    expect(anim.joints[0].positions[0].value[1]).toBeCloseTo(-0.5, 3);
  });

  it('unpacks a unit quaternion and clamps an over-long vector', () => {
    expect(unpackQuaternion(0, 0, 0)).toEqual([0, 0, 0, 1]);
    expect(unpackQuaternion(1, 1, 0)[3]).toBe(0);
  });

  it('interpolates, holds ends, and wraps to the first key', () => {
    const anim = parseAnimation(build({ length: 2, joints: [{ name: 'a', priority: 0, rot: [[0.5, [0, 0, 0]], [1.5, [0.6, 0, 0]]], pos: [[0.5, [0, 0, 0]], [1.5, [2, 0, 0]]] }] }));
    const j = anim.joints[0];
    expect(samplePosition(j, 1, 2)![0]).toBeCloseTo(1, 2);
    expect(samplePosition(j, 0.1, 2)![0]).toBeCloseTo(0, 2);
    expect(samplePosition(j, 1.9, 2)![0]).toBeCloseTo(2, 2);
    const mid = sampleRotation(j, 1, 2)!;
    expect(Math.hypot(...mid)).toBeCloseTo(1, 5);
    expect(mid[0]).toBeGreaterThan(0.2);
  });

  const anim = (over: Partial<KeyframeAnimation>): KeyframeAnimation => ({ priority: 1, length: 2, expression: '', inPoint: 0.5, outPoint: 1.5, loop: true, easeIn: 0.5, easeOut: 0.5, handPose: 0, joints: [], ...over });

  it('loops between in/out points and eases in', () => {
    const a = anim({});
    expect(animationTiming(a, 0.25).weight).toBeCloseTo(0.5, 5);
    expect(animationTiming(a, 0.25).time).toBeCloseTo(0.25);
    expect(animationTiming(a, 1.75).time).toBeCloseTo(0.75);
    expect(animationTiming(a, 10).weight).toBe(1);
  });

  it('eases out after stop and ends non-looping animations at their length', () => {
    const a = anim({ loop: false });
    expect(animationTiming(a, 5).time).toBe(2);
    expect(animationTiming(a, 1.75).weight).toBeCloseTo(0.5, 5);
    expect(animationTiming(anim({}), 3, 0.25).weight).toBeCloseTo(0.5, 5);
    expect(animationTiming(anim({ easeOut: 0 }), 3, 0.1).weight).toBe(0);
  });

  it('lets the higher priority animation own a joint', () => {
    const lo = parseAnimation(build({ length: 1, loop: true, joints: [{ name: 'mHead', priority: 1, rot: [[0, [0, 0, 0]]], pos: [] }] }));
    const hi = parseAnimation(build({ length: 1, loop: true, joints: [{ name: 'mHead', priority: 4, rot: [[0, [0.7071, 0, 0]]], pos: [] }] }));
    const pose = blendAnimations([{ anim: lo, startedAt: 0 }, { anim: hi, startedAt: 0 }], 5).get('mHead')!;
    expect(pose.rotation[0]).toBeCloseTo(0.7071, 2);
    expect(Math.hypot(...pose.rotation)).toBeCloseTo(1, 5);
  });
});
