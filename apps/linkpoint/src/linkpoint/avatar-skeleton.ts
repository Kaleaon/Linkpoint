/**
 * Avatar skeleton, posing and rigged-mesh joint matrices.
 *
 * Bone hierarchy and rest offsets are data extracted from Lumiya's recovered
 * skeleton (scripts/extract-lumiya-skeleton.py); the math here is new.
 * Matrices are column-major 4x4 (WebGL convention), applied to column vectors.
 */
import skeletonData from './avatar-data/skeleton.json';
import type { JointPose, Quat, Vec3 } from './avatar-animation';

export type Mat4 = Float32Array;

interface BoneData {
  position: number[];
  basePosition: number[];
  children: string[];
  collision: string[];
  isJoint: boolean;
  extended: boolean;
  animIndex: number;
  scale?: number[];
}

const DATA = skeletonData as unknown as { root: string; aliases: Record<string, string>; bones: Record<string, BoneData> };

export const identity = (): Mat4 => new Float32Array([1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1]);

export function multiply(a: ArrayLike<number>, b: ArrayLike<number>, out: Mat4 = new Float32Array(16)): Mat4 {
  const r = new Float32Array(16);
  for (let col = 0; col < 4; col++) for (let row = 0; row < 4; row++) {
    let sum = 0;
    for (let k = 0; k < 4; k++) sum += a[k * 4 + row] * b[col * 4 + k];
    r[col * 4 + row] = sum;
  }
  out.set(r);
  return out;
}

/** Rigid transform: rotation by unit quaternion q, then translation t. */
export function compose(t: Vec3, q: Quat = [0, 0, 0, 1]): Mat4 {
  const [x, y, z, w] = q;
  const m = identity();
  m[0] = 1 - 2 * (y * y + z * z); m[1] = 2 * (x * y + z * w); m[2] = 2 * (x * z - y * w);
  m[4] = 2 * (x * y - z * w); m[5] = 1 - 2 * (x * x + z * z); m[6] = 2 * (y * z + x * w);
  m[8] = 2 * (x * z + y * w); m[9] = 2 * (y * z - x * w); m[10] = 1 - 2 * (x * x + y * y);
  m[12] = t[0]; m[13] = t[1]; m[14] = t[2];
  return m;
}

export interface SkeletonBone {
  name: string;
  parent: string | null;
  /** Index into the pose/world arrays (depth-first, parents before children). */
  index: number;
  parentIndex: number;
  /** Local rest offset from the parent. */
  rest: Vec3;
  /** Joints take part in animation; collision volumes and attachment points follow their parent. */
  joint: boolean;
  scale: Vec3;
}

export class AvatarSkeleton {
  readonly bones: SkeletonBone[] = [];
  private readonly byName = new Map<string, number>();

  constructor() {
    const visit = (name: string, parent: string | null, parentIndex: number) => {
      const data = DATA.bones[name];
      const bone: SkeletonBone = {
        name, parent, parentIndex, index: this.bones.length,
        rest: (data.isJoint ? data.basePosition : data.position) as Vec3,
        joint: data.isJoint,
        scale: (data.scale || [1, 1, 1]) as Vec3,
      };
      this.bones.push(bone);
      this.byName.set(name, bone.index);
      for (const child of [...data.children, ...data.collision]) visit(child, name, bone.index);
    };
    visit(DATA.root, null, -1);
  }

  /**
   * Rest position of a bone in avatar space using the skeleton's default (undeformed) offsets;
   * the base avatar meshes are authored against these.
   */
  defaultPosition(name: string): Vec3 {
    let index = this.indexOf(name);
    if (index < 0) return [0, 0, 0];
    const out: Vec3 = [0, 0, 0];
    while (index >= 0) {
      const data = DATA.bones[this.bones[index].name];
      out[0] += data.position[0]; out[1] += data.position[1]; out[2] += data.position[2];
      index = this.bones[index].parentIndex;
    }
    return out;
  }

  /** Resolve a bone name or any legacy alias ("hip", "lShldr", "avatar_mPelvis"). */
  resolve(name: string): string | null {
    if (this.byName.has(name)) return name;
    return DATA.aliases[name] && this.byName.has(DATA.aliases[name]) ? DATA.aliases[name] : null;
  }

  indexOf(name: string): number {
    const resolved = this.resolve(name);
    return resolved === null ? -1 : this.byName.get(resolved)!;
  }

  /**
   * World (avatar-space) matrix of every bone for a pose keyed by joint name or alias.
   * A joint's animated position replaces its rest offset, except for the pelvis,
   * whose animated position is an offset from rest. `positionOverrides` (see
   * `jointPositionOverrides`) replace local rest positions before animation is applied.
   */
  worldMatrices(pose: Map<string, JointPose> = new Map(), positionOverrides: Map<string, Vec3> = new Map(), pelvisOffset: Vec3 = [0, 0, 0]): Mat4[] {
    const resolved = new Map<number, JointPose>();
    for (const [name, joint] of pose) {
      const index = this.indexOf(name);
      if (index >= 0 && this.bones[index].joint) resolved.set(index, joint);
    }
    const world: Mat4[] = new Array(this.bones.length);
    for (const bone of this.bones) {
      const joint = resolved.get(bone.index);
      let position: Vec3 = positionOverrides.get(bone.name) || bone.rest;
      if (bone.parent === null) position = [position[0] + pelvisOffset[0], position[1] + pelvisOffset[1], position[2] + pelvisOffset[2]];
      if (joint?.position) {
        position = bone.parent === null
          ? [position[0] + joint.position[0], position[1] + joint.position[1], position[2] + joint.position[2]]
          : joint.position;
      }
      const local = compose(position, joint?.rotation);
      world[bone.index] = bone.parentIndex < 0 ? local : multiply(world[bone.parentIndex], local);
    }
    return world;
  }
}

