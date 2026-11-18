// @vitest-environment jsdom
import { afterEach, beforeAll, beforeEach, describe, expect, it, vi } from 'vitest';
import React, { act } from 'react';
import Inventory, { GiveItemModal } from '../../screens/Inventory.jsx';
import { app } from '../app';
import { buttonByText, click, mountScreen, typeInto, unmount, type Mounted } from './ui-helpers';

let mounted: Mounted | null = null;

const friendsList = [
  { id: '11111111-1111-1111-1111-111111111111', name: 'Alice Resident', onlineStatus: 'online' },
  { id: '22222222-2222-2222-2222-222222222222', name: 'Bob Linden', onlineStatus: 'offline' },
];

const nearbyList = [
  { id: '33333333-3333-3333-3333-333333333333', name: 'Charlie Avatar', distance: 12.4 },
  { id: 'self-agent-id', name: 'Self Agent', distance: 0 },
];

beforeAll(() => {
  vi.spyOn(console, 'log').mockImplementation(() => undefined);
  vi.spyOn(console, 'warn').mockImplementation(() => undefined);
});

beforeEach(() => {
  localStorage.clear();
  app.friends.replaceFriends(friendsList);
  app.world.nearbyUsers = [...nearbyList];
  (app.auth as any).user = { id: 'self-agent-id', name: 'Current User' };
  (app.protocol as any).agentId = 'self-agent-id';
  (app.auth as any).isLoggedIn = () => true;

  // Clear and initialize inventory
  app.inventory.items.clear();
  app.inventory.folders.clear();
  app.inventory.rootFolder = {
    id: 'root-id',
    name: 'My Inventory',
    type: 'folder',
    children: ['folder-clothes', 'item-isolated'],
  };
  app.inventory.folders.set('root-id', app.inventory.rootFolder);
  app.inventory.folders.set('folder-clothes', {
    id: 'folder-clothes',
    name: 'Clothing Folder',
    type: 'folder',
    parent: 'root-id',
    children: ['item-shirt'],
  });
  app.inventory.items.set('item-shirt', {
    id: 'item-shirt',
    name: 'Cool Leather Jacket',
    type: 'item',
    assetType: 5,
    parent: 'folder-clothes',
    transferable: true,
  });
  app.inventory.items.set('item-isolated', {
    id: 'item-isolated',
    name: 'Fancy Shoes',
    type: 'item',
    assetType: 5,
    parent: 'root-id',
    transferable: true,
  });
});

afterEach(async () => {
  await unmount(mounted);
  mounted = null;
  vi.restoreAllMocks();
  vi.spyOn(console, 'log').mockImplementation(() => undefined);
  vi.spyOn(console, 'warn').mockImplementation(() => undefined);
});

