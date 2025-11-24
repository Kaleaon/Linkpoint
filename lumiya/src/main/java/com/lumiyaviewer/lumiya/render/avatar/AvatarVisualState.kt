package com.lumiyaviewer.lumiya.render.avatar

import com.google.common.collect.ImmutableSet
import com.lumiyaviewer.lumiya.Debug
import com.lumiyaviewer.lumiya.render.DrawableStore
import com.lumiyaviewer.lumiya.render.spatial.SpatialIndex
import com.lumiyaviewer.lumiya.slproto.messages.AvatarAnimation
import com.lumiyaviewer.lumiya.slproto.messages.AvatarAppearance
import com.lumiyaviewer.lumiya.slproto.objects.SLObjectAvatarInfo
import com.lumiyaviewer.lumiya.slproto.textures.SLTextureEntry
import java.util.Collections
import java.util.HashSet
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

class AvatarVisualState(
    private val agentUUID: UUID,
    private val avatarObject: SLObjectAvatarInfo,
    private val avatarUUID: UUID
) {
    private val basicAnimations: ImmutableSet<UUID> = ImmutableSet.Builder<UUID>()
        .add(UUID.fromString("2408fe9e-df1d-1d7d-f4ff-1384fa7b350f"))
        .add(UUID.fromString("15468e00-3400-bb66-cecc-646d7c14458e"))
        .add(UUID.fromString("370f3a20-6ca6-9971-848c-9a01bc42ae3c"))
        .add(UUID.fromString("42b46214-4b44-79ae-deb8-0df61424ff4b"))
        .add(UUID.fromString("f22fed8b-a5ed-2c93-64d5-bdd8b93c889f"))
        .add(UUID.fromString("201f3fdf-cb1f-dbec-201f-7333e328ae7c"))
        .add(UUID.fromString("47f5f6fb-22e5-ae44-f871-73aaaf4a6022"))
        .add(UUID.fromString("aec4610c-757f-bc4e-c092-c6e9caf18daf"))
        .add(UUID.fromString("2b5a38b2-5e00-3a97-a495-4c826bc443e6"))
        .add(UUID.fromString("4ae8016b-31b9-03bb-c401-b1ea941db41d"))
        .add(UUID.fromString("20f063ea-8306-2562-0b07-5c853b37b31e"))
        .add(UUID.fromString("62c5de58-cb33-5743-3d07-9e4cd4352864"))
        .add(UUID.fromString("05ddbff8-aaa9-92a1-2b74-8fe77a29b445"))
        .add(UUID.fromString("6ed24bd8-91aa-4b12-ccc7-c97c857ab4e0"))
        .add(UUID.fromString("f5fc7433-043d-e819-8298-f519a119b688"))
        .build()

    private val defaultStandingAnimation: UUID = UUID.fromString("2408fe9e-df1d-1d7d-f4ff-1384fa7b350f")
    private val animations: MutableMap<UUID, AnimationSequenceInfo> = ConcurrentHashMap()
    
    @Volatile
    private var avatarShapeParams: AvatarShapeParams? = null
    private val textures: AvatarTextures = AvatarTextures()

    private fun startAnimation(uuid: UUID, seqID: Int, startTime: Long, drawableAvatar: DrawableAvatar?) {
        var info = animations[uuid]
        var shouldUpdate = false

        if (info == null) {
            Debug.Printf("Anim: Starting animation %s seqID %d", uuid.toString(), seqID)
            info = AnimationSequenceInfo.newSequence(uuid, startTime, seqID)
            animations[uuid] = info
            shouldUpdate = true
        } else if (seqID != info.sequenceID) {
            info = AnimationSequenceInfo.restartSequence(startTime, seqID, info)
            animations[uuid] = info
            shouldUpdate = true
        }

        if (shouldUpdate && drawableAvatar != null) {
            drawableAvatar.AnimationUpdate(info)
        }
    }

    @Synchronized
    private fun updateAvatarShape() {
        val drawableAvatar = SpatialIndex.getInstance().getDrawableAvatar(avatarObject)
        drawableAvatar?.UpdateShapeParams(avatarShapeParams)
    }

    @Synchronized
    private fun updateTextures() {
        val drawableAvatar = SpatialIndex.getInstance().getDrawableAvatar(avatarObject)
        drawableAvatar?.UpdateTextures(textures)
    }

    @Synchronized
    fun ApplyAvatarAnimation(avatarAnimation: AvatarAnimation) {
        val currentTime = System.currentTimeMillis()
        val seenAnims = HashSet<UUID>()
        val existingAnims = HashSet<UUID>(animations.keys)
        
        val drawableAvatar = SpatialIndex.getInstance().getDrawableAvatar(avatarObject)

        for (anim in avatarAnimation.AnimationList_Fields) {
            val uuid = anim.AnimID
            seenAnims.add(uuid)
            existingAnims.remove(uuid)
            startAnimation(uuid, anim.AnimSequenceID, currentTime, drawableAvatar)
        }

        if (Collections.disjoint(seenAnims, basicAnimations)) {
            existingAnims.remove(defaultStandingAnimation)
            startAnimation(defaultStandingAnimation, 1, currentTime, drawableAvatar)
        }

        for (uuid in existingAnims) {
            var info = animations[uuid]
            if (info != null) {
                var updatedInfo: AnimationSequenceInfo? = null
                var isStopped = false
                
                if (info.sequenceID != 0) {
                    updatedInfo = AnimationSequenceInfo.stopSequence(currentTime, info)
                    if (updatedInfo != null) {
                        animations[uuid] = updatedInfo
                        info = updatedInfo
                    } else {
                        isStopped = true
                    }
                }

                if (info != null) {
                    if (info.hasStopped(currentTime)) isStopped = true
                }

                if (drawableAvatar != null) {
                    if (updatedInfo != null) {
                        drawableAvatar.AnimationUpdate(updatedInfo)
                    }
                    if (drawableAvatar.IsAnimationStopped(uuid)) {
                        isStopped = true
                    }
                }

                if (isStopped) {
                    Debug.Printf("Anim: Stopping animation %s", uuid.toString())
                    animations.remove(uuid)
                    drawableAvatar?.AnimationRemove(uuid)
                }
            }
        }
    }

    @Synchronized
    fun ApplyAvatarAppearance(avatarAppearance: AvatarAppearance) {
        val previousParams = avatarShapeParams
        avatarShapeParams = AvatarShapeParams.create(previousParams, avatarAppearance)
        
        if (avatarShapeParams != previousParams) {
            updateAvatarShape()
        }
        
        if (textures.ApplyAvatarAppearance(avatarAppearance)) {
            updateTextures()
        }
    }

    @Synchronized
    fun ApplyTextures(slTextureEntry: SLTextureEntry?, force: Boolean) {
        if (textures.ApplyTextures(slTextureEntry, force)) {
            updateTextures()
        }
    }

    @Synchronized
    fun ApplyVisualParams(params: IntArray) {
        val previousParams = avatarShapeParams
        avatarShapeParams = AvatarShapeParams.create(previousParams, params)
        
        if (avatarShapeParams != previousParams) {
            updateAvatarShape()
        }
    }

    @Synchronized
    fun createDrawableAvatar(store: DrawableStore?): DrawableAvatar {
        val avatar = DrawableAvatar(store, agentUUID, avatarObject, avatarUUID, animations)
        avatar.UpdateShapeParams(avatarShapeParams)
        avatar.UpdateTextures(textures)
        return avatar
    }

    @Synchronized
    fun createDrawableAvatarStub(store: DrawableStore?): DrawableAvatarStub {
        return DrawableAvatarStub(store, agentUUID, avatarObject)
    }

    @Synchronized
    fun getRunningAnimations(): Set<UUID> {
        return animations.keys
    }
}
