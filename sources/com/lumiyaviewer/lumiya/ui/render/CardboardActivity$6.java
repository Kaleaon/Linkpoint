// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.render;

import com.google.vr.sdk.base.GvrView;
import com.lumiyaviewer.lumiya.Debug;

// Referenced classes of package com.lumiyaviewer.lumiya.ui.render:
//            CardboardActivity

class this._cls0
    implements com.google.vr.sdk.controller.entListener
{

    final CardboardActivity this$0;

    public void onApiStatusChanged(int i)
    {
        Debug.Printf("Cardboard: controller API status: %d", new Object[] {
            Integer.valueOf(i)
        });
    }

    public void onRecentered()
    {
        if (CardboardActivity._2D_get7(CardboardActivity.this) != null)
        {
            CardboardActivity._2D_get7(CardboardActivity.this).recenterHeadTracker();
        }
    }

    stener()
    {
        this$0 = CardboardActivity.this;
        super();
    }
}
