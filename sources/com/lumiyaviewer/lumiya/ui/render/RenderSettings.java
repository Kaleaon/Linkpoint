// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.render;

import android.content.SharedPreferences;

public class RenderSettings
{

    public final int avatarCountLimit;
    public final int drawDistance;

    public RenderSettings(SharedPreferences sharedpreferences)
    {
        String s;
        int i;
        s = sharedpreferences.getString("drawDistance", "20");
        i = 20;
        int j = Integer.parseInt(s);
        i = j;
_L1:
        sharedpreferences = sharedpreferences.getString("avatarCountLimit", "5");
        j = 5;
        int k = Integer.parseInt(sharedpreferences);
        j = k;
_L2:
        drawDistance = i;
        avatarCountLimit = j;
        return;
        Exception exception;
        exception;
          goto _L1
        sharedpreferences;
          goto _L2
    }
}
