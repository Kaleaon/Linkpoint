import { useApp } from "../context/AppContext.jsx";
import { LAYOUTS } from "../theme/layouts.js";
import { PALETTES, FAMILIES } from "../theme/palettes.js";
import { DEVICES, SCREENS } from "../theme/constants.js";
import { DIALOGS } from "../theme/dialogs.js";
import ThemeStudio from "./ThemeStudio.jsx";

// Ported from the "1a" picker column (`layoutList`/`paletteFams`/`deviceList`/
// `screenList`/`condList`/`dialogList`) — the controls used to drive every
// palette/layout/device/screen/condition/dialog combination.
export default function ControlPanels() {
  const { state, actions } = useApp();

  return (
    <aside className="sidepanels" aria-label="Prototype controls">
      <div className="pnl">
        <div className="pnlh">LAYOUT PACK</div>
        <div style={{ display: "flex", flexDirection: "column", gap: "4px" }}>
          {Object.entries(LAYOUTS).map(([k, x]) => (
            <button key={k} className="pick" aria-pressed={state.layout === k} onClick={() => actions.setLayout(k)}>
              <span
                style={{
                  width: "22px", height: "22px", flex: "none", borderRadius: x.s.rs === "999px" ? "11px 2px 11px 2px" : x.s.rs,
                  border: "1px solid " + (state.layout === k ? "#6CFF9A" : "rgba(255,255,255,.3)"), background: state.layout === k ? "#6CFF9A" : "rgba(255,255,255,.08)",
                }}
              />
              <span style={{ font: "500 11.5px/1.3 'JetBrains Mono',monospace", color: "rgba(255,255,255,.85)", flex: 1 }}>{x.name}</span>
              <span style={{ font: "600 9px/1 'JetBrains Mono',monospace", letterSpacing: ".14em", color: "rgba(255,255,255,.35)" }}>{x.nav}</span>
            </button>
          ))}
        </div>

        <div className="pnlh" style={{ margin: "16px 0 10px" }}>
          COLOUR PACK
        </div>
        <div style={{ display: "flex", flexDirection: "column", gap: "4px" }}>
          {FAMILIES.map((fm) => (
            <div key={fm.name}>
              <div style={{ font: "600 9px/1 'JetBrains Mono',monospace", letterSpacing: ".18em", color: "rgba(255,255,255,.4)", margin: "9px 0 3px" }}>{fm.name}</div>
              {fm.keys
                .filter((k) => PALETTES[k])
                .map((k) => {
                  const x = PALETTES[k];
                  return (
                    <button key={k} className="pick" aria-pressed={state.palette === k} onClick={() => actions.setPalette(k)}>
                      <span
                        style={{
                          width: "34px", height: "18px", flex: "none", display: "flex", overflow: "hidden", borderRadius: "3px",
                          outline: state.palette === k ? "2px solid " + x.c.pri : "1px solid rgba(255,255,255,.2)", outlineOffset: "1px",
                        }}
                      >
                        <span style={{ flex: 1, background: x.c.bg }} />
                        <span style={{ flex: 1, background: x.c.pri }} />
                        <span style={{ flex: 1, background: x.c.sec2 }} />
                      </span>
                      <span style={{ font: "500 11.5px/1.3 'JetBrains Mono',monospace", color: "rgba(255,255,255,.85)", flex: 1 }}>{x.name}</span>
                    </button>
                  );
                })}
            </div>
          ))}
        </div>
        <ThemeStudio />
      </div>

      <div className="pnl">
        <div className="pnlh">DEVICE</div>
        <div style={{ display: "flex", flexDirection: "column", gap: "4px" }}>
          {Object.entries(DEVICES).map(([k, x]) => (
            <button key={k} className="pick" aria-pressed={state.device === k} onClick={() => actions.setDevice(k)}>
              <span style={{ font: "500 11.5px/1.3 'JetBrains Mono',monospace", color: "rgba(255,255,255,.85)", flex: 1 }}>{x.name}</span>
              <span style={{ font: "400 9.5px/1 'JetBrains Mono',monospace", color: "rgba(255,255,255,.35)" }}>{x.dims}</span>
            </button>
          ))}
        </div>
        <div style={{ display: "flex", gap: "6px", marginTop: "12px" }}>
          <button className="tag" aria-pressed={!state.dense} style={{ flex: 1, textAlign: "center" }} onClick={() => actions.setDense(false)}>
            COMFORTABLE
          </button>
          <button className="tag" aria-pressed={state.dense} style={{ flex: 1, textAlign: "center" }} onClick={() => actions.setDense(true)}>
            COMPACT
          </button>
        </div>
      </div>

      <div className="pnl">
        <div className="pnlh">SCREEN</div>
        <div style={{ display: "flex", flexWrap: "wrap", gap: "5px" }}>
          {SCREENS.map((s) => {
            const active = state.screen === s;
            return (
              <button
                key={s}
                className="tag"
                aria-pressed={active}
                onClick={() => actions.setScreen(s)}
                style={
                  active
                    ? { borderColor: "#6CFF9A", color: "#6CFF9A", background: "rgba(108,255,154,.12)" }
                    : undefined
                }
              >
                {s}
              </button>
            );
          })}
        </div>
        <div className="pnlh" style={{ margin: "16px 0 10px" }}>
          SCREEN STATE
        </div>
        <div style={{ display: "flex", flexWrap: "wrap", gap: "5px" }}>
          {[
            ["normal", "NORMAL"],
            ["loading", "LOADING"],
            ["empty", "EMPTY"],
            ["error", "ERROR"],
          ].map(([k, n]) => (
            <button
              key={k}
              className="tag"
              aria-pressed={state.cond === k}
              onClick={() => actions.setCond(k)}
              style={{ borderColor: state.cond === k ? "#6CFF9A" : "rgba(255,255,255,.18)", color: state.cond === k ? "#6CFF9A" : "rgba(255,255,255,.6)", borderRadius: "4px", padding: "6px 9px", font: "600 9.5px/1 'JetBrains Mono',monospace", letterSpacing: ".12em", cursor: "pointer" }}
            >
              {n}
            </button>
          ))}
        </div>
        <div className="pnlh" style={{ margin: "16px 0 10px" }}>
          SL SYSTEM MOMENTS
        </div>
        <div style={{ display: "flex", flexWrap: "wrap", gap: "5px" }}>
          {Object.keys(DIALOGS).map((n) => (
            <button key={n} className="tag" style={{ borderStyle: "dashed" }} onClick={() => actions.setDialog(n)}>
              {n}
            </button>
          ))}
        </div>
      </div>
    </aside>
  );
}
