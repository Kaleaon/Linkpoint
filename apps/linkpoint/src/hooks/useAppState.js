import { useCallback, useEffect, useRef, useState } from "react";
import { LAYOUTS } from "../theme/layouts.js";
import { PALETTES } from "../theme/palettes.js";
import { DEVICES, FLOATERS, HUD_DEFAULT, HUDS, CBTN, GRIDS } from "../theme/constants.js";
import { useViewerClient } from "../viewer/ViewerClientContext";

// Ported from the mockup's `state = {...}` initializer and its instance
// methods (flR/flDrag/flFocus/flToggle/flClose, hudDrag/toggleHud, T/D/navMode,
// set/setTab/dismiss/toggleSetting/pin/cycle, cf/cTap/cHold/cPress, and the
// tick interval in componentDidMount). This hook is the state + actions layer;
// theme/viewModel.js is the "renderVals()" computation layer that consumes it.
export function useAppState() {
  const viewerClient = useViewerClient();
  const [layout, setLayout] = useState("terminal");
  const [palette, setPalette] = useState("ink");
  const [device, setDevice] = useState("ios");
  const [screen, setScreen] = useState("Login");
  const [dialog, setDialog] = useState(null);
  const [dense, setDense] = useState(false);
  const [tabs, setTabs] = useState({ Chat: "LOCAL", Friends: "ALL", Diagnostics: "AGNI" });
  const [chip, setChip] = useState("");
  const [tileOk, setTileOk] = useState(true);
  const [invOpen, setInvOpen] = useState({ Objects: true });
  const [dismissed, setDismissed] = useState({});
  const [pinned, setPinned] = useState({});
  const [toggles, setToggles] = useState({ largeType: false, push: true, voice: true, chatCmds: true, autoresponse: true });
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
  const [cCam, setCCam] = useState("ORBIT");
  const [cHdg, setCHdg] = useState(214);
  const [cPitch, setCPitch] = useState(0);
  const [cDrag, setCDrag] = useState(false);
  const [cEdit, setCEdit] = useState(false);
  const [cFlash, setCFlash] = useState("");
  const [cReason, setCReason] = useState("");
  const [cTog, setCTog] = useState({});
  const [rMode, setRMode] = useState("AV");
  const [rOpen, setROpen] = useState(null);
  const [rMenu, setRMenu] = useState(null);
  const [cDock, setCDock] = useState(["fly", "sit", "snap", "mini", "inv", "home", "ao", "sun"]);
  const [flOpen, setFlOpen] = useState({ Chat: true, Radar: true, Friends: true, Inventory: true, Map: true });
  const [flMin, setFlMin] = useState({});
  const [flRect, setFlRect] = useState({});
  const [flZ, setFlZ] = useState(["Map", "Inventory", "Friends", "Radar", "Chat"]);
  const [menu, setMenu] = useState(null);
  const [tick, setTick] = useState(0);
  const [loginMode, setLoginModeState] = useState("grid");
  const [loginGrid, setLoginGrid] = useState("agni");
  const [loginBusy, setLoginBusy] = useState(false);
  const [loginError, setLoginError] = useState(null);
  const [loginName, setLoginName] = useState("");
  const [loginPassword, setLoginPassword] = useState("");
  const [rememberLogin, setRememberLogin] = useState(true);
  const [customGrids, setCustomGrids] = useState(() => {
    try { return JSON.parse(localStorage.getItem("linkpoint_custom_grids") || "[]"); } catch { return []; }
  });
  const [addGrid, setAddGrid] = useState(false);
  const [addGridName, setAddGridName] = useState("");
  const [addGridHost, setAddGridHost] = useState("");
  const [searchFrom, setSearchFrom] = useState("Friends");
  const [searchQuery, setSearchQuery] = useState("");
  const [searchState, setSearchState] = useState({});
  const [reconnecting, setReconnecting] = useState(false);
  const [toast, setToast] = useState("");

  // ---- tick clock (componentDidMount's setInterval) ---------------------
  useEffect(() => {
    const iv = setInterval(() => setTick((t) => t + 1), 1000);
    return () => clearInterval(iv);
  }, []);

  // ---- timers / drag refs (were plain `this.x` fields on the class) -----
  const cflRef = useRef(null); // console-nav tap flash timeout
  const clpRef = useRef(null); // console dock long-press-to-edit timeout
  const rlpRef = useRef(null); // radar row long-press timeout
  const lpFiredRef = useRef(false);
  const lxRef = useRef(0);
  const lyRef = useRef(0);
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
  const pin = useCallback((key) => setPinned((s) => ({ ...s, [key]: !s[key] })), []);
  const cycleLayout = useCallback(() => {
    const ks = Object.keys(LAYOUTS);
    setLayout((cur) => ks[(ks.indexOf(cur) + 1) % ks.length]);
  }, []);
  const cyclePalette = useCallback(() => {
    const ks = Object.keys(PALETTES);
    setPalette((cur) => ks[(ks.indexOf(cur) + 1) % ks.length]);
  }, []);

  // ---- login (setLoginMode/setLoginGrid/connectLogin) --------------------
  // All known grids: the built-in Second Life / OpenSim presets plus
  // whatever the resident has added themselves this session.
  const allGrids = useCallback(() => GRIDS.concat(customGrids), [customGrids]);
  const setLoginMode = useCallback((m) => {
    setLoginModeState(m);
    setLoginError(null);
  }, []);
  const connectLogin = useCallback(async () => {
    if (loginBusy) return;
    if (loginMode === "offline") {
      setLoginError(null);
      setScreen("Chat");
      notify("Offline session ready");
      return;
    }
    if (!loginName.trim() || !loginPassword) {
      setLoginError("Enter your avatar name and password.");
      return;
    }
    setLoginBusy(true);
    setLoginError(null);
    const grid = allGrids().find((item) => item.key === loginGrid) || GRIDS[0];
    const endpoint = /^https?:\/\//i.test(grid.host) ? grid.host : `https://${grid.host}`;
    try {
      await viewerClient.execute({ type: "session.login", payload: { grid: grid.key.startsWith("custom-") ? "opensim" : "second-life", loginUri: endpoint, username: loginName.trim(), password: loginPassword, start: "last" } });
      setLoginPassword("");
      setScreen("Chat");
      notify(`Connected as ${loginName.trim()}`);
    } catch (error) {
      setLoginError(error instanceof Error ? error.message : `Unable to reach ${grid.host}.`);
    } finally {
      setLoginBusy(false);
    }
  }, [loginBusy, loginMode, loginName, loginPassword, rememberLogin, loginGrid, allGrids, notify, viewerClient]);

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
    try {
      const endpoint = new URL(host);
      if (endpoint.protocol !== "https:" || endpoint.username || endpoint.password) throw new Error();
    } catch {
      notify("Login URI must be a valid HTTPS URL without embedded credentials");
      return;
    }
    const key =
      "custom-" +
      name.toLowerCase().replace(/[^a-z0-9]+/g, "-").replace(/(^-|-$)/g, "").slice(0, 24) +
      "-" +
      (customGrids.length + 1);
    setCustomGrids((grids) => {
      const next = grids.concat([{ key, label: name, host }]);
      localStorage.setItem("linkpoint_custom_grids", JSON.stringify(next));
      return next;
    });
    setLoginGrid(key);
    setAddGrid(false);
    notify("Added grid — " + name);
  }, [addGridName, addGridHost, customGrids]);

  // ---- resident search (openSearch/searchAdd) -----------------------------
  const openSearch = useCallback((from) => {
    setSearchFrom(from);
    setSearchQuery("");
    setScreen("Search");
  }, []);
  const searchAdd = useCallback((name) => {
    setSearchState((s) => ({ ...s, [name]: "sending" }));
    clearTimeout(searchTimerRef.current);
    searchTimerRef.current = setTimeout(() => setSearchState((s) => ({ ...s, [name]: "sent" })), 700);
  }, []);

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

  const flFocus = useCallback((id) => {
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
    else if (k === "cam") setCCam((c) => (c === "ORBIT" ? "MOUSELOOK" : "ORBIT"));
    else setCHeld(k);
  }, []);

  const cPress = useCallback((k) => {
    const b = CBTN[k];
    if (b.off) {
      setCReason("DENIED — " + b.off);
      return;
    }
    if (b.tog) {
      setCTog((s) => ({ ...s, [k]: !s[k] }));
      setCReason("");
    } else setCReason(b.label + " — ACKNOWLEDGED");
  }, []);

  // ---- console scene drag (sceneDown/sceneMove/sceneUp) ------------------
  const sceneDown = useCallback((e) => {
    lxRef.current = e.clientX;
    lyRef.current = e.clientY;
    setCDrag(true);
  }, []);
  const sceneMove = useCallback(
    (e) => {
      if (!cDrag) return;
      const dx = e.clientX - lxRef.current,
        dy = e.clientY - lyRef.current;
      lxRef.current = e.clientX;
      lyRef.current = e.clientY;
      setCHdg((h) => h + dx * 0.35);
      setCPitch((p) => Math.max(-70, Math.min(70, p + dy * 0.5)));
    },
    [cDrag]
  );
  const sceneUp = useCallback(() => setCDrag((d) => (d ? false : d)), []);

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

  const screenPick = useCallback(
    (id) => {
      if (navMode() === "floaters" && FLOATERS.some((f) => f.id === id)) {
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
      layout, palette, device, screen, dialog, dense, tabs, chip, tileOk, invOpen, dismissed, pinned,
      toggles, cond, hudOn, hudPos, hudPicker, target, targetPicker, navPeek,
      cPad, cHeld, cRun, cCam, cHdg, cPitch, cDrag, cEdit, cFlash, cReason, cTog,
      rMode, rOpen, rMenu, cDock, flOpen, flMin, flRect, flZ, menu, tick,
      loginMode, loginGrid, loginBusy, loginError, loginName, loginPassword, rememberLogin, customGrids, addGrid, addGridName, addGridHost,
      searchFrom, searchQuery, searchState, reconnecting, toast,
    },
    actions: {
      setLayout, setPalette, setDevice, setScreen: screenPick, setDialog, setDense,
      allGrids, openAddGrid, cancelAddGrid, saveCustomGrid, setAddGridName, setAddGridHost,
      setTab, setChip, setTileOk, toggleInvFolder, dismiss, toggleSetting, pin,
      cycleLayout, cyclePalette, setCond, setMenu,
      flR, flDrag, flFocus, flToggle, flClose,
      hudDrag, toggleHud, setHudPicker, setTarget, setTargetPicker, setNavPeek,
      cf, cTap, cHold, cPress, sceneDown, sceneMove, sceneUp,
      holdStart, holdEnd, endEdit, togglePad, toggleRun, flyUpDown, flyDnDown, flyRelease, addSlot, removeDockSlot,
      radarTap, radarHold, radarRelease, radarBlipPick,
      setRMode,
      setLoginMode, setLoginGrid, setLoginName, setLoginPassword, setRememberLogin, connectLogin, openSearch, setSearchQuery, searchAdd, reconnect, notify,
    },
    T, D, navMode,
  };
}
