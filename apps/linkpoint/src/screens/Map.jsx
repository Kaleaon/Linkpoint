import { useCallback, useEffect, useMemo, useState } from "react";
import { useTheme } from "../context/ThemeContext.jsx";
import { app } from "../linkpoint/app.ts";
import { slBridge } from "../linkpoint/sl-bridge.ts";
import { MAP_WATER_COLOR, indexBlocks, mapRange, mapTileUrl, mapTiles, ratingName, teleportTarget } from "../linkpoint/map-tiles.ts";
import Icon from "../components/Icon.jsx";

const MIN_RADIUS = 1, MAX_RADIUS = 6;

/** One map square: the region's tile image, or open water where no region exists (as the official map shows). */
function Tile({ tile, block, known, selected, showImage, onSelect, V }) {
  const [failed, setFailed] = useState(false);
  useEffect(() => setFailed(false), [tile.x, tile.y]);
  const hasRegion = Boolean(block);
  // Once the grid has answered, only regions it lists get an image request; before that, try every tile.
  const drawImage = showImage && !failed && (!known || hasRegion);
  return <button type="button" onClick={() => onSelect(tile)} aria-label={block ? `${block.name} (${tile.x}, ${tile.y})` : `Open water (${tile.x}, ${tile.y})`}
    style={{ position: "relative", padding: 0, border: selected ? `2px solid ${V.pri}` : "1px solid rgba(0,0,0,.25)", background: MAP_WATER_COLOR, minWidth: 0, minHeight: 0, overflow: "hidden", cursor: hasRegion ? "pointer" : "default" }}>
    {drawImage ? <img src={mapTileUrl(tile.x, tile.y)} alt="" loading={tile.center ? "eager" : "lazy"} onError={() => setFailed(true)} style={{ width: "100%", height: "100%", display: "block", objectFit: "cover" }} /> : null}
    {block ? <span style={{ position: "absolute", left: 3, bottom: 2, right: 3, font: "600 10px/1.2 sans-serif", color: "#fff", textShadow: "0 1px 2px #000", textAlign: "left", overflow: "hidden", textOverflow: "ellipsis", whiteSpace: "nowrap" }}>{block.name}</span> : null}
    {tile.center ? <Icon name="map-pin" size={26} style={{ position: "absolute", left: "50%", top: "50%", color: V.pri, filter: "drop-shadow(0 1px 2px #000)", transform: "translate(-50%, -100%)" }} /> : null}
  </button>;
}

