import { describe, expect, it, vi } from 'vitest';
import { createRequire } from 'node:module';
import { WorldViewer } from '../world';
import { Utils } from '../utils';

const require = createRequire(import.meta.url);
const { downloadAnimation } = require('../../../core/sl-animations.cjs');

class ProtocolStub extends Utils.EventEmitter {
  connected = true;
  authReply: Record<string, any> | null = null;
  fetchAnimation = vi.fn();
}

/** A minimal valid .anim: no joints, 1 s long. */
function emptyAnim() {
  const out: number[] = [1, 0, 0, 0];
  const dv = new DataView(new ArrayBuffer(4));
  const i32 = (v: number) => { dv.setInt32(0, v, true); out.push(...new Uint8Array(dv.buffer)); };
  const f32 = (v: number) => { dv.setFloat32(0, v, true); out.push(...new Uint8Array(dv.buffer)); };
  i32(1); f32(1); out.push(0); f32(0); f32(1); i32(0); f32(0); f32(0); i32(0); i32(0);
  return new Uint8Array(out);
}

describe('animation loading for the world', () => {
  it('falls back to the simulator for animations that are not bundled', async () => {
    const protocol = new ProtocolStub();
    protocol.fetchAnimation.mockResolvedValue(emptyAnim());
    const world = new WorldViewer(protocol);
    const loader = (world as any).animationLoader();
    const anim = await loader('11111111-2222-3333-4444-555555555555');
    expect(protocol.fetchAnimation).toHaveBeenCalledWith('11111111-2222-3333-4444-555555555555');
    expect(anim).toMatchObject({ length: 1, priority: 1 });
  });

  it('returns null (no pose) when the simulator download fails or is unsupported', async () => {
    const warn = vi.spyOn(console, 'warn').mockImplementation(() => undefined);
    const protocol = new ProtocolStub();
    protocol.fetchAnimation.mockRejectedValue(new Error('boom'));
    const world = new WorldViewer(protocol);
    expect(await (world as any).animationLoader()('11111111-2222-3333-4444-555555555555')).toBeNull();
    const bare = new WorldViewer(new Utils.EventEmitter() as any);
    expect(await (bare as any).animationLoader()('11111111-2222-3333-4444-555555555555')).toBeNull();
    warn.mockRestore();
  });
});

describe('downloadAnimation', () => {
  const id = '11111111-2222-3333-4444-555555555555';
  const botWith = (impl: (type: number, id: string) => Promise<Buffer>) => ({ clientCommands: { asset: { downloadAsset: vi.fn(impl) } } });

  it('returns the asset base64-encoded', async () => {
    const bot = botWith(async () => Buffer.from([1, 2, 3]));
    expect(await downloadAnimation(bot, id)).toBe('AQID');
    expect(bot.clientCommands.asset.downloadAsset).toHaveBeenCalledWith(20, id);
  });

  it('rejects bad ids, empty and oversized assets, and a missing session', async () => {
    await expect(downloadAnimation(botWith(async () => Buffer.from([1])), '../../etc/passwd')).rejects.toThrow('Invalid animation id');
    await expect(downloadAnimation(botWith(async () => Buffer.alloc(0)), id)).rejects.toThrow('empty');
    await expect(downloadAnimation(botWith(async () => Buffer.alloc(3 * 1024 * 1024)), id)).rejects.toThrow('too large');
    await expect(downloadAnimation(null, id)).rejects.toThrow('Not connected');
  });
});
