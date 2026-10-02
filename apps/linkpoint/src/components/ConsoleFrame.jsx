import { useApp } from "../context/AppContext.jsx";
import { useTheme } from "../context/ThemeContext.jsx";
import { NAV_ALL, HEAD } from "../data/content.js";
import { LAYOUTS } from "../theme/layouts.js";
import { PALETTES } from "../theme/palettes.js";
import { CBTN, CSUB, CPAD, CPADR, subView, setSub } from "../theme/constants.js";
import Icon from "./Icon.jsx";
import ScreenBody from "./ScreenBody.jsx";
import World3D from "../screens/World3D.jsx";
import { navActive } from "../theme/look.js";
import { formatHeading, formatLiveStats, useCameraState } from "./cameraReadout.js";
import { app } from "../linkpoint/app";

// Ported from the `isConsole` block (lines ~140-222 of the source template)
// plus the matching `cf*`/`consoleNav`/`cdock`/`cfPad`/`cfFly` computations in
// renderVals(). This is the LCARS-manifesto "sweep console" frame: the swept
// elbow IS the chrome (bar -> curve -> rail), rail segments ARE the nav
// buttons, and every screen (not just 3D View) renders inside the elbow.
export default function ConsoleFrame() {
  const { state, actions } = useApp();
  const { V, t, C, ink, consoleScene } = useTheme();
  const tick = state.tick || 0;
  void tick; // re-render each second so the live readouts below stay current
  const loggedIn = app.auth.isLoggedIn();
  const liveStats = loggedIn ? formatLiveStats({ latencyMs: app.protocol.getDiagnostics().latencyMs, nearbyCount: app.world.nearbyUsers.length }) : [];
  const sessionNote = loggedIn ? "" : "DISCONNECTED";
  const simName = app.protocol.authReply?.sim_name || app.world.region?.name || (app.auth.isLoggedIn() ? "REGION PENDING" : "DISCONNECTED");
  const simCoord = app.world.avatarPosition
    ? { x: Math.round(app.world.avatarPosition[0]), y: Math.round(app.world.avatarPosition[1]), z: Math.round(app.world.avatarPosition[2]) }
    : null;

  const cfBar = { position: "absolute", left: 0, top: 0, right: 0, height: C.bar + "px", background: V.pri, borderRadius: Math.round(C.bar * 0.66) + "px 0 0 0" };
  const cfBarEnd = { position: "absolute", right: 0, top: 0, width: (C.wide ? 104 : 66) + "px", height: C.bar + "px", background: V.sec2 };
  const cfBarGap = { position: "absolute", right: (C.wide ? 108 : 70) + "px", top: 0, width: C.gap + "px", height: C.bar + "px", background: V.bg };
  const headMap = HEAD(LAYOUTS[state.layout].name, PALETTES[state.palette].name, { cleared: state.cacheCleared, limit: state.prefs.cacheLimit, loc: state.prefs.cacheLoc });
  const cfTitleText = consoleScene ? simName : (headMap[state.screen] || ["", ""])[0];
  const cfTitle = {
    position: "absolute", right: (C.wide ? 128 : 84) + "px", left: C.rail + C.gap + "px", top: 0, height: C.bar + "px",
    display: "flex", alignItems: "flex-end", justifyContent: "flex-end", paddingBottom: (C.wide ? 11 : 7) + "px",
    font: "700 " + (C.wide ? 36 : 24) + "px/1 " + t.dfont, letterSpacing: ".05em", overflow: "hidden",
    color: ink(V.pri, [V.bg, V.onpri, V.ink]), textTransform: "uppercase", whiteSpace: "nowrap",
  };
  const cfPlate = { position: "absolute", right: 0, top: 0, height: C.bar + "px", width: (C.wide ? 104 : 66) + "px", display: "flex", alignItems: "flex-end", justifyContent: "center", paddingBottom: (C.wide ? 12 : 8) + "px", overflow: "hidden", font: "500 " + (C.wide ? 13 : 10) + "px/1 " + t.font, color: ink(V.sec2, [V.bg, V.onsec, V.ink]), whiteSpace: "nowrap" };
  // Telemetry row, LCARS-style, following the manifesto's three-tier font
  // scale (lcars-terminal.de/tutorial/guideline/font_size.gif): Main Title
  // size is the big bold cfTitle above (sim name on 3D View, screen name
  // elsewhere); this row is Normal Data size, the small supporting
  // readouts underneath it. Rule (matches cdock/cfFoot below): a row of
  // segments is ONE bar — rounding lives only on the two outer ends,
  // square joints between segments — sitting in the extra headroom
  // `cf().bar` reserves above the (bottom-anchored) title so it can never
  // collide with a long screen name.
  const cfNetWrap = { position: "absolute", left: C.rail + C.gap * 3 + "px", right: C.gap * 3 + "px", top: (C.wide ? 10 : 6) + "px", height: (C.wide ? 22 : 16) + "px", display: "flex", overflow: "hidden" };
  const cfNet = (() => {
    // On 3D View the sim name is already the big title above, so this row
    // carries the supporting world telemetry (location, height, ping)
    // instead of the generic grid-link stats every other screen shows here.
    const items = consoleScene
      ? [
          { label: "LOC", value: simCoord ? simCoord.x + "," + simCoord.y : "—" },
          { label: "HEIGHT", value: simCoord ? simCoord.z + "M" : "—" },
          { label: "REGION", value: simName.toUpperCase() },
        ]
      : [
          { label: "LINK", value: app.protocol.connected ? "CONNECTED" : "DISCONNECTED" },
          { label: "OBJECTS", value: String(app.world.objects.length) },
          { label: "CAPS", value: String(Object.keys(app.protocol.capabilities || {}).length) },
        ];
    const last = items.length - 1;
    return items.map((n, i) => {
      const bg = [V.sec2, V.surf2, V.sec][i % 3];
      return {
        ...n,
        style: {
          flex: "none", display: "flex", alignItems: "baseline", gap: "5px", height: (C.wide ? 22 : 16) + "px",
          padding: "0 " + (C.wide ? 10 : 7) + "px", background: bg, marginLeft: i === 0 ? 0 : "2px",
          borderRadius: i === 0 ? (C.wide ? 11 : 8) + "px 0 0 " + (C.wide ? 11 : 8) + "px" : i === last ? "0 " + (C.wide ? 11 : 8) + "px " + (C.wide ? 11 : 8) + "px 0" : "0",
          color: ink(bg, [V.bg, V.onsec, V.ink]),
        },
        labelStyle: { font: "700 " + (C.wide ? 9 : 7.5) + "px/1 " + t.font, letterSpacing: ".14em", opacity: 0.8 },
        valStyle: { font: "700 " + (C.wide ? 11 : 9) + "px/1 " + t.dfont, letterSpacing: ".02em" },
      };
    });
  })();
  const cfCurveFill = { position: "absolute", left: C.rail + "px", top: C.bar + "px", width: C.cur + "px", height: C.cur + "px", background: V.pri };
  const cfCurveCut = { position: "absolute", left: C.rail + "px", top: C.bar + "px", width: C.cur + "px", height: C.cur + "px", background: V.bg, borderRadius: C.cur + "px 0 0 0" };
  const cfRailCol = { position: "absolute", left: 0, top: C.bar + "px", width: C.rail + "px", bottom: C.foot + C.gap + "px", display: "flex", flexDirection: "column", gap: C.gap + "px" };
  const cfArm = { flex: "none", boxSizing: "border-box", height: (C.wide ? 42 : 32) + "px", width: C.rail + "px", background: V.pri, color: ink(V.pri, [V.bg, V.onpri, V.ink]), display: "flex", alignItems: "center", padding: "0 0 0 10px", font: "700 " + (C.wide ? 12 : 10) + "px/1.05 " + t.dfont, letterSpacing: ".14em" };

  const consoleNav = buildConsoleNav({ state, actions, V, t, C, ink });

  const shellStyle = consoleScene
    ? { display: "none" }
    : { position: "absolute", left: C.rail + C.gap + "px", top: C.bar + C.gap + "px", right: C.gap + "px", bottom: C.foot + C.gap * 2 + "px", display: "flex", minWidth: 0, borderRadius: C.rad + "px 0 0 0", overflow: "hidden", background: V.surf, zIndex: 2 };

  return (
    <div style={{ position: "absolute", inset: 0, overflow: "hidden", background: V.bg }}>
      <div style={cfBar} />
      <div style={cfBarEnd} />
      <div style={cfBarGap} />
      <div style={cfNetWrap}>
        {cfNet.map((n, i) => (
          <div key={i} style={n.style}>
            <span style={n.labelStyle}>{n.label}</span>
            <span style={n.valStyle}>{n.value}</span>
          </div>
        ))}
      </div>
      <div style={cfTitle}>{cfTitleText}</div>
      <div style={cfPlate}>LINKPOINT</div>
      <div style={cfCurveFill} />
      <div style={cfCurveCut} />

      <div style={cfRailCol}>
        <div style={cfArm}>Linkpoint</div>
        {consoleNav.map((n, i) => (
          <div key={i} onClick={n.pick} role="button" tabIndex={0} aria-label={n.label} onKeyDown={(e) => { if (e.key === "Enter" || e.key === " ") { e.preventDefault(); n.pick(); } }} style={n.style}>
            {n.code ? <span style={n.codeStyle}>{n.code}</span> : null}
            <span style={{ font: "inherit", letterSpacing: "inherit" }}>{n.label}</span>
          </div>
        ))}
      </div>

      {consoleScene ? (
        <>
          <ConsoleScene />
          <div
            onMouseDown={actions.holdStart}
            onMouseUp={actions.holdEnd}
            onMouseLeave={actions.holdEnd}
            style={{ position: "absolute", left: C.rail + C.gap + "px", right: C.gap + "px", bottom: C.foot + C.gap * 2 + "px", height: C.dock + "px", display: "flex", gap: C.gap + "px", zIndex: state.cEdit ? 6 : 1 }}
          >
            {state.cDock.map((k, i) => {
          const b = CBTN[k],
            lit = !!state.cTog[k],
            dis = !!b.off;
          const bg = dis ? V.surf : lit ? V.pri : V.surf2;
          return (
            <div
              key={k}
              onClick={() => actions.cPress(k)}
              role="button"
              tabIndex={dis ? -1 : 0}
              aria-label={k}
              onKeyDown={(e) => {
                if (dis) return;
                if (e.key === "Enter" || e.key === " ") {
                  e.preventDefault();
                  actions.cPress(k);
                }
              }}
              style={{
                position: "relative", flex: "1 1 0", minWidth: 0, display: "flex", flexDirection: "column",
                alignItems: "center", justifyContent: "center", gap: "4px", background: bg,
                color: dis ? V.ink2 : ink(bg, [V.bg, V.onpri, V.ink]), opacity: dis ? 0.72 : 1, cursor: dis ? "not-allowed" : "pointer",
                borderRadius: i === 0 ? C.rad + "px 0 0 " + C.rad + "px" : i === state.cDock.length - 1 && !state.cEdit ? "0 " + C.rad + "px " + C.rad + "px 0" : "0",
                animation: state.cEdit ? "wig .42s ease-in-out infinite" : "none", animationDelay: (i % 4) * 0.07 + "s",
              }}
            >
              <Icon name={b.icon} size={19} />
              {C.wide ? <span style={{ font: "600 9.5px/1 " + t.dfont, letterSpacing: ".12em" }}>{b.label}</span> : null}
              {state.cEdit ? (
                <span
                  onClick={(e) => {
                    e.stopPropagation();
                    actions.removeDockSlot(k);
                  }}
                  role="button"
                  tabIndex={0}
                  aria-label={"Remove " + k}
                  onKeyDown={(e) => {
                    if (e.key === "Enter" || e.key === " ") {
                      e.preventDefault();
                      e.stopPropagation();
                      actions.removeDockSlot(k);
                    }
                  }}
                  style={{ position: "absolute", right: "3px", top: "3px", width: "18px", height: "18px", borderRadius: "9px", background: V.err, color: ink(V.err, [V.bg, V.ink]), font: "700 13px/18px " + t.dfont, textAlign: "center", cursor: "pointer" }}
                >
                  &minus;
                </span>
              ) : null}
            </div>
          );
        })}
        {state.cEdit ? (
          <div onClick={actions.addSlot} role="button" tabIndex={0} aria-label="Add slot" onKeyDown={(e) => { if (e.key === "Enter" || e.key === " ") { e.preventDefault(); actions.addSlot(); } }} style={{ flex: "0 0 " + (C.wide ? 84 : 56) + "px", display: "flex", alignItems: "center", justifyContent: "center", background: V.surf, border: "2px dashed " + V.outv, color: V.ink2, cursor: "pointer", borderRadius: "0 " + C.rad + "px " + C.rad + "px 0", font: "400 22px/1 " + t.dfont }}>
            +
          </div>
        ) : null}
      </div>
        </>
      ) : null}

      <div style={{ position: "absolute", left: C.rail + C.gap + "px", right: C.gap + "px", bottom: C.gap + "px", height: C.foot + "px", display: "flex", gap: C.gap + "px" }}>
        <span style={{ width: (C.wide ? 56 : 34) + "px", background: V.sec2, flex: "none" }} />
        <span
          style={{
            flex: 1, minWidth: 0, display: "flex", alignItems: "center", justifyContent: "space-between", gap: "10px", padding: "0 12px",
            background: V.surf, color: V.ink2, borderRadius: "0 " + Math.round(C.foot / 2) + "px " + Math.round(C.foot / 2) + "px 0",
            font: "500 " + (C.wide ? 11.5 : 9.5) + "px/1 " + t.font, letterSpacing: ".05em", overflow: "hidden", whiteSpace: "nowrap",
          }}
        >
          <span>{state.cReason || liveStats.join(" · ")}</span>
          <span>{state.cReason ? "" : sessionNote}</span>
        </span>
      </div>

      {state.cEdit ? (
        <>
          <div onClick={actions.endEdit} role="button" tabIndex={0} aria-label="Cancel edit" onKeyDown={(e) => { if (e.key === "Enter" || e.key === " ") { e.preventDefault(); actions.endEdit(); } }} style={{ position: "absolute", inset: 0, background: "rgba(0,0,0,.66)", zIndex: 4 }} />
          <div style={{ position: "absolute", left: C.rail + C.gap + "px", right: C.gap + "px", bottom: C.foot + C.dock + C.gap * 3 + "px", zIndex: 7, display: "flex", alignItems: "center", gap: "12px", height: "28px", padding: "0 14px", background: V.surf, color: V.ink2, font: "500 10.5px/1 " + t.font, letterSpacing: ".12em" }}>
            <span>DOCK EDIT — MINUS REMOVES, PLUS ADDS</span>
            <span onClick={actions.endEdit} role="button" tabIndex={0} aria-label="Finish editing dock" onKeyDown={(e) => { if (e.key === "Enter" || e.key === " ") { e.preventDefault(); actions.endEdit(); } }} style={{ marginLeft: "auto", padding: "5px 16px", background: V.pri, color: ink(V.pri, [V.bg, V.onpri, V.ink]), font: "700 11px/1 " + t.dfont, letterSpacing: ".16em", cursor: "pointer" }}>
              DONE
            </span>
          </div>
        </>
      ) : null}

      {!consoleScene && (
        <div style={shellStyle}>
          <ScreenBody />
        </div>
      )}
    </div>
  );
}

