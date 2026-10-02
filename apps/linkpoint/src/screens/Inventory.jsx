import { useEffect, useMemo, useState } from "react";
import { useTheme } from "../context/ThemeContext.jsx";
import { app } from "../linkpoint/app.ts";
import Icon from "../components/Icon.jsx";

/** Inventory rows come directly from InventoryManager capability responses. */
export default function Inventory() {
  const { V, t } = useTheme();
  const [revision, setRevision] = useState(0);
  const [filter, setFilter] = useState("");
  const [selected, setSelected] = useState(null);

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

  return (
    <section className="live-screen">
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
        {rows.length ? rows.map((entry) => (
          <button className="inventory-row inventory-button" key={entry.id} style={{ borderColor: V.outv }} onClick={() => {
            setSelected(entry);
            if (entry.folder) void app.inventory.fetchFolderContents(entry.id);
          }}>
            <Icon name={entry.folder ? "folder" : "file"} size={17} style={{ color: entry.folder ? V.pri : V.sec2 }} />
            <span style={{ font: `400 13px/1.3 ${t.font}` }}>{entry.name || "Unnamed item"}</span>
          </button>
        )) : <div className="honest-empty"><Icon name="folder-open" size={28} /><p>{app.auth.isLoggedIn() ? "No inventory data has been loaded by the grid." : "Connect to a grid to load inventory."}</p></div>}
      </div>
      {selected ? <aside className="record-detail" style={{ background: V.surf, borderColor: V.outv }}><button className="detail-close" onClick={() => setSelected(null)} aria-label="Close inventory details">×</button><Icon name={selected.folder ? "folder" : "file"} size={22} /><h2>{selected.name || "Unnamed item"}</h2><dl><dt>UUID</dt><dd>{selected.id}</dd><dt>Type</dt><dd>{selected.folder ? "Folder" : selected.assetType ?? "Unknown"}</dd>{selected.description ? <><dt>Description</dt><dd>{selected.description}</dd></> : null}</dl></aside> : null}
    </section>
  );
}
