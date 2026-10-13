// @vitest-environment jsdom
import { describe, expect, it, vi } from 'vitest';
import { readFileSync } from 'node:fs';
import { resolve } from 'node:path';
import React, { act, createElement } from 'react';
import { createRoot } from 'react-dom/client';
import {
  WindlightDay, getDefaultWindlightDay, parseWindlightPreset, presetToSkyFrame, sunDirection,
  estimatedSunHour, sunPhaseToSunHour, windlightEnvironment, WINDLIGHT_HOUR_TABLE, SL_DAY_SECONDS,
  WINDLIGHT_PRESETS, getWindlightPresetOption, selectEnvironmentPreset, WindlightPresetManager,
  useWindlightPreset, EnvironmentPresetSelector, WindlightPresetSelector,
  type ViewerServiceProtocol,
} from '../windlight';
import { computeSkyUniforms } from '../sky';

(globalThis as any).IS_REACT_ACT_ENVIRONMENT = true;

async function renderComponent(element: React.ReactElement) {
  const host = document.createElement('div');
  document.body.appendChild(host);
  const root = createRoot(host);
  await act(async () => {
    root.render(element);
  });
  return {
    host,
    cleanup: async () => {
      await act(async () => {
        root.unmount();
      });
      host.remove();
    },
  };
}

const xml = (name: string) => readFileSync(resolve(__dirname, '../../assets/windlight', `${name}.xml`), 'utf8');

describe('Windlight preset parsing (Lumiya rules)', () => {
  const preset = parseWindlightPreset(xml('A-6AM'));

  it('divides by Lumiya\'s factors and gamma-encodes ambient and sunlight', () => {
    expect(preset.blueDensity[0]).toBeCloseTo(0.15793180465698242 / 2, 6);
    expect(preset.hazeDensity[0]).toBeCloseTo(0.53999996185302734 / 5, 6);
    expect(preset.hazeHorizon[0]).toBeCloseTo(0.16 / 5, 5);
    expect(preset.ambient[0]).toBeCloseTo(Math.pow(0.80999994277954102 / 3, 1 / 2.2) * 1.25, 6);
    expect(preset.sunlightColor[0]).toBeCloseTo(Math.pow(2.369999885559082 / 3, 1 / 2.2) * 1.25, 6);
    expect(preset.lightnorm[2]).toBeCloseTo(0.99556195735931396, 6);
  });

  it('rejects a preset with a missing field instead of inventing a value', () => {
    expect(() => parseWindlightPreset('<llsd><map><key>ambient</key><array><real>1</real><real>1</real><real>1</real><real>1</real></array></map></llsd>')).toThrow(/sunlight_color/);
  });

  it('rejects non-numeric values', () => {
    const bad = xml('A-6AM').replace('<real>0.15793180465698242</real>', '<string>x</string>');
    expect(() => parseWindlightPreset(bad)).toThrow(/blue_density/);
  });
});

describe('Windlight day interpolation', () => {
  const day = getDefaultWindlightDay();

  it('loads all eight bundled presets', () => {
    expect(day.presets).toHaveLength(8);
    expect(WINDLIGHT_HOUR_TABLE).toHaveLength(8);
  });

  it('returns a preset exactly at its hour and the midpoint halfway', () => {
    const noon = day.at(0.5), threePm = day.at(0.625), mid = day.at(0.5625);
    expect(noon.blueDensity[0]).toBeCloseTo(day.presets[4].blueDensity[0], 9);
    expect(threePm.hazeDensity[0]).toBeCloseTo(day.presets[5].hazeDensity[0], 9);
    expect(mid.ambient[1]).toBeCloseTo((day.presets[4].ambient[1] + day.presets[5].ambient[1]) / 2, 9);
  });

  it('wraps from 9 PM back to midnight and tolerates out-of-range hours', () => {
    const a = day.at(0.9375);
    expect(a.ambient[0]).toBeCloseTo((day.presets[7].ambient[0] + day.presets[0].ambient[0]) / 2, 9);
    expect(day.at(1.5).blueDensity[0]).toBeCloseTo(day.at(0.5).blueDensity[0], 9);
    expect(day.at(-0.5).blueDensity[0]).toBeCloseTo(day.at(0.5).blueDensity[0], 9);
  });

  it('keeps the sun angle continuous across the 6 AM wrap (no spin the long way round)', () => {
    const before = day.at(0.2).sunAngle, after = day.at(0.3).sunAngle;
    const delta = Math.min(Math.abs(after - before), Math.PI * 2 - Math.abs(after - before));
    expect(delta).toBeLessThan(0.7);
  });

  it('requires exactly eight presets', () => {
    expect(() => new WindlightDay([])).toThrow();
  });
});

