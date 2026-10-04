import { describe, expect, it, vi } from 'vitest';
import { WorldViewer } from '../world';
import { Utils } from '../utils';
import { Scene3D } from '../scene-3d';
import { Camera3D } from '../camera-3d';

class ProtocolStub extends Utils.EventEmitter {
  connected = true;
  authReply: Record<string, any> | null = null;
  agentId = 'agent-1';
  touchObject = vi.fn(async (_: any) => ({ touched: 'x' }));
}

const AVATAR = { id: 'agent-1', localId: 100, avatar: true, position: [128, 128, 30], scale: [1, 1, 1], rotation: [0, 0, 0, 1] };
const hudRoot = (extra: any = {}) => ({ id: 'hud-root', localId: 200, parentId: 100, attachmentPoint: 35, name: 'Combat HUD', position: [0, 0, 0], scale: [0.02, 1, 0.5], rotation: [0, 0, 0, 1], ...extra });
const child = (extra: any = {}) => ({ id: 'hud-btn', localId: 201, parentId: 200, attachmentPoint: 0, name: 'Button', position: [0, 0.2, 0.1], scale: [0.02, 0.2, 0.1], rotation: [0, 0, 0, 1], ...extra });

function setup() {
  const protocol = new ProtocolStub();
  const world = new WorldViewer(protocol as any);
  const add = (o: any) => protocol.emit('scene:object-add', o);
  return { protocol, world, add };
}

describe('worn HUD detection', () => {
  it('lists a HUD with all of its linked prims and the attachment point name', () => {
    const { world, add } = setup();
    add(AVATAR); add(hudRoot()); add(child());
    expect(world.getHuds()).toEqual([{ id: 'hud-root', name: 'Combat HUD', attachmentPoint: 35, pointName: 'Center', memberIds: expect.arrayContaining(['hud-root', 'hud-btn']) }]);
    expect(world.getHuds()[0].memberIds).toHaveLength(2);
  });

  it('does not treat ordinary attachments, other points, or world objects as HUDs', () => {
    const { world, add } = setup();
    add(AVATAR);
    add(hudRoot({ id: 'hat', localId: 300, attachmentPoint: 2 })); // Skull
    add(hudRoot({ id: 'hud-39', localId: 301, attachmentPoint: 39 })); // Neck
    add(hudRoot({ id: 'hud-30', localId: 302, attachmentPoint: 30 })); // Right Pec, just below the HUD range
    add({ id: 'rock', localId: 303, parentId: 0, attachmentPoint: 35, position: [1, 1, 1], scale: [1, 1, 1], rotation: [0, 0, 0, 1] }); // no parent: not worn
    expect(world.getHuds()).toEqual([]);
  });

  it('announces changes once, and again when a prim is added or removed', () => {
    const { world, protocol, add } = setup();
    const seen: any[] = [];
    world.on('huds_changed', (huds: any[]) => seen.push(huds.map((h) => h.memberIds.length)));
    add(AVATAR); add(hudRoot());
    expect(seen).toEqual([[1]]);
    add(hudRoot({ position: [0, 0, 0.1] })); // a move changes nothing in the list
    expect(seen).toEqual([[1]]);
    add(child());
    expect(seen).toEqual([[1], [2]]);
    protocol.emit('scene:object-remove', { localId: 201 });
    expect(seen.at(-1)).toEqual([1]);
    protocol.emit('scene:object-remove', { localId: 200 });
    expect(seen.at(-1)).toEqual([]);
  });

  it('recognises a prim whose root arrives after it', () => {
    const { world, add } = setup();
    add(AVATAR); add(child());
    expect(world.getHuds()).toEqual([]);
    add(hudRoot());
    expect(world.getHuds()[0].memberIds).toHaveLength(2);
  });

  it('survives a parent loop', () => {
    const { world, add } = setup();
    add({ id: 'a', localId: 1, parentId: 2, position: [0, 0, 0], scale: [1, 1, 1], rotation: [0, 0, 0, 1] });
    add({ id: 'b', localId: 2, parentId: 1, position: [0, 0, 0], scale: [1, 1, 1], rotation: [0, 0, 0, 1] });
    expect(world.getHuds()).toEqual([]);
  });
});

describe('displaying a HUD', () => {
  it('shows only known HUDs, clamps zoom, and clears when the HUD goes away', () => {
    const { world, protocol, add } = setup();
    const displays: any[] = [];
    world.on('hud_display_changed', (d: any) => displays.push(d && { ...d }));
    add(AVATAR); add(hudRoot());
    expect(world.setDisplayedHud('nope')).toBe(false);
    expect(world.displayedHud).toBeNull();
    expect(world.setDisplayedHud('hud-root')).toBe(true);
    expect(world.displayedHud).toMatchObject({ id: 'hud-root', size: 1 });
    world.zoomHud(1);
    expect(world.displayedHud!.size).toBeGreaterThan(1);
    for (let i = 0; i < 20; i++) world.zoomHud(1);
    expect(world.displayedHud!.size).toBeLessThanOrEqual(2);
    for (let i = 0; i < 40; i++) world.zoomHud(-1);
    expect(world.displayedHud!.size).toBeGreaterThan(0);
    protocol.emit('scene:object-remove', { localId: 200 });
    expect(world.displayedHud).toBeNull();
    expect(displays.at(-1)).toBeNull();
  });

  it('forgets everything when the region is left', () => {
    const { world, protocol, add } = setup();
    add(AVATAR); add(hudRoot());
    world.setDisplayedHud('hud-root');
    protocol.emit('disconnected', {});
    expect(world.getHuds()).toEqual([]);
    expect(world.displayedHud).toBeNull();
  });
});

