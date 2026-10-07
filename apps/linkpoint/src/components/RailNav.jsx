import { useApp } from "../context/AppContext.jsx";
import { useTheme } from "../context/ThemeContext.jsx";
import { NAV_ALL } from "../data/content.js";
import Icon from "./Icon.jsx";
import { navActive } from "../theme/look.js";
import { useRovingTabindex } from "../hooks/useRovingTabindex.js";

// Ported from the `isRail` <sc-if> block — the left rail (Navy Gold, Rule &
// Rail packs, or any pack on a split/tablet-width device).
export default function RailNav() {
  const { state, actions } = useApp();
  const { V, t, nav } = useTheme();
  // Navigation stays visible on the 3D View too. Hiding it left no way out of the
  // scene on phones and tablets (the 3D screen has no header or back button).
  if (nav !== "rail") return null;

  const activeIndex = NAV_ALL.findIndex((n) => navActive(state.screen, n.id));
  const { getTabProps } = useRovingTabindex({
    count: NAV_ALL.length,
    activeIndex: activeIndex >= 0 ? activeIndex : 0,
    onSelect: (idx) => actions.setScreen(NAV_ALL[idx].id),
    orientation: "vertical",
  });

  return (
    <div
      role="tablist"
      aria-label="Navigation rail"
      style={{
        flex: "none",
        width: "104px",
        background: V.surf,
        borderRight: "1px solid " + V.outv,
        display: "flex",
        flexDirection: "column",
        gap: "5px",
        padding: "12px 8px",
      }}
    >
      <div style={{ font: "700 13px/1.15 " + (t.dfont || t.font), letterSpacing: ".2em", color: V.pri, padding: "2px 6px 14px" }}>
        LINK
        <br />
        POINT
      </div>
      {NAV_ALL.map((n, index) => {
        const active = navActive(state.screen, n.id);
        const radius = V.rs || V.navr || "4px";
        const tabProps = getTabProps(index, active);
        return (
          <div
            key={n.id}
            {...tabProps}
            role="tab"
            aria-selected={active}
            aria-label={"Go to " + n.label}
            onClick={() => actions.setScreen(n.id)}
            style={{
              display: "flex",
              flexDirection: "column",
              alignItems: "center",
              gap: "5px",
              padding: "10px 4px",
              cursor: "pointer",
              borderRadius: radius,
              color: active ? (V.onpri || V.onpriC || "#ffffff") : V.ink2,
              background: active ? (V.pri || V.priC) : "transparent",
            }}
          >
            <Icon name={n.icon} size={20} />
            <span style={{ font: "600 8.5px/1 " + t.font, letterSpacing: ".1em" }}>{n.label}</span>
          </div>
        );
      })}
    </div>
  );
}
