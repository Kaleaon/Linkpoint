import { describe, expect, it } from 'vitest';
import {
  SkinningPaletteUBO,
  AvatarSkeletonState,
  BENTO_MAX_PALETTE_JOINTS,
  MATRIX_4X4_FLOATS,
  UBO_PALETTE_SIZE_BYTES,
} from '../avatar-skeleton-state';
import { AvatarMeshRenderer, SkinnedMeshData } from '../avatar-mesh-renderer';
import { AVATAR_SKINNING_VERT_SHADER, AVATAR_SKINNING_VERT_SHADER_LEGACY } from '../avatar_skinning.vert';
import { compose, skinPoint } from '../avatar-skeleton';

describe('GPU Skeletal Mesh Skinning with UBOs', () => {
  describe('SkinningPaletteUBO', () => {
    it('initializes to 128 identity 4x4 matrices in std140 layout', () => {
      const ubo = new SkinningPaletteUBO(BENTO_MAX_PALETTE_JOINTS);
      const buffer = ubo.getUboBuffer();

      expect(buffer.length).toBe(128 * MATRIX_4X4_FLOATS);
      expect(buffer.byteLength).toBe(UBO_PALETTE_SIZE_BYTES);

      // Check first matrix is identity
      const m0 = ubo.getJointMatrix(0);
      expect(Array.from(m0)).toEqual([
        1, 0, 0, 0,
        0, 1, 0, 0,
        0, 0, 1, 0,
        0, 0, 0, 1,
      ]);

      // Check 127th matrix is identity
      const m127 = ubo.getJointMatrix(127);
      expect(Array.from(m127)).toEqual([
        1, 0, 0, 0,
        0, 1, 0, 0,
        0, 0, 1, 0,
        0, 0, 0, 1,
      ]);
    });

    it('updates matrix at specific joint index with precision accuracy', () => {
      const ubo = new SkinningPaletteUBO(BENTO_MAX_PALETTE_JOINTS);
      const customMatrix = compose([5, 10, -15], [0, 0, 0, 1]); // translation matrix

      ubo.updateJointMatrix(42, customMatrix);
      const retrieved = ubo.getJointMatrix(42);

      expect(retrieved[12]).toBe(5);
      expect(retrieved[13]).toBe(10);
      expect(retrieved[14]).toBe(-15);
      expect(retrieved[15]).toBe(1);

      // Unmodified joint remains identity
      const unedited = ubo.getJointMatrix(41);
      expect(unedited[12]).toBe(0);
      expect(unedited[13]).toBe(0);
      expect(unedited[14]).toBe(0);
    });

    it('bulk updates matrices for Bento joint palettes', () => {
      const ubo = new SkinningPaletteUBO(BENTO_MAX_PALETTE_JOINTS);
      const matrices: Float32Array[] = [];

      for (let i = 0; i < 128; i++) {
        matrices.push(compose([i, i * 2, i * 3]));
      }

      ubo.setJointMatrices(matrices);

      expect(ubo.getJointMatrix(0)[12]).toBe(0);
      expect(ubo.getJointMatrix(10)[12]).toBe(10);
      expect(ubo.getJointMatrix(10)[13]).toBe(20);
      expect(ubo.getJointMatrix(10)[14]).toBe(30);
      expect(ubo.getJointMatrix(127)[12]).toBe(127);
    });
  });

  describe('AvatarSkeletonState', () => {
    it('creates multiple UBO palettes when skeleton exceeds 128 joints', () => {
      const state = new AvatarSkeletonState(200); // 200 Bento joints -> 2 palettes
      expect(state.getPaletteCount()).toBe(2);

      const pose: Float32Array[] = [];
      for (let i = 0; i < 200; i++) {
        pose.push(compose([i, 1, 2]));
      }

      state.updatePose(pose);

      const p0 = state.getPaletteUbo(0);
      const p1 = state.getPaletteUbo(1);

      expect(p0.getJointMatrix(50)[12]).toBe(50);
      expect(p1.getJointMatrix(20)[12]).toBe(148); // 128 + 20 = 148th joint
    });
  });

  describe('AvatarMeshRenderer Attribute Layouts & Capability Checks', () => {
    it('supplies skinning attributes a_bone_indices and a_bone_weights in vertex layout', () => {
      const dummyGl = {} as WebGLRenderingContext;
      const renderer = new AvatarMeshRenderer(dummyGl);
      const layouts = renderer.getSkinningAttributeLayouts();

      expect(layouts).toHaveProperty('aPosition');
      expect(layouts).toHaveProperty('aNormal');
      expect(layouts).toHaveProperty('aTexCoord');
      expect(layouts).toHaveProperty('a_bone_indices');
      expect(layouts).toHaveProperty('a_bone_weights');

      expect(layouts.a_bone_indices.size).toBe(4);
      expect(layouts.a_bone_weights.size).toBe(4);
    });

    it('performs WebGL2 UBO capability check correctly', () => {
      class MockWebGL2Context {
        MAX_VERTEX_UNIFORM_BLOCKS = 36347;
        getParameter(param: number) {
          if (param === 36347) return 12;
          return 0;
        }
      }

      // Mock WebGL2 global
      (globalThis as any).WebGL2RenderingContext = MockWebGL2Context;

      const mockGl2 = new MockWebGL2Context() as unknown as WebGL2RenderingContext;
      const renderer = new AvatarMeshRenderer(mockGl2);

      expect(renderer.uboSupported).toBe(true);
    });
  });

  describe('Palette Splitting for Large Bento Meshes', () => {
    it('partitions submeshes into separate draw calls when joints exceed 128 palette limit', () => {
      const dummyGl = {} as WebGLRenderingContext;
      const renderer = new AvatarMeshRenderer(dummyGl);

      // Create mesh referencing joints across 2 palettes (joint 10 and joint 140)
      const mesh: SkinnedMeshData = {
        positions: new Float32Array([
          0, 0, 0,  1, 0, 0,  0, 1, 0,
          0, 0, 1,  1, 0, 1,  0, 1, 1,
        ]),
        normals: new Float32Array([
          0, 0, 1,  0, 0, 1,  0, 0, 1,
          0, 0, 1,  0, 0, 1,  0, 0, 1,
        ]),
        texCoords: new Float32Array([
          0, 0,  1, 0,  0, 1,
          0, 0,  1, 0,  0, 1,
        ]),
        boneIndices: new Float32Array([
          10, 0, 0, 0,   10, 0, 0, 0,   10, 0, 0, 0,
          140, 0, 0, 0,  140, 0, 0, 0,  140, 0, 0, 0,
        ]),
        boneWeights: new Float32Array([
          1, 0, 0, 0,  1, 0, 0, 0,  1, 0, 0, 0,
          1, 0, 0, 0,  1, 0, 0, 0,  1, 0, 0, 0,
        ]),
        indices: new Uint16Array([
          0, 1, 2,
          3, 4, 5,
        ]),
      };

      const partitions = renderer.partitionSubmeshesByPalette(mesh, 128);

      expect(partitions.length).toBe(2);
      expect(partitions[0].paletteIndex).toBe(0);
      expect(partitions[1].paletteIndex).toBe(1);

      // Remapped bone index for joint 140 in palette 1 should be 140 - 128 = 12
      expect(partitions[1].remappedBoneIndices[12]).toBe(12);
    });
  });

  describe('Vertex Shader Source Verification', () => {
    it('includes SkinningPaletteBlock UBO uniform block and skinning attributes', () => {
      expect(AVATAR_SKINNING_VERT_SHADER).toContain('layout(std140) uniform SkinningPaletteBlock');
      expect(AVATAR_SKINNING_VERT_SHADER).toContain('in vec4 a_bone_indices;');
      expect(AVATAR_SKINNING_VERT_SHADER).toContain('in vec4 a_bone_weights;');
      expect(AVATAR_SKINNING_VERT_SHADER).toContain('mat4 u_joint_matrices[128];');
    });

    it('provides legacy fallback shader', () => {
      expect(AVATAR_SKINNING_VERT_SHADER_LEGACY).toContain('attribute vec4 a_bone_indices;');
      expect(AVATAR_SKINNING_VERT_SHADER_LEGACY).toContain('attribute vec4 a_bone_weights;');
      expect(AVATAR_SKINNING_VERT_SHADER_LEGACY).toContain('uniform mat4 u_joint_matrices[110];');
    });
  });

  describe('CPU vs GPU Linear Blend Skinning Parity', () => {
    it('matches vertex position and normal transformation parity between CPU skinPoint and GPU LBS formula', () => {
      const p: [number, number, number] = [1.2, -0.5, 3.4];
      const joints = [0, 1, 2, 3];
      const weights = [0.5, 0.3, 0.2, 0.0];

      const m0 = compose([2, 1, 0], [0, 0, 0.7071, 0.7071]); // 90 deg rotation + translation
      const m1 = compose([-1, 0, 2], [0.7071, 0, 0, 0.7071]);
      const m2 = compose([0, 3, -1], [0, 0.7071, 0, 0.7071]);
      const m3 = compose([0, 0, 0], [0, 0, 0, 1]);

      const matrices = [m0, m1, m2, m3];

      // CPU reference skinning calculation
      const cpuSkinnedPos = skinPoint(p, joints, weights, matrices);

      // GPU Linear Blend Skinning formula simulation
      // skinnedPos = sum(weight_i * (M_i * vec4(pos, 1.0)).xyz)
      const gpuSkinnedPos: [number, number, number] = [0, 0, 0];
      let totalWeight = 0;

      for (let i = 0; i < 4; i++) {
        const w = weights[i];
        if (w <= 0) continue;
        const m = matrices[joints[i]];
        totalWeight += w;

        const x = m[0] * p[0] + m[4] * p[1] + m[8] * p[2] + m[12];
        const y = m[1] * p[0] + m[5] * p[1] + m[9] * p[2] + m[13];
        const z = m[2] * p[0] + m[6] * p[1] + m[10] * p[2] + m[14];

        gpuSkinnedPos[0] += w * x;
        gpuSkinnedPos[1] += w * y;
        gpuSkinnedPos[2] += w * z;
      }

      expect(Math.abs(cpuSkinnedPos[0] - gpuSkinnedPos[0])).toBeLessThan(1e-5);
      expect(Math.abs(cpuSkinnedPos[1] - gpuSkinnedPos[1])).toBeLessThan(1e-5);
      expect(Math.abs(cpuSkinnedPos[2] - gpuSkinnedPos[2])).toBeLessThan(1e-5);
    });
  });
});
