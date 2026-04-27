// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.render;

import android.view.View;
import butterknife.internal.DebouncingOnClickListener;

// Referenced classes of package com.lumiyaviewer.lumiya.ui.render:
//            WorldViewActivity_ViewBinding, WorldViewActivity

class val.target extends DebouncingOnClickListener
{

    final WorldViewActivity_ViewBinding this$0;
    final WorldViewActivity val$target;

    public void doClick(View view)
    {
        val$target.onCamOnButton();
    }

    ()
    {
        this$0 = final_worldviewactivity_viewbinding;
        val$target = WorldViewActivity.this;
        super();
    }
}
