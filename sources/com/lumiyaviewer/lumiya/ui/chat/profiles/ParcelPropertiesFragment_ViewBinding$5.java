// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.chat.profiles;

import android.view.View;
import butterknife.internal.DebouncingOnClickListener;

// Referenced classes of package com.lumiyaviewer.lumiya.ui.chat.profiles:
//            ParcelPropertiesFragment_ViewBinding, ParcelPropertiesFragment

class val.target extends DebouncingOnClickListener
{

    final ParcelPropertiesFragment_ViewBinding this$0;
    final ParcelPropertiesFragment val$target;

    public void doClick(View view)
    {
        val$target.onSetHomeButton();
    }

    ()
    {
        this$0 = final_parcelpropertiesfragment_viewbinding;
        val$target = ParcelPropertiesFragment.this;
        super();
    }
}