describe('touching', () => {
  it('sends a touch through the connection and reports a failure instead of hiding it', async () => {
    const { world, protocol, add } = setup();
    add(AVATAR); add(hudRoot());
    expect(await world.touchObject('hud-root')).toBe(true);
    expect(protocol.touchObject).toHaveBeenCalledWith({ id: 'hud-root' });

    const failures: any[] = [];
    world.on('action_failed', (f: any) => failures.push(f));
    protocol.touchObject.mockRejectedValueOnce(new Error('object is gone'));
    expect(await world.touchObject('hud-root')).toBe(false);
    expect(failures).toEqual([{ action: 'touch', message: 'object is gone' }]);
  });

  it('touches the selected object only when there is one', async () => {
    const { world, protocol } = setup();
    expect(await world.touchSelected()).toBe(false);
    expect(protocol.touchObject).not.toHaveBeenCalled();
  });
});

describe('HUD placement in the scene', () => {
  function withScene() {
    const { world, protocol, add } = setup();
    const graphics = { clear: vi.fn(), clearDepth: vi.fn(), drawMesh: vi.fn(), setClearColor: vi.fn(), createMesh: vi.fn(), createRenderTarget: vi.fn(), beginRenderTarget: vi.fn(() => false), endRenderTarget: vi.fn(), getMeshBounds: vi.fn(() => ({ min: [-0.5, -0.5, -0.5], max: [0.5, 0.5, 0.5] })), textureHasAlpha: vi.fn(() => false) };
    const scene = new Scene3D(graphics as any, new Camera3D());
    (world as any).scene3d = scene;
    return { world, protocol, add, scene, graphics };
  }

  it('flags HUD prims for the HUD pass and keeps the avatar\'s world position out of their transforms', () => {
    const { add, scene } = withScene();
    add(AVATAR);
    add(hudRoot({ position: [0.1, 0.4, -0.2] }));
    add(child({ position: [0, 0.2, 0.1] }));
    const root = scene.objects.get('hud-root');
    const button = scene.objects.get('hud-btn');
    expect(root.hud).toBe(true);
    expect(root.hudRoot).toBe('hud-root');
    expect(root.position).toEqual([0.1, 0.4, -0.2]); // its own offset, not avatar (128,128,30) + offset
    expect(button.hud).toBe(true);
    expect(button.hudRoot).toBe('hud-root');
    // The child is relative to the root only.
    expect(button.position[0]).toBeCloseTo(0.1, 6);
    expect(button.position[1]).toBeCloseTo(0.6, 6);
    expect(button.position[2]).toBeCloseTo(-0.1, 6);
  });

  it('leaves ordinary attachments and world objects in the world', () => {
    const { add, scene } = withScene();
    add(AVATAR);
    add(hudRoot({ id: 'hat', localId: 300, attachmentPoint: 2, position: [0, 0, 0.2] }));
    add({ id: 'rock', localId: 301, parentId: 0, position: [10, 10, 25], scale: [1, 1, 1], rotation: [0, 0, 0, 1] });
    expect(scene.objects.get('hat').hud).toBe(false);
    expect(scene.objects.get('rock').hud).toBe(false);
    // The hat follows the avatar through its skull joint (which has a small
    // lateral rest offset), rather than being mistaken for a screen-space HUD.
    expect(scene.objects.get('hat').position[0]).toBeGreaterThan(127.9);
    expect(scene.objects.get('hat').position[0]).toBeLessThan(128);
  });

  it('re-flags a prim that was drawn in the world before its HUD root arrived', () => {
    const { add, scene } = withScene();
    add(AVATAR);
    add(child());
    expect(scene.objects.get('hud-btn').hud).toBe(false);
    add(hudRoot());
    expect(scene.objects.get('hud-btn').hud).toBe(true);
  });

  it('draws the displayed HUD in the HUD pass and a tap on it touches the prim', async () => {
    const { world, protocol, add, scene, graphics } = withScene();
    add(AVATAR); add(hudRoot()); add(child());
    world.setDisplayedHud('hud-root');
    expect(scene.displayedHud?.rootId).toBe('hud-root');
    scene.render();
    expect(graphics.clearDepth).toHaveBeenCalledOnce();

    (world as any).canvas = { getBoundingClientRect: () => ({ width: 800, height: 600, left: 0, top: 0 }) };
    const touched = world.touchHudAt(400, 300);
    expect(touched?.id).toBeTruthy();
    await Promise.resolve();
    expect(protocol.touchObject).toHaveBeenCalledWith({ id: touched!.id });
    expect(world.touchHudAt(2, 2)).toBeNull(); // empty corner of the view
  });
});
