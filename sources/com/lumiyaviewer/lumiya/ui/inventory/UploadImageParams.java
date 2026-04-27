// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.inventory;

import android.graphics.Bitmap;
import java.util.UUID;

public class UploadImageParams
{

    public final UUID agentUUID;
    public final Bitmap bitmap;
    public final UUID folderID;
    public final String name;

    public UploadImageParams(String s, Bitmap bitmap1, UUID uuid, UUID uuid1)
    {
        name = s;
        bitmap = bitmap1;
        agentUUID = uuid;
        folderID = uuid1;
    }
}
