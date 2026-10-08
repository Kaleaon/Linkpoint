package com.linkpoint.objects

import com.linkpoint.protocol.messages.UDPConnectionFixed
import com.linkpoint.protocol.messages.ids.MessageIdRegistry
import com.linkpoint.protocol.messages.ObjectUpdateData
import com.linkpoint.protocol.types.LLColor4
import com.linkpoint.protocol.types.LLQuaternion
import com.linkpoint.protocol.types.LLVector3
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.*
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.UUID

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
class ObjectManagerProtocolTest {

    private lateinit var udpConnection: UDPConnectionFixed
    private lateinit var objectManager: ObjectManager

    private val testAgentId = UUID.randomUUID()
    private val testSessionId = UUID.randomUUID()

    private fun testObjectData(
        localId: Int,
        fullId: UUID = UUID.randomUUID(),
        position: LLVector3 = LLVector3.zero(),
        updateFlags: Int = 0
    ): ObjectUpdateData {
        return ObjectUpdateData(
            localId = localId,
            fullId = fullId,
            parentId = 0,
            position = position,
            rotation = LLQuaternion.identity(),
            velocity = LLVector3.zero(),
            scale = LLVector3.one(),
            pcode = 9,
            material = 0,
            clickAction = 0,
            updateFlags = updateFlags,
            textureEntry = ByteArray(0),
            hoverText = "",
            hoverTextColor = LLColor4(0f, 0f, 0f, 0f),
            mediaUrl = ""
        )
    }

    @Before
    fun setUp() {
        udpConnection = mock {
            on { getAgentId() } doReturn testAgentId
            on { getSessionId() } doReturn testSessionId
            on { getCircuitCode() } doReturn 12345
        }
        objectManager = ObjectManager(udpConnection)
    }

    @Test
    fun createPrimSendsObjectAddPacket() = runTest {
        val params = PrimCreateParams(
            primType = BuildTools.PRIM_BOX,
            position = LLVector3(10f, 20f, 30f),
            rotation = LLQuaternion.identity(),
            scale = LLVector3(1f, 1f, 1f),
            pathParams = PathParams(),
            profileParams = ProfileParams()
        )

        objectManager.createPrim(params)

        verify(udpConnection, timeout(2000)).sendPacket(
            eq(MessageIdRegistry.OBJECT_ADD),
            check { payload ->
                assertTrue("Payload size should be non-empty", payload.isNotEmpty())
                val buffer = ByteBuffer.wrap(payload)
                val agentMsb = buffer.long
                val agentLsb = buffer.long
                assertEquals(testAgentId, UUID(agentMsb, agentLsb))
            },
            eq(true),
            eq(false),
            anyOrNull()
        )
    }

    @Test
    fun duplicateObjectSendsObjectDuplicatePacket() = runTest {
        val fullId = UUID.randomUUID()
        val localId = 1001
        objectManager.handleObjectUpdate(
            testObjectData(
                localId = localId,
                fullId = fullId,
                position = LLVector3(10f, 10f, 10f)
            )
        )

        objectManager.duplicateObject(localId, LLVector3(2f, 0f, 0f))

        verify(udpConnection, timeout(2000)).sendPacket(
            eq(MessageIdRegistry.OBJECT_DUPLICATE),
            check { payload ->
                assertTrue(payload.isNotEmpty())
            },
            eq(true),
            eq(false),
            anyOrNull()
        )
    }

    @Test
    fun sendObjectFlagsSendsObjectFlagUpdateAndUpdatesLocalFlags() = runTest {
        val fullId = UUID.randomUUID()
        val localId = 1002
        objectManager.handleObjectUpdate(
            testObjectData(
                localId = localId,
                fullId = fullId,
                position = LLVector3(5f, 5f, 5f),
                updateFlags = 0
            )
        )

        objectManager.sendObjectFlags(localId, usePhysics = true, isPhantom = true)

        val obj = objectManager.getObject(localId)
        assertNotNull(obj)
        assertTrue(obj!!.isPhysical)
        assertTrue(obj.isPhantom)

        verify(udpConnection, timeout(2000)).sendPacket(
            eq(MessageIdRegistry.OBJECT_FLAG_UPDATE),
            check { payload ->
                assertTrue(payload.isNotEmpty())
            },
            eq(true),
            eq(false),
            anyOrNull()
        )
    }

    @Test
    fun updateObjectPositionUpdatesOnlyTargetObjectPosition() {
        val id1 = 101
        val id2 = 102
        objectManager.handleObjectUpdate(testObjectData(localId = id1, position = LLVector3(10f, 0f, 0f)))
        objectManager.handleObjectUpdate(testObjectData(localId = id2, position = LLVector3(20f, 0f, 0f)))

        objectManager.selectObjects(listOf(id1, id2))

        val newPos = LLVector3(15f, 0f, 0f)
        objectManager.updateObjectPosition(id1, newPos)

        val obj1 = objectManager.getObject(id1)
        val obj2 = objectManager.getObject(id2)

        assertNotNull(obj1)
        assertNotNull(obj2)
        assertEquals(15f, obj1!!.position.x, 0.001f)
        assertEquals(20f, obj2!!.position.x, 0.001f)
    }
}
