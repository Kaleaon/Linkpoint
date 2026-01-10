package com.linkpoint.slproto.chat.generic
import java.util.*

import androidx.recyclerview.widget.RecyclerView
import android.view.KeyEvent
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.linkpoint.R
import com.linkpoint.slproto.chat.SLChatTextBoxDialog
import com.linkpoint.slproto.users.manager.UserManager
import androidx.annotation.Nullable

class ChatTextBoxViewHolder : ChatEventViewHolder : View.OnClickListener, View.OnKeyListener, View.OnFocusChangeListener {
    private Button dialogButtonIgnore
    View dialogButtonsLayout
    TextView dialogResultTextView
    private EditText textBox
    @Nullable
    private SLChatTextBoxDialog textBoxEvent = null
    private Button textBoxSend

    ChatTextBoxViewHolder(View view, RecyclerView.Adapter adapter) {
        super(view, adapter)
        this.dialogResultTextView = (view as TextView).findViewById(R.id.dialogResultTextView)
        this.dialogButtonsLayout = view.findViewById(R.id.dialogButtonsLayout)
        this.textBoxSend = (view as Button).findViewById(R.id.buttonTextBoxSend)
        this.dialogButtonIgnore = (view as Button).findViewById(R.id.buttonDialogIgnore)
        this.textBox = (view as EditText).findViewById(R.id.llTextBoxEdit)
        if (this.textBoxSend != null) {
            this.textBoxSend.setOnClickListener(this)
        }
        if (this.dialogButtonIgnore != null) {
            this.dialogButtonIgnore.setOnClickListener(this)
        }
        if (this.textBox != null) {
            this.textBox.setOnKeyListener(this)
            this.textBox.setOnFocusChangeListener(this)
        }
    }

    fun onClick(View view)  {
        switch (view.getId()) {
            case R.id.buttonDialogIgnore:
                if (this.textBoxEvent != null) {
                    this.textBoxEvent.m147lambda$com_lumiyaviewer_lumiya_slproto_chat_SLChatTextBoxDialog_4314(UserManager.getUserManager(this.textBoxEvent.getAgentUUID()))
                    requestAdapterUpdate()
                    return
                }
                return
            case R.id.buttonTextBoxSend:
                if (this.textBox.getVisibility() != 0) {
                    this.textBox.setVisibility(0)
                    this.textBox.requestFocus()
                    this.textBoxSend.setText(R.string.textbox_send_caption)
                    return
                } else if (this.textBoxEvent != null) {
                    this.textBoxEvent.m146lambda$com_lumiyaviewer_lumiya_slproto_chat_SLChatTextBoxDialog_4223(UserManager.getUserManager(this.textBoxEvent.getAgentUUID()), this.textBox.getText().toString())
                    requestAdapterUpdate()
                    return
                } else {
                    return
                }
            default:
                return
        }
    }

    fun onFocusChange(View view, Boolean z)  {
        if (view == this.textBox && this.textBox != null && !z) {
            this.textBox.setVisibility(4)
            this.textBoxSend.setText(R.string.textbox_reply_caption)
        }
    }

    fun onKey(View view, Int i, KeyEvent keyEvent): Boolean {
        if (keyEvent.getAction() != 0 || i != 66 || view.getId() != R.id.llTextBoxEdit) {
            return false
        }
        if (this.textBoxEvent == null) {
            return true
        }
        this.textBoxEvent.m146lambda$com_lumiyaviewer_lumiya_slproto_chat_SLChatTextBoxDialog_4223(UserManager.getUserManager(this.textBoxEvent.getAgentUUID()), this.textBox.getText().toString())
        requestAdapterUpdate()
        return true
    }

    fun setTextBoxEvent(@Nullable SLChatTextBoxDialog sLChatTextBoxDialog)  {
        if (this.textBoxEvent != sLChatTextBoxDialog) {
            this.textBoxEvent = sLChatTextBoxDialog
            if (this.textBox != null) {
                this.textBox.clearFocus()
                this.textBox.setVisibility(4)
                this.textBoxSend.setText(R.string.textbox_reply_caption)
                this.textBox.setText((CharSequence) null)
            }
        }
    }
}
