package com.lumiyaviewer.lumiya.ui.inventory;

import android.graphics.Bitmap;
import java.util.UUID;

public class UploadImageParams {
    public final UUID agentUUID;
    public final Bitmap bitmap;
    public final UUID folderID;
    public final String name;

    public UploadImageParams(String str, Bitmap bitmap2, UUID uuid, UUID uuid2) {
        this.name = str;
        this.bitmap = bitmap2;
        this.agentUUID = uuid;
        this.folderID = uuid2;
    }
}
