import { describe, expect, it, vi } from 'vitest';
import { InteractionsManager, MAX_INTERACTIONS } from '../interactions';
import { Utils } from '../utils';

class ProtocolStub extends Utils.EventEmitter {
  balance: number | null = 500;
  refreshBalance = vi.fn().mockImplementation(async () => this.balance);
  respondScriptDialog = vi.fn().mockResolvedValue({ answered: true });
  acceptLure = vi.fn().mockResolvedValue({ accepted: true, message: 'Arrived' });
  dismissInteraction = vi.fn().mockResolvedValue({ dismissed: true });
  payObject = vi.fn().mockImplementation(async (params: any) => {
    if (this.balance !== null) this.balance -= params.amount;
    return { paid: params.objectId, amount: params.amount, balance: this.balance };
  });
}

const dialog = (id: string, over: any = {}) => ({
  id, receivedAt: 1, objectId: 'o', objectName: 'Vendor', ownerName: 'Pat', message: 'Pick', channel: 5,
  imageId: null, buttons: ['Yes', 'No'], textBox: false, textBoxIndex: -1, ...over,
});
const lure = (id: string, over: any = {}) => ({
  id, receivedAt: 2, fromId: 'f', fromName: 'Sam', message: 'Come', regionId: 'r', position: [1, 2, 3], gridX: 10, gridY: 11, ...over,
});
const setup = () => {
  const protocol = new ProtocolStub();
  const manager = new InteractionsManager(protocol as any);
  manager.init();
  return { protocol, manager };
};

