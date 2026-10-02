import { describe, expect, it, vi } from 'vitest';
import { WorldViewer } from '../world';
import { Utils } from '../utils';

class ProtocolStub extends Utils.EventEmitter {
  connected = false;
  authReply: Record<string, any> | null = null;
}

describe('WorldViewer data status', () => {
  it('reports the live simulator stream after the protocol connects', () => {
    const protocol = new ProtocolStub();
    const world = new WorldViewer(protocol);

    expect(world.liveSceneSupported).toBe(false);
    expect(world.getDataStatus()).toBe('Disconnected');

    protocol.connected = true;
    expect(world.liveSceneSupported).toBe(true);
    expect(world.getDataStatus()).toBe('Live simulator scene: streaming from grid…');

    protocol.authReply = { native_scene: true };
    expect(world.liveSceneSupported).toBe(true);
    expect(world.getDataStatus()).toBe('Live simulator scene: streaming from grid…');
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

  it('merges terse UDP updates without losing full prim data', () => {
    const protocol = new ProtocolStub();
    const world = new WorldViewer(protocol);
    protocol.emit('scene:object-add', {
      id: 'prim', localId: 3, shape: 'cylinder', color: [1, 0, 0, 1],
      position: [1, 2, 3], scale: [2, 2, 4], rotation: [0, 0, 0, 1],
    });
    protocol.emit('scene:object-update', { id: 'prim', localId: 3, position: [4, 5, 6] });

    expect(world.objects[0]).toMatchObject({
      shape: 'cylinder', color: [1, 0, 0, 1], scale: [2, 2, 4], position: [4, 5, 6],
    });
  });

  it('retains simulator terrain and WindLight data received before WebGL mounts', () => {
    const protocol = new ProtocolStub();
    const world = new WorldViewer(protocol);
    const heights = [1, 2, 3, 4];
    const environment = { currentSky: { blueHorizon: [0.2, 0.3, 0.5] }, dayLength: 14400 };

    protocol.emit('scene:world-data', {
      region: { name: 'Live Region', x: 1000, y: 1001 },
      environment,
      terrain: { size: 2, heights },
    });

    expect(world.region).toMatchObject({ name: 'Live Region', x: 1000, y: 1001 });
    expect(world.environment).toEqual(environment);
    expect(world.terrain).toEqual({ size: 2, heights });
  });

  it('resolves linked child prim positions and reapplies them when the root moves', () => {
    const protocol = new ProtocolStub();
    const world = new WorldViewer(protocol);
    const addObject = vi.fn();
    const updateObject = vi.fn();
    (world as any).scene3d = { objects: new Map(), addObject, updateObject };

    protocol.emit('scene:object-add', {
      id: 'root', localId: 10, position: [10, 20, 30], scale: [1, 1, 1], rotation: [0, 0, 0, 1],
    });
    (world as any).scene3d.objects.set('root', {});
    protocol.emit('scene:object-add', {
      id: 'child', localId: 11, parentId: 10, position: [2, 3, 4], scale: [1, 1, 1], rotation: [0, 0, 0, 1],
    });
    (world as any).scene3d.objects.set('child', {});
    expect(addObject).toHaveBeenLastCalledWith('child', expect.objectContaining({ position: [12, 23, 34] }));

    protocol.emit('scene:object-update', { id: 'root', localId: 10, position: [20, 30, 40] });
    expect(updateObject).toHaveBeenCalledWith('child', expect.objectContaining({ position: [22, 33, 44] }));
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

  it('receives avatar_presence updates and accurately maps coordinates, distances, and bearings', () => {
    const protocol = new ProtocolStub() as any;
    protocol.connected = true;
    protocol.agentId = 'self-agent-id';
    const world = new WorldViewer(protocol);
    const nearbyListener = vi.fn();
    world.on('nearby_changed', nearbyListener);

    // 1. Self position update via avatar_presence
    protocol.emit('avatar_presence', {
      id: 'self-agent-id',
      coordinates: [100, 100, 20],
      presence: 'online',
    });
    expect(world.avatarPosition).toEqual([100, 100, 20]);

    // 2. Nearby resident enters with array coordinates
    protocol.emit('avatar_presence', {
      id: 'resident-alpha',
      name: 'Alpha Resident',
      coordinates: [100, 110, 20], // 10m North
      presence: 'entered',
    });

    expect(world.nearbyUsers).toHaveLength(1);
    expect(world.nearbyUsers[0]).toMatchObject({
      id: 'resident-alpha',
      name: 'Alpha Resident',
      position: [100, 110, 20],
      distance: 10,
      bearing: 0, // Due North
    });
    expect(nearbyListener).toHaveBeenCalledTimes(1);

    // 3. Nearby resident moves East with object coordinates { x, y, z }
    protocol.emit('avatar_presence', {
      id: 'resident-beta',
      name: 'Beta Resident',
      coordinates: { x: 110, y: 100, z: 20 }, // 10m East
      presence: 'online',
    });

    expect(world.nearbyUsers).toHaveLength(2);
    const beta = world.nearbyUsers.find(u => u.id === 'resident-beta');
    expect(beta).toMatchObject({
      id: 'resident-beta',
      name: 'Beta Resident',
      position: [110, 100, 20],
      distance: 10,
      bearing: 90, // Due East
    });

    // 4. Resident departures (left: true)
    protocol.emit('avatar_presence', {
      id: 'resident-alpha',
      left: true,
      presence: 'left',
    });
    expect(world.nearbyUsers).toHaveLength(1);
    expect(world.nearbyUsers[0].id).toBe('resident-beta');
  });

  it('handles batch avatar_presence updates and string coordinates', () => {
    const protocol = new ProtocolStub() as any;
    protocol.connected = true;
    protocol.agentId = 'my-avatar';
    const world = new WorldViewer(protocol);

    // Batch update in AgentData format
    protocol.emit('avatar_presence', {
      AgentData: [
        { AgentID: 'my-avatar', Position: [0, 0, 0] },
        { AgentID: 'gamma-avatar', Name: 'Gamma Resident', Position: '<30, 40, 0>' },
      ],
    });

    expect(world.avatarPosition).toEqual([0, 0, 0]);
    expect(world.nearbyUsers).toHaveLength(1);
    expect(world.nearbyUsers[0]).toMatchObject({
      id: 'gamma-avatar',
      name: 'Gamma Resident',
      position: [30, 40, 0],
      distance: 50, // 3-4-5 triangle: hypot(30, 40) = 50
    });
  });
});
