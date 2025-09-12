// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.chat.profiles;

import com.lumiyaviewer.lumiya.react.Subscribable;
import com.lumiyaviewer.lumiya.react.Subscription;
import com.lumiyaviewer.lumiya.react.UIThreadExecutor;
import com.lumiyaviewer.lumiya.slproto.messages.AvatarPropertiesReply;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import com.lumiyaviewer.lumiya.slproto.users.SLMessageResponseCacher;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import com.lumiyaviewer.lumiya.ui.common.TextFieldEditFragment;

public abstract class ProfileTextFieldEditFragment extends TextFieldEditFragment
{

    private Subscription avatarProperties;

    public ProfileTextFieldEditFragment()
    {
        avatarProperties = null;
    }

    void _2D_com_lumiyaviewer_lumiya_ui_chat_profiles_ProfileTextFieldEditFragment_2D_mthref_2D_0(AvatarPropertiesReply avatarpropertiesreply)
    {
        onAvatarProperties(avatarpropertiesreply);
    }

    protected abstract void onAvatarProperties(AvatarPropertiesReply avatarpropertiesreply);

    protected void onShowUser(ChatterID chatterid)
    {
        if (avatarProperties != null)
        {
            avatarProperties.unsubscribe();
            avatarProperties = null;
        }
        if (userManager != null && (chatterid instanceof com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterIDUser))
        {
            chatterid = ((com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterIDUser)chatterid).getChatterUUID();
            avatarProperties = userManager.getAvatarProperties().getPool().subscribe(chatterid, UIThreadExecutor.getInstance(), new _2D_.Lambda._cls6hJe_2D_KPqqQcY7xiCxogddm78oYc(this));
        }
    }
}
