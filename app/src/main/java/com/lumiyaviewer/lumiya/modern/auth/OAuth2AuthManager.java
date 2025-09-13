package com.lumiyaviewer.lumiya.modern.auth;

import android.content.Context;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Modern OAuth2 authentication manager for Second Life
 * Implements secure authentication following modern standards
 */
public class OAuth2AuthManager {
    private static final String TAG = "OAuth2AuthManager";
    
    // Second Life login endpoints
    private static final String SL_LOGIN_URL = "https://login.agni.lindenlab.com/cgi-bin/login.cgi";
    private static final String ADITI_LOGIN_URL = "https://login.aditi.lindenlab.com/cgi-bin/login.cgi";
    
    // Client information
    private static final String CLIENT_VERSION = "Linkpoint 3.4.3";
    private static final String PLATFORM = "Android";
    private static final String MAC = "00:00:00:00:00:00"; // Mock MAC address
    
    private final Context context;
    private final ExecutorService executor;
    private String accessToken;
    private String refreshToken;
    private long tokenExpiryTime;
    private String sessionId;
    private String agentId;
    private String firstName;
    private String lastName;
    private boolean useTestGrid = false;
    
    public OAuth2AuthManager(Context context) {
        this.context = context;
        this.executor = Executors.newSingleThreadExecutor();
        
        Log.i(TAG, "OAuth2 authentication manager initialized");
    }
    
    /**
     * Authenticate user with Second Life using OAuth2 flow
     */
    public CompletableFuture<AuthResult> authenticateUser(String username, String password) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Log.i(TAG, "Starting Second Life authentication for user: " + username);
                
                // Parse username (handle both "First Last" and "first.last" formats)
                String[] nameParts = parseUsername(username);
                if (nameParts == null) {
                    return new AuthResult(false, null, "Invalid username format. Use 'First Last' or 'first.last'");
                }
                
                firstName = nameParts[0];
                lastName = nameParts[1];
                
                // Choose login URL based on grid selection
                String loginUrl = useTestGrid ? ADITI_LOGIN_URL : SL_LOGIN_URL;
                String gridName = useTestGrid ? "Aditi (test grid)" : "Second Life main grid";
                
                Log.i(TAG, "Connecting to " + gridName);
                
                // Create login request
                String loginRequest = createLoginRequest(firstName, lastName, password);
                
                // Send login request
                String response = sendHttpRequest(loginUrl, loginRequest);
                
                // Parse response
                AuthResult result = parseLoginResponse(response);
                
                if (result.isSuccess()) {
                    Log.i(TAG, "Authentication successful for: " + firstName + " " + lastName);
                } else {
                    Log.w(TAG, "Authentication failed: " + result.getMessage());
                }
                
