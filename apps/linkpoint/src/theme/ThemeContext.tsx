import React, { createContext, useCallback, useContext, useEffect, useMemo, useState } from 'react';
import {
  DEVICES,
  LAYOUTS,
  PALETTES,
  type DeviceKey,
  type LayoutKey,
  type LayoutLook,
  type NavMode,
  type PaletteColors,
  type PaletteKey,
} from './tokens';
import { inkOn } from './contrast';

const STORAGE_KEY = 'linkpoint_theme';

/** Everything a screen needs to paint itself in the current skin. */
export interface Theme {
  layout: LayoutKey;
  palette: PaletteKey;
  device: DeviceKey;
  /** "Ink Terminal / LCARS Amber" — the combined skin name. */
  name: string;
  font: string;
  dfont: string;
  look: LayoutLook;
  /** Colour roles merged with the layout's shape scale. */
  v: PaletteColors & { rs: string; rl: string; rp: string; navr: string; pad: string; tls: string };
  /** True for light-first packs, which need different scrim and shadow handling. */
  light: boolean;
  /** Row padding after compact density has been applied. */
  pad: string;
  /** Resolved navigation model for the current layout and form factor. */
  nav: NavMode;
  /** Pick the most legible ink for a background out of this palette. */
  ink: (background: string, extra?: (string | undefined)[]) => string;
}

export interface ThemeState {
  layout: LayoutKey;
  palette: PaletteKey;
  device: DeviceKey;
  /** Compact density packs rows to Lumiya-style tightness. */
  dense: boolean;
  /** Large type and forced AA contrast, for sunlight and accessibility. */
  largeType: boolean;
}

interface ThemeContextValue extends ThemeState {
  theme: Theme;
  setLayout: (key: LayoutKey) => void;
  setPalette: (key: PaletteKey) => void;
  setDevice: (key: DeviceKey) => void;
  setDense: (on: boolean) => void;
  setLargeType: (on: boolean) => void;
  /** CSS custom properties for the current skin, to spread onto a wrapper. */
  cssVars: React.CSSProperties;
}

const DEFAULTS: ThemeState = {
  layout: 'terminal',
  palette: 'ink',
  device: 'ios',
  dense: false,
  largeType: false,
};

const ThemeContext = createContext<ThemeContextValue | null>(null);

function readStored(): ThemeState {
  try {
    const raw = localStorage.getItem(STORAGE_KEY);
    if (!raw) return DEFAULTS;
    const parsed = JSON.parse(raw) as Partial<ThemeState>;
    return {
      layout: parsed.layout && LAYOUTS[parsed.layout] ? parsed.layout : DEFAULTS.layout,
      palette: parsed.palette && PALETTES[parsed.palette] ? parsed.palette : DEFAULTS.palette,
      device: parsed.device && DEVICES[parsed.device] ? parsed.device : DEFAULTS.device,
      dense: !!parsed.dense,
      largeType: !!parsed.largeType,
    };
  } catch {
    return DEFAULTS;
  }
}

/**
 * Resolve the navigation model. A layout pack states a preference, but the
 * form factor overrides it: Sweep keeps its elbow rail at every width, split
 * devices always get a rail, and Rule & Rail only earns its rail above 700dp.
 */
export function resolveNav(layout: LayoutKey, device: DeviceKey): NavMode {
  const wanted = LAYOUTS[layout].nav;
  const d = DEVICES[device];
  if (wanted === 'SWEEP') return 'sweep';
  if (d.split) return 'rail';
  if (wanted === 'TILES') return 'tiles';
  if (wanted === 'RAIL' && d.w > 700) return 'rail';
  return 'tabs';
}

export const ThemeProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [state, setState] = useState<ThemeState>(readStored);

  useEffect(() => {
    try {
      localStorage.setItem(STORAGE_KEY, JSON.stringify(state));
    } catch {
      /* Private mode or blocked storage: the skin simply does not persist. */
    }
  }, [state]);

  const theme = useMemo<Theme>(() => {
    const L = LAYOUTS[state.layout];
    const P = PALETTES[state.palette];
    const pad = state.dense ? '8px' : L.s.pad;
    const v = { ...P.c, ...L.s };
    return {
      layout: state.layout,
      palette: state.palette,
      device: state.device,
      name: `${L.name} / ${P.name}`,
      font: L.font,
      dfont: L.dfont,
      look: L.look,
      v,
      light: P.light,
      pad,
      nav: resolveNav(state.layout, state.device),
      ink: (background, extra = []) => inkOn(background, [...extra, v.ink, v.onpri, v.ink2, v.bg]),
    };
  }, [state.layout, state.palette, state.device, state.dense]);

  const cssVars = useMemo<React.CSSProperties>(() => {
    const vars: Record<string, string> = {};
    for (const [k, val] of Object.entries(theme.v)) vars[`--${k}`] = String(val);
    vars['--font'] = theme.font;
    vars['--dfont'] = theme.dfont;
    vars['--pad'] = theme.pad;
    vars['--body'] = state.largeType ? '18px' : '13px';
    return vars as React.CSSProperties;
  }, [theme, state.largeType]);

  const setLayout = useCallback((layout: LayoutKey) => setState((s) => ({ ...s, layout })), []);
  const setPalette = useCallback((palette: PaletteKey) => setState((s) => ({ ...s, palette })), []);
  const setDevice = useCallback((device: DeviceKey) => setState((s) => ({ ...s, device })), []);
  const setDense = useCallback((dense: boolean) => setState((s) => ({ ...s, dense })), []);
  const setLargeType = useCallback((largeType: boolean) => setState((s) => ({ ...s, largeType })), []);

  const value = useMemo<ThemeContextValue>(
    () => ({ ...state, theme, cssVars, setLayout, setPalette, setDevice, setDense, setLargeType }),
    [state, theme, cssVars, setLayout, setPalette, setDevice, setDense, setLargeType],
  );

  return <ThemeContext.Provider value={value}>{children}</ThemeContext.Provider>;
};

export function useThemeContext(): ThemeContextValue {
  const ctx = useContext(ThemeContext);
  if (!ctx) throw new Error('useThemeContext must be used inside a ThemeProvider');
  return ctx;
}

/** The common case: a screen that only needs the resolved skin. */
export function useTheme(): Theme {
  return useThemeContext().theme;
}
