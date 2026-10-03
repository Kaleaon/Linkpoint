import { afterEach, beforeAll, describe, expect, it, vi } from 'vitest';
import { createElement, act } from 'react';
import { createRoot, type Root } from 'react-dom/client';
import { AppProvider } from '../../context/AppContext.jsx';
import { ThemeProvider } from '../../context/ThemeContext.jsx';
import InteractionDialog from '../../components/InteractionDialog.jsx';
import { app } from '../app';

(globalThis as any).IS_REACT_ACT_ENVIRONMENT = true;

const dialog = (id: string, over: any = {}) => ({
  id, receivedAt: 1, objectId: 'o', objectName: 'Vendor', ownerName: 'Pat Resident', message: 'Pick a colour\nthen confirm', channel: 5,
  imageId: null, buttons: ['Red', 'Blue'], textBox: false, textBoxIndex: -1, ...over,
});
const lure = (id: string, over: any = {}) => ({ id, receivedAt: 2, fromId: 'f', fromName: 'Sam Resident', message: 'Come visit', regionId: 'r', position: [10.4, 20.6, 30], gridX: 1000, gridY: 1001, ...over });

beforeAll(() => { app.interactions.init(); }); // App.tsx does this through app.init()

let mounted: { host: HTMLElement; root: Root } | null = null;
async function mount() {
  const host = document.createElement('div');
  document.body.appendChild(host);
  const r = createRoot(host);
  await act(async () => { r.render(createElement(AppProvider as any, null, createElement(ThemeProvider as any, null, createElement(InteractionDialog as any)))); });
  mounted = { host, root: r };
  return host;
}
const emit = async (type: string, data: any) => { await act(async () => { (app.protocol as any).emit(type, data); }); };
const button = (host: HTMLElement, label: string) => [...host.querySelectorAll('button')].find((b) => (b.textContent || '').trim() === label) as HTMLButtonElement | undefined;
const click = async (el: Element | null | undefined) => { await act(async () => { (el as HTMLElement).click(); }); };

afterEach(async () => {
  if (mounted) { await act(async () => mounted!.root.unmount()); mounted.host.remove(); mounted = null; }
  await act(async () => { (app.protocol as any).emit('disconnected', {}); });
  vi.restoreAllMocks();
});

