import { describe, expect, it } from 'vitest';
import { WorldViewer } from '../world';
import { Utils } from '../utils';
import { AvatarAnimator } from '../avatar-animator';
import { Camera3D } from '../camera-3d';
import type { KeyframeAnimation } from '../avatar-animation';

class ProtocolStub extends Utils.EventEmitter {
  connected = true;
  agentId: string | null = null;
  authReply: Record<string, any> | null = null;
}

const identity = [1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1];

function sceneStub() {
  const objects = new Map<string, any>();
  return {
    objects,
    graphics: { maxJoints: 110 },
    addObject: (id: string, config: any) => { objects.set(id, { ...config }); },
    updateObject: (id: string, updates: any) => { Object.assign(objects.get(id) || {}, updates); },
    removeObject: (id: string) => { objects.delete(id); },
    addAssetMesh: (assetId: string, geometry: any) => (geometry.parts || [geometry]).map((_: any, i: number) => ({ mesh: `asset:${assetId}:${i}`, materialIndex: i })),
  };
}

function setup() {
  const protocol = new ProtocolStub();
  const world = new WorldViewer(protocol);
  const scene = sceneStub();
  (world as any).scene3d = scene;
  protocol.emit('scene:object-add', { id: 'avatar', localId: 1, avatar: true, position: [10, 20, 30], rotation: [0, 0, 0, 1], scale: [0.5, 0.5, 0.5] });
  return { protocol, scene, world };
}

describe('rigged mesh in the world', () => {
  it('keeps the rear camera behind the logged-in avatar', () => {
    const { protocol, world } = setup();
    protocol.agentId = 'avatar';
    const camera = new Camera3D();
    camera.setPreset('rear');
    (world as any).camera3d = camera;
    // Identity avatar rotation faces local +X, so its rear camera belongs west of it.
    protocol.emit('scene:object-update', { id: 'avatar', avatar: true, position: [10, 20, 30], rotation: [0, 0, 0, 1] });
    expect(camera.orbitTarget).toEqual([10, 20, 31.2]);
    expect(camera.position[0]).toBeLessThan(10);
    expect(camera.position[1]).toBeCloseTo(20, 5);
  });

  it('skins a rigged mesh with rest-pose joint matrices and follows its avatar, ignoring prim scale', () => {
    const { protocol, scene } = setup();
    protocol.emit('scene:object-add', {
      id: 'rig', localId: 2, parentId: 1, avatar: false, assetId: 'mesh-1', shape: 'asset-proxy',
      position: [0.3, 0, 0.1], rotation: [0, 0, 0, 1], scale: [3, 3, 3],
    });
    protocol.emit('scene:asset-ready', {
      assetId: 'mesh-1',
      geometry: {
        parts: [{ materialIndex: 0, vertices: [0, 0, 0, 1, 0, 0, 0, 1, 0], indices: [0, 1, 2], joints: [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0], jointWeights: [1, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0] }],
        skin: { jointNames: ['mPelvis'], bindShapeMatrix: identity, inverseBindMatrices: [identity] },
      },
    });
    const rig = scene.objects.get('rig');
    expect(rig.skin).toBeInstanceOf(Float32Array);
    expect(rig.skin).toHaveLength(110 * 12);
    // identity inverse bind: joint 0 (mPelvis) is lifted to the pelvis rest height (z = 1.067)
    expect(rig.skin[3]).toBeCloseTo(0, 5);
    expect(rig.skin[11]).toBeCloseTo(1.067, 2);
    // the mesh uses the avatar's transform, not the attachment offset
    expect(rig.position).toEqual([10, 20, 30]);
    expect(rig.scale).toEqual([1, 1, 1]);
    expect(rig.meshes).toEqual([{ mesh: 'asset:mesh-1:0', materialIndex: 0 }]);
  });

  it('leaves unrigged meshes alone', () => {
    const { protocol, scene } = setup();
    protocol.emit('scene:object-add', { id: 'prim', localId: 3, avatar: false, assetId: 'mesh-2', position: [1, 2, 3], rotation: [0, 0, 0, 1], scale: [1, 1, 1] });
    protocol.emit('scene:asset-ready', { assetId: 'mesh-2', geometry: { parts: [{ materialIndex: 0, vertices: [0, 0, 0, 1, 0, 0, 0, 1, 0], indices: [0, 1, 2] }] } });
    expect(scene.objects.get('prim').skin).toBeNull();
    expect(scene.objects.get('prim').position).toEqual([1, 2, 3]);
  });

  it('rebuilds joint matrices when an asset is re-decoded', () => {
    const { protocol, scene } = setup();
    protocol.emit('scene:object-add', { id: 'rig', localId: 2, parentId: 1, assetId: 'mesh-3', position: [0, 0, 0], rotation: [0, 0, 0, 1], scale: [1, 1, 1] });
    const geometry = (z: number) => ({ parts: [{ vertices: [0, 0, 0, 1, 0, 0, 0, 1, 0], indices: [0, 1, 2] }], skin: { jointNames: ['mHead'], bindShapeMatrix: [...identity.slice(0, 14), z, 1], inverseBindMatrices: [identity] } });
    protocol.emit('scene:asset-ready', { assetId: 'mesh-3', geometry: geometry(0) });
    const before = scene.objects.get('rig').skin[11];
    protocol.emit('scene:asset-ready', { assetId: 'mesh-3', geometry: geometry(1) });
    expect(scene.objects.get('rig').skin[11]).toBeCloseTo(before + 1, 3);
  });
});