describe('sun and sky output', () => {
  const day = getDefaultWindlightDay();

  it('puts the sun overhead at noon, below the horizon at midnight and near the east horizon at 6 AM', () => {
    expect(presetToSkyFrame(day.at(0.5)).sunDirection[2]).toBeGreaterThan(0.99);
    expect(presetToSkyFrame(day.at(0)).sunDirection[2]).toBeLessThan(-0.99);
    const dawn = presetToSkyFrame(day.at(0.25)).sunDirection;
    expect(dawn[2]).toBeGreaterThan(0);
    expect(dawn[2]).toBeLessThan(0.2);
    expect(dawn[0]).toBeGreaterThan(0.9);
  });

  it('rotates with east_angle', () => {
    const d = sunDirection(0, Math.PI / 2);
    expect(d[0]).toBeCloseTo(0, 6);
    expect(d[1]).toBeCloseTo(1, 6);
  });

  it('gives a blue-leaning daytime sky and a darker night sky through computeSkyUniforms', () => {
    const noon = computeSkyUniforms(presetToSkyFrame(day.at(0.5)));
    const midnight = computeSkyUniforms(presetToSkyFrame(day.at(0)));
    expect(noon.skyColor[2]).toBeGreaterThan(noon.skyColor[0]);
    const lum = (c: number[]) => c[0] + c[1] + c[2];
    expect(lum(noon.skyColor)).toBeGreaterThan(lum(midnight.skyColor));
    expect(midnight.starBrightness).toBeGreaterThan(0);
    expect(noon.starBrightness).toBe(0);
  });
});

describe('estimated sun hour', () => {
  it('cycles every four hours and stays within [0, 1)', () => {
    expect(estimatedSunHour(0)).toBe(0);
    expect(estimatedSunHour(SL_DAY_SECONDS * 500 * 1000)).toBe(0);
    expect(estimatedSunHour((SL_DAY_SECONDS / 2) * 1000)).toBeCloseTo(0.5, 9);
    for (const t of [-1e9, 1, 123456789012]) {
      const h = estimatedSunHour(t);
      expect(h).toBeGreaterThanOrEqual(0);
      expect(h).toBeLessThan(1);
    }
  });

  it('builds an environment flagged as the bundled default, not simulator data', () => {
    expect(windlightEnvironment(0.5).source).toBe('default-windlight');
  });
});

describe('sunPhaseToSunHour conversion', () => {
  it('converts SunPhase radians to normalized sun hour fractions', () => {
    expect(sunPhaseToSunHour(0)).toBeCloseTo(0.25, 6); // Sunrise (6 AM)
    expect(sunPhaseToSunHour(Math.PI / 2)).toBeCloseTo(0.5, 6); // Noon (12 PM)
    expect(sunPhaseToSunHour(Math.PI)).toBeCloseTo(0.75, 6); // Sunset (6 PM)
    expect(sunPhaseToSunHour(1.5 * Math.PI)).toBeCloseTo(0, 6); // Midnight (12 AM)
    expect(sunPhaseToSunHour(2 * Math.PI)).toBeCloseTo(0.25, 6); // Wraps to sunrise
  });

  it('handles negative sun phases and normalizes into [0, 1)', () => {
    expect(sunPhaseToSunHour(-0.5 * Math.PI)).toBeCloseTo(0, 6);
    expect(sunPhaseToSunHour(-Math.PI)).toBeCloseTo(0.75, 6);
  });

  it('gracefully handles non-numeric and non-finite inputs by returning 0', () => {
    expect(sunPhaseToSunHour(NaN)).toBe(0);
    expect(sunPhaseToSunHour(Infinity)).toBe(0);
    expect(sunPhaseToSunHour(null as any)).toBe(0);
  });
});

