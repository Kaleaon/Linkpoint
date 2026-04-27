// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.cloud.common;


// Referenced classes of package com.lumiyaviewer.lumiya.cloud.common:
//            LogSyncStatus

public static final class  extends Enum
{

    private static final GoogleDriveError $VALUES[];
    public static final GoogleDriveError AppVersionRejected;
    public static final GoogleDriveError GoogleDriveError;
    public static final GoogleDriveError Ready;

    public static  valueOf(String s)
    {
        return ()Enum.valueOf(com/lumiyaviewer/lumiya/cloud/common/LogSyncStatus$Status, s);
    }

    public static [] values()
    {
        return ([])$VALUES.clone();
    }

    static 
    {
        Ready = new <init>("Ready", 0);
        AppVersionRejected = new <init>("AppVersionRejected", 1);
        GoogleDriveError = new <init>("GoogleDriveError", 2);
        $VALUES = (new .VALUES[] {
            Ready, AppVersionRejected, GoogleDriveError
        });
    }

    private (String s, int i)
    {
        super(s, i);
    }
}
