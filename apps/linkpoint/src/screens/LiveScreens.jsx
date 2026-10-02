import { useEffect, useMemo, useState } from "react";
import { app } from "../linkpoint/app.ts";
import Icon from "../components/Icon.jsx";
import { useTheme } from "../context/ThemeContext.jsx";
import { useApp } from "../context/AppContext.jsx";
import Radar from "./Radar.jsx";
import ContactsScreen from "./ContactsScreen.jsx";
import CalendarScreen from "./CalendarScreen.jsx";

function Empty({ icon, children }) {
  return <div className="honest-empty"><Icon name={icon} size={30} /><p>{children}</p></div>;
}

function Rows({ rows, icon = "circle" }) {
  return <div className="live-list">{rows.map((row) => <div className="inventory-row" key={row.id || row.name || row.title}><Icon name={row.icon || icon} size={17} /><div><strong>{row.name || row.title || row.id}</strong>{row.meta || row.message ? <small>{row.meta || row.message}</small> : null}</div></div>)}</div>;
}

export function FriendsScreen() {
  const { V } = useTheme();
  const { state, actions } = useApp();
  const [, setRevision] = useState(0);
  const [syncing, setSyncing] = useState(false);
  const [selectedFriend, setSelectedFriend] = useState(null);

  useEffect(() => {
    const listener = () => setRevision((n) => n + 1);
    app.friends.addStatusListener(listener);
    app.friends.on("friend_added", listener);
    app.friends.on("friend_updated", listener);
    app.friends.on("friend_removed", listener);
    app.protocol.on("friends_loaded", listener);
    app.protocol.on("friend_status", listener);
    void app.loadFriends();
    return () => {
      app.friends.removeStatusListener(listener);
      app.friends.off("friend_added", listener);
      app.friends.off("friend_updated", listener);
      app.friends.off("friend_removed", listener);
      app.protocol.off("friends_loaded", listener);
      app.protocol.off("friend_status", listener);
    };
  }, []);

  const handleSyncFriends = async () => {
    setSyncing(true);
    try {
      await app.loadFriends();
      setRevision((n) => n + 1);
    } finally {
      setSyncing(false);
    }
  };

  const filterTab = state.tabs?.Friends || "ALL";
  if (filterTab === "CONTACTS") return <ContactsScreen />;

  const saveFriends = (list) => {
    try {
      const result = app.contacts.saveFriends(list);
      actions.notify(result.added ? `Saved ${result.added} contact${result.added === 1 ? "" : "s"}.` : "Already saved.");
    } catch (error) {
      actions.notify(error?.message || "Could not save contacts.");
    }
    setRevision((n) => n + 1);
  };
  const allFriends = app.friends.getFriends();
  const filtered = allFriends.filter((f) => {
    if (filterTab === "ONLINE") return f.onlineStatus === "online";
    return true;
  });

  return (
    <div style={{ flex: 1, minHeight: 0, display: "flex", flexDirection: "column", padding: 12, gap: 10, background: V.bg }}>
      <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center" }}>
        <div style={{ fontSize: "11px", color: V.ink2, fontWeight: 700 }}>
          {allFriends.filter(f => f.onlineStatus === 'online').length} ONLINE · {allFriends.length} TOTAL FRIENDS
        </div>
        <div style={{ display: "flex", gap: 6 }}>
        <button
          type="button"
          disabled={!allFriends.length}
          onClick={() => saveFriends(allFriends)}
          style={{ padding: "4px 10px", fontSize: "11px", fontWeight: 700, background: V.surf, border: `1px solid ${V.outv}`, borderRadius: V.rs, color: V.ink, cursor: "pointer", display: "flex", alignItems: "center", gap: 6 }}
          title="Save your friends list to Contacts on this device"
        >
          <Icon name="contact" size={13} />
          SAVE TO CONTACTS
        </button>
        <button
          type="button"
          onClick={handleSyncFriends}
          disabled={syncing || !app.auth.isLoggedIn()}
          style={{
            padding: "4px 10px",
            fontSize: "11px",
            fontWeight: 700,
            background: V.surf,
            border: `1px solid ${V.outv}`,
            borderRadius: V.rs,
            color: V.pri,
            cursor: "pointer",
            display: "flex",
            alignItems: "center",
            gap: 6
          }}
          title="Reload friends list from Second Life"
        >
          <Icon name="rotate-cw" size={13} />
          {syncing ? "SYNCING…" : "SYNC FRIENDS"}
        </button>
        </div>
      </div>

      {!filtered.length ? (
        <Empty icon="users">
          {app.auth.isLoggedIn()
            ? (filterTab === "ONLINE" ? "None of your friends are currently online." : "No friends loaded from the grid.")
            : "Connect to a grid to see your friends."}
        </Empty>
      ) : (
        <div className="live-list" style={{ flex: 1, minHeight: 0, overflowY: "auto" }}>
          {filtered.map((friend) => {
            const isOnline = friend.onlineStatus === "online";
            return (
              <div
                key={friend.id}
                className="inventory-row"
                style={{
                  display: "flex",
                  alignItems: "center",
                  justifyContent: "space-between",
                  padding: "10px 12px",
                  background: V.surf,
                  border: `1px solid ${V.outv}`,
                  borderRadius: V.rs,
                  marginBottom: 6,
                }}
              >
                <div
                  style={{ display: "flex", alignItems: "center", gap: 10, cursor: "pointer", flex: 1 }}
                  onClick={() => setSelectedFriend(selectedFriend?.id === friend.id ? null : friend)}
                >
                  <span
                    style={{
                      width: 9,
                      height: 9,
                      borderRadius: "50%",
                      background: isOnline ? "#22c55e" : "#6b7280",
                      boxShadow: isOnline ? "0 0 6px rgba(34, 197, 94, 0.6)" : "none",
                      flexShrink: 0,
                    }}
                  />
                  <div>
                    <strong style={{ color: V.ink, fontSize: "13px" }}>{friend.name}</strong>
                    <div style={{ fontSize: "11px", color: isOnline ? "#22c55e" : V.ink2, textTransform: "capitalize" }}>
                      {friend.onlineStatus || "offline"}
                    </div>
                  </div>
                </div>

                <div style={{ display: "flex", gap: 6 }}>
                  <button
                    type="button"
                    disabled={app.contacts.has(friend.id)}
                    onClick={() => saveFriends([friend])}
                    style={{ padding: "4px 8px", fontSize: "11px", fontWeight: 700, background: V.surf, color: V.pri, border: `1px solid ${V.outv}`, borderRadius: V.rs, cursor: app.contacts.has(friend.id) ? "default" : "pointer", opacity: app.contacts.has(friend.id) ? 0.6 : 1 }}
                    title={app.contacts.has(friend.id) ? "Already in your contacts" : "Save to Contacts on this device"}
                  >
                    {app.contacts.has(friend.id) ? "SAVED" : "SAVE"}
                  </button>
                  <button
                    type="button"
                    onClick={() => actions.startIm(friend.name)}
                    style={{
                      padding: "4px 10px",
                      fontSize: "11px",
                      fontWeight: 700,
                      background: V.pri,
                      color: V.onpri,
                      border: 0,
                      borderRadius: V.rs,
                      cursor: "pointer",
                      display: "flex",
                      alignItems: "center",
                      gap: 4,
                    }}
                  >
                    <Icon name="message-circle" size={13} />
                    IM
                  </button>
                </div>
              </div>
            );
          })}
        </div>
      )}

      {selectedFriend && (
        <aside className="record-detail" style={{ background: V.surf, borderColor: V.outv, padding: 12 }}>
          <button className="detail-close" onClick={() => setSelectedFriend(null)} aria-label="Close friend details">×</button>
          <h2>{selectedFriend.name}</h2>
          <dl>
            <dt>Resident UUID</dt>
            <dd>{selectedFriend.id}</dd>
            <dt>Status</dt>
            <dd style={{ color: selectedFriend.onlineStatus === 'online' ? '#22c55e' : V.ink2, textTransform: 'capitalize' }}>{selectedFriend.onlineStatus || 'offline'}</dd>
            <dt>Permissions</dt>
            <dd>
              {selectedFriend.permissions?.canSeeOnline ? '✓ See online status ' : ''}
              {selectedFriend.permissions?.canSeeOnMap ? '✓ Map location ' : ''}
              {selectedFriend.permissions?.canModifyObjects ? '✓ Modify objects' : ''}
              {!selectedFriend.permissions?.canSeeOnline && !selectedFriend.permissions?.canSeeOnMap && !selectedFriend.permissions?.canModifyObjects ? 'Standard friendship' : ''}
            </dd>
          </dl>
          <div style={{ marginTop: 8 }}>
            <button
              type="button"
              onClick={() => {
                actions.startIm(selectedFriend.name);
                setSelectedFriend(null);
              }}
              style={{
                width: "100%",
                padding: "8px",
                background: V.pri,
                color: V.onpri,
                border: 0,
                borderRadius: V.rs,
                fontSize: "12px",
                fontWeight: 700,
                cursor: "pointer",
              }}
            >
              START INSTANT MESSAGE
            </button>
          </div>
        </aside>
      )}
    </div>
  );
}

