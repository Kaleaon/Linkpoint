import { describe, expect, it, vi } from 'vitest';
import { WorldViewer } from '../world';
import { Utils } from '../utils';

class ProtocolStub extends Utils.EventEmitter {
  connected = false;
  authReply: Record<string, any> | null = null;
}

describe('WorldViewer data status', () => {
  it('does not represent login metadata as a live simulator scene', () => {
    const protocol = new ProtocolStub();
    const world = new WorldViewer(protocol);

    expect(world.liveSceneSupported).toBe(false);
    expect(world.getDataStatus()).toBe('Disconnected');

    protocol.connected = true;
    expect(world.getDataStatus()).toBe(
      'Connected: region metadata only (scene streaming unavailable)',
    );

    protocol.authReply = { native_scene: true };
    expect(world.liveSceneSupported).toBe(true);
    expect(world.getDataStatus()).toBe('Live simulator scene stream');
  });

  it('tracks decoded simulator object lifecycle before the canvas is mounted', () => {
    const protocol = new ProtocolStub();
    const world = new WorldViewer(protocol);
    const object = {
      id: 'prim-id', localId: 4, avatar: false,
      position: [1, 2, 3], scale: [1, 1, 1], rotation: [0, 0, 0, 1],
    };

    protocol.emit('scene:object-add', object);
    expect(world.objects).toEqual([object]);

    protocol.emit('scene:object-update', { ...object, position: [4, 5, 6] });
    expect(world.objects[0].position).toEqual([4, 5, 6]);

    protocol.emit('scene:object-remove', { localId: 4 });
    expect(world.objects).toEqual([]);
  });

  it('starts only one animation loop and cancels it cleanly', () => {
    const protocol = new ProtocolStub();
    const world = new WorldViewer(protocol);
    const request = vi.spyOn(globalThis, 'requestAnimationFrame').mockReturnValue(42);
    const cancel = vi.spyOn(globalThis, 'cancelAnimationFrame').mockImplementation(() => undefined);

    world.startRendering();
    world.startRendering();
    expect(request).toHaveBeenCalledOnce();

    world.stopRendering();
    expect(cancel).toHaveBeenCalledWith(42);
    world.stopRendering();
    expect(cancel).toHaveBeenCalledOnce();
    request.mockRestore();
    cancel.mockRestore();
  });
});
