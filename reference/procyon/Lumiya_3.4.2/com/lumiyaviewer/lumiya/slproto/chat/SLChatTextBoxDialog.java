// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.chat;

import com.lumiyaviewer.lumiya.ui.common.TextFieldDialogBuilder;
import android.content.Context;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent;
import com.lumiyaviewer.lumiya.slproto.chat.generic.ChatTextBoxViewHolder;
import javax.annotation.Nullable;
import com.lumiyaviewer.lumiya.ui.chat.ChatEventTimestampUpdater;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import com.lumiyaviewer.lumiya.slproto.chat.generic.ChatEventViewHolder;
import com.lumiyaviewer.lumiya.slproto.messages.ScriptDialog;
import javax.annotation.Nonnull;
import java.util.UUID;
import com.lumiyaviewer.lumiya.dao.ChatMessage;
import com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatDialogEvent;

public final class SLChatTextBoxDialog extends SLChatDialogEvent
{
    private String enteredValue;
    private final int textBoxButtonIndex;
    
    public SLChatTextBoxDialog(final ChatMessage chatMessage, @Nonnull final UUID uuid) {
        super(chatMessage, uuid);
        this.enteredValue = null;
        this.textBoxButtonIndex = chatMessage.getTextBoxButtonIndex();
        this.enteredValue = chatMessage.getDialogSelectedOption();
    }
    
    public SLChatTextBoxDialog(final ScriptDialog scriptDialog, @Nonnull final UUID uuid, final int textBoxButtonIndex) {
        super(scriptDialog, uuid);
        this.enteredValue = null;
        this.textBoxButtonIndex = textBoxButtonIndex;
    }
    
    @Override
    public void bindViewHolder(final ChatEventViewHolder chatEventViewHolder, final UserManager userManager, @Nullable final ChatEventTimestampUpdater chatEventTimestampUpdater) {
        super.bindViewHolder(chatEventViewHolder, userManager, chatEventTimestampUpdater);
        if (chatEventViewHolder instanceof ChatTextBoxViewHolder) {
            final ChatTextBoxViewHolder chatTextBoxViewHolder = (ChatTextBoxViewHolder)chatEventViewHolder;
            if (this.enteredValue != null || this.ignored) {
                if (this.ignored) {
                    chatTextBoxViewHolder.dialogResultTextView.setText(2131296510);
                }
                else {
                    chatTextBoxViewHolder.dialogResultTextView.setText((CharSequence)chatTextBoxViewHolder.dialogResultTextView.getContext().getString(2131297105, new Object[] { this.enteredValue }));
                }
                chatTextBoxViewHolder.dialogResultTextView.setVisibility(0);
                chatTextBoxViewHolder.dialogButtonsLayout.setVisibility(8);
            }
            else {
                chatTextBoxViewHolder.dialogResultTextView.setVisibility(8);
                chatTextBoxViewHolder.dialogButtonsLayout.setVisibility(0);
            }
            chatTextBoxViewHolder.setTextBoxEvent(this);
        }
    }
    
    @Nonnull
    @Override
    protected ChatMessageType getMessageType() {
        return ChatMessageType.TextBoxDialog;
    }
    
    @Override
    public ChatMessageViewType getViewType() {
        return ChatMessageViewType.VIEW_TYPE_TEXTBOX;
    }
    
    @Override
    public boolean isObjectPopup() {
        return true;
    }
    
    public void onDialogIgnored(final UserManager userManager) {
        super.onDialogIgnored(userManager);
        userManager.getObjectPopupsManager().cancelObjectPopup(this);
    }
    
    public void onEnteredText(final UserManager userManager, final String enteredValue) {
        this.enteredValue = enteredValue;
        final UUID sourceUUID = this.source.getSourceUUID();
        final SLAgentCircuit activeAgentCircuit = userManager.getActiveAgentCircuit();
        if (sourceUUID != null && activeAgentCircuit != null) {
            activeAgentCircuit.SendScriptDialogReply(sourceUUID, this.chatChannel, this.textBoxButtonIndex, enteredValue);
        }
        userManager.getObjectPopupsManager().cancelObjectPopup(this);
    }
    
    @Override
    public void serializeToDatabaseObject(@Nonnull final ChatMessage chatMessage) {
        super.serializeToDatabaseObject(chatMessage);
        chatMessage.setTextBoxButtonIndex(this.textBoxButtonIndex);
        chatMessage.setDialogSelectedOption(this.enteredValue);
    }
    
    @Override
    public void showDialog(final Context context, final UserManager userManager) {
        new TextFieldDialogBuilder(context).setTitle(this.text).setOnTextEnteredListener((TextFieldDialogBuilder.OnTextEnteredListener)new _$Lambda$Iyj6QpN_ZLoXueXenKuJvDVzcmI$1(this, userManager)).setOnTextCancelledListener((TextFieldDialogBuilder.OnTextCancelledListener)new _$Lambda$Iyj6QpN_ZLoXueXenKuJvDVzcmI(this, userManager)).show();
    }
}
