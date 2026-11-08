import { describe, expect, it, vi } from 'vitest';
import { Scene3D } from '../scene-3d';
import { Graphics3D } from '../graphics-3d';
import { Camera3D } from '../camera-3d';
import { WorldViewer } from '../world';
import { Utils } from '../utils';
import { serializeTerrainMaterials } from '../../../core/sl-terrain.cjs';

class ProtocolStub extends Utils.EventEmitter { connected = true; authReply: Record<string, any> | null = null; }

function makeMockCanvas() {
  const gl: any = {
    VERTEX_SHADER: 35633,
    FRAGMENT_SHADER: 35632,
    LINK_STATUS: true,
    COMPILE_STATUS: true,
    MAX_TEXTURE_SIZE: 4096,
    MAX_VERTEX_ATTRIBS: 16,
    MAX_VERTEX_UNIFORM_VECTORS: 128,
    DEPTH_TEST: 2929,
    CULL_FACE: 2884,
    BACK: 1029,
    CCW: 2305,
    LEQUAL: 515,
    STATIC_DRAW: 35044,
    ARRAY_BUFFER: 34962,
    ELEMENT_ARRAY_BUFFER: 34963,
    UNSIGNED_SHORT: 5123,
    UNSIGNED_INT: 5125,
    FLOAT: 5126,
    TEXTURE0: 33984,
    TEXTURE_2D: 3553,
    RGBA: 6408,
    UNSIGNED_BYTE: 5121,
    LINEAR: 9729,
    LINEAR_MIPMAP_LINEAR: 9987,
    MAG_FILTER: 10240,
    MIN_FILTER: 10241,
    WRAP_S: 10242,
    WRAP_T: 10243,
    REPEAT: 10497,
    CLAMP_TO_EDGE: 33071,
    UNPACK_FLIP_Y_WEBGL: 37440,
    COLOR_BUFFER_BIT: 16384,
    DEPTH_BUFFER_BIT: 256,
    POINTS: 0,
    TRIANGLES: 4,
    SRC_ALPHA: 770,
    ONE_MINUS_SRC_ALPHA: 771,
    BLEND: 3042,
    createShader: vi.fn(() => ({})),
    shaderSource: vi.fn(),
    compileShader: vi.fn(),
    getShaderParameter: vi.fn(() => true),
    deleteShader: vi.fn(),
    createProgram: vi.fn(() => ({})),
    attachShader: vi.fn(),
    bindAttribLocation: vi.fn(),
    linkProgram: vi.fn(),
    getProgramParameter: vi.fn((prog, param) => {
      if (param === true) return true; // LINK_STATUS
      return 0;
    }),
    getActiveAttrib: vi.fn(),
    getAttribLocation: vi.fn((prog, name) => 0),
    getActiveUniform: vi.fn(),
    getUniformLocation: vi.fn((prog, name) => ({ name })),
    getExtension: vi.fn(() => null),
    getParameter: vi.fn((p) => 4096),
    enable: vi.fn(),
    disable: vi.fn(),
    cullFace: vi.fn(),
    frontFace: vi.fn(),
    depthFunc: vi.fn(),
    clearColor: vi.fn(),
    useProgram: vi.fn(),
    bindBuffer: vi.fn(),
    createBuffer: vi.fn(() => ({})),
    bufferData: vi.fn(),
    enableVertexAttribArray: vi.fn(),
    disableVertexAttribArray: vi.fn(),
    vertexAttribPointer: vi.fn(),
    createTexture: vi.fn(() => ({})),
    bindTexture: vi.fn(),
    pixelStorei: vi.fn(),
    texImage2D: vi.fn(),
    texParameteri: vi.fn(),
    generateMipmap: vi.fn(),
    activeTexture: vi.fn(),
    uniform1i: vi.fn(),
    uniform1f: vi.fn(),
    uniform2fv: vi.fn(),
    uniform3fv: vi.fn(),
    uniform4fv: vi.fn(),
    uniformMatrix3fv: vi.fn(),
    uniformMatrix4fv: vi.fn(),
    drawElements: vi.fn(),
    depthMask: vi.fn(),
    blendFunc: vi.fn(),
    clear: vi.fn(),
  };

  const canvas: any = {
    width: 800,
    height: 600,
    getContext: vi.fn((type: string) => gl),
  };
  return { canvas, gl };
}

