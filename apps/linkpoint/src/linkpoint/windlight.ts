/**
 * Windlight day cycle: the eight default sky presets Lumiya ships (text LLSD
 * XML, copied from its assets) plus the same loading and interpolation rules.
 *
 * Rules checked against Lumiya's WindlightPreset / WindlightDay:
 *  - ambient and sunlight are divided by 3, blue density/horizon by 2, haze
 *    density/horizon by 5, cloud position/density by 3;
 *  - ambient and sunlight are then gamma-encoded (pow 1/2.2) and scaled by 1.25;
 *  - the day is eight presets at 3-hour steps (hour fractions 0, 1/8 ... 7/8),
 *    linearly interpolated, with the last preset wrapping to the first.
 * Sun elevation/azimuth are derived from `sun_angle` and `east_angle`; Lumiya
 * uses the preset's own `lightnorm` vector instead, so that is kept too.
 *
 * These presets are only a fallback for when the simulator has not sent its
 * own environment. They are not a live reading of the region's sky.
 */

import React, { useState, useEffect, useCallback, useMemo } from 'react';
import { LLSD } from './llsd';
import a12am from '../assets/windlight/A-12AM.xml?raw';
import a3am from '../assets/windlight/A-3AM.xml?raw';
import a6am from '../assets/windlight/A-6AM.xml?raw';
import a9am from '../assets/windlight/A-9AM.xml?raw';
import a12pm from '../assets/windlight/A-12PM.xml?raw';
import a3pm from '../assets/windlight/A-3PM.xml?raw';
import a6pm from '../assets/windlight/A-6PM.xml?raw';
import a9pm from '../assets/windlight/A-9PM.xml?raw';

export type Vec4 = [number, number, number, number];

export const WINDLIGHT_GAMMA = 2.2;
/** Hour-of-day fractions of the eight default presets (midnight, 3 AM ... 9 PM). */
export const WINDLIGHT_HOUR_TABLE = [0, 0.125, 0.25, 0.375, 0.5, 0.625, 0.75, 0.875] as const;
/** A Second Life region day lasts four real hours. */
export const SL_DAY_SECONDS = 4 * 60 * 60;

export interface WindlightPreset {
  ambient: Vec4;
  sunlightColor: Vec4;
  lightnorm: Vec4;
  blueDensity: Vec4;
  blueHorizon: Vec4;
  hazeDensity: Vec4;
  hazeHorizon: Vec4;
  cloudColor: Vec4;
  cloudPosDensity1: Vec4;
  cloudPosDensity2: Vec4;
  cloudShadow: Vec4;
  starBrightness: number;
  sunAngle: number;
  eastAngle: number;
}

/** Sky frame in the shape `Scene3D.setEnvironment` and `computeSkyUniforms` already accept. */
export interface WindlightSkyFrame {
  blueHorizon: Vec4;
  blueDensity: Vec4;
  sunlightColor: Vec4;
  ambient: Vec4;
  hazeDensity: number;
  hazeHorizon: number;
  starBrightness: number;
  /** Unit vector towards the sun in Z-up world space. */
  sunDirection: [number, number, number];
  cloudColor: Vec4;
}

function vec4(source: any, key: string, divisor: number): Vec4 {
  const values = source?.[key];
  if (!Array.isArray(values) || values.length < 4) throw new Error(`Windlight preset is missing "${key}"`);
  const out = values.slice(0, 4).map((value) => Number(value) / divisor);
  if (!out.every(Number.isFinite)) throw new Error(`Windlight preset has a non-numeric "${key}"`);
  return out as Vec4;
}

function scalar(source: any, key: string): number {
  const value = Number(source?.[key]);
  if (!Number.isFinite(value)) throw new Error(`Windlight preset is missing "${key}"`);
  return value;
}

const gamma = (v: Vec4): Vec4 => v.map((x) => Math.pow(x, 1 / WINDLIGHT_GAMMA) * 1.25) as Vec4;

