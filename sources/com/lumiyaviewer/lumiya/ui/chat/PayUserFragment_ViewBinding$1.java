// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.chat;

import android.view.View;
import butterknife.internal.DebouncingOnClickListener;

// Referenced classes of package com.lumiyaviewer.lumiya.ui.chat:
//            PayUserFragment_ViewBinding, PayUserFragment

class val.target extends DebouncingOnClickListener
{

    final PayUserFragment_ViewBinding this$0;
    final PayUserFragment val$target;

    public void doClick(View view)
    {
        val$target.onUserPayButton();
    }

    ()
    {
        this$0 = final_payuserfragment_viewbinding;
        val$target = PayUserFragment.this;
        super();
    }
}
