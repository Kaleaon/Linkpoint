import { useTheme } from "../context/ThemeContext.jsx";
import Card from "./Card.jsx";

// Ported from `cardListStyle` in renderVals() — the scrollable column that
// Friends/Groups/Notices/Teleport/Settings/Diagnostics all share.
export default function CardList({ cards }) {
  const { LK } = useTheme();
  const style = {
    flex: 1,
    minHeight: 0,
    overflowY: "auto",
    padding: LK.card === "flat" ? "0 12px 18px 0" : "2px 16px 18px",
    display: "flex",
    flexDirection: "column",
    gap: LK.gap,
    maxWidth: LK.measure || "none",
    boxSizing: "border-box",
  };
  return (
    <div style={style}>
      {cards.map((c, i) => (
        <Card key={i} c={c} />
      ))}
    </div>
  );
}
