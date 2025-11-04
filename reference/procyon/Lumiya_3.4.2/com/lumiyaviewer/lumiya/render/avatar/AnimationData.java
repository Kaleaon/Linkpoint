// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.render.avatar;

import com.lumiyaviewer.lumiya.slproto.avatar.SLSkeletonBone;
import com.lumiyaviewer.lumiya.slproto.types.LLQuaternion;
import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import com.lumiyaviewer.lumiya.Debug;
import com.google.common.collect.ImmutableList;
import java.io.IOException;
import com.lumiyaviewer.lumiya.slproto.avatar.SLSkeletonBoneID;
import com.lumiyaviewer.lumiya.utils.LittleEndianDataInputStream;
import java.io.InputStream;
import android.util.SparseArray;
import java.util.UUID;

public class AnimationData
{
    private static final float LL_MAX_PELVIS_OFFSET = 5.0f;
    private final float animLength;
    private final int animPriority;
    private final UUID animationUUID;
    private final float easeInTime;
    private final float easeOutTime;
    private final String expressionName;
    private final int handPose;
    private final float inPoint;
    private final SparseArray<AnimationJointSet> jointSets;
    private final boolean loop;
    private final float outPoint;
    
    public AnimationData(final UUID animationUUID, final InputStream inputStream) throws IOException {
        int i = 0;
        this.jointSets = (SparseArray<AnimationJointSet>)new SparseArray();
        this.animationUUID = animationUUID;
        final LittleEndianDataInputStream littleEndianDataInputStream = new LittleEndianDataInputStream(inputStream);
        littleEndianDataInputStream.skipBytes(4);
        this.animPriority = littleEndianDataInputStream.readInt();
        this.animLength = littleEndianDataInputStream.readFloat();
        this.expressionName = littleEndianDataInputStream.readZeroTerminatedString();
        this.inPoint = littleEndianDataInputStream.readFloat();
        this.outPoint = littleEndianDataInputStream.readFloat();
        this.loop = (littleEndianDataInputStream.readInt() != 0);
        this.easeInTime = littleEndianDataInputStream.readFloat();
        this.easeOutTime = littleEndianDataInputStream.readFloat();
        this.handPose = littleEndianDataInputStream.readInt();
        while (i < littleEndianDataInputStream.readInt()) {
            final SLSkeletonBoneID slSkeletonBoneID = SLSkeletonBoneID.bones.get(littleEndianDataInputStream.readZeroTerminatedString());
            final AnimationJointData animationJointData = new AnimationJointData(littleEndianDataInputStream, this.animLength);
            if (slSkeletonBoneID != null) {
                final int animatedIndex = slSkeletonBoneID.animatedIndex;
                if (animatedIndex >= 0) {
                    AnimationJointSet set;
                    if ((set = (AnimationJointSet)this.jointSets.get(animationJointData.Priority)) == null) {
                        set = new AnimationJointSet(animationUUID, this.animLength, animationJointData.Priority, null);
                        this.jointSets.put(animationJointData.Priority, (Object)set);
                    }
                    set.addJointData(animatedIndex, animationJointData);
                }
            }
            ++i;
        }
    }
    
    private static float cubicStep(float max) {
        max = Math.max(0.0f, Math.min(1.0f, max));
        return (3.0f - max * 2.0f) * (max * max);
    }
    
    private float getInAnimationTime(float n, final float n2) {
        if (!this.loop) {
            return Math.min(n, this.animLength);
        }
        if (n < this.inPoint) {
            return n;
        }
        if (n2 >= 0.0f) {
            if (this.outPoint > this.inPoint) {
                n -= n2;
                n = n - (float)Math.floor((n - this.inPoint) / (this.outPoint - this.inPoint)) * (this.outPoint - this.inPoint) + n2;
            }
            else {
                n = this.outPoint + n2;
            }
            return Math.min(n, this.animLength);
        }
        if (this.outPoint > this.inPoint) {
            return this.inPoint + (n - this.inPoint) % (this.outPoint - this.inPoint);
        }
        return this.inPoint;
    }
    
    private float getInFactor(final float n) {
        float cubicStep;
        final float n2 = cubicStep = 1.0f;
        if (n < this.easeInTime) {
            cubicStep = n2;
            if (this.easeInTime >= 0.001f) {
                cubicStep = cubicStep(n / this.easeInTime);
                if (cubicStep > 1.0f) {
                    cubicStep = n2;
                }
            }
        }
        return cubicStep;
    }
    
