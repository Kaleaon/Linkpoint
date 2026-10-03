import { useApp } from "../context/AppContext.jsx";
import { useTheme } from "../context/ThemeContext.jsx";
import RailNav from "./RailNav.jsx";
import TileNav from "./TileNav.jsx";
import BottomTabs from "./BottomTabs.jsx";

/**
 * Unified Navigation Shell Container
 * Evaluates active navigation mode ('rail', 'tiles', 'tabs') in a single place
 * and wraps screen content with the appropriate navigation component.
 */
export default function NavShell({ children }) {
  const { state, actions } = useApp();
  const { nav, V, t } = useTheme();

  const showNav = state.screen !== "Login";
  const isDisconnected = showNav && (state.connectionState === "disconnected" || state.connectionState === "reconnecting");

  const disconnectBanner = isDisconnected ? (
    <div
      role="alert"
      className="connection-banner"
      style={{
        display: "flex",
        alignItems: "center",
        justifyContent: "space-between",
        gap: "12px",
        padding: "8px 16px",
        backgroundColor: state.connectionState === "reconnecting" ? "#b45309" : "#b91c1c",
        color: "#ffffff",
        fontSize: "12px",
        fontWeight: 600,
        fontFamily: t?.font || "sans-serif",
        borderBottom: "1px solid rgba(255,255,255,0.2)",
        zIndex: 999,
      }}
    >
      <div style={{ display: "flex", alignItems: "center", gap: "8px" }}>
        <span style={{ fontSize: "14px" }}>⚠️</span>
        <span>
          {state.connectionState === "reconnecting"
            ? "Attempting to reconnect to grid..."
            : "Connection lost — disconnected from grid."}
        </span>
      </div>
      <button
        type="button"
        onClick={() => actions?.reconnect()}
        disabled={state.reconnecting || state.connectionState === "reconnecting"}
        style={{
          padding: "4px 12px",
          borderRadius: V?.rs || "4px",
          border: "1px solid rgba(255,255,255,0.4)",
          background: "rgba(255,255,255,0.15)",
          color: "#ffffff",
          fontSize: "11px",
          fontWeight: 700,
          cursor: state.reconnecting ? "not-allowed" : "pointer",
        }}
      >
        {state.reconnecting || state.connectionState === "reconnecting" ? "Retrying..." : "Reconnect"}
      </button>
    </div>
  ) : null;

  if (showNav && nav === "rail") {
    return (
      <div className="nav-shell nav-shell-rail" style={{ flex: 1, display: "flex", flexDirection: "row", minHeight: 0, minWidth: 0, width: "100%", height: "100%" }}>
        <RailNav />
        <div style={{ flex: 1, display: "flex", flexDirection: "column", minHeight: 0, minWidth: 0, overflow: "hidden" }}>
          {disconnectBanner}
          {children}
        </div>
      </div>
    );
  }

  return (
    <div className={`nav-shell nav-shell-${nav || "bottom"}`} style={{ flex: 1, display: "flex", flexDirection: "column", minHeight: 0, minWidth: 0, width: "100%", height: "100%" }}>
      {disconnectBanner}
      <div style={{ flex: 1, display: "flex", flexDirection: "column", minHeight: 0, minWidth: 0, overflow: "hidden" }}>
        {children}
      </div>
      {showNav && nav === "tiles" && <TileNav />}
      {showNav && nav === "tabs" && <BottomTabs />}
    </div>
  );
}

