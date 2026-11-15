import { readFileSync } from 'node:fs';
import { describe, expect, it } from 'vitest';
import { AvatarSkeleton } from '../avatar-skeleton';
import { skinPoint } from '../avatar-skeleton';
import { bodyPartRows, bodyPartSkin, bodyPartVertexSkin, loadBodyParts, parseBodyPart, type BodyPartMeta } from '../avatar-body';
import { skinMatrices } from '../avatar-skeleton';
import type { JointPose } from '../avatar-animation';

const dir = `${__dirname}/../../../public/avatar/`;
const meta = JSON.parse(readFileSync(`${dir}meshes.json`, 'utf8')) as Record<string, BodyPartMeta>;
const bytes = (part: string) => { const b = Buffer.from(readFileSync(`${dir}${part}.bin.base64`, 'utf8').trim(), 'base64'); return b.buffer.slice(b.byteOffset, b.byteOffset + b.byteLength) as ArrayBuffer; };
const skeleton = new AvatarSkeleton();

function skinned(part: string, pose?: Map<string, JointPose>, rigidJoint?: string) {
  const geometry = parseBodyPart(bytes(part), meta[part]);
  const skin = bodyPartSkin(skeleton, geometry, rigidJoint);
  const matrices = skinMatrices(skeleton, skin, skeleton.worldMatrices(pose));
  const { joints, weights } = bodyPartVertexSkin(geometry);
  const points: number[][] = [];
  for (let v = 0; v < meta[part].vertexCount; v++) {
    points.push(skinPoint([geometry.vertices[v * 3], geometry.vertices[v * 3 + 1], geometry.vertices[v * 3 + 2]], joints.slice(v * 4, v * 4 + 4), weights.slice(v * 4, v * 4 + 4), matrices));
  }
  return { geometry, points };
}
const bounds = (points: number[][], axis: number) => [Math.min(...points.map((p) => p[axis])), Math.max(...points.map((p) => p[axis]))];
const mean = (points: number[][], axis: number) => points.reduce((s, p) => s + p[axis], 0) / points.length;

describe('base avatar body meshes', () => {
  it('parses every part with valid indices, joints and weights', () => {
    for (const part of Object.keys(meta)) {
      const g = parseBodyPart(bytes(part), meta[part]);
      expect(g.vertices.length, part).toBe(meta[part].vertexCount * 3);
      expect(Math.max(...g.indices), part).toBeLessThan(meta[part].vertexCount);
      for (let v = 0; v < meta[part].vertexCount; v++) {
        const w = g.jointWeights.slice(v * 4, v * 4 + 4);
        expect(w[0] + w[1] + w[2] + w[3]).toBeCloseTo(1, 4);
        for (let k = 0; k < 4; k++) expect(g.joints[v * 4 + k]).toBeLessThan(Math.max(1, g.jointNames.length));
      }
      for (const name of g.jointNames) expect(skeleton.indexOf(name), `${part}:${name}`).toBeGreaterThanOrEqual(0);
    }
  });

  it('rejects truncated mesh data', () => {
    expect(() => parseBodyPart(bytes('head').slice(0, 100), meta.head)).toThrow('expected');
  });

  it('reproduces the authored geometry in the rest pose (head above torso above legs, ~1.9 m tall)', () => {
    const head = skinned('head').points, upper = skinned('upperBody').points, lower = skinned('lowerBody').points;
    expect(bounds(head, 2)[1]).toBeGreaterThan(1.8);
    expect(bounds(head, 2)[1]).toBeLessThan(1.95);
    expect(mean(head, 2)).toBeGreaterThan(mean(upper, 2));
    expect(mean(upper, 2)).toBeGreaterThan(mean(lower, 2));
    expect(bounds(lower, 2)[0]).toBeLessThan(0.1); // feet near the ground
  });

  it('moves the body with its joints: a torso turn swings the arms, and the legs stay put', () => {
    const restUpper = skinned('upperBody').points;
    const restLower = skinned('lowerBody').points;
    const turn: JointPose = { rotation: [0, 0, Math.SQRT1_2, Math.SQRT1_2], position: null };
    const posed = new Map<string, JointPose>([['mTorso', turn]]);
    const upper = skinned('upperBody', posed).points;
    const lower = skinned('lowerBody', posed).points;
    // an arm tip (largest |y|) ends up on the x axis after a 90° turn about Z
    const tip = restUpper.reduce((best, p, i) => (Math.abs(p[1]) > Math.abs(restUpper[best][1]) ? i : best), 0);
    expect(Math.abs(upper[tip][0])).toBeGreaterThan(0.5);
    expect(Math.abs(upper[tip][1])).toBeLessThan(0.3);
    // legs hang from the hips: unaffected by the torso
    let moved = 0;
    for (let i = 0; i < lower.length; i++) moved = Math.max(moved, Math.hypot(lower[i][0] - restLower[i][0], lower[i][1] - restLower[i][1], lower[i][2] - restLower[i][2]));
    expect(moved).toBeLessThan(0.15); // only the waist seam may blend with the torso
  });

  it('seats the rigid eyes at their joints', () => {
    for (const joint of ['mEyeLeft', 'mEyeRight']) {
      const eye = skinned('eye', undefined, joint).points;
      const world = skeleton.worldMatrices()[skeleton.indexOf(joint)];
      expect(mean(eye, 0)).toBeCloseTo(world[12], 1);
      expect(mean(eye, 1)).toBeCloseTo(world[13], 1);
      expect(mean(eye, 2)).toBeCloseTo(world[14], 1);
    }
  });

  it('produces uniform rows sized to the GPU joint budget', () => {
    const g = parseBodyPart(bytes('upperBody'), meta.upperBody);
    const rows = bodyPartRows(skeleton, bodyPartSkin(skeleton, g), skeleton.worldMatrices(), 110);
    expect(rows).toHaveLength(110 * 12);
  });

  it('writes into provided target buffer when bodyPartRows is called', () => {
    const g = parseBodyPart(bytes('upperBody'), meta.upperBody);
    const target = new Float32Array(110 * 12);
    target.fill(1234);
    const rows = bodyPartRows(skeleton, bodyPartSkin(skeleton, g), skeleton.worldMatrices(), 110, target);
    expect(rows).toBe(target);
    expect(rows[0]).not.toBe(1234);
  });

  it('loads all parts through a fetcher', async () => {
    const fetcher = (async (url: string) => {
      const file = url.replace('/avatar/', '');
      const body = file === 'meshes.json' ? readFileSync(dir + file) : readFileSync(dir + file);
      const arrayBuffer = async () => body.buffer.slice(body.byteOffset, body.byteOffset + body.byteLength);
      return { ok: true, status: 200, json: async () => JSON.parse(body.toString('utf8')), text: async () => body.toString('utf8'), arrayBuffer };
    }) as unknown as typeof fetch;
    const parts = await loadBodyParts('/avatar/', fetcher);
    expect([...parts.keys()].sort()).toEqual(Object.keys(meta).sort());
    await expect(loadBodyParts('/avatar/', (async () => ({ ok: false, status: 404 })) as unknown as typeof fetch)).rejects.toThrow('404');
  });
});
