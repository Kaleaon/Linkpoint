import { describe, it, expect, beforeEach, afterEach } from 'vitest';
import React from 'react';
import { mountScreen, unmount, buttonByText, click, typeInto } from './ui-helpers';
import WindowManagerEngine, { WORKSPACE_PRESETS } from '../../components/WindowManagerEngine';
import ViewportTile from '../../components/ViewportTile';

describe('WindowManagerEngine & ViewportTile', () => {
  let mounted: any = null;

  afterEach(async () => {
    await unmount(mounted);
    mounted = null;
  });

  it('renders ViewportTile with title, controls, and density attribute', async () => {
    let densityToggled = false;
    let floated = false;

    const TestTile = () => (
      <ViewportTile
        id="Chat"
        title="Chat & IM"
        icon="message-square"
        isDocked={true}
        density="compact"
        active={true}
        onToggleDensity={() => { densityToggled = true; }}
        onFloat={() => { floated = true; }}
      >
        <div data-testid="chat-content">Chat Content Area</div>
      </ViewportTile>
    );

    mounted = await mountScreen(TestTile);
    const tileEl = mounted.host.querySelector('[data-testid="viewport-tile-Chat"]');
    expect(tileEl).not.toBeNull();
    expect(tileEl.getAttribute('data-density')).toBe('compact');
    expect(mounted.host.textContent).toContain('Chat & IM');
    expect(mounted.host.querySelector('[data-testid="chat-content"]')).not.toBeNull();

    // Click density toggle button
    const densityBtn = mounted.host.querySelector('button[aria-label="Toggle Density"]');
    expect(densityBtn).not.toBeNull();
    await click(densityBtn);
    expect(densityToggled).toBe(true);

    // Click float button
    const floatBtn = mounted.host.querySelector('button[aria-label="Float Window"]');
    expect(floatBtn).not.toBeNull();
    await click(floatBtn);
    expect(floated).toBe(true);
  });

  it('renders WindowManagerEngine with default In-World Explorer workspace preset', async () => {
    mounted = await mountScreen(WindowManagerEngine);
    const engineEl = mounted.host.querySelector('[data-testid="window-manager-engine"]');
    expect(engineEl).not.toBeNull();

    // Should render toolbar
    const toolbarEl = mounted.host.querySelector('[data-testid="window-manager-toolbar"]');
    expect(toolbarEl).not.toBeNull();

    // Default preset is In-World Explorer, containing 3D View, Map, Radar tiles
    expect(mounted.host.querySelector('[data-testid="viewport-tile-3D View"]')).not.toBeNull();
    expect(mounted.host.querySelector('[data-testid="viewport-tile-Map"]')).not.toBeNull();
    expect(mounted.host.querySelector('[data-testid="viewport-tile-Radar"]')).not.toBeNull();
  });

  it('switches workspace presets when preset buttons are clicked', async () => {
    mounted = await mountScreen(WindowManagerEngine);

    // Click Chat & Social preset button
    const chatSocialBtn = buttonByText(mounted.host, 'Chat & Social');
    expect(chatSocialBtn).not.toBeUndefined();
    await click(chatSocialBtn);

    // Should now contain Chat, Friends, Groups tiles
    expect(mounted.host.querySelector('[data-testid="viewport-tile-Chat"]')).not.toBeNull();
    expect(mounted.host.querySelector('[data-testid="viewport-tile-Friends"]')).not.toBeNull();
    expect(mounted.host.querySelector('[data-testid="viewport-tile-Groups"]')).not.toBeNull();

    // Click Inventory Editor preset button
    const inventoryBtn = buttonByText(mounted.host, 'Inventory Editor');
    expect(inventoryBtn).not.toBeUndefined();
    await click(inventoryBtn);

    // Should now contain Inventory, Outfits, Objects tiles
    expect(mounted.host.querySelector('[data-testid="viewport-tile-Inventory"]')).not.toBeNull();
    expect(mounted.host.querySelector('[data-testid="viewport-tile-Outfits"]')).not.toBeNull();
    expect(mounted.host.querySelector('[data-testid="viewport-tile-Objects"]')).not.toBeNull();
  });

  it('switches layout modes (Split, Masonry, Floaters, Single)', async () => {
    mounted = await mountScreen(WindowManagerEngine);

    // Click Masonry mode button
    const masonryBtn = buttonByText(mounted.host, 'Masonry');
    expect(masonryBtn).not.toBeUndefined();
    await click(masonryBtn);
    expect(mounted.host.querySelector('[data-testid="masonry-tile-grid"]')).not.toBeNull();

    // Click Single mode button
    const singleBtn = buttonByText(mounted.host, 'Single');
    expect(singleBtn).not.toBeUndefined();
    await click(singleBtn);
    expect(mounted.host.querySelector('[data-testid="viewport-tile-3D View"]')).not.toBeNull();

    // Click Split mode button
    const splitBtn = buttonByText(mounted.host, 'Split');
    expect(splitBtn).not.toBeUndefined();
    await click(splitBtn);
    expect(mounted.host.querySelector('[data-testid="split-viewport-container"]')).not.toBeNull();
  });

  it('supports undocking a tile to floating layer and docking it back', async () => {
    mounted = await mountScreen(WindowManagerEngine);

    // Click float button on 3D View tile
    const tile3D = mounted.host.querySelector('[data-testid="viewport-tile-3D View"]');
    expect(tile3D).not.toBeNull();
    const floatBtn = tile3D.querySelector('button[aria-label="Float Window"]');
    expect(floatBtn).not.toBeNull();
    await click(floatBtn);

    // Should render floating layer containing 3D View
    const floatLayer = mounted.host.querySelector('[data-testid="floating-tile-layer"]');
    expect(floatLayer).not.toBeNull();
    const floatingTile3D = floatLayer.querySelector('[data-testid="viewport-tile-3D View"]');
    expect(floatingTile3D).not.toBeNull();

    // Click dock button on floating 3D View tile
    const dockBtn = floatingTile3D.querySelector('button[aria-label="Dock Window"]');
    expect(dockBtn).not.toBeNull();
    await click(dockBtn);

    // Floating layer should disappear or no longer contain 3D View
    expect(mounted.host.querySelector('[data-testid="floating-tile-layer"]')).toBeNull();
  });

  it('allows adding a new tile from the toolbar dropdown', async () => {
    mounted = await mountScreen(WindowManagerEngine);

    // Initially Settings tile is not rendered in default In-World preset
    expect(mounted.host.querySelector('[data-testid="viewport-tile-Settings"]')).toBeNull();

    // Select "Viewer Settings" from select dropdown
    const select = mounted.host.querySelector('select');
    expect(select).not.toBeNull();
    await typeInto(select, 'Settings');

    // Now Settings tile should be added and rendered
    expect(mounted.host.querySelector('[data-testid="viewport-tile-Settings"]')).not.toBeNull();
  });
});
