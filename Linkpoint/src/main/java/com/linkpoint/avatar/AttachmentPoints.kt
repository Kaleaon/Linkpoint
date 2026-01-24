package com.linkpoint.avatar

import java.util.UUID

/**
 * Attachment Points - Defines all attachment points on an avatar.
 * 
 * Based on Lumiya's SLAttachmentPoint.java
 * 
 * Second Life avatars have 59+ attachment points where objects can be attached.
 * Each point has a unique ID, name, and bone/joint association.
 */
object AttachmentPoints {
    
    // Attachment point IDs (from SL protocol)
    const val ATTACH_CHEST = 1
    const val ATTACH_HEAD = 2
    const val ATTACH_LSHOULDER = 3
    const val ATTACH_RSHOULDER = 4
    const val ATTACH_LHAND = 5
    const val ATTACH_RHAND = 6
    const val ATTACH_LFOOT = 7
    const val ATTACH_RFOOT = 8
    const val ATTACH_BACK = 9
    const val ATTACH_PELVIS = 10
    const val ATTACH_MOUTH = 11
    const val ATTACH_CHIN = 12
    const val ATTACH_LEAR = 13
    const val ATTACH_REAR = 14
    const val ATTACH_LEYE = 15
    const val ATTACH_REYE = 16
    const val ATTACH_NOSE = 17
    const val ATTACH_RUARM = 18
    const val ATTACH_RLARM = 19
    const val ATTACH_LUARM = 20
    const val ATTACH_LLARM = 21
    const val ATTACH_RHIP = 22
    const val ATTACH_RULEG = 23
    const val ATTACH_RLLEG = 24
    const val ATTACH_LHIP = 25
    const val ATTACH_LULEG = 26
    const val ATTACH_LLLEG = 27
    const val ATTACH_BELLY = 28
    const val ATTACH_RPEC = 29
    const val ATTACH_LPEC = 30
    const val ATTACH_HUD_CENTER_2 = 31
    const val ATTACH_HUD_TOP_RIGHT = 32
    const val ATTACH_HUD_TOP_CENTER = 33
    const val ATTACH_HUD_TOP_LEFT = 34
    const val ATTACH_HUD_CENTER_1 = 35
    const val ATTACH_HUD_BOTTOM_LEFT = 36
    const val ATTACH_HUD_BOTTOM = 37
    const val ATTACH_HUD_BOTTOM_RIGHT = 38
    const val ATTACH_NECK = 39
    const val ATTACH_ROOT = 40
    
    // Extended attachment points (added in later SL versions)
    const val ATTACH_AVATAR_CENTER = 40
    const val ATTACH_LHAND_RING1 = 41
    const val ATTACH_RHAND_RING1 = 42
    const val ATTACH_TAIL_BASE = 43
    const val ATTACH_TAIL_TIP = 44
    const val ATTACH_LWING = 45
    const val ATTACH_RWING = 46
    const val ATTACH_FACE_JAW = 47
    const val ATTACH_FACE_LEAR = 48
    const val ATTACH_FACE_REAR = 49
    const val ATTACH_FACE_LEYE = 50
    const val ATTACH_FACE_REYE = 51
    const val ATTACH_FACE_TONGUE = 52
    const val ATTACH_GROIN = 53
    const val ATTACH_HIND_LFOOT = 54
    const val ATTACH_HIND_RFOOT = 55
    
