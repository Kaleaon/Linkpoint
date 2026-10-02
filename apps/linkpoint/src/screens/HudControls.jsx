import { useEffect, useState } from "react";
import { useTheme } from "../context/ThemeContext.jsx";
import { app } from "../linkpoint/app";

// Worn HUDs the simulator has sent. Pick one to show it over the view, zoom it,
// tap its buttons to touch them, hide it. Renders nothing until there is a HUD.
export default function HudControls() {
  const { V, t } = useTheme();
  const [huds, setHuds] = useState(() => app.world.getHuds());
  const [displayed, setDisplayed] = useState(() => app.world.displayedHud);
  const [open, setOpen] = useState(false);
  const [notice, setNotice] = useState("");

  useEffect(() => {
    let timer;
    const say = (message) => { setNotice(message); clearTimeout(timer); timer = setTimeout(() => setNotice(""), 5000); };
    const onHuds = (list) => setHuds(list);
    const onDisplay = (value) => setDisplayed(value);
    const onFailed = (failure) => say(`${failure.action === "touch" ? "Touch" : failure.action} failed: ${failure.message}`);
    app.world.on("huds_changed", onHuds);
    app.world.on("hud_display_changed", onDisplay);
    app.world.on("action_failed", onFailed);
    setHuds(app.world.getHuds());
    setDisplayed(app.world.displayedHud);
    return () => {
      clearTimeout(timer);
      app.world.off("huds_changed", onHuds);
      app.world.off("hud_display_changed", onDisplay);
      app.world.off("action_failed", onFailed);
    };
  }, []);

  if (!huds.length && !displayed && !notice) return null;

  const current = displayed ? huds.find((hud) => hud.id === displayed.id) : null;
  const small = { minHeight: 32, minWidth: 32, padding: "0 10px", border: `1px solid ${V.outv}`, borderRadius: V.rs, background: V.surf, color: V.pri, cursor: "pointer", font: `600 10px/1 ${t.font}`, letterSpacing: ".08em" };

  return <div aria-label="Worn HUDs" style={{ position: "absolute", left: "50%", bottom: 14, transform: "translateX(-50%)", display: "flex", flexDirection: "column", alignItems: "center", gap: 6, zIndex: 30, maxWidth: "92%" }}>
    {notice ? <div role="alert" style={{ padding: "6px 10px", background: V.surf, color: V.err, border: `1px solid ${V.err}`, borderRadius: V.rs, font: `500 11px/1.3 ${t.font}` }}>{notice}</div> : null}
    {open && huds.length ? <ul aria-label="Choose a HUD" style={{ listStyle: "none", margin: 0, padding: 6, display: "grid", gap: 4, background: V.surf, border: `1px solid ${V.pri}`, borderRadius: V.rs, maxHeight: 220, overflowY: "auto", minWidth: 220 }}>
      {huds.map((hud) => <li key={hud.id}>
        <button type="button" aria-pressed={displayed?.id === hud.id} onClick={() => { app.world.setDisplayedHud(displayed?.id === hud.id ? null : hud.id); setOpen(false); }} style={{ ...small, width: "100%", textAlign: "left", background: displayed?.id === hud.id ? V.pri : V.bg, color: displayed?.id === hud.id ? V.onpri : V.ink }}>
          {hud.name || "Unnamed HUD"} <span style={{ opacity: .7 }}>· {hud.pointName}</span>
        </button>
      </li>)}
    </ul> : null}
    <div style={{ display: "flex", gap: 4, alignItems: "center", padding: 4, background: V.surf, border: `1px solid ${V.outv}`, borderRadius: V.rs }}>
      {huds.length ? <button type="button" aria-expanded={open} onClick={() => setOpen((value) => !value)} style={small}>HUDS ({huds.length})</button> : null}
      {current ? <>
        <span style={{ color: V.ink2, font: `500 10px/1 ${t.font}`, maxWidth: 140, overflow: "hidden", textOverflow: "ellipsis", whiteSpace: "nowrap" }}>{current.name || "HUD"}</span>
        <button type="button" aria-label="Make HUD smaller" onClick={() => app.world.zoomHud(-1)} style={small}>−</button>
        <button type="button" aria-label="Make HUD larger" onClick={() => app.world.zoomHud(1)} style={small}>+</button>
        <button type="button" aria-label="Hide HUD" onClick={() => app.world.setDisplayedHud(null)} style={small}>HIDE</button>
      </> : null}
    </div>
  </div>;
}
