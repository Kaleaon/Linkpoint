import { useApp } from "../context/AppContext.jsx";
import { useTheme } from "../context/ThemeContext.jsx";

// Ported from `toastStyle`/`{{ toast }}` — the transient acknowledgement
// toast shared by every screen and layout pack, the feedback channel for
// actions (dialog buttons, header icons, map/profile/inventory taps, …)
// that don't have a more specific effect.
export default function Toast() {
  const { state } = useApp();
  const { V, t, isFloat } = useTheme();
  if (!state.toast) return null;

  const toastStyle = {
    position: "absolute", left: "50%", bottom: isFloat ? "14px" : "78px", transform: "translateX(-50%)",
    maxWidth: "84%", padding: "10px 16px", borderRadius: V.rs, background: V.priC, color: V.onpriC,
    font: "600 11.5px/1.3 " + t.font, letterSpacing: ".02em", textAlign: "center", zIndex: 40,
    boxShadow: "0 8px 20px rgba(0,0,0,.35)", border: "1px solid " + V.pri, pointerEvents: "none",
  };

  return <div style={toastStyle}>{state.toast}</div>;
}
