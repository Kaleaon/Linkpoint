// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import com.google.common.base.Objects;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.lumiyaviewer.lumiya.ui.media.NotificationSounds;
import com.lumiyaviewer.lumiya.utils.LEDAction;

// Referenced classes of package com.lumiyaviewer.lumiya.ui.settings:
//            NotificationType

public class NotificationSettings
{

    private LEDAction blinkAction;
    private String blinkColor;
    private boolean notificationEnabled;
    private String ringtone;
    private boolean soundEnabled;
    private NotificationType type;

    public NotificationSettings(NotificationType notificationtype)
    {
        type = notificationtype;
        notificationEnabled = false;
        soundEnabled = false;
        ringtone = "";
        blinkAction = LEDAction.None;
        blinkColor = "red";
    }

    private int getPrefColor(String s)
    {
        if (s.length() != 6)
        {
            return 0;
        }
        int i;
        try
        {
            i = Integer.parseInt(s, 16);
        }
        // Misplaced declaration of an exception variable
        catch (String s)
        {
            s.printStackTrace();
            return 0;
        }
        return i | 0xff000000;
    }

    private String getPreferenceValueName(Context context, String s, int i, int j)
    {
        String as[] = context.getResources().getStringArray(i);
        context = context.getResources().getStringArray(j);
        for (i = 0; i < as.length; i++)
        {
            if (as[i].equals(s))
            {
                return context[i];
            }
        }

        return "";
    }

    public void Load(SharedPreferences sharedpreferences)
    {
        notificationEnabled = sharedpreferences.getBoolean(type.getEnableKey(), true);
        soundEnabled = sharedpreferences.getBoolean(type.getPlaySoundKey(), true);
        Object obj = (NotificationSounds)NotificationSounds.defaultSounds.get(type);
        String s = type.getRingtoneKey();
        if (obj != null)
        {
            obj = ((NotificationSounds) (obj)).getUri().toString();
        } else
        {
            obj = null;
        }
        ringtone = sharedpreferences.getString(s, ((String) (obj)));
        blinkAction = LEDAction.getByPreferenceString(sharedpreferences.getString(type.getBlinkKey(), "none"));
        blinkColor = sharedpreferences.getString(type.getBlinkColorKey(), "FF0000");
    }

    public LEDAction getLEDAction()
    {
        return blinkAction;
    }

    public int getLEDColor()
    {
        return getPrefColor(blinkColor);
    }

    public String getRingtone()
    {
        if (soundEnabled)
        {
            return ringtone;
        } else
        {
            return null;
        }
    }

    String getSummary(Context context)
    {
        if (ringtone != null)
        {
            Object obj1 = Uri.parse(ringtone);
            Object obj = (NotificationSounds)NotificationSounds.defaultSounds.get(type);
            if (obj != null)
            {
                obj = ((NotificationSounds) (obj)).getUri();
            } else
            {
                obj = null;
            }
            if (Objects.equal(obj, obj1))
            {
                obj = "Default";
            } else
            if (ringtone.isEmpty())
            {
                obj = "Silent";
            } else
            {
                obj = RingtoneManager.getRingtone(context, ((Uri) (obj1)));
                if (obj != null)
                {
                    obj = ((Ringtone) (obj)).getTitle(context);
                } else
                {
                    obj = "No sound selected";
                }
            }
        } else
        {
            obj = "Default";
        }
        obj1 = getPreferenceValueName(context, blinkColor, 0x7f0f0012, 0x7f0f0011);
        if (notificationEnabled)
        {
            if (soundEnabled)
            {
                context = (new StringBuilder()).append("Notify").append(", play sound (").append(((String) (obj))).append(")").toString();
            } else
            {
                context = "Notify";
            }
            obj = context;
            if (blinkAction != LEDAction.None)
            {
                obj = (new StringBuilder()).append(context).append(", blink ");
                if (!Strings.isNullOrEmpty(((String) (obj1))))
                {
                    context = (new StringBuilder()).append(((String) (obj1)).toLowerCase()).append(" ").toString();
                } else
                {
                    context = "";
                }
                obj = ((StringBuilder) (obj)).append(context).append("LED").toString();
            }
            return ((String) (obj));
        } else
        {
            return "Do nothing";
        }
    }

    public boolean isEnabled()
    {
        return notificationEnabled;
    }
}
