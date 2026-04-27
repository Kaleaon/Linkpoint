// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.ui.chat.profiles;

import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import com.lumiyaviewer.lumiya.react.UIThreadExecutor;
import javax.annotation.Nullable;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import android.content.Context;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import com.lumiyaviewer.lumiya.slproto.messages.AvatarNotesReply;
import java.util.UUID;
import com.lumiyaviewer.lumiya.react.Subscription;
import com.lumiyaviewer.lumiya.ui.common.BackButtonHandler;
import com.lumiyaviewer.lumiya.ui.common.TextFieldEditFragment;

public class UserNotesEditFragment extends TextFieldEditFragment implements BackButtonHandler
{
    private Subscription<UUID, AvatarNotesReply> avatarNotesSubscription;
    
    public UserNotesEditFragment() {
        this.avatarNotesSubscription = null;
    }
    
    private void onAvatarNotes(final AvatarNotesReply avatarNotesReply) {
        this.setOriginalText(SLMessage.stringFromVariableUTF(avatarNotesReply.Data_Field.Notes).trim());
    }
    
    @Override
    protected String decorateFragmentTitle(final String s) {
        return this.getString(2131296765, s);
    }
    
    @Override
    protected String getFieldHint(final Context context) {
        return context.getString(2131297130);
    }
    
    @Override
    protected void onShowUser(@Nullable final ChatterID chatterID) {
        if (this.avatarNotesSubscription != null) {
            this.avatarNotesSubscription.unsubscribe();
            this.avatarNotesSubscription = null;
        }
        if (this.userManager != null && chatterID instanceof ChatterID.ChatterIDUser) {
            this.avatarNotesSubscription = this.userManager.getAvatarNotes().getPool().subscribe(((ChatterID.ChatterIDUser)chatterID).getChatterUUID(), UIThreadExecutor.getInstance(), new _$Lambda$gtFtIPtqrsfNaJBMezEYcryNxGg(this));
        }
    }
    
    @Override
    protected void saveEditedText(final SLAgentCircuit slAgentCircuit, final ChatterID chatterID, final String s) {
        slAgentCircuit.getModules().userProfiles.SaveUserNotes(chatterID.getOptionalChatterUUID(), s);
    }
}
