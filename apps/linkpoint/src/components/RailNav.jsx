import { useApp } from "../context/AppContext.jsx";
import { useTheme } from "../context/ThemeContext.jsx";
import { NAV_ALL } from "../data/content.js";
import Icon from "./Icon.jsx";

// Ported from the `isRail` <sc-if> block — the left rail (Navy Gold, Rule &
// Rail packs, or any pack on a split/tablet-width device).
export default function RailNav() {
  const { state, actions } = useApp();
  const { V, t, nav, immersive } = useTheme();
  if (nav !== "rail" || immersive) return null;

  return (
    <div style={{ flex: "none", width: "104px", background: V.surf, borderRight: "1px solid " + V.outv, display: "flex", flexDirection: "column", gap: "5px", padding: "12px 8px" }}>
      <div style={{ font: "700 13px/1.15 " + t.dfont, letterSpacing: ".2em", color: V.pri, padding: "2px 6px 14px" }}>
        GRID
        <br />
        LINK
      </div>
      {NAV_ALL.map((n) => {
        const active = state.screen === n.id;
        return (
          <div
            key={n.id}
            onClick={() => actions.setScreen(n.id)}
            style={{ display: "flex", flexDirection: "column", alignItems: "center", gap: "5px", padding: "10px 4px", cursor: "pointer", borderRadius: V.navr, color: active ? V.onpriC : V.ink2, background: active ? V.priC : undefined }}
          >
            <Icon name={n.icon} size={20} />
            <span style={{ font: "600 8.5px/1 " + t.font, letterSpacing: ".1em" }}>{n.label}</span>
          </div>
        );
      })}
    </div>
  );
}
