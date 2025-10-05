package com.lumiyaviewer.lumiya.ui.chat.profiles;

import android.content.Context;
import com.lumiyaviewer.lumiya.R;
import com.lumiyaviewer.lumiya.react.Subscription;
import com.lumiyaviewer.lumiya.react.UIThreadExecutor;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import com.lumiyaviewer.lumiya.slproto.messages.AvatarNotesReply;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import com.lumiyaviewer.lumiya.ui.common.BackButtonHandler;
import com.lumiyaviewer.lumiya.ui.common.TextFieldEditFragment;
import java.util.UUID;
import javax.annotation.Nullable;

public class UserNotesEditFragment extends TextFieldEditFragment implements BackButtonHandler {
    private Subscription<UUID, AvatarNotesReply> avatarNotesSubscription = null;

    /* access modifiers changed from: private */
    /* renamed from: onAvatarNotes */
    public void m510com_lumiyaviewer_lumiya_ui_chat_profiles_UserNotesEditFragmentmthref0(AvatarNotesReply avatarNotesReply) {
        setOriginalText(SLMessage.stringFromVariableUTF(avatarNotesReply.Data_Field.Notes).trim());
    }

    /* access modifiers changed from: protected */
    public String decorateFragmentTitle(String str) {
        return getString(R.string.notes_for_title, str);
    }

    /* access modifiers changed from: protected */
    public String getFieldHint(Context context) {
        return context.getString(R.string.user_notes_hint);
    }

    /* access modifiers changed from: protected */
    public void onShowUser(@Nullable ChatterID chatterID) {
        if (this.avatarNotesSubscription != null) {
            this.avatarNotesSubscription.unsubscribe();
            this.avatarNotesSubscription = null;
        }
        if (this.userManager != null && (chatterID instanceof ChatterID.ChatterIDUser)) {
            this.avatarNotesSubscription = this.userManager.getAvatarNotes().getPool().subscribe(((ChatterID.ChatterIDUser) chatterID).getChatterUUID(), UIThreadExecutor.getInstance(), new $Lambda$gtFtIPtqrsfNaJBMezEYcryNxGg(this));
        }
    }

    /* access modifiers changed from: protected */
    public void saveEditedText(SLAgentCircuit sLAgentCircuit, ChatterID chatterID, String str) {
        sLAgentCircuit.getModules().userProfiles.SaveUserNotes(chatterID.getOptionalChatterUUID(), str);
    }
}
