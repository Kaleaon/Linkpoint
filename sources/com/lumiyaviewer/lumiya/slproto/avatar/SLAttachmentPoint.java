// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.avatar;

import com.lumiyaviewer.lumiya.slproto.types.LLQuaternion;
import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import java.util.HashMap;
import java.util.Map;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.avatar:
//            SLSkeletonBoneID

public class SLAttachmentPoint
{

    public static final int NON_HUD_ATTACHMENT_POINTS = 47;
    public static final int NUM_ATTACHMENT_POINTS = 56;
    public static final SLAttachmentPoint attachmentPoints[];
    public static final int nonHUDpoints[] = {
        1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 
        11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 
        21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 
        39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 
        49, 50, 51, 52, 53, 54, 55
    };
    public static final Map pointsByName;
    public final SLSkeletonBoneID bone;
    public final int id;
    public final boolean isHUD;
    public final String name;
    public final int nonHUDindex;
    public final LLVector3 position;
    public final LLQuaternion rotation;

    private SLAttachmentPoint(int i, String s, int j, boolean flag, SLSkeletonBoneID slskeletonboneid, LLVector3 llvector3, LLQuaternion llquaternion)
    {
        id = i;
        name = s;
        nonHUDindex = j;
        isHUD = flag;
        bone = slskeletonboneid;
        position = llvector3;
        rotation = llquaternion;
    }

