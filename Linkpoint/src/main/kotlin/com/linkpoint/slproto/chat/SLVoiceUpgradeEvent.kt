package com.linkpoint.slproto.chat

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.linkpoint.R
import com.linkpoint.dao.ChatMessage
import com.linkpoint.slproto.chat.generic.SLChatEvent
import com.linkpoint.slproto.chat.generic.SLChatYesNoEvent
import com.linkpoint.slproto.users.chatsrc.ChatMessageSourceUnknown
import com.linkpoint.slproto.users.manager.UserManager
import java.util.UUID
import javax.annotation.Nonnull

val class SLVoiceUpgradeEvent : SLChatYesNoEvent() {
    private val Boolean isInstall
    private val String upgradeURL

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public SLVoiceUpgradeEvent(ChatMessage chatMessage, UUID uuid) {
        super(chatMessage, uuid)
        val z: Boolean = false
        this.upgradeURL = chatMessage.getItemName()
        this.isInstall = chatMessage.getAssetType().intValue() != 0 ? true : z
    }

    public SLVoiceUpgradeEvent(UUID uuid, String str, Boolean z, String str2) {
        super(ChatMessageSourceUnknown.getInstance(), uuid, str)
        this.upgradeURL = str2
        this.isInstall = z
    }

    /* access modifiers changed from: protected */
    public SLChatEvent.ChatMessageType getMessageType() {
        return SLChatEvent.ChatMessageType.VoiceUpgrade
    }

     public fun getNoButton(context: Context): String {
        return context.getString(R.string.voice_upgrade_no)
    }

     public fun getNoMessage(context: Context): String {
        return this.isInstall ? context.getString(R.string.voice_install_declined) : context.getString(R.string.voice_upgrade_declined)
    }

     public fun getQuestion(context: Context): String {
        return this.isInstall ? context.getString(R.string.install_now_question) : context.getString(R.string.upgrade_now_question)
    }

     public fun getText(context: Context, userManager: UserManager): String {
        return this.text
    }

     public fun getYesButton(context: Context): String {
        return this.isInstall ? context.getString(R.string.voice_install_yes) : context.getString(R.string.voice_upgrade_yes)
    }

     public fun getYesMessage(context: Context): String {
        return ""
    }

     public fun isObjectPopup(): Boolean {
        return false
    }

    /* access modifiers changed from: protected */
    fun onNoAction(context: Context, userManager: UserManager) {
        super.onNoAction(context, userManager)
        userManager.getObjectPopupsManager().cancelObjectPopup(this)
    }

    fun onYesAction(context: Context, userManager: UserManager) {
        super.onYesAction(context, userManager)
        userManager.getObjectPopupsManager().cancelObjectPopup(this)
        val intent: Intent = Intent("android.intent.action.VIEW")
        intent.setData(Uri.parse(this.upgradeURL))
        context.startActivity(intent)
    }

    fun serializeToDatabaseObject(chatMessage: ChatMessage) {
        super.serializeToDatabaseObject(chatMessage)
        chatMessage.setItemName(this.upgradeURL)
        chatMessage.setAssetType(Integer.valueOf(this.isInstall ? 1 : 0))
    }
}
