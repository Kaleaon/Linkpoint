// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.chat;

import android.view.View;
import android.content.DialogInterface;
import android.widget.Button;
import android.widget.TextView;
import android.content.DialogInterface$OnCancelListener;
import android.view.View$OnClickListener;
import android.app.Dialog;
import android.content.Context;
import java.io.OutputStream;
import java.io.ObjectOutputStream;
import java.io.ByteArrayOutputStream;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent;
import android.util.TypedValue;
import com.lumiyaviewer.lumiya.slproto.chat.generic.ChatScriptDialogViewHolder;
import javax.annotation.Nullable;
import com.lumiyaviewer.lumiya.ui.chat.ChatEventTimestampUpdater;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import com.lumiyaviewer.lumiya.slproto.chat.generic.ChatEventViewHolder;
import com.lumiyaviewer.lumiya.slproto.messages.ScriptDialog;
import java.io.IOException;
import com.lumiyaviewer.lumiya.Debug;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ByteArrayInputStream;
import javax.annotation.Nonnull;
import java.util.UUID;
import com.lumiyaviewer.lumiya.dao.ChatMessage;
import com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatDialogEvent;

public final class SLChatScriptDialog extends SLChatDialogEvent
{
    private static final int[] dialogButtonIds;
    private final String[] buttons;
    private String selectedOption;
    
    static {
        dialogButtonIds = new int[] { 2131755317, 2131755318, 2131755319, 2131755314, 2131755315, 2131755316, 2131755311, 2131755312, 2131755313, 2131755308, 2131755309, 2131755310 };
    }
    
    public SLChatScriptDialog(final ChatMessage chatMessage, @Nonnull final UUID uuid) {
        super(chatMessage, uuid);
        this.selectedOption = null;
        this.selectedOption = chatMessage.getDialogSelectedOption();
        while (true) {
            try {
                final String[] buttons = (String[])new ObjectInputStream(new ByteArrayInputStream(chatMessage.getDialogButtons())).readObject();
                this.buttons = buttons;
            }
            catch (final IOException | ClassNotFoundException ex) {
                Debug.Warning((Throwable)ex);
                final String[] buttons = null;
                continue;
            }
            break;
        }
    }
    
    public SLChatScriptDialog(final ScriptDialog scriptDialog, @Nonnull final UUID uuid, final String[] buttons) {
        super(scriptDialog, uuid);
        this.selectedOption = null;
        this.buttons = buttons;
    }
    
    @Override
    public void bindViewHolder(final ChatEventViewHolder chatEventViewHolder, final UserManager userManager, @Nullable final ChatEventTimestampUpdater chatEventTimestampUpdater) {
        super.bindViewHolder(chatEventViewHolder, userManager, chatEventTimestampUpdater);
        if (chatEventViewHolder instanceof ChatScriptDialogViewHolder) {
            final ChatScriptDialogViewHolder chatScriptDialogViewHolder = (ChatScriptDialogViewHolder)chatEventViewHolder;
            chatScriptDialogViewHolder.setDialogEvent(this);
            if (this.selectedOption != null || this.ignored) {
                if (this.ignored) {
                    chatScriptDialogViewHolder.dialogResultTextView.setText(2131296510);
                }
                else {
                    chatScriptDialogViewHolder.dialogResultTextView.setText((CharSequence)chatScriptDialogViewHolder.dialogResultTextView.getContext().getString(2131296511, new Object[] { this.selectedOption }));
                }
                chatScriptDialogViewHolder.dialogResultTextView.findViewById(2131755306).setVisibility(0);
                chatScriptDialogViewHolder.dialogButtonsLayout.findViewById(2131755307).setVisibility(8);
                chatScriptDialogViewHolder.cardView.setCardElevation(0.0f);
            }
            else {
                chatScriptDialogViewHolder.dialogResultTextView.findViewById(2131755306).setVisibility(8);
                for (int i = 0; i < chatScriptDialogViewHolder.dialogButtons.length; ++i) {
                    if (i < this.buttons.length) {
                        chatScriptDialogViewHolder.dialogButtons[i].setText((CharSequence)this.buttons[i]);
                        chatScriptDialogViewHolder.dialogButtons[i].setVisibility(0);
                    }
                    else {
                        chatScriptDialogViewHolder.dialogButtons[i].setVisibility(8);
                    }
                }
                chatScriptDialogViewHolder.dialogButtonsLayout.setVisibility(0);
                chatScriptDialogViewHolder.cardView.setCardElevation(TypedValue.applyDimension(1, 4.0f, chatScriptDialogViewHolder.cardView.getResources().getDisplayMetrics()));
            }
        }
    }
    
