// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.ui.inventory;

import android.graphics.Bitmap;
import java.util.UUID;

public class UploadImageParams
{
    public final UUID agentUUID;
    public final Bitmap bitmap;
    public final UUID folderID;
    public final String name;
    
    public UploadImageParams(final String name, final Bitmap bitmap, final UUID agentUUID, final UUID folderID) {
        this.name = name;
        this.bitmap = bitmap;
        this.agentUUID = agentUUID;
        this.folderID = folderID;
    }
}
