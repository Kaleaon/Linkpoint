package com.lumiyaviewer.lumiya.render.avatar;

import android.util.SparseArray;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.logging.nano.Vr.VREvent.EventType;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.slproto.avatar.SLSkeletonBone;
import com.lumiyaviewer.lumiya.slproto.avatar.SLSkeletonBoneID;
import com.lumiyaviewer.lumiya.slproto.types.LLQuaternion;
import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import com.lumiyaviewer.lumiya.utils.LittleEndianDataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

public class AnimationData {
    private static final float LL_MAX_PELVIS_OFFSET = 5.0f;
    private final float animLength;
    private final int animPriority;
    private final UUID animationUUID;
    private final float easeInTime;
    private final float easeOutTime;
    private final String expressionName;
    private final int handPose;
    private final float inPoint;
    private final SparseArray<AnimationJointSet> jointSets = new SparseArray();
    private final boolean loop;
    private final float outPoint;

    private static class AnimationJointData {
        private final int Priority;
        private final AnimationPosKeyframe[] posKeyframes;
        private final AnimationRotKeyframe[] rotKeyframes;

        AnimationJointData(LittleEndianDataInputStream littleEndianDataInputStream, float f) throws IOException {
            int i;
            this.Priority = littleEndianDataInputStream.readInt();
            int readInt = littleEndianDataInputStream.readInt();
            if (readInt < 0 || readInt > EventType.STREET_VIEW_COLLECTION) {
                readInt = 0;
            }
            this.rotKeyframes = new AnimationRotKeyframe[readInt];
            for (i = 0; i < readInt; i++) {
                this.rotKeyframes[i] = new AnimationRotKeyframe(uint16ToFloat(littleEndianDataInputStream.readUnsignedShort(), 0.0f, f), LLQuaternion.unpackFromVector3(new LLVector3(uint16ToFloat(littleEndianDataInputStream.readUnsignedShort(), -1.0f, 1.0f), uint16ToFloat(littleEndianDataInputStream.readUnsignedShort(), -1.0f, 1.0f), uint16ToFloat(littleEndianDataInputStream.readUnsignedShort(), -1.0f, 1.0f))));
            }
            readInt = littleEndianDataInputStream.readInt();
            if (readInt < 0 || readInt > EventType.STREET_VIEW_COLLECTION) {
                readInt = 0;
            }
            this.posKeyframes = new AnimationPosKeyframe[readInt];
            for (i = 0; i < readInt; i++) {
                this.posKeyframes[i] = new AnimationPosKeyframe(uint16ToFloat(littleEndianDataInputStream.readUnsignedShort(), 0.0f, f), new LLVector3(uint16ToFloat(littleEndianDataInputStream.readUnsignedShort(), -5.0f, AnimationData.LL_MAX_PELVIS_OFFSET), uint16ToFloat(littleEndianDataInputStream.readUnsignedShort(), -5.0f, AnimationData.LL_MAX_PELVIS_OFFSET), uint16ToFloat(littleEndianDataInputStream.readUnsignedShort(), -5.0f, AnimationData.LL_MAX_PELVIS_OFFSET)));
            }
        }

        private static <T> boolean animateArray(float f, float f2, T t, AnimationKeyframe<T>[] animationKeyframeArr) {
            if (animationKeyframeArr.length == 1) {
                animationKeyframeArr[0].setTransform(t);
                return true;
            }
            for (int i = 0; i < animationKeyframeArr.length; i++) {
                if (f2 <= animationKeyframeArr[i].time) {
                    if (f2 == animationKeyframeArr[i].time) {
                        animationKeyframeArr[i].setTransform(t);
                    } else {
                        int i2 = i - 1;
                        if (i2 < 0) {
                            i2 = 0;
                        }
                        if (i2 == i) {
                            animationKeyframeArr[i].setTransform(t);
                        } else {
                            float f3 = animationKeyframeArr[i].time;
                            float f4 = animationKeyframeArr[i2].time;
                            if (f4 > f3) {
                                f4 -= f;
                            }
                            if (f4 == f3) {
                                animationKeyframeArr[i].setTransform(t);
                            } else {
                                animationKeyframeArr[i2].setInterpolated(t, (f3 - f2) / (f3 - f4), animationKeyframeArr[i], (f2 - f4) / (f3 - f4));
                            }
                        }
                    }
                    return true;
                }
            }
            return false;
        }

