import { useApp } from "../context/AppContext.jsx";
import { useTheme } from "../context/ThemeContext.jsx";
import { LAYOUTS } from "../theme/layouts.js";
import { PALETTES } from "../theme/palettes.js";
import { HEAD } from "../data/content.js";
import { SCREENS } from "../theme/constants.js";
import { app } from "../linkpoint/app";
import Icon from "./Icon.jsx";
import ViewModeSwitcher from "./ViewModeSwitcher.jsx";

// Ported from the five header <sc-if> blocks (hasHeader/isSweepHead/
// isPivotHead/isRuleHead/isPressHead) plus the shared title/subtitle lookup.
export default function Header() {
  const { state } = useApp();
  const { V, t, headLook, condPack, scr, isConsole } = useTheme();

  const headMap = HEAD(LAYOUTS[state.layout].name, PALETTES[state.palette].name, { cleared: state.cacheCleared, limit: state.prefs.cacheLimit, loc: state.prefs.cacheLoc });
  const [rawTitle, rawSubtitle] = headMap[scr] || ["", ""];
  const title = rawTitle;
  const subtitle = condPack ? condPack.sub || null : runtimeSubtitle(scr, rawSubtitle);

  if (headLook === "none" || headLook === "sweep") {
    // "sweep" head only renders outside the console frame (isSweepHead requires
    // !isConsole); when isConsole is true the console chrome draws its own title.
    if (headLook === "sweep" && !isConsole) return <SweepHead title={title} subtitle={subtitle} scr={scr} />;
    return null;
  }
  if (headLook === "pivot") return <PivotHead title={title} subtitle={subtitle} scr={scr} />;
  if (headLook === "rule") return <RuleHead title={title} subtitle={subtitle} scr={scr} />;
  if (headLook === "editorial") return <EditorialHead title={title} subtitle={subtitle} scr={scr} />;
  return <StackHead title={title} subtitle={subtitle} scr={scr} />;
}

function runtimeSubtitle(screen, fallback) {
  const connected = app.auth.isLoggedIn();
  const region = app.world.region;
  const inventoryCount = app.inventory.items.size;
  const folderCount = app.inventory.folders.size;
  const runtime = {
    Chat: connected ? `> ${app.auth.getUserDisplayName()} · ${region?.name || "waiting for region"}` : "> disconnected",
    Friends: `> ${app.friends.getFriends().filter((friend) => friend.onlineStatus === "online").length} online / ${app.friends.getFriends().length} loaded from grid`,
    Contacts: `> ${app.contacts.size} saved contact${app.contacts.size === 1 ? "" : "s"} on this device`,
    Radar: `> ${app.world.nearbyUsers.length} avatars · ${app.world.objects.length} simulator objects`,
    Map: region ? `> ${region.name || "current region"}${Number.isFinite(region.x) ? ` <${region.x}, ${region.y}>` : ""}` : "> waiting for region handshake",
    Inventory: `> ${inventoryCount} items · ${folderCount} folders${connected ? ` · ${app.auth.getUserDisplayName()}` : ""}`,
    Profile: connected ? `> ${app.auth.getUserDisplayName()} · grid resident` : "> disconnected",
    Groups: `> ${app.groups.getGroups().length} groups loaded from grid`,
    Notices: `> ${app.notifications.items.length} notices received this session`,
    Calendar: `> ${app.notices.list().length} group notice${app.notices.list().length === 1 ? "" : "s"} saved`,
    Parcel: region?.parcel ? `> ${region.parcel.Name || region.parcel.name || "current parcel"}` : "> waiting for parcel properties",
    Transactions: "> transaction records returned by the grid",
    Diagnostics: `> ${app.protocol.state.toLowerCase()} · ${Object.keys(app.protocol.capabilities || {}).length} capabilities`,
  };
  return runtime[screen] ?? fallback;
}

