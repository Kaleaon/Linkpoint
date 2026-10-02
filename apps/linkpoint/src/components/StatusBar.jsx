import { useTheme } from "../context/ThemeContext.jsx";

// Ported from `statusStyle`/`islandStyle`/`clock` — the phone status bar
// (clock, notch/island, 5G + signal + battery glyphs). Hidden entirely in
// desktop (isFloat) mode, matching the source.
export default function StatusBar() {
  const { V, t, d, isFloat } = useTheme();
  if (isFloat) return null;

  const padding = d.notch === "island" ? "16px 26px 8px" : d.notch === "hole" ? "11px 20px 5px" : "9px 22px 4px";

  return (
    <div style={{ flex: "none", position: "relative", display: "flex", alignItems: "center", justifyContent: "space-between", padding, font: "600 11px/1 " + t.font, color: V.ink, opacity: 0.9 }}>
      <span>14:32</span>
      {d.notch === "island" ? (
        <span style={{ position: "absolute", left: "50%", top: "9px", transform: "translateX(-50%)", width: "104px", height: "26px", borderRadius: "14px", background: "#000" }} />
      ) : d.notch === "hole" ? (
        <span style={{ position: "absolute", left: "50%", top: "7px", transform: "translateX(-50%)", width: "11px", height: "11px", borderRadius: "50%", background: "#000" }} />
      ) : null}
      <span style={{ display: "flex", alignItems: "center", gap: "6px" }}>
        <span style={{ font: "600 9.5px/1 " + t.font, letterSpacing: ".1em" }}>5G</span>
        <span style={{ display: "flex", alignItems: "flex-end", gap: "1.5px", height: "9px" }}>
          <span style={{ width: "2.5px", height: "3px", background: "currentColor" }} />
          <span style={{ width: "2.5px", height: "5px", background: "currentColor" }} />
          <span style={{ width: "2.5px", height: "7px", background: "currentColor" }} />
          <span style={{ width: "2.5px", height: "9px", background: "currentColor", opacity: 0.35 }} />
        </span>
        <span style={{ width: "21px", height: "10px", border: "1px solid currentColor", borderRadius: "2px", padding: "1.5px", display: "flex" }}>
          <span style={{ width: "68%", background: "currentColor", borderRadius: "1px" }} />
        </span>
      </span>
    </div>
  );
}
