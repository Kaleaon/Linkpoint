package com.linkpoint.render.avatar

import android.util.SparseArray
import com.google.common.collect.ImmutableList
import com.google.common.collect.ImmutableList.Builder
import com.google.common.logging.nano.Vr.VREvent.EventType
import com.linkpoint.Debug
import com.linkpoint.slproto.avatar.SLSkeletonBone
import com.linkpoint.slproto.avatar.SLSkeletonBoneID
import com.linkpoint.slproto.types.LLQuaternion
import com.linkpoint.slproto.types.LLVector3
import com.linkpoint.utils.LittleEndianDataInputStream
import java.io.IOException
import java.io.InputStream
import java.util.UUID

class AnimationData {
    private const val LL_MAX_PELVIS_OFFSET: Float = 5.0f
    private val Float animLength
    private val Int animPriority
    private val UUID animationUUID
    private val Float easeInTime
    private val Float easeOutTime
    private val String expressionName
    private val Int handPose
    private val Float inPoint
    private val SparseArray<AnimationJointSet> jointSets = SparseArray()
    private val Boolean loop
    private val Float outPoint

    @JvmStatic
private class AnimationJointData {
        private val Int Priority
        private val Array<AnimationPosKeyframe> posKeyframes
        private val Array<AnimationRotKeyframe> rotKeyframes

        AnimationJointData(LittleEndianDataInputStream littleEndianDataInputStream, Float f) throws IOException {
            this.Priority = littleEndianDataInputStream.readInt()
            val readInt: Int = littleEndianDataInputStream.readInt()
            if (readInt < 0 || readInt > EventType.STREET_VIEW_COLLECTION) {
                readInt = 0
            }
            this.rotKeyframes = AnimationRotKeyframe[readInt]
            for (i = 0; i < readInt; i++) {
                this.rotKeyframes[i] = AnimationRotKeyframe(uint16ToFloat(littleEndianDataInputStream.readUnsignedShort(), 0.0f, f), LLQuaternion.unpackFromVector3(LLVector3(uint16ToFloat(littleEndianDataInputStream.readUnsignedShort(), -1.0f, 1.0f), uint16ToFloat(littleEndianDataInputStream.readUnsignedShort(), -1.0f, 1.0f), uint16ToFloat(littleEndianDataInputStream.readUnsignedShort(), -1.0f, 1.0f))))
            }
            readInt = littleEndianDataInputStream.readInt()
            if (readInt < 0 || readInt > EventType.STREET_VIEW_COLLECTION) {
                readInt = 0
            }
            this.posKeyframes = AnimationPosKeyframe[readInt]
            for (i = 0; i < readInt; i++) {
                this.posKeyframes[i] = AnimationPosKeyframe(uint16ToFloat(littleEndianDataInputStream.readUnsignedShort(), 0.0f, f), LLVector3(uint16ToFloat(littleEndianDataInputStream.readUnsignedShort(), -5.0f, AnimationData.LL_MAX_PELVIS_OFFSET), uint16ToFloat(littleEndianDataInputStream.readUnsignedShort(), -5.0f, AnimationData.LL_MAX_PELVIS_OFFSET), uint16ToFloat(littleEndianDataInputStream.readUnsignedShort(), -5.0f, AnimationData.LL_MAX_PELVIS_OFFSET)))
            }
        }

        @JvmStatic
private <T> Boolean animateArray(Float f, Float f2, T t, AnimationKeyframe<T>[] animationKeyframeArr) {
            if (animationKeyframeArr.length == 1) {
                animationKeyframeArr[0].setTransform(t)
                return true
            }
            for (Int i = 0; i < animationKeyframeArr.length; i++) {
                if (f2 <= animationKeyframeArr[i].time) {
                    if (f2 == animationKeyframeArr[i].time) {
                        animationKeyframeArr[i].setTransform(t)
                    } else {
                        val i2: Int = i - 1
                        if (i2 < 0) {
                            i2 = 0
                        }
                        if (i2 == i) {
                            animationKeyframeArr[i].setTransform(t)
                        } else {
                            val f3: Float = animationKeyframeArr[i].time
                            val f4: Float = animationKeyframeArr[i2].time
                            if (f4 > f3) {
                                f4 -= f
                            }
                            if (f4 == f3) {
                                animationKeyframeArr[i].setTransform(t)
                            } else {
                                animationKeyframeArr[i2].setInterpolated(t, (f3 - f2) / (f3 - f4), animationKeyframeArr[i], (f2 - f4) / (f3 - f4))
                            }
                        }
                    }
                    return true
                }
            }
            return false
        }

