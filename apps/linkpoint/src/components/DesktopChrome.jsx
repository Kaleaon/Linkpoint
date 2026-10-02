import { useEffect, useState } from "react";
import { useApp } from "../context/AppContext.jsx";
import { useTheme } from "../context/ThemeContext.jsx";
import { app } from "../linkpoint/app";
import { sideBorder } from "../theme/look.js";
import Icon from "./Icon.jsx";
import { deskKind, deskGeometry, elbowPath, sweepSegmentFills } from "../theme/deskStyle.js";

const TOOLBAR = [
  ["message-square", "Chat", "Chat"],
  ["users", "People", "Friends"],
  ["contact", "Contacts", "Contacts"],
  ["calendar", "Calendar", "Calendar"],
  ["radar", "Nearby", "Radar"],
  ["map", "Map", "Map"],
  ["folder", "Inventory", "Inventory"],
  ["settings", "Preferences", "Settings"],
];

/**
 * Persistent desktop chrome modelled after the conventional SL viewer layout:
 * navigation and location are always above the world, while high-frequency
 * tools stay on a compact edge rail instead of becoming another floater.
 */
export default function DesktopChrome() {
  const { state, actions } = useApp();
  const { V, t, ink } = useTheme();
  const region = app.world?.regionName || app.protocol?.authReply?.sim_name || "No region data";
  const position = app.world?.avatarPosition;
  const canonicalLocation = position ? `${region} (${position.slice(0, 3).map((value) => Math.round(value)).join(", ")})` : region;
  const [location, setLocation] = useState(canonicalLocation);
  const [balance, setBalance] = useState(app.protocol.balance ?? null);
  useEffect(() => {
    const update = (value) => setBalance(typeof value === "number" ? value : null);
    app.protocol.on("balance_updated", update);
    return () => app.protocol.off("balance_updated", update);
  }, []);
  const kind = deskKind(t);
  const G = deskGeometry(kind);

  const goToLocation = (event) => {
    event.preventDefault();
    const destination = location.trim();
    if (!destination) return;
    actions.notify(`Location: ${destination}`);
  };

  const iconButton = {
    width: 28,
    height: 28,
    border: kind === "default" ? `1px solid ${V.outv}` : "none",
    borderRadius: kind === "sweep" ? 999 : kind === "metro" ? 0 : V.rs,
    background: kind === "metro" ? "transparent" : kind === "sweep" ? V.sec2 : V.surf2,
    color: kind === "sweep" ? ink(V.sec2, [V.bg, V.onsec, V.ink]) : V.ink2,
    display: "inline-flex",
    alignItems: "center",
    justifyContent: "center",
    cursor: "pointer",
  };

  const locationBarStyle = kind === "sweep"
    ? { top: G.frame, left: G.rail + G.elbow + 10, right: 10, height: G.top - G.frame - 2, background: "transparent", borderBottom: "none", boxShadow: "none", padding: "4px 0" }
    : kind === "metro"
      ? { background: V.bg, borderBottom: "none", boxShadow: "none", height: G.top, padding: "4px 10px" }
      : { background: V.surf, borderBottomColor: V.outv };
  const formStyle = kind === "sweep"
    ? { border: "none", borderRadius: 999, background: V.surf, paddingLeft: 14 }
    : kind === "metro"
      ? { ...sideBorder("Bottom", "2px", V.pri), borderRadius: 0, background: V.surf }
      : null;
  const balanceStyle = {
    color: kind === "sweep" ? ink(V.pri, [V.bg, V.onpri, V.ink]) : V.ink,
    borderColor: V.outv,
    background: kind === "sweep" ? V.pri : kind === "metro" ? V.surf : V.surf2,
    fontFamily: kind === "sweep" ? t.dfont : t.font,
    ...(kind === "sweep" ? { borderStyle: "none", borderRadius: 999, letterSpacing: ".12em" } : kind === "metro" ? { borderStyle: "none", borderRadius: 0 } : null),
  };

  return (
    <>
      {kind === "sweep" && (
        <>
          <svg aria-hidden="true" width={G.rail + G.elbow} height={G.frame + G.elbow + 2} style={{ position: "absolute", left: 0, top: 0, zIndex: 57, pointerEvents: "none" }}>
            <path d={elbowPath(G)} fill={V.pri} />
          </svg>
          <div aria-hidden="true" style={{ position: "absolute", top: 0, left: G.rail + G.elbow - 1, right: 0, height: G.frame, background: V.pri, borderRadius: "0 999px 999px 0", zIndex: 57 }} />
        </>
      )}
      <div className="desktop-location-bar" style={locationBarStyle}>
        <div className="desktop-history-controls" aria-label="Navigation history">
          <button type="button" aria-label="Back" title="Back" style={iconButton} onClick={() => actions.notify("No previous location") }><Icon name="chevron-left" size={15} /></button>
          <button type="button" aria-label="Forward" title="Forward" style={iconButton} onClick={() => actions.notify("No next location") }><Icon name="chevron-right" size={15} /></button>
          <button type="button" aria-label="Teleport home" title="Teleport home" style={iconButton} onClick={() => { actions.flFocus("Map"); actions.notify("Teleporting Home..."); }}><Icon name="house" size={14} /></button>
        </div>

        <form className="desktop-location-form" onSubmit={goToLocation} style={formStyle || undefined}>
          <Icon name="lock-keyhole" size={13} style={{ color: V.ok }} aria-hidden="true" />
          <label className="sr-only" htmlFor="desktop-location">Location</label>
          <input id="desktop-location" value={location} onChange={(event) => setLocation(event.target.value)} onFocus={(event) => event.currentTarget.select()} style={{ color: V.ink, fontFamily: t.font }} />
          <button type="submit" aria-label="Go to location" title="Go to location" style={{ ...iconButton, width: 25, height: 24, border: 0, background: "transparent" }}><Icon name="corner-down-left" size={13} /></button>
        </form>

        <button type="button" className="desktop-balance" onClick={() => actions.notify("Opening transaction history") } style={balanceStyle}>
          <span style={{ color: kind === "sweep" ? "inherit" : V.ok }}>L$</span> {balance === null ? "—" : balance.toLocaleString()}
        </button>
        <button type="button" aria-label="Search" title="Search" style={iconButton} onClick={() => actions.openSearch(state.screen, "ALL")}><Icon name="search" size={14} /></button>
      </div>

      {kind === "sweep" ? <SweepRail G={G} V={V} t={t} ink={ink} state={state} actions={actions} /> : kind === "metro" ? <MetroTiles G={G} V={V} t={t} state={state} actions={actions} /> : (
      <nav className="desktop-tool-rail" aria-label="Viewer tools" style={{ background: V.surf, borderColor: V.outv }}>
        {TOOLBAR.map(([icon, label, screen]) => {
          const active = state.screen === screen;
          return (
            <button
              key={label}
              type="button"
              className="desktop-tool-button"
              aria-label={label}
              aria-pressed={active}
              title={label}
              onClick={() => actions.flFocus(screen)}
              style={{ background: active ? V.pri : "transparent", color: active ? ink(V.pri, [V.bg, V.onpri, V.ink]) : V.ink2, borderColor: active ? V.pri : "transparent" }}
            >
              <Icon name={icon} size={18} />
            </button>
          );
        })}
      </nav>
      )}
    </>
  );
}

