// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.chat.profiles;

import android.view.View;
import butterknife.internal.DebouncingOnClickListener;

// Referenced classes of package com.lumiyaviewer.lumiya.ui.chat.profiles:
//            UserMainProfileTab_ViewBinding, UserMainProfileTab

class val.target extends DebouncingOnClickListener
{

    final UserMainProfileTab_ViewBinding this$0;
    final UserMainProfileTab val$target;

    public void doClick(View view)
    {
        val$target.onViewProfileClicked(view);
    }

    ()
    {
        this$0 = final_usermainprofiletab_viewbinding;
        val$target = UserMainProfileTab.this;
        super();
    }
}
