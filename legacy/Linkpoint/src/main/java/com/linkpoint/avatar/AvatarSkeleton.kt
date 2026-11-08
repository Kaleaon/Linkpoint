package com.linkpoint.avatar

import android.content.Context
import android.util.Log
import com.linkpoint.protocol.types.LLQuaternion
import com.linkpoint.protocol.types.LLVector3
import org.json.JSONObject
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.InputStreamReader
import java.io.StringReader

/**
 * Avatar skeleton with bones and transforms
 * Based on Second Life's standard avatar skeleton
 */
class AvatarSkeleton(context: Context?) {

    companion object {
        private const val TAG = "AvatarSkeleton"

        // Standard SL bone names (133 Bento joints)
        val BONE_NAMES = arrayOf(
            // Base joints (26)
            "mPelvis", "mTorso", "mChest", "mNeck", "mHead", "mSkull",
            "mCollarLeft", "mShoulderLeft", "mElbowLeft", "mWristLeft",
            "mCollarRight", "mShoulderRight", "mElbowRight", "mWristRight",
            "mHipLeft", "mKneeLeft", "mAnkleLeft", "mFootLeft", "mToeLeft",
            "mHipRight", "mKneeRight", "mAnkleRight", "mFootRight", "mToeRight",
            "mEyeLeft", "mEyeRight",
            // Spine (4)
            "mSpine1", "mSpine2", "mSpine3", "mSpine4",
            // Tail (6)
            "mTail1", "mTail2", "mTail3", "mTail4", "mTail5", "mTail6",
            // Groin (1)
            "mGroin",
            // Wings (11)
            "mWingsRoot", "mWing1Left", "mWing2Left", "mWing3Left", "mWing4Left", "mWing4FanLeft",
            "mWing1Right", "mWing2Right", "mWing3Right", "mWing4Right", "mWing4FanRight",
            // Hindlimbs (9)
            "mHindLimbsRoot", "mHindLimb1Left", "mHindLimb2Left", "mHindLimb3Left", "mHindLimb4Left",
            "mHindLimb1Right", "mHindLimb2Right", "mHindLimb3Right", "mHindLimb4Right",
            // Extended bones (Bento Face - 46)
            "mFaceRoot", "mFaceEyeAltRight", "mFaceEyeAltLeft",
            "mFaceForeheadLeft", "mFaceForeheadRight", "mFaceForeheadCenter",
            "mFaceEyebrowOuterLeft", "mFaceEyebrowCenterLeft", "mFaceEyebrowInnerLeft",
            "mFaceEyebrowOuterRight", "mFaceEyebrowCenterRight", "mFaceEyebrowInnerRight",
            "mFaceEyeLidUpperLeft", "mFaceEyeLidLowerLeft",
            "mFaceEyeLidUpperRight", "mFaceEyeLidLowerRight",
            "mFaceEar1Left", "mFaceEar2Left", "mFaceEar1Right", "mFaceEar2Right",
            "mFaceNoseLeft", "mFaceNoseCenter", "mFaceNoseRight", "mFaceNoseBase", "mFaceNoseBridge",
            "mFaceCheekLowerLeft", "mFaceCheekUpperLeft",
            "mFaceCheekLowerRight", "mFaceCheekUpperRight",
            "mFaceJaw", "mFaceJawShaper", "mFaceChin", "mFaceTeethLower", "mFaceTeethUpper",
            "mFaceLipLowerLeft", "mFaceLipLowerRight", "mFaceLipLowerCenter",
            "mFaceLipUpperLeft", "mFaceLipUpperRight", "mFaceLipUpperCenter",
            "mFaceLipCornerLeft", "mFaceLipCornerRight",
            "mFaceEyecornerInnerLeft", "mFaceEyecornerInnerRight",
            "mFaceTongueBase", "mFaceTongueTip",
            // Bento Hands (30)
            "mHandMiddle1Left", "mHandMiddle2Left", "mHandMiddle3Left",
            "mHandIndex1Left", "mHandIndex2Left", "mHandIndex3Left",
            "mHandRing1Left", "mHandRing2Left", "mHandRing3Left",
            "mHandPinky1Left", "mHandPinky2Left", "mHandPinky3Left",
            "mHandThumb1Left", "mHandThumb2Left", "mHandThumb3Left",
            "mHandMiddle1Right", "mHandMiddle2Right", "mHandMiddle3Right",
            "mHandIndex1Right", "mHandIndex2Right", "mHandIndex3Right",
            "mHandRing1Right", "mHandRing2Right", "mHandRing3Right",
            "mHandPinky1Right", "mHandPinky2Right", "mHandPinky3Right",
            "mHandThumb1Right", "mHandThumb2Right", "mHandThumb3Right"
        )
        val BONES = BONE_NAMES
    }

