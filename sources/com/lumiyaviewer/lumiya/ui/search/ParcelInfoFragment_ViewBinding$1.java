// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.search;

import android.view.View;
import butterknife.internal.DebouncingOnClickListener;

// Referenced classes of package com.lumiyaviewer.lumiya.ui.search:
//            ParcelInfoFragment_ViewBinding, ParcelInfoFragment

class val.target extends DebouncingOnClickListener
{

    final ParcelInfoFragment_ViewBinding this$0;
    final ParcelInfoFragment val$target;

    public void doClick(View view)
    {
        val$target.onParcelTeleportButton();
    }

    ()
    {
        this$0 = final_parcelinfofragment_viewbinding;
        val$target = ParcelInfoFragment.this;
        super();
    }
}