function StackHead({ title, subtitle, scr }) {
  const { V, t } = useTheme();
  const { state, actions } = useApp();
  const showLink = scr === "Chat";
  const isSettingsOrSub = ["Settings", "Cache", "Diagnostics", "AO"].includes(scr);
  const headerIcons =
    scr === "Friends"
      ? [
          { icon: "contact", label: "CONTACTS", pick: () => actions.setScreen("Contacts") },
          { icon: "user-plus", label: "ADD FRIEND", pick: () => actions.openSearch("Friends", "SEARCH") },
          { icon: "search", label: "SEARCH", pick: () => actions.openSearch("Friends", "SEARCH") },
        ]
      : scr === "Contacts"
      ? [
          { icon: "users", label: "VIEW SL FRIENDS", pick: () => actions.setScreen("Friends") },
        ]
      : scr === "Notices"
      ? [
          { icon: "calendar", label: "CALENDAR", pick: () => actions.setScreen("Calendar") },
        ]
      : scr === "Calendar"
      ? [
          { icon: "bell", label: "VIEW NOTICES", pick: () => actions.setScreen("Notices") },
        ]
      : scr === "Diagnostics"
      ? [{ icon: "refresh-cw", label: "REFRESH STATUS", pick: () => actions.notify(`${app.protocol.connected ? "Connected" : "Disconnected"} · ${Object.keys(app.protocol.capabilities || {}).length} capabilities`) }]
      : null;
  return (
    <div style={{ flex: "none", display: "flex", alignItems: "center", gap: "10px", padding: "10px 14px 8px" }}>
      {isSettingsOrSub && (
        <button
          type="button"
          onClick={() => actions.setScreen(app.auth.isLoggedIn() ? "Chat" : "Login")}
          style={{
            display: "flex",
            alignItems: "center",
            gap: 4,
            height: "30px",
            padding: "0 9px",
            border: "1px solid " + V.outv,
            borderRadius: V.rs,
            background: V.surf,
            color: V.pri,
            font: "700 10px/1 " + t.font,
            letterSpacing: ".12em",
            cursor: "pointer",
          }}
          title={app.auth.isLoggedIn() ? "Back to Chat" : "Back to Login"}
        >
          <Icon name="arrow-left" size={13} />
          {app.auth.isLoggedIn() ? "CHAT" : "LOGIN"}
        </button>
      )}
      <div style={{ flex: 1, minWidth: 0 }}>
        <div style={{ font: "700 21px/1.05 " + t.dfont, letterSpacing: V.tls, color: V.pri }}>{title}</div>
        <div style={{ font: "400 11px/1.4 " + t.font, color: V.ink2, marginTop: "4px" }}>{subtitle}</div>
      </div>
      {/* Universal Mode Switcher */}
      <ViewModeSwitcher />
      {showLink ? (
        <div style={{ display: "flex", alignItems: "center", gap: "6px", height: "26px", padding: "0 8px", border: "1px solid " + V.ok, borderRadius: V.rs, background: V.surf }}>
          <span style={{ width: "6px", height: "6px", borderRadius: "3px", background: V.ok }} />
          <span style={{ font: "600 9.5px/1 " + t.font, letterSpacing: ".2em", color: V.ok }}>LINK</span>
        </div>
      ) : null}
      {headerIcons
        ? headerIcons.map((hi) => (
            <div
              key={hi.icon}
              onClick={hi.pick}
              onKeyDown={(e) => {
                if (e.key === "Enter" || e.key === " ") {
                  e.preventDefault();
                  hi.pick();
                }
              }}
              role="button"
              aria-label={hi.label}
              tabIndex={0}
              style={{ width: "36px", height: "36px", border: "1px solid " + V.outv, borderRadius: V.rs, background: V.surf, display: "flex", alignItems: "center", justifyContent: "center", color: V.pri, cursor: "pointer" }}
            >
              <Icon name={hi.icon} size={16} />
            </div>
          ))
        : null}
    </div>
  );
}

function SweepHead({ title, subtitle, scr }) {
  const { V, t } = useTheme();
  const { actions } = useApp();
  const isSettingsOrSub = ["Settings", "Cache", "Diagnostics", "AO"].includes(scr);
  return (
    <>
      <div style={{ flex: "none", display: "flex", alignItems: "flex-end", gap: "4px", padding: "10px 12px 6px 4px" }}>
        {isSettingsOrSub && (
          <button
            type="button"
            onClick={() => actions.setScreen(app.auth.isLoggedIn() ? "Chat" : "Login")}
            style={{
              display: "flex",
              alignItems: "center",
              gap: 4,
              height: "26px",
              padding: "0 8px",
              border: "1px solid " + V.outv,
              borderRadius: "999px",
              background: V.surf,
              color: V.pri,
              font: "700 9.5px/1 " + t.dfont,
              letterSpacing: ".1em",
              cursor: "pointer",
              marginRight: "6px",
            }}
          >
            <Icon name="arrow-left" size={12} />
            {app.auth.isLoggedIn() ? "CHAT" : "LOGIN"}
          </button>
        )}
        <span style={{ width: "26px", height: "14px", background: V.sec2, borderRadius: "7px 0 0 7px", flex: "none" }} />
        <span style={{ flex: 1, height: "8px", background: V.surf2 }} />
        <span style={{ font: "600 20px/1 " + t.dfont, letterSpacing: ".12em", color: V.pri, flex: "none" }}>{title}</span>
        <span style={{ width: "38px", height: "14px", background: V.pri, borderRadius: "0 7px 7px 0", flex: "none", marginRight: 8 }} />
        <ViewModeSwitcher compact={true} />
      </div>
      <div style={{ flex: "none", padding: "0 12px 8px", font: "400 11px/1.4 " + t.font, letterSpacing: ".06em", color: V.ink2 }}>{subtitle}</div>
    </>
  );
}

