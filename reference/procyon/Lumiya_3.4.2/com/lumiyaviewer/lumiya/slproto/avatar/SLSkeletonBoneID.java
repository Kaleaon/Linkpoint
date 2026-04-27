// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.avatar;

import com.google.common.collect.ImmutableMap;

public enum SLSkeletonBoneID
{
    BELLY("BELLY", 28, false, false, -1), 
    BUTT("BUTT", 27, false, false, -1), 
    CHEST("CHEST", 32, false, false, -1), 
    HEAD("HEAD", 37, false, false, -1), 
    LEFT_HANDLE("LEFT_HANDLE", 29, false, false, -1), 
    LEFT_PEC("LEFT_PEC", 33, false, false, -1), 
    LOWER_BACK("LOWER_BACK", 31, false, false, -1), 
    L_CLAVICLE("L_CLAVICLE", 38, false, false, -1), 
    L_FOOT("L_FOOT", 51, false, false, -1), 
    L_HAND("L_HAND", 41, false, false, -1), 
    L_LOWER_ARM("L_LOWER_ARM", 40, false, false, -1), 
    L_LOWER_LEG("L_LOWER_LEG", 50, false, false, -1), 
    L_UPPER_ARM("L_UPPER_ARM", 39, false, false, -1), 
    L_UPPER_LEG("L_UPPER_LEG", 49, false, false, -1), 
    NECK("NECK", 36, false, false, -1);
    
    public static final int NUM_ANIMATED = 133;
    public static final int NUM_BASE_BONES = 52;
    public static final int NUM_BASE_JOINTS = 26;
    public static final int NUM_JOINTS = 133;
    
    PELVIS("PELVIS", 26, false, false, -1), 
    RIGHT_HANDLE("RIGHT_HANDLE", 30, false, false, -1), 
    RIGHT_PEC("RIGHT_PEC", 34, false, false, -1), 
    R_CLAVICLE("R_CLAVICLE", 42, false, false, -1), 
    R_FOOT("R_FOOT", 48, false, false, -1), 
    R_HAND("R_HAND", 45, false, false, -1), 
    R_LOWER_ARM("R_LOWER_ARM", 44, false, false, -1), 
    R_LOWER_LEG("R_LOWER_LEG", 47, false, false, -1), 
    R_UPPER_ARM("R_UPPER_ARM", 43, false, false, -1), 
    R_UPPER_LEG("R_UPPER_LEG", 46, false, false, -1), 
    UPPER_BACK("UPPER_BACK", 35, false, false, -1);
    
    public static final SLSkeletonBoneID[] VALUES;
    public static ImmutableMap<String, SLSkeletonBoneID> bones;
    
