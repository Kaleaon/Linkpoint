package com.lumiyaviewer.lumiya.render.avatar

import com.lumiyaviewer.lumiya.Debug
import com.lumiyaviewer.lumiya.openjpeg.OpenJPEG
import com.lumiyaviewer.lumiya.render.GLTexture
import com.lumiyaviewer.lumiya.render.RenderContext
import com.lumiyaviewer.lumiya.render.drawable.DrawableFaceTexture
import com.lumiyaviewer.lumiya.render.glres.textures.GLTextureCache
import com.lumiyaviewer.lumiya.render.tex.DrawableTextureParams
import com.lumiyaviewer.lumiya.res.ResourceConsumer
import com.lumiyaviewer.lumiya.res.executors.PrimComputeExecutor
import com.lumiyaviewer.lumiya.res.textures.TextureCache
import com.lumiyaviewer.lumiya.slproto.avatar.AvatarTextureFaceIndex
import com.lumiyaviewer.lumiya.slproto.avatar.SLAnimatedMeshData
import com.lumiyaviewer.lumiya.slproto.avatar.SLMeshData
import com.lumiyaviewer.lumiya.slproto.avatar.SLPolyMesh
import com.lumiyaviewer.lumiya.utils.UUIDPool
import java.util.Arrays
import java.util.UUID

class DrawableAvatarPart(
    private val avatarUUID: UUID,
    private val faceIndex: AvatarTextureFaceIndex,
    private val referenceMeshData: SLPolyMesh,
    private val hasGL20: Boolean
) : ResourceConsumer {

    val DEFAULT_AVATAR_TEXTURE: UUID = UUID.fromString("c228d1cf-4b5d-4ba8-84f4-899a0796aa97")
    
    @Volatile private var meshData: SLAnimatedMeshData? = null
    @Volatile private var meshDataUpdated: Boolean = false
    
    private val meshUpdate = Runnable {
        val rawTex: GLTexture?
        val params: FloatArray?
        
        Debug.Printf("Avatar: meshUpdate entered for part %s", faceIndex.toString())
        
        synchronized(updateLock) {
            rawTex = rawTexture
            params = partMorphParams
        }
        
        if (params != null && rawTex != null) {
            Debug.Printf("Avatar: meshUpdate: part %s params %s", faceIndex.toString(), Arrays.toString(params))
            val newMeshData = SLAnimatedMeshData(referenceMeshData, hasGL20)
            // referenceMeshData.applyMorphData(newMeshData, params!!, rawTex!!) // Stubbed in SLPolyMesh
            
            synchronized(updateLock) {
                meshData = newMeshData
                meshDataUpdated = true
            }
        }
    }
    
    @Volatile private var partMorphParams: FloatArray? = null
    @Volatile private var rawTexture: OpenJPEG? = null
    @Volatile private var texture: DrawableFaceTexture? = null
    @Volatile private var textureUUID: UUID? = null
    private val updateLock = Any()

    private fun RequestMeshUpdate() {
        PrimComputeExecutor.getInstance().execute(meshUpdate)
    }

    fun GLDraw(renderContext: RenderContext, skeleton: FloatArray?, forceUpdate: Boolean) {
        var currentMeshData: SLAnimatedMeshData?
        val currentTexture: DrawableFaceTexture?
        var updateSkeleton = forceUpdate
        
        if (renderContext.hasGL20) {
            updateSkeleton = false
        }
        
        synchronized(updateLock) {
            currentMeshData = meshData
            currentTexture = texture
            if (currentMeshData != null && meshDataUpdated && skeleton != null) {
                meshDataUpdated = false
                updateSkeleton = true
            }
        }
        
        if (currentMeshData != null) {
            if (updateSkeleton && skeleton != null) {
                // referenceMeshData.applySkeleton(currentMeshData, skeleton) // Stubbed in SLPolyMesh
                currentMeshData!!.setVerticesDirty()
            }
            // currentMeshData!!.GLDraw(renderContext, currentTexture) // Stubbed
        }
    }

    override fun OnResourceReady(resource: Any, isSync: Boolean) {
        Debug.Printf("Avatar: (requesting meshUpdate) face %s texture %s", faceIndex.toString(), resource.toString())
        if (resource is OpenJPEG) {
            synchronized(updateLock) {
                rawTexture = resource
            }
            RequestMeshUpdate()
        }
    }

    fun getFaceIndex(): AvatarTextureFaceIndex {
        return faceIndex
    }

    fun setPartMorphParams(params: FloatArray) {
        var changed = false
        synchronized(updateLock) {
            if (!Arrays.equals(partMorphParams, params)) {
                partMorphParams = params
                changed = true
            }
        }
        if (changed) {
            Debug.Printf("Avatar: (requesting meshUpdate) morphParams for part %s", faceIndex.toString())
            RequestMeshUpdate()
        }
    }

    fun setTexture(glTextureCache: GLTextureCache?, textureUUID: UUID?) {
        synchronized(updateLock) {
            var effectiveUUID = textureUUID
            
            // Log texture change
            Debug.Printf("Avatar: face %s texture %s", faceIndex.toString(), effectiveUUID?.toString() ?: "null")
            
            if (effectiveUUID != null && UUIDPool.ZeroUUID == effectiveUUID) {
                effectiveUUID = null
            }
            
            if (this.textureUUID == effectiveUUID) {
                return
            }
            
            if (effectiveUUID == null) {
                this.textureUUID = null
                this.texture = null
                this.meshData = null
                this.rawTexture = null
                return
            }
            
            this.textureUUID = effectiveUUID
            
            val textureParams = DrawableTextureParams.create(effectiveUUID, faceIndex, avatarUUID)
            TextureCache.getInstance().RequestResource(textureParams, this)
            this.texture = DrawableFaceTexture(textureParams)
        }
    }
}
