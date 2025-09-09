package com.lumiyaviewer.lumiya.slproto.chat;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.R;
import com.lumiyaviewer.lumiya.dao.ChatMessage;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import com.lumiyaviewer.lumiya.slproto.chat.generic.ChatEventViewHolder;
import com.lumiyaviewer.lumiya.slproto.chat.generic.ChatScriptDialogViewHolder;
import com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatDialogEvent;
import com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent;
import com.lumiyaviewer.lumiya.slproto.messages.ScriptDialog;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import com.lumiyaviewer.lumiya.ui.chat.ChatEventTimestampUpdater;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class SLChatScriptDialog extends SLChatDialogEvent {
    /* access modifiers changed from: private */
    public static final int[] dialogButtonIds = {R.id.buttonDialog1, R.id.buttonDialog2, R.id.buttonDialog3, R.id.buttonDialog4, R.id.buttonDialog5, R.id.buttonDialog6, R.id.buttonDialog7, R.id.buttonDialog8, R.id.buttonDialog9, R.id.buttonDialog10, R.id.buttonDialog11, R.id.buttonDialog12};
    /* access modifiers changed from: private */
    public final String[] buttons;
    private String selectedOption = null;

    public class ScriptDialogDialog extends Dialog implements View.OnClickListener, DialogInterface.OnCancelListener {
        private final UserManager userManager;

        public ScriptDialogDialog(Context context, UserManager userManager2, String str, String str2) {
            super(context);
            this.userManager = userManager2;
            setContentView(R.layout.script_dialog);
            setCancelable(true);
            setTitle(str);
            ((TextView) findViewById(R.id.dialogQuestionText)).setText(str2);
            for (int i = 0; i < SLChatScriptDialog.dialogButtonIds.length; i++) {
                if (i < SLChatScriptDialog.this.buttons.length) {
                    ((Button) findViewById(SLChatScriptDialog.dialogButtonIds[i])).setText(SLChatScriptDialog.this.buttons[i]);
                    findViewById(SLChatScriptDialog.dialogButtonIds[i]).setOnClickListener(this);
                    findViewById(SLChatScriptDialog.dialogButtonIds[i]).setVisibility(0);
                } else {
                    findViewById(SLChatScriptDialog.dialogButtonIds[i]).setVisibility(8);
                }
            }
            setOnCancelListener(this);
        }

        public void onCancel(DialogInterface dialogInterface) {
            SLChatScriptDialog.this.onDialogIgnored(this.userManager);
            dismiss();
        }

        public void onClick(View view) {
            int i = 0;
            while (true) {
                if (i >= SLChatScriptDialog.dialogButtonIds.length) {
                    break;
                } else if (view.getId() == SLChatScriptDialog.dialogButtonIds[i]) {
                    SLChatScriptDialog.this.onDialogButton(this.userManager, i);
                    break;
                } else {
                    i++;
                }
            }
            dismiss();
        }
    }

    public SLChatScriptDialog(ChatMessage chatMessage, @Nonnull UUID uuid) {
        super(chatMessage, uuid);
        String[] strArr;
        this.selectedOption = chatMessage.getDialogSelectedOption();
        try {
            strArr = (String[]) new ObjectInputStream(new ByteArrayInputStream(chatMessage.getDialogButtons())).readObject();
        } catch (IOException | ClassNotFoundException e) {
            Debug.Warning(e);
            strArr = null;
        }
        this.buttons = strArr;
    }

    public SLChatScriptDialog(ScriptDialog scriptDialog, @Nonnull UUID uuid, String[] strArr) {
        super(scriptDialog, uuid);
        this.buttons = strArr;
    }

    public void bindViewHolder(ChatEventViewHolder chatEventViewHolder, UserManager userManager, @Nullable ChatEventTimestampUpdater chatEventTimestampUpdater) {
        super.bindViewHolder(chatEventViewHolder, userManager, chatEventTimestampUpdater);
        if (chatEventViewHolder instanceof ChatScriptDialogViewHolder) {
            ChatScriptDialogViewHolder chatScriptDialogViewHolder = (ChatScriptDialogViewHolder) chatEventViewHolder;
            chatScriptDialogViewHolder.setDialogEvent(this);
            if (this.selectedOption != null || this.ignored) {
                if (this.ignored) {
                    chatScriptDialogViewHolder.dialogResultTextView.setText(R.string.dialog_ignored);
                } else {
                    chatScriptDialogViewHolder.dialogResultTextView.setText(chatScriptDialogViewHolder.dialogResultTextView.getContext().getString(R.string.dialog_selected_format, new Object[]{this.selectedOption}));
                }
                chatScriptDialogViewHolder.dialogResultTextView.findViewById(R.id.dialogResultTextView).setVisibility(0);
                chatScriptDialogViewHolder.dialogButtonsLayout.findViewById(R.id.dialogButtonsLayout).setVisibility(8);
                chatScriptDialogViewHolder.cardView.setCardElevation(0.0f);
                return;
            }
            chatScriptDialogViewHolder.dialogResultTextView.findViewById(R.id.dialogResultTextView).setVisibility(8);
            for (int i = 0; i < chatScriptDialogViewHolder.dialogButtons.length; i++) {
                if (i < this.buttons.length) {
                    chatScriptDialogViewHolder.dialogButtons[i].setText(this.buttons[i]);
                    chatScriptDialogViewHolder.dialogButtons[i].setVisibility(0);
                } else {
                    chatScriptDialogViewHolder.dialogButtons[i].setVisibility(8);
                }
            }
            chatScriptDialogViewHolder.dialogButtonsLayout.setVisibility(0);
            chatScriptDialogViewHolder.cardView.setCardElevation(TypedValue.applyDimension(1, 4.0f, chatScriptDialogViewHolder.cardView.getResources().getDisplayMetrics()));
        }
    }

    public String[] getButtons() {
        return this.buttons;
    }

    /* access modifiers changed from: protected */
    @Nonnull
    public SLChatEvent.ChatMessageType getMessageType() {
        return SLChatEvent.ChatMessageType.ScriptDialog;
    }

    public SLChatEvent.ChatMessageViewType getViewType() {
        return SLChatEvent.ChatMessageViewType.VIEW_TYPE_DIALOG;
    }

    public boolean isObjectPopup() {
        return true;
    }

    public void onDialogButton(UserManager userManager, int i) {
        if (i >= 0 && i < this.buttons.length) {
            this.selectedOption = this.buttons[i];
            UUID sourceUUID = this.source.getSourceUUID();
            SLAgentCircuit activeAgentCircuit = userManager.getActiveAgentCircuit();
            if (!(sourceUUID == null || activeAgentCircuit == null)) {
                activeAgentCircuit.SendScriptDialogReply(sourceUUID, this.chatChannel, i, this.selectedOption);
            }
            userManager.getObjectPopupsManager().cancelObjectPopup(this);
        }
    }

    public void onDialogIgnored(UserManager userManager) {
        super.onDialogIgnored(userManager);
        userManager.getObjectPopupsManager().cancelObjectPopup(this);
    }

    public void serializeToDatabaseObject(@Nonnull ChatMessage chatMessage) {
        super.serializeToDatabaseObject(chatMessage);
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            new ObjectOutputStream(byteArrayOutputStream).writeObject(this.buttons);
            chatMessage.setDialogButtons(byteArrayOutputStream.toByteArray());
        } catch (IOException e) {
            Debug.Warning(e);
        }
        chatMessage.setDialogSelectedOption(this.selectedOption);
    }

    public void showDialog(Context context, UserManager userManager) {
        new ScriptDialogDialog(context, userManager, this.source.getSourceName(userManager), this.text).show();
    }
}