function buildConsoleNav({ state, actions, V, t, C, ink }) {
  const hs = C.wide ? [94, 56, 70, 46, 58, 46, 52, 46] : [58, 46, 52, 46, 48, 46, 50, 46];
  const tint = [V.sec2, V.surf2, V.sec, V.surf2, V.sec2, V.sec, V.surf2, V.sec];
  const items = NAV_ALL.map((n, i) => {
    const on = navActive(state.screen, n.id);
    const bg = state.cFlash === n.id ? V.priC : on ? V.pri : tint[i % tint.length];
    return {
      label: n.label, code: "0" + (i + 1) + "-" + (4471 + i * 17), pick: () => actions.cTap(n.id), on,
      codeStyle: { position: "absolute", left: "8px", top: "5px", font: "500 8px/1 " + t.font, color: ink(bg, [V.bg, V.ink]), opacity: 0.55 },
      style: {
        position: "relative", flex: "none", boxSizing: "border-box", height: hs[i % hs.length] + "px", width: C.rail + "px",
        background: bg, color: ink(bg, [V.bg, V.onpri, V.ink]), display: "flex", alignItems: "flex-end", justifyContent: "flex-end",
        padding: "0 8px 5px 0", cursor: "pointer", font: "700 " + (C.wide ? 11 : 10) + "px/1 " + t.dfont, letterSpacing: ".1em", transition: "background .16s ease",
      },
    };
  });
  const at = items.findIndex((x) => x.on);
  const subActive = subView(state, state.screen);
  const sub = (CSUB[state.screen] || []).map(([label, code]) => {
    const sOn = subActive === label;
    const sbg = sOn ? V.sec : V.surf2;
    return {
      label, code, on: false,
      pick: () => setSub(actions, state.screen, label),
      codeStyle: { position: "absolute", left: "7px", top: "4px", font: "500 7.5px/1 " + t.font, color: ink(sbg, [V.ink2, V.bg]), opacity: 0.8 },
      style: {
        position: "relative", flex: "none", boxSizing: "border-box", height: (C.wide ? 34 : 30) + "px", width: C.rail - 26 + "px", marginLeft: "26px",
        background: sbg, color: ink(sbg, [V.ink, V.bg]), display: "flex", alignItems: "flex-end", justifyContent: "flex-end", padding: "0 7px 4px 0",
        cursor: "pointer", font: "700 8.5px/1 " + t.dfont, letterSpacing: ".08em",
      },
    };
  });
  if (at >= 0 && sub.length) items.splice(at + 1, 0, ...sub);
  return items.concat([{ label: "", code: "", pick: () => {}, on: false, codeStyle: { display: "none" }, style: { flex: "none", height: "26px", width: C.rail + "px", background: V.outv, borderRadius: "0 0 0 " + C.rad + "px", marginTop: "auto" } }]);
}

