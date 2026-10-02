import { app } from "../linkpoint/app.ts";

// Navigation metadata only. Runtime counts and labels are derived from the
// managers below rather than being embedded as design/demo fixtures.
export const NAV_ALL = [
  { id: "Chat", label: "CHAT", tile: "chat", icon: "message-square" },
  { id: "Friends", label: "FRIENDS", tile: "people", icon: "users" },
  { id: "Radar", label: "RADAR", tile: "radar", icon: "radar" },
  { id: "Map", label: "MAP", tile: "map", icon: "map" },
  { id: "3D View", label: "3D WORLD", tile: "3d", icon: "box" },
  { id: "Inventory", label: "INV", tile: "inventory", icon: "folder" },
  { id: "Outfits", label: "OUTFITS", tile: "outfits", icon: "shirt" },
  { id: "Objects", label: "OBJECTS", tile: "objects", icon: "box" },
  { id: "Parcel", label: "PARCEL", tile: "parcel", icon: "map-pin" },
  { id: "Transactions", label: "L$", tile: "money", icon: "banknote" },
  { id: "Mute List", label: "MUTED", tile: "muted", icon: "volume-x" },
  { id: "Notecards", label: "NOTES", tile: "notecards", icon: "file-text" },
  { id: "Media", label: "MEDIA", tile: "media", icon: "radio" },
  { id: "Accounts", label: "ACCOUNTS", tile: "accounts", icon: "contact" },
  { id: "Grids", label: "GRIDS", tile: "grids", icon: "network" },
  { id: "Settings", label: "MORE", tile: "settings", icon: "settings" },
];

export const TABS_NAV_IDS = ["Chat", "Friends", "Radar", "Map", "3D View", "Settings"];

export const COMPASS = ["N", "NNE", "NE", "ENE", "E", "ESE", "SE", "SSE", "S", "SSW", "SW", "WSW", "W", "WNW", "NW", "NNW"];

export const HEAD = (layoutName, paletteName) => {
  const connected = app.auth.isLoggedIn();
  const resident = connected ? app.auth.getUserDisplayName() : "disconnected";
  const region = app.world.region;
  const position = app.world.avatarPosition;
  const friends = app.friends.getFriends();
  const online = friends.filter((friend) => friend.onlineStatus === "online").length;
  const inventoryItems = app.inventory.items.size;
  const inventoryFolders = app.inventory.folders.size;
  const muted = app.chatExtended.getMutedUsers?.().length || 0;
  const coordinate = position ? ` <${position.map((value) => Math.round(value)).join(", ")}>` : "";
  return {
    Chat: ["CHAT", connected ? `> ${resident}${region?.name ? ` @ ${region.name}` : ""}` : "> disconnected"],
    Friends: ["FRIENDS", `> ${online} online / ${friends.length} loaded`],
    Radar: ["RADAR", `> ${app.world.nearbyUsers.length} nearby avatars · ${app.world.objects.length} scene objects`],
    Map: ["WORLD MAP", region ? `> ${region.name || "unnamed region"}${coordinate}` : "> waiting for region data"],
    Inventory: ["INVENTORY", `> ${inventoryItems} items · ${inventoryFolders} folders`],
    Profile: ["PROFILE", `> ${resident}`],
    Groups: ["GROUPS", `> ${app.groups.getGroups().length} loaded`],
    Notices: ["NOTIFICATIONS", `> ${app.notifications.items.length} received this session`],
    Teleport: ["TELEPORT", region ? `> currently in ${region.name || "unnamed region"}` : "> no current region"],
    Settings: ["SETTINGS", `> ${layoutName} layout / ${paletteName} colour`],
    Cache: ["CACHE", "> local cache statistics"],
    Diagnostics: ["DIAGNOSTICS", `> ${app.protocol.state.toLowerCase()} · ${Object.keys(app.protocol.capabilities || {}).length} capabilities`],
    Outfits: ["OUTFITS", `> ${inventoryItems} inventory items available to inspect`],
    Objects: ["OBJECTS", `> ${app.world.objects.length} simulator objects`],
    Parcel: ["PARCEL", region?.parcel ? `> ${region.parcel.Name || region.parcel.name || "unnamed parcel"}` : "> waiting for parcel properties"],
    Transactions: ["L$ TRANSACTIONS", `> ${app.auth.user?.transactions?.length || 0} records returned`],
    "Mute List": ["MUTE LIST", `> ${muted} muted`],
    "3D View": ["", ""], Login: ["", ""], Search: ["SEARCH", "> session residents"],
  };
};
