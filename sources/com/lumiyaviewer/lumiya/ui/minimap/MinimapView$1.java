// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.minimap;

import android.view.ScaleGestureDetector;

// Referenced classes of package com.lumiyaviewer.lumiya.ui.minimap:
//            MinimapView

class stureListener extends android.view.tector.SimpleOnScaleGestureListener
{

    final MinimapView this$0;

    public boolean onScale(ScaleGestureDetector scalegesturedetector)
    {
        MinimapView._2D_set0(MinimapView.this, Math.min(Math.max(MinimapView._2D_get0(MinimapView.this) * scalegesturedetector.getScaleFactor(), 1.0F), 5F));
        invalidate();
        return true;
    }

    stureListener()
    {
        this$0 = MinimapView.this;
        super();
    }
}
