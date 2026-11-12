import { describe, expect, it } from 'vitest';
import {
  SkinningPaletteUBO,
  VisualParamUBO,
  AvatarSkeletonState,
  BENTO_MAX_PALETTE_JOINTS,
  MAX_ACTIVE_MORPH_TARGETS,
  MATRIX_4X4_FLOATS,
  UBO_PALETTE_SIZE_BYTES,
  VISUAL_PARAM_UBO_SIZE_BYTES,
} from '../avatar-skeleton-state';
import { AvatarMeshRenderer, createMorphTargetDataTexture, SkinnedMeshData, MorphTargetData } from '../avatar-mesh-renderer';
import { AVATAR_SKINNING_VERT_SHADER, AVATAR_SKINNING_VERT_SHADER_LEGACY } from '../avatar_skinning.vert';
import { compose, skinPoint } from '../avatar-skeleton';

describe('GPU Skeletal Mesh Skinning with UBOs and Morph Target Blending', () => {
  describe('VisualParamUBO', () => {
    it('initializes std140 memory layout with 32 morph weights and active count 0', () => {
      const visualUbo = new VisualParamUBO();
      expect(visualUbo.getArrayBuffer().byteLength).toBe(VISUAL_PARAM_UBO_SIZE_BYTES);

      const weights = visualUbo.getMorphWeights();
      expect(weights.length).toBe(MAX_ACTIVE_MORPH_TARGETS);
      expect(weights.every((w) => w === 0)).toBe(true);
      expect(visualUbo.getActiveMorphCount()).toBe(0);
    });

    it('sets single and bulk morph target weights and updates active morph count', () => {
      const visualUbo = new VisualParamUBO();
      visualUbo.setMorphWeight(0, 0.75);
      visualUbo.setMorphWeight(5, -0.25);
      visualUbo.setActiveMorphCount(6);

      const weights = visualUbo.getMorphWeights();
      expect(weights[0]).toBe(0.75);
      expect(weights[5]).toBe(-0.25);
      expect(visualUbo.getActiveMorphCount()).toBe(6);

      // Bulk update
      const inputWeights = [0.1, 0.2, 0.3, 0.4, 0.5];
      visualUbo.setMorphWeights(inputWeights);
      expect(visualUbo.getActiveMorphCount()).toBe(5);
      expect(visualUbo.getMorphWeights()[2]).toBeCloseTo(0.3);
    });
  });
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

      // Remapped bone index for joint 140 in palette 1 should be 140 % 128 = 12
      expect(partitions[1].remappedBoneIndices[12]).toBe(12);
    });

    it('preserves relative bone indices for boundary vertices without zero-index fallback', () => {
      const dummyGl = {} as WebGLRenderingContext;
      const renderer = new AvatarMeshRenderer(dummyGl);

      // A mesh where vertices reference combinations of low and high joints
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
          15, 0, 0, 0,   15, 0, 0, 0,   15, 0, 0, 0,
          140, 10, 0, 0,  140, 10, 0, 0,  140, 10, 0, 0,
        ]),
        boneWeights: new Float32Array([
          1, 0, 0, 0,    1, 0, 0, 0,    1, 0, 0, 0,
          0.7, 0.3, 0, 0, 0.7, 0.3, 0, 0, 0.7, 0.3, 0, 0,
        ]),
        indices: new Uint16Array([
          0, 1, 2,
          3, 4, 5,
        ]),
      };

      const partitions = renderer.partitionSubmeshesByPalette(mesh, 128);
      expect(partitions.length).toBe(2);

      const p1 = partitions[1]; // Partition for palette 1
      expect(p1.paletteIndex).toBe(1);

      // Check vertex 3 (index 12, 13) in remappedBoneIndices:
      // Joint 140 remapped -> 140 % 128 = 12
      // Joint 10 remapped -> 10 % 128 = 10 (MUST NOT fallback to 0)
      expect(p1.remappedBoneIndices[12]).toBe(12);
      expect(p1.remappedBoneIndices[13]).toBe(10);
    });

    it('correctly remaps joint indices across 3+ palettes (> 256 joints)', () => {
      const dummyGl = {} as WebGLRenderingContext;
      const renderer = new AvatarMeshRenderer(dummyGl);

      // Mesh with joints in Palette 0 (10), Palette 1 (140), and Palette 2 (266)
      const mesh: SkinnedMeshData = {
        positions: new Float32Array([
          0, 0, 0,  1, 0, 0,  0, 1, 0,
          0, 0, 1,  1, 0, 1,  0, 1, 1,
          0, 0, 2,  1, 0, 2,  0, 1, 2,
        ]),
        normals: new Float32Array([
          0, 0, 1,  0, 0, 1,  0, 0, 1,
          0, 0, 1,  0, 0, 1,  0, 0, 1,
          0, 0, 1,  0, 0, 1,  0, 0, 1,
        ]),
        texCoords: new Float32Array([
          0, 0,  1, 0,  0, 1,
          0, 0,  1, 0,  0, 1,
          0, 0,  1, 0,  0, 1,
        ]),
        boneIndices: new Float32Array([
          10, 0, 0, 0,   10, 0, 0, 0,   10, 0, 0, 0,
          140, 0, 0, 0,  140, 0, 0, 0,  140, 0, 0, 0,
          266, 0, 0, 0,  266, 0, 0, 0,  266, 0, 0, 0,
        ]),
        boneWeights: new Float32Array([
          1, 0, 0, 0,  1, 0, 0, 0,  1, 0, 0, 0,
          1, 0, 0, 0,  1, 0, 0, 0,  1, 0, 0, 0,
          1, 0, 0, 0,  1, 0, 0, 0,  1, 0, 0, 0,
        ]),
        indices: new Uint16Array([
          0, 1, 2,
          3, 4, 5,
          6, 7, 8,
        ]),
      };

      const partitions = renderer.partitionSubmeshesByPalette(mesh, 128);

      expect(partitions.length).toBe(3);
      expect(partitions[0].paletteIndex).toBe(0);
      expect(partitions[1].paletteIndex).toBe(1);
      expect(partitions[2].paletteIndex).toBe(2);

      // Check joint 266 in Palette 2 (vertex 6 -> boneIndices[24])
      // 266 % 128 = 10
      expect(partitions[2].remappedBoneIndices[24]).toBe(10);
    });
  });

  describe('Vertex Shader Source Verification', () => {
    it('includes SkinningPaletteBlock UBO, VisualParamBlock UBO, and morph texture declarations', () => {
      expect(AVATAR_SKINNING_VERT_SHADER).toContain('layout(std140) uniform SkinningPaletteBlock');
      expect(AVATAR_SKINNING_VERT_SHADER).toContain('layout(std140) uniform VisualParamBlock');
      expect(AVATAR_SKINNING_VERT_SHADER).toContain('uniform sampler2D u_morph_delta_texture;');
      expect(AVATAR_SKINNING_VERT_SHADER).toContain('in vec4 a_bone_indices;');
      expect(AVATAR_SKINNING_VERT_SHADER).toContain('in vec4 a_bone_weights;');
      expect(AVATAR_SKINNING_VERT_SHADER).toContain('mat4 u_joint_matrices[128];');
      expect(AVATAR_SKINNING_VERT_SHADER).toContain('texelFetch(u_morph_delta_texture, ivec2(gl_VertexID, i), 0)');
    });

    it('provides clean legacy fallback shader without WebGL2/UBO syntax', () => {
      expect(AVATAR_SKINNING_VERT_SHADER_LEGACY).toContain('attribute vec4 a_bone_indices;');
      expect(AVATAR_SKINNING_VERT_SHADER_LEGACY).toContain('attribute vec4 a_bone_weights;');
      expect(AVATAR_SKINNING_VERT_SHADER_LEGACY).toContain('uniform mat4 u_joint_matrices[110];');
      expect(AVATAR_SKINNING_VERT_SHADER_LEGACY).not.toContain('layout');
      expect(AVATAR_SKINNING_VERT_SHADER_LEGACY).not.toContain('texelFetch');
    });
  });

  describe('Morph Target Data Texture Packaging', () => {
    it('packages morph target delta arrays into 2D WebGL2 floating-point data texture', () => {
      class MockTexture {}
      let createdTex: any = null;
      let texImageData: any = null;

      class MockWebGL2ContextForTex {
        RGBA32F = 34836;
        RGBA = 6408;
        FLOAT = 5126;
        TEXTURE_2D = 3553;
        NEAREST = 9728;
        TEXTURE_MIN_FILTER = 10241;
        TEXTURE_MAG_FILTER = 10240;
        TEXTURE_WRAP_S = 10242;
        TEXTURE_WRAP_T = 10243;
        CLAMP_TO_EDGE = 33071;

        createTexture() {
          createdTex = new MockTexture();
          return createdTex;
        }
        bindTexture() {}
        texImage2D(_target: number, _level: number, _internalFormat: number, width: number, height: number, _border: number, _format: number, _type: number, srcData: Float32Array) {
          texImageData = { width, height, data: new Float32Array(srcData) };
        }
        texParameteri() {}
      }

      (globalThis as any).WebGL2RenderingContext = MockWebGL2ContextForTex;
      const mockGl2 = new MockWebGL2ContextForTex() as unknown as WebGL2RenderingContext;

      const vertexCount = 4;
      const morphTargets: MorphTargetData[] = [
        { name: 'height', deltas: new Float32Array([0, 0.1, 0,  0, 0.2, 0,  0, 0.3, 0,  0, 0.4, 0]) },
        { name: 'width', deltas: new Float32Array([0.1, 0, 0,  0.2, 0, 0,  0.3, 0, 0,  0.4, 0, 0]) },
      ];

      const tex = createMorphTargetDataTexture(mockGl2, vertexCount, morphTargets, 32);
      expect(tex).toBe(createdTex);
      expect(texImageData).not.toBeNull();
      expect(texImageData.width).toBe(4);
      expect(texImageData.height).toBe(2);
      expect(texImageData.data.length).toBe(4 * 2 * 4); // width * height * 4 components

      // Check first morph target, vertex 0 (row 0, col 0)
      expect(texImageData.data[0]).toBe(0);
      expect(texImageData.data[1]).toBeCloseTo(0.1);
      expect(texImageData.data[2]).toBe(0);
      expect(texImageData.data[3]).toBe(0);

      // Check second morph target, vertex 1 (row 1, col 1 -> index (1 * 4 + 1) * 4 = 20)
      expect(texImageData.data[20]).toBeCloseTo(0.2);
      expect(texImageData.data[21]).toBe(0);
      expect(texImageData.data[22]).toBe(0);
    });
  });

  describe('CPU vs GPU Linear Blend Skinning and Morph Blending Parity', () => {
    it('matches vertex position transformation parity with active morph target delta blending', () => {
      const basePos: [number, number, number] = [1.0, 2.0, 3.0];
      const joints = [0, 1, 2, 3];
      const weights = [0.5, 0.3, 0.2, 0.0];

      const m0 = compose([2, 1, 0], [0, 0, 0.7071, 0.7071]);
      const m1 = compose([-1, 0, 2], [0.7071, 0, 0, 0.7071]);
      const m2 = compose([0, 3, -1], [0, 0.7071, 0, 0.7071]);
      const m3 = compose([0, 0, 0], [0, 0, 0, 1]);
      const matrices = [m0, m1, m2, m3];

      // Simulate 3 active morph target deltas
      const morphDeltas: [number, number, number][] = [
        [0.1, -0.2, 0.3],
        [-0.05, 0.1, 0.15],
        [0.2, 0.0, -0.1],
      ];
      const morphWeights = [0.8, -0.5, 1.2];

      // 1. Compute morphed position
      const morphedPos: [number, number, number] = [...basePos];
      for (let m = 0; m < morphDeltas.length; m++) {
        morphedPos[0] += morphDeltas[m][0] * morphWeights[m];
        morphedPos[1] += morphDeltas[m][1] * morphWeights[m];
        morphedPos[2] += morphDeltas[m][2] * morphWeights[m];
      }

      // 2. CPU reference skinning on morphed position
      const cpuSkinnedPos = skinPoint(morphedPos, joints, weights, matrices);

      // 3. GPU Vertex Shader formula simulation
      // Morph offset calculation
      const gpuMorphOffset: [number, number, number] = [0, 0, 0];
      for (let m = 0; m < morphDeltas.length; m++) {
        const w = morphWeights[m];
        if (Math.abs(w) > 0.0001) {
          gpuMorphOffset[0] += morphDeltas[m][0] * w;
          gpuMorphOffset[1] += morphDeltas[m][1] * w;
          gpuMorphOffset[2] += morphDeltas[m][2] * w;
        }
      }
      const gpuMorphedPos: [number, number, number] = [
        basePos[0] + gpuMorphOffset[0],
        basePos[1] + gpuMorphOffset[1],
        basePos[2] + gpuMorphOffset[2],
      ];

      // Joint skinning matrix calculation
      const gpuSkinnedPos: [number, number, number] = [0, 0, 0];
      for (let i = 0; i < 4; i++) {
        const w = weights[i];
        if (w <= 0) continue;
        const m = matrices[joints[i]];

        const x = m[0] * gpuMorphedPos[0] + m[4] * gpuMorphedPos[1] + m[8] * gpuMorphedPos[2] + m[12];
        const y = m[1] * gpuMorphedPos[0] + m[5] * gpuMorphedPos[1] + m[9] * gpuMorphedPos[2] + m[13];
        const z = m[2] * gpuMorphedPos[0] + m[6] * gpuMorphedPos[1] + m[10] * gpuMorphedPos[2] + m[14];

        gpuSkinnedPos[0] += w * x;
        gpuSkinnedPos[1] += w * y;
        gpuSkinnedPos[2] += w * z;
      }

      expect(Math.abs(cpuSkinnedPos[0] - gpuSkinnedPos[0])).toBeLessThan(1e-5);
      expect(Math.abs(cpuSkinnedPos[1] - gpuSkinnedPos[1])).toBeLessThan(1e-5);
      expect(Math.abs(cpuSkinnedPos[2] - gpuSkinnedPos[2])).toBeLessThan(1e-5);
    });

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
