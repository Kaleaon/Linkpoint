// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.chat.profiles;

import android.content.Context;
import com.lumiyaviewer.lumiya.react.Subscribable;
import com.lumiyaviewer.lumiya.react.Subscription;
import com.lumiyaviewer.lumiya.react.UIThreadExecutor;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import com.lumiyaviewer.lumiya.slproto.messages.AvatarNotesReply;
import com.lumiyaviewer.lumiya.slproto.modules.SLModules;
import com.lumiyaviewer.lumiya.slproto.modules.SLUserProfiles;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import com.lumiyaviewer.lumiya.slproto.users.SLMessageResponseCacher;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import com.lumiyaviewer.lumiya.ui.common.BackButtonHandler;
import com.lumiyaviewer.lumiya.ui.common.TextFieldEditFragment;

public class UserNotesEditFragment extends TextFieldEditFragment
    implements BackButtonHandler
{

    private Subscription avatarNotesSubscription;

    public UserNotesEditFragment()
    {
        avatarNotesSubscription = null;
    }

    private void onAvatarNotes(AvatarNotesReply avatarnotesreply)
    {
        setOriginalText(SLMessage.stringFromVariableUTF(avatarnotesreply.Data_Field.Notes).trim());
    }

    void _2D_com_lumiyaviewer_lumiya_ui_chat_profiles_UserNotesEditFragment_2D_mthref_2D_0(AvatarNotesReply avatarnotesreply)
    {
        onAvatarNotes(avatarnotesreply);
    }

    protected String decorateFragmentTitle(String s)
    {
        return getString(0x7f0901fd, new Object[] {
            s
        });
    }

    protected String getFieldHint(Context context)
    {
        return context.getString(0x7f09036a);
    }

    protected void onShowUser(ChatterID chatterid)
    {
        if (avatarNotesSubscription != null)
        {
            avatarNotesSubscription.unsubscribe();
            avatarNotesSubscription = null;
        }
        if (userManager != null && (chatterid instanceof com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterIDUser))
        {
            chatterid = ((com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterIDUser)chatterid).getChatterUUID();
            avatarNotesSubscription = userManager.getAvatarNotes().getPool().subscribe(chatterid, UIThreadExecutor.getInstance(), new _2D_.Lambda.gtFtIPtqrsfNaJBMezEYcryNxGg(this));
        }
    }

    protected void saveEditedText(SLAgentCircuit slagentcircuit, ChatterID chatterid, String s)
    {
        slagentcircuit.getModules().userProfiles.SaveUserNotes(chatterid.getOptionalChatterUUID(), s);
    }
}
