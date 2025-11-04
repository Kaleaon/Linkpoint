package com.lumiyaviewer.lumiya.ui.inventory;

import android.graphics.Bitmap;
import java.util.UUID;

/* loaded from: classes.dex */
public class UploadImageParams {
    public final UUID agentUUID;
    public final Bitmap bitmap;
    public final UUID folderID;
    public final String name;

    public UploadImageParams(String str, Bitmap bitmap, UUID uuid, UUID uuid2) {
        this.name = str;
        this.bitmap = bitmap;
        this.agentUUID = uuid;
        this.folderID = uuid2;
    }
}