describe('PBR Terrain Normal Map and EEP Sky Shader Extensions', () => {
  it('serializes normalTextureIds in serializeTerrainMaterials', () => {
    const region = {
      terrainDetail0: 'id-0',
      terrainDetail1: 'id-1',
      terrainDetail2: 'id-2',
      terrainDetail3: 'id-3',
      terrainNormal0: 'norm-0',
      terrainNormal1: 'norm-1',
      terrainNormal2: 'norm-2',
      terrainNormal3: 'norm-3',
      terrainStartHeight00: 10,
      terrainStartHeight01: 10,
      terrainStartHeight10: 10,
      terrainStartHeight11: 10,
      terrainHeightRange00: 30,
      terrainHeightRange01: 30,
      terrainHeightRange10: 30,
      terrainHeightRange11: 30,
      xCoordinate: 256000,
      yCoordinate: 256256,
      waterHeight: 20,
    };
    const serialized = serializeTerrainMaterials(region);
    expect(serialized).toMatchObject({
      textureIds: ['id-0', 'id-1', 'id-2', 'id-3'],
      normalTextureIds: ['norm-0', 'norm-1', 'norm-2', 'norm-3'],
      waterHeight: 20,
    });
  });

  it('passes normalTextureNames from WorldViewer to Scene3D setTerrainMaterials', () => {
    const protocol = new ProtocolStub();
    const world = new WorldViewer(protocol);
    const scene: any = { setTerrain: vi.fn(), setTerrainMaterials: vi.fn().mockReturnValue(true), setWaterHeight: vi.fn(), objects: new Map() };
    (world as any).scene3d = scene;

    protocol.emit('scene:world-data', {
      terrainMaterials: {
        textureIds: ['a0', 'a1', 'a2', 'a3'],
        normalTextureIds: ['n0', 'n1', 'n2', 'n3'],
        startHeights: [0, 0, 0, 0],
        heightRanges: [10, 10, 10, 10],
        origin: [0, 0],
        waterHeight: 20,
      },
    });

    expect(scene.setTerrainMaterials).toHaveBeenCalledWith({
      textureNames: ['texture:a0', 'texture:a1', 'texture:a2', 'texture:a3'],
      normalTextureNames: ['texture:n0', 'texture:n1', 'texture:n2', 'texture:n3'],
      startHeights: [0, 0, 0, 0],
      heightRanges: [10, 10, 10, 10],
      origin: [0, 0],
    });
  });

  it('Scene3D.renderGrid passes active EEP sky uniforms and normal map names to drawMesh', () => {
    const graphics: any = {
      clear: vi.fn(),
      drawMesh: vi.fn(),
      setClearColor: vi.fn(),
      createMesh: vi.fn(),
      hasTexture: vi.fn((name) => Boolean(name)),
      createTexture: vi.fn(),
    };
    const camera = new Camera3D();
    const scene = new Scene3D(graphics as any, camera);

    scene.setEnvironment({
      sky: {
        blueHorizon: [0.3, 0.5, 0.8],
        blueDensity: [1, 1, 1],
        sunlightColor: [1, 1, 0.9],
        ambientColor: [0.2, 0.2, 0.3],
        hazeHorizon: 0.25,
        hazeDensity: 0.1,
      },
    });

    scene.setTerrain(Array.from({ length: 16 * 16 }, () => 10), 16);
    scene.setTerrainMaterials({
      textureNames: ['texture:t0', 'texture:t1', 'texture:t2', 'texture:t3'],
      normalTextureNames: ['texture:norm0', 'texture:norm1', 'texture:norm2', 'texture:norm3'],
      startHeights: [0, 0, 0, 0],
      heightRanges: [10, 10, 10, 10],
      origin: [0, 0],
    });

    scene.renderGrid(new Float32Array(16), new Float32Array(16));

    expect(graphics.drawMesh).toHaveBeenCalledWith(
      'terrain',
      'terrain',
      expect.objectContaining({
        uDetail0Name: 'texture:t0',
        uDetail1Name: 'texture:t1',
        uDetail2Name: 'texture:t2',
        uDetail3Name: 'texture:t3',
        uDetailNormal0Name: 'texture:norm0',
        uDetailNormal1Name: 'texture:norm1',
        uDetailNormal2Name: 'texture:norm2',
        uDetailNormal3Name: 'texture:norm3',
        uSkyColor: expect.any(Float32Array),
        uHazeHorizon: expect.any(Number),
        uHazeColor: expect.any(Float32Array),
        uAmbientColor: expect.any(Float32Array),
      })
    );
  });

  it('Graphics3D binds uDetailNormal0..3 samplers and falls back to __normal texture when missing', async () => {
    const { canvas, gl } = makeMockCanvas();
    const graphics = new Graphics3D(canvas);
    graphics.gl = gl;

    // Setup active uniforms in program
    const programInfo = {
      program: {},
      attributes: {},
      uniforms: {
        uComposition: { loc: 0 },
        uDetail0: { loc: 1 },
        uDetailNormal0: { loc: 2 },
        uDetailNormal1: { loc: 3 },
      },
      arrayUniforms: {},
    };
    (graphics as any).programs.set('terrain', programInfo);
    (graphics as any).meshes.set('terrain', { vao: null, buffers: {}, indexCount: 6, indexType: gl.UNSIGNED_SHORT });
    (graphics as any).textures.set('terrain:composition', { tex: 'comp' });
    (graphics as any).textures.set('texture:detail0', { tex: 'det0' });
    (graphics as any).textures.set('__normal', { tex: 'default-normal' });

    graphics.drawMesh('terrain', 'terrain', {
      uCompositionName: 'terrain:composition',
      uDetail0Name: 'texture:detail0',
      uDetailNormal0Name: 'texture:missing_norm', // Should fall back to '__normal'
    });

    // Check activeTexture calls
    expect(gl.activeTexture).toHaveBeenCalledWith(gl.TEXTURE0 + 0);
    expect(gl.activeTexture).toHaveBeenCalledWith(gl.TEXTURE0 + 1);
    expect(gl.activeTexture).toHaveBeenCalledWith(gl.TEXTURE0 + 2);
    // Missing normal texture maps to '__normal' fallback
    expect(gl.bindTexture).toHaveBeenCalledWith(gl.TEXTURE_2D, { tex: 'default-normal' });
  });
});
