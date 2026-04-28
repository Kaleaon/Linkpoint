package com.linkpoint.linden.llcharacter

import com.linkpoint.linden.llcommon.LLUUID
import com.linkpoint.linden.llmath.Quaternion
import com.linkpoint.linden.llmath.Vector3
import com.linkpoint.linden.llmath.slerp

const val MIN_REQUIRED_PIXEL_AREA_KEYFRAME = 40f
const val MAX_CHAIN_LENGTH = 4
const val KEYFRAME_MOTION_VERSION = 1
const val KEYFRAME_MOTION_SUBVERSION = 0

class KeyframeMotion(val id: LLUUID) {

    var name: String = ""

    enum class AssetStatus {
        LOADED, FETCHED, NEEDS_FETCH, FETCH_FAILED, UNDEFINED
    }

    enum class InterpolationType {
        STEP, LINEAR, SPLINE
    }

    enum class BlendType {
        NORMAL, ADDITIVE
    }

    enum class InitStatus {
        FAILURE, SUCCESS, HOLD
    }

    data class ScaleKey(val time: Float = 0f, val scale: Vector3 = Vector3())
    data class RotationKey(val time: Float = 0f, val rotation: Quaternion = Quaternion())
    data class PositionKey(val time: Float = 0f, val position: Vector3 = Vector3())

    class ScaleCurve {
        var interpolationType: InterpolationType = InterpolationType.LINEAR
        val keys: TreeMap<Float, ScaleKey> = TreeMap()
        var loopInKey: ScaleKey = ScaleKey()
        var loopOutKey: ScaleKey = ScaleKey()

        fun getValue(time: Float, duration: Float): Vector3 {
            if (keys.isEmpty()) return Vector3()
            val floor = keys.floorEntry(time) ?: keys.firstEntry()
            val ceil = keys.ceilingEntry(time) ?: keys.lastEntry()
            if (floor.key == ceil.key) return floor.value.scale
            val u = (time - floor.key) / (ceil.key - floor.key)
            return interp(u, floor.value, ceil.value)
        }

        private fun interp(u: Float, before: ScaleKey, after: ScaleKey): Vector3 =
            Vector3(
                before.scale.x + u * (after.scale.x - before.scale.x),
                before.scale.y + u * (after.scale.y - before.scale.y),
                before.scale.z + u * (after.scale.z - before.scale.z)
            )
    }

    class RotationCurve {
        var interpolationType: InterpolationType = InterpolationType.LINEAR
        val keys: TreeMap<Float, RotationKey> = TreeMap()
        var loopInKey: RotationKey = RotationKey()
        var loopOutKey: RotationKey = RotationKey()

        fun getValue(time: Float, duration: Float): Quaternion {
            if (keys.isEmpty()) return Quaternion()
            val floor = keys.floorEntry(time) ?: keys.firstEntry()
            val ceil = keys.ceilingEntry(time) ?: keys.lastEntry()
            if (floor.key == ceil.key) return floor.value.rotation
            val u = (time - floor.key) / (ceil.key - floor.key)
            return interp(u, floor.value, ceil.value)
        }

        private fun interp(u: Float, before: RotationKey, after: RotationKey): Quaternion =
            slerp(u, before.rotation, after.rotation)
    }

    class PositionCurve {
        var interpolationType: InterpolationType = InterpolationType.LINEAR
        val keys: TreeMap<Float, PositionKey> = TreeMap()
        var loopInKey: PositionKey = PositionKey()
        var loopOutKey: PositionKey = PositionKey()

        fun getValue(time: Float, duration: Float): Vector3 {
            if (keys.isEmpty()) return Vector3()
            val floor = keys.floorEntry(time) ?: keys.firstEntry()
            val ceil = keys.ceilingEntry(time) ?: keys.lastEntry()
            if (floor.key == ceil.key) return floor.value.position
            val u = (time - floor.key) / (ceil.key - floor.key)
            return interp(u, floor.value, ceil.value)
        }

        private fun interp(u: Float, before: PositionKey, after: PositionKey): Vector3 =
            Vector3(
                before.position.x + u * (after.position.x - before.position.x),
                before.position.y + u * (after.position.y - before.position.y),
                before.position.z + u * (after.position.z - before.position.z)
            )
    }

