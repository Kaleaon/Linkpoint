// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.avatar;

import java.util.HashMap;
import com.lumiyaviewer.lumiya.slproto.types.LLQuaternion;
import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import java.util.Map;

public class SLAttachmentPoint
{
    public static final int NON_HUD_ATTACHMENT_POINTS = 47;
    public static final int NUM_ATTACHMENT_POINTS = 56;
    public static final SLAttachmentPoint[] attachmentPoints;
    public static final int[] nonHUDpoints;
    public static final Map<String, SLAttachmentPoint> pointsByName;
    public final SLSkeletonBoneID bone;
    public final int id;
    public final boolean isHUD;
    public final String name;
    public final int nonHUDindex;
    public final LLVector3 position;
    public final LLQuaternion rotation;
    
    static {
        attachmentPoints = new SLAttachmentPoint[56];
        pointsByName = new HashMap<String, SLAttachmentPoint>();
        nonHUDpoints = new int[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55 };
        SLAttachmentPoint.attachmentPoints[1] = new SLAttachmentPoint(1, "Chest", 0, false, SLSkeletonBoneID.mChest, new LLVector3(0.15f, 0.0f, -0.1f), new LLQuaternion(0.5f, 0.5f, 0.5f, 0.5f));
        SLAttachmentPoint.attachmentPoints[2] = new SLAttachmentPoint(2, "Skull", 1, false, SLSkeletonBoneID.mHead, new LLVector3(0.0f, 0.0f, 0.15f), new LLQuaternion(0.0f, 0.0f, 0.707107f, 0.707107f));
        SLAttachmentPoint.attachmentPoints[3] = new SLAttachmentPoint(3, "Left Shoulder", 2, false, SLSkeletonBoneID.mCollarLeft, new LLVector3(0.0f, 0.0f, 0.08f), new LLQuaternion(0.0f, 0.0f, 0.0f, 1.0f));
        SLAttachmentPoint.attachmentPoints[4] = new SLAttachmentPoint(4, "Right Shoulder", 3, false, SLSkeletonBoneID.mCollarRight, new LLVector3(0.0f, 0.0f, 0.08f), new LLQuaternion(0.0f, 0.0f, 0.0f, 1.0f));
        SLAttachmentPoint.attachmentPoints[5] = new SLAttachmentPoint(5, "Left Hand", 4, false, SLSkeletonBoneID.mWristLeft, new LLVector3(0.0f, 0.08f, -0.02f), new LLQuaternion(0.0f, 0.0f, 0.0f, 1.0f));
        SLAttachmentPoint.attachmentPoints[6] = new SLAttachmentPoint(6, "Right Hand", 5, false, SLSkeletonBoneID.mWristRight, new LLVector3(0.0f, -0.08f, -0.02f), new LLQuaternion(0.0f, 0.0f, 0.0f, 1.0f));
        SLAttachmentPoint.attachmentPoints[7] = new SLAttachmentPoint(7, "Left Foot", 6, false, SLSkeletonBoneID.mFootLeft, new LLVector3(0.0f, 0.0f, 0.0f), new LLQuaternion(0.0f, 0.0f, 0.0f, 1.0f));
        SLAttachmentPoint.attachmentPoints[8] = new SLAttachmentPoint(8, "Right Foot", 7, false, SLSkeletonBoneID.mFootRight, new LLVector3(0.0f, 0.0f, 0.0f), new LLQuaternion(0.0f, 0.0f, 0.0f, 1.0f));
        SLAttachmentPoint.attachmentPoints[9] = new SLAttachmentPoint(9, "Spine", 8, false, SLSkeletonBoneID.mChest, new LLVector3(-0.15f, 0.0f, -0.1f), new LLQuaternion(-0.5f, -0.5f, 0.5f, 0.5f));
        SLAttachmentPoint.attachmentPoints[10] = new SLAttachmentPoint(10, "Pelvis", 9, false, SLSkeletonBoneID.mPelvis, new LLVector3(0.0f, 0.0f, -0.15f), new LLQuaternion(0.0f, 0.0f, 0.0f, 1.0f));
        SLAttachmentPoint.attachmentPoints[11] = new SLAttachmentPoint(11, "Mouth", 10, false, SLSkeletonBoneID.mHead, new LLVector3(0.12f, 0.0f, 0.001f), new LLQuaternion(0.0f, 0.0f, 0.0f, 1.0f));
        SLAttachmentPoint.attachmentPoints[12] = new SLAttachmentPoint(12, "Chin", 11, false, SLSkeletonBoneID.mHead, new LLVector3(0.12f, 0.0f, -0.04f), new LLQuaternion(0.0f, 0.0f, 0.0f, 1.0f));
        SLAttachmentPoint.attachmentPoints[13] = new SLAttachmentPoint(13, "Left Ear", 12, false, SLSkeletonBoneID.mHead, new LLVector3(0.015f, 0.08f, 0.017f), new LLQuaternion(0.0f, 0.0f, 0.0f, 1.0f));
        SLAttachmentPoint.attachmentPoints[14] = new SLAttachmentPoint(14, "Right Ear", 13, false, SLSkeletonBoneID.mHead, new LLVector3(0.015f, -0.08f, 0.017f), new LLQuaternion(0.0f, 0.0f, 0.0f, 1.0f));
        SLAttachmentPoint.attachmentPoints[15] = new SLAttachmentPoint(15, "Left Eyeball", 14, false, SLSkeletonBoneID.mEyeLeft, new LLVector3(0.0f, 0.0f, 0.0f), new LLQuaternion(0.0f, 0.0f, 0.0f, 1.0f));
        SLAttachmentPoint.attachmentPoints[16] = new SLAttachmentPoint(16, "Right Eyeball", 15, false, SLSkeletonBoneID.mEyeRight, new LLVector3(0.0f, 0.0f, 0.0f), new LLQuaternion(0.0f, 0.0f, 0.0f, 1.0f));
        SLAttachmentPoint.attachmentPoints[17] = new SLAttachmentPoint(17, "Nose", 16, false, SLSkeletonBoneID.mHead, new LLVector3(0.1f, 0.0f, 0.05f), new LLQuaternion(0.0f, 0.0f, 0.0f, 1.0f));
        SLAttachmentPoint.attachmentPoints[18] = new SLAttachmentPoint(18, "R Upper Arm", 17, false, SLSkeletonBoneID.mShoulderRight, new LLVector3(0.01f, -0.13f, 0.01f), new LLQuaternion(0.0f, 0.0f, 0.0f, 1.0f));
        SLAttachmentPoint.attachmentPoints[19] = new SLAttachmentPoint(19, "R Forearm", 18, false, SLSkeletonBoneID.mElbowRight, new LLVector3(0.0f, -0.12f, 0.0f), new LLQuaternion(0.0f, 0.0f, 0.0f, 1.0f));
        SLAttachmentPoint.attachmentPoints[20] = new SLAttachmentPoint(20, "L Upper Arm", 19, false, SLSkeletonBoneID.mShoulderLeft, new LLVector3(0.01f, 0.15f, -0.01f), new LLQuaternion(0.0f, 0.0f, 0.0f, 1.0f));
        SLAttachmentPoint.attachmentPoints[21] = new SLAttachmentPoint(21, "L Forearm", 20, false, SLSkeletonBoneID.mElbowLeft, new LLVector3(0.0f, 0.113f, 0.0f), new LLQuaternion(0.0f, 0.0f, 0.0f, 1.0f));
        SLAttachmentPoint.attachmentPoints[22] = new SLAttachmentPoint(22, "Right Hip", 21, false, SLSkeletonBoneID.mHipRight, new LLVector3(0.0f, 0.0f, 0.0f), new LLQuaternion(0.0f, 0.0f, 0.0f, 1.0f));
        SLAttachmentPoint.attachmentPoints[23] = new SLAttachmentPoint(23, "R Upper Leg", 22, false, SLSkeletonBoneID.mHipRight, new LLVector3(-0.017f, 0.041f, -0.31f), new LLQuaternion(0.0f, 0.0f, 0.0f, 1.0f));
        SLAttachmentPoint.attachmentPoints[24] = new SLAttachmentPoint(24, "R Lower Leg", 23, false, SLSkeletonBoneID.mKneeRight, new LLVector3(-0.044f, -0.007f, -0.262f), new LLQuaternion(0.0f, 0.0f, 0.0f, 1.0f));
        SLAttachmentPoint.attachmentPoints[25] = new SLAttachmentPoint(25, "Left Hip", 24, false, SLSkeletonBoneID.mHipLeft, new LLVector3(0.0f, 0.0f, 0.0f), new LLQuaternion(0.0f, 0.0f, 0.0f, 1.0f));
        SLAttachmentPoint.attachmentPoints[26] = new SLAttachmentPoint(26, "L Upper Leg", 25, false, SLSkeletonBoneID.mHipLeft, new LLVector3(-0.019f, -0.034f, -0.31f), new LLQuaternion(0.0f, 0.0f, 0.0f, 1.0f));
        SLAttachmentPoint.attachmentPoints[27] = new SLAttachmentPoint(27, "L Lower Leg", 26, false, SLSkeletonBoneID.mKneeLeft, new LLVector3(-0.044f, -0.007f, -0.261f), new LLQuaternion(0.0f, 0.0f, 0.0f, 1.0f));
        SLAttachmentPoint.attachmentPoints[28] = new SLAttachmentPoint(28, "Stomach", 27, false, SLSkeletonBoneID.mPelvis, new LLVector3(0.092f, 0.0f, 0.088f), new LLQuaternion(0.0f, 0.0f, 0.0f, 1.0f));
        SLAttachmentPoint.attachmentPoints[29] = new SLAttachmentPoint(29, "Left Pec", 28, false, SLSkeletonBoneID.mTorso, new LLVector3(0.104f, 0.082f, 0.247f), new LLQuaternion(0.0f, 0.0f, 0.0f, 1.0f));
        SLAttachmentPoint.attachmentPoints[30] = new SLAttachmentPoint(30, "Right Pec", 29, false, SLSkeletonBoneID.mTorso, new LLVector3(0.104f, -0.082f, 0.247f), new LLQuaternion(0.0f, 0.0f, 0.0f, 1.0f));
        SLAttachmentPoint.attachmentPoints[31] = new SLAttachmentPoint(31, "Center 2", -1, true, null, new LLVector3(0.0f, 0.0f, 0.0f), new LLQuaternion(0.0f, 0.0f, 0.0f, 1.0f));
        SLAttachmentPoint.attachmentPoints[32] = new SLAttachmentPoint(32, "Top Right", -1, true, null, new LLVector3(0.0f, -0.5f, 0.5f), new LLQuaternion(0.0f, 0.0f, 0.0f, 1.0f));
        SLAttachmentPoint.attachmentPoints[33] = new SLAttachmentPoint(33, "Top", -1, true, null, new LLVector3(0.0f, 0.0f, 0.5f), new LLQuaternion(0.0f, 0.0f, 0.0f, 1.0f));
        SLAttachmentPoint.attachmentPoints[34] = new SLAttachmentPoint(34, "Top Left", -1, true, null, new LLVector3(0.0f, 0.5f, 0.5f), new LLQuaternion(0.0f, 0.0f, 0.0f, 1.0f));
        SLAttachmentPoint.attachmentPoints[35] = new SLAttachmentPoint(35, "Center", -1, true, null, new LLVector3(0.0f, 0.0f, 0.0f), new LLQuaternion(0.0f, 0.0f, 0.0f, 1.0f));
        SLAttachmentPoint.attachmentPoints[36] = new SLAttachmentPoint(36, "Bottom Left", -1, true, null, new LLVector3(0.0f, 0.5f, -0.5f), new LLQuaternion(0.0f, 0.0f, 0.0f, 1.0f));
        SLAttachmentPoint.attachmentPoints[37] = new SLAttachmentPoint(37, "Bottom", -1, true, null, new LLVector3(0.0f, 0.0f, -0.5f), new LLQuaternion(0.0f, 0.0f, 0.0f, 1.0f));
        SLAttachmentPoint.attachmentPoints[38] = new SLAttachmentPoint(38, "Bottom Right", -1, true, null, new LLVector3(0.0f, -0.5f, -0.5f), new LLQuaternion(0.0f, 0.0f, 0.0f, 1.0f));
        SLAttachmentPoint.attachmentPoints[39] = new SLAttachmentPoint(39, "Neck", 30, false, SLSkeletonBoneID.mNeck, new LLVector3(0.0f, 0.0f, 0.0f), new LLQuaternion(0.0f, 0.0f, 0.0f, 1.0f));
        SLAttachmentPoint.attachmentPoints[40] = new SLAttachmentPoint(40, "Avatar Center", 31, false, null, new LLVector3(0.0f, 0.0f, 0.0f), new LLQuaternion(0.0f, 0.0f, 0.0f, 1.0f));
        SLAttachmentPoint.attachmentPoints[41] = new SLAttachmentPoint(41, "Left Ring Finger", 32, false, SLSkeletonBoneID.mHandRing1Left, new LLVector3(-0.006f, 0.019f, -0.002f), new LLQuaternion(0.0f, 0.0f, 0.0f, 1.0f));
        SLAttachmentPoint.attachmentPoints[42] = new SLAttachmentPoint(42, "Right Ring Finger", 33, false, SLSkeletonBoneID.mHandRing1Right, new LLVector3(-0.006f, -0.019f, -0.002f), new LLQuaternion(0.0f, 0.0f, 0.0f, 1.0f));
        SLAttachmentPoint.attachmentPoints[43] = new SLAttachmentPoint(43, "Tail Base", 34, false, SLSkeletonBoneID.mTail1, new LLVector3(0.0f, 0.0f, 0.0f), new LLQuaternion(0.0f, 0.0f, 0.0f, 1.0f));
        SLAttachmentPoint.attachmentPoints[44] = new SLAttachmentPoint(44, "Tail Tip", 35, false, SLSkeletonBoneID.mTail6, new LLVector3(-0.025f, 0.0f, 0.0f), new LLQuaternion(0.0f, 0.0f, 0.0f, 1.0f));
        SLAttachmentPoint.attachmentPoints[45] = new SLAttachmentPoint(45, "Left Wing", 36, false, SLSkeletonBoneID.mWing4Left, new LLVector3(0.0f, 0.0f, 0.0f), new LLQuaternion(0.0f, 0.0f, 0.0f, 1.0f));
        SLAttachmentPoint.attachmentPoints[46] = new SLAttachmentPoint(46, "Right Wing", 37, false, SLSkeletonBoneID.mWing4Right, new LLVector3(0.0f, 0.0f, 0.0f), new LLQuaternion(0.0f, 0.0f, 0.0f, 1.0f));
        SLAttachmentPoint.attachmentPoints[47] = new SLAttachmentPoint(47, "Jaw", 38, false, SLSkeletonBoneID.mFaceJaw, new LLVector3(0.0f, 0.0f, 0.0f), new LLQuaternion(0.0f, 0.0f, 0.0f, 1.0f));
        SLAttachmentPoint.attachmentPoints[48] = new SLAttachmentPoint(48, "Alt Left Ear", 39, false, SLSkeletonBoneID.mFaceEar1Left, new LLVector3(0.0f, 0.0f, 0.0f), new LLQuaternion(0.0f, 0.0f, 0.0f, 1.0f));
        SLAttachmentPoint.attachmentPoints[49] = new SLAttachmentPoint(49, "Alt Right Ear", 40, false, SLSkeletonBoneID.mFaceEar1Right, new LLVector3(0.0f, 0.0f, 0.0f), new LLQuaternion(0.0f, 0.0f, 0.0f, 1.0f));
        SLAttachmentPoint.attachmentPoints[50] = new SLAttachmentPoint(50, "Alt Left Eye", 41, false, SLSkeletonBoneID.mFaceEyeAltLeft, new LLVector3(0.0f, 0.0f, 0.0f), new LLQuaternion(0.0f, 0.0f, 0.0f, 1.0f));
        SLAttachmentPoint.attachmentPoints[51] = new SLAttachmentPoint(51, "Alt Right Eye", 42, false, SLSkeletonBoneID.mFaceEyeAltRight, new LLVector3(0.0f, 0.0f, 0.0f), new LLQuaternion(0.0f, 0.0f, 0.0f, 1.0f));
        SLAttachmentPoint.attachmentPoints[52] = new SLAttachmentPoint(52, "Tongue", 43, false, SLSkeletonBoneID.mFaceTongueTip, new LLVector3(0.0f, 0.0f, 0.0f), new LLQuaternion(0.0f, 0.0f, 0.0f, 1.0f));
        SLAttachmentPoint.attachmentPoints[53] = new SLAttachmentPoint(53, "Groin", 44, false, SLSkeletonBoneID.mGroin, new LLVector3(0.0f, 0.0f, 0.0f), new LLQuaternion(0.0f, 0.0f, 0.0f, 1.0f));
        SLAttachmentPoint.attachmentPoints[54] = new SLAttachmentPoint(54, "Left Hind Foot", 45, false, SLSkeletonBoneID.mHindLimb4Left, new LLVector3(0.0f, 0.0f, 0.0f), new LLQuaternion(0.0f, 0.0f, 0.0f, 1.0f));
        SLAttachmentPoint.attachmentPoints[55] = new SLAttachmentPoint(55, "Right Hind Foot", 46, false, SLSkeletonBoneID.mHindLimb4Right, new LLVector3(0.0f, 0.0f, 0.0f), new LLQuaternion(0.0f, 0.0f, 0.0f, 1.0f));
        SLAttachmentPoint.pointsByName.put("Chest", SLAttachmentPoint.attachmentPoints[1]);
        SLAttachmentPoint.pointsByName.put("Skull", SLAttachmentPoint.attachmentPoints[2]);
        SLAttachmentPoint.pointsByName.put("Left Shoulder", SLAttachmentPoint.attachmentPoints[3]);
        SLAttachmentPoint.pointsByName.put("Right Shoulder", SLAttachmentPoint.attachmentPoints[4]);
        SLAttachmentPoint.pointsByName.put("Left Hand", SLAttachmentPoint.attachmentPoints[5]);
        SLAttachmentPoint.pointsByName.put("Right Hand", SLAttachmentPoint.attachmentPoints[6]);
        SLAttachmentPoint.pointsByName.put("Left Foot", SLAttachmentPoint.attachmentPoints[7]);
        SLAttachmentPoint.pointsByName.put("Right Foot", SLAttachmentPoint.attachmentPoints[8]);
        SLAttachmentPoint.pointsByName.put("Spine", SLAttachmentPoint.attachmentPoints[9]);
        SLAttachmentPoint.pointsByName.put("Pelvis", SLAttachmentPoint.attachmentPoints[10]);
        SLAttachmentPoint.pointsByName.put("Mouth", SLAttachmentPoint.attachmentPoints[11]);
        SLAttachmentPoint.pointsByName.put("Chin", SLAttachmentPoint.attachmentPoints[12]);
        SLAttachmentPoint.pointsByName.put("Left Ear", SLAttachmentPoint.attachmentPoints[13]);
        SLAttachmentPoint.pointsByName.put("Right Ear", SLAttachmentPoint.attachmentPoints[14]);
        SLAttachmentPoint.pointsByName.put("Left Eyeball", SLAttachmentPoint.attachmentPoints[15]);
        SLAttachmentPoint.pointsByName.put("Right Eyeball", SLAttachmentPoint.attachmentPoints[16]);
        SLAttachmentPoint.pointsByName.put("Nose", SLAttachmentPoint.attachmentPoints[17]);
        SLAttachmentPoint.pointsByName.put("R Upper Arm", SLAttachmentPoint.attachmentPoints[18]);
        SLAttachmentPoint.pointsByName.put("R Forearm", SLAttachmentPoint.attachmentPoints[19]);
        SLAttachmentPoint.pointsByName.put("L Upper Arm", SLAttachmentPoint.attachmentPoints[20]);
        SLAttachmentPoint.pointsByName.put("L Forearm", SLAttachmentPoint.attachmentPoints[21]);
        SLAttachmentPoint.pointsByName.put("Right Hip", SLAttachmentPoint.attachmentPoints[22]);
        SLAttachmentPoint.pointsByName.put("R Upper Leg", SLAttachmentPoint.attachmentPoints[23]);
        SLAttachmentPoint.pointsByName.put("R Lower Leg", SLAttachmentPoint.attachmentPoints[24]);
        SLAttachmentPoint.pointsByName.put("Left Hip", SLAttachmentPoint.attachmentPoints[25]);
        SLAttachmentPoint.pointsByName.put("L Upper Leg", SLAttachmentPoint.attachmentPoints[26]);
        SLAttachmentPoint.pointsByName.put("L Lower Leg", SLAttachmentPoint.attachmentPoints[27]);
        SLAttachmentPoint.pointsByName.put("Stomach", SLAttachmentPoint.attachmentPoints[28]);
        SLAttachmentPoint.pointsByName.put("Left Pec", SLAttachmentPoint.attachmentPoints[29]);
        SLAttachmentPoint.pointsByName.put("Right Pec", SLAttachmentPoint.attachmentPoints[30]);
        SLAttachmentPoint.pointsByName.put("Center 2", SLAttachmentPoint.attachmentPoints[31]);
        SLAttachmentPoint.pointsByName.put("Top Right", SLAttachmentPoint.attachmentPoints[32]);
        SLAttachmentPoint.pointsByName.put("Top", SLAttachmentPoint.attachmentPoints[33]);
        SLAttachmentPoint.pointsByName.put("Top Left", SLAttachmentPoint.attachmentPoints[34]);
        SLAttachmentPoint.pointsByName.put("Center", SLAttachmentPoint.attachmentPoints[35]);
        SLAttachmentPoint.pointsByName.put("Bottom Left", SLAttachmentPoint.attachmentPoints[36]);
        SLAttachmentPoint.pointsByName.put("Bottom", SLAttachmentPoint.attachmentPoints[37]);
        SLAttachmentPoint.pointsByName.put("Bottom Right", SLAttachmentPoint.attachmentPoints[38]);
        SLAttachmentPoint.pointsByName.put("Neck", SLAttachmentPoint.attachmentPoints[39]);
        SLAttachmentPoint.pointsByName.put("Avatar Center", SLAttachmentPoint.attachmentPoints[40]);
        SLAttachmentPoint.pointsByName.put("Left Ring Finger", SLAttachmentPoint.attachmentPoints[41]);
        SLAttachmentPoint.pointsByName.put("Right Ring Finger", SLAttachmentPoint.attachmentPoints[42]);
        SLAttachmentPoint.pointsByName.put("Tail Base", SLAttachmentPoint.attachmentPoints[43]);
        SLAttachmentPoint.pointsByName.put("Tail Tip", SLAttachmentPoint.attachmentPoints[44]);
        SLAttachmentPoint.pointsByName.put("Left Wing", SLAttachmentPoint.attachmentPoints[45]);
        SLAttachmentPoint.pointsByName.put("Right Wing", SLAttachmentPoint.attachmentPoints[46]);
        SLAttachmentPoint.pointsByName.put("Jaw", SLAttachmentPoint.attachmentPoints[47]);
        SLAttachmentPoint.pointsByName.put("Alt Left Ear", SLAttachmentPoint.attachmentPoints[48]);
        SLAttachmentPoint.pointsByName.put("Alt Right Ear", SLAttachmentPoint.attachmentPoints[49]);
        SLAttachmentPoint.pointsByName.put("Alt Left Eye", SLAttachmentPoint.attachmentPoints[50]);
        SLAttachmentPoint.pointsByName.put("Alt Right Eye", SLAttachmentPoint.attachmentPoints[51]);
        SLAttachmentPoint.pointsByName.put("Tongue", SLAttachmentPoint.attachmentPoints[52]);
        SLAttachmentPoint.pointsByName.put("Groin", SLAttachmentPoint.attachmentPoints[53]);
        SLAttachmentPoint.pointsByName.put("Left Hind Foot", SLAttachmentPoint.attachmentPoints[54]);
        SLAttachmentPoint.pointsByName.put("Right Hind Foot", SLAttachmentPoint.attachmentPoints[55]);
    }
    
    private SLAttachmentPoint(final int id, final String name, final int nonHUDindex, final boolean isHUD, final SLSkeletonBoneID bone, final LLVector3 position, final LLQuaternion rotation) {
        this.id = id;
        this.name = name;
        this.nonHUDindex = nonHUDindex;
        this.isHUD = isHUD;
        this.bone = bone;
        this.position = position;
        this.rotation = rotation;
    }
}
