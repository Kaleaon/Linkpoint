// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.settings;

import android.content.Context;
import android.support.v7.preference.Preference;
import android.util.AttributeSet;
import com.google.common.base.Strings;

public class CacheLocationPreference extends Preference
{

    public CacheLocationPreference(Context context)
    {
        super(context);
    }

    public CacheLocationPreference(Context context, AttributeSet attributeset)
    {
        super(context, attributeset);
    }

    public CacheLocationPreference(Context context, AttributeSet attributeset, int i)
    {
        super(context, attributeset, i);
    }

    public CacheLocationPreference(Context context, AttributeSet attributeset, int i, int j)
    {
        super(context, attributeset, i, j);
    }

    public static String makeDisplayableCacheLocation(String s)
    {
        int i = s.indexOf("/Android");
        String s1 = s;
        if (i >= 0)
        {
            s1 = s.substring(0, i);
        }
        i = s1.indexOf("/com.lumiyaviewer.lumiya");
        s = s1;
        if (i >= 0)
        {
            s = s1.substring(0, i);
        }
        return s;
    }

    public CharSequence getSummary()
    {
        String s = getPersistedString(null);
        if (Strings.isNullOrEmpty(s))
        {
            return getContext().getString(0x7f0900d6);
        } else
        {
            return makeDisplayableCacheLocation(s);
        }
    }
}
