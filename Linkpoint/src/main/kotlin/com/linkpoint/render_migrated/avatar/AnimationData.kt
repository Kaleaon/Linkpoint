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
    private const val Float LL_MAX_PELVIS_OFFSET = 5.0f
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
        private val AnimationPosKeyframe[] posKeyframes
        private val AnimationRotKeyframe[] rotKeyframes

        AnimationJointData(LittleEndianDataInputStream littleEndianDataInputStream, Float f) throws IOException {
            this.Priority = littleEndianDataInputStream.readInt()
            Int readInt = littleEndianDataInputStream.readInt()
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
                        Int i2 = i - 1
                        if (i2 < 0) {
                            i2 = 0
                        }
                        if (i2 == i) {
                            animationKeyframeArr[i].setTransform(t)
                        } else {
                            Float f3 = animationKeyframeArr[i].time
                            Float f4 = animationKeyframeArr[i2].time
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
private Float uint16ToFloat(Int i, Float f, Float f2) {
            Float f3 = f2 - f
            Float f4 = ((((Float) i) * 1.5259022E-5f) * f3) + f
            return Math.abs(f4) < f3 * 1.5259022E-5f ? 0.0f : f4
        }

        Unit animate(SLSkeletonBone sLSkeletonBone, Float f, Float f2, LLQuaternion[] lLQuaternionArr, LLVector3[] lLVector3Arr, Int i, Float f3, Float[] fArr, Float[] fArr2, LLQuaternion lLQuaternion, LLVector3 lLVector3) {
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

        public String toString() {
            Int i = 0
            StringBuilder stringBuilder = StringBuilder()
            stringBuilder.append("Priority ").append(this.Priority)
            stringBuilder.append(", pos frames ").append(this.posKeyframes.length).append("[")
            for (AnimationPosKeyframe animationPosKeyframe : this.posKeyframes) {
                stringBuilder.append(animationPosKeyframe.toString())
            }
            stringBuilder.append("], rot frames ").append(this.rotKeyframes.length).append("[")
            AnimationRotKeyframe[] animationRotKeyframeArr = this.rotKeyframes
            Int length = animationRotKeyframeArr.length
            while (i < length) {
                stringBuilder.append(animationRotKeyframeArr[i].toString())
                i++
            }
            stringBuilder.append("]")
            return stringBuilder.toString()
        }
    }

    static class AnimationJointSet {
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

        Unit addJointData(Int i, AnimationJointData animationJointData) {
            this.jointAnims.put(i, animationJointData)
        }

        Unit animate(AvatarSkeleton avatarSkeleton, AnimationTiming animationTiming, Float[] fArr, Float[] fArr2, LLQuaternion[] lLQuaternionArr, LLVector3[] lLVector3Arr) {
            Float f = animationTiming.inAnimationTime
            Float f2 = animationTiming.inFactor * animationTiming.outFactor
            if (f2 > 0.0f) {
                LLQuaternion lLQuaternion = LLQuaternion()
                LLVector3 lLVector3 = LLVector3()
                Int size = this.jointAnims.size()
                for (Int i = 0; i < size; i++) {
                    Int keyAt = this.jointAnims.keyAt(i)
                    ((AnimationJointData) this.jointAnims.valueAt(i)).animate(avatarSkeleton.getAnimatedBone(keyAt), this.animLength, f, lLQuaternionArr, lLVector3Arr, keyAt, f2, fArr, fArr2, lLQuaternion, lLVector3)
                }
            }
        }

        Unit dumpJoints() {
            Debug.Printf("Anim -- joint set -- length %f prio %d joints %d", Float.valueOf(this.animLength), Integer.valueOf(this.priority), Integer.valueOf(this.jointAnims.size()))
            Int size = this.jointAnims.size()
            for (Int i = 0; i < size; i++) {
                Debug.Printf("Anim -- joint[%d] - jointIndex %d, %s", Integer.valueOf(i), Integer.valueOf(this.jointAnims.keyAt(i)), ((AnimationJointData) this.jointAnims.valueAt(i)).toString())
            }
        }

        public Int getPriority() {
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

        protected LLVector3 getTransform() {
            return this.position
        }

        public Unit setInterpolated(LLVector3 lLVector3, Float f, AnimationKeyframe<LLVector3> animationKeyframe, Float f2) {
            lLVector3.setLerp(this.position, f, (LLVector3) animationKeyframe.getTransform(), f2)
        }

        public Unit setTransform(LLVector3 lLVector3) {
            lLVector3.set(this.position)
        }

        public String toString() {
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

        protected LLQuaternion getTransform() {
            return this.quaternion
        }

        public Unit setInterpolated(LLQuaternion lLQuaternion, Float f, AnimationKeyframe<LLQuaternion> animationKeyframe, Float f2) {
            lLQuaternion.setLerp(this.quaternion, f, (LLQuaternion) animationKeyframe.getTransform(), f2)
        }

        public Unit setTransform(LLQuaternion lLQuaternion) {
            lLQuaternion.set(this.quaternion)
        }

        public String toString() {
            return this.quaternion.toString()
        }
    }

    public AnimationData(UUID uuid, InputStream inputStream) throws IOException {
        Int i = 0
        this.animationUUID = uuid
        LittleEndianDataInputStream littleEndianDataInputStream = LittleEndianDataInputStream(inputStream)
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
        Int readInt = littleEndianDataInputStream.readInt()
        while (i < readInt) {
            SLSkeletonBoneID sLSkeletonBoneID = (SLSkeletonBoneID) SLSkeletonBoneID.bones.get(littleEndianDataInputStream.readZeroTerminatedString())
            AnimationJointData animationJointData = AnimationJointData(littleEndianDataInputStream, this.animLength)
            if (sLSkeletonBoneID != null) {
                Int i2 = sLSkeletonBoneID.animatedIndex
                if (i2 >= 0) {
                    AnimationJointSet animationJointSet = (AnimationJointSet) this.jointSets.get(animationJointData.Priority)
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
private Float cubicStep(Float f) {
        Float max = Math.max(0.0f, Math.min(1.0f, f))
        return (3.0f - (max * 2.0f)) * (max * max)
    }

    private Float getInAnimationTime(Float f, Float f2) {
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

    private Float getInFactor(Float f) {
        if (f >= this.easeInTime || this.easeInTime < 0.001f) {
            return 1.0f
        }
        Float cubicStep = cubicStep(f / this.easeInTime)
        return cubicStep > 1.0f ? 1.0f : cubicStep
    }

    private Float getOutFactor(Float f) {
        Float f2 = 1.0f
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

    private Float getOutFactor(Float f, Float f2) {
        Float f3
        if (f2 >= 0.0f) {
            if (!this.loop) {
                Float f4 = f - (this.animLength - this.easeOutTime)
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
        Builder builder = ImmutableList.builder()
        for (Int i = 0; i < r2; i++) {
            builder.add(AvatarRunningAnimation(avatarRunningSequence, (AnimationJointSet) this.jointSets.valueAt(i)))
        }
        return builder.build()
    }

    public Unit dumpAnimationData() {
        Debug.Printf("Animation -- dump -- priority %d length %f joint sets %d (inPoint %f outPoint %f loop %b easeIn %f easeOut %f)", Integer.valueOf(this.animPriority), Float.valueOf(this.animLength), Integer.valueOf(this.jointSets.size()), Float.valueOf(this.inPoint), Float.valueOf(this.outPoint), Boolean.valueOf(this.loop), Float.valueOf(this.easeInTime), Float.valueOf(this.easeOutTime))
        for (Int i = 0; i < this.jointSets.size(); i++) {
            Int keyAt = this.jointSets.keyAt(i)
            Debug.Printf("Anim -- joint set %d: prio %d", Integer.valueOf(i), Integer.valueOf(keyAt))
            ((AnimationJointSet) this.jointSets.valueAt(i)).dumpJoints()
        }
        Debug.Printf("Animation -- dump end", Object[0])
    }

    public Int getPriority() {
        return this.animPriority
    }

    Boolean updateAnimationTiming(Long j, Long j2, Long j3, Boolean z, AnimationTiming animationTiming) {
        Float f = ((Float) (j - j2)) / 1000.0f
        Float f2 = (j3 == -1 || j < j3) ? -1.0f : ((Float) (j - j3)) / 1000.0f
        Float inAnimationTime = getInAnimationTime(f, f2)
        Float inFactor = getInFactor(f)
        Float outFactor = getOutFactor(f, f2)
        f2 = z ? 1.0f : inFactor
        Boolean z2 = false
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
