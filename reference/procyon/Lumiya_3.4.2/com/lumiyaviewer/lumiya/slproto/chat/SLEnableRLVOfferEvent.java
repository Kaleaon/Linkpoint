// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.chat;

import android.content.SharedPreferences$Editor;
import com.lumiyaviewer.lumiya.LumiyaApp;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import android.content.Context;
import com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent;
import com.lumiyaviewer.lumiya.slproto.users.chatsrc.ChatMessageSource;
import com.lumiyaviewer.lumiya.slproto.users.chatsrc.ChatMessageSourceObject;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import com.lumiyaviewer.lumiya.slproto.messages.ChatFromSimulator;
import javax.annotation.Nonnull;
import java.util.UUID;
import com.lumiyaviewer.lumiya.dao.ChatMessage;
import com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatYesNoEvent;

public final class SLEnableRLVOfferEvent extends SLChatYesNoEvent
{
    public SLEnableRLVOfferEvent(final ChatMessage chatMessage, @Nonnull final UUID uuid) {
        super(chatMessage, uuid);
    }
    
    public SLEnableRLVOfferEvent(final ChatFromSimulator chatFromSimulator, @Nonnull final UUID uuid) {
        super(new ChatMessageSourceObject(chatFromSimulator.ChatData_Field.SourceID, SLMessage.stringFromVariableOEM(chatFromSimulator.ChatData_Field.FromName)), uuid, SLMessage.stringFromVariableUTF(chatFromSimulator.ChatData_Field.Message));
    }
    
    @Nonnull
    @Override
    protected ChatMessageType getMessageType() {
        return ChatMessageType.EnableRLVOffer;
    }
    
    public String getNoButton(final Context context) {
        return context.getString(2131296528);
    }
    
    public String getNoMessage(final Context context) {
        return context.getString(2131296527);
    }
    
    public String getQuestion(final Context context) {
        return context.getString(2131296529);
    }
    
    @Override
    public String getText(final Context context, @Nonnull final UserManager userManager) {
        return context.getString(2131296938);
    }
    
    public String getYesButton(final Context context) {
        return context.getString(2131296530);
    }
    
    public String getYesMessage(final Context context) {
        return context.getString(2131296526);
    }
    
    @Override
    public boolean isObjectPopup() {
        return true;
    }
    
    @Override
    protected void onNoAction(final Context context, final UserManager userManager) {
        super.onNoAction(context, userManager);
        userManager.getObjectPopupsManager().cancelObjectPopup(this);
    }
    
    @Override
    public void onYesAction(final Context context, final UserManager userManager) {
        super.onYesAction(context, userManager);
        final SharedPreferences$Editor edit = LumiyaApp.getDefaultSharedPreferences().edit();
        edit.putBoolean("rlv_enabled", true);
        edit.commit();
        userManager.getObjectPopupsManager().cancelObjectPopup(this);
    }
    
    @Override
    public void serializeToDatabaseObject(@Nonnull final ChatMessage chatMessage) {
        super.serializeToDatabaseObject(chatMessage);
    }
}
