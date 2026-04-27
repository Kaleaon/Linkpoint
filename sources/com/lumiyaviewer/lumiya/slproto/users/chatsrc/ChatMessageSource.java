// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.users.chatsrc;

import com.lumiyaviewer.lumiya.dao.ChatMessage;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.users.chatsrc:
//            ChatMessageSourceUnknown, ChatMessageSourceSystem, ChatMessageSourceUser, ChatMessageSourceGroup, 
//            ChatMessageSourceObject

public abstract class ChatMessageSource
{
    public static final class ChatMessageSourceType extends Enum
    {

        private static final ChatMessageSourceType $VALUES[];
        public static final ChatMessageSourceType Group;
        public static final ChatMessageSourceType Object;
        public static final ChatMessageSourceType System;
        public static final ChatMessageSourceType Unknown;
        public static final ChatMessageSourceType User;
        public static final ChatMessageSourceType VALUES[] = values();

        public static ChatMessageSourceType valueOf(String s)
        {
            return (ChatMessageSourceType)Enum.valueOf(com/lumiyaviewer/lumiya/slproto/users/chatsrc/ChatMessageSource$ChatMessageSourceType, s);
        }

        public static ChatMessageSourceType[] values()
        {
            return $VALUES;
        }

        static 
        {
            Unknown = new ChatMessageSourceType("Unknown", 0);
            System = new ChatMessageSourceType("System", 1);
            User = new ChatMessageSourceType("User", 2);
            Group = new ChatMessageSourceType("Group", 3);
            Object = new ChatMessageSourceType("Object", 4);
            $VALUES = (new ChatMessageSourceType[] {
                Unknown, System, User, Group, Object
            });
        }

        private ChatMessageSourceType(String s, int i)
        {
            super(s, i);
        }
    }


    private static final int _2D_com_2D_lumiyaviewer_2D_lumiya_2D_slproto_2D_users_2D_chatsrc_2D_ChatMessageSource$ChatMessageSourceTypeSwitchesValues[];

    private static int[] _2D_getcom_2D_lumiyaviewer_2D_lumiya_2D_slproto_2D_users_2D_chatsrc_2D_ChatMessageSource$ChatMessageSourceTypeSwitchesValues()
    {
        if (_2D_com_2D_lumiyaviewer_2D_lumiya_2D_slproto_2D_users_2D_chatsrc_2D_ChatMessageSource$ChatMessageSourceTypeSwitchesValues != null)
        {
            return _2D_com_2D_lumiyaviewer_2D_lumiya_2D_slproto_2D_users_2D_chatsrc_2D_ChatMessageSource$ChatMessageSourceTypeSwitchesValues;
        }
        int ai[] = new int[ChatMessageSourceType.values().length];
        try
        {
            ai[ChatMessageSourceType.Group.ordinal()] = 1;
        }
        catch (NoSuchFieldError nosuchfielderror4) { }
        try
        {
            ai[ChatMessageSourceType.Object.ordinal()] = 2;
        }
        catch (NoSuchFieldError nosuchfielderror3) { }
        try
        {
            ai[ChatMessageSourceType.System.ordinal()] = 3;
        }
        catch (NoSuchFieldError nosuchfielderror2) { }
        try
        {
            ai[ChatMessageSourceType.Unknown.ordinal()] = 4;
        }
        catch (NoSuchFieldError nosuchfielderror1) { }
        try
        {
            ai[ChatMessageSourceType.User.ordinal()] = 5;
        }
        catch (NoSuchFieldError nosuchfielderror) { }
        _2D_com_2D_lumiyaviewer_2D_lumiya_2D_slproto_2D_users_2D_chatsrc_2D_ChatMessageSource$ChatMessageSourceTypeSwitchesValues = ai;
        return ai;
    }

    public ChatMessageSource()
    {
    }

    public static ChatMessageSource loadFrom(ChatMessage chatmessage)
    {
        ChatMessageSourceType chatmessagesourcetype = ChatMessageSourceType.VALUES[chatmessage.getSenderType().intValue()];
        switch (_2D_getcom_2D_lumiyaviewer_2D_lumiya_2D_slproto_2D_users_2D_chatsrc_2D_ChatMessageSource$ChatMessageSourceTypeSwitchesValues()[chatmessagesourcetype.ordinal()])
        {
        default:
            throw new IllegalArgumentException("Unknown message type");

        case 4: // '\004'
            return ChatMessageSourceUnknown.getInstance();

        case 3: // '\003'
            return new ChatMessageSourceSystem();

        case 5: // '\005'
            return new ChatMessageSourceUser(chatmessage);

        case 1: // '\001'
            return new ChatMessageSourceGroup(chatmessage);

        case 2: // '\002'
            return new ChatMessageSourceObject(chatmessage);
        }
    }

    public abstract ChatterID getDefaultChatter(UUID uuid);

    public abstract String getSourceName(UserManager usermanager);

    public abstract ChatMessageSourceType getSourceType();

    public abstract UUID getSourceUUID();

    public void serializeTo(ChatMessage chatmessage)
    {
        chatmessage.setSenderType(Integer.valueOf(getSourceType().ordinal()));
    }
}