        @JvmStatic
 private fun uint16ToFloat(i: Int, f: Float, f2: Float): Float {
            val f3: Float = f2 - f
            val f4: Float = ((((Float) i) * 1.5259022E-5f) * f3) + f
            return Math.abs(f4) < f3 * 1.5259022E-5f ? 0.0f : f4
        }

         fun animate(sLSkeletonBone: SLSkeletonBone, f: Float, f2: Float, lLQuaternionArr: Array<LLQuaternion>, lLVector3Arr: Array<LLVector3>, i: Int, f3: Float, fArr: FloatArray, fArr2: FloatArray, lLQuaternion: LLQuaternion, lLVector3: LLVector3) {
            Float f4
            if (this.posKeyframes.length != 0) {
                f4 = fArr2[i] * f3
                animateArray(f, f2, lLVector3, this.posKeyframes)
                if (!(sLSkeletonBone == null || sLSkeletonBone.boneID == SLSkeletonBoneID.mPelvis)) {
                    lLVector3.sub(sLSkeletonBone.getBasePosition())
                }
                lLVector3Arr[i].addMul(lLVector3, f4)
                fArr2[i] = fArr2[i] - f4
            }
            if (this.rotKeyframes.length != 0) {
                f4 = fArr[i] * f3
                animateArray(f, f2, lLQuaternion, this.rotKeyframes)
                lLQuaternionArr[i].addMul(lLQuaternion, f4)
                fArr[i] = fArr[i] - f4
            }
        }

         public override fun toString(): String {
            val i: Int = 0
            val stringBuilder: StringBuilder = StringBuilder()
            stringBuilder.append("Priority ").append(this.Priority)
            stringBuilder.append(", pos frames ").append(this.posKeyframes.length).append("[")
            for (AnimationPosKeyframe animationPosKeyframe : this.posKeyframes) {
                stringBuilder.append(animationPosKeyframe.toString())
            }
            stringBuilder.append("], rot frames ").append(this.rotKeyframes.length).append("[")
            val animationRotKeyframeArr: Array<AnimationRotKeyframe> = this.rotKeyframes
            val length: Int = animationRotKeyframeArr.length
            while (i < length) {
                stringBuilder.append(animationRotKeyframeArr[i].toString())
                i++
            }
            stringBuilder.append("]")
            return stringBuilder.toString()
        }
    }

    class AnimationJointSet {
        private val Float animLength
        private val UUID animationUUID
        private val SparseArray<AnimationJointData> jointAnims
        private val Int priority

        private AnimationJointSet(UUID uuid, Float f, Int i) {
            this.jointAnims = SparseArray()
            this.animationUUID = uuid
            this.animLength = f
            this.priority = i
        }

        /* synthetic */ AnimationJointSet(UUID uuid, Float f, Int i, AnimationJointSet animationJointSet) {
            this(uuid, f, i)
        }

         fun addJointData(i: Int, animationJointData: AnimationJointData) {
            this.jointAnims.put(i, animationJointData)
        }

         fun animate(avatarSkeleton: AvatarSkeleton, animationTiming: AnimationTiming, fArr: FloatArray, fArr2: FloatArray, lLQuaternionArr: Array<LLQuaternion>, lLVector3Arr: Array<LLVector3>) {
            val f: Float = animationTiming.inAnimationTime
            val f2: Float = animationTiming.inFactor * animationTiming.outFactor
            if (f2 > 0.0f) {
                val lLQuaternion: LLQuaternion = LLQuaternion()
                val lLVector3: LLVector3 = LLVector3()
                val size: Int = this.jointAnims.size()
                for (Int i = 0; i < size; i++) {
                    val keyAt: Int = this.jointAnims.keyAt(i)
                    ((AnimationJointData) this.jointAnims.valueAt(i)).animate(avatarSkeleton.getAnimatedBone(keyAt), this.animLength, f, lLQuaternionArr, lLVector3Arr, keyAt, f2, fArr, fArr2, lLQuaternion, lLVector3)
                }
            }
        }

         fun dumpJoints() {
            Debug.Printf("Anim -- joint set -- length %f prio %d joints %d", Float.valueOf(this.animLength), Integer.valueOf(this.priority), Integer.valueOf(this.jointAnims.size()))
            val size: Int = this.jointAnims.size()
            for (Int i = 0; i < size; i++) {
                Debug.Printf("Anim -- joint[%d] - jointIndex %d, %s", Integer.valueOf(i), Integer.valueOf(this.jointAnims.keyAt(i)), ((AnimationJointData) this.jointAnims.valueAt(i)).toString())
            }
        }

         public fun getPriority(): Int {
            return this.priority
        }
    }

