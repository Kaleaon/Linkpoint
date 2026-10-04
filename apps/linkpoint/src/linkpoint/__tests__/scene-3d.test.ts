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
  it('keeps every simulator terrain sample so ground and prim positions agree', () => {
    const { scene, graphics } = makeScene();
    const size = 256;
    const heights = Array.from({ length: size * size }, (_, index) => index);

    expect(scene.setTerrain(heights, size)).toBe(true);

    const [name, vertices, indices] = graphics.createMesh.mock.calls[0];
    expect(name).toBe('terrain');
    expect(vertices).toHaveLength(size * size * 3);
    expect(indices).toHaveLength((size - 1) * (size - 1) * 6);
    expect(vertices.slice(3, 6)).toEqual([1, 0, 1]);
    expect(vertices.slice(-3)).toEqual([255, 255, size * size - 1]);
    expect(indices.slice(-6)).toEqual([65278, 65279, 65534, 65279, 65535, 65534]);
  });

  it('keeps translations fixed when a prim is rotated', () => {
    const { scene } = makeScene();
    const matrix = (scene as any).calculateModelMatrix([12, 34, 56], [0.4, -0.7, 1.2], [2, 3, 4]);

    expect(Array.from(matrix.slice(12, 15))).toEqual([12, 34, 56]);
  });

  it('rotates counterclockwise using the same convention as simulator quaternions', () => {
    const { scene } = makeScene();
    const matrix = (scene as any).calculateModelMatrix([2, 3, 4], [0, 0, Math.PI / 2], [1, 1, 1]);
    const point = [
      matrix[0] + matrix[12],
      matrix[1] + matrix[13],
      matrix[2] + matrix[14],
    ];

    expect(point[0]).toBeCloseTo(2, 6);
    expect(point[1]).toBeCloseTo(4, 6);
    expect(point[2]).toBeCloseTo(4, 6);
  });

  it('uses inverse scale for the normal matrix', () => {
    const { scene } = makeScene();
    const model = (scene as any).calculateModelMatrix([0, 0, 0], [0, 0, 0], [2, 4, 8]);
    const normal = (scene as any).mat3FromMat4(model);

    expect(Array.from(normal)).toEqual([0.5, 0, 0, 0, 0.25, 0, 0, 0, 0.125]);
  });

  it('keeps the scale supplied for a skinned world object', () => {
    const { scene, graphics } = makeScene();
    (graphics as any).isSkinnedMesh = () => true;
    scene.addObject('animesh', {
      mesh: 'animated', position: [0, 0, 0], scale: [2, 3, 4], skin: new Float32Array(12),
    });

    scene.renderObject(scene.objects.get('animesh'), new Float32Array(16), new Float32Array(16));

    const model = graphics.drawMesh.mock.calls[0][2].uModelMatrix;
    expect([model[0], model[5], model[10]]).toEqual([2, 3, 4]);
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