describe('GiveItemModal component', () => {
  it('does not render when isOpen is false', async () => {
    const TestComponent = () => (
      <GiveItemModal isOpen={false} item={{ id: 'item-1', name: 'Test Item' }} onClose={() => {}} />
    );
    mounted = await mountScreen(TestComponent);
    expect(mounted.host.querySelector('.give-item-modal')).toBeNull();
  });

  it('renders modal with item name and displays online friends and nearby avatars', async () => {
    const item = { id: 'item-shirt', name: 'Cool Leather Jacket', transferable: true };
    const TestComponent = () => (
      <GiveItemModal isOpen={true} item={item} onClose={() => {}} />
    );
    mounted = await mountScreen(TestComponent);

    expect(mounted.host.querySelector('.give-item-modal')).toBeTruthy();
    expect(mounted.host.textContent).toContain('Cool Leather Jacket');
    expect(mounted.host.textContent).toContain('Online Friends (1)');
    expect(mounted.host.textContent).toContain('Alice Resident');
    expect(mounted.host.textContent).not.toContain('Bob Linden'); // Bob is offline

    expect(mounted.host.textContent).toContain('Nearby Avatars (1)');
    expect(mounted.host.textContent).toContain('Charlie Avatar');
    expect(mounted.host.textContent).not.toContain('Self Agent'); // Self filtered out
  });

  it('selects friend when friend button is clicked and populates recipient input', async () => {
    const item = { id: 'item-shirt', name: 'Cool Leather Jacket', transferable: true };
    const TestComponent = () => (
      <GiveItemModal isOpen={true} item={item} onClose={() => {}} />
    );
    mounted = await mountScreen(TestComponent);

    const friendBtn = buttonByText(mounted.host, /Alice Resident/);
    expect(friendBtn).toBeTruthy();
    await click(friendBtn);

    const input = mounted.host.querySelector('#recipient-avatar-id') as HTMLInputElement;
    expect(input.value).toBe('11111111-1111-1111-1111-111111111111');
  });

  it('selects nearby avatar when nearby button is clicked and populates recipient input', async () => {
    const item = { id: 'item-shirt', name: 'Cool Leather Jacket', transferable: true };
    const TestComponent = () => (
      <GiveItemModal isOpen={true} item={item} onClose={() => {}} />
    );
    mounted = await mountScreen(TestComponent);

    const nearbyBtn = buttonByText(mounted.host, /Charlie Avatar/);
    expect(nearbyBtn).toBeTruthy();
    await click(nearbyBtn);

    const input = mounted.host.querySelector('#recipient-avatar-id') as HTMLInputElement;
    expect(input.value).toBe('33333333-3333-3333-3333-333333333333');
  });

  it('allows manual entry of avatar UUID into input', async () => {
    const item = { id: 'item-shirt', name: 'Cool Leather Jacket', transferable: true };
    const TestComponent = () => (
      <GiveItemModal isOpen={true} item={item} onClose={() => {}} />
    );
    mounted = await mountScreen(TestComponent);

    const input = mounted.host.querySelector('#recipient-avatar-id') as HTMLInputElement;
    await typeInto(input, 'manual-uuid-999');
    expect(input.value).toBe('manual-uuid-999');
  });

  it('shows error if submitting without selecting a recipient', async () => {
    const item = { id: 'item-shirt', name: 'Cool Leather Jacket', transferable: true };
    const TestComponent = () => (
      <GiveItemModal isOpen={true} item={item} onClose={() => {}} />
    );
    mounted = await mountScreen(TestComponent);

    const giveBtn = buttonByText(mounted.host, /GIVE ITEM/);
    expect(giveBtn).toBeTruthy();
    await click(giveBtn);

    expect(mounted.host.textContent).toContain('Please select or enter a recipient avatar ID.');
  });

  it('warns and disables give button if item is marked non-transferable', async () => {
    const nonTransferItem = { id: 'item-locked', name: 'No Transfer Hair', transferable: false };
    const TestComponent = () => (
      <GiveItemModal isOpen={true} item={nonTransferItem} onClose={() => {}} />
    );
    mounted = await mountScreen(TestComponent);

    expect(mounted.host.textContent).toContain('This item cannot be transferred');

    const giveBtn = buttonByText(mounted.host, /GIVE ITEM/);
    expect(giveBtn?.disabled).toBe(true);
  });

  it('invokes app.inventory.offerItem and displays success on valid submission', async () => {
    const offerSpy = vi.spyOn(app.inventory, 'offerItem').mockResolvedValueOnce({ offered: true });
    const item = { id: 'item-shirt', name: 'Cool Leather Jacket', transferable: true };
    const onClose = vi.fn();

    const TestComponent = () => (
      <GiveItemModal isOpen={true} item={item} onClose={onClose} />
    );
    mounted = await mountScreen(TestComponent);

    const friendBtn = buttonByText(mounted.host, /Alice Resident/);
    await click(friendBtn);

    const giveBtn = buttonByText(mounted.host, /GIVE ITEM/);
    await click(giveBtn);

    expect(offerSpy).toHaveBeenCalledWith('11111111-1111-1111-1111-111111111111', item);
    expect(mounted.host.textContent).toContain('offered successfully!');
  });

  it('displays error message when app.inventory.offerItem fails', async () => {
    vi.spyOn(app.inventory, 'offerItem').mockRejectedValueOnce(new Error('Simulated network failure'));
    const item = { id: 'item-shirt', name: 'Cool Leather Jacket', transferable: true };

    const TestComponent = () => (
      <GiveItemModal isOpen={true} item={item} onClose={() => {}} />
    );
    mounted = await mountScreen(TestComponent);

    const friendBtn = buttonByText(mounted.host, /Alice Resident/);
    await click(friendBtn);

    const giveBtn = buttonByText(mounted.host, /GIVE ITEM/);
    await click(giveBtn);

    expect(mounted.host.textContent).toContain('Simulated network failure');
  });

  it('triggers onClose when Cancel button or close icon is clicked', async () => {
    const onClose = vi.fn();
    const item = { id: 'item-shirt', name: 'Cool Leather Jacket', transferable: true };

    const TestComponent = () => (
      <GiveItemModal isOpen={true} item={item} onClose={onClose} />
    );
    mounted = await mountScreen(TestComponent);

    const cancelBtn = buttonByText(mounted.host, 'CANCEL');
    await click(cancelBtn);
    expect(onClose).toHaveBeenCalledTimes(1);

    const closeBtn = mounted.host.querySelector('button[aria-label="Close give item modal"]') as HTMLButtonElement;
    await click(closeBtn);
    expect(onClose).toHaveBeenCalledTimes(2);
  });
});

