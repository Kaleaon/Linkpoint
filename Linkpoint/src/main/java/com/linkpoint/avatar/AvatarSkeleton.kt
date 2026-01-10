package com.linkpoint.avatar

import android.content.Context
import android.util.Log
import com.linkpoint.protocol.types.LLQuaternion
import com.linkpoint.protocol.types.LLVector3
import org.json.JSONObject
import java.io.InputStreamReader

/**
 * Avatar skeleton with bones and transforms
 * Based on Second Life's standard avatar skeleton
 */
class AvatarSkeleton(context: Context) {
    
    companion object {
        private const val TAG = "AvatarSkeleton"
        
        // Standard SL bone names
        val BONE_NAMES = arrayOf(
            "mPelvis", "mTorso", "mChest", "mNeck", "mHead", "mSkull",
            "mCollarLeft", "mShoulderLeft", "mElbowLeft", "mWristLeft",
            "mCollarRight", "mShoulderRight", "mElbowRight", "mWristRight",
            "mHipLeft", "mKneeLeft", "mAnkleLeft", "mFootLeft", "mToeLeft",
            "mHipRight", "mKneeRight", "mAnkleRight", "mFootRight", "mToeRight",
            "mEyeLeft", "mEyeRight",
            // Attachment bones
            "mHindLimbsRoot", "mTail1", "mTail2", "mTail3", "mTail4", "mTail5", "mTail6",
            "mGroin", "mWingsRoot", "mWing1Left", "mWing2Left", "mWing3Left", "mWing4Left",
            "mWing1Right", "mWing2Right", "mWing3Right", "mWing4Right",
            // Extended bones (Bento)
            "mFaceRoot", "mFaceEyeAltRight", "mFaceEyeAltLeft",
            "mFaceForeheadLeft", "mFaceForeheadRight", "mFaceForeheadCenter",
            "mFaceEyebrowOuterLeft", "mFaceEyebrowCenterLeft", "mFaceEyebrowInnerLeft",
            "mFaceEyebrowOuterRight", "mFaceEyebrowCenterRight", "mFaceEyebrowInnerRight",
            "mFaceEyeLidUpperLeft", "mFaceEyeLidLowerLeft",
            "mFaceEyeLidUpperRight", "mFaceEyeLidLowerRight",
            "mFaceEar1Left", "mFaceEar2Left", "mFaceEar1Right", "mFaceEar2Right",
            "mFaceNoseLeft", "mFaceNoseCenter", "mFaceNoseRight",
            "mFaceCheekLowerLeft", "mFaceCheekUpperLeft",
            "mFaceCheekLowerRight", "mFaceCheekUpperRight",
            "mFaceJaw", "mFaceChin", "mFaceTeethLower", "mFaceTeethUpper",
            "mFaceLipLowerLeft", "mFaceLipLowerRight", "mFaceLipLowerCenter",
            "mFaceLipUpperLeft", "mFaceLipUpperRight", "mFaceLipCornerLeft", "mFaceLipCornerRight",
            "mFaceTongueBase", "mFaceTongueTip",
            "mHandMiddle1Left", "mHandMiddle2Left", "mHandMiddle3Left",
            "mHandIndex1Left", "mHandIndex2Left", "mHandIndex3Left",
            "mHandRing1Left", "mHandRing2Left", "mHandRing3Left",
            "mHandPinky1Left", "mHandPinky2Left", "mHandPinky3Left",
            "mHandThumb1Left", "mHandThumb2Left", "mHandThumb3Left",
            "mHandMiddle1Right", "mHandMiddle2Right", "mHandMiddle3Right",
            "mHandIndex1Right", "mHandIndex2Right", "mHandIndex3Right",
            "mHandRing1Right", "mHandRing2Right", "mHandRing3Right",
            "mHandPinky1Right", "mHandPinky2Right", "mHandPinky3Right",
            "mHandThumb1Right", "mHandThumb2Right", "mHandThumb3Right"
        )
    }
    
    val bones = mutableMapOf<String, Bone>()
    val boneArray = mutableListOf<Bone>()
    private var rootBone: Bone? = null
    
    init {
        loadDefaultSkeleton(context)
    }
    