/** Parse one preset from LLSD XML, applying Lumiya's scale factors and gamma. Throws if a field is missing. */
export function parseWindlightPreset(xml: string): WindlightPreset {
  const source = LLSD.parseXML(xml);
  return {
    ambient: gamma(vec4(source, 'ambient', 3)),
    sunlightColor: gamma(vec4(source, 'sunlight_color', 3)),
    lightnorm: vec4(source, 'lightnorm', 1),
    blueDensity: vec4(source, 'blue_density', 2),
    blueHorizon: vec4(source, 'blue_horizon', 2),
    hazeDensity: vec4(source, 'haze_density', 5),
    hazeHorizon: vec4(source, 'haze_horizon', 5),
    cloudColor: vec4(source, 'cloud_color', 1),
    cloudPosDensity1: vec4(source, 'cloud_pos_density1', 3),
    cloudPosDensity2: vec4(source, 'cloud_pos_density2', 3),
    cloudShadow: vec4(source, 'cloud_shadow', 1),
    starBrightness: scalar(source, 'star_brightness'),
    sunAngle: scalar(source, 'sun_angle'),
    eastAngle: scalar(source, 'east_angle'),
  };
}

const lerp = (a: number, b: number, t: number) => a * (1 - t) + b * t;
const lerpVec = (a: Vec4, b: Vec4, t: number): Vec4 => a.map((x, i) => lerp(x, b[i], t)) as Vec4;

/** Interpolate along a circular angle the short way forward (a → b, wrapping once if b is behind a). */
function lerpAngleForward(a: number, b: number, t: number) {
  const end = b < a ? b + Math.PI * 2 : b;
  return lerp(a, end, t) % (Math.PI * 2);
}

export function interpolateWindlight(a: WindlightPreset, b: WindlightPreset, t: number): WindlightPreset {
  return {
    ambient: lerpVec(a.ambient, b.ambient, t),
    sunlightColor: lerpVec(a.sunlightColor, b.sunlightColor, t),
    lightnorm: lerpVec(a.lightnorm, b.lightnorm, t),
    blueDensity: lerpVec(a.blueDensity, b.blueDensity, t),
    blueHorizon: lerpVec(a.blueHorizon, b.blueHorizon, t),
    hazeDensity: lerpVec(a.hazeDensity, b.hazeDensity, t),
    hazeHorizon: lerpVec(a.hazeHorizon, b.hazeHorizon, t),
    cloudColor: lerpVec(a.cloudColor, b.cloudColor, t),
    cloudPosDensity1: lerpVec(a.cloudPosDensity1, b.cloudPosDensity1, t),
    cloudPosDensity2: lerpVec(a.cloudPosDensity2, b.cloudPosDensity2, t),
    cloudShadow: lerpVec(a.cloudShadow, b.cloudShadow, t),
    starBrightness: lerp(a.starBrightness, b.starBrightness, t),
    sunAngle: lerpAngleForward(a.sunAngle, b.sunAngle, t),
    eastAngle: lerp(a.eastAngle, b.eastAngle, t),
  };
}

/** A day of eight presets. */
export class WindlightDay {
  constructor(readonly presets: WindlightPreset[]) {
    if (presets.length !== WINDLIGHT_HOUR_TABLE.length) throw new Error(`A Windlight day needs ${WINDLIGHT_HOUR_TABLE.length} presets`);
  }

  /** Sky at `hour`, a fraction of the day in [0, 1) where 0 is midnight and 0.5 noon. */
  at(hour: number): WindlightPreset {
    const f = ((hour % 1) + 1) % 1;
    let i = 0;
    for (let k = WINDLIGHT_HOUR_TABLE.length - 1; k >= 0; k--) {
      if (f >= WINDLIGHT_HOUR_TABLE[k]) { i = k; break; }
    }
    const next = (i + 1) % WINDLIGHT_HOUR_TABLE.length;
    const start = WINDLIGHT_HOUR_TABLE[i];
    let end = WINDLIGHT_HOUR_TABLE[next];
    if (end <= start) end += 1;
    return interpolateWindlight(this.presets[i], this.presets[next], (f - start) / (end - start));
  }
}

let defaultDay: WindlightDay | null = null;

