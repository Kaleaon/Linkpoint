import { afterEach, describe, expect, it, vi } from 'vitest';
import { createElement, act } from 'react';
import { createRoot, type Root } from 'react-dom/client';
import { AppProvider } from '../../context/AppContext.jsx';
import { ThemeProvider } from '../../context/ThemeContext.jsx';
import OutfitCarouselDrawer from '../../components/OutfitCarouselDrawer.jsx';
import World3D from '../../screens/World3D.jsx';
import { app } from '../app';

(globalThis as any).IS_REACT_ACT_ENVIRONMENT = true;

let mounted: { host: HTMLElement; root: Root } | null = null;

async function mountComponent(cmp: any) {
  const host = document.createElement('div');
  document.body.appendChild(host);
  const r = createRoot(host);
  await act(async () => {
    r.render(
      createElement(
        AppProvider as any,
        null,
        createElement(ThemeProvider as any, null, cmp)
      )
    );
  });
  mounted = { host, root: r };
  return host;
}

const click = async (el: Element | null) => {
  await act(async () => {
    (el as HTMLElement)?.click();
  });
};

afterEach(async () => {
  if (mounted) {
    await act(async () => mounted!.root.unmount());
    mounted.host.remove();
    mounted = null;
  }
});

describe('Outfit Carousel Drawer for Mobile Live Preview', () => {
  it('does not render when closed', async () => {
    const host = await mountComponent(createElement(OutfitCarouselDrawer, { open: false, onClose: () => {} }));
    expect(host.querySelector('[aria-label="Outfit Carousel Drawer"]')).toBeNull();
  });

  it('renders horizontal carousel with saved outfits when open', async () => {
    const host = await mountComponent(createElement(OutfitCarouselDrawer, { open: true, onClose: () => {} }));
    const drawer = host.querySelector('[aria-label="Outfit Carousel Drawer"]');
    expect(drawer).not.toBeNull();

    expect(host.textContent).toContain('Urban Casual v2');
    expect(host.textContent).toContain('Cyberpunk Tactical');
    expect(host.textContent).toContain('Formal Eveningwear');
    expect(host.textContent).toContain('Beach & Swimwear');
  });

  it('filters outfits cleanly via search input', async () => {
    const host = await mountComponent(createElement(OutfitCarouselDrawer, { open: true, onClose: () => {} }));
    const searchInput = host.querySelector('input[aria-label="Search saved outfits"]') as HTMLInputElement;
    expect(searchInput).not.toBeNull();

    await act(async () => {
      const nativeInputValueSetter = Object.getOwnPropertyDescriptor(window.HTMLInputElement.prototype, 'value')?.set;
      nativeInputValueSetter?.call(searchInput, 'Cyberpunk');
      searchInput.dispatchEvent(new Event('change', { bubbles: true }));
      searchInput.dispatchEvent(new Event('input', { bubbles: true }));
    });

    expect(host.textContent).toContain('Cyberpunk Tactical');
    expect(host.textContent).not.toContain('Beach & Swimwear');
  });

  it('triggers immediate batch wear operation when tapping WEAR OUTFIT card', async () => {
    const wearSpy = vi.spyOn(app.inventory, 'wearOutfit');
    const host = await mountComponent(createElement(OutfitCarouselDrawer, { open: true, onClose: () => {} }));

    const buttons = Array.from(host.querySelectorAll('button'));
    const cyberpunkBtn = buttons.find((btn) => btn.textContent?.includes('WEAR OUTFIT'));
    expect(cyberpunkBtn).not.toBeUndefined();

    await click(cyberpunkBtn!);
    expect(wearSpy).toHaveBeenCalledWith('outfit-2');
  });

  it('mounts OUTFITS toggle button in World3D to open the carousel drawer over 3D viewport', async () => {
    const host = await mountComponent(createElement(World3D, { desktopBackdrop: false }));
    const toggleBtn = host.querySelector('button[aria-label="Toggle outfit drawer"]');
    expect(toggleBtn).not.toBeNull();

    expect(host.querySelector('[aria-label="Outfit Carousel Drawer"]')).toBeNull();

    await click(toggleBtn);
    expect(host.querySelector('[aria-label="Outfit Carousel Drawer"]')).not.toBeNull();
  });

  it('dismisses drawer when tapping the backdrop overlay or close button', async () => {
    const onClose = vi.fn();
    const host = await mountComponent(createElement(OutfitCarouselDrawer, { open: true, onClose }));

    const backdrop = host.querySelector('.outfit-drawer-backdrop');
    expect(backdrop).not.toBeNull();

    await click(backdrop);
    expect(onClose).toHaveBeenCalled();
  });
});
