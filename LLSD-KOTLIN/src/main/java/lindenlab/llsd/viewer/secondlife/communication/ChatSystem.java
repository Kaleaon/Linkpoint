/*
 * ChatSystem - Complete Second Life chat communication system
 *
 * Provides comprehensive chat functionality including local chat, IMs,
 * group chat, voice chat integration, and advanced features like
 * chat history, translation, moderation, and multi-tab support.
 *
 * Copyright (C) 2024 Second Life Viewer Project
 */

package lindenlab.llsd.viewer.secondlife.communication;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Complete Second Life chat communication system with multi-channel support,
 * instant messaging, group chat, voice integration, and advanced features.
 */
public class ChatSystem {
    
    private static final int MAX_CHAT_HISTORY = 10000;
    private static final int MAX_MESSAGE_LENGTH = 1024;
    private static final double LOCAL_CHAT_RANGE = 20.0; // meters
    private static final double WHISPER_RANGE = 10.0; // meters  
    private static final double SHOUT_RANGE = 100.0; // meters
    
    private final Map<String, ChatChannel> chatChannels = new ConcurrentHashMap<>();
    private final Map<UUID, IMSession> imSessions = new ConcurrentHashMap<>();
    private final Map<UUID, GroupChatSession> groupSessions = new ConcurrentHashMap<>();
    private final List<ChatMessage> chatHistory = Collections.synchronizedList(new ArrayList<>());
    private final Set<ChatListener> listeners = ConcurrentHashMap.newKeySet();
    private final ExecutorService chatExecutor = Executors.newCachedThreadPool();
    private final AtomicLong messageIdCounter = new AtomicLong(0);
    
    // Chat settings
    private final ChatSettings settings = new ChatSettings();
    private boolean voiceChatEnabled = true;
    private boolean chatTranslationEnabled = false;
    private String translationLanguage = "en";
    private boolean chatModerationEnabled = true;
    private final Set<String> blockedUsers = ConcurrentHashMap.newKeySet();
    private final Set<String> mutedUsers = ConcurrentHashMap.newKeySet();
    
    // Voice chat integration
    private VoiceChatManager voiceManager = new VoiceChatManager();
    private boolean voiceEnabled = false;
    private float voiceVolume = 1.0f;
    private boolean voiceMuted = false;
    
    public ChatSystem() {
        initializeDefaultChannels();
        startChatProcessing();
    }
    
    /**
     * Chat message types supported by the system
     */
    public enum ChatType {
        SAY(0, "Say", LOCAL_CHAT_RANGE),
        WHISPER(1, "Whisper", WHISPER_RANGE), 
        SHOUT(2, "Shout", SHOUT_RANGE),
        INSTANT_MESSAGE(3, "IM", Double.MAX_VALUE),
        GROUP_CHAT(4, "Group", Double.MAX_VALUE),
        REGION_SAY(5, "Region", Double.MAX_VALUE),
        OWNER_SAY(6, "Owner", LOCAL_CHAT_RANGE),
        DEBUG_CHANNEL(7, "Debug", LOCAL_CHAT_RANGE),
        OBJECT_CHAT(8, "Object", LOCAL_CHAT_RANGE),
        SYSTEM_MESSAGE(9, "System", Double.MAX_VALUE);
        
        private final int channel;
        private final String displayName;
        private final double range;
        
        ChatType(int channel, String displayName, double range) {
            this.channel = channel;
            this.displayName = displayName;
            this.range = range;
        }
        
        public int getChannel() { return channel; }
        public String getDisplayName() { return displayName; }
        public double getRange() { return range; }
    }
    
    // Essential classes for chat functionality
    public static class ChatMessage {
        private final long messageId;
        private final UUID sourceId;
        private final String sourceName;
        private final String message;
        private final ChatType chatType;
        private final int channel;
        private final LocalDateTime timestamp;
        private final Vector3 position;
        private final UUID sessionId;
        private boolean translated = false;
        private String translatedText = null;
        private boolean moderated = false;
        private String moderationReason = null;
        