    // All attachment points with position and rotation data from Second Life
    val ALL_POINTS: Map<Int, AttachmentPoint> = mapOf(
        ATTACH_CHEST to AttachmentPoint(ATTACH_CHEST, "Chest", "mChest", false, 
            Triple(0.15f, 0f, -0.1f), Triple(0f, 90f, 90f), true, 6, 2),
        ATTACH_HEAD to AttachmentPoint(ATTACH_HEAD, "Skull", "mHead", false,
            Triple(0f, 0f, 0.15f), Triple(0f, 0f, 90f), false, 2, 2),
        ATTACH_LSHOULDER to AttachmentPoint(ATTACH_LSHOULDER, "Left Shoulder", "mCollarLeft", false,
            Triple(0f, 0f, 0.08f), Triple(0f, 0f, 0f), true, 3, 3),
        ATTACH_RSHOULDER to AttachmentPoint(ATTACH_RSHOULDER, "Right Shoulder", "mCollarRight", false,
            Triple(0f, 0f, 0.08f), Triple(0f, 0f, 0f), true, 1, 1),
        ATTACH_LHAND to AttachmentPoint(ATTACH_LHAND, "Left Hand", "mWristLeft", false,
            Triple(0f, 0.08f, -0.02f), Triple(0f, 0f, 0f), true, 4, 0, 1.5f),
        ATTACH_RHAND to AttachmentPoint(ATTACH_RHAND, "Right Hand", "mWristRight", false,
            Triple(0f, -0.08f, -0.02f), Triple(0f, 0f, 0f), true, 0, 0, 1.5f),
        ATTACH_LFOOT to AttachmentPoint(ATTACH_LFOOT, "Left Foot", "mFootLeft", false,
            Triple(0f, 0f, 0f), Triple(0f, 0f, 0f), true, 5, 6),
        ATTACH_RFOOT to AttachmentPoint(ATTACH_RFOOT, "Right Foot", "mFootRight", false,
            Triple(0f, 0f, 0f), Triple(0f, 0f, 0f), true, 7, 6),
        ATTACH_BACK to AttachmentPoint(ATTACH_BACK, "Spine", "mChest", false,
            Triple(-0.15f, 0f, -0.1f), Triple(0f, -90f, 90f), true, 6, 7),
        ATTACH_PELVIS to AttachmentPoint(ATTACH_PELVIS, "Pelvis", "mPelvis", false,
            Triple(0f, 0f, -0.15f), Triple(0f, 0f, 0f), true, 6, 6),
        ATTACH_MOUTH to AttachmentPoint(ATTACH_MOUTH, "Mouth", "mHead", false,
            Triple(0.12f, 0f, 0.001f), Triple(0f, 0f, 0f), false, 2, 6),
        ATTACH_CHIN to AttachmentPoint(ATTACH_CHIN, "Chin", "mHead", false,
            Triple(0.12f, 0f, -0.04f), Triple(0f, 0f, 0f), false, 2, 7),
        ATTACH_LEAR to AttachmentPoint(ATTACH_LEAR, "Left Ear", "mHead", false,
            Triple(0.015f, 0.08f, 0.017f), Triple(0f, 0f, 0f), false, 2, 4),
        ATTACH_REAR to AttachmentPoint(ATTACH_REAR, "Right Ear", "mHead", false,
            Triple(0.015f, -0.08f, 0.017f), Triple(0f, 0f, 0f), false, 2, 0),
        ATTACH_LEYE to AttachmentPoint(ATTACH_LEYE, "Left Eye", "mEyeLeft", false,
            Triple(0f, 0f, 0f), Triple(0f, 0f, 0f), false, 2, 3),
        ATTACH_REYE to AttachmentPoint(ATTACH_REYE, "Right Eye", "mEyeRight", false,
            Triple(0f, 0f, 0f), Triple(0f, 0f, 0f), false, 2, 1),
        ATTACH_NOSE to AttachmentPoint(ATTACH_NOSE, "Nose", "mHead", false,
            Triple(0.1f, 0f, 0.05f), Triple(0f, 0f, 0f), false, 2, 5),
        ATTACH_RUARM to AttachmentPoint(ATTACH_RUARM, "R Upper Arm", "mShoulderRight", false,
            Triple(0f, 0f, 0f), Triple(0f, 0f, 0f), true, 1, 2),
        ATTACH_RLARM to AttachmentPoint(ATTACH_RLARM, "R Lower Arm", "mElbowRight", false,
            Triple(0f, 0f, 0f), Triple(0f, 0f, 0f), true, 1, 3),
        ATTACH_LUARM to AttachmentPoint(ATTACH_LUARM, "L Upper Arm", "mShoulderLeft", false,
            Triple(0f, 0f, 0f), Triple(0f, 0f, 0f), true, 3, 2),
        ATTACH_LLARM to AttachmentPoint(ATTACH_LLARM, "L Lower Arm", "mElbowLeft", false,
            Triple(0f, 0f, 0f), Triple(0f, 0f, 0f), true, 3, 1),
        ATTACH_RHIP to AttachmentPoint(ATTACH_RHIP, "Right Hip", "mHipRight", false,
            Triple(0f, 0f, 0f), Triple(0f, 0f, 0f), true, 7, 7),
        ATTACH_RULEG to AttachmentPoint(ATTACH_RULEG, "R Upper Leg", "mKneeRight", false,
            Triple(0f, 0f, 0f), Triple(0f, 0f, 0f), true, 7, 0),
        ATTACH_RLLEG to AttachmentPoint(ATTACH_RLLEG, "R Lower Leg", "mAnkleRight", false,
            Triple(0f, 0f, 0f), Triple(0f, 0f, 0f), true, 7, 1),
        ATTACH_LHIP to AttachmentPoint(ATTACH_LHIP, "Left Hip", "mHipLeft", false,
            Triple(0f, 0f, 0f), Triple(0f, 0f, 0f), true, 5, 7),
        ATTACH_LULEG to AttachmentPoint(ATTACH_LULEG, "L Upper Leg", "mKneeLeft", false,
            Triple(0f, 0f, 0f), Triple(0f, 0f, 0f), true, 5, 0),
        ATTACH_LLLEG to AttachmentPoint(ATTACH_LLLEG, "L Lower Leg", "mAnkleLeft", false,
            Triple(0f, 0f, 0f), Triple(0f, 0f, 0f), true, 5, 1),
        ATTACH_BELLY to AttachmentPoint(ATTACH_BELLY, "Stomach", "mChest", false,
            Triple(0f, 0f, 0f), Triple(0f, 0f, 0f), true, 6, 1),
        ATTACH_RPEC to AttachmentPoint(ATTACH_RPEC, "Right Pec", "mChest", false,
            Triple(0f, 0f, 0f), Triple(0f, 0f, 0f), true, 6, 0),
        ATTACH_LPEC to AttachmentPoint(ATTACH_LPEC, "Left Pec", "mChest", false,
            Triple(0f, 0f, 0f), Triple(0f, 0f, 0f), true, 6, 4),
        
        // HUD points
        ATTACH_HUD_CENTER_2 to AttachmentPoint(ATTACH_HUD_CENTER_2, "HUD Center 2", null, true),
        ATTACH_HUD_TOP_RIGHT to AttachmentPoint(ATTACH_HUD_TOP_RIGHT, "HUD Top Right", null, true),
        ATTACH_HUD_TOP_CENTER to AttachmentPoint(ATTACH_HUD_TOP_CENTER, "HUD Top", null, true),
        ATTACH_HUD_TOP_LEFT to AttachmentPoint(ATTACH_HUD_TOP_LEFT, "HUD Top Left", null, true),
        ATTACH_HUD_CENTER_1 to AttachmentPoint(ATTACH_HUD_CENTER_1, "HUD Center", null, true),
        ATTACH_HUD_BOTTOM_LEFT to AttachmentPoint(ATTACH_HUD_BOTTOM_LEFT, "HUD Bottom Left", null, true),
        ATTACH_HUD_BOTTOM to AttachmentPoint(ATTACH_HUD_BOTTOM, "HUD Bottom", null, true),
        ATTACH_HUD_BOTTOM_RIGHT to AttachmentPoint(ATTACH_HUD_BOTTOM_RIGHT, "HUD Bottom Right", null, true),
        
        // More avatar points
        ATTACH_NECK to AttachmentPoint(ATTACH_NECK, "Neck", "mNeck", false,
            Triple(0f, 0f, 0f), Triple(0f, 0f, 0f), true, 6, 3),
        ATTACH_ROOT to AttachmentPoint(ATTACH_ROOT, "Avatar Center", "mPelvis", false,
            Triple(0f, 0f, 0f), Triple(0f, 0f, 0f), true),
        
        // Extended/Bento points
        ATTACH_LHAND_RING1 to AttachmentPoint(ATTACH_LHAND_RING1, "Left Ring Finger", "mHandRing1Left", false),
        ATTACH_RHAND_RING1 to AttachmentPoint(ATTACH_RHAND_RING1, "Right Ring Finger", "mHandRing1Right", false),
        ATTACH_TAIL_BASE to AttachmentPoint(ATTACH_TAIL_BASE, "Tail Base", "mTail1", false),
        ATTACH_TAIL_TIP to AttachmentPoint(ATTACH_TAIL_TIP, "Tail Tip", "mTail6", false),
        ATTACH_LWING to AttachmentPoint(ATTACH_LWING, "Left Wing", "mWing1Left", false),
        ATTACH_RWING to AttachmentPoint(ATTACH_RWING, "Right Wing", "mWing1Right", false),
        ATTACH_FACE_JAW to AttachmentPoint(ATTACH_FACE_JAW, "Jaw", "mFaceJaw", false),
        ATTACH_FACE_LEAR to AttachmentPoint(ATTACH_FACE_LEAR, "Alt Left Ear", "mFaceEarLeft", false),
        ATTACH_FACE_REAR to AttachmentPoint(ATTACH_FACE_REAR, "Alt Right Ear", "mFaceEarRight", false),
        ATTACH_FACE_LEYE to AttachmentPoint(ATTACH_FACE_LEYE, "Alt Left Eye", "mFaceEyeAltLeft", false),
        ATTACH_FACE_REYE to AttachmentPoint(ATTACH_FACE_REYE, "Alt Right Eye", "mFaceEyeAltRight", false),
        ATTACH_FACE_TONGUE to AttachmentPoint(ATTACH_FACE_TONGUE, "Tongue", "mFaceTongueTip", false),
        ATTACH_GROIN to AttachmentPoint(ATTACH_GROIN, "Groin", "mGroin", false),
        ATTACH_HIND_LFOOT to AttachmentPoint(ATTACH_HIND_LFOOT, "Left Hind Foot", "mHindFoot1Left", false),
        ATTACH_HIND_RFOOT to AttachmentPoint(ATTACH_HIND_RFOOT, "Right Hind Foot", "mHindFoot1Right", false)
    )
    
