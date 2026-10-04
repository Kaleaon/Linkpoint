import { describe, expect, it, vi } from 'vitest';
import { Camera3D } from '../camera-3d';
import { Scene3D } from '../scene-3d';

const BOUNDS = { min: [-0.5, -0.5, -0.5], max: [0.5, 0.5, 0.5] };

function makeScene(alphaTextures: string[] = []) {
  const order: string[] = [];
  const graphics = {
    clear: vi.fn(() => order.push('clear')),
    clearDepth: vi.fn(() => order.push('clearDepth')),
    drawMesh: vi.fn((mesh: string, _program: string, uniforms: any) => order.push(uniforms.uAlphaMode === 2 ? 'blend' : 'draw')),
    setClearColor: vi.fn(), createMesh: vi.fn(), createRenderTarget: vi.fn(),
    beginRenderTarget: vi.fn(() => false), endRenderTarget: vi.fn(),
    getMeshBounds: vi.fn(() => BOUNDS),
    textureHasAlpha: vi.fn((name?: string) => Boolean(name && alphaTextures.includes(name))),
  };
  const camera = new Camera3D();
  camera.mode = 'first-person';
  camera.position = [0, 0, 50];
  camera.rotation = [0, 0, 0];
  camera.aspect = 2;
  camera.updateMatrices();
  const scene = new Scene3D(graphics as any, camera);
  scene.showGrid = false;
  return { scene, graphics, order };
}

// A flat 1 (y) x 0.5 (z) HUD button, thin in depth, at the HUD root.
const hudButton = (id: string, extra: any = {}) => ({ mesh: 'cube', hud: true, hudRoot: 'root', position: [0, 0, 0], scale: [0.02, 1, 0.5], ...extra });

describe('HUD pass', () => {
  it('draws nothing for the HUD until one is displayed', () => {
    const { scene, graphics } = makeScene();
    scene.addObject('b1', hudButton('b1'));
    scene.render();
    expect(graphics.drawMesh).not.toHaveBeenCalled();
    expect(graphics.clearDepth).not.toHaveBeenCalled();
  });

  it('keeps HUD prims out of the world pass', () => {
    const { scene, graphics } = makeScene();
    scene.addObject('world', { mesh: 'cube', position: [0, 20, 50] });
    scene.addObject('b1', hudButton('b1'));
    scene.render();
    expect(graphics.drawMesh).toHaveBeenCalledTimes(1); // only the world cube
    expect(graphics.drawMesh.mock.calls[0][2].uModelMatrix[13]).toBe(20);
  });

  it('draws the displayed HUD last, after clearing depth, in an orthographic full-bright view', () => {
    const { scene, graphics, order } = makeScene();
    scene.addObject('world', { mesh: 'cube', position: [0, 20, 50] });
    scene.addObject('b1', hudButton('b1'));
    scene.addObject('other', hudButton('other', { hudRoot: 'someone-else' }));
    scene.setDisplayedHud('root');
    scene.render();

    expect(order).toEqual(['clear', 'draw', 'clearDepth', 'draw']);
    const call = graphics.drawMesh.mock.calls[1][2];
    expect(call.uFullBright).toBe(true);
    expect(Array.from(call.uViewMatrix)).toEqual([1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1]);
    expect(call.uProjectionMatrix[0]).toBeCloseTo(1 / 2, 6); // aspect 2
    expect(call.uProjectionMatrix[15]).toBe(1); // orthographic, not perspective
    expect(call.uProjectionMatrix[11]).toBe(0);
  });

  it('fits the HUD to the view: centred, with its largest side filling the requested height', () => {
    const { scene, graphics } = makeScene();
    scene.addObject('b1', hudButton('b1')); // 1 wide, 0.5 tall
    scene.setDisplayedHud('root', 1);
    scene.render();
    const m = graphics.drawMesh.mock.calls[0][2].uModelMatrix as Float32Array;
    // The prim's centre lands at the screen centre.
    expect(m[12]).toBeCloseTo(0, 5);
    expect(m[13]).toBeCloseTo(0, 5);
    // Its width (SL y, mapped to screen x) is scaled to 1 view unit: the column for SL y has length 1.
    expect(Math.hypot(m[4], m[5], m[6])).toBeCloseTo(1, 5);
  });

  it('draws blended prims after opaque ones, far to near', () => {
    const { scene, order } = makeScene(['glass']);
    scene.addObject('glass', hudButton('glass', { texture: 'glass', position: [0.3, 0, 0] }));
    scene.addObject('solid', hudButton('solid', { position: [0.1, 0, 0] }));
    scene.setDisplayedHud('root');
    scene.render();
    expect(order.slice(-2)).toEqual(['draw', 'blend']);
  });

  it('lets a HUD be switched off again', () => {
    const { scene, graphics } = makeScene();
    scene.addObject('b1', hudButton('b1'));
    scene.setDisplayedHud('root');
    scene.setDisplayedHud(null);
    scene.render();
    expect(graphics.drawMesh).not.toHaveBeenCalled();
  });

  it('clamps the zoom to a sensible range', () => {
    const { scene } = makeScene();
    scene.setDisplayedHud('root', 99);
    expect(scene.displayedHud!.size).toBeLessThan(5);
    scene.setDisplayedHud('root', -3);
    expect(scene.displayedHud!.size).toBeGreaterThan(0);
  });
});

