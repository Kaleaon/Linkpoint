// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.chat;

import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import android.content.Context;
import com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent;
import com.lumiyaviewer.lumiya.slproto.inventory.SLAssetType;
import com.lumiyaviewer.lumiya.slproto.messages.ImprovedInstantMessage;
import com.lumiyaviewer.lumiya.slproto.users.chatsrc.ChatMessageSource;
import javax.annotation.Nonnull;
import java.util.UUID;
import com.lumiyaviewer.lumiya.dao.ChatMessage;

public final class SLChatInventoryItemOfferedByGroupNoticeEvent extends SLChatInventoryItemOfferedEvent
{
    public SLChatInventoryItemOfferedByGroupNoticeEvent(final ChatMessage chatMessage, @Nonnull final UUID uuid) {
        super(chatMessage, uuid);
    }
    
    public SLChatInventoryItemOfferedByGroupNoticeEvent(@Nonnull final ChatMessageSource chatMessageSource, @Nonnull final UUID uuid, final ImprovedInstantMessage improvedInstantMessage, final String s, final SLAssetType slAssetType) {
        super(chatMessageSource, uuid, improvedInstantMessage, s, SLChatInventoryItemOfferedEvent.extractItemID(improvedInstantMessage), slAssetType);
    }
    
    @Nonnull
    @Override
    protected ChatMessageType getMessageType() {
        return ChatMessageType.InventoryItemOfferedByGroupNotice;
    }
    
    @Override
    public String getText(final Context context, @Nonnull final UserManager userManager) {
        return context.getString(2131296583, new Object[] { this.getItemName() });
    }
}