// Ported from the `consoleScene` block: the parallax world view, region
// readout, movement pad and fly controls that only appear on the Sweep
// Console pack's 3D View screen (cond === "normal").
function ConsoleScene() {
  const { state, actions } = useApp();
  const { V, t, C, ink } = useTheme();

  const simName = app.protocol.authReply?.sim_name || app.world.region?.name || (app.auth.isLoggedIn() ? "REGION PENDING" : "DISCONNECTED");
  const simCoord = app.world.avatarPosition
    ? { x: Math.round(app.world.avatarPosition[0]), y: Math.round(app.world.avatarPosition[1]), z: Math.round(app.world.avatarPosition[2]) }
    : null;

  const cfScene = {
    position: "absolute", left: C.rail + C.gap + "px", top: C.bar + C.gap + "px", right: C.gap + "px",
    bottom: C.foot + C.dock + C.gap * 3 + "px", borderRadius: C.rad + "px 0 0 0", overflow: "hidden", cursor: "grab",
    background: "#000",
  };
  // The console scene has one readout strip, so 3D View's CAM/GFX sub-views swap
  // what it reports rather than adding a second strip to the LCARS frame.
  const camera = useCameraState();
  const heading = formatHeading(camera);
  const where = simCoord ? " · " + simCoord.x + "," + simCoord.y + "," + simCoord.z : "";
  const regionRead =
    state.screen === "3D View" && subView(state, "3D View") === "GFX"
      ? "GFX · DRAW " + state.prefs.draw.toUpperCase() + " · " + state.prefs.quality.toUpperCase() + " · " + state.prefs.fps.toUpperCase() +
        " · SHADOWS " + (state.toggles.shadows ? "ON" : "OFF") + " · " + (state.toggles.battery ? "SAVER" : "FULL")
      : simName.toUpperCase() + where + (heading ? " · " + heading : "");

  return (
    <div style={cfScene}>
      <World3D desktopBackdrop />
      <div style={{ position: "absolute", left: (C.wide ? 28 : 16) + "px", top: "16px", display: "flex", alignItems: "center", height: "26px", padding: "0 12px", background: V.surf, color: V.ink2, font: "500 " + (C.wide ? 12 : 10) + "px/1 " + t.font, letterSpacing: ".08em", borderLeft: "6px solid " + V.sec2, zIndex: 10 }}>
        {regionRead}
      </div>

      {state.cPad ? (
        <div style={{ position: "absolute", left: (C.wide ? 24 : 14) + "px", bottom: (C.wide ? 24 : 14) + "px", display: "flex", alignItems: "flex-start", gap: C.gap + "px" }}>
          <div style={{ display: "grid", gridTemplateColumns: "repeat(3," + (C.wide ? 52 : 46) + "px)", gridTemplateRows: "repeat(3," + (C.wide ? 52 : 46) + "px)", gap: C.gap + "px" }}>
            {CPAD.map((p, i) => {
              if (!p.k) return <div key={i} style={{ background: "transparent", pointerEvents: "none" }} />;
              const active = p.k === "cam" ? false : state.cHeld === p.k;
              const base = p.k === "cam" ? V.sec : V.surf2;
              const bg = active ? V.pri : base;
              return (
                <div
                  key={i}
                  onMouseDown={() => actions.cHold(p.k)}
                  onMouseUp={() => actions.cHold("")}
                  onMouseLeave={() => actions.cHold("")}
                  style={{ display: "flex", flexDirection: "column", alignItems: "center", justifyContent: "center", gap: "2px", cursor: "pointer", background: bg, color: ink(bg, [V.bg, V.onpri, V.ink]), opacity: 0.92, borderRadius: CPADR[i] || "0", font: "700 8px/1 " + t.dfont, letterSpacing: ".08em" }}
                >
                  {p.icon ? <Icon name={p.icon} size={19} /> : null}
                  {p.k === "cam" ? <span>{camera?.mode === "first-person" ? "MOUSE" : "ORBIT"}</span> : null}
                </div>
              );
            })}
          </div>
          <div style={{ display: "flex", flexDirection: "column", gap: "4px" }}>
            {(() => {
              const mk = (on, bg) => ({ width: (C.wide ? 60 : 52) + "px", height: (C.wide ? 44 : 46) + "px", display: "flex", alignItems: "center", justifyContent: "center", cursor: "pointer", opacity: 0.92, background: on ? V.pri : bg, color: ink(on ? V.pri : bg, [V.bg, V.onpri, V.ink]), font: "700 12px/1 " + t.dfont, letterSpacing: ".14em" });
              return (
                <>
                  <div onMouseDown={actions.flyUpDown} onMouseUp={actions.flyRelease} onMouseLeave={actions.flyRelease} style={{ ...mk(state.cHeld === "up", V.sec), borderRadius: "0 " + C.rad + "px 0 0" }}>
                    UP
                  </div>
                  <div onMouseDown={actions.flyDnDown} onMouseUp={actions.flyRelease} onMouseLeave={actions.flyRelease} style={mk(state.cHeld === "dn", V.sec)}>
                    DN
                  </div>
                  <div onClick={actions.toggleRun} role="button" tabIndex={0} aria-label="Toggle run" onKeyDown={(e) => { if (e.key === "Enter" || e.key === " ") { e.preventDefault(); actions.toggleRun(); } }} style={mk(state.cRun, V.surf2)}>
                    RUN
                  </div>
                  <div onClick={actions.togglePad} role="button" tabIndex={0} aria-label="Hide move pad" onKeyDown={(e) => { if (e.key === "Enter" || e.key === " ") { e.preventDefault(); actions.togglePad(); } }} style={{ ...mk(false, V.sec2), borderRadius: "0 0 " + C.rad + "px 0", font: "700 10px/1 " + t.dfont }}>
                    HIDE
                  </div>
                </>
              );
            })()}
          </div>
        </div>
      ) : (
        <div onClick={actions.togglePad} role="button" tabIndex={0} aria-label="Show move pad" onKeyDown={(e) => { if (e.key === "Enter" || e.key === " ") { e.preventDefault(); actions.togglePad(); } }} style={{ position: "absolute", left: (C.wide ? 24 : 14) + "px", bottom: (C.wide ? 24 : 14) + "px", display: "flex", alignItems: "center", height: "34px", padding: "0 14px", background: V.surf2, color: ink(V.surf2, [V.ink, V.bg]), cursor: "pointer", borderLeft: "6px solid " + V.sec2, font: "700 11px/1 " + t.dfont, letterSpacing: ".16em" }}>
          MOVE PAD
        </div>
      )}
    </div>
  );
}
