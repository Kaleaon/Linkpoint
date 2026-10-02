/**
 * Distance-Tiered SIMD Frustum Culling Engine
 *
 * Stores bounding boxes in contiguous 16-byte aligned Float32Array buffers with SOA
 * (Structure of Arrays) layout for 4-lane SIMD batch evaluations. Organizes objects
 * into 3 camera-distance tiers with hysteresis margins and multi-frequency pass execution.
 */

import { transformAABB, type Frustum } from './frustum';

export const NEAR_DISTANCE = 32; // metres
export const FAR_DISTANCE = 96;  // metres
export const HYSTERESIS_MARGIN = 5.0; // metres margin to prevent oscillation
export const LARGE_OBJECT_RADIUS = 20.0; // metres threshold for non-popin promotion
export const TELEPORT_THRESHOLD = 20.0; // metres threshold for camera teleport

export enum DistanceTier {
  Near = 0,
  Mid = 1,
  Far = 2,
}

export interface CullerObject {
  id: string;
  position: [number, number, number];
  rotation: [number, number, number, number]; // quaternion [x, y, z, w] or Euler
  scale: [number, number, number];
  localBounds: { min: [number, number, number]; max: [number, number, number] } | null;
  worldMin: [number, number, number];
  worldMax: [number, number, number];
  radius: number;
  tier: DistanceTier;
  distanceToCameraSq: number;
  dirtyTransform: boolean;
  alwaysVisible: boolean; // True when local bounds are missing or unknown
  visible: boolean; // Active culling state (true = drawn, false = culled)
}

function defaultModelMatrix(position: number[], rotation: number[], scale: number[]): Float32Array {
  const matrix = new Float32Array([
    1, 0, 0, 0,
    0, 1, 0, 0,
    0, 0, 1, 0,
    0, 0, 0, 1,
  ]);

  matrix[12] = position[0];
  matrix[13] = position[1];
  matrix[14] = position[2];

  const rx = rotation[0], ry = rotation[1], rz = rotation[2];
  if (rx !== 0 || ry !== 0 || rz !== 0) {
    const sx = Math.sin(rx), cx = Math.cos(rx);
    const sy = Math.sin(ry), cy = Math.cos(ry);
    const sz = Math.sin(rz), cz = Math.cos(rz);

    const m00 = cy * cz;
    const m01 = cy * sz;
    const m02 = -sy;

    const m10 = sx * sy * cz - cx * sz;
    const m11 = sx * sy * sz + cx * cz;
    const m12 = sx * cy;

    const m20 = cx * sy * cz + sx * sz;
    const m21 = cx * sy * sz - sx * cz;
    const m22 = cx * cy;

    matrix[0] = m00 * scale[0];
    matrix[1] = m01 * scale[0];
    matrix[2] = m02 * scale[0];

    matrix[4] = m10 * scale[1];
    matrix[5] = m11 * scale[1];
    matrix[6] = m12 * scale[1];

    matrix[8] = m20 * scale[2];
    matrix[9] = m21 * scale[2];
    matrix[10] = m22 * scale[2];
  } else {
    matrix[0] = scale[0];
    matrix[5] = scale[1];
    matrix[10] = scale[2];
  }

  return matrix;
}

/**
 * SOA (Structure of Arrays) 4-box batch buffer.
 * Each batch holds 4 bounding boxes across 24 floats (96 bytes), ensuring
 * 16-byte alignment boundaries for SIMD vector operations.
 */
export class SimdVectorBuffer {
  /** Float32Array buffer: 24 floats per batch of 4 boxes */
  public buffer: Float32Array;
  public capacityBatches: number;
  public activeBoxes: number = 0;

  constructor(initialCapacityBatches = 64) {
    this.capacityBatches = initialCapacityBatches;
    const floatCount = this.capacityBatches * 24;
    const arrayBuffer = new ArrayBuffer(floatCount * 4);
    if (arrayBuffer.byteLength % 16 !== 0) {
      throw new Error('SIMD vector buffer failed 16-byte alignment check');
    }
    this.buffer = new Float32Array(arrayBuffer);
  }

  public ensureCapacity(batchCount: number) {
    if (batchCount <= this.capacityBatches) return;
    let newCapacity = this.capacityBatches * 2;
    while (newCapacity < batchCount) newCapacity *= 2;
    const newArrayBuffer = new ArrayBuffer(newCapacity * 24 * 4);
    const newBuffer = new Float32Array(newArrayBuffer);
    newBuffer.set(this.buffer);
    this.buffer = newBuffer;
    this.capacityBatches = newCapacity;
  }