    val bones = mutableMapOf<String, Bone>()
    val boneArray = mutableListOf<Bone>()
    private var rootBone: Bone? = null

    init {
        if (context != null) {
            loadDefaultSkeleton(context)
        } else {
            createDefaultSkeleton()
        }
    }

    private fun loadDefaultSkeleton(context: Context) {
        try {
            // Try to load from assets
            val inputStream = context.assets.open("avatar/avatar_skeleton.xml")
            val content = InputStreamReader(inputStream).readText()
            inputStream.close()
            parseSkeletonXML(content)
        } catch (e: Exception) {
            logW("No skeleton file, using defaults", e)
            createDefaultSkeleton()
        }
    }

    private fun logI(msg: String) {
        try { Log.i(TAG, msg) } catch (_: Throwable) {}
    }

    private fun logW(msg: String, e: Throwable? = null) {
        try { if (e != null) Log.w(TAG, msg, e) else Log.w(TAG, msg) } catch (_: Throwable) {}
    }

    /**
     * Parse avatar_skeleton.xml from the Linden Lab system avatar.
     *
     * The file is a deeply-nested <linden_skeleton> document where every
     * <bone> element carries name / pivot / pos / rot / scale / aliases /
     * end attributes, with child <bone>s nested inside as the natural
     * skeleton hierarchy. Collision volumes are siblings of bones; we
     * ignore them here (they're for ray-test / physics, not rendering).
     *
     * Falls back to the hardcoded subset if parsing fails so avatars still
     * render with at least the original 24-bone placeholder skeleton.
     */
    private fun parseSkeletonXML(xml: String) {
        try {
            val factory = XmlPullParserFactory.newInstance()
            factory.isNamespaceAware = false
            val parser = factory.newPullParser()
            parser.setInput(StringReader(xml))

            val parentStack = ArrayDeque<Bone?>()
            parentStack.addLast(null)
            var rootBoneCandidate: Bone? = null
            var event = parser.eventType
            while (event != XmlPullParser.END_DOCUMENT) {
                when (event) {
                    XmlPullParser.START_TAG -> {
                        if (parser.name == "bone") {
                            val name = parser.getAttributeValue(null, "name") ?: ""
                            val pivot = parseVec3(parser.getAttributeValue(null, "pivot"))
                                ?: parseVec3(parser.getAttributeValue(null, "pos"))
                                ?: LLVector3.zero()
                            val rot = parseRotEuler(parser.getAttributeValue(null, "rot"))
                            val scale = parseVec3(parser.getAttributeValue(null, "scale"))
                                ?: LLVector3(1f, 1f, 1f)
                            val parent = parentStack.lastOrNull()
                            val bone = createBone(name, parent, pivot, rot)
                            // Apply bind scale; createBone defaults to (1,1,1).
                            bone.scale = scale
                            if (rootBoneCandidate == null && parent == null) {
                                rootBoneCandidate = bone
                            }
                            parentStack.addLast(bone)
                        } else if (parser.name == "collision_volume") {
                            // Push a sentinel so closing tag pops the right level
                            // (collision_volume can be nested under bones too).
                            parentStack.addLast(parentStack.lastOrNull())
                        }
                    }
                    XmlPullParser.END_TAG -> {
                        if (parser.name == "bone" || parser.name == "collision_volume") {
                            if (parentStack.size > 1) parentStack.removeLast()
                        }
                    }
                }
                event = parser.next()
            }

            if (boneArray.isEmpty()) {
                logW("Skeleton XML had no bones; using hardcoded fallback")
                createDefaultSkeleton()
                return
            }

            rootBone = rootBoneCandidate ?: boneArray.firstOrNull()
            updateBoneMatrices()
            logI("Loaded SL skeleton from XML: ${boneArray.size} bones")
        } catch (e: Exception) {
            logW("Skeleton XML parse failed; using hardcoded fallback: ${e.message}")
            createDefaultSkeleton()
        }
    }

