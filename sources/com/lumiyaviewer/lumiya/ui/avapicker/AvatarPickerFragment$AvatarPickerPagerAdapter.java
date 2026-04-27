// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.avapicker;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import com.lumiyaviewer.lumiya.ui.common.ActivityUtils;

// Referenced classes of package com.lumiyaviewer.lumiya.ui.avapicker:
//            AvatarPickerFragment

public class context extends PagerAdapter
{

    private final Context context;
    final AvatarPickerFragment this$0;

    public void destroyItem(ViewGroup viewgroup, int i, Object obj)
    {
        if (obj instanceof View)
        {
            if (obj instanceof ListView)
            {
                ((ListView)obj).setAdapter(null);
            }
            viewgroup.removeView((View)obj);
        }
    }

    public int getCount()
    {
        return context().length;
    }

    public CharSequence getPageTitle(int i)
    {
        if (i >= 0 && i < context().length)
        {
            return context()[i].context();
        } else
        {
            return null;
        }
    }

    public Object instantiateItem(ViewGroup viewgroup, int i)
    {
        if (i >= 0 && i < context().length)
        {
            context context1 = context()[i];
            ListView listview = new ListView(context);
            listview.setOnItemClickListener(AvatarPickerFragment.this);
            listview.setAdapter(AvatarPickerFragment._2D_wrap0(AvatarPickerFragment.this, getContext(), ActivityUtils.getUserManager(getArguments()), context1));
            viewgroup.addView(listview);
            return listview;
        } else
        {
            return null;
        }
    }

    public boolean isViewFromObject(View view, Object obj)
    {
        return view == obj;
    }

    public (Context context1)
    {
        this$0 = AvatarPickerFragment.this;
        super();
        context = context1;
    }
}
