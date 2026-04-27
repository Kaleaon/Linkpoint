// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.myava;

import android.view.View;
import butterknife.internal.DebouncingOnClickListener;

// Referenced classes of package com.lumiyaviewer.lumiya.ui.myava:
//            MuteListFragment_ViewBinding, MuteListFragment

class val.target extends DebouncingOnClickListener
{

    final MuteListFragment_ViewBinding this$0;
    final MuteListFragment val$target;

    public void doClick(View view)
    {
        val$target.onAddMuteListButtonClick();
    }

    ()
    {
        this$0 = final_mutelistfragment_viewbinding;
        val$target = MuteListFragment.this;
        super();
    }
}
