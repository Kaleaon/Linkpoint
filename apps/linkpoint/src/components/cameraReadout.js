// Camera heading readout and live status line for the 3D HUDs. Everything comes
// from the running camera and session; when a value is unknown it is omitted.
import { useEffect, useState } from "react";
import { app } from "../linkpoint/app";
import { realLatency } from "../screens/diagnosticsView.js";

/** "HDG 214° LVL" from a camera state, or null when there is no camera. */
export function formatHeading(camera) {
  const heading = camera?.heading;
  const pitch = camera?.pitch;
  if (typeof heading !== "number" || !Number.isFinite(heading)) return null;
  const degrees = String(Math.round(((heading % 360) + 360) % 360) % 360).padStart(3, "0");
  const tilt = typeof pitch !== "number" || !Number.isFinite(pitch) ? "" : pitch > 2 ? " UP" : pitch < -2 ? " DN" : " LVL";
  return `HDG ${degrees}°${tilt}`;
}

/**
 * Status line pieces: measured latency and the number of avatars in range.
 * Frame rate is not measured by the viewer, so it is not shown.
 */
export function formatLiveStats({ latencyMs, nearbyCount } = {}) {
  const parts = [];
  const ms = realLatency(latencyMs);
  if (ms !== null) parts.push(`SIM ${Math.round(ms)}ms`);
  if (Number.isInteger(nearbyCount) && nearbyCount >= 0) parts.push(`AGENTS ${nearbyCount}`);
  return parts;
}

/** Current camera state, kept in sync with the running camera. */
export function useCameraState() {
  const [camera, setCamera] = useState(() => app.world?.getCameraState?.() || null);
  useEffect(() => {
    const update = (next) => setCamera(next ? { ...next } : null);
    app.world?.on?.("camera_changed", update);
    setCamera(app.world?.getCameraState?.() || null);
    return () => app.world?.off?.("camera_changed", update);
  }, []);
  return camera;
}
