import { useEffect, useRef, useState } from "react";
import { useTheme } from "../context/ThemeContext.jsx";
import { app } from "../linkpoint/app";
import Icon from "./Icon.jsx";

/**
 * Outfit Carousel Drawer for Mobile Live Preview
 *
 * Slide-up bottom drawer overlay rendered directly over the 3D Viewport.
 * Occupies <= 30% of viewport height (keeping >= 70% 3D view visible).
 * Provides horizontal scrollable outfit preset cards, quick search filter,
 * batch wear triggers, and gesture drag-to-dismiss behavior.
 */
export default function OutfitCarouselDrawer({ open = false, onClose }) {
  const { V, t } = useTheme();
  const [query, setQuery] = useState("");
  const [outfits, setOutfits] = useState(() => app.inventory.getSavedOutfits(query));
  const [activeOutfitId, setActiveOutfitId] = useState(() => app.inventory.activeOutfitId || "outfit-1");
  const [wearingId, setWearingId] = useState(null);
  const [dragOffsetY, setDragOffsetY] = useState(0);
  const [isDragging, setIsDragging] = useState(false);

  const touchStartY = useRef(null);
  const drawerRef = useRef(null);

  useEffect(() => {
    const refresh = () => {
      setOutfits(app.inventory.getSavedOutfits(query));
      setActiveOutfitId(app.inventory.activeOutfitId || "outfit-1");
    };

    refresh();
    app.inventory.on("inventory_updated", refresh);
    app.inventory.on("outfit_worn", refresh);

    return () => {
      app.inventory.off("inventory_updated", refresh);
      app.inventory.off("outfit_worn", refresh);
    };
  }, [query]);

  if (!open) return null;

  const handleWear = async (outfitId) => {
    setWearingId(outfitId);
    try {
      await app.inventory.wearOutfit(outfitId);
      setActiveOutfitId(outfitId);
    } catch (err) {
      console.warn("Failed to wear outfit:", err);
    } finally {
      setWearingId(null);
    }
  };

  // Touch / Drag handle gesture handling
  const handleTouchStart = (e) => {
    touchStartY.current = e.touches[0].clientY;
    setIsDragging(true);
  };

  const handleTouchMove = (e) => {
    if (touchStartY.current === null) return;
    const currentY = e.touches[0].clientY;
    const deltaY = currentY - touchStartY.current;
    if (deltaY > 0) {
      setDragOffsetY(deltaY);
    }
  };

  const handleTouchEnd = () => {
    if (dragOffsetY > 50) {
      onClose?.();
    }
    setDragOffsetY(0);
    setIsDragging(false);
    touchStartY.current = null;
  };

  const handleMouseDown = (e) => {
    touchStartY.current = e.clientY;
    setIsDragging(true);

    const handleMouseMove = (moveEvent) => {
      if (touchStartY.current === null) return;
      const deltaY = moveEvent.clientY - touchStartY.current;
      if (deltaY > 0) setDragOffsetY(deltaY);
    };

    const handleMouseUp = (upEvent) => {
      const deltaY = touchStartY.current !== null ? upEvent.clientY - touchStartY.current : 0;
      if (deltaY > 50) {
        onClose?.();
      }
      setDragOffsetY(0);
      setIsDragging(false);
      touchStartY.current = null;
      window.removeEventListener("mousemove", handleMouseMove);
      window.removeEventListener("mouseup", handleMouseUp);
    };

    window.addEventListener("mousemove", handleMouseMove);
    window.addEventListener("mouseup", handleMouseUp);
  };

  return (
    <div
      className="outfit-drawer-backdrop"
      onClick={(e) => {
        if (e.target === e.currentTarget) {
          onClose?.();
        }
      }}
      style={{
        position: "absolute",
        inset: 0,
        zIndex: 40,
        display: "flex",
        flexDirection: "column",
        justifyContent: "flex-end",
        background: "rgba(0, 0, 0, 0.25)",
        pointerEvents: "auto",
        transition: "opacity 0.2s ease",
      }}
    >
      <div
        ref={drawerRef}
        aria-label="Outfit Carousel Drawer"
        style={{
          width: "100%",
          maxHeight: "30vh",
          minHeight: "210px",
          background: V.surf,
          borderTop: `2px solid ${V.pri}`,
          borderTopLeftRadius: "16px",
          borderTopRightRadius: "16px",
          boxShadow: "0 -8px 24px rgba(0,0,0,0.4)",
          display: "flex",
          flexDirection: "column",
          transform: `translateY(${dragOffsetY}px)`,
          transition: isDragging ? "none" : "transform 0.2s ease, max-height 0.2s ease",
          touchAction: "none",
          userSelect: "none",
        }}
      >
        {/* Drag Handle Bar */}
        <div
          aria-label="Drag handle"
          onTouchStart={handleTouchStart}
          onTouchMove={handleTouchMove}
          onTouchEnd={handleTouchEnd}
          onMouseDown={handleMouseDown}
          style={{
            width: "100%",
            padding: "8px 0 4px 0",
            display: "flex",
            justifyContent: "center",
            cursor: "grab",
          }}
        >
          <div
            style={{
              width: "36px",
              height: "4px",
              borderRadius: "2px",
              background: V.outv,
            }}
          />
        </div>

        {/* Header & Quick Search Bar */}
        <div
          style={{
            padding: "0 12px 6px 12px",
            display: "flex",
            alignItems: "center",
            gap: "8px",
          }}
        >
          <div style={{ display: "flex", alignItems: "center", gap: "6px", color: V.pri }}>
            <Icon name="shirt" size={16} />
            <span style={{ fontSize: "11px", fontWeight: 800, letterSpacing: "0.08em" }}>
              OUTFITS
            </span>
          </div>

          {/* Quick Search Filter */}
          <div
            style={{
              flex: 1,
              display: "flex",
              alignItems: "center",
              gap: "6px",
              padding: "4px 8px",
              background: V.bg,
              border: `1px solid ${V.outv}`,
              borderRadius: V.rs,
            }}
          >
            <Icon name="search" size={14} style={{ color: V.sec2 }} />
            <input
              type="text"
              value={query}
              onChange={(e) => setQuery(e.target.value)}
              placeholder="Search saved outfits..."
              aria-label="Search saved outfits"
              style={{
                width: "100%",
                background: "transparent",
                border: "none",
                outline: "none",
                color: V.ink,
                fontSize: "11px",
                fontFamily: t.font,
              }}
            />
            {query && (
              <button
                type="button"
                onClick={() => setQuery("")}
                aria-label="Clear outfit search"
                style={{
                  background: "none",
                  border: "none",
                  color: V.sec2,
                  cursor: "pointer",
                  padding: 0,
                  fontSize: "12px",
                }}
              >
                ×
              </button>
            )}
          </div>

          <button
            type="button"
            onClick={onClose}
            aria-label="Close outfit drawer"
            style={{
              background: "none",
              border: "none",
              color: V.ink,
              fontSize: "18px",
              fontWeight: "bold",
              cursor: "pointer",
              padding: "0 4px",
            }}
          >
            ×
          </button>
        </div>

        {/* Horizontal Outfit Carousel */}
        <div
          aria-label="Outfit presets carousel"
          style={{
            flex: 1,
            overflowX: "auto",
            overflowY: "hidden",
            display: "flex",
            gap: "10px",
            padding: "4px 12px 12px 12px",
            scrollSnapType: "x mandatory",
            WebkitOverflowScrolling: "touch",
          }}
        >
          {outfits.length > 0 ? (
            outfits.map((outfit) => {
              const isWorn = outfit.id === activeOutfitId || outfit.category === "WORN";
              const isWearingThis = wearingId === outfit.id;

              return (
                <div
                  key={outfit.id}
                  aria-label={`Outfit option ${outfit.name}`}
                  style={{
                    flex: "0 0 170px",
                    scrollSnapAlign: "start",
                    background: isWorn ? V.bg : V.surf,
                    border: `1px solid ${isWorn ? V.pri : V.outv}`,
                    borderRadius: V.rs,
                    padding: "8px 10px",
                    display: "flex",
                    flexDirection: "column",
                    justifyContent: "space-between",
                    boxShadow: isWorn ? `0 0 8px ${V.pri}44` : "none",
                    position: "relative",
                  }}
                >
                  <div>
                    <div
                      style={{
                        display: "flex",
                        alignItems: "center",
                        justifyContent: "space-between",
                        marginBottom: "4px",
                      }}
                    >
                      <Icon name={isWorn ? "user-check" : "shirt"} size={14} style={{ color: isWorn ? V.pri : V.sec2 }} />
                      <span
                        style={{
                          fontSize: "9px",
                          fontWeight: 800,
                          padding: "1px 5px",
                          borderRadius: "4px",
                          background: isWorn ? V.pri : V.outv,
                          color: isWorn ? V.onpri : V.ink,
                        }}
                      >
                        {isWorn ? "WORN" : "SAVED"}
                      </span>
                    </div>

                    <strong
                      style={{
                        display: "block",
                        fontSize: "12px",
                        fontWeight: 700,
                        color: V.ink,
                        whiteSpace: "nowrap",
                        overflow: "hidden",
                        textOverflow: "ellipsis",
                      }}
                      title={outfit.name}
                    >
                      {outfit.name}
                    </strong>

                    <span
                      style={{
                        display: "block",
                        fontSize: "10px",
                        color: V.sec2,
                        marginTop: "2px",
                        whiteSpace: "nowrap",
                        overflow: "hidden",
                        textOverflow: "ellipsis",
                      }}
                    >
                      {outfit.itemCount} items · {outfit.description}
                    </span>
                  </div>

                  <button
                    type="button"
                    onClick={() => handleWear(outfit.id)}
                    disabled={isWorn || isWearingThis}
                    style={{
                      marginTop: "8px",
                      width: "100%",
                      padding: "4px 8px",
                      fontSize: "10px",
                      fontWeight: 700,
                      borderRadius: V.rs,
                      border: "none",
                      background: isWorn ? V.outv : V.pri,
                      color: isWorn ? V.ink : V.onpri,
                      cursor: isWorn ? "default" : "pointer",
                      display: "flex",
                      alignItems: "center",
                      justifyContent: "center",
                      gap: "4px",
                    }}
                  >
                    {isWearingThis ? (
                      "WEARING..."
                    ) : isWorn ? (
                      <>
                        <Icon name="check" size={12} />
                        ACTIVE
                      </>
                    ) : (
                      "WEAR OUTFIT"
                    )}
                  </button>
                </div>
              );
            })
          ) : (
            <div
              style={{
                flex: 1,
                display: "flex",
                flexDirection: "column",
                alignItems: "center",
                justifyContent: "center",
                padding: "16px",
                color: V.sec2,
                fontSize: "11px",
              }}
            >
              <Icon name="folder-open" size={20} style={{ marginBottom: "4px" }} />
              <span>No outfits match "{query}"</span>
              <button
                type="button"
                onClick={() => setQuery("")}
                style={{
                  marginTop: "6px",
                  padding: "2px 8px",
                  background: V.pri,
                  color: V.onpri,
                  border: "none",
                  borderRadius: V.rs,
                  fontSize: "10px",
                  cursor: "pointer",
                }}
              >
                Clear Search
              </button>
            </div>
          )}
        </div>
      </div>
    </div>
  );
}