    // HUD attachment point IDs
    val HUD_POINTS = setOf(
        ATTACH_HUD_CENTER_2,
        ATTACH_HUD_TOP_RIGHT,
        ATTACH_HUD_TOP_CENTER,
        ATTACH_HUD_TOP_LEFT,
        ATTACH_HUD_CENTER_1,
        ATTACH_HUD_BOTTOM_LEFT,
        ATTACH_HUD_BOTTOM,
        ATTACH_HUD_BOTTOM_RIGHT
    )
    
    /**
     * Get attachment point by ID.
     */
    fun getPoint(id: Int): AttachmentPoint? = ALL_POINTS[id]
    
    /**
     * Get attachment point name.
     */
    fun getPointName(id: Int): String = ALL_POINTS[id]?.name ?: "Unknown ($id)"
    
    /**
     * Check if attachment point is a HUD point.
     */
    fun isHudPoint(id: Int): Boolean = HUD_POINTS.contains(id)
    
    /**
     * Get all body (non-HUD) attachment points.
     */
    fun getBodyPoints(): List<AttachmentPoint> {
        return ALL_POINTS.values.filter { !it.isHud }
    }
    
    /**
     * Get all HUD attachment points.
     */
    fun getHudPoints(): List<AttachmentPoint> {
        return ALL_POINTS.values.filter { it.isHud }
    }
    