    private fun parseVec3(s: String?): LLVector3? {
        if (s.isNullOrBlank()) return null
        val parts = s.trim().split(Regex("\\s+"))
        if (parts.size < 3) return null
        return try {
            LLVector3(parts[0].toFloat(), parts[1].toFloat(), parts[2].toFloat())
        } catch (e: NumberFormatException) {
            null
        }
    }

    /**
     * `rot` in avatar_skeleton.xml is given as Euler angles in degrees
     * (X Y Z order), not as a quaternion. Default to identity when the
     * angles are zero (the common case for the rest pose).
     */
    private fun parseRotEuler(s: String?): LLQuaternion {
        val v = parseVec3(s) ?: return LLQuaternion.identity()
        if (v.x == 0f && v.y == 0f && v.z == 0f) return LLQuaternion.identity()
        val cx = kotlin.math.cos(Math.toRadians(v.x.toDouble()) * 0.5).toFloat()
        val sx = kotlin.math.sin(Math.toRadians(v.x.toDouble()) * 0.5).toFloat()
        val cy = kotlin.math.cos(Math.toRadians(v.y.toDouble()) * 0.5).toFloat()
        val sy = kotlin.math.sin(Math.toRadians(v.y.toDouble()) * 0.5).toFloat()
        val cz = kotlin.math.cos(Math.toRadians(v.z.toDouble()) * 0.5).toFloat()
        val sz = kotlin.math.sin(Math.toRadians(v.z.toDouble()) * 0.5).toFloat()
        // ZYX order (matches the LL viewer's LLQuaternion::setEulerAngles).
        return LLQuaternion(
            sx * cy * cz - cx * sy * sz,
            cx * sy * cz + sx * cy * sz,
            cx * cy * sz - sx * sy * cz,
            cx * cy * cz + sx * sy * sz
        )
    }

