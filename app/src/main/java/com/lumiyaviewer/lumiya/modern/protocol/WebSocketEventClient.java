package com.lumiyaviewer.lumiya.modern.protocol;

import android.util.Log;
import android.os.Handler;
import android.os.Looper;
import okhttp3.*;
import okio.ByteString;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    
    // Reconnection management
    private final AtomicInteger reconnectAttempts = new AtomicInteger(0);
    private static final int MAX_RECONNECT_ATTEMPTS = 5;
    private String lastConnectionUrl;
    private final Handler reconnectHandler = new Handler(Looper.getMainLooper());
    
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
        this.lastConnectionUrl = eventQueueUrl;
        this.reconnectAttempts.set(0); // Reset reconnect attempts on manual connect
        
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
     * Send message through WebSocket connection
     */
    public boolean sendMessage(String message) {
        if (webSocket == null || !connected) {
            Log.w(TAG, "Cannot send message: WebSocket not connected");
            return false;
        }
        
        try {
            return webSocket.send(message);
        } catch (Exception e) {
            Log.e(TAG, "Failed to send message", e);
            return false;
        }
    }
    
    /**
     * Send binary message through WebSocket connection
     */
    public boolean sendBinaryMessage(byte[] data) {
        if (webSocket == null || !connected) {
            Log.w(TAG, "Cannot send binary message: WebSocket not connected");
            return false;
        }
        
        try {
            return webSocket.send(ByteString.of(data));
        } catch (Exception e) {
            Log.e(TAG, "Failed to send binary message", e);
            return false;
        }
    }
    
    // WebSocketListener implementation
    
    @Override
    public void onOpen(WebSocket webSocket, Response response) {
        Log.i(TAG, "WebSocket connected: " + response.message());
        connected = true;
        
        // Reset reconnection attempts on successful connection
        reconnectAttempts.set(0);
        
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
        if (lastConnectionUrl == null) {
            Log.w(TAG, "No last connection URL available for reconnection");
            return;
        }
        
        int currentAttempt = reconnectAttempts.incrementAndGet();
        if (currentAttempt > MAX_RECONNECT_ATTEMPTS) {
            Log.e(TAG, "Maximum reconnection attempts reached (" + MAX_RECONNECT_ATTEMPTS + ")");
            return;
        }
        
        // Exponential backoff: 1s, 2s, 4s, 8s, 16s
        long delaySeconds = (long) Math.pow(2, currentAttempt - 1);
        
        Log.i(TAG, "Scheduling reconnection attempt " + currentAttempt + " in " + delaySeconds + " seconds");
        
        // Use Handler to schedule the reconnection
        reconnectHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.i(TAG, "Attempting reconnection " + currentAttempt + "/" + MAX_RECONNECT_ATTEMPTS);
                connect(lastConnectionUrl);
            }
        }, delaySeconds * 1000); // Convert to milliseconds
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
         * Supports Second Life event queue format
         */
        public static EventMessage parseFromJson(String json) {
            try {
                // Simple JSON parsing for Second Life event format
                // Expected format: {"message": "type:data", "timestamp": 123456}
                String type = "unknown";
                String data = json;
                long timestamp = System.currentTimeMillis();
                
                // Extract message type from common SL event patterns
                if (json.contains("\"message\"")) {
                    Pattern messagePattern = Pattern.compile("\"message\"\\s*:\\s*\"([^\"]+)\"");
                    Matcher matcher = messagePattern.matcher(json);
                    if (matcher.find()) {
                        String messageContent = matcher.group(1);
                        if (messageContent.contains(":")) {
                            String[] parts = messageContent.split(":", 2);
                            type = parts[0].trim();
                            data = parts[1].trim();
                        } else {
                            data = messageContent;
                        }
                    }
                }
                
                // Extract timestamp if present
                Pattern timestampPattern = Pattern.compile("\"timestamp\"\\s*:\\s*(\\d+)");
                Matcher timestampMatcher = timestampPattern.matcher(json);
                if (timestampMatcher.find()) {
                    timestamp = Long.parseLong(timestampMatcher.group(1));
                }
                
                // Identify common Second Life event types
                if (json.toLowerCase().contains("chat") || json.toLowerCase().contains("im")) {
                    type = "chat";
                } else if (json.toLowerCase().contains("objectupdate")) {
                    type = "objectUpdate";
                } else if (json.toLowerCase().contains("agent")) {
                    type = "agentUpdate";
                }
                
                return new EventMessage(type, data, timestamp);
                
            } catch (Exception e) {
                Log.w(TAG, "Failed to parse JSON event message: " + json, e);
                return new EventMessage("parse_error", json, System.currentTimeMillis());
            }
        }
        
        /**
         * Parse event message from binary data
         * Handles Second Life UDP-style binary messages
         */
        public static EventMessage parseFromBytes(byte[] bytes) {
            try {
                if (bytes == null || bytes.length == 0) {
                    return new EventMessage("empty", "", System.currentTimeMillis());
                }
                
                // Second Life binary messages often start with message type flags
                String type = "binary";
                String data = "";
                
                // Check for common Second Life binary message patterns
                if (bytes.length > 4) {
                    // First 4 bytes often contain message type information
                    int messageType = ((bytes[0] & 0xFF) << 24) | 
                                    ((bytes[1] & 0xFF) << 16) |
                                    ((bytes[2] & 0xFF) << 8) | 
                                    (bytes[3] & 0xFF);
                    
                    // Map common Second Life message types
                    switch (messageType & 0xFF) {
                        case 0x01:
                            type = "objectUpdate";
                            break;
                        case 0x02:
                            type = "agentMovement";
                            break;
                        case 0x03:
                            type = "chatMessage";
                            break;
                        default:
                            type = "binary_" + Integer.toHexString(messageType & 0xFF);
                    }
                }
                
                // Convert bytes to hex string for debugging
                StringBuilder hexString = new StringBuilder();
                for (int i = 0; i < Math.min(bytes.length, 32); i++) { // Limit to first 32 bytes
                    hexString.append(String.format("%02X ", bytes[i]));
                }
                data = "Binary data (" + bytes.length + " bytes): " + hexString.toString();
                
                return new EventMessage(type, data, System.currentTimeMillis());
                
            } catch (Exception e) {
                Log.w(TAG, "Failed to parse binary event message", e);
                return new EventMessage("binary_error", "Failed to parse " + bytes.length + " bytes", 
                                      System.currentTimeMillis());
            }
        }
    }
    
    public void shutdown() {
        disconnect();
        client.dispatcher().executorService().shutdown();
        client.connectionPool().evictAll();
    }
}