    static 
    {
        attachmentPoints = new SLAttachmentPoint[56];
        pointsByName = new HashMap();
        attachmentPoints[1] = new SLAttachmentPoint(1, "Chest", 0, false, SLSkeletonBoneID.mChest, new LLVector3(0.15F, 0.0F, -0.1F), new LLQuaternion(0.5F, 0.5F, 0.5F, 0.5F));
        attachmentPoints[2] = new SLAttachmentPoint(2, "Skull", 1, false, SLSkeletonBoneID.mHead, new LLVector3(0.0F, 0.0F, 0.15F), new LLQuaternion(0.0F, 0.0F, 0.707107F, 0.707107F));
        attachmentPoints[3] = new SLAttachmentPoint(3, "Left Shoulder", 2, false, SLSkeletonBoneID.mCollarLeft, new LLVector3(0.0F, 0.0F, 0.08F), new LLQuaternion(0.0F, 0.0F, 0.0F, 1.0F));
        attachmentPoints[4] = new SLAttachmentPoint(4, "Right Shoulder", 3, false, SLSkeletonBoneID.mCollarRight, new LLVector3(0.0F, 0.0F, 0.08F), new LLQuaternion(0.0F, 0.0F, 0.0F, 1.0F));
        attachmentPoints[5] = new SLAttachmentPoint(5, "Left Hand", 4, false, SLSkeletonBoneID.mWristLeft, new LLVector3(0.0F, 0.08F, -0.02F), new LLQuaternion(0.0F, 0.0F, 0.0F, 1.0F));
        attachmentPoints[6] = new SLAttachmentPoint(6, "Right Hand", 5, false, SLSkeletonBoneID.mWristRight, new LLVector3(0.0F, -0.08F, -0.02F), new LLQuaternion(0.0F, 0.0F, 0.0F, 1.0F));
        attachmentPoints[7] = new SLAttachmentPoint(7, "Left Foot", 6, false, SLSkeletonBoneID.mFootLeft, new LLVector3(0.0F, 0.0F, 0.0F), new LLQuaternion(0.0F, 0.0F, 0.0F, 1.0F));
        attachmentPoints[8] = new SLAttachmentPoint(8, "Right Foot", 7, false, SLSkeletonBoneID.mFootRight, new LLVector3(0.0F, 0.0F, 0.0F), new LLQuaternion(0.0F, 0.0F, 0.0F, 1.0F));
        attachmentPoints[9] = new SLAttachmentPoint(9, "Spine", 8, false, SLSkeletonBoneID.mChest, new LLVector3(-0.15F, 0.0F, -0.1F), new LLQuaternion(-0.5F, -0.5F, 0.5F, 0.5F));
        attachmentPoints[10] = new SLAttachmentPoint(10, "Pelvis", 9, false, SLSkeletonBoneID.mPelvis, new LLVector3(0.0F, 0.0F, -0.15F), new LLQuaternion(0.0F, 0.0F, 0.0F, 1.0F));
        attachmentPoints[11] = new SLAttachmentPoint(11, "Mouth", 10, false, SLSkeletonBoneID.mHead, new LLVector3(0.12F, 0.0F, 0.001F), new LLQuaternion(0.0F, 0.0F, 0.0F, 1.0F));
        attachmentPoints[12] = new SLAttachmentPoint(12, "Chin", 11, false, SLSkeletonBoneID.mHead, new LLVector3(0.12F, 0.0F, -0.04F), new LLQuaternion(0.0F, 0.0F, 0.0F, 1.0F));
        attachmentPoints[13] = new SLAttachmentPoint(13, "Left Ear", 12, false, SLSkeletonBoneID.mHead, new LLVector3(0.015F, 0.08F, 0.017F), new LLQuaternion(0.0F, 0.0F, 0.0F, 1.0F));
        attachmentPoints[14] = new SLAttachmentPoint(14, "Right Ear", 13, false, SLSkeletonBoneID.mHead, new LLVector3(0.015F, -0.08F, 0.017F), new LLQuaternion(0.0F, 0.0F, 0.0F, 1.0F));
        attachmentPoints[15] = new SLAttachmentPoint(15, "Left Eyeball", 14, false, SLSkeletonBoneID.mEyeLeft, new LLVector3(0.0F, 0.0F, 0.0F), new LLQuaternion(0.0F, 0.0F, 0.0F, 1.0F));
        attachmentPoints[16] = new SLAttachmentPoint(16, "Right Eyeball", 15, false, SLSkeletonBoneID.mEyeRight, new LLVector3(0.0F, 0.0F, 0.0F), new LLQuaternion(0.0F, 0.0F, 0.0F, 1.0F));
        attachmentPoints[17] = new SLAttachmentPoint(17, "Nose", 16, false, SLSkeletonBoneID.mHead, new LLVector3(0.1F, 0.0F, 0.05F), new LLQuaternion(0.0F, 0.0F, 0.0F, 1.0F));
        attachmentPoints[18] = new SLAttachmentPoint(18, "R Upper Arm", 17, false, SLSkeletonBoneID.mShoulderRight, new LLVector3(0.01F, -0.13F, 0.01F), new LLQuaternion(0.0F, 0.0F, 0.0F, 1.0F));
        attachmentPoints[19] = new SLAttachmentPoint(19, "R Forearm", 18, false, SLSkeletonBoneID.mElbowRight, new LLVector3(0.0F, -0.12F, 0.0F), new LLQuaternion(0.0F, 0.0F, 0.0F, 1.0F));
        attachmentPoints[20] = new SLAttachmentPoint(20, "L Upper Arm", 19, false, SLSkeletonBoneID.mShoulderLeft, new LLVector3(0.01F, 0.15F, -0.01F), new LLQuaternion(0.0F, 0.0F, 0.0F, 1.0F));
        attachmentPoints[21] = new SLAttachmentPoint(21, "L Forearm", 20, false, SLSkeletonBoneID.mElbowLeft, new LLVector3(0.0F, 0.113F, 0.0F), new LLQuaternion(0.0F, 0.0F, 0.0F, 1.0F));
        attachmentPoints[22] = new SLAttachmentPoint(22, "Right Hip", 21, false, SLSkeletonBoneID.mHipRight, new LLVector3(0.0F, 0.0F, 0.0F), new LLQuaternion(0.0F, 0.0F, 0.0F, 1.0F));
        attachmentPoints[23] = new SLAttachmentPoint(23, "R Upper Leg", 22, false, SLSkeletonBoneID.mHipRight, new LLVector3(-0.017F, 0.041F, -0.31F), new LLQuaternion(0.0F, 0.0F, 0.0F, 1.0F));
        attachmentPoints[24] = new SLAttachmentPoint(24, "R Lower Leg", 23, false, SLSkeletonBoneID.mKneeRight, new LLVector3(-0.044F, -0.007F, -0.262F), new LLQuaternion(0.0F, 0.0F, 0.0F, 1.0F));
        attachmentPoints[25] = new SLAttachmentPoint(25, "Left Hip", 24, false, SLSkeletonBoneID.mHipLeft, new LLVector3(0.0F, 0.0F, 0.0F), new LLQuaternion(0.0F, 0.0F, 0.0F, 1.0F));
        attachmentPoints[26] = new SLAttachmentPoint(26, "L Upper Leg", 25, false, SLSkeletonBoneID.mHipLeft, new LLVector3(-0.019F, -0.034F, -0.31F), new LLQuaternion(0.0F, 0.0F, 0.0F, 1.0F));
        attachmentPoints[27] = new SLAttachmentPoint(27, "L Lower Leg", 26, false, SLSkeletonBoneID.mKneeLeft, new LLVector3(-0.044F, -0.007F, -0.261F), new LLQuaternion(0.0F, 0.0F, 0.0F, 1.0F));
        attachmentPoints[28] = new SLAttachmentPoint(28, "Stomach", 27, false, SLSkeletonBoneID.mPelvis, new LLVector3(0.092F, 0.0F, 0.088F), new LLQuaternion(0.0F, 0.0F, 0.0F, 1.0F));
        attachmentPoints[29] = new SLAttachmentPoint(29, "Left Pec", 28, false, SLSkeletonBoneID.mTorso, new LLVector3(0.104F, 0.082F, 0.247F), new LLQuaternion(0.0F, 0.0F, 0.0F, 1.0F));
        attachmentPoints[30] = new SLAttachmentPoint(30, "Right Pec", 29, false, SLSkeletonBoneID.mTorso, new LLVector3(0.104F, -0.082F, 0.247F), new LLQuaternion(0.0F, 0.0F, 0.0F, 1.0F));
        attachmentPoints[31] = new SLAttachmentPoint(31, "Center 2", -1, true, null, new LLVector3(0.0F, 0.0F, 0.0F), new LLQuaternion(0.0F, 0.0F, 0.0F, 1.0F));
        attachmentPoints[32] = new SLAttachmentPoint(32, "Top Right", -1, true, null, new LLVector3(0.0F, -0.5F, 0.5F), new LLQuaternion(0.0F, 0.0F, 0.0F, 1.0F));
        attachmentPoints[33] = new SLAttachmentPoint(33, "Top", -1, true, null, new LLVector3(0.0F, 0.0F, 0.5F), new LLQuaternion(0.0F, 0.0F, 0.0F, 1.0F));
        attachmentPoints[34] = new SLAttachmentPoint(34, "Top Left", -1, true, null, new LLVector3(0.0F, 0.5F, 0.5F), new LLQuaternion(0.0F, 0.0F, 0.0F, 1.0F));
        attachmentPoints[35] = new SLAttachmentPoint(35, "Center", -1, true, null, new LLVector3(0.0F, 0.0F, 0.0F), new LLQuaternion(0.0F, 0.0F, 0.0F, 1.0F));
        attachmentPoints[36] = new SLAttachmentPoint(36, "Bottom Left", -1, true, null, new LLVector3(0.0F, 0.5F, -0.5F), new LLQuaternion(0.0F, 0.0F, 0.0F, 1.0F));
        attachmentPoints[37] = new SLAttachmentPoint(37, "Bottom", -1, true, null, new LLVector3(0.0F, 0.0F, -0.5F), new LLQuaternion(0.0F, 0.0F, 0.0F, 1.0F));
        attachmentPoints[38] = new SLAttachmentPoint(38, "Bottom Right", -1, true, null, new LLVector3(0.0F, -0.5F, -0.5F), new LLQuaternion(0.0F, 0.0F, 0.0F, 1.0F));
        attachmentPoints[39] = new SLAttachmentPoint(39, "Neck", 30, false, SLSkeletonBoneID.mNeck, new LLVector3(0.0F, 0.0F, 0.0F), new LLQuaternion(0.0F, 0.0F, 0.0F, 1.0F));
        attachmentPoints[40] = new SLAttachmentPoint(40, "Avatar Center", 31, false, null, new LLVector3(0.0F, 0.0F, 0.0F), new LLQuaternion(0.0F, 0.0F, 0.0F, 1.0F));
        attachmentPoints[41] = new SLAttachmentPoint(41, "Left Ring Finger", 32, false, SLSkeletonBoneID.mHandRing1Left, new LLVector3(-0.006F, 0.019F, -0.002F), new LLQuaternion(0.0F, 0.0F, 0.0F, 1.0F));
        attachmentPoints[42] = new SLAttachmentPoint(42, "Right Ring Finger", 33, false, SLSkeletonBoneID.mHandRing1Right, new LLVector3(-0.006F, -0.019F, -0.002F), new LLQuaternion(0.0F, 0.0F, 0.0F, 1.0F));
        attachmentPoints[43] = new SLAttachmentPoint(43, "Tail Base", 34, false, SLSkeletonBoneID.mTail1, new LLVector3(0.0F, 0.0F, 0.0F), new LLQuaternion(0.0F, 0.0F, 0.0F, 1.0F));
        attachmentPoints[44] = new SLAttachmentPoint(44, "Tail Tip", 35, false, SLSkeletonBoneID.mTail6, new LLVector3(-0.025F, 0.0F, 0.0F), new LLQuaternion(0.0F, 0.0F, 0.0F, 1.0F));
        attachmentPoints[45] = new SLAttachmentPoint(45, "Left Wing", 36, false, SLSkeletonBoneID.mWing4Left, new LLVector3(0.0F, 0.0F, 0.0F), new LLQuaternion(0.0F, 0.0F, 0.0F, 1.0F));
        attachmentPoints[46] = new SLAttachmentPoint(46, "Right Wing", 37, false, SLSkeletonBoneID.mWing4Right, new LLVector3(0.0F, 0.0F, 0.0F), new LLQuaternion(0.0F, 0.0F, 0.0F, 1.0F));
        attachmentPoints[47] = new SLAttachmentPoint(47, "Jaw", 38, false, SLSkeletonBoneID.mFaceJaw, new LLVector3(0.0F, 0.0F, 0.0F), new LLQuaternion(0.0F, 0.0F, 0.0F, 1.0F));
        attachmentPoints[48] = new SLAttachmentPoint(48, "Alt Left Ear", 39, false, SLSkeletonBoneID.mFaceEar1Left, new LLVector3(0.0F, 0.0F, 0.0F), new LLQuaternion(0.0F, 0.0F, 0.0F, 1.0F));
        attachmentPoints[49] = new SLAttachmentPoint(49, "Alt Right Ear", 40, false, SLSkeletonBoneID.mFaceEar1Right, new LLVector3(0.0F, 0.0F, 0.0F), new LLQuaternion(0.0F, 0.0F, 0.0F, 1.0F));
        attachmentPoints[50] = new SLAttachmentPoint(50, "Alt Left Eye", 41, false, SLSkeletonBoneID.mFaceEyeAltLeft, new LLVector3(0.0F, 0.0F, 0.0F), new LLQuaternion(0.0F, 0.0F, 0.0F, 1.0F));
        attachmentPoints[51] = new SLAttachmentPoint(51, "Alt Right Eye", 42, false, SLSkeletonBoneID.mFaceEyeAltRight, new LLVector3(0.0F, 0.0F, 0.0F), new LLQuaternion(0.0F, 0.0F, 0.0F, 1.0F));
        attachmentPoints[52] = new SLAttachmentPoint(52, "Tongue", 43, false, SLSkeletonBoneID.mFaceTongueTip, new LLVector3(0.0F, 0.0F, 0.0F), new LLQuaternion(0.0F, 0.0F, 0.0F, 1.0F));
        attachmentPoints[53] = new SLAttachmentPoint(53, "Groin", 44, false, SLSkeletonBoneID.mGroin, new LLVector3(0.0F, 0.0F, 0.0F), new LLQuaternion(0.0F, 0.0F, 0.0F, 1.0F));
        attachmentPoints[54] = new SLAttachmentPoint(54, "Left Hind Foot", 45, false, SLSkeletonBoneID.mHindLimb4Left, new LLVector3(0.0F, 0.0F, 0.0F), new LLQuaternion(0.0F, 0.0F, 0.0F, 1.0F));
        attachmentPoints[55] = new SLAttachmentPoint(55, "Right Hind Foot", 46, false, SLSkeletonBoneID.mHindLimb4Right, new LLVector3(0.0F, 0.0F, 0.0F), new LLQuaternion(0.0F, 0.0F, 0.0F, 1.0F));
        pointsByName.put("Chest", attachmentPoints[1]);
        pointsByName.put("Skull", attachmentPoints[2]);
        pointsByName.put("Left Shoulder", attachmentPoints[3]);
        pointsByName.put("Right Shoulder", attachmentPoints[4]);
        pointsByName.put("Left Hand", attachmentPoints[5]);
        pointsByName.put("Right Hand", attachmentPoints[6]);
        pointsByName.put("Left Foot", attachmentPoints[7]);
        pointsByName.put("Right Foot", attachmentPoints[8]);
        pointsByName.put("Spine", attachmentPoints[9]);
        pointsByName.put("Pelvis", attachmentPoints[10]);
        pointsByName.put("Mouth", attachmentPoints[11]);
        pointsByName.put("Chin", attachmentPoints[12]);
        pointsByName.put("Left Ear", attachmentPoints[13]);
        pointsByName.put("Right Ear", attachmentPoints[14]);
        pointsByName.put("Left Eyeball", attachmentPoints[15]);
        pointsByName.put("Right Eyeball", attachmentPoints[16]);
        pointsByName.put("Nose", attachmentPoints[17]);
        pointsByName.put("R Upper Arm", attachmentPoints[18]);
        pointsByName.put("R Forearm", attachmentPoints[19]);
        pointsByName.put("L Upper Arm", attachmentPoints[20]);
        pointsByName.put("L Forearm", attachmentPoints[21]);
        pointsByName.put("Right Hip", attachmentPoints[22]);
        pointsByName.put("R Upper Leg", attachmentPoints[23]);
        pointsByName.put("R Lower Leg", attachmentPoints[24]);
        pointsByName.put("Left Hip", attachmentPoints[25]);
        pointsByName.put("L Upper Leg", attachmentPoints[26]);
        pointsByName.put("L Lower Leg", attachmentPoints[27]);
        pointsByName.put("Stomach", attachmentPoints[28]);
        pointsByName.put("Left Pec", attachmentPoints[29]);
        pointsByName.put("Right Pec", attachmentPoints[30]);
        pointsByName.put("Center 2", attachmentPoints[31]);
        pointsByName.put("Top Right", attachmentPoints[32]);
        pointsByName.put("Top", attachmentPoints[33]);
        pointsByName.put("Top Left", attachmentPoints[34]);
        pointsByName.put("Center", attachmentPoints[35]);
        pointsByName.put("Bottom Left", attachmentPoints[36]);
        pointsByName.put("Bottom", attachmentPoints[37]);
        pointsByName.put("Bottom Right", attachmentPoints[38]);
        pointsByName.put("Neck", attachmentPoints[39]);
        pointsByName.put("Avatar Center", attachmentPoints[40]);
        pointsByName.put("Left Ring Finger", attachmentPoints[41]);
        pointsByName.put("Right Ring Finger", attachmentPoints[42]);
        pointsByName.put("Tail Base", attachmentPoints[43]);
        pointsByName.put("Tail Tip", attachmentPoints[44]);
        pointsByName.put("Left Wing", attachmentPoints[45]);
        pointsByName.put("Right Wing", attachmentPoints[46]);
        pointsByName.put("Jaw", attachmentPoints[47]);
        pointsByName.put("Alt Left Ear", attachmentPoints[48]);
        pointsByName.put("Alt Right Ear", attachmentPoints[49]);
        pointsByName.put("Alt Left Eye", attachmentPoints[50]);
        pointsByName.put("Alt Right Eye", attachmentPoints[51]);
        pointsByName.put("Tongue", attachmentPoints[52]);
        pointsByName.put("Groin", attachmentPoints[53]);
        pointsByName.put("Left Hind Foot", attachmentPoints[54]);
        pointsByName.put("Right Hind Foot", attachmentPoints[55]);
    }
}
