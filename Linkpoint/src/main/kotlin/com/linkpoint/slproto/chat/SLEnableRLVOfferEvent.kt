package com.linkpoint.slproto.chat

import android.content.Context
import android.content.SharedPreferences
import com.linkpoint.LinkpointApp
import com.linkpoint.R
import com.linkpoint.dao.ChatMessage
import com.linkpoint.slproto.SLMessage
import com.linkpoint.slproto.chat.generic.SLChatEvent
import com.linkpoint.slproto.chat.generic.SLChatYesNoEvent
import com.linkpoint.slproto.messages.ChatFromSimulator
import com.linkpoint.slproto.users.chatsrc.ChatMessageSourceObject
import com.linkpoint.slproto.users.manager.UserManager
import java.util.UUID
import javax.annotation.Nonnull

val class SLEnableRLVOfferEvent : SLChatYesNoEvent() {
    public SLEnableRLVOfferEvent(ChatMessage chatMessage, UUID uuid) {
        super(chatMessage, uuid)
    }

    public SLEnableRLVOfferEvent(ChatFromSimulator chatFromSimulator, UUID uuid) {
        super(ChatMessageSourceObject(chatFromSimulator.ChatData_Field.SourceID, SLMessage.stringFromVariableOEM(chatFromSimulator.ChatData_Field.FromName)), uuid, SLMessage.stringFromVariableUTF(chatFromSimulator.ChatData_Field.Message))
    }

    /* access modifiers changed from: protected */
    public SLChatEvent.ChatMessageType getMessageType() {
        return SLChatEvent.ChatMessageType.EnableRLVOffer
    }

    public String getNoButton(Context context) {
        return context.getString(R.string.enable_rlv_no)
    }

    public String getNoMessage(Context context) {
        return context.getString(R.string.enable_rlv_declined)
    }

    public String getQuestion(Context context) {
        return context.getString(R.string.enable_rlv_question)
    }

    public String getText(Context context, UserManager userManager) {
        return context.getString(R.string.rlv_enable_chat_message)
    }

    public String getYesButton(Context context) {
        return context.getString(R.string.enable_rlv_yes)
    }

    public String getYesMessage(Context context) {
        return context.getString(R.string.enable_rlv_accepted)
    }

    public Boolean isObjectPopup() {
        return true
    }

    /* access modifiers changed from: protected */
    public Unit onNoAction(Context context, UserManager userManager) {
        super.onNoAction(context, userManager)
        userManager.getObjectPopupsManager().cancelObjectPopup(this)
    }

    public Unit onYesAction(Context context, UserManager userManager) {
        super.onYesAction(context, userManager)
        SharedPreferences.Editor edit = LinkpointApp.getDefaultSharedPreferences().edit()
        edit.putBoolean("rlv_enabled", true)
        edit.commit()
        userManager.getObjectPopupsManager().cancelObjectPopup(this)
    }

    public Unit serializeToDatabaseObject(ChatMessage chatMessage) {
        super.serializeToDatabaseObject(chatMessage)
    }
}
