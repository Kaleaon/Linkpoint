// Ported verbatim from index.html's <script> block: device list,
// the desktop floater/window model, the menu bar, screen order, custom dock
// buttons, per-screen sub-segments, the movement pad, worn HUDs, world-view
// targets and the loading/empty/error copy bank.

export const DEVICES = {
  ios:  { name: "iPhone 15 Pro", dims: "393×852", w: 393, h: 852, split: false, notch: "island" },
  and:  { name: "Pixel 8", dims: "412×892", w: 412, h: 892, split: false, notch: "hole" },
  tab:  { name: 'Tablet 12.9" landscape', dims: "1194×834", w: 1194, h: 834, split: true, notch: "none" },
  fold: { name: "Foldable, unfolded", dims: "840×880", w: 840, h: 880, split: true, notch: "hole" },
  desk: { name: "Desktop", dims: "1440×900", w: 1440, h: 900, split: true, notch: "none", desk: true },
};

// Desktop SL is not a screen stack — it is N resizable windows over one scene. The
// floater set is the window model: position, size, z-order and minimise all live in state.
export const FBAR = 26;
export const FLOATERS = [
  { id:"Chat",        title:"Local Chat",    icon:"message-square", x:20,   y:24,  w:424, h:296 },
  { id:"Radar",       title:"Nearby",        icon:"radar",          x:462,  y:24,  w:372, h:296 },
  { id:"Friends",     title:"People",        icon:"users",          x:20,   y:340, w:300, h:262 },
  { id:"Inventory",   title:"Inventory",     icon:"folder",         x:1056, y:24,  w:346, h:420 },
  { id:"Map",         title:"World Map",     icon:"map",            x:462,  y:340, w:372, h:262 },
  { id:"Profile",     title:"Profile",       icon:"user",           x:340,  y:110, w:392, h:430 },
  { id:"Groups",      title:"Groups",        icon:"users-round",    x:852,  y:24,  w:196, h:252 },
  { id:"Notices",     title:"Notifications", icon:"bell",           x:852,  y:292, w:196, h:252 },
  { id:"Teleport",    title:"Places",        icon:"zap",            x:330,  y:150, w:370, h:350 },
  { id:"Settings",    title:"Preferences",   icon:"settings",       x:290,  y:80,  w:540, h:470 },
  { id:"Cache",       title:"Cache",         icon:"hard-drive",     x:360,  y:120, w:460, h:420 },
  { id:"Diagnostics", title:"Statistics",    icon:"activity",       x:1056, y:462, w:346, h:196 },
  { id:"Search",      title:"Search",        icon:"search",         x:390,  y:70,  w:520, h:440 },
  { id:"Contacts",    title:"Contacts",      icon:"contact",        x:360,  y:90,  w:500, h:460 },
  { id:"Calendar",    title:"Calendar",      icon:"calendar",       x:420,  y:90,  w:500, h:470 },
];
export const FMENU = [
  { label:"File",  items:[["Upload Image…","⌘U"],["Take Snapshot","⌘`"],["Save Texture As…",""],["Quit","⌘Q"]] },
  { label:"Edit",  items:[["Undo","⌘Z"],["Redo","⇧⌘Z"],["Appearance…",""],["Preferences…","⌘,"]] },
  { label:"View",  items:"WINDOWS" },
  { label:"World", items:[["Teleport Home","⇧⌘H"],["Set Home to Here",""],["About Land…",""],["Region / Estate…",""]] },
  { label:"Build", items:[["Focus","⌥1"],["Move","⌥2"],["Edit","⌥3"],["Create","⌥4"],["Land","⌥5"]] },
  { label:"Help",  items:[["Second Life Help","F1"],["Report Abuse…",""],["Report Bug…",""],["About Linkpoint",""]] },
];

export const SCREENS = ["Chat","Friends","Radar","Map","3D View","Inventory","Profile","Groups","Notices","Teleport","Outfits","Objects","Parcel","Transactions","Mute List","Settings","Cache","Diagnostics","Login","Search","Contacts","Calendar"];
// Grid picker for Login: Second Life's own two (Agni/Aditi) plus a few
// well-known OpenSim grids, so the login screen isn't LL-only.
export const GRIDS = [
  { key: "agni", label: "Second Life (Main Grid - Agni)", host: "login.agni.lindenlab.com" },
  { key: "aditi", label: "Second Life Beta (Aditi)", host: "login.aditi.lindenlab.com" },
  { key: "osgrid", label: "OSgrid (OpenSim)", host: "login.osgrid.org" },
  { key: "kitely", label: "Kitely (OpenSim)", host: "login.kitely.com" },
];

// Firestorm-style custom button array: the user's dock is a list of keys into this palette.
export const CBTN = {
  // `nav` opens the matching screen. Everything else is not implemented yet and
  // says so when pressed; no button invents a toggle state or a permission.
  fly:  { label: "FLY",   icon: "plane" },
  sit:  { label: "SIT",   icon: "armchair" },
  snap: { label: "SNAP",  icon: "camera" },
  mini: { label: "MAP",   icon: "map",             nav: "Map" },
  inv:  { label: "INV",   icon: "package",         nav: "Inventory" },
  home: { label: "HOME",  icon: "house" },
  ao:   { label: "AO",    icon: "person-standing" },
  sun:  { label: "NOON",  icon: "sun" },
  mute: { label: "MUTE",  icon: "volume-x" },
  drnd: { label: "DEREND",icon: "eye-off" },
  rgn:  { label: "REGION",icon: "info",            nav: "Parcel" },
  bld:  { label: "BUILD", icon: "hammer" },
};

