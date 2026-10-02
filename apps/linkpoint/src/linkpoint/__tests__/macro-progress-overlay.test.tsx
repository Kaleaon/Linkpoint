import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest';
import { createElement, act } from 'react';
import { createRoot, type Root } from 'react-dom/client';
import { AppProvider } from '../../context/AppContext.jsx';
import { ThemeProvider } from '../../context/ThemeContext.jsx';
import MacroProgressOverlay from '../../components/MacroProgressOverlay.jsx';
import Inventory from '../../screens/Inventory.jsx';
import { macroTaskQueue } from '../macro-task-queue';
import { app } from '../app';

(globalThis as any).IS_REACT_ACT_ENVIRONMENT = true;

let mounted: { host: HTMLElement; root: Root } | null = null;

async function mountComponent(Comp: any) {
  const host = document.createElement('div');
  document.body.appendChild(host);
  const root = createRoot(host);
  await act(async () => {
    root.render(
      createElement(AppProvider as any, null,
        createElement(ThemeProvider as any, null,
          createElement(Comp as any)
        )
      )
    );
  });
  mounted = { host, root };
  return host;
}

const click = async (el: Element | null | undefined) => {
  if (!el) throw new Error('element not found to click');
  await act(async () => {
    (el as HTMLElement).click();
  });
};

afterEach(async () => {
  if (mounted) {
    await act(async () => mounted!.root.unmount());
    mounted.host.remove();
    mounted = null;
  }
  macroTaskQueue.reset();
  vi.restoreAllMocks();
});

describe('MacroProgressOverlay & Inventory Folder Macro Controls', () => {
  beforeEach(() => {
    vi.useFakeTimers();
    macroTaskQueue.reset();
  });

  afterEach(() => {
    vi.useRealTimers();
  });

  it('renders nothing when macroTaskQueue is idle', async () => {
    const host = await mountComponent(MacroProgressOverlay);
    expect(host.querySelector('[role="dialog"]')).toBeNull();
  });

  it('renders overlay dialog and updates progress dynamically during macro task execution', async () => {
    const host = await mountComponent(MacroProgressOverlay);

    const items = [
      { id: '1', name: 'Leather Jacket' },
      { id: '2', name: 'Denim Jeans' },
    ];

    macroTaskQueue.setWearHandler(async () => {
      await new Promise((resolve) => setTimeout(resolve, 50));
    });

    let startPromise: Promise<void>;
    act(() => {
      startPromise = macroTaskQueue.start(items, { mode: 'replace', delayMs: 200 }, 'Casual Outfit');
    });

    const dialog = host.querySelector('[role="dialog"]');
    expect(dialog).not.toBeNull();
    expect(host.textContent).toContain('Casual Outfit');
    expect(host.textContent).toContain('Leather Jacket');

    // Advance time for first item to finish and delay to pass
    await act(async () => {
      await vi.advanceTimersByTimeAsync(250);
    });

    expect(host.textContent).toContain('Denim Jeans');

    // Finish remaining item
    await act(async () => {
      await vi.advanceTimersByTimeAsync(250);
      await startPromise;
    });

    expect(host.querySelector('[role="dialog"]')).toBeNull();
  });

  it('aborts remaining tasks immediately when Cancel is clicked on overlay', async () => {
    const host = await mountComponent(MacroProgressOverlay);

    const items = [
      { id: '1', name: 'Shirt' },
      { id: '2', name: 'Pants' },
      { id: '3', name: 'Shoes' },
    ];

    const wearSpy = vi.fn();
    macroTaskQueue.setWearHandler(async (item) => {
      wearSpy(item.name);
      await new Promise((resolve) => setTimeout(resolve, 50));
    });

    let startPromise: Promise<void>;
    act(() => {
      startPromise = macroTaskQueue.start(items, { mode: 'replace', delayMs: 300 }, 'Full Suit');
    });

    expect(wearSpy).toHaveBeenCalledTimes(1);
    expect(wearSpy).toHaveBeenLastCalledWith('Shirt');

    const cancelButton = [...host.querySelectorAll('button')].find(
      (b) => (b.textContent || '').trim().toLowerCase() === 'cancel'
    );
    expect(cancelButton).toBeDefined();

    await click(cancelButton);

    expect(macroTaskQueue.getStatus().status).toBe('cancelled');

    await act(async () => {
      await vi.advanceTimersByTimeAsync(1000);
      await startPromise;
    });

    expect(wearSpy).toHaveBeenCalledTimes(1);
    expect(host.querySelector('[role="dialog"]')).toBeNull();
  });

  it('provides "Wear All Items" and "Append All Items" triggers in folder context menu and details', async () => {
    app.inventory.folders.set('folder-1', { id: 'folder-1', name: 'Summer Outfit', type: 'folder', children: ['item-1', 'item-2'] });
    app.inventory.items.set('item-1', { id: 'item-1', name: 'Sunglasses', type: 'item', parent: 'folder-1' });
    app.inventory.items.set('item-2', { id: 'item-2', name: 'Shorts', type: 'item', parent: 'folder-1' });

    const host = await mountComponent(Inventory);

    const folderButton = [...host.querySelectorAll('.inventory-button')].find(
      (b) => (b.textContent || '').includes('Summer Outfit')
    );
    expect(folderButton).toBeDefined();

    await click(folderButton);

    // Detail panel displays "Wear All Items" and "Append All Items"
    expect(host.textContent).toContain('Wear All Items');
    expect(host.textContent).toContain('Append All Items');

    const trigger = host.querySelector('.folder-context-trigger');
    expect(trigger).not.toBeNull();

    await click(trigger);

    // Floating context menu displays both options
    const contextMenu = host.querySelector('.inventory-context-menu');
    expect(contextMenu).not.toBeNull();
    expect(contextMenu?.textContent).toContain('Wear All Items');
    expect(contextMenu?.textContent).toContain('Append All Items');

    const startSpy = vi.spyOn(macroTaskQueue, 'start');
    const wearAllBtn = [...contextMenu!.querySelectorAll('button')].find(
      (b) => (b.textContent || '').includes('Wear All Items')
    );

    await click(wearAllBtn);

    expect(startSpy).toHaveBeenCalledWith(
      expect.arrayContaining([
        expect.objectContaining({ name: 'Sunglasses' }),
        expect.objectContaining({ name: 'Shorts' }),
      ]),
      { mode: 'replace' },
      'Summer Outfit'
    );
  });
});
