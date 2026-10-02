import { LAYOUTS } from "./layouts.js";
import { PALETTES } from "./palettes.js";
import { DEVICES, STATES } from "./constants.js";
import { pickInk } from "./color.js";

// Ported from the top of renderVals(): resolves the active layout+palette into
// the token set `V`, the device, the console geometry, and the handful of
// screen-independent flags (isConsole/isFloat/bleed/bare/immersive/norm/
// headLook/stateBlock) that every screen and chrome component needs.
export function computeTheme(state, cf) {
  const L = LAYOUTS[state.layout];
  const base = PALETTES[state.palette];
  const P = state.customTheme?.active
    ? { ...base, name: state.customTheme.name, note: "A custom, shareable colour theme.", c: { ...base.c, ...state.customTheme.colors } }
    : base;
  const t = { name: L.name + " / " + P.name, nav: L.nav, font: L.font, dfont: L.dfont, note: L.note + "   Colour pack: " + P.note + ".", v: { ...P.c, ...L.s } };
  const d = DEVICES[state.device];

  let nav;
  if (d.desk) nav = "floaters";
  else if (t.nav === "SWEEP") nav = "sweep";
  else if (d.split) nav = "rail";
  else nav = t.nav === "TILES" ? "tiles" : t.nav === "RAIL" && d.w > 700 ? "rail" : "tabs";

  const V = t.v;
  const pad = state.dense ? "8px" : V.pad;
  const C = cf();
  const isConsole = nav === "sweep";
  const consoleScene = isConsole && state.screen === "3D View" && state.cond === "normal";
  const isFloat = nav === "floaters";
  const isSweepDesk = isFloat && (state.layout === "sweep" || t.nav === "SWEEP");
  const bleed = isConsole || isFloat;
  const LK = LAYOUTS[state.layout].look;

  const scr = state.screen;
  const sel = (n) => scr === n;
  const ink = (bg, candidates) => pickInk(bg, candidates);

  const condPack = state.cond === "normal" ? null : STATES[state.cond][scr] || STATES[state.cond]._;
  const stateBlockActive = !!condPack && !["Login", "Settings", "Cache", "Search"].includes(scr);
  const norm = !stateBlockActive;
  const bare = ["3D View", "Login", "Search"].includes(scr);
  const immersive = scr === "3D View" && norm;
  const headLook = bare || isFloat ? "none" : nav === "sweep" ? "sweep" : LK.head;

  return { t, d, isSweepDesk, V, pad, C, isConsole, consoleScene, isFloat, bleed, LK, nav, scr, sel, ink, condPack, stateBlockActive, norm, bare, immersive, headLook };
}
