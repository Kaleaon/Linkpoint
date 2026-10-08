package com.linkpoint.ui.chat;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * SpatialChatView handles proximity-based spatial attenuation for avatar chat
 * and filters channel 0 object script chatter into a secondary collapsible drawer.
 */
public class SpatialChatView extends FrameLayout {

    public enum ChatSourceType {
        AGENT,
        OBJECT,
        SYSTEM,
        UNKNOWN
    }

    public static class ChatMessage {
        private final String id;
        private final int channel;
        private final ChatSourceType sourceType;
        private final String sourceName;
        private final String message;
        private final float positionX;
        private final float positionY;
        private final float positionZ;
        private final long timestamp;
        private float opacity;

        public ChatMessage(String id, int channel, ChatSourceType sourceType, String sourceName,
                           String message, float positionX, float positionY, float positionZ, long timestamp) {
            this.id = id;
            this.channel = channel;
            this.sourceType = sourceType;
            this.sourceName = sourceName;
            this.message = message;
            this.positionX = positionX;
            this.positionY = positionY;
            this.positionZ = positionZ;
            this.timestamp = timestamp;
            this.opacity = 1.0f;
        }

        public String getId() { return id; }
        public int getChannel() { return channel; }
        public ChatSourceType getSourceType() { return sourceType; }
        public String getSourceName() { return sourceName; }
        public String getMessage() { return message; }
        public float getPositionX() { return positionX; }
        public float getPositionY() { return positionY; }
        public float getPositionZ() { return positionZ; }
        public long getTimestamp() { return timestamp; }
        public float getOpacity() { return opacity; }
        public void setOpacity(float opacity) { this.opacity = opacity; }
    }

    public static final int MAX_VISIBLE_AVATAR_LINES = 4;
    public static final float MAX_OVERLAY_HEIGHT_RATIO = 0.20f; // 20% vertical screen height

    public static final int DEFAULT_CHAT_HISTORY_CAPACITY = 200;
    public static final int DEFAULT_SCRIPT_DRAWER_CAPACITY = 100;

    private static boolean nativeLibraryLoaded = false;

    static {
        try {
            System.loadLibrary("lumiya-native");
            nativeLibraryLoaded = true;
        } catch (Throwable t) {
            nativeLibraryLoaded = false;
        }
    }

    public static native float nativeComputeOpacity(
            float cameraX, float cameraY, float cameraZ,
            float sourceX, float sourceY, float sourceZ
    );

    private final BoundedFifoBuffer<ChatMessage> chatHistory;
    private final List<ChatMessage> activeAvatarOverlayMessages = new ArrayList<>();
    private final BoundedFifoBuffer<ChatMessage> scriptDrawerMessages;

    private boolean isScriptDrawerExpanded = false;
    private int unreadScriptCount = 0;

    private float cameraX = 0.0f;
    private float cameraY = 0.0f;
    private float cameraZ = 0.0f;

    public SpatialChatView(Context context) {
        this(context, null);
    }

