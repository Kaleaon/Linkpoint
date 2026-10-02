import { useEffect, useRef, useState } from "react";
import { useTheme } from "../context/ThemeContext.jsx";
import { app } from "../linkpoint/app";
import HudControls from "./HudControls.jsx";

export default function World3D({ desktopBackdrop = false }) {
  const { V, t } = useTheme();
  const canvasRef = useRef(null);
  const [ready, setReady] = useState(false);
  const [error, setError] = useState("");
  const [position, setPosition] = useState([0, 0, 0]);
  const [objectCount, setObjectCount] = useState(app.world.objects.length);
  const [cameraPreset, setCameraPreset] = useState("rear");
  const [selection, setSelection] = useState(app.world.selectedObject);

  const refresh = () => setPosition(app.world.camera3d?.position.map(Math.round) || [0, 0, 0]);
  useEffect(() => {
    let active = true;
    const canvas = canvasRef.current;
    const updateObjects = (objects) => { if (active) setObjectCount(objects.length); };
    const updateCamera = (camera) => { if (active && camera) { setPosition(camera.position.map(Math.round)); setCameraPreset(camera.preset); } };
    app.world.on("objects_changed", updateObjects);
    app.world.on("camera_changed", updateCamera);
    const updateSelection = (object) => { if (active) setSelection(object); };
    app.world.on("selection_changed", updateSelection);
    app.world.init(canvas).then(() => { if (active) { setReady(!!app.world.graphics3d); refresh(); } }).catch((reason) => { if (active) setError(reason instanceof Error ? reason.message : "WebGL initialization failed"); });
    return () => { active = false; app.world.off("objects_changed", updateObjects); app.world.off("camera_changed", updateCamera); app.world.off("selection_changed", updateSelection); app.world.destroyRenderer(canvas); };
  }, []);

  const move = (forward, right, up = 0) => { app.world.moveCamera(right, forward, up); refresh(); };
  const region = app.protocol.authReply?.sim_name || app.protocol.authReply?.region_name || app.world.region?.name || "Region unavailable";
  const dataStatus = app.world.getDataStatus();
  const button = { minWidth: 44, minHeight: 44, border: `1px solid ${V.outv}`, borderRadius: V.rs, background: V.surf, color: V.pri, cursor: "pointer" };

  const handleRefreshScene = async () => {
    try {
      await app.world.loadScene();
      setObjectCount(app.world.objects.length);
      refresh();
    } catch (err) {
      console.warn('Refresh scene error:', err);
    }
  };

  const sceneStyle = desktopBackdrop
    ? { position: "absolute", inset: 0, width: "100%", height: "100%", overflow: "hidden", background: "#000" }
    : { flex: 1, minHeight: 0, position: "relative", background: "#000" };

  return <section aria-label="3D world view" style={sceneStyle}>
    <canvas ref={canvasRef} id={desktopBackdrop ? "world-canvas-backdrop" : "world-canvas"} aria-label={`Interactive 3D canvas for ${region}. Drag to look, shift drag to pan, wheel or pinch to zoom. W A S D or arrow keys move the camera.`} style={{ width: "100%", height: "100%", display: "block", cursor: "grab", touchAction: "none", outline: "none" }} />
    {!desktopBackdrop && <output style={{ position: "absolute", left: 12, top: 12, padding: 8, background: V.surf, color: V.ink, font: `400 10px/1.5 ${t.font}`, borderRadius: V.rs, border: `1px solid ${V.outv}`, backdropFilter: "blur(4px)" }}>
      <strong>{region}</strong><br />
      Pos: {position.join(", ")}<br />
      {dataStatus}<br />
      {objectCount} simulator objects
      <div style={{ marginTop: 6, opacity: .75 }}>Drag: orbit · Shift-drag: pan · Wheel/pinch: zoom<br />WASD / ↑↓: move · ←→: turn · E/Q: up/down · Shift: run</div>
      <div style={{ marginTop: 4, opacity: .9 }}>Tap an object to inspect it</div>
      <div style={{ marginTop: 4 }}>
        <button
          type="button"
          onClick={handleRefreshScene}
          disabled={!app.auth.isLoggedIn()}
          style={{ padding: "2px 8px", fontSize: "10px", background: V.pri, color: V.onpri, border: 0, borderRadius: V.rs, cursor: "pointer" }}
        >
          SYNC SCENE
        </button>
      </div>
      {!ready && !error ? <><br />Starting renderer…</> : null}
      {error ? <><br /><span style={{ color: V.err }}>{error}</span></> : null}
    </output>}
    {!desktopBackdrop && selection && <aside aria-label="Selected object" style={{ position: "absolute", right: 14, top: 66, width: 210, padding: 10, color: V.ink, background: V.surf, border: `1px solid ${V.pri}`, borderRadius: V.rs, boxShadow: "0 8px 24px #0008", font: `400 11px/1.4 ${t.font}` }}>
      <div style={{ color: V.pri, fontWeight: 800, letterSpacing: ".08em", fontSize: 9 }}>SELECTED</div>
      <strong style={{ display: "block", marginTop: 3 }}>{selection.name || selection.id || "Simulator object"}</strong>
      <span style={{ opacity: .7 }}>{selection.shape || (selection.avatar ? "Avatar" : "Object")} · {selection.distance?.toFixed?.(1) || "—"} m</span>
      <button type="button" onClick={() => app.world.focusSelectedObject()} style={{ ...button, width: "100%", minHeight: 32, marginTop: 8, background: V.pri, color: V.onpri, fontSize: 10 }}>FOCUS CAMERA</button>
      {!selection.avatar && app.auth.isLoggedIn() ? <div style={{ display: "flex", gap: 4, marginTop: 4 }}>
        <button type="button" onClick={() => void app.world.touchSelected()} style={{ ...button, flex: 1, minHeight: 32, fontSize: 10 }}>TOUCH</button>
        <button type="button" onClick={() => void app.protocol.sit(selection.id).catch((error) => app.world.emit("action_failed", { action: "sit", message: error instanceof Error ? error.message : "Sit failed" }))} style={{ ...button, flex: 1, minHeight: 32, fontSize: 10 }}>SIT</button>
      </div> : null}
    </aside>}
    {!desktopBackdrop && <div aria-label="Camera view" style={{ position: "absolute", right: 14, top: 14, display: "flex", gap: 4 }}>
      {[['rear','REAR'], ['front','FRONT'], ['first-person','MOUSELOOK'], ['free','FREE']].map(([value, label]) => <button key={value} type="button" aria-pressed={cameraPreset === value} onClick={() => { app.world.setCameraPreset(value); setCameraPreset(value); refresh(); }} style={{ ...button, minWidth: 0, padding: "0 8px", background: cameraPreset === value ? V.pri : V.surf, color: cameraPreset === value ? V.onpri : V.pri, fontSize: 9 }}>{label}</button>)}
    </div>}
    {!desktopBackdrop && <div aria-label="Movement controls" style={{ position: "absolute", left: 14, bottom: 14, display: "grid", gridTemplateColumns: "repeat(3,44px)", gap: 4 }}>
      <span /><button aria-label="Move forward" onClick={() => move(1, 0)} style={button}>↑</button><span />
      <button aria-label="Move left" onClick={() => move(0, -1)} style={button}>←</button><button aria-label="Move backward" onClick={() => move(-1, 0)} style={button}>↓</button><button aria-label="Move right" onClick={() => move(0, 1)} style={button}>→</button>
    </div>}
    {!desktopBackdrop && <div style={{ position: "absolute", right: 14, bottom: 14, display: "grid", gap: 5 }}><button aria-label="Move up" onClick={() => move(0, 0, 1)} style={button}>UP</button><button aria-label="Move down" onClick={() => move(0, 0, -1)} style={button}>DN</button></div>}
    <HudControls />
  </section>;
}

export function World3DActionBar() {
  const { V } = useTheme();
  const connected = app.auth.isLoggedIn();
  const count = app.world.objects.length;
  return (
    <div role="status" style={{ padding: 8, textAlign: "center", color: connected ? V.pri : V.err, background: V.surf, fontSize: "11px", fontWeight: 700, letterSpacing: "0.5px" }}>
      {connected ? `CONNECTED — LIVE SECOND LIFE SCENE (${count} OBJECTS)` : "DISCONNECTED"}
    </div>
  );
}
