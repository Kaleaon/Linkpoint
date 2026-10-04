import React, { createContext, useContext, useMemo, useState } from "react";
import {
  DEVICES,
  LAYOUTS,
  PALETTES,
  computeThemeTokens,
  type BreakpointPreset,
  type DensityMode,
  type DeviceKey,
  type DevicePack,
  type LayoutKey,
  type LayoutLook,
  type LayoutMode,
  type NavMode,
  type PaletteKey,
  type ThemeTokens
} from "../tokens/index.js";
import { generateCssVariables } from "../css/index.js";

export interface LayoutContextState {
  layout: LayoutKey;
  palette: PaletteKey;
  device: DeviceKey;
  dense: boolean;
  density: DensityMode;
  layoutMode: LayoutMode;
  breakpoint: BreakpointPreset;
  largeType: boolean;
}

export interface LayoutTheme {
  layout: LayoutKey;
  palette: PaletteKey;
  device: DeviceKey;
  name: string;
  font: string;
  dfont: string;
  look: LayoutLook;
  v: ThemeTokens;
  light: boolean;
  pad: string;
  nav: NavMode;
  d: DevicePack;
  density: DensityMode;
  layoutMode: LayoutMode;
  breakpoint: BreakpointPreset;
}

export interface LayoutContextValue extends LayoutContextState {
  theme: LayoutTheme;
  setLayout: (key: LayoutKey) => void;
  setPalette: (key: PaletteKey) => void;
  setDevice: (key: DeviceKey) => void;
  setDense: (dense: boolean) => void;
  setDensity: (density: DensityMode) => void;
  setLayoutMode: (mode: LayoutMode) => void;
  setBreakpoint: (bp: BreakpointPreset) => void;
  setLargeType: (largeType: boolean) => void;
  cssVars: React.CSSProperties;
}

export function resolveNav(layout: LayoutKey, device: DeviceKey): NavMode {
  const L = LAYOUTS[layout] || LAYOUTS.terminal;
  const d = DEVICES[device] || DEVICES.ios;
  if (d.desk) return "floaters";
  if (L.nav === "SWEEP") return "sweep";
  if (d.split) return "rail";
  if (L.nav === "TILES") return "tiles";
  if (L.nav === "RAIL" && d.w > 700) return "rail";
  return "tabs";
}

const LayoutContext = createContext<LayoutContextValue | null>(null);

export interface LayoutProviderProps {
  initialLayout?: LayoutKey;
  initialPalette?: PaletteKey;
  initialDevice?: DeviceKey;
  initialDense?: boolean;
  initialDensity?: DensityMode;
  initialLayoutMode?: LayoutMode;
  initialBreakpoint?: BreakpointPreset;
  initialLargeType?: boolean;
  children: React.ReactNode;
}

export const LayoutProvider: React.FC<LayoutProviderProps> = ({
  initialLayout = "terminal",
  initialPalette = "ink",
  initialDevice = "ios",
  initialDense = false,
  initialDensity = "standard",
  initialLayoutMode = "grid",
  initialBreakpoint = "desktop",
  initialLargeType = false,
  children
}) => {
  const [layout, setLayout] = useState<LayoutKey>(initialLayout);
  const [palette, setPalette] = useState<PaletteKey>(initialPalette);
  const [device, setDevice] = useState<DeviceKey>(initialDevice);
  const [dense, setDenseState] = useState<boolean>(initialDense);
  const [density, setDensity] = useState<DensityMode>(initialDensity || (initialDense ? "compact" : "standard"));
  const [layoutMode, setLayoutMode] = useState<LayoutMode>(initialLayoutMode);
  const [breakpoint, setBreakpoint] = useState<BreakpointPreset>(initialBreakpoint);
  const [largeType, setLargeType] = useState<boolean>(initialLargeType);

  const setDense = (d: boolean) => {
    setDenseState(d);
    setDensity(d ? "compact" : "standard");
  };

  const theme = useMemo<LayoutTheme>(() => {
    const L = LAYOUTS[layout] || LAYOUTS.terminal;
    const P = PALETTES[palette] || PALETTES.ink;
    const d = DEVICES[device] || DEVICES.ios;
    const v = computeThemeTokens(layout, palette, { dense, density, layoutMode });
    const pad = density === "compact" ? "6px" : density === "comfortable" ? "18px" : L.s.pad;
    const nav = resolveNav(layout, device);

    return {
      layout,
      palette,
      device,
      name: `${L.name} / ${P.name}`,
      font: L.font,
      dfont: L.dfont,
      look: L.look,
      v,
      light: P.light,
      pad,
      nav,
      d,
      density,
      layoutMode,
      breakpoint
    };
  }, [layout, palette, device, dense, density, layoutMode, breakpoint]);

  const cssVars = useMemo<React.CSSProperties>(() => {
    const raw = generateCssVariables(theme.v);
    const vars: Record<string, string> = { ...raw };
    vars["--font"] = theme.font;
    vars["--dfont"] = theme.dfont;
    vars["--pad"] = theme.pad;
    vars["--body"] = largeType ? "18px" : "13px";
    vars["--layout-mode"] = layoutMode;
    vars["--density"] = density;
    vars["--breakpoint"] = breakpoint;
    return vars as React.CSSProperties;
  }, [theme, largeType, layoutMode, density, breakpoint]);

  const value = useMemo<LayoutContextValue>(
    () => ({
      layout,
      palette,
      device,
      dense,
      density,
      layoutMode,
      breakpoint,
      largeType,
      theme,
      setLayout,
      setPalette,
      setDevice,
      setDense,
      setDensity,
      setLayoutMode,
      setBreakpoint,
      setLargeType,
      cssVars
    }),
    [layout, palette, device, dense, density, layoutMode, breakpoint, largeType, theme, cssVars]
  );

  return <LayoutContext.Provider value={value}>{children}</LayoutContext.Provider>;
};

export function useThemeContext(): LayoutContextValue | null {
  return useContext(LayoutContext);
}

export function useTheme(): LayoutTheme {
  const ctx = useThemeContext();
  if (ctx) return ctx.theme;
  const v = computeThemeTokens("terminal", "ink");
  const L = LAYOUTS.terminal;
  const P = PALETTES.ink;
  const d = DEVICES.ios;
  return {
    layout: "terminal",
    palette: "ink",
    device: "ios",
    name: `${L.name} / ${P.name}`,
    font: L.font,
    dfont: L.dfont,
    look: L.look,
    v,
    light: P.light,
    pad: L.s.pad,
    nav: "tabs",
    d
  };
}

/** Hook returning theme token set (`V`). */
export function useThemeTokens(): ThemeTokens {
  return useTheme().v;
}

/** Hook returning resolved navigation layout mode (`nav`). */
export function useLayoutMode(): NavMode {
  return useTheme().nav;
}
