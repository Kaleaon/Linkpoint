package com.lumiyaviewer.lumiya.modern.protocol;

import android.util.Log;

import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Map;
import java.util.HashMap;

/**
 * Hybrid transport layer combining HTTP/2, WebSocket, and UDP
 * Based on Second Life Integration Guide modernization plans
 */
public class HybridSLTransport {
    private static final String TAG = "HybridSLTransport";
    
    private final HTTP2CapsClient capsClient;        // Modern CAPS using HTTP/2
    private final WebSocketEventClient eventClient;  // Real-time events
    private final MessageRouter router;
    
    public HybridSLTransport() {
        this.capsClient = new HTTP2CapsClient();
        this.eventClient = new WebSocketEventClient();
        this.router = new MessageRouter();
        
        Log.i(TAG, "Hybrid transport layer initialized");
    }
    
    /**
     * Set authentication token for all transport layers
     */
    public void setAuthToken(String token) {
        capsClient.setAuthToken(token);
        eventClient.setAuthToken(token);
        Log.d(TAG, "Auth token configured for all transports");
    }
    
    /**
     * Initialize connections based on authentication data
     */
    public void initialize(String eventQueueUrl, String seedCapability) {
        try {
            // Configure HTTP/2 CAPS client
            if (seedCapability != null) {
                Log.i(TAG, "Configuring CAPS client with seed capability");
                Map<String, String> capsUrls = parseSeedCapability(seedCapability);
                capsClient.configureCapabilities(capsUrls);
            }
            
            // Configure WebSocket event client
            if (eventQueueUrl != null) {
                Log.i(TAG, "Connecting to event queue: " + eventQueueUrl);
                eventClient.connect(eventQueueUrl);
            }
            
            Log.i(TAG, "Hybrid transport initialized successfully");
            
        } catch (Exception e) {
            Log.e(TAG, "Failed to initialize hybrid transport", e);
        }
    }
    
    /**
     * Send message using optimal transport route
     */
    public CompletableFuture<SLResponse> sendMessage(ModernMessage message) {
        TransportRoute route = router.selectOptimalRoute(message);
        
        Log.d(TAG, "Routing message via " + route.getTransport() + ": " + message.getClass().getSimpleName());
        
        switch (route.getTransport()) {
            case HTTP2_CAPS:
                // Use HTTP/2 for large data transfers, asset uploads
                return capsClient.sendAsync(route.getUrl(), message.toLLSDXML())
                    .thenApply(this::parseHTTP2Response);
                    
            case WEBSOCKET_REALTIME:
                // Use WebSocket for chat, object updates, real-time events
                return sendViaWebSocket(message);
                    
            case UDP_LEGACY:
                // Legacy UDP not available in modern-only build
                return CompletableFuture.failedFuture(
                    new UnsupportedOperationException("UDP transport not available"));
                    
            default:
                return CompletableFuture.failedFuture(
                    new UnsupportedOperationException("Unknown transport: " + route));
        }
    }
    
    /**
     * Send message via WebSocket (async simulation)
     */
    private CompletableFuture<SLResponse> sendViaWebSocket(ModernMessage message) {
        CompletableFuture<SLResponse> future = new CompletableFuture<>();
        
        try {
            if (!eventClient.isConnected()) {
                future.completeExceptionally(new IllegalStateException("WebSocket not connected"));
                return future;
            }
            
            // Convert message to WebSocket format
            String jsonMessage = message.toJSON();
            Log.d(TAG, "Sending WebSocket message: " + jsonMessage.substring(0, Math.min(100, jsonMessage.length())));
            
            // Send via WebSocket (OkHttp WebSocket send returns boolean)
            boolean sent = eventClient.sendMessage(jsonMessage);
            
            if (sent) {
                // Simulate immediate acknowledgment for real-time messages
                future.complete(new SLResponse("websocket_ack", "Message sent successfully"));
            } else {
                future.completeExceptionally(new RuntimeException("Failed to send WebSocket message"));
            }
            
        } catch (Exception e) {
            Log.e(TAG, "Error sending WebSocket message", e);
            future.completeExceptionally(e);
        }
        
        return future;
    }
    
