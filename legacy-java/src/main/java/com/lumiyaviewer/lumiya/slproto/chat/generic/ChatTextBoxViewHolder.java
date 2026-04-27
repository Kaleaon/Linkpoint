package com.lumiyaviewer.lumiya.slproto.chat.generic;

import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.lumiyaviewer.lumiya.R;
import com.lumiyaviewer.lumiya.slproto.chat.SLChatTextBoxDialog;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import javax.annotation.Nullable;

/* loaded from: classes.dex */
public class ChatTextBoxViewHolder extends ChatEventViewHolder implements View.OnClickListener, View.OnKeyListener, View.OnFocusChangeListener {
    private final Button dialogButtonIgnore;
    public final View dialogButtonsLayout;
    public final TextView dialogResultTextView;
    private final EditText textBox;

    @Nullable
    private SLChatTextBoxDialog textBoxEvent;
    private final Button textBoxSend;

    /* JADX INFO: Access modifiers changed from: package-private */
    public ChatTextBoxViewHolder(View view, RecyclerView.Adapter adapter) {
        super(view, adapter);
        this.textBoxEvent = null;
        this.dialogResultTextView = (TextView) view.findViewById(R.id.dialogResultTextView);
        this.dialogButtonsLayout = view.findViewById(R.id.dialogButtonsLayout);
        this.textBoxSend = (Button) view.findViewById(R.id.buttonTextBoxSend);
        this.dialogButtonIgnore = (Button) view.findViewById(R.id.buttonDialogIgnore);
        this.textBox = (EditText) view.findViewById(R.id.llTextBoxEdit);
        if (this.textBoxSend != null) {
            this.textBoxSend.setOnClickListener(this);
        }
        if (this.dialogButtonIgnore != null) {
            this.dialogButtonIgnore.setOnClickListener(this);
        }
        if (this.textBox != null) {
            this.textBox.setOnKeyListener(this);
            this.textBox.setOnFocusChangeListener(this);
        }
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.buttonDialogIgnore /* 2131755305 */:
                if (this.textBoxEvent != null) {
                    this.textBoxEvent.m157xe7f9f9c3(UserManager.getUserManager(this.textBoxEvent.getAgentUUID()));
                    requestAdapterUpdate();
                    return;
                }
                return;
            case R.id.buttonTextBoxSend /* 2131755321 */:
                if (this.textBox.getVisibility() != 0) {
                    this.textBox.setVisibility(0);
                    this.textBox.requestFocus();
                    this.textBoxSend.setText(R.string.textbox_send_caption);
                    return;
                } else {
                    if (this.textBoxEvent != null) {
                        this.textBoxEvent.m156xe7f9f620(UserManager.getUserManager(this.textBoxEvent.getAgentUUID()), this.textBox.getText().toString());
                        requestAdapterUpdate();
                        return;
                    }
                    return;
                }
            default:
                return;
        }
    }

    @Override // android.view.View.OnFocusChangeListener
    public void onFocusChange(View view, boolean z) {
        if (view != this.textBox || this.textBox == null || z) {
            return;
        }
        this.textBox.setVisibility(4);
        this.textBoxSend.setText(R.string.textbox_reply_caption);
    }

    @Override // android.view.View.OnKeyListener
    public boolean onKey(View view, int i, KeyEvent keyEvent) {
        if (keyEvent.getAction() != 0 || i != 66 || view.getId() != R.id.llTextBoxEdit) {
            return false;
        }
        if (this.textBoxEvent == null) {
            return true;
        }
        this.textBoxEvent.m156xe7f9f620(UserManager.getUserManager(this.textBoxEvent.getAgentUUID()), this.textBox.getText().toString());
        requestAdapterUpdate();
        return true;
    }

    public void setTextBoxEvent(@Nullable SLChatTextBoxDialog sLChatTextBoxDialog) {
        if (this.textBoxEvent != sLChatTextBoxDialog) {
            this.textBoxEvent = sLChatTextBoxDialog;
            if (this.textBox != null) {
                this.textBox.clearFocus();
                this.textBox.setVisibility(4);
                this.textBoxSend.setText(R.string.textbox_reply_caption);
                this.textBox.setText((CharSequence) null);
            }
        }
    }
}