        private static float uint16ToFloat(int i, float f, float f2) {
            float f3 = f2 - f;
            float f4 = ((((float) i) * 1.5259022E-5f) * f3) + f;
            return Math.abs(f4) < f3 * 1.5259022E-5f ? 0.0f : f4;
        }

        void animate(SLSkeletonBone sLSkeletonBone, float f, float f2, LLQuaternion[] lLQuaternionArr, LLVector3[] lLVector3Arr, int i, float f3, float[] fArr, float[] fArr2, LLQuaternion lLQuaternion, LLVector3 lLVector3) {
            float f4;
            if (this.posKeyframes.length != 0) {
                f4 = fArr2[i] * f3;
                animateArray(f, f2, lLVector3, this.posKeyframes);
                if (!(sLSkeletonBone == null || sLSkeletonBone.boneID == SLSkeletonBoneID.mPelvis)) {
                    lLVector3.sub(sLSkeletonBone.getBasePosition());
                }
                lLVector3Arr[i].addMul(lLVector3, f4);
                fArr2[i] = fArr2[i] - f4;
            }
            if (this.rotKeyframes.length != 0) {
                f4 = fArr[i] * f3;
                animateArray(f, f2, lLQuaternion, this.rotKeyframes);
                lLQuaternionArr[i].addMul(lLQuaternion, f4);
                fArr[i] = fArr[i] - f4;
            }
        }

        public String toString() {
            int i = 0;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Priority ").append(this.Priority);
            stringBuilder.append(", pos frames ").append(this.posKeyframes.length).append("[");
            for (AnimationPosKeyframe animationPosKeyframe : this.posKeyframes) {
                stringBuilder.append(animationPosKeyframe.toString());
            }
            stringBuilder.append("], rot frames ").append(this.rotKeyframes.length).append("[");
            AnimationRotKeyframe[] animationRotKeyframeArr = this.rotKeyframes;
            int length = animationRotKeyframeArr.length;
            while (i < length) {
                stringBuilder.append(animationRotKeyframeArr[i].toString());
                i++;
            }
            stringBuilder.append("]");
            return stringBuilder.toString();
        }
    }

    static class AnimationJointSet {
        private final float animLength;
        private final UUID animationUUID;
        private final SparseArray<AnimationJointData> jointAnims;
        private final int priority;

        private AnimationJointSet(UUID uuid, float f, int i) {
            this.jointAnims = new SparseArray();
            this.animationUUID = uuid;
            this.animLength = f;
            this.priority = i;
        }

        /* synthetic */ AnimationJointSet(UUID uuid, float f, int i, AnimationJointSet animationJointSet) {
            this(uuid, f, i);
        }

        void addJointData(int i, AnimationJointData animationJointData) {
            this.jointAnims.put(i, animationJointData);
        }

        void animate(AvatarSkeleton avatarSkeleton, AnimationTiming animationTiming, float[] fArr, float[] fArr2, LLQuaternion[] lLQuaternionArr, LLVector3[] lLVector3Arr) {
            float f = animationTiming.inAnimationTime;
            float f2 = animationTiming.inFactor * animationTiming.outFactor;
            if (f2 > 0.0f) {
                LLQuaternion lLQuaternion = new LLQuaternion();
                LLVector3 lLVector3 = new LLVector3();
                int size = this.jointAnims.size();
                for (int i = 0; i < size; i++) {
                    int keyAt = this.jointAnims.keyAt(i);
                    ((AnimationJointData) this.jointAnims.valueAt(i)).animate(avatarSkeleton.getAnimatedBone(keyAt), this.animLength, f, lLQuaternionArr, lLVector3Arr, keyAt, f2, fArr, fArr2, lLQuaternion, lLVector3);
                }
            }
        }

        void dumpJoints() {
            Debug.Printf("Anim -- joint set -- length %f prio %d joints %d", Float.valueOf(this.animLength), Integer.valueOf(this.priority), Integer.valueOf(this.jointAnims.size()));
            int size = this.jointAnims.size();
            for (int i = 0; i < size; i++) {
                Debug.Printf("Anim -- joint[%d] - jointIndex %d, %s", Integer.valueOf(i), Integer.valueOf(this.jointAnims.keyAt(i)), ((AnimationJointData) this.jointAnims.valueAt(i)).toString());
            }
        }

        public int getPriority() {
            return this.priority;
        }
    }

    private static abstract class AnimationKeyframe<T> {
        public final float time;

        private AnimationKeyframe(float f) {
            this.time = f;
        }

