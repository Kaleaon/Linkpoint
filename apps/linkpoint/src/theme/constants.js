/** Runtime layout and protocol metadata. No sample residents or simulator state. */
export const DEVICES = {
  ios: { name: "Compact phone", dims: "viewport", w: 393, h: 852, split: false, notch: "none" },
  and: { name: "Phone", dims: "viewport", w: 412, h: 892, split: false, notch: "none" },
  tab: { name: "Tablet", dims: "viewport", w: 1194, h: 834, split: true, notch: "none" },
  fold: { name: "Foldable", dims: "viewport", w: 840, h: 880, split: true, notch: "none" },
  desk: { name: "Desktop", dims: "viewport", w: 1440, h: 900, split: true, notch: "none", desk: true },
};

export const FBAR = 0;
export const FLOATERS = [];
export const FMENU = [];
export const SCREENS = ["Chat", "Friends", "Radar", "Map", "3D View", "Inventory", "Profile", "Groups", "Notices", "Teleport", "Outfits", "Objects", "Parcel", "Transactions", "Mute List", "Notecards", "Media", "Accounts", "Grids", "Settings", "Diagnostics", "Login", "Search"];
export const GRIDS = [
  { key: "agni", label: "Second Life", host: "https://login.agni.lindenlab.com/cgi-bin/login.cgi" },
  { key: "aditi", label: "Second Life Beta", host: "https://login.aditi.lindenlab.com/cgi-bin/login.cgi" },
  { key: "osgrid", label: "OSgrid", host: "https://login.osgrid.org/" },
  { key: "kitely", label: "Kitely", host: "https://grid.kitely.com:8002/" },
];
export const CBTN = {};
export const CSUB = {};
export const CPAD = [];
export const CPADR = {};
export const HUDS = [];
export const HUD_DEFAULT = {};
export const TARGETS = [];
export const STATES = {
  loading: { _: { icon: "loader", title: "LOADING", body: "Waiting for live grid data." } },
  empty: { _: { icon: "inbox", title: "NO DATA", body: "The grid has not returned data for this view." } },
  error: { _: { icon: "wifi-off", title: "CONNECTION ERROR", body: "The grid request failed." } },
};