    private fun createDefaultSkeleton() {
        // Root
        val pelvis = createBone("mPelvis", null, LLVector3(0f, 0f, 1.0f), LLQuaternion.identity())

        // Spine hierarchy
        val spine1 = createBone("mSpine1", pelvis, LLVector3(0f, 0f, 0.04f), LLQuaternion.identity())
        val spine2 = createBone("mSpine2", spine1, LLVector3(0f, 0f, 0.04f), LLQuaternion.identity())
        val torso = createBone("mTorso", spine2, LLVector3(0f, 0f, 0.084f), LLQuaternion.identity())
        val spine3 = createBone("mSpine3", torso, LLVector3(0f, 0f, 0.09f), LLQuaternion.identity())
        val spine4 = createBone("mSpine4", spine3, LLVector3(0f, 0f, 0.09f), LLQuaternion.identity())
        val chest = createBone("mChest", spine4, LLVector3(0f, 0f, 0.184f), LLQuaternion.identity())
        val neck = createBone("mNeck", chest, LLVector3(0f, 0f, 0.206f), LLQuaternion.identity())
        val head = createBone("mHead", neck, LLVector3(0f, 0f, 0.076f), LLQuaternion.identity())
        createBone("mSkull", head, LLVector3(0f, 0f, 0.079f), LLQuaternion.identity())

        // Eyes
        createBone("mEyeLeft", head, LLVector3(0.033f, 0.029f, 0.055f), LLQuaternion.identity())
        createBone("mEyeRight", head, LLVector3(-0.033f, 0.029f, 0.055f), LLQuaternion.identity())

        // Face root & facial Bento joints
        val faceRoot = createBone("mFaceRoot", head, LLVector3(0f, 0.08f, 0.05f), LLQuaternion.identity())
        createBone("mFaceEyeAltLeft", faceRoot, LLVector3(0.033f, 0f, 0f), LLQuaternion.identity())
        createBone("mFaceEyeAltRight", faceRoot, LLVector3(-0.033f, 0f, 0f), LLQuaternion.identity())
        createBone("mFaceForeheadLeft", faceRoot, LLVector3(0.03f, 0f, 0.04f), LLQuaternion.identity())
        createBone("mFaceForeheadRight", faceRoot, LLVector3(-0.03f, 0f, 0.04f), LLQuaternion.identity())
        createBone("mFaceForeheadCenter", faceRoot, LLVector3(0f, 0f, 0.04f), LLQuaternion.identity())
        createBone("mFaceEyebrowOuterLeft", faceRoot, LLVector3(0.04f, 0f, 0.025f), LLQuaternion.identity())
        createBone("mFaceEyebrowCenterLeft", faceRoot, LLVector3(0.025f, 0f, 0.025f), LLQuaternion.identity())
        createBone("mFaceEyebrowInnerLeft", faceRoot, LLVector3(0.01f, 0f, 0.025f), LLQuaternion.identity())
        createBone("mFaceEyebrowOuterRight", faceRoot, LLVector3(-0.04f, 0f, 0.025f), LLQuaternion.identity())
        createBone("mFaceEyebrowCenterRight", faceRoot, LLVector3(-0.025f, 0f, 0.025f), LLQuaternion.identity())
        createBone("mFaceEyebrowInnerRight", faceRoot, LLVector3(-0.01f, 0f, 0.025f), LLQuaternion.identity())
        createBone("mFaceEyeLidUpperLeft", faceRoot, LLVector3(0.033f, 0f, 0.01f), LLQuaternion.identity())
        createBone("mFaceEyeLidLowerLeft", faceRoot, LLVector3(0.033f, 0f, -0.01f), LLQuaternion.identity())
        createBone("mFaceEyeLidUpperRight", faceRoot, LLVector3(-0.033f, 0f, 0.01f), LLQuaternion.identity())
        createBone("mFaceEyeLidLowerRight", faceRoot, LLVector3(-0.033f, 0f, -0.01f), LLQuaternion.identity())

        val ear1L = createBone("mFaceEar1Left", faceRoot, LLVector3(0.06f, -0.02f, 0.01f), LLQuaternion.identity())
        createBone("mFaceEar2Left", ear1L, LLVector3(0.02f, -0.01f, 0f), LLQuaternion.identity())
        val ear1R = createBone("mFaceEar1Right", faceRoot, LLVector3(-0.06f, -0.02f, 0.01f), LLQuaternion.identity())
        createBone("mFaceEar2Right", ear1R, LLVector3(-0.02f, -0.01f, 0f), LLQuaternion.identity())

        createBone("mFaceNoseLeft", faceRoot, LLVector3(0.01f, 0.02f, 0f), LLQuaternion.identity())
        createBone("mFaceNoseCenter", faceRoot, LLVector3(0f, 0.02f, 0f), LLQuaternion.identity())
        createBone("mFaceNoseRight", faceRoot, LLVector3(-0.01f, 0.02f, 0f), LLQuaternion.identity())
        createBone("mFaceNoseBase", faceRoot, LLVector3(0f, 0.02f, -0.01f), LLQuaternion.identity())
        createBone("mFaceNoseBridge", faceRoot, LLVector3(0f, 0.01f, 0.01f), LLQuaternion.identity())

        createBone("mFaceCheekLowerLeft", faceRoot, LLVector3(0.03f, 0.01f, -0.02f), LLQuaternion.identity())
        createBone("mFaceCheekUpperLeft", faceRoot, LLVector3(0.03f, 0.01f, 0.01f), LLQuaternion.identity())
        createBone("mFaceCheekLowerRight", faceRoot, LLVector3(-0.03f, 0.01f, -0.02f), LLQuaternion.identity())
        createBone("mFaceCheekUpperRight", faceRoot, LLVector3(-0.03f, 0.01f, 0.01f), LLQuaternion.identity())

        val jaw = createBone("mFaceJaw", faceRoot, LLVector3(0f, 0f, -0.04f), LLQuaternion.identity())
        createBone("mFaceChin", jaw, LLVector3(0f, 0.02f, -0.02f), LLQuaternion.identity())
        createBone("mFaceJawShaper", faceRoot, LLVector3(0f, 0f, -0.03f), LLQuaternion.identity())

        val teethLower = createBone("mFaceTeethLower", jaw, LLVector3(0f, 0.02f, 0f), LLQuaternion.identity())
        createBone("mFaceLipLowerLeft", teethLower, LLVector3(0.01f, 0.005f, -0.005f), LLQuaternion.identity())
        createBone("mFaceLipLowerRight", teethLower, LLVector3(-0.01f, 0.005f, -0.005f), LLQuaternion.identity())
        createBone("mFaceLipLowerCenter", teethLower, LLVector3(0f, 0.005f, -0.005f), LLQuaternion.identity())
        val tongueBase = createBone("mFaceTongueBase", teethLower, LLVector3(0f, -0.01f, 0f), LLQuaternion.identity())
        createBone("mFaceTongueTip", tongueBase, LLVector3(0f, 0.015f, 0f), LLQuaternion.identity())

        val teethUpper = createBone("mFaceTeethUpper", faceRoot, LLVector3(0f, 0.02f, -0.02f), LLQuaternion.identity())
        createBone("mFaceLipUpperLeft", teethUpper, LLVector3(0.01f, 0.005f, 0.005f), LLQuaternion.identity())
        createBone("mFaceLipUpperRight", teethUpper, LLVector3(-0.01f, 0.005f, 0.005f), LLQuaternion.identity())
        createBone("mFaceLipUpperCenter", teethUpper, LLVector3(0f, 0.005f, 0.005f), LLQuaternion.identity())
        createBone("mFaceLipCornerLeft", teethUpper, LLVector3(0.02f, 0.002f, 0f), LLQuaternion.identity())
        createBone("mFaceLipCornerRight", teethUpper, LLVector3(-0.02f, 0.002f, 0f), LLQuaternion.identity())
        createBone("mFaceEyecornerInnerLeft", faceRoot, LLVector3(0.015f, 0.01f, 0.01f), LLQuaternion.identity())
        createBone("mFaceEyecornerInnerRight", faceRoot, LLVector3(-0.015f, 0.01f, 0.01f), LLQuaternion.identity())

        // Left arm
        val collarL = createBone("mCollarLeft", chest, LLVector3(0.021f, 0f, 0.142f), LLQuaternion.identity())
        val shoulderL = createBone("mShoulderLeft", collarL, LLVector3(0.085f, 0f, 0f), LLQuaternion.identity())
        val elbowL = createBone("mElbowLeft", shoulderL, LLVector3(0.248f, 0f, 0f), LLQuaternion.identity())
        val wristL = createBone("mWristLeft", elbowL, LLVector3(0.205f, 0f, 0f), LLQuaternion.identity())

        // Left hand fingers
        val m1L = createBone("mHandMiddle1Left", wristL, LLVector3(0.08f, 0f, 0f), LLQuaternion.identity())
        val m2L = createBone("mHandMiddle2Left", m1L, LLVector3(0.03f, 0f, 0f), LLQuaternion.identity())
        createBone("mHandMiddle3Left", m2L, LLVector3(0.02f, 0f, 0f), LLQuaternion.identity())
        val i1L = createBone("mHandIndex1Left", wristL, LLVector3(0.075f, 0.015f, 0f), LLQuaternion.identity())
        val i2L = createBone("mHandIndex2Left", i1L, LLVector3(0.028f, 0f, 0f), LLQuaternion.identity())
        createBone("mHandIndex3Left", i2L, LLVector3(0.018f, 0f, 0f), LLQuaternion.identity())
        val r1L = createBone("mHandRing1Left", wristL, LLVector3(0.075f, -0.015f, 0f), LLQuaternion.identity())
        val r2L = createBone("mHandRing2Left", r1L, LLVector3(0.028f, 0f, 0f), LLQuaternion.identity())
        createBone("mHandRing3Left", r2L, LLVector3(0.018f, 0f, 0f), LLQuaternion.identity())
        val p1L = createBone("mHandPinky1Left", wristL, LLVector3(0.07f, -0.028f, 0f), LLQuaternion.identity())
        val p2L = createBone("mHandPinky2Left", p1L, LLVector3(0.022f, 0f, 0f), LLQuaternion.identity())
        createBone("mHandPinky3Left", p2L, LLVector3(0.015f, 0f, 0f), LLQuaternion.identity())
        val t1L = createBone("mHandThumb1Left", wristL, LLVector3(0.025f, 0.02f, -0.01f), LLQuaternion.identity())
        val t2L = createBone("mHandThumb2Left", t1L, LLVector3(0.025f, 0f, 0f), LLQuaternion.identity())
        createBone("mHandThumb3Left", t2L, LLVector3(0.018f, 0f, 0f), LLQuaternion.identity())

        // Right arm
        val collarR = createBone("mCollarRight", chest, LLVector3(-0.021f, 0f, 0.142f), LLQuaternion.identity())
        val shoulderR = createBone("mShoulderRight", collarR, LLVector3(-0.085f, 0f, 0f), LLQuaternion.identity())
        val elbowR = createBone("mElbowRight", shoulderR, LLVector3(-0.248f, 0f, 0f), LLQuaternion.identity())
        val wristR = createBone("mWristRight", elbowR, LLVector3(-0.205f, 0f, 0f), LLQuaternion.identity())

        // Right hand fingers
        val m1R = createBone("mHandMiddle1Right", wristR, LLVector3(-0.08f, 0f, 0f), LLQuaternion.identity())
        val m2R = createBone("mHandMiddle2Right", m1R, LLVector3(-0.03f, 0f, 0f), LLQuaternion.identity())
        createBone("mHandMiddle3Right", m2R, LLVector3(-0.02f, 0f, 0f), LLQuaternion.identity())
        val i1R = createBone("mHandIndex1Right", wristR, LLVector3(-0.075f, 0.015f, 0f), LLQuaternion.identity())
        val i2R = createBone("mHandIndex2Right", i1R, LLVector3(-0.028f, 0f, 0f), LLQuaternion.identity())
        createBone("mHandIndex3Right", i2R, LLVector3(-0.018f, 0f, 0f), LLQuaternion.identity())
        val r1R = createBone("mHandRing1Right", wristR, LLVector3(-0.075f, -0.015f, 0f), LLQuaternion.identity())
        val r2R = createBone("mHandRing2Right", r1R, LLVector3(-0.028f, 0f, 0f), LLQuaternion.identity())
        createBone("mHandRing3Right", r2R, LLVector3(-0.018f, 0f, 0f), LLQuaternion.identity())
        val p1R = createBone("mHandPinky1Right", wristR, LLVector3(-0.07f, -0.028f, 0f), LLQuaternion.identity())
        val p2R = createBone("mHandPinky2Right", p1R, LLVector3(-0.022f, 0f, 0f), LLQuaternion.identity())
        createBone("mHandPinky3Right", p2R, LLVector3(-0.015f, 0f, 0f), LLQuaternion.identity())
        val t1R = createBone("mHandThumb1Right", wristR, LLVector3(-0.025f, 0.02f, -0.01f), LLQuaternion.identity())
        val t2R = createBone("mHandThumb2Right", t1R, LLVector3(-0.025f, 0f, 0f), LLQuaternion.identity())
        createBone("mHandThumb3Right", t2R, LLVector3(-0.018f, 0f, 0f), LLQuaternion.identity())

        // Wings
        val wingsRoot = createBone("mWingsRoot", chest, LLVector3(0f, -0.1f, 0.1f), LLQuaternion.identity())
        val w1L = createBone("mWing1Left", wingsRoot, LLVector3(0.1f, 0f, 0f), LLQuaternion.identity())
        val w2L = createBone("mWing2Left", w1L, LLVector3(0.2f, 0f, 0f), LLQuaternion.identity())
        val w3L = createBone("mWing3Left", w2L, LLVector3(0.2f, 0f, 0f), LLQuaternion.identity())
        createBone("mWing4Left", w3L, LLVector3(0.2f, 0f, 0f), LLQuaternion.identity())
        createBone("mWing4FanLeft", w3L, LLVector3(0.2f, -0.05f, 0f), LLQuaternion.identity())

        val w1R = createBone("mWing1Right", wingsRoot, LLVector3(-0.1f, 0f, 0f), LLQuaternion.identity())
        val w2R = createBone("mWing2Right", w1R, LLVector3(-0.2f, 0f, 0f), LLQuaternion.identity())
        val w3R = createBone("mWing3Right", w2R, LLVector3(-0.2f, 0f, 0f), LLQuaternion.identity())
        createBone("mWing4Right", w3R, LLVector3(-0.2f, 0f, 0f), LLQuaternion.identity())
        createBone("mWing4FanRight", w3R, LLVector3(-0.2f, -0.05f, 0f), LLQuaternion.identity())

        // Left leg
        val hipL = createBone("mHipLeft", pelvis, LLVector3(0.034f, 0f, -0.107f), LLQuaternion.identity())
        val kneeL = createBone("mKneeLeft", hipL, LLVector3(0f, 0f, -0.422f), LLQuaternion.identity())
        val ankleL = createBone("mAnkleLeft", kneeL, LLVector3(0f, 0f, -0.408f), LLQuaternion.identity())
        val footL = createBone("mFootLeft", ankleL, LLVector3(0f, 0.112f, -0.061f), LLQuaternion.identity())
        createBone("mToeLeft", footL, LLVector3(0f, 0.065f, 0f), LLQuaternion.identity())

        // Right leg
        val hipR = createBone("mHipRight", pelvis, LLVector3(-0.034f, 0f, -0.107f), LLQuaternion.identity())
        val kneeR = createBone("mKneeRight", hipR, LLVector3(0f, 0f, -0.422f), LLQuaternion.identity())
        val ankleR = createBone("mAnkleRight", kneeR, LLVector3(0f, 0f, -0.408f), LLQuaternion.identity())
        val footR = createBone("mFootRight", ankleR, LLVector3(0f, 0.112f, -0.061f), LLQuaternion.identity())
        createBone("mToeRight", footR, LLVector3(0f, 0.065f, 0f), LLQuaternion.identity())

        // Tail
        val tail1 = createBone("mTail1", pelvis, LLVector3(0f, -0.1f, -0.05f), LLQuaternion.identity())
        val tail2 = createBone("mTail2", tail1, LLVector3(0f, -0.1f, -0.05f), LLQuaternion.identity())
        val tail3 = createBone("mTail3", tail2, LLVector3(0f, -0.1f, -0.05f), LLQuaternion.identity())
        val tail4 = createBone("mTail4", tail3, LLVector3(0f, -0.1f, -0.05f), LLQuaternion.identity())
        val tail5 = createBone("mTail5", tail4, LLVector3(0f, -0.1f, -0.05f), LLQuaternion.identity())
        createBone("mTail6", tail5, LLVector3(0f, -0.1f, -0.05f), LLQuaternion.identity())

        // Groin
        createBone("mGroin", pelvis, LLVector3(0f, 0.02f, -0.12f), LLQuaternion.identity())

        // Hindlimbs
        val hindRoot = createBone("mHindLimbsRoot", pelvis, LLVector3(0f, -0.08f, -0.1f), LLQuaternion.identity())
        val h1L = createBone("mHindLimb1Left", hindRoot, LLVector3(0.05f, 0f, 0f), LLQuaternion.identity())
        val h2L = createBone("mHindLimb2Left", h1L, LLVector3(0f, 0f, -0.2f), LLQuaternion.identity())
        val h3L = createBone("mHindLimb3Left", h2L, LLVector3(0f, 0f, -0.2f), LLQuaternion.identity())
        createBone("mHindLimb4Left", h3L, LLVector3(0f, 0.05f, -0.05f), LLQuaternion.identity())

        val h1R = createBone("mHindLimb1Right", hindRoot, LLVector3(-0.05f, 0f, 0f), LLQuaternion.identity())
        val h2R = createBone("mHindLimb2Right", h1R, LLVector3(0f, 0f, -0.2f), LLQuaternion.identity())
        val h3R = createBone("mHindLimb3Right", h2R, LLVector3(0f, 0f, -0.2f), LLQuaternion.identity())
        createBone("mHindLimb4Right", h3R, LLVector3(0f, 0.05f, -0.05f), LLQuaternion.identity())

        // Fallback for any bone name in BONE_NAMES not explicitly created
        for (name in BONE_NAMES) {
            if (!bones.containsKey(name)) {
                createBone(name, pelvis, LLVector3.zero(), LLQuaternion.identity())
            }
        }

        rootBone = pelvis

        // Calculate rest pose matrices
        updateBoneMatrices()

        logI("Created default Bento skeleton with ${bones.size} bones")
    }