        /* synthetic */ AnimationKeyframe(float f, AnimationKeyframe animationKeyframe) {
            this(f);
        }

        protected abstract T getTransform();

        public abstract void setInterpolated(T t, float f, AnimationKeyframe<T> animationKeyframe, float f2);

        public abstract void setTransform(T t);
    }

    private static class AnimationPosKeyframe extends AnimationKeyframe<LLVector3> {
        private final LLVector3 position;

        AnimationPosKeyframe(float f, LLVector3 lLVector3) {
            super(f, null);
            this.position = lLVector3;
        }

        protected LLVector3 getTransform() {
            return this.position;
        }

        public void setInterpolated(LLVector3 lLVector3, float f, AnimationKeyframe<LLVector3> animationKeyframe, float f2) {
            lLVector3.setLerp(this.position, f, (LLVector3) animationKeyframe.getTransform(), f2);
        }

        public void setTransform(LLVector3 lLVector3) {
            lLVector3.set(this.position);
        }

        public String toString() {
            return this.position.toString();
        }
    }

    private static class AnimationRotKeyframe extends AnimationKeyframe<LLQuaternion> {
        private final LLQuaternion quaternion;

        AnimationRotKeyframe(float f, LLQuaternion lLQuaternion) {
            super(f, null);
            this.quaternion = lLQuaternion;
        }

        protected LLQuaternion getTransform() {
            return this.quaternion;
        }

        public void setInterpolated(LLQuaternion lLQuaternion, float f, AnimationKeyframe<LLQuaternion> animationKeyframe, float f2) {
            lLQuaternion.setLerp(this.quaternion, f, (LLQuaternion) animationKeyframe.getTransform(), f2);
        }

        public void setTransform(LLQuaternion lLQuaternion) {
            lLQuaternion.set(this.quaternion);
        }

        public String toString() {
            return this.quaternion.toString();
        }
    }

    public AnimationData(UUID uuid, InputStream inputStream) throws IOException {
        int i = 0;
        this.animationUUID = uuid;
        LittleEndianDataInputStream littleEndianDataInputStream = new LittleEndianDataInputStream(inputStream);
        littleEndianDataInputStream.skipBytes(4);
        this.animPriority = littleEndianDataInputStream.readInt();
        this.animLength = littleEndianDataInputStream.readFloat();
        this.expressionName = littleEndianDataInputStream.readZeroTerminatedString();
        this.inPoint = littleEndianDataInputStream.readFloat();
        this.outPoint = littleEndianDataInputStream.readFloat();
        this.loop = littleEndianDataInputStream.readInt() != 0;
        this.easeInTime = littleEndianDataInputStream.readFloat();
        this.easeOutTime = littleEndianDataInputStream.readFloat();
        this.handPose = littleEndianDataInputStream.readInt();
        int readInt = littleEndianDataInputStream.readInt();
        while (i < readInt) {
            SLSkeletonBoneID sLSkeletonBoneID = (SLSkeletonBoneID) SLSkeletonBoneID.bones.get(littleEndianDataInputStream.readZeroTerminatedString());
            AnimationJointData animationJointData = new AnimationJointData(littleEndianDataInputStream, this.animLength);
            if (sLSkeletonBoneID != null) {
                int i2 = sLSkeletonBoneID.animatedIndex;
                if (i2 >= 0) {
                    AnimationJointSet animationJointSet = (AnimationJointSet) this.jointSets.get(animationJointData.Priority);
                    if (animationJointSet == null) {
                        animationJointSet = new AnimationJointSet(uuid, this.animLength, animationJointData.Priority, null);
                        this.jointSets.put(animationJointData.Priority, animationJointSet);
                    }
                    animationJointSet.addJointData(i2, animationJointData);
                }
            }
            i++;
        }
    }

    private static float cubicStep(float f) {
        float max = Math.max(0.0f, Math.min(1.0f, f));
        return (3.0f - (max * 2.0f)) * (max * max);
    }

    private float getInAnimationTime(float f, float f2) {
        if (!this.loop) {
            return Math.min(f, this.animLength);
        }
        if (f < this.inPoint) {
            return f;
        }
        if (f2 < 0.0f) {
            return this.outPoint > this.inPoint ? this.inPoint + ((f - this.inPoint) % (this.outPoint - this.inPoint)) : this.inPoint;
        } else {
            float f3;
            if (this.outPoint > this.inPoint) {
                f3 = f - f2;
                f3 = (f3 - (((float) Math.floor((double) ((f3 - this.inPoint) / (this.outPoint - this.inPoint)))) * (this.outPoint - this.inPoint))) + f2;
            } else {
                f3 = this.outPoint + f2;
            }
            return Math.min(f3, this.animLength);
        }
    }

