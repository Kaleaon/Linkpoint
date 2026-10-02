import { describe, expect, it } from 'vitest';
import { WorldViewer } from '../world';
import { Utils } from '../utils';

class ProtocolStub extends Utils.EventEmitter { connected = true; authReply: Record<string, any> | null = null; }

function setup() {
  const protocol = new ProtocolStub();
  const world = new WorldViewer(protocol);
  const objects = new Map<string, any>();
  const uploaded: Array<{ key: string; faces: any[] }> = [];
  const scene: any = {
    objects, graphics: { maxJoints: 110 },
    addObject: (id: string, c: any) => { objects.set(id, { ...c }); },
    updateObject: (id: string, u: any) => { Object.assign(objects.get(id) || {}, u); },
    removeObject: (id: string) => { objects.delete(id); },
    addAssetMesh: () => [], addSkinnedMesh: () => '',
    addVolumeMeshes: (key: string, faces: any[]) => { uploaded.push({ key, faces }); return faces.map((f) => ({ mesh: `volume:${key}:${f.faceIndex}`, materialIndex: f.faceIndex })); },
  };
  (world as any).scene3d = scene;
  return { protocol, scene, uploaded };
}

const shape = (over: Record<string, number> = {}) => ({
  pathCurve: 0x10, profileCurve: 0x01, pathBegin: 0, pathEnd: 1, pathScaleX: 1, pathScaleY: 1, pathShearX: 0, pathShearY: 0,
  pathTwist: 0, pathTwistBegin: 0, pathRadiusOffset: 0, pathTaperX: 0, pathTaperY: 0, pathRevolutions: 1, pathSkew: 0,
  profileBegin: 0, profileEnd: 1, profileHollow: 0, ...over,
});
const prim = (id: string, localId: number, shapeParams: any, extra: Record<string, any> = {}) => ({
  id, localId, avatar: false, shape: 'cube', position: [1, 2, 3], rotation: [0, 0, 0, 1], scale: [2, 2, 2], shapeParams, ...extra,
});

describe('prim geometry in the world', () => {
  it('draws a prim with its real SL volume, one mesh per texture face', () => {
    const { protocol, scene } = setup();
    protocol.emit('scene:object-add', prim('box', 1, shape()));
    const meshes = scene.objects.get('box').meshes;
    expect(meshes.map((m: any) => m.materialIndex)).toEqual([0, 1, 2, 3, 4, 5]);
    expect(scene.objects.get('box').scale).toEqual([2, 2, 2]);
  });

  it('hollow and cut prims get their extra faces', () => {
    const { protocol, scene } = setup();
    protocol.emit('scene:object-add', prim('hollow', 1, shape({ profileHollow: 0.5 })));
    expect(scene.objects.get('hollow').meshes).toHaveLength(7);
    protocol.emit('scene:object-add', prim('cut', 2, shape({ profileBegin: 0.25, profileEnd: 0.75 })));
    expect(scene.objects.get('cut').meshes).toHaveLength(6);
  });

  it('shares geometry between prims with the same shape and regenerates when it changes', () => {
    const { protocol, scene, uploaded } = setup();
    protocol.emit('scene:object-add', prim('a', 1, shape()));
    protocol.emit('scene:object-add', prim('b', 2, shape()));
    expect(uploaded).toHaveLength(1);
    expect(scene.objects.get('a').meshes).toBe(scene.objects.get('b').meshes);
    protocol.emit('scene:object-update', prim('a', 1, shape({ pathTwist: 0.5 })));
    expect(uploaded).toHaveLength(2);
    expect(scene.objects.get('a').meshes).not.toBe(scene.objects.get('b').meshes);
  });

  it('leaves meshes, sculpts and prims without shape data on their existing paths', () => {
    const { protocol, scene, uploaded } = setup();
    protocol.emit('scene:object-add', prim('mesh', 1, shape(), { assetId: 'asset-1', assetKind: 'mesh' }));
    protocol.emit('scene:object-add', prim('bare', 2, undefined));
    protocol.emit('scene:object-add', prim('undef', 3, { pathCurve: undefined, profileCurve: undefined }));
    expect(uploaded).toHaveLength(0);
    for (const id of ['mesh', 'bare', 'undef']) expect(scene.objects.get(id).meshes ?? null).toBeNull();
  });

  it('falls back to the basic shape when generation fails', () => {
    const { protocol, scene } = setup();
    protocol.emit('scene:object-add', prim('bad', 1, shape({ profileBegin: 0.9, profileEnd: 0.9 })));
    expect(scene.objects.get('bad').meshes ?? null).toBeNull();
  });
});