    private fun createBone(
        name: String,
        parent: Bone?,
        offset: LLVector3,
        rotation: LLQuaternion
    ): Bone {
        val index = boneArray.size
        val bone = Bone(
            name = name,
            index = index,
            parent = parent,
            children = mutableListOf(),
            bindPosition = offset,
            bindRotation = rotation,
            position = offset.copy(),
            rotation = rotation.copy(),
            scale = LLVector3(1f, 1f, 1f)
        )

        parent?.children?.add(bone)
        bones[name] = bone
        boneArray.add(bone)

        return bone
    }

    /**
     * Apply VisualParam-driven bone scale offsets to the rest pose.
     *
     * For each `<param_skeleton>` in avatar_lad.xml, the LL viewer multiplies
     * the param's weight by the per-bone scale offsets and adds the result
     * to that bone's bind-pose scale (LLPolySkeletalDistortion in LL terms).
     * The accumulated effect is what makes "Male_Skeleton", "BodyMass",
     * "Heel_Height" etc. visibly change the avatar's silhouette.
     *
     * Resets every bone's scale to (1, 1, 1) before re-applying so this is
     * idempotent across AvatarAppearance updates.
     */
    fun applySkeletonParams(
        visualParams: ByteArray,
        params: List<VisualParamLoader.VisualParam>
    ) {
        // Reset to bind-pose scale.
        for (bone in boneArray) {
            bone.scale = LLVector3(1f, 1f, 1f)
        }
        if (visualParams.isEmpty() || params.isEmpty()) return

        val maxIdx = minOf(visualParams.size, params.size)
        for (i in 0 until maxIdx) {
            val param = params[i]
            if (param.boneScales.isEmpty()) continue
            val w = param.weightForByte(visualParams[i].toInt())
            // Skip params at their default — no contribution.
            if (kotlin.math.abs(w - param.valueDefault) < 1e-4f) continue
            for ((boneName, offset) in param.boneScales) {
                val bone = bones[boneName] ?: continue
                bone.scale = LLVector3(
                    bone.scale.x + offset[0] * w,
                    bone.scale.y + offset[1] * w,
                    bone.scale.z + offset[2] * w
                )
            }
        }
        updateBoneMatrices()
    }

