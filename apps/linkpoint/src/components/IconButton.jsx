import DecorativeIcon from "./DecorativeIcon.jsx";

/**
 * Primitive component for icon-only action buttons.
 * Enforces mandatory accessible label via aria-label prop per ARIA14 and wraps native <button>
 * elements to eliminate custom role="button" div anti-patterns per WCAG 4.1.2.
 */
export default function IconButton({
  icon,
  label = undefined,
  onClick = undefined,
  size = 16,
  buttonSize = 36,
  style = undefined,
  className = undefined,
  disabled = false,
  title = undefined,
  type = "button",
  ...rest
}) {
  if (!label) {
    console.warn(`[IconButton] Missing required label prop for icon "${icon}".`);
  }

  return (
    <button
      type={type}
      onClick={onClick}
      disabled={disabled}
      aria-label={label}
      title={title || label}
      className={className}
      style={{
        width: typeof buttonSize === "number" ? `${buttonSize}px` : buttonSize,
        height: typeof buttonSize === "number" ? `${buttonSize}px` : buttonSize,
        display: "inline-flex",
        alignItems: "center",
        justifyContent: "center",
        cursor: disabled ? "default" : "pointer",
        ...style,
      }}
      {...rest}
    >
      <DecorativeIcon name={icon} size={size} />
    </button>
  );
}
