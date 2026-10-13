// @vitest-environment jsdom
import { describe, expect, it, vi } from 'vitest';
import { Graphics3D } from '../graphics-3d';
import { WorldViewer } from '../world';
import { Utils } from '../utils';

function createFakeGl() {
  const gl = {
    getParameter: vi.fn((param) => {
      if (param === 'MAX_TEXTURE_SIZE') return 4096;
      if (param === 'MAX_VERTEX_ATTRIBS') return 16;
      if (param === 'MAX_VERTEX_UNIFORM_VECTORS') return 128;
      return 0;
    }),
    getExtension: vi.fn((ext) => {
      if (ext === 'OES_vertex_array_object') {
        return {
          createVertexArrayOES: vi.fn().mockReturnValue({}),
          bindVertexArrayOES: vi.fn(),
          deleteVertexArrayOES: vi.fn(),
        };
      }
      return null;
    }),
    enable: vi.fn(),
    disable: vi.fn(),
    cullFace: vi.fn(),
    frontFace: vi.fn(),
    depthFunc: vi.fn(),
    clearColor: vi.fn(),
    clear: vi.fn(),
    createShader: vi.fn().mockReturnValue({}),
    shaderSource: vi.fn(),
    compileShader: vi.fn(),
    getShaderParameter: vi.fn().mockReturnValue(true),
    createProgram: vi.fn().mockReturnValue({}),
    attachShader: vi.fn(),
    bindAttribLocation: vi.fn(),
    linkProgram: vi.fn(),
    getProgramParameter: vi.fn().mockReturnValue(1),
    getActiveAttrib: vi.fn().mockReturnValue({ name: 'aPosition' }),
    getAttribLocation: vi.fn().mockReturnValue(0),
    getActiveUniform: vi.fn().mockReturnValue({ name: 'uModelMatrix', size: 1 }),
    getUniformLocation: vi.fn().mockReturnValue({}),
    createBuffer: vi.fn().mockReturnValue({}),
    bindBuffer: vi.fn(),
    bufferData: vi.fn(),
    createTexture: vi.fn().mockReturnValue({}),
    bindTexture: vi.fn(),
    pixelStorei: vi.fn(),
    texImage2D: vi.fn(),
    texParameteri: vi.fn(),
    texParameterf: vi.fn(),
    generateMipmap: vi.fn(),
    deleteTexture: vi.fn(),
    deleteProgram: vi.fn(),
    deleteBuffer: vi.fn(),
    useProgram: vi.fn(),
    enableVertexAttribArray: vi.fn(),
    disableVertexAttribArray: vi.fn(),
    vertexAttribPointer: vi.fn(),
    drawElements: vi.fn(),
    depthMask: vi.fn(),
    viewport: vi.fn(),
  };
  return gl;
}

class ProtocolStub extends Utils.EventEmitter {
  connected = false;
}

describe('WebGL context loss handling', () => {
  it('dispatches webglcontextlost, calls preventDefault, sets isContextLost, and suppresses draw calls', async () => {
    vi.spyOn(console, 'log').mockImplementation(() => undefined);
    const canvas = document.createElement('canvas');
    const fakeGl = createFakeGl();
    vi.spyOn(canvas, 'getContext').mockImplementation(() => fakeGl as any);

    const graphics = new Graphics3D(canvas);
    await graphics.init();

    expect(graphics.isContextLost).toBe(false);

    let contextLostFired = false;
    graphics.on('contextlost', () => {
      contextLostFired = true;
    });

    const event = new Event('webglcontextlost', { cancelable: true });
    const preventDefaultSpy = vi.spyOn(event, 'preventDefault');

    canvas.dispatchEvent(event);

    expect(preventDefaultSpy).toHaveBeenCalled();
    expect(graphics.isContextLost).toBe(true);
    expect(contextLostFired).toBe(true);

    fakeGl.clear.mockClear();
    fakeGl.drawElements.mockClear();

    graphics.clear();
    graphics.drawMesh('cube', 'basic', {});

    expect(fakeGl.clear).not.toHaveBeenCalled();
    expect(fakeGl.drawElements).not.toHaveBeenCalled();

    vi.restoreAllMocks();
  });

  it('dispatches webglcontextrestored, re-initializes shaders, sets isContextLost false, and emits contextrestored', async () => {
    vi.spyOn(console, 'log').mockImplementation(() => undefined);
    const canvas = document.createElement('canvas');
    const fakeGl = createFakeGl();
    vi.spyOn(canvas, 'getContext').mockImplementation(() => fakeGl as any);

    const graphics = new Graphics3D(canvas);
    await graphics.init();

    const lostEvent = new Event('webglcontextlost', { cancelable: true });
    canvas.dispatchEvent(lostEvent);
    expect(graphics.isContextLost).toBe(true);

    let contextRestoredFired = false;
    graphics.on('contextrestored', () => {
      contextRestoredFired = true;
    });

    const createShadersSpy = vi.spyOn(graphics, 'createDefaultShaders');

    const restoredEvent = new Event('webglcontextrestored');
    canvas.dispatchEvent(restoredEvent);

    await new Promise((resolve) => setTimeout(resolve, 10));

    expect(graphics.isContextLost).toBe(false);
    expect(contextRestoredFired).toBe(true);
    expect(createShadersSpy).toHaveBeenCalled();

    vi.restoreAllMocks();
  });

  it('WorldViewer pauses rendering while context is lost and re-initializes scene when context restores', async () => {
    vi.spyOn(console, 'log').mockImplementation(() => undefined);
    const canvas = document.createElement('canvas');
    const fakeGl = createFakeGl();
    vi.spyOn(canvas, 'getContext').mockImplementation(() => fakeGl as any);

    const protocol = new ProtocolStub();
    const world = new WorldViewer(protocol);

    let rafCallback: ((time: number) => void) | null = null;
    vi.spyOn(globalThis, 'requestAnimationFrame').mockImplementation((cb: FrameRequestCallback) => {
      rafCallback = cb as any;
      return 1;
    });

    await world.init(canvas);

    const scene3d = world.scene3d!;
    const renderSpy = vi.spyOn(scene3d, 'render').mockImplementation(() => undefined);

    if (rafCallback) (rafCallback as any)(1000);
    expect(renderSpy).toHaveBeenCalledTimes(1);
    renderSpy.mockClear();

    const lostEvent = new Event('webglcontextlost', { cancelable: true });
    canvas.dispatchEvent(lostEvent);

    if (rafCallback) (rafCallback as any)(1016);
    expect(renderSpy).not.toHaveBeenCalled();

    const createPrimitivesSpy = vi.spyOn(scene3d, 'createDefaultPrimitives');

    const restoredEvent = new Event('webglcontextrestored');
    canvas.dispatchEvent(restoredEvent);
    await new Promise((resolve) => setTimeout(resolve, 10));

    expect(createPrimitivesSpy).toHaveBeenCalled();

    if (rafCallback) (rafCallback as any)(1032);
    expect(renderSpy).toHaveBeenCalledTimes(1);

    world.stopRendering();
    world.destroyRenderer();
    vi.restoreAllMocks();
  });
});
