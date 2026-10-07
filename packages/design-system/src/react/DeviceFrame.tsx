import React from "react";
import { useTheme } from "./LayoutContext.js";
import { BottomTabs } from "./BottomTabs.js";
import { TileNav } from "./TileNav.js";
import { RailNav } from "./RailNav.js";
import { ConsoleFrame } from "./ConsoleFrame.js";

export interface DeviceFrameProps {
  children?: React.ReactNode;
}

export const DeviceFrame: React.FC<DeviceFrameProps> = ({ children }) => {
  const theme = useTheme();
  const V = theme.v;
  const nav = theme.nav;
  const breakpoint = theme.breakpoint || "desktop";

  const isConsole = nav === "sweep";
  const isRail = nav === "rail";

  const frameWidth = breakpoint === "mobile" ? "375px" : breakpoint === "tablet" ? "768px" : "100%";

  const frameStyle: React.CSSProperties = {
    position: "relative",
    overflow: "hidden",
    background: V.bg,
    color: V.ink,
    fontFamily: theme.font,
    display: "flex",
    flexDirection: isRail ? "row" : "column",
    width: frameWidth,
    maxWidth: "100%",
    height: "100%",
    margin: breakpoint === "desktop" ? "0" : "0 auto",
    boxShadow: breakpoint === "desktop" ? "none" : "0 8px 30px rgba(0,0,0,0.35)",
    transition: "width 0.25s ease-in-out, margin 0.25s ease-in-out"
  };

  if (isConsole) {
    return <ConsoleFrame>{children}</ConsoleFrame>;
  }

  return (
    <div className="device-frame" style={frameStyle}>
      {isRail && <RailNav />}
      <div style={{ flex: 1, minHeight: 0, minWidth: 0, position: "relative", display: "flex", flexDirection: "column", overflow: "hidden" }}>
        {children}
      </div>
      <BottomTabs />
      <TileNav />
    </div>
  );
};