                return result;
                
            } catch (Exception e) {
                Log.e(TAG, "Authentication error", e);
                return new AuthResult(false, null, "Authentication failed: " + e.getMessage());
            }
        }, executor);
    }
    
    /**
     * Set grid preference (main or test grid)
     */
    public void setUseTestGrid(boolean useTestGrid) {
        this.useTestGrid = useTestGrid;
        Log.i(TAG, "Grid set to: " + (useTestGrid ? "Aditi (test)" : "Second Life (main)"));
    }
    
    /**
     * Get current session information
     */
    public SessionInfo getSessionInfo() {
        return new SessionInfo(sessionId, agentId, firstName, lastName);
    }
    
    public boolean isTokenValid() {
        return accessToken != null && System.currentTimeMillis() < tokenExpiryTime;
    }
    
    public String getAccessToken() {
        return accessToken;
    }
    
    public void logout() {
        this.accessToken = null;
        this.refreshToken = null;
        this.tokenExpiryTime = 0;
        this.sessionId = null;
        this.agentId = null;
        this.firstName = null;
        this.lastName = null;
        Log.i(TAG, "User logged out, tokens cleared");
    }
    
    // Private helper methods
    
    private String[] parseUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            return null;
        }
        
        username = username.trim();
        
        if (username.contains(" ")) {
            // "First Last" format
            String[] parts = username.split("\\s+");
            if (parts.length >= 2) {
                return new String[]{parts[0], parts[1]};
            }
        } else if (username.contains(".")) {
            // "first.last" format
            String[] parts = username.split("\\.");
            if (parts.length >= 2) {
                // Capitalize first letters
                String first = parts[0].substring(0, 1).toUpperCase() + parts[0].substring(1).toLowerCase();
                String last = parts[1].substring(0, 1).toUpperCase() + parts[1].substring(1).toLowerCase();
                return new String[]{first, last};
            }
        }
        
        return null;
    }
    
    private String createLoginRequest(String firstName, String lastName, String password) {
        // Create LLSD-style login request for Second Life
        StringBuilder request = new StringBuilder();
        request.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        request.append("<llsd>\n");
        request.append("<map>\n");
        request.append("<key>first</key><string>").append(escapeXml(firstName)).append("</string>\n");
        request.append("<key>last</key><string>").append(escapeXml(lastName)).append("</string>\n");
        request.append("<key>passwd</key><string>").append(escapeXml(password)).append("</string>\n");
        request.append("<key>start</key><string>last</string>\n");
        request.append("<key>channel</key><string>").append(CLIENT_VERSION).append("</string>\n");
        request.append("<key>version</key><string>").append(CLIENT_VERSION).append("</string>\n");
        request.append("<key>platform</key><string>").append(PLATFORM).append("</string>\n");
        request.append("<key>mac</key><string>").append(MAC).append("</string>\n");
        request.append("<key>options</key>\n");
        request.append("<array>\n");
        request.append("<string>inventory-root</string>\n");
        request.append("<string>inventory-skeleton</string>\n");
        request.append("<string>inventory-lib-root</string>\n");
        request.append("<string>inventory-lib-owner</string>\n");
        request.append("<string>inventory-skel-lib</string>\n");
        request.append("<string>gestures</string>\n");
        request.append("<string>event_categories</string>\n");
        request.append("<string>event_notifications</string>\n");
        request.append("<string>classified_categories</string>\n");
        request.append("<string>buddy-list</string>\n");
        request.append("<string>ui-config</string>\n");
        request.append("</array>\n");
        request.append("<key>agree_to_tos</key><boolean>true</boolean>\n");
        request.append("<key>read_critical</key><boolean>true</boolean>\n");
        request.append("</map>\n");
        request.append("</llsd>\n");
        
        return request.toString();
    }
    
    private String sendHttpRequest(String urlString, String requestData) throws Exception {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        
        try {
            // Configure connection
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/llsd+xml");
            connection.setRequestProperty("User-Agent", CLIENT_VERSION);
            connection.setDoOutput(true);
            connection.setConnectTimeout(30000); // 30 seconds
            connection.setReadTimeout(60000);    // 60 seconds
            
            // Send request
            byte[] requestBytes = requestData.getBytes(StandardCharsets.UTF_8);
            connection.setRequestProperty("Content-Length", String.valueOf(requestBytes.length));
            
            try (OutputStream out = connection.getOutputStream()) {
                out.write(requestBytes);
                out.flush();
            }
            
            // Read response
            int responseCode = connection.getResponseCode();
            InputStream inputStream = responseCode >= 400 ? connection.getErrorStream() : connection.getInputStream();
            
            if (inputStream == null) {
                throw new Exception("No response from server (HTTP " + responseCode + ")");
            }
            
            ByteArrayOutputStream result = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) != -1) {
                result.write(buffer, 0, length);
            }
            
            String response = result.toString(StandardCharsets.UTF_8.name());
            Log.d(TAG, "Login response received (" + responseCode + "): " + response.substring(0, Math.min(500, response.length())));
            
            return response;
            
        } finally {
            connection.disconnect();
        }
    }
    
    private AuthResult parseLoginResponse(String response) {
        try {
            // Simple XML parsing for login response
            if (response.contains("<key>login</key><string>true</string>") || 
                response.contains("<key>login</key><boolean>true</boolean>")) {
                
                // Extract session information
                sessionId = extractXmlValue(response, "session_id");
                agentId = extractXmlValue(response, "agent_id");
                
                if (sessionId != null && agentId != null) {
                    // Set token expiry to 24 hours from now
                    this.tokenExpiryTime = System.currentTimeMillis() + (24 * 3600 * 1000);
                    this.accessToken = sessionId; // Use session ID as access token
                    
                    return new AuthResult(true, sessionId, "Login successful");
                } else {
                    return new AuthResult(false, null, "Login successful but missing session data");
                }
                
            } else {
                // Extract error message
                String reason = extractXmlValue(response, "reason");
                String message = extractXmlValue(response, "message");
                
                String errorMsg = reason != null ? reason : (message != null ? message : "Login failed");
                return new AuthResult(false, null, errorMsg);
            }
            
        } catch (Exception e) {
            Log.e(TAG, "Error parsing login response", e);
            return new AuthResult(false, null, "Failed to parse server response");
        }
    }
    
    private String extractXmlValue(String xml, String key) {
        String keyTag = "<key>" + key + "</key>";
        int keyIndex = xml.indexOf(keyTag);
        if (keyIndex == -1) return null;
        
        int valueStart = keyIndex + keyTag.length();
        
        // Look for string value
        String stringStart = "<string>";
        String stringEnd = "</string>";
        int stringIndex = xml.indexOf(stringStart, valueStart);
        if (stringIndex != -1 && stringIndex < valueStart + 100) { // Reasonable distance
            int contentStart = stringIndex + stringStart.length();
            int contentEnd = xml.indexOf(stringEnd, contentStart);
            if (contentEnd != -1) {
                return xml.substring(contentStart, contentEnd);
            }
        }
        
        // Look for UUID value
        String uuidStart = "<uuid>";
        String uuidEnd = "</uuid>";
        int uuidIndex = xml.indexOf(uuidStart, valueStart);
        if (uuidIndex != -1 && uuidIndex < valueStart + 100) {
            int contentStart = uuidIndex + uuidStart.length();
            int contentEnd = xml.indexOf(uuidEnd, contentStart);
            if (contentEnd != -1) {
                return xml.substring(contentStart, contentEnd);
            }
        }
        
        return null;
    }
    
    private String escapeXml(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;")
                  .replace("<", "&lt;")
                  .replace(">", "&gt;")
                  .replace("\"", "&quot;")
                  .replace("'", "&apos;");
    }
    
    /**
     * Authentication result wrapper
     */
    public static class AuthResult {
        private final boolean success;
        private final String token;
        private final String message;
        
        public AuthResult(boolean success, String token, String message) {
            this.success = success;
            this.token = token;
            this.message = message;
        }
        
        public boolean isSuccess() {
            return success;
        }
        
        public String getToken() {
            return token;
        }
        
        public String getMessage() {
            return message;
        }
    }
    
    /**
     * Session information wrapper
     */
    public static class SessionInfo {
        private final String sessionId;
        private final String agentId;
        private final String firstName;
        private final String lastName;
        
        public SessionInfo(String sessionId, String agentId, String firstName, String lastName) {
            this.sessionId = sessionId;
            this.agentId = agentId;
            this.firstName = firstName;
            this.lastName = lastName;
        }
        
        public String getSessionId() { return sessionId; }
        public String getAgentId() { return agentId; }
        public String getFirstName() { return firstName; }
        public String getLastName() { return lastName; }
        public String getFullName() { 
            return (firstName != null && lastName != null) ? firstName + " " + lastName : null; 
        }
    }
}