import { useEffect, useMemo, useState } from "react";
import { useTheme } from "../context/ThemeContext.jsx";
import Icon from "../components/Icon.jsx";
import { app } from "../linkpoint/app";

export default function Chat() {
  const { V, t } = useTheme();
  const [messages, setMessages] = useState(() => [...app.chat.messages]);
  const [draft, setDraft] = useState("");
  const [sending, setSending] = useState(false);
  const [error, setError] = useState("");
  const connected = app.auth.isLoggedIn();

  useEffect(() => {
    const refresh = () => setMessages([...app.chat.messages]);
    app.chat.on("message_received", refresh);
    app.chat.on("message_sent", refresh);
    return () => {
      app.chat.off("message_received", refresh);
      app.chat.off("message_sent", refresh);
    };
  }, []);

  const formatted = useMemo(() => messages.map((message) => ({
    ...message,
    time: new Date(message.timestamp).toLocaleTimeString([], { hour: "2-digit", minute: "2-digit" }),
  })), [messages]);

  const send = async (event) => {
    event.preventDefault();
    const text = draft.trim();
    if (!text || sending) return;
    if (!connected) return setError("Chat is unavailable while disconnected.");
    setSending(true);
    setError("");
    try {
      await app.chat.sendMessage(text, 0, 1);
      setDraft("");
      setMessages([...app.chat.messages]);
    } catch (reason) {
      setError(reason instanceof Error ? reason.message : "Message could not be sent.");
    } finally {
      setSending(false);
    }
  };

  return <>
    <section aria-label="Local chat transcript" aria-live="polite" style={{ flex: 1, minHeight: 0, overflowY: "auto", padding: 12, display: "flex", flexDirection: "column", gap: 8 }}>
      {!formatted.length && <div style={{ color: V.ink2, margin: "auto", textAlign: "center" }}>No messages in this session.</div>}
      {formatted.map((m) => <article key={m.id} style={{ alignSelf: m.senderId === app.auth.user?.id ? "flex-end" : "flex-start", maxWidth: "88%", padding: "8px 10px", border: `1px solid ${V.outv}`, borderRadius: V.rp, background: V.surf }}>
        <header style={{ color: V.pri, font: `600 10px/1.3 ${t.font}` }}>[{m.time}] {m.sender}</header>
        <div style={{ color: V.ink, font: `400 13px/1.45 ${t.font}`, whiteSpace: "pre-wrap", overflowWrap: "anywhere" }}>{m.text}</div>
      </article>)}
    </section>
    <form onSubmit={send} style={{ padding: 12, borderTop: `1px solid ${V.outv}`, background: V.surf }}>
      {error && <div role="alert" style={{ color: V.err, marginBottom: 7 }}>{error}</div>}
      <div style={{ display: "flex", gap: 8 }}>
        <label htmlFor="local-chat" className="sr-only">Message local chat</label>
        <input id="local-chat" value={draft} onChange={(e) => setDraft(e.target.value)} disabled={!connected || sending} placeholder={connected ? "Say to local…" : "Disconnected"} autoComplete="off" style={{ flex: 1, minWidth: 0, minHeight: 44, padding: "0 12px", border: `1px solid ${V.outv}`, borderRadius: V.rs, background: V.bg, color: V.ink, fontSize: 16 }} />
        <button type="submit" disabled={!connected || !draft.trim() || sending} aria-label="Send local chat" style={{ width: 48, border: 0, borderRadius: V.rs, background: V.pri, color: V.onpri, cursor: "pointer" }}><Icon name="send" size={19} /></button>
      </div>
    </form>
  </>;
}
