import { useEffect, useRef, useState } from "react";
import { useApp } from "../context/AppContext.jsx";
import { useTheme } from "../context/ThemeContext.jsx";
import { app } from "../linkpoint/app";
import { TEXT_BOX_MARKER } from "../linkpoint/interactions";

// Requests the simulator is waiting on: object dialogs (llDialog), text boxes
// (llTextBox) and teleport offers. Shows the oldest unanswered one as a sheet.
// Nothing appears unless the grid sent it, and the sheet leaves only when the
// request is answered, dismissed, or the session ends.
export default function InteractionDialog() {
  const { actions } = useApp();
  const { V, t } = useTheme();
  const [items, setItems] = useState(() => [...app.interactions.items]);
  const [, setBusyTick] = useState(0);
  const [errors, setErrors] = useState({});
  const [text, setText] = useState("");
  const firstRef = useRef(null);
  const current = items[0] || null;

  useEffect(() => {
    const manager = app.interactions;
    const onChanged = (list) => { setItems([...list]); setBusyTick((n) => n + 1); };
    const onFailed = ({ id, message }) => setErrors((previous) => ({ ...previous, [id]: message }));
    const onAccepted = () => actions.notify("Teleport accepted");
    manager.on("interactions_changed", onChanged);
    manager.on("interaction_failed", onFailed);
    manager.on("lure_accepted", onAccepted);
    setItems([...manager.items]);
    return () => {
      manager.off("interactions_changed", onChanged);
      manager.off("interaction_failed", onFailed);
      manager.off("lure_accepted", onAccepted);
    };
  }, [actions]);

  // A new request starts with an empty text box, and focus moves into it.
  const currentId = current ? current.id : null;
  useEffect(() => {
    setText("");
    if (currentId && firstRef.current) firstRef.current.focus();
  }, [currentId]);

  useEffect(() => {
    if (!currentId) return undefined;
    const onKey = (event) => { if (event.key === "Escape") { void app.interactions.dismiss(currentId); } };
    window.addEventListener("keydown", onKey);
    return () => window.removeEventListener("keydown", onKey);
  }, [currentId]);

  if (!current) return null;

  const busy = app.interactions.isBusy(current.id);
  const error = errors[current.id];
  const waiting = items.length - 1;
  const isLure = current.kind === "lure";
  const isTextBox = !isLure && current.textBox;

  const button = { flex: "1 1 40%", minHeight: 46, display: "flex", alignItems: "center", justifyContent: "center", borderWidth: 1, borderStyle: "solid", borderColor: V.outv, borderRadius: V.rs, background: "transparent", font: `700 11px/1 ${t.font}`, letterSpacing: ".1em", color: V.ink, textAlign: "center", padding: "0 8px", cursor: busy ? "default" : "pointer", opacity: busy ? 0.55 : 1 };
  const primary = { ...button, background: V.pri, color: V.onpri, borderColor: V.pri };
  const dim = { ...button, color: V.ink2 };

  const where = isLure && current.position
    ? `Position ${current.position.map((n) => Math.round(n)).join(", ")}${current.gridX != null && current.gridY != null ? ` · grid ${current.gridX}, ${current.gridY}` : ""}`
    : null;

  const heading = isLure ? `${current.fromName || "A resident"} offers to teleport you` : (current.objectName || "Object");
  const kind = isLure ? "TELEPORT OFFER" : isTextBox ? "TEXT INPUT" : "OBJECT DIALOG";
  const visibleButtons = isLure || isTextBox ? [] : current.buttons.map((label, index) => ({ label, index })).filter((entry) => entry.label !== TEXT_BOX_MARKER);

  // Starting a new attempt clears the previous failure for this request.
  const attempt = (run) => {
    setErrors(({ [current.id]: _cleared, ...rest }) => rest);
    void run();
  };
  const submitText = () => { if (!busy && text.trim()) attempt(() => app.interactions.answerText(current.id, text)); };

  return (
    <div style={{ position: "absolute", inset: 0, zIndex: 70, background: "rgba(0,0,0,.62)", display: "flex", alignItems: "flex-end" }}>
      <div role="alertdialog" aria-modal="true" aria-labelledby="interaction-title" aria-describedby="interaction-body" style={{ width: "100%", background: V.surf, borderTop: `1px solid ${V.pri}`, borderRadius: `${V.rl} ${V.rl} 0 0`, padding: "18px 16px 22px", maxHeight: "86%", overflowY: "auto" }}>
        <div style={{ display: "flex", alignItems: "center", gap: 9, marginBottom: 10 }}>
          <span style={{ flex: 1, font: `600 12px/1 ${t.font}`, letterSpacing: ".2em", color: V.pri }}>{kind}</span>
          {waiting > 0 ? <span style={{ font: `400 11px/1 ${t.font}`, color: V.ink2 }}>{waiting} more waiting</span> : null}
        </div>
        <div id="interaction-title" style={{ font: `600 15px/1.35 ${t.font}`, color: V.ink }}>{heading}</div>
        {!isLure && current.ownerName ? <div style={{ font: `400 11px/1.4 ${t.font}`, color: V.ink2, marginTop: 2 }}>Owned by {current.ownerName}</div> : null}
        <div id="interaction-body" style={{ font: `400 12.5px/1.65 ${t.font}`, color: V.ink2, marginTop: 8, whiteSpace: "pre-wrap", overflowWrap: "anywhere" }}>{current.message || (isLure ? "No message." : "")}</div>
        {where ? <div style={{ marginTop: 10, border: `1px dashed ${V.outv}`, borderRadius: V.rs, padding: 9, font: `400 11px/1.6 ${t.font}`, color: V.ink2 }}>{where}</div> : null}

        {isTextBox ? (
          <form onSubmit={(event) => { event.preventDefault(); submitText(); }} style={{ marginTop: 12 }}>
            <input
              ref={firstRef}
              aria-label="Your reply"
              value={text}
              maxLength={255}
              disabled={busy}
              onChange={(event) => setText(event.target.value)}
              style={{ width: "100%", boxSizing: "border-box", minHeight: 44, padding: "0 10px", border: `1px solid ${V.outv}`, borderRadius: V.rs, background: V.bg, color: V.ink, font: `400 13px/1 ${t.font}` }}
            />
          </form>
        ) : null}

        {error ? <div role="alert" style={{ marginTop: 10, color: V.err, font: `500 11.5px/1.4 ${t.font}` }}>{error}</div> : null}
        {busy ? <div role="status" style={{ marginTop: 10, color: V.ink2, font: `400 11.5px/1.4 ${t.font}` }}>{isLure ? "Teleporting…" : "Waiting for the grid…"}</div> : null}

        <div style={{ display: "flex", flexWrap: "wrap", gap: 8, marginTop: 14 }}>
          {isLure ? (
            <>
              <button type="button" ref={firstRef} disabled={busy} style={primary} onClick={() => attempt(() => app.interactions.acceptLure(current.id))}>ACCEPT</button>
              <button type="button" disabled={busy} style={dim} onClick={() => void app.interactions.dismiss(current.id)}>DISMISS</button>
            </>
          ) : isTextBox ? (
            <>
              <button type="button" disabled={busy || !text.trim()} style={busy || !text.trim() ? { ...primary, opacity: 0.55, cursor: "default" } : primary} onClick={submitText}>SEND</button>
              <button type="button" disabled={busy} style={dim} onClick={() => void app.interactions.dismiss(current.id)}>IGNORE</button>
            </>
          ) : (
            <>
              {visibleButtons.map((entry, position) => (
                <button type="button" key={entry.index} ref={position === 0 ? firstRef : undefined} disabled={busy} style={button} onClick={() => attempt(() => app.interactions.answerButton(current.id, entry.index))}>{entry.label}</button>
              ))}
              <button type="button" ref={visibleButtons.length ? undefined : firstRef} disabled={busy} style={dim} onClick={() => void app.interactions.dismiss(current.id)}>{visibleButtons.length ? "IGNORE" : "OK"}</button>
            </>
          )}
        </div>
        {isLure ? <div style={{ marginTop: 8, font: `400 10.5px/1.4 ${t.font}`, color: V.ink2 }}>Dismissing does not notify the sender.</div> : null}
      </div>
    </div>
  );
}
