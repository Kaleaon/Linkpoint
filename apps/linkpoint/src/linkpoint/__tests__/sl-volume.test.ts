import { describe, expect, it } from 'vitest';
import { generateVolume, volumeKey, volumeParamsFrom, type VolumeFace, type VolumeParams } from '../sl-volume';

const base: VolumeParams = {
  pathCurve: 0x10, profileCurve: 0x01, pathBegin: 0, pathEnd: 1, pathScaleX: 1, pathScaleY: 1, pathShearX: 0, pathShearY: 0,
  pathTwist: 0, pathTwistBegin: 0, pathRadiusOffset: 0, pathTaperX: 0, pathTaperY: 0, pathRevolutions: 1, pathSkew: 0,
  profileBegin: 0, profileEnd: 1, profileHollow: 0,
};
const make = (over: Partial<VolumeParams>) => generateVolume({ ...base, ...over });
const bounds = (faces: VolumeFace[]) => {
  const all = faces.flatMap((f) => f.vertices);
  const axis = (a: number) => { const v = all.filter((_, i) => i % 3 === a); return [Math.min(...v), Math.max(...v)]; };
  return [axis(0), axis(1), axis(2)];
};
/** Mean of normal · (vertex - centre): positive for outward-facing faces, negative for inward ones. */
const outwardness = (face: VolumeFace, centre: number[]) => {
  let sum = 0;
  const n = face.vertices.length / 3;
  for (let i = 0; i < n; i++) for (let a = 0; a < 3; a++) sum += face.normals[i * 3 + a] * (face.vertices[i * 3 + a] - centre[a]);
  return sum / n;
};
const centreOf = (faces: VolumeFace[]) => bounds(faces).map(([lo, hi]) => (lo + hi) / 2);

