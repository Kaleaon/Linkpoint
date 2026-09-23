import { useTheme } from "../context/ThemeContext.jsx";
import RailNav from "./RailNav.jsx";
import ScreenBody from "./ScreenBody.jsx";

// Ported from `shellStyle` in the non-console, non-desktop case: a plain flex
// row of [optional rail nav] + [screen content column (+ split pane)].
export default function Shell() {
  return (
    <div style={{ flex: 1, display: "flex", minHeight: 0, minWidth: 0 }}>
      <RailNav />
      <ScreenBody />
    </div>
  );
}