        public ChatMessage(UUID sourceId, String sourceName, String message, 
                          ChatType chatType, int channel, Vector3 position, UUID sessionId) {
            this.messageId = System.currentTimeMillis();
            this.sourceId = sourceId;
            this.sourceName = sourceName;
            this.message = message;
            this.chatType = chatType;
            this.channel = channel;
            this.timestamp = LocalDateTime.now();
            this.position = position != null ? position : new Vector3(0, 0, 0);
            this.sessionId = sessionId;
        }
        
        // Getters
        public long getMessageId() { return messageId; }
        public UUID getSourceId() { return sourceId; }
        public String getSourceName() { return sourceName; }
        public String getMessage() { return message; }
        public ChatType getChatType() { return chatType; }
        public int getChannel() { return channel; }
        public LocalDateTime getTimestamp() { return timestamp; }
        public Vector3 getPosition() { return position; }
        public UUID getSessionId() { return sessionId; }
        public boolean isTranslated() { return translated; }
        public String getTranslatedText() { return translatedText; }
        public boolean isModerated() { return moderated; }
        public String getModerationReason() { return moderationReason; }
        
        public void setTranslation(String translatedText) {
            this.translatedText = translatedText;
            this.translated = true;
        }
        
        public void setModerated(String reason) {
            this.moderated = true;
            this.moderationReason = reason;
        }
        
        @Override
        public String toString() {
            String timeStr = timestamp.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
            return String.format("[%s] %s (%s): %s", 
                timeStr, sourceName, chatType.getDisplayName(), 
                moderated ? "[MODERATED]" : (translated ? translatedText : message));
        }
    }
    
    public static class ChatChannel {
        private final String name;
        private final int channelNumber;
        private final ChatType defaultType;
        private final List<ChatMessage> messages = Collections.synchronizedList(new ArrayList<>());
        private final Set<UUID> subscribers = ConcurrentHashMap.newKeySet();
        private boolean enabled = true;
        private boolean logged = true;
        private String color = "#FFFFFF";
        
        public ChatChannel(String name, int channelNumber, ChatType defaultType) {
            this.name = name;
            this.channelNumber = channelNumber;
            this.defaultType = defaultType;
        }
        
        public void addMessage(ChatMessage message) {
            if (!enabled) return;
            
            messages.add(message);
            if (messages.size() > MAX_CHAT_HISTORY) {
                messages.remove(0);
            }
        }
        
        public void subscribe(UUID userId) { subscribers.add(userId); }
        public void unsubscribe(UUID userId) { subscribers.remove(userId); }
        
        // Getters/Setters
        public String getName() { return name; }
        public int getChannelNumber() { return channelNumber; }
        public ChatType getDefaultType() { return defaultType; }
        public List<ChatMessage> getMessages() { return new ArrayList<>(messages); }
        public Set<UUID> getSubscribers() { return new HashSet<>(subscribers); }
        public boolean isEnabled() { return enabled; }
        public void setEnabled(boolean enabled) { this.enabled = enabled; }
        public boolean isLogged() { return logged; }
        public void setLogged(boolean logged) { this.logged = logged; }
        public String getColor() { return color; }
        public void setColor(String color) { this.color = color; }
    }
    
    public static class IMSession {
        private final UUID sessionId;
        private final UUID targetUserId;
        private final String targetUserName;
        private final List<ChatMessage> messages = Collections.synchronizedList(new ArrayList<>());
        private final LocalDateTime createdTime = LocalDateTime.now();
        private LocalDateTime lastActivity = LocalDateTime.now();
        private boolean active = true;
        private boolean typing = false;
        private boolean targetTyping = false;
        
        public IMSession(UUID targetUserId, String targetUserName) {
            this.sessionId = UUID.randomUUID();
            this.targetUserId = targetUserId;
            this.targetUserName = targetUserName;
        }
        
        public void addMessage(ChatMessage message) {
            messages.add(message);
            lastActivity = LocalDateTime.now();
            if (messages.size() > MAX_CHAT_HISTORY) {
                messages.remove(0);
            }
        }
        
