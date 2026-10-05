import { useEffect, useState } from "react";
import { useApp } from "../context/AppContext.jsx";
import { useTheme } from "../context/ThemeContext.jsx";
import { app } from "../linkpoint/app.ts";
import { slBridge } from "../linkpoint/sl-bridge.ts";
import Icon from "../components/Icon.jsx";

const LOCAL_TABS = [
  { id: "FRIENDS", label: "FRIENDS", icon: "users" },
  { id: "NEARBY", label: "NEARBY", icon: "radar" },
];

const DIRECTORY_TABS = [
  { id: "PLACES", label: "PLACES", category: "places", icon: "map-pin" },
  { id: "EVENTS", label: "EVENTS", category: "events", icon: "calendar" },
  { id: "LAND", label: "LAND", category: "land", icon: "mountain" },
  { id: "GROUPS", label: "GROUPS", category: "groups", icon: "users-round" },
  { id: "PEOPLE", label: "PEOPLE", category: "people", icon: "user-search" },
];

const ALL_TABS = [...LOCAL_TABS, ...DIRECTORY_TABS];

export default function Search() {
  const { state, actions } = useApp();
  const { V, t } = useTheme();

  const [friendsList, setFriendsList] = useState(() => app.friends.getFriends());
  const [nearbyList, setNearbyList] = useState(() => app.world.getNearbyUsers());

  // Maturity filter state
  const [maturityGeneral, setMaturityGeneral] = useState(true);
  const [maturityModerate, setMaturityModerate] = useState(true);
  const [maturityAdult, setMaturityAdult] = useState(false);

  // Search and remote directory state
  const [debouncedQuery, setDebouncedQuery] = useState(state.searchQuery || "");
  const [directoryResults, setDirectoryResults] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [hasMore, setHasMore] = useState(false);
  const [start, setStart] = useState(0);

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

  // Debounce input by 300ms
  useEffect(() => {
    const timer = setTimeout(() => {
      setDebouncedQuery(state.searchQuery || "");
    }, 300);
    return () => clearTimeout(timer);
  }, [state.searchQuery]);

  let activeTab = state.searchTab;
  if (activeTab === "SEARCH") activeTab = "PEOPLE";
  if (!ALL_TABS.some((x) => x.id === activeTab)) activeTab = "FRIENDS";

  const activeDirTab = DIRECTORY_TABS.find((t) => t.id === activeTab);
  const maturityMask = (maturityGeneral ? 1 : 0) | (maturityModerate ? 2 : 0) | (maturityAdult ? 4 : 0) || 1;

  // Reset pagination offset when tab, search query or maturity selection changes
  useEffect(() => {
    if (activeDirTab) {
      setStart(0);
    }
  }, [activeTab, debouncedQuery, maturityMask, activeDirTab]);

  // Execute remote grid directory search
  useEffect(() => {
    if (!activeDirTab) {
      setDirectoryResults([]);
      setError(null);
      setLoading(false);
      return;
    }

    const query = debouncedQuery.trim();
    if (!query && activeDirTab.category !== "land") {
      setDirectoryResults([]);
      setError(null);
      setLoading(false);
      return;
    }

    let cancelled = false;
    setLoading(true);
    setError(null);

    slBridge
      .searchDir({
        category: activeDirTab.category,
        query,
        start,
        maturity: maturityMask,
      })
      .then((res) => {
        if (cancelled) return;
        const newItems = res?.results || [];
        if (start === 0) {
          setDirectoryResults(newItems);
        } else {
          setDirectoryResults((prev) => [...prev, ...newItems]);
        }
        setHasMore(Boolean(res?.hasMore));
        setLoading(false);
      })
      .catch((err) => {
        if (cancelled) return;
        setError(err?.message || "Grid directory search failed");
        setDirectoryResults([]);
        setLoading(false);
      });

    return () => {
      cancelled = true;
    };
  }, [activeDirTab, debouncedQuery, maturityMask, start]);

  const q = (state.searchQuery || "").trim().toLowerCase();
  const startIm = (name) => actions.startIm(name);

  const handleTeleport = async (item) => {
    try {
      const destination = item.location || item.simName;
      if (app.protocol && typeof app.protocol.teleportTo === "function") {
        await app.protocol.teleportTo(destination);
      } else {
        await slBridge.teleport({
          destination,
          region: item.simName,
          x: item.localX ?? 128,
          y: item.localY ?? 128,
          z: item.localZ ?? 30,
        });
      }
      if (actions.notify) actions.notify(`Teleport to ${item.name || destination} requested`);
    } catch (err) {
      if (actions.notify) actions.notify(`Teleport failed: ${err.message || err}`);
    }
  };

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

  const actionBtnStyle = (primary = true) => ({
    display: "flex",
    alignItems: "center",
    justifyContent: "center",
    gap: "6px",
    height: "32px",
    padding: "0 12px",
    borderRadius: V.rs,
    border: "1px solid " + V.pri,
    background: primary ? V.pri : "transparent",
    color: primary ? V.onpri : V.pri,
    font: "700 10.5px/1 " + t.font,
    letterSpacing: ".12em",
    cursor: "pointer",
    flex: "none",
  });

  const renderMaturityBadge = (maturity) => {
    if (!maturity) return null;
    let bg = V.ok;
    let fg = V.onpri || "#ffffff";
    if (maturity === "Moderate" || maturity === "Mature") {
      bg = V.warn || "#f59e0b";
    } else if (maturity === "Adult") {
      bg = V.err || "#ef4444";
    }
    return (
      <span
        style={{
          padding: "2px 6px",
          borderRadius: V.rs,
          background: bg,
          color: fg,
          font: "700 9px/1 " + t.font,
          letterSpacing: ".08em",
          textTransform: "uppercase",
          flex: "none",
        }}
      >
        {maturity}
      </span>
    );
  };

  let rows = null;
  let emptyText = "";

  if (activeTab === "FRIENDS") {
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
  } else if (activeTab === "NEARBY") {
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
  } else if (activeDirTab) {
    if (loading && directoryResults.length === 0) {
      rows = (
        <div style={{ padding: "30px 0", display: "flex", flexDirection: "column", alignItems: "center", gap: "8px", font: "400 12px/1.5 " + t.font, color: V.ink2 }}>
          <Icon name="loader-2" size={24} style={{ color: V.pri }} />
          <span>Searching Second Life grid directory...</span>
        </div>
      );
    } else if (error) {
      rows = (
        <div style={{ padding: "12px 16px", borderRadius: V.rs, border: "1px solid " + V.err, background: V.surf, color: V.err || "#ef4444", font: "400 12px/1.4 " + t.font }}>
          <strong>Search Error:</strong> {error}
        </div>
      );
    } else {
      rows = directoryResults.map((item) => {
        if (activeDirTab.category === "places" || activeDirTab.category === "events" || activeDirTab.category === "land") {
          return (
            <div key={item.id} style={{ display: "flex", flexDirection: "column", gap: "8px", padding: "12px", border: "1px solid " + V.outv, borderRadius: V.rs, background: V.surf }}>
              <div style={{ display: "flex", alignItems: "flex-start", justifyContent: "space-between", gap: "8px" }}>
                <div style={{ flex: 1, minWidth: 0 }}>
                  <div style={{ display: "flex", alignItems: "center", gap: "6px", flexWrap: "wrap" }}>
                    <span style={{ font: "600 14px/1.3 " + t.font, color: V.ink }}>{item.name}</span>
                    {renderMaturityBadge(item.maturity)}
                    {item.forSale ? <span style={{ padding: "2px 6px", borderRadius: V.rs, background: V.pri, color: V.onpri, font: "700 9px/1 " + t.font }}>FOR SALE</span> : null}
                  </div>
                  {item.description ? (
                    <div style={{ font: "400 11.5px/1.4 " + t.font, color: V.ink2, marginTop: "4px" }}>
                      {item.description}
                    </div>
                  ) : null}
                  <div style={{ display: "flex", alignItems: "center", gap: "10px", marginTop: "6px", font: "400 11px/1.2 " + t.font, color: V.ink2, flexWrap: "wrap" }}>
                    {item.location || item.simName ? (
                      <span style={{ display: "flex", alignItems: "center", gap: "4px" }}>
                        <Icon name="map-pin" size={12} /> {item.location || item.simName}
                      </span>
                    ) : null}
                    {item.dwell != null ? <span>Dwell: {item.dwell}</span> : null}
                    {item.area != null ? <span>Area: {item.area} m²</span> : null}
                    {item.price != null ? <span>Price: L${item.price}</span> : null}
                    {item.date ? <span>{item.date} {item.time}</span> : null}
                    {item.cost ? <span>Cost: {item.cost}</span> : null}
                  </div>
                </div>
                <button onClick={() => handleTeleport(item)} style={actionBtnStyle(true)}>
                  <Icon name="zap" size={14} /> Teleport
                </button>
              </div>
            </div>
          );
        } else if (activeDirTab.category === "groups") {
          return (
            <div key={item.id} style={{ display: "flex", alignItems: "center", gap: "10px", padding: "12px", border: "1px solid " + V.outv, borderRadius: V.rs, background: V.surf }}>
              <Icon name="users-round" size={24} style={{ color: V.sec2, flex: "none" }} />
              <div style={{ flex: 1, minWidth: 0 }}>
                <div style={{ font: "600 13.5px/1.3 " + t.font, color: V.ink }}>{item.name}</div>
                {item.description ? <div style={{ font: "400 11px/1.3 " + t.font, color: V.ink2, marginTop: "2px" }}>{item.description}</div> : null}
                {item.members != null ? <div style={{ font: "400 10.5px/1.3 " + t.font, color: V.ink2, marginTop: "2px" }}>{item.members} members</div> : null}
              </div>
              <button onClick={() => actions.notify && actions.notify(`Joined group ${item.name}`)} style={actionBtnStyle(true)}>
                Join
              </button>
            </div>
          );
        } else {
          // category === 'people'
          return (
            <div key={item.id} style={{ display: "flex", alignItems: "center", gap: "10px", padding: "12px", border: "1px solid " + V.outv, borderRadius: V.rs, background: V.surf }}>
              <Icon name="circle-user-round" size={24} style={{ color: V.sec2, flex: "none" }} />
              <div style={{ flex: 1, minWidth: 0 }}>
                <div style={{ font: "600 13.5px/1.3 " + t.font, color: V.ink }}>{item.name}</div>
                {item.username ? <div style={{ font: "400 10.5px/1.3 " + t.font, color: V.ink2, marginTop: "2px" }}>@{item.username}</div> : null}
                <div style={{ font: "400 10px/1.3 " + t.font, color: item.online ? V.ok : V.ink2, marginTop: "2px" }}>
                  {item.online ? "Online" : "Offline"}
                </div>
              </div>
              <button onClick={() => startIm(item.name)} style={actionBtnStyle(true)}>
                IM
              </button>
            </div>
          );
        }
      });
      emptyText = debouncedQuery ? `> no grid results found for “${debouncedQuery}”` : "> type a query to search grid directory";
    }
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
          <div style={{ font: "700 18px/1 " + t.dfont, letterSpacing: ".16em", color: V.pri }}>GRID DIRECTORY</div>
          <div style={{ font: "400 10.5px/1.3 " + t.font, color: V.ink2, marginTop: "2px" }}>&gt; contacts, places, events, land, groups & people</div>
        </div>
      </div>

      <div style={{ flex: "none", display: "flex", gap: "6px", padding: "0 16px 10px", overflowX: "auto", scrollbarWidth: "none" }}>
        {ALL_TABS.map((tb) => {
          const on = activeTab === tb.id;
          return (
            <div
              key={tb.id}
              onClick={() => actions.setSearchTab(tb.id)}
              style={{
                flex: "none",
                display: "flex",
                alignItems: "center",
                justifyContent: "center",
                gap: "5px",
                height: "36px",
                padding: "0 12px",
                borderRadius: V.rs,
                border: "1px solid " + (on ? V.pri : V.outv),
                background: on ? V.priC : V.surf,
                color: on ? V.onpriC : V.ink2,
                font: "700 10.5px/1 " + t.font,
                letterSpacing: ".1em",
                cursor: "pointer",
                whiteSpace: "nowrap",
              }}
            >
              <Icon name={tb.icon} size={13} />
              {tb.label}
            </div>
          );
        })}
      </div>

      {activeDirTab ? (
        <div style={{ flex: "none", display: "flex", alignItems: "center", gap: "12px", padding: "0 16px 10px", flexWrap: "wrap" }}>
          <span style={{ font: "700 11px/1 " + t.dfont, color: V.ink2, letterSpacing: ".08em" }}>MATURITY:</span>
          <label style={{ display: "flex", alignItems: "center", gap: "4px", font: "400 11.5px/1 " + t.font, color: V.ink, cursor: "pointer" }}>
            <input type="checkbox" checked={maturityGeneral} onChange={(e) => setMaturityGeneral(e.target.checked)} />
            General
          </label>
          <label style={{ display: "flex", alignItems: "center", gap: "4px", font: "400 11.5px/1 " + t.font, color: V.ink, cursor: "pointer" }}>
            <input type="checkbox" checked={maturityModerate} onChange={(e) => setMaturityModerate(e.target.checked)} />
            Moderate
          </label>
          <label style={{ display: "flex", alignItems: "center", gap: "4px", font: "400 11.5px/1 " + t.font, color: V.ink, cursor: "pointer" }}>
            <input type="checkbox" checked={maturityAdult} onChange={(e) => setMaturityAdult(e.target.checked)} />
            Adult
          </label>
        </div>
      ) : null}

      <div style={{ flex: "none", margin: "0 16px 10px", height: "44px", display: "flex", alignItems: "center", gap: "8px", padding: "0 12px", border: "1px solid " + V.outv, borderRadius: V.rs, background: V.surf }}>
        <Icon name="search" size={16} style={{ color: V.ink2 }} />
        <input
          value={state.searchQuery}
          onChange={(e) => actions.setSearchQuery(e.target.value)}
          placeholder={activeDirTab ? `search ${activeDirTab.category}...` : "filter by name"}
          style={{ flex: 1, minWidth: 0, border: "none", outline: "none", background: "transparent", font: "400 13px/1 " + t.font, color: V.ink }}
        />
      </div>

      <div style={{ flex: 1, minHeight: 0, overflowY: "auto", padding: "0 16px 16px", display: "flex", flexDirection: "column", gap: "8px" }}>
        {rows}
        {Array.isArray(rows) && rows.length === 0 && !loading ? (
          <div style={{ padding: "40px 0", textAlign: "center", font: "400 12px/1.5 " + t.font, color: V.ink2 }}>{emptyText}</div>
        ) : null}
        {activeDirTab && hasMore && !loading ? (
          <button
            onClick={() => setStart((prev) => prev + 10)}
            style={{
              margin: "12px auto 0",
              display: "block",
              padding: "8px 20px",
              borderRadius: V.rs,
              border: "1px solid " + V.pri,
              background: "transparent",
              color: V.pri,
              font: "700 11px/1 " + t.font,
              cursor: "pointer",
            }}
          >
            Load More Results
          </button>
        ) : null}
      </div>
    </div>
  );
}
