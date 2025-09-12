// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.render.avatar;

import android.util.SparseArray;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.slproto.avatar.SLSkeletonBone;
import com.lumiyaviewer.lumiya.slproto.avatar.SLSkeletonBoneID;
import com.lumiyaviewer.lumiya.slproto.types.LLQuaternion;
import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import com.lumiyaviewer.lumiya.utils.LittleEndianDataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.render.avatar:
//            AvatarRunningAnimation, AnimationTiming, AvatarRunningSequence, AvatarSkeleton

public class AnimationData
{
    private static class AnimationJointData
    {

        private final int Priority;
        private final AnimationPosKeyframe posKeyframes[];
        private final AnimationRotKeyframe rotKeyframes[];

        static int _2D_get0(AnimationJointData animationjointdata)
        {
            return animationjointdata.Priority;
        }

        private static boolean animateArray(float f, float f1, Object obj, AnimationKeyframe aanimationkeyframe[])
        {
            if (aanimationkeyframe.length == 1)
            {
                aanimationkeyframe[0].setTransform(obj);
                return true;
            }
            for (int i = 0; i < aanimationkeyframe.length; i++)
            {
                if (f1 <= aanimationkeyframe[i].time)
                {
                    if (f1 == aanimationkeyframe[i].time)
                    {
                        aanimationkeyframe[i].setTransform(obj);
                        return true;
                    }
                    int k = i - 1;
                    int j = k;
                    if (k < 0)
                    {
                        j = 0;
                    }
                    if (j == i)
                    {
                        aanimationkeyframe[i].setTransform(obj);
                        return true;
                    }
                    float f4 = aanimationkeyframe[i].time;
                    float f3 = aanimationkeyframe[j].time;
                    float f2 = f3;
                    if (f3 > f4)
                    {
                        f2 = f3 - f;
                    }
                    if (f2 == f4)
                    {
                        aanimationkeyframe[i].setTransform(obj);
                        return true;
                    } else
                    {
                        f = (f4 - f1) / (f4 - f2);
                        f1 = (f1 - f2) / (f4 - f2);
                        aanimationkeyframe[j].setInterpolated(obj, f, aanimationkeyframe[i], f1);
                        return true;
                    }
                }
            }

            return false;
        }

        private static float uint16ToFloat(int i, float f, float f1)
        {
            float f3 = i;
            float f2 = f1 - f;
            f1 = f3 * 1.525902E-05F * f2 + f;
            f = f1;
            if (Math.abs(f1) < f2 * 1.525902E-05F)
            {
                f = 0.0F;
            }
            return f;
        }

        void animate(SLSkeletonBone slskeletonbone, float f, float f1, LLQuaternion allquaternion[], LLVector3 allvector3[], int i, float f2, 
                float af[], float af1[], LLQuaternion llquaternion, LLVector3 llvector3)
        {
            if (posKeyframes.length != 0)
            {
                float f3 = af1[i] * f2;
                animateArray(f, f1, llvector3, posKeyframes);
                if (slskeletonbone != null && slskeletonbone.boneID != SLSkeletonBoneID.mPelvis)
                {
                    llvector3.sub(slskeletonbone.getBasePosition());
                }
                allvector3[i].addMul(llvector3, f3);
                af1[i] = af1[i] - f3;
            }
            if (rotKeyframes.length != 0)
            {
                f2 = af[i] * f2;
                animateArray(f, f1, llquaternion, rotKeyframes);
                allquaternion[i].addMul(llquaternion, f2);
                af[i] = af[i] - f2;
            }
        }

        public String toString()
        {
            boolean flag = false;
            StringBuilder stringbuilder = new StringBuilder();
            stringbuilder.append("Priority ").append(Priority);
            stringbuilder.append(", pos frames ").append(posKeyframes.length).append("[");
            Object aobj[] = posKeyframes;
            int k = aobj.length;
            for (int i = 0; i < k; i++)
            {
                stringbuilder.append(aobj[i].toString());
            }

            stringbuilder.append("], rot frames ").append(rotKeyframes.length).append("[");
            aobj = rotKeyframes;
            k = aobj.length;
            for (int j = ((flag) ? 1 : 0); j < k; j++)
            {
                stringbuilder.append(aobj[j].toString());
            }

            stringbuilder.append("]");
            return stringbuilder.toString();
        }

