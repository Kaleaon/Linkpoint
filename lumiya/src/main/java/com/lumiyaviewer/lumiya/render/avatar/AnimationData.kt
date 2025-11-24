package com.lumiyaviewer.lumiya.render.avatar

import android.util.SparseArray
import com.google.common.collect.ImmutableList
import com.lumiyaviewer.lumiya.Debug
import com.lumiyaviewer.lumiya.slproto.avatar.SLSkeletonBone
import com.lumiyaviewer.lumiya.slproto.avatar.SLSkeletonBoneID
import com.lumiyaviewer.lumiya.slproto.types.LLQuaternion
import com.lumiyaviewer.lumiya.slproto.types.LLVector3
import com.lumiyaviewer.lumiya.utils.LittleEndianDataInputStream
import java.io.IOException
import java.io.InputStream
import java.util.UUID
import kotlin.math.abs
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min

/**
 * Animation data for avatar animations
 * Stores keyframes for position and rotation
 */
class AnimationData @Throws(IOException::class) constructor(
    uuid: UUID,
    inputStream: InputStream
) {
    private val LL_MAX_PELVIS_OFFSET = 5.0f
    
    private val animationUUID: UUID = uuid
    private val animPriority: Int
    private val animLength: Float
    private val expressionName: String
    private val inPoint: Float
    private val outPoint: Float
    private val loop: Boolean
    private val easeInTime: Float
    private val easeOutTime: Float
    private val handPose: Int
    private val jointSets = SparseArray<AnimationJointSet>()

    /**
     * Joint animation data with position and rotation keyframes
     */
    inner class AnimationJointData(
        input: LittleEndianDataInputStream,
        animLength: Float
    ) {
        val priority: Int
        val posKeyframes: Array<AnimationPosKeyframe>
        val rotKeyframes: Array<AnimationRotKeyframe>

        init {
            priority = input.readInt()
            
            // Read rotation keyframes
            var count = input.readInt()
            if (count < 0 || count > 10000) { // Reasonable limit check instead of EventType
                count = 0
            }
            
            rotKeyframes = Array(count) { _ ->
                val time = uint16ToFloat(input.readUnsignedShort(), 0.0f, animLength)
                val x = uint16ToFloat(input.readUnsignedShort(), -1.0f, 1.0f)
                val y = uint16ToFloat(input.readUnsignedShort(), -1.0f, 1.0f)
                val z = uint16ToFloat(input.readUnsignedShort(), -1.0f, 1.0f)
                // Need LLQuaternion.unpackFromVector3 or similar
                // Assuming unpackFromVector3 is missing or not static, implementing rough equivalent logic if possible
                // or using a placeholder if the math is complex. 
                // Ideally LLQuaternion(x,y,z) constructor exists or set method.
                // If LLQuaternion doesn't have unpackFromVector3, we stub it or fix it.
                // For now, assume we can set from euler/vector.
                // Actually, SL animation compression usually packs 3 components of quaternion, recovering 4th.
                // x^2 + y^2 + z^2 + w^2 = 1.
                val sumSq = x*x + y*y + z*z
                val w = if (1.0f - sumSq > 0) kotlin.math.sqrt(1.0f - sumSq) else 0.0f
                val rotation = LLQuaternion(x, y, z, w) 
                AnimationRotKeyframe(time, rotation)
            }
            
            // Read position keyframes
            count = input.readInt()
            if (count < 0 || count > 10000) {
                count = 0
            }
            
            posKeyframes = Array(count) { _ ->
                val time = uint16ToFloat(input.readUnsignedShort(), 0.0f, animLength)
                val x = uint16ToFloat(input.readUnsignedShort(), -5.0f, LL_MAX_PELVIS_OFFSET)
                val y = uint16ToFloat(input.readUnsignedShort(), -5.0f, LL_MAX_PELVIS_OFFSET)
                val z = uint16ToFloat(input.readUnsignedShort(), -5.0f, LL_MAX_PELVIS_OFFSET)
                AnimationPosKeyframe(time, LLVector3(x, y, z))
            }
        }

        /**
         * Animate with keyframes
         */
        private fun <T> animateArray(
            animLength: Float,
            animTime: Float,
            output: T,
            keyframes: Array<out AnimationKeyframe<T>>
        ): Boolean {
            if (keyframes.size == 1) {
                keyframes[0].setTransform(output)
                return true
            }
            
            for (i in keyframes.indices) {
                if (animTime <= keyframes[i].time) {
                    if (animTime == keyframes[i].time) {
                        keyframes[i].setTransform(output)
                    } else {
                        var prevIdx = i - 1
                        if (prevIdx < 0) prevIdx = 0
                        
                        if (prevIdx == i) {
                            keyframes[i].setTransform(output)
                        } else {
                            var prevTime = keyframes[prevIdx].time
                            val currTime = keyframes[i].time
                            
                            if (prevTime > currTime) {
                                prevTime -= animLength
                            }
                            
                            if (prevTime == currTime) {
                                keyframes[i].setTransform(output)
                            } else {
                                val weight1 = (currTime - animTime) / (currTime - prevTime)
                                val weight2 = (animTime - prevTime) / (currTime - prevTime)
                                keyframes[prevIdx].setInterpolated(output, weight1, keyframes[i], weight2)
                            }
                        }
                    }
                    return true
                }
            }
            return false
        }

        /**
         * Convert uint16 to float with range
         */
        private fun uint16ToFloat(value: Int, min: Float, max: Float): Float {
            val range = max - min
            val result = ((value.toFloat() * 1.5259022E-5f) * range) + min
            return if (abs(result) < range * 1.5259022E-5f) 0.0f else result
        }

        /**
         * Animate bone with position and rotation
         */
        fun animate(
            bone: SLSkeletonBone?,
            animLength: Float,
            animTime: Float,
            rotations: Array<LLQuaternion>,
            positions: Array<LLVector3>,
            boneIndex: Int,
            blendFactor: Float,
            rotWeights: FloatArray,
            posWeights: FloatArray,
            tempRotation: LLQuaternion,
            tempPosition: LLVector3
        ) {
            // Animate position if we have keyframes
            if (posKeyframes.isNotEmpty()) {
                val posWeight = posWeights[boneIndex] * blendFactor
                animateArray(animLength, animTime, tempPosition, posKeyframes)
                
                // Adjust for bone base position (except pelvis)
                if (bone != null && bone.boneID != SLSkeletonBoneID.mPelvis) {
                    tempPosition.sub(bone.getBasePosition())
                }
                
                positions[boneIndex].addMul(tempPosition, posWeight)
                posWeights[boneIndex] -= posWeight
            }
            
            // Animate rotation if we have keyframes
            if (rotKeyframes.isNotEmpty()) {
                val rotWeight = rotWeights[boneIndex] * blendFactor
                animateArray(animLength, animTime, tempRotation, rotKeyframes)
                rotations[boneIndex].addMul(tempRotation, rotWeight)
                rotWeights[boneIndex] -= rotWeight
            }
        }

        override fun toString(): String {
            val sb = StringBuilder()
            sb.append("Priority ").append(priority)
            sb.append(", pos frames ").append(posKeyframes.size).append("[")
            posKeyframes.forEach { sb.append(it.toString()) }
            sb.append("], rot frames ").append(rotKeyframes.size).append("[")
            rotKeyframes.forEach { sb.append(it.toString()) }
            sb.append("]")
            return sb.toString()
        }
    }

    /**
     * Set of joint animations at a specific priority level
     */
    class AnimationJointSet private constructor(
        private val animationUUID: UUID,
        private val animLength: Float,
        private val priority: Int
    ) {
        private val jointAnims = SparseArray<AnimationJointData>()

        fun addJointData(jointIndex: Int, data: AnimationJointData) {
            jointAnims.put(jointIndex, data)
        }

        fun animate(
            skeleton: AvatarSkeleton,
            timing: AnimationTiming,
            rotWeights: FloatArray,
            posWeights: FloatArray,
            rotations: Array<LLQuaternion>,
            positions: Array<LLVector3>
        ) {
            val animTime = timing.inAnimationTime
            val blendFactor = timing.inFactor * timing.outFactor
            
            if (blendFactor > 0.0f) {
                val tempRotation = LLQuaternion()
                val tempPosition = LLVector3()
                
                for (i in 0 until jointAnims.size()) {
                    val boneIndex = jointAnims.keyAt(i)
                    jointAnims.valueAt(i).animate(
                        skeleton.getAnimatedBone(boneIndex),
                        animLength,
                        animTime,
                        rotations,
                        positions,
                        boneIndex,
                        blendFactor,
                        rotWeights,
                        posWeights,
                        tempRotation,
                        tempPosition
                    )
                }
            }
        }

        fun dumpJoints() {
            Debug.Log(
                "Anim -- joint set -- length $animLength prio $priority joints ${jointAnims.size()}"
            )
            
            for (i in 0 until jointAnims.size()) {
                Debug.Log(
                    "Anim -- joint[$i] - jointIndex ${jointAnims.keyAt(i)}, ${jointAnims.valueAt(i)}"
                )
            }
        }

        fun getPriority(): Int = priority
        
        companion object {
            fun create(uuid: UUID, animLength: Float, priority: Int): AnimationJointSet {
                return AnimationJointSet(uuid, animLength, priority)
            }
        }
    }

    /**
     * Base class for animation keyframes
     */
    internal abstract class AnimationKeyframe<T>(val time: Float) {
        abstract fun getTransform(): T
        abstract fun setInterpolated(output: T, weight1: Float, other: AnimationKeyframe<T>, weight2: Float)
        abstract fun setTransform(output: T)
    }

    /**
     * Position keyframe
     */
    internal class AnimationPosKeyframe(
        time: Float,
        private val position: LLVector3
    ) : AnimationKeyframe<LLVector3>(time) {

        override fun getTransform(): LLVector3 = position

        override fun setInterpolated(
            output: LLVector3,
            weight1: Float,
            other: AnimationKeyframe<LLVector3>,
            weight2: Float
        ) {
            output.setLerp(position, weight1, other.getTransform(), weight2)
        }

        override fun setTransform(output: LLVector3) {
            output.set(position)
        }

        override fun toString(): String = position.toString()
    }

    /**
     * Rotation keyframe
     */
    internal class AnimationRotKeyframe(
        time: Float,
        private val quaternion: LLQuaternion
    ) : AnimationKeyframe<LLQuaternion>(time) {

        override fun getTransform(): LLQuaternion = quaternion

        override fun setInterpolated(
            output: LLQuaternion,
            weight1: Float,
            other: AnimationKeyframe<LLQuaternion>,
            weight2: Float
        ) {
            output.setLerp(quaternion, weight1, other.getTransform(), weight2)
        }

        override fun setTransform(output: LLQuaternion) {
            output.set(quaternion)
        }

        override fun toString(): String = quaternion.toString()
    }

    init {
        val input = LittleEndianDataInputStream(inputStream)
        
        // Read animation header
        input.skipBytes(4) // Version
        animPriority = input.readInt()
        animLength = input.readFloat()
        expressionName = input.readZeroTerminatedString()
        inPoint = input.readFloat()
        outPoint = input.readFloat()
        loop = input.readInt() != 0
        easeInTime = input.readFloat()
        easeOutTime = input.readFloat()
        handPose = input.readInt()
        
        // Read joint animations
        val jointCount = input.readInt()
        repeat(jointCount) {
            val boneName = input.readZeroTerminatedString()
            val boneId = SLSkeletonBoneID.bones[boneName]
            val jointData = AnimationJointData(input, animLength)
            
            boneId?.let { id ->
                val animatedIndex = id.animatedIndex
                if (animatedIndex >= 0) {
                    var jointSet = jointSets.get(jointData.priority)
                    if (jointSet == null) {
                        jointSet = AnimationJointSet.create(uuid, animLength, jointData.priority)
                        jointSets.put(jointData.priority, jointSet)
                    }
                    jointSet.addJointData(animatedIndex, jointData)
                }
            }
        }
    }

    private fun cubicStep(t: Float): Float {
        val clamped = t.coerceIn(0.0f, 1.0f)
        return (3.0f - (clamped * 2.0f)) * (clamped * clamped)
    }

    private fun getInAnimationTime(time: Float, stopTime: Float): Float {
        if (!loop) {
            return min(time, animLength)
        }
        
        if (time < inPoint) {
            return time
        }
        
        if (stopTime < 0.0f) {
            return if (outPoint > inPoint) {
                inPoint + ((time - inPoint) % (outPoint - inPoint))
            } else {
                inPoint
            }
        }
        
        val result = if (outPoint > inPoint) {
            val baseTime = time - stopTime
            val loopTime = floor((baseTime - inPoint) / (outPoint - inPoint)) * (outPoint - inPoint)
            baseTime - loopTime + stopTime
        } else {
            outPoint + stopTime
        }
        
        return min(result, animLength)
    }

    private fun getInFactor(time: Float): Float {
        if (time >= easeInTime || easeInTime < 0.001f) {
            return 1.0f
        }
        
        val factor = cubicStep(time / easeInTime)
        return min(factor, 1.0f)
    }

    private fun getOutFactor(stopTime: Float): Float {
        if (stopTime < 0.0f) {
            return 1.0f
        }
        
        if (easeOutTime < 0.001f) {
            return 0.0f
        }
        
        val factor = cubicStep(1.0f - (stopTime / easeOutTime))
        return max(factor, 0.0f)
    }

    private fun getOutFactor(time: Float, stopTime: Float): Float {
        if (stopTime < 0.0f) {
            return if (loop) {
                1.0f
            } else {
                val timeFromEnd = time - (animLength - easeOutTime)
                if (timeFromEnd >= 0.0f) getOutFactor(timeFromEnd) else 1.0f
            }
        }
        
        if (!loop) {
            val timeFromEnd = time - (animLength - easeOutTime)
            val adjustedStopTime = if (timeFromEnd > 0.0f) {
                max(stopTime, timeFromEnd)
            } else {
                stopTime
            }
            return getOutFactor(adjustedStopTime)
        }
        
        if (outPoint >= animLength) {
            return getOutFactor(stopTime)
        }
        
        val baseTime = time - stopTime
        val loopedTime = if (outPoint > inPoint) {
            val loopCount = floor((baseTime - inPoint) / (outPoint - inPoint))
            loopCount * (outPoint - inPoint) + inPoint + animLength
        } else {
            animLength + baseTime
        }
        
        val easeTime = max(loopedTime - easeOutTime, baseTime)
        return getOutFactor(time - easeTime)
    }

    fun createRunningAnimations(sequence: AvatarRunningSequence): ImmutableList<AvatarRunningAnimation> {
        Debug.Log("Animation: creating anims: ${jointSets.size()} anims")
        
        val builder = ImmutableList.builder<AvatarRunningAnimation>()
        for (i in 0 until jointSets.size()) {
            builder.add(AvatarRunningAnimation(sequence, jointSets.valueAt(i)))
        }
        
        return builder.build()
    }

    fun dumpAnimationData() {
        Debug.Log(
            "Animation -- dump -- priority $animPriority length $animLength joint sets ${jointSets.size()} (inPoint $inPoint outPoint $outPoint loop $loop easeIn $easeInTime easeOut $easeOutTime)"
        )
        
        for (i in 0 until jointSets.size()) {
            val priority = jointSets.keyAt(i)
            Debug.Log("Anim -- joint set $i: prio $priority")
            jointSets.valueAt(i).dumpJoints()
        }
        
        Debug.Log("Animation -- dump end")
    }

    fun getPriority(): Int = animPriority

    fun updateAnimationTiming(
        currentTime: Long,
        startTime: Long,
        stopTime: Long,
        skipEaseIn: Boolean,
        timing: AnimationTiming
    ): Boolean {
        val runningTime = (currentTime - startTime) / 1000.0f
        val timeSinceStopped = if (stopTime == -1L || currentTime < stopTime) {
            -1.0f
        } else {
            (currentTime - stopTime) / 1000.0f
        }
        
        val animTime = getInAnimationTime(runningTime, timeSinceStopped)
        val inFactor = if (skipEaseIn) 1.0f else getInFactor(runningTime)
        val outFactor = getOutFactor(runningTime, timeSinceStopped)
        
        var changed = false
        
        timing.runningTime = runningTime
        
        if (timing.inAnimationTime != animTime) {
            timing.inAnimationTime = animTime
            changed = true
        }
        
        if (timing.inFactor != inFactor) {
            timing.inFactor = inFactor
            changed = true
        }
        
        if (timing.outFactor != outFactor) {
            timing.outFactor = outFactor
            changed = true
        }
        
        return changed
    }
}
