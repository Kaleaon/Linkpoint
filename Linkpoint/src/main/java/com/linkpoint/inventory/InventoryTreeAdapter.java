package com.linkpoint.inventory;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Inventory tree adapter with an integrated collapsible HUD quick action bar for gestures.
 */
public class InventoryTreeAdapter {
    private static final String TAG = "InventoryTreeAdapter";

    private static void logI(String tag, String msg) {
        try {
            Log.i(tag, msg);
        } catch (Throwable t) {
            System.out.println("[" + tag + "] INFO: " + msg);
        }
    }

    private static void logW(String tag, String msg) {
        try {
            Log.w(tag, msg);
        } catch (Throwable t) {
            System.out.println("[" + tag + "] WARN: " + msg);
        }
    }

    private static void logD(String tag, String msg) {
        try {
            Log.d(tag, msg);
        } catch (Throwable t) {
            System.out.println("[" + tag + "] DEBUG: " + msg);
        }
    }

    public static class InventoryNode {
        public final String id;
        public final String parentId;
        public final String name;
        public final boolean isFolder;
        public final String assetId;
        public final int itemType;
        public final List<InventoryNode> children = new ArrayList<>();

        public InventoryNode(String id, String parentId, String name, boolean isFolder, String assetId, int itemType) {
            this.id = id;
            this.parentId = parentId;
            this.name = name;
            this.isFolder = isFolder;
            this.assetId = assetId;
            this.itemType = itemType;
        }
    }

    public static class ShortcutSlot {
        public final int slot;
        public final String gestureId;
        public final String name;
        public final boolean isCacheReady;

        public ShortcutSlot(int slot, String gestureId, String name, boolean isCacheReady) {
            this.slot = slot;
            this.gestureId = gestureId;
            this.name = name;
            this.isCacheReady = isCacheReady;
        }
    }

    public interface OnShortcutClickListener {
        void onShortcutClick(int slot, String gestureId);
    }

    public interface OnGestureTapListener {
        void onGestureTap(String gestureId, String name);
    }

    public interface AssetCacheProvider {
        boolean isAssetCached(String assetId);
    }

    private final Context context;
    private final GestureTriggerEngine gestureTriggerEngine;
    private AssetCacheProvider cacheProvider;

    // HUD Quick Action Bar State
    private boolean isCollapsed = false;
    private final Map<Integer, ShortcutSlot> favoriteShortcuts = new ConcurrentHashMap<>();
    private OnShortcutClickListener shortcutClickListener;
    private OnGestureTapListener gestureTapListener;

    // Inventory Tree Navigation State
    private final InventoryNode rootNode;
    private final Map<String, InventoryNode> nodeMap = new ConcurrentHashMap<>();
    private final Set<String> expandedFolderIds = Collections.synchronizedSet(new HashSet<String>());
    private final List<InventoryNode> visibleNodes = new ArrayList<>();

    // Threading for Non-Blocking Region Teleport Cache Resolution
    private final ExecutorService asyncExecutor = Executors.newSingleThreadExecutor();
    private Handler mainHandler;
    private volatile boolean isRegionTeleporting = false;

    public InventoryTreeAdapter(Context context, GestureTriggerEngine triggerEngine) {
        this.context = context;
        this.gestureTriggerEngine = (triggerEngine != null) ? triggerEngine : new GestureTriggerEngine();
        this.rootNode = new InventoryNode("root", null, "My Inventory", true, null, 0);
        nodeMap.put("root", rootNode);
        expandedFolderIds.add("root");

        try {
            if (Looper.getMainLooper() != null) {
                this.mainHandler = new Handler(Looper.getMainLooper());
            }
        } catch (Throwable ignored) {
            this.mainHandler = null;
        }

        rebuildVisibleNodes();
    }

    public void setAssetCacheProvider(AssetCacheProvider provider) {
        this.cacheProvider = provider;
    }

    public void setOnShortcutClickListener(OnShortcutClickListener listener) {
        this.shortcutClickListener = listener;
    }

    public void setOnGestureTapListener(OnGestureTapListener listener) {
        this.gestureTapListener = listener;
    }

    // -------------------------------------------------------------------------
    // HUD Quick Action Gesture Bar API
    // -------------------------------------------------------------------------

    public boolean isBarCollapsed() {
        return isCollapsed;
    }

    public void setBarCollapsed(boolean collapsed) {
        this.isCollapsed = collapsed;
        logI(TAG, "HUD Quick Action Bar collapsed: " + collapsed);
    }

    public void toggleBarCollapsed() {
        setBarCollapsed(!isCollapsed);
    }

    public boolean addFavoriteGestureShortcut(int slot, String gestureId, String name) {
        if (slot < 0 || gestureId == null || gestureId.isEmpty()) {
            return false;
        }

        boolean cached = checkAssetCachedAsync(gestureId);
        ShortcutSlot shortcutSlot = new ShortcutSlot(slot, gestureId, name, cached);
        favoriteShortcuts.put(slot, shortcutSlot);

        // Register in native/managed engine
        gestureTriggerEngine.registerShortcut(slot, gestureId, name);
        logI(TAG, "Added favorite gesture shortcut slot " + slot + " (" + name + ")");
        return true;
    }

