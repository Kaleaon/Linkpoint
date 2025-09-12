package com.lumiyaviewer.lumiya.slproto.chat.generic;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.lumiyaviewer.lumiya.R;
import com.lumiyaviewer.lumiya.slproto.chat.SLChatScriptDialog;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import javax.annotation.Nullable;

public class ChatScriptDialogViewHolder extends ChatEventViewHolder implements View.OnClickListener {
    private static final int[] dialogButtonIds = {R.id.buttonDialog1, R.id.buttonDialog2, R.id.buttonDialog3, R.id.buttonDialog4, R.id.buttonDialog5, R.id.buttonDialog6, R.id.buttonDialog7, R.id.buttonDialog8, R.id.buttonDialog9, R.id.buttonDialog10, R.id.buttonDialog11, R.id.buttonDialog12};
    public final CardView cardView;
    public final Button dialogButtonIgnore;
    public final Button[] dialogButtons = new Button[dialogButtonIds.length];
    public final View dialogButtonsLayout;
    @Nullable
    private SLChatScriptDialog dialogEvent;
    public final TextView dialogResultTextView;

    ChatScriptDialogViewHolder(View view, RecyclerView.Adapter adapter) {
        super(view, adapter);
        this.dialogResultTextView = (TextView) view.findViewById(R.id.dialogResultTextView);
        this.dialogButtonsLayout = view.findViewById(R.id.dialogButtonsLayout);
        this.cardView = (CardView) view.findViewById(R.id.chatMessageCardView);
        int i = 0;
        while (true) {
            int i2 = i;
            if (i2 >= dialogButtonIds.length) {
                break;
            }
            this.dialogButtons[i2] = (Button) view.findViewById(dialogButtonIds[i2]);
            if (this.dialogButtons[i2] != null) {
                this.dialogButtons[i2].setOnClickListener(this);
            }
            i = i2 + 1;
        }
        this.dialogButtonIgnore = (Button) view.findViewById(R.id.buttonDialogIgnore);
        if (this.dialogButtonIgnore != null) {
            this.dialogButtonIgnore.setOnClickListener(this);
        }
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.buttonDialogIgnore:
                if (this.dialogEvent != null) {
                    this.dialogEvent.onDialogIgnored(UserManager.getUserManager(this.dialogEvent.getAgentUUID()));
                    requestAdapterUpdate();
                    return;
                }
                return;
            default:
                int i = 0;
                while (i < dialogButtonIds.length) {
                    if (view.getId() != dialogButtonIds[i]) {
                        i++;
                    } else if (this.dialogEvent != null) {
                        this.dialogEvent.onDialogButton(UserManager.getUserManager(this.dialogEvent.getAgentUUID()), i);
                        requestAdapterUpdate();
                        return;
                    } else {
                        return;
                    }
                }
                return;
        }
    }

    public void setDialogEvent(@Nullable SLChatScriptDialog sLChatScriptDialog) {
        this.dialogEvent = sLChatScriptDialog;
    }
}
