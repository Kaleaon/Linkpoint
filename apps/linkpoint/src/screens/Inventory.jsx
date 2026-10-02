import { useEffect, useMemo, useState } from "react";
import { useApp } from "../context/AppContext.jsx";
import { useTheme } from "../context/ThemeContext.jsx";
import { app } from "../linkpoint/app.ts";
import { macroTaskQueue } from "../linkpoint/macro-task-queue.ts";
import Icon from "../components/Icon.jsx";
import ListSkeletonLoader from "../components/ListSkeletonLoader.jsx";
import GuidedEmptyState from "../components/GuidedEmptyState.jsx";

/** Inventory rows come directly from InventoryManager capability responses. */
export default function Inventory() {
  const { V, t } = useTheme();
  const { actions } = useApp();
  const [revision, setRevision] = useState(0);
  const [filter, setFilter] = useState("");
  const [selected, setSelected] = useState(null);
  const [contextMenu, setContextMenu] = useState(null);


  useEffect(() => {
    const refresh = () => setRevision((value) => value + 1);
    app.inventory.on("inventory_loaded", refresh);
    app.inventory.on("inventory_updated", refresh);
    return () => {
      app.inventory.off("inventory_loaded", refresh);
      app.inventory.off("inventory_updated", refresh);
    };
  }, []);

  const rows = useMemo(() => {
    const query = filter.trim().toLocaleLowerCase();
    const folders = Array.from(app.inventory.folders.values()).map((entry) => ({ ...entry, folder: true }));
    const items = Array.from(app.inventory.items.values()).map((entry) => ({ ...entry, folder: false }));
    return [...folders, ...items]
      .filter((entry) => !query || String(entry.name || "").toLocaleLowerCase().includes(query))
      .sort((a, b) => Number(b.folder) - Number(a.folder) || String(a.name).localeCompare(String(b.name)));
  }, [filter, revision]);

  const [refreshing, setRefreshing] = useState(false);

  const handleRefresh = async () => {
    setRefreshing(true);
    try {
      await app.inventory.load();
    } finally {
      setRefreshing(false);
    }
  };

  const handleRunMacro = (folder, mode) => {
    setContextMenu(null);
    let items = app.inventory.getFolderItemsRecursive(folder.id);
    if (!items.length) {
      items = Array.from(app.inventory.items.values()).filter((item) => item.parent === folder.id);
    }
    void macroTaskQueue.start(items, { mode }, folder.name || "Outfit");
  };

  const handleContextMenu = (event, entry) => {
    event.preventDefault();
    setSelected(entry);
    if (entry.folder) void app.inventory.fetchFolderContents(entry.id);
    setContextMenu({
      entry,
      x: Math.min(event.clientX, window.innerWidth - 200),
      y: Math.min(event.clientY, window.innerHeight - 150),
    });
  };

  useEffect(() => {
    const visibleFolderIds = rows.filter((r) => r.folder).map((r) => r.id).slice(0, 15);
    if (visibleFolderIds.length && typeof app.inventory.updateViewportFolders === "function") {
      app.inventory.updateViewportFolders(visibleFolderIds);
    }
  }, [rows]);

  return (
    <section className="live-screen" onClick={() => contextMenu && setContextMenu(null)}>
      <div style={{ display: "flex", gap: 8, alignItems: "center" }}>
        <label className="filter-field" style={{ borderColor: V.outv, background: V.surf, flex: 1, margin: 0 }}>
          <Icon name="search" size={16} />
          <input value={filter} onChange={(event) => setFilter(event.target.value)} placeholder="Filter Second Life inventory" aria-label="Filter inventory" />
        </label>
        <button
          type="button"
          onClick={handleRefresh}
          disabled={refreshing || !app.auth.isLoggedIn()}
          style={{
            height: "40px",
            padding: "0 12px",
            display: "flex",
            alignItems: "center",
            gap: 6,
            background: V.surf,
            border: `1px solid ${V.outv}`,
            borderRadius: V.rs,
            color: V.pri,
            fontSize: "11px",
            fontWeight: 700,
            cursor: "pointer",
            flex: "none",
          }}
          title="Reload Second Life inventory"
        >
          <Icon name="rotate-cw" size={14} />
          {refreshing ? "FETCHING…" : "REFRESH"}
        </button>
      </div>
      <div className="live-list">
        {refreshing ? (
          <ListSkeletonLoader count={5} variant="inventory" />
        ) : rows.length ? (
          rows.map((entry) => (
            <div
              key={entry.id}
              style={{ display: "flex", alignItems: "center", position: "relative" }}
            >
              <button
                type="button"
                className="inventory-row inventory-button"
                style={{ borderColor: V.outv, flex: 1 }}
                onClick={() => {
                  setSelected(entry);
                  if (entry.folder) {
                    if (typeof app.inventory.updateViewportFolders === "function") {
                      app.inventory.updateViewportFolders([entry.id]);
                    }
                    void app.inventory.fetchFolderContents(entry.id, true);
                  }
                }}
                onContextMenu={(e) => handleContextMenu(e, entry)}
              >
                <Icon name={entry.folder ? "folder" : "file"} size={17} style={{ color: entry.folder ? V.pri : V.sec2 }} />
                <span style={{ font: `400 13px/1.3 ${t.font}`, flex: 1, textAlign: "left" }}>{entry.name || "Unnamed item"}</span>
              </button>
              {entry.folder ? (
                <button
                  type="button"
                  className="folder-context-trigger"
                  aria-label={`Options for folder ${entry.name || 'Folder'}`}
                  style={{
                    background: "none",
                    border: "none",
                    color: V.pri,
                    padding: "8px 12px",
                    cursor: "pointer",
                    display: "flex",
                    alignItems: "center",
                  }}
                  onClick={(e) => {
                    e.stopPropagation();
                    handleContextMenu(e, entry);
                  }}
                >
                  <Icon name="more-vertical" size={16} />
                </button>
              ) : null}
            </div>
          ))
        ) : filter.trim() ? (
          <GuidedEmptyState
            icon="search"
            title="No search results"
            description={`No inventory items match "${filter.trim()}".`}
            isSearch={true}
          />
        ) : app.auth.isLoggedIn() ? (
          <GuidedEmptyState
            icon="folder-open"
            title="No inventory loaded"
            description="Your inventory is empty or has not been loaded from the grid yet."
            actionLabel="RELOAD INVENTORY"
            onAction={handleRefresh}
          />
        ) : (
          <GuidedEmptyState
            icon="folder-open"
            title="Not connected to grid"
            description="Connect to a Second Life or OpenSim grid to load and view your inventory."
            actionLabel="CONNECT TO GRID"
            onAction={() => actions.setScreen("Login")}
          />
        )}
      </div>
      {/* Floating Context Menu */}
      {contextMenu ? (
        <div
          className="inventory-context-menu"
          style={{
            position: "fixed",
            left: contextMenu.x,
            top: contextMenu.y,
            zIndex: 1000,
            background: V.surf,
            border: `1px solid ${V.pri}`,
            borderRadius: V.rs || "6px",
            boxShadow: "0 8px 24px rgba(0, 0, 0, 0.4)",
            padding: "4px 0",
            minWidth: "180px",
          }}
          onClick={(e) => e.stopPropagation()}
        >
          {contextMenu.entry.folder ? (
            <>
              <button
                type="button"
                style={{
                  width: "100%",
                  padding: "8px 14px",
                  background: "none",
                  border: "none",
                  textAlign: "left",
                  color: V.ink,
                  fontSize: "12px",
                  fontWeight: 600,
                  cursor: "pointer",
                  display: "flex",
                  alignItems: "center",
                  gap: "8px",
                }}
                onClick={() => handleRunMacro(contextMenu.entry, "replace")}
              >
                <Icon name="user-check" size={14} style={{ color: V.pri }} />
                Wear All Items
              </button>
              <button
                type="button"
                style={{
                  width: "100%",
                  padding: "8px 14px",
                  background: "none",
                  border: "none",
                  textAlign: "left",
                  color: V.ink,
                  fontSize: "12px",
                  fontWeight: 600,
                  cursor: "pointer",
                  display: "flex",
                  alignItems: "center",
                  gap: "8px",
                }}
                onClick={() => handleRunMacro(contextMenu.entry, "append")}
              >
                <Icon name="plus-circle" size={14} style={{ color: V.pri }} />
                Append All Items
              </button>
            </>
          ) : (
            <button
              type="button"
              style={{
                width: "100%",
                padding: "8px 14px",
                background: "none",
                border: "none",
                textAlign: "left",
                color: V.ink,
                fontSize: "12px",
                fontWeight: 600,
                cursor: "pointer",
                display: "flex",
                alignItems: "center",
                gap: "8px",
              }}
              onClick={() => {
                setContextMenu(null);
                void app.inventory.wearItem(contextMenu.entry, { append: true });
              }}
            >
              <Icon name="user-check" size={14} style={{ color: V.pri }} />
              Wear Item
            </button>
          )}
        </div>
      ) : null}

      {/* Record detail panel */}
      {selected ? (
        <aside className="record-detail" style={{ background: V.surf, borderColor: V.outv }}>
          <button className="detail-close" onClick={() => setSelected(null)} aria-label="Close inventory details">×</button>
          <Icon name={selected.folder ? "folder" : "file"} size={22} />
          <h2>{selected.name || "Unnamed item"}</h2>
          {selected.folder ? (
            <div style={{ display: "flex", gap: "8px", margin: "12px 0 16px" }}>
              <button
                type="button"
                style={{
                  flex: 1,
                  padding: "8px 10px",
                  background: V.pri,
                  color: V.onpri || V.bg,
                  border: "none",
                  borderRadius: V.rs || "4px",
                  fontWeight: 700,
                  fontSize: "11px",
                  cursor: "pointer",
                  display: "flex",
                  alignItems: "center",
                  justifyContent: "center",
                  gap: "4px",
                }}
                onClick={() => handleRunMacro(selected, "replace")}
              >
                <Icon name="user-check" size={14} />
                Wear All Items
              </button>
              <button
                type="button"
                style={{
                  flex: 1,
                  padding: "8px 10px",
                  background: V.bg,
                  color: V.pri,
                  border: `1px solid ${V.pri}`,
                  borderRadius: V.rs || "4px",
                  fontWeight: 700,
                  fontSize: "11px",
                  cursor: "pointer",
                  display: "flex",
                  alignItems: "center",
                  justifyContent: "center",
                  gap: "4px",
                }}
                onClick={() => handleRunMacro(selected, "append")}
              >
                <Icon name="plus-circle" size={14} />
                Append All Items
              </button>
            </div>
          ) : null}
          <dl>
            <dt>UUID</dt>
            <dd>{selected.id}</dd>
            <dt>Type</dt>
            <dd>{selected.folder ? "Folder" : selected.assetType ?? "Unknown"}</dd>
            {selected.description ? <><dt>Description</dt><dd>{selected.description}</dd></> : null}
          </dl>
        </aside>
      ) : null}
    </section>
  );
}
