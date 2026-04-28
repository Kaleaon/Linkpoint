package com.linkpoint.linden.llcharacter

import com.linkpoint.linden.llcommon.LLUUID
import com.linkpoint.linden.llmath.Quaternion

class LLHandMotion : LLMotion(LLUUID.generate()) {

    enum class HandPose(val id: Int) {
        RELAX(0), OPEN(1), POINT(2), FIST(3), CLENCH(4),
        PEACE(5), SPLAYED(6), VULCAN(7)
    }

    override var priority: Int = 1
    override var duration: Float = 0f
    override var loop: Boolean = true

    var handPose: HandPose = HandPose.RELAX
        set(value) { field = value }

    private val fingerJoints = listOf(
        "mHandThumb1", "mHandThumb2", "mHandThumb3",
        "mHandIndex1", "mHandIndex2", "mHandIndex3",
        "mHandMiddle1", "mHandMiddle2", "mHandMiddle3",
        "mHandRing1", "mHandRing2", "mHandRing3",
        "mHandPinky1", "mHandPinky2", "mHandPinky3"
    )

    override fun onUpdate(time: Float): Boolean = true

    fun getJointRotation(jointName: String): Quaternion {
        val curl = when (handPose) {
            HandPose.RELAX   -> 0.05f
            HandPose.OPEN    -> 0f
            HandPose.POINT   -> if ("Index" in jointName) 0f else 0.8f
            HandPose.FIST    -> 0.9f
            HandPose.CLENCH  -> 0.7f
            HandPose.PEACE   -> if ("Index" in jointName || "Middle" in jointName) 0f else 0.8f
            HandPose.SPLAYED -> 0f
            HandPose.VULCAN  -> if ("Ring" in jointName || "Pinky" in jointName) 0f else
                                if ("Middle" in jointName) 0.15f else 0f
        }
        return Quaternion().setEulerAngles(curl, 0f, 0f)
    }
}