    class JointMotion {
        val positionCurve = PositionCurve()
        val rotationCurve = RotationCurve()
        val scaleCurve = ScaleCurve()
        var jointName: String = ""
        var usage: UInt = 0u
        var priority: JointPriority = JointPriority.LOW

        fun update(jointState: JointState, time: Float, duration: Float) {
            if (usage and JointState.Usage.POS.mask != 0u)
                jointState.position = positionCurve.getValue(time, duration)
            if (usage and JointState.Usage.ROT.mask != 0u)
                jointState.rotation = rotationCurve.getValue(time, duration)
            if (usage and JointState.Usage.SCALE.mask != 0u)
                jointState.scale = scaleCurve.getValue(time, duration)
        }
    }

    class JointMotionList {
        val jointMotions: MutableList<JointMotion> = mutableListOf()
        var duration: Float = 0f
        var loop: Boolean = false
        var loopInPoint: Float = 0f
        var loopOutPoint: Float = 0f
        var easeInDuration: Float = 0f
        var easeOutDuration: Float = 0f
        var basePriority: JointPriority = JointPriority.LOW
        var maxPriority: JointPriority = JointPriority.LOW
        var emoteName: String = ""
        var emoteID: LLUUID = LLUUID.NULL

        val numJointMotions: Int get() = jointMotions.size
        fun getJointMotion(index: Int): JointMotion = jointMotions[index]
    }

    var jointMotionList: JointMotionList? = null
    val jointStates: MutableList<JointState> = mutableListOf()
    var lastUpdateTime: Float = 0f
    var lastLoopedTime: Float = 0f
    var assetStatus: AssetStatus = AssetStatus.UNDEFINED

    fun getID(): LLUUID = id
    fun getDuration(): Float = jointMotionList?.duration ?: 0f
    fun getEaseInDuration(): Float = jointMotionList?.easeInDuration ?: 0f
    fun getEaseOutDuration(): Float = jointMotionList?.easeOutDuration ?: 0f
    fun getLoop(): Boolean = jointMotionList?.loop ?: false
    fun getPriority(): JointPriority = jointMotionList?.basePriority ?: JointPriority.LOW
    fun getNumJointMotions(): Int = jointMotionList?.numJointMotions ?: 0
    fun getBlendType(): BlendType = BlendType.NORMAL
    fun getMinPixelArea(): Float = MIN_REQUIRED_PIXEL_AREA_KEYFRAME
    fun isLoaded(): Boolean = jointMotionList != null

    fun getLoopIn(): Float = jointMotionList?.loopInPoint ?: 0f
    fun getLoopOut(): Float = jointMotionList?.loopOutPoint ?: 0f

    fun setLoop(loop: Boolean) { jointMotionList?.loop = loop }
    fun setLoopIn(inPoint: Float) { jointMotionList?.loopInPoint = inPoint }
    fun setLoopOut(outPoint: Float) { jointMotionList?.loopOutPoint = outPoint }
    fun setEaseIn(easeIn: Float) { jointMotionList?.easeInDuration = easeIn }
    fun setEaseOut(easeOut: Float) { jointMotionList?.easeOutDuration = easeOut }
    fun setPriority(priority: Int) { jointMotionList?.basePriority = JointPriority.fromInt(priority) }
    fun setEmote(emoteId: LLUUID) { jointMotionList?.emoteID = emoteId }

    companion object {
        private val dataCache: MutableMap<LLUUID, JointMotionList> = mutableMapOf()

        fun create(id: LLUUID): KeyframeMotion = KeyframeMotion(id)

        fun getKeyframeData(id: LLUUID): JointMotionList? = dataCache[id]
        fun addKeyframeData(id: LLUUID, list: JointMotionList) { dataCache[id] = list }
        fun removeKeyframeData(id: LLUUID) { dataCache.remove(id) }
        fun flushKeyframeCache() { dataCache.clear() }
    }
}

private typealias TreeMap<K, V> = java.util.TreeMap<K, V>
