import React from "react";
import { useTheme } from "./LayoutContext.js";

export interface NavItem {
  id: string;
  label: string;
  icon?: string;
  badge?: string | number;
}

export interface BottomTabsProps {
  items?: NavItem[];
  activeId?: string;
  onSelect?: (id: string) => void;
  children?: React.ReactNode;
}

export const BottomTabs: React.FC<BottomTabsProps> = ({ items = [], activeId, onSelect, children }) => {
  const theme = useTheme();
  const V = theme.v;

  if (theme.nav !== "tabs" && !items.length) {
    return null;
  }

  return (
    <nav
      aria-label="Bottom Navigation"
      style={{
        flex: "none",
        display: "flex",
        background: V.surf,
        borderTop: "1px solid " + V.outv,
        padding: "6px 0 10px"
      }}
    >
      <div role="tablist" style={{ display: "flex", width: "100%" }}>
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
              role="tab"
              tabIndex={0}
              aria-selected={active}
              aria-label={n.label}
              onClick={() => onSelect?.(n.id)}
              onKeyDown={handleKeyDown}
              style={{
                flex: 1,
                display: "flex",
                flexDirection: "column",
                alignItems: "center",
                gap: "4px",
                padding: "6px 0",
                cursor: "pointer",
                color: active ? V.pri : V.ink2,
                position: "relative"
              }}
            >
              <span style={{ font: "600 11px/1 " + theme.font, letterSpacing: ".14em" }}>{n.label}</span>
              {n.badge ? (
                <span
                  style={{
                    position: "absolute",
                    top: "2px",
                    right: "24%",
                    minWidth: "16px",
                    height: "16px",
                    padding: "0 4px",
                    borderRadius: "8px",
                    background: V.bdg,
                    color: V.onbdg,
                    font: "700 9px/16px " + theme.font,
                    textAlign: "center"
                  }}
                >
                  {n.badge}
                </span>
              ) : null}
            </div>
          );
        })}
        {children}
      </div>
    </nav>
  );
};
