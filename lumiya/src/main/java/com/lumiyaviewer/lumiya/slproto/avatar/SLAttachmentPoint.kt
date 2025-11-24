package com.lumiyaviewer.lumiya.slproto.avatar

import com.lumiyaviewer.lumiya.slproto.types.LLQuaternion
import com.lumiyaviewer.lumiya.slproto.types.LLVector3
import java.util.HashMap

class SLAttachmentPoint(
    val id: Int,
    val name: String,
    val nonHUDindex: Int,
    val isHUD: Boolean,
    val bone: SLSkeletonBoneID?,
    val position: LLVector3,
    val rotation: LLQuaternion
) {
    companion object {
        const val NON_HUD_ATTACHMENT_POINTS = 47
        const val NUM_ATTACHMENT_POINTS = 56
        
        val attachmentPoints = arrayOfNulls<SLAttachmentPoint>(56)
        val nonHUDpoints = intArrayOf(
            1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 
            25, 26, 27, 28, 29, 30, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55
        )
        val pointsByName = HashMap<String, SLAttachmentPoint>()

        init {
            add(1, "Chest", 0, false, SLSkeletonBoneID.mChest, LLVector3(0.15f, 0.0f, -0.1f), LLQuaternion(0.5f, 0.5f, 0.5f, 0.5f))
            add(2, "Skull", 1, false, SLSkeletonBoneID.mHead, LLVector3(0.0f, 0.0f, 0.15f), LLQuaternion(0.0f, 0.0f, 0.707107f, 0.707107f))
            add(3, "Left Shoulder", 2, false, SLSkeletonBoneID.mCollarLeft, LLVector3(0.0f, 0.0f, 0.08f), LLQuaternion(0.0f, 0.0f, 0.0f, 1.0f))
            add(4, "Right Shoulder", 3, false, SLSkeletonBoneID.mCollarRight, LLVector3(0.0f, 0.0f, 0.08f), LLQuaternion(0.0f, 0.0f, 0.0f, 1.0f))
            add(5, "Left Hand", 4, false, SLSkeletonBoneID.mWristLeft, LLVector3(0.0f, 0.08f, -0.02f), LLQuaternion(0.0f, 0.0f, 0.0f, 1.0f))
            add(6, "Right Hand", 5, false, SLSkeletonBoneID.mWristRight, LLVector3(0.0f, -0.08f, -0.02f), LLQuaternion(0.0f, 0.0f, 0.0f, 1.0f))
            add(7, "Left Foot", 6, false, SLSkeletonBoneID.mFootLeft, LLVector3(0.0f, 0.0f, 0.0f), LLQuaternion(0.0f, 0.0f, 0.0f, 1.0f))
            add(8, "Right Foot", 7, false, SLSkeletonBoneID.mFootRight, LLVector3(0.0f, 0.0f, 0.0f), LLQuaternion(0.0f, 0.0f, 0.0f, 1.0f))
            add(9, "Spine", 8, false, SLSkeletonBoneID.mChest, LLVector3(-0.15f, 0.0f, -0.1f), LLQuaternion(-0.5f, -0.5f, 0.5f, 0.5f))
            add(10, "Pelvis", 9, false, SLSkeletonBoneID.mPelvis, LLVector3(0.0f, 0.0f, -0.15f), LLQuaternion(0.0f, 0.0f, 0.0f, 1.0f))
            add(11, "Mouth", 10, false, SLSkeletonBoneID.mHead, LLVector3(0.12f, 0.0f, 0.001f), LLQuaternion(0.0f, 0.0f, 0.0f, 1.0f))
            add(12, "Chin", 11, false, SLSkeletonBoneID.mHead, LLVector3(0.12f, 0.0f, -0.04f), LLQuaternion(0.0f, 0.0f, 0.0f, 1.0f))
            add(13, "Left Ear", 12, false, SLSkeletonBoneID.mHead, LLVector3(0.015f, 0.08f, 0.017f), LLQuaternion(0.0f, 0.0f, 0.0f, 1.0f))
            add(14, "Right Ear", 13, false, SLSkeletonBoneID.mHead, LLVector3(0.015f, -0.08f, 0.017f), LLQuaternion(0.0f, 0.0f, 0.0f, 1.0f))
            add(15, "Left Eyeball", 14, false, SLSkeletonBoneID.mEyeLeft, LLVector3(0.0f, 0.0f, 0.0f), LLQuaternion(0.0f, 0.0f, 0.0f, 1.0f))
            add(16, "Right Eyeball", 15, false, SLSkeletonBoneID.mEyeRight, LLVector3(0.0f, 0.0f, 0.0f), LLQuaternion(0.0f, 0.0f, 0.0f, 1.0f))
            add(17, "Nose", 16, false, SLSkeletonBoneID.mHead, LLVector3(0.1f, 0.0f, 0.05f), LLQuaternion(0.0f, 0.0f, 0.0f, 1.0f))
            add(18, "R Upper Arm", 17, false, SLSkeletonBoneID.mShoulderRight, LLVector3(0.01f, -0.13f, 0.01f), LLQuaternion(0.0f, 0.0f, 0.0f, 1.0f))
            add(19, "R Forearm", 18, false, SLSkeletonBoneID.mElbowRight, LLVector3(0.0f, -0.12f, 0.0f), LLQuaternion(0.0f, 0.0f, 0.0f, 1.0f))
            add(20, "L Upper Arm", 19, false, SLSkeletonBoneID.mShoulderLeft, LLVector3(0.01f, 0.15f, -0.01f), LLQuaternion(0.0f, 0.0f, 0.0f, 1.0f))
            add(21, "L Forearm", 20, false, SLSkeletonBoneID.mElbowLeft, LLVector3(0.0f, 0.113f, 0.0f), LLQuaternion(0.0f, 0.0f, 0.0f, 1.0f))
            add(22, "Right Hip", 21, false, SLSkeletonBoneID.mHipRight, LLVector3(0.0f, 0.0f, 0.0f), LLQuaternion(0.0f, 0.0f, 0.0f, 1.0f))
            add(23, "R Upper Leg", 22, false, SLSkeletonBoneID.mHipRight, LLVector3(-0.017f, 0.041f, -0.31f), LLQuaternion(0.0f, 0.0f, 0.0f, 1.0f))
            add(24, "R Lower Leg", 23, false, SLSkeletonBoneID.mKneeRight, LLVector3(-0.044f, -0.007f, -0.262f), LLQuaternion(0.0f, 0.0f, 0.0f, 1.0f))
            add(25, "Left Hip", 24, false, SLSkeletonBoneID.mHipLeft, LLVector3(0.0f, 0.0f, 0.0f), LLQuaternion(0.0f, 0.0f, 0.0f, 1.0f))
            add(26, "L Upper Leg", 25, false, SLSkeletonBoneID.mHipLeft, LLVector3(-0.019f, -0.034f, -0.31f), LLQuaternion(0.0f, 0.0f, 0.0f, 1.0f))
            add(27, "L Lower Leg", 26, false, SLSkeletonBoneID.mKneeLeft, LLVector3(-0.044f, -0.007f, -0.261f), LLQuaternion(0.0f, 0.0f, 0.0f, 1.0f))
            add(28, "Stomach", 27, false, SLSkeletonBoneID.mPelvis, LLVector3(0.092f, 0.0f, 0.088f), LLQuaternion(0.0f, 0.0f, 0.0f, 1.0f))
            add(29, "Left Pec", 28, false, SLSkeletonBoneID.mTorso, LLVector3(0.104f, 0.082f, 0.247f), LLQuaternion(0.0f, 0.0f, 0.0f, 1.0f))
            add(30, "Right Pec", 29, false, SLSkeletonBoneID.mTorso, LLVector3(0.104f, -0.082f, 0.247f), LLQuaternion(0.0f, 0.0f, 0.0f, 1.0f))
            add(31, "Center 2", -1, true, null, LLVector3(0.0f, 0.0f, 0.0f), LLQuaternion(0.0f, 0.0f, 0.0f, 1.0f))
            add(32, "Top Right", -1, true, null, LLVector3(0.0f, -0.5f, 0.5f), LLQuaternion(0.0f, 0.0f, 0.0f, 1.0f))
            add(33, "Top", -1, true, null, LLVector3(0.0f, 0.0f, 0.5f), LLQuaternion(0.0f, 0.0f, 0.0f, 1.0f))
            add(34, "Top Left", -1, true, null, LLVector3(0.0f, 0.5f, 0.5f), LLQuaternion(0.0f, 0.0f, 0.0f, 1.0f))
            add(35, "Center", -1, true, null, LLVector3(0.0f, 0.0f, 0.0f), LLQuaternion(0.0f, 0.0f, 0.0f, 1.0f))
            add(36, "Bottom Left", -1, true, null, LLVector3(0.0f, 0.5f, -0.5f), LLQuaternion(0.0f, 0.0f, 0.0f, 1.0f))
            add(37, "Bottom", -1, true, null, LLVector3(0.0f, 0.0f, -0.5f), LLQuaternion(0.0f, 0.0f, 0.0f, 1.0f))
            add(38, "Bottom Right", -1, true, null, LLVector3(0.0f, -0.5f, -0.5f), LLQuaternion(0.0f, 0.0f, 0.0f, 1.0f))
            add(39, "Neck", 30, false, SLSkeletonBoneID.mNeck, LLVector3(0.0f, 0.0f, 0.0f), LLQuaternion(0.0f, 0.0f, 0.0f, 1.0f))
            add(40, "Avatar Center", 31, false, null, LLVector3(0.0f, 0.0f, 0.0f), LLQuaternion(0.0f, 0.0f, 0.0f, 1.0f))
            add(41, "Left Ring Finger", 32, false, SLSkeletonBoneID.mHandRing1Left, LLVector3(-0.006f, 0.019f, -0.002f), LLQuaternion(0.0f, 0.0f, 0.0f, 1.0f))
            add(42, "Right Ring Finger", 33, false, SLSkeletonBoneID.mHandRing1Right, LLVector3(-0.006f, -0.019f, -0.002f), LLQuaternion(0.0f, 0.0f, 0.0f, 1.0f))
            add(43, "Tail Base", 34, false, SLSkeletonBoneID.mTail1, LLVector3(0.0f, 0.0f, 0.0f), LLQuaternion(0.0f, 0.0f, 0.0f, 1.0f))
            add(44, "Tail Tip", 35, false, SLSkeletonBoneID.mTail6, LLVector3(-0.025f, 0.0f, 0.0f), LLQuaternion(0.0f, 0.0f, 0.0f, 1.0f))
            add(45, "Left Wing", 36, false, SLSkeletonBoneID.mWing4Left, LLVector3(0.0f, 0.0f, 0.0f), LLQuaternion(0.0f, 0.0f, 0.0f, 1.0f))
            add(46, "Right Wing", 37, false, SLSkeletonBoneID.mWing4Right, LLVector3(0.0f, 0.0f, 0.0f), LLQuaternion(0.0f, 0.0f, 0.0f, 1.0f))
            add(47, "Jaw", 38, false, SLSkeletonBoneID.mFaceJaw, LLVector3(0.0f, 0.0f, 0.0f), LLQuaternion(0.0f, 0.0f, 0.0f, 1.0f))
            add(48, "Alt Left Ear", 39, false, SLSkeletonBoneID.mFaceEar1Left, LLVector3(0.0f, 0.0f, 0.0f), LLQuaternion(0.0f, 0.0f, 0.0f, 1.0f))
            add(49, "Alt Right Ear", 40, false, SLSkeletonBoneID.mFaceEar1Right, LLVector3(0.0f, 0.0f, 0.0f), LLQuaternion(0.0f, 0.0f, 0.0f, 1.0f))
            add(50, "Alt Left Eye", 41, false, SLSkeletonBoneID.mFaceEyeAltLeft, LLVector3(0.0f, 0.0f, 0.0f), LLQuaternion(0.0f, 0.0f, 0.0f, 1.0f))
            add(51, "Alt Right Eye", 42, false, SLSkeletonBoneID.mFaceEyeAltRight, LLVector3(0.0f, 0.0f, 0.0f), LLQuaternion(0.0f, 0.0f, 0.0f, 1.0f))
            add(52, "Tongue", 43, false, SLSkeletonBoneID.mFaceTongueTip, LLVector3(0.0f, 0.0f, 0.0f), LLQuaternion(0.0f, 0.0f, 0.0f, 1.0f))
            add(53, "Groin", 44, false, SLSkeletonBoneID.mGroin, LLVector3(0.0f, 0.0f, 0.0f), LLQuaternion(0.0f, 0.0f, 0.0f, 1.0f))
            add(54, "Left Hind Foot", 45, false, SLSkeletonBoneID.mHindLimb4Left, LLVector3(0.0f, 0.0f, 0.0f), LLQuaternion(0.0f, 0.0f, 0.0f, 1.0f))
            add(55, "Right Hind Foot", 46, false, SLSkeletonBoneID.mHindLimb4Right, LLVector3(0.0f, 0.0f, 0.0f), LLQuaternion(0.0f, 0.0f, 0.0f, 1.0f))
        }

        private fun add(id: Int, name: String, index: Int, isHUD: Boolean, bone: SLSkeletonBoneID?, pos: LLVector3, rot: LLQuaternion) {
            val point = SLAttachmentPoint(id, name, index, isHUD, bone, pos, rot)
            attachmentPoints[id] = point
            pointsByName[name] = point
        }
    }
}
