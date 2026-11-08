import { useEffect, useMemo, useState } from "react";
import { app } from "../linkpoint/app.ts";
import { GRIDS } from "../theme/constants.js";
import Icon from "../components/Icon.jsx";
import AudioPlayer from "../components/AudioPlayer.jsx";

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
  return <div className="tool-page"><h2>Streaming media</h2><div className="inline-tool"><input type="url" value={url} onChange={(e) => setUrl(e.target.value)} placeholder="HTTPS audio stream URL" /><button onClick={() => setActive(url.trim())} disabled={!/^https:\/\//i.test(url.trim())}>Play</button></div>{active ? <AudioPlayer src={active} onError={() => setActive("")} /> : <Empty icon="radio">Enter an HTTPS stream supplied by the current parcel or broadcaster.</Empty>}</div>;
}

export function NotecardsScreen() {
  const cards = useMemo(() => Array.from(app.inventory.items.values()).filter((item) => Number(item.assetType) === 7), []);
  const [selected, setSelected] = useState(null);
  return <div className="tool-page"><h2>Notecards</h2>{cards.length ? cards.map((card) => <button className="runtime-card runtime-card-button" key={card.id} onClick={() => setSelected(card)}><Icon name="file-text" size={18} /><span>{card.name}</span></button>) : <Empty icon="file-text">No notecards have been loaded from inventory.</Empty>}{selected ? <section className="runtime-card"><h3>{selected.name}</h3><p>{selected.description || "Notecard asset content has not been downloaded."}</p><small>{selected.id}</small></section> : null}</div>;
}

export function parseParcelFlags(flags) {
  const mask = Number(flags) || 0;
  const items = [];
  if (mask & (1 << 0)) items.push("Fly enabled");
  else items.push("No fly restriction");

  if (mask & (1 << 1)) items.push("Scripts allowed");
  else items.push("Scripts restricted");

  if (mask & (1 << 2)) items.push("Build allowed");
  else items.push("Build restricted");

  if (mask & (1 << 4)) items.push("Terraform allowed");
  if (mask & (1 << 6)) items.push("Group build allowed");
  if (mask & (1 << 7)) items.push("Group terraform allowed");

  if (mask & (1 << 10)) items.push("Voice enabled");
  else items.push("Voice disabled");

  if (mask & (1 << 12)) items.push("Push restricted");
  if (mask & (1 << 16)) items.push("Direct teleport allowed");

  return items;
}