    mAnkleLeft("mAnkleLeft", 23, true, false, 23), 
    mAnkleRight("mAnkleRight", 18, true, false, 18), 
    mChest("mChest", 2, true, false, 2), 
    mCollarLeft("mCollarLeft", 8, true, false, 8), 
    mCollarRight("mCollarRight", 12, true, false, 12), 
    mElbowLeft("mElbowLeft", 10, true, false, 10), 
    mElbowRight("mElbowRight", 14, true, false, 14), 
    mEyeLeft("mEyeLeft", 7, true, false, 7), 
    mEyeRight("mEyeRight", 6, true, false, 6), 
    mFaceCheekLowerLeft("mFaceCheekLowerLeft", 78, true, true, 52), 
    mFaceCheekLowerRight("mFaceCheekLowerRight", 80, true, true, 54), 
    mFaceCheekUpperLeft("mFaceCheekUpperLeft", 79, true, true, 53), 
    mFaceCheekUpperRight("mFaceCheekUpperRight", 81, true, true, 55), 
    mFaceChin("mFaceChin", 83, true, true, 57), 
    mFaceEar1Left("mFaceEar1Left", 71, true, true, 45), 
    mFaceEar1Right("mFaceEar1Right", 73, true, true, 47), 
    mFaceEar2Left("mFaceEar2Left", 72, true, true, 46), 
    mFaceEar2Right("mFaceEar2Right", 74, true, true, 48), 
    mFaceEyeAltLeft("mFaceEyeAltLeft", 58, true, true, 32), 
    mFaceEyeAltRight("mFaceEyeAltRight", 57, true, true, 31), 
    mFaceEyeLidLowerLeft("mFaceEyeLidLowerLeft", 68, true, true, 42), 
    mFaceEyeLidLowerRight("mFaceEyeLidLowerRight", 70, true, true, 44), 
    mFaceEyeLidUpperLeft("mFaceEyeLidUpperLeft", 67, true, true, 41), 
    mFaceEyeLidUpperRight("mFaceEyeLidUpperRight", 69, true, true, 43), 
    mFaceEyebrowCenterLeft("mFaceEyebrowCenterLeft", 62, true, true, 36), 
    mFaceEyebrowCenterRight("mFaceEyebrowCenterRight", 65, true, true, 39), 
    mFaceEyebrowInnerLeft("mFaceEyebrowInnerLeft", 63, true, true, 37), 
    mFaceEyebrowInnerRight("mFaceEyebrowInnerRight", 66, true, true, 40), 
    mFaceEyebrowOuterLeft("mFaceEyebrowOuterLeft", 61, true, true, 35), 
    mFaceEyebrowOuterRight("mFaceEyebrowOuterRight", 64, true, true, 38), 
    mFaceEyecornerInnerLeft("mFaceEyecornerInnerLeft", 99, true, true, 73), 
    mFaceEyecornerInnerRight("mFaceEyecornerInnerRight", 100, true, true, 74), 
    mFaceForeheadCenter("mFaceForeheadCenter", 91, true, true, 65), 
    mFaceForeheadLeft("mFaceForeheadLeft", 59, true, true, 33), 
    mFaceForeheadRight("mFaceForeheadRight", 60, true, true, 34), 
    mFaceJaw("mFaceJaw", 82, true, true, 56), 
    mFaceJawShaper("mFaceJawShaper", 90, true, true, 64), 
    mFaceLipCornerLeft("mFaceLipCornerLeft", 96, true, true, 70), 
    mFaceLipCornerRight("mFaceLipCornerRight", 97, true, true, 71), 
    mFaceLipLowerCenter("mFaceLipLowerCenter", 87, true, true, 61), 
    mFaceLipLowerLeft("mFaceLipLowerLeft", 85, true, true, 59), 
    mFaceLipLowerRight("mFaceLipLowerRight", 86, true, true, 60), 
    mFaceLipUpperCenter("mFaceLipUpperCenter", 98, true, true, 72), 
    mFaceLipUpperLeft("mFaceLipUpperLeft", 94, true, true, 68), 
    mFaceLipUpperRight("mFaceLipUpperRight", 95, true, true, 69), 
    mFaceNoseBase("mFaceNoseBase", 92, true, true, 66), 
    mFaceNoseBridge("mFaceNoseBridge", 101, true, true, 75), 
    mFaceNoseCenter("mFaceNoseCenter", 76, true, true, 50), 
    mFaceNoseLeft("mFaceNoseLeft", 75, true, true, 49), 
    mFaceNoseRight("mFaceNoseRight", 77, true, true, 51), 
    mFaceRoot("mFaceRoot", 56, true, true, 30), 
    mFaceTeethLower("mFaceTeethLower", 84, true, true, 58), 
    mFaceTeethUpper("mFaceTeethUpper", 93, true, true, 67), 
    mFaceTongueBase("mFaceTongueBase", 88, true, true, 62), 
    mFaceTongueTip("mFaceTongueTip", 89, true, true, 63), 
    mFootLeft("mFootLeft", 24, true, false, 24), 
    mFootRight("mFootRight", 19, true, false, 19), 
    mGroin("mGroin", 149, true, true, 123), 
    mHandIndex1Left("mHandIndex1Left", 105, true, true, 79), 
    mHandIndex1Right("mHandIndex1Right", 120, true, true, 94), 
    mHandIndex2Left("mHandIndex2Left", 106, true, true, 80), 
    mHandIndex2Right("mHandIndex2Right", 121, true, true, 95), 
    mHandIndex3Left("mHandIndex3Left", 107, true, true, 81), 
    mHandIndex3Right("mHandIndex3Right", 122, true, true, 96), 
    mHandMiddle1Left("mHandMiddle1Left", 102, true, true, 76), 
    mHandMiddle1Right("mHandMiddle1Right", 117, true, true, 91), 
    mHandMiddle2Left("mHandMiddle2Left", 103, true, true, 77), 
    mHandMiddle2Right("mHandMiddle2Right", 118, true, true, 92), 
    mHandMiddle3Left("mHandMiddle3Left", 104, true, true, 78), 
    mHandMiddle3Right("mHandMiddle3Right", 119, true, true, 93), 
    mHandPinky1Left("mHandPinky1Left", 111, true, true, 85), 
    mHandPinky1Right("mHandPinky1Right", 126, true, true, 100), 
    mHandPinky2Left("mHandPinky2Left", 112, true, true, 86), 
    mHandPinky2Right("mHandPinky2Right", 127, true, true, 101), 
    mHandPinky3Left("mHandPinky3Left", 113, true, true, 87), 
    mHandPinky3Right("mHandPinky3Right", 128, true, true, 102), 
    mHandRing1Left("mHandRing1Left", 108, true, true, 82), 
    mHandRing1Right("mHandRing1Right", 123, true, true, 97), 
    mHandRing2Left("mHandRing2Left", 109, true, true, 83), 
    mHandRing2Right("mHandRing2Right", 124, true, true, 98), 
    mHandRing3Left("mHandRing3Left", 110, true, true, 84), 
    mHandRing3Right("mHandRing3Right", 125, true, true, 99), 
    mHandThumb1Left("mHandThumb1Left", 114, true, true, 88), 
    mHandThumb1Right("mHandThumb1Right", 129, true, true, 103), 
    mHandThumb2Left("mHandThumb2Left", 115, true, true, 89), 
    mHandThumb2Right("mHandThumb2Right", 130, true, true, 104), 
    mHandThumb3Left("mHandThumb3Left", 116, true, true, 90), 
    mHandThumb3Right("mHandThumb3Right", 131, true, true, 105), 
    mHead("mHead", 4, true, false, 4), 
    mHindLimb1Left("mHindLimb1Left", 151, true, true, 125), 
    mHindLimb1Right("mHindLimb1Right", 155, true, true, 129), 
    mHindLimb2Left("mHindLimb2Left", 152, true, true, 126), 
    mHindLimb2Right("mHindLimb2Right", 156, true, true, 130), 
    mHindLimb3Left("mHindLimb3Left", 153, true, true, 127), 
    mHindLimb3Right("mHindLimb3Right", 157, true, true, 131), 
    mHindLimb4Left("mHindLimb4Left", 154, true, true, 128), 
    mHindLimb4Right("mHindLimb4Right", 158, true, true, 132), 
    mHindLimbsRoot("mHindLimbsRoot", 150, true, true, 124), 
    mHipLeft("mHipLeft", 21, true, false, 21), 
    mHipRight("mHipRight", 16, true, false, 16), 
    mKneeLeft("mKneeLeft", 22, true, false, 22), 
    mKneeRight("mKneeRight", 17, true, false, 17), 
    mNeck("mNeck", 3, true, false, 3), 
    mPelvis("mPelvis", 0, true, false, 0), 
    mShoulderLeft("mShoulderLeft", 9, true, false, 9), 
    mShoulderRight("mShoulderRight", 13, true, false, 13), 
    mSkull("mSkull", 5, true, false, 5), 
    mSpine1("mSpine1", 52, true, true, 26), 
    mSpine2("mSpine2", 53, true, true, 27), 
    mSpine3("mSpine3", 54, true, true, 28), 
    mSpine4("mSpine4", 55, true, true, 29), 
    mTail1("mTail1", 143, true, true, 117), 
    mTail2("mTail2", 144, true, true, 118), 
    mTail3("mTail3", 145, true, true, 119), 
    mTail4("mTail4", 146, true, true, 120), 
    mTail5("mTail5", 147, true, true, 121), 
    mTail6("mTail6", 148, true, true, 122), 
    mToeLeft("mToeLeft", 25, true, false, 25), 
    mToeRight("mToeRight", 20, true, false, 20), 
    mTorso("mTorso", 1, true, false, 1), 
    mWing1Left("mWing1Left", 133, true, true, 107), 
    mWing1Right("mWing1Right", 138, true, true, 112), 
    mWing2Left("mWing2Left", 134, true, true, 108), 
    mWing2Right("mWing2Right", 139, true, true, 113), 
    mWing3Left("mWing3Left", 135, true, true, 109), 
    mWing3Right("mWing3Right", 140, true, true, 114), 
    mWing4FanLeft("mWing4FanLeft", 137, true, true, 111), 
    mWing4FanRight("mWing4FanRight", 142, true, true, 116), 
    mWing4Left("mWing4Left", 136, true, true, 110), 
    mWing4Right("mWing4Right", 141, true, true, 115), 
    mWingsRoot("mWingsRoot", 132, true, true, 106), 
    mWristLeft("mWristLeft", 11, true, false, 11), 
    mWristRight("mWristRight", 15, true, false, 15);
    
