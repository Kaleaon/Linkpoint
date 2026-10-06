import { useEffect, useRef, useState, useId } from "react";
import { useTranslation } from "react-i18next";
import { useTheme } from "../context/ThemeContext.jsx";
import Icon from "./Icon.jsx";

export const RECENT_LOCATIONS_KEY = "linkpoint_recent_start_locations";

export function getRecentLocations(storageKey = RECENT_LOCATIONS_KEY) {
  if (typeof localStorage === "undefined") return [];
  try {
    const raw = localStorage.getItem(storageKey);
    if (!raw) return [];
    const parsed = JSON.parse(raw);
    if (!Array.isArray(parsed)) return [];
    const clean = [];
    for (const item of parsed) {
      if (typeof item === "string" && item.trim()) {
        const trimmed = item.trim();
        if (!clean.some((c) => c.toLowerCase() === trimmed.toLowerCase())) {
          clean.push(trimmed);
        }
      }
    }
    return clean;
  } catch {
    return [];
  }
}

export function saveRecentLocation(location, storageKey = RECENT_LOCATIONS_KEY) {
  if (typeof localStorage === "undefined" || !location || typeof location !== "string") {
    return getRecentLocations(storageKey);
  }
  const trimmed = location.trim();
  if (!trimmed) return getRecentLocations(storageKey);
  const lower = trimmed.toLowerCase();
  if (["last", "last location", "first", "home"].includes(lower)) {
    return getRecentLocations(storageKey);
  }

  const recents = getRecentLocations(storageKey);
  const filtered = recents.filter((item) => item.toLowerCase() !== lower);
  filtered.unshift(trimmed);
  const updated = filtered.slice(0, 10);
  try {
    localStorage.setItem(storageKey, JSON.stringify(updated));
  } catch (_e) {
    // ignore quota error
  }
  return updated;
}

export function clearRecentLocations(storageKey = RECENT_LOCATIONS_KEY) {
  if (typeof localStorage === "undefined") return;
  try {
    localStorage.removeItem(storageKey);
  } catch (_e) {
    // ignore
  }
}