/** Rigged-mesh skin block as decoded from an LLMesh asset (matrices are SL's 16-float row-vector layout). */
export interface MeshSkin {
  jointNames: string[];
  bindShapeMatrix: ArrayLike<number> | null;
  inverseBindMatrices: ArrayLike<number>[];
  /** Bento / modern rigging: per-joint alternate inverse bind; its translation is the joint's local position override. */
  alternateInverseBindMatrices?: ArrayLike<number>[];
  /** Extra offset applied to the pelvis when joint position overrides are in effect. */
  pelvisOffset?: ArrayLike<number> | null;
  lockScaleIfJointPosition?: boolean;
}

/** Overrides apply only when every joint has an alternate bind matrix (the viewer ignores mismatched counts). */
export function hasJointOverrides(skin: MeshSkin): boolean {
  const alternates = skin.alternateInverseBindMatrices || [];
  return alternates.length > 0 && alternates.length === skin.jointNames.length;
}

/** Joint position changes smaller than this (metres) are treated as the default (the viewer's 0.1 mm threshold). */
export const JOINT_POSITION_THRESHOLD = 0.0001;

/**
 * Joint position overrides requested by a rigged mesh: the translation of each alternate inverse
 * bind matrix replaces that joint's local rest position (this is how modern SL meshes, including
 * Bento and Animesh, reshape the skeleton). Row-vector storage keeps translation at indices 12-14.
 * Mirrors the official viewer (LLVOAvatar::addAttachmentOverridesForObject): the counts must match
 * and only positions further than 0.1 mm from the joint's default count.
 */
export function jointPositionOverrides(skeleton: AvatarSkeleton, skin: MeshSkin): Map<string, Vec3> {
  const overrides = new Map<string, Vec3>();
  if (!hasJointOverrides(skin)) return overrides;
  const alternates = skin.alternateInverseBindMatrices!;
  skin.jointNames.forEach((name, i) => {
    const m = alternates[i];
    const bone = skeleton.resolve(name);
    if (!m || m.length !== 16 || !bone) return;
    const position: Vec3 = [m[12], m[13], m[14]];
    if (!position.every((v) => Number.isFinite(v))) return;
    const rest = skeleton.bones[skeleton.indexOf(bone)].rest;
    if (Math.hypot(position[0] - rest[0], position[1] - rest[1], position[2] - rest[2]) > JOINT_POSITION_THRESHOLD) overrides.set(bone, position);
  });
  return overrides;
}

/**
 * Per-joint skinning matrices for a rigged mesh: world * inverseBind * bindShape, one per entry of
 * `skin.jointNames`. A position `v` skins as `sum(weight_i * M_i * v)`. SL stores matrices in a
 * row-vector layout, which read as column-major is exactly the column-vector form, so no transpose
 * is needed. Joints the skeleton does not know are treated as mPelvis, as the viewer does.
 */
export function skinMatrices(skeleton: AvatarSkeleton, skin: MeshSkin, world: Mat4[]): Mat4[] {
  const bind = skin.bindShapeMatrix && skin.bindShapeMatrix.length === 16 ? Float32Array.from(skin.bindShapeMatrix) : identity();
  return skin.jointNames.map((name, i) => {
    // The viewer rewrites joints it does not know to mPelvis (LLSkinningUtil::scrubInvalidJoints).
    const found = skeleton.indexOf(name);
    const index = found >= 0 ? found : 0; // the skeleton root is mPelvis
    const inverse = skin.inverseBindMatrices[i];
    if (!inverse || inverse.length !== 16) return multiply(identity(), bind);
    return multiply(multiply(world[index], Float32Array.from(inverse)), bind);
  });
}

/** CPU skinning of one vertex by up to four weighted joints (reference implementation / tests / fallback). */
export function skinPoint(point: Vec3, joints: ArrayLike<number>, weights: ArrayLike<number>, matrices: Mat4[]): Vec3 {
  const out: Vec3 = [0, 0, 0];
  let total = 0;
  for (let i = 0; i < 4; i++) {
    const w = weights[i];
    const m = matrices[joints[i]];
    if (!(w > 0) || !m) continue;
    total += w;
    out[0] += w * (m[0] * point[0] + m[4] * point[1] + m[8] * point[2] + m[12]);
    out[1] += w * (m[1] * point[0] + m[5] * point[1] + m[9] * point[2] + m[13]);
    out[2] += w * (m[2] * point[0] + m[6] * point[1] + m[10] * point[2] + m[14]);
  }
  return total > 0 ? out : point;
}
