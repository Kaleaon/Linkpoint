// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.render;

import android.view.ScaleGestureDetector;
import android.view.View;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.slproto.modules.SLAvatarControl;

// Referenced classes of package com.lumiyaviewer.lumiya.ui.render:
//            WorldViewActivity, WorldSurfaceView

class Listener extends android.view..SimpleOnScaleGestureListener
{

    final WorldViewActivity this$0;

    public boolean onScale(ScaleGestureDetector scalegesturedetector)
    {
        Debug.Printf("Gesture: scale factor: %f", new Object[] {
            Float.valueOf(scalegesturedetector.getScaleFactor())
        });
        if (WorldViewActivity._2D_get4(WorldViewActivity.this) != 0)
        {
            WorldViewActivity._2D_set3(WorldViewActivity.this, Math.max(0.1F, Math.min(WorldViewActivity._2D_get8(WorldViewActivity.this) * scalegesturedetector.getScaleFactor(), 10F)));
            WorldViewActivity._2D_get14(WorldViewActivity.this).setHUDScaleFactor(WorldViewActivity._2D_get8(WorldViewActivity.this));
        } else
        {
            float f4 = worldViewTouchReceiver.getWidth();
            float f3 = worldViewTouchReceiver.getHeight();
            float f = scalegesturedetector.getFocusX();
            float f1 = scalegesturedetector.getFocusY();
            float f2 = f / f4;
            f4 = f3 / f4;
            float f5 = f1 / f3;
            float f6 = (f - WorldViewActivity._2D_get17(WorldViewActivity.this)) / f3;
            f3 = (f1 - WorldViewActivity._2D_get18(WorldViewActivity.this)) / f3;
            WorldViewActivity._2D_set7(WorldViewActivity.this, f);
            WorldViewActivity._2D_set8(WorldViewActivity.this, f1);
            if (WorldViewActivity._2D_get1(WorldViewActivity.this) != null)
            {
                WorldViewActivity._2D_get1(WorldViewActivity.this).processCameraZoom(scalegesturedetector.getScaleFactor(), -((f2 - 0.5F) * f4) * 2.0F, -(f5 - 0.5F) * 2.0F, f6, f3);
                return true;
            }
        }
        return true;
    }

    public boolean onScaleBegin(ScaleGestureDetector scalegesturedetector)
    {
        WorldViewActivity._2D_set4(WorldViewActivity.this, true);
        WorldViewActivity._2D_set7(WorldViewActivity.this, scalegesturedetector.getFocusX());
        WorldViewActivity._2D_set8(WorldViewActivity.this, scalegesturedetector.getFocusY());
        return true;
    }

    public void onScaleEnd(ScaleGestureDetector scalegesturedetector)
    {
        WorldViewActivity._2D_set4(WorldViewActivity.this, false);
    }

    l()
    {
        this$0 = WorldViewActivity.this;
        super();
    }
}
