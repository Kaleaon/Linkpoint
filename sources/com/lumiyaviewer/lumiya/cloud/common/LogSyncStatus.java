// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.cloud.common;

import android.os.Bundle;

// Referenced classes of package com.lumiyaviewer.lumiya.cloud.common:
//            Bundleable

public class LogSyncStatus
    implements Bundleable
{
    public static final class Status extends Enum
    {

        private static final Status $VALUES[];
        public static final Status AppVersionRejected;
        public static final Status GoogleDriveError;
        public static final Status Ready;

        public static Status valueOf(String s)
        {
            return (Status)Enum.valueOf(com/lumiyaviewer/lumiya/cloud/common/LogSyncStatus$Status, s);
        }

        public static Status[] values()
        {
            return (Status[])$VALUES.clone();
        }

        static 
        {
            Ready = new Status("Ready", 0);
            AppVersionRejected = new Status("AppVersionRejected", 1);
            GoogleDriveError = new Status("GoogleDriveError", 2);
            $VALUES = (new Status[] {
                Ready, AppVersionRejected, GoogleDriveError
            });
        }

        private Status(String s, int i)
        {
            super(s, i);
        }
    }


    public final String errorMessage;
    public final int pluginVersionCode;
    public final Status status;

    public LogSyncStatus(int i, Status status1, String s)
    {
        pluginVersionCode = i;
        status = status1;
        errorMessage = s;
    }

    public LogSyncStatus(Bundle bundle)
    {
        pluginVersionCode = bundle.getInt("pluginVersionCode");
        status = Status.valueOf(bundle.getString("status"));
        errorMessage = bundle.getString("errorMessage");
    }

    public Bundle toBundle()
    {
        Bundle bundle = new Bundle();
        bundle.putInt("pluginVersionCode", pluginVersionCode);
        bundle.putString("status", status.toString());
        bundle.putString("errorMessage", errorMessage);
        return bundle;
    }
}