export function ParcelScreen() {
  const [region, setRegion] = useState(() => app.world.region);
  const [parcel, setParcel] = useState(() => app.world.region?.parcel);

  const [streamUrl, setStreamUrl] = useState(() => app.world.region?.parcel?.musicUrl || app.world.region?.parcel?.mediaUrl || "");
  const [isPlaying, setIsPlaying] = useState(false);
  const [streamError, setStreamError] = useState("");

  useEffect(() => {
    const handleParcelChanged = (updatedParcel) => {
      setParcel(updatedParcel);
      setRegion(app.world.region);
      if (updatedParcel?.musicUrl || updatedParcel?.mediaUrl) {
        setStreamUrl(updatedParcel.musicUrl || updatedParcel.mediaUrl);
      }
    };
    const handleRegionChanged = (updatedRegion) => {
      setRegion(updatedRegion);
      if (updatedRegion?.parcel) {
        setParcel(updatedRegion.parcel);
        if (updatedRegion.parcel.musicUrl || updatedRegion.parcel.mediaUrl) {
          setStreamUrl(updatedRegion.parcel.musicUrl || updatedRegion.parcel.mediaUrl);
        }
      }
    };

    app.world.on("parcel_changed", handleParcelChanged);
    app.world.on("region_changed", handleRegionChanged);

    return () => {
      app.world.off("parcel_changed", handleParcelChanged);
      app.world.off("region_changed", handleRegionChanged);
    };
  }, []);

  if (!parcel) {
    return <Empty icon="map-pin">No parcel-properties message has been received yet for this region.</Empty>;
  }

  const name = parcel.name || parcel.Name || "Unnamed parcel";
  const desc = parcel.description || parcel.Desc || parcel.desc || "No description available for this parcel.";
  const regionName = region?.name || region?.Name || "Unknown region";
  const area = parcel.area ?? parcel.Area ?? 0;

  const ownerId = parcel.ownerId || parcel.OwnerID;
  const ownerLabel = ownerId ? String(ownerId) : "Linden Public / Public Land";
  const groupId = parcel.groupId || parcel.GroupID;
  const groupLabel = groupId ? String(groupId) : "No Group Affinity";

  const totalPrims = Number(parcel.totalPrims ?? parcel.TotalPrims ?? 0);
  const maxPrims = Number(parcel.maxPrims ?? parcel.MaxPrims ?? 0);
  const pct = maxPrims > 0 ? Math.min(100, Math.round((totalPrims / maxPrims) * 100)) : 0;
  const availablePrims = Math.max(0, maxPrims - totalPrims);

  const flagList = parseParcelFlags(parcel.parcelFlags ?? parcel.ParcelFlags ?? parcel.Flags ?? parcel.flags);

  const activeStream = streamUrl.trim();
  const isValidUrl = /^https?:\/\//i.test(activeStream);

  const handlePlay = () => {
    if (!isValidUrl) {
      setStreamError("Please enter a valid HTTP or HTTPS stream URL.");
      return;
    }
    setStreamError("");
    setIsPlaying(true);
  };

  const handlePause = () => {
    setIsPlaying(false);
  };

  const handleStop = () => {
    setIsPlaying(false);
    setStreamError("");
  };

  return (
    <div className="tool-page">
      <section className="runtime-card" aria-label="Land details">
        <div style={{ display: "flex", alignItems: "center", gap: 10 }}>
          <Icon name="map-pin" size={22} />
          <div>
            <h2 style={{ margin: 0, fontSize: 18 }}>{name}</h2>
            <small style={{ opacity: 0.7 }}>{regionName} · {area} sq.m.</small>
          </div>
        </div>
        <p style={{ margin: "8px 0 0", fontSize: 13, lineHeight: 1.4 }}>{desc}</p>
        {parcel.id != null ? <small style={{ opacity: 0.6 }}>Parcel ID: {parcel.id}</small> : null}
      </section>

      <section className="runtime-card" aria-label="Land ownership">
        <h3 style={{ margin: 0, fontSize: 14 }}>Ownership & Group</h3>
        <dl>
          <dt>Owner</dt>
          <dd>{ownerLabel}</dd>
          <dt>Group</dt>
          <dd>{groupLabel}</dd>
        </dl>
      </section>

      <section className="runtime-card" aria-label="Prim capacity">
        <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center" }}>
          <h3 style={{ margin: 0, fontSize: 14 }}>Prim Capacity</h3>
          <strong>{totalPrims} / {maxPrims}</strong>
        </div>
        <div style={{ background: "rgba(127,127,127,0.2)", height: 10, borderRadius: 5, overflow: "hidden", margin: "6px 0" }}>
          <div style={{ background: pct > 90 ? "#e53935" : pct > 75 ? "#fb8c00" : "#4caf50", width: `${pct}%`, height: "100%", transition: "width 0.3s ease" }} />
        </div>
        <small style={{ opacity: 0.8 }}>{pct}% prim capacity used · {availablePrims} prims available</small>
      </section>

      <section className="runtime-card" aria-label="Parcel permissions">
        <h3 style={{ margin: 0, fontSize: 14 }}>Parcel Flags & Permissions</h3>
        <div style={{ display: "flex", flexWrap: "wrap", gap: 6, marginTop: 6 }}>
          {flagList.map((flag) => (
            <span key={flag} style={{ padding: "3px 8px", borderRadius: 12, border: "1px solid rgba(127,127,127,0.3)", fontSize: 11, background: "rgba(127,127,127,0.1)" }}>
              {flag}
            </span>
          ))}
        </div>
      </section>

      <section className="runtime-card" aria-label="Region audio stream">
        <h3 style={{ margin: 0, fontSize: 14 }}>Region Audio Stream</h3>
        <div className="inline-tool" style={{ display: "flex", gap: 8, marginTop: 8 }}>
          <input
            type="url"
            value={streamUrl}
            onChange={(e) => setStreamUrl(e.target.value)}
            placeholder="HTTPS audio stream URL"
            style={{ flex: 1, padding: "6px 10px", borderRadius: 4, border: "1px solid rgba(127,127,127,0.4)" }}
          />
          {isPlaying ? (
            <button type="button" onClick={handlePause} style={{ padding: "6px 12px" }}>Pause</button>
          ) : (
            <button type="button" onClick={handlePlay} disabled={!isValidUrl} style={{ padding: "6px 12px" }}>Play</button>
          )}
          <button type="button" onClick={handleStop} disabled={!isPlaying} style={{ padding: "6px 12px" }}>Stop</button>
        </div>
        {streamError ? <small style={{ color: "#e53935", marginTop: 4 }}>{streamError}</small> : null}
        {activeStream && isValidUrl && isPlaying ? (
          <AudioPlayer
            src={activeStream}
            onError={() => {
              setStreamError("Stream error: unable to play audio stream.");
              setIsPlaying(false);
            }}
            style={{ marginTop: 8, width: "100%" }}
          />
        ) : null}
      </section>
    </div>
  );
}