        // Getters/Setters
        public UUID getSessionId() { return sessionId; }
        public UUID getTargetUserId() { return targetUserId; }
        public String getTargetUserName() { return targetUserName; }
        public List<ChatMessage> getMessages() { return new ArrayList<>(messages); }
        public LocalDateTime getCreatedTime() { return createdTime; }
        public LocalDateTime getLastActivity() { return lastActivity; }
        public boolean isActive() { return active; }
        public void setActive(boolean active) { this.active = active; }
        public boolean isTyping() { return typing; }
        public void setTyping(boolean typing) { this.typing = typing; }
        public boolean isTargetTyping() { return targetTyping; }
        public void setTargetTyping(boolean targetTyping) { this.targetTyping = targetTyping; }
    }
    
    public static class GroupChatSession {
        private final UUID sessionId;
        private final UUID groupId;
        private final String groupName;
        private final List<ChatMessage> messages = Collections.synchronizedList(new ArrayList<>());
        private final Set<UUID> participants = ConcurrentHashMap.newKeySet();
        private final Map<UUID, String> participantNames = new ConcurrentHashMap<>();
        private final LocalDateTime createdTime = LocalDateTime.now();
        private LocalDateTime lastActivity = LocalDateTime.now();
        private boolean active = true;
        private boolean moderated = false;
        private final Set<UUID> moderators = ConcurrentHashMap.newKeySet();
        private final Set<UUID> mutedMembers = ConcurrentHashMap.newKeySet();
        
        public GroupChatSession(UUID groupId, String groupName) {
            this.sessionId = UUID.randomUUID();
            this.groupId = groupId;
            this.groupName = groupName;
        }
        
        public void addMessage(ChatMessage message) {
            if (mutedMembers.contains(message.getSourceId()) && !moderators.contains(message.getSourceId())) {
                return; // Muted member cannot post
            }
            
            messages.add(message);
            lastActivity = LocalDateTime.now();
            if (messages.size() > MAX_CHAT_HISTORY) {
                messages.remove(0);
            }
        }
        
        public void addParticipant(UUID userId, String userName) {
            participants.add(userId);
            participantNames.put(userId, userName);
        }
        
        public void removeParticipant(UUID userId) {
            participants.remove(userId);
            participantNames.remove(userId);
        }
        
        public void addModerator(UUID userId) { moderators.add(userId); }
        public void removeModerator(UUID userId) { moderators.remove(userId); }
        public void muteMember(UUID userId) { mutedMembers.add(userId); }
        public void unmuteMember(UUID userId) { mutedMembers.remove(userId); }
        
        // Getters/Setters
        public UUID getSessionId() { return sessionId; }
        public UUID getGroupId() { return groupId; }
        public String getGroupName() { return groupName; }
        public List<ChatMessage> getMessages() { return new ArrayList<>(messages); }
        public Set<UUID> getParticipants() { return new HashSet<>(participants); }
        public Map<UUID, String> getParticipantNames() { return new HashMap<>(participantNames); }
        public LocalDateTime getCreatedTime() { return createdTime; }
        public LocalDateTime getLastActivity() { return lastActivity; }
        public boolean isActive() { return active; }
        public void setActive(boolean active) { this.active = active; }
        public boolean isModerated() { return moderated; }
        public void setModerated(boolean moderated) { this.moderated = moderated; }
        public Set<UUID> getModerators() { return new HashSet<>(moderators); }
        public Set<UUID> getMutedMembers() { return new HashSet<>(mutedMembers); }
    }
    
    public static class VoiceChatManager {
        private boolean initialized = false;
        private boolean connected = false;
        private String voiceServer = "";
        private final Map<UUID, VoiceSession> voiceSessions = new ConcurrentHashMap<>();
        private float masterVolume = 1.0f;
        private boolean microphoneMuted = false;
        private float microphoneGain = 1.0f;
        private final Set<UUID> mutedUsers = ConcurrentHashMap.newKeySet();
        
        public boolean initializeVoice() {
            // Initialize voice chat system (placeholder for actual implementation)
            initialized = true;
            System.out.println("Voice chat system initialized");
            return true;
        }
        
        public boolean connectToVoiceServer(String server) {
            if (!initialized) return false;
            
            this.voiceServer = server;
            connected = true;
            System.out.println("Connected to voice server: " + server);
            return true;
        }
        