    private float getOutFactor(float n) {
        float cubicStep = 1.0f;
        final float n2 = 0.0f;
        if (n >= 0.0f) {
            cubicStep = n2;
            if (this.easeOutTime >= 0.001f) {
                n = (cubicStep = cubicStep(1.0f - n / this.easeOutTime));
                if (n < 0.0f) {
                    cubicStep = n2;
                }
            }
        }
        return cubicStep;
    }
    
    private float getOutFactor(float max, float a) {
        final float n = 1.0f;
        if (a >= 0.0f) {
            if (this.loop) {
                if (this.outPoint >= this.animLength) {
                    a = this.getOutFactor(a);
                }
                else {
                    final float b = max - a;
                    if (this.outPoint > this.inPoint) {
                        a = (float)Math.floor((b - this.inPoint) / (this.outPoint - this.inPoint)) * (this.outPoint - this.inPoint) + this.inPoint + this.animLength;
                    }
                    else {
                        a = this.animLength + b;
                    }
                    a = this.getOutFactor(max - Math.max(a - this.easeOutTime, b));
                }
            }
            else {
                final float b2 = max - (this.animLength - this.easeOutTime);
                max = a;
                if (b2 > 0.0f) {
                    max = Math.max(a, b2);
                }
                a = this.getOutFactor(max);
            }
        }
        else {
            a = n;
            if (!this.loop) {
                max -= this.animLength - this.easeOutTime;
                a = n;
                if (max >= 0.0f) {
                    a = this.getOutFactor(max);
                }
            }
        }
        return a;
    }
    
    ImmutableList<AvatarRunningAnimation> createRunningAnimations(final AvatarRunningSequence avatarRunningSequence) {
        final int size = this.jointSets.size();
        Debug.Printf("Animation: creating anims: %d anims", size);
        final ImmutableList.Builder<AvatarRunningAnimation> builder = ImmutableList.builder();
        for (int i = 0; i < size; ++i) {
            builder.add(new AvatarRunningAnimation(avatarRunningSequence, (AnimationJointSet)this.jointSets.valueAt(i)));
        }
        return builder.build();
    }
    
    public void dumpAnimationData() {
        Debug.Printf("Animation -- dump -- priority %d length %f joint sets %d (inPoint %f outPoint %f loop %b easeIn %f easeOut %f)", this.animPriority, this.animLength, this.jointSets.size(), this.inPoint, this.outPoint, this.loop, this.easeInTime, this.easeOutTime);
        for (int i = 0; i < this.jointSets.size(); ++i) {
            Debug.Printf("Anim -- joint set %d: prio %d", i, this.jointSets.keyAt(i));
            ((AnimationJointSet)this.jointSets.valueAt(i)).dumpJoints();
        }
        Debug.Printf("Animation -- dump end", new Object[0]);
    }
    
    public int getPriority() {
        return this.animPriority;
    }
    
    boolean updateAnimationTiming(final long n, final long n2, final long n3, final boolean b, final AnimationTiming animationTiming) {
        final boolean b2 = true;
        final float runningTime = (n - n2) / 1000.0f;
        float n4;
        if (n3 != -1L && n >= n3) {
            n4 = (n - n3) / 1000.0f;
        }
        else {
            n4 = -1.0f;
        }
        final float inAnimationTime = this.getInAnimationTime(runningTime, n4);
        final float inFactor = this.getInFactor(runningTime);
        final float outFactor = this.getOutFactor(runningTime, n4);
        float inFactor2;
        if (b) {
            inFactor2 = 1.0f;
        }
        else {
            inFactor2 = inFactor;
        }
        boolean b3 = false;
        animationTiming.runningTime = runningTime;
        if (animationTiming.inAnimationTime != inAnimationTime) {
            animationTiming.inAnimationTime = inAnimationTime;
            b3 = true;
        }
        if (animationTiming.inFactor != inFactor2) {
            animationTiming.inFactor = inFactor2;
            b3 = true;
        }
        if (animationTiming.outFactor != outFactor) {
            animationTiming.outFactor = outFactor;
            b3 = b2;
        }
        return b3;
    }
    
    private static class AnimationJointData
    {
        private final int Priority;
        private final AnimationPosKeyframe[] posKeyframes;
        private final AnimationRotKeyframe[] rotKeyframes;
        
