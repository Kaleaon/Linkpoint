import { useEffect, useMemo, useState } from "react";
import { app } from "../linkpoint/app.ts";
import Icon from "../components/Icon.jsx";

function Empty({ icon, children }) {
  return <div className="honest-empty"><Icon name={icon} size={30} /><p>{children}</p></div>;
}

function Rows({ rows, icon = "circle" }) {
  return <div className="live-list">{rows.map((row) => <div className="inventory-row" key={row.id || row.name || row.title}><Icon name={row.icon || icon} size={17} /><div><strong>{row.name || row.title || row.id}</strong>{row.meta || row.message ? <small>{row.meta || row.message}</small> : null}</div></div>)}</div>;
}

export function FriendsScreen() {
  const [, refresh] = useState(0);
  useEffect(() => { const listener = () => refresh((n) => n + 1); app.friends.addStatusListener(listener); return () => app.friends.removeStatusListener(listener); }, []);
  const rows = app.friends.getFriends().map((friend) => ({ ...friend, meta: friend.onlineStatus || "unknown" }));
  return rows.length ? <Rows rows={rows} icon="user" /> : <Empty icon="users">No friends have been received from the grid.</Empty>;
}

export function RadarScreen() {
  const [objects, setObjects] = useState(() => [...app.world.objects]);
  const [nearby, setNearby] = useState(() => [...app.world.nearbyUsers]);
  useEffect(() => { const refresh = (items) => setObjects([...items]); app.world.on("objects_changed", refresh); return () => app.world.off("objects_changed", refresh); }, []);
  useEffect(() => { const refresh = (items) => setNearby([...items]); app.world.on("nearby_changed", refresh); return () => app.world.off("nearby_changed", refresh); }, []);
  const rows = [
    ...nearby.map((user) => ({ ...user, name: user.name || user.id, meta: user.distance != null ? `${user.distance.toFixed(1)} m` : "Nearby avatar", icon: "user" })),
    ...objects.map((object) => ({ ...object, name: object.name || object.id, meta: object.distance != null ? `${object.distance} m` : "Simulator object" })),
  ];
  return rows.length ? <Rows rows={rows} icon="box" /> : <Empty icon="radar">No nearby simulator objects or avatars have been received.</Empty>;
}

export function MapScreen() {
  const [region, setRegion] = useState(app.world.region);
  useEffect(() => { const refresh = (next) => setRegion(next); app.world.on("region_changed", refresh); return () => app.world.off("region_changed", refresh); }, []);
  return region ? <Rows rows={[{ id: region.id || region.name, name: region.name, meta: [region.x, region.y].filter((v) => v != null).join(", ") }]} icon="map-pin" /> : <Empty icon="map">No region handshake has been received.</Empty>;
}

export function WorldScreen() {
  useEffect(() => { void app.world.init(); return () => app.world.stopRendering(); }, []);
  return <div className="world-runtime"><canvas id="world-canvas" aria-label="Live simulator scene" />{!app.world.region ? <div className="world-overlay">Waiting for a live simulator scene</div> : null}</div>;
}

export function GroupsScreen() {
  const rows = app.groups.getGroups();
  return rows.length ? <Rows rows={rows} icon="users-round" /> : <Empty icon="users-round">No group records have been received.</Empty>;
}

export function NoticesScreen() {
  const [items, setItems] = useState(() => [...app.notifications.items]);
  useEffect(() => { const refresh = () => setItems([...app.notifications.items]); app.notifications.on("notification_received", refresh); app.notifications.on("cleared", refresh); return () => { app.notifications.off("notification_received", refresh); app.notifications.off("cleared", refresh); }; }, []);
  return items.length ? <><Rows rows={items} icon="bell" /><button className="screen-action" onClick={() => app.notifications.clear()}>Clear notifications</button></> : <Empty icon="bell">No notifications have been received.</Empty>;
}

export function MuteListScreen() {
  const [entry, setEntry] = useState("");
  const [revision, setRevision] = useState(0);
  const muted = useMemo(() => app.chatExtended.getMutedUsers?.() || [], [revision]);
  const add = () => { if (entry.trim()) { app.chatExtended.muteUser(entry.trim()); setEntry(""); setRevision((n) => n + 1); } };
  return <div className="tool-screen"><div className="inline-tool"><input value={entry} onChange={(e) => setEntry(e.target.value)} placeholder="Avatar UUID" /><button onClick={add}>Mute</button></div>{muted.length ? <Rows rows={muted.map((id) => ({ id, name: id }))} icon="volume-x" /> : <Empty icon="volume-x">No avatars are muted.</Empty>}</div>;
}

export function GenericInventoryScreen({ kind }) {
  const wearableTypes = new Set([5, 13, 18, 19, 20, 21, 22, 23, 24]);
  const values = Array.from(app.inventory.items.values()).filter((item) => kind === "wearable" ? wearableTypes.has(Number(item.assetType)) : Number(item.assetType) === 6);
  return values.length ? <Rows rows={values} icon="package" /> : <Empty icon="package">No {kind.toLowerCase()} data has been loaded from inventory.</Empty>;
}

export function ConnectionScreen({ title }) {
  return <Empty icon="plug">{app.auth.isLoggedIn() ? `${title} is waiting for data from the current grid.` : `Connect to a grid to use ${title.toLowerCase()}.`}</Empty>;
}

export function SearchScreen() {
  const [query, setQuery] = useState("");
  const [status, setStatus] = useState("");
  const matches = app.friends.getFriends().filter((friend) => String(friend.name || friend.id).toLowerCase().includes(query.trim().toLowerCase()));
  const request = async () => {
    if (!query.trim()) return;
    try {
      await app.friends.sendFriendRequest(query.trim());
      setStatus("Friend request queued.");
    } catch (error) {
      setStatus(error instanceof Error ? error.message : "Unable to queue friend request.");
    }
  };
  return <div className="tool-screen"><div className="inline-tool"><input value={query} onChange={(e) => setQuery(e.target.value)} placeholder="Resident UUID or loaded name" aria-label="Resident search" /><button onClick={() => void request()}>Add</button></div>{status ? <p className="tool-status">{status}</p> : null}{query && matches.length ? <Rows rows={matches} icon="user" /> : <Empty icon="search">Search checks residents received in this session. Enter a resident UUID to send a request.</Empty>}</div>;
}
