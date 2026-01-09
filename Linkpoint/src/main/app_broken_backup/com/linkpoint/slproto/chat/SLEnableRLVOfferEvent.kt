package com.linkpoint.slproto.chat

import android.content.Context
import android.content.SharedPreferences
import com.linkpoint.LumiyaApp
import com.linkpoint.R
import com.linkpoint.dao.ChatMessage
import com.linkpoint.slproto.SLMessage
import com.linkpoint.slproto.chat.generic.SLChatEvent
import com.linkpoint.slproto.chat.generic.SLChatYesNoEvent
import com.linkpoint.slproto.messages.ChatFromSimulator
import com.linkpoint.slproto.users.chatsrc.ChatMessageSourceObject
import com.linkpoint.slproto.users.manager.UserManager
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

    fun getNoButton(Context context): String {
        return context.getString(R.string.enable_rlv_no)
    }

    fun getNoMessage(Context context): String {
        return context.getString(R.string.enable_rlv_declined)
    }

    fun getQuestion(Context context): String {
        return context.getString(R.string.enable_rlv_question)
    }

    fun getText(Context context, @NonNull UserManager userManager): String {
        return context.getString(R.string.rlv_enable_chat_message)
    }

    fun getYesButton(Context context): String {
        return context.getString(R.string.enable_rlv_yes)
    }

    fun getYesMessage(Context context): String {
        return context.getString(R.string.enable_rlv_accepted)
    }

    fun isObjectPopup(): Boolean {
        return true
    }

    /* access modifiers changed from: protected */
    fun onNoAction(Context context, UserManager userManager)  {
        super.onNoAction(context, userManager)
        userManager.getObjectPopupsManager().cancelObjectPopup(this)
    }

    fun onYesAction(Context context, UserManager userManager)  {
        super.onYesAction(context, userManager)
        SharedPreferences.Editor edit = LumiyaApp.getDefaultSharedPreferences().edit()
        edit.putBoolean("rlv_enabled", true)
        edit.commit()
        userManager.getObjectPopupsManager().cancelObjectPopup(this)
    }

    fun serializeToDatabaseObject(@NonNull ChatMessage chatMessage)  {
        super.serializeToDatabaseObject(chatMessage)
    }
}
