package com.linkpoint.avatar.baking

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.util.Log
import kotlinx.coroutines.*
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

/**
 * Avatar Baking System - Bakes multiple texture layers into final avatar textures.
 * 
 * Based on Lumiya's BakeProcess.java, BakeLayer.java, BakeLayers.java
 * 
 * Baking combines:
 * - Skin texture (base layer)
 * - Tattoo layers
 * - Clothing layers (undershirt, shirt, jacket, etc.)
 * - Alpha masks
 * 
 * Each body part (head, upper, lower, eyes, hair, skirt) is baked separately.
 */
class AvatarBakingSystem {
    
    companion object {
        private const val TAG = "AvatarBakingSystem"
        
        // Bake texture sizes
        const val BAKE_WIDTH = 512
        const val BAKE_HEIGHT = 512
        
        // Body parts that get baked
        const val BAKE_HEAD = 0
        const val BAKE_UPPER = 1
        const val BAKE_LOWER = 2
        const val BAKE_EYES = 3
        const val BAKE_SKIRT = 4
        const val BAKE_HAIR = 5
        const val BAKE_LEFTARM = 6
        const val BAKE_LEFTLEG = 7
        const val BAKE_AUX1 = 8
        const val BAKE_AUX2 = 9
        const val BAKE_AUX3 = 10
        
        val BAKE_NAMES = arrayOf(
            "head", "upper", "lower", "eyes", "skirt", "hair",
            "leftarm", "leftleg", "aux1", "aux2", "aux3"
        )
    }
    
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    
    // Active bake processes
    private val activeBakes = ConcurrentHashMap<UUID, BakeProcess>()
    
    // Baked texture cache
    private val bakedTextures = ConcurrentHashMap<BakeKey, Bitmap>()
    
    // Bake listeners
    private val listeners = mutableListOf<BakeListener>()
    
    /**
     * Start baking textures for an avatar.
     */
    fun startBake(
        avatarId: UUID,
        bodyPart: Int,
        layers: List<BakeLayer>,
        callback: BakeCallback? = null
    ) {
        val process = BakeProcess(
            avatarId = avatarId,
            bodyPart = bodyPart,
            layers = layers,
            width = BAKE_WIDTH,
            height = BAKE_HEIGHT
        )
        
        activeBakes[avatarId] = process
        
        scope.launch {
            try {
                val result = executeBake(process)
                
                val key = BakeKey(avatarId, bodyPart)
                bakedTextures[key] = result
                
                withContext(Dispatchers.Main) {
                    callback?.onBakeComplete(avatarId, bodyPart, result)
                    notifyBakeComplete(avatarId, bodyPart, result)
                }
                
                Log.i(TAG, "Bake complete for $avatarId part ${BAKE_NAMES[bodyPart]}")
            } catch (e: Exception) {
                Log.e(TAG, "Bake failed for $avatarId", e)
                callback?.onBakeFailed(avatarId, bodyPart, e.message ?: "Unknown error")
            } finally {
                activeBakes.remove(avatarId)
            }
        }
    }
    
    /**
     * Execute the baking process.
     */
    private suspend fun executeBake(process: BakeProcess): Bitmap = withContext(Dispatchers.Default) {
        val bitmap = Bitmap.createBitmap(process.width, process.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        
        // Sort layers by render order
        val sortedLayers = process.layers.sortedBy { it.renderOrder }
        
        for (layer in sortedLayers) {
            if (!layer.enabled) continue
            
            val layerBitmap = layer.texture ?: continue
            
            val paint = Paint().apply {
                isAntiAlias = true
                alpha = (layer.alpha * 255).toInt()
                
                // Apply blend mode
                xfermode = when (layer.blendMode) {
                    BlendMode.NORMAL -> null
                    BlendMode.MULTIPLY -> PorterDuffXfermode(PorterDuff.Mode.MULTIPLY)
                    BlendMode.ADD -> PorterDuffXfermode(PorterDuff.Mode.ADD)
                    BlendMode.MASK -> PorterDuffXfermode(PorterDuff.Mode.DST_IN)
                }
                
                // Apply tint color if specified
                if (layer.tintColor != null) {
                    colorFilter = android.graphics.PorterDuffColorFilter(
                        layer.tintColor,
                        PorterDuff.Mode.MULTIPLY
                    )
                }
            }
            
            // Draw scaled to fit
            val srcRect = android.graphics.Rect(0, 0, layerBitmap.width, layerBitmap.height)
            val dstRect = android.graphics.Rect(0, 0, process.width, process.height)
            canvas.drawBitmap(layerBitmap, srcRect, dstRect, paint)
        }
        
        bitmap
    }
    
    /**
     * Get baked texture for avatar body part.
     */
    fun getBakedTexture(avatarId: UUID, bodyPart: Int): Bitmap? {
        return bakedTextures[BakeKey(avatarId, bodyPart)]
    }
    
    /**
     * Invalidate baked textures for an avatar.
     */
    fun invalidateBake(avatarId: UUID) {
        val keysToRemove = bakedTextures.keys.filter { it.avatarId == avatarId }
        keysToRemove.forEach { bakedTextures.remove(it) }
        Log.d(TAG, "Invalidated ${keysToRemove.size} baked textures for $avatarId")
    }
    
    /**
     * Register bake listener.
     */
    fun addListener(listener: BakeListener) {
        listeners.add(listener)
    }
    
    fun removeListener(listener: BakeListener) {
        listeners.remove(listener)
    }
    
    private fun notifyBakeComplete(avatarId: UUID, bodyPart: Int, bitmap: Bitmap) {
        listeners.forEach { it.onBakeComplete(avatarId, bodyPart, bitmap) }
    }
    
    /**
     * Create a standard layer set for a body part.
     */
    fun createLayerSet(bodyPart: Int): BakeLayerSet {
        return BakeLayerSet(bodyPart)
    }
    
    fun shutdown() {
        scope.cancel()
        bakedTextures.values.forEach { it.recycle() }
        bakedTextures.clear()
    }
}

/**
 * Bake process state.
 */
data class BakeProcess(
    val avatarId: UUID,
    val bodyPart: Int,
    val layers: List<BakeLayer>,
    val width: Int,
    val height: Int
)

/**
 * A single layer in the bake stack.
 */
data class BakeLayer(
    val layerType: LayerType,
    val texture: Bitmap?,
    val textureId: UUID? = null,
    val renderOrder: Int = 0,
    val enabled: Boolean = true,
    val alpha: Float = 1f,
    val blendMode: BlendMode = BlendMode.NORMAL,
    val tintColor: Int? = null
)

/**
 * Layer types for baking.
 */
enum class LayerType(val renderOrder: Int) {
    // Base layers
    SKIN_BASE(0),
    SKIN_TONE(1),
    
