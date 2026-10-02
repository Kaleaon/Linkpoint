import { describe, expect, it } from 'vitest';
import { Primitives3D } from '../primitives-3d';

/** Fraction of triangles whose counter-clockwise normal agrees with the mesh's own vertex normals. */
function outwardFraction(mesh: { vertices: number[]; normals: number[]; indices: number[] }) {
  const v = mesh.vertices, nm = mesh.normals;
  let outward = 0, total = 0;
  for (let i = 0; i < mesh.indices.length; i += 3) {
    const idx = [0, 1, 2].map((k) => mesh.indices[i + k]);
    const p = idx.map((o) => [v[o * 3], v[o * 3 + 1], v[o * 3 + 2]]);
    const e1 = p[1].map((x, k) => x - p[0][k]), e2 = p[2].map((x, k) => x - p[0][k]);
    const n = [e1[1] * e2[2] - e1[2] * e2[1], e1[2] * e2[0] - e1[0] * e2[2], e1[0] * e2[1] - e1[1] * e2[0]];
    if (Math.hypot(...n) < 1e-12) continue; // degenerate (pole) triangle
    const vn = [0, 1, 2].map((k) => idx.reduce((sum, o) => sum + nm[o * 3 + k], 0));
    total++;
    if (n[0] * vn[0] + n[1] * vn[1] + n[2] * vn[2] > 0) outward++;
  }
  return outward / total;
}

describe('primitive meshes face outward (counter-clockwise front faces, as the renderer culls back faces)', () => {
  const meshes: Array<[string, () => { vertices: number[]; normals: number[]; indices: number[] }]> = [
    ['cube', () => Primitives3D.createCube(1)],
    ['sphere', () => Primitives3D.createSphere(0.5, 32, 16)],
    ['cylinder', () => Primitives3D.createCylinder(0.5, 0.5, 1, 32)],
    ['prism', () => Primitives3D.createPrism()],
    ['torus', () => Primitives3D.createTorus()],
  ];
  for (const [name, make] of meshes) {
    it(`${name}: every triangle's front side is the outside`, () => {
      expect(outwardFraction(make())).toBeGreaterThan(0.99);
    });
  }
});

describe('primitive meshes use Second Life conventions (unit box, Z axis)', () => {
  const bounds = (vertices: number[]) => {
    const min = [Infinity, Infinity, Infinity], max = [-Infinity, -Infinity, -Infinity];
    for (let i = 0; i < vertices.length; i += 3) for (let k = 0; k < 3; k++) {
      min[k] = Math.min(min[k], vertices[i + k]);
      max[k] = Math.max(max[k], vertices[i + k]);
    }
    return { min, max };
  };

  it.each([
    ['cube', Primitives3D.createCube(1)],
    ['sphere', Primitives3D.createSphere(0.5, 32, 16)],
    ['cylinder', Primitives3D.createCylinder(0.5, 0.5, 1, 32)],
  ])('%s fills the unit box', (_name, mesh) => {
    const { min, max } = bounds(mesh.vertices);
    for (let k = 0; k < 3; k++) {
      expect(min[k]).toBeCloseTo(-0.5, 5);
      expect(max[k]).toBeCloseTo(0.5, 5);
    }
  });

  it('cylinder has a cap at each end facing along +Z / -Z', () => {
    const mesh = Primitives3D.createCylinder(0.5, 0.5, 1, 16);
    const capNormals = new Set<string>();
    for (let i = 0; i < mesh.normals.length; i += 3) {
      if (mesh.normals[i] === 0 && mesh.normals[i + 1] === 0 && Math.abs(mesh.normals[i + 2]) === 1) capNormals.add(`${mesh.normals[i + 2]}@${mesh.vertices[i + 2]}`);
    }
    expect([...capNormals].sort()).toEqual(['-1@-0.5', '1@0.5']);
  });

  it('keeps all normals unit length', () => {
    for (const mesh of [Primitives3D.createSphere(0.5, 16, 8), Primitives3D.createCylinder(0.25, 0.5, 1, 16)]) {
      for (let i = 0; i < mesh.normals.length; i += 3) expect(Math.hypot(mesh.normals[i], mesh.normals[i + 1], mesh.normals[i + 2])).toBeCloseTo(1, 5);
    }
  });
});
