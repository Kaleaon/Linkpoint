package com.lumiyaviewer.lumiya.graphics.filament

import android.content.Context
import android.util.Log
import com.lumiyaviewer.lumiya.slproto.users.manager.ObjectsManager
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager
import com.lumiyaviewer.lumiya.slproto.terrain.TerrainData
import java.util.UUID

class FilamentWorldDataBridge(
    val context: Context,
    val renderContext: FilamentRenderContext,
    val materialManager: FilamentMaterialManager,
    val avatarRenderer: FilamentAvatarRenderer
) {
    fun setObjectsManager(manager: ObjectsManager) {}
    fun setUserManager(manager: UserManager) {}
    fun setTerrainData(data: TerrainData) {}
    
    fun startSync() {
        Log.d("FilamentBridge", "Start Sync")
    }
    
    fun stopSync() {
        Log.d("FilamentBridge", "Stop Sync")
    }
    
    fun destroy() {
        Log.d("FilamentBridge", "Destroy")
    }

    fun clearAll() {
        Log.d("FilamentBridge", "Clear all")
    }
    
    fun update(uuid: UUID, info: Any?) {
        Log.d("FilamentBridge", "Update $uuid")
    }
    
    fun removeAvatar(uuid: UUID) {
        Log.d("FilamentBridge", "Remove $uuid")
    }
    
    fun isValid(id: Int): Boolean {
        return id != 0
    }
}
