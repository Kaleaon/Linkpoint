import { useEffect, useState } from "react";
import { useTheme } from "../context/ThemeContext.jsx";
import { app } from "../linkpoint/app";

export default function World3D() {
  const { V, t } = useTheme();
  const [ready, setReady] = useState(false);
  const [error, setError] = useState("");
  const [position, setPosition] = useState([0, 0, 0]);
  const [objectCount, setObjectCount] = useState(app.world.objects.length);

  const refresh = () => setPosition(app.world.camera3d?.position.map(Math.round) || [0, 0, 0]);
  useEffect(() => {
    let active = true;
    const updateObjects = (objects) => { if (active) setObjectCount(objects.length); };
    app.world.on("objects_changed", updateObjects);
    app.world.init().then(() => { if (active) { setReady(!!app.world.graphics3d); refresh(); } }).catch((reason) => { if (active) setError(reason instanceof Error ? reason.message : "WebGL initialization failed"); });
    return () => { active = false; app.world.off("objects_changed", updateObjects); app.world.destroyRenderer(); };
  }, []);

  const move = (forward, right, up = 0) => { app.world.moveCamera(right, forward, up); refresh(); };
  const region = app.protocol.authReply?.sim_name || app.protocol.authReply?.region_name || "Region unavailable";
  const dataStatus = app.world.getDataStatus();
  const button = { minWidth: 44, minHeight: 44, border: `1px solid ${V.outv}`, borderRadius: V.rs, background: V.surf, color: V.pri, cursor: "pointer" };

  return <section aria-label="3D world view" style={{ flex: 1, minHeight: 0, position: "relative", background: "#000" }}>
    <canvas id="world-canvas" aria-label={`3D canvas for ${region}`} style={{ width: "100%", height: "100%", display: "block" }} />
    <output style={{ position: "absolute", left: 12, top: 12, padding: 8, background: V.surf, color: V.ink, font: `400 10px/1.5 ${t.font}` }}>{region}<br />{position.join(", ")}<br />{dataStatus}<br />{objectCount} scene objects{!ready && !error ? <><br />Starting renderer…</> : null}{error ? <><br /><span style={{ color: V.err }}>{error}</span></> : null}</output>
    <div aria-label="Movement controls" style={{ position: "absolute", left: 14, bottom: 14, display: "grid", gridTemplateColumns: "repeat(3,44px)", gap: 4 }}>
      <span /><button aria-label="Move forward" onClick={() => move(1, 0)} style={button}>↑</button><span />
      <button aria-label="Move left" onClick={() => move(0, -1)} style={button}>←</button><button aria-label="Move backward" onClick={() => move(-1, 0)} style={button}>↓</button><button aria-label="Move right" onClick={() => move(0, 1)} style={button}>→</button>
    </div>
    <div style={{ position: "absolute", right: 14, bottom: 14, display: "grid", gap: 5 }}><button aria-label="Move up" onClick={() => move(0, 0, 1)} style={button}>UP</button><button aria-label="Move down" onClick={() => move(0, 0, -1)} style={button}>DN</button></div>
  </section>;
}

export function World3DActionBar() {
  const { V } = useTheme();
  const connected = app.auth.isLoggedIn();
  const live = app.world.liveSceneSupported;
  return <div role="status" style={{ padding: 8, textAlign: "center", color: connected ? V.pri : V.err, background: V.surf }}>{connected ? (live ? "CONNECTED — LIVE SIMULATOR SCENE" : "CONNECTED — REGION METADATA ONLY; LIVE SCENE REQUIRES DESKTOP") : "DISCONNECTED"}</div>;
}
