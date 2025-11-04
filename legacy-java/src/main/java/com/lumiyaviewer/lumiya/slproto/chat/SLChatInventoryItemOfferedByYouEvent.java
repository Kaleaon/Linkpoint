package com.lumiyaviewer.lumiya.slproto.chat;

import android.content.Context;
import com.lumiyaviewer.lumiya.R;
import com.lumiyaviewer.lumiya.dao.ChatMessage;
import com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent;
import com.lumiyaviewer.lumiya.slproto.users.chatsrc.ChatMessageSourceUnknown;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import java.util.UUID;
import javax.annotation.Nonnull;

/* loaded from: classes.dex */
public final class SLChatInventoryItemOfferedByYouEvent extends SLChatEvent {
    private final String itemName;

    public SLChatInventoryItemOfferedByYouEvent(ChatMessage chatMessage, @Nonnull UUID uuid) {
        super(chatMessage, uuid);
        this.itemName = chatMessage.getItemName();
    }

    public SLChatInventoryItemOfferedByYouEvent(@Nonnull UUID uuid, String str) {
        super(ChatMessageSourceUnknown.getInstance(), uuid);
        this.itemName = str;
    }

    @Override // com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent
    @Nonnull
    protected SLChatEvent.ChatMessageType getMessageType() {
        return SLChatEvent.ChatMessageType.InventoryItemOfferedByYou;
    }

    @Override // com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent
    protected String getText(Context context, @Nonnull UserManager userManager) {
        return context.getString(R.string.chat_inventory_own_offer_format, this.itemName);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent
    public SLChatEvent.ChatMessageViewType getViewType() {
        return SLChatEvent.ChatMessageViewType.VIEW_TYPE_NORMAL;
    }

    @Override // com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent
    protected boolean isActionMessage(@Nonnull UserManager userManager) {
        return false;
    }

    @Override // com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent
    public void serializeToDatabaseObject(@Nonnull ChatMessage chatMessage) {
        super.serializeToDatabaseObject(chatMessage);
        chatMessage.setItemName(this.itemName);
    }
}
