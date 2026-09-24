import { useCallback, useEffect, useRef, useState } from "react";
import { useViewerState } from "../viewer/ViewerClientContext";
import { createBabylonRenderer } from "@linkpoint/renderer";

export default function World3D() {
  const viewer = useViewerState();
  const canvas = useRef(null);
  const renderer = useRef(null);
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(true);
  const [attempt, setAttempt] = useState(0);

  useEffect(() => {
    let active = true;
    if (!canvas.current) return undefined;
    setLoading(true);
    setError("");
    void createBabylonRenderer(canvas.current)
      .then((next) => {
        if (!active) return next.dispose();
        renderer.current = next;
        next.applySnapshot(Object.values(viewer.scene));
        setLoading(false);
      })
      .catch((reason) => {
        if (active) {
          setLoading(false);
          setError(reason instanceof Error ? reason.message : "GPU initialization failed");
        }
      });
    return () => { active = false; renderer.current?.dispose(); renderer.current = null; };
  }, [attempt]);

  useEffect(() => { renderer.current?.applySnapshot(Object.values(viewer.scene)); }, [viewer.scene]);
  const focusScene = useCallback(() => renderer.current?.focusAll(), []);

  return <section aria-label="3D world view" className="world-viewer">
    <canvas ref={canvas} id="world-canvas" aria-label={`3D canvas for ${viewer.session?.regionName || "disconnected region"}`} />
    <div className="world-hud" aria-live="polite">
      <div className="world-hud__eyebrow">LIVE REGION</div>
      <strong>{viewer.session?.regionName || "Region unavailable"}</strong>
      <span>{Object.keys(viewer.scene).length} scene objects</span>
      {loading ? <span>Starting renderer…</span> : null}
    </div>
    <div className="world-controls" aria-label="World camera controls">
      <button type="button" onClick={focusScene} disabled={loading || Boolean(error)}>Frame scene</button>
    </div>
    {error ? <div className="world-error" role="alert"><strong>3D renderer unavailable</strong><span>{error}</span><button type="button" onClick={() => setAttempt((value) => value + 1)}>Try again</button></div> : null}
  </section>;
}

export function World3DActionBar() {
  const { connection } = useViewerState();
  const connected = connection === "connected";
  return <div role="status" className={`world-connection ${connected ? "is-connected" : "is-offline"}`}>{connected ? "CONNECTED — SCENE STREAM ACTIVE" : "DISCONNECTED"}</div>;
}
