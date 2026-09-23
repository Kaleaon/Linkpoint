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

  return (
    <section className="live-screen">
      <label className="filter-field" style={{ borderColor: V.outv, background: V.surf }}>
        <Icon name="search" size={16} />
        <input value={filter} onChange={(event) => setFilter(event.target.value)} placeholder="Filter loaded inventory" aria-label="Filter inventory" />
      </label>
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
