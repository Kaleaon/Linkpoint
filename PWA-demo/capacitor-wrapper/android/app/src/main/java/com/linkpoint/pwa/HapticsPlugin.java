package com.linkpoint.pwa;

import android.content.Context;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;

import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;

/**
 * Haptics Plugin for Linkpoint PWA
 * Provides vibration and haptic feedback capabilities
 */
@CapacitorPlugin(name = "Haptics")
public class HapticsPlugin extends Plugin {

    private Vibrator vibrator;

    @Override
    public void load() {
        super.load();
        vibrator = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
    }

    /**
     * Check if haptics are available
     */
    @PluginMethod
    public void isAvailable(PluginCall call) {
        boolean available = vibrator != null && vibrator.hasVibrator();

        JSObject result = new JSObject();
        result.put("available", available);
        call.resolve(result);
    }

    /**
     * Trigger haptic feedback with predefined impact
     */
    @PluginMethod
    public void impact(PluginCall call) {
        String style = call.getString("style", "medium");

        if (vibrator == null || !vibrator.hasVibrator()) {
            call.reject("Haptics not available");
            return;
        }

        long duration = getDurationForStyle(style);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            VibrationEffect effect = VibrationEffect.createOneShot(duration, VibrationEffect.DEFAULT_AMPLITUDE);
            vibrator.vibrate(effect);
        } else {
            vibrator.vibrate(duration);
        }

        JSObject result = new JSObject();
        result.put("success", true);
        call.resolve(result);
    }

    /**
     * Trigger notification haptic pattern
     */
    @PluginMethod
    public void notification(PluginCall call) {
        String type = call.getString("type", "success");

        if (vibrator == null || !vibrator.hasVibrator()) {
            call.reject("Haptics not available");
            return;
        }

        long[] pattern = getPatternForNotification(type);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            VibrationEffect effect = VibrationEffect.createWaveform(pattern, -1);
            vibrator.vibrate(effect);
        } else {
            vibrator.vibrate(pattern, -1);
        }

        JSObject result = new JSObject();
        result.put("success", true);
        call.resolve(result);
    }

    /**
     * Trigger custom vibration pattern
     */
    @PluginMethod
    public void vibrate(PluginCall call) {
        Integer duration = call.getInt("duration", 200);

        if (vibrator == null || !vibrator.hasVibrator()) {
            call.reject("Haptics not available");
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            VibrationEffect effect = VibrationEffect.createOneShot(duration, VibrationEffect.DEFAULT_AMPLITUDE);
            vibrator.vibrate(effect);
        } else {
            vibrator.vibrate(duration);
        }

        JSObject result = new JSObject();
        result.put("success", true);
        call.resolve(result);
    }

    /**
     * Trigger custom vibration pattern with timings
     */
    @PluginMethod
    public void vibratePattern(PluginCall call) {
        JSArray patternArray = call.getArray("pattern");

        if (vibrator == null || !vibrator.hasVibrator()) {
            call.reject("Haptics not available");
            return;
        }

        if (patternArray == null) {
            call.reject("Missing pattern");
            return;
        }

        try {
            long[] pattern = new long[patternArray.length()];
            for (int i = 0; i < patternArray.length(); i++) {
                pattern[i] = patternArray.getLong(i);
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                VibrationEffect effect = VibrationEffect.createWaveform(pattern, -1);
                vibrator.vibrate(effect);
            } else {
                vibrator.vibrate(pattern, -1);
            }

            JSObject result = new JSObject();
            result.put("success", true);
            call.resolve(result);
        } catch (Exception e) {
            call.reject("Invalid pattern: " + e.getMessage());
        }
    }

    /**
     * Cancel all vibrations
     */
    @PluginMethod
    public void cancel(PluginCall call) {
        if (vibrator != null) {
            vibrator.cancel();
        }

        JSObject result = new JSObject();
        result.put("success", true);
        call.resolve(result);
    }

    /**
     * Trigger selection change haptic
     */
    @PluginMethod
    public void selectionChanged(PluginCall call) {
        if (vibrator == null || !vibrator.hasVibrator()) {
            call.reject("Haptics not available");
            return;
        }

        // Light tap for selection changes
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            VibrationEffect effect = VibrationEffect.createOneShot(10, VibrationEffect.DEFAULT_AMPLITUDE);
            vibrator.vibrate(effect);
        } else {
            vibrator.vibrate(10);
        }

        JSObject result = new JSObject();
        result.put("success", true);
        call.resolve(result);
    }

    // Helper methods

    private long getDurationForStyle(String style) {
        switch (style.toLowerCase()) {
            case "light":
                return 50;
            case "medium":
                return 100;
            case "heavy":
                return 200;
            default:
                return 100;
        }
    }

    private long[] getPatternForNotification(String type) {
        switch (type.toLowerCase()) {
            case "success":
                return new long[]{0, 50, 100, 50};
            case "warning":
                return new long[]{0, 100, 100, 100};
            case "error":
                return new long[]{0, 200, 100, 200, 100, 200};
            default:
                return new long[]{0, 100};
        }
    }

    private void loadQueuesFromPreferences() {
        Map<String, ?> all = sharedPreferences.getAll();
        for (String key : all.keySet()) {
            if (key.startsWith(QUEUE_KEY_PREFIX)) {
                String tag = key.substring(QUEUE_KEY_PREFIX.length());
                String queueData = sharedPreferences.getString(key, null);
                if (queueData != null) {
                    try {
                        SyncQueue queue = SyncQueue.fromJson(tag, queueData);
                        syncQueues.put(tag, queue);
                    } catch (Exception e) {
                        // Skip invalid queues
                    }
                }
            }
        }
    }

    private void saveQueueToPreferences(SyncQueue queue) {
        try {
            String json = queue.toJson();
            sharedPreferences.edit()
                .putString(QUEUE_KEY_PREFIX + queue.tag, json)
                .apply();
        } catch (Exception e) {
            // Ignore save errors
        }
    }

    // Queue data structures

    private static class SyncQueue {
        String tag;
        List<SyncItem> items;

        SyncQueue(String tag) {
            this.tag = tag;
            this.items = new ArrayList<>();
        }

        String toJson() throws JSONException {
            JSONObject json = new JSONObject();
            json.put("tag", tag);
            
            org.json.JSONArray itemsArray = new org.json.JSONArray();
            for (SyncItem item : items) {
                JSONObject itemJson = new JSONObject();
                itemJson.put("timestamp", item.timestamp);
                itemJson.put("data", item.data);
                itemsArray.put(itemJson);
            }
            json.put("items", itemsArray);
            
            return json.toString();
        }

        static SyncQueue fromJson(String tag, String jsonString) throws JSONException {
            SyncQueue queue = new SyncQueue(tag);
            JSONObject json = new JSONObject(jsonString);
            
            org.json.JSONArray itemsArray = json.getJSONArray("items");
            for (int i = 0; i < itemsArray.length(); i++) {
                JSONObject itemJson = itemsArray.getJSONObject(i);
                long timestamp = itemJson.getLong("timestamp");
                String data = itemJson.getString("data");
                queue.items.add(new SyncItem(timestamp, data));
            }
            
            return queue;
        }
    }

    private static class SyncItem {
        long timestamp;
        String data;

        SyncItem(long timestamp, String data) {
            this.timestamp = timestamp;
            this.data = data;
        }
    }
}
