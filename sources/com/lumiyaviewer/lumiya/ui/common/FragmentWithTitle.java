// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.common;

import android.os.Bundle;
import com.lumiyaviewer.lumiya.Debug;

// Referenced classes of package com.lumiyaviewer.lumiya.ui.common:
//            StateAwareFragment, FragmentHasTitle, DetailsActivity

public class FragmentWithTitle extends StateAwareFragment
    implements FragmentHasTitle
{

    private static final String FRAGMENT_SUBTITLE_TAG = "FragmentWithTitle:fragmentSubTitle";
    private static final String FRAGMENT_TITLE_TAG = "FragmentWithTitle:fragmentTitle";
    private String fragmentSubTitle;
    private String fragmentTitle;

    public FragmentWithTitle()
    {
        fragmentTitle = null;
        fragmentSubTitle = null;
    }

    public String getSubTitle()
    {
        return fragmentSubTitle;
    }

    public String getTitle()
    {
        return fragmentTitle;
    }

    public void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);
        if (bundle != null)
        {
            fragmentTitle = bundle.getString("FragmentWithTitle:fragmentTitle");
            fragmentSubTitle = bundle.getString("FragmentWithTitle:fragmentSubTitle");
        }
    }

    public void onDetach()
    {
        super.onDetach();
        android.support.v4.app.FragmentActivity fragmentactivity = getActivity();
        if (fragmentactivity instanceof DetailsActivity)
        {
            ((DetailsActivity)fragmentactivity).onFragmentTitleUpdated();
        }
    }

    public void onHiddenChanged(boolean flag)
    {
        super.onHiddenChanged(flag);
        android.support.v4.app.FragmentActivity fragmentactivity = getActivity();
        if (fragmentactivity instanceof DetailsActivity)
        {
            ((DetailsActivity)fragmentactivity).onFragmentTitleUpdated();
        }
    }

    public void onSaveInstanceState(Bundle bundle)
    {
        super.onSaveInstanceState(bundle);
        bundle.putString("FragmentWithTitle:fragmentTitle", fragmentTitle);
        bundle.putString("FragmentWithTitle:fragmentSubTitle", fragmentSubTitle);
    }

    public void onStart()
    {
        super.onStart();
        android.support.v4.app.FragmentActivity fragmentactivity = getActivity();
        if (fragmentactivity instanceof DetailsActivity)
        {
            ((DetailsActivity)fragmentactivity).onFragmentTitleUpdated();
        }
    }

    public void setTitle(String s, String s1)
    {
        fragmentTitle = s;
        fragmentSubTitle = s1;
        android.support.v4.app.FragmentActivity fragmentactivity = getActivity();
        Debug.Printf("updateTitle: title '%s', subTitle '%s', activity %s, fragment %s", new Object[] {
            s, s1, fragmentactivity, this
        });
        if (fragmentactivity instanceof DetailsActivity)
        {
            ((DetailsActivity)fragmentactivity).onFragmentTitleUpdated();
        }
    }
}