export function TransactionsScreen() {
  const transactions = app.auth.user?.transactions || [];
  return transactions.length ? <div className="tool-page">{transactions.map((entry) => <section className="runtime-card" key={entry.id}><strong>{entry.description || entry.id}</strong><small>{entry.amount}</small></section>)}</div> : <Empty icon="banknote">No transaction history has been returned by the grid.</Empty>;
}

export function TeleportScreen() {
  const [destination, setDestination] = useState("");
  const [status, setStatus] = useState("");
  const teleport = async () => {
    try {
      const result = await app.protocol.teleportTo(destination.trim());
      setStatus(result?.message ? `Grid: ${result.message}` : `Teleport to ${result.requested.region} requested.`);
    } catch (error) { setStatus(error instanceof Error ? error.message : "Teleport failed."); }
  };
  return <div className="tool-page"><h2>Teleport</h2><div className="inline-tool"><input value={destination} onChange={(e) => setDestination(e.target.value)} placeholder="secondlife://Region/x/y/z" /><button onClick={() => void teleport()} disabled={!destination.trim()}>Go</button></div>{status ? <p className="tool-status">{status}</p> : null}</div>;
}

export function DiagnosticsScreen() {
  const capabilities = Object.keys(app.protocol.capabilities || {});
  return <div className="tool-page"><section className="runtime-card"><h2>Connection</h2><dl><dt>State</dt><dd>{app.protocol.connected ? "Connected" : "Disconnected"}</dd><dt>Agent</dt><dd>{app.protocol.agentId || "—"}</dd><dt>Session</dt><dd>{app.protocol.sessionId || "—"}</dd><dt>Capabilities</dt><dd>{capabilities.length}</dd></dl></section>{capabilities.length ? <section className="runtime-card"><h2>Capabilities</h2>{capabilities.sort().map((name) => <small key={name}>{name}</small>)}</section> : null}</div>;
}

// The animation overrider is a worn attachment with its own scripts. The grid
// does not tell the viewer which attachments, scripts or animations are running,
// so there is nothing real to list here yet.
export function AOScreen() {
  return <div className="tool-page"><Empty icon="person-standing">Animation overrider status is not available. The viewer does not receive worn-attachment or script information from the grid yet.</Empty></div>;
}
