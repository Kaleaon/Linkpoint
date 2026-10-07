import React, { useState } from "react";

/**
 * Unified SpatialViewportContainer
 * Wraps 3D WebGL and 2D canvas elements with keyboard camera navigation,
 * dynamic ARIA live region announcements, and an accessible HTML camera toolbar.
 */
export function SpatialViewportContainer({
  children,
  ariaLabel = "3D Viewport",
  yaw = 0,
  pitch = 0,
  zoom = 1,
  onRotate,
  onZoom,
  onReset,
  style = {},
  className = "",
}) {
  const [liveAnnounce, setLiveAnnounce] = useState("");

  const handleKeyDown = (e) => {
    switch (e.key) {
      case "ArrowLeft":
      case "a":
      case "A":
        e.preventDefault();
        if (onRotate) onRotate({ yawDelta: -15, pitchDelta: 0 });
        setLiveAnnounce(`Rotated camera left. Yaw: ${Math.round(yaw - 15)} degrees.`);
        break;
      case "ArrowRight":
      case "d":
      case "D":
        e.preventDefault();
        if (onRotate) onRotate({ yawDelta: 15, pitchDelta: 0 });
        setLiveAnnounce(`Rotated camera right. Yaw: ${Math.round(yaw + 15)} degrees.`);
        break;
      case "ArrowUp":
      case "w":
      case "W":
        e.preventDefault();
        if (onRotate) onRotate({ yawDelta: 0, pitchDelta: 10 });
        setLiveAnnounce(`Tilted camera up. Pitch: ${Math.round(pitch + 10)} degrees.`);
        break;
      case "ArrowDown":
      case "s":
      case "S":
        e.preventDefault();
        if (onRotate) onRotate({ yawDelta: 0, pitchDelta: -10 });
        setLiveAnnounce(`Tilted camera down. Pitch: ${Math.round(pitch - 10)} degrees.`);
        break;
      case "+":
      case "=":
        e.preventDefault();
        if (onZoom) onZoom(0.2);
        setLiveAnnounce(`Zoomed in. Scale: ${(zoom + 0.2).toFixed(1)}x.`);
        break;
      case "-":
      case "_":
        e.preventDefault();
        if (onZoom) onZoom(-0.2);
        setLiveAnnounce(`Zoomed out. Scale: ${(zoom - 0.2).toFixed(1)}x.`);
        break;
      case "r":
      case "R":
      case "Home":
        e.preventDefault();
        if (onReset) onReset();
        setLiveAnnounce("Reset camera to default view.");
        break;
      default:
        break;
    }
  };

  const buttonStyle = {
    padding: "4px 8px",
    fontSize: "11px",
    fontWeight: "600",
    borderRadius: "4px",
    border: "1px solid rgba(255, 255, 255, 0.25)",
    background: "rgba(255, 255, 255, 0.12)",
    color: "#f8fafc",
    cursor: "pointer",
  };

  return (
    <div style={{ position: "relative", width: "100%", height: "100%", ...style }} className={className}>
      {/* Offscreen ARIA Live Region */}
      <div
        role="status"
        aria-live="polite"
        aria-atomic="true"
        style={{
          position: "absolute",
          width: 1,
          height: 1,
          padding: 0,
          margin: -1,
          overflow: "hidden",
          clip: "rect(0,0,0,0)",
          border: 0,
        }}
      >
        {liveAnnounce}
      </div>

      {/* Focusable Spatial Canvas Wrapper */}
      <div
        tabIndex={0}
        role="region"
        aria-label={ariaLabel}
        onKeyDown={handleKeyDown}
        style={{
          outline: "none",
          position: "relative",
          width: "100%",
          height: "100%",
        }}
      >
        {children}
      </div>

      {/* Accessible Camera Control Toolbar */}
      <div
        role="toolbar"
        aria-label="3D Viewport Camera Controls"
        style={{
          position: "absolute",
          bottom: 10,
          left: 10,
          right: 10,
          display: "flex",
          gap: 6,
          alignItems: "center",
          background: "rgba(15, 23, 42, 0.9)",
          padding: "6px 10px",
          borderRadius: 6,
          zIndex: 10,
        }}
      >
        <button
          type="button"
          aria-label="Rotate Left"
          style={buttonStyle}
          onClick={() => {
            if (onRotate) onRotate({ yawDelta: -15, pitchDelta: 0 });
            setLiveAnnounce(`Rotated camera left. Yaw: ${Math.round(yaw - 15)} degrees.`);
          }}
        >
          Orbit Left
        </button>
        <button
          type="button"
          aria-label="Rotate Right"
          style={buttonStyle}
          onClick={() => {
            if (onRotate) onRotate({ yawDelta: 15, pitchDelta: 0 });
            setLiveAnnounce(`Rotated camera right. Yaw: ${Math.round(yaw + 15)} degrees.`);
          }}
        >
          Orbit Right
        </button>
        <button
          type="button"
          aria-label="Zoom In"
          style={buttonStyle}
          onClick={() => {
            if (onZoom) onZoom(0.2);
            setLiveAnnounce(`Zoomed in. Scale: ${(zoom + 0.2).toFixed(1)}x.`);
          }}
        >
          Zoom In
        </button>
        <button
          type="button"
          aria-label="Zoom Out"
          style={buttonStyle}
          onClick={() => {
            if (onZoom) onZoom(-0.2);
            setLiveAnnounce(`Zoomed out. Scale: ${(zoom - 0.2).toFixed(1)}x.`);
          }}
        >
          Zoom Out
        </button>
        <button
          type="button"
          aria-label="Reset Camera"
          style={buttonStyle}
          onClick={() => {
            if (onReset) onReset();
            setLiveAnnounce("Reset camera view.");
          }}
        >
          Reset
        </button>
      </div>
    </div>
  );
}

export default SpatialViewportContainer;