describe('Environment preset options catalog', () => {
  it('defines region default and 8 day cycle presets', () => {
    expect(WINDLIGHT_PRESETS).toHaveLength(9);
    expect(WINDLIGHT_PRESETS[0].id).toBe('region-default');
    expect(WINDLIGHT_PRESETS[0].hour).toBeNull();
    expect(WINDLIGHT_PRESETS[1].id).toBe('A-12AM');
    expect(WINDLIGHT_PRESETS[5].id).toBe('A-12PM');
    expect(WINDLIGHT_PRESETS[5].hour).toBe(0.5);
  });

  it('looks up preset options by id case-insensitively', () => {
    expect(getWindlightPresetOption('a-12pm')?.id).toBe('A-12PM');
    expect(getWindlightPresetOption('REGION-DEFAULT')?.id).toBe('region-default');
    expect(getWindlightPresetOption('non-existent')).toBeUndefined();
    expect(getWindlightPresetOption('')).toBeUndefined();
  });
});

describe('selectEnvironmentPreset and protocol dispatching', () => {
  it('selects a valid preset and calculates sky frame', async () => {
    const result = await selectEnvironmentPreset('A-12PM');
    expect(result.selectedPresetId).toBe('A-12PM');
    expect(result.isCustomOverride).toBe(true);
    expect(result.activeHour).toBe(0.5);
    expect(result.isConnected).toBe(true);
    expect(result.error).toBeNull();
    expect(result.preset).not.toBeNull();
    expect(result.skyFrame).not.toBeNull();
  });

  it('binds to protocol.setEnvironment method when provided', async () => {
    const mockSetEnv = vi.fn().mockResolvedValue(true);
    const protocol: ViewerServiceProtocol = {
      connected: true,
      setEnvironment: mockSetEnv,
    };

    const result = await selectEnvironmentPreset('A-6AM', protocol);
    expect(mockSetEnv).toHaveBeenCalledWith('A-6AM', 0.25);
    expect(result.selectedPresetId).toBe('A-6AM');
    expect(result.isConnected).toBe(true);
    expect(result.error).toBeNull();
  });

  it('binds to protocol.dispatch method when setEnvironment is not present', async () => {
    const mockDispatch = vi.fn().mockResolvedValue(true);
    const protocol: ViewerServiceProtocol = {
      connected: true,
      dispatch: mockDispatch,
    };

    await selectEnvironmentPreset('A-6PM', protocol);
    expect(mockDispatch).toHaveBeenCalledWith('ENVIRONMENT_PRESET_CHANGE', { presetId: 'A-6PM', hour: 0.75 });
  });

  it('binds to protocol.send method when setEnvironment and dispatch are not present', async () => {
    const mockSend = vi.fn().mockResolvedValue(true);
    const protocol: ViewerServiceProtocol = {
      connected: true,
      send: mockSend,
    };

    await selectEnvironmentPreset('A-3PM', protocol);
    expect(mockSend).toHaveBeenCalledWith('set_environment', { presetId: 'A-3PM', hour: 0.625 });
  });

  it('handles disconnected network sessions gracefully and provides local preview', async () => {
    const protocol: ViewerServiceProtocol = {
      connected: false,
    };

    const result = await selectEnvironmentPreset('A-12AM', protocol);
    expect(result.isConnected).toBe(false);
    expect(result.error).toContain('Disconnected from simulator');
    expect(result.selectedPresetId).toBe('A-12AM');
    expect(result.skyFrame).not.toBeNull();
  });

  it('handles transient protocol call failures gracefully', async () => {
    const protocol: ViewerServiceProtocol = {
      connected: true,
      setEnvironment: vi.fn().mockRejectedValue(new Error('Network RPC Timeout')),
    };

    const result = await selectEnvironmentPreset('A-12PM', protocol);
    expect(result.error).toContain('Network RPC Timeout');
    expect(result.selectedPresetId).toBe('A-12PM');
    expect(result.skyFrame).not.toBeNull();
  });

  it('falls back to region-default for empty or invalid preset IDs', async () => {
    const result = await selectEnvironmentPreset('');
    expect(result.selectedPresetId).toBe('region-default');
    expect(result.isCustomOverride).toBe(false);
    expect(result.activeHour).toBeNull();
  });
});

