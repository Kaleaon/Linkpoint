// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.chat.profiles;

import android.content.Context;
import android.os.Bundle;
import com.lumiyaviewer.lumiya.react.SubscriptionData;
import com.lumiyaviewer.lumiya.react.UIThreadExecutor;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import com.lumiyaviewer.lumiya.slproto.messages.PickInfoReply;
import com.lumiyaviewer.lumiya.slproto.modules.SLModules;
import com.lumiyaviewer.lumiya.slproto.modules.SLUserProfiles;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import com.lumiyaviewer.lumiya.slproto.users.SLMessageResponseCacher;
import com.lumiyaviewer.lumiya.slproto.users.manager.AvatarPickKey;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import com.lumiyaviewer.lumiya.ui.common.ChatterFragment;
import com.lumiyaviewer.lumiya.ui.common.TextFieldEditFragment;

public class PickDescriptionEditFragment extends TextFieldEditFragment
{

    private static final String AVATAR_PICK_KEY = "avatarPickKey";
    private final SubscriptionData pickInfo = new SubscriptionData(UIThreadExecutor.getInstance(), new _2D_.Lambda.Y7Ne2VWglUcvjFUgJydWWKVgIXM(this));

    public PickDescriptionEditFragment()
    {
    }

    private AvatarPickKey getPickKey()
    {
        Bundle bundle = getArguments();
        if (bundle != null && bundle.containsKey("avatarPickKey"))
        {
            return (AvatarPickKey)bundle.getParcelable("avatarPickKey");
        } else
        {
            return null;
        }
    }

    public static Bundle makeSelection(ChatterID chatterid, AvatarPickKey avatarpickkey)
    {
        chatterid = ChatterFragment.makeSelection(chatterid);
        chatterid.putParcelable("avatarPickKey", avatarpickkey);
        return chatterid;
    }

    private void onPickInfoReply(PickInfoReply pickinforeply)
    {
        if (pickinforeply != null)
        {
            setOriginalText(SLMessage.stringFromVariableUTF(pickinforeply.Data_Field.Desc));
        }
    }

    void _2D_com_lumiyaviewer_lumiya_ui_chat_profiles_PickDescriptionEditFragment_2D_mthref_2D_0(PickInfoReply pickinforeply)
    {
        onPickInfoReply(pickinforeply);
    }

    protected String getFieldHint(Context context)
    {
        return getString(0x7f090273);
    }

    protected void onShowUser(ChatterID chatterid)
    {
        AvatarPickKey avatarpickkey = getPickKey();
        if (userManager != null && (chatterid instanceof com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterIDUser) && avatarpickkey != null)
        {
            pickInfo.subscribe(userManager.getAvatarPickInfos().getPool(), avatarpickkey);
            return;
        } else
        {
            pickInfo.unsubscribe();
            return;
        }
    }

    protected void saveEditedText(SLAgentCircuit slagentcircuit, ChatterID chatterid, String s)
    {
        chatterid = getPickKey();
        PickInfoReply pickinforeply = (PickInfoReply)pickInfo.getData();
        if (slagentcircuit != null && chatterid != null && pickinforeply != null)
        {
            slagentcircuit.getModules().userProfiles.UpdatePickInfo(((AvatarPickKey) (chatterid)).pickID, pickinforeply.Data_Field.CreatorID, pickinforeply.Data_Field.ParcelID, SLMessage.stringFromVariableOEM(pickinforeply.Data_Field.Name), s, pickinforeply.Data_Field.SnapshotID, pickinforeply.Data_Field.PosGlobal, pickinforeply.Data_Field.SortOrder, pickinforeply.Data_Field.Enabled);
        }
    }
}
