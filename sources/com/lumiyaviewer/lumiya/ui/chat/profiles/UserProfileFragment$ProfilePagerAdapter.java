// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.chat.profiles;

import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;
import java.lang.ref.WeakReference;
import java.util.Map;

// Referenced classes of package com.lumiyaviewer.lumiya.ui.chat.profiles:
//            UserProfileFragment

private class this._cls0 extends FragmentStatePagerAdapter
{

    final UserProfileFragment this$0;

    public void destroyItem(ViewGroup viewgroup, int i, Object obj)
    {
        this._cls0 _lcls0 = com.lumiyaviewer.lumiya.ui.chat.profiles.this._mth0()[i];
        if (_lcls0 != null)
        {
            UserProfileFragment._2D_get0(UserProfileFragment.this).remove(_lcls0);
        }
        super.destroyItem(viewgroup, i, obj);
    }

    public int getCount()
    {
        return com.lumiyaviewer.lumiya.ui.chat.profiles.this._mth0().length;
    }

    public Fragment getItem(int i)
    {
        this._cls0 _lcls0 = com.lumiyaviewer.lumiya.ui.chat.profiles.this._mth0()[i];
        Fragment fragment;
        try
        {
            fragment = (Fragment)com.lumiyaviewer.lumiya.ui.chat.profiles.this._mth0(_lcls0).newInstance();
            fragment.setArguments(UserProfileFragment.makeSelection(UserProfileFragment._2D_get1(UserProfileFragment.this)));
            UserProfileFragment._2D_get0(UserProfileFragment.this).put(_lcls0, new WeakReference(fragment));
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
    }

    public CharSequence getPageTitle(int i)
    {
        this._cls0 _lcls0 = com.lumiyaviewer.lumiya.ui.chat.profiles.this._mth0()[i];
        return getString(com.lumiyaviewer.lumiya.ui.chat.profiles.this._mth0(_lcls0));
    }

    public Parcelable saveState()
    {
        return null;
    }

    (FragmentManager fragmentmanager)
    {
        this$0 = UserProfileFragment.this;
        super(fragmentmanager);
    }
}
