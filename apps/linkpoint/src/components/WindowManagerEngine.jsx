import React, { useState, useEffect, useCallback, useMemo } from "react";
import { useApp } from "../context/AppContext.jsx";
import { useTheme } from "../context/ThemeContext.jsx";
import ViewportTile from "./ViewportTile.jsx";
import Icon from "./Icon.jsx";

// Import feature screens
import Chat from "../screens/Chat.jsx";
import Radar from "../screens/Radar.jsx";
import Map from "../screens/Map.jsx";
import World3D, { World3DActionBar } from "../screens/World3D.jsx";
import Inventory from "../screens/Inventory.jsx";
import Profile from "../screens/Profile.jsx";
import Login from "../screens/Login.jsx";
import CacheScreen from "../screens/CacheScreen.jsx";
import Settings from "../screens/Settings.jsx";
import ContactsScreen from "../screens/ContactsScreen.jsx";
import CalendarScreen from "../screens/CalendarScreen.jsx";
import OutfitViewer from "../screens/OutfitViewer.jsx";
import {
  FriendsScreen,
  GroupsScreen,
  NoticesScreen,
  MuteListScreen,
  GenericInventoryScreen,
  SearchScreen,
} from "../screens/LiveScreens.jsx";
import {
  AOScreen,
  AccountsScreen,
  DiagnosticsScreen,
  GridsScreen,
  MediaScreen,
  NotecardsScreen,
  ParcelScreen,
  TeleportScreen,
  TransactionsScreen,
} from "../screens/LumiyaTools.jsx";

// Workspace Presets definition
export const WORKSPACE_PRESETS = {
  inworld: {
    id: "inworld",
    name: "In-World Explorer",
    icon: "box",
    tiles: ["3D View", "Map", "Radar"],
    layout: "split",
  },
  chatsocial: {
    id: "chatsocial",
    name: "Chat & Social",
    icon: "message-square",
    tiles: ["Chat", "Friends", "Groups"],
    layout: "split",
  },
  inventory: {
    id: "inventory",
    name: "Inventory Editor",
    icon: "folder",
    tiles: ["Inventory", "Outfits", "Objects"],
    layout: "masonry",
  },
  custom: {
    id: "custom",
    name: "Custom Workspace",
    icon: "grid",
    tiles: ["Chat", "Map", "Inventory"],
    layout: "split",
  },
};

const SCREEN_METADATA = {
  "3D View": { icon: "box", title: "3D World View" },
  Chat: { icon: "message-square", title: "Chat & IM" },
  Friends: { icon: "users", title: "Friends List" },
  Radar: { icon: "radar", title: "Radar & Nearby" },
  Map: { icon: "map", title: "World Map" },
  Inventory: { icon: "folder", title: "Inventory" },
  Outfits: { icon: "shirt", title: "Outfits & Appearance" },
  Objects: { icon: "box", title: "Object Inspector" },
  Groups: { icon: "users", title: "Groups" },
  Profile: { icon: "user", title: "Resident Profile" },
  Settings: { icon: "settings", title: "Viewer Settings" },
  Search: { icon: "search", title: "Search" },
  Parcel: { icon: "map-pin", title: "Parcel Info" },
  Teleport: { icon: "navigation", title: "Teleport History" },
  Transactions: { icon: "banknote", title: "L$ Transactions" },
  Notecards: { icon: "file-text", title: "Notecards Editor" },
  Notices: { icon: "bell", title: "Notices & Feeds" },
  Diagnostics: { icon: "activity", title: "System Diagnostics" },
  Contacts: { icon: "contact", title: "Contacts" },
  Calendar: { icon: "calendar", title: "Calendar" },
  Media: { icon: "radio", title: "Media Settings" },
  Accounts: { icon: "user-check", title: "Grid Accounts" },
  Grids: { icon: "network", title: "Grid Management" },
  Cache: { icon: "database", title: "Cache Manager" },
  AO: { icon: "zap", title: "Animation Overrider" },
  "Mute List": { icon: "volume-x", title: "Mute List" },
  Login: { icon: "log-in", title: "Login & Auth" },
};

/**
 * Render feature content component based on screen ID
 */
