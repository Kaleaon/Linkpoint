import { useEffect, useState } from "react";
import { useApp } from "../context/AppContext.jsx";
import { useTheme } from "../context/ThemeContext.jsx";
import { app } from "../linkpoint/app";

// Ported from `chips`/`chipList`/`activeChip` — Chat's IM/GROUP thread picker.
// In Lumiya design, IMs ONLY show in-use chats, never the full friend list.
export default function ChipRow() {
  const { state, actions } = useApp();
  const { V, t, LK, scr, norm } = useTheme();
  const [, setRevision] = useState(0);

  useEffect(() => {
    const refresh = () => setRevision((n) => n + 1);
    app.chat.on("message_received", refresh);
    app.chat.on("message_sent", refresh);
    app.chat.on("sessions_changed", refresh);
    return () => {
      app.chat.off("message_received", refresh);
      app.chat.off("message_sent", refresh);
      app.chat.off("sessions_changed", refresh);
    };
  }, []);

  if (!norm || scr !== "Chat" || LK.chips === false) return null;

  const curTab = state.tabs.Chat;
  const friends = app.friends.getFriends();
  const imThreads = app.chat.getIMThreads();
  const realGroups = app.groups.getGroups();

  // ONLY in-use chats are shown for the IM tab (Lumiya design)
  const inUseImContacts = imThreads.map((t) => t.contactName).filter(Boolean);
  const groupNames = realGroups.map((g) => g.name || g.title).filter(Boolean);

  if (curTab === "IM" && !inUseImContacts.length) {
    return null;
  }

  const chipList = curTab === "GROUP" ? groupNames : curTab === "IM" ? inUseImContacts : [];
  if (!chipList.length) return null;

  const activeChip = chipList.includes(state.chip) ? state.chip : chipList[0];

  const chipBase = {
    flex: "none",
    height: "36px",
    padding: "0 10px",
    display: "flex",
    alignItems: "center",
    gap: "6px",
    borderWidth: "1px", borderStyle: "solid", borderColor: V.outv,
    borderRadius: V.rs,
    background: V.surf,
    cursor: "pointer",
    color: V.ink2,
  };

  const handleCloseSession = (e, name) => {
    e.stopPropagation();
    app.chat.closeSession(name);
    if (activeChip === name) {
      const remaining = inUseImContacts.filter((n) => n !== name);
      actions.setChip(remaining[0] || "");
    }
  };

  return (
    <div style={{ flex: "none", display: "flex", gap: "6px", padding: "8px 12px", overflowX: "auto", background: V.bg, borderBottom: `1px solid ${V.outv}` }}>
      {chipList.map((n) => {
        const active = activeChip === n;
        const friendObj = friends.find((f) => f.name === n);
        const isOnline = friendObj?.onlineStatus === "online";
        const threadObj = imThreads.find((t) => t.contactName === n);
        const unreadCount = threadObj?.unreadCount || 0;

        return (
          <div
            key={n}
            onClick={() => actions.setChip(n)}
            role="button"
            tabIndex={0}
            aria-label={`Chat with ${n}`}
            onKeyDown={(e) => {
              if (e.key === "Enter" || e.key === " ") {
                e.preventDefault();
                actions.setChip(n);
              }
            }}
            style={{
              ...chipBase,
              ...(active ? { borderColor: V.pri, background: V.priC, color: V.onpriC } : null),
            }}
          >
            <span
              style={{
                width: "7px",
                height: "7px",
                borderRadius: "50%",
                flex: "none",
                background: isOnline ? V.ok : V.ink2,
              }}
            />
            <span style={{ font: "500 11px/1 " + t.font, whiteSpace: "nowrap" }}>{n}</span>
            {unreadCount > 0 && (
              <span
                style={{
                  padding: "1px 5px",
                  borderRadius: "10px",
                  fontSize: "9px",
                  fontWeight: 700,
                  background: V.bdg || "#ef4444",
                  color: "#fff",
                }}
              >
                {unreadCount}
              </span>
            )}
            {curTab === "IM" && (
              <button
                type="button"
                onClick={(e) => handleCloseSession(e, n)}
                style={{
                  background: "transparent",
                  border: 0,
                  padding: "0 2px",
                  fontSize: "12px",
                  color: "inherit",
                  opacity: 0.65,
                  cursor: "pointer",
                  display: "flex",
                  alignItems: "center",
                }}
                title={`Close conversation with ${n}`}
                aria-label={`Close conversation with ${n}`}
              >
                &times;
              </button>
            )}
          </div>
        );
      })}
      {curTab === "IM" && (
        <div
          onClick={() => actions.openSearch("Chat", "FRIENDS")}
          role="button"
          tabIndex={0}
          aria-label="Start new Instant Message"
          onKeyDown={(e) => {
            if (e.key === "Enter" || e.key === " ") {
              e.preventDefault();
              actions.openSearch("Chat", "FRIENDS");
            }
          }}
          style={{ ...chipBase, borderStyle: "dashed", color: V.pri }}
          title="Start conversation with friend or resident"
        >
          <span style={{ font: "700 12px/1 " + t.font, flex: "none" }}>+</span>
          <span style={{ font: "600 11px/1 " + t.font, whiteSpace: "nowrap" }}>NEW IM</span>
        </div>
      )}
    </div>
  );
}
