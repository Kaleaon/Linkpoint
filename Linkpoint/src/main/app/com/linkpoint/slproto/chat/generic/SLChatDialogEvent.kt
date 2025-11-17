package com.linkpoint.slproto.chat.generic

import android.content.Context
import com.linkpoint.dao.ChatMessage
import com.linkpoint.slproto.SLMessage
import com.linkpoint.slproto.chat.SLChatTextEvent
import com.linkpoint.slproto.messages.ScriptDialog
import com.linkpoint.slproto.users.chatsrc.ChatMessageSource
import com.linkpoint.slproto.users.chatsrc.ChatMessageSourceObject
import com.linkpoint.slproto.users.manager.UserManager
import java.util.UUID
import androidx.annotation.NonNull

abstract class SLChatDialogEvent : SLChatTextEvent {
    protected Int chatChannel
    protected Boolean ignored = false

    SLChatDialogEvent(ChatMessage chatMessage, @NonNull UUID uuid) {
        super(chatMessage, uuid)
        this.chatChannel = chatMessage.getChatChannel().intValue()
        this.ignored = chatMessage.getDialogIgnored().booleanValue()
    }

    SLChatDialogEvent(ScriptDialog scriptDialog, @NonNull UUID uuid) {
        super((ChatMessageSource) ChatMessageSourceObject(scriptDialog.Data_Field.ObjectID, SLMessage.stringFromVariableOEM(scriptDialog.Data_Field.ObjectName)), uuid, sanitizeDialogText(SLMessage.stringFromVariableUTF(scriptDialog.Data_Field.Message)))
        this.chatChannel = scriptDialog.Data_Field.ChatChannel
    }

    private String sanitizeDialogText(String str) {
        while (str.contains("\n\n")) {
            str = str.replace("\n\n", "\n")
        }
        return str.trim()
    }

    /* access modifiers changed from: protected */
    Unit onDialogIgnored(UserManager userManager) {
        this.ignored = true
    }

    Unit serializeToDatabaseObject(@NonNull ChatMessage chatMessage) {
        super.serializeToDatabaseObject(chatMessage)
        chatMessage.setChatChannel(Integer.valueOf(this.chatChannel))
        chatMessage.setDialogIgnored(Boolean.valueOf(this.ignored))
    }

    abstract Unit showDialog(Context context, UserManager userManager)
}