/** The eight bundled presets, parsed once. */
export function getDefaultWindlightDay(): WindlightDay {
  if (!defaultDay) {
    defaultDay = new WindlightDay([a12am, a3am, a6am, a9am, a12pm, a3pm, a6pm, a9pm].map(parseWindlightPreset));
  }
  return defaultDay;
}

/**
 * Sun direction in Z-up world space. At `sun_angle` 0 the sun is on the eastern
 * horizon, at π/2 overhead; `east_angle` rotates the compass.
 */
export function sunDirection(sunAngle: number, eastAngle = 0): [number, number, number] {
  const horizontal = Math.cos(sunAngle);
  return [horizontal * Math.cos(eastAngle), horizontal * Math.sin(eastAngle), Math.sin(sunAngle)];
}

export function presetToSkyFrame(preset: WindlightPreset): WindlightSkyFrame {
  return {
    blueHorizon: preset.blueHorizon,
    blueDensity: preset.blueDensity,
    sunlightColor: preset.sunlightColor,
    ambient: preset.ambient,
    hazeDensity: preset.hazeDensity[0],
    hazeHorizon: preset.hazeHorizon[0],
    starBrightness: preset.starBrightness,
    sunDirection: sunDirection(preset.sunAngle, preset.eastAngle),
    cloudColor: preset.cloudColor,
  };
}

/**
 * Convert simulator SunPhase telemetry (in radians) into a normalized sun hour fraction in [0, 1).
 * SunPhase is zero at sunrise (0.25 in sun hour fraction / 6 AM), π/2 at noon (0.5), π at sunset (0.75),
 * and 1.5π at midnight (0.0).
 */
export function sunPhaseToSunHour(sunPhase: number): number {
  if (typeof sunPhase !== 'number' || !Number.isFinite(sunPhase)) return 0;
  const raw = ((sunPhase / (2 * Math.PI)) + 0.25) % 1.0;
  return ((raw % 1.0) + 1.0) % 1.0;
}

/**
 * Time of day as a fraction of a four-hour Second Life day, from a Unix time in
 * milliseconds. This is an estimate: used as a fallback when simulator SunPhase
 * telemetry is not yet available or when disconnected.
 */
export function estimatedSunHour(nowMs: number): number {
  const seconds = nowMs / 1000;
  return (((seconds % SL_DAY_SECONDS) + SL_DAY_SECONDS) % SL_DAY_SECONDS) / SL_DAY_SECONDS;
}

/** Environment object for the scene built from the bundled day at `hour`. */
export function windlightEnvironment(hour: number) {
  return { currentSky: presetToSkyFrame(getDefaultWindlightDay().at(hour)), source: 'default-windlight' as const };
}

// --- Preset Option Types & Definition ---

export interface WindlightPresetOption {
  id: string;
  name: string;
  label: string;
  description: string;
  hour: number | null;
}

export const WINDLIGHT_PRESETS: WindlightPresetOption[] = [
  {
    id: 'region-default',
    name: 'Region Default',
    label: 'Region Default',
    description: 'Use simulator region environment and daylight cycle',
    hour: null,
  },
  {
    id: 'A-12AM',
    name: 'Midnight',
    label: 'Midnight (12 AM)',
    description: 'Deep night preset with dark sky and bright star field',
    hour: 0.0,
  },
  {
    id: 'A-3AM',
    name: 'Late Night',
    label: 'Late Night (3 AM)',
    description: 'Pre-dawn horizon with low ambient illumination',
    hour: 0.125,
  },
  {
    id: 'A-6AM',
    name: 'Sunrise',
    label: 'Sunrise (6 AM)',
    description: 'Warm morning dawn with eastern sun angle',
    hour: 0.25,
  },
  {
    id: 'A-9AM',
    name: 'Morning',
    label: 'Morning (9 AM)',
    description: 'Soft morning light with gentle horizon gradient',
    hour: 0.375,
  },
  {
    id: 'A-12PM',
    name: 'Noon',
    label: 'Noon (12 PM)',
    description: 'Bright midday sun overhead with intense skylight',
    hour: 0.5,
  },
  {
    id: 'A-3PM',
    name: 'Afternoon',
    label: 'Afternoon (3 PM)',
    description: 'Warm afternoon lighting with lengthening shadows',
    hour: 0.625,
  },
  {
    id: 'A-6PM',
    name: 'Sunset',
    label: 'Sunset (6 PM)',
    description: 'Golden hour sunset with vivid atmospheric haze',
    hour: 0.75,
  },
  {
    id: 'A-9PM',
    name: 'Evening',
    label: 'Evening (9 PM)',
    description: 'Twilight dusk transitioning into dark night sky',
    hour: 0.875,
  },
];

