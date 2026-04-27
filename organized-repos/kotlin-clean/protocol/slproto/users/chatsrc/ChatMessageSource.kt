package com.linkpoint.slproto.users.chatsrc

import com.linkpoint.dao.ChatMessage
import com.linkpoint.slproto.users.ChatterID
import com.linkpoint.slproto.users.manager.UserManager
import java.util.UUID
import javax.annotation.Nonnull
import javax.annotation.Nullable

abstract class ChatMessageSource {

    /* renamed from: -com-lumiyaviewer-lumiya-slproto-users-chatsrc-ChatMessageSource$ChatMessageSourceTypeSwitchesValues  reason: not valid java name */
    private const val /* synthetic */ IntArray f151comlumiyaviewerlumiyaslprotouserschatsrcChatMessageSource$ChatMessageSourceTypeSwitchesValues = null

    enum class ChatMessageSourceType {
        Unknown,
        System,
        User,
        Group,
        Object
        
        const val ChatMessageSourceType[] VALUES = null

        static {
            VALUES = values()
        }
    }

    /* renamed from: -getcom-lumiyaviewer-lumiya-slproto-users-chatsrc-ChatMessageSource$ChatMessageSourceTypeSwitchesValues  reason: not valid java name */
    @JvmStatic
private /* synthetic */ IntArray m268getcomlumiyaviewerlumiyaslprotouserschatsrcChatMessageSource$ChatMessageSourceTypeSwitchesValues() {
        if (f151comlumiyaviewerlumiyaslprotouserschatsrcChatMessageSource$ChatMessageSourceTypeSwitchesValues != null) {
            return f151comlumiyaviewerlumiyaslprotouserschatsrcChatMessageSource$ChatMessageSourceTypeSwitchesValues
        }
        IntArray iArr = Int[ChatMessageSourceType.values().length]
        try {
            iArr[ChatMessageSourceType.Group.ordinal()] = 1
        } catch (NoSuchFieldError e) {
        }
        try {
            iArr[ChatMessageSourceType.Object.ordinal()] = 2
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

    @JvmStatic
    ChatMessageSource loadFrom(ChatMessage chatMessage) {
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

    public abstract ChatterID getDefaultChatter(UUID uuid)

    public abstract String getSourceName(UserManager userManager)

    public abstract ChatMessageSourceType getSourceType()

    public abstract UUID getSourceUUID()

    fun serializeTo(ChatMessage chatMessage) {
        chatMessage.setSenderType(Integer.valueOf(getSourceType().ordinal()))
    }
}
