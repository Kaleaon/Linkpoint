package com.lumiyaviewer.lumiya.modern.protocol;

import android.util.Log;
import okhttp3.*;
import okio.ByteString;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

/**
 * WebSocket-based event client for real-time Second Life events
 * Implements modern event streaming as described in the documentation
 */
public class WebSocketEventClient extends WebSocketListener {
    private static final String TAG = "WebSocketEventClient";
    
    private final OkHttpClient client;
    private final ConcurrentHashMap<String, CopyOnWriteArrayList<EventListener>> eventListeners = new ConcurrentHashMap<>();
    private WebSocket webSocket;
    private volatile boolean connected = false;
    private String authToken;
    
    public WebSocketEventClient() {
        this.client = new OkHttpClient.Builder()
            .pingInterval(30, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .build();
    }
    
    public void setAuthToken(String token) {
        this.authToken = token;
    }
    
    /**
     * Connect to Second Life event queue via WebSocket
     */
    public void connect(String eventQueueUrl) {
        if (webSocket != null) {
            webSocket.close(1000, "Reconnecting");
        }
        
        Request.Builder requestBuilder = new Request.Builder()
            .url(eventQueueUrl);
            
        if (authToken != null) {
            requestBuilder.addHeader("Authorization", "Bearer " + authToken);
        }
        
        Request request = requestBuilder.build();
        webSocket = client.newWebSocket(request, this);
        
        Log.i(TAG, "Connecting to event queue: " + eventQueueUrl);
    }
    
    /**
     * Disconnect from event queue
     */
    public void disconnect() {
        if (webSocket != null) {
            webSocket.close(1000, "Normal closure");
            webSocket = null;
        }
        connected = false;
    }
    
    /**
     * Subscribe to a specific event type
     */
    public void subscribe(String eventType, EventListener listener) {
        eventListeners.computeIfAbsent(eventType, k -> new CopyOnWriteArrayList<>()).add(listener);
        
        // Send subscription message if connected
        if (connected && webSocket != null) {
            String subscriptionMessage = String.format(
                "{\"action\":\"subscribe\",\"eventType\":\"%s\"}", 
                eventType
            );
            webSocket.send(subscriptionMessage);
            Log.d(TAG, "Subscribed to event type: " + eventType);
        }
    }
    
    /**
     * Unsubscribe from an event type
     */
    public void unsubscribe(String eventType, EventListener listener) {
        CopyOnWriteArrayList<EventListener> listeners = eventListeners.get(eventType);
        if (listeners != null) {
            listeners.remove(listener);
            if (listeners.isEmpty()) {
                eventListeners.remove(eventType);
                
                // Send unsubscription message if connected
                if (connected && webSocket != null) {
                    String unsubscribeMessage = String.format(
                        "{\"action\":\"unsubscribe\",\"eventType\":\"%s\"}", 
                        eventType
                    );
                    webSocket.send(unsubscribeMessage);
                    Log.d(TAG, "Unsubscribed from event type: " + eventType);
                }
            }
        }
    }
    
    /**
     * Send a message through the WebSocket connection
     */
    public java.util.concurrent.CompletableFuture<Boolean> sendMessage(String messageType, String payload) {
        return java.util.concurrent.CompletableFuture.supplyAsync(() -> {
            if (connected && webSocket != null) {
                try {
                    String message = String.format(
                        "{\"type\":\"%s\",\"payload\":%s}", 
                        messageType, 
                        payload
                    );
                    boolean success = webSocket.send(message);
                    Log.d(TAG, "Sent message: " + messageType + " (success: " + success + ")");
                    return success;
                } catch (Exception e) {
                    Log.e(TAG, "Failed to send message: " + messageType, e);
                    return false;
                }
            } else {
                Log.w(TAG, "Cannot send message - WebSocket not connected");
                return false;
            }
        });
    }
    
    // WebSocketListener implementation
    
    @Override
    public void onOpen(WebSocket webSocket, Response response) {
        Log.i(TAG, "WebSocket connected: " + response.message());
        connected = true;
        
        // Re-subscribe to all event types
        for (String eventType : eventListeners.keySet()) {
            String subscriptionMessage = String.format(
                "{\"action\":\"subscribe\",\"eventType\":\"%s\"}", 
                eventType
            );
            webSocket.send(subscriptionMessage);
        }
    }
    
    @Override
    public void onMessage(WebSocket webSocket, String text) {
        Log.d(TAG, "Received text message: " + text.substring(0, Math.min(100, text.length())));
        
        try {
            EventMessage event = EventMessage.parseFromJson(text);
            dispatchEvent(event);
        } catch (Exception e) {
            Log.e(TAG, "Failed to parse text event message", e);
        }
    }
    
    @Override
    public void onMessage(WebSocket webSocket, ByteString bytes) {
        Log.d(TAG, "Received binary message: " + bytes.size() + " bytes");
        
        try {
            EventMessage event = EventMessage.parseFromBytes(bytes.toByteArray());
            dispatchEvent(event);
        } catch (Exception e) {
            Log.e(TAG, "Failed to parse binary event message", e);
        }
    }
    
    @Override
    public void onClosing(WebSocket webSocket, int code, String reason) {
        Log.i(TAG, "WebSocket closing: " + reason);
        connected = false;
    }
    
    @Override
    public void onClosed(WebSocket webSocket, int code, String reason) {
        Log.i(TAG, "WebSocket closed: " + reason);
        connected = false;
    }
    
    @Override
    public void onFailure(WebSocket webSocket, Throwable t, Response response) {
        Log.e(TAG, "WebSocket failure", t);
        connected = false;
        
        // Attempt reconnection after delay
        scheduleReconnect();
    }
    
    /**
     * Dispatch event to registered listeners
     */
    private void dispatchEvent(EventMessage event) {
        CopyOnWriteArrayList<EventListener> listeners = eventListeners.get(event.getType());
        if (listeners != null) {
            for (EventListener listener : listeners) {
                try {
                    listener.onEvent(event);
                } catch (Exception e) {
                    Log.e(TAG, "Error in event listener", e);
                }
            }
        }
    }
    
    /**
     * Schedule reconnection with exponential backoff
     */
    private void scheduleReconnect() {
        // TODO: Implement exponential backoff reconnection
        Log.w(TAG, "TODO: Implement reconnection logic");
    }
    
    public boolean isConnected() {
        return connected;
    }
    
    /**
     * Event listener interface
     */
    public interface EventListener {
        void onEvent(EventMessage event);
    }
    
    /**
     * Event message wrapper
     */
    public static class EventMessage {
        private final String type;
        private final String data;
        private final long timestamp;
        
        public EventMessage(String type, String data, long timestamp) {
            this.type = type;
            this.data = data;
            this.timestamp = timestamp;
        }
        
        public String getType() {
            return type;
        }
        
        public String getData() {
            return data;
        }
        
        public long getTimestamp() {
            return timestamp;
        }
        
        /**
         * Parse event message from JSON text
         */
        public static EventMessage parseFromJson(String json) {
            // TODO: Implement proper JSON parsing
            // For now, return a placeholder
            return new EventMessage("chat", json, System.currentTimeMillis());
        }
        
        /**
         * Parse event message from binary data
         */
        public static EventMessage parseFromBytes(byte[] bytes) {
            // TODO: Implement proper binary message parsing
            // For now, return a placeholder
            return new EventMessage("binary", new String(bytes), System.currentTimeMillis());
        }
    }
    
    public void shutdown() {
        disconnect();
        client.dispatcher().executorService().shutdown();
        client.connectionPool().evictAll();
    }
}