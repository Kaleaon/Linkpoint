import { useEffect, useMemo, useState } from "react";
import { useApp } from "../context/AppContext.jsx";
import { useTheme } from "../context/ThemeContext.jsx";
import { useRlvSafe } from "../viewer/RlvContext";
import { app } from "../linkpoint/app.ts";
import { macroTaskQueue } from "../linkpoint/macro-task-queue.ts";
import Icon from "../components/Icon.jsx";
import ListSkeletonLoader from "../components/ListSkeletonLoader.jsx";
import GuidedEmptyState from "../components/GuidedEmptyState.jsx";
import TextureUploadModal from "../components/TextureUploadModal.jsx";
import FocusTrap from "../components/FocusTrap.jsx";

export function GiveItemModal({ isOpen, item, onClose }) {
  const { V } = useTheme();
  const { actions } = useApp();
  const [recipientId, setRecipientId] = useState("");
  const [error, setError] = useState(null);
  const [success, setSuccess] = useState(null);
  const [busy, setBusy] = useState(false);

  useEffect(() => {
    if (isOpen) {
      setRecipientId("");
      setError(null);
      setSuccess(null);
      setBusy(false);
    }
  }, [isOpen]);

  const onlineFriends = useMemo(() => {
    if (!isOpen) return [];
    const friends = app.friends?.getFriends?.() || [];
    return friends.filter(
      (f) => f && (f.onlineStatus === "online" || f.online === true || String(f.onlineStatus).toLowerCase() === "online")
    );
  }, [isOpen]);

  const nearbyAvatars = useMemo(() => {
    if (!isOpen) return [];
    const nearby = (typeof app.world?.getNearbyUsers === "function" ? app.world.getNearbyUsers() : app.world?.nearbyUsers) || [];
    const currentAgentId = app.auth?.user?.id || app.protocol?.agentId;
    return nearby.filter((u) => u && u.id && u.id !== currentAgentId);
  }, [isOpen]);

  if (!isOpen || !item) return null;

  const isNonTransferable = item.transferable === false;

  const handleGive = async () => {
    setError(null);
    setSuccess(null);
    const target = recipientId.trim();
    if (!target) {
      setError("Please select or enter a recipient avatar ID.");
      return;
    }
    if (isNonTransferable) {
      setError("Item is not transferable.");
      return;
    }
    setBusy(true);
    try {
      await app.inventory.offerItem(target, item);
      setSuccess(`Item "${item.name || "Item"}" offered successfully!`);
      if (actions?.notify) {
        actions.notify(`Offered ${item.name || "item"} to avatar`);
      }
      setTimeout(() => {
        onClose();
      }, 1000);
    } catch (err) {
      setError(err?.message || "Failed to offer item to avatar.");
    } finally {
      setBusy(false);
    }
  };

  return (
    <FocusTrap
      active={isOpen}
      onEscape={busy ? undefined : onClose}
      style={{
        position: "fixed",
        inset: 0,
        zIndex: 1100,
        background: "rgba(0,0,0,0.72)",
        display: "flex",
        alignItems: "center",
        justifyContent: "center",
        padding: "16px",
      }}
    >
      <div
        role="dialog"
        aria-modal="true"
        aria-labelledby="give-item-modal-title"
        className="give-item-modal"
        style={{
          width: "100%",
          maxWidth: "480px",
          background: V.surf,
          border: `1px solid ${V.pri}`,
          borderRadius: V.rm || "8px",
          boxShadow: "0 12px 32px rgba(0,0,0,0.5)",
          padding: "20px",
          maxHeight: "90vh",
          overflowY: "auto",
        }}
      >
        <div style={{ display: "flex", alignItems: "center", justifyContent: "space-between", marginBottom: "16px", borderBottom: `1px solid ${V.outv}`, paddingBottom: "12px" }}>
          <div style={{ display: "flex", alignItems: "center", gap: "10px" }}>
            <Icon name="share-2" size={20} style={{ color: V.pri }} />
            <h2 id="give-item-modal-title" style={{ fontSize: "16px", fontWeight: "700", color: V.ink, margin: 0 }}>
              Give / Share Item
            </h2>
          </div>
          <button
            type="button"
            onClick={onClose}
            disabled={busy}
            aria-label="Close give item modal"
            style={{ background: "none", border: "none", color: V.ink2, fontSize: "20px", cursor: busy ? "not-allowed" : "pointer", padding: "0 4px" }}
          >
            ×
          </button>
        </div>

        {/* Item Information */}
        <div style={{ padding: "10px 12px", background: V.bg, borderRadius: V.rs || "4px", marginBottom: "16px", border: `1px solid ${V.outv}` }}>
          <div style={{ display: "flex", alignItems: "center", gap: "8px" }}>
            <Icon name="file" size={16} style={{ color: V.sec2 }} />
            <span style={{ fontWeight: 600, fontSize: "13px", color: V.ink }}>{item.name || "Unnamed Item"}</span>
          </div>
          {isNonTransferable ? (
            <div style={{ marginTop: "6px", fontSize: "11px", color: "#e53e3e", fontWeight: 600 }}>
              ⚠ This item cannot be transferred (no-transfer permissions).
            </div>
          ) : null}
        </div>

        {/* Online Friends List */}
        <div style={{ marginBottom: "14px" }}>
          <h3 style={{ fontSize: "12px", fontWeight: 700, textTransform: "uppercase", letterSpacing: "0.5px", color: V.ink2, margin: "0 0 6px" }}>
            Online Friends ({onlineFriends.length})
          </h3>
          {onlineFriends.length ? (
            <div style={{ maxHeight: "110px", overflowY: "auto", display: "flex", flexDirection: "column", gap: "4px", padding: "4px", background: V.bg, borderRadius: V.rs || "4px", border: `1px solid ${V.outv}` }}>
              {onlineFriends.map((friend) => {
                const isSelected = recipientId === friend.id;
                return (
                  <button
                    key={friend.id}
                    type="button"
                    style={{
                      display: "flex",
                      alignItems: "center",
                      justifyContent: "space-between",
                      padding: "6px 10px",
                      background: isSelected ? V.pri : "transparent",
                      color: isSelected ? (V.onpri || "#fff") : V.ink,
                      border: "none",
                      borderRadius: V.rs || "4px",
                      cursor: "pointer",
                      textAlign: "left",
                      fontSize: "12px",
                      fontWeight: isSelected ? 700 : 500,
                    }}
                    onClick={() => setRecipientId(friend.id)}
                  >
                    <span style={{ display: "flex", alignItems: "center", gap: "6px" }}>
                      <span style={{ width: 8, height: 8, borderRadius: "50%", background: "#48bb78", display: "inline-block" }} />
                      {friend.name || "Friend"}
                    </span>
                    <span style={{ fontSize: "10px", opacity: 0.8 }}>Select</span>
                  </button>
                );
              })}
            </div>
          ) : (
            <div style={{ fontSize: "11px", color: V.ink2, padding: "6px 0", fontStyle: "italic" }}>
              No online friends available
            </div>
          )}
        </div>

        {/* Nearby Avatars List */}
        <div style={{ marginBottom: "14px" }}>
          <h3 style={{ fontSize: "12px", fontWeight: 700, textTransform: "uppercase", letterSpacing: "0.5px", color: V.ink2, margin: "0 0 6px" }}>
            Nearby Avatars ({nearbyAvatars.length})
          </h3>
          {nearbyAvatars.length ? (
            <div style={{ maxHeight: "110px", overflowY: "auto", display: "flex", flexDirection: "column", gap: "4px", padding: "4px", background: V.bg, borderRadius: V.rs || "4px", border: `1px solid ${V.outv}` }}>
              {nearbyAvatars.map((user) => {
                const isSelected = recipientId === user.id;
                return (
                  <button
                    key={user.id}
                    type="button"
                    style={{
                      display: "flex",
                      alignItems: "center",
                      justifyContent: "space-between",
                      padding: "6px 10px",
                      background: isSelected ? V.pri : "transparent",
                      color: isSelected ? (V.onpri || "#fff") : V.ink,
                      border: "none",
                      borderRadius: V.rs || "4px",
                      cursor: "pointer",
                      textAlign: "left",
                      fontSize: "12px",
                      fontWeight: isSelected ? 700 : 500,
                    }}
                    onClick={() => setRecipientId(user.id)}
                  >
                    <span>{user.name || "Avatar"}</span>
                    {user.distance !== undefined ? (
                      <span style={{ fontSize: "10px", opacity: 0.8 }}>{Math.round(user.distance)}m</span>
                    ) : (
                      <span style={{ fontSize: "10px", opacity: 0.8 }}>Nearby</span>
                    )}
                  </button>
                );
              })}
            </div>
          ) : (
            <div style={{ fontSize: "11px", color: V.ink2, padding: "6px 0", fontStyle: "italic" }}>
              No avatars nearby
            </div>
          )}
        </div>

        {/* Avatar ID Input */}
        <div style={{ marginBottom: "16px" }}>
          <label
            htmlFor="recipient-avatar-id"
            style={{ display: "block", fontSize: "12px", fontWeight: 700, textTransform: "uppercase", letterSpacing: "0.5px", color: V.ink2, marginBottom: "6px" }}
          >
            Recipient Avatar UUID
          </label>
          <input
            id="recipient-avatar-id"
            type="text"
            value={recipientId}
            onChange={(e) => setRecipientId(e.target.value)}
            placeholder="e.g. 00000000-0000-0000-0000-000000000000"
            aria-label="Recipient Avatar UUID"
            disabled={busy}
            style={{
              width: "100%",
              boxSizing: "border-box",
              padding: "8px 10px",
              background: V.bg,
              border: `1px solid ${V.outv}`,
              borderRadius: V.rs || "4px",
              color: V.ink,
              fontSize: "12px",
              outline: "none",
            }}
          />
        </div>

        {/* Error / Success Feedback */}
        {error ? (
          <div
            role="alert"
            style={{
              padding: "8px 12px",
              background: "rgba(229, 62, 62, 0.15)",
              border: "1px solid #e53e3e",
              borderRadius: V.rs || "4px",
              color: "#e53e3e",
              fontSize: "12px",
              fontWeight: 600,
              marginBottom: "14px",
            }}
          >
            {error}
          </div>
        ) : null}

        {success ? (
          <div
            role="status"
            style={{
              padding: "8px 12px",
              background: "rgba(72, 187, 120, 0.15)",
              border: "1px solid #48bb78",
              borderRadius: V.rs || "4px",
              color: "#48bb78",
              fontSize: "12px",
              fontWeight: 600,
              marginBottom: "14px",
            }}
          >
            {success}
          </div>
        ) : null}

        {/* Action Buttons */}
        <div style={{ display: "flex", gap: "10px", justifyContent: "flex-end" }}>
          <button
            type="button"
            onClick={onClose}
            disabled={busy}
            style={{
              padding: "8px 14px",
              background: "transparent",
              border: `1px solid ${V.outv}`,
              borderRadius: V.rs || "4px",
              color: V.ink,
              fontSize: "12px",
              fontWeight: 600,
              cursor: busy ? "not-allowed" : "pointer",
            }}
          >
            CANCEL
          </button>
          <button
            type="button"
            onClick={handleGive}
            disabled={busy || isNonTransferable}
            style={{
              padding: "8px 16px",
              background: isNonTransferable ? V.outv : V.pri,
              color: isNonTransferable ? V.ink2 : (V.onpri || "#fff"),
              border: "none",
              borderRadius: V.rs || "4px",
              fontSize: "12px",
              fontWeight: 700,
              cursor: busy || isNonTransferable ? "not-allowed" : "pointer",
            }}
          >
            {busy ? "OFFERING…" : "GIVE ITEM"}
          </button>
        </div>
      </div>
    </FocusTrap>
  );
}

