import { useEffect, useState } from "react";
import { useApp } from "../context/AppContext.jsx";
import { useTheme } from "../context/ThemeContext.jsx";
import { FLOATERS, CBTN } from "../theme/constants.js";
import { sideBorder, fullBorder } from "../theme/look.js";
import { app } from "../linkpoint/app";
import Icon from "./Icon.jsx";
import ScreenBody from "./ScreenBody.jsx";
import DesktopChrome from "./DesktopChrome.jsx";
import World3D from "../screens/World3D.jsx";
import { formatHeading, useCameraState } from "./cameraReadout.js";
import { deskKind, deskGeometry, floaterStyle, chipStyle } from "../theme/deskStyle.js";

// Ported from the `isFloat` block: desktop SL isn't a screen stack, it's N
// resizable windows over one scene (the `FLOATERS` window model — position,
// size, z-order and minimise all live in state). The currently-focused
// floater's real interactive screen content is overlaid on top of its
// window chrome via `shellStyle`; every other open floater just shows a
// read-only preview list built from the same data (`FBODY` in the source).
export default function FloatersDesktop() {
  const { state, actions } = useApp();
  const { V, t, ink } = useTheme();
  const kind = deskKind(t);
  const G = deskGeometry(kind);
  const DESKTOP_TOP = G.top;
  const DESKTOP_RAIL = G.rail;
  // Sweep floaters have a 10px cap down the left edge; Metro ones a 3px accent line on top.
  const btnRadius = kind === "sweep" ? "999px" : kind === "metro" ? 0 : V.rs;
  const bodyInsetX = kind === "sweep" ? 10 : 0;
  const bodyInsetTop = kind === "metro" ? 3 : 0;
  const [quickMsg, setQuickMsg] = useState("");
  const [showCamHud, setShowCamHud] = useState(true);
  const [, setRuntimeRevision] = useState(0);

  useEffect(() => {
    const refresh = () => setRuntimeRevision((value) => value + 1);
    const worldEvents = ["region_changed", "objects_changed", "nearby_changed"];
    const protocolEvents = ["friends_loaded", "friend_status", "connected", "disconnected"];
    worldEvents.forEach((event) => app.world.on(event, refresh));
    protocolEvents.forEach((event) => app.protocol.on(event, refresh));
    app.inventory.on("inventory_loaded", refresh);
    app.notifications.on("notification_received", refresh);
    return () => {
      worldEvents.forEach((event) => app.world.off(event, refresh));
      protocolEvents.forEach((event) => app.protocol.off(event, refresh));
      app.inventory.off("inventory_loaded", refresh);
      app.notifications.off("notification_received", refresh);
    };
  }, []);

  const fBody = buildFBody(state);

  const flScene = {
    position: "absolute", inset: 0, overflow: "hidden", cursor: "grab",
    background: "linear-gradient(180deg," + V.sky1 + " 0%," + V.sky2 + " 52%," + V.gnd + " 52%," + V.gnd2 + " 100%)",
  };
  const currentRegion = (app.world?.region?.name || "NO REGION DATA").toUpperCase();
  const avatarPos = app.world?.avatarPosition;
  const camera = useCameraState();
  const heading = formatHeading(camera);
  const regionRead = `${currentRegion}${avatarPos ? ` · ${Math.round(avatarPos[0])},${Math.round(avatarPos[1])},${Math.round(avatarPos[2])}` : ""}${heading ? ` · ${heading}` : ""}`;

  const openFloaters = FLOATERS.filter((f) => state.flOpen[f.id] && !state.flMin[f.id]);
  const flFocused = state.flOpen[state.screen] && !state.flMin[state.screen] ? actions.flR(state.screen) : null;

  const sendQuickChat = async (e) => {
    e?.preventDefault();
    const text = quickMsg.trim();
    if (!text) return;
    try {
      if (app.auth.isLoggedIn()) {
        await app.chat.sendMessage(text, 0, 1);
      } else {
        actions.notify("Local Chat (" + text + ")");
      }
    } catch (err) {
      actions.notify("Chat error: " + (err.message || String(err)));
    }
    setQuickMsg("");
  };

  return (
    <div style={{ position: "absolute", inset: 0, overflow: "hidden", background: V.bg }}>
      <div
        onClick={() => state.menu && actions.setMenu(null)}
        style={flScene}
      >
        {/* Desktop uses the same live WebGL scene and simulator object stream as
            mobile. Floaters and desktop chrome remain layered above it. */}
        <World3D desktopBackdrop />
        <div style={{ position: "absolute", left: "14px", bottom: "52px", display: "flex", alignItems: "center", height: "24px", padding: "0 11px", background: V.surf, color: V.ink2, font: "500 10px/1 " + t.font, letterSpacing: ".08em", borderLeft: "5px solid " + V.sec2 }}>
          {regionRead}
        </div>

        {/* Firestorm Camera & Orbit HUD Overlay */}
        {showCamHud && (
          <div
            onClick={(e) => e.stopPropagation()}
            onMouseDown={(e) => e.stopPropagation()}
            style={{
              position: "absolute", right: "16px", bottom: G.dock + 12 + "px", padding: "8px", background: V.surf, ...(kind === "sweep" ? { ...sideBorder("Left", "10px", V.pri), borderRadius: "26px 18px 18px 18px", boxShadow: "none" } : kind === "metro" ? { ...sideBorder("Top", "3px", V.pri), borderRadius: 0, boxShadow: "none" } : { ...fullBorder("1px", V.outv), borderRadius: V.rp, boxShadow: "0 12px 32px rgba(0,0,0,0.5)" }), display: "flex", flexDirection: "column", gap: "6px", zIndex: 20
            }}
          >
            <div style={{ display: "flex", alignItems: "center", justifyContent: "space-between", gap: "12px", borderBottom: "1px solid " + V.outv, paddingBottom: "4px" }}>
              <span style={{ font: "600 9px/1 " + t.dfont, color: V.ink2, letterSpacing: ".08em" }}>CAMERA CONTROLS</span>
              <span onClick={() => setShowCamHud(false)} style={{ cursor: "pointer", color: V.ink2, fontSize: "12px", lineHeight: 1 }}>&times;</span>
            </div>
            <div style={{ display: "grid", gridTemplateColumns: "repeat(3, 24px)", gap: "3px", justifyContent: "center" }}>
              <button type="button" onClick={() => app.world.rotateCamera(8, 0)} style={{ height: "24px", background: V.surf2, border: "1px solid " + V.outv, color: V.ink, borderRadius: btnRadius, cursor: "pointer", display: "flex", alignItems: "center", justifyContent: "center" }} aria-label="Orbit Up">
                <Icon name="chevron-up" size={12} />
              </button>
              <button type="button" onClick={() => app.world.toggleCameraMode()} style={{ height: "24px", background: V.pri, border: "1px solid " + V.pri, color: ink(V.pri, [V.bg, V.onpri, V.ink]), borderRadius: btnRadius, cursor: "pointer", display: "flex", alignItems: "center", justifyContent: "center", font: "600 8px/1 " + t.dfont }} aria-label="Toggle Mode">
                {camera?.mode === "first-person" ? "LOOK" : "ORB"}
              </button>
              <button type="button" onClick={() => app.world.rotateCamera(-8, 0)} style={{ height: "24px", background: V.surf2, border: "1px solid " + V.outv, color: V.ink, borderRadius: btnRadius, cursor: "pointer", display: "flex", alignItems: "center", justifyContent: "center" }} aria-label="Orbit Down">
                <Icon name="chevron-down" size={12} />
              </button>
              <button type="button" onClick={() => app.world.rotateCamera(0, -10)} style={{ height: "24px", background: V.surf2, border: "1px solid " + V.outv, color: V.ink, borderRadius: btnRadius, cursor: "pointer", display: "flex", alignItems: "center", justifyContent: "center" }} aria-label="Orbit Left">
                <Icon name="chevron-left" size={12} />
              </button>
              <button type="button" onClick={() => { app.world.setCameraPreset(camera?.mode === "first-person" ? "first-person" : "rear"); }} style={{ height: "24px", background: V.surf2, border: "1px solid " + V.outv, color: V.ink, borderRadius: btnRadius, cursor: "pointer", display: "flex", alignItems: "center", justifyContent: "center" }} aria-label="Reset View">
                <Icon name="rotate-ccw" size={11} />
              </button>
              <button type="button" onClick={() => app.world.rotateCamera(0, 10)} style={{ height: "24px", background: V.surf2, border: "1px solid " + V.outv, color: V.ink, borderRadius: btnRadius, cursor: "pointer", display: "flex", alignItems: "center", justifyContent: "center" }} aria-label="Orbit Right">
                <Icon name="chevron-right" size={12} />
              </button>
            </div>
          </div>
        )}
      </div>

      <DesktopChrome />

      {openFloaters.map((f) => {
        const r = actions.flR(f.id);
        const act = f.id === state.screen;
        const rows = act ? null : fBody[f.id] || [];
        const fs = floaterStyle(kind, { V, t, act, ink });
        return (
          <div
            key={f.id}
            onMouseDown={() => actions.flFocus(f.id)}
            style={{
              position: "absolute", left: r.x + DESKTOP_RAIL + "px", top: r.y + DESKTOP_TOP + "px", width: r.w + "px", height: r.h + "px",
              zIndex: 10 + Math.max(0, state.flZ.indexOf(f.id)), display: "flex", flexDirection: "column", background: V.surf,
              overflow: "hidden", boxSizing: "border-box", ...fs.frame,
            }}
          >
            <div
              onMouseDown={(e) => actions.flDrag(f.id, e, "move")}
              style={{ flex: "none", height: G.bar + "px", display: "flex", alignItems: "center", gap: "7px", cursor: "move", userSelect: "none", ...fs.bar }}
            >
              {kind === "sweep" && (
                <span style={{ display: "flex", alignItems: "center", height: "100%", padding: "0 8px", background: act ? V.sec : V.sec2, color: act ? V.bg : V.onsec, borderRadius: "18px 0 10px 0", font: "700 9.5px/1 " + t.dfont, letterSpacing: ".1em", flex: "none", marginRight: "4px" }}>
                  {act ? "LCARS" : "SYS"}
                </span>
              )}
              {kind !== "metro" && <Icon name={f.icon} size={13} />}
              <span style={{ flex: 1, minWidth: 0, font: "inherit", letterSpacing: "inherit", textTransform: "inherit", overflow: "hidden", whiteSpace: "nowrap", textOverflow: "ellipsis" }}>{f.title}</span>
              <span
                onClick={(e) => {
                  e.stopPropagation();
                  actions.flToggle(f.id);
                }}
                onKeyDown={(e) => {
                  if (e.key === "Enter" || e.key === " ") {
                    e.preventDefault();
                    e.stopPropagation();
                    actions.flToggle(f.id);
                  }
                }}
                style={{ width: "17px", height: "17px", flex: "none", display: "flex", alignItems: "center", justifyContent: "center", ...fs.control, font: "700 11px/1 " + t.dfont, cursor: "pointer", opacity: 0.85 }}
                role="button" tabIndex={0} aria-label="Minimize"
              >
                &minus;
              </span>
              <span
                onClick={(e) => {
                  e.stopPropagation();
                  actions.flClose(f.id);
                }}
                onKeyDown={(e) => {
                  if (e.key === "Enter" || e.key === " ") {
                    e.preventDefault();
                    e.stopPropagation();
                    actions.flClose(f.id);
                  }
                }}
                style={{ width: "17px", height: "17px", flex: "none", display: "flex", alignItems: "center", justifyContent: "center", ...fs.control, font: "700 11px/1 " + t.dfont, cursor: "pointer", opacity: 0.85 }}
                role="button" tabIndex={0} aria-label="Close"
              >
                &times;
              </span>
            </div>
            {rows ? (
              <div style={{ flex: 1, minHeight: 0, overflowY: "auto", display: "flex", flexDirection: "column", gap: "1px", background: V.bg }}>
                {rows.map((row, i) => (
                  <div key={i} style={{ display: "flex", alignItems: "center", gap: "10px", minHeight: "26px", padding: "0 10px", background: V.surf }}>
                    <span style={{ flex: 1, minWidth: 0, font: "400 11.5px/1.2 " + t.font, color: V.ink, overflow: "hidden", whiteSpace: "nowrap", textOverflow: "ellipsis" }}>{row.a}</span>
                    <span style={{ flex: "none", font: "400 10px/1 " + t.font, color: V.ink2 }}>{row.b}</span>
                  </div>
                ))}
              </div>
            ) : (
              <div style={{ flex: 1, minHeight: 0, background: V.bg }} />
            )}
            <div
              onMouseDown={(e) => actions.flDrag(f.id, e, "size")}
              style={{ position: "absolute", right: 0, bottom: 0, width: "15px", height: "15px", cursor: "nwse-resize", background: "linear-gradient(135deg,transparent 0 52%," + fs.grip + " 52% 100%)" }}
            />
          </div>
        );
      })}

      {flFocused ? (
        <div
          style={{
            position: "absolute", left: flFocused.x + DESKTOP_RAIL + bodyInsetX + "px", top: flFocused.y + DESKTOP_TOP + G.bar + bodyInsetTop + "px", width: flFocused.w - bodyInsetX + "px", height: flFocused.h - G.bar - bodyInsetTop + "px",
            display: "flex", minWidth: 0, overflow: "hidden", background: V.surf, ...floaterStyle(kind, { V, t, act: true, ink }).bodyBorder,
            borderBottomLeftRadius: kind === "default" ? V.rp : 0, borderBottomRightRadius: kind === "default" ? V.rp : kind === "sweep" ? 14 : 0,
            boxSizing: "border-box", zIndex: 50,
          }}
        >
          <ScreenBody />
        </div>
      ) : null}

      {/* Firestorm Desktop Taskbar & Nearby Quick-Chat Dock */}
      <div className="quick-chat-dock" style={{ position: "absolute", left: 0, right: 0, bottom: 0, height: G.dock + "px", display: "flex", alignItems: "center", gap: "6px", padding: "0 8px", background: "var(--chat-bg-scrim, " + (kind === "default" || kind === "sweep" ? V.surf : V.bg) + ")", borderTop: kind === "sweep" ? "6px solid " + V.pri : kind === "metro" ? "none" : "1px solid " + V.outv, borderTopRightRadius: kind === "sweep" ? 999 : 0, zIndex: 60 }}>
        {/* Persistent Firestorm Nearby Quick Chat Input Bar */}
        <form onSubmit={sendQuickChat} style={{ display: "flex", alignItems: "center", gap: "4px", minWidth: "260px", maxWidth: "340px" }}>
          <div style={{ position: "relative", flex: 1 }}>
            <input
              type="text"
              value={quickMsg}
              onChange={(e) => setQuickMsg(e.target.value)}
              placeholder="Nearby Chat..."
              className="chat-input-field"
              style={{
                width: "100%", height: "26px", padding: "0 8px", background: "var(--chat-bg-scrim, " + V.bg + ")", color: "var(--chat-text, " + V.ink + ")",
                border: "1px solid " + V.outv, borderRadius: kind === "sweep" ? "999px" : kind === "metro" ? 0 : V.rs, font: "400 11px/1 " + t.font, outline: "none"
              }}
            />
          </div>
          <button
            type="submit"
            style={{
              height: "26px", padding: "0 10px", background: V.pri, color: ink(V.pri, [V.bg, V.onpri, V.ink]),
              border: "none", borderRadius: kind === "sweep" ? "999px" : kind === "metro" ? 0 : V.rs, font: kind === "metro" ? "300 12px/1 " + t.dfont : "700 9.5px/1 " + t.dfont, letterSpacing: kind === "metro" ? "0" : ".12em", textTransform: kind === "metro" ? "lowercase" : "none", cursor: "pointer", flex: "none"
            }}
          >
            SAY
          </button>
        </form>

        <div style={{ width: "1px", height: "20px", background: V.outv, margin: "0 2px" }} />

        {FLOATERS.filter((f) => state.flOpen[f.id]).map((f) => {
          const min = !!state.flMin[f.id];
          const act = f.id === state.screen && !min;
          return (
            <div
              key={f.id}
              onClick={() => actions.flToggle(f.id)}
              style={{ flex: "none", whiteSpace: "nowrap", ...chipStyle(kind, { V, t, on: act, ink, dim: min }) }}
            >
              {f.title}
            </div>
          );
        })}

        <div style={{ flex: 1, minWidth: "8px" }} />

        {/* Toggle Camera HUD Button */}
        <button
          type="button"
          onClick={() => setShowCamHud((v) => !v)}
          style={{
            flex: "none", whiteSpace: "nowrap", ...chipStyle(kind, { V, t, on: showCamHud, ink }),
          }}
          title="Toggle Camera HUD"
        >
          <Icon name="video" size={12} />
          CAM HUD
        </button>

        {state.cDock.map((k) => {
          const b = CBTN[k],
            lit = !!state.cTog[k],
            dis = !!b.off;
          const bg = lit ? V.pri : V.surf2;
          return (
            <div
              key={k}
              onClick={() => actions.cPress(k)}
              style={{ flex: "none", whiteSpace: "nowrap", cursor: dis ? "not-allowed" : "pointer", ...chipStyle(kind, { V, t, on: lit, ink, dim: dis }) }}
            >
              <Icon name={b.icon} size={13} />
              {b.label}
            </div>
          );
        })}
      </div>
    </div>
  );
}

