import { describe, expect, it, vi } from 'vitest';
import { Scene3D } from '../scene-3d';

function makeScene() {
  const graphics = {
    clear: vi.fn(),
    drawMesh: vi.fn(),
    setClearColor: vi.fn(),
    createMesh: vi.fn(),
    createRenderTarget: vi.fn(),
    beginRenderTarget: vi.fn(() => false),
    endRenderTarget: vi.fn(),
  };
  const camera = {
    position: [10, 20, 30],
    getViewMatrix: () => new Float32Array(16),
    getProjectionMatrix: () => new Float32Array(16),
  };
  return { scene: new Scene3D(graphics as any, camera as any), graphics };
}

describe('Scene3D rendering state', () => {
  it('keeps translations fixed when a prim is rotated', () => {
    const { scene } = makeScene();
    const matrix = (scene as any).calculateModelMatrix([12, 34, 56], [0.4, -0.7, 1.2], [2, 3, 4]);

    expect(Array.from(matrix.slice(12, 15))).toEqual([12, 34, 56]);
  });

  it('uses inverse scale for the normal matrix', () => {
    const { scene } = makeScene();
    const model = (scene as any).calculateModelMatrix([0, 0, 0], [0, 0, 0], [2, 4, 8]);
    const normal = (scene as any).mat3FromMat4(model);

    expect(Array.from(normal)).toEqual([0.5, 0, 0, 0, 0.25, 0, 0, 0, 0.125]);
  });

  it('resets persistent material uniforms when drawing terrain', () => {
    const { scene, graphics } = makeScene();
    scene.renderGrid(new Float32Array(16), new Float32Array(16));

    const uniforms = graphics.drawMesh.mock.calls[0][2];
    expect(uniforms).toMatchObject({
      uUseTexture: false,
      uFullBright: false,
      uUseNormalTexture: false,
      uAlphaMode: 0,
    });
    expect(Array.from(uniforms.uTexTransform)).toEqual([1, 1, 0, 0]);
  });

  it('persists the environment sky color through the graphics clear path', () => {
    const { scene, graphics } = makeScene();
    scene.setEnvironment({ sky: { blueHorizon: [0.2, 0.4, 0.6] } });

    // the clear colour is the tone-mapped zenith of the atmosphere: a valid, bluish colour
    const [color] = graphics.setClearColor.mock.calls.at(-1)!;
    expect(color).toHaveLength(4);
    expect(color.slice(0, 3).every((v: number) => v >= 0 && v <= 1)).toBe(true);
    expect(color[2]).toBeGreaterThan(color[0]);
    expect(color[3]).toBe(1);
  });

  it('draws opaque objects first and blended objects back-to-front', () => {
    const { scene, graphics } = makeScene();
    scene.showGrid = false;
    scene.addObject('near-glass', { mesh: 'cube', position: [11, 20, 30], faces: [{ pbr: { alphaMode: 'BLEND' } }] });
    scene.addObject('solid', { mesh: 'cube', position: [12, 20, 30] });
    scene.addObject('far-glass', { mesh: 'cube', position: [20, 20, 30], faces: [{ pbr: { alphaMode: 'BLEND' } }] });

    scene.render();

    expect(graphics.drawMesh.mock.calls.map((call) => call[2].uModelMatrix[12])).toEqual([12, 20, 11]);
  });
});
