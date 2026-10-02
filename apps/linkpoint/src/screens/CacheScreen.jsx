import React, { useState, useEffect, useRef } from "react";
import { useTheme } from "../context/ThemeContext.jsx";
import { useApp } from "../context/AppContext.jsx";
import { app } from "../linkpoint/app";
import { localCache } from "../linkpoint/local-cache";
import Icon from "../components/Icon.jsx";

export default function CacheScreen() {
  const { V, t } = useTheme();
  const { state, actions } = useApp();
  const fileInputRef = useRef(null);

  const [stats, setStats] = useState({
    locationType: localCache.locationType,
    locationName: "Internal Browser Storage",
    inventoryFolders: 0,
    inventoryItems: 0,
    texturesCount: 0,
    totalSizeMb: 0,
    lastUpdated: null,
    flashdriveReady: false,
  });

  const [locType, setLocType] = useState(localCache.locationType);
  const [customPath, setCustomPath] = useState(localCache.customFlashdrivePath);
  const [busy, setBusy] = useState(false);
  const [message, setMessage] = useState("");
  const [error, setError] = useState("");

  const agentId = app.auth.user?.id || "current";

  const refreshStats = async () => {
    try {
      const s = await localCache.getStats(agentId);
      setStats(s);
    } catch (e) {
      console.warn("Failed to get cache stats:", e);
    }
  };

  useEffect(() => {
    refreshStats();
    const handleUpdate = () => refreshStats();
    localCache.on("cache_updated", handleUpdate);
    localCache.on("cache_cleared", handleUpdate);
    localCache.on("cache_location_changed", handleUpdate);
    return () => {
      localCache.off("cache_updated", handleUpdate);
      localCache.off("cache_cleared", handleUpdate);
      localCache.off("cache_location_changed", handleUpdate);
    };
  }, [agentId]);

  const handleSelectLocation = (type) => {
    setLocType(type);
    localCache.setLocation(type, customPath);
    actions.notify(`Cache location set to: ${type === "flashdrive_path" ? "Flashdrive Path" : type === "flashdrive_fs" ? "Flashdrive Directory" : "Internal"}`);
    refreshStats();
  };

  const handlePickDirectory = async () => {
    setBusy(true);
    setError("");
    setMessage("");
    try {
      const res = await localCache.pickFlashdriveDirectory();
      if (res.success) {
        setLocType("flashdrive_fs");
        setMessage(`Selected flashdrive folder: ${res.name}`);
        actions.notify(`Flashdrive folder mounted: ${res.name}`);
        await refreshStats();
      } else if (res.error) {
        setError(res.error);
      }
    } catch (err) {
      setError(err instanceof Error ? err.message : "Failed to select folder");
    } finally {
      setBusy(false);
    }
  };

  const handleSaveCustomPath = () => {
    localCache.setLocation("flashdrive_path", customPath);
    setLocType("flashdrive_path");
    actions.notify(`Flashdrive custom path saved: ${customPath}`);
    setMessage(`Flashdrive path configured: ${customPath}`);
    refreshStats();
  };

  const handleSaveInventoryNow = async () => {
    setBusy(true);
    setError("");
    setMessage("");
    try {
      const foldersArr = Array.from(app.inventory.folders.values());
      const itemsArr = Array.from(app.inventory.items.values());

      if (foldersArr.length === 0) {
        throw new Error("No inventory loaded to save. Connect to Second Life first.");
      }

      await localCache.saveInventory(agentId, {
        folders: foldersArr,
        items: itemsArr,
        rootId: app.inventory.rootFolder?.id,
        rootName: app.inventory.rootFolder?.name,
      });

      setMessage(`Saved ${foldersArr.length} folders and ${itemsArr.length} items to flashdrive cache!`);
      actions.notify(`Saved ${foldersArr.length} folders to cache`);
      await refreshStats();
    } catch (err) {
      setError(err instanceof Error ? err.message : "Save failed");
    } finally {
      setBusy(false);
    }
  };

  const handleLoadFromCacheNow = async () => {
    setBusy(true);
    setError("");
    setMessage("");
    try {
      await app.inventory.load(false);
      setMessage(`Loaded inventory from cache without network rebuild!`);
      actions.notify("Inventory loaded from flashdrive cache");
      await refreshStats();
    } catch (err) {
      setError(err instanceof Error ? err.message : "Load failed");
    } finally {
      setBusy(false);
    }
  };

  const handleRebuildFromGrid = async () => {
    setBusy(true);
    setError("");
    setMessage("");
    try {
      await app.inventory.load(true);
      setMessage("Inventory rebuild completed from Second Life grid!");
      actions.notify("Inventory re-fetched from Second Life grid");
      await refreshStats();
    } catch (err) {
      setError(err instanceof Error ? err.message : "Rebuild failed");
    } finally {
      setBusy(false);
    }
  };

  const handleExportBundle = async () => {
    setBusy(true);
    try {
      const blob = await localCache.exportCacheBundle(agentId);
      const url = URL.createObjectURL(blob);
      const a = document.createElement("a");
      a.href = url;
      a.download = `secondlife-cache-${agentId.slice(0, 8)}.json`;
      document.body.appendChild(a);
      a.click();
      document.body.removeChild(a);
      URL.revokeObjectURL(url);
      setMessage("Cache export initiated. Save file to your flashdrive.");
      actions.notify("Cache bundle exported to file");
    } catch (err) {
      setError(err instanceof Error ? err.message : "Export failed");
    } finally {
      setBusy(false);
    }
  };

  const handleImportFile = async (e) => {
    const file = e.target.files?.[0];
    if (!file) return;
    setBusy(true);
    setError("");
    try {
      const res = await localCache.importCacheBundle(file);
      if (res.success) {
        setMessage(`Successfully imported ${res.foldersCount} folders from flashdrive!`);
        actions.notify(`Imported ${res.foldersCount} folders from flashdrive`);
        await app.inventory.load(false);
        await refreshStats();
      } else {
        setError(res.error || "Import failed");
      }
    } catch (err) {
      setError(err instanceof Error ? err.message : "Import failed");
    } finally {
      setBusy(false);
      if (fileInputRef.current) fileInputRef.current.value = "";
    }
  };

  const handleClearCache = async () => {
    if (!confirm("Are you sure you want to clear the local/flashdrive cache?")) return;
    setBusy(true);
    try {
      await localCache.clearCache(agentId);
      setMessage("Cache cleared. Next startup will refetch from Second Life.");
      actions.notify("Cache cleared");
      await refreshStats();
    } catch (err) {
      setError(err instanceof Error ? err.message : "Clear failed");
    } finally {
      setBusy(false);
    }
  };

  const cardStyle = {
    background: V.surf,
    border: `1px solid ${V.outv}`,
    borderRadius: V.rs,
    padding: 16,
    display: "grid",
    gap: 12,
  };

  const btnStyle = (primary = false) => ({
    minHeight: 40,
    padding: "0 16px",
    background: primary ? V.pri : V.surf2,
    color: primary ? V.onpri : V.ink,
    borderWidth: primary ? 0 : 1, borderStyle: "solid", borderColor: V.outv,
    borderRadius: V.rs,
    fontWeight: 700,
    fontSize: 12,
    letterSpacing: ".1em",
    cursor: busy ? "wait" : "pointer",
    display: "inline-flex",
    alignItems: "center",
    justifyContent: "center",
    gap: 8,
  });

  return (
    <div style={{ flex: 1, minHeight: 0, overflowY: "auto", padding: "16px", display: "grid", gap: 16, maxWidth: 680, margin: "0 auto" }}>
      {/* Header Banner */}
      <div style={{ display: "flex", alignItems: "center", justifyContent: "space-between", borderBottom: `1px solid ${V.outv}`, paddingBottom: 12 }}>
        <div>
          <h2 style={{ margin: 0, color: V.pri, font: `700 20px/1.2 ${t.dfont}`, letterSpacing: ".08em", display: "flex", alignItems: "center", gap: 8 }}>
            <Icon name="hard-drive" size={22} />
            CACHE & FLASHDRIVE STORAGE
          </h2>
          <div style={{ fontSize: 12, color: V.ink2, marginTop: 4 }}>
            Store inventory skeletons, textures, and assets on a removable flashdrive to eliminate rebuild wait times.
          </div>
        </div>
        <button type="button" onClick={refreshStats} style={btnStyle(false)}>
          <Icon name="refresh-cw" size={14} /> REFRESH
        </button>
      </div>

      {message && (
        <div style={{ background: V.priC, color: V.pri, border: `1px solid ${V.pri}`, borderRadius: V.rs, padding: 12, fontSize: 12, fontWeight: 600 }}>
          ✓ {message}
        </div>
      )}

      {error && (
        <div style={{ background: "rgba(255,108,108,0.1)", color: V.err, border: `1px solid ${V.err}`, borderRadius: V.rs, padding: 12, fontSize: 12, fontWeight: 600 }}>
          ⚠ {error}
        </div>
      )}

      {/* Live Cache Status Card */}
      <div style={cardStyle}>
        <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center" }}>
          <div style={{ color: V.pri, fontWeight: 700, fontSize: 12, letterSpacing: ".12em" }}>ACTIVE CACHE STATUS</div>
          <span style={{ fontSize: 11, color: V.ink2 }}>{stats.locationName}</span>
        </div>

        <div style={{ display: "grid", gridTemplateColumns: "repeat(auto-fit, minmax(130px, 1fr))", gap: 10 }}>
          <div style={{ background: V.surf2, padding: 10, borderRadius: V.rs, textAlign: "center" }}>
            <div style={{ fontSize: 20, fontWeight: 700, color: V.pri }}>{stats.inventoryFolders.toLocaleString()}</div>
            <div style={{ fontSize: 10, color: V.ink2, letterSpacing: ".06em" }}>FOLDERS CACHED</div>
          </div>
          <div style={{ background: V.surf2, padding: 10, borderRadius: V.rs, textAlign: "center" }}>
            <div style={{ fontSize: 20, fontWeight: 700, color: V.pri }}>{stats.inventoryItems.toLocaleString()}</div>
            <div style={{ fontSize: 10, color: V.ink2, letterSpacing: ".06em" }}>ITEMS CACHED</div>
          </div>
          <div style={{ background: V.surf2, padding: 10, borderRadius: V.rs, textAlign: "center" }}>
            <div style={{ fontSize: 20, fontWeight: 700, color: V.pri }}>{stats.texturesCount.toLocaleString()}</div>
            <div style={{ fontSize: 10, color: V.ink2, letterSpacing: ".06em" }}>TEXTURES</div>
          </div>
          <div style={{ background: V.surf2, padding: 10, borderRadius: V.rs, textAlign: "center" }}>
            <div style={{ fontSize: 20, fontWeight: 700, color: V.pri }}>{stats.totalSizeMb} MB</div>
            <div style={{ fontSize: 10, color: V.ink2, letterSpacing: ".06em" }}>CACHE SIZE</div>
          </div>
        </div>

        <div style={{ display: "flex", alignItems: "center", gap: 8, fontSize: 11, color: V.ink2, marginTop: 4 }}>
          <Icon name="check-circle" size={14} />
          <span>
            {stats.inventoryFolders > 0
              ? `Cache ready — ${stats.inventoryFolders} Second Life folders will load instantly on startup.`
              : "No cache yet — inventory will be saved automatically upon next fetch."}
          </span>
        </div>
      </div>

      {/* Cache Location Selection Card */}
      <div style={cardStyle}>
        <div style={{ color: V.pri, fontWeight: 700, fontSize: 12, letterSpacing: ".12em" }}>SELECT CACHE LOCATION</div>
        <p style={{ margin: 0, fontSize: 12, color: V.ink2, lineHeight: 1.4 }}>
          Choose where Second Life data is written so you can carry your viewer cache on a USB stick or external drive.
        </p>

        <div style={{ display: "grid", gap: 10 }}>
          {/* Option 1: Web Directory Picker (Flashdrive) */}
          <label style={{ display: "flex", alignItems: "flex-start", gap: 10, padding: 10, border: `1px solid ${locType === "flashdrive_fs" ? V.pri : V.outv}`, borderRadius: V.rs, background: locType === "flashdrive_fs" ? V.priC : "transparent", cursor: "pointer" }}>
            <input
              type="radio"
              name="cacheLoc"
              checked={locType === "flashdrive_fs"}
              onChange={() => handleSelectLocation("flashdrive_fs")}
              style={{ marginTop: 3 }}
            />
            <div style={{ flex: 1 }}>
              <div style={{ fontWeight: 700, fontSize: 13, color: V.ink, display: "flex", alignItems: "center", gap: 6 }}>
                <Icon name="folder-open" size={16} /> Removable Flashdrive / USB Folder
              </div>
              <div style={{ fontSize: 11, color: V.ink2, marginTop: 2 }}>
                Direct file access to a connected USB thumb drive or SD card via the File System Access API.
              </div>
              <button
                type="button"
                onClick={handlePickDirectory}
                disabled={busy}
                style={{ ...btnStyle(false), marginTop: 8, minHeight: 32, fontSize: 11 }}
              >
                <Icon name="disc" size={14} /> SELECT FLASHDRIVE FOLDER…
              </button>
            </div>
          </label>

          {/* Option 2: Custom Directory Path (Server / Desktop Flashdrive) */}
          <label style={{ display: "flex", alignItems: "flex-start", gap: 10, padding: 10, border: `1px solid ${locType === "flashdrive_path" ? V.pri : V.outv}`, borderRadius: V.rs, background: locType === "flashdrive_path" ? V.priC : "transparent", cursor: "pointer" }}>
            <input
              type="radio"
              name="cacheLoc"
              checked={locType === "flashdrive_path"}
              onChange={() => handleSelectLocation("flashdrive_path")}
              style={{ marginTop: 3 }}
            />
            <div style={{ flex: 1 }}>
              <div style={{ fontWeight: 700, fontSize: 13, color: V.ink, display: "flex", alignItems: "center", gap: 6 }}>
                <Icon name="hard-drive" size={16} /> Flashdrive Mount Path / Custom Directory
              </div>
              <div style={{ fontSize: 11, color: V.ink2, marginTop: 2 }}>
                Specify an exact mount point or drive letter (e.g. <code>E:\SL_Cache</code> or <code>/media/usb/sl-cache</code>).
              </div>
              <div style={{ display: "flex", gap: 8, marginTop: 8 }}>
                <input
                  type="text"
                  value={customPath}
                  onChange={(e) => setCustomPath(e.target.value)}
                  placeholder="/media/usb/sl-cache"
                  style={{
                    flex: 1,
                    minHeight: 34,
                    padding: "0 10px",
                    border: `1px solid ${V.outv}`,
                    borderRadius: V.rs,
                    background: V.bg,
                    color: V.ink,
                    font: `400 13px ${t.font}`,
                  }}
                />
                <button type="button" onClick={handleSaveCustomPath} style={{ ...btnStyle(false), minHeight: 34, fontSize: 11 }}>
                  SAVE PATH
                </button>
              </div>
            </div>
          </label>

          {/* Option 3: Internal Browser Storage */}
          <label style={{ display: "flex", alignItems: "flex-start", gap: 10, padding: 10, border: `1px solid ${locType === "internal" ? V.pri : V.outv}`, borderRadius: V.rs, background: locType === "internal" ? V.priC : "transparent", cursor: "pointer" }}>
            <input
              type="radio"
              name="cacheLoc"
              checked={locType === "internal"}
              onChange={() => handleSelectLocation("internal")}
              style={{ marginTop: 3 }}
            />
            <div style={{ flex: 1 }}>
              <div style={{ fontWeight: 700, fontSize: 13, color: V.ink, display: "flex", alignItems: "center", gap: 6 }}>
                <Icon name="database" size={16} /> Internal Storage (Local IndexedDB &amp; Cache)
              </div>
              <div style={{ fontSize: 11, color: V.ink2, marginTop: 2 }}>
                Standard persistent browser &amp; backend cache on host disk.
              </div>
            </div>
          </label>
        </div>
      </div>

      {/* Flashdrive Operations Card */}
      <div style={cardStyle}>
        <div style={{ color: V.pri, fontWeight: 700, fontSize: 12, letterSpacing: ".12em" }}>FLASHDRIVE ACTIONS &amp; REBUILD</div>

        <div style={{ display: "grid", gridTemplateColumns: "repeat(auto-fit, minmax(200px, 1fr))", gap: 10 }}>
          <button type="button" onClick={handleSaveInventoryNow} disabled={busy} style={btnStyle(true)}>
            <Icon name="save" size={16} /> SAVE INVENTORY TO CACHE
          </button>
          <button type="button" onClick={handleLoadFromCacheNow} disabled={busy} style={btnStyle(false)}>
            <Icon name="folder-check" size={16} /> LOAD FROM CACHE NOW
          </button>
          <button type="button" onClick={handleExportBundle} disabled={busy} style={btnStyle(false)}>
            <Icon name="download" size={16} /> EXPORT TO FLASHDRIVE (.JSON)
          </button>
          <button type="button" onClick={() => fileInputRef.current?.click()} disabled={busy} style={btnStyle(false)}>
            <Icon name="upload" size={16} /> IMPORT FROM FLASHDRIVE…
          </button>
          <input
            type="file"
            ref={fileInputRef}
            onChange={handleImportFile}
            accept=".json"
            style={{ display: "none" }}
          />
        </div>

        <div style={{ display: "flex", gap: 10, marginTop: 6 }}>
          <button type="button" onClick={handleRebuildFromGrid} disabled={busy} style={{ ...btnStyle(false), flex: 1, borderColor: V.pri, color: V.pri }}>
            <Icon name="refresh-cw" size={14} /> REBUILD FROM GRID (FORCE)
          </button>
          <button type="button" onClick={handleClearCache} disabled={busy} style={{ ...btnStyle(false), flex: 1, color: V.err, borderColor: V.err }}>
            <Icon name="trash-2" size={14} /> CLEAR CACHE
          </button>
        </div>
      </div>
    </div>
  );
}
