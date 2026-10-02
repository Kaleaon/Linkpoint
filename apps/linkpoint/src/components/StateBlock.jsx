import { useApp } from "../context/AppContext.jsx";
import { useTheme } from "../context/ThemeContext.jsx";
import Icon from "./Icon.jsx";
import CrystalLoader from "./CrystalLoader.jsx";

// Ported from the `stateBlock` computation + its <sc-if> template block —
// the shared loading/empty/error UI shown in place of a screen's normal body.
export default function StateBlock() {
  const { state } = useApp();
  const { V, t, condPack, stateBlockActive } = useTheme();
  if (!stateBlockActive || !condPack) return null;
  const isError = state.cond === "error";
  const isLoading = state.cond === "loading";

  return (
    <div
      style={{
        flex: 1,
        minHeight: 0,
        display: "flex",
        flexDirection: "column",
        alignItems: "center",
        justifyContent: "center",
        gap: "15px",
        padding: "34px 26px",
        textAlign: "center",
      }}
    >
      {/* Loading swaps the lucide glyph for the crystal loader; empty/error keep the
          glyph, which carries the specific meaning (inbox, plug-zap, cloud-off, …). */}
      {isLoading ? (
        <CrystalLoader />
      ) : (
        <div
          style={{
            width: "62px",
            height: "62px",
            flex: "none",
            borderRadius: V.rp,
            border: "1px solid " + (isError ? V.err : V.outv),
            background: V.surf,
            display: "flex",
            alignItems: "center",
            justifyContent: "center",
            color: isError ? V.err : V.pri,
          }}
        >
          <Icon name={condPack.icon} size={26} />
        </div>
      )}
      <div style={{ font: "700 15px/1.3 " + t.dfont, letterSpacing: V.tls, color: isError ? V.err : V.ink }}>{condPack.title}</div>
      <div style={{ maxWidth: "300px", font: "400 12px/1.7 " + t.font, color: V.ink2 }}>{condPack.body}</div>
      {condPack.bar ? (
        <div style={{ width: "216px", height: "4px", borderRadius: "2px", background: V.surf2, overflow: "hidden" }}>
          <div style={{ width: Math.round(condPack.bar * 100) + "%", height: "100%", background: V.pri }} />
        </div>
      ) : null}
      {condPack.log ? (
        <div style={{ font: "400 10.5px/1.75 " + t.font, color: V.ink2, textAlign: "left", border: "1px dashed " + V.outv, borderRadius: V.rs, padding: "8px 10px", maxWidth: "300px" }}>
          {condPack.log.map((ln, i) => (
            <div key={i}>{ln}</div>
          ))}
        </div>
      ) : null}
      {condPack.btn ? (
        <div
          style={{
            minHeight: "44px",
            padding: "0 22px",
            display: "inline-flex",
            alignItems: "center",
            justifyContent: "center",
            borderRadius: V.rs,
            background: isError ? V.err : V.pri,
            color: isError ? "#FFFFFF" : V.onpri,
            font: "700 11px/1 " + t.font,
            letterSpacing: ".18em",
            cursor: "pointer",
          }}
        >
          {condPack.btn}
        </div>
      ) : null}
    </div>
  );
}