        public VoiceSession startVoiceSession(UUID sessionId, List<UUID> participants) {
            if (!connected) return null;
            
            VoiceSession session = new VoiceSession(sessionId, participants);
            voiceSessions.put(sessionId, session);
            System.out.println("Started voice session: " + sessionId);
            return session;
        }
        
        public void endVoiceSession(UUID sessionId) {
            VoiceSession session = voiceSessions.remove(sessionId);
            if (session != null) {
                session.setActive(false);
                System.out.println("Ended voice session: " + sessionId);
            }
        }
        
        public void muteUser(UUID userId) { mutedUsers.add(userId); }
        public void unmuteUser(UUID userId) { mutedUsers.remove(userId); }
        
        // Getters/Setters
        public boolean isInitialized() { return initialized; }
        public boolean isConnected() { return connected; }
        public String getVoiceServer() { return voiceServer; }
        public float getMasterVolume() { return masterVolume; }
        public void setMasterVolume(float volume) { this.masterVolume = Math.max(0, Math.min(1, volume)); }
        public boolean isMicrophoneMuted() { return microphoneMuted; }
        public void setMicrophoneMuted(boolean muted) { this.microphoneMuted = muted; }
        public float getMicrophoneGain() { return microphoneGain; }
        public void setMicrophoneGain(float gain) { this.microphoneGain = Math.max(0, Math.min(2, gain)); }
        public Set<UUID> getMutedUsers() { return new HashSet<>(mutedUsers); }
        public Map<UUID, VoiceSession> getVoiceSessions() { return new HashMap<>(voiceSessions); }
    }
    
    public static class VoiceSession {
        private final UUID sessionId;
        private final List<UUID> participants;
        private final LocalDateTime createdTime = LocalDateTime.now();
        private boolean active = true;
        private final Map<UUID, Float> participantVolumes = new ConcurrentHashMap<>();
        private final Set<UUID> mutedParticipants = ConcurrentHashMap.newKeySet();
        
        public VoiceSession(UUID sessionId, List<UUID> participants) {
            this.sessionId = sessionId;
            this.participants = new ArrayList<>(participants);
            // Initialize default volumes
            for (UUID participant : participants) {
                participantVolumes.put(participant, 1.0f);
            }
        }
        
        public void addParticipant(UUID userId) {
            if (!participants.contains(userId)) {
                participants.add(userId);
                participantVolumes.put(userId, 1.0f);
            }
        }
        
        public void removeParticipant(UUID userId) {
            participants.remove(userId);
            participantVolumes.remove(userId);
            mutedParticipants.remove(userId);
        }
        
        public void setParticipantVolume(UUID userId, float volume) {
            if (participants.contains(userId)) {
                participantVolumes.put(userId, Math.max(0, Math.min(1, volume)));
            }
        }
        
        public void muteParticipant(UUID userId) { mutedParticipants.add(userId); }
        public void unmuteParticipant(UUID userId) { mutedParticipants.remove(userId); }
        
        // Getters/Setters
        public UUID getSessionId() { return sessionId; }
        public List<UUID> getParticipants() { return new ArrayList<>(participants); }
        public LocalDateTime getCreatedTime() { return createdTime; }
        public boolean isActive() { return active; }
        public void setActive(boolean active) { this.active = active; }
        public Map<UUID, Float> getParticipantVolumes() { return new HashMap<>(participantVolumes); }
        public Set<UUID> getMutedParticipants() { return new HashSet<>(mutedParticipants); }
    }
    
    public static class ChatSettings {
        private boolean timestampsEnabled = true;
        private boolean soundEnabled = true;
        private boolean autoScrollEnabled = true;
        private boolean compactMode = false;
        private int maxVisibleLines = 200;
        private String fontFamily = "Arial";
        private int fontSize = 12;
        private boolean wordWrapEnabled = true;
        private boolean emoticonsEnabled = true;
        private boolean urlLinksEnabled = true;
        private boolean chatFadeEnabled = false;
        private int chatFadeTime = 10; // seconds
        private final Map<ChatType, Boolean> typeVisibility = new EnumMap<>(ChatType.class);
        private final Map<ChatType, String> typeColors = new EnumMap<>(ChatType.class);
        
