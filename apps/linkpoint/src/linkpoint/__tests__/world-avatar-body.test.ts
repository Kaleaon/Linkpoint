import { readFileSync } from 'node:fs';
import { describe, expect, it } from 'vitest';
import { WorldViewer } from '../world';
import { Utils } from '../utils';
import { AvatarAnimator } from '../avatar-animator';
import { parseBodyPart, type BodyPartMeta } from '../avatar-body';
import type { KeyframeAnimation } from '../avatar-animation';

const dir = `${__dirname}/../../../public/avatar/`;
const meta = JSON.parse(readFileSync(`${dir}meshes.json`, 'utf8')) as Record<string, BodyPartMeta>;
const loadParts = () => new Map(Object.keys(meta).map((part) => {
  const b = Buffer.from(readFileSync(`${dir}${part}.bin.base64`, 'utf8').trim(), 'base64');
  return [part, parseBodyPart(b.buffer.slice(b.byteOffset, b.byteOffset + b.byteLength) as ArrayBuffer, meta[part])] as const;
}));

class ProtocolStub extends Utils.EventEmitter { connected = true; authReply: Record<string, any> | null = null; }

function setup(withBody: boolean) {
  const protocol = new ProtocolStub();
  const world = new WorldViewer(protocol);
  const objects = new Map<string, any>();
  const scene: any = {
    objects, graphics: { maxJoints: 110 },
    addObject: (id: string, c: any) => { objects.set(id, { ...c }); },
    updateObject: (id: string, u: any) => { Object.assign(objects.get(id) || {}, u); },
    removeObject: (id: string) => { objects.delete(id); },
    addAssetMesh: () => [], addSkinnedMesh: () => '', addAssetTexture: () => undefined,
  };
  (world as any).scene3d = scene;
  if (withBody) { (world as any).bodyParts = loadParts(); (world as any).bodyMeshesReadyFor = scene; }
  return { protocol, world, scene };
}

const avatar = { id: 'av', localId: 1, avatar: true, position: [10, 20, 31], rotation: [0, 0, 0, 1], scale: [0.45, 0.6, 1.9] };
const bodyIds = (scene: any) => [...scene.objects.keys()].filter((id: string) => id.startsWith('av:body:')).sort();