    /**
     * Parse Second Life seed capability response
     * Extracts capability URLs from LLSD response
     */
    private Map<String, String> parseSeedCapability(String seedCapability) {
        Map<String, String> capabilities = new HashMap<>();
        
        try {
            // Parse LLSD-XML format seed capability
            // Example format: <map><key>EventQueueGet</key><string>http://...</string></map>
            
            // Common Second Life capabilities to extract
            String[] capabilityNames = {
                "EventQueueGet", "ChatSessionRequest", "SendChatMessage",
                "UploadBakedTexture", "FetchInventory", "GetMesh", 
                "GetTexture", "AgentPreferences", "UpdateAgentInformation"
            };
            
            for (String capName : capabilityNames) {
                // Pattern to match LLSD capability entries
                // <key>CapabilityName</key><string>URL</string>
                Pattern capPattern = Pattern.compile(
                    "<key>" + Pattern.quote(capName) + "</key>\\s*<string>([^<]+)</string>",
                    Pattern.CASE_INSENSITIVE
                );
                
                Matcher matcher = capPattern.matcher(seedCapability);
                if (matcher.find()) {
                    String capUrl = matcher.group(1).trim();
                    capabilities.put(capName, capUrl);
                    Log.d(TAG, "Parsed capability: " + capName + " -> " + capUrl);
                }
            }
            
            // Also try simplified key:value parsing for JSON-like formats
            if (capabilities.isEmpty() && seedCapability.contains(":")) {
                Pattern jsonPattern = Pattern.compile("\"([^\"]+)\"\\s*:\\s*\"([^\"]+)\"");
                Matcher jsonMatcher = jsonPattern.matcher(seedCapability);
                
                while (jsonMatcher.find()) {
                    String key = jsonMatcher.group(1);
                    String value = jsonMatcher.group(2);
                    
                    // Only store known capability names
                    for (String capName : capabilityNames) {
                        if (key.equalsIgnoreCase(capName)) {
                            capabilities.put(capName, value);
                            Log.d(TAG, "Parsed JSON capability: " + key + " -> " + value);
                            break;
                        }
                    }
                }
            }
            
            Log.i(TAG, "Parsed " + capabilities.size() + " capabilities from seed");
            
        } catch (Exception e) {
            Log.e(TAG, "Failed to parse seed capability", e);
        }
        
        return capabilities;
    }
    
    /**
     * Parse HTTP/2 CAPS response
     * Handles LLSD-XML format responses common in Second Life
     */
    private SLResponse parseHTTP2Response(String responseData) {
        try {
            // Basic LLSD-XML parsing for Second Life responses
            String responseType = "caps_response";
            String parsedData = responseData;
            
            if (responseData != null) {
                // Extract common LLSD response patterns
                if (responseData.contains("<map>")) {
                    responseType = "llsd_map";
                    
                    // Extract key-value pairs from LLSD map
                    Pattern mapPattern = Pattern.compile("<key>([^<]+)</key>\\s*<(string|integer|real|boolean)>([^<]*)</\\2>");
                    Matcher matcher = mapPattern.matcher(responseData);
                    
                    StringBuilder parsed = new StringBuilder("LLSD Map: ");
                    while (matcher.find()) {
                        String key = matcher.group(1);
                        String type = matcher.group(2);
                        String value = matcher.group(3);
                        parsed.append(key).append("=").append(value).append(" ");
                    }
                    
                    if (parsed.length() > "LLSD Map: ".length()) {
                        parsedData = parsed.toString();
                    }
                    
                } else if (responseData.contains("<array>")) {
                    responseType = "llsd_array";
                    parsedData = "LLSD Array with " + responseData.split("<").length + " elements";
                    
                } else if (responseData.contains("<string>")) {
                    // Simple string response
                    Pattern stringPattern = Pattern.compile("<string>([^<]*)</string>");
                    Matcher stringMatcher = stringPattern.matcher(responseData);
                    if (stringMatcher.find()) {
                        parsedData = stringMatcher.group(1);
                        responseType = "llsd_string";
                    }
                }
                
                // Check for common error responses
                if (responseData.toLowerCase().contains("error") || 
                    responseData.toLowerCase().contains("fail")) {
                    responseType = "error_response";
                }
            }
            
            Log.d(TAG, "Parsed CAPS response type: " + responseType);
            return new SLResponse(responseType, parsedData);
            
        } catch (Exception e) {
            Log.e(TAG, "Failed to parse HTTP/2 response", e);
            return new SLResponse("parse_error", "Failed to parse response: " + e.getMessage());
        }
    }
    
