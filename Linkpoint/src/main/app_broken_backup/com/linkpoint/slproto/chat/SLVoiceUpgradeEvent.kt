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
import androidx.annotation.NonNull

class SLVoiceUpgradeEvent : SLChatYesNoEvent {
    private Boolean isInstall
    private String upgradeURL

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    SLVoiceUpgradeEvent(ChatMessage chatMessage, @NonNull UUID uuid) {
        super(chatMessage, uuid)
        var z: Boolean = false
        this.upgradeURL = chatMessage.getItemName()
        this.isInstall = chatMessage.getAssetType().intValue() != 0 ? true : z
    }

    SLVoiceUpgradeEvent(@NonNull UUID uuid, String str, Boolean z, String str2) {
        super(ChatMessageSourceUnknown.getInstance(), uuid, str)
        this.upgradeURL = str2
        this.isInstall = z
    }

    /* access modifiers changed from: protected */
    @NonNull
    SLChatEvent.ChatMessageType getMessageType() {
        return SLChatEvent.ChatMessageType.VoiceUpgrade
    }

    fun getNoButton(Context context): String {
        return context.getString(R.string.voice_upgrade_no)
    }

    fun getNoMessage(Context context): String {
        return this.isInstall ? context.getString(R.string.voice_install_declined) : context.getString(R.string.voice_upgrade_declined)
    }

    fun getQuestion(Context context): String {
        return this.isInstall ? context.getString(R.string.install_now_question) : context.getString(R.string.upgrade_now_question)
    }

    fun getText(Context context, @NonNull UserManager userManager): String {
        return this.text
    }

    fun getYesButton(Context context): String {
        return this.isInstall ? context.getString(R.string.voice_install_yes) : context.getString(R.string.voice_upgrade_yes)
    }

    fun getYesMessage(Context context): String {
        return ""
    }

    fun isObjectPopup(): Boolean {
        return false
    }

    /* access modifiers changed from: protected */
    fun onNoAction(Context context, UserManager userManager)  {
        super.onNoAction(context, userManager)
        userManager.getObjectPopupsManager().cancelObjectPopup(this)
    }

    fun onYesAction(Context context, UserManager userManager)  {
        super.onYesAction(context, userManager)
        userManager.getObjectPopupsManager().cancelObjectPopup(this)
        Intent intent = Intent("android.intent.action.VIEW")
        intent.setData(Uri.parse(this.upgradeURL))
        context.startActivity(intent)
    }

    fun serializeToDatabaseObject(@NonNull ChatMessage chatMessage)  {
        super.serializeToDatabaseObject(chatMessage)
        chatMessage.setItemName(this.upgradeURL)
        chatMessage.setAssetType(Integer.valueOf(this.isInstall ? 1 : 0))
    }
}
