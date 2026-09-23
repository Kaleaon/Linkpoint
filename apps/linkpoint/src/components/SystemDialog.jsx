import { useApp } from "../context/AppContext.jsx";
import { useTheme } from "../context/ThemeContext.jsx";
import { DIALOGS } from "../theme/dialogs.js";
import Icon from "./Icon.jsx";

// Ported from the `dialog` computation + its <sc-if> block — the 6 SL
// "system moment" sheets (llDialog, Permissions, Inventory offer, Teleport
// lure, Pay L$, Region restart).
export default function SystemDialog() {
  const { state, actions } = useApp();
  const { V, t } = useTheme();
  const dlg = state.dialog && DIALOGS[state.dialog];
  if (!dlg) return null;

  const btnBase = { flex: "1 1 40%", minHeight: "46px", display: "flex", alignItems: "center", justifyContent: "center", border: "1px solid " + V.outv, borderRadius: V.rs, font: "700 11px/1 " + t.font, letterSpacing: ".14em", color: V.ink, textAlign: "center", padding: "0 8px", cursor: "pointer" };

  return (
    <div style={{ position: "absolute", inset: 0, zIndex: 9, background: "rgba(0,0,0,.62)", display: "flex", alignItems: "flex-end" }}>
      <div style={{ width: "100%", background: V.surf, borderTop: "1px solid " + V.pri, borderRadius: V.rl + " " + V.rl + " 0 0", padding: "18px 16px 22px" }}>
        <div style={{ display: "flex", alignItems: "center", gap: "9px", marginBottom: "10px" }}>
          <Icon name={dlg.icon} size={18} style={{ color: V.pri }} />
          <span style={{ flex: 1, font: "600 12px/1 " + t.font, letterSpacing: ".2em", color: V.pri }}>{dlg.kind}</span>
          <span onClick={() => actions.setDialog(null)} style={{ font: "400 11px/1 " + t.font, color: V.ink2, cursor: "pointer" }}>
            CLOSE
          </span>
        </div>
        <div style={{ font: "600 15px/1.35 " + t.font, color: V.ink }}>{dlg.title}</div>
        <div style={{ font: "400 12.5px/1.65 " + t.font, color: V.ink2, marginTop: "8px" }}>{dlg.body}</div>
        {dlg.meta ? (
          <div style={{ marginTop: "10px", border: "1px dashed " + V.outv, borderRadius: V.rs, padding: "9px", font: "400 11px/1.6 " + t.font, color: V.ink2 }}>{dlg.meta}</div>
        ) : null}
        <div style={{ display: "flex", flexWrap: "wrap", gap: "8px", marginTop: "14px" }}>
          {/* Every button closes the sheet and posts a toast — matches the
              source's current dialog.buttons mapping (index.html), not the
              older "decorative, CLOSE-only" behavior this port originally
              matched. */}
          {dlg.buttons.map((b, i) => (
            <div
              key={i}
              onClick={() => {
                actions.setDialog(null);
                actions.notify(dlg.title.split(" ").slice(0, 4).join(" ") + " — " + b.label);
              }}
              style={{ ...btnBase, ...(b.primary ? { background: V.pri, color: V.onpri, borderColor: V.pri } : b.dim ? { color: V.ink2 } : null) }}
            >
              {b.label}
            </div>
          ))}
        </div>
      </div>
    </div>
  );
}