describe('InteractionsManager', () => {
  it('lists what the grid sent, oldest first, and ignores duplicates and malformed events', () => {
    const { protocol, manager } = setup();
    protocol.emit('script_dialog', dialog('a'));
    protocol.emit('lure', lure('b'));
    protocol.emit('script_dialog', dialog('a'));
    protocol.emit('script_dialog', { message: 'no id' });
    protocol.emit('lure', null);
    expect(manager.items.map((item) => item.id)).toEqual(['a', 'b']);
    expect(manager.current?.id).toBe('a');
    expect(manager.items[1]).toMatchObject({ kind: 'lure', fromName: 'Sam', position: [1, 2, 3] });
  });

  it('normalises sloppy fields instead of passing them on', () => {
    const { protocol, manager } = setup();
    protocol.emit('script_dialog', { id: 'x', buttons: [1, 'B'], channel: 'nope', textBoxIndex: 'z' });
    protocol.emit('lure', { id: 'y', position: [1, 2], gridX: 'q' });
    expect(manager.items[0]).toMatchObject({ kind: 'script-dialog', buttons: ['1', 'B'], channel: 0, textBoxIndex: -1, objectName: '', message: '' });
    expect(manager.items[1]).toMatchObject({ kind: 'lure', position: null, gridX: null, fromName: '' });
  });

  it('keeps at most MAX_INTERACTIONS, dropping the oldest', () => {
    const { protocol, manager } = setup();
    for (let i = 0; i < MAX_INTERACTIONS + 3; i++) protocol.emit('script_dialog', dialog(`d${i}`));
    expect(manager.items).toHaveLength(MAX_INTERACTIONS);
    expect(manager.items[0].id).toBe('d3');
  });

  it('answers a button dialog and removes it once the grid acknowledges', async () => {
    const { protocol, manager } = setup();
    protocol.emit('script_dialog', dialog('a'));
    await expect(manager.answerButton('a', 1)).resolves.toBe(true);
    expect(protocol.respondScriptDialog).toHaveBeenCalledWith({ id: 'a', buttonIndex: 1 });
    expect(manager.items).toHaveLength(0);
  });

  it('sends typed text for a text box', async () => {
    const { protocol, manager } = setup();
    protocol.emit('script_dialog', dialog('t', { textBox: true, textBoxIndex: 0, buttons: ['!!llTextBox!!'] }));
    await manager.answerText('t', 'hello');
    expect(protocol.respondScriptDialog).toHaveBeenCalledWith({ id: 't', text: 'hello' });
    expect(manager.items).toHaveLength(0);
  });

  it('keeps the request and reports why when the grid does not acknowledge, so it can be retried', async () => {
    const { protocol, manager } = setup();
    const failed = vi.fn();
    manager.on('interaction_failed', failed);
    protocol.respondScriptDialog.mockRejectedValueOnce(new Error('timeout'));
    protocol.emit('script_dialog', dialog('a'));
    await expect(manager.answerButton('a', 0)).resolves.toBe(false);
    expect(failed).toHaveBeenCalledWith({ id: 'a', message: 'timeout' });
    expect(manager.items).toHaveLength(1);
    expect(manager.isBusy('a')).toBe(false);
    await expect(manager.answerButton('a', 0)).resolves.toBe(true);
    expect(manager.items).toHaveLength(0);
  });

  it('marks a request busy while the call is in flight and ignores a second tap', async () => {
    const { protocol, manager } = setup();
    let release: (value: any) => void = () => undefined;
    protocol.respondScriptDialog.mockReturnValueOnce(new Promise((resolve) => { release = resolve; }));
    protocol.emit('script_dialog', dialog('a'));
    const first = manager.answerButton('a', 0);
    expect(manager.isBusy('a')).toBe(true);
    await expect(manager.answerButton('a', 1)).resolves.toBe(false);
    expect(protocol.respondScriptDialog).toHaveBeenCalledTimes(1);
    release({ answered: true });
    await expect(first).resolves.toBe(true);
    expect(manager.isBusy('a')).toBe(false);
  });

  it('accepts a lure, announces it, and keeps it when the teleport fails', async () => {
    const { protocol, manager } = setup();
    const accepted = vi.fn();
    manager.on('lure_accepted', accepted);
    protocol.emit('lure', lure('l1'));
    protocol.acceptLure.mockRejectedValueOnce(new Error('Teleport failed'));
    await expect(manager.acceptLure('l1')).resolves.toBe(false);
    expect(manager.items).toHaveLength(1);
    expect(accepted).not.toHaveBeenCalled();
    await expect(manager.acceptLure('l1')).resolves.toBe(true);
    expect(accepted).toHaveBeenCalledWith({ lure: expect.objectContaining({ id: 'l1', fromName: 'Sam' }), message: 'Arrived' });
    expect(manager.items).toHaveLength(0);
  });

  it('dismisses locally even if the server call fails', async () => {
    const { protocol, manager } = setup();
    protocol.emit('lure', lure('l1'));
    protocol.dismissInteraction.mockRejectedValueOnce(new Error('gone'));
    await manager.dismiss('l1');
    expect(manager.items).toHaveLength(0);
    expect(protocol.dismissInteraction).toHaveBeenCalledWith('l1');
  });

  it('notifies listeners on every change and empties on disconnect', () => {
    const { protocol, manager } = setup();
    const changed = vi.fn();
    manager.on('interactions_changed', changed);
    protocol.emit('script_dialog', dialog('a'));
    expect(changed).toHaveBeenCalledTimes(1);
    protocol.emit('disconnected', {});
    expect(manager.items).toHaveLength(0);
    expect(changed).toHaveBeenCalledTimes(2);
    protocol.emit('disconnected', {});
    expect(changed).toHaveBeenCalledTimes(2);
  });

  describe('payment requests', () => {
    it('intercepts payment requests and triggers a balance refresh', () => {
      const { protocol, manager } = setup();
      const id = manager.requestPayment({ objectId: 'vendor-1', objectName: 'Sunset Lamp', sellerName: 'Pat Resident', price: 150 });
      expect(manager.items).toHaveLength(1);
      expect(manager.items[0]).toMatchObject({
        id,
        kind: 'payment',
        objectId: 'vendor-1',
        objectName: 'Sunset Lamp',
        sellerName: 'Pat Resident',
        price: 150,
      });
      expect(protocol.refreshBalance).toHaveBeenCalled();
    });

    it('confirms payment when user has sufficient balance and notifies completion', async () => {
      const { protocol, manager } = setup();
      protocol.balance = 500;
      const completed = vi.fn();
      manager.on('payment_completed', completed);
      const id = manager.requestPayment({ objectId: 'v1', objectName: 'Mesh Outfit', sellerName: 'Vendor Store', price: 200 });

      await expect(manager.confirmPayment(id)).resolves.toBe(true);
      expect(protocol.payObject).toHaveBeenCalledWith({
        objectId: 'v1',
        amount: 200,
        targetId: undefined,
        description: 'Payment for Mesh Outfit',
      });
      expect(manager.items).toHaveLength(0);
      expect(completed).toHaveBeenCalledWith(expect.objectContaining({
        newBalance: 300,
        message: expect.stringContaining('Paid L$ 200 for "Mesh Outfit"'),
      }));
    });

    it('rejects confirmation and throws when user has insufficient balance', async () => {
      const { protocol, manager } = setup();
      protocol.balance = 50;
      const failed = vi.fn();
      manager.on('interaction_failed', failed);
      const id = manager.requestPayment({ objectId: 'v2', objectName: 'Expensive Rig', sellerName: 'Luxury Goods', price: 500 });

      await expect(manager.confirmPayment(id)).rejects.toThrow(/Insufficient funds/);
      expect(protocol.payObject).not.toHaveBeenCalled();
      expect(failed).toHaveBeenCalledWith({
        id,
        message: expect.stringContaining('Insufficient funds'),
      });
      expect(manager.items).toHaveLength(1);
    });

    it('cancels payment without dispatching any payment packets', async () => {
      const { protocol, manager } = setup();
      const id = manager.requestPayment({ objectId: 'v3', objectName: 'Cancelled Item', sellerName: 'Seller', price: 100 });
      expect(manager.items).toHaveLength(1);

      await manager.cancelPayment(id);
      expect(manager.items).toHaveLength(0);
      expect(protocol.payObject).not.toHaveBeenCalled();
    });
  });
});
