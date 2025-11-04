// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.chat;

import com.lumiyaviewer.lumiya.slproto.modules.SLModules;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import android.content.Context;
import com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent;
import com.lumiyaviewer.lumiya.slproto.users.chatsrc.ChatMessageSource;
import javax.annotation.Nonnull;
import java.util.UUID;
import com.lumiyaviewer.lumiya.dao.ChatMessage;
import com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatYesNoEvent;

public final class SLMissedVoiceCallEvent extends SLChatYesNoEvent
{
    public SLMissedVoiceCallEvent(final ChatMessage chatMessage, @Nonnull final UUID uuid) {
        super(chatMessage, uuid);
    }
    
    public SLMissedVoiceCallEvent(@Nonnull final ChatMessageSource chatMessageSource, @Nonnull final UUID uuid, final String s) {
        super(chatMessageSource, uuid, s);
    }
    
    @Nonnull
    @Override
    protected ChatMessageType getMessageType() {
        return ChatMessageType.MissedVoiceCall;
    }
    
    public String getNoButton(final Context context) {
        return context.getString(2131296700);
    }
    
    public String getNoMessage(final Context context) {
        return context.getString(2131296699);
    }
    
    public String getQuestion(final Context context) {
        return context.getString(2131296701);
    }
    
    public String getYesButton(final Context context) {
        return context.getString(2131296702);
    }
    
    public String getYesMessage(final Context context) {
        return "";
    }
    
    @Override
    public void onYesAction(final Context context, final UserManager userManager) {
        super.onYesAction(context, userManager);
        final SLAgentCircuit activeAgentCircuit = userManager.getActiveAgentCircuit();
        if (activeAgentCircuit != null) {
            final SLModules modules = activeAgentCircuit.getModules();
            if (modules != null) {
                modules.voice.userVoiceChatRequest(this.source.getSourceUUID());
            }
        }
    }
    
    @Override
    public void serializeToDatabaseObject(@Nonnull final ChatMessage chatMessage) {
        super.serializeToDatabaseObject(chatMessage);
    }
}
