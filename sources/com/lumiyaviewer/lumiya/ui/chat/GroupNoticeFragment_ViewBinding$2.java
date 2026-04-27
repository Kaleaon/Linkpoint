// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.chat;

import android.view.View;
import butterknife.internal.DebouncingOnClickListener;

// Referenced classes of package com.lumiyaviewer.lumiya.ui.chat:
//            GroupNoticeFragment_ViewBinding, GroupNoticeFragment

class val.target extends DebouncingOnClickListener
{

    final GroupNoticeFragment_ViewBinding this$0;
    final GroupNoticeFragment val$target;

    public void doClick(View view)
    {
        val$target.onGroupNoticeSendButton();
    }

    ()
    {
        this$0 = final_groupnoticefragment_viewbinding;
        val$target = GroupNoticeFragment.this;
        super();
    }
}
