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
import javax.annotation.Nonnull

abstract class SLChatDialogEvent : SLChatTextEvent() {
    protected val Int chatChannel
    protected Boolean ignored = false

    public SLChatDialogEvent(ChatMessage chatMessage, UUID uuid) {
        super(chatMessage, uuid)
        this.chatChannel = chatMessage.getChatChannel().intValue()
        this.ignored = chatMessage.getDialogIgnored().booleanValue()
    }

    public SLChatDialogEvent(ScriptDialog scriptDialog, UUID uuid) {
        super((ChatMessageSource) ChatMessageSourceObject(scriptDialog.Data_Field.ObjectID, SLMessage.stringFromVariableOEM(scriptDialog.Data_Field.ObjectName)), uuid, sanitizeDialogText(SLMessage.stringFromVariableUTF(scriptDialog.Data_Field.Message)))
        this.chatChannel = scriptDialog.Data_Field.ChatChannel
    }

    @JvmStatic
private String sanitizeDialogText(String str) {
        while (str.contains("\n\n")) {
            str = str.replace("\n\n", "\n")
        }
        return str.trim()
    }

    /* access modifiers changed from: protected */
    public Unit onDialogIgnored(UserManager userManager) {
        this.ignored = true
    }

    public Unit serializeToDatabaseObject(ChatMessage chatMessage) {
        super.serializeToDatabaseObject(chatMessage)
        chatMessage.setChatChannel(Integer.valueOf(this.chatChannel))
        chatMessage.setDialogIgnored(Boolean.valueOf(this.ignored))
    }

    public abstract Unit showDialog(Context context, UserManager userManager)
}