    @JvmStatic
private abstract class AnimationKeyframe<T> {
        val Float time

        private AnimationKeyframe(Float f) {
            this.time = f
        }

        /* synthetic */ AnimationKeyframe(Float f, AnimationKeyframe animationKeyframe) {
            this(f)
        }

        protected abstract T getTransform()

        public abstract Unit setInterpolated(T t, Float f, AnimationKeyframe<T> animationKeyframe, Float f2)

        public abstract Unit setTransform(T t)
    }

    @JvmStatic
private class AnimationPosKeyframe : AnimationKeyframe()<LLVector3> {
        private val LLVector3 position

        AnimationPosKeyframe(Float f, LLVector3 lLVector3) {
            super(f, null)
            this.position = lLVector3
        }

         protected fun getTransform(): LLVector3 {
            return this.position
        }

        fun setInterpolated(lLVector3: LLVector3, f: Float, animationKeyframe: AnimationKeyframe<LLVector3>, f2: Float) {
            lLVector3.setLerp(this.position, f, (LLVector3) animationKeyframe.getTransform(), f2)
        }

        fun setTransform(lLVector3: LLVector3) {
            lLVector3.set(this.position)
        }

         public override fun toString(): String {
            return this.position.toString()
        }
    }

    @JvmStatic
private class AnimationRotKeyframe : AnimationKeyframe()<LLQuaternion> {
        private val LLQuaternion quaternion

        AnimationRotKeyframe(Float f, LLQuaternion lLQuaternion) {
            super(f, null)
            this.quaternion = lLQuaternion
        }

         protected fun getTransform(): LLQuaternion {
            return this.quaternion
        }

        fun setInterpolated(lLQuaternion: LLQuaternion, f: Float, animationKeyframe: AnimationKeyframe<LLQuaternion>, f2: Float) {
            lLQuaternion.setLerp(this.quaternion, f, (LLQuaternion) animationKeyframe.getTransform(), f2)
        }

        fun setTransform(lLQuaternion: LLQuaternion) {
            lLQuaternion.set(this.quaternion)
        }

         public override fun toString(): String {
            return this.quaternion.toString()
        }
    }

    public AnimationData(UUID uuid, InputStream inputStream) throws IOException {
        val i: Int = 0
        this.animationUUID = uuid
        val littleEndianDataInputStream: LittleEndianDataInputStream = LittleEndianDataInputStream(inputStream)
        littleEndianDataInputStream.skipBytes(4)
        this.animPriority = littleEndianDataInputStream.readInt()
        this.animLength = littleEndianDataInputStream.readFloat()
        this.expressionName = littleEndianDataInputStream.readZeroTerminatedString()
        this.inPoint = littleEndianDataInputStream.readFloat()
        this.outPoint = littleEndianDataInputStream.readFloat()
        this.loop = littleEndianDataInputStream.readInt() != 0
        this.easeInTime = littleEndianDataInputStream.readFloat()
        this.easeOutTime = littleEndianDataInputStream.readFloat()
        this.handPose = littleEndianDataInputStream.readInt()
        val readInt: Int = littleEndianDataInputStream.readInt()
        while (i < readInt) {
            val sLSkeletonBoneID: SLSkeletonBoneID = (SLSkeletonBoneID) SLSkeletonBoneID.bones.get(littleEndianDataInputStream.readZeroTerminatedString())
            val animationJointData: AnimationJointData = AnimationJointData(littleEndianDataInputStream, this.animLength)
            if (sLSkeletonBoneID != null) {
                val i2: Int = sLSkeletonBoneID.animatedIndex
                if (i2 >= 0) {
                    val animationJointSet: AnimationJointSet = (AnimationJointSet) this.jointSets.get(animationJointData.Priority)
                    if (animationJointSet == null) {
                        animationJointSet = AnimationJointSet(uuid, this.animLength, animationJointData.Priority, null)
                        this.jointSets.put(animationJointData.Priority, animationJointSet)
                    }
                    animationJointSet.addJointData(i2, animationJointData)
                }
            }
            i++
        }
    }

    @JvmStatic
 private fun cubicStep(f: Float): Float {
        val max: Float = Math.max(0.0f, Math.min(1.0f, f))
        return (3.0f - (max * 2.0f)) * (max * max)
    }