    public SpatialChatView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SpatialChatView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, DEFAULT_CHAT_HISTORY_CAPACITY, DEFAULT_SCRIPT_DRAWER_CAPACITY);
    }

    public SpatialChatView(Context context, int chatHistoryCapacity, int scriptDrawerCapacity) {
        this(context, null, 0, chatHistoryCapacity, scriptDrawerCapacity);
    }

    public SpatialChatView(Context context, AttributeSet attrs, int defStyleAttr,
                           int chatHistoryCapacity, int scriptDrawerCapacity) {
        super(context, attrs, defStyleAttr);
        this.chatHistory = new BoundedFifoBuffer<>(chatHistoryCapacity);
        this.scriptDrawerMessages = new BoundedFifoBuffer<>(scriptDrawerCapacity);
    }

    public void setChatHistoryCapacity(int capacity) {
        chatHistory.setCapacity(capacity);
    }

    public int getChatHistoryCapacity() {
        return chatHistory.getCapacity();
    }

    public void setScriptDrawerCapacity(int capacity) {
        scriptDrawerMessages.setCapacity(capacity);
        if (unreadScriptCount > scriptDrawerMessages.size()) {
            unreadScriptCount = scriptDrawerMessages.size();
        }
    }

    public int getScriptDrawerCapacity() {
        return scriptDrawerMessages.getCapacity();
    }

    /**
     * Updates current 3D camera position used for proximity attenuation computations.
     */
    public void updateCameraPosition(float x, float y, float z) {
        this.cameraX = x;
        this.cameraY = y;
        this.cameraZ = z;
        recalculateAllAvatarOpacities();
    }

    /**
     * Computes opacity for a source relative to the camera position.
     * Distance < 5.0m -> 1.0f (100% opacity)
     * 5.0m <= Distance <= 20.0m -> Smooth linear scaling down to 0.3f (30% opacity)
     * Distance > 20.0m -> 0.3f (30% opacity)
     */
    public float computeOpacity(float srcX, float srcY, float srcZ) {
        if (nativeLibraryLoaded) {
            try {
                return nativeComputeOpacity(cameraX, cameraY, cameraZ, srcX, srcY, srcZ);
            } catch (UnsatisfiedLinkError e) {
                // Fallback to Java calculation
            }
        }
        return computeJavaOpacity(cameraX, cameraY, cameraZ, srcX, srcY, srcZ);
    }

    public static float computeJavaOpacity(float camX, float camY, float camZ,
                                           float srcX, float srcY, float srcZ) {
        float dx = camX - srcX;
        float dy = camY - srcY;
        float dz = camZ - srcZ;
        float distance = (float) Math.sqrt(dx * dx + dy * dy + dz * dz);

        final float minDist = 5.0f;
        final float maxDist = 20.0f;
        final float minOpacity = 0.3f;
        final float maxOpacity = 1.0f;

        if (distance <= minDist) {
            return maxOpacity;
        }
        if (distance >= maxDist) {
            return minOpacity;
        }

        float fraction = (distance - minDist) / (maxDist - minDist);
        return maxOpacity - fraction * (maxOpacity - minOpacity);
    }

    /**
     * Categorizes and processes channel 0 chat packets.
     */
    public void processChatPacket(ChatMessage message) {
        // Always record in full chat history log so no channel 0 messages are lost.
        chatHistory.add(message);

        if (message.getChannel() != 0) {
            return;
        }

        if (message.getSourceType() == ChatSourceType.AGENT) {
            float opacity = computeOpacity(message.getPositionX(), message.getPositionY(), message.getPositionZ());
            message.setOpacity(opacity);

            activeAvatarOverlayMessages.add(message);
            // Limit visible active avatar text lines to MAX_VISIBLE_AVATAR_LINES (4 lines)
            while (activeAvatarOverlayMessages.size() > MAX_VISIBLE_AVATAR_LINES) {
                activeAvatarOverlayMessages.remove(0);
            }
        } else if (message.getSourceType() == ChatSourceType.OBJECT) {
            scriptDrawerMessages.add(message);
            if (!isScriptDrawerExpanded) {
                unreadScriptCount++;
                if (unreadScriptCount > scriptDrawerMessages.size()) {
                    unreadScriptCount = scriptDrawerMessages.size();
                }
            }
        }
    }

    private void recalculateAllAvatarOpacities() {
        for (ChatMessage msg : activeAvatarOverlayMessages) {
            float opacity = computeOpacity(msg.getPositionX(), msg.getPositionY(), msg.getPositionZ());
            msg.setOpacity(opacity);
        }
    }

    /**
     * Toggles or sets expanded state of the script chat drawer.
     */
    public void setScriptDrawerExpanded(boolean expanded) {
        this.isScriptDrawerExpanded = expanded;
        if (expanded) {
            unreadScriptCount = 0;
        }
    }

    public boolean isScriptDrawerExpanded() {
        return isScriptDrawerExpanded;
    }

    public int getUnreadScriptCount() {
        return unreadScriptCount;
    }

    public List<ChatMessage> getActiveAvatarOverlayMessages() {
        return Collections.unmodifiableList(activeAvatarOverlayMessages);
    }

    public List<ChatMessage> getScriptDrawerMessages() {
        return Collections.unmodifiableList(scriptDrawerMessages);
    }

    public List<ChatMessage> getChatHistory() {
        return Collections.unmodifiableList(chatHistory);
    }

    /**
     * Calculates current overlay vertical height footprint as ratio of screen height.
     * Capped to maximum 20% (0.20f) of vertical screen height on 5-inch viewports.
     */
    public float calculateOverlayHeightRatio(int screenHeightPx, int singleLineHeightPx) {
        if (screenHeightPx <= 0) return 0.0f;
        int lineCount = Math.min(activeAvatarOverlayMessages.size(), MAX_VISIBLE_AVATAR_LINES);
        float calculatedHeight = lineCount * singleLineHeightPx;
        float ratio = calculatedHeight / (float) screenHeightPx;
        return Math.min(ratio, MAX_OVERLAY_HEIGHT_RATIO);
    }
}