export function getWindlightPresetOption(id: string): WindlightPresetOption | undefined {
  if (!id || typeof id !== 'string') return undefined;
  return WINDLIGHT_PRESETS.find((p) => p.id.toLowerCase() === id.trim().toLowerCase());
}

// --- State Management Interfaces ---

export interface EnvironmentPresetState {
  selectedPresetId: string;
  isCustomOverride: boolean;
  activeHour: number | null;
  isConnected: boolean;
  isLoading: boolean;
  error: string | null;
  lastUpdated: number | null;
  preset: WindlightPreset | null;
  skyFrame: WindlightSkyFrame | null;
}

export interface ViewerServiceProtocol {
  connected?: boolean;
  send?: (messageType: string, payload?: any) => Promise<any> | void;
  dispatch?: (action: string, payload?: any) => Promise<any> | void;
  setEnvironment?: (presetId: string, hour?: number | null) => Promise<any> | void;
  on?: (event: string, listener: (...args: any[]) => void) => void;
  off?: (event: string, listener: (...args: any[]) => void) => void;
}

export interface PresetSelectOptions {
  hour?: number;
  silent?: boolean;
}

// --- Handler Functions & Dispatchers ---

/**
 * Connects UI preset selection to viewer service protocol methods,
 * updates local state, and handles network disconnections and errors gracefully.
 */
export async function selectEnvironmentPreset(
  presetId: string,
  protocol?: ViewerServiceProtocol,
  options?: PresetSelectOptions
): Promise<EnvironmentPresetState> {
  const normId = (!presetId || typeof presetId !== 'string') ? 'region-default' : presetId.trim();
  const option = getWindlightPresetOption(normId) || WINDLIGHT_PRESETS[0];

  const targetHour = options?.hour !== undefined && Number.isFinite(options.hour)
    ? ((options.hour % 1) + 1) % 1
    : option.hour;

  let isConnected = true;
  let error: string | null = null;
  let isLoading = false;

  if (protocol) {
    if (protocol.connected === false) {
      isConnected = false;
      error = 'Disconnected from simulator. Local environment preview applied.';
    } else {
      try {
        isLoading = true;
        if (typeof protocol.setEnvironment === 'function') {
          await protocol.setEnvironment(option.id, targetHour);
        } else if (typeof protocol.dispatch === 'function') {
          await protocol.dispatch('ENVIRONMENT_PRESET_CHANGE', { presetId: option.id, hour: targetHour });
        } else if (typeof protocol.send === 'function') {
          await protocol.send('set_environment', { presetId: option.id, hour: targetHour });
        }
      } catch (err: any) {
        error = err?.message || 'Failed to apply environment preset on server. Using local preview.';
      } finally {
        isLoading = false;
      }
    }
  }

  let preset: WindlightPreset | null = null;
  let skyFrame: WindlightSkyFrame | null = null;

  if (targetHour !== null) {
    preset = getDefaultWindlightDay().at(targetHour);
    skyFrame = presetToSkyFrame(preset);
  }

  return {
    selectedPresetId: option.id,
    isCustomOverride: option.id !== 'region-default',
    activeHour: targetHour,
    isConnected,
    isLoading,
    error,
    lastUpdated: Date.now(),
    preset,
    skyFrame,
  };
}

// --- Manager Class ---

export class WindlightPresetManager {
  private state: EnvironmentPresetState;
  private listeners: Set<(state: EnvironmentPresetState) => void> = new Set();

