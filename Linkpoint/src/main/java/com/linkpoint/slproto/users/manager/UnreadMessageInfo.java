package com.linkpoint.slproto.users.manager;

import com.linkpoint.slproto.chat.generic.SLChatEvent;
import javax.annotation.Nullable;

public abstract class UnreadMessageInfo {
    public static UnreadMessageInfo create(int i, @Nullable SLChatEvent sLChatEvent) {
        return new AutoValue_UnreadMessageInfo(i, sLChatEvent);
    }

    @Nullable
    public abstract SLChatEvent lastMessage();

    public abstract int unreadCount();
}
