// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.common;

import android.widget.AbsListView;

// Referenced classes of package com.lumiyaviewer.lumiya.ui.common:
//            SwipeDismissListViewTouchListener

class this._cls0
    implements android.widget.iewTouchListener._cls1
{

    final SwipeDismissListViewTouchListener this$0;

    public void onScroll(AbsListView abslistview, int i, int j, int k)
    {
    }

    public void onScrollStateChanged(AbsListView abslistview, int i)
    {
        boolean flag = true;
        abslistview = SwipeDismissListViewTouchListener.this;
        if (i == 1)
        {
            flag = false;
        }
        abslistview.setEnabled(flag);
    }

    ()
    {
        this$0 = SwipeDismissListViewTouchListener.this;
        super();
    }
}