describe('avatar body in the world', () => {
  it('draws an avatar as skinned body parts standing on the ground and hides the placeholder', () => {
    const { protocol, scene } = setup(true);
    protocol.emit('scene:object-add', avatar);
    expect(bodyIds(scene)).toEqual(['av:body:eyeLeft', 'av:body:eyeRight', 'av:body:hair', 'av:body:head', 'av:body:eyelashes', 'av:body:lowerBody', 'av:body:upperBody'].sort());
    const head = scene.objects.get('av:body:head');
    expect(head.skin).toBeInstanceOf(Float32Array);
    expect(head.position).toEqual([10, 20, 31 - 0.95]); // feet = centre minus half the body height
    expect(head.scale).toEqual([1, 1, 1]);
    expect(head.meshes).toEqual([{ mesh: 'avatar-body:head', materialIndex: 0 }]);
    expect(scene.objects.get('av').visible).toBe(false);
    expect(scene.objects.has('av:legs')).toBe(false);
  });

  it('does not regress to block and cylinder placeholders while body meshes load', () => {
    const { protocol, world, scene } = setup(false);
    // Keep this test focused on the synchronous loading state rather than issuing real fetches.
    (world as any).loadBody = () => new Promise(() => undefined);
    protocol.emit('scene:object-add', avatar);
    expect(bodyIds(scene)).toEqual([]);
    expect(scene.objects.has('av:legs')).toBe(false);
    expect(scene.objects.get('av').visible).toBe(false);
  });

  it('re-poses the body from running animations and removes every part with the avatar', async () => {
    const { protocol, world, scene } = setup(true);
    let t = 0;
    const turn: KeyframeAnimation = { priority: 4, length: 2, expression: '', inPoint: 0, outPoint: 2, loop: true, easeIn: 0, easeOut: 0.5, handPose: 0,
      joints: [{ name: 'mTorso', priority: 4, rotations: [{ time: 0, value: [0, 0, Math.SQRT1_2, Math.SQRT1_2] }], positions: [] }] };
    (world as any).animator = new AvatarAnimator(async () => turn, () => t);
    protocol.emit('scene:object-add', avatar);
    protocol.emit('scene:animations', { kind: 'avatar', id: 'av', animations: [{ id: 'turn', seq: 1 }] });
    await new Promise((resolve) => setTimeout(resolve, 0));
    // upperBody's second joint is mTorso (the first, mPelvis, does not move with a torso turn)
    const before = Array.from(scene.objects.get('av:body:upperBody').skin.slice(12, 24));
    t = 1;
    (world as any).updateAnimatedAvatars();
    const after = Array.from(scene.objects.get('av:body:upperBody').skin.slice(12, 24));
    expect(after).not.toEqual(before);

    protocol.emit('scene:object-remove', { id: 'av', localId: 1 });
    expect(bodyIds(scene)).toEqual([]);
  });

  it('reuses Float32Array buffers for avatar body parts across frames and cleans up on removal', () => {
    const { protocol, world, scene } = setup(true);
    protocol.emit('scene:object-add', avatar);
    const upper1 = scene.objects.get('av:body:upperBody').skin;
    expect(upper1).toBeInstanceOf(Float32Array);

    (world as any).updateAnimatedAvatars();
    const upper2 = scene.objects.get('av:body:upperBody').skin;
    expect(upper2).toBe(upper1);

    expect((world as any).avatarBodyBuffers.has('av')).toBe(true);
    protocol.emit('scene:object-remove', { id: 'av', localId: 1 });
    expect((world as any).avatarBodyBuffers.has('av')).toBe(false);
  });

  const faces = (entries: Record<number, string>) => {
    const list: any[] = Array.from({ length: 21 }, () => ({ textureId: null }));
    for (const [index, textureId] of Object.entries(entries)) list[Number(index)] = { textureId };
    return list;
  };

  it('wears baked textures once downloaded: head, upper, lower, eyes and hair slots', () => {
    const { protocol, world, scene } = setup(true);
    for (const id of ['head-bake', 'upper-bake', 'lower-bake', 'eyes-bake', 'hair-bake']) (world as any).decodedTextures.set(id, {});
    protocol.emit('scene:object-add', { ...avatar, faceTextures: faces({ 8: 'head-bake', 9: 'upper-bake', 10: 'lower-bake', 11: 'eyes-bake', 20: 'hair-bake' }) });
    expect(scene.objects.get('av:body:head').faces[0].texture).toBe('texture:head-bake');
    expect(scene.objects.get('av:body:eyelashes').faces[0].texture).toBe('texture:head-bake'); // lashes share the head bake
    expect(scene.objects.get('av:body:upperBody').faces[0].texture).toBe('texture:upper-bake');
    expect(scene.objects.get('av:body:lowerBody').faces[0].texture).toBe('texture:lower-bake');
    expect(scene.objects.get('av:body:eyeLeft').faces[0].texture).toBe('texture:eyes-bake');
    expect(scene.objects.get('av:body:eyeRight').faces[0].texture).toBe('texture:eyes-bake');
    expect(scene.objects.get('av:body:hair').faces[0].pbr.alphaMode).toBe('BLEND');
    expect(scene.objects.get('av:body:upperBody').faces[0].pbr.alphaMode).toBe('MASK');
    expect(scene.objects.get('av:body:upperBody').color).toEqual([1, 1, 1, 1]);
  });

  it('keeps flat colours while a bake is missing, undownloaded, or the simulator placeholder', () => {
    const { protocol, world, scene } = setup(true);
    (world as any).decodedTextures.set('c228d1cf-4b5d-4ba8-84f4-899a0796aa97', {});
    protocol.emit('scene:object-add', { ...avatar, faceTextures: faces({ 8: 'not-downloaded-yet', 9: 'c228d1cf-4b5d-4ba8-84f4-899a0796aa97' }) });
    expect(scene.objects.get('av:body:head').faces).toEqual([]);
    expect(scene.objects.get('av:body:upperBody').faces).toEqual([]);
    expect(scene.objects.get('av:body:upperBody').color).not.toEqual([1, 1, 1, 1]);
  });

  it('applies a bake that arrives after the avatar', () => {
    const { protocol, scene } = setup(true);
    protocol.emit('scene:object-add', { ...avatar, faceTextures: faces({ 9: 'late-bake' }) });
    expect(scene.objects.get('av:body:upperBody').faces).toEqual([]);
    protocol.emit('scene:texture-ready', { assetId: 'late-bake', width: 1, height: 1, rgba: btoa('\u00ff\u00ff\u00ff\u00ff') });
    expect(scene.objects.get('av:body:upperBody').faces[0].texture).toBe('texture:late-bake');
  });

  it('keeps the last-known-good bake while a replacement is incomplete', () => {
    const { protocol, world, scene } = setup(true);
    (world as any).decodedTextures.set('good-bake', {});
    protocol.emit('scene:object-add', { ...avatar, faceTextures: faces({ 9: 'good-bake' }) });
    expect(scene.objects.get('av:body:upperBody').faces[0].texture).toBe('texture:good-bake');
    protocol.emit('scene:object-update', { ...avatar, faceTextures: faces({ 9: 'replacement-not-loaded' }) });
    expect(scene.objects.get('av:body:upperBody').faces[0].texture).toBe('texture:good-bake');
  });
});
