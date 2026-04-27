// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.common;

import android.app.Activity;
import android.content.Context;
import com.lumiyaviewer.lumiya.ui.login.LogoutDialog;

// Referenced classes of package com.lumiyaviewer.lumiya.ui.common:
//            NavDrawerAdapter

static final class vDrawerItem extends vDrawerItem
{

    public void onClick(Context context)
    {
        if (context instanceof Activity)
        {
            LogoutDialog.show((Activity)context);
        }
    }

    vDrawerItem(int i, int j, int k)
    {
        super(i, j, k);
    }
}
