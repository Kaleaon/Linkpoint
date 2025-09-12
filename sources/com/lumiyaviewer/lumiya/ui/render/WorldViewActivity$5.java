// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.render;

import android.support.v4.view.GestureDetectorCompat;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

// Referenced classes of package com.lumiyaviewer.lumiya.ui.render:
//            WorldViewActivity, WorldSurfaceView

class this._cls0
    implements android.view.
{

    final WorldViewActivity this$0;

    public boolean onTouch(View view, MotionEvent motionevent)
    {
        boolean flag1 = WorldViewActivity._2D_get11(WorldViewActivity.this);
        motionevent.getActionMasked();
        JVM INSTR tableswitch 0 1: default 36
    //                   0 143
    //                   1 157;
           goto _L1 _L2 _L3
_L1:
        boolean flag = false;
_L5:
        if (WorldViewActivity._2D_get11(WorldViewActivity.this) && flag1 ^ true)
        {
            WorldViewActivity._2D_get14(WorldViewActivity.this).setIsInteracting(true);
        }
        WorldViewActivity._2D_set9(WorldViewActivity.this, WorldViewActivity._2D_get10(WorldViewActivity.this));
        boolean flag2 = WorldViewActivity._2D_get19(WorldViewActivity.this).onTouchEvent(motionevent);
        boolean flag3 = WorldViewActivity._2D_get5(WorldViewActivity.this).onTouchEvent(motionevent);
        if (flag1 && WorldViewActivity._2D_get11(WorldViewActivity.this) ^ true)
        {
            WorldViewActivity._2D_get14(WorldViewActivity.this).setIsInteracting(false);
        }
        return flag | flag2 | flag3;
_L2:
        WorldViewActivity._2D_set5(WorldViewActivity.this, true);
        flag = true;
        continue; /* Loop/switch isn't completed */
_L3:
        WorldViewActivity._2D_set5(WorldViewActivity.this, false);
        flag = true;
        if (true) goto _L5; else goto _L4
_L4:
    }

    ()
    {
        this$0 = WorldViewActivity.this;
        super();
    }
}
