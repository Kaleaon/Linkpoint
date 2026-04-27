// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.chat.profiles;

import android.content.Context;
import android.os.Bundle;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import com.lumiyaviewer.lumiya.slproto.messages.AvatarPropertiesReply;
import com.lumiyaviewer.lumiya.slproto.modules.SLModules;
import com.lumiyaviewer.lumiya.slproto.modules.SLUserProfiles;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import com.lumiyaviewer.lumiya.ui.common.ChatterFragment;

// Referenced classes of package com.lumiyaviewer.lumiya.ui.chat.profiles:
//            ProfileTextFieldEditFragment

public class UserAboutTextEditFragment extends ProfileTextFieldEditFragment
{

    private static final String IS_FIRST_LIFE_KEY = "isFirstLife";
    private AvatarPropertiesReply avatarProperties;

    public UserAboutTextEditFragment()
    {
    }

    private boolean isFirstLife()
    {
        Bundle bundle = getArguments();
        if (bundle != null)
        {
            return bundle.getBoolean("isFirstLife");
        } else
        {
            return false;
        }
    }

    public static Bundle makeSelection(ChatterID chatterid, boolean flag)
    {
        chatterid = ChatterFragment.makeSelection(chatterid);
        chatterid.putBoolean("isFirstLife", flag);
        return chatterid;
    }

    protected String decorateFragmentTitle(String s)
    {
        return getString(0x7f090105, new Object[] {
            s
        });
    }

    protected String getFieldHint(Context context)
    {
        return getString(0x7f090104);
    }

    protected void onAvatarProperties(AvatarPropertiesReply avatarpropertiesreply)
    {
        avatarProperties = avatarpropertiesreply;
        if (isFirstLife())
        {
            avatarpropertiesreply = SLMessage.stringFromVariableOEM(avatarProperties.PropertiesData_Field.FLAboutText);
        } else
        {
            avatarpropertiesreply = SLMessage.stringFromVariableUTF(avatarpropertiesreply.PropertiesData_Field.AboutText);
        }
        setOriginalText(avatarpropertiesreply);
    }

    protected void saveEditedText(SLAgentCircuit slagentcircuit, ChatterID chatterid, String s)
    {
        boolean flag1 = true;
        if (avatarProperties != null)
        {
            chatterid = SLMessage.stringFromVariableUTF(avatarProperties.PropertiesData_Field.AboutText);
            Object obj = SLMessage.stringFromVariableOEM(avatarProperties.PropertiesData_Field.FLAboutText);
            java.util.UUID uuid;
            boolean flag;
            if (!isFirstLife())
            {
                chatterid = s;
                s = ((String) (obj));
            }
            slagentcircuit = slagentcircuit.getModules().userProfiles;
            obj = avatarProperties.PropertiesData_Field.ImageID;
            uuid = avatarProperties.PropertiesData_Field.FLImageID;
            if ((avatarProperties.PropertiesData_Field.Flags & 1) != 0)
            {
                flag = true;
            } else
            {
                flag = false;
            }
            if ((avatarProperties.PropertiesData_Field.Flags & 2) == 0)
            {
                flag1 = false;
            }
            slagentcircuit.UpdateAvatarProperties(((java.util.UUID) (obj)), uuid, chatterid, s, flag, flag1, SLMessage.stringFromVariableOEM(avatarProperties.PropertiesData_Field.ProfileURL));
        }
    }
}