        AnimationJointData(LittleEndianDataInputStream littleendiandatainputstream, float f)
            throws IOException
        {
            int i;
label0:
            {
                super();
                Priority = littleendiandatainputstream.readInt();
                int j = littleendiandatainputstream.readInt();
                if (j >= 0)
                {
                    i = j;
                    if (j <= 10000)
                    {
                        break label0;
                    }
                }
                i = 0;
            }
label1:
            {
                rotKeyframes = new AnimationRotKeyframe[i];
                for (int k = 0; k < i; k++)
                {
                    float f1 = uint16ToFloat(littleendiandatainputstream.readUnsignedShort(), 0.0F, f);
                    float f3 = uint16ToFloat(littleendiandatainputstream.readUnsignedShort(), -1F, 1.0F);
                    float f5 = uint16ToFloat(littleendiandatainputstream.readUnsignedShort(), -1F, 1.0F);
                    float f7 = uint16ToFloat(littleendiandatainputstream.readUnsignedShort(), -1F, 1.0F);
                    rotKeyframes[k] = new AnimationRotKeyframe(f1, LLQuaternion.unpackFromVector3(new LLVector3(f3, f5, f7)));
                }

                int l = littleendiandatainputstream.readInt();
                if (l >= 0)
                {
                    i = l;
                    if (l <= 10000)
                    {
                        break label1;
                    }
                }
                i = 0;
            }
            posKeyframes = new AnimationPosKeyframe[i];
            for (int i1 = 0; i1 < i; i1++)
            {
                float f2 = uint16ToFloat(littleendiandatainputstream.readUnsignedShort(), 0.0F, f);
                float f4 = uint16ToFloat(littleendiandatainputstream.readUnsignedShort(), -5F, 5F);
                float f6 = uint16ToFloat(littleendiandatainputstream.readUnsignedShort(), -5F, 5F);
                float f8 = uint16ToFloat(littleendiandatainputstream.readUnsignedShort(), -5F, 5F);
                posKeyframes[i1] = new AnimationPosKeyframe(f2, new LLVector3(f4, f6, f8));
            }

        }
    }

    static class AnimationJointSet
    {

        private final float animLength;
        private final UUID animationUUID;
        private final SparseArray jointAnims;
        private final int priority;

        void addJointData(int i, AnimationJointData animationjointdata)
        {
            jointAnims.put(i, animationjointdata);
        }

        void animate(AvatarSkeleton avatarskeleton, AnimationTiming animationtiming, float af[], float af1[], LLQuaternion allquaternion[], LLVector3 allvector3[])
        {
            float f = animationtiming.inAnimationTime;
            float f1 = animationtiming.inFactor * animationtiming.outFactor;
            if (f1 > 0.0F)
            {
                animationtiming = new LLQuaternion();
                LLVector3 llvector3 = new LLVector3();
                int j = jointAnims.size();
                for (int i = 0; i < j; i++)
                {
                    int k = jointAnims.keyAt(i);
                    ((AnimationJointData)jointAnims.valueAt(i)).animate(avatarskeleton.getAnimatedBone(k), animLength, f, allquaternion, allvector3, k, f1, af, af1, animationtiming, llvector3);
                }

            }
        }

        void dumpJoints()
        {
            Debug.Printf("Anim -- joint set -- length %f prio %d joints %d", new Object[] {
                Float.valueOf(animLength), Integer.valueOf(priority), Integer.valueOf(jointAnims.size())
            });
            int j = jointAnims.size();
            for (int i = 0; i < j; i++)
            {
                Debug.Printf("Anim -- joint[%d] - jointIndex %d, %s", new Object[] {
                    Integer.valueOf(i), Integer.valueOf(jointAnims.keyAt(i)), ((AnimationJointData)jointAnims.valueAt(i)).toString()
                });
            }

        }

        public int getPriority()
        {
            return priority;
        }

        private AnimationJointSet(UUID uuid, float f, int i)
        {
            jointAnims = new SparseArray();
            animationUUID = uuid;
            animLength = f;
            priority = i;
        }

        AnimationJointSet(UUID uuid, float f, int i, AnimationJointSet animationjointset)
        {
            this(uuid, f, i);
        }
    }

    private static abstract class AnimationKeyframe
    {

        public final float time;

        protected abstract Object getTransform();

        public abstract void setInterpolated(Object obj, float f, AnimationKeyframe animationkeyframe, float f1);

        public abstract void setTransform(Object obj);

        private AnimationKeyframe(float f)
        {
            time = f;
        }

        AnimationKeyframe(float f, AnimationKeyframe animationkeyframe)
        {
            this(f);
        }
    }

    private static class AnimationPosKeyframe extends AnimationKeyframe
    {

        private final LLVector3 position;

        protected LLVector3 getTransform()
        {
            return position;
        }

        protected volatile Object getTransform()
        {
            return getTransform();
        }

        public void setInterpolated(LLVector3 llvector3, float f, AnimationKeyframe animationkeyframe, float f1)
        {
            llvector3.setLerp(position, f, (LLVector3)animationkeyframe.getTransform(), f1);
        }

