// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.chat;

import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import com.lumiyaviewer.lumiya.ui.common.TeleportProgressDialog;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import android.content.Context;
import com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent;
import com.lumiyaviewer.lumiya.slproto.messages.ImprovedInstantMessage;
import com.lumiyaviewer.lumiya.slproto.users.chatsrc.ChatMessageSource;
import javax.annotation.Nonnull;
import com.lumiyaviewer.lumiya.dao.ChatMessage;
import java.util.UUID;
import com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatYesNoEvent;

public final class SLChatLureEvent extends SLChatYesNoEvent
{
    private final UUID lureID;
    
    public SLChatLureEvent(final ChatMessage chatMessage, @Nonnull final UUID uuid) {
        super(chatMessage, uuid);
        this.lureID = chatMessage.getSessionID();
    }
    
    public SLChatLureEvent(@Nonnull final ChatMessageSource chatMessageSource, @Nonnull final UUID uuid, final ImprovedInstantMessage improvedInstantMessage) {
        super(chatMessageSource, uuid, improvedInstantMessage, null);
        this.lureID = improvedInstantMessage.MessageBlock_Field.ID;
    }
    
    @Nonnull
    @Override
    protected ChatMessageType getMessageType() {
        return ChatMessageType.Lure;
    }
    
    public String getNoButton(final Context context) {
        return context.getString(2131297089);
    }
    
    public String getNoMessage(final Context context) {
        return context.getString(2131297088);
    }
    
    public String getQuestion(final Context context) {
        return context.getString(2131297090);
    }
    
    public String getYesButton(final Context context) {
        return context.getString(2131297097);
    }
    
    public String getYesMessage(final Context context) {
        return context.getString(2131297087);
    }
    
    @Override
    public void onYesAction(final Context context, final UserManager userManager) {
        super.onYesAction(context, userManager);
        final SLAgentCircuit activeAgentCircuit = userManager.getActiveAgentCircuit();
        if (activeAgentCircuit != null) {
            new TeleportProgressDialog(context, userManager, 2131297104).show();
            activeAgentCircuit.TeleportToLure(this.lureID);
        }
    }
    
    @Override
    public void serializeToDatabaseObject(@Nonnull final ChatMessage chatMessage) {
        super.serializeToDatabaseObject(chatMessage);
        chatMessage.setSessionID(this.lureID);
    }
}
