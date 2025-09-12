// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.search;

import android.view.KeyEvent;
import android.widget.TextView;

// Referenced classes of package com.lumiyaviewer.lumiya.ui.search:
//            SearchGridFragment_ViewBinding, SearchGridFragment

class val.target
    implements android.widget.ment_ViewBinding._cls1
{

    final SearchGridFragment_ViewBinding this$0;
    final SearchGridFragment val$target;

    public boolean onEditorAction(TextView textview, int i, KeyEvent keyevent)
    {
        return val$target.onSearchTextAction(i, keyevent);
    }

    ()
    {
        this$0 = final_searchgridfragment_viewbinding;
        val$target = SearchGridFragment.this;
        super();
    }
}