  /**
   * Set world bounding box coordinates for a box index in SOA layout.
   */
  public setBox(boxIndex: number, min: ArrayLike<number>, max: ArrayLike<number>) {
    const batch = Math.floor(boxIndex / 4);
    const lane = boxIndex % 4;
    this.ensureCapacity(batch + 1);

    const base = batch * 24;
    this.buffer[base + 0 + lane] = min[0];
    this.buffer[base + 4 + lane] = min[1];
    this.buffer[base + 8 + lane] = min[2];
    this.buffer[base + 12 + lane] = max[0];
    this.buffer[base + 16 + lane] = max[1];
    this.buffer[base + 20 + lane] = max[2];

    if (boxIndex >= this.activeBoxes) {
      this.activeBoxes = boxIndex + 1;
    }
  }

  /**
   * Batch evaluate frustum planes for 4 boxes in SIMD SOA layout.
   * Returns a 4-bit bitmask where bit i is 1 if box i is VISIBLE, 0 if CULLED.
   */
  public evaluateBatch(batchIndex: number, planes: ArrayLike<number>): number {
    const base = batchIndex * 24;
    const b = this.buffer;

    let visibilityMask = 0b1111; // Assume all 4 boxes visible initially

    for (let p = 0; p < 6; p++) {
      const planeBase = p * 4;
      const nx = planes[planeBase];
      const ny = planes[planeBase + 1];
      const nz = planes[planeBase + 2];
      const d = planes[planeBase + 3];

      // Lane 0
      const px0 = nx >= 0 ? b[base + 12] : b[base + 0];
      const py0 = ny >= 0 ? b[base + 16] : b[base + 4];
      const pz0 = nz >= 0 ? b[base + 20] : b[base + 8];
      if (nx * px0 + ny * py0 + nz * pz0 + d < 0) {
        visibilityMask &= ~0b0001;
      }

      // Lane 1
      const px1 = nx >= 0 ? b[base + 13] : b[base + 1];
      const py1 = ny >= 0 ? b[base + 17] : b[base + 5];
      const pz1 = nz >= 0 ? b[base + 21] : b[base + 9];
      if (nx * px1 + ny * py1 + nz * pz1 + d < 0) {
        visibilityMask &= ~0b0010;
      }

      // Lane 2
      const px2 = nx >= 0 ? b[base + 14] : b[base + 2];
      const py2 = ny >= 0 ? b[base + 18] : b[base + 6];
      const pz2 = nz >= 0 ? b[base + 22] : b[base + 10];
      if (nx * px2 + ny * py2 + nz * pz2 + d < 0) {
        visibilityMask &= ~0b0100;
      }

      // Lane 3
      const px3 = nx >= 0 ? b[base + 15] : b[base + 3];
      const py3 = ny >= 0 ? b[base + 19] : b[base + 7];
      const pz3 = nz >= 0 ? b[base + 23] : b[base + 11];
      if (nx * px3 + ny * py3 + nz * pz3 + d < 0) {
        visibilityMask &= ~0b1000;
      }

      if (visibilityMask === 0) break; // All 4 boxes culled by this plane
    }

    return visibilityMask;
  }
}

export class TieredSimdCuller {
  private objects: Map<string, CullerObject> = new Map();
  private objectList: CullerObject[] = [];
  private vectorBuffer: SimdVectorBuffer;

  private lastCameraPosition: [number, number, number] = [0, 0, 0];

  public frameStats = { drawn: 0, culled: 0 };

  constructor() {
    this.vectorBuffer = new SimdVectorBuffer(128);
  }