export function RadarScreen() {
  return <Radar />;
}

export function MapScreen() {
  const [region, setRegion] = useState(app.world.region);
  useEffect(() => { const refresh = (next) => setRegion(next); app.world.on("region_changed", refresh); return () => app.world.off("region_changed", refresh); }, []);
  return region ? <Rows rows={[{ id: region.id || region.name, name: region.name, meta: [region.x, region.y].filter((v) => v != null).join(", ") }]} icon="map-pin" /> : <Empty icon="map">No region handshake has been received.</Empty>;
}

export function WorldScreen() {
  useEffect(() => { void app.world.init(); return () => app.world.stopRendering(); }, []);
  return <div className="world-runtime"><canvas id="world-canvas" aria-label="Live simulator scene" />{!app.world.region ? <div className="world-overlay">Waiting for a live simulator scene</div> : null}</div>;
}

export function GroupsScreen() {
  const rows = app.groups.getGroups();
  return rows.length ? <Rows rows={rows} icon="users-round" /> : <Empty icon="users-round">No group records have been received.</Empty>;
}

export function NoticesScreen() {
  const { state } = useApp();
  const sub = state.tabs?.Notices || "NOTICES";
  // The sub-tab is chosen here, before any hooks run, so switching tabs never changes the hook order.
  return sub === "CALENDAR" ? <CalendarScreen /> : <NoticesList />;
}