    /**
     * Get attachment points for a specific bone.
     */
    fun getPointsForBone(boneName: String): List<AttachmentPoint> {
        return ALL_POINTS.values.filter { it.jointName == boneName }
    }
    
    /**
     * Get attachment points visible in first person view.
     */
    fun getFirstPersonVisiblePoints(): List<AttachmentPoint> {
        return ALL_POINTS.values.filter { it.visibleInFirstPerson }
    }
    
    /**
     * Get attachment points by group.
     */
    fun getPointsByGroup(group: Int): List<AttachmentPoint> {
        return ALL_POINTS.values.filter { it.group == group }
    }
}

/**
 * Represents an attachment point on the avatar.
 * Enhanced with position, rotation, and first-person visibility data from Second Life.
 */
data class AttachmentPoint(
    val id: Int,
    val name: String,
    val jointName: String?,
    val isHud: Boolean,
    val position: Triple<Float, Float, Float> = Triple(0f, 0f, 0f),
    val rotation: Triple<Float, Float, Float> = Triple(0f, 0f, 0f),
    val visibleInFirstPerson: Boolean = true,
    val group: Int = 0,
    val pieSlice: Int = 0,
    val maxAttachmentOffset: Float = 0f
)

/**
 * Represents an object attached to an avatar.
 */
data class Attachment(
    val itemId: UUID,
    val objectId: UUID,
    val attachmentPoint: Int,
    val localId: Int = 0
) {
    val pointName: String get() = AttachmentPoints.getPointName(attachmentPoint)
    val isHud: Boolean get() = AttachmentPoints.isHudPoint(attachmentPoint)
}