describe('animated rigged mesh in the world', () => {
  const turnTorso: KeyframeAnimation = {
    priority: 4, length: 2, expression: '', inPoint: 0, outPoint: 2, loop: true, easeIn: 0, easeOut: 0.5, handPose: 0,
    // 90 degrees about Z
    joints: [{ name: 'mTorso', priority: 4, rotations: [{ time: 0, value: [0, 0, Math.SQRT1_2, Math.SQRT1_2] }], positions: [] }],
  };
  const flush = () => new Promise((resolve) => setTimeout(resolve, 0));

  it('re-poses a worn rigged mesh from its avatar\'s animations, then restores rest when they end', async () => {
    const { protocol, scene, world } = setup();
    let t = 0;
    (world as any).animator = new AvatarAnimator(async () => turnTorso, () => t);
    protocol.emit('scene:object-add', { id: 'rig', localId: 2, parentId: 1, assetId: 'mesh-9', position: [0, 0, 0], rotation: [0, 0, 0, 1], scale: [1, 1, 1] });
    protocol.emit('scene:asset-ready', {
      assetId: 'mesh-9',
      geometry: { parts: [{ vertices: [0, 0, 0, 1, 0, 0, 0, 1, 0], indices: [0, 1, 2] }], skin: { jointNames: ['mTorso'], bindShapeMatrix: identity, inverseBindMatrices: [identity] } },
    });
    const rest = Array.from(scene.objects.get('rig').skin.slice(0, 12)) as number[];
    // rest: identity rotation, torso 0.084 above the pelvis (1.067)
    expect(rest[0]).toBeCloseTo(1, 4);

    protocol.emit('scene:animations', { kind: 'avatar', id: 'avatar', animations: [{ id: 'turn', seq: 1 }] });
    await flush();
    t = 1;
    (world as any).updateAnimatedSkins();
    const posed = Array.from(scene.objects.get('rig').skin.slice(0, 12)) as number[];
    expect(posed[0]).toBeCloseTo(0, 3);   // x axis now maps to +Y: row0 = (0, -1, 0, ..)
    expect(posed[1]).toBeCloseTo(-1, 3);
    expect(posed[4]).toBeCloseTo(1, 3);

    protocol.emit('scene:animations', { kind: 'avatar', id: 'avatar', animations: [] });
    t = 5; // well past the 0.5 s ease-out
    (world as any).updateAnimatedSkins();
    expect(Array.from(scene.objects.get('rig').skin.slice(0, 12))).toEqual(rest);
  });

  it('treats an animated object as its own subject', async () => {
    const { protocol, scene, world } = setup();
    let t = 0;
    (world as any).animator = new AvatarAnimator(async () => turnTorso, () => t);
    protocol.emit('scene:object-add', { id: 'animesh', localId: 5, assetId: 'mesh-8', position: [4, 5, 6], rotation: [0, 0, 0, 1], scale: [1, 1, 1] });
    protocol.emit('scene:asset-ready', {
      assetId: 'mesh-8',
      geometry: { parts: [{ vertices: [0, 0, 0, 1, 0, 0, 0, 1, 0], indices: [0, 1, 2] }], skin: { jointNames: ['mTorso'], bindShapeMatrix: identity, inverseBindMatrices: [identity] } },
    });
    protocol.emit('scene:animations', { kind: 'object', id: 'animesh', animations: [{ id: 'turn', seq: 1 }] });
    await flush();
    t = 1;
    (world as any).updateAnimatedSkins();
    expect(scene.objects.get('animesh').skin[1]).toBeCloseTo(-1, 3);
    expect(scene.objects.get('animesh').position).toEqual([4, 5, 6]);
  });
});
