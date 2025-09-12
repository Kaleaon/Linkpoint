// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.chat.profiles;

import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;
import com.google.common.collect.ImmutableList;
import java.lang.ref.WeakReference;
import java.util.Map;

// Referenced classes of package com.lumiyaviewer.lumiya.ui.chat.profiles:
//            GroupProfileFragment

private class this._cls0 extends FragmentStatePagerAdapter
{

    private ImmutableList tabs;
    final GroupProfileFragment this$0;

    public void destroyItem(ViewGroup viewgroup, int i, Object obj)
    {
        if (tabs != null)
        {
            this._cls0 _lcls0 = (tabs)tabs.get(i);
            if (_lcls0 != null)
            {
                GroupProfileFragment._2D_get0(GroupProfileFragment.this).remove(_lcls0);
            }
        }
        super.destroyItem(viewgroup, i, obj);
    }

    public int getCount()
    {
        if (tabs != null)
        {
            return tabs.size();
        } else
        {
            return 0;
        }
    }

    public Fragment getItem(int i)
    {
        if (tabs != null)
        {
            tabs tabs1 = (tabs)tabs.get(i);
            Fragment fragment;
            try
            {
                fragment = (Fragment)tabs(tabs1).newInstance();
                fragment.setArguments(GroupProfileFragment.makeSelection(GroupProfileFragment._2D_get2(GroupProfileFragment.this)));
                GroupProfileFragment._2D_get0(GroupProfileFragment.this).put(tabs1, new WeakReference(fragment));
            }
            catch (InstantiationException instantiationexception)
            {
                return null;
            }
            catch (IllegalAccessException illegalaccessexception)
            {
                return null;
            }
            return fragment;
        } else
        {
            return null;
        }
    }

    public CharSequence getPageTitle(int i)
    {
        if (tabs != null)
        {
            this._cls0 _lcls0 = (tabs)tabs.get(i);
            return getString(tabs(_lcls0));
        } else
        {
            return null;
        }
    }

    ImmutableList getTabs()
    {
        return tabs;
    }

    public Parcelable saveState()
    {
        return null;
    }

    void setTabs(ImmutableList immutablelist)
    {
        if (tabs != immutablelist)
        {
            tabs = immutablelist;
            notifyDataSetChanged();
        }
    }

    (FragmentManager fragmentmanager)
    {
        this$0 = GroupProfileFragment.this;
        super(fragmentmanager);
    }
}
