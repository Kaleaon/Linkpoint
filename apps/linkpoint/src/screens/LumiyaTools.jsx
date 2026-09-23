import { useMemo, useState } from "react";
import { app } from "../linkpoint/app.ts";
import { GRIDS } from "../theme/constants.js";
import Icon from "../components/Icon.jsx";

function Empty({ icon, children }) {
  return <div className="honest-empty"><Icon name={icon} size={30} /><p>{children}</p></div>;
}

export function AccountsScreen() {
  const [credentials, setCredentials] = useState(() => app.auth.credentials);
  const clear = () => { app.auth.credentials = null; localStorage.removeItem("linkpoint_credentials"); setCredentials(null); };
  return credentials ? <div className="tool-page"><section className="runtime-card"><Icon name="contact" size={24} /><h2>{credentials.username}</h2><p>{credentials.grid}</p><button onClick={clear}>Forget account</button></section></div> : <Empty icon="contact">No remembered account. Linkpoint never stores the account password.</Empty>;
}

export function GridsScreen() {
  const custom = app.preferences.get("network", "customGrids") || [];
  const grids = [...GRIDS, ...custom];
  return <div className="tool-page"><h2>Login grids</h2>{grids.map((grid) => <section className="runtime-card" key={grid.key || grid.host}><strong>{grid.label}</strong><small>{grid.host}</small></section>)}</div>;
}

export function MediaScreen() {
  const [url, setUrl] = useState("");
  const [active, setActive] = useState("");
  return <div className="tool-page"><h2>Streaming media</h2><div className="inline-tool"><input type="url" value={url} onChange={(e) => setUrl(e.target.value)} placeholder="HTTPS audio stream URL" /><button onClick={() => setActive(url.trim())} disabled={!/^https:\/\//i.test(url.trim())}>Play</button></div>{active ? <audio className="media-player" src={active} controls autoPlay onError={() => setActive("")} /> : <Empty icon="radio">Enter an HTTPS stream supplied by the current parcel or broadcaster.</Empty>}</div>;
}

export function NotecardsScreen() {
  const cards = useMemo(() => Array.from(app.inventory.items.values()).filter((item) => Number(item.assetType) === 7), []);
  const [selected, setSelected] = useState(null);
  return <div className="tool-page"><h2>Notecards</h2>{cards.length ? cards.map((card) => <button className="runtime-card runtime-card-button" key={card.id} onClick={() => setSelected(card)}><Icon name="file-text" size={18} /><span>{card.name}</span></button>) : <Empty icon="file-text">No notecards have been loaded from inventory.</Empty>}{selected ? <section className="runtime-card"><h3>{selected.name}</h3><p>{selected.description || "Notecard asset content has not been downloaded."}</p><small>{selected.id}</small></section> : null}</div>;
}

export function ParcelScreen() {
  const parcel = app.world.region?.parcel;
  return parcel ? <div className="tool-page"><section className="runtime-card"><h2>{parcel.name || "Unnamed parcel"}</h2><pre>{JSON.stringify(parcel, null, 2)}</pre></section></div> : <Empty icon="map-pin">No parcel-properties message has been received.</Empty>;
}

export function TransactionsScreen() {
  const transactions = app.auth.user?.transactions || [];
  return transactions.length ? <div className="tool-page">{transactions.map((entry) => <section className="runtime-card" key={entry.id}><strong>{entry.description || entry.id}</strong><small>{entry.amount}</small></section>)}</div> : <Empty icon="banknote">No transaction history has been returned by the grid.</Empty>;
}

export function TeleportScreen() {
  const [destination, setDestination] = useState("");
  const [status, setStatus] = useState("");
  const teleport = async () => {
    const method = app.protocol.teleportTo || app.protocol.teleport;
    if (typeof method !== "function") { setStatus("This grid connection has not exposed teleport capability."); return; }
    try { await method.call(app.protocol, destination.trim()); setStatus("Teleport requested."); } catch (error) { setStatus(error instanceof Error ? error.message : "Teleport failed."); }
  };
  return <div className="tool-page"><h2>Teleport</h2><div className="inline-tool"><input value={destination} onChange={(e) => setDestination(e.target.value)} placeholder="secondlife://Region/x/y/z" /><button onClick={() => void teleport()} disabled={!destination.trim()}>Go</button></div>{status ? <p className="tool-status">{status}</p> : null}</div>;
}

export function DiagnosticsScreen() {
  const capabilities = Object.keys(app.protocol.capabilities || {});
  return <div className="tool-page"><section className="runtime-card"><h2>Connection</h2><dl><dt>State</dt><dd>{app.protocol.connected ? "Connected" : "Disconnected"}</dd><dt>Agent</dt><dd>{app.protocol.agentId || "—"}</dd><dt>Session</dt><dd>{app.protocol.sessionId || "—"}</dd><dt>Capabilities</dt><dd>{capabilities.length}</dd></dl></section>{capabilities.length ? <section className="runtime-card"><h2>Capabilities</h2>{capabilities.sort().map((name) => <small key={name}>{name}</small>)}</section> : null}</div>;
}