        public volatile void setInterpolated(Object obj, float f, AnimationKeyframe animationkeyframe, float f1)
        {
            setInterpolated((LLVector3)obj, f, animationkeyframe, f1);
        }

        public void setTransform(LLVector3 llvector3)
        {
            llvector3.set(position);
        }

        public volatile void setTransform(Object obj)
        {
            setTransform((LLVector3)obj);
        }

        public String toString()
        {
            return position.toString();
        }

        AnimationPosKeyframe(float f, LLVector3 llvector3)
        {
            super(f, null);
            position = llvector3;
        }
    }

    private static class AnimationRotKeyframe extends AnimationKeyframe
    {

        private final LLQuaternion quaternion;

        protected LLQuaternion getTransform()
        {
            return quaternion;
        }

        protected volatile Object getTransform()
        {
            return getTransform();
        }

        public void setInterpolated(LLQuaternion llquaternion, float f, AnimationKeyframe animationkeyframe, float f1)
        {
            llquaternion.setLerp(quaternion, f, (LLQuaternion)animationkeyframe.getTransform(), f1);
        }

        public volatile void setInterpolated(Object obj, float f, AnimationKeyframe animationkeyframe, float f1)
        {
            setInterpolated((LLQuaternion)obj, f, animationkeyframe, f1);
        }

        public void setTransform(LLQuaternion llquaternion)
        {
            llquaternion.set(quaternion);
        }

        public volatile void setTransform(Object obj)
        {
            setTransform((LLQuaternion)obj);
        }

        public String toString()
        {
            return quaternion.toString();
        }

        AnimationRotKeyframe(float f, LLQuaternion llquaternion)
        {
            super(f, null);
            quaternion = llquaternion;
        }
    }


    private static final float LL_MAX_PELVIS_OFFSET = 5F;
    private final float animLength;
    private final int animPriority;
    private final UUID animationUUID;
    private final float easeInTime;
    private final float easeOutTime;
    private final String expressionName;
    private final int handPose;
    private final float inPoint;
    private final SparseArray jointSets = new SparseArray();
    private final boolean loop;
    private final float outPoint;

    public AnimationData(UUID uuid, InputStream inputstream)
        throws IOException
    {
        int i = 0;
        super();
        animationUUID = uuid;
        LittleEndianDataInputStream littleendiandatainputstream = new LittleEndianDataInputStream(inputstream);
        littleendiandatainputstream.skipBytes(4);
        animPriority = littleendiandatainputstream.readInt();
        animLength = littleendiandatainputstream.readFloat();
        expressionName = littleendiandatainputstream.readZeroTerminatedString();
        inPoint = littleendiandatainputstream.readFloat();
        outPoint = littleendiandatainputstream.readFloat();
        boolean flag;
        if (littleendiandatainputstream.readInt() != 0)
        {
            flag = true;
        } else
        {
            flag = false;
        }
        loop = flag;
        easeInTime = littleendiandatainputstream.readFloat();
        easeOutTime = littleendiandatainputstream.readFloat();
        handPose = littleendiandatainputstream.readInt();
        for (int j = littleendiandatainputstream.readInt(); i < j; i++)
        {
            inputstream = littleendiandatainputstream.readZeroTerminatedString();
            inputstream = (SLSkeletonBoneID)SLSkeletonBoneID.bones.get(inputstream);
            AnimationJointData animationjointdata = new AnimationJointData(littleendiandatainputstream, animLength);
            if (inputstream == null)
            {
                continue;
            }
            int k = ((SLSkeletonBoneID) (inputstream)).animatedIndex;
            if (k < 0)
            {
                continue;
            }
            AnimationJointSet animationjointset = (AnimationJointSet)jointSets.get(AnimationJointData._2D_get0(animationjointdata));
            inputstream = animationjointset;
            if (animationjointset == null)
            {
                inputstream = new AnimationJointSet(uuid, animLength, AnimationJointData._2D_get0(animationjointdata), null);
                jointSets.put(AnimationJointData._2D_get0(animationjointdata), inputstream);
            }
            inputstream.addJointData(k, animationjointdata);
        }

    }

    private static float cubicStep(float f)
    {
        f = Math.max(0.0F, Math.min(1.0F, f));
        return (3F - f * 2.0F) * (f * f);
    }

    private float getInAnimationTime(float f, float f1)
    {
        if (loop)
        {
            if (f < inPoint)
            {
                return f;
            }
            if (f1 >= 0.0F)
            {
                if (outPoint > inPoint)
                {
                    f -= f1;
                    f = (f - (float)Math.floor((f - inPoint) / (outPoint - inPoint)) * (outPoint - inPoint)) + f1;
                } else
                {
                    f = outPoint + f1;
                }
                return Math.min(f, animLength);
            }
            if (outPoint > inPoint)
            {
                return inPoint + (f - inPoint) % (outPoint - inPoint);
            } else
            {
                return inPoint;
            }
        } else
        {
            return Math.min(f, animLength);
        }
    }

