// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.users.chatsrc;

import javax.annotation.Nullable;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import java.util.UUID;
import javax.annotation.Nonnull;
import com.lumiyaviewer.lumiya.dao.ChatMessage;

public abstract class ChatMessageSource
{
    private static /* synthetic */ int[] -getcom-lumiyaviewer-lumiya-slproto-users-chatsrc-ChatMessageSource$ChatMessageSourceTypeSwitchesValues() {
        if (ChatMessageSource.-com-lumiyaviewer-lumiya-slproto-users-chatsrc-ChatMessageSource$ChatMessageSourceTypeSwitchesValues != null) {
            return ChatMessageSource.-com-lumiyaviewer-lumiya-slproto-users-chatsrc-ChatMessageSource$ChatMessageSourceTypeSwitchesValues;
        }
        int[] -com-lumiyaviewer-lumiya-slproto-users-chatsrc-ChatMessageSource$ChatMessageSourceTypeSwitchesValues = new int[ChatMessageSourceType.values().length];
        while (true) {
            try {
                -com-lumiyaviewer-lumiya-slproto-users-chatsrc-ChatMessageSource$ChatMessageSourceTypeSwitchesValues[ChatMessageSourceType.Group.ordinal()] = 1;
                try {
                    -com-lumiyaviewer-lumiya-slproto-users-chatsrc-ChatMessageSource$ChatMessageSourceTypeSwitchesValues[ChatMessageSourceType.Object.ordinal()] = 2;
                    try {
                        -com-lumiyaviewer-lumiya-slproto-users-chatsrc-ChatMessageSource$ChatMessageSourceTypeSwitchesValues[ChatMessageSourceType.System.ordinal()] = 3;
                        try {
                            -com-lumiyaviewer-lumiya-slproto-users-chatsrc-ChatMessageSource$ChatMessageSourceTypeSwitchesValues[ChatMessageSourceType.Unknown.ordinal()] = 4;
                            try {
                                -com-lumiyaviewer-lumiya-slproto-users-chatsrc-ChatMessageSource$ChatMessageSourceTypeSwitchesValues[ChatMessageSourceType.User.ordinal()] = 5;
                                return -com-lumiyaviewer-lumiya-slproto-users-chatsrc-ChatMessageSource$ChatMessageSourceTypeSwitchesValues = -com-lumiyaviewer-lumiya-slproto-users-chatsrc-ChatMessageSource$ChatMessageSourceTypeSwitchesValues;
                            }
                            catch (final NoSuchFieldError noSuchFieldError) {}
                        }
                        catch (final NoSuchFieldError noSuchFieldError2) {}
                    }
                    catch (final NoSuchFieldError noSuchFieldError3) {}
                }
                catch (final NoSuchFieldError noSuchFieldError4) {}
            }
            catch (final NoSuchFieldError noSuchFieldError5) {
                continue;
            }
            break;
        }
    }
    
    @Nonnull
    public static ChatMessageSource loadFrom(@Nonnull final ChatMessage chatMessage) {
        switch (-getcom-lumiyaviewer-lumiya-slproto-users-chatsrc-ChatMessageSource$ChatMessageSourceTypeSwitchesValues()[ChatMessageSourceType.VALUES[chatMessage.getSenderType()].ordinal()]) {
            default: {
                throw new IllegalArgumentException("Unknown message type");
            }
            case 4: {
                return ChatMessageSourceUnknown.getInstance();
            }
            case 3: {
                return new ChatMessageSourceSystem();
            }
            case 5: {
                return new ChatMessageSourceUser(chatMessage);
            }
            case 1: {
                return new ChatMessageSourceGroup(chatMessage);
            }
            case 2: {
                return new ChatMessageSourceObject(chatMessage);
            }
        }
    }
    
    @Nonnull
    public abstract ChatterID getDefaultChatter(final UUID p0);
    
    @Nullable
    public abstract String getSourceName(@Nonnull final UserManager p0);
    
    @Nonnull
    public abstract ChatMessageSourceType getSourceType();
    
    @Nullable
    public abstract UUID getSourceUUID();
    
    public void serializeTo(@Nonnull final ChatMessage chatMessage) {
        chatMessage.setSenderType(this.getSourceType().ordinal());
    }
    
    public enum ChatMessageSourceType
    {
        Group("Group", 3), 
        Object("Object", 4), 
        System("System", 1), 
        Unknown("Unknown", 0), 
        User("User", 2);
        
        public static final ChatMessageSourceType[] VALUES;
        
        static {
            VALUES = values();
        }
        
        private ChatMessageSourceType(final String name, final int ordinal) {
        }
    }
}
