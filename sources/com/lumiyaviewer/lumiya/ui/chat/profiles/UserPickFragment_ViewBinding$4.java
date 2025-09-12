// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.chat.profiles;

import android.view.View;
import butterknife.internal.DebouncingOnClickListener;

// Referenced classes of package com.lumiyaviewer.lumiya.ui.chat.profiles:
//            UserPickFragment_ViewBinding, UserPickFragment

class val.target extends DebouncingOnClickListener
{

    final UserPickFragment_ViewBinding this$0;
    final UserPickFragment val$target;

    public void doClick(View view)
    {
        val$target.onTeleportToPickClick(view);
    }

    ()
    {
        this$0 = final_userpickfragment_viewbinding;
        val$target = UserPickFragment.this;
        super();
    }
}