describe('HUD picking', () => {
  it('finds the button under the cursor and misses empty space', () => {
    const { scene } = makeScene();
    scene.addObject('left', hudButton('left', { position: [0, 1, 0] }));
    scene.addObject('right', hudButton('right', { position: [0, -1, 0] }));
    scene.setDisplayedHud('root');
    // SL +y is screen-left, so 'left' is on the left half of the view.
    // The fitted HUD spans +-0.5 of a view 4 units wide: its halves sit near x = +-0.33, i.e. pixels 333 and 467.
    const leftHit = scene.pickHud(333, 300, 800, 600);
    const rightHit = scene.pickHud(467, 300, 800, 600);
    expect(leftHit?.id).toBe('left');
    expect(rightHit?.id).toBe('right');
    expect(scene.pickHud(5, 5, 800, 600)).toBeNull();
    expect(scene.pickHud(10, 10, 0, 0)).toBeNull();
  });

  it('picks the nearer of two overlapping prims (SL +x is away from the viewer)', () => {
    const { scene } = makeScene();
    scene.addObject('back', hudButton('back', { position: [0.2, 0, 0] }));
    scene.addObject('front', hudButton('front', { position: [0, 0, 0] }));
    scene.setDisplayedHud('root');
    expect(scene.pickHud(400, 300, 800, 600)?.id).toBe('front');
  });

  it('ignores HUD prims for ordinary world picking, and world objects for HUD picking', () => {
    const { scene } = makeScene();
    scene.addObject('b1', hudButton('b1'));
    scene.addObject('world', { mesh: 'cube', position: [0, 20, 50], scale: [2, 2, 2] });
    scene.setDisplayedHud('root');
    expect(scene.pick(400, 300, 800, 600)?.id).toBe('world');
    expect(scene.pickHud(400, 300, 800, 600)?.id).toBe('b1');
    scene.setDisplayedHud(null);
    expect(scene.pickHud(400, 300, 800, 600)).toBeNull();
  });
});

describe('textures with transparency', () => {
  it('are blended automatically unless a material says otherwise', () => {
    const { scene, graphics } = makeScene(['leaf']);
    scene.addObject('tree', { mesh: 'cube', position: [0, 20, 50], texture: 'leaf', faces: [{ texture: 'leaf' }] });
    scene.addObject('wall', { mesh: 'cube', position: [0, 21, 50], texture: 'brick' });
    scene.addObject('masked', { mesh: 'cube', position: [0, 22, 50], faces: [{ texture: 'leaf', pbr: { alphaMode: 'MASK' } }] });
    scene.render();
    const modes = Object.fromEntries(graphics.drawMesh.mock.calls.map((c: any[]) => [c[2].uModelMatrix[13], c[2].uAlphaMode]));
    expect(modes[20]).toBe(2); // transparent texture -> blend
    expect(modes[21]).toBe(0); // opaque texture
    expect(modes[22]).toBe(1); // explicit MASK is respected
  });

  it('treat a translucent colour as blended too', () => {
    const { scene, graphics } = makeScene();
    scene.addObject('tint', { mesh: 'cube', position: [0, 20, 50], color: [1, 0, 0, 0.4] });
    scene.addObject('solid', { mesh: 'cube', position: [0, 21, 50], color: [1, 0, 0, 1] });
    scene.render();
    const modes = Object.fromEntries(graphics.drawMesh.mock.calls.map((c: any[]) => [c[2].uModelMatrix[13], c[2].uAlphaMode]));
    expect(modes).toEqual({ 20: 2, 21: 0 });
  });

  it('are drawn after opaque objects so blending composes correctly', () => {
    const { scene, graphics } = makeScene(['leaf']);
    scene.addObject('tree', { mesh: 'cube', position: [0, 10, 50], faces: [{ texture: 'leaf' }] });
    scene.addObject('wall', { mesh: 'cube', position: [0, 30, 50] });
    scene.render();
    expect(graphics.drawMesh.mock.calls.map((c: any[]) => c[2].uModelMatrix[13])).toEqual([30, 10]);
  });
});
