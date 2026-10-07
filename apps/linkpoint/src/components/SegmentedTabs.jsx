import { useApp } from "../context/AppContext.jsx";
import { useTheme } from "../context/ThemeContext.jsx";
import { segLooks } from "../theme/look.js";
import { CSUB, subView, setSub } from "../theme/constants.js";
import { app } from "../linkpoint/app";
import { useRovingTabindex } from "../hooks/useRovingTabindex.js";

// Ported from `segTabs`/`segWrap`/`hasSeg`. The labels come from CSUB — the one
// sub-view table the LCARS rail also renders — so a screen gains a tab strip by
// appearing there, not by being named here.
export default function SegmentedTabs() {
  const { state, actions } = useApp();
  const { V, t, LK, nav, isFloat, norm, scr } = useTheme();

  const curSub = norm && CSUB[scr] && scr !== "Radar" && scr !== "3D View" ? subView(state, scr) : null;

  // Real unread counts from live Second Life chat session
  const unreadIM = app.chat?.messages?.filter((m) => m.type === "im" && m.unread)?.length || 0;
  const unreadGroup = app.chat?.messages?.filter((m) => m.type === "group" && m.unread)?.length || 0;
  const chatBadges = {};
  if (unreadIM > 0) chatBadges.IM = unreadIM;
  if (unreadGroup > 0) chatBadges.GROUP = unreadGroup;

  const tabs = (norm && CSUB[scr] && scr !== "Radar" && scr !== "3D View")
    ? CSUB[scr].map(([label]) => ({ label, badge: scr === "Chat" ? chatBadges[label] : undefined }))
    : [];

  const isActive = (label) => curSub === label;
  const activeIndex = tabs.findIndex((x) => isActive(x.label));
  const { getTabProps } = useRovingTabindex({
    count: tabs.length,
    activeIndex: activeIndex >= 0 ? activeIndex : 0,
    onSelect: (idx) => tabs[idx] && setSub(actions, scr, tabs[idx].label),
    orientation: "horizontal",
  });

  if (!norm) return null;
  // Radar draws its own AVATARS/OBJECTS pill row inside the screen body, and 3D
  // View is immersive with no header to hang a strip under — it carries its
  // CAM/GFX switch in-scene instead. Both still get the LCARS sub-nav.
  if (!CSUB[scr] || scr === "Radar" || scr === "3D View") return null;

  const segLook = LK.seg || "fill";
  const looks = segLooks(V, t.font);
  const base = looks[segLook];
  const activeStyle = segLook === "fill"
    ? { background: V.pri, color: V.onpri }
    : segLook === "pivot"
    ? { color: V.pri, fontWeight: 600 }
    : { color: V.pri, boxShadow: "inset 0 -2px 0 " + V.pri, letterSpacing: V.tls };

  const wrap = isFloat
    ? { flex: "none", display: "flex", flexWrap: "wrap", margin: 0, borderBottom: "1px solid " + V.outv, background: V.surf }
    : segLook === "fill"
    ? { flex: "none", display: "flex", margin: nav === "sweep" ? "12px 12px 10px 4px" : "2px 16px 10px", border: "1px solid " + V.outv, borderRadius: V.rs, overflow: "hidden", background: V.surf }
    : { flex: "none", display: "flex", margin: "0 16px 8px", borderBottom: segLook === "text" ? "1px solid " + V.outv : "none", overflowX: "auto" };

  return (
    <div role="tablist" aria-label="Sub navigation tabs" style={wrap}>
      {tabs.map((x, index) => {
        const active = isActive(x.label);
        const tabProps = getTabProps(index, active);
        const style = {
          ...base,
          ...(active ? activeStyle : null),
          ...(isFloat ? { minHeight: "24px", height: "24px", padding: "0 9px", flex: "none", borderRadius: 0, font: "600 9.5px/1 " + t.font, letterSpacing: ".1em" } : null),
        };
        return (
          <div
            key={x.label}
            {...tabProps}
            onClick={() => setSub(actions, scr, x.label)}
            role="tab"
            aria-selected={active}
            style={style}
          >
            <span style={{ font: "inherit", letterSpacing: "inherit" }}>{x.label}</span>
            {x.badge ? (
              <span
                style={{
                  minWidth: "18px",
                  height: "18px",
                  padding: "0 5px",
                  borderRadius: "9px",
                  background: V.bdg,
                  color: V.onbdg,
                  display: "flex",
                  alignItems: "center",
                  justifyContent: "center",
                  font: "700 10px/1 " + t.font,
                }}
              >
                {x.badge}
              </span>
            ) : null}
          </div>
        );
      })}
    </div>
  );
}