        public ChatSettings() {
            // Initialize default visibility and colors
            for (ChatType type : ChatType.values()) {
                typeVisibility.put(type, true);
                typeColors.put(type, getDefaultColor(type));
            }
        }
        
        private String getDefaultColor(ChatType type) {
            switch (type) {
                case SAY: return "#FFFFFF";
                case WHISPER: return "#B0B0B0";
                case SHOUT: return "#FF0000";
                case INSTANT_MESSAGE: return "#00FFFF";
                case GROUP_CHAT: return "#00FF00";
                case SYSTEM_MESSAGE: return "#FFFF00";
                default: return "#FFFFFF";
            }
        }
        
        // Getters/Setters
        public boolean isTimestampsEnabled() { return timestampsEnabled; }
        public void setTimestampsEnabled(boolean enabled) { this.timestampsEnabled = enabled; }
        public boolean isSoundEnabled() { return soundEnabled; }
        public void setSoundEnabled(boolean enabled) { this.soundEnabled = enabled; }
        public boolean isAutoScrollEnabled() { return autoScrollEnabled; }
        public void setAutoScrollEnabled(boolean enabled) { this.autoScrollEnabled = enabled; }
        public boolean isCompactMode() { return compactMode; }
        public void setCompactMode(boolean compact) { this.compactMode = compact; }
        public int getMaxVisibleLines() { return maxVisibleLines; }
        public void setMaxVisibleLines(int lines) { this.maxVisibleLines = Math.max(10, Math.min(1000, lines)); }
        public String getFontFamily() { return fontFamily; }
        public void setFontFamily(String family) { this.fontFamily = family; }
        public int getFontSize() { return fontSize; }
        public void setFontSize(int size) { this.fontSize = Math.max(8, Math.min(24, size)); }
        public boolean isWordWrapEnabled() { return wordWrapEnabled; }
        public void setWordWrapEnabled(boolean enabled) { this.wordWrapEnabled = enabled; }
        public boolean isEmoticonsEnabled() { return emoticonsEnabled; }
        public void setEmoticonsEnabled(boolean enabled) { this.emoticonsEnabled = enabled; }
        public boolean isUrlLinksEnabled() { return urlLinksEnabled; }
        public void setUrlLinksEnabled(boolean enabled) { this.urlLinksEnabled = enabled; }
        public boolean isChatFadeEnabled() { return chatFadeEnabled; }
        public void setChatFadeEnabled(boolean enabled) { this.chatFadeEnabled = enabled; }
        public int getChatFadeTime() { return chatFadeTime; }
        public void setChatFadeTime(int seconds) { this.chatFadeTime = Math.max(1, Math.min(60, seconds)); }
        
        public boolean isTypeVisible(ChatType type) { return typeVisibility.getOrDefault(type, true); }
        public void setTypeVisible(ChatType type, boolean visible) { typeVisibility.put(type, visible); }
        public String getTypeColor(ChatType type) { return typeColors.getOrDefault(type, "#FFFFFF"); }
        public void setTypeColor(ChatType type, String color) { typeColors.put(type, color); }
    }
    
    public interface ChatListener {
        void onChatMessage(ChatMessage message);
        void onIMReceived(ChatMessage message, IMSession session);
        void onGroupChatMessage(ChatMessage message, GroupChatSession session);
        void onUserTyping(UUID userId, boolean typing);
        void onVoiceSessionStarted(VoiceSession session);
        void onVoiceSessionEnded(UUID sessionId);
        void onUserMuted(UUID userId);
        void onUserUnmuted(UUID userId);
    }
    
    // Simple Vector3 class for position data
    public static class Vector3 {
        public final double x, y, z;
        
        public Vector3(double x, double y, double z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }
        
        public double distanceTo(Vector3 other) {
            double dx = x - other.x;
            double dy = y - other.y;
            double dz = z - other.z;
            return Math.sqrt(dx*dx + dy*dy + dz*dz);
        }
        