    public final int animatedIndex;
    public final boolean isExtended;
    public final boolean isJoint;
    
    static {
        VALUES = values();
        SLSkeletonBoneID.bones = new ImmutableMap.Builder<String, SLSkeletonBoneID>().put("avatar_mPelvis", SLSkeletonBoneID.mPelvis).put("mPelvis", SLSkeletonBoneID.mPelvis).put("hip", SLSkeletonBoneID.mPelvis).put("mTorso", SLSkeletonBoneID.mTorso).put("avatar_mTorso", SLSkeletonBoneID.mTorso).put("abdomen", SLSkeletonBoneID.mTorso).put("avatar_mChest", SLSkeletonBoneID.mChest).put("chest", SLSkeletonBoneID.mChest).put("mChest", SLSkeletonBoneID.mChest).put("mNeck", SLSkeletonBoneID.mNeck).put("neck", SLSkeletonBoneID.mNeck).put("avatar_mNeck", SLSkeletonBoneID.mNeck).put("head", SLSkeletonBoneID.mHead).put("avatar_mHead", SLSkeletonBoneID.mHead).put("mHead", SLSkeletonBoneID.mHead).put("mSkull", SLSkeletonBoneID.mSkull).put("figureHair", SLSkeletonBoneID.mSkull).put("avatar_mSkull", SLSkeletonBoneID.mSkull).put("mEyeRight", SLSkeletonBoneID.mEyeRight).put("avatar_mEyeRight", SLSkeletonBoneID.mEyeRight).put("mEyeLeft", SLSkeletonBoneID.mEyeLeft).put("avatar_mEyeLeft", SLSkeletonBoneID.mEyeLeft).put("avatar_mCollarLeft", SLSkeletonBoneID.mCollarLeft).put("mCollarLeft", SLSkeletonBoneID.mCollarLeft).put("lCollar", SLSkeletonBoneID.mCollarLeft).put("avatar_mShoulderLeft", SLSkeletonBoneID.mShoulderLeft).put("lShldr", SLSkeletonBoneID.mShoulderLeft).put("mShoulderLeft", SLSkeletonBoneID.mShoulderLeft).put("lForeArm", SLSkeletonBoneID.mElbowLeft).put("avatar_mElbowLeft", SLSkeletonBoneID.mElbowLeft).put("mElbowLeft", SLSkeletonBoneID.mElbowLeft).put("avatar_mWristLeft", SLSkeletonBoneID.mWristLeft).put("mWristLeft", SLSkeletonBoneID.mWristLeft).put("lHand", SLSkeletonBoneID.mWristLeft).put("rCollar", SLSkeletonBoneID.mCollarRight).put("mCollarRight", SLSkeletonBoneID.mCollarRight).put("avatar_mCollarRight", SLSkeletonBoneID.mCollarRight).put("mShoulderRight", SLSkeletonBoneID.mShoulderRight).put("rShldr", SLSkeletonBoneID.mShoulderRight).put("avatar_mShoulderRight", SLSkeletonBoneID.mShoulderRight).put("mElbowRight", SLSkeletonBoneID.mElbowRight).put("rForeArm", SLSkeletonBoneID.mElbowRight).put("avatar_mElbowRight", SLSkeletonBoneID.mElbowRight).put("avatar_mWristRight", SLSkeletonBoneID.mWristRight).put("mWristRight", SLSkeletonBoneID.mWristRight).put("rHand", SLSkeletonBoneID.mWristRight).put("mHipRight", SLSkeletonBoneID.mHipRight).put("avatar_mHipRight", SLSkeletonBoneID.mHipRight).put("rThigh", SLSkeletonBoneID.mHipRight).put("avatar_mKneeRight", SLSkeletonBoneID.mKneeRight).put("rShin", SLSkeletonBoneID.mKneeRight).put("mKneeRight", SLSkeletonBoneID.mKneeRight).put("avatar_mAnkleRight", SLSkeletonBoneID.mAnkleRight).put("rFoot", SLSkeletonBoneID.mAnkleRight).put("mAnkleRight", SLSkeletonBoneID.mAnkleRight).put("avatar_mFootRight", SLSkeletonBoneID.mFootRight).put("mFootRight", SLSkeletonBoneID.mFootRight).put("mToeRight", SLSkeletonBoneID.mToeRight).put("avatar_mToeRight", SLSkeletonBoneID.mToeRight).put("avatar_mHipLeft", SLSkeletonBoneID.mHipLeft).put("lThigh", SLSkeletonBoneID.mHipLeft).put("mHipLeft", SLSkeletonBoneID.mHipLeft).put("mKneeLeft", SLSkeletonBoneID.mKneeLeft).put("lShin", SLSkeletonBoneID.mKneeLeft).put("avatar_mKneeLeft", SLSkeletonBoneID.mKneeLeft).put("mAnkleLeft", SLSkeletonBoneID.mAnkleLeft).put("lFoot", SLSkeletonBoneID.mAnkleLeft).put("avatar_mAnkleLeft", SLSkeletonBoneID.mAnkleLeft).put("mFootLeft", SLSkeletonBoneID.mFootLeft).put("avatar_mFootLeft", SLSkeletonBoneID.mFootLeft).put("avatar_mToeLeft", SLSkeletonBoneID.mToeLeft).put("mToeLeft", SLSkeletonBoneID.mToeLeft).put("PELVIS", SLSkeletonBoneID.PELVIS).put("BUTT", SLSkeletonBoneID.BUTT).put("BELLY", SLSkeletonBoneID.BELLY).put("LEFT_HANDLE", SLSkeletonBoneID.LEFT_HANDLE).put("RIGHT_HANDLE", SLSkeletonBoneID.RIGHT_HANDLE).put("LOWER_BACK", SLSkeletonBoneID.LOWER_BACK).put("CHEST", SLSkeletonBoneID.CHEST).put("LEFT_PEC", SLSkeletonBoneID.LEFT_PEC).put("RIGHT_PEC", SLSkeletonBoneID.RIGHT_PEC).put("UPPER_BACK", SLSkeletonBoneID.UPPER_BACK).put("NECK", SLSkeletonBoneID.NECK).put("HEAD", SLSkeletonBoneID.HEAD).put("L_CLAVICLE", SLSkeletonBoneID.L_CLAVICLE).put("L_UPPER_ARM", SLSkeletonBoneID.L_UPPER_ARM).put("L_LOWER_ARM", SLSkeletonBoneID.L_LOWER_ARM).put("L_HAND", SLSkeletonBoneID.L_HAND).put("R_CLAVICLE", SLSkeletonBoneID.R_CLAVICLE).put("R_UPPER_ARM", SLSkeletonBoneID.R_UPPER_ARM).put("R_LOWER_ARM", SLSkeletonBoneID.R_LOWER_ARM).put("R_HAND", SLSkeletonBoneID.R_HAND).put("R_UPPER_LEG", SLSkeletonBoneID.R_UPPER_LEG).put("R_LOWER_LEG", SLSkeletonBoneID.R_LOWER_LEG).put("R_FOOT", SLSkeletonBoneID.R_FOOT).put("L_UPPER_LEG", SLSkeletonBoneID.L_UPPER_LEG).put("L_LOWER_LEG", SLSkeletonBoneID.L_LOWER_LEG).put("L_FOOT", SLSkeletonBoneID.L_FOOT).put("mSpine1", SLSkeletonBoneID.mSpine1).put("mSpine2", SLSkeletonBoneID.mSpine2).put("mSpine3", SLSkeletonBoneID.mSpine3).put("mSpine4", SLSkeletonBoneID.mSpine4).put("mFaceRoot", SLSkeletonBoneID.mFaceRoot).put("mFaceEyeAltRight", SLSkeletonBoneID.mFaceEyeAltRight).put("mFaceEyeAltLeft", SLSkeletonBoneID.mFaceEyeAltLeft).put("mFaceForeheadLeft", SLSkeletonBoneID.mFaceForeheadLeft).put("mFaceForeheadRight", SLSkeletonBoneID.mFaceForeheadRight).put("mFaceEyebrowOuterLeft", SLSkeletonBoneID.mFaceEyebrowOuterLeft).put("mFaceEyebrowCenterLeft", SLSkeletonBoneID.mFaceEyebrowCenterLeft).put("mFaceEyebrowInnerLeft", SLSkeletonBoneID.mFaceEyebrowInnerLeft).put("mFaceEyebrowOuterRight", SLSkeletonBoneID.mFaceEyebrowOuterRight).put("mFaceEyebrowCenterRight", SLSkeletonBoneID.mFaceEyebrowCenterRight).put("mFaceEyebrowInnerRight", SLSkeletonBoneID.mFaceEyebrowInnerRight).put("mFaceEyeLidUpperLeft", SLSkeletonBoneID.mFaceEyeLidUpperLeft).put("mFaceEyeLidLowerLeft", SLSkeletonBoneID.mFaceEyeLidLowerLeft).put("mFaceEyeLidUpperRight", SLSkeletonBoneID.mFaceEyeLidUpperRight).put("mFaceEyeLidLowerRight", SLSkeletonBoneID.mFaceEyeLidLowerRight).put("mFaceEar1Left", SLSkeletonBoneID.mFaceEar1Left).put("mFaceEar2Left", SLSkeletonBoneID.mFaceEar2Left).put("mFaceEar1Right", SLSkeletonBoneID.mFaceEar1Right).put("mFaceEar2Right", SLSkeletonBoneID.mFaceEar2Right).put("mFaceNoseLeft", SLSkeletonBoneID.mFaceNoseLeft).put("mFaceNoseCenter", SLSkeletonBoneID.mFaceNoseCenter).put("mFaceNoseRight", SLSkeletonBoneID.mFaceNoseRight).put("mFaceCheekLowerLeft", SLSkeletonBoneID.mFaceCheekLowerLeft).put("mFaceCheekUpperLeft", SLSkeletonBoneID.mFaceCheekUpperLeft).put("mFaceCheekLowerRight", SLSkeletonBoneID.mFaceCheekLowerRight).put("mFaceCheekUpperRight", SLSkeletonBoneID.mFaceCheekUpperRight).put("mFaceJaw", SLSkeletonBoneID.mFaceJaw).put("mFaceChin", SLSkeletonBoneID.mFaceChin).put("mFaceTeethLower", SLSkeletonBoneID.mFaceTeethLower).put("mFaceLipLowerLeft", SLSkeletonBoneID.mFaceLipLowerLeft).put("mFaceLipLowerRight", SLSkeletonBoneID.mFaceLipLowerRight).put("mFaceLipLowerCenter", SLSkeletonBoneID.mFaceLipLowerCenter).put("mFaceTongueBase", SLSkeletonBoneID.mFaceTongueBase).put("mFaceTongueTip", SLSkeletonBoneID.mFaceTongueTip).put("mFaceJawShaper", SLSkeletonBoneID.mFaceJawShaper).put("mFaceForeheadCenter", SLSkeletonBoneID.mFaceForeheadCenter).put("mFaceNoseBase", SLSkeletonBoneID.mFaceNoseBase).put("mFaceTeethUpper", SLSkeletonBoneID.mFaceTeethUpper).put("mFaceLipUpperLeft", SLSkeletonBoneID.mFaceLipUpperLeft).put("mFaceLipUpperRight", SLSkeletonBoneID.mFaceLipUpperRight).put("mFaceLipCornerLeft", SLSkeletonBoneID.mFaceLipCornerLeft).put("mFaceLipCornerRight", SLSkeletonBoneID.mFaceLipCornerRight).put("mFaceLipUpperCenter", SLSkeletonBoneID.mFaceLipUpperCenter).put("mFaceEyecornerInnerLeft", SLSkeletonBoneID.mFaceEyecornerInnerLeft).put("mFaceEyecornerInnerRight", SLSkeletonBoneID.mFaceEyecornerInnerRight).put("mFaceNoseBridge", SLSkeletonBoneID.mFaceNoseBridge).put("mHandMiddle1Left", SLSkeletonBoneID.mHandMiddle1Left).put("mHandMiddle2Left", SLSkeletonBoneID.mHandMiddle2Left).put("mHandMiddle3Left", SLSkeletonBoneID.mHandMiddle3Left).put("mHandIndex1Left", SLSkeletonBoneID.mHandIndex1Left).put("mHandIndex2Left", SLSkeletonBoneID.mHandIndex2Left).put("mHandIndex3Left", SLSkeletonBoneID.mHandIndex3Left).put("mHandRing1Left", SLSkeletonBoneID.mHandRing1Left).put("mHandRing2Left", SLSkeletonBoneID.mHandRing2Left).put("mHandRing3Left", SLSkeletonBoneID.mHandRing3Left).put("mHandPinky1Left", SLSkeletonBoneID.mHandPinky1Left).put("mHandPinky2Left", SLSkeletonBoneID.mHandPinky2Left).put("mHandPinky3Left", SLSkeletonBoneID.mHandPinky3Left).put("mHandThumb1Left", SLSkeletonBoneID.mHandThumb1Left).put("mHandThumb2Left", SLSkeletonBoneID.mHandThumb2Left).put("mHandThumb3Left", SLSkeletonBoneID.mHandThumb3Left).put("mHandMiddle1Right", SLSkeletonBoneID.mHandMiddle1Right).put("mHandMiddle2Right", SLSkeletonBoneID.mHandMiddle2Right).put("mHandMiddle3Right", SLSkeletonBoneID.mHandMiddle3Right).put("mHandIndex1Right", SLSkeletonBoneID.mHandIndex1Right).put("mHandIndex2Right", SLSkeletonBoneID.mHandIndex2Right).put("mHandIndex3Right", SLSkeletonBoneID.mHandIndex3Right).put("mHandRing1Right", SLSkeletonBoneID.mHandRing1Right).put("mHandRing2Right", SLSkeletonBoneID.mHandRing2Right).put("mHandRing3Right", SLSkeletonBoneID.mHandRing3Right).put("mHandPinky1Right", SLSkeletonBoneID.mHandPinky1Right).put("mHandPinky2Right", SLSkeletonBoneID.mHandPinky2Right).put("mHandPinky3Right", SLSkeletonBoneID.mHandPinky3Right).put("mHandThumb1Right", SLSkeletonBoneID.mHandThumb1Right).put("mHandThumb2Right", SLSkeletonBoneID.mHandThumb2Right).put("mHandThumb3Right", SLSkeletonBoneID.mHandThumb3Right).put("mWingsRoot", SLSkeletonBoneID.mWingsRoot).put("mWing1Left", SLSkeletonBoneID.mWing1Left).put("mWing2Left", SLSkeletonBoneID.mWing2Left).put("mWing3Left", SLSkeletonBoneID.mWing3Left).put("mWing4Left", SLSkeletonBoneID.mWing4Left).put("mWing4FanLeft", SLSkeletonBoneID.mWing4FanLeft).put("mWing1Right", SLSkeletonBoneID.mWing1Right).put("mWing2Right", SLSkeletonBoneID.mWing2Right).put("mWing3Right", SLSkeletonBoneID.mWing3Right).put("mWing4Right", SLSkeletonBoneID.mWing4Right).put("mWing4FanRight", SLSkeletonBoneID.mWing4FanRight).put("mTail1", SLSkeletonBoneID.mTail1).put("mTail2", SLSkeletonBoneID.mTail2).put("mTail3", SLSkeletonBoneID.mTail3).put("mTail4", SLSkeletonBoneID.mTail4).put("mTail5", SLSkeletonBoneID.mTail5).put("mTail6", SLSkeletonBoneID.mTail6).put("mGroin", SLSkeletonBoneID.mGroin).put("mHindLimbsRoot", SLSkeletonBoneID.mHindLimbsRoot).put("mHindLimb1Left", SLSkeletonBoneID.mHindLimb1Left).put("mHindLimb2Left", SLSkeletonBoneID.mHindLimb2Left).put("mHindLimb3Left", SLSkeletonBoneID.mHindLimb3Left).put("mHindLimb4Left", SLSkeletonBoneID.mHindLimb4Left).put("mHindLimb1Right", SLSkeletonBoneID.mHindLimb1Right).put("mHindLimb2Right", SLSkeletonBoneID.mHindLimb2Right).put("mHindLimb3Right", SLSkeletonBoneID.mHindLimb3Right).put("mHindLimb4Right", SLSkeletonBoneID.mHindLimb4Right).build();
    }
    
    private SLSkeletonBoneID(final String name, final int ordinal, final boolean isJoint, final boolean isExtended, final int animatedIndex) {
        this.isJoint = isJoint;
        this.isExtended = isExtended;
        this.animatedIndex = animatedIndex;
    }
}