    private fun loadDefaultSkeleton(context: Context) {
        try {
            // Try to load from assets
            val inputStream = context.assets.open("avatar/avatar_skeleton.xml")
            val content = InputStreamReader(inputStream).readText()
            inputStream.close()
            parseSkeletonXML(content)
        } catch (e: Exception) {
            Log.w(TAG, "No skeleton file, using defaults", e)
            createDefaultSkeleton()
        }
    }
    
    private fun parseSkeletonXML(xml: String) {
        // Parse skeleton XML (simplified)
        createDefaultSkeleton()
    }
    
    private fun createDefaultSkeleton() {
        // Create standard SL skeleton hierarchy
        val pelvis = createBone("mPelvis", null, LLVector3(0f, 0f, 1.0f), LLQuaternion.identity())
        
        // Spine
        val torso = createBone("mTorso", pelvis, LLVector3(0f, 0f, 0.084f), LLQuaternion.identity())
        val chest = createBone("mChest", torso, LLVector3(0f, 0f, 0.184f), LLQuaternion.identity())
        val neck = createBone("mNeck", chest, LLVector3(0f, 0f, 0.206f), LLQuaternion.identity())
        val head = createBone("mHead", neck, LLVector3(0f, 0f, 0.076f), LLQuaternion.identity())
        createBone("mSkull", head, LLVector3(0f, 0f, 0.079f), LLQuaternion.identity())
        
        // Eyes
        createBone("mEyeLeft", head, LLVector3(0.033f, 0.029f, 0.055f), LLQuaternion.identity())
        createBone("mEyeRight", head, LLVector3(-0.033f, 0.029f, 0.055f), LLQuaternion.identity())
        
        // Left arm
        val collarL = createBone("mCollarLeft", chest, LLVector3(0.021f, 0f, 0.142f), LLQuaternion.identity())
        val shoulderL = createBone("mShoulderLeft", collarL, LLVector3(0.085f, 0f, 0f), LLQuaternion.identity())
        val elbowL = createBone("mElbowLeft", shoulderL, LLVector3(0.248f, 0f, 0f), LLQuaternion.identity())
        createBone("mWristLeft", elbowL, LLVector3(0.205f, 0f, 0f), LLQuaternion.identity())
        
        // Right arm
        val collarR = createBone("mCollarRight", chest, LLVector3(-0.021f, 0f, 0.142f), LLQuaternion.identity())
        val shoulderR = createBone("mShoulderRight", collarR, LLVector3(-0.085f, 0f, 0f), LLQuaternion.identity())
        val elbowR = createBone("mElbowRight", shoulderR, LLVector3(-0.248f, 0f, 0f), LLQuaternion.identity())
        createBone("mWristRight", elbowR, LLVector3(-0.205f, 0f, 0f), LLQuaternion.identity())
        
        // Left leg
        val hipL = createBone("mHipLeft", pelvis, LLVector3(0.034f, 0f, -0.107f), LLQuaternion.identity())
        val kneeL = createBone("mKneeLeft", hipL, LLVector3(0f, 0f, -0.422f), LLQuaternion.identity())
        val ankleL = createBone("mAnkleLeft", kneeL, LLVector3(0f, 0f, -0.408f), LLQuaternion.identity())
        val footL = createBone("mFootLeft", ankleL, LLVector3(0f, 0.112f, -0.061f), LLQuaternion.identity())
        createBone("mToeLeft", footL, LLVector3(0f, 0.065f, 0f), LLQuaternion.identity())
        
        // Right leg
        val hipR = createBone("mHipRight", pelvis, LLVector3(-0.034f, 0f, -0.107f), LLQuaternion.identity())
        val kneeR = createBone("mKneeRight", hipR, LLVector3(0f, 0f, -0.422f), LLQuaternion.identity())
        val ankleR = createBone("mAnkleRight", kneeR, LLVector3(0f, 0f, -0.408f), LLQuaternion.identity())
        val footR = createBone("mFootRight", ankleR, LLVector3(0f, 0.112f, -0.061f), LLQuaternion.identity())
        createBone("mToeRight", footR, LLVector3(0f, 0.065f, 0f), LLQuaternion.identity())
        
        rootBone = pelvis
        
        // Calculate rest pose matrices
        updateBoneMatrices()
        
        Log.i(TAG, "Created skeleton with ${bones.size} bones")
    }
    
