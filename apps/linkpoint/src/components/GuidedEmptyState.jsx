import { useTheme } from "../context/ThemeContext.jsx";
import Icon from "./Icon.jsx";

/**
 * Reusable guided empty state component with onboarding prompts and actions.
 */
export default function GuidedEmptyState({
  icon = "inbox",
  title = "No data available",
  description = null,
  message = null,
  actionLabel = null,
  onAction = null,
  secondaryActionLabel = null,
  onSecondaryAction = null,
  isSearch = false,
  className = "",
  style = {},
}) {
  const { V, t } = useTheme();

  const bodyText = description || message || (isSearch ? "No matching results were found." : "There are no items to display.");
  const displayTitle = isSearch && title === "No data available" ? "No search results" : title;
  const displayIcon = isSearch && icon === "inbox" ? "search" : icon;

  return (
    <div
      className={`guided-empty-state ${className}`}
      style={{
        color: V?.ink || "inherit",
        fontFamily: t?.font || "inherit",
        ...style,
      }}
      role="region"
      aria-label={displayTitle}
    >
      <div
        className="guided-empty-icon"
        style={{
          border: `1px solid ${V?.outv || "rgba(255,255,255,0.15)"}`,
          background: V?.surf || "rgba(255,255,255,0.05)",
          color: V?.pri || "#6cff9a",
        }}
      >
        {typeof displayIcon === "string" ? (
          <Icon name={displayIcon} size={28} />
        ) : (
          displayIcon
        )}
      </div>

      <h3
        style={{
          margin: "8px 0 4px",
          fontSize: "15px",
          fontWeight: 700,
          color: V?.ink || "inherit",
        }}
      >
        {displayTitle}
      </h3>

      <p
        style={{
          margin: 0,
          maxWidth: "380px",
          fontSize: "12px",
          lineHeight: 1.5,
          color: V?.ink2 || "rgba(255,255,255,0.65)",
        }}
      >
        {bodyText}
      </p>

      {!isSearch && (actionLabel || secondaryActionLabel) ? (
        <div className="guided-empty-actions">
          {actionLabel && onAction ? (
            <button
              type="button"
              className="guided-empty-btn"
              onClick={onAction}
              style={{
                background: V?.pri || "#6cff9a",
                color: V?.onpri || "#000000",
                border: "none",
              }}
            >
              {actionLabel}
            </button>
          ) : null}

          {secondaryActionLabel && onSecondaryAction ? (
            <button
              type="button"
              className="guided-empty-btn"
              onClick={onSecondaryAction}
              style={{
                background: V?.surf || "transparent",
                color: V?.pri || "#6cff9a",
                border: `1px solid ${V?.outv || "rgba(255,255,255,0.2)"}`,
              }}
            >
              {secondaryActionLabel}
            </button>
          ) : null}
        </div>
      ) : null}
    </div>
  );
}
