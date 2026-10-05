/**
 * Linkpoint - Coordinate Normalizer
 * 
 * Normalizes 3D spatial coordinates between region-local vectors (0 to 256 meters)
 * and global grid coordinates for SL and OpenSim worlds.
 */

export interface RegionOrigin {
  x?: number | null;
  y?: number | null;
}

export type Vector3Tuple = [number, number, number];

export class CoordinateNormalizer {
  public static readonly DEFAULT_REGION_SIZE = 256;

  /** Normalize region coordinate value (converting meter coordinates >= 25600 to tile index if needed). */
  public static normalizeRegionTileCoordinate(value: number | string | null | undefined): number {
    const val = Number(value);
    if (!Number.isFinite(val)) return 0;
    return val >= 25600 ? Math.floor(val / 256) : Math.floor(val);
  }

  /** Calculate region origin in global meters given region grid tile or meter coordinates. */
  public static getRegionOriginMeters(origin?: RegionOrigin | null): [number, number] {
    if (!origin) return [0, 0];
    const rawX = Number(origin.x ?? 0);
    const rawY = Number(origin.y ?? 0);

    const originX = rawX >= 25600 ? Math.floor(rawX / 256) * 256 : rawX * 256;
    const originY = rawY >= 25600 ? Math.floor(rawY / 256) * 256 : rawY * 256;

    return [originX, originY];
  }

  /**
   * Parse a raw input position (tuple, object, or string format) into a [x, y, z] vector.
   */
  public static parseVector(raw: any): Vector3Tuple | null {
    if (!raw) return null;
    if (Array.isArray(raw)) {
      const x = Number(raw[0]);
      const y = Number(raw[1]);
      const z = Number(raw[2] ?? 0);
      if (Number.isFinite(x) && Number.isFinite(y)) {
        return [x, y, Number.isFinite(z) ? z : 0];
      }
      return null;
    }
    if (typeof raw === 'object') {
      const x = Number(raw.x ?? raw.X);
      const y = Number(raw.y ?? raw.Y);
      const z = Number(raw.z ?? raw.Z ?? 0);
      if (Number.isFinite(x) && Number.isFinite(y)) {
        return [x, y, Number.isFinite(z) ? z : 0];
      }
      return null;
    }
    if (typeof raw === 'string') {
      const parts = raw.replace(/[<>[\]()]/g, '').split(',').map((s) => Number(s.trim()));
      if (parts.length >= 2 && Number.isFinite(parts[0]) && Number.isFinite(parts[1])) {
        return [parts[0], parts[1], Number.isFinite(parts[2]) ? parts[2] : 0];
      }
    }
    return null;
  }

  /**
   * Translates a spatial position (global grid coordinate or region vector)
   * into a region-local vector normalized to region-local origin (0 to 256 meters).
   */
  public static globalToRegionLocal(
    position: any,
    regionOrigin?: RegionOrigin | null,
    regionSize: number = CoordinateNormalizer.DEFAULT_REGION_SIZE
  ): Vector3Tuple {
    const vec = CoordinateNormalizer.parseVector(position);
    if (!vec) return [0, 0, 0];

    const [originX, originY] = CoordinateNormalizer.getRegionOriginMeters(regionOrigin);
    let [x, y, z] = vec;

    // If coordinate is in global meters, translate relative to region origin
    if (x >= originX && originX > 0) {
      x = x - originX;
    } else if (x > regionSize && originX === 0) {
      x = x % regionSize;
    }

    if (y >= originY && originY > 0) {
      y = y - originY;
    } else if (y > regionSize && originX === 0) {
      y = y % regionSize;
    }

    return CoordinateNormalizer.clampToRegionBounds([x, y, z], [regionSize, regionSize]);
  }

  /**
   * Converts a region-local vector (0 to 256 meters) to global grid coordinates in meters.
   */
  public static regionLocalToGlobal(
    localPos: any,
    regionOrigin?: RegionOrigin | null
  ): Vector3Tuple {
    const vec = CoordinateNormalizer.parseVector(localPos) || [0, 0, 0];
    const [originX, originY] = CoordinateNormalizer.getRegionOriginMeters(regionOrigin);

    return [originX + vec[0], originY + vec[1], vec[2]];
  }

  /**
   * Clamps a 3D position vector within 0 to maxBounds (default 256m) for X and Y.
   */
  public static clampToRegionBounds(
    pos: Vector3Tuple,
    bounds: [number, number] = [CoordinateNormalizer.DEFAULT_REGION_SIZE, CoordinateNormalizer.DEFAULT_REGION_SIZE]
  ): Vector3Tuple {
    const clampedX = Math.max(0, Math.min(bounds[0], pos[0]));
    const clampedY = Math.max(0, Math.min(bounds[1], pos[1]));
    const clampedZ = Number.isFinite(pos[2]) ? pos[2] : 0;
    return [clampedX, clampedY, clampedZ];
  }
}
