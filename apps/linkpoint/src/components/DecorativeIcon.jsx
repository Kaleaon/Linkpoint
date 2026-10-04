import Icon from "./Icon.jsx";

/**
 * Primitive component for rendering decorative Lucide icons.
 * Enforces aria-hidden="true" so screen readers ignore decorative icon graphics.
 */
export default function DecorativeIcon({
  name,
  size = 18,
  style = undefined,
  className = undefined,
  strokeWidth = undefined,
}) {
  return <Icon name={name} size={size} style={style} className={className} strokeWidth={strokeWidth} aria-hidden="true" />;
}
