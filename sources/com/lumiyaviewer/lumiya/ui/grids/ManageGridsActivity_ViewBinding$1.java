// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.grids;

import android.view.View;
import butterknife.internal.DebouncingOnClickListener;

// Referenced classes of package com.lumiyaviewer.lumiya.ui.grids:
//            ManageGridsActivity_ViewBinding, ManageGridsActivity

class val.target extends DebouncingOnClickListener
{

    final ManageGridsActivity_ViewBinding this$0;
    final ManageGridsActivity val$target;

    public void doClick(View view)
    {
        val$target.onAddNewGridButton();
    }

    ()
    {
        this$0 = final_managegridsactivity_viewbinding;
        val$target = ManageGridsActivity.this;
        super();
    }
}
