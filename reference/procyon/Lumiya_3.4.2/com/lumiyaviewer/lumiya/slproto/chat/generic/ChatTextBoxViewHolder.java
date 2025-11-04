// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.chat.generic;

import android.view.KeyEvent;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import android.support.v7.widget.RecyclerView;
import javax.annotation.Nullable;
import com.lumiyaviewer.lumiya.slproto.chat.SLChatTextBoxDialog;
import android.widget.EditText;
import android.widget.TextView;
import android.view.View;
import android.widget.Button;
import android.view.View$OnFocusChangeListener;
import android.view.View$OnKeyListener;
import android.view.View$OnClickListener;

public class ChatTextBoxViewHolder extends ChatEventViewHolder implements View$OnClickListener, View$OnKeyListener, View$OnFocusChangeListener
{
    private final Button dialogButtonIgnore;
    public final View dialogButtonsLayout;
    public final TextView dialogResultTextView;
    private final EditText textBox;
    @Nullable
    private SLChatTextBoxDialog textBoxEvent;
    private final Button textBoxSend;
    
    ChatTextBoxViewHolder(final View view, final Adapter adapter) {
        super(view, adapter);
        this.textBoxEvent = null;
        this.dialogResultTextView = (TextView)view.findViewById(2131755306);
        this.dialogButtonsLayout = view.findViewById(2131755307);
        this.textBoxSend = (Button)view.findViewById(2131755321);
        this.dialogButtonIgnore = (Button)view.findViewById(2131755305);
        this.textBox = (EditText)view.findViewById(2131755320);
        if (this.textBoxSend != null) {
            this.textBoxSend.setOnClickListener((View$OnClickListener)this);
        }
        if (this.dialogButtonIgnore != null) {
            this.dialogButtonIgnore.setOnClickListener((View$OnClickListener)this);
        }
        if (this.textBox != null) {
            this.textBox.setOnKeyListener((View$OnKeyListener)this);
            this.textBox.setOnFocusChangeListener((View$OnFocusChangeListener)this);
        }
    }
    
    public void onClick(final View view) {
        switch (view.getId()) {
            case 2131755305: {
                if (this.textBoxEvent != null) {
                    this.textBoxEvent.onDialogIgnored(UserManager.getUserManager(this.textBoxEvent.getAgentUUID()));
                    this.requestAdapterUpdate();
                    break;
                }
                break;
            }
            case 2131755321: {
                if (this.textBox.getVisibility() != 0) {
                    this.textBox.setVisibility(0);
                    this.textBox.requestFocus();
                    this.textBoxSend.setText(2131297108);
                    break;
                }
                if (this.textBoxEvent != null) {
                    this.textBoxEvent.onEnteredText(UserManager.getUserManager(this.textBoxEvent.getAgentUUID()), this.textBox.getText().toString());
                    this.requestAdapterUpdate();
                    break;
                }
                break;
            }
        }
    }
    
    public void onFocusChange(final View view, final boolean b) {
        if (view == this.textBox && this.textBox != null && !b) {
            this.textBox.setVisibility(4);
            this.textBoxSend.setText(2131297107);
        }
    }
    
    public boolean onKey(final View view, final int n, final KeyEvent keyEvent) {
        if (keyEvent.getAction() == 0 && n == 66 && view.getId() == 2131755320) {
            if (this.textBoxEvent != null) {
                this.textBoxEvent.onEnteredText(UserManager.getUserManager(this.textBoxEvent.getAgentUUID()), this.textBox.getText().toString());
                this.requestAdapterUpdate();
            }
            return true;
        }
        return false;
    }
    
    public void setTextBoxEvent(@Nullable final SLChatTextBoxDialog textBoxEvent) {
        if (this.textBoxEvent != textBoxEvent) {
            this.textBoxEvent = textBoxEvent;
            if (this.textBox != null) {
                this.textBox.clearFocus();
                this.textBox.setVisibility(4);
                this.textBoxSend.setText(2131297107);
                this.textBox.setText((CharSequence)null);
            }
        }
    }
}
