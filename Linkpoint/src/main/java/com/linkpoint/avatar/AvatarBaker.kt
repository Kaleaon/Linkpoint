package com.linkpoint.avatar

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.util.Log
import com.linkpoint.assets.AssetCache
import com.linkpoint.assets.AssetType
import com.linkpoint.assets.TextureManager
import com.linkpoint.network.SSLHelper
import com.linkpoint.protocol.capabilities.CapabilityManager
import com.linkpoint.protocol.llsd.*
import kotlinx.coroutines.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream
import java.util.UUID
import java.util.concurrent.TimeUnit

/**
 * Handles avatar texture baking
 * Composites multiple wearable textures into baked textures
 * 
 * Note: Uses custom SSL configuration to handle Akamai CDN hostname verification.
 * The baked texture upload capability URLs go through the CDN.
 */
class AvatarBaker(
    private val context: Context,
    private val textureManager: TextureManager,
    private val capabilityManager: CapabilityManager
) {
    companion object {
        private const val TAG = "AvatarBaker"
        
        // Bake texture sizes
        const val BAKE_WIDTH = 512
        const val BAKE_HEIGHT = 512
        
        // Classic bake channels (avatar layers)
        const val BAKE_HEAD = 0
        const val BAKE_UPPER = 1
        const val BAKE_LOWER = 2
        const val BAKE_EYES = 3
        const val BAKE_SKIRT = 4
        const val BAKE_HAIR = 5
        
        // Bakes on Mesh (BoM) channels for mesh bodies/heads
        const val BAKE_LEFTARM = 6
        const val BAKE_LEFTLEG = 7
        const val BAKE_AUX1 = 8     // Often used for mesh head
        const val BAKE_AUX2 = 9     // Often used for mesh upper body
        const val BAKE_AUX3 = 10    // Often used for mesh lower body
        
        const val NUM_BAKE_CHANNELS = 11
        
        // Texture indices in wearables
        const val TEX_HEAD_BODYPAINT = 0
        const val TEX_UPPER_SHIRT = 1
        const val TEX_LOWER_PANTS = 2
        // ... etc
    }
    
    // HTTP client configured for CDN access with custom hostname verification
    // Baked texture uploads go through capability URLs which may use CDN
    private val httpClient = SSLHelper.configureForCdn(
        OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
    ).build()
    
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    
    // Current baked textures
    private val bakedTextures = mutableMapOf<Int, UUID>()
    
    // Wearable data
    private val wearables = mutableMapOf<WearableType, WearableData>()
    
    /**
     * Set wearable data
     */
    fun setWearable(type: WearableType, data: WearableData) {
        wearables[type] = data
    }
    
    /**
     * Remove wearable
     */
    fun removeWearable(type: WearableType) {
        wearables.remove(type)
    }
    
    /**
     * Bake all textures (classic + BoM)
     */
    suspend fun bakeAll(includeBoM: Boolean = true): Map<Int, UUID> = withContext(Dispatchers.Default) {
        val results = mutableMapOf<Int, UUID>()
        
        // Bake classic channels
        val classicJobs = listOf(
            async { bakeChannel(BAKE_HEAD) },
            async { bakeChannel(BAKE_UPPER) },
            async { bakeChannel(BAKE_LOWER) },
            async { bakeChannel(BAKE_EYES) },
            async { bakeChannel(BAKE_HAIR) }
        )
        
        classicJobs.forEachIndexed { index, job ->
            val textureId = job.await()
            if (textureId != null) {
                results[index] = textureId
            }
        }
        
        // Bake BoM channels if requested
        if (includeBoM) {
            val bomJobs = listOf(
                async { bakeChannel(BAKE_LEFTARM) },
                async { bakeChannel(BAKE_LEFTLEG) },
                async { bakeChannel(BAKE_AUX1) },
                async { bakeChannel(BAKE_AUX2) },
                async { bakeChannel(BAKE_AUX3) }
            )
            
            bomJobs.forEachIndexed { index, job ->
                val textureId = job.await()
                if (textureId != null) {
                    results[index + 6] = textureId  // Offset by 6 for BoM channels
                }
            }
        }
        
        bakedTextures.clear()
        bakedTextures.putAll(results)
        
        results
    }
    
    /**
     * Bake a single channel
     */
    suspend fun bakeChannel(channel: Int): UUID? = withContext(Dispatchers.Default) {
        Log.d(TAG, "Baking channel: $channel")
        
        // Create base bitmap
        val bitmap = Bitmap.createBitmap(BAKE_WIDTH, BAKE_HEIGHT, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        
        // Get layers for this channel
        val layers = getLayersForChannel(channel)
        
        // Composite each layer
        for (layer in layers) {
            try {
                val texture = textureManager.getTexture(layer.textureId) ?: continue
                
                // Tint if needed
                val tinted = if (layer.tint != null) {
                    tintBitmap(texture, layer.tint)
                } else {
                    texture
                }
                
                // Set blend mode
                paint.xfermode = when (layer.blendMode) {
                    BlendMode.NORMAL -> null
                    BlendMode.MULTIPLY -> PorterDuffXfermode(PorterDuff.Mode.MULTIPLY)
                    BlendMode.ADD -> PorterDuffXfermode(PorterDuff.Mode.ADD)
                    BlendMode.MASK -> PorterDuffXfermode(PorterDuff.Mode.DST_IN)
                }
                
                canvas.drawBitmap(tinted, 0f, 0f, paint)
                paint.xfermode = null
            } catch (e: Exception) {
                Log.w(TAG, "Failed to composite layer: ${layer.textureId}", e)
            }
        }
        
        // Upload baked texture
        uploadBakedTexture(bitmap, channel)
    }
    
    private fun getLayersForChannel(channel: Int): List<BakeLayer> {
        val layers = mutableListOf<BakeLayer>()
        
        when (channel) {
            BAKE_HEAD -> {
                // Base skin
                wearables[WearableType.SKIN]?.let { skin ->
                    skin.textures[TEX_HEAD_BODYPAINT]?.let { texId ->
                        layers.add(BakeLayer(texId, null, BlendMode.NORMAL))
                    }
                }
                // Tattoos
                wearables[WearableType.TATTOO]?.let { tattoo ->
                    tattoo.textures[TEX_HEAD_BODYPAINT]?.let { texId ->
                        layers.add(BakeLayer(texId, null, BlendMode.MULTIPLY))
                    }
                }
            }
            BAKE_UPPER -> {
                // Base skin
                wearables[WearableType.SKIN]?.let { skin ->
                    skin.textures[TEX_UPPER_SHIRT]?.let { texId ->
                        layers.add(BakeLayer(texId, null, BlendMode.NORMAL))
                    }
                }
                // Undershirt
                wearables[WearableType.UNDERSHIRT]?.let { under ->
                    under.textures[TEX_UPPER_SHIRT]?.let { texId ->
                        layers.add(BakeLayer(texId, under.tintColor, BlendMode.NORMAL))
                    }
                }
                // Shirt
                wearables[WearableType.SHIRT]?.let { shirt ->
                    shirt.textures[TEX_UPPER_SHIRT]?.let { texId ->
                        layers.add(BakeLayer(texId, shirt.tintColor, BlendMode.NORMAL))
                    }
                }
                // Jacket
                wearables[WearableType.JACKET]?.let { jacket ->
                    jacket.textures[TEX_UPPER_SHIRT]?.let { texId ->
                        layers.add(BakeLayer(texId, jacket.tintColor, BlendMode.NORMAL))
                    }
                }
            }
            BAKE_LOWER -> {
                // Base skin
                wearables[WearableType.SKIN]?.let { skin ->
                    skin.textures[TEX_LOWER_PANTS]?.let { texId ->
                        layers.add(BakeLayer(texId, null, BlendMode.NORMAL))
                    }
                }
                // Underpants
                wearables[WearableType.UNDERPANTS]?.let { under ->
                    under.textures[TEX_LOWER_PANTS]?.let { texId ->
                        layers.add(BakeLayer(texId, under.tintColor, BlendMode.NORMAL))
                    }
                }
                // Pants
                wearables[WearableType.PANTS]?.let { pants ->
                    pants.textures[TEX_LOWER_PANTS]?.let { texId ->
                        layers.add(BakeLayer(texId, pants.tintColor, BlendMode.NORMAL))
                    }
                }
            }
            BAKE_EYES -> {
                wearables[WearableType.EYES]?.let { eyes ->
                    eyes.textures[0]?.let { texId ->
                        layers.add(BakeLayer(texId, eyes.tintColor, BlendMode.NORMAL))
                    }
                }
            }
            BAKE_HAIR -> {
                wearables[WearableType.HAIR]?.let { hair ->
                    hair.textures[0]?.let { texId ->
                        layers.add(BakeLayer(texId, hair.tintColor, BlendMode.NORMAL))
                    }
                }
            }
            // Bakes on Mesh (BoM) channels
            BAKE_LEFTARM, BAKE_LEFTLEG -> {
                // BoM uses same skin textures as upper/lower but for mesh bodies
                wearables[WearableType.SKIN]?.let { skin ->
                    val texIndex = if (channel == BAKE_LEFTARM) TEX_UPPER_SHIRT else TEX_LOWER_PANTS
                    skin.textures[texIndex]?.let { texId ->
                        layers.add(BakeLayer(texId, null, BlendMode.NORMAL))
                    }
                }
                // Add clothing layers
                wearables[WearableType.TATTOO]?.let { tattoo ->
                    val texIndex = if (channel == BAKE_LEFTARM) TEX_UPPER_SHIRT else TEX_LOWER_PANTS
                    tattoo.textures[texIndex]?.let { texId ->
                        layers.add(BakeLayer(texId, null, BlendMode.MULTIPLY))
                    }
                }
            }
            BAKE_AUX1 -> {
                // Mesh head - use head textures
                wearables[WearableType.SKIN]?.let { skin ->
                    skin.textures[TEX_HEAD_BODYPAINT]?.let { texId ->
                        layers.add(BakeLayer(texId, null, BlendMode.NORMAL))
                    }
                }
                wearables[WearableType.TATTOO]?.let { tattoo ->
                    tattoo.textures[TEX_HEAD_BODYPAINT]?.let { texId ->
                        layers.add(BakeLayer(texId, null, BlendMode.MULTIPLY))
                    }
                }
            }
            BAKE_AUX2 -> {
                // Mesh upper body - use upper textures
                wearables[WearableType.SKIN]?.let { skin ->
                    skin.textures[TEX_UPPER_SHIRT]?.let { texId ->
                        layers.add(BakeLayer(texId, null, BlendMode.NORMAL))
                    }
                }
                wearables[WearableType.SHIRT]?.let { shirt ->
                    shirt.textures[TEX_UPPER_SHIRT]?.let { texId ->
                        layers.add(BakeLayer(texId, shirt.tintColor, BlendMode.NORMAL))
                    }
                }
            }
            BAKE_AUX3 -> {
                // Mesh lower body - use lower textures
                wearables[WearableType.SKIN]?.let { skin ->
                    skin.textures[TEX_LOWER_PANTS]?.let { texId ->
                        layers.add(BakeLayer(texId, null, BlendMode.NORMAL))
                    }
                }
                wearables[WearableType.PANTS]?.let { pants ->
                    pants.textures[TEX_LOWER_PANTS]?.let { texId ->
                        layers.add(BakeLayer(texId, pants.tintColor, BlendMode.NORMAL))
                    }
                }
            }
        }
        
        return layers
    }
    
    private fun tintBitmap(source: Bitmap, tint: IntArray): Bitmap {
        val result = source.copy(Bitmap.Config.ARGB_8888, true)
        
        val pixels = IntArray(result.width * result.height)
        result.getPixels(pixels, 0, result.width, 0, 0, result.width, result.height)
        
        for (i in pixels.indices) {
            val pixel = pixels[i]
            val a = (pixel shr 24) and 0xFF
            val r = ((pixel shr 16) and 0xFF) * tint[0] / 255
            val g = ((pixel shr 8) and 0xFF) * tint[1] / 255
            val b = (pixel and 0xFF) * tint[2] / 255
            pixels[i] = (a shl 24) or (r shl 16) or (g shl 8) or b
        }
        
        result.setPixels(pixels, 0, result.width, 0, 0, result.width, result.height)
        return result
    }
    
    private suspend fun uploadBakedTexture(bitmap: Bitmap, channel: Int): UUID? {
        val capUrl = capabilityManager.getCapability(CapabilityManager.CAP_UPLOAD_BAKED_TEXTURE)
            ?: return null
        
        return withContext(Dispatchers.IO) {
            try {
                // Compress to JPEG2000 (or use PNG fallback)
                val outputStream = ByteArrayOutputStream()
                // Try the native JPEG2000 encoder first; fall back to PNG
                // (with a different MIME type) if the native library
                // failed to load or encoding fails. The PNG fallback won't
                // actually be accepted by SL simulators, but it lets the
                // upload path log a real HTTP response instead of silently
                // doing nothing on the device.
                val j2kBytes = com.linkpoint.assets.JPEG2000Encoder.encode(bitmap, lossless = false)
                val data: ByteArray
                val mimeType: String
                if (j2kBytes != null) {
                    data = j2kBytes
                    mimeType = "image/x-j2c"
                } else {
                    Log.w(TAG, "J2K encoder unavailable; uploading PNG (sim may reject)")
                    bitmap.compress(Bitmap.CompressFormat.PNG, 90, outputStream)
                    data = outputStream.toByteArray()
                    mimeType = "image/png"
                }
                val request = Request.Builder()
                    .url(capUrl)
                    .post(data.toRequestBody(mimeType.toMediaType()))
                    .build()
                
                val response = httpClient.newCall(request).execute()
                val body = response.body?.string() ?: return@withContext null
                
                // Parse response for texture UUID
                val llsd = LLSDParser.parseXML(body)
                if (llsd is LLSDMap) {
                    val uuidStr = llsd.getString("new_asset")
                    if (uuidStr != null) {
                        Log.i(TAG, "Uploaded baked texture: $uuidStr (channel $channel)")
                        return@withContext UUID.fromString(uuidStr)
                    }
                }
                
                null
            } catch (e: Exception) {
                Log.e(TAG, "Failed to upload baked texture", e)
                null
            }
        }
    }
    
    /**
     * Get currently baked textures
     */
    fun getBakedTextures(): Map<Int, UUID> = bakedTextures.toMap()

    /**
     * Adopt baked-texture UUIDs that were composed elsewhere — used for
     * remote avatars whose AvatarAppearance message tells us their bake
     * UUIDs but we don't own the source layers. These UUIDs aren't
     * uploaded; they're fed straight to the BoM resolver so mesh
     * attachments referencing IMG_USE_BAKED_* substitute correctly.
     */
    fun setExternalBakedTextures(bakes: Map<Int, UUID>) {
        bakedTextures.clear()
        bakedTextures.putAll(bakes)
    }
    
    fun shutdown() {
        scope.cancel()
    }
}

enum class WearableType(val value: Int) {
    SHAPE(0),
    SKIN(1),
    HAIR(2),
    EYES(3),
    SHIRT(4),
    PANTS(5),
    SHOES(6),
    SOCKS(7),
    JACKET(8),
    GLOVES(9),
    UNDERSHIRT(10),
    UNDERPANTS(11),
    SKIRT(12),
    ALPHA(13),
    TATTOO(14),
    PHYSICS(15);
    
    companion object {
        fun fromValue(value: Int) = values().find { it.value == value } ?: SHAPE
    }
}

data class WearableData(
    val type: WearableType,
    val assetId: UUID,
    val textures: Map<Int, UUID>,
    val params: Map<Int, Float>,
    val tintColor: IntArray? = null
)

data class BakeLayer(
    val textureId: UUID,
    val tint: IntArray?,
    val blendMode: BlendMode
)

enum class BlendMode {
    NORMAL, MULTIPLY, ADD, MASK
}
