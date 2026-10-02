import { useEffect, useState } from "react";
import { useApp } from "../context/AppContext.jsx";
import { useTheme } from "../context/ThemeContext.jsx";
import { app } from "../linkpoint/app.ts";
import Icon from "../components/Icon.jsx";

const TABS = [
  { id: "FRIENDS", label: "FRIENDS", icon: "users" },
  { id: "NEARBY", label: "NEARBY", icon: "radar" },
  { id: "SEARCH", label: "SEARCH", icon: "search" },
];

export default function Search() {
  const { state, actions } = useApp();
  const { V, t } = useTheme();

  const [friendsList, setFriendsList] = useState(() => app.friends.getFriends());
  const [nearbyList, setNearbyList] = useState(() => app.world.getNearbyUsers());

  useEffect(() => {
    const updateFriends = () => setFriendsList(app.friends.getFriends());
    const updateNearby = (users) => setNearbyList(users);

    app.friends.on("friend_added", updateFriends);
    app.friends.on("friend_updated", updateFriends);
    app.friends.on("friend_removed", updateFriends);
    app.world.on("nearby_changed", updateNearby);

    return () => {
      app.friends.off("friend_added", updateFriends);
      app.friends.off("friend_updated", updateFriends);
      app.friends.off("friend_removed", updateFriends);
      app.world.off("nearby_changed", updateNearby);
    };
  }, []);

  const tab = TABS.some((x) => x.id === state.searchTab) ? state.searchTab : "FRIENDS";
  const q = (state.searchQuery || "").trim().toLowerCase();

  const startIm = (name) => actions.startIm(name);

  const imPillStyle = (known) => ({
    display: "flex",
    alignItems: "center",
    gap: "6px",
    height: "30px",
    padding: "0 12px",
    borderRadius: V.rs,
    border: "1px solid " + V.pri,
    background: known ? V.pri : "transparent",
    color: known ? V.onpri : V.pri,
    font: "700 10.5px/1 " + t.font,
    letterSpacing: ".14em",
    cursor: "pointer",
    flex: "none",
  });

  let rows = null;
  let emptyText = "";

  if (tab === "FRIENDS") {
    const list = friendsList.filter((f) => !q || (f.name || "").toLowerCase().includes(q));
    rows = list.map((f) => {
      const isOnline = f.onlineStatus === "online";
      return (
        <div key={f.id} onClick={() => startIm(f.name)} style={{ display: "flex", alignItems: "center", gap: "10px", padding: "10px 12px", border: "1px solid " + V.outv, borderRadius: V.rs, background: V.surf, cursor: "pointer" }}>
          <Icon name={isOnline ? "circle-dot" : "circle"} size={20} style={{ color: isOnline ? V.ok : V.ink2, flex: "none" }} />
          <div style={{ flex: 1, minWidth: 0 }}>
            <div style={{ font: "600 13px/1.3 " + t.font, color: V.ink }}>{f.name}</div>
            <div style={{ font: "400 10px/1.3 " + t.font, color: V.ink2, marginTop: "1px" }}>{isOnline ? "Online" : "Offline"}</div>
          </div>
          <div onClick={(e) => { e.stopPropagation(); startIm(f.name); }} style={imPillStyle(true)}>
            IM
          </div>
        </div>
      );
    });
    emptyText = q ? `> no friends match “${state.searchQuery}”` : "> no friends added yet";
  } else if (tab === "NEARBY") {
    const list = nearbyList.filter((n) => !q || (n.name || "").toLowerCase().includes(q)).sort((a, b) => Number(a.distance ?? Infinity) - Number(b.distance ?? Infinity));
    rows = list.map((item) => {
      const name = item.name || item.id;
      const dm = item.distance != null ? Math.round(item.distance) : 0;
      return (
        <div key={item.id} onClick={() => startIm(name)} style={{ display: "flex", alignItems: "center", gap: "10px", padding: "10px 12px", border: "1px solid " + V.outv, borderRadius: V.rs, background: V.surf, cursor: "pointer" }}>
          <Icon name="circle-user-round" size={20} style={{ color: V.sec2, flex: "none" }} />
          <div style={{ flex: 1, minWidth: 0 }}>
            <div style={{ font: "600 13px/1.3 " + t.font, color: V.ink }}>{name}</div>
            <div style={{ font: "400 10px/1.3 " + t.font, color: V.ink2, marginTop: "1px" }}>Nearby resident</div>
          </div>
          <div style={{ padding: "4px 8px", border: "1px solid " + V.outv, borderRadius: V.rs, font: "400 11px/1 " + t.font, color: V.ink2, flex: "none" }}>{dm}m</div>
          <div onClick={(e) => { e.stopPropagation(); startIm(name); }} style={imPillStyle(false)}>
            IM
          </div>
        </div>
      );
    });
    emptyText = q ? `> nobody nearby matches “${state.searchQuery}”` : "> nobody in range right now";
  } else {
    const matchedFriends = friendsList.filter((f) => q.length >= 2 && (f.name || "").toLowerCase().includes(q));
    const matchedNearby = nearbyList.filter((n) => q.length >= 2 && (n.name || "").toLowerCase().includes(q) && !matchedFriends.some((f) => f.name === n.name));
    const combined = [...matchedFriends.map((f) => ({ ...f, type: "friend" })), ...matchedNearby.map((n) => ({ id: n.id, name: n.name, type: "nearby" }))];

    rows = combined.map((sr) => (
      <div key={sr.id || sr.name} style={{ display: "flex", alignItems: "center", gap: "10px", padding: "10px 12px", border: "1px solid " + V.outv, borderRadius: V.rs, background: V.surf }}>
        <Icon name="circle-user-round" size={22} style={{ color: V.sec2, flex: "none" }} />
        <div style={{ flex: 1, minWidth: 0 }}>
          <div style={{ font: "600 13px/1.3 " + t.font, color: V.ink }}>{sr.name}</div>
          <div style={{ font: "400 10px/1.3 " + t.font, color: V.ink2, marginTop: "1px" }}>{sr.type === "friend" ? "Friend" : "Nearby resident"}</div>
        </div>
        <div onClick={() => startIm(sr.name)} style={imPillStyle(true)}>
          IM
        </div>
      </div>
    ));
    emptyText = q.length < 2 ? "> type a name to filter resident contacts" : "> no matching residents found";
  }

  return (
    <div style={{ flex: 1, minHeight: 0, display: "flex", flexDirection: "column" }}>
      <div style={{ flex: "none", display: "flex", alignItems: "center", gap: "8px", padding: "14px 16px 10px" }}>
        <div
          onClick={() => actions.setScreen(state.searchFrom || "Friends")}
          style={{ width: "36px", height: "36px", display: "flex", alignItems: "center", justifyContent: "center", color: V.pri, cursor: "pointer" }}
        >
          <Icon name="chevron-left" size={22} />
        </div>
        <div>
          <div style={{ font: "700 18px/1 " + t.dfont, letterSpacing: ".16em", color: V.pri }}>PEOPLE</div>
          <div style={{ font: "400 10.5px/1.3 " + t.font, color: V.ink2, marginTop: "2px" }}>&gt; friends or who's nearby</div>
        </div>
      </div>

      <div style={{ flex: "none", display: "flex", gap: "8px", padding: "0 16px 10px" }}>
        {TABS.map((tb) => {
          const on = tab === tb.id;
          return (
            <div
              key={tb.id}
              onClick={() => actions.setSearchTab(tb.id)}
              style={{
                flex: 1,
                display: "flex",
                alignItems: "center",
                justifyContent: "center",
                gap: "6px",
                height: "38px",
                borderRadius: V.rs,
                border: "1px solid " + (on ? V.pri : V.outv),
                background: on ? V.priC : V.surf,
                color: on ? V.onpriC : V.ink2,
                font: "700 10.5px/1 " + t.font,
                letterSpacing: ".12em",
                cursor: "pointer",
              }}
            >
              <Icon name={tb.icon} size={14} />
              {tb.label}
            </div>
          );
        })}
      </div>

      <div style={{ flex: "none", margin: "0 16px 10px", height: "44px", display: "flex", alignItems: "center", gap: "8px", padding: "0 12px", border: "1px solid " + V.outv, borderRadius: V.rs, background: V.surf }}>
        <Icon name="search" size={16} style={{ color: V.ink2 }} />
        <input
          value={state.searchQuery}
          onChange={(e) => actions.setSearchQuery(e.target.value)}
          placeholder={tab === "SEARCH" ? "resident name (min 2 chars)" : "filter by name"}
          style={{ flex: 1, minWidth: 0, border: "none", outline: "none", background: "transparent", font: "400 13px/1 " + t.font, color: V.ink }}
        />
      </div>

      <div style={{ flex: 1, minHeight: 0, overflowY: "auto", padding: "0 16px 16px", display: "flex", flexDirection: "column", gap: "8px" }}>
        {rows}
        {rows.length === 0 ? (
          <div style={{ padding: "40px 0", textAlign: "center", font: "400 12px/1.5 " + t.font, color: V.ink2 }}>{emptyText}</div>
        ) : null}
      </div>
    </div>
  );
}
