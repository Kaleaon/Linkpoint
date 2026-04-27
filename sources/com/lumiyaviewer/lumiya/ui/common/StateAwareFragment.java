// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.common;

import android.support.v4.app.Fragment;

public class StateAwareFragment extends Fragment
{

    private boolean fragmentStarted;
    private boolean fragmentVisible;

    public StateAwareFragment()
    {
        fragmentStarted = false;
        fragmentVisible = false;
    }

    public boolean isFragmentStarted()
    {
        return fragmentStarted;
    }

    public boolean isFragmentVisible()
    {
        return fragmentVisible;
    }

    public void onPause()
    {
        fragmentVisible = false;
        super.onPause();
    }

    public void onResume()
    {
        super.onResume();
        fragmentVisible = true;
    }

    public void onStart()
    {
        super.onStart();
        fragmentStarted = true;
    }

    public void onStop()
    {
        fragmentStarted = false;
        super.onStop();
    }
}
