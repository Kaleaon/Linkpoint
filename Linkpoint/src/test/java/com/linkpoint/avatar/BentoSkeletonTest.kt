package com.linkpoint.avatar

import com.linkpoint.protocol.messages.UDPConnectionFixed
import com.linkpoint.protocol.types.LLQuaternion
import com.linkpoint.protocol.types.LLVector3
import org.junit.Assert.*
import org.junit.Test
import org.mockito.Mockito.mock
import kotlin.system.measureTimeMillis

class BentoSkeletonTest {

    @Test
    fun testBonesContainsAllBentoJoints() {
        val bones = AvatarSkeleton.BONES
        assertTrue("BONES must contain at least 133 Bento joints", bones.size >= 133)

        // Facial joints
        val facialJoints = listOf(
            "mFaceRoot", "mFaceEar1Left", "mFaceEar2Left", "mFaceEar1Right", "mFaceEar2Right",
            "mFaceJaw", "mFaceChin", "mFaceTeethLower", "mFaceTeethUpper", "mFaceTongueTip", "mFaceTongueBase",
            "mFaceEyeAltLeft", "mFaceEyeAltRight", "mFaceForeheadCenter", "mFaceNoseBridge"
        )
        for (joint in facialJoints) {
            assertTrue("BONES must include facial joint $joint", bones.contains(joint))
        }

        // Hand joints
        val handJoints = listOf(
            "mHandMiddle1Left", "mHandMiddle2Left", "mHandMiddle3Left",
            "mHandIndex1Left", "mHandIndex2Left", "mHandIndex3Left",
            "mHandRing1Left", "mHandRing2Left", "mHandRing3Left",
            "mHandPinky1Left", "mHandPinky2Left", "mHandPinky3Left",
            "mHandThumb1Left", "mHandThumb2Left", "mHandThumb3Left"
        )
        for (joint in handJoints) {
            assertTrue("BONES must include hand joint $joint", bones.contains(joint))
        }

        // Wing joints
        val wingJoints = listOf("mWingsRoot", "mWing1Left", "mWing2Left", "mWing3Left", "mWing4Left", "mWing4FanLeft")
        for (joint in wingJoints) {
            assertTrue("BONES must include wing joint $joint", bones.contains(joint))
        }

        // Tail joints
        val tailJoints = listOf("mTail1", "mTail2", "mTail3", "mTail4", "mTail5", "mTail6")
        for (joint in tailJoints) {
            assertTrue("BONES must include tail joint $joint", bones.contains(joint))
        }

        // Groin & Hindlimbs
        val hindJoints = listOf("mGroin", "mHindLimbsRoot", "mHindLimb1Left", "mHindLimb4Left")
        for (joint in hindJoints) {
            assertTrue("BONES must include hind joint $joint", bones.contains(joint))
        }
    }

    @Test
    fun testDefaultSkeletonHierarchyAndMatrixPropagation() {
        val skeleton = AvatarSkeleton(null)
        
        // Every bone in BONES should exist in skeleton.bones
        for (boneName in AvatarSkeleton.BONES) {
            assertNotNull("Bone $boneName should exist in skeleton", skeleton.getBone(boneName))
        }

        // Parent-child linkage tests
        val faceRoot = skeleton.getBone("mFaceRoot")
        assertNotNull(faceRoot)
        assertEquals("mHead", faceRoot?.parent?.name)

        val ear2Left = skeleton.getBone("mFaceEar2Left")
        assertNotNull(ear2Left)
        assertEquals("mFaceEar1Left", ear2Left?.parent?.name)

        val tail6 = skeleton.getBone("mTail6")
        assertNotNull(tail6)
        assertEquals("mTail5", tail6?.parent?.name)

        // Matrix propagation test
        val originalJawWorldZ = skeleton.getBone("mFaceJaw")!!.worldMatrix[14]
        
        // Shift head position upwards
        val head = skeleton.getBone("mHead")!!
        head.position = LLVector3(head.position.x, head.position.y, head.position.z + 1.0f)
        
        // Recalculate
        val propagationTime = measureTimeMillis {
            skeleton.updateBoneMatrices()
        }

        val updatedJawWorldZ = skeleton.getBone("mFaceJaw")!!.worldMatrix[14]
        assertEquals("Child Bento bone world matrix Z must propagate parent translation",
            originalJawWorldZ + 1.0f, updatedJawWorldZ, 1e-3f)
        assertTrue("Matrix propagation must run within 2ms (was ${propagationTime}ms)", propagationTime <= 2)
    }