function renderScreenContent(screenId) {
  switch (screenId) {
    case "3D View":
      return (
        <div style={{ flex: 1, display: "flex", flexDirection: "column", minHeight: 0 }}>
          <World3D />
          <World3DActionBar />
        </div>
      );
    case "Chat":
      return <Chat />;
    case "Radar":
      return <Radar />;
    case "Map":
      return <Map />;
    case "Inventory":
      return <Inventory />;
    case "Profile":
      return <Profile />;
    case "Cache":
      return <CacheScreen />;
    case "Settings":
      return <Settings />;
    case "Friends":
      return <FriendsScreen />;
    case "Contacts":
      return <ContactsScreen />;
    case "Calendar":
      return <CalendarScreen />;
    case "Groups":
      return <GroupsScreen />;
    case "Notices":
      return <NoticesScreen />;
    case "Mute List":
      return <MuteListScreen />;
    case "Outfits":
      return <OutfitViewer />;
    case "Objects":
      return <GenericInventoryScreen kind="object" />;
    case "Notecards":
      return <NotecardsScreen />;
    case "Media":
      return <MediaScreen />;
    case "Accounts":
      return <AccountsScreen />;
    case "Grids":
      return <GridsScreen />;
    case "Teleport":
      return <TeleportScreen />;
    case "Parcel":
      return <ParcelScreen />;
    case "Transactions":
      return <TransactionsScreen />;
    case "Diagnostics":
      return <DiagnosticsScreen />;
    case "AO":
      return <AOScreen />;
    case "Login":
      return <Login />;
    case "Search":
      return <SearchScreen />;
    default:
      return <div style={{ padding: "16px" }}>Screen content for {screenId}</div>;
  }
}

/**
 * WindowManagerEngine provides multi-pane split view, masonry grid,
 * dockable floaters, and workspace presets across all feature routes.
 */
