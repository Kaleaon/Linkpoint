package com.linkpoint.ui.inventory

import android.graphics.Bitmap
import java.util.UUID

class UploadImageParams {
    val UUID agentUUID
    val Bitmap bitmap
    val UUID folderID
    val String name

    public UploadImageParams(String str, Bitmap bitmap2, UUID uuid, UUID uuid2) {
        this.name = str
        this.bitmap = bitmap2
        this.agentUUID = uuid
        this.folderID = uuid2
    }
}