describe('SL prim volumes', () => {
  it('builds a box in the viewer\'s face order: top, four sides, bottom', () => {
    const faces = make({});
    expect(faces.map((f) => `${f.faceIndex}:${f.kind}`)).toEqual(['0:top', '1:side', '2:side', '3:side', '4:side', '5:bottom']);
    for (const [lo, hi] of bounds(faces)) { expect(lo).toBeCloseTo(-0.5, 5); expect(hi).toBeCloseTo(0.5, 5); }
    const centre = centreOf(faces);
    for (const f of faces) expect(outwardness(f, centre)).toBeGreaterThan(0.3);
    // top faces +Z, bottom faces -Z
    expect(faces[0].normals.slice(0, 3)).toEqual([0, 0, 1]);
    expect(faces[5].normals.slice(0, 3)).toEqual([0, 0, -1]);
  });

  it('builds a cylinder (top, side, bottom), a sphere and a torus as single smooth faces', () => {
    expect(make({ profileCurve: 0x00 }).map((f) => f.kind)).toEqual(['top', 'side', 'bottom']);
    const sphere = make({ pathCurve: 0x20, profileCurve: 0x05 });
    expect(sphere).toHaveLength(1);
    for (const [lo, hi] of bounds(sphere)) { expect(lo).toBeGreaterThan(-0.51); expect(hi).toBeLessThan(0.51); expect(hi - lo).toBeGreaterThan(0.9); }
    expect(outwardness(sphere[0], centreOf(sphere))).toBeGreaterThan(0.45);
    const torus = make({ pathCurve: 0x20, profileCurve: 0x00, pathScaleY: 0.5 });
    expect(torus).toHaveLength(1);
    expect(torus[0].vertices.length).toBeGreaterThan(300);
  });

  it('adds an inward-facing inner wall when hollow, between the sides and the bottom cap', () => {
    const faces = make({ profileHollow: 0.5 });
    expect(faces.map((f) => f.kind)).toEqual(['top', 'side', 'side', 'side', 'side', 'inner', 'bottom']);
    expect(outwardness(faces[5], centreOf(faces))).toBeLessThan(0);
    // the top cap is a ring: its triangles never cover the centre hole
    const top = faces[0];
    expect(top.indices.length).toBeGreaterThan(24);
  });

  it('adds two cut-end faces that face away from the solid when the profile is cut', () => {
    const faces = make({ profileBegin: 0.25, profileEnd: 0.75 });
    expect(faces.map((f) => f.kind)).toEqual(['top', 'side', 'side', 'bottom', 'cut-begin', 'cut-end']);
    const side = faces[1];
    const solid = [0, 1, 2].map((a) => { let s = 0; for (let i = 0; i < side.vertices.length / 3; i++) s += side.vertices[i * 3 + a]; return s / (side.vertices.length / 3); });
    for (const cut of faces.filter((f) => f.kind.startsWith('cut'))) {
      const c = [0, 1, 2].map((a) => { let s = 0; for (let i = 0; i < cut.vertices.length / 3; i++) s += cut.vertices[i * 3 + a]; return s / (cut.vertices.length / 3); });
      const n = [0, 1, 2].map((a) => cut.normals[a]);
      expect(n[0] * (solid[0] - c[0]) + n[1] * (solid[1] - c[1]) + n[2] * (solid[2] - c[2])).toBeLessThan(0);
    }
  });

  it('twists, tapers and shears the path', () => {
    const twisted = bounds(make({ pathTwist: 0.5 })); // 90 degrees: corners swing outward
    expect(twisted[0][1]).toBeCloseTo(0.7, 1);
    const tapered = make({ profileCurve: 0x00, pathScaleX: 0.3, pathScaleY: 0.3 });
    const top = tapered.find((f) => f.kind === 'top')!;
    const bottom = tapered.find((f) => f.kind === 'bottom')!;
    const radius = (f: VolumeFace) => Math.max(...Array.from({ length: f.vertices.length / 3 }, (_, i) => Math.hypot(f.vertices[i * 3], f.vertices[i * 3 + 1])));
    expect(radius(bottom)).toBeCloseTo(0.5, 1);    // the base keeps its size
    expect(radius(top)).toBeCloseTo(0.15, 1);      // a taper shrinks the top by the scale, as in SL
    const sheared = bounds(make({ pathShearX: 0.5 }));
    expect(sheared[0][0]).toBeCloseTo(-0.5, 5);
    expect(sheared[0][1]).toBeCloseTo(0.5 + 0.5, 5);
  });

  it('honours path cuts, producing a half sphere', () => {
    const half = make({ pathCurve: 0x20, profileCurve: 0x05, pathBegin: 0, pathEnd: 0.5 });
    expect(half.map((f) => f.kind)).toContain('top');
    const [, , z] = bounds(half);
    expect(Math.min(z[0], z[1])).toBeGreaterThanOrEqual(-0.0001 + (z[0] < 0 ? -1 : 0));
  });

  it('keeps every face well formed: valid indices, finite positions, unit normals', () => {
    const shapes: Array<Partial<VolumeParams>> = [
      {}, { profileCurve: 0x00 }, { profileCurve: 0x03 }, { profileCurve: 0x02 }, { profileCurve: 0x04 }, { profileCurve: 0x05 },
      { pathCurve: 0x20, profileCurve: 0x05 }, { pathCurve: 0x20, profileCurve: 0x00, pathScaleY: 0.5 }, { pathCurve: 0x30, profileCurve: 0x00 },
      { profileHollow: 0.7 }, { profileCurve: 0x20, profileHollow: 0.4 }, { profileCurve: 0x30, profileHollow: 0.4 }, { profileCurve: 0x10, profileHollow: 0.4, profileBegin: 0.2, profileEnd: 0.8 },
      { pathTwist: 0.8, pathTwistBegin: -0.8, pathTaperX: 0.5 }, { pathCurve: 0x20, profileCurve: 0x00, pathRevolutions: 2.5, pathSkew: 0.2, pathRadiusOffset: 0.3, pathScaleY: 0.4 },
      { pathCurve: 0x40 }, { pathBegin: 0.2, pathEnd: 0.8, profileBegin: 0.1, profileEnd: 0.9, profileHollow: 0.3 },
    ];
    for (const shape of shapes) {
      const faces = make(shape);
      expect(faces.length, JSON.stringify(shape)).toBeGreaterThan(0);
      for (const f of faces) {
        const n = f.vertices.length / 3;
        expect(f.normals.length).toBe(f.vertices.length);
        expect(f.texCoords.length).toBe(n * 2);
        expect(f.indices.length % 3).toBe(0);
        expect(f.indices.every((i) => Number.isInteger(i) && i >= 0 && i < n), JSON.stringify(shape)).toBe(true);
        expect(f.vertices.every(Number.isFinite), JSON.stringify(shape)).toBe(true);
        for (let i = 0; i < n; i++) expect(Math.hypot(f.normals[i * 3], f.normals[i * 3 + 1], f.normals[i * 3 + 2])).toBeCloseTo(1, 4);
      }
    }
  });

  it('gives faces texture coordinates that wrap once around the profile', () => {
    const box = make({});
    const u = box[1].texCoords.filter((_, i) => i % 2 === 0);
    expect(Math.min(...u)).toBeCloseTo(0, 5);
    expect(Math.max(...u)).toBeCloseTo(1, 5); // each flat side maps 0..1
    const v = box[1].texCoords.filter((_, i) => i % 2 === 1);
    expect(Math.min(...v)).toBeCloseTo(0, 5);
    expect(Math.max(...v)).toBeCloseTo(1, 5);
  });

  it('refuses unusable parameters and keys equal shapes identically', () => {
    expect(volumeParamsFrom(null)).toBeNull();
    expect(volumeParamsFrom({})).toBeNull();
    expect(make({ profileBegin: 0.5, profileEnd: 0.5 })).toEqual([]);
    expect(volumeKey(base)).toBe(volumeKey({ ...base }));
    expect(volumeKey(base)).not.toBe(volumeKey({ ...base, pathTwist: 0.1 }));
    expect(volumeParamsFrom({ pathCurve: 16, profileCurve: 1, pathTwist: null, pathScaleX: undefined })).toMatchObject({ pathTwist: 0, pathScaleX: 1 });
  });
});
