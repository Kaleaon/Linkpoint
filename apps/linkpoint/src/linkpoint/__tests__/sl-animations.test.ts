import { describe, expect, it, vi } from 'vitest';
import { createRequire } from 'node:module';

const require = createRequire(import.meta.url);
const { serializeAnimationMessage, subscribeAnimations, watchAnimations, downloadAnimation } = require('../../../core/sl-animations.cjs');
const { Message } = require('@caspertech/node-metaverse/dist/lib/enums/Message');
const { serializeObject } = require('../../../core/viewer-session.cjs');

const uuid = (value: string) => ({ toString: () => value });

describe('simulator animation messages', () => {
  it('serialises AvatarAnimation and ObjectAnimation with sequence ids', () => {
    const avatar = serializeAnimationMessage({ message: { id: Message.AvatarAnimation, Sender: { ID: uuid('agent-1') }, AnimationList: [{ AnimID: uuid('anim-a'), AnimSequenceID: 7 }, { AnimID: uuid('anim-b'), AnimSequenceID: 2 }] } });
    expect(avatar).toEqual({ kind: 'avatar', id: 'agent-1', animations: [{ id: 'anim-a', seq: 7 }, { id: 'anim-b', seq: 2 }] });
    const object = serializeAnimationMessage({ message: { id: Message.ObjectAnimation, Sender: { ID: uuid('obj-1') }, AnimationList: [{ AnimID: uuid('anim-c'), AnimSequenceID: 1 }] } });
    expect(object).toMatchObject({ kind: 'object', id: 'obj-1' });
  });

  it('ignores unrelated or malformed messages', () => {
    expect(serializeAnimationMessage(null)).toBeNull();
    expect(serializeAnimationMessage({ message: { id: Message.ChatFromSimulator, Sender: { ID: uuid('x') } } })).toBeNull();
    expect(serializeAnimationMessage({ message: { id: Message.AvatarAnimation } })).toBeNull();
  });

  it('subscribes to both message types on the region circuit and forwards payloads', () => {
    const subscribeToMessages = vi.fn((ids: number[], callback: (packet: unknown) => void) => { callback({ message: { id: Message.AvatarAnimation, Sender: { ID: uuid('agent-1') }, AnimationList: [] } }); return { unsubscribe: vi.fn() }; });
    const send = vi.fn();
    const subscription = subscribeAnimations({ circuit: { subscribeToMessages } }, send);
    expect(subscription).toBeTruthy();
    expect(subscribeToMessages.mock.calls[0][0]).toEqual([Message.AvatarAnimation, Message.ObjectAnimation]);
    expect(send).toHaveBeenCalledWith('animations', { kind: 'avatar', id: 'agent-1', animations: [] });
    expect(subscribeAnimations({}, send)).toBeNull();
  });
});

describe('following the agent across regions', () => {
  it('moves the listener to the new region circuit when the current region changes, and stops on unsubscribe', () => {
    vi.useFakeTimers();
    const makeRegion = () => { const unsubscribe = vi.fn(); return { unsubscribe, circuit: { subscribeToMessages: vi.fn(() => ({ unsubscribe })) } }; };
    const first = makeRegion(), second = makeRegion();
    let current: any = first;
    const watcher = watchAnimations(() => current, vi.fn(), 1000);
    expect(first.circuit.subscribeToMessages).toHaveBeenCalledTimes(1);
    vi.advanceTimersByTime(3000);
    expect(first.circuit.subscribeToMessages).toHaveBeenCalledTimes(1); // unchanged: no resubscribe
    current = second;
    vi.advanceTimersByTime(1000);
    expect(first.unsubscribe).toHaveBeenCalledTimes(1);
    expect(second.circuit.subscribeToMessages).toHaveBeenCalledTimes(1);
    current = undefined; // between regions: keep the old listener, do not crash
    vi.advanceTimersByTime(2000);
    expect(second.unsubscribe).not.toHaveBeenCalled();
    watcher.unsubscribe();
    expect(second.unsubscribe).toHaveBeenCalledTimes(1);
    vi.advanceTimersByTime(5000);
    expect(second.circuit.subscribeToMessages).toHaveBeenCalledTimes(1);
    vi.useRealTimers();
  });
});

describe('animated mesh flag', () => {
  const object = (flags?: number, mesh = true) => ({
    FullID: uuid('obj'), PCode: 9, Position: { x: 1, y: 2, z: 3 }, Scale: { x: 1, y: 1, z: 1 },
    extraParams: { meshData: mesh ? { meshData: uuid('mesh-asset') } : undefined, extendedMeshData: flags === undefined ? undefined : { flags } },
  });
  it('marks mesh objects whose ExtendedMesh flags enable Animesh', () => {
    expect(serializeObject({ localID: 1, object: object(1) }).animatedMesh).toBe(true);
    expect(serializeObject({ localID: 1, object: object(0) }).animatedMesh).toBe(false);
    expect(serializeObject({ localID: 1, object: object(undefined) }).animatedMesh).toBe(false);
    expect(serializeObject({ localID: 1, object: object(1, false) }).animatedMesh).toBe(false);
  });
});

describe('animation asset downloads', () => {
  it('falls back to the simulator transfer service when ViewerAsset returns 403', async () => {
    const bytes = Buffer.from([1, 2, 3]);
    const transfer = vi.fn().mockResolvedValue(bytes);
    const bot = { clientCommands: { asset: {
      downloadAsset: vi.fn().mockRejectedValue(new Error('Response code 403 (Forbidden)')),
      transfer,
    } } };
    const id = '835965c6-7f2f-bda2-5deb-2478737f91bf';
    await expect(downloadAnimation(bot, id)).resolves.toBe(bytes.toString('base64'));
    expect(transfer).toHaveBeenCalledTimes(1);
    expect(transfer.mock.calls[0][3].subarray(0, 16)).toHaveLength(16);
  });
});
