import { useMemo, useState } from "react";
import Icon from "../components/Icon.jsx";
import { useViewerState } from "../viewer/ViewerClientContext";

function Empty({ icon, children }) { return <div className="honest-empty"><Icon name={icon} size={30} /><p>{children}</p></div>; }
function Rows({ rows, icon = "circle" }) { return <div className="live-list">{rows.map((row) => <div className="inventory-row" key={row.id || row.name}><Icon name={row.icon || icon} size={17} /><div><strong>{row.name || row.id}</strong>{row.meta ? <small>{row.meta}</small> : null}</div></div>)}</div>; }
function Pending({ title, icon }) { const { connection } = useViewerState(); return <Empty icon={icon}>{connection === "connected" ? `${title} is waiting for data from the current grid.` : `Connect to a grid to use ${title.toLowerCase()}.`}</Empty>; }

export function FriendsScreen() { return <Pending title="Friends" icon="users" />; }
export function GroupsScreen() { return <Pending title="Groups" icon="users-round" />; }
export function NoticesScreen() { return <Pending title="Notices" icon="bell" />; }
export function GenericInventoryScreen({ kind }) { return <Pending title={kind} icon="package" />; }
export function ConnectionScreen({ title }) { return <Pending title={title} icon="plug" />; }

export function RadarScreen() {
  const { scene } = useViewerState();
  const rows = Object.values(scene).map((entity) => ({ id: entity.id, name: entity.id, meta: entity.position.map(Math.round).join(", ") }));
  return rows.length ? <Rows rows={rows} icon="box" /> : <Pending title="Radar" icon="radar" />;
}

export function MapScreen() {
  const { session } = useViewerState();
  return session ? <Rows rows={[{ id: session.regionName, name: session.regionName, meta: "Current region" }]} icon="map-pin" /> : <Pending title="Map" icon="map" />;
}

export function WorldScreen() {
  const { scene, session } = useViewerState();
  return <div className="world-runtime"><canvas id="world-canvas" aria-label="Live simulator scene" /><div className="world-overlay">{session ? `${session.regionName} · ${Object.keys(scene).length} objects` : "Waiting for a live simulator scene"}</div></div>;
}

export function MuteListScreen() {
  const [entry, setEntry] = useState("");
  const [muted, setMuted] = useState([]);
  const add = () => { if (entry.trim()) { setMuted((items) => [...items, entry.trim()]); setEntry(""); } };
  return <div className="tool-screen"><div className="inline-tool"><input value={entry} onChange={(e) => setEntry(e.target.value)} placeholder="Avatar UUID" /><button onClick={add}>Mute</button></div>{muted.length ? <Rows rows={muted.map((id) => ({ id, name: id }))} icon="volume-x" /> : <Empty icon="volume-x">No avatars are muted.</Empty>}</div>;
}

export function SearchScreen() {
  const [query, setQuery] = useState("");
  const status = useMemo(() => query.trim() ? "Resident search transport is not available yet." : "", [query]);
  return <div className="tool-screen"><div className="inline-tool"><input value={query} onChange={(e) => setQuery(e.target.value)} placeholder="Resident name or UUID" aria-label="Resident search" /></div>{status ? <p className="tool-status">{status}</p> : <Empty icon="search">Enter a resident name or UUID.</Empty>}</div>;
}
