package com.lumiyaviewer.lumiya.slproto.users.manager;

import com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent;
import javax.annotation.Nullable;

/* loaded from: classes.dex */
public abstract class UnreadMessageInfo {
    public static UnreadMessageInfo create(int i, @Nullable SLChatEvent sLChatEvent) {
        return new AutoValue_UnreadMessageInfo(i, sLChatEvent);
    }

    @Nullable
    public abstract SLChatEvent lastMessage();

    public abstract int unreadCount();
}
