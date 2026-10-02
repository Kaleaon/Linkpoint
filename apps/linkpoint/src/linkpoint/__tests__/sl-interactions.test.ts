import { createRequire } from 'node:module';
import { describe, expect, it, vi } from 'vitest';

const require = createRequire(import.meta.url);
const {
  TEXT_BOX_MARKER, MAX_REPLY_BYTES, serializeScriptDialog, serializeLure, PendingInteractions,
  subscribeInteractions, respondScriptDialog, acceptLure, dismissInteraction, serializeGroupNotice,
} = require('../../../core/sl-interactions.cjs');

const uuid = (value: string) => ({ toString: () => value });
const dialogEvent = (over: any = {}) => ({
  ObjectID: uuid('11111111-1111-1111-1111-111111111111'),
  FirstName: 'Pat', LastName: 'Resident', ObjectName: 'Vendor', Message: 'Pick one',
  ChatChannel: -4242, ImageID: uuid('00000000-0000-0000-0000-000000000000'), Buttons: ['Yes', 'No'], Owners: [],
  ...over,
});
const lureEvent = (over: any = {}) => ({
  from: uuid('22222222-2222-2222-2222-222222222222'), fromName: 'Sam Resident', lureMessage: 'Come visit',
  regionID: uuid('33333333-3333-3333-3333-333333333333'), position: { x: 10, y: 20, z: 30 },
  gridX: 1000, gridY: 1001, lureID: uuid('44444444-4444-4444-4444-444444444444'), ...over,
});
const botWith = (comms: any = {}, teleport: any = {}) => ({ clientCommands: { comms, teleport } });

describe('serializing interactions', () => {
  it('describes a button dialog', () => {
    expect(serializeScriptDialog(dialogEvent())).toEqual({
      objectId: '11111111-1111-1111-1111-111111111111', objectName: 'Vendor', ownerName: 'Pat Resident', message: 'Pick one',
      channel: -4242, imageId: '00000000-0000-0000-0000-000000000000', buttons: ['Yes', 'No'], textBox: false, textBoxIndex: -1,
    });
  });

  it('treats the !!llTextBox!! marker as a text box and remembers where it was', () => {
    const wire = serializeScriptDialog(dialogEvent({ Buttons: ['Cancel', TEXT_BOX_MARKER] }));
    expect(wire.textBox).toBe(true);
    expect(wire.textBoxIndex).toBe(1);
  });

  it('tolerates missing fields', () => {
    const wire = serializeScriptDialog({ ObjectID: uuid('x') });
    expect(wire).toMatchObject({ objectName: '', ownerName: '', message: '', channel: 0, buttons: [], textBox: false });
  });

  it('treats null, empty and non-numeric values as missing rather than as 0', () => {
    const lure = serializeLure(lureEvent({ gridX: null, gridY: '', ...{} }));
    expect(lure.gridX).toBeNull();
    expect(lure.gridY).toBeNull();
    expect(serializeLure(lureEvent({ gridX: true, gridY: {} })).gridX).toBeNull();
    expect(serializeLure(lureEvent({ gridX: '1000', gridY: 0 }))).toMatchObject({ gridX: 1000, gridY: 0 });
    expect(serializeScriptDialog(dialogEvent({ ChatChannel: null })).channel).toBe(0);
    expect(serializeScriptDialog(dialogEvent({ ChatChannel: '-5' })).channel).toBe(-5);
  });

  it('describes a lure with a plain position array', () => {
    expect(serializeLure(lureEvent())).toEqual({
      fromId: '22222222-2222-2222-2222-222222222222', fromName: 'Sam Resident', message: 'Come visit',
      regionId: '33333333-3333-3333-3333-333333333333', position: [10, 20, 30], gridX: 1000, gridY: 1001,
    });
  });

  it('reads library-style vectors and drops an unreadable position or grid', () => {
    expect(serializeLure(lureEvent({ position: { getX: () => 1, getY: () => 2, getZ: () => 3 } })).position).toEqual([1, 2, 3]);
    const odd = serializeLure(lureEvent({ position: { x: NaN, y: 0, z: 0 }, gridX: 'nope' }));
    expect(odd.position).toBeNull();
    expect(odd.gridX).toBeNull();
  });
});

