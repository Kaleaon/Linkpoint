import { sideBorder } from "./look.js";
// Desktop (floater) chrome styles per layout family.
//
//  sweep   LCARS-rule console: a swept elbow frames the screen, the rail
//          segments ARE the buttons (label bottom-right, rounded end cap), the
//          title bar reads cap -> bar -> title -> end cap, fills only.
//  metro   Metro/Windows-Phone: flat, zero radius, no borders or shadows,
//          big light lowercase pivot titles, square tiles for navigation.
//  default the established SL-viewer look (bordered windows, icon rail).
//
// Geometry feeds window placement as well as styling, so it lives here rather
// than in scattered constants.

export function deskKind(t) {
  if (t.nav === "SWEEP") return "sweep";
  if (t.nav === "TILES") return "metro";
  return "default";
}

const GEOMETRY = {
  // `frame` is the thickness of the top bar that the elbow sweeps out of.
  sweep: { top: 54, rail: 128, bar: 28, dock: 40, frame: 14, elbow: 26 },
  metro: { top: 40, rail: 78, bar: 40, dock: 44, frame: 0, elbow: 0 },
  default: { top: 38, rail: 42, bar: 26, dock: 40, frame: 0, elbow: 0 },
};

export const deskGeometry = (kind) => GEOMETRY[kind] || GEOMETRY.default;

// Rail segment fills for the sweep console, cycled top to bottom.
export const sweepSegmentFills = (V) => [V.pri, V.sec2, V.sec, V.bdg];

// SVG path for the swept elbow: an outer-rounded corner joining the top bar to
// the rail, with a concave inner curve where they meet.
export function elbowPath({ rail, frame, elbow }, outer = 44) {
  const bottom = frame + elbow;
  return [
    `M0,${outer}`,
    `A${outer},${outer} 0 0 1 ${outer},0`,
    `H${rail + elbow}`,
    `V${frame}`,
    `A${elbow},${elbow} 0 0 0 ${rail},${bottom}`,
    `V${bottom + 2}`,
    `H0`,
    "Z",
  ].join(" ");
}

export function floaterStyle(kind, { V, t, act, ink }) {
  const onPri = ink(V.pri, [V.bg, V.onpri, V.ink]);
  if (kind === "sweep") {
    return {
      frame: { ...sideBorder("Left", "10px", act ? V.pri : V.sec2), borderRadius: "26px 22px 14px 14px", boxShadow: "none" },
      bar: { background: act ? V.pri : V.surf2, color: act ? onPri : V.ink2, font: "700 11px/1 " + t.dfont, letterSpacing: ".16em", textTransform: "uppercase", padding: "0 6px 0 4px", borderRadius: "0 22px 0 0" },
      control: { border: "none", borderRadius: "999px", background: act ? V.bg : V.surf, color: act ? V.pri : V.ink2 },
      grip: act ? V.pri : V.sec2,
      bodyBorder: { borderWidth: "0 0 0 0" },
    };
  }
  if (kind === "metro") {
    return {
      frame: { ...sideBorder("Top", "3px", act ? V.pri : "transparent"), borderRadius: 0, boxShadow: "none" },
      bar: { background: V.surf, color: act ? V.ink : V.ink2, font: "300 21px/1 " + t.dfont, letterSpacing: "0", textTransform: "lowercase", padding: "0 6px 0 12px" },
      control: { border: "none", borderRadius: 0, background: "transparent", color: act ? V.ink : V.ink2 },
      grip: "transparent",
      bodyBorder: { borderWidth: "0" },
    };
  }
  return {
    frame: { border: "1px solid " + (act ? V.pri : V.outv), borderRadius: V.rp, boxShadow: act ? "0 18px 48px rgba(0,0,0,.65)" : "0 6px 18px rgba(0,0,0,.34)" },
    bar: { background: act ? V.pri : V.surf2, color: act ? onPri : V.ink2, font: "700 10.5px/1 " + t.dfont, letterSpacing: ".14em", textTransform: "none", padding: "0 5px 0 9px" },
    control: { border: "1px solid currentColor", borderRadius: V.rs, background: "transparent", color: "inherit" },
    grip: act ? V.pri : V.outv,
    bodyBorder: { borderWidth: "0 1px 1px", borderStyle: "solid", borderColor: V.pri },
  };
}

// Small buttons and chips in the dock / camera HUD.
export function chipStyle(kind, { V, t, on, ink, dim }) {
  const bg = on ? V.pri : V.surf2;
  const base = { display: "flex", alignItems: "center", gap: "6px", cursor: "pointer", color: dim ? V.ink2 : ink(bg, [V.bg, V.onpri, V.ink]), background: dim ? "transparent" : bg };
  if (kind === "sweep") return { ...base, height: "26px", padding: "0 10px", border: "none", borderRadius: "999px", font: "700 10px/1 " + t.dfont, letterSpacing: ".08em", textTransform: "uppercase" };
  if (kind === "metro") return { ...base, height: "30px", padding: "0 12px", border: "none", borderRadius: 0, font: "300 12px/1 " + t.dfont, letterSpacing: "0", textTransform: "lowercase" };
  return { ...base, height: "26px", padding: "0 9px", border: "1px solid " + (dim ? V.outv : "transparent"), borderRadius: V.rs, font: "600 10px/1 " + t.dfont, letterSpacing: ".1em", textTransform: "none" };
}