export default function WindowManagerEngine({
  presetOverride = null,
  compactBar = false,
}) {
  const { state, actions } = useApp();
  const { V, t, ink } = useTheme();

  // Active workspace preset
  const [preset, setPreset] = useState(presetOverride || "inworld");
  // Active docked tile IDs
  const [dockedTiles, setDockedTiles] = useState(() => WORKSPACE_PRESETS.inworld.tiles);
  // Floating tile IDs
  const [floatingTiles, setFloatingTiles] = useState([]);
  // Floating rectangles: { [id]: { x, y, w, h } }
  const [floatingRects, setFloatingRects] = useState({
    Objects: { x: 80, y: 60, w: 340, h: 280 },
    Profile: { x: 120, y: 80, w: 320, h: 300 },
  });
  // Active z-index ordering for floaters
  const [floatingZ, setFloatingZ] = useState(["Objects", "Profile"]);
  // Layout mode: 'split' | 'masonry' | 'floaters' | 'single'
  const [layoutMode, setLayoutMode] = useState("split");
  // Global density: 'compact' | 'normal' | 'spacious'
  const [globalDensity, setGlobalDensity] = useState("normal");
  // Per-tile density overrides
  const [tileDensityMap, setTileDensityMap] = useState({});
  // Active tile ID
  const [activeTileId, setActiveTileId] = useState(state.screen || "3D View");
  // Minimized tiles map
  const [minimizedTiles, setMinimizedTiles] = useState({});
  // Split direction: 'row' | 'column'
  const [splitDirection, setSplitDirection] = useState("row");
  // Split proportions for tiles
  const [splitRatios, setSplitRatios] = useState([1, 1, 1]);

  // Sync state.screen with active tile
  useEffect(() => {
    if (state.screen && state.screen !== activeTileId) {
      setActiveTileId(state.screen);
      // Ensure the screen is present either in docked or floating tiles
      if (!dockedTiles.includes(state.screen) && !floatingTiles.includes(state.screen)) {
        setDockedTiles((tiles) => [...tiles, state.screen]);
      }
    }
  }, [state.screen]);

  // Handle Preset Selection
  const applyPreset = useCallback((presetKey) => {
    const config = WORKSPACE_PRESETS[presetKey];
    if (!config) return;
    setPreset(presetKey);
    setDockedTiles([...config.tiles]);
    setLayoutMode(config.layout || "split");
    setActiveTileId(config.tiles[0] || "3D View");
    if (actions.setScreen) {
      actions.setScreen(config.tiles[0] || "3D View");
    }
    actions.notify?.(`Loaded Preset: ${config.name}`);
  }, [actions]);

  // Focus tile and bring to front
  const focusTile = useCallback((id) => {
    setActiveTileId(id);
    if (actions.setScreen) {
      actions.setScreen(id);
    }
    if (floatingTiles.includes(id)) {
      setFloatingZ((zList) => [...zList.filter((x) => x !== id), id]);
    }
  }, [floatingTiles, actions]);

  // Float tile
  const undockTile = useCallback((id) => {
    setDockedTiles((tiles) => tiles.filter((x) => x !== id));
    if (!floatingTiles.includes(id)) {
      setFloatingTiles((floats) => [...floats, id]);
      setFloatingZ((zList) => [...zList.filter((x) => x !== id), id]);
    }
    if (!floatingRects[id]) {
      setFloatingRects((rects) => ({
        ...rects,
        [id]: { x: 60 + floatingTiles.length * 20, y: 50 + floatingTiles.length * 20, w: 360, h: 280 },
      }));
    }
    focusTile(id);
  }, [floatingTiles, floatingRects, focusTile]);

  // Dock tile
  const dockTile = useCallback((id) => {
    setFloatingTiles((floats) => floats.filter((x) => x !== id));
    if (!dockedTiles.includes(id)) {
      setDockedTiles((tiles) => [...tiles, id]);
    }
    focusTile(id);
  }, [dockedTiles, focusTile]);

  // Close tile
  const closeTile = useCallback((id) => {
    setDockedTiles((tiles) => tiles.filter((x) => x !== id));
    setFloatingTiles((floats) => floats.filter((x) => x !== id));
  }, []);

  // Minimize tile
  const toggleMinimizeTile = useCallback((id) => {
    setMinimizedTiles((minMap) => ({ ...minMap, [id]: !minMap[id] }));
  }, []);

  // Toggle per-tile density
  const cycleTileDensity = useCallback((id) => {
    const current = tileDensityMap[id] || globalDensity;
    const next =
      current === "compact"
        ? "normal"
        : current === "normal"
        ? "spacious"
        : "compact";
    setTileDensityMap((m) => ({ ...m, [id]: next }));
  }, [tileDensityMap, globalDensity]);

  // Add new tile to docked layout
  const addTileToDock = useCallback((id) => {
    if (!dockedTiles.includes(id)) {
      setDockedTiles((tiles) => [...tiles, id]);
    }
    focusTile(id);
  }, [dockedTiles, focusTile]);

  // Dragging floating tile with snapping
  const handleDragFloating = useCallback((id, e) => {
    e.preventDefault();
    e.stopPropagation();
    const isTouch = e.type.startsWith("touch");
    const startX = isTouch ? e.touches[0].clientX : e.clientX;
    const startY = isTouch ? e.touches[0].clientY : e.clientY;
    const initialRect = floatingRects[id] || { x: 50, y: 50, w: 320, h: 240 };

    const SNAP_THRESHOLD = 16;

    const onMove = (ev) => {
      const curX = isTouch ? ev.touches[0].clientX : ev.clientX;
      const curY = isTouch ? ev.touches[0].clientY : ev.clientY;
      let newX = Math.max(0, initialRect.x + (curX - startX));
      let newY = Math.max(0, initialRect.y + (curY - startY));

      // Boundary snapping
      if (Math.abs(newX) < SNAP_THRESHOLD) newX = 0;
      if (Math.abs(newY) < SNAP_THRESHOLD) newY = 0;

      setFloatingRects((rects) => ({
        ...rects,
        [id]: { ...initialRect, x: newX, y: newY },
      }));
    };

    const onEnd = () => {
      window.removeEventListener("mousemove", onMove);
      window.removeEventListener("mouseup", onEnd);
      window.removeEventListener("touchmove", onMove);
      window.removeEventListener("touchend", onEnd);
    };

    window.addEventListener("mousemove", onMove);
    window.addEventListener("mouseup", onEnd);
    window.addEventListener("touchmove", onMove, { passive: false });
    window.addEventListener("touchend", onEnd);

    focusTile(id);
  }, [floatingRects, focusTile]);

  // Resizing floating tile
  const handleResizeFloating = useCallback((id, e) => {
    e.preventDefault();
    e.stopPropagation();
    const isTouch = e.type.startsWith("touch");
    const startX = isTouch ? e.touches[0].clientX : e.clientX;
    const startY = isTouch ? e.touches[0].clientY : e.clientY;
    const initialRect = floatingRects[id] || { x: 50, y: 50, w: 320, h: 240 };

    const onMove = (ev) => {
      const curX = isTouch ? ev.touches[0].clientX : ev.clientX;
      const curY = isTouch ? ev.touches[0].clientY : ev.clientY;
      const newW = Math.max(220, initialRect.w + (curX - startX));
      const newH = Math.max(140, initialRect.h + (curY - startY));

      setFloatingRects((rects) => ({
        ...rects,
        [id]: { ...initialRect, w: newW, h: newH },
      }));
    };

    const onEnd = () => {
      window.removeEventListener("mousemove", onMove);
      window.removeEventListener("mouseup", onEnd);
      window.removeEventListener("touchmove", onMove);
      window.removeEventListener("touchend", onEnd);
    };

    window.addEventListener("mousemove", onMove);
    window.addEventListener("mouseup", onEnd);
    window.addEventListener("touchmove", onMove, { passive: false });
    window.addEventListener("touchend", onEnd);

    focusTile(id);
  }, [floatingRects, focusTile]);

  // Render control toolbar for Workspace Viewport Engine
  const renderToolbar = () => (
    <div
      data-testid="window-manager-toolbar"
      style={{
        display: "flex",
        alignItems: "center",
        justifyContent: "space-between",
        flexWrap: "wrap",
        gap: "8px",
        padding: compactBar ? "4px 8px" : "8px 12px",
        background: V.surf,
        borderBottom: `1px solid ${V.outv || "rgba(255,255,255,0.12)"}`,
        font: `500 11px/1 ${t.font}`,
        color: V.ink,
      }}
    >
      {/* Workspace Presets */}
      <div style={{ display: "flex", alignItems: "center", gap: "6px" }}>
        <span style={{ fontWeight: 700, fontSize: "10px", color: V.ink2, letterSpacing: ".08em" }}>
          WORKSPACES:
        </span>
        {Object.keys(WORKSPACE_PRESETS).map((key) => {
          const item = WORKSPACE_PRESETS[key];
          const active = preset === key;
          const bg = active ? V.pri : V.surf2;
          const fg = ink(bg, [V.bg, V.onpri, V.ink]);

          return (
            <button
              key={key}
              type="button"
              onClick={() => applyPreset(key)}
              style={{
                display: "inline-flex",
                alignItems: "center",
                gap: "5px",
                padding: "4px 8px",
                background: bg,
                color: fg,
                border: "none",
                borderRadius: V.rs || "4px",
                font: `600 10.5px/1 ${t.dfont}`,
                cursor: "pointer",
                letterSpacing: ".04em",
              }}
            >
              <Icon name={item.icon} size={12} />
              {item.name}
            </button>
          );
        })}
      </div>

      {/* Layout & Density Controls */}
      <div style={{ display: "flex", alignItems: "center", gap: "8px" }}>
        {/* Layout Mode Selector */}
        <div style={{ display: "flex", alignItems: "center", gap: "2px", background: V.bg, padding: "2px", borderRadius: "4px" }}>
          {[
            { id: "split", label: "Split", icon: "columns" },
            { id: "masonry", label: "Masonry", icon: "grid" },
            { id: "floaters", label: "Floaters", icon: "layers" },
            { id: "single", label: "Single", icon: "square" },
          ].map((mode) => {
            const active = layoutMode === mode.id;
            return (
              <button
                key={mode.id}
                type="button"
                onClick={() => setLayoutMode(mode.id)}
                title={`Layout mode: ${mode.label}`}
                style={{
                  display: "inline-flex",
                  alignItems: "center",
                  gap: "4px",
                  padding: "3px 6px",
                  background: active ? V.pri : "transparent",
                  color: active ? ink(V.pri, [V.bg, V.onpri, V.ink]) : V.ink2,
                  border: "none",
                  borderRadius: "3px",
                  font: `600 10px/1 ${t.dfont}`,
                  cursor: "pointer",
                }}
              >
                <Icon name={mode.icon} size={11} />
                {mode.label}
              </button>
            );
          })}
        </div>

        {/* Global Density Switcher */}
        <div style={{ display: "flex", alignItems: "center", gap: "4px" }}>
          <span style={{ fontSize: "10px", color: V.ink2 }}>Density:</span>
          {["compact", "normal", "spacious"].map((d) => (
            <button
              key={d}
              type="button"
              onClick={() => setGlobalDensity(d)}
              style={{
                padding: "2px 6px",
                background: globalDensity === d ? V.sec : V.surf2,
                color: globalDensity === d ? ink(V.sec, [V.bg, V.onsec, V.ink]) : V.ink2,
                border: "none",
                borderRadius: "3px",
                font: `600 9.5px/1 ${t.dfont}`,
                cursor: "pointer",
                textTransform: "capitalize",
              }}
            >
              {d[0].toUpperCase()}
            </button>
          ))}
        </div>

        {/* Add Viewport Tile Selector */}
        <select
          onChange={(e) => {
            if (e.target.value) {
              addTileToDock(e.target.value);
              e.target.value = "";
            }
          }}
          defaultValue=""
          style={{
            height: "24px",
            background: V.surf2,
            color: V.ink,
            border: `1px solid ${V.outv || "rgba(255,255,255,0.2)"}`,
            borderRadius: "4px",
            font: `500 10.5px/1 ${t.font}`,
            padding: "0 4px",
          }}
        >
          <option value="" disabled>
            + Add Tile Screen
          </option>
          {Object.keys(SCREEN_METADATA)
            .filter((scr) => !dockedTiles.includes(scr) && !floatingTiles.includes(scr))
            .map((scr) => (
              <option key={scr} value={scr}>
                {SCREEN_METADATA[scr].title}
              </option>
            ))}
        </select>
      </div>
    </div>
  );

  // Render main viewport container according to layoutMode
  const renderDockedContainer = () => {
    if (dockedTiles.length === 0) {
      return (
        <div
          style={{
            flex: 1,
            display: "flex",
            alignItems: "center",
            justifyContent: "center",
            color: V.ink2,
            font: `500 13px/1 ${t.font}`,
            padding: "32px",
            textAlign: "center",
          }}
        >
          No docked viewport tiles. Select a workspace preset or add a tile from the toolbar above.
        </div>
      );
    }

    if (layoutMode === "single") {
      const activeId = dockedTiles.includes(activeTileId) ? activeTileId : dockedTiles[0];
      const meta = SCREEN_METADATA[activeId] || { icon: "box", title: activeId };
      const density = tileDensityMap[activeId] || globalDensity;

      return (
        <div style={{ flex: 1, display: "flex", flexDirection: "column", minHeight: 0 }}>
          {/* Tile Tabs */}
          <div style={{ display: "flex", gap: "2px", background: V.bg, padding: "4px 8px 0" }}>
            {dockedTiles.map((id) => {
              const active = id === activeId;
              const bg = active ? V.surf : V.surf2;

              return (
                <button
                  key={id}
                  type="button"
                  onClick={() => focusTile(id)}
                  style={{
                    display: "inline-flex",
                    alignItems: "center",
                    gap: "6px",
                    padding: "6px 12px",
                    background: bg,
                    color: active ? V.pri : V.ink2,
                    border: "none",
                    borderTopLeftRadius: "6px",
                    borderTopRightRadius: "6px",
                    font: `600 11px/1 ${t.dfont}`,
                    cursor: "pointer",
                  }}
                >
                  <Icon name={(SCREEN_METADATA[id] || {}).icon || "box"} size={12} />
                  {id}
                </button>
              );
            })}
          </div>

          <ViewportTile
            id={activeId}
            title={meta.title}
            icon={meta.icon}
            isDocked={true}
            density={density}
            active={true}
            onFocus={() => focusTile(activeId)}
            onFloat={() => undockTile(activeId)}
            onClose={() => closeTile(activeId)}
            onToggleDensity={() => cycleTileDensity(activeId)}
          >
            {renderScreenContent(activeId)}
          </ViewportTile>
        </div>
      );
    }

    if (layoutMode === "masonry") {
      return (
        <div
          data-testid="masonry-tile-grid"
          style={{
            flex: 1,
            display: "grid",
            gridTemplateColumns: "repeat(auto-fit, minmax(320px, 1fr))",
            gridAutoRows: "minmax(280px, auto)",
            gap: "8px",
            padding: "8px",
            overflow: "auto",
          }}
        >
          {dockedTiles.map((id) => {
            const meta = SCREEN_METADATA[id] || { icon: "box", title: id };
            const density = tileDensityMap[id] || globalDensity;
            const active = id === activeTileId;

            return (
              <ViewportTile
                key={id}
                id={id}
                title={meta.title}
                icon={meta.icon}
                isDocked={true}
                density={density}
                active={active}
                onFocus={() => focusTile(id)}
                onFloat={() => undockTile(id)}
                onClose={() => closeTile(id)}
                onMinimize={() => toggleMinimizeTile(id)}
                onToggleDensity={() => cycleTileDensity(id)}
                style={{ height: "100%", minHeight: "260px" }}
              >
                {renderScreenContent(id)}
              </ViewportTile>
            );
          })}
        </div>
      );
    }

    // Default: split view (side-by-side flex row or column)
    return (
      <div
        data-testid="split-viewport-container"
        style={{
          flex: 1,
          display: "flex",
          flexDirection: splitDirection,
          gap: "6px",
          padding: "6px",
          minHeight: 0,
          minWidth: 0,
          overflow: "hidden",
        }}
      >
        {dockedTiles.map((id, index) => {
          const meta = SCREEN_METADATA[id] || { icon: "box", title: id };
          const density = tileDensityMap[id] || globalDensity;
          const active = id === activeTileId;
          const flexRatio = splitRatios[index] || 1;

          return (
            <ViewportTile
              key={id}
              id={id}
              title={meta.title}
              icon={meta.icon}
              isDocked={true}
              density={density}
              active={active}
              onFocus={() => focusTile(id)}
              onFloat={() => undockTile(id)}
              onClose={() => closeTile(id)}
              onSplitHorizontal={() => setSplitDirection("row")}
              onSplitVertical={() => setSplitDirection("column")}
              onToggleDensity={() => cycleTileDensity(id)}
              style={{ flex: `${flexRatio} 1 0%` }}
            >
              {renderScreenContent(id)}
            </ViewportTile>
          );
        })}
      </div>
    );
  };

  // Render floating tiles layer overlay
  const renderFloatingLayer = () => {
    if (floatingTiles.length === 0) return null;

    return (
      <div
        data-testid="floating-tile-layer"
        style={{
          position: "absolute",
          inset: 0,
          pointerEvents: "none",
          zIndex: 100,
        }}
      >
        {floatingTiles.map((id) => {
          const meta = SCREEN_METADATA[id] || { icon: "box", title: id };
          const rect = floatingRects[id] || { x: 50, y: 50, w: 340, h: 260 };
          const zIdx = 10 + floatingZ.indexOf(id);
          const density = tileDensityMap[id] || globalDensity;
          const active = id === activeTileId;

          return (
            <div
              key={id}
              style={{
                position: "absolute",
                left: `${rect.x}px`,
                top: `${rect.y}px`,
                width: `${rect.w}px`,
                height: `${rect.h}px`,
                zIndex: zIdx,
                pointerEvents: "auto",
              }}
            >
              <ViewportTile
                id={id}
                title={meta.title}
                icon={meta.icon}
                isDocked={false}
                isFloating={true}
                density={density}
                active={active}
                onFocus={() => focusTile(id)}
                onDock={() => dockTile(id)}
                onClose={() => closeTile(id)}
                onToggleDensity={() => cycleTileDensity(id)}
                onDragStart={(e) => handleDragFloating(id, e)}
                onResizeStart={(e) => handleResizeFloating(id, e)}
                style={{ width: "100%", height: "100%" }}
              >
                {renderScreenContent(id)}
              </ViewportTile>
            </div>
          );
        })}
      </div>
    );
  };

  return (
    <div
      data-testid="window-manager-engine"
      style={{
        flex: 1,
        display: "flex",
        flexDirection: "column",
        minWidth: 0,
        minHeight: 0,
        position: "relative",
        background: V.bg,
        overflow: "hidden",
      }}
    >
      {renderToolbar()}
      <div
        style={{
          flex: 1,
          display: "flex",
          minWidth: 0,
          minHeight: 0,
          position: "relative",
          overflow: "hidden",
        }}
      >
        {renderDockedContainer()}
        {renderFloatingLayer()}
      </div>
    </div>
  );
}
