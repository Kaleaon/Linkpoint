import { useEffect, useMemo, useRef, useState } from "react";
import { useTheme } from "../context/ThemeContext.jsx";
import { useApp } from "../context/AppContext.jsx";
import Icon from "../components/Icon.jsx";
import { app } from "../linkpoint/app";

export default function Chat() {
  const { V, t } = useTheme();
  const { state, actions } = useApp();
  const [messages, setMessages] = useState(() => [...app.chat.messages]);
  const [draft, setDraft] = useState("");
  const [sending, setSending] = useState(false);
  const [error, setError] = useState("");
  const messagesEndRef = useRef(null);
  const connected = app.auth.isLoggedIn();

  // Auto-Reply / Away Message state
  const [autoReplyEnabled, setAutoReplyEnabled] = useState(() => app.chat.isAutoReplyEnabled());
  const [awayMessage, setAwayMessage] = useState(() => app.chat.getAwayMessage());
  const [showAwaySettings, setShowAwaySettings] = useState(false);
  const [savedNotice, setSavedNotice] = useState(false);

  // Active sub-tab from navigation (LOCAL, IM, GROUP)
  const activeTab = state.tabs?.Chat || "LOCAL";

  // Selected contact for IMs
  const [selectedContact, setSelectedContact] = useState(() => state.chip || "");

  // Update selected contact if chip changes
  useEffect(() => {
    if (state.chip) {
      setSelectedContact(state.chip);
    }
  }, [state.chip]);

  useEffect(() => {
    const refresh = () => setMessages([...app.chat.messages]);
    const onAutoReplyChange = (cfg) => {
      setAutoReplyEnabled(cfg.enabled);
      setAwayMessage(cfg.awayMessage);
    };

    app.chat.on("message_received", refresh);
    app.chat.on("message_sent", refresh);
    app.chat.on("auto_reply_changed", onAutoReplyChange);

    return () => {
      app.chat.off("message_received", refresh);
      app.chat.off("message_sent", refresh);
      app.chat.off("auto_reply_changed", onAutoReplyChange);
    };
  }, []);

  useEffect(() => {
    messagesEndRef.current?.scrollIntoView({ behavior: "smooth" });
  }, [messages, activeTab, selectedContact]);

  // Friends and IM Threads for Contact Picker
  const [friends, setFriends] = useState(() => app.friends.getFriends());
  useEffect(() => {
    const refreshFriends = () => setFriends(app.friends.getFriends());
    app.friends.on("friend_added", refreshFriends);
    app.friends.on("friend_updated", refreshFriends);
    app.friends.on("friend_removed", refreshFriends);
    return () => {
      app.friends.off("friend_added", refreshFriends);
      app.friends.off("friend_updated", refreshFriends);
      app.friends.off("friend_removed", refreshFriends);
    };
  }, []);
  const imThreads = useMemo(() => app.chat.getIMThreads(), [messages]);

  // Groups for the group picker: known group records plus any group that has already spoken to us.
  const [knownGroups, setKnownGroups] = useState(() => app.groups.getGroups());
  useEffect(() => {
    if (activeTab !== "GROUP") return undefined;
    let cancelled = false;
    Promise.resolve(app.loadGroups())
      .then((list) => { if (!cancelled) setKnownGroups(Array.isArray(list) ? list : app.groups.getGroups()); })
      .catch(() => {});
    return () => { cancelled = true; };
  }, [activeTab, connected]);
  const groupOptions = useMemo(() => {
    const byId = new Map(knownGroups.filter((g) => g?.id).map((g) => [g.id, { id: g.id, name: g.name || "Group" }]));
    for (const m of messages) {
      if (m.type === "group" && m.groupId && !byId.has(m.groupId)) byId.set(m.groupId, { id: m.groupId, name: m.groupName || "Group" });
    }
    return Array.from(byId.values());
  }, [knownGroups, messages]);
  const [selectedGroup, setSelectedGroup] = useState("");
  useEffect(() => {
    if (!selectedGroup && groupOptions.length === 1) setSelectedGroup(groupOptions[0].id);
  }, [groupOptions, selectedGroup]);

  // Filter messages based on activeTab
  const visibleMessages = useMemo(() => {
    const myId = app.auth.user?.id;
    return messages.filter((m) => {
      if (activeTab === "LOCAL") {
        return m.type !== "im" && m.type !== "group";
      }
      if (activeTab === "IM") {
        if (m.type !== "im") return false;
        if (!selectedContact) return true; // Show all IMs if no contact picked
        const targetClean = selectedContact.toLowerCase().trim();
        const matchesSender = (m.sender || "").toLowerCase().trim() === targetClean || m.senderId === selectedContact;
        const matchesRecipient = (m.recipientName || "").toLowerCase().trim() === targetClean || m.recipientId === selectedContact;
        return matchesSender || matchesRecipient;
      }
      if (activeTab === "GROUP") {
        return m.type === "group";
      }
      return true;
    });
  }, [messages, activeTab, selectedContact]);

  const formatted = useMemo(() => visibleMessages.map((message) => ({
    ...message,
    time: new Date(message.timestamp).toLocaleTimeString([], { hour: "2-digit", minute: "2-digit" }),
  })), [visibleMessages]);

  const handleToggleAutoReply = () => {
    const next = !autoReplyEnabled;
    app.chat.setAutoReplyEnabled(next);
    setAutoReplyEnabled(next);
  };

  const handleSaveAwayMessage = (e) => {
    e.preventDefault();
    app.chat.setAwayMessage(awayMessage);
    setSavedNotice(true);
    setTimeout(() => setSavedNotice(false), 2000);
  };

  const handleResetRecipients = () => {
    app.chat.clearAutoReplyRecipients();
    setSavedNotice(true);
    setTimeout(() => setSavedNotice(false), 2000);
  };

  const send = async (event) => {
    event.preventDefault();
    const text = draft.trim();
    if (!text || sending) return;
    if (!connected) return setError("Chat is unavailable while disconnected.");
    setSending(true);
    setError("");

    try {
      if (activeTab === "IM") {
        // Resolve recipient ID
        let targetId = selectedContact;
        let targetName = selectedContact;
        const matchedFriend = friends.find(
          (f) => f.id === selectedContact || f.name?.toLowerCase() === selectedContact.toLowerCase()
        );
        if (matchedFriend) {
          targetId = matchedFriend.id;
          targetName = matchedFriend.name;
        } else {
          const matchedThread = imThreads.find(
            (t) => t.contactId === selectedContact || t.contactName?.toLowerCase() === selectedContact.toLowerCase()
          );
          if (matchedThread) {
            targetId = matchedThread.contactId;
            targetName = matchedThread.contactName;
          }
        }

        if (!targetId) {
          throw new Error("Please select a friend or recipient to send an Instant Message.");
        }

        await app.chat.sendInstantMessage(targetId, text, targetName);
      } else if (activeTab === "GROUP") {
        const group = groupOptions.find((g) => g.id === selectedGroup);
        if (!group) throw new Error("Select a group above before sending a group message.");
        await app.chat.sendGroupMessage(group.id, text, group.name);
      } else {
        await app.chat.sendMessage(text, 0, 1);
      }

      setDraft("");
      setMessages([...app.chat.messages]);
    } catch (reason) {
      setError(reason instanceof Error ? reason.message : "Message could not be sent.");
    } finally {
      setSending(false);
    }
  };

  return (
    <div style={{ flex: 1, minHeight: 0, display: "flex", flexDirection: "column", background: V.bg }}>
      {/* Auto-Reply / Away Message Controls Header */}
      <div style={{ padding: "8px 12px", background: V.surf, borderBottom: `1px solid ${V.outv}`, display: "flex", flexDirection: "column", gap: 6 }}>
        <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center", gap: 8 }}>
          <div style={{ display: "flex", alignItems: "center", gap: 8 }}>
            <span style={{ fontSize: "11px", fontWeight: 700, color: autoReplyEnabled ? "#eab308" : V.ink2, display: "flex", alignItems: "center", gap: 4 }}>
              <Icon name="clock" size={13} />
              AWAY AUTO-REPLY: {autoReplyEnabled ? "ON" : "OFF"}
            </span>
            {autoReplyEnabled && (
              <span style={{ fontSize: "10px", padding: "1px 6px", borderRadius: V.rs, background: "rgba(234, 179, 8, 0.15)", color: "#eab308", border: "1px solid rgba(234, 179, 8, 0.3)" }}>
                ACTIVE
              </span>
            )}
          </div>
          <div style={{ display: "flex", gap: 6 }}>
            <button
              type="button"
              onClick={handleToggleAutoReply}
              style={{
                padding: "3px 8px",
                fontSize: "10px",
                fontWeight: 700,
                background: autoReplyEnabled ? "#eab308" : V.bg,
                color: autoReplyEnabled ? "#000" : V.ink,
                border: `1px solid ${V.outv}`,
                borderRadius: V.rs,
                cursor: "pointer",
              }}
              title="Toggle Auto-Reply on incoming IMs"
            >
              {autoReplyEnabled ? "DISABLE AWAY" : "SET AWAY"}
            </button>
            <button
              type="button"
              onClick={() => setShowAwaySettings(!showAwaySettings)}
              style={{
                padding: "3px 8px",
                fontSize: "10px",
                background: V.bg,
                color: V.pri,
                border: `1px solid ${V.outv}`,
                borderRadius: V.rs,
                cursor: "pointer",
              }}
              title="Configure custom away message"
            >
              {showAwaySettings ? "HIDE AWAY CONFIG" : "CONFIG AWAY MSG"}
            </button>
          </div>
        </div>

        {/* Collapsible Away Message Configuration */}
        {showAwaySettings && (
          <form onSubmit={handleSaveAwayMessage} style={{ marginTop: 4, display: "flex", flexDirection: "column", gap: 6, padding: "8px", background: V.bg, borderRadius: V.rs, border: `1px solid ${V.outv}` }}>
            <label style={{ fontSize: "11px", fontWeight: 600, color: V.ink }}>Custom 'Away' Message for incoming IMs:</label>
            <textarea
              value={awayMessage}
              onChange={(e) => setAwayMessage(e.target.value)}
              rows={2}
              placeholder="Enter custom away message..."
              style={{
                width: "100%",
                padding: "6px 8px",
                fontSize: "12px",
                fontFamily: t.font,
                background: V.surf,
                color: V.ink,
                border: `1px solid ${V.outv}`,
                borderRadius: V.rs,
                resize: "vertical",
              }}
            />
            <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center" }}>
              <div style={{ display: "flex", gap: 6 }}>
                <button
                  type="submit"
                  style={{
                    padding: "4px 10px",
                    fontSize: "11px",
                    fontWeight: 700,
                    background: V.pri,
                    color: V.onpri,
                    border: 0,
                    borderRadius: V.rs,
                    cursor: "pointer",
                  }}
                >
                  SAVE AWAY MESSAGE
                </button>
                <button
                  type="button"
                  onClick={handleResetRecipients}
                  style={{
                    padding: "4px 8px",
                    fontSize: "10px",
                    background: V.surf,
                    color: V.ink2,
                    border: `1px solid ${V.outv}`,
                    borderRadius: V.rs,
                    cursor: "pointer",
                  }}
                  title="Clear already-replied contact memory so they receive the notice again"
                >
                  CLEAR REPLIED CONTACTS
                </button>
              </div>
              {savedNotice && <span style={{ fontSize: "10px", color: V.pri, fontWeight: 600 }}>SAVED!</span>}
            </div>
          </form>
        )}

        {/* Group Selector (When GROUP tab is active) */}
        {activeTab === "GROUP" && (
          <div style={{ display: "flex", alignItems: "center", gap: 6, overflowX: "auto", paddingBottom: 2 }}>
            <span style={{ fontSize: "10px", fontWeight: 700, color: V.ink2, flexShrink: 0 }}>GROUP:</span>
            {groupOptions.length === 0 && (
              <span style={{ fontSize: "10px", color: V.ink2 }}>No groups available on this connection.</span>
            )}
            {groupOptions.map((g) => {
              const isSelected = selectedGroup === g.id;
              return (
                <button
                  key={g.id}
                  type="button"
                  aria-pressed={isSelected}
                  onClick={() => setSelectedGroup(g.id)}
                  style={{
                    padding: "2px 8px",
                    fontSize: "10px",
                    borderRadius: V.rs,
                    borderWidth: 1,
                    borderStyle: "solid",
                    borderColor: isSelected ? V.pri : V.outv,
                    background: isSelected ? V.pri : V.bg,
                    color: isSelected ? V.onpri : V.ink,
                    cursor: "pointer",
                    flexShrink: 0,
                  }}
                >
                  {g.name}
                </button>
              );
            })}
          </div>
        )}

        {/* Instant Messenger Contact Selector (When IM tab is active) */}
        {activeTab === "IM" && (
          <div style={{ display: "flex", alignItems: "center", gap: 6, overflowX: "auto", paddingBottom: 2 }}>
            <span style={{ fontSize: "10px", fontWeight: 700, color: V.ink2, flexShrink: 0 }}>
              RECIPIENT:
            </span>
            <button
              type="button"
              onClick={() => { setSelectedContact(""); actions?.setChip(""); }}
              style={{
                padding: "2px 8px",
                fontSize: "10px",
                borderRadius: V.rs,
                border: `1px solid ${!selectedContact ? V.pri : V.outv}`,
                background: !selectedContact ? V.pri : V.bg,
                color: !selectedContact ? V.onpri : V.ink,
                cursor: "pointer",
                flexShrink: 0,
              }}
            >
              ALL CONVERSATIONS
            </button>
            {friends.map((f) => {
              const isSelected = selectedContact === f.name || selectedContact === f.id;
              return (
                <button
                  key={f.id}
                  type="button"
                  onClick={() => { setSelectedContact(f.name); actions?.setChip(f.name); }}
                  style={{
                    padding: "2px 8px",
                    fontSize: "10px",
                    borderRadius: V.rs,
                    border: `1px solid ${isSelected ? V.pri : V.outv}`,
                    background: isSelected ? V.pri : V.bg,
                    color: isSelected ? V.onpri : V.ink,
                    cursor: "pointer",
                    display: "flex",
                    alignItems: "center",
                    gap: 4,
                    flexShrink: 0,
                  }}
                >
                  <span style={{ width: 6, height: 6, borderRadius: "50%", background: f.onlineStatus === "online" ? "#22c55e" : "#6b7280" }} />
                  {f.name}
                </button>
              );
            })}
            {imThreads.filter((t) => !friends.some((f) => f.id === t.contactId || f.name === t.contactName)).map((t) => {
              const isSelected = selectedContact === t.contactName || selectedContact === t.contactId;
              return (
                <button
                  key={t.contactId}
                  type="button"
                  onClick={() => { setSelectedContact(t.contactName); actions?.setChip(t.contactName); }}
                  style={{
                    padding: "2px 8px",
                    fontSize: "10px",
                    borderRadius: V.rs,
                    border: `1px solid ${isSelected ? V.pri : V.outv}`,
                    background: isSelected ? V.pri : V.bg,
                    color: isSelected ? V.onpri : V.ink,
                    cursor: "pointer",
                    flexShrink: 0,
                  }}
                >
                  {t.contactName}
                </button>
              );
            })}
          </div>
        )}
      </div>

      {/* Messages Transcript */}
      <section
        aria-label={`${activeTab} chat transcript`}
        aria-live="polite"
        style={{ flex: 1, minHeight: 0, overflowY: "auto", padding: 12, display: "flex", flexDirection: "column", gap: 8 }}
      >
        {!formatted.length && (
          <div style={{ color: V.ink2, margin: "auto", textAlign: "center", display: "flex", flexDirection: "column", alignItems: "center", gap: 6 }}>
            <Icon name={activeTab === "IM" ? "message-circle" : "message-square"} size={26} />
            <div>
              {connected
                ? activeTab === "IM"
                  ? (selectedContact ? `No Instant Messages yet with ${selectedContact}.` : "No Instant Messages yet. Pick a resident above to start an IM.")
                  : activeTab === "GROUP"
                  ? "No group chat received."
                  : "Listening to live Second Life region chat…"
                : "Connect to a grid to chat."}
            </div>
          </div>
        )}
        {formatted.map((m) => {
          const isMe = m.senderId === app.auth.user?.id;
          return (
            <article
              key={m.id}
              style={{
                alignSelf: isMe ? "flex-end" : "flex-start",
                maxWidth: "88%",
                padding: "8px 10px",
                border: `1px solid ${m.isAutoReply ? "#eab308" : V.outv}`,
                borderRadius: V.rp,
                background: m.isAutoReply ? "rgba(234, 179, 8, 0.08)" : V.surf,
              }}
            >
              <header style={{ color: m.isAutoReply ? "#eab308" : V.pri, font: `600 10px/1.3 ${t.font}`, display: "flex", justifyContent: "space-between", gap: 8 }}>
                <span>
                  [{m.time}] {m.sender}
                  {m.recipientName && isMe ? ` → ${m.recipientName}` : ""}
                </span>
                <span style={{ opacity: 0.8, fontSize: 9 }}>
                  {m.isAutoReply ? "AUTO-REPLY" : m.type === "im" ? "IM" : m.type === "group" ? "GROUP" : "LOCAL"}
                </span>
              </header>
              <div style={{ color: V.ink, font: `400 13px/1.45 ${t.font}`, whiteSpace: "pre-wrap", overflowWrap: "anywhere", marginTop: 2 }}>
                {m.text}
              </div>
            </article>
          );
        })}
        <div ref={messagesEndRef} />
      </section>

      {/* Chat Send Input Form */}
      <form onSubmit={send} style={{ padding: 12, borderTop: `1px solid ${V.outv}`, background: V.surf }}>
        {error && <div role="alert" style={{ color: V.err, marginBottom: 7, fontSize: "12px" }}>{error}</div>}
        <div style={{ display: "flex", gap: 8 }}>
          <label htmlFor="chat-input" className="sr-only">
            {activeTab === "IM" ? `Instant message ${selectedContact || "resident"}` : activeTab === "GROUP" ? "Message group chat" : "Message local chat"}
          </label>
          <input
            id="chat-input"
            value={draft}
            onChange={(e) => setDraft(e.target.value)}
            disabled={!connected || sending}
            placeholder={
              !connected
                ? "Disconnected"
                : activeTab === "IM"
                ? (selectedContact ? `Instant Message to ${selectedContact}…` : "Select a friend above or enter message…")
                : activeTab === "GROUP"
                ? (selectedGroup ? "Send to group…" : "Select a group above…")
                : "Say to nearby region…"
            }
            autoComplete="off"
            style={{
              flex: 1,
              minWidth: 0,
              minHeight: 44,
              padding: "0 12px",
              border: `1px solid ${V.outv}`,
              borderRadius: V.rs,
              background: V.bg,
              color: V.ink,
              fontSize: 16,
            }}
          />
          <button
            type="submit"
            disabled={!connected || !draft.trim() || sending}
            aria-label={activeTab === "IM" ? "Send Instant Message" : activeTab === "GROUP" ? "Send group message" : "Send local chat"}
            style={{ width: 48, border: 0, borderRadius: V.rs, background: V.pri, color: V.onpri, cursor: "pointer", display: "flex", alignItems: "center", justifyContent: "center" }}
          >
            <Icon name="send" size={19} />
          </button>
        </div>
      </form>
    </div>
  );
}
