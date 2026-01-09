package com.linkpoint.ui.chat.profiles

import android.content.Context
import com.linkpoint.R
import com.linkpoint.react.Subscription
import com.linkpoint.react.UIThreadExecutor
import com.linkpoint.slproto.SLAgentCircuit
import com.linkpoint.slproto.SLMessage
import com.linkpoint.slproto.messages.AvatarNotesReply
import com.linkpoint.slproto.users.ChatterID
import com.linkpoint.ui.common.BackButtonHandler
import com.linkpoint.ui.common.TextFieldEditFragment
import java.util.UUID
import androidx.annotation.Nullable

class UserNotesEditFragment : TextFieldEditFragment : BackButtonHandler {
    private Subscription<UUID, AvatarNotesReply> avatarNotesSubscription = null

    /* access modifiers changed from: private */
    /* renamed from: onAvatarNotes */
    fun m510com_lumiyaviewer_lumiya_ui_chat_profiles_UserNotesEditFragmentmthref0(AvatarNotesReply avatarNotesReply)  {
        setOriginalText(SLMessage.stringFromVariableUTF(avatarNotesReply.Data_Field.Notes).trim())
    }

    /* access modifiers changed from: protected */
    fun decorateFragmentTitle(String str): String {
        return getString(R.string.notes_for_title, str)
    }

    /* access modifiers changed from: protected */
    fun getFieldHint(Context context): String {
        return context.getString(R.string.user_notes_hint)
    }

    /* access modifiers changed from: protected */
    fun onShowUser(@Nullable ChatterID chatterID)  {
        if (this.avatarNotesSubscription != null) {
            this.avatarNotesSubscription.unsubscribe()
            this.avatarNotesSubscription = null
        }
        if (this.userManager != null && (chatterID is ChatterID.ChatterIDUser)) {
            this.avatarNotesSubscription = this.userManager.getAvatarNotes().getPool().subscribe(((ChatterID.ChatterIDUser) chatterID).getChatterUUID(), UIThreadExecutor.getInstance(), $Lambda$gtFtIPtqrsfNaJBMezEYcryNxGg(this))
        }
    }

    /* access modifiers changed from: protected */
    fun saveEditedText(SLAgentCircuit sLAgentCircuit, ChatterID chatterID, String str)  {
        sLAgentCircuit.getModules().userProfiles.SaveUserNotes(chatterID.getOptionalChatterUUID(), str)
    }
}
