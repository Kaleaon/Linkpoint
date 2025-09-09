package com.lumiyaviewer.lumiya.slproto.chat.generic;

import android.content.Context;
import com.lumiyaviewer.lumiya.dao.ChatMessage;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import com.lumiyaviewer.lumiya.slproto.chat.SLChatTextEvent;
import com.lumiyaviewer.lumiya.slproto.messages.ScriptDialog;
import com.lumiyaviewer.lumiya.slproto.users.chatsrc.ChatMessageSource;
import com.lumiyaviewer.lumiya.slproto.users.chatsrc.ChatMessageSourceObject;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import java.util.UUID;
import javax.annotation.Nonnull;

public abstract class SLChatDialogEvent extends SLChatTextEvent {
    protected final int chatChannel;
    protected boolean ignored = false;

    public SLChatDialogEvent(ChatMessage chatMessage, @Nonnull UUID uuid) {
        super(chatMessage, uuid);
        this.chatChannel = chatMessage.getChatChannel().intValue();
        this.ignored = chatMessage.getDialogIgnored().booleanValue();
    }

    public SLChatDialogEvent(ScriptDialog scriptDialog, @Nonnull UUID uuid) {
        super((ChatMessageSource) new ChatMessageSourceObject(scriptDialog.Data_Field.ObjectID, SLMessage.stringFromVariableOEM(scriptDialog.Data_Field.ObjectName)), uuid, sanitizeDialogText(SLMessage.stringFromVariableUTF(scriptDialog.Data_Field.Message)));
        this.chatChannel = scriptDialog.Data_Field.ChatChannel;
    }

    private static String sanitizeDialogText(String str) {
        while (str.contains("\n\n")) {
            str = str.replace("\n\n", "\n");
        }
        return str.trim();
    }

    /* access modifiers changed from: protected */
    public void onDialogIgnored(UserManager userManager) {
        this.ignored = true;
    }

    public void serializeToDatabaseObject(@Nonnull ChatMessage chatMessage) {
        super.serializeToDatabaseObject(chatMessage);
        chatMessage.setChatChannel(Integer.valueOf(this.chatChannel));
        chatMessage.setDialogIgnored(Boolean.valueOf(this.ignored));
    }

    public abstract void showDialog(Context context, UserManager userManager);
}