  /**
   * Register or update an object in the culler.
   */
  public upsertObject(
    id: string,
    position: [number, number, number],
    rotation: [number, number, number, number] | [number, number, number],
    scale: [number, number, number],
    localBounds: { min: ArrayLike<number>; max: ArrayLike<number> } | null,
    modelMatrixCalculator?: (pos: number[], rot: number[], sc: number[]) => ArrayLike<number>
  ) {
    let obj = this.objects.get(id);

    const rot4: [number, number, number, number] =
      rotation.length === 4
        ? (rotation as [number, number, number, number])
        : [rotation[0], rotation[1], rotation[2], 1.0];

    if (!obj) {
      obj = {
        id,
        position: [...position],
        rotation: rot4,
        scale: [...scale],
        localBounds: localBounds ? { min: [localBounds.min[0], localBounds.min[1], localBounds.min[2]], max: [localBounds.max[0], localBounds.max[1], localBounds.max[2]] } : null,
        worldMin: [0, 0, 0],
        worldMax: [0, 0, 0],
        radius: 0,
        tier: DistanceTier.Near,
        distanceToCameraSq: 0,
        dirtyTransform: true,
        alwaysVisible: !localBounds,
        visible: true,
      };
      this.objects.set(id, obj);
      this.objectList.push(obj);
    } else {
      obj.position = [...position];
      obj.rotation = rot4;
      obj.scale = [...scale];
      obj.localBounds = localBounds ? { min: [localBounds.min[0], localBounds.min[1], localBounds.min[2]], max: [localBounds.max[0], localBounds.max[1], localBounds.max[2]] } : null;
      obj.alwaysVisible = !localBounds;
      obj.dirtyTransform = true;
    }

    if (localBounds) {
      const calc = modelMatrixCalculator || defaultModelMatrix;
      const model = calc(obj.position, obj.rotation, obj.scale);
      const world = transformAABB(model, localBounds.min, localBounds.max);
      obj.worldMin = [world.min[0], world.min[1], world.min[2]];
      obj.worldMax = [world.max[0], world.max[1], world.max[2]];

      const dx = world.max[0] - world.min[0];
      const dy = world.max[1] - world.min[1];
      const dz = world.max[2] - world.min[2];
      obj.radius = Math.hypot(dx, dy, dz) / 2;
      obj.dirtyTransform = false;
    }
  }

  /**
   * Remove an object from the culler.
   */
  public removeObject(id: string) {
    if (this.objects.delete(id)) {
      this.objectList = this.objectList.filter((o) => o.id !== id);
    }
  }

  /**
   * Clear all objects.
   */
  public clear() {
    this.objects.clear();
    this.objectList = [];
    this.vectorBuffer = new SimdVectorBuffer(128);
  }

  /**
   * Update world bounds for objects whose transforms changed.
   */
  public updateWorldBounds(
    modelMatrixCalculator?: (pos: number[], rot: number[], sc: number[]) => ArrayLike<number>
  ) {
    const calc = modelMatrixCalculator || defaultModelMatrix;
    for (let i = 0; i < this.objectList.length; i++) {
      const obj = this.objectList[i];
      if (obj.dirtyTransform && obj.localBounds) {
        const model = calc(obj.position, obj.rotation, obj.scale);
        const world = transformAABB(model, obj.localBounds.min, obj.localBounds.max);
        obj.worldMin = [world.min[0], world.min[1], world.min[2]];
        obj.worldMax = [world.max[0], world.max[1], world.max[2]];

        const dx = world.max[0] - world.min[0];
        const dy = world.max[1] - world.min[1];
        const dz = world.max[2] - world.min[2];
        obj.radius = Math.hypot(dx, dy, dz) / 2;
        obj.dirtyTransform = false;
      }
    }
  }

  /**
   * Categorize objects into distance tiers centered around the active camera position.
   * Applies hysteresis thresholds to prevent oscillation between buckets when moving.
   * Detects rapid camera teleportation to force immediate re-binning.
   */
  private updateDistanceTiers(cameraPos: [number, number, number], forceRefresh = false) {
    const camX = cameraPos[0], camY = cameraPos[1], camZ = cameraPos[2];

    const teleDistSq =
      (camX - this.lastCameraPosition[0]) ** 2 +
      (camY - this.lastCameraPosition[1]) ** 2 +
      (camZ - this.lastCameraPosition[2]) ** 2;

    const isTeleport = forceRefresh || teleDistSq >= TELEPORT_THRESHOLD ** 2;
    this.lastCameraPosition = [camX, camY, camZ];

    const nearSq = NEAR_DISTANCE ** 2;
    const farSq = FAR_DISTANCE ** 2;

    for (let i = 0; i < this.objectList.length; i++) {
      const obj = this.objectList[i];
      const distSq =
        (obj.position[0] - camX) ** 2 +
        (obj.position[1] - camY) ** 2 +
        (obj.position[2] - camZ) ** 2;

      obj.distanceToCameraSq = distSq;

      // Large distant objects safeguard: promoted to Tier 0 to avoid pop-in
      if (obj.radius >= LARGE_OBJECT_RADIUS) {
        obj.tier = DistanceTier.Near;
        continue;
      }

      if (isTeleport) {
        if (distSq <= nearSq) {
          obj.tier = DistanceTier.Near;
        } else if (distSq <= farSq) {
          obj.tier = DistanceTier.Mid;
        } else {
          obj.tier = DistanceTier.Far;
        }
      } else {
        // Hysteresis boundary checks
        const dist = Math.sqrt(distSq);
        if (obj.tier === DistanceTier.Near) {
          if (dist > NEAR_DISTANCE + HYSTERESIS_MARGIN) {
            obj.tier = dist > FAR_DISTANCE ? DistanceTier.Far : DistanceTier.Mid;
          }
        } else if (obj.tier === DistanceTier.Mid) {
          if (dist < NEAR_DISTANCE - HYSTERESIS_MARGIN) {
            obj.tier = DistanceTier.Near;
          } else if (dist > FAR_DISTANCE + HYSTERESIS_MARGIN) {
            obj.tier = DistanceTier.Far;
          }
        } else {
          // DistanceTier.Far
          if (dist < FAR_DISTANCE - HYSTERESIS_MARGIN) {
            obj.tier = dist < NEAR_DISTANCE ? DistanceTier.Near : DistanceTier.Mid;
          }
        }
      }
    }

    return isTeleport;
  }

