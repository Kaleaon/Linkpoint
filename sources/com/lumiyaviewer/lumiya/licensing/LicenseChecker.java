// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.licensing;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

public class LicenseChecker
{

    public static final String APP_STORE_NAME = "Google Play";
    public static final String APP_STORE_URL = "https://play.google.com/store/apps/details?id=com.lumiyaviewer.lumiya";
    public static final String CLOUD_PLUGIN_URL = "https://play.google.com/store/apps/details?id=com.lumiyaviewer.lumiya.cloud";
    public static final int MSG_LICENSING_ALLOW = 0x7f100019;
    public static final int MSG_LICENSING_APP_ERROR = 0x7f10001a;
    public static final int MSG_LICENSING_DONT_ALLOW = 0x7f10001b;
    public static final String VOICE_PLUGIN_URL = "https://play.google.com/store/apps/details?id=com.lumiyaviewer.lumiya.voice";

    public LicenseChecker(Context context, Handler handler, Object obj)
    {
        handler.obtainMessage(0x7f100019, obj).sendToTarget();
    }
}
