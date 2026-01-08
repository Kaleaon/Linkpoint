package com.lumiyaviewer.lumiya.slproto.chat

import android.content.Context
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

class SLEnableRLVOfferEvent : SLChatYesNoEvent {

    constructor(chatMessage: ChatMessage, uuid: UUID) : super(chatMessage, uuid)

    constructor(chatFromSimulator: ChatFromSimulator, uuid: UUID) : super(
        ChatMessageSourceObject(
            chatFromSimulator.ChatData_Field.SourceID,
            SLMessage.stringFromVariableOEM(chatFromSimulator.ChatData_Field.FromName)
        ),
        uuid,
        SLMessage.stringFromVariableUTF(chatFromSimulator.ChatData_Field.Message)
    )

    override fun getMessageType(): ChatMessageType {
        return ChatMessageType.EnableRLVOffer
    }

    override fun getNoButton(context: Context): String {
        return context.getString(R.string.enable_rlv_no)
    }

    override fun getNoMessage(context: Context): String {
        return context.getString(R.string.enable_rlv_declined)
    }

    override fun getQuestion(context: Context): String {
        return context.getString(R.string.enable_rlv_question)
    }

    fun getText(context: Context, userManager: UserManager): String {
        return context.getString(R.string.rlv_enable_chat_message)
    }

    override fun getYesButton(context: Context): String {
        return context.getString(R.string.enable_rlv_yes)
    }

    override fun getYesMessage(context: Context): String {
        return context.getString(R.string.enable_rlv_accepted)
    }

    fun isObjectPopup(): Boolean {
        return true
    }

    override fun onNoAction(context: Context, userManager: UserManager) {
        super.onNoAction(context, userManager)
        userManager.objectPopupsManager.cancelObjectPopup(this)
    }

    override fun onYesAction(context: Context, userManager: UserManager) {
        super.onYesAction(context, userManager)
        val edit = LumiyaApp.getDefaultSharedPreferences().edit()
        edit.putBoolean("rlv_enabled", true)
        edit.commit()
        userManager.objectPopupsManager.cancelObjectPopup(this)
    }

    override fun serializeToDatabaseObject(chatMessage: ChatMessage) {
        super.serializeToDatabaseObject(chatMessage)
    }
}
