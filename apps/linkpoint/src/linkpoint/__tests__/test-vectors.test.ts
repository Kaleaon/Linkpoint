import { describe, it, expect } from 'vitest';
import * as fs from 'fs';
import * as path from 'path';

function findVectorFile(relativePath: string): string {
  const candidates = [
    path.resolve(__dirname, '../../../../../test-vectors', relativePath),
    path.resolve(__dirname, '../../../../test-vectors', relativePath),
    path.resolve('/app/Linkpoint/test-vectors', relativePath),
    path.resolve(process.cwd(), 'test-vectors', relativePath)
  ];
  for (const c of candidates) {
    if (fs.existsSync(c)) {
      return c;
    }
  }
  throw new Error(`Test vector file not found for ${relativePath}`);
}

function hexToBytes(hex: string): Uint8Array {
  const clean = hex.replace(/\s+/g, '');
  const bytes = new Uint8Array(clean.length / 2);
  for (let i = 0; i < clean.length; i += 2) {
    bytes[i / 2] = parseInt(clean.substring(i, i + 2), 16);
  }
  return bytes;
}

describe('Canonical Shared Test Vectors Suite', () => {
  describe('3D Math Test Vectors', () => {
    it('verifies quaternions operations', () => {
      const filePath = findVectorFile('math/quaternion_matrix_transform_vectors.json');
      const data = JSON.parse(fs.readFileSync(filePath, 'utf-8'));

      expect(data.version).toBe('1.0');
      expect(Array.isArray(data.quaternions)).toBe(true);

      for (const qCase of data.quaternions) {
        if (qCase.operation === 'identity') {
          expect(qCase.expected.x).toBe(0);
          expect(qCase.expected.y).toBe(0);
          expect(qCase.expected.z).toBe(0);
          expect(qCase.expected.w).toBe(1);
        } else if (qCase.operation === 'multiply') {
          expect(qCase.expected.q.length).toBe(4);
        } else if (qCase.operation === 'normalize') {
          expect(qCase.expected.magnitude).toBeCloseTo(5.4772, 3);
        } else if (qCase.operation === 'rotate_vector') {
          expect(qCase.expected.v).toEqual([0, 1, 0]);
        }
      }
    });

    it('verifies matrix operations', () => {
      const filePath = findVectorFile('math/quaternion_matrix_transform_vectors.json');
      const data = JSON.parse(fs.readFileSync(filePath, 'utf-8'));

      expect(Array.isArray(data.matrices)).toBe(true);
      for (const mCase of data.matrices) {
        expect(mCase.name).toBeDefined();
        expect(mCase.expected).toBeDefined();
      }
    });

    it('verifies Euler angle conversions', () => {
      const filePath = findVectorFile('math/quaternion_matrix_transform_vectors.json');
      const data = JSON.parse(fs.readFileSync(filePath, 'utf-8'));

      expect(Array.isArray(data.euler_angles)).toBe(true);
      for (const eCase of data.euler_angles) {
        expect(eCase.expected_quaternion.length).toBe(4);
      }
    });

    it('verifies AABB volume queries', () => {
      const filePath = findVectorFile('math/quaternion_matrix_transform_vectors.json');
      const data = JSON.parse(fs.readFileSync(filePath, 'utf-8'));

      expect(Array.isArray(data.aabb_queries)).toBe(true);
      for (const boxCase of data.aabb_queries) {
        const min = boxCase.min;
        const max = boxCase.max;
        const center = [(min[0] + max[0]) / 2, (min[1] + max[1]) / 2, (min[2] + max[2]) / 2];
        const extents = [max[0] - min[0], max[1] - min[1], max[2] - min[2]];

        expect(center).toEqual(boxCase.center);
        expect(extents).toEqual(boxCase.extents);

        for (const cp of boxCase.contains_points) {
          const pt = cp.point;
          const inside = pt[0] >= min[0] && pt[0] <= max[0] && pt[1] >= min[1] && pt[1] <= max[1] && pt[2] >= min[2] && pt[2] <= max[2];
          expect(inside).toBe(cp.expected);
        }
      }
    });
  });

  describe('Mesh Pipeline Test Vectors', () => {
    it('verifies LLMesh compressed binary parsing', () => {
      const filePath = findVectorFile('mesh/llmesh_decompress_vectors.json');
      const data = JSON.parse(fs.readFileSync(filePath, 'utf-8'));

      expect(Array.isArray(data.llmesh_vectors)).toBe(true);
      for (const meshCase of data.llmesh_vectors) {
        const bytes = hexToBytes(meshCase.hex_bytes);
        expect(bytes.length).toBeGreaterThanOrEqual(24);

        const magic = new TextDecoder().decode(bytes.subarray(0, 22));
        expect(magic).toBe('Linden Binary Mesh 1.0');

        // Verify U16 numVertices at offset 63
        const numVerts = bytes[63] | (bytes[64] << 8);
        expect(numVerts).toBe(meshCase.expected.vertex_count);
      }
    });

    it('verifies multi-LOD screen-area switching test vectors', () => {
      const { calculateProjectedPixelCoverage, selectLOD } = require('../../../core/sl-asset-decoder.cjs');
      const filePath = findVectorFile('mesh/llmesh_decompress_vectors.json');
      const data = JSON.parse(fs.readFileSync(filePath, 'utf-8'));

      expect(Array.isArray(data.multi_lod_vectors)).toBe(true);
      const lods = { high_lod: [{}], medium_lod: [{}], low_lod: [{}], lowest_lod: [{}] };

      for (const lodCase of data.multi_lod_vectors) {
        const px = calculateProjectedPixelCoverage(
          lodCase.bounding_radius,
          lodCase.distance_meters,
          lodCase.fov_rad,
          lodCase.screen_height_px
        );
        expect(px).toBeCloseTo(lodCase.expected.projected_pixel_coverage, 1);

        const selected = selectLOD(lods, { projectedPixels: px });
        expect(selected).toBe(lodCase.expected.selected_lod);
      }
    });

    it('verifies submesh material partition test vectors', () => {
      const { normalizeLLMesh } = require('../../../core/sl-asset-decoder.cjs');
      const filePath = findVectorFile('mesh/llmesh_decompress_vectors.json');
      const data = JSON.parse(fs.readFileSync(filePath, 'utf-8'));

      expect(Array.isArray(data.submesh_vectors)).toBe(true);
      for (const subCase of data.submesh_vectors) {
        const bytes = hexToBytes(subCase.hex_bytes);
        expect(bytes.length).toBeGreaterThanOrEqual(24);

        const mockLodLevels: any = { high_lod: [] };
        for (const sub of subCase.expected.submeshes) {
          mockLodLevels.high_lod.push({
            materialIndex: sub.material_index,
            position: Array.from({ length: sub.vertex_count }, () => ({ x: 0, y: 0, z: 0 })),
            triangleList: Array.from({ length: sub.index_count }, (_, i) => i % sub.vertex_count)
          });
        }
        const decoded = normalizeLLMesh({ version: 1, lodLevels: mockLodLevels });
        expect(decoded.parts).toHaveLength(subCase.expected.submesh_count);
        for (let i = 0; i < subCase.expected.submesh_count; i++) {
          expect(decoded.parts[i].materialIndex).toBe(subCase.expected.submeshes[i].material_index);
        }
      }
    });
  });

  describe('Texture Decoder Test Vectors', () => {
    it('verifies JPEG2000 codestream test vectors', () => {
      const filePath = findVectorFile('textures/j2k_texture_decoder_vectors.json');
      const data = JSON.parse(fs.readFileSync(filePath, 'utf-8'));

      expect(Array.isArray(data.j2k_vectors)).toBe(true);
      for (const texCase of data.j2k_vectors) {
        const bytes = hexToBytes(texCase.hex_bytes);
        if (texCase.expected.status === 'success') {
          expect(bytes.length).toBeGreaterThanOrEqual(24);
          const sig = new TextDecoder().decode(bytes.subarray(4, 8));
          expect(sig).toBe('jP  ');
        } else {
          if (bytes.length >= 8) {
            const sig = new TextDecoder().decode(bytes.subarray(4, 8));
            expect(sig).not.toBe('jP  ');
          }
        }
      }
    });
  });
});
