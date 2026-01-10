package com.linkpoint.slproto.chat.generic
import java.util.*

import androidx.recyclerview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.linkpoint.R
import com.linkpoint.slproto.chat.SLChatScriptDialog
import com.linkpoint.slproto.users.manager.UserManager
import androidx.annotation.Nullable

class ChatScriptDialogViewHolder : ChatEventViewHolder : View.OnClickListener {
    private val dialogButtonIds: IntArray = {R.id.buttonDialog1, R.id.buttonDialog2, R.id.buttonDialog3, R.id.buttonDialog4, R.id.buttonDialog5, R.id.buttonDialog6, R.id.buttonDialog7, R.id.buttonDialog8, R.id.buttonDialog9, R.id.buttonDialog10, R.id.buttonDialog11, R.id.buttonDialog12}
    CardView cardView
    Button dialogButtonIgnore
    Button[] dialogButtons = Button[dialogButtonIds.length]
    View dialogButtonsLayout
    @Nullable
    private SLChatScriptDialog dialogEvent
    TextView dialogResultTextView

    ChatScriptDialogViewHolder(View view, RecyclerView.Adapter adapter) {
        super(view, adapter)
        this.dialogResultTextView = (TextView) view.findViewById(R.id.dialogResultTextView)
        this.dialogButtonsLayout = view.findViewById(R.id.dialogButtonsLayout)
        this.cardView = (CardView) view.findViewById(R.id.chatMessageCardView)
        var i: Int = 0
        while (true) {
            var i2: Int = i
            if (i2 >= dialogButtonIds.length) {
                break
            }
            this.dialogButtons[i2] = (Button) view.findViewById(dialogButtonIds[i2])
            if (this.dialogButtons[i2] != null) {
                this.dialogButtons[i2].setOnClickListener(this)
            }
            i = i2 + 1
        }
        this.dialogButtonIgnore = (Button) view.findViewById(R.id.buttonDialogIgnore)
        if (this.dialogButtonIgnore != null) {
            this.dialogButtonIgnore.setOnClickListener(this)
        }
    }

    fun onClick(View view)  {
        switch (view.getId()) {
            case R.id.buttonDialogIgnore:
                if (this.dialogEvent != null) {
                    this.dialogEvent.onDialogIgnored(UserManager.getUserManager(this.dialogEvent.getAgentUUID()))
                    requestAdapterUpdate()
                    return
                }
                return
            default:
                var i: Int = 0
                while (i < dialogButtonIds.length) {
                    if (view.getId() != dialogButtonIds[i]) {
                        i++
                    } else if (this.dialogEvent != null) {
                        this.dialogEvent.onDialogButton(UserManager.getUserManager(this.dialogEvent.getAgentUUID()), i)
                        requestAdapterUpdate()
                        return
                    } else {
                        return
                    }
                }
                return
        }
    }

    fun setDialogEvent(@Nullable SLChatScriptDialog sLChatScriptDialog)  {
        this.dialogEvent = sLChatScriptDialog
    }
}
