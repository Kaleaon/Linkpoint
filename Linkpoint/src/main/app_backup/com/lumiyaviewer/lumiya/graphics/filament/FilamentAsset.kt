package com.lumiyaviewer.lumiya.graphics.filament

import com.google.android.filament.Entity

// Stub implementation of FilamentAsset to resolve references in FilamentAvatarRenderer
data class FilamentAsset(
    val root: Int = 0,
    val entities: IntArray = IntArray(0)
) {
    val entityCount: Int get() = entities.size
}
