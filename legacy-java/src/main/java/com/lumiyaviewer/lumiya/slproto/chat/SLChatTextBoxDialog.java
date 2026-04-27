package com.lumiyaviewer.lumiya.slproto.chat;

import android.content.Context;
import com.lumiyaviewer.lumiya.R;
import com.lumiyaviewer.lumiya.dao.ChatMessage;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import com.lumiyaviewer.lumiya.slproto.chat.generic.ChatEventViewHolder;
import com.lumiyaviewer.lumiya.slproto.chat.generic.ChatTextBoxViewHolder;
import com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatDialogEvent;
import com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent;
import com.lumiyaviewer.lumiya.slproto.messages.ScriptDialog;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import com.lumiyaviewer.lumiya.ui.chat.ChatEventTimestampUpdater;
import com.lumiyaviewer.lumiya.ui.common.TextFieldDialogBuilder;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/* loaded from: classes.dex */
public final class SLChatTextBoxDialog extends SLChatDialogEvent {
    private String enteredValue;
    private final int textBoxButtonIndex;

    public SLChatTextBoxDialog(ChatMessage chatMessage, @Nonnull UUID uuid) {
        super(chatMessage, uuid);
        this.enteredValue = null;
        this.textBoxButtonIndex = chatMessage.getTextBoxButtonIndex().intValue();
        this.enteredValue = chatMessage.getDialogSelectedOption();
    }

    public SLChatTextBoxDialog(ScriptDialog scriptDialog, @Nonnull UUID uuid, int i) {
        super(scriptDialog, uuid);
        this.enteredValue = null;
        this.textBoxButtonIndex = i;
    }

    @Override // com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent
    public void bindViewHolder(ChatEventViewHolder chatEventViewHolder, UserManager userManager, @Nullable ChatEventTimestampUpdater chatEventTimestampUpdater) {
        super.bindViewHolder(chatEventViewHolder, userManager, chatEventTimestampUpdater);
        if (chatEventViewHolder instanceof ChatTextBoxViewHolder) {
            ChatTextBoxViewHolder chatTextBoxViewHolder = (ChatTextBoxViewHolder) chatEventViewHolder;
            if (this.enteredValue != null || this.ignored) {
                if (this.ignored) {
                    chatTextBoxViewHolder.dialogResultTextView.setText(R.string.dialog_ignored);
                } else {
                    chatTextBoxViewHolder.dialogResultTextView.setText(chatTextBoxViewHolder.dialogResultTextView.getContext().getString(R.string.text_box_entered, this.enteredValue));
                }
                chatTextBoxViewHolder.dialogResultTextView.setVisibility(0);
                chatTextBoxViewHolder.dialogButtonsLayout.setVisibility(8);
            } else {
                chatTextBoxViewHolder.dialogResultTextView.setVisibility(8);
                chatTextBoxViewHolder.dialogButtonsLayout.setVisibility(0);
            }
            chatTextBoxViewHolder.setTextBoxEvent(this);
        }
    }

    @Override // com.lumiyaviewer.lumiya.slproto.chat.SLChatTextEvent, com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent
    @Nonnull
    protected SLChatEvent.ChatMessageType getMessageType() {
        return SLChatEvent.ChatMessageType.TextBoxDialog;
    }

    @Override // com.lumiyaviewer.lumiya.slproto.chat.SLChatTextEvent, com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent
    public SLChatEvent.ChatMessageViewType getViewType() {
        return SLChatEvent.ChatMessageViewType.VIEW_TYPE_TEXTBOX;
    }

    @Override // com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent
    public boolean isObjectPopup() {
        return true;
    }

    @Override // com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatDialogEvent
    /* renamed from: onDialogIgnored, reason: merged with bridge method [inline-methods] */
    public void m157xe7f9f9c3(UserManager userManager) {
        super.m157xe7f9f9c3(userManager);
        userManager.getObjectPopupsManager().cancelObjectPopup(this);
    }

    /* renamed from: onEnteredText, reason: merged with bridge method [inline-methods] */
    public void m156xe7f9f620(UserManager userManager, String str) {
        this.enteredValue = str;
        UUID sourceUUID = this.source.getSourceUUID();
        SLAgentCircuit activeAgentCircuit = userManager.getActiveAgentCircuit();
        if (sourceUUID != null && activeAgentCircuit != null) {
            activeAgentCircuit.SendScriptDialogReply(sourceUUID, this.chatChannel, this.textBoxButtonIndex, str);
        }
        userManager.getObjectPopupsManager().cancelObjectPopup(this);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatDialogEvent, com.lumiyaviewer.lumiya.slproto.chat.SLChatTextEvent, com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent
    public void serializeToDatabaseObject(@Nonnull ChatMessage chatMessage) {
        super.serializeToDatabaseObject(chatMessage);
        chatMessage.setTextBoxButtonIndex(Integer.valueOf(this.textBoxButtonIndex));
        chatMessage.setDialogSelectedOption(this.enteredValue);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatDialogEvent
    public void showDialog(Context context, final UserManager userManager) {
        new TextFieldDialogBuilder(context).setTitle(this.text).setOnTextEnteredListener(new TextFieldDialogBuilder.OnTextEnteredListener() { // from class: com.lumiyaviewer.lumiya.slproto.chat.-$Lambda$Iyj6QpN-ZLoXueXenKuJvDVzcmI.1
            private final /* synthetic */ void $m$0(String str) {
                ((SLChatTextBoxDialog) this).m156xe7f9f620((UserManager) userManager, str);
            }

            @Override // com.lumiyaviewer.lumiya.ui.common.TextFieldDialogBuilder.OnTextEnteredListener
            public final void onTextEntered(String str) {
                $m$0(str);
            }
        }).setOnTextCancelledListener(new TextFieldDialogBuilder.OnTextCancelledListener() { // from class: com.lumiyaviewer.lumiya.slproto.chat.-$Lambda$Iyj6QpN-ZLoXueXenKuJvDVzcmI
            private final /* synthetic */ void $m$0() {
                ((SLChatTextBoxDialog) this).m157xe7f9f9c3((UserManager) userManager);
            }

            @Override // com.lumiyaviewer.lumiya.ui.common.TextFieldDialogBuilder.OnTextCancelledListener
            public final void onTextCancelled() {
                $m$0();
            }
        }).show();
    }
}