    /**
     * Modern message base class
     */
    public static abstract class ModernMessage {
        protected final String type;
        protected final long timestamp;
        
        public ModernMessage(String type) {
            this.type = type;
            this.timestamp = System.currentTimeMillis();
        }
        
        public String getType() {
            return type;
        }
        
        public long getTimestamp() {
            return timestamp;
        }
        
        /**
         * Convert message to LLSD XML format
         */
        public abstract String toLLSDXML();
        
        /**
         * Convert message to JSON format for WebSocket transport
         */
        public String toJSON() {
            return String.format("{\"type\":\"%s\",\"timestamp\":%d,\"data\":%s}", 
                               type, timestamp, getMessageDataJSON());
        }
        
        /**
         * Get message-specific data in JSON format
         * Override in subclasses to provide specific data
         */
        protected String getMessageDataJSON() {
            return "{}";
        }
    }
    
    /**
     * Subscribe to real-time events
     */
    public void subscribeToEvents(String eventType, WebSocketEventClient.EventListener listener) {
        eventClient.subscribe(eventType, listener);
        Log.d(TAG, "Subscribed to event type: " + eventType);
    }
    
    /**
     * Upload asset with progress tracking
     */
    public CompletableFuture<String> uploadAsset(byte[] assetData, String contentType, 
                                                HTTP2CapsClient.ProgressListener progressListener) {
        // TODO: Get actual upload URL from CAPS
        String uploadUrl = "https://example.com/upload"; // Placeholder
        return capsClient.uploadAssetAsync(uploadUrl, assetData, contentType, progressListener);
    }
    
    /**
     * Check connection status
     */
    public boolean isConnected() {
        return eventClient.isConnected(); // Basic connectivity check
    }
    
    /**
     * Shutdown all transport layers
     */
    public void shutdown() {
        Log.i(TAG, "Shutting down hybrid transport");
        capsClient.shutdown();
        eventClient.shutdown();
    }
    
    /**
     * Message routing logic
     */
    private static class MessageRouter {
        
        public TransportRoute selectOptimalRoute(ModernMessage message) {
            // Basic routing logic - can be enhanced based on message type
            String messageType = message.getClass().getSimpleName();
            
            // Route asset-related messages via HTTP/2 CAPS
            if (messageType.contains("Asset") || messageType.contains("Upload") || 
                messageType.contains("Texture") || messageType.contains("Inventory")) {
                return new TransportRoute(TransportType.HTTP2_CAPS, "https://example.com/caps");
            }
            
            // Route real-time messages via WebSocket
            if (messageType.contains("Chat") || messageType.contains("ObjectUpdate") || 
                messageType.contains("Avatar") || messageType.contains("Position")) {
                return new TransportRoute(TransportType.WEBSOCKET_REALTIME, null);
            }
            
            // Default to HTTP/2 for modern build
            return new TransportRoute(TransportType.HTTP2_CAPS, "https://example.com/caps");
        }
    }
    
    /**
     * Transport route descriptor
     */
    private static class TransportRoute {
        private final TransportType transport;
        private final String url;
        
        public TransportRoute(TransportType transport, String url) {
            this.transport = transport;
            this.url = url;
        }
        
        public TransportType getTransport() {
            return transport;
        }
        
        public String getUrl() {
            return url;
        }
    }
    
    /**
     * Transport type enumeration
     */
    private enum TransportType {
        HTTP2_CAPS,
        WEBSOCKET_REALTIME,
        UDP_LEGACY
    }
    
    /**
     * Generic response wrapper
     */
    public static class SLResponse {
        private final String type;
        private final String data;
        
        public SLResponse(String type, String data) {
            this.type = type;
            this.data = data;
        }
        
        public String getType() {
            return type;
        }
        
        public String getData() {
            return data;
        }
    }
}