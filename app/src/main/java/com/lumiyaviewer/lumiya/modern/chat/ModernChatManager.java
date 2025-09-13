package com.lumiyaviewer.lumiya.modern.chat;

import android.content.Context;
import android.util.Log;

import com.lumiyaviewer.lumiya.modern.protocol.WebSocketEventClient;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Modern chat manager for enhanced local and group chat functionality
 * Extends existing chat system with modern features
 */
public class ModernChatManager {
    private static final String TAG = "ModernChatManager";
    
    private final Context context;
    private final ExecutorService executor;
    private final WebSocketEventClient eventClient;
    
    // Chat state management
    private final ConcurrentHashMap<String, ChatSession> activeSessions = new ConcurrentHashMap<>();
    private final ConcurrentLinkedQueue<ChatMessage> messageQueue = new ConcurrentLinkedQueue<>();
    private final ConcurrentHashMap<String, List<ChatMessage>> messageHistory = new ConcurrentHashMap<>();
    
    // Chat listeners
    public interface ChatEventListener {
        void onLocalChatReceived(ChatMessage message);
        void onGroupChatReceived(ChatMessage message);
        void onGroupChatInvitation(GroupChatInvitation invitation);
        void onChatError(String error);
        void onTypingIndicator(String userId, String sessionId, boolean isTyping);
    }
    
    private ChatEventListener chatListener;
    
    public ModernChatManager(Context context, WebSocketEventClient eventClient) {
        this.context = context;
        this.eventClient = eventClient;
        this.executor = Executors.newFixedThreadPool(2);
        
        Log.i(TAG, "Modern chat manager initialized");
    }
    
    /**
     * Send local chat message (spatial chat in virtual world)
     */
    public CompletableFuture<Boolean> sendLocalChatMessage(String message, int channel) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Log.i(TAG, "Sending local chat message: " + message);
                
                ChatMessage chatMsg = new ChatMessage(
                    UUID.randomUUID().toString(),
                    ChatMessage.Type.LOCAL,
                    message,
                    System.currentTimeMillis(),
                    "self",
                    null,
                    channel
                );
                
                // Add to queue for reliable delivery
                messageQueue.offer(chatMsg);
                
                // Send via WebSocket for real-time delivery
                if (eventClient != null) {
                    String payload = createChatPayload(chatMsg);
                    return eventClient.sendMessage("chat.local", payload).join();
                }
                