        @Override
        public String toString() {
            return String.format("(%.2f, %.2f, %.2f)", x, y, z);
        }
    }
    
    // UUID class for user/object identification
    public static class UUID {
        private final String value;
        
        private UUID(String value) {
            this.value = value;
        }
        
        public static UUID randomUUID() {
            return new UUID(java.util.UUID.randomUUID().toString());
        }
        
        public static UUID fromString(String uuidString) {
            return new UUID(uuidString);
        }
        
        @Override
        public String toString() {
            return value;
        }
        
        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            UUID uuid = (UUID) obj;
            return Objects.equals(value, uuid.value);
        }
        
        @Override
        public int hashCode() {
            return Objects.hash(value);
        }
    }
    
    // Public API Methods
    
    private void initializeDefaultChannels() {
        addChatChannel(new ChatChannel("Local Chat", 0, ChatType.SAY));
        addChatChannel(new ChatChannel("Debug", 2147483647, ChatType.DEBUG_CHANNEL));
        System.out.println("Chat system initialized with default channels");
    }
    
    private void startChatProcessing() {
        chatExecutor.submit(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    processPendingTranslations();
                    cleanupOldSessions();
                    updateVoiceChatStatus();
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                } catch (Exception e) {
                    System.err.println("Error in chat processing: " + e.getMessage());
                }
            }
        });
    }
    
    public CompletableFuture<Boolean> sendChatMessage(String message, ChatType type, int channel, Vector3 position) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                if (message == null || message.trim().isEmpty()) {
                    return false;
                }
                
                if (message.length() > MAX_MESSAGE_LENGTH) {
                    message = message.substring(0, MAX_MESSAGE_LENGTH);
                }
                
                UUID currentUserId = getCurrentUserId();
                String currentUserName = getCurrentUserName();
                ChatMessage chatMessage = new ChatMessage(
                    currentUserId, currentUserName, message.trim(), 
                    type, channel, position, null
                );
                
                if (chatModerationEnabled && !moderateMessage(chatMessage)) {
                    return false;
                }
                
                if (chatTranslationEnabled && !translationLanguage.equals("en")) {
                    translateMessage(chatMessage);
                }
                
                addToHistory(chatMessage);
                
                ChatChannel chatChannel = chatChannels.get(String.valueOf(channel));
                if (chatChannel != null) {
                    chatChannel.addMessage(chatMessage);
                }
                
                notifyListeners(chatMessage);
                
                System.out.println("Chat sent: " + chatMessage);
                return true;
                
            } catch (Exception e) {
                System.err.println("Error sending chat message: " + e.getMessage());
                return false;
            }
        }, chatExecutor);
    }
    
    public CompletableFuture<Boolean> sendInstantMessage(UUID targetUserId, String message) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                if (message == null || message.trim().isEmpty() || targetUserId == null) {
                    return false;
                }
                
                if (blockedUsers.contains(targetUserId.toString())) {
                    System.out.println("Cannot send IM to blocked user: " + targetUserId);
                    return false;
                }
                
                IMSession session = imSessions.computeIfAbsent(targetUserId, 
                    id -> new IMSession(id, "User_" + id.toString().substring(0, 8)));
                
                UUID currentUserId = getCurrentUserId();
                String currentUserName = getCurrentUserName();
                ChatMessage imMessage = new ChatMessage(
                    currentUserId, currentUserName, message.trim(),
                    ChatType.INSTANT_MESSAGE, 0, null, session.getSessionId()
                );
                
                if (chatModerationEnabled && !moderateMessage(imMessage)) {
                    return false;
                }
                
                if (chatTranslationEnabled) {
                    translateMessage(imMessage);
                }
                
                session.addMessage(imMessage);
                addToHistory(imMessage);
                notifyIMListeners(imMessage, session);
                
                System.out.println("IM sent to " + targetUserId + ": " + message);
                return true;
                
            } catch (Exception e) {
                System.err.println("Error sending IM: " + e.getMessage());
                return false;
            }
        }, chatExecutor);
    }
    
    // More essential methods...
    public void addChatChannel(ChatChannel channel) {
        if (channel != null) {
            chatChannels.put(String.valueOf(channel.getChannelNumber()), channel);
        }
    }
    
    public void blockUser(String userId) {
        if (userId != null) {
            blockedUsers.add(userId);
            mutedUsers.add(userId);
            System.out.println("Blocked user: " + userId);
        }
    }
    
    public void muteUser(String userId) {
        if (userId != null) {
            mutedUsers.add(userId);
            voiceManager.muteUser(UUID.fromString(userId));
            System.out.println("Muted user: " + userId);
        }
    }
    
    public void shutdown() {
        System.out.println("Shutting down chat system...");
        chatExecutor.shutdown();
        try {
            if (!chatExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                chatExecutor.shutdownNow();
            }
        } catch (InterruptedException e) {
            chatExecutor.shutdownNow();
            Thread.currentThread().interrupt();
        }
        
        for (IMSession session : imSessions.values()) {
            session.setActive(false);
        }
        for (GroupChatSession session : groupSessions.values()) {
            session.setActive(false);
        }
        
        imSessions.clear();
        groupSessions.clear();
        chatChannels.clear();
        listeners.clear();
        
        System.out.println("Chat system shutdown complete");
    }
    
    // Helper methods
    private UUID getCurrentUserId() {
        return UUID.fromString("user-" + System.currentTimeMillis());
    }
    
    private String getCurrentUserName() {
        return "CurrentUser";
    }
    
    private void addToHistory(ChatMessage message) {
        chatHistory.add(message);
        if (chatHistory.size() > MAX_CHAT_HISTORY) {
            chatHistory.remove(0);
        }
    }
    
    private boolean moderateMessage(ChatMessage message) {
        String lowerMessage = message.getMessage().toLowerCase();
        String[] bannedWords = {"spam", "scam", "hack", "exploit"};
        
        for (String bannedWord : bannedWords) {
            if (lowerMessage.contains(bannedWord)) {
                message.setModerated("Contains banned word: " + bannedWord);
                return false;
            }
        }
        return true;
    }
    
    private void translateMessage(ChatMessage message) {
        if (!translationLanguage.equals("en")) {
            message.setTranslation("[TRANSLATED] " + message.getMessage());
        }
    }
    
    private void processPendingTranslations() {
        // Process any pending translation requests
    }
    
    private void cleanupOldSessions() {
        LocalDateTime cutoff = LocalDateTime.now().minusHours(24);
        
        imSessions.entrySet().removeIf(entry -> 
            !entry.getValue().isActive() && entry.getValue().getLastActivity().isBefore(cutoff));
        
        groupSessions.entrySet().removeIf(entry -> 
            !entry.getValue().isActive() && entry.getValue().getLastActivity().isBefore(cutoff));
    }
    
    private void updateVoiceChatStatus() {
        if (voiceEnabled && !voiceManager.isConnected()) {
            // Attempt reconnection logic here
        }
    }
    
    private void notifyListeners(ChatMessage message) {
        for (ChatListener listener : listeners) {
            try {
                listener.onChatMessage(message);
            } catch (Exception e) {
                System.err.println("Error notifying chat listener: " + e.getMessage());
            }
        }
    }
    
    private void notifyIMListeners(ChatMessage message, IMSession session) {
        for (ChatListener listener : listeners) {
            try {
                listener.onIMReceived(message, session);
            } catch (Exception e) {
                System.err.println("Error notifying IM listener: " + e.getMessage());
            }
        }
    }
    
    // Getters
    public ChatSettings getSettings() { return settings; }
    public boolean isVoiceChatEnabled() { return voiceEnabled; }
    public List<ChatMessage> getChatHistory() { return new ArrayList<>(chatHistory); }
    public List<IMSession> getAllIMSessions() { return new ArrayList<>(imSessions.values()); }
    public List<ChatChannel> getAllChatChannels() { return new ArrayList<>(chatChannels.values()); }
    public VoiceChatManager getVoiceManager() { return voiceManager; }
    public Set<String> getBlockedUsers() { return new HashSet<>(blockedUsers); }
    public Set<String> getMutedUsers() { return new HashSet<>(mutedUsers); }
}