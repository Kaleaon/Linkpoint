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
  const { state } = useApp();
  const { nav } = useTheme();

  const showNav = state.screen !== "Login";

  if (showNav && nav === "rail") {
    return (
      <div className="nav-shell nav-shell-rail" style={{ flex: 1, display: "flex", flexDirection: "row", minHeight: 0, minWidth: 0, width: "100%", height: "100%" }}>
        <RailNav />
        <div style={{ flex: 1, display: "flex", flexDirection: "column", minHeight: 0, minWidth: 0, overflow: "hidden" }}>
          {children}
        </div>
      </div>
    );
  }

  return (
    <div className={`nav-shell nav-shell-${nav || "bottom"}`} style={{ flex: 1, display: "flex", flexDirection: "column", minHeight: 0, minWidth: 0, width: "100%", height: "100%" }}>
      <div style={{ flex: 1, display: "flex", flexDirection: "column", minHeight: 0, minWidth: 0, overflow: "hidden" }}>
        {children}
      </div>
      {showNav && nav === "tiles" && <TileNav />}
      {showNav && nav === "tabs" && <BottomTabs />}
    </div>
  );
}
