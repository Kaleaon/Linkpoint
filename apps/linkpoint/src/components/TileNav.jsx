import { useApp } from "../context/AppContext.jsx";
import { useTheme } from "../context/ThemeContext.jsx";
import { NAV_ALL } from "../data/content.js";
import Icon from "./Icon.jsx";
import { navActive } from "../theme/look.js";
import { useRovingTabindex } from "../hooks/useRovingTabindex.js";

// Ported from the `isTiles` <sc-if> block — Metro's bottom tile strip.
export default function TileNav() {
  const { state, actions } = useApp();
  const { V, t, nav } = useTheme();
  // Navigation stays visible on the 3D View too. Hiding it left no way out of the
  // scene on phones and tablets (the 3D screen has no header or back button).
  if (nav !== "tiles") return null;

  const activeIndex = NAV_ALL.findIndex((n) => navActive(state.screen, n.id));
  const { getTabProps } = useRovingTabindex({
    count: NAV_ALL.length,
    activeIndex: activeIndex >= 0 ? activeIndex : 0,
    onSelect: (idx) => actions.setScreen(NAV_ALL[idx].id),
    orientation: "horizontal",
  });

  return (
    <div
      role="tablist"
      aria-label="Tile navigation"
      style={{
        flex: "none",
        display: "flex",
        gap: "2px",
        background: V.surf,
        borderTop: "1px solid " + V.outv,
        padding: "2px",
      }}
    >
      {NAV_ALL.map((n, index) => {
        const active = navActive(state.screen, n.id);
        const radius = V.rs || "0px";
        const tabProps = getTabProps(index, active);
        return (
          <div
            key={n.id}
            {...tabProps}
            role="tab"
            aria-selected={active}
            aria-label={"Go to " + (n.label || n.id)}
            onClick={() => actions.setScreen(n.id)}
            style={{
              flex: 1,
              height: "62px",
              display: "flex",
              flexDirection: "column",
              justifyContent: "flex-end",
              padding: "7px",
              cursor: "pointer",
              borderRadius: radius,
              background: active ? V.pri : V.surf,
              color: active ? V.onpri : V.ink2,
            }}
          >
            <Icon name={n.icon} size={18} />
            <span style={{ font: "300 10px/1.2 " + (t.dfont || t.font), letterSpacing: ".04em", marginTop: "5px" }}>{n.tile}</span>
          </div>
        );
      })}
    </div>
  );
}
