package com.linkpoint.objects

import com.linkpoint.protocol.messages.ObjectUpdateData
import com.linkpoint.protocol.messages.UDPConnectionFixed
import com.linkpoint.protocol.types.LLColor4
import com.linkpoint.protocol.types.LLQuaternion
import com.linkpoint.protocol.types.LLVector3
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.util.UUID

@RunWith(RobolectricTestRunner::class)
class BuildToolsDistributionTest {

    private lateinit var udpConnection: UDPConnectionFixed
    private lateinit var objectManager: ObjectManager
    private lateinit var buildTools: BuildTools

    private fun testObjectData(
        localId: Int,
        fullId: UUID = UUID.randomUUID(),
        position: LLVector3 = LLVector3.zero()
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
            updateFlags = 0,
            textureEntry = ByteArray(0),
            hoverText = "",
            hoverTextColor = LLColor4(0f, 0f, 0f, 0f),
            mediaUrl = ""
        )
    }

    @Before
    fun setUp() {
        udpConnection = mock {
            on { getAgentId() } doReturn UUID.randomUUID()
            on { getSessionId() } doReturn UUID.randomUUID()
            on { getCircuitCode() } doReturn 12345
        }
        objectManager = ObjectManager(udpConnection)
        buildTools = BuildTools(objectManager)
    }

    @Test
    fun distributeSelectionPositionsObjectsIndependentlyWithoutCompoundDisplacement() {
        val id1 = 1
        val id2 = 2
        val id3 = 3

        objectManager.handleObjectUpdate(testObjectData(localId = id1, position = LLVector3(0f, 0f, 0f)))
        objectManager.handleObjectUpdate(testObjectData(localId = id2, position = LLVector3(1f, 0f, 0f)))
        objectManager.handleObjectUpdate(testObjectData(localId = id3, position = LLVector3(10f, 0f, 0f)))

        objectManager.selectObjects(listOf(id1, id2, id3))

        buildTools.distributeSelection(Axis.X)

        val obj1 = objectManager.getObject(id1)
        val obj2 = objectManager.getObject(id2)
        val obj3 = objectManager.getObject(id3)

        assertEquals(0f, obj1!!.position.x, 0.001f)
        assertEquals(5f, obj2!!.position.x, 0.001f)
        assertEquals(10f, obj3!!.position.x, 0.001f)
    }
}
