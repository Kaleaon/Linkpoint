package com.lumiyaviewer.lumiya.slproto.baker

import com.lumiyaviewer.lumiya.Debug
import com.lumiyaviewer.lumiya.LumiyaApp
import com.lumiyaviewer.lumiya.openjpeg.OpenJPEG
import com.lumiyaviewer.lumiya.slproto.avatar.AvatarTextureFaceIndex
import com.lumiyaviewer.lumiya.slproto.avatar.SLAvatarParamColor
import com.lumiyaviewer.lumiya.slproto.avatar.SLAvatarParams
import java.io.InputStream
import java.util.List

class BakeLayer(
    val layerName: String,
    val globalColor: SLAvatarGlobalColor?,
    val hasFixedColor: Boolean,
    val fixedColor: Int,
    val isRenderPassBump: Boolean,
    val visibilityMask: Boolean,
    val writeAllChannels: Boolean,
    val localTexture: AvatarTextureFaceIndex?,
    val localTextureAlphaOnly: Boolean,
    val tgaTexture: String?,
    val tgaFileIsMask: Boolean,
    val paramIDs: IntArray?
) {
    // Helper property for color operation switches, replaced with direct when logic
    
    private fun getColorByParamList(bakeProcess: BakeProcess, paramIDs: IntArray, defaultColor: Int, fallbackColor: Int): Int {
        var currentColor = defaultColor
        var hasParam = false
        
        if (layerName == "lipstick") {
            Debug.Log("Baking: lipstick start color %08x default %08x".format(defaultColor, fallbackColor))
        }
        
        for (id in paramIDs) {
            val paramSet = SLAvatarParams.paramByIDs[id]
            if (paramSet != null && paramSet.params.isNotEmpty()) {
                val avatarParam = paramSet.params[0]
                val sLAvatarParamColor = avatarParam.paramColor
                if (sLAvatarParamColor != null) {
                    hasParam = true
                    val paramWeight = bakeProcess.getParamWeight(id, avatarParam)
                    val color = sLAvatarParamColor.getColor(paramWeight)
                    
                    if (layerName == "lipstick") {
                        Debug.Log("Baking: lipstick color param weight %f color %08x".format(paramWeight, color))
                    }
                    
                    currentColor = when (sLAvatarParamColor.colorOperation) {
                        SLAvatarParamColor.ColorOperation.Blend -> SLAvatarParamColor.colorLerp(currentColor, color, paramWeight)
                        SLAvatarParamColor.ColorOperation.Default -> SLAvatarParamColor.colorAdd(currentColor, color)
                        SLAvatarParamColor.ColorOperation.Multiply -> SLAvatarParamColor.colorMult(currentColor, color)
                        else -> currentColor
                    }
                    
                    if (layerName == "lipstick") {
                        Debug.Log("Baking: after op, lipstick color result %08x".format(currentColor))
                    }
                }
            }
        }
        
        return if (!hasParam) fallbackColor else currentColor
    }

    private fun getNetColor(bakeProcess: BakeProcess): Int {
        val pIDs = paramIDs ?: return if (hasFixedColor) fixedColor else -1
        var hasColorParam = false
        
        for (id in pIDs) {
            val paramSet = SLAvatarParams.paramByIDs[id]
            if (paramSet != null && paramSet.params.isNotEmpty() && paramSet.params[0].paramColor != null) {
                hasColorParam = true
                break
            }
        }
        
        if (hasColorParam) {
            val colorByParamList = if (globalColor != null) {
                getColorByParamList(bakeProcess, globalColor.getParamIDs(), 0, 0)
            } else if (hasFixedColor) {
                fixedColor
            } else {
                0
            }
            return getColorByParamList(bakeProcess, pIDs, colorByParamList, colorByParamList)
        } else if (globalColor != null) {
            return getColorByParamList(bakeProcess, globalColor.getParamIDs(), 0, 0)
        } else if (hasFixedColor) {
            return fixedColor
        }
        return -1
    }

    fun bake(openJPEG: OpenJPEG, bakeProcess: BakeProcess) {
        val netColor = getNetColor(bakeProcess)
        
        // Initialize OpenJPEG with base colors (simple solid color or clear)
        val scratch = OpenJPEG(openJPEG.width, openJPEG.height, 4, 4, 0, 0)
        val layer = OpenJPEG(openJPEG.width, openJPEG.height, 4, 4, 0, -16777216) // ABGR black?
        
        Debug.Log("Baking: layer %s net_color 0x%08x.".format(layerName, netColor))

        val pIDs = paramIDs ?: IntArray(0)
        
        // 1. Process Params (Alpha masks)
        for (id in pIDs) {
            val paramSet = SLAvatarParams.paramByIDs[id]
            if (paramSet != null && paramSet.params.isNotEmpty()) {
                val avatarParam = paramSet.params[0]
                val paramAlpha = avatarParam.paramAlpha
                
                if (paramAlpha != null && paramAlpha.tgaFile != null) {
                    val paramWeight = bakeProcess.getParamWeight(id, avatarParam)
                    
                    if (!paramAlpha.multiplyBlend) {
                        layer.setComponent(3, 0) // Clear alpha
                    }
                    
                    if (paramWeight == 0f && paramAlpha.skipIfZero) {
                        continue
                    }
                    
                    try {
                        val assetManager = LumiyaApp.getAssetManager()
                        val inputStream = assetManager.open("tga/${paramAlpha.tgaFile}")
                        val mask = OpenJPEG(inputStream, OpenJPEG.ImageFormat.TGA, true, true, paramAlpha.domain, paramWeight, false)
                        
                        Debug.Log("Baking: layer %s: applying alpha (weight %f domain %f) mask texture %s".format(
                            layerName, paramWeight, paramAlpha.domain, paramAlpha.tgaFile
                        ))
                        
                        layer.blendAlpha(mask, !paramAlpha.multiplyBlend)
                        inputStream.close()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
        
        // 2. Apply Local Texture
        var processedTexture = false
        if (localTexture != null) {
            try {
                val localTextures = bakeProcess.getLocalTexture(localTexture)
                if (localTextures != null) {
                    for (tex in localTextures) {
                        Debug.Log("Baking: layer %s: applying local texture, writeAllChannels %s".format(layerName, writeAllChannels))
                        openJPEG.draw(tex, -1, 0) // Assuming draw takes (src, color, mode?)
                        processedTexture = true
                    }
                }
            } catch (e: Exception) {
                Debug.Log("Baking: layer %s: default local texture".format(layerName))
                processedTexture = true // Treated as processed to skip TGA fallback logic if implied
            }
        } else {
            processedTexture = false
        }
        
        // 3. Apply TGA Texture if Local didn't cover it fully? Or layered on top?
        // Original logic falls through.
        
        if (tgaTexture != null) {
            try {
                val assetManager = LumiyaApp.getAssetManager()
                val inputStream = assetManager.open("tga/$tgaTexture")
                val tex = OpenJPEG(inputStream, OpenJPEG.ImageFormat.TGA, tgaFileIsMask, false, 0f, 0f, false)
                
                Debug.Log("Baking: layer %s: applying tga texture %s, writeAllChannels %s".format(layerName, tgaTexture, writeAllChannels))
                
                openJPEG.draw(tex, -1, 0)
                processedTexture = true
                inputStream.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        // 4. Fallback color
        if (!processedTexture) {
            openJPEG.setComponent(0, -1)
            openJPEG.setComponent(1, -1)
            openJPEG.setComponent(2, -1)
            openJPEG.setComponent(3, -1)
        }

        // 5. Blend Alpha
        openJPEG.blendAlpha(layer, false)

        // 6. Bump Pass
        if (!visibilityMask) {
            if (isRenderPassBump) {
                openJPEG.drawBump(openJPEG, netColor, writeAllChannels, false) // Assuming drawBump signature
            } else {
                openJPEG.draw(openJPEG, netColor, writeAllChannels) // Assuming draw signature
            }
        }
    }

    fun bakeAlpha(openJPEG: OpenJPEG, bakeProcess: BakeProcess) {
        if (!isRenderPassBump) {
            if (tgaTexture != null) {
                try {
                    val inputStream = LumiyaApp.getAssetManager().open("tga/$tgaTexture")
                    val mask = OpenJPEG(inputStream, OpenJPEG.ImageFormat.TGA, tgaFileIsMask, false, 0.0f, 0.0f, false)
                    Debug.Log("Baking: layer %s: applying tga alpha mask %s".format(layerName, tgaTexture))
                    openJPEG.blendAlpha(mask, false)
                    inputStream.close()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            if (localTexture != null) {
                try {
                    val localTextures = bakeProcess.getLocalTexture(localTexture)
                    if (localTextures != null) {
                        for (blendAlpha in localTextures) {
                            Debug.Log("Baking: layer %s: applying local texture alpha".format(layerName))
                            openJPEG.blendAlpha(blendAlpha, false)
                        }
                    }
                } catch (e: Exception) {
                    Debug.Log("Baking: layer %s: default local texture for alpha".format(layerName))
                }
            }
        }
    }
}
