import { useTheme } from "../context/ThemeContext.jsx";

// Ported from CARDS[].toggleStyle/knobStyle in renderVals().
export default function Toggle({ on, onClick }) {
  const { V } = useTheme();
  return (
    <span
      role="switch"
      aria-checked={on}
      tabIndex={0}
      onClick={onClick}
      onKeyDown={(e) => {
        if (e.key === "Enter" || e.key === " ") {
          e.preventDefault();
          if (onClick) onClick();
        }
      }}
      style={{
        width: "42px",
        height: "24px",
        borderRadius: "12px",
        position: "relative",
        display: "inline-block",
        flex: "none",
        cursor: "pointer",
        background: on === false ? V.surf2 : V.priC,
      }}
    >
      <span
        style={{
          position: "absolute",
          top: "3px",
          width: "18px",
          height: "18px",
          borderRadius: "9px",
          left: on === false ? "3px" : "21px",
          background: on === false ? V.ink2 : V.pri,
          transition: "left .18s ease",
        }}
      />
    </span>
  );
}
