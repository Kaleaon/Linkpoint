package com.linkpoint.feature.system

import com.linkpoint.objects.BuildTools
import com.linkpoint.objects.ObjectManager
import com.linkpoint.protocol.messages.ObjectUpdateData
import com.linkpoint.protocol.messages.UDPConnectionFixed
import com.linkpoint.protocol.types.LLColor4
import com.linkpoint.protocol.types.LLQuaternion
import com.linkpoint.protocol.types.LLVector3
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.util.UUID

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
class BuildToolsViewModelTest {

    private lateinit var udpConnection: UDPConnectionFixed
    private lateinit var objectManager: ObjectManager
    private lateinit var buildTools: BuildTools
    private lateinit var viewModel: BuildToolsViewModel

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
        viewModel = BuildToolsViewModel(objectManager, buildTools)
    }

    @Test
    fun initialStateIsEmptySelection() = runTest {
        val state = viewModel.uiState.first { it != BuildToolsUiState.Loading }
        assertEquals(BuildToolsUiState.EmptySelection, state)
    }

    @Test
    fun selectingObjectEmitsReadyState() = runTest {
        val localId = 42
        val fullId = UUID.randomUUID()
        objectManager.handleObjectUpdate(
            testObjectData(
                localId = localId,
                fullId = fullId,
                position = LLVector3(12f, 24f, 36f)
            )
        )
        objectManager.setObjectName(localId, "Test Prim")

        objectManager.selectObjects(listOf(localId))

        val state = viewModel.uiState.first { it is BuildToolsUiState.Ready } as BuildToolsUiState.Ready
        assertEquals("Test Prim", state.selectionName)
        assertEquals(localId, state.primaryObjectLocalId)
        assertEquals(12f, state.position.x, 0.001f)
        assertEquals(24f, state.position.y, 0.001f)
        assertEquals(36f, state.position.z, 0.001f)
    }

    @Test
    fun updatePositionDelegatesToObjectManager() = runTest {
        val localId = 43
        val fullId = UUID.randomUUID()
        objectManager.handleObjectUpdate(
            testObjectData(
                localId = localId,
                fullId = fullId,
                position = LLVector3(0f, 0f, 0f)
            )
        )

        viewModel.updatePosition(localId, LLVector3(5f, 10f, 15f))

        val obj = objectManager.getObject(localId)
        assertNotNull(obj)
        assertEquals(5f, obj!!.position.x, 0.001f)
        assertEquals(10f, obj.position.y, 0.001f)
        assertEquals(15f, obj.position.z, 0.001f)
    }
}
