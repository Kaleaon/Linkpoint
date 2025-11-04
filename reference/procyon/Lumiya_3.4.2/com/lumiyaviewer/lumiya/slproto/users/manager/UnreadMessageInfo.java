// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.users.manager;

import javax.annotation.Nullable;
import com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent;

public abstract class UnreadMessageInfo
{
    public static UnreadMessageInfo create(final int n, @Nullable final SLChatEvent slChatEvent) {
        return new AutoValue_UnreadMessageInfo(n, slChatEvent);
    }
    
    @Nullable
    public abstract SLChatEvent lastMessage();
    
    public abstract int unreadCount();
}
