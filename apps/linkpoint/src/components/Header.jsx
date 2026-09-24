import { useApp } from "../context/AppContext.jsx";
import { useTheme } from "../context/ThemeContext.jsx";
import { useViewerState } from "../viewer/ViewerClientContext";

/** Runtime header: only reports state that exists in the connection managers. */
export default function Header() {
  const { state } = useApp();
  const { V, t, scr } = useTheme();
  const viewer = useViewerState();
  const connected = viewer.connection === "connected";
  const subtitle = connected
    ? `${viewer.session?.regionName || "Grid"} · connected`
    : state.loginMode === "offline"
      ? "Offline session"
      : "Not connected";

  return (
    <header className="screen-header" style={{ borderColor: V.outv, background: V.bg }}>
      <h1 style={{ color: V.pri, fontFamily: t.dfont }}>{scr === "3D View" ? "World" : scr}</h1>
      <p style={{ color: V.ink2 }}>{subtitle}</p>
    </header>
  );
}
