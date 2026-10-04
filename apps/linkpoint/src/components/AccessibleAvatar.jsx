import { useTheme } from "../context/ThemeContext.jsx";

/** Initials for a resident name: first letters of the first and last word. */
export function initialsOf(name) {
  const words = String(name || "").trim().split(/\s+/).filter(Boolean);
  if (!words.length) return "?";
  const letters = words.length > 1 ? words[0][0] + words[words.length - 1][0] : words[0].slice(0, 2);
  return letters.toUpperCase();
}

/**
 * Primitive component for resident avatars.
 * Parameterizes whether the avatar is purely decorative or requires a standalone accessible name.
 */
export default function AccessibleAvatar({
  name,
  photo = undefined,
  size = 40,
  label = undefined,
  decorative = true,
  className = undefined,
  style = undefined,
  ...rest
}) {
  const { V, t } = useTheme();
  const box = {
    width: size,
    height: size,
    borderRadius: "50%",
    flex: "none",
    border: `1.5px solid ${V.outv}`,
    ...style,
  };

  const accessibleName = label || name || "Avatar";

  if (photo) {
    return (
      <img
        src={photo}
        alt={decorative ? "" : accessibleName}
        aria-hidden={decorative ? "true" : undefined}
        width={size}
        height={size}
        className={className}
        style={{ ...box, objectFit: "cover", display: "block" }}
        {...rest}
      />
    );
  }

  if (decorative) {
    return (
      <span
        aria-hidden="true"
        className={className}
        style={{
          ...box,
          display: "inline-flex",
          alignItems: "center",
          justifyContent: "center",
          background: V.surf2 || V.surf,
          color: V.pri,
          font: `700 ${Math.round(size * 0.36)}px/1 ${t.font}`,
        }}
        {...rest}
      >
        {initialsOf(name)}
      </span>
    );
  }

  return (
    <span
      role="img"
      aria-label={accessibleName}
      className={className}
      style={{
        ...box,
        display: "inline-flex",
        alignItems: "center",
        justifyContent: "center",
        background: V.surf2 || V.surf,
        color: V.pri,
        font: `700 ${Math.round(size * 0.36)}px/1 ${t.font}`,
      }}
      {...rest}
    >
      {initialsOf(name)}
    </span>
  );
}
