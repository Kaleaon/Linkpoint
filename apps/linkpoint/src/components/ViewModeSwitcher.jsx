import React from "react";
import { useApp } from "../context/AppContext.jsx";
import { useTheme } from "../context/ThemeContext.jsx";
import Icon from "./Icon.jsx";

/**
 * Universal Mobile vs Desktop View Mode Switcher
 * Allows 1-click toggling between:
 * - Mobile Mode (Lumiya Touch interface: full-screen, bottom tabs, virtual pad)
 * - Desktop Mode (Firestorm Multi-Window interface: top menu bar, draggable floaters, live status)
 * - Auto-Responsive Mode (adapts to screen width)
 */
export default function ViewModeSwitcher({ compact = false, style = {} }) {
  const { state, actions } = useApp();
  const { V, t, isSweepDesk } = useTheme();

  const currentMode = state.viewMode || "auto"; // "mobile" | "desktop" | "auto"
  const isDesktopActive = actions.navMode?.() === "floaters" || state.device === "desk";

  const handleSelectMode = (mode) => {
    actions.setViewMode(mode);
  };

  const borderRadius = isSweepDesk ? "999px" : V.rs;
  const font = t.font;

  if (compact) {
    return (
      <div style={{ display: "flex", alignItems: "center", gap: 3, ...style }}>
        <button
          type="button"
          onClick={() => handleSelectMode(isDesktopActive ? "mobile" : "desktop")}
          style={{
            display: "inline-flex",
            alignItems: "center",
            gap: 5,
            height: "24px",
            padding: "0 8px",
            border: `1px solid ${V.outv}`,
            borderRadius,
            background: V.surf,
            color: V.ink,
            font: "700 10px/1 " + font,
            letterSpacing: ".08em",
            cursor: "pointer",
          }}
          title={isDesktopActive ? "Switch to Mobile (Lumiya Touch)" : "Switch to Desktop (Firestorm Floaters)"}
        >
          <Icon name={isDesktopActive ? "smartphone" : "monitor"} size={12} />
          <span>{isDesktopActive ? "TO MOBILE" : "TO DESKTOP"}</span>
        </button>
      </div>
    );
  }

  const getOptionStyle = (active, isAuto = false) => ({
    display: "inline-flex",
    alignItems: "center",
    gap: isAuto ? 3 : 4,
    height: "22px",
    padding: isAuto ? "0 6px" : "0 7px",
    border: 0,
    borderRadius,
    background: active ? (isAuto ? (isSweepDesk ? V.sec : V.priC || V.surf2 || V.surf) : V.pri) : "transparent",
    color: active ? (isAuto ? V.ink : V.onpri) : V.ink2,
    font: `${isAuto ? 600 : 700} ${isAuto ? 9 : 9.5}px/1 ${font}`,
    letterSpacing: isAuto ? ".06em" : ".08em",
    cursor: "pointer",
    opacity: isAuto && !active ? 0.6 : 1,
    transition: "background 0.15s, color 0.15s",
  });

  const mobileActive = currentMode === "mobile" || (!isDesktopActive && currentMode !== "desktop");
  const desktopActive = currentMode === "desktop" || (isDesktopActive && currentMode !== "mobile");
  const autoActive = currentMode === "auto";

  return (
    <div
      role="group"
      aria-label="Viewer Interface Mode"
      style={{
        display: "inline-flex",
        alignItems: "center",
        padding: "2px",
        background: V.surf2 || V.surf,
        border: `1px solid ${V.outv}`,
        borderRadius,
        gap: "2px",
        ...style,
      }}
    >
      {/* Mobile Mode Button */}
      <button
        type="button"
        onClick={() => handleSelectMode("mobile")}
        style={getOptionStyle(mobileActive)}
        title="Mobile Touch Interface (Lumiya style with bottom navigation and touch controls)"
      >
        <Icon name="smartphone" size={11} />
        <span>MOBILE</span>
      </button>

      {/* Desktop Mode Button */}
      <button
        type="button"
        onClick={() => handleSelectMode("desktop")}
        style={getOptionStyle(desktopActive)}
        title="Desktop Multi-Window Interface (Firestorm style with draggable floaters and top menu bar)"
      >
        <Icon name="monitor" size={11} />
        <span>DESKTOP</span>
      </button>

      {/* Auto Button */}
      <button
        type="button"
        onClick={() => handleSelectMode("auto")}
        style={getOptionStyle(autoActive, true)}
        title="Auto-responsive (Switches layout automatically based on viewport size)"
      >
        <Icon name="sparkles" size={10} />
        <span>AUTO</span>
      </button>
    </div>
  );
}
