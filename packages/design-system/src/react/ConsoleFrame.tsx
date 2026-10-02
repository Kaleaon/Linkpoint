import React from "react";
import { useTheme } from "./LayoutContext.js";

export interface ConsoleFrameProps {
  title?: string;
  subTitle?: string;
  items?: { id: string; label: string; pick?: () => void }[];
  children?: React.ReactNode;
}

export const ConsoleFrame: React.FC<ConsoleFrameProps> = ({
  title = "LINKPOINT",
  subTitle,
  items = [],
  children
}) => {
  const theme = useTheme();
  const V = theme.v;

  const barHeight = 44;
  const railWidth = 140;

  return (
    <div style={{ position: "relative", width: "100%", height: "100%", overflow: "hidden", background: V.bg, color: V.ink }}>
      {/* Top LCARS Bar */}
      <div style={{ position: "absolute", top: 0, left: 0, right: 0, height: `${barHeight}px`, background: V.pri, display: "flex", alignItems: "center", paddingLeft: "16px", gap: "12px" }}>
        <span style={{ font: "700 18px/1 " + theme.dfont, color: V.onpri, letterSpacing: ".1em" }}>{title}</span>
        {subTitle && <span style={{ font: "500 12px/1 " + theme.font, color: V.onpri, opacity: 0.8 }}>{subTitle}</span>}
      </div>

      {/* Side Rail */}
      <div style={{ position: "absolute", top: `${barHeight + 4}px`, left: 0, width: `${railWidth}px`, bottom: 0, display: "flex", flexDirection: "column", gap: "4px" }}>
        {items.map((n) => (
          <div
            key={n.id}
            onClick={n.pick}
            role="button"
            tabIndex={0}
            aria-label={n.label}
            style={{
              padding: "10px 12px",
              background: V.sec2,
              color: V.onsec,
              font: "700 11px/1 " + theme.dfont,
              letterSpacing: ".1em",
              cursor: "pointer"
            }}
          >
            {n.label}
          </div>
        ))}
      </div>

      {/* Content Body area */}
      <div style={{ position: "absolute", top: `${barHeight + 4}px`, left: `${railWidth + 4}px`, right: 0, bottom: 0, background: V.surf, borderRadius: `${V.rp} 0 0 0`, overflow: "auto", padding: theme.pad }}>
        {children}
      </div>
    </div>
  );
};
