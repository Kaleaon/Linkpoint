import React from "react";

export interface ElevationBadgeProps extends React.HTMLAttributes<HTMLSpanElement> {
  zDelta?: number;
  compact?: boolean;
  style?: React.CSSProperties;
  className?: string;
}

export const ElevationBadge: React.FC<ElevationBadgeProps> = ({
  zDelta = 0,
  compact = false,
  style,
  className = "",
  ...props
}) => {
  const delta = typeof zDelta === "number" && !isNaN(zDelta) ? zDelta : 0;
  const isUp = delta > 0;
  const isDown = delta < 0;

  const label = isUp ? `+${delta}m` : `${delta}m`;

  const color = isUp ? "#38bdf8" : isDown ? "#fb923c" : "#94a3b8";
  const bg = isUp ? "rgba(56, 189, 248, 0.15)" : isDown ? "rgba(251, 146, 60, 0.15)" : "rgba(148, 163, 184, 0.15)";
  const border = isUp ? "rgba(56, 189, 248, 0.35)" : isDown ? "rgba(251, 146, 60, 0.35)" : "rgba(148, 163, 184, 0.25)";

  const badgeStyle: React.CSSProperties = compact
    ? {
        display: "inline-flex",
        alignItems: "center",
        gap: "2px",
        padding: "1px 4px",
        borderRadius: "3px",
        fontSize: "8.5px",
        fontWeight: 600,
        lineHeight: 1,
        whiteSpace: "nowrap",
        color,
        background: bg,
        border: `1px solid ${border}`,
        boxShadow: "0 1px 2px rgba(0,0,0,0.4)",
        pointerEvents: "none",
        ...style,
      }
    : {
        display: "inline-flex",
        alignItems: "center",
        gap: "4px",
        padding: "3px 7px",
        borderRadius: "4px",
        fontSize: "11px",
        fontWeight: 600,
        lineHeight: 1,
        whiteSpace: "nowrap",
        color,
        background: bg,
        border: `1px solid ${border}`,
        flexShrink: 0,
        ...style,
      };

  const svgSize = compact ? 8 : 11;

  return (
    <span
      style={badgeStyle}
      title={`Altitude relative to viewer: ${label}`}
      className={`elevation-badge ${className}`.trim()}
      {...props}
    >
      <svg
        width={svgSize}
        height={svgSize}
        viewBox="0 0 24 24"
        fill="none"
        stroke="currentColor"
        strokeWidth="3"
        strokeLinecap="round"
        strokeLinejoin="round"
        style={{ flexShrink: 0 }}
      >
        {isUp ? (
          <path d="M12 19V5M5 12l7-7 7 7" />
        ) : isDown ? (
          <path d="M12 5v14M5 12l7 7 7-7" />
        ) : (
          <path d="M5 12h14" />
        )}
      </svg>
      <span>{label}</span>
    </span>
  );
};

export default ElevationBadge;
