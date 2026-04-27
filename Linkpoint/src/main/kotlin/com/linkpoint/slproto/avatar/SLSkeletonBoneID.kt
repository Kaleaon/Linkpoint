package com.linkpoint.slproto.avatar

import com.google.common.collect.ImmutableMap

/**
 * SLSkeletonBoneID - Defines all bones in Second Life avatar skeleton
 * 
 * Includes:
 * - Base skeleton (26 joints for animation)
 * - Collision volumes (for physics)
 * - Extended bones (face, hands, wings, tail - 107 additional)
 * 
 * Total: 133 animated bones
 * 
 * Based on Firestorm's avatar skeleton definition
 */
enum class SLSkeletonBoneID(
    val isJoint: Boolean,        // True if bone is animated joint
    val isExtended: Boolean,     // True if extended bone (face/hands/etc)
    val animatedIndex: Int       // Index in animation array (-1 for collision volumes)
) {
    // Base Skeleton - Core animated bones (26 joints)
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
    
    // Collision Volumes - For avatar physics (not animated)
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
    
    // Extended Bones - Detailed face, hands, wings, tail (indices 26-132)
    mSpine1(true, true, 26),
    mSpine2(true, true, 27),
    mSpine3(true, true, 28),
    mSpine4(true, true, 29),
    
    // Face bones for expressions
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
    
    // Hand bones - Full finger articulation
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
    
    // Wings (for dragon/angel avatars)
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
    
    // Tail (for furry/dragon avatars)
    mTail1(true, true, 117),
    mTail2(true, true, 118),
    mTail3(true, true, 119),
    mTail4(true, true, 120),
    mTail5(true, true, 121),
    mTail6(true, true, 122),
    
    // Hind limbs (for quadruped avatars)
    mGroin(true, true, 123),
    mHindLimbsRoot(true, true, 124),
    mHindLimb1Left(true, true, 125),
    mHindLimb2Left(true, true, 126),
    mHindLimb3Left(true, true, 127),
    mHindLimb4Left(true, true, 128),
    mHindLimb1Right(true, true, 129),
    mHindLimb2Right(true, true, 130),
    mHindLimb3Right(true, true, 131),
    mHindLimb4Right(true, true, 132)
    
    companion object {
        // Counts
        const val NUM_JOINTS: Int = 133          // Total animated bones
        const val NUM_BASE_JOINTS: Int = 26      // Core skeleton
        const val NUM_BASE_BONES: Int = 52       // Core + collision volumes
        const val NUM_ANIMATED: Int = 133        // All animated
        
        // All bone IDs as array
        val VALUES: Array<SLSkeletonBoneID> = values()
        
        // Bone name to ID mapping (includes all aliases used in Second Life)
        val bones: ImmutableMap<String, SLSkeletonBoneID> = ImmutableMap.builder<String, SLSkeletonBoneID>()
            // Base skeleton with various name aliases
            .put("avatar_mPelvis", mPelvis).put("mPelvis", mPelvis).put("hip", mPelvis)
            .put("mTorso", mTorso).put("avatar_mTorso", mTorso).put("abdomen", mTorso)
            .put("avatar_mChest", mChest).put("chest", mChest).put("mChest", mChest)
            .put("mNeck", mNeck).put("neck", mNeck).put("avatar_mNeck", mNeck)
            .put("head", mHead).put("avatar_mHead", mHead).put("mHead", mHead)
            .put("mSkull", mSkull).put("figureHair", mSkull).put("avatar_mSkull", mSkull)
            .put("mEyeRight", mEyeRight).put("avatar_mEyeRight", mEyeRight)
            .put("mEyeLeft", mEyeLeft).put("avatar_mEyeLeft", mEyeLeft)
            
            // Left arm
            .put("avatar_mCollarLeft", mCollarLeft).put("mCollarLeft", mCollarLeft).put("lCollar", mCollarLeft)
            .put("avatar_mShoulderLeft", mShoulderLeft).put("lShldr", mShoulderLeft).put("mShoulderLeft", mShoulderLeft)
            .put("lForeArm", mElbowLeft).put("avatar_mElbowLeft", mElbowLeft).put("mElbowLeft", mElbowLeft)
            .put("avatar_mWristLeft", mWristLeft).put("mWristLeft", mWristLeft).put("lHand", mWristLeft)
            
            // Right arm
            .put("rCollar", mCollarRight).put("mCollarRight", mCollarRight).put("avatar_mCollarRight", mCollarRight)
            .put("mShoulderRight", mShoulderRight).put("rShldr", mShoulderRight).put("avatar_mShoulderRight", mShoulderRight)
            .put("mElbowRight", mElbowRight).put("rForeArm", mElbowRight).put("avatar_mElbowRight", mElbowRight)
            .put("avatar_mWristRight", mWristRight).put("mWristRight", mWristRight).put("rHand", mWristRight)
            
            // Right leg
            .put("mHipRight", mHipRight).put("avatar_mHipRight", mHipRight).put("rThigh", mHipRight)
            .put("avatar_mKneeRight", mKneeRight).put("rShin", mKneeRight).put("mKneeRight", mKneeRight)
            .put("avatar_mAnkleRight", mAnkleRight).put("rFoot", mAnkleRight).put("mAnkleRight", mAnkleRight)
            .put("avatar_mFootRight", mFootRight).put("mFootRight", mFootRight)
            .put("mToeRight", mToeRight).put("avatar_mToeRight", mToeRight)
            
            // Left leg
            .put("avatar_mHipLeft", mHipLeft).put("lThigh", mHipLeft).put("mHipLeft", mHipLeft)
            .put("mKneeLeft", mKneeLeft).put("lShin", mKneeLeft).put("avatar_mKneeLeft", mKneeLeft)
            .put("mAnkleLeft", mAnkleLeft).put("lFoot", mAnkleLeft).put("avatar_mAnkleLeft", mAnkleLeft)
            .put("mFootLeft", mFootLeft).put("avatar_mFootLeft", mFootLeft)
            .put("avatar_mToeLeft", mToeLeft).put("mToeLeft", mToeLeft)
            
            // Collision volumes
            .put("PELVIS", PELVIS).put("BUTT", BUTT).put("BELLY", BELLY)
            .put("LEFT_HANDLE", LEFT_HANDLE).put("RIGHT_HANDLE", RIGHT_HANDLE)
            .put("LOWER_BACK", LOWER_BACK).put("CHEST", CHEST)
            .put("LEFT_PEC", LEFT_PEC).put("RIGHT_PEC", RIGHT_PEC)
            .put("UPPER_BACK", UPPER_BACK).put("NECK", NECK).put("HEAD", HEAD)
            .put("L_CLAVICLE", L_CLAVICLE).put("L_UPPER_ARM", L_UPPER_ARM)
            .put("L_LOWER_ARM", L_LOWER_ARM).put("L_HAND", L_HAND)
            .put("R_CLAVICLE", R_CLAVICLE).put("R_UPPER_ARM", R_UPPER_ARM)
            .put("R_LOWER_ARM", R_LOWER_ARM).put("R_HAND", R_HAND)
            .put("R_UPPER_LEG", R_UPPER_LEG).put("R_LOWER_LEG", R_LOWER_LEG).put("R_FOOT", R_FOOT)
            .put("L_UPPER_LEG", L_UPPER_LEG).put("L_LOWER_LEG", L_LOWER_LEG).put("L_FOOT", L_FOOT)
            
            // Extended bones - All face/hand/wing/tail bones
            .put("mSpine1", mSpine1).put("mSpine2", mSpine2).put("mSpine3", mSpine3).put("mSpine4", mSpine4)
            .put("mFaceRoot", mFaceRoot)
            .put("mFaceEyeAltRight", mFaceEyeAltRight).put("mFaceEyeAltLeft", mFaceEyeAltLeft)
            .put("mFaceForeheadLeft", mFaceForeheadLeft).put("mFaceForeheadRight", mFaceForeheadRight)
            .put("mFaceEyebrowOuterLeft", mFaceEyebrowOuterLeft).put("mFaceEyebrowCenterLeft", mFaceEyebrowCenterLeft)
            .put("mFaceEyebrowInnerLeft", mFaceEyebrowInnerLeft).put("mFaceEyebrowOuterRight", mFaceEyebrowOuterRight)
            .put("mFaceEyebrowCenterRight", mFaceEyebrowCenterRight).put("mFaceEyebrowInnerRight", mFaceEyebrowInnerRight)
            .put("mFaceEyeLidUpperLeft", mFaceEyeLidUpperLeft).put("mFaceEyeLidLowerLeft", mFaceEyeLidLowerLeft)
            .put("mFaceEyeLidUpperRight", mFaceEyeLidUpperRight).put("mFaceEyeLidLowerRight", mFaceEyeLidLowerRight)
            .put("mFaceEar1Left", mFaceEar1Left).put("mFaceEar2Left", mFaceEar2Left)
            .put("mFaceEar1Right", mFaceEar1Right).put("mFaceEar2Right", mFaceEar2Right)
            .put("mFaceNoseLeft", mFaceNoseLeft).put("mFaceNoseCenter", mFaceNoseCenter).put("mFaceNoseRight", mFaceNoseRight)
            .put("mFaceCheekLowerLeft", mFaceCheekLowerLeft).put("mFaceCheekUpperLeft", mFaceCheekUpperLeft)
            .put("mFaceCheekLowerRight", mFaceCheekLowerRight).put("mFaceCheekUpperRight", mFaceCheekUpperRight)
            .put("mFaceJaw", mFaceJaw).put("mFaceChin", mFaceChin)
            .put("mFaceTeethLower", mFaceTeethLower).put("mFaceTeethUpper", mFaceTeethUpper)
            .put("mFaceLipLowerLeft", mFaceLipLowerLeft).put("mFaceLipLowerRight", mFaceLipLowerRight)
            .put("mFaceLipLowerCenter", mFaceLipLowerCenter)
            .put("mFaceTongueBase", mFaceTongueBase).put("mFaceTongueTip", mFaceTongueTip)
            .put("mFaceJawShaper", mFaceJawShaper).put("mFaceForeheadCenter", mFaceForeheadCenter)
            .put("mFaceNoseBase", mFaceNoseBase).put("mFaceNoseBridge", mFaceNoseBridge)
            .put("mFaceLipUpperLeft", mFaceLipUpperLeft).put("mFaceLipUpperRight", mFaceLipUpperRight)
            .put("mFaceLipCornerLeft", mFaceLipCornerLeft).put("mFaceLipCornerRight", mFaceLipCornerRight)
            .put("mFaceLipUpperCenter", mFaceLipUpperCenter)
            .put("mFaceEyecornerInnerLeft", mFaceEyecornerInnerLeft).put("mFaceEyecornerInnerRight", mFaceEyecornerInnerRight)
            
            // Hand bones
            .put("mHandMiddle1Left", mHandMiddle1Left).put("mHandMiddle2Left", mHandMiddle2Left).put("mHandMiddle3Left", mHandMiddle3Left)
            .put("mHandIndex1Left", mHandIndex1Left).put("mHandIndex2Left", mHandIndex2Left).put("mHandIndex3Left", mHandIndex3Left)
            .put("mHandRing1Left", mHandRing1Left).put("mHandRing2Left", mHandRing2Left).put("mHandRing3Left", mHandRing3Left)
            .put("mHandPinky1Left", mHandPinky1Left).put("mHandPinky2Left", mHandPinky2Left).put("mHandPinky3Left", mHandPinky3Left)
            .put("mHandThumb1Left", mHandThumb1Left).put("mHandThumb2Left", mHandThumb2Left).put("mHandThumb3Left", mHandThumb3Left)
            .put("mHandMiddle1Right", mHandMiddle1Right).put("mHandMiddle2Right", mHandMiddle2Right).put("mHandMiddle3Right", mHandMiddle3Right)
            .put("mHandIndex1Right", mHandIndex1Right).put("mHandIndex2Right", mHandIndex2Right).put("mHandIndex3Right", mHandIndex3Right)
            .put("mHandRing1Right", mHandRing1Right).put("mHandRing2Right", mHandRing2Right).put("mHandRing3Right", mHandRing3Right)
            .put("mHandPinky1Right", mHandPinky1Right).put("mHandPinky2Right", mHandPinky2Right).put("mHandPinky3Right", mHandPinky3Right)
            .put("mHandThumb1Right", mHandThumb1Right).put("mHandThumb2Right", mHandThumb2Right).put("mHandThumb3Right", mHandThumb3Right)
            
            // Wings, tail, hind limbs
            .put("mWingsRoot", mWingsRoot)
            .put("mWing1Left", mWing1Left).put("mWing2Left", mWing2Left).put("mWing3Left", mWing3Left)
            .put("mWing4Left", mWing4Left).put("mWing4FanLeft", mWing4FanLeft)
            .put("mWing1Right", mWing1Right).put("mWing2Right", mWing2Right).put("mWing3Right", mWing3Right)
            .put("mWing4Right", mWing4Right).put("mWing4FanRight", mWing4FanRight)
            .put("mTail1", mTail1).put("mTail2", mTail2).put("mTail3", mTail3)
            .put("mTail4", mTail4).put("mTail5", mTail5).put("mTail6", mTail6)
            .put("mGroin", mGroin).put("mHindLimbsRoot", mHindLimbsRoot)
            .put("mHindLimb1Left", mHindLimb1Left).put("mHindLimb2Left", mHindLimb2Left)
            .put("mHindLimb3Left", mHindLimb3Left).put("mHindLimb4Left", mHindLimb4Left)
            .put("mHindLimb1Right", mHindLimb1Right).put("mHindLimb2Right", mHindLimb2Right)
            .put("mHindLimb3Right", mHindLimb3Right).put("mHindLimb4Right", mHindLimb4Right)
            .build()
    }
}
