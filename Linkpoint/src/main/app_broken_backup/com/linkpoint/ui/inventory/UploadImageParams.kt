package com.linkpoint.ui.inventory

import android.graphics.Bitmap
import java.util.UUID

data class UploadImageParams(
    val name: String,
    val bitmap: Bitmap,
    val agentUUID: UUID,
    val folderID: UUID,
)