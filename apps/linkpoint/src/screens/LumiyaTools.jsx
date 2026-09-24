import { useState } from "react";
import { GRIDS } from "../theme/constants.js";
import Icon from "../components/Icon.jsx";
import { useViewerState } from "../viewer/ViewerClientContext";

function Empty({ icon, children }) { return <div className="honest-empty"><Icon name={icon} size={30} /><p>{children}</p></div>; }
export function AccountsScreen() { const [forgotten, setForgotten] = useState(false); const clear = () => { localStorage.removeItem("linkpoint_credentials"); setForgotten(true); }; return <div className="tool-page"><Empty icon="contact">{forgotten ? "Remembered account cleared." : "Passwords are never stored."}</Empty><button onClick={clear}>Forget remembered account</button></div>; }
export function GridsScreen() { return <div className="tool-page"><h2>Login grids</h2>{GRIDS.map((grid) => <section className="runtime-card" key={grid.key}><strong>{grid.label}</strong><small>{grid.host}</small></section>)}</div>; }
export function MediaScreen() { const [url, setUrl] = useState(""); const [active, setActive] = useState(""); return <div className="tool-page"><h2>Streaming media</h2><div className="inline-tool"><input type="url" value={url} onChange={(e) => setUrl(e.target.value)} placeholder="HTTPS audio stream URL" /><button onClick={() => setActive(url.trim())} disabled={!/^https:\/\//i.test(url.trim())}>Play</button></div>{active ? <audio className="media-player" src={active} controls autoPlay onError={() => setActive("")} /> : <Empty icon="radio">Enter an HTTPS stream.</Empty>}</div>; }
export function NotecardsScreen() { return <Empty icon="file-text">No notecards have been received.</Empty>; }
export function ParcelScreen() { return <Empty icon="map-pin">No parcel properties have been received.</Empty>; }
export function TransactionsScreen() { return <Empty icon="banknote">No transaction history has been received.</Empty>; }
export function TeleportScreen() { return <Empty icon="map-pin">Teleport will be enabled when its native command is available.</Empty>; }
export function DiagnosticsScreen() { const viewer = useViewerState(); return <div className="tool-page"><section className="runtime-card"><h2>Connection</h2><dl><dt>State</dt><dd>{viewer.connection}</dd><dt>Agent</dt><dd>{viewer.session?.agentId || "—"}</dd><dt>Session</dt><dd>{viewer.session?.sessionId || "—"}</dd><dt>Region</dt><dd>{viewer.session?.regionName || "—"}</dd></dl>{viewer.error ? <p role="alert">{viewer.error.code}: {viewer.error.message}</p> : null}</section></div>; }
