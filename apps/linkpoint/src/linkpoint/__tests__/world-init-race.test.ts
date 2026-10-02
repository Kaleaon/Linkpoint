// @vitest-environment jsdom
import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest';

// Graphics3D.init is held open so a test can unmount the view while it is pending.
const graphicsInstances: any[] = [];
vi.mock('../graphics-3d', () => ({
  Graphics3D: class {
    resolveInit!: () => void;
    rejectInit!: (error: Error) => void;
    destroy = vi.fn();
    resize = vi.fn();
    initPromise = new Promise<void>((resolve, reject) => { this.resolveInit = resolve; this.rejectInit = reject; });
    constructor(public canvas: HTMLCanvasElement) { graphicsInstances.push(this); }
    init() { return this.initPromise; }
  },
}));
vi.mock('../scene-3d', () => ({
  Scene3D: class {
    init = vi.fn().mockResolvedValue(undefined);
    setEnvironment = vi.fn();
    setTerrain = vi.fn();
    setDisplayedHud = vi.fn();
    addAssetMesh = vi.fn();
    render = vi.fn();
  },
}));

import { WorldViewer } from '../world';
import { Utils } from '../utils';

class ProtocolStub extends Utils.EventEmitter {
  connected = false;
  authReply: Record<string, any> | null = null;
}

const makeCanvas = () => {
  const parent = document.createElement('div');
  const canvas = document.createElement('canvas');
  parent.appendChild(canvas);
  document.body.appendChild(parent);
  return canvas;
};
const tick = () => new Promise((resolve) => setTimeout(resolve, 0));

describe('WorldViewer.init when the view goes away mid-initialisation', () => {
  beforeEach(() => {
    graphicsInstances.length = 0;
    vi.spyOn(globalThis, 'requestAnimationFrame').mockImplementation(() => 1);
    vi.spyOn(globalThis, 'cancelAnimationFrame').mockImplementation(() => undefined);
    vi.spyOn(console, 'error').mockImplementation(() => undefined);
  });
  afterEach(() => {
    vi.restoreAllMocks();
    document.body.innerHTML = '';
  });

  it('does not throw (no "reading style of null") when the canvas is torn down while graphics init is pending', async () => {
    const world = new WorldViewer(new ProtocolStub());
    const canvas = makeCanvas();
    const initialising = world.init(canvas);
    await tick();

    world.destroyRenderer(canvas); // the screen unmounted
    graphicsInstances[0].resolveInit();

    await expect(initialising).resolves.toBeUndefined();
    expect(world.graphics3d).toBeNull();
    expect(world.canvas).toBeNull();
    expect((world as any).cameraControls).toBeNull();
    expect(window.getComputedStyle(canvas).touchAction).not.toBe('none'); // no controls were attached to the dead canvas
  });

  it('lets a newer init win and leaves nothing of the stale one attached (StrictMode-style remount)', async () => {
    const world = new WorldViewer(new ProtocolStub());
    const first = makeCanvas();
    const second = makeCanvas();

    const firstInit = world.init(first);
    await tick();
    world.destroyRenderer(first);
    const secondInit = world.init(second);
    await tick();

    graphicsInstances[0].resolveInit(); // the stale one finishes first
    await expect(firstInit).resolves.toBeUndefined();
    expect(world.canvas).toBe(second);
    expect((world as any).cameraControls).toBeNull(); // the stale init must not attach controls to the new canvas

    graphicsInstances[1].resolveInit();
    await expect(secondInit).resolves.toBeUndefined();
    expect(world.canvas).toBe(second);
    expect(world.graphics3d).toBe(graphicsInstances[1]);
    expect((world as any).cameraControls).not.toBeNull();
    expect(graphicsInstances[1].destroy).not.toHaveBeenCalled();
  });

  it('does not tear down a newer renderer when a stale init fails afterwards', async () => {
    const world = new WorldViewer(new ProtocolStub());
    const first = makeCanvas();
    const second = makeCanvas();
    const firstInit = world.init(first);
    await tick();
    world.destroyRenderer(first);
    const secondInit = world.init(second);
    await tick();
    graphicsInstances[1].resolveInit();
    await secondInit;

    graphicsInstances[0].rejectInit(new Error('context lost'));
    await expect(firstInit).resolves.toBeUndefined();
    expect(world.graphics3d).toBe(graphicsInstances[1]);
    expect(graphicsInstances[1].destroy).not.toHaveBeenCalled();
  });

  it('still reports a genuine initialisation failure', async () => {
    const world = new WorldViewer(new ProtocolStub());
    const canvas = makeCanvas();
    const initialising = world.init(canvas);
    await tick();
    graphicsInstances[0].rejectInit(new Error('WebGL unavailable'));
    await expect(initialising).rejects.toThrow('WebGL unavailable');
    expect(world.graphics3d).toBeNull();
  });

  it('initialises normally when nothing interrupts it', async () => {
    const world = new WorldViewer(new ProtocolStub());
    const canvas = makeCanvas();
    const initialising = world.init(canvas);
    await tick();
    graphicsInstances[0].resolveInit();
    await initialising;
    expect(world.graphics3d).toBe(graphicsInstances[0]);
    expect((world as any).cameraControls).not.toBeNull();
    expect(canvas.style.touchAction).toBe('none');
  });
});

describe('WorldViewer.init on a canvas it already owns', () => {
  it('does not restart rendering if the view is torn down while the scene reloads', async () => {
    vi.spyOn(globalThis, 'requestAnimationFrame').mockImplementation(() => 1);
    vi.spyOn(globalThis, 'cancelAnimationFrame').mockImplementation(() => undefined);
    graphicsInstances.length = 0;
    const world = new WorldViewer(new ProtocolStub());
    const canvas = makeCanvas();
    const first = world.init(canvas);
    await tick();
    graphicsInstances[0].resolveInit();
    await first;

    const startRendering = vi.spyOn(world, 'startRendering');
    const loadScene = vi.spyOn(world, 'loadScene').mockImplementation(async () => { world.destroyRenderer(canvas); });
    await world.init(canvas);
    expect(loadScene).toHaveBeenCalled();
    expect(startRendering).not.toHaveBeenCalled();
    vi.restoreAllMocks();
  });
});
