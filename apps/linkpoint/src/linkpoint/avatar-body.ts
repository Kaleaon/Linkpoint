/**
 * The base avatar body (head, torso, legs, eyes, hair...) as skinned meshes.
 *
 * Geometry comes from Lumiya's recovered character data, converted by
 * scripts/extract-lumiya-avatar-meshes.py into base64 text resources plus meshes.json.
 * Shape sliders (morph targets) are not applied yet: every avatar uses the default shape.
 */
import { AvatarSkeleton, compose, skinMatrices, type Mat4, type MeshSkin } from './avatar-skeleton';
import { packJointRows } from './skinning';
import { assetBase } from './avatar-animator';

export interface BodyPartMeta { vertexCount: number; faceCount: number; hasWeights: boolean; jointNames: string[] }
export interface BodyPartGeometry {
  vertices: number[];
  normals: number[];
  texCoords: number[];
  indices: number[];
  /** Four joint indices / weights per vertex, indexing into `jointNames`. */
  joints: number[];
  jointWeights: number[];
  jointNames: string[];
  /** Eyes are rigid and authored around the origin: they ride their joint instead of using an inverse bind. */
  rigid: boolean;
}

/** Parts drawn for a default avatar, with the baked-texture slot each one wears and a plain fallback colour. */
export const BODY_PARTS: Array<{ part: string; instance: string; bake: 'head' | 'upper' | 'lower' | 'eyes' | 'hair'; color: [number, number, number, number]; rigidJoint?: string }> = [
  { part: 'upperBody', instance: 'upperBody', bake: 'upper', color: [0.82, 0.62, 0.48, 1] },
  { part: 'lowerBody', instance: 'lowerBody', bake: 'lower', color: [0.82, 0.62, 0.48, 1] },
  { part: 'head', instance: 'head', bake: 'head', color: [0.82, 0.62, 0.48, 1] },
  { part: 'eyelashes', instance: 'eyelashes', bake: 'head', color: [0.12, 0.08, 0.06, 1] },
  { part: 'eye', instance: 'eyeLeft', bake: 'eyes', color: [0.95, 0.95, 0.95, 1], rigidJoint: 'mEyeLeft' },
  { part: 'eye', instance: 'eyeRight', bake: 'eyes', color: [0.95, 0.95, 0.95, 1], rigidJoint: 'mEyeRight' },
  { part: 'hair', instance: 'hair', bake: 'hair', color: [0.28, 0.2, 0.14, 1] },
];

/** Decode one `<part>.bin` (little-endian: position, normal, uv, weight, uint16 index). */
export function parseBodyPart(buffer: ArrayBuffer, meta: BodyPartMeta): BodyPartGeometry {
  const n = meta.vertexCount;
  const expected = n * 9 * 4 + meta.faceCount * 3 * 2;
  if (buffer.byteLength !== expected) throw new Error(`Avatar mesh has ${buffer.byteLength} bytes, expected ${expected}`);
  const floats = new Float32Array(buffer, 0, n * 9);
  const indices = Array.from(new Uint16Array(buffer.slice(n * 9 * 4)));
  if (indices.some((index) => index >= n)) throw new Error('Avatar mesh index out of range');
  const vertices = Array.from(floats.subarray(0, n * 3));
  const normals = Array.from(floats.subarray(n * 3, n * 6));
  const texCoords = Array.from(floats.subarray(n * 6, n * 8));
  const weights = floats.subarray(n * 8, n * 9);
  const joints: number[] = [], jointWeights: number[] = [];
  const lastJoint = meta.jointNames.length - 1;
  for (let v = 0; v < n; v++) {
    if (!meta.hasWeights || lastJoint < 0) { joints.push(0, 0, 0, 0); jointWeights.push(1, 0, 0, 0); continue; }
    // Blend weight = (joint index + 1) + fraction: skin between that joint and the next one.
    const w = weights[v];
    const base = Math.floor(w);
    const fraction = w - base;
    const first = Math.min(Math.max(base - 1, 0), lastJoint);
    const second = Math.min(first + 1, lastJoint);
    joints.push(first, second, 0, 0);
    jointWeights.push(second === first ? 1 : 1 - fraction, second === first ? 0 : fraction, 0, 0);
  }
  return { vertices, normals, texCoords, indices, joints, jointWeights, jointNames: meta.jointNames, rigid: !meta.hasWeights };
}

/** Skin description for a body part: the joint list and, for weighted parts, inverse binds that undo each joint's rest position. */
export function bodyPartSkin(skeleton: AvatarSkeleton, geometry: BodyPartGeometry, rigidJoint?: string): MeshSkin {
  if (geometry.rigid) {
    return { jointNames: [rigidJoint || 'mPelvis'], bindShapeMatrix: null, inverseBindMatrices: [compose([0, 0, 0])] };
  }
  return {
    jointNames: geometry.jointNames,
    bindShapeMatrix: null,
    inverseBindMatrices: geometry.jointNames.map((name) => {
      const [x, y, z] = skeleton.defaultPosition(name);
      return compose([-x, -y, -z]);
    }),
  };
}

/** Rigid parts reference a single joint; shift their per-vertex joint indices to it. */
export function bodyPartVertexSkin(geometry: BodyPartGeometry) {
  return geometry.rigid
    ? { joints: geometry.joints.map(() => 0), weights: geometry.jointWeights.map((_, i) => (i % 4 === 0 ? 1 : 0)) }
    : { joints: geometry.joints, weights: geometry.jointWeights };
}

/** Packed uniform rows for one part given the avatar's current world joint matrices. */
export function bodyPartRows(skeleton: AvatarSkeleton, skin: MeshSkin, world: Mat4[], maxJoints: number, target?: Float32Array): Float32Array {
  return packJointRows(skinMatrices(skeleton, skin, world), maxJoints, target);
}

export async function loadBodyParts(baseUrl = `${assetBase()}avatar/`, fetcher: typeof fetch = (input, init) => fetch(input, init)): Promise<Map<string, BodyPartGeometry>> {
  const metaResponse = await fetcher(`${baseUrl}meshes.json`);
  if (!metaResponse.ok) throw new Error(`Avatar mesh index unavailable (HTTP ${metaResponse.status})`);
  let meta: Record<string, BodyPartMeta>;
  try { meta = (await metaResponse.json()) as Record<string, BodyPartMeta>; }
  catch { throw new Error(`Avatar mesh index at ${baseUrl}meshes.json is not JSON (is public/avatar deployed?)`); }
  const parts = new Map<string, BodyPartGeometry>();
  await Promise.all(Object.keys(meta).map(async (part) => {
    const response = await fetcher(`${baseUrl}${part}.bin.base64`);
    if (!response.ok) throw new Error(`Avatar mesh ${part} unavailable (HTTP ${response.status})`);
    try {
      const encoded = (await response.text()).replace(/\s/g, '');
      const bytes = Uint8Array.from(atob(encoded), (character) => character.charCodeAt(0));
      parts.set(part, parseBodyPart(bytes.buffer, meta[part]));
    }
    catch (error) { throw new Error(`Avatar mesh ${baseUrl}${part}.bin.base64 is invalid: ${(error as Error).message}`); }
  }));
  return parts;
}
