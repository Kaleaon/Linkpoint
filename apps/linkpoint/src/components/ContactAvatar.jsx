import { useTheme } from "../context/ThemeContext.jsx";

/** Initials for a resident name: first letters of the first and last word. */
export function initialsOf(name) {
  const words = String(name || "").trim().split(/\s+/).filter(Boolean);
  if (!words.length) return "?";
  const letters = words.length > 1 ? words[0][0] + words[words.length - 1][0] : words[0].slice(0, 2);
  return letters.toUpperCase();
}

// The photo the user chose for a contact, or plain initials when there is none.
// Initials are drawn as text, never as a generated picture.
export default function ContactAvatar({ name, photo, size = 40 }) {
  const { V, t } = useTheme();
  const box = { width: size, height: size, borderRadius: "50%", flex: "none", border: `1.5px solid ${V.outv}` };
  if (photo) return <img src={photo} alt="" width={size} height={size} style={{ ...box, objectFit: "cover", display: "block" }} />;
  return (
    <span aria-hidden="true" style={{ ...box, display: "inline-flex", alignItems: "center", justifyContent: "center", background: V.surf2 || V.surf, color: V.pri, font: `700 ${Math.round(size * 0.36)}px/1 ${t.font}` }}>
      {initialsOf(name)}
    </span>
  );
}