export default function Map() {
  const { V, t } = useTheme();
  const [region, setRegion] = useState(app.world.region);
  const [pan, setPan] = useState({ x: 0, y: 0 });
  const [radius, setRadius] = useState(3);
  const [blocks, setBlocks] = useState(null); // null until the grid has answered
  const [selected, setSelected] = useState(null);
  const [message, setMessage] = useState("");

  useEffect(() => {
    const changed = (next) => { setRegion(next); setPan({ x: 0, y: 0 }); setBlocks(null); };
    app.world.on("region_changed", changed);
    return () => app.world.off("region_changed", changed);
  }, []);

  const hasCoordinates = Number.isFinite(region?.x) && Number.isFinite(region?.y);
  const isSecondLife = ["agni", "aditi"].includes(String(app.auth.user?.grid || "").toLowerCase());
  const view = hasCoordinates ? { x: Math.max(0, region.x + pan.x), y: Math.max(0, region.y + pan.y) } : null;
  const tiles = useMemo(() => (view ? mapTiles(view, radius) : []), [view?.x, view?.y, radius]);

  // Ask the simulator for the names and ratings of every region in view, like the official map's block requests.
  useEffect(() => {
    if (!view || !slBridge.connected) return undefined;
    let cancelled = false;
    slBridge.call("getMapBlocks", mapRange(view, radius))
      .then((list) => { if (!cancelled) setBlocks(indexBlocks(Array.isArray(list) ? list : [])); })
      .catch((error) => { if (!cancelled) { console.warn("[Map] map blocks unavailable:", error); setBlocks(null); } });
    return () => { cancelled = true; };
  }, [view?.x, view?.y, radius]);

  const move = useCallback((dx, dy) => setPan((p) => ({ x: p.x + dx, y: p.y + dy })), []);
  const block = (tile) => blocks?.get(`${tile.x},${tile.y}`) || null;
  const chosen = selected ? { tile: selected, block: block(selected) } : null;

  const teleport = async () => {
    if (!chosen?.block) return;
    setMessage("");
    try { await app.protocol.teleportTo(teleportTarget(chosen.block)); setMessage(`Teleport to ${chosen.block.name} requested`); }
    catch (error) { setMessage(error?.message || "Teleport failed"); }
  };

  const button = { border: `1px solid ${V.outv}`, background: V.surf, color: V.on, borderRadius: 6, padding: "4px 10px", cursor: "pointer" };

  return <section className="live-screen">
    {region ? <div className="tool-page">
      {tiles.length ? <>
        <div role="toolbar" aria-label="Map controls" style={{ display: "flex", gap: 6, flexWrap: "wrap", alignItems: "center" }}>
          <button type="button" style={button} onClick={() => move(0, 1)} aria-label="Pan north">N</button>
          <button type="button" style={button} onClick={() => move(0, -1)} aria-label="Pan south">S</button>
          <button type="button" style={button} onClick={() => move(-1, 0)} aria-label="Pan west">W</button>
          <button type="button" style={button} onClick={() => move(1, 0)} aria-label="Pan east">E</button>
          <button type="button" style={button} onClick={() => setPan({ x: 0, y: 0 })}>My region</button>
          <button type="button" style={button} disabled={radius <= MIN_RADIUS} onClick={() => setRadius((r) => Math.max(MIN_RADIUS, r - 1))} aria-label="Zoom in">+</button>
          <button type="button" style={button} disabled={radius >= MAX_RADIUS} onClick={() => setRadius((r) => Math.min(MAX_RADIUS, r + 1))} aria-label="Zoom out">−</button>
        </div>
        <div aria-label={`Map around ${region.name || "the current region"}`} style={{ position: "relative", width: "min(100%, 720px)", aspectRatio: "1", display: "grid", gridTemplateColumns: `repeat(${radius * 2 + 1}, 1fr)`, gridTemplateRows: `repeat(${radius * 2 + 1}, 1fr)`, overflow: "hidden", border: `1px solid ${V.outv}`, background: MAP_WATER_COLOR }}>
          {tiles.map((tile) => <Tile key={`${tile.x}-${tile.y}`} tile={tile} block={block(tile)} known={blocks !== null} showImage={isSecondLife}
            selected={selected?.x === tile.x && selected?.y === tile.y} onSelect={setSelected} V={V} />)}
        </div>
      </> : null}
      <article className="runtime-card" style={{ borderColor: V.outv, background: V.surf }}>
        <Icon name="map-pin" size={26} style={{ color: V.pri }} />
        {chosen ? <>
          <h2 style={{ font: `600 18px/1.3 ${t.dfont}` }}>{chosen.block?.name || "Open water"}</h2>
          <dl>
            <dt>Grid coordinates</dt><dd>{chosen.tile.x}, {chosen.tile.y}</dd>
            {chosen.block ? <><dt>Maturity</dt><dd>{ratingName(chosen.block.access)}</dd></> : <><dt>Region</dt><dd>No region here</dd></>}
          </dl>
          {chosen.block ? <button type="button" style={button} onClick={teleport}>Teleport</button> : null}
          {message ? <small role="status">{message}</small> : null}
        </> : <>
          <h2 style={{ font: `600 18px/1.3 ${t.dfont}` }}>{region.name || "Current region"}</h2>
          <dl><dt>Region ID</dt><dd>{region.id || "Not supplied by grid"}</dd><dt>Grid coordinates</dt><dd>{hasCoordinates ? `${region.x}, ${region.y}` : "Not supplied by grid"}</dd>{region.parcel ? <><dt>Parcel</dt><dd>{region.parcel.Name || region.parcel.name || "Unnamed parcel"}</dd></> : null}</dl>
          <small>Select a square to see its region.</small>
        </>}
        {!isSecondLife && <small>Map images are not advertised by this grid; regions are listed by name and empty squares are open water.</small>}
      </article>
    </div> : <div className="honest-empty"><Icon name="map" size={30} /><h2>Map</h2><p>{app.auth.isLoggedIn() ? "Waiting for a region handshake from the simulator." : "Connect to a grid to load your current region."}</p></div>}
  </section>;
}