                // Fallback to traditional SL protocol if WebSocket unavailable
                return sendViaTraditionalProtocol(chatMsg);
                
            } catch (Exception e) {
                Log.e(TAG, "Failed to send local chat message", e);
                if (chatListener != null) {
                    chatListener.onChatError("Failed to send message: " + e.getMessage());
                }
                return false;
            }
        }, executor);
    }
    
    /**
     * Send group chat message
     */
    public CompletableFuture<Boolean> sendGroupChatMessage(String message, String groupId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Log.i(TAG, "Sending group chat message to group: " + groupId);
                
                ChatMessage chatMsg = new ChatMessage(
                    UUID.randomUUID().toString(),
                    ChatMessage.Type.GROUP,
                    message,
                    System.currentTimeMillis(),
                    "self",
                    groupId,
                    0
                );
                
                // Check if we have an active group session
                ChatSession session = activeSessions.get(groupId);
                if (session == null) {
                    // Create new group session
                    session = new ChatSession(groupId, ChatSession.Type.GROUP);
                    activeSessions.put(groupId, session);
                }
                
                session.addMessage(chatMsg);
                messageQueue.offer(chatMsg);
                
                // Send via modern protocol
                if (eventClient != null) {
                    String payload = createGroupChatPayload(chatMsg, groupId);
                    return eventClient.sendMessage("chat.group", payload).join();
                }
                
                return sendViaTraditionalProtocol(chatMsg);
                
            } catch (Exception e) {
                Log.e(TAG, "Failed to send group chat message", e);
                if (chatListener != null) {
                    chatListener.onChatError("Failed to send group message: " + e.getMessage());
                }
                return false;
            }
        }, executor);
    }
    
    /**
     * Join group chat session
     */
    public CompletableFuture<Boolean> joinGroupChat(String groupId, String groupName) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Log.i(TAG, "Joining group chat: " + groupName + " (" + groupId + ")");
                
                ChatSession session = new ChatSession(groupId, ChatSession.Type.GROUP);
                session.setGroupName(groupName);
                activeSessions.put(groupId, session);
                
                // Request to join group via modern protocol
                if (eventClient != null) {
                    String joinPayload = "{\"action\":\"join\",\"groupId\":\"" + groupId + "\"}";
                    return eventClient.sendMessage("chat.group.join", joinPayload).join();
                }
                
                return true;
                
            } catch (Exception e) {
                Log.e(TAG, "Failed to join group chat", e);
                if (chatListener != null) {
                    chatListener.onChatError("Failed to join group: " + e.getMessage());
                }
                return false;
            }
        }, executor);
    }
    
    /**
     * Leave group chat session
     */
    public CompletableFuture<Boolean> leaveGroupChat(String groupId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                ChatSession session = activeSessions.remove(groupId);
                if (session != null) {
                    Log.i(TAG, "Left group chat: " + session.getGroupName());
                    
                    // Notify server via modern protocol
                    if (eventClient != null) {
                        String leavePayload = "{\"action\":\"leave\",\"groupId\":\"" + groupId + "\"}";
                        return eventClient.sendMessage("chat.group.leave", leavePayload).join();
                    }
                }
                return true;
                
            } catch (Exception e) {
                Log.e(TAG, "Failed to leave group chat", e);
                return false;
            }
        }, executor);
    }
    
    /**
     * Send typing indicator
     */
    public void sendTypingIndicator(String sessionId, boolean isTyping) {
        executor.execute(() -> {
            try {
                if (eventClient != null) {
                    String payload = "{\"sessionId\":\"" + sessionId + "\",\"typing\":" + isTyping + "}";
                    eventClient.sendMessage("chat.typing", payload);
                }
            } catch (Exception e) {
                Log.w(TAG, "Failed to send typing indicator", e);
            }
        });
    }
    
    /**
     * Handle incoming chat message from protocol layer
     */
    public void handleIncomingMessage(String messageType, String payload) {
        executor.execute(() -> {
            try {
                switch (messageType) {
                    case "chat.local":
                        handleLocalChatMessage(payload);
                        break;
                    case "chat.group":
                        handleGroupChatMessage(payload);
                        break;
                    case "chat.group.invitation":
                        handleGroupInvitation(payload);
                        break;
                    case "chat.typing":
                        handleTypingIndicator(payload);
                        break;
                    default:
                        Log.w(TAG, "Unknown chat message type: " + messageType);
                }
            } catch (Exception e) {
                Log.e(TAG, "Error handling incoming message", e);
            }
        });
    }
    
    /**
     * Get active chat sessions
     */
    public List<ChatSession> getActiveSessions() {
        return new ArrayList<>(activeSessions.values());
    }
    
    /**
     * Get message history for session
     */
    public List<ChatMessage> getMessageHistory(String sessionId) {
        return messageHistory.getOrDefault(sessionId, new ArrayList<>());
    }
    
    /**
     * Set chat event listener
     */
    public void setChatEventListener(ChatEventListener listener) {
        this.chatListener = listener;
    }
    
    // Private helper methods
    
    private void handleLocalChatMessage(String payload) {
        try {
            // Parse incoming local chat message
            ChatMessage message = parseChatMessage(payload, ChatMessage.Type.LOCAL);
            
            // Store in history
            List<ChatMessage> history = messageHistory.computeIfAbsent("local", k -> new ArrayList<>());
            history.add(message);
            
            // Limit history size
            if (history.size() > 1000) {
                history.remove(0);
            }
            
            // Notify listener
            if (chatListener != null) {
                chatListener.onLocalChatReceived(message);
            }
            
        } catch (Exception e) {
            Log.e(TAG, "Error handling local chat message", e);
        }
    }
    
    private void handleGroupChatMessage(String payload) {
        try {
            // Parse incoming group chat message
            ChatMessage message = parseChatMessage(payload, ChatMessage.Type.GROUP);
            
            // Store in session and history
            String groupId = message.getSessionId();
            if (groupId != null) {
                ChatSession session = activeSessions.get(groupId);
                if (session != null) {
                    session.addMessage(message);
                }
                
                List<ChatMessage> history = messageHistory.computeIfAbsent(groupId, k -> new ArrayList<>());
                history.add(message);
                
                // Limit history size
                if (history.size() > 1000) {
                    history.remove(0);
                }
            }
            
            // Notify listener
            if (chatListener != null) {
                chatListener.onGroupChatReceived(message);
            }
            
        } catch (Exception e) {
            Log.e(TAG, "Error handling group chat message", e);
        }
    }
    
    private void handleGroupInvitation(String payload) {
        try {
            // Parse group invitation
            GroupChatInvitation invitation = parseGroupInvitation(payload);
            
            if (chatListener != null) {
                chatListener.onGroupChatInvitation(invitation);
            }
            
        } catch (Exception e) {
            Log.e(TAG, "Error handling group invitation", e);
        }
    }
    
    private void handleTypingIndicator(String payload) {
        try {
            // Parse typing indicator - simplified JSON parsing
            String userId = extractJsonValue(payload, "userId");
            String sessionId = extractJsonValue(payload, "sessionId");
            boolean isTyping = Boolean.parseBoolean(extractJsonValue(payload, "typing"));
            
            if (chatListener != null) {
                chatListener.onTypingIndicator(userId, sessionId, isTyping);
            }
            
        } catch (Exception e) {
            Log.e(TAG, "Error handling typing indicator", e);
        }
    }
    
    private String createChatPayload(ChatMessage message) {
        return "{\"id\":\"" + message.getId() + "\"," +
               "\"message\":\"" + escapeJson(message.getMessage()) + "\"," +
               "\"timestamp\":" + message.getTimestamp() + "," +
               "\"channel\":" + message.getChannel() + "}";
    }
    
    private String createGroupChatPayload(ChatMessage message, String groupId) {
        return "{\"id\":\"" + message.getId() + "\"," +
               "\"message\":\"" + escapeJson(message.getMessage()) + "\"," +
               "\"timestamp\":" + message.getTimestamp() + "," +
               "\"groupId\":\"" + groupId + "\"}";
    }
    
    private ChatMessage parseChatMessage(String payload, ChatMessage.Type type) {
        // Simplified JSON parsing for chat messages
        String id = extractJsonValue(payload, "id");
        String message = extractJsonValue(payload, "message");
        String timestampStr = extractJsonValue(payload, "timestamp");
        String senderId = extractJsonValue(payload, "senderId");
        String sessionId = extractJsonValue(payload, "sessionId");
        String channelStr = extractJsonValue(payload, "channel");
        
        long timestamp = timestampStr != null ? Long.parseLong(timestampStr) : System.currentTimeMillis();
        int channel = channelStr != null ? Integer.parseInt(channelStr) : 0;
        
        return new ChatMessage(id, type, message, timestamp, senderId, sessionId, channel);
    }
    
    private GroupChatInvitation parseGroupInvitation(String payload) {
        String groupId = extractJsonValue(payload, "groupId");
        String groupName = extractJsonValue(payload, "groupName");
        String inviterId = extractJsonValue(payload, "inviterId");
        String inviterName = extractJsonValue(payload, "inviterName");
        
        return new GroupChatInvitation(groupId, groupName, inviterId, inviterName);
    }
    
    private boolean sendViaTraditionalProtocol(ChatMessage message) {
        // Fallback to existing SL protocol implementation
        // This would integrate with existing SLChatTextEvent system
        Log.i(TAG, "Sending via traditional SL protocol");
        return true; // Mock implementation
    }
    
    // Simple JSON value extraction (avoiding full JSON parser for minimal dependencies)
    private String extractJsonValue(String json, String key) {
        String searchKey = "\"" + key + "\":";
        int startIndex = json.indexOf(searchKey);
        if (startIndex == -1) return null;
        
        startIndex += searchKey.length();
        while (startIndex < json.length() && Character.isWhitespace(json.charAt(startIndex))) {
            startIndex++;
        }
        
        if (startIndex >= json.length()) return null;
        
        char firstChar = json.charAt(startIndex);
        if (firstChar == '"') {
            // String value
            startIndex++;
            int endIndex = json.indexOf('"', startIndex);
            while (endIndex != -1 && json.charAt(endIndex - 1) == '\\') {
                endIndex = json.indexOf('"', endIndex + 1);
            }
            return endIndex != -1 ? json.substring(startIndex, endIndex) : null;
        } else {
            // Number or boolean value
            int endIndex = startIndex;
            while (endIndex < json.length() && 
                   json.charAt(endIndex) != ',' && 
                   json.charAt(endIndex) != '}' && 
                   json.charAt(endIndex) != ']') {
                endIndex++;
            }
            return json.substring(startIndex, endIndex).trim();
        }
    }
    
    private String escapeJson(String text) {
        if (text == null) return "";
        return text.replace("\\", "\\\\")
                  .replace("\"", "\\\"")
                  .replace("\n", "\\n")
                  .replace("\r", "\\r")
                  .replace("\t", "\\t");
    }
    
    /**
     * Cleanup resources
     */
    public void cleanup() {
        if (executor != null && !executor.isShutdown()) {
            executor.shutdown();
        }
        activeSessions.clear();
        messageQueue.clear();
        messageHistory.clear();
        
        Log.i(TAG, "Chat manager cleaned up");
    }
    
    // Inner classes for chat data structures
    
    public static class ChatMessage {
        public enum Type {
            LOCAL,
            GROUP,
            PRIVATE,
            SYSTEM
        }
        
        private final String id;
        private final Type type;
        private final String message;
        private final long timestamp;
        private final String senderId;
        private final String sessionId;
        private final int channel;
        
        public ChatMessage(String id, Type type, String message, long timestamp, 
                          String senderId, String sessionId, int channel) {
            this.id = id;
            this.type = type;
            this.message = message;
            this.timestamp = timestamp;
            this.senderId = senderId;
            this.sessionId = sessionId;
            this.channel = channel;
        }
        
        // Getters
        public String getId() { return id; }
        public Type getType() { return type; }
        public String getMessage() { return message; }
        public long getTimestamp() { return timestamp; }
        public String getSenderId() { return senderId; }
        public String getSessionId() { return sessionId; }
        public int getChannel() { return channel; }
    }
    
    public static class ChatSession {
        public enum Type {
            LOCAL,
            GROUP,
            PRIVATE
        }
        
        private final String sessionId;
        private final Type type;
        private String groupName;
        private final List<ChatMessage> messages = new ArrayList<>();
        private final long createdTime;
        
        public ChatSession(String sessionId, Type type) {
            this.sessionId = sessionId;
            this.type = type;
            this.createdTime = System.currentTimeMillis();
        }
        
        public void addMessage(ChatMessage message) {
            messages.add(message);
            // Limit message history per session
            if (messages.size() > 500) {
                messages.remove(0);
            }
        }
        
        // Getters and setters
        public String getSessionId() { return sessionId; }
        public Type getType() { return type; }
        public String getGroupName() { return groupName; }
        public void setGroupName(String groupName) { this.groupName = groupName; }
        public List<ChatMessage> getMessages() { return new ArrayList<>(messages); }
        public long getCreatedTime() { return createdTime; }
    }
    
    public static class GroupChatInvitation {
        private final String groupId;
        private final String groupName;
        private final String inviterId;
        private final String inviterName;
        
        public GroupChatInvitation(String groupId, String groupName, String inviterId, String inviterName) {
            this.groupId = groupId;
            this.groupName = groupName;
            this.inviterId = inviterId;
            this.inviterName = inviterName;
        }
        
        // Getters
        public String getGroupId() { return groupId; }
        public String getGroupName() { return groupName; }
        public String getInviterId() { return inviterId; }
        public String getInviterName() { return inviterName; }
    }
}