// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.common;

import android.view.View;

// Referenced classes of package com.lumiyaviewer.lumiya.ui.common:
//            SwipeDismissListViewTouchListener

class view
    implements Comparable
{

    public int position;
    final SwipeDismissListViewTouchListener this$0;
    public View view;

    public int compareTo(view view1)
    {
        return view1.position - position;
    }

    public volatile int compareTo(Object obj)
    {
        return compareTo((compareTo)obj);
    }

    public (int i, View view1)
    {
        this$0 = SwipeDismissListViewTouchListener.this;
        super();
        position = i;
        view = view1;
    }
}
