// @vitest-environment jsdom
import { afterEach, beforeAll, beforeEach, describe, expect, it, vi } from 'vitest';
import { act } from 'react';
import OutfitViewer, { DEFAULT_SHAPE_VALUES, SHAPE_PRESETS } from '../../screens/OutfitViewer.jsx';
import { app } from '../app';
import { buttonByText, click, mountScreen, typeInto, unmount, type Mounted } from './ui-helpers';

let mounted: Mounted | null = null;

beforeAll(() => {
  vi.spyOn(console, 'log').mockImplementation(() => undefined);
});

beforeEach(() => {
  localStorage.clear();
});

afterEach(async () => {
  await unmount(mounted);
  mounted = null;
  vi.restoreAllMocks();
  vi.spyOn(console, 'log').mockImplementation(() => undefined);
});

describe('OutfitViewer screen', () => {
  it('renders the header and default 3D Viewport with panel switcher buttons', async () => {
    mounted = await mountScreen(OutfitViewer);
    expect(mounted.host.textContent).toContain('FULL OUTFIT VIEWER');
    expect(mounted.host.textContent).toContain('Avatar 3D Viewport, Mesh Inspection & Shape Tuning');
    
    // Check panel buttons exist
    expect(buttonByText(mounted.host, /3D View/)).toBeTruthy();
    expect(buttonByText(mounted.host, /Outfit Items/)).toBeTruthy();
    expect(buttonByText(mounted.host, /Shape Sliders/)).toBeTruthy();
    expect(buttonByText(mounted.host, /Mesh View/)).toBeTruthy();
  });

  it('switches between panels (Outfit Items, Shape Sliders, Mesh View)', async () => {
    mounted = await mountScreen(OutfitViewer);
    
    // Switch to Outfit Items
    await click(buttonByText(mounted.host, /Outfit Items/)!);
    expect(mounted.host.textContent).toContain('WORN OUTFIT ITEMS');

    // Switch to Shape Sliders
    await click(buttonByText(mounted.host, /Shape Sliders/)!);
    expect(mounted.host.textContent).toContain('AVATAR SHAPE SLIDERS');
    expect(mounted.host.textContent).toContain('PRESETS');

    // Switch to Mesh View
    await click(buttonByText(mounted.host, /Mesh View/)!);
    expect(mounted.host.textContent).toContain('MESH & SKELETON INSPECTION');
    expect(mounted.host.textContent).toContain('AVATAR MESH STATISTICS');
  });

  it('handles camera presets and 3D rotation controls', async () => {
    mounted = await mountScreen(OutfitViewer);

    // Click camera preset buttons
    await click(buttonByText(mounted.host, 'Back')!);
    await click(buttonByText(mounted.host, 'Face')!);
    await click(buttonByText(mounted.host, 'Side')!);
    await click(buttonByText(mounted.host, 'Full')!);
    await click(buttonByText(mounted.host, 'Front')!);

    // Toggle Auto-Spin button
    const autoSpinBtn = buttonByText(mounted.host, /AUTO-SPIN/);
    expect(autoSpinBtn).toBeTruthy();
    await click(autoSpinBtn!);
    expect(mounted.host.textContent).toContain('SPINNING');

    // Toggle Mesh Wireframe button
    const meshBtn = buttonByText(mounted.host, /MESH/);
    expect(meshBtn).toBeTruthy();
    await click(meshBtn!);
    expect(mounted.host.textContent).toContain('WIREFRAME');
  });

  it('applies shape presets, adjusts shape sliders, and saves shape', async () => {
    mounted = await mountScreen(OutfitViewer);

    // Switch to Shape Sliders
    await click(buttonByText(mounted.host, /Shape Sliders/)!);

    // Select Athletic preset
    const athleticBtn = buttonByText(mounted.host, 'Athletic');
    expect(athleticBtn).toBeTruthy();
    await click(athleticBtn!);
    expect(mounted.host.textContent).toContain('Applied Athletic shape preset');

    // Select Petite preset
    const petiteBtn = buttonByText(mounted.host, 'Petite');
    expect(petiteBtn).toBeTruthy();
    await click(petiteBtn!);
    expect(mounted.host.textContent).toContain('Applied Petite shape preset');

    // Reset Shape
    const resetBtn = buttonByText(mounted.host, 'RESET SHAPE');
    expect(resetBtn).toBeTruthy();
    await click(resetBtn!);
    expect(mounted.host.textContent).toContain('Reset shape sliders to default');

    // Apply / Save Shape
    const shapeUpdatedSpy = vi.fn();
    app.inventory.on('shape_updated', shapeUpdatedSpy);

    const applyBtn = buttonByText(mounted.host, 'APPLY SHAPE');
    expect(applyBtn).toBeTruthy();
    await click(applyBtn!);
    expect(shapeUpdatedSpy).toHaveBeenCalledTimes(1);
    expect(mounted.host.textContent).toContain('Shape saved & applied to avatar');
  });

  it('filters outfit items and toggles worn state', async () => {
    mounted = await mountScreen(OutfitViewer);

    // Switch to Outfit Items
    await click(buttonByText(mounted.host, /Outfit Items/)!);

    // Search filter input
    const filterInput = mounted.host.querySelector('input[placeholder="Filter outfit items..."]') as HTMLInputElement;
    expect(filterInput).toBeTruthy();
    await typeInto(filterInput, 'Denim');

    expect(mounted.host.textContent).toContain('Urban Casual Denim Jacket');
    expect(mounted.host.textContent).not.toContain('Hazel Eyes');

    // Clear filter
    await typeInto(filterInput, '');

    // Toggle worn button for an item
    const wornBtns = [...mounted.host.querySelectorAll('button')].filter((b) => b.textContent?.trim() === 'WORN' || b.textContent?.trim() === 'WEAR');
    expect(wornBtns.length).toBeGreaterThan(0);
    const initialText = wornBtns[0].textContent;
    await click(wornBtns[0]);
    expect(wornBtns[0].textContent).not.toBe(initialText);
  });
});