        AnimationJointData(final LittleEndianDataInputStream littleEndianDataInputStream, final float n) throws IOException {
            this.Priority = littleEndianDataInputStream.readInt();
            final int int1 = littleEndianDataInputStream.readInt();
            int n2;
            if (int1 < 0 || (n2 = int1) > 10000) {
                n2 = 0;
            }
            this.rotKeyframes = new AnimationRotKeyframe[n2];
            for (int i = 0; i < n2; ++i) {
                this.rotKeyframes[i] = new AnimationRotKeyframe(uint16ToFloat(littleEndianDataInputStream.readUnsignedShort(), 0.0f, n), LLQuaternion.unpackFromVector3(new LLVector3(uint16ToFloat(littleEndianDataInputStream.readUnsignedShort(), -1.0f, 1.0f), uint16ToFloat(littleEndianDataInputStream.readUnsignedShort(), -1.0f, 1.0f), uint16ToFloat(littleEndianDataInputStream.readUnsignedShort(), -1.0f, 1.0f))));
            }
            final int int2 = littleEndianDataInputStream.readInt();
            int n3;
            if (int2 < 0 || (n3 = int2) > 10000) {
                n3 = 0;
            }
            this.posKeyframes = new AnimationPosKeyframe[n3];
            for (int j = 0; j < n3; ++j) {
                this.posKeyframes[j] = new AnimationPosKeyframe(uint16ToFloat(littleEndianDataInputStream.readUnsignedShort(), 0.0f, n), new LLVector3(uint16ToFloat(littleEndianDataInputStream.readUnsignedShort(), -5.0f, 5.0f), uint16ToFloat(littleEndianDataInputStream.readUnsignedShort(), -5.0f, 5.0f), uint16ToFloat(littleEndianDataInputStream.readUnsignedShort(), -5.0f, 5.0f)));
            }
        }
        
        private static <T> boolean animateArray(float n, float n2, final T t, final AnimationKeyframe<T>[] array) {
            if (array.length == 1) {
                array[0].setTransform(t);
                return true;
            }
            for (int i = 0; i < array.length; ++i) {
                if (n2 <= array[i].time) {
                    if (n2 == array[i].time) {
                        array[i].setTransform(t);
                    }
                    else {
                        int n3;
                        if ((n3 = i - 1) < 0) {
                            n3 = 0;
                        }
                        if (n3 == i) {
                            array[i].setTransform(t);
                        }
                        else {
                            final float time = array[i].time;
                            float time2;
                            final float n4 = time2 = array[n3].time;
                            if (n4 > time) {
                                time2 = n4 - n;
                            }
                            if (time2 == time) {
                                array[i].setTransform(t);
                            }
                            else {
                                n = (time - n2) / (time - time2);
                                n2 = (n2 - time2) / (time - time2);
                                array[n3].setInterpolated(t, n, array[i], n2);
                            }
                        }
                    }
                    return true;
                }
            }
            return false;
        }
        
        private static float uint16ToFloat(final int n, float n2, float a) {
            final float n3 = (float)n;
            final float n4 = a - n2;
            a = (n2 += n3 * 1.5259022E-5f * n4);
            if (Math.abs(a) < n4 * 1.5259022E-5f) {
                n2 = 0.0f;
            }
            return n2;
        }
        
        void animate(final SLSkeletonBone slSkeletonBone, final float n, final float n2, final LLQuaternion[] array, final LLVector3[] array2, final int n3, float n4, final float[] array3, final float[] array4, final LLQuaternion llQuaternion, final LLVector3 llVector3) {
            if (this.posKeyframes.length != 0) {
                final float n5 = array4[n3] * n4;
                animateArray(n, n2, llVector3, this.posKeyframes);
                if (slSkeletonBone != null && slSkeletonBone.boneID != SLSkeletonBoneID.mPelvis) {
                    llVector3.sub(slSkeletonBone.getBasePosition());
                }
                array2[n3].addMul(llVector3, n5);
                array4[n3] -= n5;
            }
            if (this.rotKeyframes.length != 0) {
                n4 *= array3[n3];
                animateArray(n, n2, llQuaternion, this.rotKeyframes);
                array[n3].addMul(llQuaternion, n4);
                array3[n3] -= n4;
            }
        }
        
