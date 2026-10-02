import { describe, expect, it } from 'vitest';
import { Primitives3D } from '../primitives-3d';

describe('advanced visible scene geometry', () => {
  it.each([
    ['prism', Primitives3D.createPrism()],
    ['torus', Primitives3D.createTorus()],
  ])('creates indexed %s geometry with normals and UVs', (_name, geometry) => {
    expect(geometry.vertices.length).toBeGreaterThan(0);
    expect(geometry.indices.length).toBeGreaterThan(0);
    expect(geometry.vertices.length % 3).toBe(0);
    expect(geometry.normals).toHaveLength(geometry.vertices.length);
    expect(geometry.texCoords).toHaveLength(geometry.vertices.length / 3 * 2);
    expect(Math.max(...geometry.indices)).toBeLessThan(geometry.vertices.length / 3);
  });
});
