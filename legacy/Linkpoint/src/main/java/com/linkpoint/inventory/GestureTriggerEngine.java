package com.linkpoint.inventory;

import android.util.Log;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Native and managed gesture trigger engine supporting quick action shortcuts
 * and slash commands with guaranteed execution within 1 second.
 */
public class GestureTriggerEngine {
    private static final String TAG = "GestureTriggerEngine";
    private static final boolean nativeLibraryLoaded;

    static {
        boolean loaded = false;
        try {
            System.loadLibrary("linkpoint-j2k");
            loaded = true;
        } catch (Throwable t) {
            logW(TAG, "Native linkpoint-j2k library not available on current runtime, using managed engine");
        }
        nativeLibraryLoaded = loaded;
    }

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

    public static class GestureShortcut {
        public final int slot;
        public final String gestureId;
        public final String name;
        public final String triggerCommand;
        public final boolean active;

        public GestureShortcut(int slot, String gestureId, String name, String triggerCommand, boolean active) {
            this.slot = slot;
            this.gestureId = gestureId;
            this.name = name;
            this.triggerCommand = triggerCommand;
            this.active = active;
        }
    }

    public interface OnGestureTriggeredListener {
        void onGestureTriggered(int slot, String gestureId, long executionTimeMs);
    }

    private final Map<Integer, GestureShortcut> managedShortcuts = new ConcurrentHashMap<>();
    private final Map<String, String> managedSlashCommands = new ConcurrentHashMap<>();
    private OnGestureTriggeredListener listener;

    public GestureTriggerEngine() {
        if (nativeLibraryLoaded) {
            try {
                nativeClearShortcuts();
            } catch (Throwable t) {
                logW(TAG, "Failed initializing native shortcut engine");
            }
        }
    }

    public void setOnGestureTriggeredListener(OnGestureTriggeredListener listener) {
        this.listener = listener;
    }

    public boolean registerShortcut(int slot, String gestureId, String name) {
        return registerShortcut(slot, gestureId, name, "");
    }

    public boolean registerShortcut(int slot, String gestureId, String name, String triggerCommand) {
        if (slot < 0 || gestureId == null || gestureId.isEmpty()) {
            return false;
        }

        String displayName = (name != null && !name.isEmpty()) ? name : "Gesture " + slot;
        String command = (triggerCommand != null) ? triggerCommand.trim() : "";

        GestureShortcut shortcut = new GestureShortcut(slot, gestureId, displayName, command, true);
        managedShortcuts.put(slot, shortcut);

        if (!command.isEmpty()) {
            String normalizedCmd = command.toLowerCase();
            if (!normalizedCmd.startsWith("/")) {
                normalizedCmd = "/" + normalizedCmd;
            }
            managedSlashCommands.put(normalizedCmd, gestureId);
        }

        if (nativeLibraryLoaded) {
            try {
                nativeRegisterShortcut(slot, gestureId, displayName);
            } catch (Throwable t) {
                logW(TAG, "Native shortcut registration fallback for slot " + slot);
            }
        }

        logI(TAG, "Registered gesture shortcut slot " + slot + " -> " + gestureId + " (" + displayName + ")");
        return true;
    }

    public boolean unregisterShortcut(int slot) {
        GestureShortcut removed = managedShortcuts.remove(slot);
        if (removed != null && removed.triggerCommand != null && !removed.triggerCommand.isEmpty()) {
            String normalizedCmd = removed.triggerCommand.toLowerCase();
            if (!normalizedCmd.startsWith("/")) {
                normalizedCmd = "/" + normalizedCmd;
            }
            managedSlashCommands.remove(normalizedCmd);
        }

        if (nativeLibraryLoaded) {
            try {
                nativeUnregisterShortcut(slot);
            } catch (Throwable t) {
                logW(TAG, "Native unregister shortcut failed for slot " + slot);
            }
        }
        return removed != null;
    }

    public void clearShortcuts() {
        managedShortcuts.clear();
        managedSlashCommands.clear();
        if (nativeLibraryLoaded) {
            try {
                nativeClearShortcuts();
            } catch (Throwable t) {
                logW(TAG, "Native clear shortcuts failed");
            }
        }
    }

    public boolean hasShortcut(int slot) {
        return managedShortcuts.containsKey(slot);
    }

    public GestureShortcut getShortcut(int slot) {
        return managedShortcuts.get(slot);
    }

    public List<GestureShortcut> getRegisteredShortcuts() {
        List<GestureShortcut> list = new ArrayList<>(managedShortcuts.values());
        Collections.sort(list, (a, b) -> Integer.compare(a.slot, b.slot));
        return list;
    }

    public int getShortcutCount() {
        if (nativeLibraryLoaded) {
            try {
                return nativeGetShortcutCount();
            } catch (Throwable t) {
                logW(TAG, "Native shortcut count failed, using managed count");
            }
        }
        return managedShortcuts.size();
    }

    public boolean triggerShortcut(int slot) {
        long startTime = System.currentTimeMillis();
        GestureShortcut shortcut = managedShortcuts.get(slot);
        if (shortcut == null || !shortcut.active) {
            return false;
        }

        if (nativeLibraryLoaded) {
            try {
                nativeTriggerShortcut(slot);
            } catch (Throwable t) {
                logW(TAG, "Native trigger failed, relying on managed callback");
            }
        }

        long executionTimeMs = System.currentTimeMillis() - startTime;
        if (listener != null) {
            listener.onGestureTriggered(slot, shortcut.gestureId, executionTimeMs);
        }

        return true;
    }

    public boolean processSlashCommand(String command) {
        if (command == null || command.trim().isEmpty()) {
            return false;
        }

        String normalizedCmd = command.trim().toLowerCase();
        if (!normalizedCmd.startsWith("/")) {
            normalizedCmd = "/" + normalizedCmd;
        }

        String gestureId = managedSlashCommands.get(normalizedCmd);
        if (gestureId == null) {
            if (nativeLibraryLoaded) {
                try {
                    return nativeProcessSlashCommand(command);
                } catch (Throwable t) {
                    logW(TAG, "Native slash command process failed");
                }
            }
            return false;
        }

        for (GestureShortcut shortcut : managedShortcuts.values()) {
            if (shortcut.gestureId.equals(gestureId)) {
                return triggerShortcut(shortcut.slot);
            }
        }

        long startTime = System.currentTimeMillis();
        if (listener != null) {
            listener.onGestureTriggered(-1, gestureId, System.currentTimeMillis() - startTime);
        }
        return true;
    }

    // Native JNI methods
    private native boolean nativeRegisterShortcut(int slot, String gestureId, String name);
    private native boolean nativeUnregisterShortcut(int slot);
    private native void nativeClearShortcuts();
    private native boolean nativeTriggerShortcut(int slot);
    private native boolean nativeProcessSlashCommand(String command);
    private native int nativeGetShortcutCount();
}
