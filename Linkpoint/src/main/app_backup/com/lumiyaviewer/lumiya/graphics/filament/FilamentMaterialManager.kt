package com.lumiyaviewer.lumiya.graphics.filament

import android.content.Context
import android.util.Log
import com.google.android.filament.Engine
import com.google.android.filament.Material
import java.nio.ByteBuffer
import java.nio.channels.Channels
import java.util.concurrent.ConcurrentHashMap

class FilamentMaterialManager(
    private val context: Context,
    private val engine: Engine
) {
    companion object {
        private const val TAG = "FilamentMaterialMgr"
        private const val MATERIALS_PATH = "materials"
    }
    
    enum class MaterialType {
        UNLIT_COLOR,
        TERRAIN,
        PRIM_BASIC,
        PRIM_PBR,
        AVATAR_SKIN,
        WATER,
        SKY,
        TRANSPARENT
    }
    
    private val materialCache = ConcurrentHashMap<MaterialType, Material>()
    
    fun preloadMaterials() {
        Log.i(TAG, "Preloading materials...")
        loadMaterial(MaterialType.UNLIT_COLOR)
        loadMaterial(MaterialType.TERRAIN)
        loadMaterial(MaterialType.PRIM_BASIC)
        loadMaterial(MaterialType.PRIM_PBR)
        Log.i(TAG, "Preloaded ${materialCache.size} materials")
    }
    
    fun getMaterial(type: MaterialType): Material {
        return materialCache.getOrPut(type) {
            loadMaterial(type)
        }
    }
    
    private fun loadMaterial(type: MaterialType): Material {
        val filamatName = getFilamatName(type)
        try {
            val material = loadPrecompiledMaterial(filamatName)
            if (material != null) {
                Log.i(TAG, "Loaded precompiled material: $filamatName")
                return material
            }
        } catch (e: Exception) {
            Log.w(TAG, "Failed to load precompiled material $filamatName: ${e.message}")
        }
        // Runtime compilation removed due to dependency issues
        // Returning a default or throwing runtime exception
        throw RuntimeException("Material $filamatName could not be loaded and runtime compilation is disabled.")
    }
    
    private fun loadPrecompiledMaterial(filename: String): Material? {
        return try {
            val path = "$MATERIALS_PATH/$filename"
            val materialData = context.assets.open(path).use { input ->
                val buffer = ByteBuffer.allocateDirect(input.available())
                val channel = Channels.newChannel(input)
                channel.read(buffer)
                buffer.flip()
                buffer
            }
            Material.Builder()
                .payload(materialData, materialData.remaining())
                .build(engine)
        } catch (e: Exception) {
            Log.w(TAG, "Could not load $filename: ${e.message}")
            null
        }
    }
    
    private fun getFilamatName(type: MaterialType): String {
        return when (type) {
            MaterialType.UNLIT_COLOR -> "unlit_color.filamat"
            MaterialType.TERRAIN -> "terrain.filamat"
            MaterialType.PRIM_BASIC -> "prim_basic.filamat"
            MaterialType.PRIM_PBR -> "prim_pbr.filamat"
            MaterialType.AVATAR_SKIN -> "avatar_skin.filamat"
            MaterialType.WATER -> "water.filamat"
            MaterialType.SKY -> "sky.filamat"
            MaterialType.TRANSPARENT -> "transparent.filamat"
        }
    }
    
    fun destroy() {
        Log.i(TAG, "Destroying ${materialCache.size} materials...")
        materialCache.values.forEach { material ->
            try {
                engine.destroyMaterial(material)
            } catch (e: Exception) {
                Log.w(TAG, "Error destroying material: ${e.message}")
            }
        }
        materialCache.clear()
        Log.i(TAG, "Material manager destroyed")
    }
}
