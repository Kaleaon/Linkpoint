package com.linkpoint.pwa;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;

import com.getcapacitor.JSArray;
import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Background Sync Plugin for Linkpoint PWA
 * Manages offline data synchronization with queue management
 */
@CapacitorPlugin(name = "BackgroundSync")
public class BackgroundSyncPlugin extends Plugin {

    private static final String PREFS_NAME = "linkpoint_sync_queue";
    private static final String QUEUE_KEY_PREFIX = "queue_";
    private static final int MAX_QUEUE_SIZE = 1000;

    private SharedPreferences sharedPreferences;
    private ExecutorService executorService;
    private Handler mainHandler;
    private Map<String, SyncQueue> syncQueues;

    @Override
    public void load() {
        super.load();
        sharedPreferences = getContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        executorService = Executors.newSingleThreadExecutor();
        mainHandler = new Handler(Looper.getMainLooper());
        syncQueues = new HashMap<>();
        loadQueuesFromPreferences();
    }

    /**
     * Register a sync queue
     */
    @PluginMethod
    public void register(PluginCall call) {
        String tag = call.getString("tag");

        if (tag == null) {
            call.reject("Missing tag");
            return;
        }

        if (!syncQueues.containsKey(tag)) {
            SyncQueue queue = new SyncQueue(tag);
            syncQueues.put(tag, queue);
            saveQueueToPreferences(queue);
        }

        JSObject result = new JSObject();
        result.put("success", true);
        result.put("tag", tag);
        call.resolve(result);
    }

    /**
     * Add item to sync queue
     */
    @PluginMethod
    public void enqueue(PluginCall call) {
        String tag = call.getString("tag");
        JSObject data = call.getObject("data");

        if (tag == null || data == null) {
            call.reject("Missing tag or data");
            return;
        }

        SyncQueue queue = syncQueues.get(tag);
        if (queue == null) {
            call.reject("Queue not registered: " + tag);
            return;
        }

        if (queue.items.size() >= MAX_QUEUE_SIZE) {
            call.reject("Queue is full");
            return;
        }

        SyncItem item = new SyncItem(System.currentTimeMillis(), data.toString());
        queue.items.add(item);
        saveQueueToPreferences(queue);

        JSObject result = new JSObject();
        result.put("success", true);
        result.put("queueSize", queue.items.size());
        call.resolve(result);
    }

    /**
     * Get all items from queue
     */
    @PluginMethod
    public void getQueue(PluginCall call) {
        String tag = call.getString("tag");

        if (tag == null) {
            call.reject("Missing tag");
            return;
        }

        SyncQueue queue = syncQueues.get(tag);
        if (queue == null) {
            call.reject("Queue not registered: " + tag);
            return;
        }

        try {
            JSArray items = new JSArray();
            for (SyncItem item : queue.items) {
                JSObject itemObj = new JSObject(item.data);
                itemObj.put("_timestamp", item.timestamp);
                items.put(itemObj);
            }

            JSObject result = new JSObject();
            result.put("items", items);
            result.put("queueSize", queue.items.size());
            result.put("success", true);
            call.resolve(result);
        } catch (JSONException e) {
            call.reject("Failed to get queue: " + e.getMessage());
        }
    }

    /**
     * Clear specific items from queue
     */
    @PluginMethod
    public void dequeue(PluginCall call) {
        String tag = call.getString("tag");
        Integer count = call.getInt("count", 1);

        if (tag == null) {
            call.reject("Missing tag");
            return;
        }

        SyncQueue queue = syncQueues.get(tag);
        if (queue == null) {
            call.reject("Queue not registered: " + tag);
            return;
        }

        int removed = 0;
        while (removed < count && !queue.items.isEmpty()) {
            queue.items.remove(0);
            removed++;
        }

        saveQueueToPreferences(queue);

        JSObject result = new JSObject();
        result.put("success", true);
        result.put("removed", removed);
        result.put("remaining", queue.items.size());
        call.resolve(result);
    }

    /**
     * Clear entire queue
     */
    @PluginMethod
    public void clearQueue(PluginCall call) {
        String tag = call.getString("tag");

        if (tag == null) {
            call.reject("Missing tag");
            return;
        }

        SyncQueue queue = syncQueues.get(tag);
        if (queue == null) {
            call.reject("Queue not registered: " + tag);
            return;
        }

        queue.items.clear();
        saveQueueToPreferences(queue);

        JSObject result = new JSObject();
        result.put("success", true);
        call.resolve(result);
    }

    /**
     * Get queue size
     */
    @PluginMethod
    public void getQueueSize(PluginCall call) {
        String tag = call.getString("tag");

        if (tag == null) {
            call.reject("Missing tag");
            return;
        }

        SyncQueue queue = syncQueues.get(tag);
        if (queue == null) {
            call.reject("Queue not registered: " + tag);
            return;
        }

        JSObject result = new JSObject();
        result.put("size", queue.items.size());
        result.put("success", true);
        call.resolve(result);
    }

    /**
     * Trigger sync for a queue
     */
    @PluginMethod
    public void sync(PluginCall call) {
        String tag = call.getString("tag");

        if (tag == null) {
            call.reject("Missing tag");
            return;
        }

        SyncQueue queue = syncQueues.get(tag);
        if (queue == null) {
            call.reject("Queue not registered: " + tag);
            return;
        }

        // Notify JavaScript to process the queue
        JSObject syncEvent = new JSObject();
        syncEvent.put("tag", tag);
        syncEvent.put("queueSize", queue.items.size());
        notifyListeners("syncTriggered", syncEvent);

        JSObject result = new JSObject();
        result.put("success", true);
        result.put("tag", tag);
        call.resolve(result);
    }

    // Helper methods

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

    // Sync Queue data structures

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
