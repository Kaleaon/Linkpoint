// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.chat;

import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import android.content.Context;
import com.lumiyaviewer.lumiya.slproto.users.chatsrc.ChatMessageSource;
import com.lumiyaviewer.lumiya.slproto.users.chatsrc.ChatMessageSourceUnknown;
import javax.annotation.Nonnull;
import java.util.UUID;
import com.lumiyaviewer.lumiya.dao.ChatMessage;
import com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent;

public final class SLChatInventoryItemOfferedByYouEvent extends SLChatEvent
{
    private final String itemName;
    
    public SLChatInventoryItemOfferedByYouEvent(final ChatMessage chatMessage, @Nonnull final UUID uuid) {
        super(chatMessage, uuid);
        this.itemName = chatMessage.getItemName();
    }
    
    public SLChatInventoryItemOfferedByYouEvent(@Nonnull final UUID uuid, final String itemName) {
        super(ChatMessageSourceUnknown.getInstance(), uuid);
        this.itemName = itemName;
    }
    
    @Nonnull
    @Override
    protected ChatMessageType getMessageType() {
        return ChatMessageType.InventoryItemOfferedByYou;
    }
    
    @Override
    protected String getText(final Context context, @Nonnull final UserManager userManager) {
        return context.getString(2131296431, new Object[] { this.itemName });
    }
    
    @Override
    public ChatMessageViewType getViewType() {
        return ChatMessageViewType.VIEW_TYPE_NORMAL;
    }
    
    @Override
    protected boolean isActionMessage(@Nonnull final UserManager userManager) {
        return false;
    }
    
    public void serializeToDatabaseObject(@Nonnull final ChatMessage chatMessage) {
        super.serializeToDatabaseObject(chatMessage);
        chatMessage.setItemName(this.itemName);
    }
}
