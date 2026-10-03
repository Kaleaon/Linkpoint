import { describe, expect, it, vi } from 'vitest';
import { createRequire } from 'node:module';
const require = createRequire(import.meta.url);
const actions = require('../../../core/sl-actions.cjs');

class Vector3 { constructor(public v: number[]) {} }
class UUID { constructor(public s: string) {} toString() { return this.s; } }
const lib = { Vector3, UUID };
const ID = '12345678-1234-1234-1234-123456789abc';

function makeBot() {
  const calls: Record<string, any[]> = {};
  const record = (name: string, result?: any) => vi.fn(async (...args: any[]) => { (calls[name] ||= []).push(args); return result; });
  return {
    calls,
    bot: { clientCommands: {
      teleport: { teleportTo: record('teleportTo', { message: 'Teleport finished' }) },
      region: { touchObject: record('touchObject') },
      movement: { sitOnObject: record('sitOnObject'), sitOnGround: vi.fn(() => { (calls.sitOnGround ||= []).push([]); }), stand: vi.fn(() => { (calls.stand ||= []).push([]); }) },
      grid: { getBalance: record('getBalance', 1234) },
    } },
  };
}

describe('attachment points (Lumiya SLObjectInfo.attachmentIDFromState)', () => {
  it('swaps the two nibbles of the state byte', () => {
    expect(actions.attachmentIdFromState(0x32)).toBe(35);
    expect(actions.attachmentIdFromState(0x01)).toBe(16);
    expect(actions.attachmentIdFromState(0x10)).toBe(1);
    expect(actions.attachmentIdFromState(0x1ff32)).toBe(35); // only the low byte matters
    for (let id = 1; id <= 55; id++) {
      const wire = ((id & 0x0f) << 4) | ((id & 0xf0) >> 4);
      expect(actions.attachmentIdFromState(wire)).toBe(id);
    }
  });

  it('flags only points 31-38 as HUDs', () => {
    for (let id = 1; id <= 55; id++) expect(actions.isHudPoint(id)).toBe(id >= 31 && id <= 38);
    expect(actions.ATTACHMENT_NAMES[35]).toBe('Center');
    expect(actions.ATTACHMENT_NAMES[32]).toBe('Top Right');
    expect(actions.ATTACHMENT_NAMES[38]).toBe('Bottom Right');
  });

  it('reads node-metaverse objects whether the raw or swapped value is present', () => {
    const raw = { IsAttachment: true, attachmentPoint: 0x32, State: 35 };
    expect(actions.attachmentInfo(raw)).toEqual({ attachmentPoint: 35, attachmentName: 'Center', isHud: true });
    expect(actions.attachmentInfo({ IsAttachment: true, attachmentPoint: 0, State: 35 }).attachmentPoint).toBe(35);
    expect(actions.attachmentInfo({ IsAttachment: true, attachmentPoint: 0x01, State: 16 })).toMatchObject({ attachmentPoint: 16, isHud: false });
  });

  it('treats non-attachments and nonsense values as not attached', () => {
    expect(actions.attachmentInfo({ IsAttachment: false, attachmentPoint: 0x32, State: 35 })).toEqual({ attachmentPoint: 0, attachmentName: null, isHud: false });
    expect(actions.attachmentInfo(null).attachmentPoint).toBe(0);
    expect(actions.attachmentInfo({ IsAttachment: true, attachmentPoint: 0xff, State: 255 }).attachmentPoint).toBe(0); // point 255 does not exist
  });
});

describe('parseDestination', () => {
  it('accepts SLURLs, map URLs and plain region paths', () => {
    expect(actions.parseDestination('secondlife://Ahern/128/64/22')).toEqual({ region: 'Ahern', x: 128, y: 64, z: 22 });
    expect(actions.parseDestination('secondlife:///app/teleport/Da%20Boom/10/20/30')).toEqual({ region: 'Da Boom', x: 10, y: 20, z: 30 });
    expect(actions.parseDestination('http://maps.secondlife.com/secondlife/Ahern/1/2/3')).toEqual({ region: 'Ahern', x: 1, y: 2, z: 3 });
    expect(actions.parseDestination('Ahern/5/6/7')).toEqual({ region: 'Ahern', x: 5, y: 6, z: 7 });
    expect(actions.parseDestination('Ahern')).toMatchObject({ region: 'Ahern' });
  });

  it('rejects empty, non-numeric and out-of-range destinations', () => {
    expect(() => actions.parseDestination('')).toThrow(/destination/i);
    expect(() => actions.parseDestination('   ')).toThrow();
    expect(() => actions.parseDestination('Ahern/x/2/3')).toThrow(/number/);
    expect(() => actions.parseDestination('Ahern/300/2/3')).toThrow(/within the region/);
    expect(() => actions.parseDestination('Ahern/1/-5/3')).toThrow(/within the region/);
    expect(() => actions.parseDestination('Ahern/1/2/99999')).toThrow(/Altitude/);
  });
});

