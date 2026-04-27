// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.avatar;

import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import java.util.Map;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.avatar:
//            SLSkeleton, SLSkeletonBone, SLSkeletonBoneID

public class SLDefaultSkeleton extends SLSkeleton
{

    protected SLDefaultSkeleton()
    {
        SLSkeletonBone slskeletonbone = new SLSkeletonBone(SLSkeletonBoneID.mSkull, new LLVector3(0.0F, 0.0F, 0.079F), new LLVector3(0.0F, 0.0F, 0.079F), null, null);
        bones.put(SLSkeletonBoneID.mSkull, slskeletonbone);
        SLSkeletonBone slskeletonbone1 = new SLSkeletonBone(SLSkeletonBoneID.mEyeRight, new LLVector3(0.098F, -0.036F, 0.079F), new LLVector3(0.098466F, -0.036F, 0.079F), null, null);
        bones.put(SLSkeletonBoneID.mEyeRight, slskeletonbone1);
        SLSkeletonBone slskeletonbone2 = new SLSkeletonBone(SLSkeletonBoneID.mEyeLeft, new LLVector3(0.098F, 0.036F, 0.079F), new LLVector3(0.098461F, 0.036F, 0.079F), null, null);
        bones.put(SLSkeletonBoneID.mEyeLeft, slskeletonbone2);
        SLSkeletonBone slskeletonbone3 = new SLSkeletonBone(SLSkeletonBoneID.mFaceEyeAltRight, new LLVector3(0.073F, -0.036F, 0.034F), new LLVector3(0.073466F, -0.036F, 0.03393F), null, null);
        bones.put(SLSkeletonBoneID.mFaceEyeAltRight, slskeletonbone3);
        SLSkeletonBone slskeletonbone4 = new SLSkeletonBone(SLSkeletonBoneID.mFaceEyeAltLeft, new LLVector3(0.073F, 0.036F, 0.034F), new LLVector3(0.073461F, 0.036F, 0.03393F), null, null);
        bones.put(SLSkeletonBoneID.mFaceEyeAltLeft, slskeletonbone4);
        SLSkeletonBone slskeletonbone5 = new SLSkeletonBone(SLSkeletonBoneID.mFaceForeheadLeft, new LLVector3(0.061F, 0.035F, 0.083F), new LLVector3(0.061F, 0.035F, 0.083F), null, null);
        bones.put(SLSkeletonBoneID.mFaceForeheadLeft, slskeletonbone5);
        SLSkeletonBone slskeletonbone6 = new SLSkeletonBone(SLSkeletonBoneID.mFaceForeheadRight, new LLVector3(0.061F, -0.035F, 0.083F), new LLVector3(0.061F, -0.035F, 0.083F), null, null);
        bones.put(SLSkeletonBoneID.mFaceForeheadRight, slskeletonbone6);
        SLSkeletonBone slskeletonbone7 = new SLSkeletonBone(SLSkeletonBoneID.mFaceEyebrowOuterLeft, new LLVector3(0.064F, 0.051F, 0.048F), new LLVector3(0.064F, 0.051F, 0.048F), null, null);
        bones.put(SLSkeletonBoneID.mFaceEyebrowOuterLeft, slskeletonbone7);
        SLSkeletonBone slskeletonbone8 = new SLSkeletonBone(SLSkeletonBoneID.mFaceEyebrowCenterLeft, new LLVector3(0.07F, 0.043F, 0.056F), new LLVector3(0.07F, 0.043F, 0.056F), null, null);
        bones.put(SLSkeletonBoneID.mFaceEyebrowCenterLeft, slskeletonbone8);
        SLSkeletonBone slskeletonbone9 = new SLSkeletonBone(SLSkeletonBoneID.mFaceEyebrowInnerLeft, new LLVector3(0.075F, 0.022F, 0.051F), new LLVector3(0.075F, 0.022F, 0.051F), null, null);
        bones.put(SLSkeletonBoneID.mFaceEyebrowInnerLeft, slskeletonbone9);
        SLSkeletonBone slskeletonbone10 = new SLSkeletonBone(SLSkeletonBoneID.mFaceEyebrowOuterRight, new LLVector3(0.064F, -0.051F, 0.048F), new LLVector3(0.064F, -0.051F, 0.048F), null, null);
        bones.put(SLSkeletonBoneID.mFaceEyebrowOuterRight, slskeletonbone10);
        SLSkeletonBone slskeletonbone11 = new SLSkeletonBone(SLSkeletonBoneID.mFaceEyebrowCenterRight, new LLVector3(0.07F, -0.043F, 0.056F), new LLVector3(0.07F, -0.043F, 0.056F), null, null);
        bones.put(SLSkeletonBoneID.mFaceEyebrowCenterRight, slskeletonbone11);
        SLSkeletonBone slskeletonbone12 = new SLSkeletonBone(SLSkeletonBoneID.mFaceEyebrowInnerRight, new LLVector3(0.075F, -0.022F, 0.051F), new LLVector3(0.075F, -0.022F, 0.051F), null, null);
        bones.put(SLSkeletonBoneID.mFaceEyebrowInnerRight, slskeletonbone12);
        SLSkeletonBone slskeletonbone13 = new SLSkeletonBone(SLSkeletonBoneID.mFaceEyeLidUpperLeft, new LLVector3(0.073F, 0.036F, 0.034F), new LLVector3(0.073F, 0.036F, 0.034F), null, null);
        bones.put(SLSkeletonBoneID.mFaceEyeLidUpperLeft, slskeletonbone13);
        SLSkeletonBone slskeletonbone14 = new SLSkeletonBone(SLSkeletonBoneID.mFaceEyeLidLowerLeft, new LLVector3(0.073F, 0.036F, 0.034F), new LLVector3(0.073F, 0.036F, 0.034F), null, null);
        bones.put(SLSkeletonBoneID.mFaceEyeLidLowerLeft, slskeletonbone14);
        SLSkeletonBone slskeletonbone15 = new SLSkeletonBone(SLSkeletonBoneID.mFaceEyeLidUpperRight, new LLVector3(0.073F, -0.036F, 0.034F), new LLVector3(0.073F, -0.036F, 0.034F), null, null);
        bones.put(SLSkeletonBoneID.mFaceEyeLidUpperRight, slskeletonbone15);
        SLSkeletonBone slskeletonbone16 = new SLSkeletonBone(SLSkeletonBoneID.mFaceEyeLidLowerRight, new LLVector3(0.073F, -0.036F, 0.034F), new LLVector3(0.073F, -0.036F, 0.034F), null, null);
        bones.put(SLSkeletonBoneID.mFaceEyeLidLowerRight, slskeletonbone16);
        SLSkeletonBone slskeletonbone17 = new SLSkeletonBone(SLSkeletonBoneID.mFaceEar2Left, new LLVector3(-0.019F, 0.018F, 0.025F), new LLVector3(-0.019F, 0.018F, 0.025F), null, null);
        bones.put(SLSkeletonBoneID.mFaceEar2Left, slskeletonbone17);
        slskeletonbone17 = new SLSkeletonBone(SLSkeletonBoneID.mFaceEar1Left, new LLVector3(0.0F, 0.08F, 0.002F), new LLVector3(0.0F, 0.08F, 0.002F), new SLSkeletonBone[] {
            slskeletonbone17
        }, null);
        bones.put(SLSkeletonBoneID.mFaceEar1Left, slskeletonbone17);
        SLSkeletonBone slskeletonbone18 = new SLSkeletonBone(SLSkeletonBoneID.mFaceEar2Right, new LLVector3(-0.019F, -0.018F, 0.025F), new LLVector3(-0.019F, -0.018F, 0.025F), null, null);
        bones.put(SLSkeletonBoneID.mFaceEar2Right, slskeletonbone18);
        slskeletonbone18 = new SLSkeletonBone(SLSkeletonBoneID.mFaceEar1Right, new LLVector3(0.0F, -0.08F, 0.002F), new LLVector3(0.0F, -0.08F, 0.002F), new SLSkeletonBone[] {
            slskeletonbone18
        }, null);
        bones.put(SLSkeletonBoneID.mFaceEar1Right, slskeletonbone18);
        SLSkeletonBone slskeletonbone19 = new SLSkeletonBone(SLSkeletonBoneID.mFaceNoseLeft, new LLVector3(0.086F, 0.015F, -0.004F), new LLVector3(0.086F, 0.015F, -0.004F), null, null);
        bones.put(SLSkeletonBoneID.mFaceNoseLeft, slskeletonbone19);
        SLSkeletonBone slskeletonbone20 = new SLSkeletonBone(SLSkeletonBoneID.mFaceNoseCenter, new LLVector3(0.102F, 0.0F, 0.0F), new LLVector3(0.102F, 0.0F, 0.0F), null, null);
        bones.put(SLSkeletonBoneID.mFaceNoseCenter, slskeletonbone20);
        SLSkeletonBone slskeletonbone21 = new SLSkeletonBone(SLSkeletonBoneID.mFaceNoseRight, new LLVector3(0.086F, -0.015F, -0.004F), new LLVector3(0.086F, -0.015F, -0.004F), null, null);
        bones.put(SLSkeletonBoneID.mFaceNoseRight, slskeletonbone21);
        SLSkeletonBone slskeletonbone22 = new SLSkeletonBone(SLSkeletonBoneID.mFaceCheekLowerLeft, new LLVector3(0.05F, 0.034F, -0.031F), new LLVector3(0.05F, 0.034F, -0.031F), null, null);
        bones.put(SLSkeletonBoneID.mFaceCheekLowerLeft, slskeletonbone22);
        SLSkeletonBone slskeletonbone23 = new SLSkeletonBone(SLSkeletonBoneID.mFaceCheekUpperLeft, new LLVector3(0.07F, 0.034F, -0.005F), new LLVector3(0.07F, 0.034F, -0.005F), null, null);
        bones.put(SLSkeletonBoneID.mFaceCheekUpperLeft, slskeletonbone23);
        SLSkeletonBone slskeletonbone24 = new SLSkeletonBone(SLSkeletonBoneID.mFaceCheekLowerRight, new LLVector3(0.05F, -0.034F, -0.031F), new LLVector3(0.05F, -0.034F, -0.031F), null, null);
        bones.put(SLSkeletonBoneID.mFaceCheekLowerRight, slskeletonbone24);
        SLSkeletonBone slskeletonbone25 = new SLSkeletonBone(SLSkeletonBoneID.mFaceCheekUpperRight, new LLVector3(0.07F, -0.034F, -0.005F), new LLVector3(0.07F, -0.034F, -0.005F), null, null);
        bones.put(SLSkeletonBoneID.mFaceCheekUpperRight, slskeletonbone25);
        SLSkeletonBone slskeletonbone26 = new SLSkeletonBone(SLSkeletonBoneID.mFaceChin, new LLVector3(0.074F, 0.0F, -0.054F), new LLVector3(0.074F, 0.0F, -0.054F), null, null);
        bones.put(SLSkeletonBoneID.mFaceChin, slskeletonbone26);
        SLSkeletonBone slskeletonbone27 = new SLSkeletonBone(SLSkeletonBoneID.mFaceLipLowerLeft, new LLVector3(0.045F, 0.0F, 0.0F), new LLVector3(0.045F, 0.0F, 0.0F), null, null);
        bones.put(SLSkeletonBoneID.mFaceLipLowerLeft, slskeletonbone27);
        SLSkeletonBone slskeletonbone28 = new SLSkeletonBone(SLSkeletonBoneID.mFaceLipLowerRight, new LLVector3(0.045F, 0.0F, 0.0F), new LLVector3(0.045F, 0.0F, 0.0F), null, null);
        bones.put(SLSkeletonBoneID.mFaceLipLowerRight, slskeletonbone28);
        SLSkeletonBone slskeletonbone29 = new SLSkeletonBone(SLSkeletonBoneID.mFaceLipLowerCenter, new LLVector3(0.045F, 0.0F, 0.0F), new LLVector3(0.045F, 0.0F, 0.0F), null, null);
        bones.put(SLSkeletonBoneID.mFaceLipLowerCenter, slskeletonbone29);
        SLSkeletonBone slskeletonbone30 = new SLSkeletonBone(SLSkeletonBoneID.mFaceTongueTip, new LLVector3(0.022F, 0.0F, 0.007F), new LLVector3(0.022F, 0.0F, 0.007F), null, null);
        bones.put(SLSkeletonBoneID.mFaceTongueTip, slskeletonbone30);
        slskeletonbone30 = new SLSkeletonBone(SLSkeletonBoneID.mFaceTongueBase, new LLVector3(0.039F, 0.0F, 0.005F), new LLVector3(0.039F, 0.0F, 0.005F), new SLSkeletonBone[] {
            slskeletonbone30
        }, null);
        bones.put(SLSkeletonBoneID.mFaceTongueBase, slskeletonbone30);
        slskeletonbone27 = new SLSkeletonBone(SLSkeletonBoneID.mFaceTeethLower, new LLVector3(0.021F, 0.0F, -0.039F), new LLVector3(0.021F, 0.0F, -0.039F), new SLSkeletonBone[] {
            slskeletonbone27, slskeletonbone28, slskeletonbone29, slskeletonbone30
        }, null);
        bones.put(SLSkeletonBoneID.mFaceTeethLower, slskeletonbone27);
        slskeletonbone26 = new SLSkeletonBone(SLSkeletonBoneID.mFaceJaw, new LLVector3(-0.001F, 0.0F, -0.015F), new LLVector3(-0.001F, 0.0F, -0.015F), new SLSkeletonBone[] {
            slskeletonbone26, slskeletonbone27
        }, null);
        bones.put(SLSkeletonBoneID.mFaceJaw, slskeletonbone26);
        slskeletonbone27 = new SLSkeletonBone(SLSkeletonBoneID.mFaceJawShaper, new LLVector3(0.0F, 0.0F, 0.0F), new LLVector3(0.0F, 0.0F, 0.0F), null, null);
        bones.put(SLSkeletonBoneID.mFaceJawShaper, slskeletonbone27);
        slskeletonbone28 = new SLSkeletonBone(SLSkeletonBoneID.mFaceForeheadCenter, new LLVector3(0.069F, 0.0F, 0.065F), new LLVector3(0.069F, 0.0F, 0.065F), null, null);
        bones.put(SLSkeletonBoneID.mFaceForeheadCenter, slskeletonbone28);
        slskeletonbone29 = new SLSkeletonBone(SLSkeletonBoneID.mFaceNoseBase, new LLVector3(0.094F, 0.0F, -0.016F), new LLVector3(0.094F, 0.0F, -0.016F), null, null);
        bones.put(SLSkeletonBoneID.mFaceNoseBase, slskeletonbone29);
        slskeletonbone30 = new SLSkeletonBone(SLSkeletonBoneID.mFaceLipUpperLeft, new LLVector3(0.045F, 0.0F, -0.003F), new LLVector3(0.045F, 0.0F, -0.003F), null, null);
        bones.put(SLSkeletonBoneID.mFaceLipUpperLeft, slskeletonbone30);
        SLSkeletonBone slskeletonbone31 = new SLSkeletonBone(SLSkeletonBoneID.mFaceLipUpperRight, new LLVector3(0.045F, 0.0F, -0.003F), new LLVector3(0.045F, 0.0F, -0.003F), null, null);
        bones.put(SLSkeletonBoneID.mFaceLipUpperRight, slskeletonbone31);
        SLSkeletonBone slskeletonbone32 = new SLSkeletonBone(SLSkeletonBoneID.mFaceLipCornerLeft, new LLVector3(0.028F, -0.019F, -0.01F), new LLVector3(0.028F, -0.019F, -0.01F), null, null);
        bones.put(SLSkeletonBoneID.mFaceLipCornerLeft, slskeletonbone32);
        SLSkeletonBone slskeletonbone33 = new SLSkeletonBone(SLSkeletonBoneID.mFaceLipCornerRight, new LLVector3(0.028F, 0.019F, -0.01F), new LLVector3(0.028F, 0.019F, -0.01F), null, null);
        bones.put(SLSkeletonBoneID.mFaceLipCornerRight, slskeletonbone33);
        SLSkeletonBone slskeletonbone34 = new SLSkeletonBone(SLSkeletonBoneID.mFaceLipUpperCenter, new LLVector3(0.045F, 0.0F, -0.003F), new LLVector3(0.045F, 0.0F, -0.003F), null, null);
        bones.put(SLSkeletonBoneID.mFaceLipUpperCenter, slskeletonbone34);
        slskeletonbone30 = new SLSkeletonBone(SLSkeletonBoneID.mFaceTeethUpper, new LLVector3(0.02F, 0.0F, -0.03F), new LLVector3(0.02F, 0.0F, -0.03F), new SLSkeletonBone[] {
            slskeletonbone30, slskeletonbone31, slskeletonbone32, slskeletonbone33, slskeletonbone34
        }, null);
        bones.put(SLSkeletonBoneID.mFaceTeethUpper, slskeletonbone30);
        slskeletonbone31 = new SLSkeletonBone(SLSkeletonBoneID.mFaceEyecornerInnerLeft, new LLVector3(0.075F, 0.017F, 0.032F), new LLVector3(0.075F, 0.017F, 0.032F), null, null);
        bones.put(SLSkeletonBoneID.mFaceEyecornerInnerLeft, slskeletonbone31);
        slskeletonbone32 = new SLSkeletonBone(SLSkeletonBoneID.mFaceEyecornerInnerRight, new LLVector3(0.075F, -0.017F, 0.032F), new LLVector3(0.075F, -0.017F, 0.032F), null, null);
        bones.put(SLSkeletonBoneID.mFaceEyecornerInnerRight, slskeletonbone32);
        slskeletonbone33 = new SLSkeletonBone(SLSkeletonBoneID.mFaceNoseBridge, new LLVector3(0.091F, 0.0F, 0.02F), new LLVector3(0.091F, 0.0F, 0.02F), null, null);
        bones.put(SLSkeletonBoneID.mFaceNoseBridge, slskeletonbone33);
        slskeletonbone3 = new SLSkeletonBone(SLSkeletonBoneID.mFaceRoot, new LLVector3(0.025F, 0.0F, 0.045F), new LLVector3(0.025F, 0.0F, 0.045F), new SLSkeletonBone[] {
            slskeletonbone3, slskeletonbone4, slskeletonbone5, slskeletonbone6, slskeletonbone7, slskeletonbone8, slskeletonbone9, slskeletonbone10, slskeletonbone11, slskeletonbone12, 
            slskeletonbone13, slskeletonbone14, slskeletonbone15, slskeletonbone16, slskeletonbone17, slskeletonbone18, slskeletonbone19, slskeletonbone20, slskeletonbone21, slskeletonbone22, 
            slskeletonbone23, slskeletonbone24, slskeletonbone25, slskeletonbone26, slskeletonbone27, slskeletonbone28, slskeletonbone29, slskeletonbone30, slskeletonbone31, slskeletonbone32, 
            slskeletonbone33
        }, null);
        bones.put(SLSkeletonBoneID.mFaceRoot, slskeletonbone3);
        slskeletonbone4 = new SLSkeletonBone(SLSkeletonBoneID.HEAD, new LLVector3(0.02F, 0.0F, 0.07F), new LLVector3(0.0F, 0.0F, 0.0F), null, null);
        slskeletonbone4.deform(new LLVector3(), new LLVector3(0.11F, 0.09F, 0.12F));
        bones.put(SLSkeletonBoneID.HEAD, slskeletonbone4);
        slskeletonbone = new SLSkeletonBone(SLSkeletonBoneID.mHead, new LLVector3(0.0F, 0.0F, 0.076F), new LLVector3(0.0F, 0.0F, 0.07563F), new SLSkeletonBone[] {
            slskeletonbone, slskeletonbone1, slskeletonbone2, slskeletonbone3
        }, new SLSkeletonBone[] {
            slskeletonbone4
        });
        bones.put(SLSkeletonBoneID.mHead, slskeletonbone);
        slskeletonbone1 = new SLSkeletonBone(SLSkeletonBoneID.NECK, new LLVector3(0.0F, 0.0F, 0.02F), new LLVector3(0.0F, 0.0F, 0.0F), null, null);
        slskeletonbone1.deform(new LLVector3(), new LLVector3(0.05F, 0.06F, 0.08F));
        bones.put(SLSkeletonBoneID.NECK, slskeletonbone1);
        slskeletonbone = new SLSkeletonBone(SLSkeletonBoneID.mNeck, new LLVector3(-0.01F, 0.0F, 0.251F), new LLVector3(-0.009507F, 0.0F, 0.251108F), new SLSkeletonBone[] {
            slskeletonbone
        }, new SLSkeletonBone[] {
            slskeletonbone1
        });
        bones.put(SLSkeletonBoneID.mNeck, slskeletonbone);
        slskeletonbone1 = new SLSkeletonBone(SLSkeletonBoneID.mHandMiddle3Left, new LLVector3(-0.001F, 0.049F, -0.008F), new LLVector3(-0.001F, 0.049F, -0.008F), null, null);
        bones.put(SLSkeletonBoneID.mHandMiddle3Left, slskeletonbone1);
        slskeletonbone1 = new SLSkeletonBone(SLSkeletonBoneID.mHandMiddle2Left, new LLVector3(-0.001F, 0.04F, -0.006F), new LLVector3(-0.001F, 0.04F, -0.006F), new SLSkeletonBone[] {
            slskeletonbone1
        }, null);
        bones.put(SLSkeletonBoneID.mHandMiddle2Left, slskeletonbone1);
        slskeletonbone1 = new SLSkeletonBone(SLSkeletonBoneID.mHandMiddle1Left, new LLVector3(0.013F, 0.101F, 0.015F), new LLVector3(0.013F, 0.101F, 0.015F), new SLSkeletonBone[] {
            slskeletonbone1
        }, null);
        bones.put(SLSkeletonBoneID.mHandMiddle1Left, slskeletonbone1);
        slskeletonbone2 = new SLSkeletonBone(SLSkeletonBoneID.mHandIndex3Left, new LLVector3(0.014F, 0.032F, -0.006F), new LLVector3(0.014F, 0.032F, -0.006F), null, null);
        bones.put(SLSkeletonBoneID.mHandIndex3Left, slskeletonbone2);
        slskeletonbone2 = new SLSkeletonBone(SLSkeletonBoneID.mHandIndex2Left, new LLVector3(0.017F, 0.036F, -0.006F), new LLVector3(0.017F, 0.036F, -0.006F), new SLSkeletonBone[] {
            slskeletonbone2
        }, null);
        bones.put(SLSkeletonBoneID.mHandIndex2Left, slskeletonbone2);
        slskeletonbone2 = new SLSkeletonBone(SLSkeletonBoneID.mHandIndex1Left, new LLVector3(0.038F, 0.097F, 0.015F), new LLVector3(0.038F, 0.097F, 0.015F), new SLSkeletonBone[] {
            slskeletonbone2
        }, null);
        bones.put(SLSkeletonBoneID.mHandIndex1Left, slskeletonbone2);
        slskeletonbone3 = new SLSkeletonBone(SLSkeletonBoneID.mHandRing3Left, new LLVector3(-0.013F, 0.04F, -0.009F), new LLVector3(-0.013F, 0.04F, -0.009F), null, null);
        bones.put(SLSkeletonBoneID.mHandRing3Left, slskeletonbone3);
        slskeletonbone3 = new SLSkeletonBone(SLSkeletonBoneID.mHandRing2Left, new LLVector3(-0.013F, 0.038F, -0.008F), new LLVector3(-0.013F, 0.038F, -0.008F), new SLSkeletonBone[] {
            slskeletonbone3
        }, null);
        bones.put(SLSkeletonBoneID.mHandRing2Left, slskeletonbone3);
        slskeletonbone3 = new SLSkeletonBone(SLSkeletonBoneID.mHandRing1Left, new LLVector3(-0.01F, 0.099F, 0.009F), new LLVector3(-0.01F, 0.099F, 0.009F), new SLSkeletonBone[] {
            slskeletonbone3
        }, null);
        bones.put(SLSkeletonBoneID.mHandRing1Left, slskeletonbone3);
        slskeletonbone4 = new SLSkeletonBone(SLSkeletonBoneID.mHandPinky3Left, new LLVector3(-0.015F, 0.018F, -0.004F), new LLVector3(-0.015F, 0.018F, -0.004F), null, null);
        bones.put(SLSkeletonBoneID.mHandPinky3Left, slskeletonbone4);
        slskeletonbone4 = new SLSkeletonBone(SLSkeletonBoneID.mHandPinky2Left, new LLVector3(-0.024F, 0.025F, -0.006F), new LLVector3(-0.024F, 0.025F, -0.006F), new SLSkeletonBone[] {
            slskeletonbone4
        }, null);
        bones.put(SLSkeletonBoneID.mHandPinky2Left, slskeletonbone4);
        slskeletonbone4 = new SLSkeletonBone(SLSkeletonBoneID.mHandPinky1Left, new LLVector3(-0.031F, 0.095F, 0.003F), new LLVector3(-0.031F, 0.095F, 0.003F), new SLSkeletonBone[] {
            slskeletonbone4
        }, null);
        bones.put(SLSkeletonBoneID.mHandPinky1Left, slskeletonbone4);
        slskeletonbone5 = new SLSkeletonBone(SLSkeletonBoneID.mHandThumb3Left, new LLVector3(0.023F, 0.031F, -0.001F), new LLVector3(0.023F, 0.031F, -0.001F), null, null);
        bones.put(SLSkeletonBoneID.mHandThumb3Left, slskeletonbone5);
        slskeletonbone5 = new SLSkeletonBone(SLSkeletonBoneID.mHandThumb2Left, new LLVector3(0.028F, 0.032F, -0.001F), new LLVector3(0.028F, 0.032F, -0.001F), new SLSkeletonBone[] {
            slskeletonbone5
        }, null);
        bones.put(SLSkeletonBoneID.mHandThumb2Left, slskeletonbone5);
        slskeletonbone5 = new SLSkeletonBone(SLSkeletonBoneID.mHandThumb1Left, new LLVector3(0.031F, 0.026F, 0.004F), new LLVector3(0.031F, 0.026F, 0.004F), new SLSkeletonBone[] {
            slskeletonbone5
        }, null);
        bones.put(SLSkeletonBoneID.mHandThumb1Left, slskeletonbone5);
        slskeletonbone6 = new SLSkeletonBone(SLSkeletonBoneID.L_HAND, new LLVector3(0.01F, 0.05F, 0.0F), new LLVector3(0.0F, 0.0F, 0.0F), null, null);
        slskeletonbone6.deform(new LLVector3(), new LLVector3(0.05F, 0.08F, 0.03F));
        bones.put(SLSkeletonBoneID.L_HAND, slskeletonbone6);
        slskeletonbone1 = new SLSkeletonBone(SLSkeletonBoneID.mWristLeft, new LLVector3(0.0F, 0.205F, 0.0F), new LLVector3(0.0F, 0.204846F, 0.0F), new SLSkeletonBone[] {
            slskeletonbone1, slskeletonbone2, slskeletonbone3, slskeletonbone4, slskeletonbone5
        }, new SLSkeletonBone[] {
            slskeletonbone6
        });
        bones.put(SLSkeletonBoneID.mWristLeft, slskeletonbone1);
        slskeletonbone2 = new SLSkeletonBone(SLSkeletonBoneID.L_LOWER_ARM, new LLVector3(0.0F, 0.1F, 0.0F), new LLVector3(0.0F, 0.0F, 0.0F), null, null);
        slskeletonbone2.deform(new LLVector3(), new LLVector3(0.04F, 0.14F, 0.04F));
        bones.put(SLSkeletonBoneID.L_LOWER_ARM, slskeletonbone2);
        slskeletonbone1 = new SLSkeletonBone(SLSkeletonBoneID.mElbowLeft, new LLVector3(0.0F, 0.248F, 0.0F), new LLVector3(0.0F, 0.248F, 0.0F), new SLSkeletonBone[] {
            slskeletonbone1
        }, new SLSkeletonBone[] {
            slskeletonbone2
        });
        bones.put(SLSkeletonBoneID.mElbowLeft, slskeletonbone1);
        slskeletonbone2 = new SLSkeletonBone(SLSkeletonBoneID.L_UPPER_ARM, new LLVector3(0.0F, 0.12F, 0.01F), new LLVector3(0.0F, 0.0F, 0.0F), null, null);
        slskeletonbone2.deform(new LLVector3(), new LLVector3(0.05F, 0.17F, 0.05F));
        bones.put(SLSkeletonBoneID.L_UPPER_ARM, slskeletonbone2);
        slskeletonbone1 = new SLSkeletonBone(SLSkeletonBoneID.mShoulderLeft, new LLVector3(0.0F, 0.079F, 0.0F), new LLVector3(0.0F, 0.079F, 0.0F), new SLSkeletonBone[] {
            slskeletonbone1
        }, new SLSkeletonBone[] {
            slskeletonbone2
        });
        bones.put(SLSkeletonBoneID.mShoulderLeft, slskeletonbone1);
        slskeletonbone2 = new SLSkeletonBone(SLSkeletonBoneID.L_CLAVICLE, new LLVector3(0.02F, 0.0F, 0.02F), new LLVector3(0.0F, 0.0F, 0.0F), null, null);
        slskeletonbone2.deform(new LLVector3(), new LLVector3(0.07F, 0.14F, 0.05F));
        bones.put(SLSkeletonBoneID.L_CLAVICLE, slskeletonbone2);
        slskeletonbone1 = new SLSkeletonBone(SLSkeletonBoneID.mCollarLeft, new LLVector3(-0.021F, 0.085F, 0.165F), new LLVector3(-0.020927F, 0.084665F, 0.165396F), new SLSkeletonBone[] {
            slskeletonbone1
        }, new SLSkeletonBone[] {
            slskeletonbone2
        });
        bones.put(SLSkeletonBoneID.mCollarLeft, slskeletonbone1);
        slskeletonbone2 = new SLSkeletonBone(SLSkeletonBoneID.mHandMiddle3Right, new LLVector3(-0.001F, -0.049F, -0.008F), new LLVector3(-0.001F, -0.049F, -0.008F), null, null);
        bones.put(SLSkeletonBoneID.mHandMiddle3Right, slskeletonbone2);
        slskeletonbone2 = new SLSkeletonBone(SLSkeletonBoneID.mHandMiddle2Right, new LLVector3(-0.001F, -0.04F, -0.006F), new LLVector3(-0.001F, -0.04F, -0.006F), new SLSkeletonBone[] {
            slskeletonbone2
        }, null);
        bones.put(SLSkeletonBoneID.mHandMiddle2Right, slskeletonbone2);
        slskeletonbone2 = new SLSkeletonBone(SLSkeletonBoneID.mHandMiddle1Right, new LLVector3(0.013F, -0.101F, 0.015F), new LLVector3(0.013F, -0.101F, 0.015F), new SLSkeletonBone[] {
            slskeletonbone2
        }, null);
        bones.put(SLSkeletonBoneID.mHandMiddle1Right, slskeletonbone2);
        slskeletonbone3 = new SLSkeletonBone(SLSkeletonBoneID.mHandIndex3Right, new LLVector3(0.014F, -0.032F, -0.006F), new LLVector3(0.014F, -0.032F, -0.006F), null, null);
        bones.put(SLSkeletonBoneID.mHandIndex3Right, slskeletonbone3);
        slskeletonbone3 = new SLSkeletonBone(SLSkeletonBoneID.mHandIndex2Right, new LLVector3(0.017F, -0.036F, -0.006F), new LLVector3(0.017F, -0.036F, -0.006F), new SLSkeletonBone[] {
            slskeletonbone3
        }, null);
        bones.put(SLSkeletonBoneID.mHandIndex2Right, slskeletonbone3);
        slskeletonbone3 = new SLSkeletonBone(SLSkeletonBoneID.mHandIndex1Right, new LLVector3(0.038F, -0.097F, 0.015F), new LLVector3(0.038F, -0.097F, 0.015F), new SLSkeletonBone[] {
            slskeletonbone3
        }, null);
        bones.put(SLSkeletonBoneID.mHandIndex1Right, slskeletonbone3);
        slskeletonbone4 = new SLSkeletonBone(SLSkeletonBoneID.mHandRing3Right, new LLVector3(-0.013F, -0.04F, -0.009F), new LLVector3(-0.013F, -0.04F, -0.009F), null, null);
        bones.put(SLSkeletonBoneID.mHandRing3Right, slskeletonbone4);
        slskeletonbone4 = new SLSkeletonBone(SLSkeletonBoneID.mHandRing2Right, new LLVector3(-0.013F, -0.038F, -0.008F), new LLVector3(-0.013F, -0.038F, -0.008F), new SLSkeletonBone[] {
            slskeletonbone4
        }, null);
        bones.put(SLSkeletonBoneID.mHandRing2Right, slskeletonbone4);
        slskeletonbone4 = new SLSkeletonBone(SLSkeletonBoneID.mHandRing1Right, new LLVector3(-0.01F, -0.099F, 0.009F), new LLVector3(-0.01F, -0.099F, 0.009F), new SLSkeletonBone[] {
            slskeletonbone4
        }, null);
        bones.put(SLSkeletonBoneID.mHandRing1Right, slskeletonbone4);
        slskeletonbone5 = new SLSkeletonBone(SLSkeletonBoneID.mHandPinky3Right, new LLVector3(-0.015F, -0.018F, -0.004F), new LLVector3(-0.015F, -0.018F, -0.004F), null, null);
        bones.put(SLSkeletonBoneID.mHandPinky3Right, slskeletonbone5);
        slskeletonbone5 = new SLSkeletonBone(SLSkeletonBoneID.mHandPinky2Right, new LLVector3(-0.024F, -0.025F, -0.006F), new LLVector3(-0.024F, -0.025F, -0.006F), new SLSkeletonBone[] {
            slskeletonbone5
        }, null);
        bones.put(SLSkeletonBoneID.mHandPinky2Right, slskeletonbone5);
        slskeletonbone5 = new SLSkeletonBone(SLSkeletonBoneID.mHandPinky1Right, new LLVector3(-0.031F, -0.095F, 0.003F), new LLVector3(-0.031F, -0.095F, 0.003F), new SLSkeletonBone[] {
            slskeletonbone5
        }, null);
        bones.put(SLSkeletonBoneID.mHandPinky1Right, slskeletonbone5);
        slskeletonbone6 = new SLSkeletonBone(SLSkeletonBoneID.mHandThumb3Right, new LLVector3(0.023F, -0.031F, -0.001F), new LLVector3(0.023F, -0.031F, -0.001F), null, null);
        bones.put(SLSkeletonBoneID.mHandThumb3Right, slskeletonbone6);
        slskeletonbone6 = new SLSkeletonBone(SLSkeletonBoneID.mHandThumb2Right, new LLVector3(0.028F, -0.032F, -0.001F), new LLVector3(0.028F, -0.032F, -0.001F), new SLSkeletonBone[] {
            slskeletonbone6
        }, null);
        bones.put(SLSkeletonBoneID.mHandThumb2Right, slskeletonbone6);
        slskeletonbone6 = new SLSkeletonBone(SLSkeletonBoneID.mHandThumb1Right, new LLVector3(0.031F, -0.026F, 0.004F), new LLVector3(0.031F, -0.026F, 0.004F), new SLSkeletonBone[] {
            slskeletonbone6
        }, null);
        bones.put(SLSkeletonBoneID.mHandThumb1Right, slskeletonbone6);
        slskeletonbone7 = new SLSkeletonBone(SLSkeletonBoneID.R_HAND, new LLVector3(0.01F, -0.05F, 0.0F), new LLVector3(0.0F, 0.0F, 0.0F), null, null);
        slskeletonbone7.deform(new LLVector3(), new LLVector3(0.05F, 0.08F, 0.03F));
        bones.put(SLSkeletonBoneID.R_HAND, slskeletonbone7);
        slskeletonbone2 = new SLSkeletonBone(SLSkeletonBoneID.mWristRight, new LLVector3(0.0F, -0.205F, 0.0F), new LLVector3(0.0F, -0.205F, 0.0F), new SLSkeletonBone[] {
            slskeletonbone2, slskeletonbone3, slskeletonbone4, slskeletonbone5, slskeletonbone6
        }, new SLSkeletonBone[] {
            slskeletonbone7
        });
        bones.put(SLSkeletonBoneID.mWristRight, slskeletonbone2);
        slskeletonbone3 = new SLSkeletonBone(SLSkeletonBoneID.R_LOWER_ARM, new LLVector3(0.0F, -0.1F, 0.0F), new LLVector3(0.0F, 0.0F, 0.0F), null, null);
        slskeletonbone3.deform(new LLVector3(), new LLVector3(0.04F, 0.14F, 0.04F));
        bones.put(SLSkeletonBoneID.R_LOWER_ARM, slskeletonbone3);
        slskeletonbone2 = new SLSkeletonBone(SLSkeletonBoneID.mElbowRight, new LLVector3(0.0F, -0.248F, 0.0F), new LLVector3(0.0F, -0.248F, 0.0F), new SLSkeletonBone[] {
            slskeletonbone2
        }, new SLSkeletonBone[] {
            slskeletonbone3
        });
        bones.put(SLSkeletonBoneID.mElbowRight, slskeletonbone2);
        slskeletonbone3 = new SLSkeletonBone(SLSkeletonBoneID.R_UPPER_ARM, new LLVector3(0.0F, -0.12F, 0.01F), new LLVector3(0.0F, 0.0F, 0.0F), null, null);
        slskeletonbone3.deform(new LLVector3(), new LLVector3(0.05F, 0.17F, 0.05F));
        bones.put(SLSkeletonBoneID.R_UPPER_ARM, slskeletonbone3);
        slskeletonbone2 = new SLSkeletonBone(SLSkeletonBoneID.mShoulderRight, new LLVector3(0.0F, -0.079F, 0.0F), new LLVector3(0.0F, -0.079418F, 0.0F), new SLSkeletonBone[] {
            slskeletonbone2
        }, new SLSkeletonBone[] {
            slskeletonbone3
        });
        bones.put(SLSkeletonBoneID.mShoulderRight, slskeletonbone2);
        slskeletonbone3 = new SLSkeletonBone(SLSkeletonBoneID.R_CLAVICLE, new LLVector3(0.02F, 0.0F, 0.02F), new LLVector3(0.0F, 0.0F, 0.0F), null, null);
        slskeletonbone3.deform(new LLVector3(), new LLVector3(0.07F, 0.14F, 0.05F));
        bones.put(SLSkeletonBoneID.R_CLAVICLE, slskeletonbone3);
        slskeletonbone2 = new SLSkeletonBone(SLSkeletonBoneID.mCollarRight, new LLVector3(-0.021F, -0.085F, 0.165F), new LLVector3(-0.020927F, -0.085F, 0.165396F), new SLSkeletonBone[] {
            slskeletonbone2
        }, new SLSkeletonBone[] {
            slskeletonbone3
        });
        bones.put(SLSkeletonBoneID.mCollarRight, slskeletonbone2);
        slskeletonbone3 = new SLSkeletonBone(SLSkeletonBoneID.mWing4Left, new LLVector3(-0.171F, 0.173F, 0.0F), new LLVector3(-0.171F, 0.173F, 0.0F), null, null);
        bones.put(SLSkeletonBoneID.mWing4Left, slskeletonbone3);
        slskeletonbone4 = new SLSkeletonBone(SLSkeletonBoneID.mWing4FanLeft, new LLVector3(-0.171F, 0.173F, 0.0F), new LLVector3(-0.171F, 0.173F, 0.0F), null, null);
        bones.put(SLSkeletonBoneID.mWing4FanLeft, slskeletonbone4);
        slskeletonbone3 = new SLSkeletonBone(SLSkeletonBoneID.mWing3Left, new LLVector3(-0.181F, 0.183F, 0.0F), new LLVector3(-0.181F, 0.183F, 0.0F), new SLSkeletonBone[] {
            slskeletonbone3, slskeletonbone4
        }, null);
        bones.put(SLSkeletonBoneID.mWing3Left, slskeletonbone3);
        slskeletonbone3 = new SLSkeletonBone(SLSkeletonBoneID.mWing2Left, new LLVector3(-0.168F, 0.169F, 0.067F), new LLVector3(-0.168F, 0.169F, 0.067F), new SLSkeletonBone[] {
            slskeletonbone3
        }, null);
        bones.put(SLSkeletonBoneID.mWing2Left, slskeletonbone3);
        slskeletonbone3 = new SLSkeletonBone(SLSkeletonBoneID.mWing1Left, new LLVector3(-0.099F, 0.105F, 0.181F), new LLVector3(-0.099F, 0.105F, 0.181F), new SLSkeletonBone[] {
            slskeletonbone3
        }, null);
        bones.put(SLSkeletonBoneID.mWing1Left, slskeletonbone3);
        slskeletonbone4 = new SLSkeletonBone(SLSkeletonBoneID.mWing4Right, new LLVector3(-0.171F, -0.173F, 0.0F), new LLVector3(-0.171F, -0.173F, 0.0F), null, null);
        bones.put(SLSkeletonBoneID.mWing4Right, slskeletonbone4);
        slskeletonbone5 = new SLSkeletonBone(SLSkeletonBoneID.mWing4FanRight, new LLVector3(-0.171F, -0.173F, 0.0F), new LLVector3(-0.171F, -0.173F, 0.0F), null, null);
        bones.put(SLSkeletonBoneID.mWing4FanRight, slskeletonbone5);
        slskeletonbone4 = new SLSkeletonBone(SLSkeletonBoneID.mWing3Right, new LLVector3(-0.181F, -0.183F, 0.0F), new LLVector3(-0.181F, -0.183F, 0.0F), new SLSkeletonBone[] {
            slskeletonbone4, slskeletonbone5
        }, null);
        bones.put(SLSkeletonBoneID.mWing3Right, slskeletonbone4);
        slskeletonbone4 = new SLSkeletonBone(SLSkeletonBoneID.mWing2Right, new LLVector3(-0.168F, -0.169F, 0.067F), new LLVector3(-0.168F, -0.169F, 0.067F), new SLSkeletonBone[] {
            slskeletonbone4
        }, null);
        bones.put(SLSkeletonBoneID.mWing2Right, slskeletonbone4);
        slskeletonbone4 = new SLSkeletonBone(SLSkeletonBoneID.mWing1Right, new LLVector3(-0.099F, -0.105F, 0.181F), new LLVector3(-0.099F, -0.105F, 0.181F), new SLSkeletonBone[] {
            slskeletonbone4
        }, null);
        bones.put(SLSkeletonBoneID.mWing1Right, slskeletonbone4);
        slskeletonbone3 = new SLSkeletonBone(SLSkeletonBoneID.mWingsRoot, new LLVector3(-0.014F, 0.0F, 0.0F), new LLVector3(-0.014F, 0.0F, 0.0F), new SLSkeletonBone[] {
            slskeletonbone3, slskeletonbone4
        }, null);
        bones.put(SLSkeletonBoneID.mWingsRoot, slskeletonbone3);
        slskeletonbone4 = new SLSkeletonBone(SLSkeletonBoneID.CHEST, new LLVector3(0.028F, 0.0F, 0.07F), new LLVector3(0.0F, 0.0F, 0.0F), null, null);
        slskeletonbone4.deform(new LLVector3(), new LLVector3(0.11F, 0.15F, 0.2F));
        bones.put(SLSkeletonBoneID.CHEST, slskeletonbone4);
        slskeletonbone5 = new SLSkeletonBone(SLSkeletonBoneID.LEFT_PEC, new LLVector3(0.119F, 0.082F, 0.042F), new LLVector3(0.0F, 0.0F, 0.0F), null, null);
        slskeletonbone5.deform(new LLVector3(), new LLVector3(0.05F, 0.05F, 0.05F));
        bones.put(SLSkeletonBoneID.LEFT_PEC, slskeletonbone5);
        slskeletonbone6 = new SLSkeletonBone(SLSkeletonBoneID.RIGHT_PEC, new LLVector3(0.119F, -0.082F, 0.042F), new LLVector3(0.0F, 0.0F, 0.0F), null, null);
        slskeletonbone6.deform(new LLVector3(), new LLVector3(0.05F, 0.05F, 0.05F));
        bones.put(SLSkeletonBoneID.RIGHT_PEC, slskeletonbone6);
        slskeletonbone7 = new SLSkeletonBone(SLSkeletonBoneID.UPPER_BACK, new LLVector3(0.0F, 0.0F, 0.017F), new LLVector3(0.0F, 0.0F, 0.0F), null, null);
        slskeletonbone7.deform(new LLVector3(), new LLVector3(0.09F, 0.13F, 0.15F));
        bones.put(SLSkeletonBoneID.UPPER_BACK, slskeletonbone7);
        slskeletonbone = new SLSkeletonBone(SLSkeletonBoneID.mChest, new LLVector3(-0.015F, 0.0F, 0.205F), new LLVector3(-0.015368F, 0.0F, 0.204877F), new SLSkeletonBone[] {
            slskeletonbone, slskeletonbone1, slskeletonbone2, slskeletonbone3
        }, new SLSkeletonBone[] {
            slskeletonbone4, slskeletonbone5, slskeletonbone6, slskeletonbone7
        });
        bones.put(SLSkeletonBoneID.mChest, slskeletonbone);
        slskeletonbone = new SLSkeletonBone(SLSkeletonBoneID.mSpine4, new LLVector3(0.015F, 0.0F, -0.205F), new LLVector3(0.015368F, 0.0F, -0.204877F), new SLSkeletonBone[] {
            slskeletonbone
        }, null);
        bones.put(SLSkeletonBoneID.mSpine4, slskeletonbone);
        slskeletonbone = new SLSkeletonBone(SLSkeletonBoneID.mSpine3, new LLVector3(-0.015F, 0.0F, 0.205F), new LLVector3(-0.015368F, 0.0F, 0.204877F), new SLSkeletonBone[] {
            slskeletonbone
        }, null);
        bones.put(SLSkeletonBoneID.mSpine3, slskeletonbone);
        slskeletonbone1 = new SLSkeletonBone(SLSkeletonBoneID.BELLY, new LLVector3(0.028F, 0.0F, 0.04F), new LLVector3(0.0F, 0.0F, 0.0F), null, null);
        slskeletonbone1.deform(new LLVector3(), new LLVector3(0.09F, 0.13F, 0.15F));
        bones.put(SLSkeletonBoneID.BELLY, slskeletonbone1);
        slskeletonbone2 = new SLSkeletonBone(SLSkeletonBoneID.LEFT_HANDLE, new LLVector3(0.0F, 0.1F, 0.058F), new LLVector3(0.0F, 0.0F, 0.0F), null, null);
        slskeletonbone2.deform(new LLVector3(), new LLVector3(0.05F, 0.05F, 0.05F));
        bones.put(SLSkeletonBoneID.LEFT_HANDLE, slskeletonbone2);
        slskeletonbone3 = new SLSkeletonBone(SLSkeletonBoneID.RIGHT_HANDLE, new LLVector3(0.0F, -0.1F, 0.058F), new LLVector3(0.0F, 0.0F, 0.0F), null, null);
        slskeletonbone3.deform(new LLVector3(), new LLVector3(0.05F, 0.05F, 0.05F));
        bones.put(SLSkeletonBoneID.RIGHT_HANDLE, slskeletonbone3);
        slskeletonbone4 = new SLSkeletonBone(SLSkeletonBoneID.LOWER_BACK, new LLVector3(0.0F, 0.0F, 0.023F), new LLVector3(0.0F, 0.0F, 0.0F), null, null);
        slskeletonbone4.deform(new LLVector3(), new LLVector3(0.09F, 0.13F, 0.15F));
        bones.put(SLSkeletonBoneID.LOWER_BACK, slskeletonbone4);
        slskeletonbone = new SLSkeletonBone(SLSkeletonBoneID.mTorso, new LLVector3(0.0F, 0.0F, 0.084F), new LLVector3(0.0F, 0.0F, 0.084073F), new SLSkeletonBone[] {
            slskeletonbone
        }, new SLSkeletonBone[] {
            slskeletonbone1, slskeletonbone2, slskeletonbone3, slskeletonbone4
        });
        bones.put(SLSkeletonBoneID.mTorso, slskeletonbone);
        slskeletonbone = new SLSkeletonBone(SLSkeletonBoneID.mSpine2, new LLVector3(0.0F, 0.0F, -0.084F), new LLVector3(0.0F, 0.0F, -0.084073F), new SLSkeletonBone[] {
            slskeletonbone
        }, null);
        bones.put(SLSkeletonBoneID.mSpine2, slskeletonbone);
        slskeletonbone = new SLSkeletonBone(SLSkeletonBoneID.mSpine1, new LLVector3(0.0F, 0.0F, 0.084F), new LLVector3(0.0F, 0.0F, 0.084073F), new SLSkeletonBone[] {
            slskeletonbone
        }, null);
        bones.put(SLSkeletonBoneID.mSpine1, slskeletonbone);
        slskeletonbone1 = new SLSkeletonBone(SLSkeletonBoneID.mToeRight, new LLVector3(0.109F, 0.0F, 0.0F), new LLVector3(0.105399F, -0.010408F, -0.000104F), null, null);
        bones.put(SLSkeletonBoneID.mToeRight, slskeletonbone1);
        slskeletonbone1 = new SLSkeletonBone(SLSkeletonBoneID.mFootRight, new LLVector3(0.112F, 0.0F, -0.061F), new LLVector3(0.111956F, 0.0F, -0.060637F), new SLSkeletonBone[] {
            slskeletonbone1
        }, null);
        bones.put(SLSkeletonBoneID.mFootRight, slskeletonbone1);
        slskeletonbone2 = new SLSkeletonBone(SLSkeletonBoneID.R_FOOT, new LLVector3(0.077F, 0.0F, -0.041F), new LLVector3(0.0F, 0.0F, 0.0F), null, null);
        slskeletonbone2.deform(new LLVector3(), new LLVector3(0.13F, 0.05F, 0.05F));
        bones.put(SLSkeletonBoneID.R_FOOT, slskeletonbone2);
        slskeletonbone1 = new SLSkeletonBone(SLSkeletonBoneID.mAnkleRight, new LLVector3(-0.029F, 0.0F, -0.468F), new LLVector3(-0.028869F, 0.0F, -0.468494F), new SLSkeletonBone[] {
            slskeletonbone1
        }, new SLSkeletonBone[] {
            slskeletonbone2
        });
        bones.put(SLSkeletonBoneID.mAnkleRight, slskeletonbone1);
        slskeletonbone2 = new SLSkeletonBone(SLSkeletonBoneID.R_LOWER_LEG, new LLVector3(-0.02F, 0.0F, -0.2F), new LLVector3(0.0F, 0.0F, 0.0F), null, null);
        slskeletonbone2.deform(new LLVector3(), new LLVector3(0.06F, 0.06F, 0.25F));
        bones.put(SLSkeletonBoneID.R_LOWER_LEG, slskeletonbone2);
        slskeletonbone1 = new SLSkeletonBone(SLSkeletonBoneID.mKneeRight, new LLVector3(-0.001F, 0.049F, -0.491F), new LLVector3(-0.00078F, 0.048635F, -0.490922F), new SLSkeletonBone[] {
            slskeletonbone1
        }, new SLSkeletonBone[] {
            slskeletonbone2
        });
        bones.put(SLSkeletonBoneID.mKneeRight, slskeletonbone1);
        slskeletonbone2 = new SLSkeletonBone(SLSkeletonBoneID.R_UPPER_LEG, new LLVector3(-0.02F, 0.05F, -0.22F), new LLVector3(0.0F, 0.0F, 0.0F), null, null);
        slskeletonbone2.deform(new LLVector3(), new LLVector3(0.09F, 0.09F, 0.32F));
        bones.put(SLSkeletonBoneID.R_UPPER_LEG, slskeletonbone2);
        slskeletonbone1 = new SLSkeletonBone(SLSkeletonBoneID.mHipRight, new LLVector3(0.034F, -0.129F, -0.041F), new LLVector3(0.03362F, -0.128806F, -0.041086F), new SLSkeletonBone[] {
            slskeletonbone1
        }, new SLSkeletonBone[] {
            slskeletonbone2
        });
        bones.put(SLSkeletonBoneID.mHipRight, slskeletonbone1);
        slskeletonbone2 = new SLSkeletonBone(SLSkeletonBoneID.mToeLeft, new LLVector3(0.109F, 0.0F, 0.0F), new LLVector3(0.105387F, 0.00827F, 0.000871F), null, null);
        bones.put(SLSkeletonBoneID.mToeLeft, slskeletonbone2);
        slskeletonbone2 = new SLSkeletonBone(SLSkeletonBoneID.mFootLeft, new LLVector3(0.112F, 0.0F, -0.061F), new LLVector3(0.111956F, 0.0F, -0.06062F), new SLSkeletonBone[] {
            slskeletonbone2
        }, null);
        bones.put(SLSkeletonBoneID.mFootLeft, slskeletonbone2);
        slskeletonbone3 = new SLSkeletonBone(SLSkeletonBoneID.L_FOOT, new LLVector3(0.077F, 0.0F, -0.041F), new LLVector3(0.0F, 0.0F, 0.0F), null, null);
        slskeletonbone3.deform(new LLVector3(), new LLVector3(0.13F, 0.05F, 0.05F));
        bones.put(SLSkeletonBoneID.L_FOOT, slskeletonbone3);
        slskeletonbone2 = new SLSkeletonBone(SLSkeletonBoneID.mAnkleLeft, new LLVector3(-0.029F, 0.001F, -0.468F), new LLVector3(-0.028887F, 0.001378F, -0.468449F), new SLSkeletonBone[] {
            slskeletonbone2
        }, new SLSkeletonBone[] {
            slskeletonbone3
        });
        bones.put(SLSkeletonBoneID.mAnkleLeft, slskeletonbone2);
        slskeletonbone3 = new SLSkeletonBone(SLSkeletonBoneID.L_LOWER_LEG, new LLVector3(-0.02F, 0.0F, -0.2F), new LLVector3(0.0F, 0.0F, 0.0F), null, null);
        slskeletonbone3.deform(new LLVector3(), new LLVector3(0.06F, 0.06F, 0.25F));
        bones.put(SLSkeletonBoneID.L_LOWER_LEG, slskeletonbone3);
        slskeletonbone2 = new SLSkeletonBone(SLSkeletonBoneID.mKneeLeft, new LLVector3(-0.001F, -0.046F, -0.491F), new LLVector3(-0.000887F, -0.045568F, -0.491053F), new SLSkeletonBone[] {
            slskeletonbone2
        }, new SLSkeletonBone[] {
            slskeletonbone3
        });
        bones.put(SLSkeletonBoneID.mKneeLeft, slskeletonbone2);
        slskeletonbone3 = new SLSkeletonBone(SLSkeletonBoneID.L_UPPER_LEG, new LLVector3(-0.02F, -0.05F, -0.22F), new LLVector3(0.0F, 0.0F, 0.0F), null, null);
        slskeletonbone3.deform(new LLVector3(), new LLVector3(0.09F, 0.09F, 0.32F));
        bones.put(SLSkeletonBoneID.L_UPPER_LEG, slskeletonbone3);
        slskeletonbone2 = new SLSkeletonBone(SLSkeletonBoneID.mHipLeft, new LLVector3(0.034F, 0.127F, -0.041F), new LLVector3(0.033757F, 0.126765F, -0.040998F), new SLSkeletonBone[] {
            slskeletonbone2
        }, new SLSkeletonBone[] {
            slskeletonbone3
        });
        bones.put(SLSkeletonBoneID.mHipLeft, slskeletonbone2);
        slskeletonbone3 = new SLSkeletonBone(SLSkeletonBoneID.mTail6, new LLVector3(-0.094F, 0.0F, 0.0F), new LLVector3(-0.094F, 0.0F, 0.0F), null, null);
        bones.put(SLSkeletonBoneID.mTail6, slskeletonbone3);
        slskeletonbone3 = new SLSkeletonBone(SLSkeletonBoneID.mTail5, new LLVector3(-0.112F, 0.0F, 0.0F), new LLVector3(-0.112F, 0.0F, 0.0F), new SLSkeletonBone[] {
            slskeletonbone3
        }, null);
        bones.put(SLSkeletonBoneID.mTail5, slskeletonbone3);
        slskeletonbone3 = new SLSkeletonBone(SLSkeletonBoneID.mTail4, new LLVector3(-0.142F, 0.0F, 0.0F), new LLVector3(-0.142F, 0.0F, 0.0F), new SLSkeletonBone[] {
            slskeletonbone3
        }, null);
        bones.put(SLSkeletonBoneID.mTail4, slskeletonbone3);
        slskeletonbone3 = new SLSkeletonBone(SLSkeletonBoneID.mTail3, new LLVector3(-0.168F, 0.0F, 0.0F), new LLVector3(-0.168F, 0.0F, 0.0F), new SLSkeletonBone[] {
            slskeletonbone3
        }, null);
        bones.put(SLSkeletonBoneID.mTail3, slskeletonbone3);
        slskeletonbone3 = new SLSkeletonBone(SLSkeletonBoneID.mTail2, new LLVector3(-0.197F, 0.0F, 0.0F), new LLVector3(-0.197F, 0.0F, 0.0F), new SLSkeletonBone[] {
            slskeletonbone3
        }, null);
        bones.put(SLSkeletonBoneID.mTail2, slskeletonbone3);
        slskeletonbone3 = new SLSkeletonBone(SLSkeletonBoneID.mTail1, new LLVector3(-0.116F, 0.0F, 0.047F), new LLVector3(-0.116F, 0.0F, 0.047F), new SLSkeletonBone[] {
            slskeletonbone3
        }, null);
        bones.put(SLSkeletonBoneID.mTail1, slskeletonbone3);
        slskeletonbone4 = new SLSkeletonBone(SLSkeletonBoneID.mGroin, new LLVector3(0.064F, 0.0F, -0.097F), new LLVector3(0.064F, 0.0F, -0.097F), null, null);
        bones.put(SLSkeletonBoneID.mGroin, slskeletonbone4);
        slskeletonbone5 = new SLSkeletonBone(SLSkeletonBoneID.mHindLimb4Left, new LLVector3(0.112F, 0.0F, -0.061F), new LLVector3(0.112F, 0.0F, -0.061F), null, null);
        bones.put(SLSkeletonBoneID.mHindLimb4Left, slskeletonbone5);
        slskeletonbone5 = new SLSkeletonBone(SLSkeletonBoneID.mHindLimb3Left, new LLVector3(-0.03F, -0.003F, -0.468F), new LLVector3(-0.03F, -0.003F, -0.468F), new SLSkeletonBone[] {
            slskeletonbone5
        }, null);
        bones.put(SLSkeletonBoneID.mHindLimb3Left, slskeletonbone5);
        slskeletonbone5 = new SLSkeletonBone(SLSkeletonBoneID.mHindLimb2Left, new LLVector3(0.002F, -0.046F, -0.491F), new LLVector3(0.002F, -0.046F, -0.491F), new SLSkeletonBone[] {
            slskeletonbone5
        }, null);
        bones.put(SLSkeletonBoneID.mHindLimb2Left, slskeletonbone5);
        slskeletonbone5 = new SLSkeletonBone(SLSkeletonBoneID.mHindLimb1Left, new LLVector3(-0.204F, 0.129F, -0.125F), new LLVector3(-0.204F, 0.129F, -0.125F), new SLSkeletonBone[] {
            slskeletonbone5
        }, null);
        bones.put(SLSkeletonBoneID.mHindLimb1Left, slskeletonbone5);
        slskeletonbone6 = new SLSkeletonBone(SLSkeletonBoneID.mHindLimb4Right, new LLVector3(0.112F, 0.0F, -0.061F), new LLVector3(0.112F, 0.0F, -0.061F), null, null);
        bones.put(SLSkeletonBoneID.mHindLimb4Right, slskeletonbone6);
        slskeletonbone6 = new SLSkeletonBone(SLSkeletonBoneID.mHindLimb3Right, new LLVector3(-0.03F, 0.003F, -0.468F), new LLVector3(-0.03F, 0.003F, -0.468F), new SLSkeletonBone[] {
            slskeletonbone6
        }, null);
        bones.put(SLSkeletonBoneID.mHindLimb3Right, slskeletonbone6);
        slskeletonbone6 = new SLSkeletonBone(SLSkeletonBoneID.mHindLimb2Right, new LLVector3(0.002F, 0.046F, -0.491F), new LLVector3(0.002F, 0.046F, -0.491F), new SLSkeletonBone[] {
            slskeletonbone6
        }, null);
        bones.put(SLSkeletonBoneID.mHindLimb2Right, slskeletonbone6);
        slskeletonbone6 = new SLSkeletonBone(SLSkeletonBoneID.mHindLimb1Right, new LLVector3(-0.204F, -0.129F, -0.125F), new LLVector3(-0.204F, -0.129F, -0.125F), new SLSkeletonBone[] {
            slskeletonbone6
        }, null);
        bones.put(SLSkeletonBoneID.mHindLimb1Right, slskeletonbone6);
        slskeletonbone5 = new SLSkeletonBone(SLSkeletonBoneID.mHindLimbsRoot, new LLVector3(-0.2F, 0.0F, 0.084F), new LLVector3(-0.2F, 0.0F, 0.084F), new SLSkeletonBone[] {
            slskeletonbone5, slskeletonbone6
        }, null);
        bones.put(SLSkeletonBoneID.mHindLimbsRoot, slskeletonbone5);
        slskeletonbone6 = new SLSkeletonBone(SLSkeletonBoneID.PELVIS, new LLVector3(-0.01F, 0.0F, -0.02F), new LLVector3(0.0F, 0.0F, 0.0F), null, null);
        slskeletonbone6.deform(new LLVector3(), new LLVector3(0.12F, 0.16F, 0.17F));
        bones.put(SLSkeletonBoneID.PELVIS, slskeletonbone6);
        slskeletonbone7 = new SLSkeletonBone(SLSkeletonBoneID.BUTT, new LLVector3(-0.06F, 0.0F, -0.1F), new LLVector3(0.0F, 0.0F, 0.0F), null, null);
        slskeletonbone7.deform(new LLVector3(), new LLVector3(0.1F, 0.1F, 0.1F));
        bones.put(SLSkeletonBoneID.BUTT, slskeletonbone7);
        slskeletonbone = new SLSkeletonBone(SLSkeletonBoneID.mPelvis, new LLVector3(0.0F, 0.0F, 1.067F), new LLVector3(0.0F, 0.0F, 1.067015F), new SLSkeletonBone[] {
            slskeletonbone, slskeletonbone1, slskeletonbone2, slskeletonbone3, slskeletonbone4, slskeletonbone5
        }, new SLSkeletonBone[] {
            slskeletonbone6, slskeletonbone7
        });
        rootBone = slskeletonbone;
        bones.put(SLSkeletonBoneID.mPelvis, slskeletonbone);
    }
}