export default function StartLocationCombobox({
  value = "last",
  onChange,
  style,
  placeholder,
  storageKey = RECENT_LOCATIONS_KEY,
}) {
  const { t } = useTranslation();
  const { V, t: typography } = useTheme();
  const [isOpen, setIsOpen] = useState(false);
  const [recents, setRecents] = useState([]);
  const [activeIndex, setActiveIndex] = useState(-1);
  const containerRef = useRef(null);

  const baseId = useId();
  const listboxId = `start-location-listbox-${baseId}`;
  const optionIdPrefix = `start-location-option-${baseId}`;

  useEffect(() => {
    setRecents(getRecentLocations(storageKey));
  }, [isOpen, storageKey]);

  useEffect(() => {
    if (!isOpen) {
      setActiveIndex(-1);
    }
  }, [isOpen]);

  useEffect(() => {
    const handleClickOutside = (event) => {
      if (containerRef.current && !containerRef.current.contains(event.target)) {
        setIsOpen(false);
        setActiveIndex(-1);
      }
    };
    document.addEventListener("mousedown", handleClickOutside);
    return () => document.removeEventListener("mousedown", handleClickOutside);
  }, []);

  const standardOptions = [
    { value: "last", label: t("login_option_last_location") || "Last location" },
    { value: "home", label: t("login_option_home") || "Home" },
  ];

  const allOptions = [
    ...standardOptions.map((opt) => ({ type: "standard", value: opt.value, label: opt.label })),
    ...recents.map((loc) => ({ type: "recent", value: loc, label: loc })),
  ];
  const totalOptions = allOptions.length;

  useEffect(() => {
    if (isOpen && activeIndex >= 0) {
      const activeEl = document.getElementById(`${optionIdPrefix}-${activeIndex}`);
      if (activeEl && typeof activeEl.scrollIntoView === "function") {
        activeEl.scrollIntoView({ block: "nearest" });
      }
    }
  }, [activeIndex, isOpen, optionIdPrefix]);

  const handleSelectOption = (optValue) => {
    onChange?.(optValue);
    setIsOpen(false);
    setActiveIndex(-1);
  };

  const handleKeyDown = (e) => {
    if (e.key === "ArrowDown") {
      e.preventDefault();
      if (!isOpen) {
        setIsOpen(true);
        setActiveIndex(0);
      } else if (totalOptions > 0) {
        setActiveIndex((prev) => (prev < 0 ? 0 : (prev + 1) % totalOptions));
      }
    } else if (e.key === "ArrowUp") {
      e.preventDefault();
      if (isOpen && totalOptions > 0) {
        setActiveIndex((prev) => (prev <= 0 ? totalOptions - 1 : prev - 1));
      }
    } else if (e.key === "Enter") {
      if (isOpen && activeIndex >= 0 && activeIndex < totalOptions) {
        e.preventDefault();
        const selectedOption = allOptions[activeIndex];
        handleSelectOption(selectedOption.value);
      }
    } else if (e.key === "Escape") {
      if (isOpen) {
        e.preventDefault();
        setIsOpen(false);
        setActiveIndex(-1);
      }
    } else if (e.key === "Home") {
      if (isOpen && totalOptions > 0) {
        e.preventDefault();
        setActiveIndex(0);
      }
    } else if (e.key === "End") {
      if (isOpen && totalOptions > 0) {
        e.preventDefault();
        setActiveIndex(totalOptions - 1);
      }
    }
  };

  const getDisplayValue = () => {
    if (value === "last") return t("login_option_last_location") || "Last location";
    if (value === "home") return t("login_option_home") || "Home";
    return value || "";
  };

  const controlStyle = {
    width: "100%",
    minHeight: 44,
    boxSizing: "border-box",
    padding: "0 11px",
    border: `1px solid ${V.outv}`,
    borderRadius: V.rs,
    background: V.bg,
    color: V.ink,
    font: `400 15px/1.3 ${typography.font}`,
    display: "flex",
    alignItems: "center",
    justifyContent: "space-between",
    position: "relative",
    ...style,
  };

  return (
    <div ref={containerRef} style={{ position: "relative", width: "100%", marginTop: 6 }}>
      <div style={controlStyle}>
        <input
          type="text"
          role="combobox"
          aria-expanded={isOpen}
          aria-haspopup="listbox"
          aria-controls={listboxId}
          aria-autocomplete="list"
          aria-activedescendant={activeIndex >= 0 ? `${optionIdPrefix}-${activeIndex}` : undefined}
          value={getDisplayValue()}
          onChange={(e) => {
            setActiveIndex(-1);
            if (!isOpen) setIsOpen(true);
            const val = e.target.value;
            const lower = val.trim().toLowerCase();
            const lastLabel = (t("login_option_last_location") || "last location").toLowerCase();
            const homeLabel = (t("login_option_home") || "home").toLowerCase();

            if (lower === lastLabel || lower === "last") {
              onChange?.("last");
            } else if (lower === homeLabel || lower === "home" || lower === "first") {
              onChange?.("home");
            } else {
              onChange?.(val);
            }
          }}
          onFocus={() => setIsOpen(true)}
          onKeyDown={handleKeyDown}
          placeholder={placeholder || t("login_option_last_location") || "Start location or SLurl"}
          style={{
            flex: 1,
            border: "none",
            outline: "none",
            background: "transparent",
            color: V.ink,
            font: `400 15px/1.3 ${typography.font}`,
            padding: 0,
            margin: 0,
          }}
        />
        <button
          type="button"
          tabIndex={-1}
          onClick={() => setIsOpen(!isOpen)}
          aria-label="Toggle start location options"
          style={{
            border: "none",
            background: "transparent",
            color: V.ink2,
            cursor: "pointer",
            padding: "4px",
            display: "flex",
            alignItems: "center",
          }}
        >
          <Icon name={isOpen ? "chevron-up" : "chevron-down"} size={16} />
        </button>
      </div>

      {isOpen && (
        <ul
          id={listboxId}
          role="listbox"
          style={{
            position: "absolute",
            top: "100%",
            left: 0,
            right: 0,
            zIndex: 100,
            marginTop: 4,
            padding: "6px 0",
            listStyle: "none",
            background: V.surf || V.bg,
            border: `1px solid ${V.outv}`,
            borderRadius: V.rs,
            boxShadow: "0 4px 12px rgba(0, 0, 0, 0.15)",
            maxHeight: 220,
            overflowY: "auto",
          }}
        >
          {standardOptions.map((opt, index) => {
            const globalIndex = index;
            const isSelected = value === opt.value;
            const isHighlighted = activeIndex === globalIndex;
            const activeOrSelected = isSelected || isHighlighted;

            return (
              <li
                key={opt.value}
                id={`${optionIdPrefix}-${globalIndex}`}
                role="option"
                aria-selected={isSelected || isHighlighted}
                onClick={() => handleSelectOption(opt.value)}
                onMouseEnter={() => setActiveIndex(globalIndex)}
                style={{
                  padding: "8px 12px",
                  cursor: "pointer",
                  background: activeOrSelected ? V.priC || "rgba(0,0,0,0.05)" : "transparent",
                  color: activeOrSelected ? V.pri : V.ink,
                  fontSize: 14,
                  fontFamily: typography.font,
                  display: "flex",
                  alignItems: "center",
                  justifyContent: "space-between",
                }}
              >
                <span>{opt.label}</span>
                {isSelected && <Icon name="check" size={14} />}
              </li>
            );
          })}

          {recents.length > 0 && (
            <>
              <li
                role="presentation"
                style={{
                  padding: "8px 12px 4px 12px",
                  fontSize: 11,
                  fontWeight: 600,
                  letterSpacing: ".08em",
                  color: V.pri,
                  textTransform: "uppercase",
                  borderTop: `1px solid ${V.outv}`,
                  marginTop: 4,
                }}
              >
                {t("login_recent_locations") || "Recent Destinations"}
              </li>
              {recents.map((loc, index) => {
                const globalIndex = standardOptions.length + index;
                const isSelected = value === loc;
                const isHighlighted = activeIndex === globalIndex;
                const activeOrSelected = isSelected || isHighlighted;

                return (
                  <li
                    key={loc}
                    id={`${optionIdPrefix}-${globalIndex}`}
                    role="option"
                    aria-selected={isSelected || isHighlighted}
                    onClick={() => handleSelectOption(loc)}
                    onMouseEnter={() => setActiveIndex(globalIndex)}
                    style={{
                      padding: "8px 12px",
                      cursor: "pointer",
                      background: activeOrSelected ? V.priC || "rgba(0,0,0,0.05)" : "transparent",
                      color: activeOrSelected ? V.pri : V.ink,
                      fontSize: 14,
                      fontFamily: typography.font,
                      display: "flex",
                      alignItems: "center",
                      justifyContent: "space-between",
                    }}
                  >
                    <span style={{ overflow: "hidden", textOverflow: "ellipsis", whiteSpace: "nowrap" }}>
                      {loc}
                    </span>
                    {isSelected && <Icon name="check" size={14} />}
                  </li>
                );
              })}
            </>
          )}
        </ul>
      )}
    </div>
  );
}
