// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.search;

import android.view.View;
import butterknife.internal.DebouncingOnClickListener;

// Referenced classes of package com.lumiyaviewer.lumiya.ui.search:
//            SearchGridFragment_ViewBinding, SearchGridFragment

class val.target extends DebouncingOnClickListener
{

    final SearchGridFragment_ViewBinding this$0;
    final SearchGridFragment val$target;

    public void doClick(View view)
    {
        val$target.onSearchButtonClicked();
    }

    ()
    {
        this$0 = final_searchgridfragment_viewbinding;
        val$target = SearchGridFragment.this;
        super();
    }
}