describe('Inventory screen Give / Share integration', () => {
  it('shows "Give / Share Item" in record detail panel when an item is selected', async () => {
    mounted = await mountScreen(Inventory);

    // Locate the item button
    const itemButton = [...mounted.host.querySelectorAll('.inventory-row')].find(
      (b) => b.textContent?.includes('Fancy Shoes')
    );
    expect(itemButton).toBeTruthy();
    await click(itemButton);

    // Detail panel should be visible with "Give / Share Item" button
    const giveBtn = buttonByText(mounted.host, /Give \/ Share Item/);
    expect(giveBtn).toBeTruthy();

    // Clicking Give / Share Item opens the modal
    await click(giveBtn);
    expect(mounted.host.querySelector('.give-item-modal')).toBeTruthy();
    expect(mounted.host.textContent).toContain('Fancy Shoes');
  });

  it('shows "Give / Share Item" in context menu on right click of an item', async () => {
    mounted = await mountScreen(Inventory);

    const itemButton = [...mounted.host.querySelectorAll('.inventory-row')].find(
      (b) => b.textContent?.includes('Fancy Shoes')
    );
    expect(itemButton).toBeTruthy();

    // Trigger right click (contextmenu event)
    await act(async () => {
      itemButton!.dispatchEvent(new MouseEvent('contextmenu', { bubbles: true, clientX: 100, clientY: 150 }));
    });

    const contextMenu = mounted.host.querySelector('.inventory-context-menu');
    expect(contextMenu).toBeTruthy();
    expect(contextMenu?.textContent).toContain('Wear Item');
    expect(contextMenu?.textContent).toContain('Give / Share Item');

    // Click Give / Share Item in context menu
    const menuGiveBtn = [...contextMenu!.querySelectorAll('button')].find((b) =>
      b.textContent?.includes('Give / Share Item')
    );
    expect(menuGiveBtn).toBeTruthy();
    await click(menuGiveBtn);

    // Modal should be opened
    expect(mounted.host.querySelector('.give-item-modal')).toBeTruthy();
  });

  it('does NOT show "Give / Share Item" for folder in context menu or detail panel', async () => {
    mounted = await mountScreen(Inventory);

    const folderButton = [...mounted.host.querySelectorAll('.inventory-row')].find(
      (b) => b.textContent?.includes('Clothing Folder')
    );
    expect(folderButton).toBeTruthy();

    // Select the folder
    await click(folderButton);

    // Detail panel for folder should have "Wear All Items" and "Append All Items", but NOT "Give / Share Item"
    const detailPanel = mounted.host.querySelector('.record-detail');
    expect(detailPanel).toBeTruthy();
    expect(detailPanel?.textContent).toContain('Wear All Items');
    expect(detailPanel?.textContent).not.toContain('Give / Share Item');

    // Context menu on folder
    await act(async () => {
      folderButton!.dispatchEvent(new MouseEvent('contextmenu', { bubbles: true, clientX: 100, clientY: 150 }));
    });
    const contextMenu = mounted.host.querySelector('.inventory-context-menu');
    expect(contextMenu).toBeTruthy();
    expect(contextMenu?.textContent).toContain('Wear All Items');
    expect(contextMenu?.textContent).not.toContain('Give / Share Item');
  });
});