    @Test
    fun testAttachmentPointsBentoBindings() {
        assertEquals("mHandRing1Left", AttachmentPoints.getPoint(AttachmentPoints.ATTACH_LHAND_RING1)?.jointName)
        assertEquals("mHandRing1Right", AttachmentPoints.getPoint(AttachmentPoints.ATTACH_RHAND_RING1)?.jointName)
        assertEquals("mTail1", AttachmentPoints.getPoint(AttachmentPoints.ATTACH_TAIL_BASE)?.jointName)
        assertEquals("mTail6", AttachmentPoints.getPoint(AttachmentPoints.ATTACH_TAIL_TIP)?.jointName)
        assertEquals("mWing1Left", AttachmentPoints.getPoint(AttachmentPoints.ATTACH_LWING)?.jointName)
        assertEquals("mWing1Right", AttachmentPoints.getPoint(AttachmentPoints.ATTACH_RWING)?.jointName)
        assertEquals("mFaceJaw", AttachmentPoints.getPoint(AttachmentPoints.ATTACH_FACE_JAW)?.jointName)
        assertEquals("mFaceEar1Left", AttachmentPoints.getPoint(AttachmentPoints.ATTACH_FACE_LEAR)?.jointName)
        assertEquals("mFaceEar1Right", AttachmentPoints.getPoint(AttachmentPoints.ATTACH_FACE_REAR)?.jointName)
        assertEquals("mFaceEyeAltLeft", AttachmentPoints.getPoint(AttachmentPoints.ATTACH_FACE_LEYE)?.jointName)
        assertEquals("mFaceEyeAltRight", AttachmentPoints.getPoint(AttachmentPoints.ATTACH_FACE_REYE)?.jointName)
        assertEquals("mFaceTongueTip", AttachmentPoints.getPoint(AttachmentPoints.ATTACH_FACE_TONGUE)?.jointName)
        assertEquals("mGroin", AttachmentPoints.getPoint(AttachmentPoints.ATTACH_GROIN)?.jointName)
        assertEquals("mHindLimb4Left", AttachmentPoints.getPoint(AttachmentPoints.ATTACH_HIND_LFOOT)?.jointName)
        assertEquals("mHindLimb4Right", AttachmentPoints.getPoint(AttachmentPoints.ATTACH_HIND_RFOOT)?.jointName)
    }

    @Test
    fun testAppearanceManagerSetVisualParamsTriggersPropagation() {
        val udp = mock(UDPConnectionFixed::class.java)
        val baker = mock(AvatarBaker::class.java)
        val skeleton = AvatarSkeleton(null)
        val appMgr = AppearanceManager(udp, baker, null, skeleton)

        val params = ByteArray(AppearanceManager.VISUAL_PARAM_COUNT) { 200.toByte() }
        // Warm up JVM JIT / coroutine dispatcher
        appMgr.setVisualParams(params)

        val start = System.nanoTime()
        appMgr.setVisualParams(params)
        val durationMs = (System.nanoTime() - start) / 1_000_000.0
        println("DEBUG setVisualParams durationMs: $durationMs")

        assertTrue("setVisualParams execution must be fast (< 10ms, was ${durationMs}ms)", durationMs <= 10.0)
        assertNotNull(skeleton.getBone("mFaceJaw")?.worldMatrix)
    }

    @Test
    fun testSkinningMatricesPreserveBentoJointsWithoutScrubbingToPelvis() {
        val skeleton = AvatarSkeleton(null)
        val testJoints = listOf("mFaceJaw", "mHandRing1Left", "mWing1Left", "mTail1", "mHindLimb4Left")
        
        val pelvisBone = skeleton.getBone("mPelvis")!!
        
        for (jointName in testJoints) {
            val bone = skeleton.getBone(jointName)
            assertNotNull("Bento joint $jointName must not return null bone", bone)
            assertNotEquals("Bento joint $jointName index must not scrub to mPelvis index",
                pelvisBone.index, bone?.index)
            assertNotNull("Bento joint $jointName must have a valid 16-float skinningMatrix",
                bone?.skinningMatrix)
            assertEquals(16, bone?.skinningMatrix?.size)
        }
    }
}
