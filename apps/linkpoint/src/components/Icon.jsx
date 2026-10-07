import * as LucideIcons from "lucide-react";

// The mockup renders icons by looking up a kebab-case lucide name (e.g.
// "message-square") against window.lucide.icons[PascalCase]. lucide-react
// exports the same icon set as named PascalCase components, so this just
// does the same kebab -> Pascal conversion and looks the component up.
function pascal(name) {
  return String(name || "")
    .replace(/(^|-)([a-z0-9])/g, (_, __, c) => c.toUpperCase());
}

const cache = new Map();
function resolve(name) {
  if (!name) return null;
  if (cache.has(name)) return cache.get(name);
  const key = pascal(name);
  const Cmp = LucideIcons[key] || null;
  cache.set(name, Cmp);
  return Cmp;
}

export default function Icon({
  name,
  size = 18,
  style = undefined,
  className = undefined,
  strokeWidth = undefined,
  title = undefined,
  "aria-label": ariaLabel = undefined,
  "aria-labelledby": ariaLabelledBy = undefined,
  "aria-hidden": ariaHidden = undefined,
  ...rest
}) {
  const Cmp = resolve(name);
  const hasAccessibleName = Boolean(title || ariaLabel || ariaLabelledBy);
  const isHidden = ariaHidden ?? (!hasAccessibleName ? "true" : undefined);
  const role = rest.role ?? (hasAccessibleName ? "img" : undefined);

  if (!Cmp) {
    return (
      <span
        className={className}
        style={{ width: size, height: size, display: "inline-block", ...style }}
        aria-hidden={isHidden}
      />
    );
  }

  return (
    <Cmp
      className={className}
      width={style?.width ?? size}
      height={style?.height ?? size}
      strokeWidth={strokeWidth}
      style={{ display: "inline-flex", flexShrink: 0, ...style }}
      aria-hidden={isHidden}
      aria-label={ariaLabel}
      aria-labelledby={ariaLabelledBy}
      role={role}
      {...rest}
    >
      {title ? <title>{title}</title> : null}
    </Cmp>
  );
}
