// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.settings;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.preference.Preference;
import android.util.AttributeSet;
import com.lumiyaviewer.lumiya.ui.notify.NotificationChannels;

// Referenced classes of package com.lumiyaviewer.lumiya.ui.settings:
//            NotificationType, NotificationSettings

public class PreferenceSubPage extends Preference
{

    private NotificationType notificationType;
    private boolean pageNotificationDetails;
    private int pageResource;

    public PreferenceSubPage(Context context)
    {
        super(context);
        pageResource = 0;
        pageNotificationDetails = false;
        notificationType = null;
    }

    public PreferenceSubPage(Context context, AttributeSet attributeset)
    {
        super(context, attributeset);
        pageResource = 0;
        pageNotificationDetails = false;
        notificationType = null;
        applyAttributes(context, attributeset, 0, 0);
    }

    public PreferenceSubPage(Context context, AttributeSet attributeset, int i)
    {
        super(context, attributeset, i);
        pageResource = 0;
        pageNotificationDetails = false;
        notificationType = null;
        applyAttributes(context, attributeset, i, 0);
    }

    public PreferenceSubPage(Context context, AttributeSet attributeset, int i, int j)
    {
        super(context, attributeset, i, j);
        pageResource = 0;
        pageNotificationDetails = false;
        notificationType = null;
        applyAttributes(context, attributeset, i, j);
    }

    private void applyAttributes(Context context, AttributeSet attributeset, int i, int j)
    {
        context = context.getTheme().obtainStyledAttributes(attributeset, com.lumiyaviewer.lumiya.R.styleable.PreferenceSubPage, i, j);
        pageResource = context.getResourceId(0, pageResource);
        pageNotificationDetails = context.getBoolean(1, pageNotificationDetails);
        attributeset = context.getString(2);
        if (attributeset == null)
        {
            break MISSING_BLOCK_LABEL_59;
        }
        notificationType = NotificationType.valueOf(attributeset);
_L2:
        context.recycle();
        return;
        attributeset;
        notificationType = null;
        if (true) goto _L2; else goto _L1
_L1:
        attributeset;
        context.recycle();
        throw attributeset;
    }

    NotificationType getNotificationType()
    {
        if (pageNotificationDetails)
        {
            return notificationType;
        } else
        {
            return null;
        }
    }

    int getPageResource()
    {
        return pageResource;
    }

    public CharSequence getSummary()
    {
        if (pageNotificationDetails && notificationType != null)
        {
            Object obj = NotificationChannels.getInstance();
            if (((NotificationChannels) (obj)).areNotificationsSystemControlled())
            {
                obj = ((NotificationChannels) (obj)).getNotificationSummary(getContext(), ((NotificationChannels) (obj)).getChannelByType(notificationType));
                if (obj != null)
                {
                    return ((CharSequence) (obj));
                } else
                {
                    return super.getSummary();
                }
            } else
            {
                NotificationSettings notificationsettings = new NotificationSettings(notificationType);
                notificationsettings.Load(getSharedPreferences());
                return notificationsettings.getSummary(getContext());
            }
        } else
        {
            return super.getSummary();
        }
    }
}
