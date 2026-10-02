import { useApp } from "../context/AppContext.jsx";
import { useTheme } from "../context/ThemeContext.jsx";
import { NAV_ALL } from "../data/content.js";
import Icon from "./Icon.jsx";
import { navActive } from "../theme/look.js";

// Ported from the `isTiles` <sc-if> block — Metro's bottom tile strip.
export default function TileNav() {
  const { state, actions } = useApp();
  const { V, t, nav } = useTheme();
  // Navigation stays visible on the 3D View too. Hiding it left no way out of the
  // scene on phones and tablets (the 3D screen has no header or back button).
  if (nav !== "tiles") return null;

  return (
    <div style={{ flex: "none", display: "flex", gap: "2px", background: V.bg, padding: "2px" }}>
      {NAV_ALL.map((n) => {
        const active = navActive(state.screen, n.id);
        return (
          <div
            key={n.id}
            onClick={() => actions.setScreen(n.id)}
            role="button"
            tabIndex={0}
            aria-label={"Go to " + n.id}
            aria-pressed={active}
            onKeyDown={(e) => {
              if (e.key === "Enter" || e.key === " ") {
                e.preventDefault();
                actions.setScreen(n.id);
              }
            }}
            style={{ flex: 1, height: "62px", display: "flex", flexDirection: "column", justifyContent: "flex-end", padding: "7px", cursor: "pointer", background: active ? V.pri : V.surf, color: active ? V.onpri : V.ink }}
          >
            <Icon name={n.icon} size={18} />
            <span style={{ font: "300 10px/1.2 " + t.dfont, letterSpacing: ".04em", marginTop: "5px" }}>{n.tile}</span>
          </div>
        );
      })}
    </div>
  );
}