    public boolean removeFavoriteGestureShortcut(int slot) {
        boolean removed = favoriteShortcuts.remove(slot) != null;
        if (removed) {
            gestureTriggerEngine.unregisterShortcut(slot);
        }
        return removed;
    }

    public List<ShortcutSlot> getFavoriteGestureShortcuts() {
        List<ShortcutSlot> list = new ArrayList<>();
        for (int slot = 0; slot < 8; slot++) { // Up to 8 quick action slots
            ShortcutSlot s = favoriteShortcuts.get(slot);
            if (s != null) {
                // Verify cache status without blocking UI
                boolean isCached = !isRegionTeleporting && checkAssetCachedAsync(s.gestureId);
                list.add(new ShortcutSlot(s.slot, s.gestureId, s.name, isCached));
            } else {
                // Empty placeholder slot
                list.add(new ShortcutSlot(slot, null, "Empty", false));
            }
        }
        return list;
    }

    public void handleShortcutTap(int slot) {
        ShortcutSlot slotData = favoriteShortcuts.get(slot);
        if (slotData == null || slotData.gestureId == null) {
            logD(TAG, "Tapped empty shortcut slot " + slot);
            return;
        }

        long startTime = System.currentTimeMillis();
        if (shortcutClickListener != null) {
            shortcutClickListener.onShortcutClick(slot, slotData.gestureId);
        }

        if (gestureTapListener != null) {
            gestureTapListener.onGestureTap(slotData.gestureId, slotData.name);
        }

        // Trigger gesture execution via GestureTriggerEngine (<1s target)
        boolean executed = gestureTriggerEngine.triggerShortcut(slot);
        long duration = System.currentTimeMillis() - startTime;
        logI(TAG, "Shortcut tap executed slot " + slot + " in " + duration + " ms, status: " + executed);
    }

    // -------------------------------------------------------------------------
    // Teleport & Missing Asset Cache Edge Case Handling
    // -------------------------------------------------------------------------

    public void onRegionTeleportStarted() {
        this.isRegionTeleporting = true;
        logI(TAG, "Region teleport started: rendering empty/placeholder shortcut slots non-blockingly");
    }

    public void onRegionTeleportCompleted() {
        asyncExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(50);
                } catch (InterruptedException ignored) {}

                isRegionTeleporting = false;
                Runnable callback = new Runnable() {
                    @Override
                    public void run() {
                        logI(TAG, "Region teleport completed: restored quick action bar shortcut states");
                    }
                };

                if (mainHandler != null) {
                    mainHandler.post(callback);
                } else {
                    callback.run();
                }
            }
        });
    }

    private boolean checkAssetCachedAsync(String assetId) {
        if (isRegionTeleporting) {
            return false; // Show empty slot during teleports
        }
        if (cacheProvider != null) {
            try {
                return cacheProvider.isAssetCached(assetId);
            } catch (Exception e) {
                logW(TAG, "Asset cache check failed for " + assetId);
                return false;
            }
        }
        return true; // Default ready if no cache restriction
    }

    // -------------------------------------------------------------------------
    // Deep Inventory Folder Tree Navigation (Preserved)
    // -------------------------------------------------------------------------

    public InventoryNode addNode(String id, String parentId, String name, boolean isFolder, String assetId, int itemType) {
        InventoryNode parent = nodeMap.get(parentId != null ? parentId : "root");
        if (parent == null) {
            parent = rootNode;
        }

        InventoryNode node = new InventoryNode(id, parent.id, name, isFolder, assetId, itemType);
        parent.children.add(node);
        nodeMap.put(id, node);

        rebuildVisibleNodes();
        return node;
    }

    public void expandFolder(String folderId) {
        expandedFolderIds.add(folderId);
        rebuildVisibleNodes();
    }

    public void collapseFolder(String folderId) {
        expandedFolderIds.remove(folderId);
        rebuildVisibleNodes();
    }

    public boolean isFolderExpanded(String folderId) {
        return expandedFolderIds.contains(folderId);
    }

    public List<InventoryNode> getVisibleNodes() {
        return new ArrayList<>(visibleNodes);
    }

    public int getItemCount() {
        return visibleNodes.size();
    }

    public InventoryNode getItemAt(int position) {
        if (position >= 0 && position < visibleNodes.size()) {
            return visibleNodes.get(position);
        }
        return null;
    }

    private synchronized void rebuildVisibleNodes() {
        visibleNodes.clear();
        traverseAndAddVisible(rootNode);
    }

    private void traverseAndAddVisible(InventoryNode node) {
        if (!"root".equals(node.id)) {
            visibleNodes.add(node);
        }
        if (node.isFolder && expandedFolderIds.contains(node.id)) {
            for (InventoryNode child : node.children) {
                traverseAndAddVisible(child);
            }
        }
    }

    public void shutdown() {
        asyncExecutor.shutdown();
    }
}