describe('WindlightPresetManager state management', () => {
  it('manages state changes and notifies subscribers', async () => {
    const manager = new WindlightPresetManager('region-default');
    expect(manager.getState().selectedPresetId).toBe('region-default');

    const listener = vi.fn();
    const unsubscribe = manager.subscribe(listener);

    await manager.selectPreset('A-12PM');
    expect(listener).toHaveBeenCalled();
    expect(manager.getState().selectedPresetId).toBe('A-12PM');
    expect(manager.getState().activeHour).toBe(0.5);

    unsubscribe();
  });

  it('supports custom sun hour adjustment and reset to region default', async () => {
    const manager = new WindlightPresetManager('A-12PM');
    await manager.setCustomSunHour(0.75);
    expect(manager.getState().activeHour).toBe(0.75);

    await manager.resetToRegionDefault();
    expect(manager.getState().selectedPresetId).toBe('region-default');
    expect(manager.getState().isCustomOverride).toBe(false);
  });

  it('clears error state when clearError is called', async () => {
    const manager = new WindlightPresetManager('region-default');
    const protocol: ViewerServiceProtocol = { connected: false };
    await manager.selectPreset('A-6AM', protocol);
    expect(manager.getState().error).not.toBeNull();

    manager.clearError();
    expect(manager.getState().error).toBeNull();
  });
});

describe('EnvironmentPresetSelector UI component', () => {
  it('renders preset selector controls and accessibility attributes', async () => {
    const { host, cleanup } = await renderComponent(
      createElement(EnvironmentPresetSelector, { currentPresetId: 'region-default' })
    );

    const group = host.querySelector('.environment-preset-selector');
    expect(group).not.toBeNull();
    expect(group?.getAttribute('aria-label')).toBe('Environment Preset Selector');
    expect(group?.getAttribute('role')).toBe('group');

    const select = host.querySelector('#windlight-preset-select') as HTMLSelectElement;
    expect(select).not.toBeNull();
    expect(select.value).toBe('region-default');

    const radioGroup = host.querySelector('[role="radiogroup"]');
    expect(radioGroup).not.toBeNull();

    const buttons = host.querySelectorAll('button[role="radio"]');
    expect(buttons.length).toBe(9);

    await cleanup();
  });

  it('handles user preset selection and triggers onPresetChange callback', async () => {
    const onPresetChange = vi.fn();
    const { host, cleanup } = await renderComponent(
      createElement(EnvironmentPresetSelector, {
        currentPresetId: 'region-default',
        onPresetChange,
      })
    );

    const noonButton = Array.from(host.querySelectorAll('button[role="radio"]')).find(
      (b) => b.textContent?.includes('Noon')
    ) as HTMLButtonElement;

    expect(noonButton).not.toBeUndefined();

    await act(async () => {
      noonButton.click();
    });

    expect(onPresetChange).toHaveBeenCalledWith('A-12PM', expect.objectContaining({
      selectedPresetId: 'A-12PM',
      activeHour: 0.5,
    }));

    await cleanup();
  });

  it('displays error alert when disconnected and allows user dismissal', async () => {
    const protocol: ViewerServiceProtocol = { connected: false };
    const { host, cleanup } = await renderComponent(
      createElement(EnvironmentPresetSelector, {
        protocol,
        currentPresetId: 'A-6AM',
      })
    );

    const alert = host.querySelector('[role="alert"]');
    expect(alert).not.toBeNull();
    expect(alert?.textContent).toContain('Disconnected from simulator');

    const dismissBtn = host.querySelector('.environment-preset-dismiss-btn') as HTMLButtonElement;
    expect(dismissBtn).not.toBeNull();

    await act(async () => {
      dismissBtn.click();
    });

    expect(host.querySelector('[role="alert"]')).toBeNull();

    await cleanup();
  });

  it('renders sun hour slider when custom preset is selected and handles slider change', async () => {
    const onPresetChange = vi.fn();
    const { host, cleanup } = await renderComponent(
      createElement(EnvironmentPresetSelector, {
        currentPresetId: 'A-12PM',
        onPresetChange,
        showHourSlider: true,
      })
    );

    const slider = host.querySelector('#windlight-hour-slider') as HTMLInputElement;
    expect(slider).not.toBeNull();
    expect(slider.value).toBe('0.5');

    await act(async () => {
      const nativeSetter = Object.getOwnPropertyDescriptor(window.HTMLInputElement.prototype, 'value')?.set;
      nativeSetter?.call(slider, '0.75');
      slider.dispatchEvent(new Event('input', { bubbles: true }));
      slider.dispatchEvent(new Event('change', { bubbles: true }));
    });

    expect(onPresetChange).toHaveBeenCalledWith('A-12PM', expect.objectContaining({
      activeHour: 0.75,
    }));

    await cleanup();
  });

  it('exports WindlightPresetSelector as an alias', () => {
    expect(WindlightPresetSelector).toBe(EnvironmentPresetSelector);
  });
});

