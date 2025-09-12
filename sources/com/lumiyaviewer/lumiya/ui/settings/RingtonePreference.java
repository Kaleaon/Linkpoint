// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v7.preference.Preference;
import android.util.AttributeSet;
import com.google.common.base.Objects;
import com.lumiyaviewer.lumiya.ui.media.NotificationSounds;

public class RingtonePreference extends Preference
{

    private int defaultRawResource;

    public RingtonePreference(Context context)
    {
        super(context);
        defaultRawResource = 0;
    }

    public RingtonePreference(Context context, AttributeSet attributeset)
    {
        super(context, attributeset);
        defaultRawResource = 0;
        applyAttributes(context, attributeset, 0, 0);
    }

    public RingtonePreference(Context context, AttributeSet attributeset, int i)
    {
        super(context, attributeset, i);
        defaultRawResource = 0;
        applyAttributes(context, attributeset, i, 0);
    }

    public RingtonePreference(Context context, AttributeSet attributeset, int i, int j)
    {
        super(context, attributeset, i, j);
        defaultRawResource = 0;
        applyAttributes(context, attributeset, i, j);
    }

    private void applyAttributes(Context context, AttributeSet attributeset, int i, int j)
    {
        context = context.getTheme().obtainStyledAttributes(attributeset, com.lumiyaviewer.lumiya.R.styleable.RingtonePreference, i, j);
        defaultRawResource = context.getResourceId(0, defaultRawResource);
        context.recycle();
        return;
        attributeset;
        context.recycle();
        throw attributeset;
    }

    int getDefaultRawResource()
    {
        return defaultRawResource;
    }

    public CharSequence getSummary()
    {
        String s;
        Object obj;
        Uri uri;
        s = "No sound selected";
        obj = getSharedPreferences().getString(getKey(), null);
        if (obj == null)
        {
            break MISSING_BLOCK_LABEL_78;
        }
        uri = Uri.parse(((String) (obj)));
        if (!Objects.equal(NotificationSounds.getResourceUri(defaultRawResource), uri)) goto _L2; else goto _L1
_L1:
        s = "Default";
_L4:
        return s;
_L2:
        if (((String) (obj)).isEmpty())
        {
            return "Silent";
        }
        obj = RingtoneManager.getRingtone(getContext(), uri);
        if (obj == null) goto _L4; else goto _L3
_L3:
        return ((Ringtone) (obj)).getTitle(getContext());
        return "Default";
    }
}
