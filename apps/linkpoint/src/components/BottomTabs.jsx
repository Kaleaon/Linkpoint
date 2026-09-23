import { useApp } from "../context/AppContext.jsx";
import { useTheme } from "../context/ThemeContext.jsx";
import { NAV_ALL, TABS_NAV_IDS } from "../data/content.js";
import Icon from "./Icon.jsx";

// Ported from the `isTabs` <sc-if> block — bottom tab bar (Ink Terminal,
// Aero Glass, Press packs).
export default function BottomTabs() {
  const { state, actions } = useApp();
  const { V, t, nav, immersive } = useTheme();
  if (nav !== "tabs" || immersive) return null;
  const items = NAV_ALL.filter((n) => TABS_NAV_IDS.includes(n.id));

  return (
    <div style={{ flex: "none", display: "flex", background: V.surf, borderTop: "1px solid " + V.outv, padding: "6px 0 10px" }}>
      {items.map((n) => {
        const active = state.screen === n.id;
        return (
          <div
            key={n.id}
            onClick={() => actions.setScreen(n.id)}
            style={{ flex: 1, display: "flex", flexDirection: "column", alignItems: "center", gap: "4px", padding: "6px 0", cursor: "pointer", color: active ? V.pri : V.ink2, position: "relative" }}
          >
            <Icon name={n.icon} size={22} />
            <span style={{ font: "600 9px/1 " + t.font, letterSpacing: ".14em" }}>{n.label}</span>
            {n.badge ? (
              <span style={{ position: "absolute", top: "2px", right: "24%", minWidth: "16px", height: "16px", padding: "0 4px", borderRadius: "8px", background: V.bdg, color: V.onbdg, font: "700 9px/16px " + t.font, textAlign: "center" }}>
                {n.badge}
              </span>
            ) : null}
          </div>
        );
      })}
    </div>
  );
}