    public String[] getButtons() {
        return this.buttons;
    }
    
    @Nonnull
    @Override
    protected ChatMessageType getMessageType() {
        return ChatMessageType.ScriptDialog;
    }
    
    @Override
    public ChatMessageViewType getViewType() {
        return ChatMessageViewType.VIEW_TYPE_DIALOG;
    }
    
    @Override
    public boolean isObjectPopup() {
        return true;
    }
    
    public void onDialogButton(final UserManager userManager, final int n) {
        if (n >= 0 && n < this.buttons.length) {
            this.selectedOption = this.buttons[n];
            final UUID sourceUUID = this.source.getSourceUUID();
            final SLAgentCircuit activeAgentCircuit = userManager.getActiveAgentCircuit();
            if (sourceUUID != null && activeAgentCircuit != null) {
                activeAgentCircuit.SendScriptDialogReply(sourceUUID, this.chatChannel, n, this.selectedOption);
            }
            userManager.getObjectPopupsManager().cancelObjectPopup(this);
        }
    }
    
    public void onDialogIgnored(final UserManager userManager) {
        super.onDialogIgnored(userManager);
        userManager.getObjectPopupsManager().cancelObjectPopup(this);
    }
    
    @Override
    public void serializeToDatabaseObject(@Nonnull final ChatMessage chatMessage) {
        super.serializeToDatabaseObject(chatMessage);
        while (true) {
            try {
                final ByteArrayOutputStream out = new ByteArrayOutputStream();
                new ObjectOutputStream(out).writeObject(this.buttons);
                chatMessage.setDialogButtons(out.toByteArray());
                chatMessage.setDialogSelectedOption(this.selectedOption);
            }
            catch (final IOException ex) {
                Debug.Warning(ex);
                continue;
            }
            break;
        }
    }
    
    @Override
    public void showDialog(final Context context, final UserManager userManager) {
        new ScriptDialogDialog(context, userManager, this.source.getSourceName(userManager), this.text).show();
    }
    
    public class ScriptDialogDialog extends Dialog implements View$OnClickListener, DialogInterface$OnCancelListener
    {
        private final UserManager userManager;
        
        public ScriptDialogDialog(final Context context, final UserManager userManager, final String title, final String text) {
            super(context);
            this.userManager = userManager;
            this.setContentView(2130968726);
            this.setCancelable(true);
            this.setTitle((CharSequence)title);
            ((TextView)this.findViewById(2131755636)).setText((CharSequence)text);
            for (int i = 0; i < SLChatScriptDialog.dialogButtonIds.length; ++i) {
                if (i < SLChatScriptDialog.this.buttons.length) {
                    ((Button)this.findViewById(SLChatScriptDialog.dialogButtonIds[i])).setText((CharSequence)SLChatScriptDialog.this.buttons[i]);
                    this.findViewById(SLChatScriptDialog.dialogButtonIds[i]).setOnClickListener((View$OnClickListener)this);
                    this.findViewById(SLChatScriptDialog.dialogButtonIds[i]).setVisibility(0);
                }
                else {
                    this.findViewById(SLChatScriptDialog.dialogButtonIds[i]).setVisibility(8);
                }
            }
            this.setOnCancelListener((DialogInterface$OnCancelListener)this);
        }
        
        public void onCancel(final DialogInterface dialogInterface) {
            SLChatScriptDialog.this.onDialogIgnored(this.userManager);
            this.dismiss();
        }
        
        public void onClick(final View view) {
            for (int i = 0; i < SLChatScriptDialog.dialogButtonIds.length; ++i) {
                if (view.getId() == SLChatScriptDialog.dialogButtonIds[i]) {
                    SLChatScriptDialog.this.onDialogButton(this.userManager, i);
                    break;
                }
            }
            this.dismiss();
        }
    }
}
