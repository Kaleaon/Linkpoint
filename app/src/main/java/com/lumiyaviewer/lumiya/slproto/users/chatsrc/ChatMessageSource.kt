package com.lumiyaviewer.lumiya.slproto.users.chatsrc

import com.lumiyaviewer.lumiya.dao.ChatMessage
import com.lumiyaviewer.lumiya.slproto.users.ChatterID
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager
import java.util.UUID
import javax.annotation.Nonnull
import javax.annotation.Nullable

abstract class ChatMessageSource {

    /* renamed from: -com-lumiyaviewer-lumiya-slproto-users-chatsrc-ChatMessageSource$ChatMessageSourceTypeSwitchesValues  reason: not valid java name */
    private /* synthetic */ Int[] f151comlumiyaviewerlumiyaslprotouserschatsrcChatMessageSource$ChatMessageSourceTypeSwitchesValues = null

    enum ChatMessageSourceType {
        Unknown,
        System,
        User,
        Group,
        Any
        
        ChatMessageSourceType[] VALUES = null

        {
            VALUES = values()
        }
    }

    /* renamed from: -getcom-lumiyaviewer-lumiya-slproto-users-chatsrc-ChatMessageSource$ChatMessageSourceTypeSwitchesValues  reason: not valid java name */
    private /* synthetic */ Int[] m268getcomlumiyaviewerlumiyaslprotouserschatsrcChatMessageSource$ChatMessageSourceTypeSwitchesValues() {
        if (f151comlumiyaviewerlumiyaslprotouserschatsrcChatMessageSource$ChatMessageSourceTypeSwitchesValues != null) {
            return f151comlumiyaviewerlumiyaslprotouserschatsrcChatMessageSource$ChatMessageSourceTypeSwitchesValues
        }
        Int[] iArr = Int[ChatMessageSourceType.values().length]
        try {
            iArr[ChatMessageSourceType.Group.ordinal()] = 1
        } catch (NoSuchFieldError e) {
        }
        try {
            iArr[ChatMessageSourceType.Any.ordinal()] = 2
        } catch (NoSuchFieldError e2) {
        }
        try {
            iArr[ChatMessageSourceType.System.ordinal()] = 3
        } catch (NoSuchFieldError e3) {
        }
        try {
            iArr[ChatMessageSourceType.Unknown.ordinal()] = 4
        } catch (NoSuchFieldError e4) {
        }
        try {
            iArr[ChatMessageSourceType.User.ordinal()] = 5
        } catch (NoSuchFieldError e5) {
        }
        f151comlumiyaviewerlumiyaslprotouserschatsrcChatMessageSource$ChatMessageSourceTypeSwitchesValues = iArr
        return iArr
    }

    @Nonnull
    ChatMessageSource loadFrom(@Nonnull ChatMessage chatMessage) {
        switch (m268getcomlumiyaviewerlumiyaslprotouserschatsrcChatMessageSource$ChatMessageSourceTypeSwitchesValues()[ChatMessageSourceType.VALUES[chatMessage.getSenderType().intValue()].ordinal()]) {
            case 1:
                return ChatMessageSourceGroup(chatMessage)
            case 2:
                return ChatMessageSourceObject(chatMessage)
            case 3:
                return ChatMessageSourceSystem()
            case 4:
                return ChatMessageSourceUnknown.getInstance()
            case 5:
                return ChatMessageSourceUser(chatMessage)
            default:
                throw IllegalArgumentException("Unknown message type")
        }
    }

    @Nonnull
    abstract ChatterID getDefaultChatter(UUID uuid)

    @Nullable
    abstract String getSourceName(@Nonnull UserManager userManager)

    @Nonnull
    abstract ChatMessageSourceType getSourceType()

    @Nullable
    abstract UUID getSourceUUID()

    Unit serializeTo(@Nonnull ChatMessage chatMessage) {
        chatMessage.setSenderType(Int.valueOf(getSourceType().ordinal()))
    }
}
