// Ported style-builder helpers from renderVals() — the per-layout-pack "look"
// treatments (card chrome, segmented-tab chrome, action buttons) shared by
// several screens/components.

export function cardLooks(V, pad) {
  return {
    box: { border: "1px solid " + V.outv, borderRadius: V.rp, background: V.surf, padding: pad },
    flat: { border: "none", borderLeft: "4px solid transparent", borderRadius: "0", background: V.surf, padding: pad },
    soft: { border: "none", borderRadius: V.rp, background: V.surf, padding: pad, boxShadow: "0 3px 12px rgba(0,0,0,.20), inset 0 1px 0 rgba(255,255,255,.16)" },
    rule: { border: "none", borderTop: "1px solid " + V.outv, borderRadius: "0", background: "transparent", padding: pad + " 2px" },
    quiet: { border: "none", borderBottom: "1px solid " + V.outv, borderRadius: "0", background: "transparent", padding: "4px 0 " + pad },
    cap: { border: "none", borderLeft: "9px solid " + V.sec2, borderRadius: "0 " + V.rp + " " + V.rp + " 0", background: V.surf, padding: pad },
  };
}

export function cardAccentStyle(cardKind, V, accent) {
  if (!accent) return null;
  if (cardKind === "box") return { borderColor: accent };
  if (cardKind === "flat" || cardKind === "cap") return { borderLeftColor: accent };
  if (cardKind === "quiet") return { borderBottomColor: accent };
  if (cardKind === "rule") return { borderTopColor: accent };
  return { boxShadow: "inset 3px 0 0 " + accent + ", 0 3px 12px rgba(0,0,0,.20)" };
}

export function actionButtonStyle(V, font, a) {
  const base = { flex: 1, height: "44px", border: "1px solid " + V.outv, borderRadius: V.rs, display: "flex", alignItems: "center", justifyContent: "center", font: "700 10.5px/1 " + font, letterSpacing: ".16em", color: V.ink, cursor: "pointer" };
  if (a.primary) return { ...base, background: V.pri, color: V.onpri, borderColor: V.pri };
  if (a.dim) return { ...base, color: V.err, borderColor: V.err };
  return base;
}

export function segLooks(V, font) {
  return {
    fill: { flex: 1, display: "flex", alignItems: "center", justifyContent: "center", gap: "6px", padding: "13px 0", cursor: "pointer", background: V.surf, color: V.ink2, font: "600 12px/1 " + font, letterSpacing: ".22em" },
    pivot: { flex: "none", display: "flex", alignItems: "baseline", justifyContent: "flex-start", gap: "6px", padding: "6px 18px 12px 0", cursor: "pointer", background: "transparent", color: V.ink2, font: "300 24px/1 " + font, textTransform: "lowercase" },
    text: { flex: "none", display: "flex", alignItems: "center", justifyContent: "flex-start", gap: "6px", padding: "13px 20px 11px 0", cursor: "pointer", background: "transparent", color: V.ink2, font: "600 12px/1 " + font, letterSpacing: undefined },
  };
}

export function segOnLooks(V) {
  return {
    fill: { background: undefined }, // filled in by caller with `on` shared style
    pivot: { color: V.pri, fontWeight: 400 },
    text: { color: V.pri, boxShadow: "inset 0 -2px 0 " + V.pri },
  };
}
