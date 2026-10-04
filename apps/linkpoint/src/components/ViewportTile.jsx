import React, { useState } from "react";
import { useTheme } from "../context/ThemeContext.jsx";
import Icon from "./Icon.jsx";

/**
 * ViewportTile container for feature screens/routes.
 * Supports dynamic docking, splitting, floating, resizing, and density controls.
 */
export default function ViewportTile({
  id,
  title,
  icon = "box",
  isDocked = true,
  isFloating = false,
  isMinimized = false,
  isMaximized = false,
  density = "normal",
  active = false,
  onFocus = () => {},
  onClose = null,
  onMinimize = null,
  onMaximize = null,
  onDock = null,
  onFloat = null,
  onSplitHorizontal = null,
  onSplitVertical = null,
  onToggleDensity = null,
  onDragStart = null,
  onResizeStart = null,
  onMoveStep = null,
  onResizeStep = null,
  children = null,
  style = {},
  className = "",
}) {
  const { V, t, ink } = useTheme();

  const densityPadding =
    density === "compact"
      ? "2px 4px"
      : density === "spacious"
      ? "12px 16px"
      : "6px 10px";

  const densityFontSize =
    density === "compact" ? "11px" : density === "spacious" ? "14px" : "12px";

  const headerBg = active ? V.pri : V.surf2;
  const headerFg = ink(headerBg, [V.bg, V.onpri, V.ink]);

  const tileHeaderStyle = {
    display: "flex",
    alignItems: "center",
    justify: "space-between",
    gap: "8px",
    padding: "6px 10px",
    background: headerBg,
    color: headerFg,
    font: `600 11px/1 ${t.dfont}`,
    letterSpacing: ".06em",
    cursor: isFloating ? "move" : "grab",
    userSelect: "none",
    borderBottom: `1px solid ${V.outv || "rgba(255,255,255,0.1)"}`,
    flexShrink: 0,
  };

  const actionBtnStyle = {
    display: "inline-flex",
    alignItems: "center",
    justifyContent: "center",
    width: "20px",
    height: "20px",
    background: "transparent",
    border: "none",
    color: headerFg,
    cursor: "pointer",
    borderRadius: "3px",
    opacity: 0.85,
    padding: 0,
  };

  const handleHeaderKeyDown = (e) => {
    const keys = ["ArrowUp", "ArrowDown", "ArrowLeft", "ArrowRight"];
    if (!keys.includes(e.key)) return;
    e.preventDefault();
    e.stopPropagation();
    const step = 32;
    if (e.shiftKey) {
      if (e.key === "ArrowUp") onResizeStep?.(0, -step);
      else if (e.key === "ArrowDown") onResizeStep?.(0, step);
      else if (e.key === "ArrowLeft") onResizeStep?.(-step, 0);
      else if (e.key === "ArrowRight") onResizeStep?.(step, 0);
    } else {
      if (e.key === "ArrowUp") onMoveStep?.(0, -step);
      else if (e.key === "ArrowDown") onMoveStep?.(0, step);
      else if (e.key === "ArrowLeft") onMoveStep?.(-step, 0);
      else if (e.key === "ArrowRight") onMoveStep?.(step, 0);
    }
  };

  return (
    <div
      data-testid={`viewport-tile-${id}`}
      data-tile-id={id}
      data-density={density}
      onClick={onFocus}
      onMouseDown={onFocus}
      onTouchStart={onFocus}
      className={`viewport-tile tile-${id.toLowerCase().replace(/\s+/g, "-")} ${
        active ? "active-tile" : ""
      } ${className}`}
      style={{
        display: isMinimized ? "none" : "flex",
        flexDirection: "column",
        background: V.surf,
        border: active
          ? `1px solid ${V.pri}`
          : `1px solid ${V.outv || "rgba(255,255,255,0.12)"}`,
        borderRadius: isFloating ? V.rp || "6px" : "0px",
        overflow: "hidden",
        boxSizing: "border-box",
        transition: "box-shadow 0.15s ease",
        boxShadow: isFloating
          ? "0 12px 28px rgba(0,0,0,0.4)"
          : active
          ? `0 0 0 1px ${V.pri}`
          : "none",
        fontSize: densityFontSize,
        position: isFloating ? "absolute" : "relative",
        flex: isFloating ? "none" : isMaximized ? "1 1 100%" : "1 1 0%",
        minWidth: "180px",
        minHeight: "120px",
        ...style,
      }}
    >
      {/* Tile Header / Window Bar */}
      <div
        className="viewport-tile-header"
        style={tileHeaderStyle}
        tabIndex={0}
        role="region"
        aria-label={`${title || id} header. Use arrow keys to move, Shift+arrow keys to resize.`}
        onKeyDown={handleHeaderKeyDown}
        onMouseDown={(e) => {
          if (isFloating && onDragStart) {
            onDragStart(e);
          }
        }}
        onTouchStart={(e) => {
          if (isFloating && onDragStart) {
            onDragStart(e);
          }
        }}
      >
        <div style={{ display: "flex", alignItems: "center", gap: "6px", flex: 1, minWidth: 0 }}>
          {icon && <Icon name={icon} size={13} />}
          <span
            style={{
              overflow: "hidden",
              whiteSpace: "nowrap",
              textOverflow: "ellipsis",
              textTransform: "uppercase",
            }}
          >
            {title || id}
          </span>
          <span
            style={{
              fontSize: "9px",
              padding: "1px 4px",
              borderRadius: "3px",
              background: "rgba(0,0,0,0.2)",
              opacity: 0.75,
              textTransform: "lowercase",
            }}
          >
            {density}
          </span>
        </div>

        {/* Action Controls */}
        <div
          style={{ display: "flex", alignItems: "center", gap: "4px" }}
          onClick={(e) => e.stopPropagation()}
          onMouseDown={(e) => e.stopPropagation()}
        >
          {isFloating && (
            <div style={{ display: "flex", alignItems: "center", gap: "2px" }}>
              <button
                type="button"
                style={actionBtnStyle}
                onClick={() => onMoveStep?.(0, -32)}
                title="Move panel up"
                aria-label="Move Up"
              >
                <Icon name="chevron-up" size={12} />
              </button>
              <button
                type="button"
                style={actionBtnStyle}
                onClick={() => onMoveStep?.(0, 32)}
                title="Move panel down"
                aria-label="Move Down"
              >
                <Icon name="chevron-down" size={12} />
              </button>
              <button
                type="button"
                style={actionBtnStyle}
                onClick={() => onMoveStep?.(-32, 0)}
                title="Move panel left"
                aria-label="Move Left"
              >
                <Icon name="chevron-left" size={12} />
              </button>
              <button
                type="button"
                style={actionBtnStyle}
                onClick={() => onMoveStep?.(32, 0)}
                title="Move panel right"
                aria-label="Move Right"
              >
                <Icon name="chevron-right" size={12} />
              </button>
            </div>
          )}

          {onToggleDensity && (
            <button
              type="button"
              style={actionBtnStyle}
              onClick={onToggleDensity}
              title={`Density: ${density} (click to toggle)`}
              aria-label="Toggle Density"
            >
              <Icon name="sliders" size={12} />
            </button>
          )}

          {onSplitHorizontal && isDocked && (
            <button
              type="button"
              style={actionBtnStyle}
              onClick={onSplitHorizontal}
              title="Split horizontally"
              aria-label="Split Horizontal"
            >
              <Icon name="columns" size={12} />
            </button>
          )}

          {onSplitVertical && isDocked && (
            <button
              type="button"
              style={actionBtnStyle}
              onClick={onSplitVertical}
              title="Split vertically"
              aria-label="Split Vertical"
            >
              <Icon name="rows" size={12} />
            </button>
          )}

          {isDocked && onFloat && (
            <button
              type="button"
              style={actionBtnStyle}
              onClick={onFloat}
              title="Undock / Float window"
              aria-label="Float Window"
            >
              <Icon name="external-link" size={12} />
            </button>
          )}

          {isFloating && onDock && (
            <button
              type="button"
              style={actionBtnStyle}
              onClick={onDock}
              title="Dock into layout"
              aria-label="Dock Window"
            >
              <Icon name="grid" size={12} />
            </button>
          )}

          {onMinimize && (
            <button
              type="button"
              style={actionBtnStyle}
              onClick={onMinimize}
              title="Minimize tile"
              aria-label="Minimize"
            >
              <span style={{ fontWeight: "bold", fontSize: "14px" }}>&minus;</span>
            </button>
          )}

          {onMaximize && (
            <button
              type="button"
              style={actionBtnStyle}
              onClick={onMaximize}
              title={isMaximized ? "Restore size" : "Maximize tile"}
              aria-label="Maximize"
            >
              <Icon name={isMaximized ? "minimize-2" : "maximize-2"} size={11} />
            </button>
          )}

          {onClose && (
            <button
              type="button"
              style={actionBtnStyle}
              onClick={onClose}
              title="Close tile"
              aria-label="Close"
            >
              <span style={{ fontWeight: "bold", fontSize: "14px" }}>&times;</span>
            </button>
          )}
        </div>
      </div>

      {/* Tile Content Area */}
      <div
        className="viewport-tile-body"
        style={{
          flex: 1,
          minHeight: 0,
          display: "flex",
          flexDirection: "column",
          overflow: "auto",
          padding: densityPadding,
          background: V.bg,
          position: "relative",
        }}
      >
        {children}
      </div>

      {/* Floating Window Resize Grip & Scale Controls */}
      {isFloating && (
        <div
          style={{
            position: "absolute",
            right: 0,
            bottom: 0,
            display: "flex",
            alignItems: "center",
            gap: "2px",
            zIndex: 10,
            background: V.surf,
            padding: "1px",
            borderRadius: "3px 0 0 0",
          }}
          onClick={(e) => e.stopPropagation()}
          onMouseDown={(e) => e.stopPropagation()}
        >
          <button
            type="button"
            onClick={() => onResizeStep?.(-32, -32)}
            style={{
              width: "16px",
              height: "16px",
              padding: 0,
              fontSize: "12px",
              fontWeight: "bold",
              lineHeight: "1",
              border: `1px solid ${active ? V.pri : V.outv || "#888"}`,
              background: V.surf,
              color: V.ink,
              borderRadius: "2px",
              cursor: "pointer",
              display: "flex",
              alignItems: "center",
              justifyContent: "center",
              opacity: 0.9,
            }}
            aria-label="Decrease tile size"
            title="Decrease tile size"
          >
            &minus;
          </button>
          <button
            type="button"
            onClick={() => onResizeStep?.(32, 32)}
            style={{
              width: "16px",
              height: "16px",
              padding: 0,
              fontSize: "12px",
              fontWeight: "bold",
              lineHeight: "1",
              border: `1px solid ${active ? V.pri : V.outv || "#888"}`,
              background: V.surf,
              color: V.ink,
              borderRadius: "2px",
              cursor: "pointer",
              display: "flex",
              alignItems: "center",
              justifyContent: "center",
              opacity: 0.9,
            }}
            aria-label="Increase tile size"
            title="Increase tile size"
          >
            &#43;
          </button>
          {onResizeStart && (
            <div
              onMouseDown={onResizeStart}
              onTouchStart={onResizeStart}
              style={{
                width: "16px",
                height: "16px",
                cursor: "nwse-resize",
                background: `linear-gradient(135deg, transparent 50%, ${
                  active ? V.pri : V.outv || "#888"
                } 50%)`,
              }}
              title="Resize window"
            />
          )}
        </div>
      )}
    </div>
  );
}
