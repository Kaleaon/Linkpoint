import React from "react";
import { useThemeContext } from "./LayoutContext.js";
import type { BreakpointPreset, DensityMode, LayoutMode } from "../tokens/index.js";

export interface LayoutControlToolbarProps {
  layoutMode?: LayoutMode;
  density?: DensityMode;
  breakpoint?: BreakpointPreset;
  onLayoutModeChange?: (mode: LayoutMode) => void;
  onDensityChange?: (density: DensityMode) => void;
  onBreakpointChange?: (breakpoint: BreakpointPreset) => void;
  className?: string;
  style?: React.CSSProperties;
}

const LAYOUT_MODES: { key: LayoutMode; label: string; icon: string }[] = [
  { key: "grid", label: "Grid", icon: "⊞" },
  { key: "list", label: "List", icon: "☰" },
  { key: "rail", label: "Rail", icon: "❚" },
  { key: "split", label: "Split", icon: "◧" }
];

const DENSITY_OPTIONS: { key: DensityMode; label: string; icon: string }[] = [
  { key: "compact", label: "Compact", icon: "⇥" },
  { key: "standard", label: "Standard", icon: "↔" },
  { key: "comfortable", label: "Comfortable", icon: "⇄" }
];

const BREAKPOINT_PRESETS: { key: BreakpointPreset; label: string; width: string; icon: string }[] = [
  { key: "mobile", label: "Mobile", width: "375px", icon: "📱" },
  { key: "tablet", label: "Tablet", width: "768px", icon: "📲" },
  { key: "desktop", label: "Desktop", width: "100%", icon: "💻" }
];

export const LayoutControlToolbar: React.FC<LayoutControlToolbarProps> = ({
  layoutMode: propMode,
  density: propDensity,
  breakpoint: propBp,
  onLayoutModeChange,
  onDensityChange,
  onBreakpointChange,
  className = "",
  style = {}
}) => {
  const ctx = useThemeContext();

  const activeMode = propMode ?? ctx?.layoutMode ?? "grid";
  const activeDensity = propDensity ?? ctx?.density ?? "standard";
  const activeBp = propBp ?? ctx?.breakpoint ?? "desktop";

  const handleMode = (mode: LayoutMode) => {
    if (onLayoutModeChange) onLayoutModeChange(mode);
    else if (ctx?.setLayoutMode) ctx.setLayoutMode(mode);
  };

  const handleDensity = (den: DensityMode) => {
    if (onDensityChange) onDensityChange(den);
    else if (ctx?.setDensity) ctx.setDensity(den);
  };

  const handleBreakpoint = (bp: BreakpointPreset) => {
    if (onBreakpointChange) onBreakpointChange(bp);
    else if (ctx?.setBreakpoint) ctx.setBreakpoint(bp);
  };

  const barStyle: React.CSSProperties = {
    display: "flex",
    flexWrap: "wrap",
    alignItems: "center",
    gap: "12px",
    padding: "8px 12px",
    borderRadius: "8px",
    background: "rgba(0, 0, 0, 0.08)",
    border: "1px solid rgba(255, 255, 255, 0.12)",
    fontSize: "12px",
    fontFamily: "var(--font, system-ui, sans-serif)",
    color: "inherit",
    userSelect: "none",
    ...style
  };

  const groupStyle: React.CSSProperties = {
    display: "flex",
    alignItems: "center",
    gap: "4px",
    background: "rgba(0, 0, 0, 0.15)",
    padding: "2px",
    borderRadius: "6px"
  };

  const btnStyle = (active: boolean): React.CSSProperties => ({
    display: "inline-flex",
    alignItems: "center",
    gap: "4px",
    padding: "4px 8px",
    borderRadius: "4px",
    border: "none",
    fontSize: "11px",
    fontWeight: active ? 600 : 400,
    background: active ? "rgba(255, 255, 255, 0.22)" : "transparent",
    color: active ? "var(--pv-primary, inherit)" : "inherit",
    cursor: "pointer",
    transition: "background 0.12s, color 0.12s"
  });

  return (
    <div className={`layout-control-toolbar ${className}`} style={barStyle} aria-label="Layout Control Toolbar">
      <div className="toolbar-group" style={{ display: "flex", alignItems: "center", gap: "6px" }}>
        <span style={{ fontSize: "10px", textTransform: "uppercase", opacity: 0.65, fontWeight: 600 }}>Mode</span>
        <div style={groupStyle} role="radiogroup" aria-label="Layout Mode">
          {LAYOUT_MODES.map((m) => (
            <button
              key={m.key}
              type="button"
              role="radio"
              aria-checked={activeMode === m.key}
              style={btnStyle(activeMode === m.key)}
              onClick={() => handleMode(m.key)}
              title={`Switch layout to ${m.label}`}
            >
              <span aria-hidden="true">{m.icon}</span>
              <span>{m.label}</span>
            </button>
          ))}
        </div>
      </div>

      <div className="toolbar-group" style={{ display: "flex", alignItems: "center", gap: "6px" }}>
        <span style={{ fontSize: "10px", textTransform: "uppercase", opacity: 0.65, fontWeight: 600 }}>Density</span>
        <div style={groupStyle} role="radiogroup" aria-label="Density Level">
          {DENSITY_OPTIONS.map((d) => (
            <button
              key={d.key}
              type="button"
              role="radio"
              aria-checked={activeDensity === d.key}
              style={btnStyle(activeDensity === d.key)}
              onClick={() => handleDensity(d.key)}
              title={`Switch density to ${d.label}`}
            >
              <span aria-hidden="true">{d.icon}</span>
              <span>{d.label}</span>
            </button>
          ))}
        </div>
      </div>

      <div className="toolbar-group" style={{ display: "flex", alignItems: "center", gap: "6px" }}>
        <span style={{ fontSize: "10px", textTransform: "uppercase", opacity: 0.65, fontWeight: 600 }}>Viewport</span>
        <div style={groupStyle} role="radiogroup" aria-label="Breakpoint Presets">
          {BREAKPOINT_PRESETS.map((bp) => (
            <button
              key={bp.key}
              type="button"
              role="radio"
              aria-checked={activeBp === bp.key}
              style={btnStyle(activeBp === bp.key)}
              onClick={() => handleBreakpoint(bp.key)}
              title={`Set preview width to ${bp.label} (${bp.width})`}
            >
              <span aria-hidden="true">{bp.icon}</span>
              <span>{bp.label}</span>
            </button>
          ))}
        </div>
      </div>
    </div>
  );
};
