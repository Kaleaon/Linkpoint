package com.lumiyaviewer.lumiya.slproto.chat

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.lumiyaviewer.lumiya.R
import com.lumiyaviewer.lumiya.dao.ChatMessage
import com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent
import com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatYesNoEvent
import com.lumiyaviewer.lumiya.slproto.users.chatsrc.ChatMessageSourceUnknown
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager
import java.util.UUID

class SLVoiceUpgradeEvent : SLChatYesNoEvent {
    private var isInstall: Boolean = false
    private var upgradeURL: String? = null

    constructor(chatMessage: ChatMessage, uuid: UUID) : super(chatMessage, uuid) {
        this.upgradeURL = chatMessage.itemName
        this.isInstall = chatMessage.assetType != 0
    }

    constructor(uuid: UUID, str: String, z: Boolean, str2: String) : super(
        ChatMessageSourceUnknown.getInstance(),
        uuid,
        str
    ) {
        this.upgradeURL = str2
        this.isInstall = z
    }

    override fun getMessageType(): ChatMessageType {
        return ChatMessageType.VoiceUpgrade
    }

    override fun getNoButton(context: Context): String {
        return context.getString(R.string.voice_upgrade_no)
    }

    override fun getNoMessage(context: Context): String {
        return if (isInstall) context.getString(R.string.voice_install_declined) else context.getString(R.string.voice_upgrade_declined)
    }

    override fun getQuestion(context: Context): String {
        return if (isInstall) context.getString(R.string.install_now_question) else context.getString(R.string.upgrade_now_question)
    }

    override fun getText(context: Context, userManager: UserManager): String? {
        return this.getMessage().toString()
    }

    override fun getYesButton(context: Context): String {
        return if (isInstall) context.getString(R.string.voice_install_yes) else context.getString(R.string.voice_upgrade_yes)
    }

    override fun getYesMessage(context: Context): String {
        return ""
    }

    fun isObjectPopup(): Boolean {
        return false
    }

    override fun onNoAction(context: Context, userManager: UserManager) {
        super.onNoAction(context, userManager)
        userManager.objectPopupsManager.cancelObjectPopup(this)
    }

    override fun onYesAction(context: Context, userManager: UserManager) {
        super.onYesAction(context, userManager)
        userManager.objectPopupsManager.cancelObjectPopup(this)
        try {
            val intent = Intent("android.intent.action.VIEW")
            intent.data = Uri.parse(this.upgradeURL)
            context.startActivity(intent)
        } catch (e: Exception) {
            // ignore
        }
    }

    override fun serializeToDatabaseObject(chatMessage: ChatMessage) {
        super.serializeToDatabaseObject(chatMessage)
        chatMessage.itemName = this.upgradeURL
        chatMessage.assetType = if (this.isInstall) 1 else 0
    }
}