    /** Recompute every bone's worldMatrix + skinningMatrix from the rest pose. */
    fun updateBoneMatrices() {
        rootBone?.let { calculateBoneMatrix(it, FloatArray(16).apply {
            this[0] = 1f; this[5] = 1f; this[10] = 1f; this[15] = 1f
        }) }
    }

    private fun calculateBoneMatrix(bone: Bone, parentMatrix: FloatArray) {
        // Local transform
        val localMatrix = FloatArray(16)

        // Scale
        val s = bone.scale
        // Rotation
        val r = bone.rotation
        // Translation
        val t = bone.position

        // Create matrix: T * R * S
        bone.rotation.toMatrix(localMatrix)
        localMatrix[12] = t.x
        localMatrix[13] = t.y
        localMatrix[14] = t.z

        // Apply scale
        localMatrix[0] *= s.x; localMatrix[1] *= s.x; localMatrix[2] *= s.x
        localMatrix[4] *= s.y; localMatrix[5] *= s.y; localMatrix[6] *= s.y
        localMatrix[8] *= s.z; localMatrix[9] *= s.z; localMatrix[10] *= s.z

        // Multiply with parent
        bone.worldMatrix = multiplyMatrices(parentMatrix, localMatrix)

        // Skinning matrix = worldMatrix * inverseBindMatrix
        bone.skinningMatrix = multiplyMatrices(bone.worldMatrix, bone.inverseBindMatrix)

        // Recursively update children
        bone.children.forEach { calculateBoneMatrix(it, bone.worldMatrix) }
    }

