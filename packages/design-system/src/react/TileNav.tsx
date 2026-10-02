import React from "react";
import { useTheme } from "./LayoutContext.js";
import { type NavItem } from "./BottomTabs.js";

export interface TileNavProps {
  items?: NavItem[];
  activeId?: string;
  onSelect?: (id: string) => void;
  children?: React.ReactNode;
}

export const TileNav: React.FC<TileNavProps> = ({ items = [], activeId, onSelect, children }) => {
  const theme = useTheme();
  const V = theme.v;

  if (theme.nav !== "tiles" && !items.length) {
    return null;
  }

  return (
    <div style={{ flex: "none", display: "flex", gap: "3px", background: V.bg, padding: "3px" }}>
      {items.map((n) => {
        const active = activeId === n.id;
        const bg = active ? V.pri : V.surf;
        const fg = active ? V.onpri : V.ink;
        return (
          <div
            key={n.id}
            onClick={() => onSelect?.(n.id)}
            role="button"
            tabIndex={0}
            aria-label={"Go to " + n.id}
            aria-pressed={active}
            onKeyDown={(e) => {
              if (e.key === "Enter" || e.key === " ") {
                e.preventDefault();
                onSelect?.(n.id);
              }
            }}
            style={{
              flex: 1,
              height: "64px",
              display: "flex",
              flexDirection: "column",
              justifyContent: "space-between",
              padding: "8px",
              cursor: "pointer",
              background: bg,
              color: fg,
              borderRadius: "0px",
              boxShadow: active ? "inset 0 0 0 2px " + V.onpri : "none",
              transition: "background .15s ease"
            }}
          >
            <span style={{ font: "300 11px/1 " + theme.dfont, letterSpacing: ".02em", textTransform: "lowercase" }}>{n.label}</span>
          </div>
        );
      })}
      {children}
    </div>
  );
};