describe('teleport', () => {
  it('sends the parsed region and position, and reports only what the grid said', async () => {
    const { bot, calls } = makeBot();
    const result = await actions.teleport(bot, { destination: 'secondlife://Ahern/10/20/30' }, lib);
    expect(result).toEqual({ requested: { region: 'Ahern', x: 10, y: 20, z: 30 }, message: 'Teleport finished' });
    expect(calls.teleportTo[0][0]).toBe('Ahern');
    expect((calls.teleportTo[0][1] as Vector3).v).toEqual([10, 20, 30]);
  });

  it('accepts explicit coordinates but validates them', async () => {
    const { bot } = makeBot();
    await expect(actions.teleport(bot, { region: 'Ahern', x: 'abc', y: 1, z: 1 }, lib)).rejects.toThrow(/x must be a number/);
    await expect(actions.teleport(bot, { destination: '' }, lib)).rejects.toThrow();
  });

  it('fails clearly when there is no connection', async () => {
    await expect(actions.teleport({}, { destination: 'Ahern' }, lib)).rejects.toThrow(/Not connected/);
    await expect(actions.teleport(null, { destination: 'Ahern' }, lib)).rejects.toThrow(/Not connected/);
  });
});

describe('touchObject', () => {
  it('touches by UUID or by local id', async () => {
    const { bot, calls } = makeBot();
    expect(await actions.touchObject(bot, { id: ID }, lib)).toEqual({ touched: ID });
    expect(calls.touchObject[0][0]).toBeInstanceOf(UUID);
    await actions.touchObject(bot, { localId: 4242 }, lib);
    expect(calls.touchObject[1][0]).toBe(4242);
  });

  it('passes face and coordinates only when given', async () => {
    const { bot, calls } = makeBot();
    await actions.touchObject(bot, { id: ID }, lib);
    expect(calls.touchObject[0].slice(1)).toEqual([undefined, undefined, undefined, undefined, undefined]);
    await actions.touchObject(bot, { id: ID, face: 2, uv: [0.25, 0.75, 0], st: { x: 1, y: 2, z: 0 }, position: [1, 2, 3] }, lib);
    const call = calls.touchObject[1];
    // Library argument order: (target, grabOffset, uv, st, face, position)
    expect(call[1]).toBeUndefined();
    expect((call[2] as Vector3).v).toEqual([0.25, 0.75, 0]);
    expect((call[3] as Vector3).v).toEqual([1, 2, 0]);
    expect(call[4]).toBe(2);
    expect((call[5] as Vector3).v).toEqual([1, 2, 3]);
  });

  it('rejects missing, malformed or hostile targets and values', async () => {
    const { bot, calls } = makeBot();
    await expect(actions.touchObject(bot, {}, lib)).rejects.toThrow(/target/);
    await expect(actions.touchObject(bot, { id: 'not-a-uuid' }, lib)).rejects.toThrow(/valid UUID/);
    await expect(actions.touchObject(bot, { id: '00000000-0000-0000-0000-000000000000' }, lib)).rejects.toThrow(/valid UUID/);
    await expect(actions.touchObject(bot, { id: `${ID}; drop` }, lib)).rejects.toThrow(/valid UUID/);
    await expect(actions.touchObject(bot, { id: ID, face: 1000 }, lib)).rejects.toThrow(/out of range/);
    await expect(actions.touchObject(bot, { id: ID, face: 'x' }, lib)).rejects.toThrow(/number/);
    await expect(actions.touchObject(bot, { id: ID, uv: ['a', 0, 0] }, lib)).rejects.toThrow(/number/);
    await expect(actions.touchObject(bot, { localId: -3 }, lib)).rejects.toThrow(/target/);
    await expect(actions.touchObject(bot, { localId: 1.5 }, lib)).rejects.toThrow(/target/);
    expect(calls.touchObject).toBeUndefined(); // nothing reached the grid
  });
});

describe('sit, stand and balance', () => {
  it('sits on an object or on the ground, and stands', async () => {
    const { bot, calls } = makeBot();
    expect(await actions.sit(bot, { id: ID }, lib)).toEqual({ sitting: ID });
    expect(calls.sitOnObject[0][0]).toBeInstanceOf(UUID);
    expect(await actions.sit(bot, {}, lib)).toEqual({ sitting: 'ground' });
    expect(calls.sitOnGround).toHaveLength(1);
    expect(actions.stand(bot)).toEqual({ standing: true });
    await expect(actions.sit(bot, { id: 'nope' }, lib)).rejects.toThrow(/valid UUID/);
  });

  it('returns the balance the grid reported, and refuses to invent one', async () => {
    const { bot } = makeBot();
    expect(await actions.getBalance(bot)).toEqual({ balance: 1234, currencySymbol: 'L$', isZeroCurrency: false });
    bot.clientCommands.grid.getBalance = vi.fn(async () => undefined as any);
    await expect(actions.getBalance(bot)).rejects.toThrow(/no balance/);
    bot.clientCommands.grid.getBalance = vi.fn(async () => { throw new Error('timeout'); });
    await expect(actions.getBalance(bot)).rejects.toThrow('timeout');
  });
});