    private fun multiplyMatrices(a: FloatArray, b: FloatArray): FloatArray {
        val result = FloatArray(16)
        for (i in 0..3) {
            for (j in 0..3) {
                result[i * 4 + j] =
                    a[i * 4 + 0] * b[0 + j] +
                    a[i * 4 + 1] * b[4 + j] +
                    a[i * 4 + 2] * b[8 + j] +
                    a[i * 4 + 3] * b[12 + j]
            }
        }
        return result
    }

    /**
     * Get bone by name
     */
    fun getBone(name: String): Bone? = bones[name]

    /**
     * Get bone by index
     */
    fun getBoneByIndex(index: Int): Bone? = boneArray.getOrNull(index)

    /**
     * Set bone rotation
     */
    fun setBoneRotation(boneName: String, rotation: LLQuaternion) {
        bones[boneName]?.rotation = rotation
    }

    /**
     * Set bone position (for attachment points)
     */
    fun setBonePosition(boneName: String, position: LLVector3) {
        bones[boneName]?.position = position
    }

    /**
     * Get skinning matrices for GPU
     */
    fun getSkinningMatrices(): FloatArray {
        val result = FloatArray(boneArray.size * 16)
        boneArray.forEachIndexed { index, bone ->
            bone.skinningMatrix.copyInto(result, index * 16)
        }
        return result
    }
}

data class Bone(
    val name: String,
    val index: Int,
    val parent: Bone?,
    val children: MutableList<Bone>,
    val bindPosition: LLVector3,
    val bindRotation: LLQuaternion,
    var position: LLVector3,
    var rotation: LLQuaternion,
    var scale: LLVector3,
    var worldMatrix: FloatArray = FloatArray(16).apply {
        this[0] = 1f; this[5] = 1f; this[10] = 1f; this[15] = 1f
    },
    var inverseBindMatrix: FloatArray = FloatArray(16).apply {
        this[0] = 1f; this[5] = 1f; this[10] = 1f; this[15] = 1f
    },
    var skinningMatrix: FloatArray = FloatArray(16).apply {
        this[0] = 1f; this[5] = 1f; this[10] = 1f; this[15] = 1f
    }
)
