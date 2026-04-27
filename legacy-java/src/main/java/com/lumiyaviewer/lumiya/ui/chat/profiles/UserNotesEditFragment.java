package com.lumiyaviewer.lumiya.ui.chat.profiles;

import android.content.Context;
import com.lumiyaviewer.lumiya.R;
import com.lumiyaviewer.lumiya.react.Subscribable;
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

/* loaded from: classes.dex */
public class UserNotesEditFragment extends TextFieldEditFragment implements BackButtonHandler {
    private Subscription<UUID, AvatarNotesReply> avatarNotesSubscription = null;

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: onAvatarNotes, reason: merged with bridge method [inline-methods] */
    public void m520x6c22cc6e(AvatarNotesReply avatarNotesReply) {
        setOriginalText(SLMessage.stringFromVariableUTF(avatarNotesReply.Data_Field.Notes).trim());
    }

    @Override // com.lumiyaviewer.lumiya.ui.common.ChatterFragment
    protected String decorateFragmentTitle(String str) {
        return getString(R.string.notes_for_title, str);
    }

    @Override // com.lumiyaviewer.lumiya.ui.common.TextFieldEditFragment
    protected String getFieldHint(Context context) {
        return context.getString(R.string.user_notes_hint);
    }

    @Override // com.lumiyaviewer.lumiya.ui.common.ChatterFragment
    protected void onShowUser(@Nullable ChatterID chatterID) {
        if (this.avatarNotesSubscription != null) {
            this.avatarNotesSubscription.unsubscribe();
            this.avatarNotesSubscription = null;
        }
        if (this.userManager == null || !(chatterID instanceof ChatterID.ChatterIDUser)) {
            return;
        }
        this.avatarNotesSubscription = this.userManager.getAvatarNotes().getPool().subscribe((Subscribable) ((ChatterID.ChatterIDUser) chatterID).getChatterUUID(), UIThreadExecutor.getInstance(), new Subscription.OnData() { // from class: com.lumiyaviewer.lumiya.ui.chat.profiles.-$Lambda$gtFtIPtqrsfNaJBMezEYcryNxGg
            private final /* synthetic */ void $m$0(Object obj) {
                ((UserNotesEditFragment) this).m520x6c22cc6e((AvatarNotesReply) obj);
            }

            @Override // com.lumiyaviewer.lumiya.react.Subscription.OnData
            public final void onData(Object obj) {
                $m$0(obj);
            }
        });
    }

    @Override // com.lumiyaviewer.lumiya.ui.common.TextFieldEditFragment
    protected void saveEditedText(SLAgentCircuit sLAgentCircuit, ChatterID chatterID, String str) {
        sLAgentCircuit.getModules().userProfiles.SaveUserNotes(chatterID.getOptionalChatterUUID(), str);
    }
}
