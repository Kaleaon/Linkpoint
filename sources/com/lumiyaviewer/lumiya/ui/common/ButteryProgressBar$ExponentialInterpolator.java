// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.common;

import android.view.animation.Interpolator;

// Referenced classes of package com.lumiyaviewer.lumiya.ui.common:
//            ButteryProgressBar

private static class <init>
    implements Interpolator
{

    public float getInterpolation(float f)
    {
        return (float)Math.pow(2D, f) - 1.0F;
    }

    private ()
    {
    }

    ( )
    {
        this();
    }
}
