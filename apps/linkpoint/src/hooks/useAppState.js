import { useCallback, useEffect, useRef, useState } from "react";
import { LAYOUTS } from "../theme/layouts.js";
import { PALETTES } from "../theme/palettes.js";
import { DEVICES, FLOATERS, HUD_DEFAULT, HUDS, CBTN, GRIDS } from "../theme/constants.js";
import { decodeSharedTheme, encodeSharedTheme, readSavedTheme, sanitizeTheme, themeFromPalette, THEME_STORAGE_KEY } from "../theme/customTheme.js";
import { app } from "../linkpoint/app";

// Ported from the mockup's `state = {...}` initializer and its instance
// methods (flR/flDrag/flFocus/flToggle/flClose, hudDrag/toggleHud, T/D/navMode,
// set/setTab/dismiss/toggleSetting/pin/cycle, cf/cTap/cHold/cPress, and the
// tick interval in componentDidMount). This hook is the state + actions layer;
// theme/viewModel.js is the "renderVals()" computation layer that consumes it.
export function useAppState() {
  const [layout, setLayout] = useState("terminal");
  const [palette, setPalette] = useState("ink");
  const [customTheme, setCustomTheme] = useState(() => {
    const shared = decodeSharedTheme(new URLSearchParams(window.location.search).get("theme") || "");
    return shared || readSavedTheme() || themeFromPalette(PALETTES.ink);
  });
  const [viewMode, setViewModeState] = useState(() => {
    try {
      return localStorage.getItem("linkpoint_view_mode") || "auto";
    } catch {
      return "auto";
    }
  });

  const deviceForViewport = useCallback(() => {
    const width = window.innerWidth;
    if (width >= 1280) return "desk";
    if (width >= 900) return "tab";
    if (width >= 600) return "fold";
    return width >= 400 ? "and" : "ios";
  }, []);

  const resolveDevice = useCallback((mode) => {
    if (mode === "desktop") return "desk";
    if (mode === "mobile") return "and";
    return deviceForViewport();
  }, [deviceForViewport]);

  const [device, setDevice] = useState(() => resolveDevice(viewMode));

  useEffect(() => {
    if (viewMode !== "auto") return;
    const onResize = () => setDevice(deviceForViewport());
    window.addEventListener("resize", onResize);
    return () => window.removeEventListener("resize", onResize);
  }, [viewMode, deviceForViewport]);
  const [screen, setScreen] = useState("Login");
  const [dialog, setDialog] = useState(null);
  const [dense, setDense] = useState(false);
  const [tabs, setTabs] = useState({ Chat: "LOCAL", Friends: "ALL", Diagnostics: "AGNI" });
  const [chip, setChip] = useState("");
  const [tileOk, setTileOk] = useState(true);
  const [invOpen, setInvOpen] = useState({ Objects: true });
  const [dismissed, setDismissed] = useState({});
  const [pinned, setPinned] = useState({});
  const [toggles, setToggles] = useState({
    largeType: false, push: true, voice: true, chatCmds: true, autoresponse: true,
    rlv: false, shadows: false, battery: true, timestamps: true, imLogs: true, mediaAuto: false,
    showOnline: true, typingSent: true, cacheOnExit: false,
  });
  // Everything the preferences screens expose as a <select>: one flat bag so a
  // new preference is one entry here plus one card, not a new state key each time.
  const [prefs, setPrefs] = useState({
    draw: "96 m", quality: "Balanced", fps: "60 fps", complexity: "80 000",
    volume: "70%", translate: "Off", maturity: "Moderate", bandwidth: "1 500 kbps",
    cacheLimit: 512, cacheLoc: "Internal storage",
  });
  const [cacheCleared, setCacheCleared] = useState({});
  const [camPreset, setCamPreset] = useState("ORBIT");
  const [cond, setCond] = useState("normal");
  const [hudOn, setHudOn] = useState({ ...HUD_DEFAULT });
  const [hudPos, setHudPos] = useState({});
  const [hudPicker, setHudPicker] = useState(false);
  const [target, setTarget] = useState(null);
  const [targetPicker, setTargetPicker] = useState(false);
  const [navPeek, setNavPeek] = useState(false);
  const [cPad, setCPad] = useState(true);
  const [cHeld, setCHeld] = useState("");
  const [cRun, setCRun] = useState(false);
  const [cEdit, setCEdit] = useState(false);
  const [cFlash, setCFlash] = useState("");
  const [cReason, setCReason] = useState("");
  const [cTog, setCTog] = useState({});
  const [rMode, setRMode] = useState("AV");
  const [rOpen, setROpen] = useState(null);
  const [rMenu, setRMenu] = useState(null);
  const [cDock, setCDock] = useState(["fly", "sit", "snap", "mini", "inv", "home", "ao", "sun"]);
  const [flOpen, setFlOpen] = useState({ Chat: true, Radar: true, Friends: true, Inventory: true, Map: true, Settings: true, AO: false });
  const [flMin, setFlMin] = useState({});
  const [flRect, setFlRect] = useState({});
  const [flZ, setFlZ] = useState(["Map", "Inventory", "Friends", "Radar", "Chat", "Settings"]);
  const [menu, setMenu] = useState(null);
  const [tick, setTick] = useState(0);
  const [loginMode, setLoginModeState] = useState("grid");
  const [loginGrid, setLoginGrid] = useState("agni");
  const [loginBusy, setLoginBusy] = useState(false);
  const [loginError, setLoginError] = useState(null);
  const [customGrids, setCustomGrids] = useState([]);
  const [addGrid, setAddGrid] = useState(false);
  const [addGridName, setAddGridName] = useState("");
  const [addGridHost, setAddGridHost] = useState("");
  const [searchFrom, setSearchFrom] = useState("Friends");
  const [searchTab, setSearchTab] = useState("FRIENDS");
  const [searchQuery, setSearchQuery] = useState("");
  const [searchState, setSearchState] = useState({});
  const [reconnecting, setReconnecting] = useState(false);
  const [toast, setToast] = useState("");

  // ---- movement pad: held buttons move the real camera --------------------
  useEffect(() => {
    const step = { fwd: [0, 1, 0], bck: [0, -1, 0], lft: [-1, 0, 0], rgt: [1, 0, 0], up: [0, 0, 1], dn: [0, 0, -1] }[cHeld];
    if (!step) return undefined;
    const metersPerTick = cRun ? 1.4 : 0.55;
    const timer = setInterval(() => app.world.moveCamera(step[0] * metersPerTick, step[1] * metersPerTick, step[2] * metersPerTick), 50);
    return () => clearInterval(timer);
  }, [cHeld, cRun]);

  // ---- tick clock (componentDidMount's setInterval) ---------------------
  useEffect(() => {
    const iv = setInterval(() => setTick((t) => t + 1), 1000);
    return () => clearInterval(iv);
  }, []);

  // Form factor is an implementation concern in the real app. The design
  // canvas exposes a manual device picker, but the React port follows its host
  // viewport and changes navigation/layout at the same breakpoints instead.
  useEffect(() => {
    const syncDevice = () => setDevice(deviceForViewport());
    window.addEventListener("resize", syncDevice);
    return () => window.removeEventListener("resize", syncDevice);
  }, [deviceForViewport]);

  // ---- timers / drag refs (were plain `this.x` fields on the class) -----
  const cflRef = useRef(null); // console-nav tap flash timeout
  const clpRef = useRef(null); // console dock long-press-to-edit timeout
  const rlpRef = useRef(null); // radar row long-press timeout
  const lpFiredRef = useRef(false);
  const loginTimerRef = useRef(null);
  const searchTimerRef = useRef(null);
  const reconnectTimerRef = useRef(null);
  const toastTimerRef = useRef(null);

  useEffect(
    () => () => {
      clearTimeout(cflRef.current);
      clearTimeout(clpRef.current);
      clearTimeout(rlpRef.current);
      clearTimeout(loginTimerRef.current);
      clearTimeout(searchTimerRef.current);
      clearTimeout(reconnectTimerRef.current);
      clearTimeout(toastTimerRef.current);
    },
    []
  );

  // ---- transient acknowledgement toast (notify) --------------------------
  // Ported from the mockup's `notify(msg)` — the shared feedback channel for
  // actions that don't have a more specific effect (dialog buttons, header
  // icons, map/profile/inventory taps, …).
  const notify = useCallback((msg) => {
    clearTimeout(toastTimerRef.current);
    setToast(msg);
    toastTimerRef.current = setTimeout(() => setToast(""), 2200);
  }, []);

  const setThemeColor = useCallback((key, value) => setCustomTheme((theme) => ({ ...theme, active: true, colors: { ...theme.colors, [key]: value } })), []);
  const renameTheme = useCallback((name) => setCustomTheme((theme) => ({ ...theme, active: true, name })), []);
  const selectPalette = useCallback((key) => { setPalette(key); setCustomTheme(themeFromPalette(PALETTES[key])); }, []);
  const saveTheme = useCallback(() => { localStorage.setItem(THEME_STORAGE_KEY, JSON.stringify(customTheme)); notify("Theme saved to this device."); }, [customTheme, notify]);
  const resetTheme = useCallback(() => { setCustomTheme(themeFromPalette(PALETTES[palette])); localStorage.removeItem(THEME_STORAGE_KEY); notify("Theme reset to the selected colour pack."); }, [palette, notify]);
  const importTheme = useCallback(async (json) => {
    try { const imported = sanitizeTheme(JSON.parse(json)); if (!imported) throw new Error(); setCustomTheme(imported); notify(`Imported “${imported.name}”.`); return true; }
    catch { notify("That file is not a valid Linkpoint theme."); return false; }
  }, [notify]);
  const downloadTheme = useCallback(() => {
    const blob = new Blob([JSON.stringify(customTheme, null, 2)], { type: "application/json" });
    const link = document.createElement("a"); link.href = URL.createObjectURL(blob); link.download = `${customTheme.name.toLowerCase().replace(/[^a-z0-9]+/g, "-").replace(/(^-|-$)/g, "") || "linkpoint-theme"}.json`; link.click(); URL.revokeObjectURL(link.href);
    notify("Theme JSON exported.");
  }, [customTheme, notify]);
  const shareTheme = useCallback(async () => {
    const url = new URL(window.location.href); url.searchParams.set("theme", encodeSharedTheme(customTheme));
    try { await navigator.clipboard.writeText(url.toString()); notify("Share link copied."); }
    catch { window.prompt("Copy this theme link", url.toString()); }
  }, [customTheme, notify]);

  // ---- derived lookups (T()/D()/navMode()) -------------------------------
  const T = useCallback(() => {
    const L = LAYOUTS[layout],
      P = PALETTES[palette];
    return {
      name: L.name + " / " + P.name,
      nav: L.nav,
      font: L.font,
      dfont: L.dfont,
      note: L.note + "   Colour pack: " + P.note + ".",
      v: { ...P.c, ...L.s },
    };
  }, [layout, palette]);

  const D = useCallback(() => DEVICES[device], [device]);

  const navMode = useCallback(() => {
    const t = T(),
      d = D();
    if (d.desk) return "floaters";
    if (t.nav === "SWEEP") return "sweep";
    if (d.split) return "rail";
    return t.nav === "TILES" ? "tiles" : t.nav === "RAIL" && d.w > 700 ? "rail" : "tabs";
  }, [T, D]);

  // ---- simple setters (set/setTab/dismiss/toggleSetting/pin/cycle) ------
  const setTab = useCallback((scr, v) => setTabs((s) => ({ ...s, [scr]: v })), []);
  const dismiss = useCallback((key) => setDismissed((s) => ({ ...s, [key]: true })), []);
  const toggleSetting = useCallback((key) => setToggles((s) => ({ ...s, [key]: !s[key] })), []);
  const setPref = useCallback((key, v) => setPrefs((s) => ({ ...s, [key]: v })), []);
  const pin = useCallback((key) => setPinned((s) => ({ ...s, [key]: !s[key] })), []);
  const cycleLayout = useCallback(() => {
    const ks = Object.keys(LAYOUTS);
    setLayout((cur) => ks[(ks.indexOf(cur) + 1) % ks.length]);
  }, []);
  const cyclePalette = useCallback(() => {
    const ks = Object.keys(PALETTES);
    const next = ks[(ks.indexOf(palette) + 1) % ks.length];
    setPalette(next);
    setCustomTheme(themeFromPalette(PALETTES[next]));
  }, [palette]);

  // ---- login (setLoginMode/setLoginGrid/connectLogin) --------------------
  // All known grids: the built-in Second Life / OpenSim presets plus
  // whatever the resident has added themselves this session.
  const allGrids = useCallback(() => GRIDS.concat(customGrids), [customGrids]);
  const setLoginMode = useCallback((m) => {
    setLoginModeState(m);
    setLoginError(null);
  }, []);
  const connectLogin = useCallback(async (gridKey, user, pass, remember = true, startLoc = "last") => {
    setLoginBusy(true);
    setLoginError(null);
    try {
      const gridToUse = gridKey || loginGrid;
      const gridObj = allGrids().find((g) => g.key === gridToUse);
      if (!gridObj || ["offline", "gemini"].includes(gridObj.key)) throw new Error("Select a live grid endpoint.");
      const endpoint = gridObj.key.startsWith("custom-") ? gridObj.host : gridObj.key;
      await app.auth.login(endpoint, user?.trim() || "", pass, remember, startLoc);
      setScreen("Chat");
      notify("Connected as " + app.auth.getUserDisplayName());
    } catch (err) {
      setLoginError(err instanceof Error ? err.message : "Unable to connect to grid. Check your connection.");
    } finally {
      setLoginBusy(false);
    }
  }, [loginGrid, allGrids, notify]);

  // ---- login: add a custom grid ------------------------------------------
  // A resident can point the viewer at any OpenSim grid, not just the
  // built-in presets — this is the mockup's "add custom grid URI" flow.
  const openAddGrid = useCallback(() => {
    setAddGrid(true);
    setAddGridName("");
    setAddGridHost("");
  }, []);
  const cancelAddGrid = useCallback(() => setAddGrid(false), []);
  const saveCustomGrid = useCallback(() => {
    const name = addGridName.trim(),
      host = addGridHost.trim();
    if (!name || !host) {
      notify("Grid name and login URI are both required");
      return;
    }
    const key =
      "custom-" +
      name.toLowerCase().replace(/[^a-z0-9]+/g, "-").replace(/(^-|-$)/g, "").slice(0, 24) +
      "-" +
      (customGrids.length + 1);
    setCustomGrids((g) => g.concat([{ key, label: name, host }]));
    setLoginGrid(key);
    setAddGrid(false);
    notify("Added grid — " + name);
  }, [addGridName, addGridHost, customGrids]);

  // ---- resident search (openSearch/searchAdd) -----------------------------
  // `tab` picks which of the picker's three panes opens first: FRIENDS (the
  // contacts list, default — matches a real SL viewer's "start a conversation"
  // flow), NEARBY (radar-range residents), or SEARCH (grid-wide name lookup).
  const openSearch = useCallback((from, tab = "FRIENDS") => {
    setSearchFrom(from);
    setSearchTab(tab);
    setSearchQuery("");
    setScreen("Search");
  }, []);
  const searchAdd = useCallback((name) => {
    setSearchState((s) => ({ ...s, [name]: "sending" }));
    clearTimeout(searchTimerRef.current);
    searchTimerRef.current = setTimeout(() => setSearchState((s) => ({ ...s, [name]: "sent" })), 700);
  }, []);
  // Start (or resume) an IM thread with a resident picked from Friends/Nearby.
  const startIm = useCallback((name) => {
    if (name) {
      app.chat.openSession(name);
    }
    setTabs((s) => ({ ...s, Chat: "IM" }));
    setChip(name);
    setScreen("Chat");
  }, []);

  // ---- cache management ---------------------------------------------------
  const clearCache = useCallback(
    (key, name) => {
      setCacheCleared((c) => ({ ...c, [key]: true }));
      notify(name + " cleared \u2014 assets refetch on demand");
    },
    [notify]
  );
  const clearAllCache = useCallback(() => {
    setCacheCleared({});
    notify("All caches cleared \u2014 assets refetch on demand");
  }, [notify]);

  // ---- settings: reconnect to grid ---------------------------------------
  const reconnect = useCallback(() => {
    setReconnecting(true);
    clearTimeout(reconnectTimerRef.current);
    reconnectTimerRef.current = setTimeout(() => setReconnecting(false), 1200);
  }, []);

  // ---- desktop floater window model (flR/flDrag/flFocus/flToggle/flClose) --
  const flR = useCallback(
    (id) => {
      const f = FLOATERS.find((x) => x.id === id) || { x: 24, y: 24, w: 320, h: 240 };
      return { ...f, ...(flRect[id] || {}) };
    },
    [flRect]
  );

  // Focusing a window has to open it as well: only five floaters start open, so
  // navigating to Preferences/Groups/Notices/Teleport/Profile/Statistics on Desktop
  // used to raise and un-minimise a window that was never opened — the screen
  // simply did not change.
  const flFocus = useCallback((id) => {
    setFlOpen((o) => (o[id] ? o : { ...o, [id]: true }));
    setFlZ((z) => z.filter((x) => x !== id).concat(id));
    setScreen(id);
    setFlMin((m) => ({ ...m, [id]: false }));
    setMenu(null);
  }, []);

  const flDrag = useCallback(
    (id, e, mode) => {
      e.preventDefault();
      e.stopPropagation();
      const r = flR(id),
        sx = e.clientX,
        sy = e.clientY;
      const move = (ev) => {
        const dx = ev.clientX - sx,
          dy = ev.clientY - sy;
        const n =
          mode === "size"
            ? { x: r.x, y: r.y, w: Math.max(216, r.w + dx), h: Math.max(96, r.h + dy) }
            : { x: Math.max(0, r.x + dx), y: Math.max(0, r.y + dy), w: r.w, h: r.h };
        setFlRect((st) => ({ ...st, [id]: n }));
      };
      const up = () => {
        window.removeEventListener("mousemove", move);
        window.removeEventListener("mouseup", up);
      };
      window.addEventListener("mousemove", move);
      window.addEventListener("mouseup", up);
      flFocus(id);
    },
    [flR, flFocus]
  );

  const flToggle = useCallback(
    (id) => {
      setFlOpen((open) => {
        const isMin = flMin[id];
        if (open[id] && !isMin) {
          setFlMin((m) => ({ ...m, [id]: true }));
          setMenu(null);
          return open;
        }
        setFlMin((m) => ({ ...m, [id]: false }));
        setFlZ((z) => z.filter((x) => x !== id).concat(id));
        setScreen(id);
        setMenu(null);
        return { ...open, [id]: true };
      });
    },
    [flMin]
  );

  const flClose = useCallback((id) => {
    setFlOpen((o) => ({ ...o, [id]: false }));
    setScreen((cur) => (cur === id ? (app.auth.isLoggedIn() ? "Chat" : "Login") : cur));
    setMenu(null);
  }, []);

  // ---- worn HUDs (hudDrag/toggleHud) -------------------------------------
  const hudDrag = useCallback(
    (id, e) => {
      const start = hudPos[id] || { x: (HUDS.find((h) => h.id === id) || {}).x || 12, y: (HUDS.find((h) => h.id === id) || {}).y || 90 };
      const sx = e.clientX,
        sy = e.clientY;
      const move = (ev) => setHudPos((s) => ({ ...s, [id]: { x: start.x + (ev.clientX - sx), y: start.y + (ev.clientY - sy) } }));
      const up = () => {
        window.removeEventListener("pointermove", move);
        window.removeEventListener("pointerup", up);
      };
      window.addEventListener("pointermove", move);
      window.addEventListener("pointerup", up);
      e.preventDefault();
    },
    [hudPos]
  );
  const toggleHud = useCallback((id) => setHudOn((s) => ({ ...s, [id]: !s[id] })), []);

  // ---- console frame geometry (cf()) -------------------------------------
  const cf = useCallback(() => {
    const d = D(),
      wide = !!d.split && !d.desk;
    const gap = 4,
      cur = wide ? 64 : 40;
    return { gap, cur, rad: cur - gap, rail: wide ? 132 : 84, bar: wide ? 104 : 70, dock: wide ? 58 : 52, foot: wide ? 36 : 30, wide };
  }, [D]);

  const cTap = useCallback((id) => {
    clearTimeout(cflRef.current);
    setScreen(id);
    setCFlash(id);
    cflRef.current = setTimeout(() => setCFlash(""), 200);
  }, []);

  const cHold = useCallback((k) => {
    if (k === "run") setCRun((r) => !r);
    else if (k === "cam") app.world.toggleCameraMode();
    else setCHeld(k);
  }, []);

  // Dock buttons either open the matching screen or say plainly that the viewer
  // cannot do that yet. Nothing here pretends to have performed an action or
  // reports permissions (no-fly, not-your-land) the viewer has no way to know.
  const cPress = useCallback((k) => {
    const b = CBTN[k];
    if (k === "ao") {
      if (navMode() === "floaters") flToggle("AO");
      else setScreen("AO");
      return;
    }
    if (b.nav) {
      if (navMode() === "floaters") flFocus(b.nav);
      else setScreen(b.nav);
      setCReason("");
      return;
    }
    setCReason(b.label + " — NOT AVAILABLE IN THIS VIEWER YET");
  }, [navMode, flToggle, flFocus, setScreen]);

  // ---- console dock hold-to-edit / radar long-press ----------------------
  const holdStart = useCallback(() => {
    clpRef.current = setTimeout(() => setCEdit(true), 480);
  }, []);
  const holdEnd = useCallback(() => clearTimeout(clpRef.current), []);
  const endEdit = useCallback(() => setCEdit(false), []);
  const togglePad = useCallback(() => setCPad((p) => !p), []);
  const toggleRun = useCallback(() => setCRun((r) => !r), []);
  const flyUpDown = useCallback(() => setCHeld("up"), []);
  const flyDnDown = useCallback(() => setCHeld("dn"), []);
  const flyRelease = useCallback(() => setCHeld(""), []);
  const addSlot = useCallback(() => {
    setCDock((dock) => {
      const miss = Object.keys(CBTN).filter((k) => dock.indexOf(k) < 0);
      if (!miss.length) {
        setCReason("PALETTE EMPTY");
        return dock;
      }
      return dock.concat(miss[0]);
    });
  }, []);
  const removeDockSlot = useCallback((k) => setCDock((d) => d.filter((x) => x !== k)), []);

  const radarTap = useCallback((id) => {
    if (lpFiredRef.current) {
      lpFiredRef.current = false;
      return;
    }
    setROpen((cur) => (cur === id ? null : id));
    setRMenu(null);
  }, []);
  const radarHold = useCallback((id) => {
    rlpRef.current = setTimeout(() => {
      lpFiredRef.current = true;
      setRMenu(id);
      setROpen(null);
    }, 460);
  }, []);
  const radarRelease = useCallback(() => clearTimeout(rlpRef.current), []);
  const radarBlipPick = useCallback((name) => {
    setROpen((cur) => (cur === name ? null : name));
    setRMenu(null);
  }, []);

  const toggleInvFolder = useCallback((name) => {
    setInvOpen((st) => ({ ...st, [name]: !(st[name] !== false) }));
  }, []);

  const setViewMode = useCallback((mode) => {
    setViewModeState(mode);
    try {
      localStorage.setItem("linkpoint_view_mode", mode);
    } catch {}
    const newDev = resolveDevice(mode);
    setDevice(newDev);
    notify(mode === "desktop" ? "Switched to Desktop Mode (Firestorm Multi-Window)" : mode === "mobile" ? "Switched to Mobile Mode (Lumiya Touch)" : "Switched to Auto-Responsive Mode");
  }, [resolveDevice, notify]);

  const toggleViewMode = useCallback(() => {
    const next = device === "desk" ? "mobile" : "desktop";
    setViewMode(next);
  }, [device, setViewMode]);

  const screenPick = useCallback(
    (id) => {
      if (app.auth.isLoggedIn() && navMode() === "floaters" && FLOATERS.some((f) => f.id === id)) {
        flFocus(id);
        setDialog(null);
        return;
      }
      setScreen(id);
      setDialog(null);
    },
    [navMode, flFocus]
  );

  return {
    state: {
      layout, palette, customTheme, device, viewMode, screen, dialog, dense, tabs, chip, tileOk, invOpen, dismissed, pinned,
      toggles, cond, hudOn, hudPos, hudPicker, target, targetPicker, navPeek,
      cPad, cHeld, cRun, cEdit, cFlash, cReason, cTog,
      rMode, rOpen, rMenu, cDock, flOpen, flMin, flRect, flZ, menu, tick,
      loginMode, loginGrid, loginBusy, loginError, customGrids, addGrid, addGridName, addGridHost,
      searchFrom, searchTab, searchQuery, searchState, reconnecting, toast,
      prefs, cacheCleared, camPreset,
    },
    actions: {
      setLayout, setPalette: selectPalette, setThemeColor, renameTheme, saveTheme, resetTheme, importTheme, downloadTheme, shareTheme, setDevice, setViewMode, toggleViewMode, setScreen: screenPick, setDialog, setDense,
      allGrids, openAddGrid, cancelAddGrid, saveCustomGrid, setAddGridName, setAddGridHost,
      setTab, setChip, setTileOk, toggleInvFolder, dismiss, toggleSetting, pin,
      cycleLayout, cyclePalette, setCond, setMenu,
      flR, flDrag, flFocus, flToggle, flClose,
      hudDrag, toggleHud, setHudPicker, setTarget, setTargetPicker, setNavPeek,
      cf, cTap, cHold, cPress,
      holdStart, holdEnd, endEdit, togglePad, toggleRun, flyUpDown, flyDnDown, flyRelease, addSlot, removeDockSlot,
      radarTap, radarHold, radarRelease, radarBlipPick,
      setRMode,
      setLoginMode, setLoginGrid, connectLogin, openSearch, setSearchTab, setSearchQuery, searchAdd, startIm, reconnect, notify,
      setPref, clearCache, clearAllCache, setCamPreset,
    },
    T, D, navMode,
  };
}
