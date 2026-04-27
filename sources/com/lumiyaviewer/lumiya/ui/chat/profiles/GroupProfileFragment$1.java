// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.chat.profiles;

import com.google.common.collect.ImmutableList;

// Referenced classes of package com.lumiyaviewer.lumiya.ui.chat.profiles:
//            GroupProfileFragment

class this._cls0
    implements android.support.v4.view.istener
{

    final GroupProfileFragment this$0;

    public void onPageScrollStateChanged(int i)
    {
    }

    public void onPageScrolled(int i, float f, int j)
    {
    }

    public void onPageSelected(int i)
    {
        if (GroupProfileFragment._2D_get1(GroupProfileFragment.this) != null)
        {
            ImmutableList immutablelist = GroupProfileFragment._2D_get1(GroupProfileFragment.this).getTabs();
            if (immutablelist != null && i >= 0 && i < immutablelist.size())
            {
                GroupProfileFragment._2D_set1(GroupProfileFragment.this, (ofileTab)immutablelist.get(i));
                GroupProfileFragment._2D_set0(GroupProfileFragment.this, GroupProfileFragment._2D_get2(GroupProfileFragment.this));
            }
        }
    }

    ofileTab()
    {
        this$0 = GroupProfileFragment.this;
        super();
    }
}
