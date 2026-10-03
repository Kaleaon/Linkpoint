import { useEffect, useRef, useState } from "react";
import { useApp } from "../context/AppContext.jsx";
import { useTheme } from "../context/ThemeContext.jsx";
import { app } from "../linkpoint/app";
import { TEXT_BOX_MARKER } from "../linkpoint/interactions";
import FocusTrap from "./FocusTrap.jsx";

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
  const [userBalance, setUserBalance] = useState(() => app.protocol.balance);
  const firstRef = useRef(null);
  const current = items[0] || null;

  useEffect(() => {
    const manager = app.interactions;
    const protocol = app.protocol;
    const onChanged = (list) => { setItems([...list]); setBusyTick((n) => n + 1); };
    const onFailed = ({ id, message }) => setErrors((previous) => ({ ...previous, [id]: message }));
    const onAccepted = () => actions.notify("Teleport accepted");
    const onInventoryAccepted = () => actions.notify("Inventory offer accepted");
    const onGroupInviteAccepted = () => actions.notify("Joined group successfully");
    const onPaymentCompleted = ({ message }) => { if (message) actions.notify(message); };
    const onBalanceUpdated = (balance) => setUserBalance(balance);

    manager.on("interactions_changed", onChanged);
    manager.on("interaction_failed", onFailed);
    manager.on("lure_accepted", onAccepted);
    manager.on("inventory_offer_accepted", onInventoryAccepted);
    manager.on("group_invite_accepted", onGroupInviteAccepted);
    manager.on("payment_completed", onPaymentCompleted);
    protocol.on("balance_updated", onBalanceUpdated);

    setItems([...manager.items]);
    setUserBalance(protocol.balance);
    void protocol.refreshBalance();

    return () => {
      manager.off("interactions_changed", onChanged);
      manager.off("interaction_failed", onFailed);
      manager.off("lure_accepted", onAccepted);
      manager.off("inventory_offer_accepted", onInventoryAccepted);
      manager.off("group_invite_accepted", onGroupInviteAccepted);
      manager.off("payment_completed", onPaymentCompleted);
      protocol.off("balance_updated", onBalanceUpdated);
    };
  }, [actions]);

  // A new request starts with an empty text box, and focus moves into it.
  const currentId = current ? current.id : null;
  useEffect(() => {
    setText("");
    if (currentId) {
      if (app.interactions.current?.kind === "payment") {
        void app.protocol.refreshBalance();
      }
      if (firstRef.current) firstRef.current.focus();
    }
  }, [currentId]);

  if (!current) return null;

  const busy = app.interactions.isBusy(current.id);
  const error = errors[current.id];
  const waiting = items.length - 1;
  const isLure = current.kind === "lure";
  const isPayment = current.kind === "payment";
  const isInventoryOffer = current.kind === "inventory-offer";
  const isGroupInvite = current.kind === "group-invite";
  const isTextBox = !isLure && !isPayment && !isInventoryOffer && !isGroupInvite && current.textBox;

  const button = { flex: "1 1 40%", minHeight: 46, display: "flex", alignItems: "center", justifyContent: "center", borderWidth: 1, borderStyle: "solid", borderColor: V.outv, borderRadius: V.rs, background: "transparent", font: `700 11px/1 ${t.font}`, letterSpacing: ".1em", color: V.ink, textAlign: "center", padding: "0 8px", cursor: busy ? "default" : "pointer", opacity: busy ? 0.55 : 1 };
  const primary = { ...button, background: V.pri, color: V.onpri, borderColor: V.pri };
  const dim = { ...button, color: V.ink2 };

  const where = isLure && current.position
    ? `Position ${current.position.map((n) => Math.round(n)).join(", ")}${current.gridX != null && current.gridY != null ? ` · grid ${current.gridX}, ${current.gridY}` : ""}`
    : null;

  const heading = isLure
    ? `${current.fromName || "A resident"} offers to teleport you`
    : isPayment
    ? current.objectName || "Vendor Item"
    : isInventoryOffer
    ? `${current.senderName || current.fromName || "A resident"} offered you ${current.itemName || "an item"}`
    : isGroupInvite
    ? `Invitation to join ${current.groupName || "Group"}`
    : (current.objectName || "Object");

  const kind = isLure
    ? "TELEPORT OFFER"
    : isPayment
    ? "PAYMENT CONFIRMATION"
    : isInventoryOffer
    ? "INVENTORY OFFER"
    : isGroupInvite
    ? "GROUP INVITATION"
    : isTextBox
    ? "TEXT INPUT"
    : "OBJECT DIALOG";

  const visibleButtons = isLure || isPayment || isInventoryOffer || isGroupInvite || isTextBox ? [] : current.buttons.map((label, index) => ({ label, index })).filter((entry) => entry.label !== TEXT_BOX_MARKER);

  const insufficient = isPayment && userBalance !== null && userBalance < current.price;

  // Starting a new attempt clears the previous failure for this request.
  const attempt = (run) => {
    setErrors(({ [current.id]: _cleared, ...rest }) => rest);
    void run();
  };
  const submitText = () => { if (!busy && text.trim()) attempt(() => app.interactions.answerText(current.id, text)); };

  return (
    <FocusTrap
      active={!!current}
      onEscape={() => { if (currentId) void app.interactions.dismiss(currentId); }}
      onClick={(e) => e.stopPropagation()}
      style={{ position: "absolute", inset: 0, zIndex: 100, background: "rgba(0,0,0,.75)", backdropFilter: "blur(4px)", display: "flex", alignItems: "flex-end", touchAction: "none", pointerEvents: "auto" }}
    >
      <div role="alertdialog" aria-modal="true" aria-labelledby="interaction-title" aria-describedby="interaction-body" style={{ width: "100%", background: V.surf, borderTop: `1px solid ${V.pri}`, borderRadius: `${V.rl} ${V.rl} 0 0`, padding: "18px 16px 22px", maxHeight: "86%", overflowY: "auto" }}>
        <div style={{ display: "flex", alignItems: "center", gap: 9, marginBottom: 10 }}>
          <span style={{ flex: 1, font: `600 12px/1 ${t.font}`, letterSpacing: ".2em", color: V.pri }}>{kind}</span>
          {waiting > 0 ? <span style={{ font: `400 11px/1 ${t.font}`, color: V.ink2 }}>{waiting} more waiting</span> : null}
        </div>
        <div id="interaction-title" style={{ font: `600 15px/1.35 ${t.font}`, color: V.ink }}>{heading}</div>
        {isPayment ? (
          <div style={{ marginTop: 2, font: `400 11.5px/1.4 ${t.font}`, color: V.ink2 }}>
            Seller: <strong style={{ color: V.ink }}>{current.sellerName || "Simulator Resident"}</strong>
          </div>
        ) : (!isLure && !isInventoryOffer && !isGroupInvite && current.ownerName) ? (
          <div style={{ font: `400 11px/1.4 ${t.font}`, color: V.ink2, marginTop: 2 }}>Owned by {current.ownerName}</div>
        ) : null}

        {isInventoryOffer ? (
          <div id="interaction-body" style={{ marginTop: 12, padding: 12, background: V.bg, border: `1px solid ${V.outv}`, borderRadius: V.rs }}>
            <div style={{ font: `400 11.5px/1.4 ${t.font}`, color: V.ink2, marginBottom: 6 }}>
              Sender: <strong style={{ color: V.ink }}>{current.senderName || current.fromName || "Resident"}</strong>
            </div>
            <div style={{ font: `400 11.5px/1.4 ${t.font}`, color: V.ink2, marginBottom: 6 }}>
              Item: <strong style={{ color: V.ink }}>{current.itemName || "Inventory Item"}</strong>
            </div>
            <div style={{ font: `400 11.5px/1.4 ${t.font}`, color: V.ink2 }}>
              Asset Type: <strong style={{ color: V.ink }}>{current.assetType}</strong>
            </div>
          </div>
        ) : isGroupInvite ? (
          <div id="interaction-body" style={{ marginTop: 12, padding: 12, background: V.bg, border: `1px solid ${V.outv}`, borderRadius: V.rs }}>
            <div style={{ font: `400 11.5px/1.4 ${t.font}`, color: V.ink2, marginBottom: 6 }}>
              Group: <strong style={{ color: V.ink }}>{current.groupName || "Group"}</strong>
            </div>
            <div style={{ font: `400 11.5px/1.4 ${t.font}`, color: V.ink2, marginBottom: 6 }}>
              Invited by: <strong style={{ color: V.ink }}>{current.senderName || current.fromName || "Resident"}</strong>
            </div>
            <div style={{ font: `400 11.5px/1.4 ${t.font}`, color: V.ink2 }}>
              Join Fee: <strong style={{ color: V.ink }}>{current.fee > 0 ? `L$ ${current.fee}` : "Free"}</strong>
            </div>
          </div>
        ) : isPayment ? (
          <div id="interaction-body" style={{ marginTop: 12, padding: 12, background: V.bg, border: `1px solid ${V.outv}`, borderRadius: V.rs }}>
            <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center", marginBottom: 8 }}>
              <span style={{ font: `500 12px/1 ${t.font}`, color: V.ink2 }}>Price:</span>
              <span style={{ font: `700 18px/1 ${t.font}`, color: V.pri }}>{current.currency || "L$"} {current.price?.toLocaleString?.() ?? current.price}</span>
            </div>
            <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center", paddingTop: 8, borderTop: `1px border-style ${V.outv}` }}>
              <span style={{ font: `500 12px/1 ${t.font}`, color: V.ink2 }}>Your Balance:</span>
              <span style={{ font: `600 13px/1 ${t.font}`, color: insufficient ? V.err : V.ink }}>
                {userBalance !== null ? `L$ ${userBalance.toLocaleString()}` : "Refreshing…"}
              </span>
            </div>
          </div>
        ) : (
          <div id="interaction-body" style={{ font: `400 12.5px/1.65 ${t.font}`, color: V.ink2, marginTop: 8, whiteSpace: "pre-wrap", overflowWrap: "anywhere" }}>{current.message || (isLure ? "No message." : "")}</div>
        )}

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

        {insufficient ? (
          <div role="alert" style={{ marginTop: 10, padding: 10, background: "rgba(255, 60, 60, 0.12)", border: `1px solid ${V.err}`, borderRadius: V.rs, color: V.err, font: `600 11.5px/1.4 ${t.font}` }}>
            Insufficient Funds: This purchase requires L$ {current.price}, but your available balance is L$ {userBalance ?? 0}.
          </div>
        ) : null}

        {error ? <div role="alert" style={{ marginTop: 10, color: V.err, font: `500 11.5px/1.4 ${t.font}` }}>{error}</div> : null}
        {busy ? <div role="status" style={{ marginTop: 10, color: V.ink2, font: `400 11.5px/1.4 ${t.font}` }}>{isLure ? "Teleporting…" : isPayment ? "Dispatching payment packet…" : "Waiting for the grid…"}</div> : null}

        <div style={{ display: "flex", flexWrap: "wrap", gap: 8, marginTop: 14 }}>
          {isPayment ? (
            <>
              <button
                type="button"
                ref={firstRef}
                disabled={busy || insufficient}
                style={busy || insufficient ? { ...primary, opacity: 0.55, cursor: "default" } : primary}
                onClick={() => attempt(() => app.interactions.confirmPayment(current.id))}
              >
                {insufficient ? "INSUFFICIENT FUNDS" : `CONFIRM (L$ ${current.price})`}
              </button>
              <button type="button" disabled={busy} style={dim} onClick={() => void app.interactions.cancelPayment(current.id)}>
                CANCEL
              </button>
            </>
          ) : isLure ? (
            <>
              <button type="button" ref={firstRef} disabled={busy} style={primary} onClick={() => attempt(() => app.interactions.acceptLure(current.id))}>ACCEPT</button>
              <button type="button" disabled={busy} style={dim} onClick={() => void app.interactions.dismiss(current.id)}>DISMISS</button>
            </>
          ) : isInventoryOffer ? (
            <>
              <button type="button" ref={firstRef} disabled={busy} style={primary} onClick={() => attempt(() => app.interactions.acceptInventoryOffer(current.id))}>ACCEPT</button>
              <button type="button" disabled={busy} style={dim} onClick={() => attempt(() => app.interactions.declineInventoryOffer(current.id))}>DECLINE</button>
            </>
          ) : isGroupInvite ? (
            <>
              <button type="button" ref={firstRef} disabled={busy} style={primary} onClick={() => attempt(() => app.interactions.acceptGroupInvite(current.id))}>JOIN GROUP</button>
              <button type="button" disabled={busy} style={dim} onClick={() => attempt(() => app.interactions.declineGroupInvite(current.id))}>DECLINE</button>
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
        {isPayment ? <div style={{ marginTop: 8, font: `400 10.5px/1.4 ${t.font}`, color: V.ink2 }}>Payment confirmation prevents accidental currency transfer.</div> : null}
      </div>
    </FocusTrap>
  );
}

