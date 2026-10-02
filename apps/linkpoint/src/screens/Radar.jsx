import { useEffect, useMemo, useState, useRef } from "react";
import { useTheme } from "../context/ThemeContext.jsx";
import { useApp } from "../context/AppContext.jsx";
import { app } from "../linkpoint/app.ts";
import Icon from "../components/Icon.jsx";
import { COMPASS } from "../data/content.js";

export default function Radar() {
  const { V, t } = useTheme();
  const { state, actions } = useApp();

  // Mode: "person" (Avatars) vs "item" (Objects & Attachments)
  // Synchronized with state.rMode ("person" / "item" or "AV" / "OBJ")
  const activeMode = useMemo(() => {
    const m = state.rMode || "person";
    if (m === "OBJ" || m === "item" || m === "ITEM" || m === "OBJECT") return "item";
    return "person";
  }, [state.rMode]);

  // Sub-filter for items: "all" | "in_sim" | "on_avatar"
  const [itemSubFilter, setItemSubFilter] = useState("all");

  // Search & Sorting
  const [searchQuery, setSearchQuery] = useState("");
  const [sortBy, setSortBy] = useState("distance"); // "distance" | "name" | "impact"
  const [groupByType, setGroupByType] = useState(false); // whether to group items into In Sim vs On Avatar sections
  const [showScope, setShowScope] = useState(true); // show/collapse radar sweep scope

  // Selection & Actions
  const [selectedId, setSelectedId] = useState(null);
  const [longPressMenuId, setLongPressMenuId] = useState(null);
  const holdTimerRef = useRef(null);
  const longPressFiredRef = useRef(false);

  // Live simulator updates
  const [liveObjects, setLiveObjects] = useState(() => [...(app.world?.objects || [])]);
  const [liveNearby, setLiveNearby] = useState(() => [...(app.world?.nearbyUsers || [])]);

  useEffect(() => {
    const handleObjectsChanged = (items) => setLiveObjects([...items]);
    const handleNearbyChanged = (items) => setLiveNearby([...items]);

    app.world?.on?.("objects_changed", handleObjectsChanged);
    app.world?.on?.("nearby_changed", handleNearbyChanged);

    return () => {
      app.world?.off?.("objects_changed", handleObjectsChanged);
      app.world?.off?.("nearby_changed", handleNearbyChanged);
    };
  }, []);

  // Distance range classifications (Firestorm style)
  const getBand = (dist) => {
    if (dist == null || !Number.isFinite(Number(dist))) return { name: "RANGE UNKNOWN", tone: V.ink2, code: "unknown" };
    const d = Number(dist);
    if (d <= 10) return { name: "WHISPER (≤10m)", tone: V.pri, code: "whisper" };
    if (d <= 20) return { name: "CHAT (≤20m)", tone: V.ok, code: "chat" };
    if (d <= 100) return { name: "SHOUT (≤100m)", tone: V.info, code: "shout" };
    return { name: "BEYOND (>100m)", tone: V.ink2, code: "beyond" };
  };

  const getCompass = (bearing) => {
    if (bearing == null || isNaN(bearing)) return "N";
    const idx = Math.round(Number(bearing) / 22.5) % 16;
    return COMPASS[idx >= 0 ? idx : (idx + 16) % 16];
  };

  // 1. Process People (Avatars)
  const peopleList = useMemo(() => {
    const seen = new Set();
    const result = [];

    // Live avatars from protocol
    liveNearby.forEach((u) => {
      if (!u.id || seen.has(u.id)) return;
      seen.add(u.id);
      const dist = u.distance != null ? Number(u.distance) : null;
      const brg = u.bearing ?? null;
      result.push({
        id: u.id,
        name: u.name || `Resident ${String(u.id).slice(0, 8)}`,
        kind: "person",
        distance: dist,
        bearing: brg,
        compass: getCompass(brg),
        meta: u.meta || "",
        icon: "user",
        isFriend: Boolean(app.friends?.isFriend?.(u.id) || app.friends?.isFriend?.(u.name)),
        typing: Boolean(u.typing),
        voice: Boolean(u.voice),
        payment: u.payment || null,
        age: u.age || null,
        altitude: u.position?.[2] ? `${Math.round(u.position[2])}m` : null,
        coords: u.position ? `<${Math.round(u.position[0])}, ${Math.round(u.position[1])}, ${Math.round(u.position[2])}>` : null,
      });
    });

    return result;
  }, [liveNearby]);

  // 2. Process Items (Objects with IN SIM vs ON-AVATAR distinction)
  const itemsList = useMemo(() => {
    const seen = new Set();
    const result = [];

    // Live scene objects from simulator
    liveObjects.forEach((obj) => {
      if (!obj.id || obj.avatar || seen.has(obj.id)) return;
      seen.add(obj.id);

      const isAttachment = Boolean(
        obj.isAttachment ||
        obj.locationType === "on_avatar" ||
        (obj.parentId && obj.parentId > 0) ||
        obj.attachmentPoint ||
        obj.attached
      );

      const locationType = isAttachment ? "on_avatar" : "in_sim";
      const dist = obj.distance != null ? Number(obj.distance) : null;
      const brg = obj.bearing ?? null;

      result.push({
        id: obj.id,
        name: obj.name || `Object ${String(obj.id).slice(0, 8)}`,
        kind: "item",
        locationType, // "in_sim" | "on_avatar"
        distance: dist,
        bearing: brg,
        compass: getCompass(brg),
        icon: isAttachment ? "layers" : "box",
        meta: obj.meta || "",
        attachedTo: obj.attachedTo || null,
        attachPoint: obj.attachmentPoint || null,
        parcel: obj.parcel || null,
        prims: obj.prims || obj.landImpact || null,
        scriptTime: obj.scriptTime || null,
        scripts: obj.scripts ?? null,
        memory: obj.memory || null,
        owner: obj.owner || null,
        coords: obj.position ? `<${Math.round(obj.position[0])}, ${Math.round(obj.position[1])}, ${Math.round(obj.position[2])}>` : null,
      });
    });

    return result;
  }, [liveObjects]);

  // Counts for tabs & filters
  const inSimCount = useMemo(() => itemsList.filter((it) => it.locationType === "in_sim").length, [itemsList]);
  const onAvatarCount = useMemo(() => itemsList.filter((it) => it.locationType === "on_avatar").length, [itemsList]);

  // Filtered & Sorted active list
  const activeEntries = useMemo(() => {
    let list = activeMode === "person" ? peopleList : itemsList;

    // Filter items by In Sim vs On-Avatar sub-filter
    if (activeMode === "item" && itemSubFilter !== "all") {
      list = list.filter((item) => item.locationType === itemSubFilter);
    }

    // Filter by search query
    if (searchQuery.trim()) {
      const q = searchQuery.toLowerCase().trim();
      list = list.filter((entry) => {
        const matchName = entry.name.toLowerCase().includes(q);
        const matchOwner = entry.owner?.toLowerCase().includes(q);
        const matchAttached = entry.attachedTo?.toLowerCase().includes(q);
        const matchParcel = entry.parcel?.toLowerCase().includes(q);
        const matchMeta = entry.meta?.toLowerCase().includes(q);
        const matchSlot = entry.attachPoint?.toLowerCase().includes(q);
        return matchName || matchOwner || matchAttached || matchParcel || matchMeta || matchSlot;
      });
    }

    // Sorting
    return [...list].sort((a, b) => {
      if (sortBy === "name") return a.name.localeCompare(b.name);
      if (sortBy === "impact") {
        const primsA = a.prims || a.scripts || 0;
        const primsB = b.prims || b.scripts || 0;
        return primsB - primsA;
      }
      return Number(a.distance ?? 9999) - Number(b.distance ?? 9999);
    });
  }, [activeMode, peopleList, itemsList, itemSubFilter, searchQuery, sortBy]);

  // Touch & Long-press handlers
  const handleItemTap = (id) => {
    if (longPressFiredRef.current) {
      longPressFiredRef.current = false;
      return;
    }
    setSelectedId((cur) => (cur === id ? null : id));
    setLongPressMenuId(null);
  };

  const handleTouchStart = (id) => {
    longPressFiredRef.current = false;
    holdTimerRef.current = setTimeout(() => {
      longPressFiredRef.current = true;
      setLongPressMenuId(id);
      setSelectedId(null);
    }, 450);
  };

  const handleTouchEnd = () => {
    if (holdTimerRef.current) {
      clearTimeout(holdTimerRef.current);
      holdTimerRef.current = null;
    }
  };

  // Actions
  const handleAction = (label, target) => {
    setSelectedId(null);
    setLongPressMenuId(null);

    if (label === "IM") {
      actions.setTab("Chat", "IM");
      actions.setChip(target.name);
      actions.setScreen("Chat");
      actions.notify(`Opened IM with ${target.name}`);
      return;
    }

    if (label === "PROFILE" || label === "VIEW PROFILE") {
      actions.setScreen("Profile");
      actions.notify(`Opened Profile for ${target.attachedTo || target.name}`);
      return;
    }

    if (label === "TELEPORT TO" || label === "OFFER TP") {
      actions.setScreen("Map");
      actions.notify(`Teleport coordinate set to ${target.name} (${target.distance}m)`);
      return;
    }

    if (label === "CAM TO" || label === "TRACK") {
      if (app.world?.camera3d && target.distance != null) {
        actions.notify(`Camera focused on ${target.name}`);
      } else {
        actions.notify(`Tracking ${target.name} (${target.distance}m, ${target.compass})`);
      }
      return;
    }

    if (label === "TOUCH") {
      actions.notify(`Touched object: ${target.name}`);
      return;
    }

    if (label === "MUTE" || label === "MUTE OBJECT") {
      if (target.kind === "person") {
        app.chatExtended?.muteUser?.(target.name);
      } else {
        app.chatExtended?.muteObject?.(target.name);
      }
      actions.notify(`Muted: ${target.name}`);
      return;
    }

    if (label === "DETACH") {
      actions.notify(`Detached worn item: ${target.name}`);
      return;
    }

    actions.notify(`${label}: ${target.name}`);
  };

  // Switch between PERSON and ITEM
  const switchRadarMode = (mode) => {
    actions.setRMode(mode);
    setSelectedId(null);
    setLongPressMenuId(null);
  };

  // Render Scope Rings & Blips
  // Sqrt scale allows close range (10m) to stay clear while 150m still fits nicely
  const rPix = (dm) => Math.min(88, 76 * Math.sqrt(Math.min(Math.max(dm, 0), 160) / 100));

  const scopeBlips = useMemo(() => {
    return activeEntries.filter((entry) => entry.distance != null && entry.bearing != null).slice(0, 24).map((entry) => {
      const dm = Number(entry.distance || 0);
      const rad = (Number(entry.bearing || 0) - 90) * (Math.PI / 180);
      const distPx = rPix(dm);
      const x = Math.cos(rad) * distPx;
      const y = Math.sin(rad) * distPx;
      const isSelected = selectedId === entry.id;

      return {
        ...entry,
        x,
        y,
        isSelected,
      };
    });
  }, [activeEntries, selectedId]);

  return (
    <div style={{ flex: 1, minHeight: 0, display: "flex", flexDirection: "column", background: V.bg, color: V.ink, overflow: "hidden" }}>
      {/* 1. PRIMARY TOGGLE: PERSON VS ITEM */}
      <div
        style={{
          flex: "none",
          display: "flex",
          alignItems: "center",
          gap: "8px",
          padding: "8px 12px",
          background: V.surf,
          borderBottom: "1px solid " + V.outv,
          zIndex: 10,
        }}
      >
        <div style={{ flex: 1, display: "flex", background: V.surf2, borderRadius: V.rs, padding: "3px", gap: "4px", border: "1px solid " + V.outv }}>
          {/* PERSON TOGGLE BUTTON */}
          <button
            onClick={() => switchRadarMode("person")}
            style={{
              flex: 1,
              display: "flex",
              alignItems: "center",
              justifyContent: "center",
              gap: "7px",
              padding: "7px 10px",
              borderRadius: V.rs,
              border: activeMode === "person" ? "1px solid " + V.pri : "1px solid transparent",
              background: activeMode === "person" ? V.pri : "transparent",
              color: activeMode === "person" ? V.onpri : V.ink,
              cursor: "pointer",
              font: "700 11.5px/1 " + t.dfont,
              letterSpacing: ".08em",
              transition: "all .15s ease",
            }}
            role="tab"
            aria-selected={activeMode === "person"}
          >
            <Icon name="user" size={15} />
            <span>PERSON</span>
            <span
              style={{
                padding: "2px 6px",
                borderRadius: "999px",
                fontSize: "10px",
                background: activeMode === "person" ? "rgba(0,0,0,0.22)" : V.surf,
                color: activeMode === "person" ? V.onpri : V.pri,
                fontWeight: 700,
              }}
            >
              {peopleList.length}
            </span>
          </button>

          {/* ITEM TOGGLE BUTTON */}
          <button
            onClick={() => switchRadarMode("item")}
            style={{
              flex: 1,
              display: "flex",
              alignItems: "center",
              justifyContent: "center",
              gap: "7px",
              padding: "7px 10px",
              borderRadius: V.rs,
              border: activeMode === "item" ? "1px solid " + V.pri : "1px solid transparent",
              background: activeMode === "item" ? V.pri : "transparent",
              color: activeMode === "item" ? V.onpri : V.ink,
              cursor: "pointer",
              font: "700 11.5px/1 " + t.dfont,
              letterSpacing: ".08em",
              transition: "all .15s ease",
            }}
            role="tab"
            aria-selected={activeMode === "item"}
          >
            <Icon name="box" size={15} />
            <span>ITEM</span>
            <span
              style={{
                padding: "2px 6px",
                borderRadius: "999px",
                fontSize: "10px",
                background: activeMode === "item" ? "rgba(0,0,0,0.22)" : V.surf,
                color: activeMode === "item" ? V.onpri : V.pri,
                fontWeight: 700,
              }}
            >
              {itemsList.length}
            </span>
          </button>
        </div>

        {/* Toggle Radar Scope Visibility */}
        <button
          onClick={() => setShowScope((s) => !s)}
          title={showScope ? "Collapse Radar Scope" : "Expand Radar Scope"}
          style={{
            flex: "none",
            width: "32px",
            height: "32px",
            display: "flex",
            alignItems: "center",
            justifyContent: "center",
            border: "1px solid " + V.outv,
            borderRadius: V.rs,
            background: showScope ? V.surf2 : "transparent",
            color: showScope ? V.pri : V.ink2,
            cursor: "pointer",
          }}
        >
          <Icon name="radar" size={16} />
        </button>
      </div>

      {/* 2. SUB-FILTERS: WHEN IN ITEM MODE -> IN SIM VS ON-AVATAR DISTINCTION */}
      {activeMode === "item" && (
        <div
          style={{
            flex: "none",
            display: "flex",
            alignItems: "center",
            gap: "6px",
            padding: "6px 12px",
            background: V.surf,
            borderBottom: "1px solid " + V.outv,
            overflowX: "auto",
          }}
        >
          <span style={{ font: "700 9.5px/1 " + t.dfont, letterSpacing: ".12em", color: V.ink2, marginRight: "4px", flex: "none" }}>
            DISTINCTION:
          </span>

          {/* ALL ITEMS */}
          <button
            onClick={() => setItemSubFilter("all")}
            style={{
              padding: "4px 10px",
              borderRadius: "999px",
              border: "1px solid " + (itemSubFilter === "all" ? V.pri : V.outv),
              background: itemSubFilter === "all" ? V.priC : "transparent",
              color: itemSubFilter === "all" ? V.onpriC : V.ink,
              font: "600 10.5px/1 " + t.font,
              cursor: "pointer",
              display: "flex",
              alignItems: "center",
              gap: "5px",
              whiteSpace: "nowrap",
            }}
          >
            <span>All Items</span>
            <span style={{ opacity: 0.8, fontSize: "9px" }}>({itemsList.length})</span>
          </button>

          {/* IN SIM DISTINCTION */}
          <button
            onClick={() => setItemSubFilter("in_sim")}
            style={{
              padding: "4px 10px",
              borderRadius: "999px",
              border: "1px solid " + (itemSubFilter === "in_sim" ? V.ok : V.outv),
              background: itemSubFilter === "in_sim" ? "rgba(34, 197, 94, 0.18)" : "transparent",
              color: itemSubFilter === "in_sim" ? V.ok : V.ink,
              font: "700 10.5px/1 " + t.font,
              cursor: "pointer",
              display: "flex",
              alignItems: "center",
              gap: "5px",
              whiteSpace: "nowrap",
            }}
          >
            <span style={{ width: "7px", height: "7px", borderRadius: "50%", background: V.ok, display: "inline-block" }} />
            <span>IN SIM (Rezzed)</span>
            <span style={{ opacity: 0.8, fontSize: "9px" }}>({inSimCount})</span>
          </button>

          {/* ON-AVATAR DISTINCTION */}
          <button
            onClick={() => setItemSubFilter("on_avatar")}
            style={{
              padding: "4px 10px",
              borderRadius: "999px",
              border: "1px solid " + (itemSubFilter === "on_avatar" ? V.sec2 : V.outv),
              background: itemSubFilter === "on_avatar" ? "rgba(168, 85, 247, 0.18)" : "transparent",
              color: itemSubFilter === "on_avatar" ? V.sec2 : V.ink,
              font: "700 10.5px/1 " + t.font,
              cursor: "pointer",
              display: "flex",
              alignItems: "center",
              gap: "5px",
              whiteSpace: "nowrap",
            }}
          >
            <span style={{ width: "7px", height: "7px", borderRadius: "50%", background: V.sec2, display: "inline-block" }} />
            <span>ON-AVATAR (Worn / HUDs)</span>
            <span style={{ opacity: 0.8, fontSize: "9px" }}>({onAvatarCount})</span>
          </button>

          {/* Grouping toggle */}
          <div style={{ marginLeft: "auto", display: "flex", alignItems: "center", gap: "4px", flex: "none" }}>
            <button
              onClick={() => setGroupByType((g) => !g)}
              title="Toggle Grouped View"
              style={{
                padding: "3px 8px",
                borderRadius: V.rs,
                border: "1px solid " + (groupByType ? V.pri : V.outv),
                background: groupByType ? V.priC : "transparent",
                color: groupByType ? V.pri : V.ink2,
                fontSize: "10px",
                fontFamily: t.dfont,
                cursor: "pointer",
              }}
            >
              {groupByType ? "GROUPED" : "FLAT"}
            </button>
          </div>
        </div>
      )}

      {/* 3. SEARCH & SORT BAR */}
      <div
        style={{
          flex: "none",
          display: "flex",
          alignItems: "center",
          gap: "8px",
          padding: "6px 12px",
          background: V.surf2,
          borderBottom: "1px solid " + V.outv,
        }}
      >
        <div style={{ flex: 1, display: "flex", alignItems: "center", gap: "6px", background: V.surf, border: "1px solid " + V.outv, borderRadius: V.rs, padding: "0 8px", height: "28px" }}>
          <Icon name="search" size={13} style={{ color: V.ink2 }} />
          <input
            type="text"
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            placeholder={activeMode === "person" ? "Filter residents by name, status..." : "Filter items by name, parcel, avatar..."}
            style={{
              flex: 1,
              background: "transparent",
              border: "none",
              outline: "none",
              color: V.ink,
              fontSize: "11px",
              fontFamily: t.font,
            }}
          />
          {searchQuery && (
            <button
              onClick={() => setSearchQuery("")}
              style={{ background: "none", border: "none", color: V.ink2, cursor: "pointer", padding: "0 2px" }}
            >
              &times;
            </button>
          )}
        </div>

        {/* Sort selector */}
        <select
          value={sortBy}
          onChange={(e) => setSortBy(e.target.value)}
          style={{
            height: "28px",
            background: V.surf,
            color: V.ink,
            border: "1px solid " + V.outv,
            borderRadius: V.rs,
            padding: "0 6px",
            fontSize: "10px",
            fontFamily: t.dfont,
            cursor: "pointer",
          }}
        >
          <option value="distance">SORT: DISTANCE</option>
          <option value="name">SORT: NAME</option>
          {activeMode === "item" && <option value="impact">SORT: PRIMS / IMPACT</option>}
        </select>
      </div>

      {/* 4. CIRCULAR RADAR SCOPE (FIRESTORM CONCENTRIC RINGS & BLIPS) */}
      {showScope && (
        <div
          style={{
            flex: "none",
            height: "148px",
            position: "relative",
            background: "radial-gradient(circle, " + V.surf + " 0%, " + V.bg + " 100%)",
            borderBottom: "1px solid " + V.outv,
            display: "flex",
            alignItems: "center",
            justifyContent: "center",
            overflow: "hidden",
          }}
        >
          {/* Subtle Grid Backdrop */}
          <div
            style={{
              position: "absolute",
              inset: 0,
              backgroundImage: "linear-gradient(" + V.outv + " 1px, transparent 1px), linear-gradient(90deg, " + V.outv + " 1px, transparent 1px)",
              backgroundSize: "20px 20px",
              opacity: 0.25,
            }}
          />

          {/* Compass Axis Lines */}
          <div style={{ position: "absolute", width: "100%", height: "1px", background: V.outv, opacity: 0.4 }} />
          <div style={{ position: "absolute", height: "100%", width: "1px", background: V.outv, opacity: 0.4 }} />

          {/* Compass Cardinal Indicators */}
          <span style={{ position: "absolute", top: "4px", font: "700 8.5px/1 " + t.dfont, color: V.ink2, letterSpacing: ".1em" }}>N</span>
          <span style={{ position: "absolute", bottom: "4px", font: "700 8.5px/1 " + t.dfont, color: V.ink2, letterSpacing: ".1em" }}>S</span>
          <span style={{ position: "absolute", right: "6px", font: "700 8.5px/1 " + t.dfont, color: V.ink2, letterSpacing: ".1em" }}>E</span>
          <span style={{ position: "absolute", left: "6px", font: "700 8.5px/1 " + t.dfont, color: V.ink2, letterSpacing: ".1em" }}>W</span>

          {/* Concentric Range Rings: Whisper (10m), Chat (20m), Shout (100m) */}
          {[
            { dm: 10, label: "10m", color: V.pri, opacity: 0.35 },
            { dm: 20, label: "20m", color: V.ok, opacity: 0.3 },
            { dm: 100, label: "100m", color: V.info, opacity: 0.2 },
          ].map((ring, idx) => {
            const r = rPix(ring.dm);
            return (
              <div
                key={idx}
                style={{
                  position: "absolute",
                  left: "50%",
                  top: "50%",
                  width: `${r * 2}px`,
                  height: `${r * 2}px`,
                  transform: "translate(-50%, -50%)",
                  borderRadius: "50%",
                  border: `1px solid ${ring.color}`,
                  opacity: ring.opacity,
                  pointerEvents: "none",
                }}
              />
            );
          })}

          {/* Central Ping for "You" */}
          <div
            style={{
              position: "absolute",
              left: "50%",
              top: "50%",
              width: "12px",
              height: "12px",
              transform: "translate(-50%, -50%)",
              borderRadius: "50%",
              background: V.pri,
              opacity: 0.3,
              animation: "ping 2.5s cubic-bezier(0, 0, 0.2, 1) infinite",
            }}
          />
          <div
            style={{
              position: "absolute",
              left: "50%",
              top: "50%",
              width: "6px",
              height: "6px",
              transform: "translate(-50%, -50%)",
              borderRadius: "50%",
              background: V.pri,
              zIndex: 5,
            }}
            title="You (Center)"
          />

          {/* Interactive Radar Blips */}
          {scopeBlips.map((blip) => {
            const isAvatar = blip.kind === "person";
            const isInSim = blip.locationType === "in_sim";
            const blipColor = isAvatar ? V.pri : isInSim ? V.ok : V.sec2;

            return (
              <div
                key={blip.id}
                onClick={(e) => {
                  e.stopPropagation();
                  setSelectedId(blip.id);
                }}
                title={`${blip.name} (${Math.round(blip.distance)}m, ${blip.compass})`}
                style={{
                  position: "absolute",
                  left: `calc(50% + ${blip.x}px)`,
                  top: `calc(50% + ${blip.y}px)`,
                  transform: "translate(-50%, -50%)",
                  width: blip.isSelected ? "11px" : "7px",
                  height: blip.isSelected ? "11px" : "7px",
                  borderRadius: isInSim ? "1px" : "50%",
                  background: blipColor,
                  border: blip.isSelected ? "2px solid #fff" : "1px solid rgba(0,0,0,0.5)",
                  boxShadow: blip.isSelected ? `0 0 8px ${blipColor}` : "none",
                  cursor: "pointer",
                  zIndex: blip.isSelected ? 8 : 4,
                  transition: "all .15s ease",
                }}
              />
            );
          })}

          {/* Legend Info */}
          <div
            style={{
              position: "absolute",
              bottom: "4px",
              right: "8px",
              display: "flex",
              gap: "8px",
              fontSize: "8.5px",
              fontFamily: t.dfont,
              color: V.ink2,
            }}
          >
            {activeMode === "item" ? (
              <>
                <span style={{ display: "flex", alignItems: "center", gap: "3px" }}>
                  <span style={{ width: "6px", height: "6px", background: V.ok, display: "inline-block" }} />
                  IN SIM
                </span>
                <span style={{ display: "flex", alignItems: "center", gap: "3px" }}>
                  <span style={{ width: "6px", height: "6px", borderRadius: "50%", background: V.sec2, display: "inline-block" }} />
                  ON-AVATAR
                </span>
              </>
            ) : (
              <>
                <span style={{ display: "flex", alignItems: "center", gap: "3px" }}>
                  <span style={{ width: "6px", height: "6px", borderRadius: "50%", background: V.pri, display: "inline-block" }} />
                  WHISPER 10m
                </span>
                <span style={{ display: "flex", alignItems: "center", gap: "3px" }}>
                  <span style={{ width: "6px", height: "6px", borderRadius: "50%", background: V.ok, display: "inline-block" }} />
                  CHAT 20m
                </span>
              </>
            )}
          </div>
        </div>
      )}

      {/* 5. RADAR LIST ENTRIES */}
      <div style={{ flex: 1, minHeight: 0, overflowY: "auto", padding: "8px 12px", display: "flex", flexDirection: "column", gap: "6px" }}>
        {activeEntries.length === 0 ? (
          <div style={{ padding: "32px 16px", textAlign: "center", color: V.ink2 }}>
            <Icon name="radar" size={36} style={{ margin: "0 auto 12px", opacity: 0.6 }} />
            <h3 style={{ font: "700 14px/1.3 " + t.dfont, color: V.ink, marginBottom: "4px" }}>
              {activeMode === "person" ? "NO RESIDENTS IN RANGE" : "NO SIMULATOR ITEMS IN RANGE"}
            </h3>
            <p style={{ fontSize: "11px", maxWidth: "300px", margin: "0 auto" }}>
              {searchQuery
                ? `No results matching "${searchQuery}". Try clearing search filter.`
                : activeMode === "person"
                ? "No other avatars within region draw distance."
                : "No items matching selected location filter."}
            </p>
          </div>
        ) : (
          activeEntries.map((entry) => {
            const isSelected = selectedId === entry.id;
            const isLongPressMenu = longPressMenuId === entry.id;
            const band = getBand(entry.distance);
            const isAvatar = entry.kind === "person";
            const isInSim = entry.locationType === "in_sim";
            const isOnAvatar = entry.locationType === "on_avatar";

            return (
              <div
                key={entry.id}
                style={{
                  border: isSelected || isLongPressMenu ? `1px solid ${band.tone}` : `1px solid ${V.outv}`,
                  borderRadius: V.rs,
                  background: isSelected ? V.surf2 : V.surf,
                  transition: "all .12s ease",
                  overflow: "hidden",
                }}
              >
                {/* Main Row Content */}
                <div
                  onClick={() => handleItemTap(entry.id)}
                  onTouchStart={() => handleTouchStart(entry.id)}
                  onTouchEnd={handleTouchEnd}
                  onMouseDown={() => handleTouchStart(entry.id)}
                  onMouseUp={handleTouchEnd}
                  onMouseLeave={handleTouchEnd}
                  style={{
                    display: "flex",
                    alignItems: "flex-start",
                    gap: "10px",
                    padding: "9px 12px",
                    cursor: "pointer",
                  }}
                >
                  {/* Left Icon with Band Tone */}
                  <div
                    style={{
                      width: "32px",
                      height: "32px",
                      borderRadius: V.rs,
                      background: isAvatar ? V.priC : isInSim ? "rgba(34, 197, 94, 0.14)" : "rgba(168, 85, 247, 0.14)",
                      color: isAvatar ? V.pri : isInSim ? V.ok : V.sec2,
                      display: "flex",
                      alignItems: "center",
                      justifyContent: "center",
                      flex: "none",
                      marginTop: "2px",
                    }}
                  >
                    <Icon name={entry.icon} size={16} />
                  </div>

                  {/* Center Details */}
                  <div style={{ flex: 1, minWidth: 0 }}>
                    <div style={{ display: "flex", alignItems: "center", gap: "6px", flexWrap: "wrap" }}>
                      <strong style={{ font: "600 13px/1.2 " + t.font, color: V.ink }}>
                        {entry.name}
                      </strong>

                      {/* ITEM LOCATION DISTINCTION BADGE */}
                      {!isAvatar && isInSim && (
                        <span
                          style={{
                            padding: "1px 6px",
                            borderRadius: "4px",
                            background: "rgba(34, 197, 94, 0.18)",
                            color: V.ok,
                            fontSize: "9px",
                            fontWeight: 800,
                            fontFamily: t.dfont,
                            letterSpacing: ".06em",
                            border: "1px solid rgba(34, 197, 94, 0.35)",
                          }}
                        >
                          IN SIM
                        </span>
                      )}

                      {!isAvatar && isOnAvatar && (
                        <span
                          style={{
                            padding: "1px 6px",
                            borderRadius: "4px",
                            background: "rgba(168, 85, 247, 0.18)",
                            color: V.sec2,
                            fontSize: "9px",
                            fontWeight: 800,
                            fontFamily: t.dfont,
                            letterSpacing: ".06em",
                            border: "1px solid rgba(168, 85, 247, 0.35)",
                          }}
                        >
                          ON AVATAR
                        </span>
                      )}

                      {/* Person Badges */}
                      {isAvatar && entry.isFriend && (
                        <span
                          style={{
                            padding: "1px 5px",
                            borderRadius: "4px",
                            background: V.priC,
                            color: V.pri,
                            fontSize: "8.5px",
                            fontWeight: 700,
                          }}
                        >
                          FRIEND
                        </span>
                      )}

                      {isAvatar && entry.typing && (
                        <span
                          style={{
                            padding: "1px 5px",
                            borderRadius: "4px",
                            background: "rgba(59, 130, 246, 0.15)",
                            color: V.info,
                            fontSize: "8.5px",
                            fontWeight: 700,
                          }}
                        >
                          TYPING
                        </span>
                      )}
                    </div>

                    {/* Secondary Information Line */}
                    <div style={{ font: "400 11px/1.3 " + t.font, color: V.ink2, marginTop: "2px" }}>
                      {!isAvatar && isInSim && (
                        <span>
                          {entry.parcel ? `Parcel: ${entry.parcel} · ` : ""}
                          {entry.prims ? `${entry.prims} prims · ` : ""}
                          {entry.scriptTime ? `${entry.scriptTime} script` : ""}
                          {entry.owner ? ` · Owner: ${entry.owner}` : ""}
                        </span>
                      )}

                      {!isAvatar && isOnAvatar && (
                        <span>
                          <strong style={{ color: V.sec2 }}>
                            {entry.attachedTo === "you" ? "Worn by you" : `Worn by ${entry.attachedTo}`}
                          </strong>
                          {entry.attachPoint ? ` · ${entry.attachPoint}` : ""}
                          {entry.scripts ? ` · ${entry.scripts} scripts${entry.memory ? ` (${entry.memory})` : ""}` : ""}
                        </span>
                      )}

                      {isAvatar && (
                        <span>
                          {entry.coords ? `${entry.coords} · ` : ""}{entry.meta}
                        </span>
                      )}
                    </div>
                  </div>

                  {/* Right Distance & Bearing Pill */}
                  <div style={{ flex: "none", textAlign: "right" }}>
                    <div
                      style={{
                        padding: "3px 7px",
                        borderRadius: V.rs,
                        border: `1px solid ${band.tone}`,
                        color: band.tone,
                        font: "700 11px/1 " + t.dfont,
                        background: "transparent",
                        display: "inline-block",
                      }}
                    >
                      {entry.distance != null ? `${Math.round(entry.distance)}m` : "Nearby"}
                    </div>
                    <div style={{ fontSize: "9px", fontFamily: t.dfont, color: V.ink2, marginTop: "3px" }}>
                      {entry.compass} · {Math.round(entry.bearing || 0)}°
                    </div>
                  </div>
                </div>

                {/* 6. EXPANDED ACTIONS ON TAP */}
                {isSelected && (
                  <div
                    style={{
                      borderTop: "1px solid " + V.outv,
                      background: V.surf2,
                      padding: "8px 12px",
                      display: "flex",
                      flexWrap: "wrap",
                      gap: "6px",
                    }}
                  >
                    {isAvatar ? (
                      <>
                        <button
                          onClick={() => handleAction("PROFILE", entry)}
                          style={{
                            padding: "5px 10px",
                            borderRadius: V.rs,
                            border: "1px solid " + V.outv,
                            background: V.surf,
                            color: V.ink,
                            font: "600 10.5px/1 " + t.dfont,
                            cursor: "pointer",
                          }}
                        >
                          PROFILE
                        </button>
                        <button
                          onClick={() => handleAction("IM", entry)}
                          style={{
                            padding: "5px 10px",
                            borderRadius: V.rs,
                            border: "1px solid " + V.pri,
                            background: V.pri,
                            color: V.onpri,
                            font: "700 10.5px/1 " + t.dfont,
                            cursor: "pointer",
                          }}
                        >
                          IM CHAT
                        </button>
                        <button
                          onClick={() => handleAction("TELEPORT TO", entry)}
                          style={{
                            padding: "5px 10px",
                            borderRadius: V.rs,
                            border: "1px solid " + V.outv,
                            background: V.surf,
                            color: V.ink,
                            font: "600 10.5px/1 " + t.dfont,
                            cursor: "pointer",
                          }}
                        >
                          TELEPORT TO
                        </button>
                        <button
                          onClick={() => handleAction("CAM TO", entry)}
                          style={{
                            padding: "5px 10px",
                            borderRadius: V.rs,
                            border: "1px solid " + V.outv,
                            background: V.surf,
                            color: V.ink,
                            font: "600 10.5px/1 " + t.dfont,
                            cursor: "pointer",
                          }}
                        >
                          CAM / TRACK
                        </button>
                        <button
                          onClick={() => handleAction("MUTE", entry)}
                          style={{
                            padding: "5px 10px",
                            borderRadius: V.rs,
                            border: "1px solid " + V.err,
                            background: "transparent",
                            color: V.err,
                            font: "600 10.5px/1 " + t.dfont,
                            cursor: "pointer",
                            marginLeft: "auto",
                          }}
                        >
                          MUTE
                        </button>
                      </>
                    ) : isInSim ? (
                      /* IN SIM OBJECT ACTIONS */
                      <>
                        <button
                          onClick={() => handleAction("TOUCH", entry)}
                          style={{
                            padding: "5px 10px",
                            borderRadius: V.rs,
                            border: "1px solid " + V.pri,
                            background: V.pri,
                            color: V.onpri,
                            font: "700 10.5px/1 " + t.dfont,
                            cursor: "pointer",
                          }}
                        >
                          TOUCH
                        </button>
                        <button
                          onClick={() => handleAction("CAM TO", entry)}
                          style={{
                            padding: "5px 10px",
                            borderRadius: V.rs,
                            border: "1px solid " + V.outv,
                            background: V.surf,
                            color: V.ink,
                            font: "600 10.5px/1 " + t.dfont,
                            cursor: "pointer",
                          }}
                        >
                          CAM TO OBJECT
                        </button>
                        <button
                          onClick={() => handleAction("INSPECT", entry)}
                          style={{
                            padding: "5px 10px",
                            borderRadius: V.rs,
                            border: "1px solid " + V.outv,
                            background: V.surf,
                            color: V.ink,
                            font: "600 10.5px/1 " + t.dfont,
                            cursor: "pointer",
                          }}
                        >
                          INSPECT PRIMS
                        </button>
                        <button
                          onClick={() => handleAction("MUTE OBJECT", entry)}
                          style={{
                            padding: "5px 10px",
                            borderRadius: V.rs,
                            border: "1px solid " + V.err,
                            background: "transparent",
                            color: V.err,
                            font: "600 10.5px/1 " + t.dfont,
                            cursor: "pointer",
                            marginLeft: "auto",
                          }}
                        >
                          MUTE OBJECT
                        </button>
                      </>
                    ) : (
                      /* ON-AVATAR ATTACHMENT ACTIONS */
                      <>
                        {entry.attachedTo && (
                          <button
                            onClick={() => handleAction("VIEW PROFILE", entry)}
                            style={{
                              padding: "5px 10px",
                              borderRadius: V.rs,
                              border: "1px solid " + V.outv,
                              background: V.surf,
                              color: V.ink,
                              font: "600 10.5px/1 " + t.dfont,
                              cursor: "pointer",
                            }}
                          >
                            WEARER PROFILE
                          </button>
                        )}
                        <button
                          onClick={() => handleAction("CAM TO", entry)}
                          style={{
                            padding: "5px 10px",
                            borderRadius: V.rs,
                            border: "1px solid " + V.outv,
                            background: V.surf,
                            color: V.ink,
                            font: "600 10.5px/1 " + t.dfont,
                            cursor: "pointer",
                          }}
                        >
                          FOCUS ATTACHMENT
                        </button>
                        {entry.attachedTo === "you" ? (
                          <button
                            onClick={() => handleAction("DETACH", entry)}
                            style={{
                              padding: "5px 10px",
                              borderRadius: V.rs,
                              border: "1px solid " + V.err,
                              background: "transparent",
                              color: V.err,
                              font: "700 10.5px/1 " + t.dfont,
                              cursor: "pointer",
                            }}
                          >
                            DETACH FROM AVATAR
                          </button>
                        ) : (
                          <button
                            onClick={() => handleAction("MUTE OBJECT", entry)}
                            style={{
                              padding: "5px 10px",
                              borderRadius: V.rs,
                              border: "1px solid " + V.err,
                              background: "transparent",
                              color: V.err,
                              font: "600 10.5px/1 " + t.dfont,
                              cursor: "pointer",
                              marginLeft: "auto",
                            }}
                          >
                            MUTE SCRIPTS
                          </button>
                        )}
                      </>
                    )}
                  </div>
                )}

                {/* 7. LONG-PRESS MODERATOR MENU */}
                {isLongPressMenu && (
                  <div
                    style={{
                      borderTop: "1px solid " + V.err,
                      background: "rgba(239, 68, 68, 0.08)",
                      padding: "8px 12px",
                      display: "flex",
                      flexDirection: "column",
                      gap: "6px",
                    }}
                  >
                    <div style={{ font: "700 9px/1 " + t.dfont, letterSpacing: ".15em", color: V.err }}>
                      {isAvatar ? "MODERATOR / ESTATE ACTIONS (LONG PRESS)" : "OBJECT MODERATOR ACTIONS (LONG PRESS)"}
                    </div>
                    <div style={{ display: "flex", flexWrap: "wrap", gap: "6px" }}>
                      {(isAvatar
                        ? ["EJECT", "FREEZE", "BAN", "REPORT ABUSE"]
                        : isInSim
                        ? ["RETURN TO OWNER", "DERENDER", "BLOCK SOUND", "REPORT PRIM"]
                        : ["DERENDER ATTACHMENT", "BLOCK SCRIPTS", "REPORT ATTACHMENT"]
                      ).map((label) => (
                        <button
                          key={label}
                          onClick={() => handleAction(label, entry)}
                          style={{
                            padding: "4px 8px",
                            borderRadius: V.rs,
                            border: "1px solid " + V.err,
                            background: "transparent",
                            color: V.err,
                            font: "700 9.5px/1 " + t.dfont,
                            cursor: "pointer",
                          }}
                        >
                          {label}
                        </button>
                      ))}
                    </div>
                  </div>
                )}
              </div>
            );
          })
        )}
      </div>

      {/* 8. FOOTER SUMMARY STATUS BAR */}
      <div
        style={{
          flex: "none",
          padding: "5px 12px",
          background: V.surf,
          borderTop: "1px solid " + V.outv,
          display: "flex",
          alignItems: "center",
          justifyContent: "space-between",
          fontSize: "10px",
          fontFamily: t.dfont,
          color: V.ink2,
        }}
      >
        <span>
          RADAR: {activeMode === "person" ? `${peopleList.length} RESIDENTS` : `${itemsList.length} ITEMS (${inSimCount} IN SIM, ${onAvatarCount} ON AVATAR)`}
        </span>
        <span>
          DRAW DISTANCE: 128m
        </span>
      </div>
    </div>
  );
}
