import { useTheme } from "../context/ThemeContext.jsx";
import { cardLooks, cardAccentStyle, actionButtonStyle } from "../theme/look.js";
import Icon from "./Icon.jsx";
import Toggle from "./Toggle.jsx";

// Ported from the CARDS.map(...) block + the `isCards` template section
// (sc-if value="{{ isCards }}"). One card renders: icon+title+right/badge/
// toggle header row, optional body text, optional big stat, optional action
// row.
export default function Card({ c }) {
  const { V, t, LK, pad } = useTheme();
  const looks = cardLooks(V, pad);

  // A `sect` entry is a group heading inside the list, not a card: it drops the
  // card chrome and prints a tracked label, so a long preferences screen reads as
  // sections instead of thirty identical boxes.
  if (c.sect) {
    return (
      <div style={{ border: "none", borderRadius: 0, background: "transparent", padding: LK.card === "flat" ? "18px 16px 2px" : "18px 2px 2px" }}>
        <span style={{ font: "700 10px/1.4 " + t.font, letterSpacing: ".26em", color: V.ink2, textTransform: "uppercase" }}>{c.title}</span>
      </div>
    );
  }

  const style = { ...(looks[LK.card] || looks.box), ...cardAccentStyle(LK.card, V, c.accent ? V[c.accent] : null) };
  const meterPct = c.meter == null ? null : Math.max(1, Math.min(100, Math.round(c.meter * 100)));

  return (
    <div style={style}>
      <div style={{ display: "flex", alignItems: "center", gap: "9px" }}>
        {c.icon && <Icon name={c.icon} size={16} style={{ color: V.pri }} />}
        <span style={{ flex: 1, font: "600 13px/1.25 " + t.font, color: V.ink }}>{c.title}</span>
        {c.right != null && c.right !== "" && <span style={{ font: "400 10.5px/1 " + t.font, color: V.ink2 }}>{c.right}</span>}
        {c.rights && c.rights.length ? (
          <span style={{ display: "flex", gap: "4px", flex: "none" }}>
            {c.rights.map((r) => (
              <Icon key={r} name={r} size={13} style={{ color: V.sec2 }} />
            ))}
          </span>
        ) : null}
        {c.badge ? (
          <span
            style={{
              minWidth: "20px",
              height: "20px",
              padding: "0 6px",
              borderRadius: "10px",
              background: V.bdg,
              color: V.onbdg,
              display: "flex",
              alignItems: "center",
              justifyContent: "center",
              font: "700 10px/1 " + t.font,
            }}
          >
            {c.badge}
          </span>
        ) : null}
        {c.toggle ? <Toggle on={c.on} onClick={c.togglePick} /> : null}
        {c.select ? (
          <select
            value={c.value}
            onChange={(e) => {
              // <option value> is always a string, so hand the card back the
              // option it actually chose rather than the stringified value —
              // numeric prefs (cache size in MB) would otherwise come back as text.
              const opt = (c.options || []).find((o) => String(o.value) === e.target.value);
              c.onChange(opt ? opt.value : e.target.value);
            }}
            style={{
              marginLeft: "10px", background: V.surf2, color: V.ink, border: "1px solid " + V.outv,
              borderRadius: V.rs, padding: "4px 8px", font: "400 12px/1 " + t.font,
              maxWidth: "150px", textOverflow: "ellipsis", whiteSpace: "nowrap", overflow: "hidden",
            }}
          >
            {(c.options || []).map((o) => (
              <option key={String(o.value)} value={o.value}>
                {o.label}
              </option>
            ))}
          </select>
        ) : null}
      </div>
      {c.body ? <div style={{ font: "400 12px/1.6 " + t.font, color: V.ink2, marginTop: "8px" }}>{c.body}</div> : null}
      {c.big ? <div style={{ font: "700 46px/1 " + t.font, color: c.tone ? V[c.tone] : V.ok, marginTop: "6px" }}>{c.big}</div> : null}
      {meterPct != null ? (
        <div style={{ height: "4px", borderRadius: "2px", background: V.surf2, overflow: "hidden", marginTop: "10px" }}>
          <div style={{ width: meterPct + "%", height: "100%", background: c.meter > 0.9 ? V.err : c.meter > 0.7 ? V.sec2 : V.pri, transition: "width .2s ease" }} />
        </div>
      ) : null}
      {c.actions && c.actions.length ? (
        <div style={{ display: "flex", gap: "8px", marginTop: "11px" }}>
          {c.actions.map((a, i) => (
            <div key={i} onClick={a.pick} style={actionButtonStyle(V, t.font, a)}>
              {a.label}
            </div>
          ))}
        </div>
      ) : null}
    </div>
  );
}