describe('InteractionDialog', () => {
  it('renders nothing until the simulator sends a request', async () => {
    const host = await mount();
    expect(host.querySelector('[role="alertdialog"]')).toBeNull();
  });

  it('shows an object dialog with its buttons and answers with the tapped button index', async () => {
    const respond = vi.spyOn(app.protocol, 'respondScriptDialog').mockResolvedValue({ answered: true } as any);
    vi.spyOn(app.protocol as any, 'requireConnected').mockImplementation(() => undefined);
    const host = await mount();
    await emit('script_dialog', dialog('d1'));
    const sheet = host.querySelector('[role="alertdialog"]');
    expect(sheet).not.toBeNull();
    expect(host.textContent).toContain('Vendor');
    expect(host.textContent).toContain('Owned by Pat Resident');
    expect(host.textContent).toContain('Pick a colour');
    expect(host.textContent).toContain('OBJECT DIALOG');

    await click(button(host, 'Blue'));
    expect(respond).toHaveBeenCalledWith({ id: 'd1', buttonIndex: 1 });
    expect(host.querySelector('[role="alertdialog"]')).toBeNull();
  });

  it('asks for text when a script sends the llTextBox marker, and sends what was typed', async () => {
    const respond = vi.spyOn(app.protocol, 'respondScriptDialog').mockResolvedValue({ answered: true } as any);
    vi.spyOn(app.protocol as any, 'requireConnected').mockImplementation(() => undefined);
    const host = await mount();
    await emit('script_dialog', dialog('t1', { buttons: ['!!llTextBox!!'], textBox: true, textBoxIndex: 0, message: 'Your name?' }));
    expect(host.textContent).toContain('TEXT INPUT');
    expect(host.textContent).not.toContain('!!llTextBox!!');
    const send = button(host, 'SEND')!;
    expect(send.disabled).toBe(true);

    const input = host.querySelector('input[aria-label="Your reply"]') as HTMLInputElement;
    await act(async () => {
      const setter = Object.getOwnPropertyDescriptor(HTMLInputElement.prototype, 'value')!.set!;
      setter.call(input, 'Arapaima');
      input.dispatchEvent(new Event('input', { bubbles: true }));
    });
    expect(button(host, 'SEND')!.disabled).toBe(false);
    await click(button(host, 'SEND'));
    expect(respond).toHaveBeenCalledWith({ id: 't1', text: 'Arapaima' });
  });

  it('shows a teleport offer, accepts it, and says dismissing does not notify the sender', async () => {
    const accept = vi.spyOn(app.protocol, 'acceptLure').mockResolvedValue({ accepted: true, message: '' } as any);
    vi.spyOn(app.protocol as any, 'requireConnected').mockImplementation(() => undefined);
    const host = await mount();
    await emit('lure', lure('l1'));
    expect(host.textContent).toContain('TELEPORT OFFER');
    expect(host.textContent).toContain('Sam Resident offers to teleport you');
    expect(host.textContent).toContain('Come visit');
    expect(host.textContent).toContain('Position 10, 21, 30');
    expect(host.textContent).toContain('grid 1000, 1001');
    expect(host.textContent).toContain('does not notify the sender');
    await click(button(host, 'ACCEPT'));
    expect(accept).toHaveBeenCalledWith('l1');
    expect(host.querySelector('[role="alertdialog"]')).toBeNull();
  });

  it('dismisses with the DISMISS button and with Escape, without answering the grid', async () => {
    const respond = vi.spyOn(app.protocol, 'respondScriptDialog');
    vi.spyOn(app.protocol as any, 'requireConnected').mockImplementation(() => undefined);
    vi.spyOn(app.protocol, 'dismissInteraction').mockResolvedValue({ dismissed: true } as any);
    const host = await mount();
    await emit('lure', lure('l1'));
    await click(button(host, 'DISMISS'));
    expect(host.querySelector('[role="alertdialog"]')).toBeNull();

    await emit('script_dialog', dialog('d1'));
    expect(host.querySelector('[role="alertdialog"]')).not.toBeNull();
    await act(async () => { window.dispatchEvent(new KeyboardEvent('keydown', { key: 'Escape' })); });
    expect(host.querySelector('[role="alertdialog"]')).toBeNull();
    expect(respond).not.toHaveBeenCalled();
  });

  it('shows the grid failure, keeps the request for a retry, and queues later requests behind it', async () => {
    const respond = vi.spyOn(app.protocol, 'respondScriptDialog').mockRejectedValueOnce(new Error('The grid did not respond')).mockResolvedValue({ answered: true } as any);
    vi.spyOn(app.protocol as any, 'requireConnected').mockImplementation(() => undefined);
    const host = await mount();
    await emit('script_dialog', dialog('d1'));
    await emit('lure', lure('l1'));
    expect(host.textContent).toContain('1 more waiting');

    await click(button(host, 'Red'));
    expect(host.querySelector('[role="alert"]')?.textContent).toContain('The grid did not respond');
    expect(host.textContent).toContain('OBJECT DIALOG');

    await click(button(host, 'Red'));
    expect(respond).toHaveBeenCalledTimes(2);
    expect(host.querySelector('[role="alert"]')).toBeNull();
    expect(host.textContent).toContain('TELEPORT OFFER'); // the queued lure is next
  });

  it('offers OK for a dialog with no buttons', async () => {
    vi.spyOn(app.protocol as any, 'requireConnected').mockImplementation(() => undefined);
    vi.spyOn(app.protocol, 'dismissInteraction').mockResolvedValue({ dismissed: true } as any);
    const host = await mount();
    await emit('script_dialog', dialog('n1', { buttons: [] }));
    expect(button(host, 'OK')).toBeDefined();
    await click(button(host, 'OK'));
    expect(host.querySelector('[role="alertdialog"]')).toBeNull();
  });

  describe('Payment confirmation modal', () => {
    it('presents a modal confirmation dialog displaying object name, seller identity, price, and balance', async () => {
      vi.spyOn(app.protocol as any, 'requireConnected').mockImplementation(() => undefined);
      vi.spyOn(app.protocol, 'refreshBalance').mockResolvedValue(1000);
      (app.protocol as any).balance = 1000;

      const host = await mount();
      await act(async () => {
        app.interactions.requestPayment({
          objectId: 'obj-450',
          objectName: 'Sunset Lamp v3',
          sellerName: 'Pat Resident',
          price: 450,
        });
      });

      const sheet = host.querySelector('[role="alertdialog"]');
      expect(sheet).not.toBeNull();
      expect(host.textContent).toContain('PAYMENT CONFIRMATION');
      expect(host.textContent).toContain('Sunset Lamp v3');
      expect(host.textContent).toContain('Seller: Pat Resident');
      expect(host.textContent).toContain('L$ 450');
      expect(host.textContent).toContain('L$ 1,000');
    });

    it('detects insufficient balance, displays an alert, and disables the payment button', async () => {
      vi.spyOn(app.protocol as any, 'requireConnected').mockImplementation(() => undefined);
      vi.spyOn(app.protocol, 'refreshBalance').mockResolvedValue(100);
      (app.protocol as any).balance = 100;

      const host = await mount();
      await act(async () => {
        app.interactions.requestPayment({
          objectId: 'obj-999',
          objectName: 'Luxury Mansion',
          sellerName: 'Real Estate Inc',
          price: 500,
        });
      });

      expect(host.textContent).toContain('Insufficient Funds');
      expect(host.textContent).toContain('requires L$ 500, but your available balance is L$ 100');
      const confirmBtn = button(host, 'INSUFFICIENT FUNDS') || button(host, 'CONFIRM (L$ 500)');
      expect(confirmBtn).toBeDefined();
      expect(confirmBtn!.disabled).toBe(true);
    });

    it('dispatches payment packet on Confirm when balance is sufficient', async () => {
      vi.spyOn(app.protocol as any, 'requireConnected').mockImplementation(() => undefined);
      vi.spyOn(app.protocol, 'refreshBalance').mockResolvedValue(1000);
      (app.protocol as any).balance = 1000;
      const paySpy = vi.spyOn(app.protocol, 'payObject').mockResolvedValue({ paid: 'obj-100', amount: 200, balance: 800 });

      const host = await mount();
      await act(async () => {
        app.interactions.requestPayment({
          objectId: 'obj-100',
          objectName: 'Sculpted Light',
          sellerName: 'Kit Sandalwood',
          price: 200,
        });
      });

      const confirmBtn = button(host, 'CONFIRM (L$ 200)');
      expect(confirmBtn).toBeDefined();
      expect(confirmBtn!.disabled).toBe(false);

      await click(confirmBtn);
      expect(paySpy).toHaveBeenCalledWith({
        objectId: 'obj-100',
        amount: 200,
        targetId: undefined,
        description: 'Payment for Sculpted Light',
      });
      expect(host.querySelector('[role="alertdialog"]')).toBeNull();
    });

    it('discards transaction immediately on Cancel without dispatching payment packets', async () => {
      vi.spyOn(app.protocol as any, 'requireConnected').mockImplementation(() => undefined);
      const paySpy = vi.spyOn(app.protocol, 'payObject');
      const dismissSpy = vi.spyOn(app.protocol, 'dismissInteraction').mockResolvedValue({ dismissed: true });

      const host = await mount();
      await act(async () => {
        app.interactions.requestPayment({
          objectId: 'obj-300',
          objectName: 'Cancelled Dress',
          sellerName: 'Boutique',
          price: 150,
        });
      });

      const cancelBtn = button(host, 'CANCEL');
      expect(cancelBtn).toBeDefined();
      await click(cancelBtn);

      expect(paySpy).not.toHaveBeenCalled();
      expect(dismissSpy).toHaveBeenCalled();
      expect(host.querySelector('[role="alertdialog"]')).toBeNull();
    });
  });

  describe('Inventory offers and Group invites UI modals', () => {
    it('renders an inventory offer modal banner and calls acceptInventoryOffer on ACCEPT', async () => {
      vi.spyOn(app.protocol as any, 'requireConnected').mockImplementation(() => undefined);
      const acceptSpy = vi.spyOn(app.protocol, 'acceptInventoryOffer').mockResolvedValue({ accepted: true } as any);

      const host = await mount();
      await emit('inventory_offer', {
        id: 'io-99',
        senderName: 'Alice Resident',
        itemName: 'Vintage Leather Jacket',
        assetType: 0,
      });

      const sheet = host.querySelector('[role="alertdialog"]');
      expect(sheet).not.toBeNull();
      expect(host.textContent).toContain('INVENTORY OFFER');
      expect(host.textContent).toContain('Alice Resident offered you Vintage Leather Jacket');
      expect(host.textContent).toContain('Sender: Alice Resident');
      expect(host.textContent).toContain('Item: Vintage Leather Jacket');

      const acceptBtn = button(host, 'ACCEPT');
      expect(acceptBtn).toBeDefined();
      await click(acceptBtn);

      expect(acceptSpy).toHaveBeenCalledWith('io-99');
      expect(host.querySelector('[role="alertdialog"]')).toBeNull();
    });

    it('renders a group invitation modal banner and calls declineGroupInvite on DECLINE', async () => {
      vi.spyOn(app.protocol as any, 'requireConnected').mockImplementation(() => undefined);
      const declineSpy = vi.spyOn(app.protocol, 'declineGroupInvite').mockResolvedValue({ declined: true } as any);

      const host = await mount();
      await emit('group_invite', {
        id: 'gi-88',
        senderName: 'Bob Resident',
        groupName: 'Second Life Creators',
        fee: 0,
      });

      const sheet = host.querySelector('[role="alertdialog"]');
      expect(sheet).not.toBeNull();
      expect(host.textContent).toContain('GROUP INVITATION');
      expect(host.textContent).toContain('Invitation to join Second Life Creators');
      expect(host.textContent).toContain('Invited by: Bob Resident');
      expect(host.textContent).toContain('Join Fee: Free');

      const declineBtn = button(host, 'DECLINE');
      expect(declineBtn).toBeDefined();
      await click(declineBtn);

      expect(declineSpy).toHaveBeenCalledWith('gi-88');
      expect(host.querySelector('[role="alertdialog"]')).toBeNull();
    });
  });
});
