import { useEffect, useRef, useState } from "react";
import { useTheme } from "../context/ThemeContext.jsx";
import { useViewerState } from "../viewer/ViewerClientContext";
import { createBabylonRenderer } from "@linkpoint/renderer";

export default function World3D() {
  const { V, t } = useTheme();
  const viewer = useViewerState();
  const canvas = useRef(null);
  const renderer = useRef(null);
  const [error, setError] = useState("");
  useEffect(() => {
    let active = true;
    if (!canvas.current) return undefined;
    void createBabylonRenderer(canvas.current).then((next) => { if (active) { renderer.current = next; next.applySnapshot(Object.values(viewer.scene)); } else next.dispose(); }).catch((reason) => setError(reason instanceof Error ? reason.message : "GPU initialization failed"));
    return () => { active = false; renderer.current?.dispose(); renderer.current = null; };
  }, []);
  useEffect(() => { renderer.current?.applySnapshot(Object.values(viewer.scene)); }, [viewer.scene]);
  return <section aria-label="3D world view" style={{ flex: 1, minHeight: 0, position: "relative", background: "#000" }}>
    <canvas ref={canvas} id="world-canvas" aria-label={`3D canvas for ${viewer.session?.regionName || "disconnected region"}`} style={{ width: "100%", height: "100%", display: "block" }} />
    <output style={{ position: "absolute", left: 12, top: 12, padding: 8, background: V.surf, color: V.ink, font: `400 10px/1.5 ${t.font}` }}>{viewer.session?.regionName || "Region unavailable"}<br />{Object.keys(viewer.scene).length} scene objects{error ? <><br />{error}</> : null}</output>
  </section>;
}
export function World3DActionBar() { const { V } = useTheme(); const { connection } = useViewerState(); const connected = connection === "connected"; return <div role="status" style={{ padding: 8, textAlign: "center", color: connected ? V.pri : V.err, background: V.surf }}>{connected ? "CONNECTED — SCENE STREAM ACTIVE" : "DISCONNECTED"}</div>; }
