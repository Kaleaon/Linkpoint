package com.lumiyaviewer.lumiya.slproto.chat

import android.content.Context
import android.content.SharedPreferences
import com.lumiyaviewer.lumiya.LumiyaApp
import com.lumiyaviewer.lumiya.R
import com.lumiyaviewer.lumiya.dao.ChatMessage
import com.lumiyaviewer.lumiya.slproto.SLMessage
import com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent
import com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatYesNoEvent
import com.lumiyaviewer.lumiya.slproto.messages.ChatFromSimulator
import com.lumiyaviewer.lumiya.slproto.users.chatsrc.ChatMessageSourceObject
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager
import java.util.UUID
import androidx.annotation.NonNull

class SLEnableRLVOfferEvent : SLChatYesNoEvent {
    SLEnableRLVOfferEvent(ChatMessage chatMessage, @NonNull UUID uuid) {
        super(chatMessage, uuid)
    }

    SLEnableRLVOfferEvent(ChatFromSimulator chatFromSimulator, @NonNull UUID uuid) {
        super(ChatMessageSourceObject(chatFromSimulator.ChatData_Field.SourceID, SLMessage.stringFromVariableOEM(chatFromSimulator.ChatData_Field.FromName)), uuid, SLMessage.stringFromVariableUTF(chatFromSimulator.ChatData_Field.Message))
    }

    /* access modifiers changed from: protected */
    @NonNull
    SLChatEvent.ChatMessageType getMessageType() {
        return SLChatEvent.ChatMessageType.EnableRLVOffer
    }

    String getNoButton(Context context) {
        return context.getString(R.string.enable_rlv_no)
    }

    String getNoMessage(Context context) {
        return context.getString(R.string.enable_rlv_declined)
    }

    String getQuestion(Context context) {
        return context.getString(R.string.enable_rlv_question)
    }

    String getText(Context context, @NonNull UserManager userManager) {
        return context.getString(R.string.rlv_enable_chat_message)
    }

    String getYesButton(Context context) {
        return context.getString(R.string.enable_rlv_yes)
    }

    String getYesMessage(Context context) {
        return context.getString(R.string.enable_rlv_accepted)
    }

    Boolean isObjectPopup() {
        return true
    }

    /* access modifiers changed from: protected */
    Unit onNoAction(Context context, UserManager userManager) {
        super.onNoAction(context, userManager)
        userManager.getObjectPopupsManager().cancelObjectPopup(this)
    }

    Unit onYesAction(Context context, UserManager userManager) {
        super.onYesAction(context, userManager)
        SharedPreferences.Editor edit = LumiyaApp.getDefaultSharedPreferences().edit()
        edit.putBoolean("rlv_enabled", true)
        edit.commit()
        userManager.getObjectPopupsManager().cancelObjectPopup(this)
    }

    Unit serializeToDatabaseObject(@NonNull ChatMessage chatMessage) {
        super.serializeToDatabaseObject(chatMessage)
    }
}
