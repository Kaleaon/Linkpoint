import { afterEach, describe, expect, it, vi } from 'vitest';
import { createElement, act } from 'react';
import { createRoot, type Root } from 'react-dom/client';
import { AppProvider } from '../../context/AppContext.jsx';
import { ThemeProvider } from '../../context/ThemeContext.jsx';
import HudControls from '../../screens/HudControls.jsx';
import { app } from '../app';

(globalThis as any).IS_REACT_ACT_ENVIRONMENT = true;

const avatar = { id: 'me', localId: 1, avatar: true, position: [1, 1, 1], scale: [1, 1, 1], rotation: [0, 0, 0, 1] };
const root = (id: string, localId: number, name: string, point: number) => ({ id, localId, parentId: 1, attachmentPoint: point, name, position: [0, 0, 0], scale: [0.02, 1, 0.5], rotation: [0, 0, 0, 1] });

let mounted: { host: HTMLElement; root: Root } | null = null;
async function mount() {
  const host = document.createElement('div');
  document.body.appendChild(host);
  const r = createRoot(host);
  await act(async () => { r.render(createElement(AppProvider as any, null, createElement(ThemeProvider as any, null, createElement(HudControls as any)))); });
  mounted = { host, root: r };
  return host;
}
const click = async (el: Element | null) => { await act(async () => { (el as HTMLElement).click(); }); };
const emit = async (type: string, data: any) => { await act(async () => { (app.protocol as any).emit(type, data); }); };
const byText = (host: HTMLElement, text: RegExp) => [...host.querySelectorAll('button')].find((b) => text.test(b.textContent || '')) || null;

afterEach(async () => {
  if (mounted) { await act(async () => mounted!.root.unmount()); mounted.host.remove(); mounted = null; }
  await act(async () => { (app.protocol as any).emit('disconnected', {}); });
  await act(async () => { (app.world as any).emit('huds_changed', []); });
});

describe('HUD controls', () => {
  it('render nothing until the simulator has sent a HUD', async () => {
    const host = await mount();
    expect(host.querySelector('[aria-label="Worn HUDs"]')).toBeNull();
  });

  it('list HUDs, show the chosen one, zoom it and hide it', async () => {
    const host = await mount();
    await emit('scene:object-add', avatar);
    await emit('scene:object-add', root('h1', 10, 'Combat HUD', 35));
    await emit('scene:object-add', root('h2', 11, 'Map HUD', 32));

    const toggle = byText(host, /HUDS \(2\)/);
    expect(toggle).not.toBeNull();
    await click(toggle);
    expect(host.textContent).toContain('Combat HUD');
    expect(host.textContent).toContain('Center');
    expect(host.textContent).toContain('Top Right');

    await click(byText(host, /Combat HUD/));
    expect(app.world.displayedHud?.id).toBe('h1');
    expect(host.querySelector('[aria-label="Hide HUD"]')).not.toBeNull();

    const before = app.world.displayedHud!.size;
    await click(host.querySelector('[aria-label="Make HUD larger"]'));
    expect(app.world.displayedHud!.size).toBeGreaterThan(before);
    await click(host.querySelector('[aria-label="Make HUD smaller"]'));
    expect(app.world.displayedHud!.size).toBeCloseTo(before, 6);

    await click(host.querySelector('[aria-label="Hide HUD"]'));
    expect(app.world.displayedHud).toBeNull();
    expect(host.querySelector('[aria-label="Hide HUD"]')).toBeNull();
  });

  it('say so when a touch fails', async () => {
    const host = await mount();
    await emit('scene:object-add', avatar);
    await emit('scene:object-add', root('h1', 10, 'Combat HUD', 35));
    vi.spyOn(app.protocol, 'touchObject').mockRejectedValueOnce(new Error('object is gone'));
    await act(async () => { await app.world.touchObject('h1'); });
    expect(host.querySelector('[role="alert"]')?.textContent).toBe('Touch failed: object is gone');
  });
});
