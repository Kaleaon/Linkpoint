// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.render.avatar;

import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.slproto.messages.AvatarAppearance;
import java.util.ArrayList;
import java.util.Arrays;

public class AvatarShapeParams
{

    private final int visualParamValues[];

    private AvatarShapeParams(int ai[])
    {
        visualParamValues = ai;
    }

    public static AvatarShapeParams create(AvatarShapeParams avatarshapeparams, AvatarAppearance avatarappearance)
    {
        Debug.Log((new StringBuilder()).append("DrawableAvatar: new appearance for avatar ").append(avatarappearance.Sender_Field.ID).append(", numParams = ").append(avatarappearance.VisualParam_Fields.size()).append(", appData = ").append(avatarappearance.AppearanceData_Fields.size()).toString());
        for (int i = 0; i < avatarappearance.AppearanceData_Fields.size(); i++)
        {
            Debug.Printf("appData[%d]: appVer %d, cofVer %d, flags 0x%x", new Object[] {
                Integer.valueOf(i), Integer.valueOf(((com.lumiyaviewer.lumiya.slproto.messages.AvatarAppearance.AppearanceData)avatarappearance.AppearanceData_Fields.get(i)).AppearanceVersion), Integer.valueOf(((com.lumiyaviewer.lumiya.slproto.messages.AvatarAppearance.AppearanceData)avatarappearance.AppearanceData_Fields.get(i)).CofVersion), Integer.valueOf(((com.lumiyaviewer.lumiya.slproto.messages.AvatarAppearance.AppearanceData)avatarappearance.AppearanceData_Fields.get(i)).Flags)
            });
        }

        int ai[] = new int[218];
        int j = 0;
        while (j < 218) 
        {
            if (j < avatarappearance.VisualParam_Fields.size())
            {
                ai[j] = ((com.lumiyaviewer.lumiya.slproto.messages.AvatarAppearance.VisualParam)avatarappearance.VisualParam_Fields.get(j)).ParamValue;
            } else
            {
                int k;
                if (avatarshapeparams != null)
                {
                    k = avatarshapeparams.visualParamValues[j];
                } else
                {
                    k = 0;
                }
                ai[j] = k;
            }
            j++;
        }
        return new AvatarShapeParams(ai);
    }

    public static AvatarShapeParams create(AvatarShapeParams avatarshapeparams, int ai[])
    {
        if (ai.length != 218)
        {
            int ai1[] = new int[218];
            System.arraycopy(ai, 0, ai1, 0, Math.min(ai.length, 218));
            if (ai.length < 218 && avatarshapeparams != null)
            {
                int i = ai.length;
                System.arraycopy(avatarshapeparams.visualParamValues, ai.length, ai1, ai.length, 218 - i);
                ai = ai1;
            } else
            {
                ai = ai1;
            }
        }
        return new AvatarShapeParams(ai);
    }

    public boolean equals(Object obj)
    {
        if (obj instanceof AvatarShapeParams)
        {
            return Arrays.equals(visualParamValues, ((AvatarShapeParams)obj).visualParamValues);
        } else
        {
            return false;
        }
    }

    public int getParamCount()
    {
        return 218;
    }

    public int getParamValue(int i)
    {
        if (i >= 0 && i < 218)
        {
            return visualParamValues[i];
        } else
        {
            return 0;
        }
    }

    public int hashCode()
    {
        return Arrays.hashCode(visualParamValues);
    }
}
