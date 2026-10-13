// @vitest-environment jsdom
import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest';
import CacheScreen from '../../screens/CacheScreen.jsx';
import { app } from '../app';
import { localCache } from '../local-cache';
import { buttonByText, click, flush, mountScreen, unmount, type Mounted } from './ui-helpers';

let mounted: Mounted | null = null;

beforeEach(() => {
  localStorage.clear();
  vi.spyOn(window, 'confirm').mockImplementation(() => true);
});

afterEach(async () => {
  await unmount(mounted);
  mounted = null;
  vi.restoreAllMocks();
});

describe('CacheScreen', () => {
  it('renders active cache status and texture cache clear control', async () => {
    mounted = await mountScreen(CacheScreen);
    expect(mounted.host.textContent).toContain('CACHE & FLASHDRIVE STORAGE');
    expect(mounted.host.textContent).toContain('ACTIVE CACHE STATUS');
    expect(buttonByText(mounted.host, /CLEAR TEXTURE CACHE/)).toBeTruthy();
    expect(buttonByText(mounted.host, /CLEAR ALL CACHE/)).toBeTruthy();
  });

  it('cancels clear texture cache action if user denies confirmation', async () => {
    vi.spyOn(window, 'confirm').mockReturnValue(false);
    const clearSpy = vi.spyOn(app, 'clearTextureCache');

    mounted = await mountScreen(CacheScreen);
    const clearBtn = buttonByText(mounted.host, /CLEAR TEXTURE CACHE/)!;
    await click(clearBtn);

    expect(clearSpy).not.toHaveBeenCalled();
  });

  it('clears texture cache, dispatches notification, and displays success message when confirmed', async () => {
    vi.spyOn(window, 'confirm').mockReturnValue(true);
    const appClearSpy = vi.spyOn(app, 'clearTextureCache').mockResolvedValue(undefined);
    const notifySpy = vi.fn();

    mounted = await mountScreen(CacheScreen);
    mounted.ctx.current.actions.notify = notifySpy;

    const clearBtn = buttonByText(mounted.host, /CLEAR TEXTURE CACHE/)!;
    await click(clearBtn);
    await flush();

    expect(appClearSpy).toHaveBeenCalledTimes(1);
    expect(notifySpy).toHaveBeenCalledWith('Texture cache cleared');
    expect(mounted.host.textContent).toContain('Texture cache cleared successfully.');
  });

  it('falls back to localCache.clearTextureCache if app method is absent and handles errors', async () => {
    vi.spyOn(window, 'confirm').mockReturnValue(true);
    const localSpy = vi.spyOn(localCache, 'clearTextureCache').mockRejectedValueOnce(new Error('IndexedDB storage fault'));

    // Temporarily replace app.clearTextureCache
    const originalMethod = app.clearTextureCache;
    delete (app as any).clearTextureCache;

    try {
      mounted = await mountScreen(CacheScreen);
      const clearBtn = buttonByText(mounted.host, /CLEAR TEXTURE CACHE/)!;
      await click(clearBtn);
      await flush();

      expect(localSpy).toHaveBeenCalled();
      expect(mounted.host.textContent).toContain('IndexedDB storage fault');
    } finally {
      app.clearTextureCache = originalMethod;
    }
  });
});
