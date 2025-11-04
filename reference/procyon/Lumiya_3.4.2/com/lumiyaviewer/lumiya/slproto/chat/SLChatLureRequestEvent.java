// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.chat;

import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import com.google.common.base.Strings;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import android.content.Context;
import com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent;
import com.lumiyaviewer.lumiya.slproto.messages.ImprovedInstantMessage;
import com.lumiyaviewer.lumiya.slproto.users.chatsrc.ChatMessageSource;
import javax.annotation.Nonnull;
import java.util.UUID;
import com.lumiyaviewer.lumiya.dao.ChatMessage;
import com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatYesNoEvent;

public final class SLChatLureRequestEvent extends SLChatYesNoEvent
{
    public SLChatLureRequestEvent(final ChatMessage chatMessage, @Nonnull final UUID uuid) {
        super(chatMessage, uuid);
    }
    
    public SLChatLureRequestEvent(final ChatMessageSource chatMessageSource, @Nonnull final UUID uuid, final ImprovedInstantMessage improvedInstantMessage) {
        super(chatMessageSource, uuid, improvedInstantMessage, null);
    }
    
    @Nonnull
    @Override
    protected ChatMessageType getMessageType() {
        return ChatMessageType.LureRequest;
    }
    
    public String getNoButton(final Context context) {
        return context.getString(2131297094);
    }
    
    public String getNoMessage(final Context context) {
        return context.getString(2131297092);
    }
    
    public String getQuestion(final Context context) {
        return context.getString(2131297095);
    }
    
    @Override
    public String getText(final Context context, @Nonnull final UserManager userManager) {
        final String string = context.getString(2131297093);
        String s;
        if (!Strings.isNullOrEmpty(this.text)) {
            s = string + ": " + this.text;
        }
        else {
            s = string + ".";
        }
        return s;
    }
    
    public String getYesButton(final Context context) {
        return context.getString(2131297096);
    }
    
    public String getYesMessage(final Context context) {
        return context.getString(2131297091);
    }
    
    @Override
    protected boolean isActionMessage(@Nonnull final UserManager userManager) {
        return true;
    }
    
    @Override
    public void onYesAction(final Context context, final UserManager userManager) {
        super.onYesAction(context, userManager);
        final UUID sourceUUID = this.source.getSourceUUID();
        final SLAgentCircuit activeAgentCircuit = userManager.getActiveAgentCircuit();
        if (sourceUUID != null && activeAgentCircuit != null) {
            String s;
            if (Strings.isNullOrEmpty(s = activeAgentCircuit.getRegionName())) {
                s = context.getString(2131297122);
            }
            activeAgentCircuit.OfferTeleport(sourceUUID, context.getString(2131296654, new Object[] { s }));
        }
    }
    
    @Override
    public void serializeToDatabaseObject(@Nonnull final ChatMessage chatMessage) {
        super.serializeToDatabaseObject(chatMessage);
    }
}