  constructor(initialPresetId = 'region-default', protocol?: ViewerServiceProtocol) {
    const option = getWindlightPresetOption(initialPresetId) || WINDLIGHT_PRESETS[0];
    const hour = option.hour;
    const preset = hour !== null ? getDefaultWindlightDay().at(hour) : null;
    const isConnected = protocol?.connected !== false;
    const error = protocol?.connected === false ? 'Disconnected from simulator. Local environment preview applied.' : null;
    this.state = {
      selectedPresetId: option.id,
      isCustomOverride: option.id !== 'region-default',
      activeHour: hour,
      isConnected,
      isLoading: false,
      error,
      lastUpdated: Date.now(),
      preset,
      skyFrame: preset ? presetToSkyFrame(preset) : null,
    };
  }

  public getState(): EnvironmentPresetState {
    return { ...this.state };
  }

  public subscribe(listener: (state: EnvironmentPresetState) => void): () => void {
    this.listeners.add(listener);
    return () => {
      this.listeners.delete(listener);
    };
  }

  private emit() {
    for (const listener of this.listeners) {
      listener(this.getState());
    }
  }

  public async selectPreset(
    presetId: string,
    protocol?: ViewerServiceProtocol,
    options?: PresetSelectOptions
  ): Promise<EnvironmentPresetState> {
    this.state = { ...this.state, isLoading: true };
    this.emit();

    const newState = await selectEnvironmentPreset(presetId, protocol, options);
    this.state = newState;
    this.emit();
    return this.state;
  }

  public async setCustomSunHour(
    hour: number,
    protocol?: ViewerServiceProtocol
  ): Promise<EnvironmentPresetState> {
    const validHour = Number.isFinite(hour) ? ((hour % 1) + 1) % 1 : 0.5;
    const activePreset = this.state.selectedPresetId === 'region-default' ? 'A-12PM' : this.state.selectedPresetId;
    return this.selectPreset(activePreset, protocol, { hour: validHour });
  }

  public async resetToRegionDefault(protocol?: ViewerServiceProtocol): Promise<EnvironmentPresetState> {
    return this.selectPreset('region-default', protocol);
  }

  public clearError() {
    if (this.state.error) {
      this.state = { ...this.state, error: null };
      this.emit();
    }
  }
}

// --- React Custom Hook ---

export function useWindlightPreset(protocol?: ViewerServiceProtocol, initialPresetId = 'region-default') {
  const manager = useMemo(() => new WindlightPresetManager(initialPresetId, protocol), [initialPresetId, protocol]);
  const [state, setState] = useState<EnvironmentPresetState>(() => manager.getState());

  useEffect(() => {
    setState(manager.getState());
    return manager.subscribe((newState) => setState(newState));
  }, [manager]);

  const selectPreset = useCallback(
    (presetId: string, options?: PresetSelectOptions) => manager.selectPreset(presetId, protocol, options),
    [manager, protocol]
  );

  const setCustomHour = useCallback(
    (hour: number) => manager.setCustomSunHour(hour, protocol),
    [manager, protocol]
  );

  const resetToRegionDefault = useCallback(
    () => manager.resetToRegionDefault(protocol),
    [manager, protocol]
  );

  const clearError = useCallback(() => manager.clearError(), [manager]);

  return {
    state,
    selectPreset,
    setCustomHour,
    resetToRegionDefault,
    clearError,
    presets: WINDLIGHT_PRESETS,
  };
}

// --- React UI Component ---

export interface EnvironmentPresetSelectorProps {
  protocol?: ViewerServiceProtocol;
  currentPresetId?: string;
  onPresetChange?: (presetId: string, state: EnvironmentPresetState) => void;
  className?: string;
  disabled?: boolean;
  showHourSlider?: boolean;
}

