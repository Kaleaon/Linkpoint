// @vitest-environment jsdom
import { describe, expect, it } from 'vitest';
import { readFileSync } from 'node:fs';
import { resolve } from 'node:path';
import {
  WindlightDay, getDefaultWindlightDay, parseWindlightPreset, presetToSkyFrame, sunDirection,
  estimatedSunHour, windlightEnvironment, WINDLIGHT_HOUR_TABLE, SL_DAY_SECONDS,
} from '../windlight';
import { computeSkyUniforms } from '../sky';

const xml = (name: string) => readFileSync(resolve(process.cwd(), 'src/assets/windlight', `${name}.xml`), 'utf8');

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
