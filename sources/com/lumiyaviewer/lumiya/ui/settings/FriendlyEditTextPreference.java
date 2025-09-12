// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.settings;

import android.content.Context;
import android.support.v7.preference.EditTextPreference;
import android.text.TextUtils;
import android.util.AttributeSet;

public class FriendlyEditTextPreference extends EditTextPreference
{

    public FriendlyEditTextPreference(Context context)
    {
        super(context);
    }

    public FriendlyEditTextPreference(Context context, AttributeSet attributeset)
    {
        super(context, attributeset);
    }

    public FriendlyEditTextPreference(Context context, AttributeSet attributeset, int i)
    {
        super(context, attributeset, i);
    }

    public CharSequence getSummary()
    {
        String s = getText();
        if (TextUtils.isEmpty(s))
        {
            return null;
        }
        CharSequence charsequence = super.getSummary();
        if (charsequence != null)
        {
            return String.format(charsequence.toString(), new Object[] {
                s
            });
        } else
        {
            return null;
        }
    }
}