/** Inventory rows come directly from InventoryManager capability responses. */
export default function Inventory() {
  const { V, t } = useTheme();
  const { actions } = useApp();
  const [revision, setRevision] = useState(0);
  const [filter, setFilter] = useState("");
  const [category, setCategory] = useState("all");
  const [selected, setSelected] = useState(null);
  const [contextMenu, setContextMenu] = useState(null);
  const [uploadModalOpen, setUploadModalOpen] = useState(false);
  const [giveModalItem, setGiveModalItem] = useState(null);


  useEffect(() => {
    const refresh = () => setRevision((value) => value + 1);
    app.inventory.on("inventory_loaded", refresh);
    app.inventory.on("inventory_updated", refresh);
    return () => {
      app.inventory.off("inventory_loaded", refresh);
      app.inventory.off("inventory_updated", refresh);
    };
  }, []);

  const rows = useMemo(() => {
    const tree = app.inventory.getFilteredFolderTree({
      category,
      search: filter,
    });
    const flat = app.inventory.flattenTree(tree);
    const rootId = app.inventory.rootFolder?.id || "root";
    return flat
      .filter((entry) => entry && entry.id !== rootId)
      .map((entry) => ({ ...entry, folder: entry.type === "folder" || Boolean(entry.folder) }))
      .sort((a, b) => Number(b.folder) - Number(a.folder) || String(a.name || "").localeCompare(String(b.name || "")));
  }, [filter, category, revision]);

  const [refreshing, setRefreshing] = useState(false);

  const handleRefresh = async () => {
    setRefreshing(true);
    try {
      await app.inventory.load();
    } finally {
      setRefreshing(false);
    }
  };

  const handleRunMacro = (folder, mode) => {
    setContextMenu(null);
    let items = app.inventory.getFolderItemsRecursive(folder.id);
    if (!items.length) {
      items = Array.from(app.inventory.items.values()).filter((item) => item.parent === folder.id);
    }
    void macroTaskQueue.start(items, { mode }, folder.name || "Outfit");
  };

  const handleContextMenu = (event, entry) => {
    event.preventDefault();
    setSelected(entry);
    if (entry.folder) void app.inventory.fetchFolderContents(entry.id);
    setContextMenu({
      entry,
      x: Math.min(event.clientX, window.innerWidth - 200),
      y: Math.min(event.clientY, window.innerHeight - 150),
    });
  };
  useEffect(() => {
    const visibleFolderIds = rows.filter((r) => r.folder).map((r) => r.id).slice(0, 15);
    if (visibleFolderIds.length && typeof app.inventory.updateViewportFolders === "function") {
      app.inventory.updateViewportFolders(visibleFolderIds);
    }
  }, [rows]);

  const rlv = useRlvSafe();
  const invRestricted = rlv.restricted("showinv");
  const invReason = rlv.reasonFor("showinv");

  if (invRestricted) {
    return (
      <section className="live-screen" style={{ padding: 24, textAlign: "center" }}>
        <div style={{ padding: 24, border: `1px solid ${V.outv}`, borderRadius: V.rp, background: V.surf, maxWidth: 480, margin: "40px auto" }}>
          <Icon name="lock" size={32} style={{ marginBottom: 12, color: V.pri }} />
          <h2 style={{ fontSize: 18, color: V.ink, marginBottom: 8 }}>Inventory Restricted</h2>
          <p style={{ color: V.ink2, fontSize: 14 }}>{invReason}</p>
        </div>
      </section>
    );
  }

  return (
    <section className="live-screen" onClick={() => contextMenu && setContextMenu(null)}>
      <div style={{ display: "flex", gap: 8, alignItems: "center" }}>
        <label className="filter-field" style={{ borderColor: V.outv, background: V.surf, flex: 1, margin: 0 }}>
          <Icon name="search" size={16} />
          <input value={filter} onChange={(event) => setFilter(event.target.value)} placeholder="Filter Second Life inventory" aria-label="Filter inventory" />
        </label>
        <select
          value={category}
          onChange={(event) => setCategory(event.target.value)}
          aria-label="Filter inventory by category"
          style={{
            height: "40px",
            padding: "0 10px",
            background: V.surf,
            border: `1px solid ${V.outv}`,
            borderRadius: V.rs,
            color: V.pri,
            fontSize: "12px",
            fontWeight: 600,
            cursor: "pointer",
            outline: "none",
            flex: "none",
          }}
        >
          <option value="all">All Categories</option>
          <option value="clothing">Clothing</option>
          <option value="bodypart">Body Parts</option>
          <option value="object">Objects</option>
          <option value="texture">Textures</option>
          <option value="sound">Sounds</option>
          <option value="notecard">Notecards</option>
          <option value="landmark">Landmarks</option>
          <option value="animation">Animations</option>
          <option value="gesture">Gestures</option>
          <option value="script">Scripts</option>
        </select>
        <button
          type="button"
          onClick={handleRefresh}
          disabled={refreshing || !app.auth.isLoggedIn()}
          style={{
            height: "40px",
            padding: "0 12px",
            display: "flex",
            alignItems: "center",
            gap: 6,
            background: V.surf,
            border: `1px solid ${V.outv}`,
            borderRadius: V.rs,
            color: V.pri,
            fontSize: "11px",
            fontWeight: 700,
            cursor: "pointer",
            flex: "none",
          }}
          title="Reload Second Life inventory"
        >
          <Icon name="rotate-cw" size={14} />
          {refreshing ? "FETCHING…" : "REFRESH"}
        </button>
        <button
          type="button"
          onClick={() => setUploadModalOpen(true)}
          style={{
            height: "40px",
            padding: "0 12px",
            display: "flex",
            alignItems: "center",
            gap: 6,
            background: V.pri,
            border: "none",
            borderRadius: V.rs,
            color: V.onpri || "#fff",
            fontSize: "11px",
            fontWeight: 700,
            cursor: "pointer",
            flex: "none",
          }}
          title="Upload texture (JPEG2000)"
        >
          <Icon name="upload" size={14} />
          UPLOAD TEXTURE
        </button>
      </div>
      <TextureUploadModal isOpen={uploadModalOpen} onClose={() => setUploadModalOpen(false)} />
      <div className="live-list">
        {refreshing ? (
          <ListSkeletonLoader count={5} variant="inventory" />
        ) : rows.length ? (
          rows.map((entry) => (
            <div
              key={entry.id}
              style={{ display: "flex", alignItems: "center", position: "relative" }}
            >
              <button
                type="button"
                className="inventory-row inventory-button"
                style={{ borderColor: V.outv, flex: 1 }}
                onClick={() => {
                  setSelected(entry);
                  if (entry.folder) {
                    if (typeof app.inventory.updateViewportFolders === "function") {
                      app.inventory.updateViewportFolders([entry.id]);
                    }
                    void app.inventory.fetchFolderContents(entry.id, true);
                  }
                }}
                onContextMenu={(e) => handleContextMenu(e, entry)}
              >
                <Icon name={entry.folder ? "folder" : "file"} size={17} style={{ color: entry.folder ? V.pri : V.sec2 }} />
                <span style={{ font: `400 13px/1.3 ${t.font}`, flex: 1, textAlign: "left" }}>{entry.name || "Unnamed item"}</span>
              </button>
              {entry.folder ? (
                <button
                  type="button"
                  className="folder-context-trigger"
                  aria-label={`Options for folder ${entry.name || 'Folder'}`}
                  style={{
                    background: "none",
                    border: "none",
                    color: V.pri,
                    padding: "8px 12px",
                    cursor: "pointer",
                    display: "flex",
                    alignItems: "center",
                  }}
                  onClick={(e) => {
                    e.stopPropagation();
                    handleContextMenu(e, entry);
                  }}
                >
                  <Icon name="more-vertical" size={16} />
                </button>
              ) : null}
            </div>
          ))
        ) : filter.trim() ? (
          <GuidedEmptyState
            icon="search"
            title="No search results"
            description={`No inventory items match "${filter.trim()}".`}
            isSearch={true}
          />
        ) : app.auth.isLoggedIn() ? (
          <GuidedEmptyState
            icon="folder-open"
            title="No inventory loaded"
            description="Your inventory is empty or has not been loaded from the grid yet."
            actionLabel="RELOAD INVENTORY"
            onAction={handleRefresh}
          />
        ) : (
          <GuidedEmptyState
            icon="folder-open"
            title="Not connected to grid"
            description="Connect to a Second Life or OpenSim grid to load and view your inventory."
            actionLabel="CONNECT TO GRID"
            onAction={() => actions.setScreen("Login")}
          />
        )}
      </div>
      {/* Floating Context Menu */}
      {contextMenu ? (
        <div
          className="inventory-context-menu"
          style={{
            position: "fixed",
            left: contextMenu.x,
            top: contextMenu.y,
            zIndex: 1000,
            background: V.surf,
            border: `1px solid ${V.pri}`,
            borderRadius: V.rs || "6px",
            boxShadow: "0 8px 24px rgba(0, 0, 0, 0.4)",
            padding: "4px 0",
            minWidth: "180px",
          }}
          onClick={(e) => e.stopPropagation()}
        >
          {contextMenu.entry.folder ? (
            <>
              <button
                type="button"
                style={{
                  width: "100%",
                  padding: "8px 14px",
                  background: "none",
                  border: "none",
                  textAlign: "left",
                  color: V.ink,
                  fontSize: "12px",
                  fontWeight: 600,
                  cursor: "pointer",
                  display: "flex",
                  alignItems: "center",
                  gap: "8px",
                }}
                onClick={() => handleRunMacro(contextMenu.entry, "replace")}
              >
                <Icon name="user-check" size={14} style={{ color: V.pri }} />
                Wear All Items
              </button>
              <button
                type="button"
                style={{
                  width: "100%",
                  padding: "8px 14px",
                  background: "none",
                  border: "none",
                  textAlign: "left",
                  color: V.ink,
                  fontSize: "12px",
                  fontWeight: 600,
                  cursor: "pointer",
                  display: "flex",
                  alignItems: "center",
                  gap: "8px",
                }}
                onClick={() => handleRunMacro(contextMenu.entry, "append")}
              >
                <Icon name="plus-circle" size={14} style={{ color: V.pri }} />
                Append All Items
              </button>
            </>
          ) : (
            <>
              <button
                type="button"
                style={{
                  width: "100%",
                  padding: "8px 14px",
                  background: "none",
                  border: "none",
                  textAlign: "left",
                  color: V.ink,
                  fontSize: "12px",
                  fontWeight: 600,
                  cursor: "pointer",
                  display: "flex",
                  alignItems: "center",
                  gap: "8px",
                }}
                onClick={() => {
                  setContextMenu(null);
                  void app.inventory.wearItem(contextMenu.entry, { append: true });
                }}
              >
                <Icon name="user-check" size={14} style={{ color: V.pri }} />
                Wear Item
              </button>
              <button
                type="button"
                aria-label="Give / Share Item"
                style={{
                  width: "100%",
                  padding: "8px 14px",
                  background: "none",
                  border: "none",
                  textAlign: "left",
                  color: V.ink,
                  fontSize: "12px",
                  fontWeight: 600,
                  cursor: "pointer",
                  display: "flex",
                  alignItems: "center",
                  gap: "8px",
                }}
                onClick={() => {
                  const entry = contextMenu.entry;
                  setContextMenu(null);
                  setGiveModalItem(entry);
                }}
              >
                <Icon name="share-2" size={14} style={{ color: V.pri }} />
                Give / Share Item
              </button>
            </>
          )}
        </div>
      ) : null}

      {/* Record detail panel */}
      {selected ? (
        <aside className="record-detail" style={{ background: V.surf, borderColor: V.outv }}>
          <button className="detail-close" onClick={() => setSelected(null)} aria-label="Close inventory details">×</button>
          <Icon name={selected.folder ? "folder" : "file"} size={22} />
          <h2>{selected.name || "Unnamed item"}</h2>
          {selected.folder ? (
            <div style={{ display: "flex", gap: "8px", margin: "12px 0 16px" }}>
              <button
                type="button"
                style={{
                  flex: 1,
                  padding: "8px 10px",
                  background: V.pri,
                  color: V.onpri || V.bg,
                  border: "none",
                  borderRadius: V.rs || "4px",
                  fontWeight: 700,
                  fontSize: "11px",
                  cursor: "pointer",
                  display: "flex",
                  alignItems: "center",
                  justifyContent: "center",
                  gap: "4px",
                }}
                onClick={() => handleRunMacro(selected, "replace")}
              >
                <Icon name="user-check" size={14} />
                Wear All Items
              </button>
              <button
                type="button"
                style={{
                  flex: 1,
                  padding: "8px 10px",
                  background: V.bg,
                  color: V.pri,
                  border: `1px solid ${V.pri}`,
                  borderRadius: V.rs || "4px",
                  fontWeight: 700,
                  fontSize: "11px",
                  cursor: "pointer",
                  display: "flex",
                  alignItems: "center",
                  justifyContent: "center",
                  gap: "4px",
                }}
                onClick={() => handleRunMacro(selected, "append")}
              >
                <Icon name="plus-circle" size={14} />
                Append All Items
              </button>
            </div>
          ) : (
            <div style={{ display: "flex", gap: "8px", margin: "12px 0 16px" }}>
              <button
                type="button"
                style={{
                  flex: 1,
                  padding: "8px 10px",
                  background: V.pri,
                  color: V.onpri || V.bg,
                  border: "none",
                  borderRadius: V.rs || "4px",
                  fontWeight: 700,
                  fontSize: "11px",
                  cursor: "pointer",
                  display: "flex",
                  alignItems: "center",
                  justifyContent: "center",
                  gap: "4px",
                }}
                onClick={() => void app.inventory.wearItem(selected, { append: true })}
              >
                <Icon name="user-check" size={14} />
                Wear Item
              </button>
              <button
                type="button"
                aria-label="Give / Share Item"
                style={{
                  flex: 1,
                  padding: "8px 10px",
                  background: V.bg,
                  color: V.pri,
                  border: `1px solid ${V.pri}`,
                  borderRadius: V.rs || "4px",
                  fontWeight: 700,
                  fontSize: "11px",
                  cursor: "pointer",
                  display: "flex",
                  alignItems: "center",
                  justifyContent: "center",
                  gap: "4px",
                }}
                onClick={() => setGiveModalItem(selected)}
              >
                <Icon name="share-2" size={14} />
                Give / Share Item
              </button>
            </div>
          )}
          <dl>
            <dt>UUID</dt>
            <dd>{selected.id}</dd>
            <dt>Type</dt>
            <dd>{selected.folder ? "Folder" : selected.assetType ?? "Unknown"}</dd>
            {selected.description ? <><dt>Description</dt><dd>{selected.description}</dd></> : null}
          </dl>
        </aside>
      ) : null}
      <GiveItemModal
        isOpen={Boolean(giveModalItem)}
        item={giveModalItem}
        onClose={() => setGiveModalItem(null)}
      />
    </section>
  );
}
