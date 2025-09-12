// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.render;

import android.animation.ValueAnimator;
import android.widget.FrameLayout;

// Referenced classes of package com.lumiyaviewer.lumiya.ui.render:
//            WorldViewActivity

class this._cls0
    implements Runnable
{

    final WorldViewActivity this$0;

    public void run()
    {
        if (android.os.T >= 11)
        {
            if (WorldViewActivity._2D_get2(WorldViewActivity.this) != null)
            {
                WorldViewActivity._2D_get2(WorldViewActivity.this).cancel();
            }
            insetsBackground.setAlpha(1.0F);
        }
    }

    ()
    {
        this$0 = WorldViewActivity.this;
        super();
    }
}
