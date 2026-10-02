import { useTheme } from "../context/ThemeContext.jsx";

/**
 * Reusable skeleton loader placeholder for async data lists.
 */
export default function ListSkeletonLoader({
  count = 5,
  variant = "list",
  className = "",
  style = {},
}) {
  const { V } = useTheme();

  const skeletonRows = Array.from({ length: count }, (_, index) => index);

  return (
    <div
      className={`skeleton-loader ${className}`}
      style={style}
      role="status"
      aria-busy="true"
      aria-label="Loading contents..."
    >
      {skeletonRows.map((index) => (
        <div
          key={index}
          className="skeleton-row"
          style={{
            borderColor: V?.outv || "rgba(255,255,255,0.1)",
            background: V?.surf || "rgba(255,255,255,0.03)",
          }}
        >
          {variant === "contact" ? (
            <div className="skeleton-bone skeleton-avatar" />
          ) : (
            <div className="skeleton-bone skeleton-icon" />
          )}
          <div className="skeleton-content">
            <div
              className="skeleton-bone skeleton-line"
              style={{ width: `${60 + ((index * 13) % 35)}%` }}
            />
            <div
              className="skeleton-bone skeleton-line short"
              style={{ width: `${30 + ((index * 17) % 25)}%` }}
            />
          </div>
        </div>
      ))}
    </div>
  );
}
