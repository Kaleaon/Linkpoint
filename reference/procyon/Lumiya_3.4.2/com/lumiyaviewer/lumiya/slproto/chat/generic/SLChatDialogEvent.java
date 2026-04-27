// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.chat.generic;

import android.content.Context;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import com.lumiyaviewer.lumiya.slproto.users.chatsrc.ChatMessageSource;
import com.lumiyaviewer.lumiya.slproto.users.chatsrc.ChatMessageSourceObject;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import com.lumiyaviewer.lumiya.slproto.messages.ScriptDialog;
import javax.annotation.Nonnull;
import java.util.UUID;
import com.lumiyaviewer.lumiya.dao.ChatMessage;
import com.lumiyaviewer.lumiya.slproto.chat.SLChatTextEvent;

public abstract class SLChatDialogEvent extends SLChatTextEvent
{
    protected final int chatChannel;
    protected boolean ignored;
    
    public SLChatDialogEvent(final ChatMessage chatMessage, @Nonnull final UUID uuid) {
        super(chatMessage, uuid);
        this.ignored = false;
        this.chatChannel = chatMessage.getChatChannel();
        this.ignored = chatMessage.getDialogIgnored();
    }
    
    public SLChatDialogEvent(final ScriptDialog scriptDialog, @Nonnull final UUID uuid) {
        super(new ChatMessageSourceObject(scriptDialog.Data_Field.ObjectID, SLMessage.stringFromVariableOEM(scriptDialog.Data_Field.ObjectName)), uuid, sanitizeDialogText(SLMessage.stringFromVariableUTF(scriptDialog.Data_Field.Message)));
        this.ignored = false;
        this.chatChannel = scriptDialog.Data_Field.ChatChannel;
    }
    
    private static String sanitizeDialogText(String replace) {
        while (replace.contains("\n\n")) {
            replace = replace.replace("\n\n", "\n");
        }
        return replace.trim();
    }
    
    protected void onDialogIgnored(final UserManager userManager) {
        this.ignored = true;
    }
    
    @Override
    public void serializeToDatabaseObject(@Nonnull final ChatMessage chatMessage) {
        super.serializeToDatabaseObject(chatMessage);
        chatMessage.setChatChannel(this.chatChannel);
        chatMessage.setDialogIgnored(this.ignored);
    }
    
    public abstract void showDialog(final Context p0, final UserManager p1);
}