function PivotHead({ title, subtitle, scr }) {
  const { t, V } = useTheme();
  const { actions } = useApp();
  const nextScr = SCREENS[(SCREENS.indexOf(scr) + 1) % SCREENS.length];
  const isSettingsOrSub = ["Settings", "Cache", "Diagnostics", "AO"].includes(scr);
  return (
    <>
      <div style={{ flex: "none", padding: "14px 16px 2px 16px", display: "flex", alignItems: "center", justifyContent: "space-between", overflow: "hidden" }}>
        <div style={{ display: "flex", alignItems: "baseline", gap: "18px" }}>
          {isSettingsOrSub && (
            <button
              type="button"
              onClick={() => actions.setScreen(app.auth.isLoggedIn() ? "Chat" : "Login")}
              style={{
                display: "flex",
                alignItems: "center",
                gap: 4,
                height: "28px",
                padding: "0 8px",
                border: "1px solid " + V.outv,
                borderRadius: V.rs,
                background: V.surf,
                color: V.pri,
                font: "700 10px/1 " + t.font,
                letterSpacing: ".1em",
                cursor: "pointer",
              }}
            >
              <Icon name="arrow-left" size={12} />
              {app.auth.isLoggedIn() ? "CHAT" : "LOGIN"}
            </button>
          )}
          <span style={{ flex: "none", font: "300 36px/1 " + t.dfont, color: V.ink }}>{String(title || "").toLowerCase()}</span>
          <span style={{ flex: "none", font: "300 36px/1 " + t.dfont, color: V.ink2, opacity: 0.4 }}>{nextScr.toLowerCase()}</span>
        </div>
        <ViewModeSwitcher compact={true} />
      </div>
      <div style={{ flex: "none", padding: "2px 16px 10px", font: "300 12px/1.4 " + t.font, color: V.ink2 }}>{subtitle}</div>
    </>
  );
}

function RuleHead({ title, subtitle, scr }) {
  const { V, t } = useTheme();
  const { actions } = useApp();
  const isSettingsOrSub = ["Settings", "Cache", "Diagnostics", "AO"].includes(scr);
  return (
    <div style={{ flex: "none", padding: "12px 16px 4px" }}>
      <div style={{ height: "1px", background: V.pri }} />
      <div style={{ height: "3px", borderBottom: "1px solid " + V.pri }} />
      <div style={{ display: "flex", alignItems: "center", justifyContent: "space-between", gap: 12, padding: "8px 0" }}>
        <div style={{ display: "flex", alignItems: "center", gap: 8 }}>
          {isSettingsOrSub && (
            <button
              type="button"
              onClick={() => actions.setScreen(app.auth.isLoggedIn() ? "Chat" : "Login")}
              style={{
                display: "flex",
                alignItems: "center",
                gap: 4,
                height: "24px",
                padding: "0 7px",
                border: "1px solid " + V.outv,
                borderRadius: V.rs,
                background: V.surf,
                color: V.pri,
                font: "700 9px/1 " + t.font,
                letterSpacing: ".1em",
                cursor: "pointer",
              }}
            >
              <Icon name="arrow-left" size={11} />
              {app.auth.isLoggedIn() ? "CHAT" : "LOGIN"}
            </button>
          )}
          <div style={{ font: "600 15px/1.1 " + t.dfont, letterSpacing: V.tls, color: V.pri, textIndent: V.tls }}>{title}</div>
        </div>
        <ViewModeSwitcher compact={true} />
      </div>
      <div style={{ textAlign: "center", font: "400 10px/1.4 " + t.font, letterSpacing: ".16em", color: V.ink2 }}>{subtitle}</div>
      <div style={{ height: "1px", background: V.outv, marginTop: "8px" }} />
    </div>
  );
}

function EditorialHead({ title, subtitle, scr }) {
  const { V, t } = useTheme();
  const { actions } = useApp();
  const isSettingsOrSub = ["Settings", "Cache", "Diagnostics", "AO"].includes(scr);
  return (
    <div style={{ flex: "none", padding: "14px 18px 8px", borderBottom: "2px solid " + V.ink }}>
      <div style={{ display: "flex", alignItems: "center", justifyContent: "space-between", gap: 12 }}>
        <div style={{ display: "flex", alignItems: "center", gap: 10 }}>
          {isSettingsOrSub && (
            <button
              type="button"
              onClick={() => actions.setScreen(app.auth.isLoggedIn() ? "Chat" : "Login")}
              style={{
                display: "flex",
                alignItems: "center",
                gap: 4,
                height: "28px",
                padding: "0 8px",
                border: "1px solid " + V.outv,
                borderRadius: V.rs,
                background: V.surf,
                color: V.pri,
                font: "700 9.5px/1 " + t.font,
                letterSpacing: ".1em",
                cursor: "pointer",
              }}
            >
              <Icon name="arrow-left" size={12} />
              {app.auth.isLoggedIn() ? "CHAT" : "LOGIN"}
            </button>
          )}
          <div style={{ font: "600 24px/1.12 " + t.font, letterSpacing: "-.01em", color: V.ink, textTransform: "capitalize" }}>{title}</div>
        </div>
        <ViewModeSwitcher compact={true} />
      </div>
      <div style={{ font: "400 11.5px/1.5 " + t.font, color: V.ink2, marginTop: "4px", maxWidth: "46ch" }}>{subtitle}</div>
    </div>
  );
}
