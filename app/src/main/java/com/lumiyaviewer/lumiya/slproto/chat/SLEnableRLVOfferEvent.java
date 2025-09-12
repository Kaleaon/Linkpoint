package com.lumiyaviewer.lumiya.slproto.chat;

import android.content.Context;
import android.content.SharedPreferences;
import com.lumiyaviewer.lumiya.LumiyaApp;
import com.lumiyaviewer.lumiya.R;
import com.lumiyaviewer.lumiya.dao.ChatMessage;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent;
import com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatYesNoEvent;
import com.lumiyaviewer.lumiya.slproto.messages.ChatFromSimulator;
import com.lumiyaviewer.lumiya.slproto.users.chatsrc.ChatMessageSourceObject;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import java.util.UUID;
import javax.annotation.Nonnull;

public final class SLEnableRLVOfferEvent extends SLChatYesNoEvent {
    public SLEnableRLVOfferEvent(ChatMessage chatMessage, @Nonnull UUID uuid) {
        super(chatMessage, uuid);
    }

    public SLEnableRLVOfferEvent(ChatFromSimulator chatFromSimulator, @Nonnull UUID uuid) {
        super(new ChatMessageSourceObject(chatFromSimulator.ChatData_Field.SourceID, SLMessage.stringFromVariableOEM(chatFromSimulator.ChatData_Field.FromName)), uuid, SLMessage.stringFromVariableUTF(chatFromSimulator.ChatData_Field.Message));
    }

    /* access modifiers changed from: protected */
    @Nonnull
    public SLChatEvent.ChatMessageType getMessageType() {
        return SLChatEvent.ChatMessageType.EnableRLVOffer;
    }

    public String getNoButton(Context context) {
        return context.getString(R.string.enable_rlv_no);
    }

    public String getNoMessage(Context context) {
        return context.getString(R.string.enable_rlv_declined);
    }

    public String getQuestion(Context context) {
        return context.getString(R.string.enable_rlv_question);
    }

    public String getText(Context context, @Nonnull UserManager userManager) {
        return context.getString(R.string.rlv_enable_chat_message);
    }

    public String getYesButton(Context context) {
        return context.getString(R.string.enable_rlv_yes);
    }

    public String getYesMessage(Context context) {
        return context.getString(R.string.enable_rlv_accepted);
    }

    public boolean isObjectPopup() {
        return true;
    }

    /* access modifiers changed from: protected */
    public void onNoAction(Context context, UserManager userManager) {
        super.onNoAction(context, userManager);
        userManager.getObjectPopupsManager().cancelObjectPopup(this);
    }

    public void onYesAction(Context context, UserManager userManager) {
        super.onYesAction(context, userManager);
        SharedPreferences.Editor edit = LumiyaApp.getDefaultSharedPreferences().edit();
        edit.putBoolean("rlv_enabled", true);
        edit.commit();
        userManager.getObjectPopupsManager().cancelObjectPopup(this);
    }

    public void serializeToDatabaseObject(@Nonnull ChatMessage chatMessage) {
        super.serializeToDatabaseObject(chatMessage);
    }
}