    private float getInFactor(float f)
    {
label0:
        {
            if (f < easeInTime && easeInTime >= 0.001F)
            {
                f = cubicStep(f / easeInTime);
                if (f <= 1.0F)
                {
                    break label0;
                }
            }
            return 1.0F;
        }
        return f;
    }

    private float getOutFactor(float f)
    {
        float f1;
label0:
        {
            f1 = 1.0F;
            if (f < 0.0F)
            {
                break label0;
            }
            if (easeOutTime >= 0.001F)
            {
                f = cubicStep(1.0F - f / easeOutTime);
                f1 = f;
                if (f >= 0.0F)
                {
                    break label0;
                }
            }
            return 0.0F;
        }
        return f1;
    }

    private float getOutFactor(float f, float f1)
    {
        float f2 = 1.0F;
        if (f1 < 0.0F) goto _L2; else goto _L1
_L1:
        if (!loop) goto _L4; else goto _L3
_L3:
        if (outPoint < animLength) goto _L6; else goto _L5
_L5:
        f1 = getOutFactor(f1);
_L8:
        return f1;
_L6:
        f2 = f - f1;
        if (outPoint > inPoint)
        {
            f1 = (float)Math.floor((f2 - inPoint) / (outPoint - inPoint)) * (outPoint - inPoint) + inPoint + animLength;
        } else
        {
            f1 = animLength + f2;
        }
        return getOutFactor(f - Math.max(f1 - easeOutTime, f2));
_L4:
        f2 = f - (animLength - easeOutTime);
        f = f1;
        if (f2 > 0.0F)
        {
            f = Math.max(f1, f2);
        }
        return getOutFactor(f);
_L2:
        f1 = f2;
        if (!loop)
        {
            f -= animLength - easeOutTime;
            f1 = f2;
            if (f >= 0.0F)
            {
                return getOutFactor(f);
            }
        }
        if (true) goto _L8; else goto _L7
_L7:
    }

    ImmutableList createRunningAnimations(AvatarRunningSequence avatarrunningsequence)
    {
        int j = jointSets.size();
        Debug.Printf("Animation: creating anims: %d anims", new Object[] {
            Integer.valueOf(j)
        });
        com.google.common.collect.ImmutableList.Builder builder = ImmutableList.builder();
        for (int i = 0; i < j; i++)
        {
            builder.add(new AvatarRunningAnimation(avatarrunningsequence, (AnimationJointSet)jointSets.valueAt(i)));
        }

        return builder.build();
    }

    public void dumpAnimationData()
    {
        Debug.Printf("Animation -- dump -- priority %d length %f joint sets %d (inPoint %f outPoint %f loop %b easeIn %f easeOut %f)", new Object[] {
            Integer.valueOf(animPriority), Float.valueOf(animLength), Integer.valueOf(jointSets.size()), Float.valueOf(inPoint), Float.valueOf(outPoint), Boolean.valueOf(loop), Float.valueOf(easeInTime), Float.valueOf(easeOutTime)
        });
        for (int i = 0; i < jointSets.size(); i++)
        {
            Debug.Printf("Anim -- joint set %d: prio %d", new Object[] {
                Integer.valueOf(i), Integer.valueOf(jointSets.keyAt(i))
            });
            ((AnimationJointSet)jointSets.valueAt(i)).dumpJoints();
        }

        Debug.Printf("Animation -- dump end", new Object[0]);
    }

    public int getPriority()
    {
        return animPriority;
    }

    boolean updateAnimationTiming(long l, long l1, long l2, boolean flag, 
            AnimationTiming animationtiming)
    {
        float f2 = (float)(l - l1) / 1000F;
        float f;
        float f1;
        float f3;
        float f4;
        if (l2 != -1L && l >= l2)
        {
            f = (float)(l - l2) / 1000F;
        } else
        {
            f = -1F;
        }
        f3 = getInAnimationTime(f2, f);
        f1 = getInFactor(f2);
        f4 = getOutFactor(f2, f);
        if (flag)
        {
            f = 1.0F;
        } else
        {
            f = f1;
        }
        flag = false;
        animationtiming.runningTime = f2;
        if (animationtiming.inAnimationTime != f3)
        {
            animationtiming.inAnimationTime = f3;
            flag = true;
        }
        if (animationtiming.inFactor != f)
        {
            animationtiming.inFactor = f;
            flag = true;
        }
        if (animationtiming.outFactor != f4)
        {
            animationtiming.outFactor = f4;
            return true;
        } else
        {
            return flag;
        }
    }
}
