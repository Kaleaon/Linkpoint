package com.lumiyaviewer.lumiya.slproto.avatar

import java.util.HashMap

enum class SLSkeletonBoneID(
    val isJoint: Boolean,
    val isExtended: Boolean,
    val animatedIndex: Int
) {
    mPelvis(true, false, 0),
    mTorso(true, false, 1),
    mChest(true, false, 2),
    mNeck(true, false, 3),
    mHead(true, false, 4),
    mSkull(true, false, 5),
    mEyeRight(true, false, 6),
    mEyeLeft(true, false, 7),
    mCollarLeft(true, false, 8),
    mShoulderLeft(true, false, 9),
    mElbowLeft(true, false, 10),
    mWristLeft(true, false, 11),
    mCollarRight(true, false, 12),
    mShoulderRight(true, false, 13),
    mElbowRight(true, false, 14),
    mWristRight(true, false, 15),
    mHipRight(true, false, 16),
    mKneeRight(true, false, 17),
    mAnkleRight(true, false, 18),
    mFootRight(true, false, 19),
    mToeRight(true, false, 20),
    mHipLeft(true, false, 21),
    mKneeLeft(true, false, 22),
    mAnkleLeft(true, false, 23),
    mFootLeft(true, false, 24),
    mToeLeft(true, false, 25),
    PELVIS(false, false, -1),
    BUTT(false, false, -1),
    BELLY(false, false, -1),
    LEFT_HANDLE(false, false, -1),
    RIGHT_HANDLE(false, false, -1),
    LOWER_BACK(false, false, -1),
    CHEST(false, false, -1),
    LEFT_PEC(false, false, -1),
    RIGHT_PEC(false, false, -1),
    UPPER_BACK(false, false, -1),
    NECK(false, false, -1),
    HEAD(false, false, -1),
    L_CLAVICLE(false, false, -1),
    L_UPPER_ARM(false, false, -1),
    L_LOWER_ARM(false, false, -1),
    L_HAND(false, false, -1),
    R_CLAVICLE(false, false, -1),
    R_UPPER_ARM(false, false, -1),
    R_LOWER_ARM(false, false, -1),
    R_HAND(false, false, -1),
    R_UPPER_LEG(false, false, -1),
    R_LOWER_LEG(false, false, -1),
    R_FOOT(false, false, -1),
    L_UPPER_LEG(false, false, -1),
    L_LOWER_LEG(false, false, -1),
    L_FOOT(false, false, -1),
    mSpine1(true, true, 26),
    mSpine2(true, true, 27),
    mSpine3(true, true, 28),
    mSpine4(true, true, 29),
    mFaceRoot(true, true, 30),
    mFaceEyeAltRight(true, true, 31),
    mFaceEyeAltLeft(true, true, 32),
    mFaceForeheadLeft(true, true, 33),
    mFaceForeheadRight(true, true, 34),
    mFaceEyebrowOuterLeft(true, true, 35),
    mFaceEyebrowCenterLeft(true, true, 36),
    mFaceEyebrowInnerLeft(true, true, 37),
    mFaceEyebrowOuterRight(true, true, 38),
    mFaceEyebrowCenterRight(true, true, 39),
    mFaceEyebrowInnerRight(true, true, 40),
    mFaceEyeLidUpperLeft(true, true, 41),
    mFaceEyeLidLowerLeft(true, true, 42),
    mFaceEyeLidUpperRight(true, true, 43),
    mFaceEyeLidLowerRight(true, true, 44),
    mFaceEar1Left(true, true, 45),
    mFaceEar2Left(true, true, 46),
    mFaceEar1Right(true, true, 47),
    mFaceEar2Right(true, true, 48),
    mFaceNoseLeft(true, true, 49),
    mFaceNoseCenter(true, true, 50),
    mFaceNoseRight(true, true, 51),
    mFaceCheekLowerLeft(true, true, 52),
    mFaceCheekUpperLeft(true, true, 53),
    mFaceCheekLowerRight(true, true, 54),
    mFaceCheekUpperRight(true, true, 55),
    mFaceJaw(true, true, 56),
    mFaceChin(true, true, 57),
    mFaceTeethLower(true, true, 58),
    mFaceLipLowerLeft(true, true, 59),
    mFaceLipLowerRight(true, true, 60),
    mFaceLipLowerCenter(true, true, 61),
    mFaceTongueBase(true, true, 62),
    mFaceTongueTip(true, true, 63),
    mFaceJawShaper(true, true, 64),
    mFaceForeheadCenter(true, true, 65),
    mFaceNoseBase(true, true, 66),
    mFaceTeethUpper(true, true, 67),
    mFaceLipUpperLeft(true, true, 68),
    mFaceLipUpperRight(true, true, 69),
    mFaceLipCornerLeft(true, true, 70),
    mFaceLipCornerRight(true, true, 71),
    mFaceLipUpperCenter(true, true, 72),
    mFaceEyecornerInnerLeft(true, true, 73),
    mFaceEyecornerInnerRight(true, true, 74),
    mFaceNoseBridge(true, true, 75),
    mHandMiddle1Left(true, true, 76),
    mHandMiddle2Left(true, true, 77),
    mHandMiddle3Left(true, true, 78),
    mHandIndex1Left(true, true, 79),
    mHandIndex2Left(true, true, 80),
    mHandIndex3Left(true, true, 81),
    mHandRing1Left(true, true, 82),
    mHandRing2Left(true, true, 83),
    mHandRing3Left(true, true, 84),
    mHandPinky1Left(true, true, 85),
    mHandPinky2Left(true, true, 86),
    mHandPinky3Left(true, true, 87),
    mHandThumb1Left(true, true, 88),
    mHandThumb2Left(true, true, 89),
    mHandThumb3Left(true, true, 90),
    mHandMiddle1Right(true, true, 91),
    mHandMiddle2Right(true, true, 92),
    mHandMiddle3Right(true, true, 93),
    mHandIndex1Right(true, true, 94),
    mHandIndex2Right(true, true, 95),
    mHandIndex3Right(true, true, 96),
    mHandRing1Right(true, true, 97),
    mHandRing2Right(true, true, 98),
    mHandRing3Right(true, true, 99),
    mHandPinky1Right(true, true, 100),
    mHandPinky2Right(true, true, 101),
    mHandPinky3Right(true, true, 102),
    mHandThumb1Right(true, true, 103),
    mHandThumb2Right(true, true, 104),
    mHandThumb3Right(true, true, 105),
    mWingsRoot(true, true, 106),
    mWing1Left(true, true, 107),
    mWing2Left(true, true, 108),
    mWing3Left(true, true, 109),
    mWing4Left(true, true, 110),
    mWing4FanLeft(true, true, 111),
    mWing1Right(true, true, 112),
    mWing2Right(true, true, 113),
    mWing3Right(true, true, 114),
    mWing4Right(true, true, 115),
    mWing4FanRight(true, true, 116),
    mTail1(true, true, 117),
    mTail2(true, true, 118),
    mTail3(true, true, 119),
    mTail4(true, true, 120),
    mTail5(true, true, 121),
    mTail6(true, true, 122),
    mGroin(true, true, 123),
    mHindLimbsRoot(true, true, 124),
    mHindLimb1Left(true, true, 125),
    mHindLimb2Left(true, true, 126),
    mHindLimb3Left(true, true, 127),
    mHindLimb4Left(true, true, 128),
    mHindLimb1Right(true, true, 129),
    mHindLimb2Right(true, true, 130),
    mHindLimb3Right(true, true, 131),
    mHindLimb4Right(true, true, 132);

    companion object {
        val NUM_ANIMATED: Int = 133
        val NUM_BASE_BONES: Int = 52
        val NUM_BASE_JOINTS: Int = 26
        val NUM_JOINTS: Int = 133
        
        val bones: Map<String, SLSkeletonBoneID>
        
        init {
            val map = HashMap<String, SLSkeletonBoneID>()
            for (bone in values()) {
                map[bone.name] = bone
            }
            // Add aliases
            map["avatar_mPelvis"] = mPelvis
            map["hip"] = mPelvis
            map["abdomen"] = mTorso
            map["chest"] = mChest
            map["neck"] = mNeck
            map["figureHair"] = mSkull
            map["lCollar"] = mCollarLeft
            map["lShldr"] = mShoulderLeft
            map["lForeArm"] = mElbowLeft
            map["lHand"] = mWristLeft
            map["rCollar"] = mCollarRight
            map["rShldr"] = mShoulderRight
            map["rForeArm"] = mElbowRight
            map["rHand"] = mWristRight
            map["rThigh"] = mHipRight
            map["rShin"] = mKneeRight
            map["rFoot"] = mAnkleRight
            map["lThigh"] = mHipLeft
            map["lShin"] = mKneeLeft
            map["lFoot"] = mAnkleLeft
            
            bones = map
        }
    }
}