function NoticesList() {
  const { V, t } = useTheme();
  const { actions } = useApp();
  const [notices, setNotices] = useState(() => app.notices.list());
  const [others, setOthers] = useState(() => app.notifications.items.filter((item) => item.kind !== "notice"));
  const [openId, setOpenId] = useState(null);

  useEffect(() => {
    const onNotices = (list) => setNotices(list);
    const onOthers = () => setOthers(app.notifications.items.filter((item) => item.kind !== "notice"));
    app.notices.on("notices_changed", onNotices);
    app.notifications.on("notification_received", onOthers);
    app.notifications.on("cleared", onOthers);
    setNotices(app.notices.list());
    return () => {
      app.notices.off("notices_changed", onNotices);
      app.notifications.off("notification_received", onOthers);
      app.notifications.off("cleared", onOthers);
    };
  }, []);

  const groupName = (id) => (id ? app.groups.getGroups().find((group) => group.id === id || group.groupId === id)?.name : null) || null;
  const button = { minHeight: 32, padding: "0 12px", border: `1px solid ${V.outv}`, borderRadius: V.rs, background: V.surf, color: V.pri, cursor: "pointer", font: `700 10.5px/1 ${t.font}`, letterSpacing: ".08em" };

  if (!notices.length && !others.length) return <Empty icon="bell">No group notices have been received. Notices you receive are kept on this device.</Empty>;

  return (
    <div style={{ flex: 1, minHeight: 0, overflowY: "auto", padding: 12, display: "grid", gap: 8, alignContent: "start", background: V.bg, color: V.ink }}>
      {notices.map((notice) => {
        const open = openId === notice.id;
        return (
          <article key={notice.id} style={{ padding: "10px 12px", background: V.surf, border: `1px solid ${open ? V.pri : V.outv}`, borderRadius: V.rs }}>
            <button type="button" aria-expanded={open} onClick={() => setOpenId(open ? null : notice.id)} style={{ all: "unset", cursor: "pointer", display: "block", width: "100%" }}>
              <strong style={{ display: "block", fontSize: 13 }}>{notice.subject}</strong>
              <small style={{ color: V.ink2 }}>{[groupName(notice.groupId), notice.from, new Date(notice.timestamp).toLocaleString()].filter(Boolean).join(" · ")}{notice.calendar ? " · in calendar" : ""}</small>
            </button>
            {open ? (
              <div style={{ marginTop: 8, display: "grid", gap: 8 }}>
                <div style={{ whiteSpace: "pre-wrap", overflowWrap: "anywhere", font: `400 12.5px/1.6 ${t.font}`, color: V.ink2 }}>{notice.message || "No message."}</div>
                <div style={{ display: "flex", gap: 6, flexWrap: "wrap" }}>
                  <button type="button" style={{ ...button, background: V.pri, color: V.onpri, borderColor: V.pri }} onClick={() => { app.notices.focus(notice.id); actions.setScreen("Calendar"); }}>ADD TO CALENDAR</button>
                  <button type="button" style={{ ...button, color: V.err }} onClick={() => { app.notices.remove(notice.id); setOpenId(null); }}>DELETE</button>
                </div>
              </div>
            ) : null}
          </article>
        );
      })}
      {others.length ? <Rows rows={others} icon="bell" /> : null}
      {others.length ? <button className="screen-action" onClick={() => app.notifications.clear()}>Clear other notifications</button> : null}
    </div>
  );
}