describe('PendingInteractions', () => {
  it('returns the stored event only for the right kind and id', () => {
    const pending = new PendingInteractions();
    const event = dialogEvent();
    const id = pending.add('script-dialog', event);
    expect(pending.get('script-dialog', id)).toBe(event);
    expect(() => pending.get('lure', id)).toThrow(/no longer pending/);
    expect(() => pending.get('script-dialog', 'nope')).toThrow(/no longer pending/);
    expect(() => pending.get('script-dialog', undefined)).toThrow(/no longer pending/);
  });

  it('forgets removed entries, drops the oldest past its bound and clears on demand', () => {
    const pending = new PendingInteractions(2);
    const first = pending.add('lure', lureEvent());
    const second = pending.add('lure', lureEvent());
    const third = pending.add('lure', lureEvent());
    expect(pending.size).toBe(2);
    expect(() => pending.get('lure', first)).toThrow();
    expect(pending.remove(second)).toBe(true);
    expect(pending.remove(second)).toBe(false);
    pending.get('lure', third);
    pending.clear();
    expect(pending.size).toBe(0);
  });
});

describe('subscribeInteractions', () => {
  it('forwards each event with an id and keeps the original for answering', () => {
    const handlers: Record<string, (e: any) => void> = {};
    const subject = (name: string) => ({ subscribe: (fn: any) => { handlers[name] = fn; return { unsubscribe: vi.fn() }; } });
    const pending = new PendingInteractions();
    const send = vi.fn();
    const subs = subscribeInteractions({ onScriptDialog: subject('d'), onLure: subject('l') }, pending, send);
    expect(subs).toHaveLength(2);
    const event = dialogEvent();
    handlers.d(event);
    handlers.l(lureEvent());
    expect(send).toHaveBeenCalledTimes(2);
    const [type, data] = send.mock.calls[0];
    expect(type).toBe('script-dialog');
    expect(data).toMatchObject({ objectName: 'Vendor', buttons: ['Yes', 'No'] });
    expect(typeof data.id).toBe('string');
    expect(pending.get('script-dialog', data.id)).toBe(event);
    expect(send.mock.calls[1][0]).toBe('lure');
  });

  it('forwards group notices with an id and a timestamp, without keeping anything to answer', () => {
    let handler: (e: any) => void = () => undefined;
    const pending = new PendingInteractions();
    const send = vi.fn();
    subscribeInteractions({ onGroupNotice: { subscribe: (fn: any) => { handler = fn; return { unsubscribe: vi.fn() }; } } }, pending, send);
    handler({ groupID: uuid('g'), from: uuid('f'), fromName: 'Officer', subject: 'Meeting at 7pm', message: 'Bring friends' });
    const [type, data] = send.mock.calls[0];
    expect(type).toBe('group-notice');
    expect(data).toMatchObject({ groupId: 'g', fromId: 'f', fromName: 'Officer', subject: 'Meeting at 7pm', message: 'Bring friends' });
    expect(typeof data.id).toBe('string');
    expect(Number.isFinite(data.timestamp)).toBe(true);
    expect(pending.size).toBe(0);
  });

  it('fills in defaults for a sparse group notice', () => {
    expect(serializeGroupNotice({})).toEqual({ groupId: null, fromId: null, fromName: 'Resident', subject: 'Group Notice', message: '' });
  });

  it('copes with a library that lacks the subjects', () => {
    expect(subscribeInteractions({}, new PendingInteractions(), vi.fn())).toEqual([]);
  });
});