  /**
   * Execute frustum culling pass over object bounding boxes using SIMD vector buffers.
   * Multi-frequency execution: Tier 0 & 1 every frame, Tier 2 on alternating frames.
   */
  public cull(
    cameraPos: [number, number, number],
    frustum: Frustum | null,
    frameCount: number,
    modelMatrixCalculator?: (pos: number[], rot: number[], sc: number[]) => ArrayLike<number>
  ): { drawn: number; culled: number } {
    this.updateWorldBounds(modelMatrixCalculator);

    if (!frustum) {
      let drawn = 0;
      for (let i = 0; i < this.objectList.length; i++) {
        this.objectList[i].visible = true;
        drawn++;
      }
      this.frameStats = { drawn, culled: 0 };
      return this.frameStats;
    }

    const isTeleport = this.updateDistanceTiers(cameraPos);

    // Tier 2 is evaluated on alternating frames (50% frequency reduction),
    // OR immediately on camera teleport or forced refresh.
    const evaluateFarTier = isTeleport || frameCount % 2 === 0;

    const evaluatableObjects: CullerObject[] = [];

    for (let i = 0; i < this.objectList.length; i++) {
      const obj = this.objectList[i];
      if (obj.alwaysVisible) {
        obj.visible = true;
        continue;
      }

      if (obj.tier === DistanceTier.Near || obj.tier === DistanceTier.Mid) {
        evaluatableObjects.push(obj);
      } else if (evaluateFarTier) {
        evaluatableObjects.push(obj);
      }
    }

    const batchCount = Math.ceil(evaluatableObjects.length / 4);
    this.vectorBuffer.ensureCapacity(batchCount);

    for (let i = 0; i < evaluatableObjects.length; i++) {
      const obj = evaluatableObjects[i];
      this.vectorBuffer.setBox(i, obj.worldMin, obj.worldMax);
    }

    // Zero out unused lanes in the last batch if not a multiple of 4
    const remainder = evaluatableObjects.length % 4;
    if (remainder !== 0 && evaluatableObjects.length > 0) {
      const dummyMin = [0, 0, 0];
      const dummyMax = [0, 0, 0];
      for (let pad = evaluatableObjects.length; pad < batchCount * 4; pad++) {
        this.vectorBuffer.setBox(pad, dummyMin, dummyMax);
      }
    }

    // SIMD Batch evaluation loop
    for (let b = 0; b < batchCount; b++) {
      const mask = this.vectorBuffer.evaluateBatch(b, frustum);
      const startIdx = b * 4;
      for (let lane = 0; lane < 4; lane++) {
        const objIdx = startIdx + lane;
        if (objIdx < evaluatableObjects.length) {
          const isVisible = (mask & (1 << lane)) !== 0;
          evaluatableObjects[objIdx].visible = isVisible;
        }
      }
    }

    let drawn = 0;
    let culled = 0;
    for (let i = 0; i < this.objectList.length; i++) {
      if (this.objectList[i].visible) {
        drawn++;
      } else {
        culled++;
      }
    }

    this.frameStats = { drawn, culled };
    return this.frameStats;
  }

  public isCulled(id: string): boolean {
    const obj = this.objects.get(id);
    return obj ? !obj.visible : false;
  }

  public getObject(id: string): CullerObject | undefined {
    return this.objects.get(id);
  }

  public getObjects(): CullerObject[] {
    return this.objectList;
  }
}