export function MuteListScreen() {
  const { state } = useApp();
  const sub = state.tabs?.["Mute List"] || "AVATARS";
  const [entry, setEntry] = useState("");
  const [revision, setRevision] = useState(0);

  const isObjects = sub === "OBJECTS";
  const mutedUsers = useMemo(() => app.chatExtended.getMutedUsers?.() || [], [revision]);
  const mutedObjects = useMemo(() => app.chatExtended.getMutedObjects?.() || [], [revision]);
  const activeList = isObjects ? mutedObjects : mutedUsers;

  const add = () => {
    if (!entry.trim()) return;
    if (isObjects) {
      app.chatExtended.muteObject(entry.trim());
    } else {
      app.chatExtended.muteUser(entry.trim());
    }
    setEntry("");
    setRevision((n) => n + 1);
  };

  const remove = (nameOrId) => {
    if (isObjects) {
      app.chatExtended.unmuteObject(nameOrId);
    } else {
      app.chatExtended.unmuteUser(nameOrId);
    }
    setRevision((n) => n + 1);
  };

  return (
    <div className="tool-screen">
      <div className="inline-tool">
        <input
          value={entry}
          onChange={(e) => setEntry(e.target.value)}
          placeholder={isObjects ? "Object or HUD name (e.g. av)" : "Avatar UUID or Name"}
        />
        <button onClick={add}>Mute {isObjects ? "Object" : "Avatar"}</button>
      </div>
      {activeList.length ? (
        <div style={{ display: "flex", flexDirection: "column", gap: 4, width: "100%" }}>
          {activeList.map((id) => (
            <div
              key={id}
              style={{
                display: "flex",
                alignItems: "center",
                justifyContent: "space-between",
                padding: "8px 12px",
                background: "rgba(255,255,255,0.04)",
                borderRadius: 4,
              }}
            >
              <span style={{ fontSize: 13, fontWeight: 600 }}>{id}</span>
              <button
                type="button"
                onClick={() => remove(id)}
                style={{
                  padding: "3px 8px",
                  fontSize: 11,
                  background: "transparent",
                  border: "1px solid rgba(255,255,255,0.2)",
                  borderRadius: 4,
                  cursor: "pointer",
                }}
              >
                Unmute
              </button>
            </div>
          ))}
        </div>
      ) : (
        <Empty icon="volume-x">No {isObjects ? "objects or HUDs" : "avatars"} are muted.</Empty>
      )}
    </div>
  );
}

export function GenericInventoryScreen({ kind }) {
  const wearableTypes = new Set([5, 13, 18, 19, 20, 21, 22, 23, 24]);
  const values = Array.from(app.inventory.items.values()).filter((item) => kind === "wearable" ? wearableTypes.has(Number(item.assetType)) : Number(item.assetType) === 6);
  return values.length ? <Rows rows={values} icon="package" /> : <Empty icon="package">No {kind.toLowerCase()} data has been loaded from inventory.</Empty>;
}

export function ConnectionScreen({ title }) {
  return <Empty icon="plug">{app.auth.isLoggedIn() ? `${title} is waiting for data from the current grid.` : `Connect to a grid to use ${title.toLowerCase()}.`}</Empty>;
}

export function SearchScreen() {
  const [query, setQuery] = useState("");
  const [status, setStatus] = useState("");
  const matches = app.friends.getFriends().filter((friend) => String(friend.name || friend.id).toLowerCase().includes(query.trim().toLowerCase()));
  const request = async () => {
    if (!query.trim()) return;
    try {
      await app.friends.sendFriendRequest(query.trim());
      setStatus("Friend request queued.");
    } catch (error) {
      setStatus(error instanceof Error ? error.message : "Unable to queue friend request.");
    }
  };
  return <div className="tool-screen"><div className="inline-tool"><input value={query} onChange={(e) => setQuery(e.target.value)} placeholder="Resident UUID or loaded name" aria-label="Resident search" /><button onClick={() => void request()}>Add</button></div>{status ? <p className="tool-status">{status}</p> : null}{query && matches.length ? <Rows rows={matches} icon="user" /> : <Empty icon="search">Search checks residents received in this session. Enter a resident UUID to send a request.</Empty>}</div>;
}