    private float getInFactor(float f) {
        if (f >= this.easeInTime || this.easeInTime < 0.001f) {
            return 1.0f;
        }
        float cubicStep = cubicStep(f / this.easeInTime);
        return cubicStep > 1.0f ? 1.0f : cubicStep;
    }

    private float getOutFactor(float f) {
        float f2 = 1.0f;
        if (f >= 0.0f) {
            if (this.easeOutTime < 0.001f) {
                return 0.0f;
            }
            f2 = cubicStep(1.0f - (f / this.easeOutTime));
            if (f2 < 0.0f) {
                return 0.0f;
            }
        }
        return f2;
    }

    private float getOutFactor(float f, float f2) {
        float f3;
        if (f2 >= 0.0f) {
            if (!this.loop) {
                float f4 = f - (this.animLength - this.easeOutTime);
                if (f4 > 0.0f) {
                    f2 = Math.max(f2, f4);
                }
                return getOutFactor(f2);
            } else if (this.outPoint >= this.animLength) {
                return getOutFactor(f2);
            } else {
                f3 = f - f2;
                return getOutFactor(f - Math.max((this.outPoint > this.inPoint ? ((((float) Math.floor((double) ((f3 - this.inPoint) / (this.outPoint - this.inPoint)))) * (this.outPoint - this.inPoint)) + this.inPoint) + this.animLength : this.animLength + f3) - this.easeOutTime, f3));
            }
        } else if (this.loop) {
            return 1.0f;
        } else {
            f3 = f - (this.animLength - this.easeOutTime);
            return f3 >= 0.0f ? getOutFactor(f3) : 1.0f;
        }
    }

    ImmutableList<AvatarRunningAnimation> createRunningAnimations(AvatarRunningSequence avatarRunningSequence) {
        Debug.Printf("Animation: creating anims: %d anims", Integer.valueOf(this.jointSets.size()));
        Builder builder = ImmutableList.builder();
        for (int i = 0; i < r2; i++) {
            builder.add(new AvatarRunningAnimation(avatarRunningSequence, (AnimationJointSet) this.jointSets.valueAt(i)));
        }
        return builder.build();
    }

    public void dumpAnimationData() {
        Debug.Printf("Animation -- dump -- priority %d length %f joint sets %d (inPoint %f outPoint %f loop %b easeIn %f easeOut %f)", Integer.valueOf(this.animPriority), Float.valueOf(this.animLength), Integer.valueOf(this.jointSets.size()), Float.valueOf(this.inPoint), Float.valueOf(this.outPoint), Boolean.valueOf(this.loop), Float.valueOf(this.easeInTime), Float.valueOf(this.easeOutTime));
        for (int i = 0; i < this.jointSets.size(); i++) {
            int keyAt = this.jointSets.keyAt(i);
            Debug.Printf("Anim -- joint set %d: prio %d", Integer.valueOf(i), Integer.valueOf(keyAt));
            ((AnimationJointSet) this.jointSets.valueAt(i)).dumpJoints();
        }
        Debug.Printf("Animation -- dump end", new Object[0]);
    }

    public int getPriority() {
        return this.animPriority;
    }

    boolean updateAnimationTiming(long j, long j2, long j3, boolean z, AnimationTiming animationTiming) {
        float f = ((float) (j - j2)) / 1000.0f;
        float f2 = (j3 == -1 || j < j3) ? -1.0f : ((float) (j - j3)) / 1000.0f;
        float inAnimationTime = getInAnimationTime(f, f2);
        float inFactor = getInFactor(f);
        float outFactor = getOutFactor(f, f2);
        f2 = z ? 1.0f : inFactor;
        boolean z2 = false;
        animationTiming.runningTime = f;
        if (animationTiming.inAnimationTime != inAnimationTime) {
            animationTiming.inAnimationTime = inAnimationTime;
            z2 = true;
        }
        if (animationTiming.inFactor != f2) {
            animationTiming.inFactor = f2;
            z2 = true;
        }
        if (animationTiming.outFactor == outFactor) {
            return z2;
        }
        animationTiming.outFactor = outFactor;
        return true;
    }
}