// Ported from `FBODY` — unfocused floaters keep ticking with real data, not
// placeholders.
function buildFBody(state) {
  const friends = app.friends?.getFriends?.() || [];
  const groups = app.groups?.getGroups?.() || [];
  const invFolders = Array.from(app.inventory?.folders?.values() || []);
  const chatMessages = app.chat?.messages || [];
  const nearby = app.world?.nearbyUsers || [];

  return {
    Chat: chatMessages.slice(-6).map((m) => ({
      a: m.sender || "Resident",
      b: new Date(m.timestamp).toLocaleTimeString([], { hour: "2-digit", minute: "2-digit" }),
    })),
    Radar: nearby.slice(0, 8).map((r) => ({
      a: r.name || `Resident ${r.id.slice(0, 8)}`,
      b: `${Math.round(r.distance || 0)}m`,
    })),
    Friends: friends.slice(0, 12).map((r) => ({
      a: r.name,
      b: r.onlineStatus || "offline",
    })),
    Inventory: invFolders.slice(0, 10).map((n) => ({
      a: n.name,
      b: `${(n.children || []).length} items`,
    })),
    Map: app.world?.region ? [{
      a: app.world.region.name || "Current region",
      b: Number.isFinite(app.world.region.x) ? `${app.world.region.x}, ${app.world.region.y}` : "coordinates unavailable",
    }] : [],
    Profile: [
      { a: app.auth.user?.fullName || "Not connected", b: app.auth.isLoggedIn() ? "online" : "offline" },
      { a: "Region", b: app.world?.region?.name || "not supplied" },
      { a: "Grid", b: app.auth.user?.grid || "not connected" },
      { a: "Groups", b: String(groups.length) },
    ],
    Groups: groups.map((g) => ({ a: g.name, b: g.title || "Member" })),
    Notices: (app.notifications?.items || []).map((item) => ({ a: item.title || item.message, b: item.type || "" })),
    Teleport: [],
    Settings: [
      { a: "Connection", b: app.protocol.connected ? "connected" : "disconnected" },
      { a: "Resident", b: app.auth.user?.fullName || "not connected" },
      { a: "Region", b: app.world?.region?.name || "not supplied" },
    ],
    Diagnostics: [
      { a: "Connection", b: app.protocol.connected ? "connected" : "disconnected" },
      { a: "Capabilities", b: String(Object.keys(app.protocol.capabilities || {}).length) },
    ],
  };
}