        @Override
        public String toString() {
            final int n = 0;
            final StringBuilder sb = new StringBuilder();
            sb.append("Priority ").append(this.Priority);
            sb.append(", pos frames ").append(this.posKeyframes.length).append("[");
            final AnimationPosKeyframe[] posKeyframes = this.posKeyframes;
            for (int length = posKeyframes.length, i = 0; i < length; ++i) {
                sb.append(posKeyframes[i].toString());
            }
            sb.append("], rot frames ").append(this.rotKeyframes.length).append("[");
            final AnimationRotKeyframe[] rotKeyframes = this.rotKeyframes;
            for (int length2 = rotKeyframes.length, j = n; j < length2; ++j) {
                sb.append(rotKeyframes[j].toString());
            }
            sb.append("]");
            return sb.toString();
        }
    }
    
    static class AnimationJointSet
    {
        private final float animLength;
        private final UUID animationUUID;
        private final SparseArray<AnimationJointData> jointAnims;
        private final int priority;
        
        private AnimationJointSet(final UUID animationUUID, final float animLength, final int priority) {
            this.jointAnims = (SparseArray<AnimationJointData>)new SparseArray();
            this.animationUUID = animationUUID;
            this.animLength = animLength;
            this.priority = priority;
        }
        
        void addJointData(final int n, final AnimationJointData animationJointData) {
            this.jointAnims.put(n, (Object)animationJointData);
        }
        
        void animate(final AvatarSkeleton avatarSkeleton, final AnimationTiming animationTiming, final float[] array, final float[] array2, final LLQuaternion[] array3, final LLVector3[] array4) {
            final float inAnimationTime = animationTiming.inAnimationTime;
            final float n = animationTiming.inFactor * animationTiming.outFactor;
            if (n > 0.0f) {
                final LLQuaternion llQuaternion = new LLQuaternion();
                final LLVector3 llVector3 = new LLVector3();
                for (int size = this.jointAnims.size(), i = 0; i < size; ++i) {
                    final int key = this.jointAnims.keyAt(i);
                    ((AnimationJointData)this.jointAnims.valueAt(i)).animate(avatarSkeleton.getAnimatedBone(key), this.animLength, inAnimationTime, array3, array4, key, n, array, array2, llQuaternion, llVector3);
                }
            }
        }
        
        void dumpJoints() {
            Debug.Printf("Anim -- joint set -- length %f prio %d joints %d", this.animLength, this.priority, this.jointAnims.size());
            for (int size = this.jointAnims.size(), i = 0; i < size; ++i) {
                Debug.Printf("Anim -- joint[%d] - jointIndex %d, %s", i, this.jointAnims.keyAt(i), ((AnimationJointData)this.jointAnims.valueAt(i)).toString());
            }
        }
        
        public int getPriority() {
            return this.priority;
        }
    }
    
    private abstract static class AnimationKeyframe<T>
    {
        public final float time;
        
        private AnimationKeyframe(final float time) {
            this.time = time;
        }
        
        protected abstract T getTransform();
        
        public abstract void setInterpolated(final T p0, final float p1, final AnimationKeyframe<T> p2, final float p3);
        
        public abstract void setTransform(final T p0);
    }
    
    private static class AnimationPosKeyframe extends AnimationKeyframe<LLVector3>
    {
        private final LLVector3 position;
        
        AnimationPosKeyframe(final float n, final LLVector3 position) {
            super(n, null);
            this.position = position;
        }
        
        protected LLVector3 getTransform() {
            return this.position;
        }
        
        public void setInterpolated(final LLVector3 llVector3, final float n, final AnimationKeyframe<LLVector3> animationKeyframe, final float n2) {
            llVector3.setLerp(this.position, n, animationKeyframe.getTransform(), n2);
        }
        
        public void setTransform(final LLVector3 llVector3) {
            llVector3.set(this.position);
        }
        
        @Override
        public String toString() {
            return this.position.toString();
        }
    }
    
    private static class AnimationRotKeyframe extends AnimationKeyframe<LLQuaternion>
    {
        private final LLQuaternion quaternion;
        
        AnimationRotKeyframe(final float n, final LLQuaternion quaternion) {
            super(n, null);
            this.quaternion = quaternion;
        }
        
        protected LLQuaternion getTransform() {
            return this.quaternion;
        }
        
        public void setInterpolated(final LLQuaternion llQuaternion, final float n, final AnimationKeyframe<LLQuaternion> animationKeyframe, final float n2) {
            llQuaternion.setLerp(this.quaternion, n, animationKeyframe.getTransform(), n2);
        }
        
        public void setTransform(final LLQuaternion llQuaternion) {
            llQuaternion.set(this.quaternion);
        }
        
        @Override
        public String toString() {
            return this.quaternion.toString();
        }
    }
}