    private fun createBone(
        name: String,
        parent: Bone?,
        offset: LLVector3,
        rotation: LLQuaternion
    ): Bone {
        val index = boneArray.size
        val bone = Bone(
            name = name,
            index = index,
            parent = parent,
            children = mutableListOf(),
            bindPosition = offset,
            bindRotation = rotation,
            position = offset.copy(),
            rotation = rotation.copy(),
            scale = LLVector3(1f, 1f, 1f)
        )
        
        parent?.children?.add(bone)
        bones[name] = bone
        boneArray.add(bone)
        
        return bone
    }
    
    /**
     * Update bone matrices (call after modifying poses)
     */
    fun updateBoneMatrices() {
        rootBone?.let { calculateBoneMatrix(it, FloatArray(16).apply { 
            this[0] = 1f; this[5] = 1f; this[10] = 1f; this[15] = 1f 
        }) }
    }
    
    private fun calculateBoneMatrix(bone: Bone, parentMatrix: FloatArray) {
        // Local transform
        val localMatrix = FloatArray(16)
        
        // Scale
        val s = bone.scale
        // Rotation
        val r = bone.rotation
        // Translation
        val t = bone.position
        
        // Create matrix: T * R * S
        bone.rotation.toMatrix(localMatrix)
        localMatrix[12] = t.x
        localMatrix[13] = t.y
        localMatrix[14] = t.z
        
        // Apply scale
        localMatrix[0] *= s.x; localMatrix[1] *= s.x; localMatrix[2] *= s.x
        localMatrix[4] *= s.y; localMatrix[5] *= s.y; localMatrix[6] *= s.y
        localMatrix[8] *= s.z; localMatrix[9] *= s.z; localMatrix[10] *= s.z
        
        // Multiply with parent
        bone.worldMatrix = multiplyMatrices(parentMatrix, localMatrix)
        
        // Skinning matrix = worldMatrix * inverseBindMatrix
        bone.skinningMatrix = multiplyMatrices(bone.worldMatrix, bone.inverseBindMatrix)
        
        // Recursively update children
        bone.children.forEach { calculateBoneMatrix(it, bone.worldMatrix) }
    }
    
    private fun multiplyMatrices(a: FloatArray, b: FloatArray): FloatArray {
        val result = FloatArray(16)
        for (i in 0..3) {
            for (j in 0..3) {
                result[i * 4 + j] = 
                    a[i * 4 + 0] * b[0 + j] +
                    a[i * 4 + 1] * b[4 + j] +
                    a[i * 4 + 2] * b[8 + j] +
                    a[i * 4 + 3] * b[12 + j]
            }
        }
        return result
    }
    
    /**
     * Get bone by name
     */
    fun getBone(name: String): Bone? = bones[name]
    
    /**
     * Get bone by index
     */
    fun getBoneByIndex(index: Int): Bone? = boneArray.getOrNull(index)
    
    /**
     * Set bone rotation
     */
    fun setBoneRotation(boneName: String, rotation: LLQuaternion) {
        bones[boneName]?.rotation = rotation
    }
    
    /**
     * Set bone position (for attachment points)
     */
    fun setBonePosition(boneName: String, position: LLVector3) {
        bones[boneName]?.position = position
    }
    
    /**
     * Get skinning matrices for GPU
     */
    fun getSkinningMatrices(): FloatArray {
        val result = FloatArray(boneArray.size * 16)
        boneArray.forEachIndexed { index, bone ->
            bone.skinningMatrix.copyInto(result, index * 16)
        }
        return result
    }
}

data class Bone(
    val name: String,
    val index: Int,
    val parent: Bone?,
    val children: MutableList<Bone>,
    val bindPosition: LLVector3,
    val bindRotation: LLQuaternion,
    var position: LLVector3,
    var rotation: LLQuaternion,
    var scale: LLVector3,
    var worldMatrix: FloatArray = FloatArray(16).apply { 
        this[0] = 1f; this[5] = 1f; this[10] = 1f; this[15] = 1f 
    },
    var inverseBindMatrix: FloatArray = FloatArray(16).apply {
        this[0] = 1f; this[5] = 1f; this[10] = 1f; this[15] = 1f
    },
    var skinningMatrix: FloatArray = FloatArray(16).apply {
        this[0] = 1f; this[5] = 1f; this[10] = 1f; this[15] = 1f
    }
)
