// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.chat.generic;

import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;
import javax.annotation.Nullable;
import com.lumiyaviewer.lumiya.slproto.chat.SLChatScriptDialog;
import android.view.View;
import android.widget.Button;
import android.support.v7.widget.CardView;
import android.view.View$OnClickListener;

public class ChatScriptDialogViewHolder extends ChatEventViewHolder implements View$OnClickListener
{
    private static final int[] dialogButtonIds;
    public final CardView cardView;
    public final Button dialogButtonIgnore;
    public final Button[] dialogButtons;
    public final View dialogButtonsLayout;
    @Nullable
    private SLChatScriptDialog dialogEvent;
    public final TextView dialogResultTextView;
    
    static {
        dialogButtonIds = new int[] { 2131755317, 2131755318, 2131755319, 2131755314, 2131755315, 2131755316, 2131755311, 2131755312, 2131755313, 2131755308, 2131755309, 2131755310 };
    }
    
    ChatScriptDialogViewHolder(final View view, final Adapter adapter) {
        super(view, adapter);
        this.dialogResultTextView = (TextView)view.findViewById(2131755306);
        this.dialogButtonsLayout = view.findViewById(2131755307);
        this.cardView = (CardView)view.findViewById(2131755303);
        this.dialogButtons = new Button[ChatScriptDialogViewHolder.dialogButtonIds.length];
        for (int i = 0; i < ChatScriptDialogViewHolder.dialogButtonIds.length; ++i) {
            this.dialogButtons[i] = (Button)view.findViewById(ChatScriptDialogViewHolder.dialogButtonIds[i]);
            if (this.dialogButtons[i] != null) {
                this.dialogButtons[i].setOnClickListener((View$OnClickListener)this);
            }
        }
        this.dialogButtonIgnore = (Button)view.findViewById(2131755305);
        if (this.dialogButtonIgnore != null) {
            this.dialogButtonIgnore.setOnClickListener((View$OnClickListener)this);
        }
    }
    
    public void onClick(final View view) {
        switch (view.getId()) {
            default: {
                int i = 0;
                while (i < ChatScriptDialogViewHolder.dialogButtonIds.length) {
                    if (view.getId() == ChatScriptDialogViewHolder.dialogButtonIds[i]) {
                        if (this.dialogEvent != null) {
                            this.dialogEvent.onDialogButton(UserManager.getUserManager(this.dialogEvent.getAgentUUID()), i);
                            this.requestAdapterUpdate();
                            break;
                        }
                        break;
                    }
                    else {
                        ++i;
                    }
                }
                break;
            }
            case 2131755305: {
                if (this.dialogEvent != null) {
                    this.dialogEvent.onDialogIgnored(UserManager.getUserManager(this.dialogEvent.getAgentUUID()));
                    this.requestAdapterUpdate();
                    break;
                }
                break;
            }
        }
    }
    
    public void setDialogEvent(@Nullable final SLChatScriptDialog dialogEvent) {
        this.dialogEvent = dialogEvent;
    }
}