// Sweep: the rail segments ARE the buttons. Full-width fills in stepped hues,
// label bottom-right, a rounded end cap, and a closing bar below the last one.
function SweepRail({ G, V, t, ink, state, actions }) {
  const inactive = sweepSegmentFills(V).slice(1);
  const top = G.frame + G.elbow + 2;
  return (
    <nav aria-label="Viewer tools" style={{ position: "absolute", zIndex: 55, left: 0, top, bottom: G.dock, width: G.rail, display: "flex", flexDirection: "column", gap: 4 }}>
      {TOOLBAR.map(([icon, label, screen], i) => {
        const active = state.screen === screen;
        const fill = active ? V.pri : inactive[i % inactive.length];
        return (
          <button
            key={label}
            type="button"
            aria-label={label}
            aria-pressed={active}
            title={label}
            onClick={() => actions.flFocus(screen)}
            style={{ flex: "none", height: 44, width: "100%", display: "flex", alignItems: "flex-end", justifyContent: "flex-end", padding: "0 10px 5px 8px", border: "none", borderRadius: 0, cursor: "pointer", background: fill, color: ink(fill, [V.bg, V.onpri, V.ink]), font: "700 13px/1 " + t.dfont, letterSpacing: ".1em", textTransform: "uppercase" }}
          >
            <span>{label}</span>
          </button>
        );
      })}
      <div aria-hidden="true" style={{ flex: 1, minHeight: 12, background: V.sec, borderRadius: "0 0 28px 0" }} />
    </nav>
  );
}

// Metro: square tiles, icon top-left, light lowercase label bottom-left.
function MetroTiles({ G, V, t, state, actions }) {
  return (
    <nav aria-label="Viewer tools" style={{ position: "absolute", zIndex: 55, left: 0, top: G.top, bottom: G.dock, width: G.rail, display: "flex", flexDirection: "column", gap: 4, padding: 4, background: V.bg, overflowY: "auto" }}>
      {TOOLBAR.map(([icon, label, screen]) => {
        const active = state.screen === screen;
        return (
          <button
            key={label}
            type="button"
            aria-label={label}
            aria-pressed={active}
            title={label}
            onClick={() => actions.flFocus(screen)}
            style={{ flex: "none", width: G.rail - 8, height: G.rail - 8, display: "flex", flexDirection: "column", justifyContent: "space-between", alignItems: "flex-start", padding: 8, border: "none", borderRadius: 0, cursor: "pointer", background: active ? V.pri : V.surf, color: active ? V.onpri : V.ink, font: "300 11px/1 " + t.dfont, textTransform: "lowercase" }}
          >
            <Icon name={icon} size={20} />
            <span>{label}</span>
          </button>
        );
      })}
    </nav>
  );
}
