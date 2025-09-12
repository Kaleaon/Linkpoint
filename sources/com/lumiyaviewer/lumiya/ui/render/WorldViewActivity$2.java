// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.render;

import android.os.Handler;
import android.os.SystemClock;
import com.lumiyaviewer.lumiya.Debug;

// Referenced classes of package com.lumiyaviewer.lumiya.ui.render:
//            WorldViewActivity

class this._cls0
    implements Runnable
{

    final WorldViewActivity this$0;

    public void run()
    {
        long l;
label0:
        {
            WorldViewActivity._2D_set6(WorldViewActivity.this, false);
            if (!WorldViewActivity._2D_wrap0(WorldViewActivity.this) && WorldViewActivity._2D_get9(WorldViewActivity.this) ^ true)
            {
                l = SystemClock.uptimeMillis();
                l = (WorldViewActivity._2D_get13(WorldViewActivity.this) + 6000L) - l;
                Debug.Printf("ObjectDeselect: remaining %d", new Object[] {
                    Long.valueOf(l)
                });
                if (l > 0L)
                {
                    break label0;
                }
                handlePickedObject(null);
            }
            return;
        }
        WorldViewActivity._2D_set6(WorldViewActivity.this, true);
        WorldViewActivity._2D_get15(WorldViewActivity.this).postDelayed(WorldViewActivity._2D_get16(WorldViewActivity.this), l);
    }

    ()
    {
        this$0 = WorldViewActivity.this;
        super();
    }
}