export const EnvironmentPresetSelector: React.FC<EnvironmentPresetSelectorProps> = ({
  protocol,
  currentPresetId = 'region-default',
  onPresetChange,
  className = '',
  disabled = false,
  showHourSlider = true,
}) => {
  const { state, selectPreset, setCustomHour, resetToRegionDefault, clearError, presets } = useWindlightPreset(
    protocol,
    currentPresetId
  );

  const handleSelect = async (presetId: string) => {
    if (disabled) return;
    const newState = await selectPreset(presetId);
    if (onPresetChange) {
      onPresetChange(presetId, newState);
    }
  };

  const handleSliderChange = async (e: React.ChangeEvent<HTMLInputElement>) => {
    if (disabled) return;
    const val = parseFloat(e.target.value);
    const newState = await setCustomHour(val);
    if (onPresetChange) {
      onPresetChange(state.selectedPresetId, newState);
    }
  };

  const currentOption = getWindlightPresetOption(state.selectedPresetId) || presets[0];

  return React.createElement(
    'div',
    {
      className: `environment-preset-selector ${className}`.trim(),
      'aria-label': 'Environment Preset Selector',
      role: 'group',
    },
    React.createElement(
      'div',
      { className: 'environment-preset-header' },
      React.createElement('h3', { className: 'environment-preset-title' }, 'Environment Presets'),
      state.isCustomOverride &&
        React.createElement(
          'button',
          {
            type: 'button',
            className: 'environment-preset-reset-btn',
            onClick: () => resetToRegionDefault(),
            disabled,
            'aria-label': 'Reset to region default environment',
          },
          'Reset to Region'
        )
    ),
    state.error &&
      React.createElement(
        'div',
        {
          className: 'environment-preset-alert environment-preset-error',
          role: 'alert',
          'aria-live': 'polite',
        },
        React.createElement('span', null, state.error),
        React.createElement(
          'button',
          {
            type: 'button',
            className: 'environment-preset-dismiss-btn',
            onClick: clearError,
            'aria-label': 'Dismiss alert',
          },
          '×'
        )
      ),
    React.createElement(
      'div',
      { className: 'environment-preset-controls' },
      React.createElement(
        'label',
        { htmlFor: 'windlight-preset-select', className: 'environment-preset-label' },
        'Active Sky & Day Cycle Preset'
      ),
      React.createElement(
        'select',
        {
          id: 'windlight-preset-select',
          className: 'environment-preset-dropdown',
          value: state.selectedPresetId,
          onChange: (e: React.ChangeEvent<HTMLSelectElement>) => handleSelect(e.target.value),
          disabled: disabled || state.isLoading,
          'aria-label': 'Select Environment Preset',
        },
        presets.map((p) =>
          React.createElement('option', { key: p.id, value: p.id }, p.label)
        )
      )
    ),
    React.createElement(
      'div',
      { className: 'environment-preset-grid', role: 'radiogroup', 'aria-label': 'Quick Preset Selection' },
      presets.map((p) => {
        const isActive = state.selectedPresetId === p.id;
        return React.createElement(
          'button',
          {
            key: p.id,
            type: 'button',
            role: 'radio',
            'aria-checked': isActive,
            'aria-label': p.label,
            className: `environment-preset-chip ${isActive ? 'active' : ''}`.trim(),
            onClick: () => handleSelect(p.id),
            disabled: disabled || state.isLoading,
          },
          p.name
        );
      })
    ),
    React.createElement(
      'div',
      { className: 'environment-preset-description' },
      React.createElement('p', null, currentOption.description)
    ),
    showHourSlider &&
      state.isCustomOverride &&
      state.activeHour !== null &&
      React.createElement(
        'div',
        { className: 'environment-preset-slider-container' },
        React.createElement(
          'label',
          { htmlFor: 'windlight-hour-slider', className: 'environment-preset-slider-label' },
          `Sun Hour: ${(state.activeHour * 24).toFixed(1)}h (${Math.round(state.activeHour * 100)}% of day cycle)`
        ),
        React.createElement('input', {
          id: 'windlight-hour-slider',
          type: 'range',
          min: '0',
          max: '0.999',
          step: '0.005',
          value: state.activeHour,
          onChange: handleSliderChange,
          disabled: disabled || state.isLoading,
          className: 'environment-preset-slider',
          'aria-label': 'Adjust Sun Hour',
        })
      )
  );
};

export const WindlightPresetSelector = EnvironmentPresetSelector;