describe('answering a script dialog', () => {
  const setup = (event: any) => {
    const pending = new PendingInteractions();
    const id = pending.add('script-dialog', event);
    const respond = vi.fn().mockResolvedValue(undefined);
    return { pending, id, respond, bot: botWith({ respondToScriptDialog: respond }) };
  };

  it('sends the chosen button and forgets the dialog', async () => {
    const event = dialogEvent();
    const { pending, id, respond, bot } = setup(event);
    await expect(respondScriptDialog(bot, pending, { id, buttonIndex: 1 })).resolves.toEqual({ answered: true });
    const [sent, index] = respond.mock.calls[0];
    expect(index).toBe(1);
    expect(sent.Buttons[1]).toBe('No');
    expect(sent.ChatChannel).toBe(-4242);
    expect(sent.ObjectID).toBe(event.ObjectID);
    expect(pending.size).toBe(0);
  });

  it('rejects a button that does not exist or is not an integer, leaving the dialog pending', async () => {
    const { pending, id, respond, bot } = setup(dialogEvent());
    for (const buttonIndex of [2, -1, 0.5, '0', undefined]) {
      await expect(respondScriptDialog(bot, pending, { id, buttonIndex })).rejects.toThrow(/does not exist/);
    }
    expect(respond).not.toHaveBeenCalled();
    expect(pending.size).toBe(1);
  });

  it('sends typed text as the label of the text box button, and not mutating the original event', async () => {
    const event = dialogEvent({ Buttons: ['Ignore', TEXT_BOX_MARKER] });
    const { pending, id, respond, bot } = setup(event);
    await respondScriptDialog(bot, pending, { id, text: 'héllo' });
    const [sent, index] = respond.mock.calls[0];
    expect(index).toBe(1);
    expect(sent.Buttons).toEqual(['Ignore', 'héllo']);
    expect(event.Buttons).toEqual(['Ignore', TEXT_BOX_MARKER]);
  });

  it('refuses empty or oversized text, counting bytes not characters', async () => {
    const { pending, id, respond, bot } = setup(dialogEvent({ Buttons: [TEXT_BOX_MARKER] }));
    await expect(respondScriptDialog(bot, pending, { id, text: '' })).rejects.toThrow(/Enter some text/);
    await expect(respondScriptDialog(bot, pending, { id })).rejects.toThrow(/Enter some text/);
    await expect(respondScriptDialog(bot, pending, { id, text: 'a'.repeat(MAX_REPLY_BYTES + 1) })).rejects.toThrow(/too long/);
    await expect(respondScriptDialog(bot, pending, { id, text: '€'.repeat(86) })).rejects.toThrow(/too long/); // 86 x 3 bytes
    await respondScriptDialog(bot, pending, { id, text: 'a'.repeat(MAX_REPLY_BYTES) });
    expect(respond).toHaveBeenCalledTimes(1);
  });

  it('keeps the dialog pending when the grid does not acknowledge', async () => {
    const { pending, id, respond, bot } = setup(dialogEvent());
    respond.mockRejectedValueOnce(new Error('timeout'));
    await expect(respondScriptDialog(bot, pending, { id, buttonIndex: 0 })).rejects.toThrow('timeout');
    expect(pending.size).toBe(1);
    await respondScriptDialog(bot, pending, { id, buttonIndex: 0 });
    expect(pending.size).toBe(0);
  });

  it('rejects an unknown or already answered dialog and a missing connection', async () => {
    const { pending, id, bot } = setup(dialogEvent());
    await expect(respondScriptDialog(bot, pending, { id: 'nope', buttonIndex: 0 })).rejects.toThrow(/no longer pending/);
    await expect(respondScriptDialog(undefined, pending, { id, buttonIndex: 0 })).rejects.toThrow(/Not connected/);
    await respondScriptDialog(bot, pending, { id, buttonIndex: 0 });
    await expect(respondScriptDialog(bot, pending, { id, buttonIndex: 0 })).rejects.toThrow(/no longer pending/);
  });
});

describe('lures', () => {
  it('accepts through the library with the original event and forgets it', async () => {
    const pending = new PendingInteractions();
    const event = lureEvent();
    const id = pending.add('lure', event);
    const acceptTeleport = vi.fn().mockResolvedValue({ message: 'Arrived' });
    await expect(acceptLure(botWith({}, { acceptTeleport }), pending, { id })).resolves.toEqual({ accepted: true, message: 'Arrived' });
    expect(acceptTeleport).toHaveBeenCalledWith(event);
    expect(pending.size).toBe(0);
  });

  it('keeps the lure when teleporting fails, and will not accept a dialog id as a lure', async () => {
    const pending = new PendingInteractions();
    const id = pending.add('lure', lureEvent());
    const dialogId = pending.add('script-dialog', dialogEvent());
    const acceptTeleport = vi.fn().mockRejectedValue(new Error('Teleport failed'));
    const bot = botWith({}, { acceptTeleport });
    await expect(acceptLure(bot, pending, { id })).rejects.toThrow('Teleport failed');
    expect(pending.size).toBe(2);
    await expect(acceptLure(bot, pending, { id: dialogId })).rejects.toThrow(/no longer pending/);
  });

  it('dismisses locally without calling the grid', () => {
    const pending = new PendingInteractions();
    const id = pending.add('lure', lureEvent());
    expect(dismissInteraction(pending, { id })).toEqual({ dismissed: true });
    expect(dismissInteraction(pending, { id })).toEqual({ dismissed: false });
    expect(() => dismissInteraction(pending, {})).toThrow(/id is required/);
  });
});
