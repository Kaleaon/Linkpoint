// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.common;

import android.animation.ValueAnimator;

// Referenced classes of package com.lumiyaviewer.lumiya.ui.common:
//            ButteryProgressBar

class this._cls0
    implements android.animation.rUpdateListener
{

    final ButteryProgressBar this$0;

    public void onAnimationUpdate(ValueAnimator valueanimator)
    {
        invalidate();
    }

    ()
    {
        this$0 = ButteryProgressBar.this;
        super();
    }
}