    // Tattoo layers
    TATTOO_HEAD(10),
    TATTOO_UPPER(11),
    TATTOO_LOWER(12),
    
    // Clothing layers (lower body)
    UNDERWEAR_BOTTOM(20),
    SOCKS(21),
    PANTS(22),
    SHOES(23),
    
    // Clothing layers (upper body)
    UNDERWEAR_TOP(30),
    UNDERSHIRT(31),
    SHIRT(32),
    JACKET_INNER(33),
    JACKET_OUTER(34),
    GLOVES(35),
    
    // Alpha mask (last)
    ALPHA(100)
}

/**
 * Blend modes for layers.
 */
enum class BlendMode {
    NORMAL,
    MULTIPLY,
    ADD,
    MASK
}

/**
 * Key for baked texture cache.
 */
data class BakeKey(
    val avatarId: UUID,
    val bodyPart: Int
)

/**
 * Layer set builder for a body part.
 */
class BakeLayerSet(val bodyPart: Int) {
    private val layers = mutableListOf<BakeLayer>()
    
    fun addLayer(layer: BakeLayer): BakeLayerSet {
        layers.add(layer)
        return this
    }
    
    fun addSkin(texture: Bitmap, tintColor: Int? = null): BakeLayerSet {
        layers.add(BakeLayer(
            layerType = LayerType.SKIN_BASE,
            texture = texture,
            renderOrder = LayerType.SKIN_BASE.renderOrder,
            tintColor = tintColor
        ))
        return this
    }
    
    fun addTattoo(texture: Bitmap): BakeLayerSet {
        val type = when (bodyPart) {
            AvatarBakingSystem.BAKE_HEAD -> LayerType.TATTOO_HEAD
            AvatarBakingSystem.BAKE_UPPER -> LayerType.TATTOO_UPPER
            else -> LayerType.TATTOO_LOWER
        }
        layers.add(BakeLayer(
            layerType = type,
            texture = texture,
            renderOrder = type.renderOrder
        ))
        return this
    }
    
    fun addClothing(layerType: LayerType, texture: Bitmap, tintColor: Int? = null): BakeLayerSet {
        layers.add(BakeLayer(
            layerType = layerType,
            texture = texture,
            renderOrder = layerType.renderOrder,
            tintColor = tintColor
        ))
        return this
    }
    
    fun addAlphaMask(texture: Bitmap): BakeLayerSet {
        layers.add(BakeLayer(
            layerType = LayerType.ALPHA,
            texture = texture,
            renderOrder = LayerType.ALPHA.renderOrder,
            blendMode = BlendMode.MASK
        ))
        return this
    }
    
    fun build(): List<BakeLayer> = layers.toList()
}

/**
 * Bake completion callback.
 */
interface BakeCallback {
    fun onBakeComplete(avatarId: UUID, bodyPart: Int, bitmap: Bitmap)
    fun onBakeFailed(avatarId: UUID, bodyPart: Int, error: String)
}

/**
 * Bake listener for UI updates.
 */
interface BakeListener {
    fun onBakeComplete(avatarId: UUID, bodyPart: Int, bitmap: Bitmap)
}