     private fun getInAnimationTime(f: Float, f2: Float): Float {
        if (!this.loop) {
            return Math.min(f, this.animLength)
        }
        if (f < this.inPoint) {
            return f
        }
        if (f2 < 0.0f) {
            return this.outPoint > this.inPoint ? this.inPoint + ((f - this.inPoint) % (this.outPoint - this.inPoint)) : this.inPoint
        } else {
            Float f3
            if (this.outPoint > this.inPoint) {
                f3 = f - f2
                f3 = (f3 - (((Float) Math.floor((Double) ((f3 - this.inPoint) / (this.outPoint - this.inPoint)))) * (this.outPoint - this.inPoint))) + f2
            } else {
                f3 = this.outPoint + f2
            }
            return Math.min(f3, this.animLength)
        }
    }

     private fun getInFactor(f: Float): Float {
        if (f >= this.easeInTime || this.easeInTime < 0.001f) {
            return 1.0f
        }
        val cubicStep: Float = cubicStep(f / this.easeInTime)
        return cubicStep > 1.0f ? 1.0f : cubicStep
    }

     private fun getOutFactor(f: Float): Float {
        val f2: Float = 1.0f
        if (f >= 0.0f) {
            if (this.easeOutTime < 0.001f) {
                return 0.0f
            }
            f2 = cubicStep(1.0f - (f / this.easeOutTime))
            if (f2 < 0.0f) {
                return 0.0f
            }
        }
        return f2
    }

     private fun getOutFactor(f: Float, f2: Float): Float {
        Float f3
        if (f2 >= 0.0f) {
            if (!this.loop) {
                val f4: Float = f - (this.animLength - this.easeOutTime)
                if (f4 > 0.0f) {
                    f2 = Math.max(f2, f4)
                }
                return getOutFactor(f2)
            } else if (this.outPoint >= this.animLength) {
                return getOutFactor(f2)
            } else {
                f3 = f - f2
                return getOutFactor(f - Math.max((this.outPoint > this.inPoint ? ((((Float) Math.floor((Double) ((f3 - this.inPoint) / (this.outPoint - this.inPoint)))) * (this.outPoint - this.inPoint)) + this.inPoint) + this.animLength : this.animLength + f3) - this.easeOutTime, f3))
            }
        } else if (this.loop) {
            return 1.0f
        } else {
            f3 = f - (this.animLength - this.easeOutTime)
            return f3 >= 0.0f ? getOutFactor(f3) : 1.0f
        }
    }

    ImmutableList<AvatarRunningAnimation> createRunningAnimations(AvatarRunningSequence avatarRunningSequence) {
        Debug.Printf("Animation: creating anims: %d anims", Integer.valueOf(this.jointSets.size()))
        val builder: Builder = ImmutableList.builder()
        for (Int i = 0; i < r2; i++) {
            builder.add(AvatarRunningAnimation(avatarRunningSequence, (AnimationJointSet) this.jointSets.valueAt(i)))
        }
        return builder.build()
    }

    fun dumpAnimationData() {
        Debug.Printf("Animation -- dump -- priority %d length %f joint sets %d (inPoint %f outPoint %f loop %b easeIn %f easeOut %f)", Integer.valueOf(this.animPriority), Float.valueOf(this.animLength), Integer.valueOf(this.jointSets.size()), Float.valueOf(this.inPoint), Float.valueOf(this.outPoint), Boolean.valueOf(this.loop), Float.valueOf(this.easeInTime), Float.valueOf(this.easeOutTime))
        for (Int i = 0; i < this.jointSets.size(); i++) {
            val keyAt: Int = this.jointSets.keyAt(i)
            Debug.Printf("Anim -- joint set %d: prio %d", Integer.valueOf(i), Integer.valueOf(keyAt))
            ((AnimationJointSet) this.jointSets.valueAt(i)).dumpJoints()
        }
        Debug.Printf("Animation -- dump end", Object[0])
    }

     public fun getPriority(): Int {
        return this.animPriority
    }

     fun updateAnimationTiming(j: Long, j2: Long, j3: Long, z: Boolean, animationTiming: AnimationTiming): Boolean {
        val f: Float = ((Float) (j - j2)) / 1000.0f
        val f2: Float = (j3 == -1 || j < j3) ? -1.0f : ((Float) (j - j3)) / 1000.0f
        val inAnimationTime: Float = getInAnimationTime(f, f2)
        val inFactor: Float = getInFactor(f)
        val outFactor: Float = getOutFactor(f, f2)
        f2 = z ? 1.0f : inFactor
        val z2: Boolean = false
        animationTiming.runningTime = f
        if (animationTiming.inAnimationTime != inAnimationTime) {
            animationTiming.inAnimationTime = inAnimationTime
            z2 = true
        }
        if (animationTiming.inFactor != f2) {
            animationTiming.inFactor = f2
            z2 = true
        }
        if (animationTiming.outFactor == outFactor) {
            return z2
        }
        animationTiming.outFactor = outFactor
        return true
    }
}