// Rail sub-segments are per-screen sub-nav, not decoration: each active area exposes
// its own two or three sub-views the way LCARS indents sub-functions off the spine.
// Every screen's sub-views, in one table. This is the model, not decoration: the
// LCARS rail indents it off the spine as sub-segments, every other layout pack
// renders it as the segmented tab strip above the body, and both write the same
// `tabs[screen]` state that the screen bodies filter on. A screen missing from
// this table simply has no sub-views. Mirrors the CSUB table in docs/index.html.
export const CSUB = {
  "3D View":     [["CAM", "· 01"], ["GFX", "· 02"]],
  Chat:          [["LOCAL", "· 01"], ["IM", "· 02"], ["GROUP", "· 03"]],
  Friends:       [["ALL", "· 01"], ["ONLINE", "· 02"], ["CONTACTS", "· 03"]],
  Contacts:      [["SAVED", "· 01"], ["ADD FROM SL", "· 02"]],
  Calendar:      [["NOTICES", "· 01"], ["GOOGLE CALENDAR", "· 02"]],
  Radar:         [["AVATAR", "· 01"], ["OBJECT", "· 02"]],
  Map:           [["WORLD", "· 01"], ["MINI", "· 02"]],
  Inventory:     [["ALL", "· 01"], ["RECENT", "· 02"], ["WORN", "· 03"]],
  Profile:       [["2ND LIFE", "· 01"], ["PICKS", "· 02"]],
  Groups:        [["GROUPS", "· 01"], ["ROLES", "· 02"]],
  Notices:       [["NOTICES", "· 01"], ["CALENDAR", "· 02"]],
  Teleport:      [["LANDMARK", "· 01"], ["HISTORY", "· 02"]],
  Settings:      [["PREFS", "· 01"], ["CACHE", "· 02"]],
  Cache:         [["PREFS", "· 01"], ["CACHE", "· 02"]],
  Diagnostics:   [["AGNI", "· 01"], ["ADITI", "· 02"]],
  Outfits:       [["WORN", "· 01"], ["SAVED", "· 02"]],
  Objects:       [["NEARBY", "· 01"], ["INSPECT", "· 02"]],
  Parcel:        [["GENERAL", "· 01"], ["MEDIA", "· 02"]],
  Transactions:  [["ALL", "· 01"], ["PAYMENTS", "· 02"]],
  "Mute List":   [["AVATARS", "· 01"], ["OBJECTS", "· 02"]],
};

// The screen's current sub-view: `tabs[screen]` when it is one of that screen's
// own labels, otherwise the first label. Every screen defaults to its leading
// sub-view rather than to an implicit "no filter", so the tab strip and the
// LCARS sub-nav always agree on what is highlighted.
//  - Radar's sub-view predates this table and still lives in `rMode`.
//  - Preferences and Cache are two screens, not two tabs, so the sub-view is
//    simply which of them you are on.
export const subView = (state, scr) => {
  const subs = (CSUB[scr] || []).map((x) => x[0]);
  if (!subs.length) return null;
  if (scr === "Radar") return state.rMode === "OBJ" ? "OBJECT" : "AVATAR";
  if (scr === "Settings" || scr === "Cache") return scr === "Cache" ? "CACHE" : "PREFS";
  const cur = state.tabs[scr];
  return subs.includes(cur) ? cur : subs[0];
};
export const setSub = (actions, scr, label) => {
  if (scr === "Radar") return actions.setRMode(label === "AVATAR" ? "AV" : "OBJ");
  if (scr === "Settings" || scr === "Cache") return actions.setScreen(label === "CACHE" ? "Cache" : "Settings");
  return actions.setTab(scr, label);
};
// A card tagged `sub` belongs to those sub-views only; an untagged card shows
// under all of them. Section headings carry the tag too, so a heading never
// outlives the rows beneath it.
export const inSub = (c, curSub) => !c.sub || (Array.isArray(c.sub) ? c.sub.includes(curSub) : c.sub === curSub);

// Movement pad: turning is drag-to-look, so the pad is a cross with a camera-mode centre.
export const CPAD = [
  { k: "" },                          { k: "fwd", icon: "chevron-up" },    { k: "" },
  { k: "lft", icon: "chevron-left" }, { k: "cam", icon: "video" },        { k: "rgt", icon: "chevron-right" },
  { k: "" },                          { k: "bck", icon: "chevron-down" },  { k: "" },
];
export const CPADR = { 1: "50% 50% 0 0", 3: "50% 0 0 50%", 5: "0 50% 50% 0", 7: "0 0 50% 50%" };

// Worn HUDs, Lumiya-style: the viewer lists what you have on, you pick which ones
// paint over the 3D view, and each visible one can be dragged to a new spot.
export const HUDS = [];
export const HUD_DEFAULT = {};
export const TARGETS = [];

// Loading / empty / error copy per screen, with a generic fallback for the rest.
export const STATES = {
  loading: { _: { sub: "> awaiting live simulator response", icon: "loader", title: "LOADING", body: "Waiting for data from the current grid connection.", bar: 0 } },
  empty: { _: { sub: "> no live records", icon: "inbox", title: "NOTHING RECEIVED", body: "The current grid session has not supplied records for this view.", btn: "REFRESH" } },
  error: { _: { sub: "> live request failed", icon: "plug-zap", title: "CONNECTION ERROR", body: "The current grid request failed. Check the connection diagnostics and try again.", btn: "RETRY" } },
};
