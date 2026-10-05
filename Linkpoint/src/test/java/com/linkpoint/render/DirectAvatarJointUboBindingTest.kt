package com.linkpoint.render

import com.linkpoint.assets.MeshData
import com.linkpoint.assets.MeshFace
import com.linkpoint.assets.SkinData
import com.linkpoint.avatar.AvatarSkeleton
import com.linkpoint.render.lumiya.drawable.DrawableAvatarStore
import com.linkpoint.render.lumiya.drawable.DrawableMeshStore
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test
import java.util.UUID

class DirectAvatarJointUboBindingTest {

    @Test
    fun testStandardBoneNameLookup() {
        val pelvisIdx = AvatarSkeleton.BONE_NAMES.indexOf("mPelvis")
        val torsoIdx = AvatarSkeleton.BONE_NAMES.indexOf("mTorso")
        val headIdx = AvatarSkeleton.BONE_NAMES.indexOf("mHead")
        val wristLeftIdx = AvatarSkeleton.BONE_NAMES.indexOf("mWristLeft")

        assertEquals(0, pelvisIdx)
        assertEquals(1, torsoIdx)
        assertEquals(4, headIdx)
        assertEquals(9, wristLeftIdx)
    }

    @Test
    fun testMeshInstanceHostAvatarId() {
        val meshId = UUID.randomUUID()
        val avatarId = UUID.randomUUID()

        val instance = DrawableMeshStore.MeshInstance(
            id = 1001L,
            meshId = meshId,
            hostAvatarId = avatarId
        )

        assertEquals(1001L, instance.id)
        assertEquals(meshId, instance.meshId)
        assertEquals(avatarId, instance.hostAvatarId)
    }

    @Test
    fun testDrawableAvatarStoreGetAvatar() {
        val store = DrawableAvatarStore()
        val avatarId = UUID.randomUUID()

        assertNull(store.getAvatar(avatarId))

        store.addAvatar(avatarId, 10f, 20f, 30f)

        val avatar = store.getAvatar(avatarId)
        assertNotNull(avatar)
        assertEquals(avatarId, avatar?.id)
        assertEquals(0, avatar?.jointUBO) // Joint UBO initialized to 0 prior to updateJoints
    }

    @Test
    fun testSkinDataJointMappingLogic() {
        // Verify mapping of local joint indices to global avatar skeleton bone indices
        val jointNames = listOf("mPelvis", "mHead", "custom_unknown_bone")
        val skinData = SkinData(
            jointNames = jointNames,
            bindShapeMatrix = FloatArray(16) { if (it % 5 == 0) 1f else 0f }, // Identity 4x4
            inverseBindMatrices = emptyList()
        )

        val boneMap = AvatarSkeleton.BONE_NAMES.withIndex().associate { it.value to it.index }

        // Local index 0 ("mPelvis") -> Global index 0
        assertEquals(0, boneMap["mPelvis"])
        // Local index 1 ("mHead") -> Global index 4
        assertEquals(4, boneMap["mHead"])
        // Local index 2 ("custom_unknown_bone") -> Unmapped (null)
        assertNull(boneMap["custom_unknown_bone"])
    }

    @Test
    fun testBindShapeMatrixTransformation() {
        // Test translation along X axis (+5) via bind shape matrix
        val bindShape = FloatArray(16) { if (it % 5 == 0) 1f else 0f }
        bindShape[12] = 5.0f // TX = 5.0

        val origX = 1.0f
        val origY = 2.0f
        val origZ = 3.0f

        val transX = bindShape[0] * origX + bindShape[4] * origY + bindShape[8] * origZ + bindShape[12]
        val transY = bindShape[1] * origX + bindShape[5] * origY + bindShape[9] * origZ + bindShape[13]
        val transZ = bindShape[2] * origX + bindShape[6] * origY + bindShape[10] * origZ + bindShape[14]

        assertEquals(6.0f, transX, 1e-4f)
        assertEquals(2.0f, transY, 1e-4f)
        assertEquals(3.0f, transZ, 1e-4f)
    }
}
