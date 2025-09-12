package com.lumiyaviewer.lumiya.slproto.users.chatsrc;

import com.lumiyaviewer.lumiya.dao.ChatMessage;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class ChatMessageSource {

    /* renamed from: -com-lumiyaviewer-lumiya-slproto-users-chatsrc-ChatMessageSource$ChatMessageSourceTypeSwitchesValues  reason: not valid java name */
    private static final /* synthetic */ int[] f151comlumiyaviewerlumiyaslprotouserschatsrcChatMessageSource$ChatMessageSourceTypeSwitchesValues = null;

    public enum ChatMessageSourceType {
        Unknown,
        System,
        User,
        Group,
        Object;
        
        public static final ChatMessageSourceType[] VALUES = null;

        static {
            VALUES = values();
        }
    }

    /* renamed from: -getcom-lumiyaviewer-lumiya-slproto-users-chatsrc-ChatMessageSource$ChatMessageSourceTypeSwitchesValues  reason: not valid java name */
    private static /* synthetic */ int[] m268getcomlumiyaviewerlumiyaslprotouserschatsrcChatMessageSource$ChatMessageSourceTypeSwitchesValues() {
        if (f151comlumiyaviewerlumiyaslprotouserschatsrcChatMessageSource$ChatMessageSourceTypeSwitchesValues != null) {
            return f151comlumiyaviewerlumiyaslprotouserschatsrcChatMessageSource$ChatMessageSourceTypeSwitchesValues;
        }
        int[] iArr = new int[ChatMessageSourceType.values().length];
        try {
            iArr[ChatMessageSourceType.Group.ordinal()] = 1;
        } catch (NoSuchFieldError e) {
        }
        try {
            iArr[ChatMessageSourceType.Object.ordinal()] = 2;
        } catch (NoSuchFieldError e2) {
        }
        try {
            iArr[ChatMessageSourceType.System.ordinal()] = 3;
        } catch (NoSuchFieldError e3) {
        }
        try {
            iArr[ChatMessageSourceType.Unknown.ordinal()] = 4;
        } catch (NoSuchFieldError e4) {
        }
        try {
            iArr[ChatMessageSourceType.User.ordinal()] = 5;
        } catch (NoSuchFieldError e5) {
        }
        f151comlumiyaviewerlumiyaslprotouserschatsrcChatMessageSource$ChatMessageSourceTypeSwitchesValues = iArr;
        return iArr;
    }

    @Nonnull
    public static ChatMessageSource loadFrom(@Nonnull ChatMessage chatMessage) {
        switch (m268getcomlumiyaviewerlumiyaslprotouserschatsrcChatMessageSource$ChatMessageSourceTypeSwitchesValues()[ChatMessageSourceType.VALUES[chatMessage.getSenderType().intValue()].ordinal()]) {
            case 1:
                return new ChatMessageSourceGroup(chatMessage);
            case 2:
                return new ChatMessageSourceObject(chatMessage);
            case 3:
                return new ChatMessageSourceSystem();
            case 4:
                return ChatMessageSourceUnknown.getInstance();
            case 5:
                return new ChatMessageSourceUser(chatMessage);
            default:
                throw new IllegalArgumentException("Unknown message type");
        }
    }

    @Nonnull
    public abstract ChatterID getDefaultChatter(UUID uuid);

    @Nullable
    public abstract String getSourceName(@Nonnull UserManager userManager);

    @Nonnull
    public abstract ChatMessageSourceType getSourceType();

    @Nullable
    public abstract UUID getSourceUUID();

    public void serializeTo(@Nonnull ChatMessage chatMessage) {
        chatMessage.setSenderType(Integer.valueOf(getSourceType().ordinal()));
    }
}
