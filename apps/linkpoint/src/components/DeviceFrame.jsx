import { useApp } from "../context/AppContext.jsx";
import { useTheme } from "../context/ThemeContext.jsx";
import { app } from "../linkpoint/app";
import MenuBar from "./MenuBar.jsx";
import Shell from "./Shell.jsx";
import ConsoleFrame from "./ConsoleFrame.jsx";
import FloatersDesktop from "./FloatersDesktop.jsx";
import SystemDialog from "./SystemDialog.jsx";
import InteractionDialog from "./InteractionDialog.jsx";
import Toast from "./Toast.jsx";
import BottomTabs from "./BottomTabs.jsx";
import TileNav from "./TileNav.jsx";

// The React deliverable is the application itself, not the GitHub Pages
// presentation canvas. Device bezels, fake OS chrome and design pickers remain
// in docs/index.html; this root deliberately fills its host viewport.
export default function DeviceFrame() {
  const { state } = useApp();
  const { V, t, isFloat, isConsole } = useTheme();
  const loggedIn = app.auth.isLoggedIn();

  const frameStyle = { position: "relative", overflow: "hidden", background: V.bg, color: V.ink, fontFamily: t.font, display: "flex", flexDirection: "column", width: "100%", height: "100%" };
  const cfWrap = { flex: 1, minHeight: 0, minWidth: 0, position: "relative", display: "flex", overflow: "hidden", background: V.bg };

  return (
    <div className="device-frame">
      <div style={frameStyle}>
        {loggedIn && state.screen !== "Login" && <MenuBar />}
        <div style={cfWrap}>
          {state.screen === "Login" || !loggedIn ? (
            <Shell />
          ) : isConsole ? (
            <ConsoleFrame />
          ) : isFloat ? (
            <FloatersDesktop />
          ) : (
            <Shell />
          )}
          <SystemDialog />
          <InteractionDialog />
          <Toast />
        </div>
        <BottomTabs />
        <TileNav />
      </div>
    </div>
  );
}
