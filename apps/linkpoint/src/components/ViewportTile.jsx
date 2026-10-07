import React, { useState } from "react";
import { useTheme } from "../context/ThemeContext.jsx";
import Icon from "./Icon.jsx";
import SpatialViewportContainer from "./SpatialViewportContainer.jsx";

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
  onRotate = null,
  onZoom = null,
  onReset = null,
  yaw = 0,
  pitch = 0,
  zoom = 1,
  isSpatialViewport = false,
  spatialAriaLabel = "",
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
        tabIndex={0}
        role="region"
        aria-label={title || id || "Viewport Tile"}
        style={{
          flex: 1,
          minHeight: 0,
          display: "flex",
          flexDirection: "column",
          overflow: "auto",
          padding: densityPadding,
          background: V.bg,
          position: "relative",
          outline: "none",
        }}
      >
        {isSpatialViewport || onRotate || onZoom || onReset ? (
          <SpatialViewportContainer
            ariaLabel={spatialAriaLabel || title || id || "3D Viewport Tile"}
            yaw={yaw}
            pitch={pitch}
            zoom={zoom}
            onRotate={onRotate}
            onZoom={onZoom}
            onReset={onReset}
            style={{ width: "100%", height: "100%", position: "relative" }}
          >
            {children}
          </SpatialViewportContainer>
        ) : (
          children
        )}
      </div>

      {/* Floating Window Resize Grip */}
      {isFloating && onResizeStart && (
        <div
          onMouseDown={onResizeStart}
          onTouchStart={onResizeStart}
          style={{
            position: "absolute",
            right: 0,
            bottom: 0,
            width: "16px",
            height: "16px",
            cursor: "nwse-resize",
            background: `linear-gradient(135deg, transparent 50%, ${
              active ? V.pri : V.outv || "#888"
            } 50%)`,
            zIndex: 10,
          }}
          title="Resize window"
        />
      )}
    </div>
  );
}
