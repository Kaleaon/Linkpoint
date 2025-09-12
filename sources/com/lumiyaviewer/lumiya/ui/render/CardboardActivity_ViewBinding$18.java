// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.render;

import android.view.View;
import butterknife.internal.DebouncingOnClickListener;

// Referenced classes of package com.lumiyaviewer.lumiya.ui.render:
//            CardboardActivity_ViewBinding, CardboardActivity

class val.target extends DebouncingOnClickListener
{

    final CardboardActivity_ViewBinding this$0;
    final CardboardActivity val$target;

    public void doClick(View view)
    {
        val$target.onObjectSit();
    }

    ()
    {
        this$0 = final_cardboardactivity_viewbinding;
        val$target = CardboardActivity.this;
        super();
    }
}
