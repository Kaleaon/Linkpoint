import React from "react";
import { useTheme } from "./LayoutContext.js";
import { type NavItem } from "./BottomTabs.js";

export interface RailNavProps {
  items?: NavItem[];
  activeId?: string;
  onSelect?: (id: string) => void;
  title?: string;
  children?: React.ReactNode;
}

export const RailNav: React.FC<RailNavProps> = ({ items = [], activeId, onSelect, title = "LINKPOINT", children }) => {
  const theme = useTheme();
  const V = theme.v;

  if (theme.nav !== "rail" && !items.length) {
    return null;
  }

  return (
    <nav
      aria-label="Side Rail Navigation"
      style={{
        flex: "none",
        width: "104px",
        background: V.surf,
        borderRight: "1px solid " + V.outv,
        display: "flex",
        flexDirection: "column",
        gap: "5px",
        padding: "12px 8px"
      }}
    >
      <div style={{ font: "700 13px/1.15 " + theme.dfont, letterSpacing: ".2em", color: V.pri, padding: "2px 6px 14px" }}>
        {title}
      </div>
      {items.map((n) => {
        const active = activeId === n.id;
        const handleKeyDown = (e: React.KeyboardEvent) => {
          if (e.key === "Enter" || e.key === " ") {
            e.preventDefault();
            onSelect?.(n.id);
          }
        };
        return (
          <div
            key={n.id}
            role="button"
            tabIndex={0}
            aria-current={active ? "page" : undefined}
            aria-label={n.label}
            onClick={() => onSelect?.(n.id)}
            onKeyDown={handleKeyDown}
            style={{
              display: "flex",
              flexDirection: "column",
              alignItems: "center",
              gap: "5px",
              padding: "10px 4px",
              cursor: "pointer",
              borderRadius: V.navr,
              color: active ? V.onpriC : V.ink2,
              background: active ? V.priC : undefined
            }}
          >
            <span style={{ font: "600 8.5px/1 " + theme.font, letterSpacing: ".1em" }}>{n.label}</span>
          </div>
        );
      })}
      {children}
    </nav>
  );
};
