package com.linkpoint.rlv

import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.util.UUID

class RLVControllerTest {

    private lateinit var controller: RLVController

    @Before
    fun setUp() {
        controller = RLVController()
        controller.setEnabled(true)
    }

    @Test
    fun `Tier 1 soft restrictions execute immediately and dispatch notification listener event`() {
        var notifiedObjectId: UUID? = null
        var notifiedObjectName: String? = null
        var notifiedCommand: String? = null
        var notifiedRestricted: Boolean? = null

        controller.registerNotificationListener { objectId, objectName, command, _, restricted ->
            notifiedObjectId = objectId
            notifiedObjectName = objectName
            notifiedCommand = command
            notifiedRestricted = restricted
        }

        val objId = UUID.randomUUID()
        val result = controller.processCommand(objId, "Collar Device", "@detach=n")

        assertEquals(RLVResult.Success, result)
        assertTrue(controller.isRestricted("detach"))
        assertEquals(objId, notifiedObjectId)
        assertEquals("Collar Device", notifiedObjectName)
        assertEquals("detach", notifiedCommand)
        assertEquals(true, notifiedRestricted)
    }

    @Test
    fun `Tier 2 force command pauses execution and requests user approval from prompt handler`() {
        var promptReceived = false
        var requestedAction: String? = null

        val objId = UUID.randomUUID()

        controller.setPromptHandler { _, _, objectName, action, _, onDecision ->
            promptReceived = true
            requestedAction = action
            assertEquals("Trap Chair", objectName)
            onDecision(RLVDecision.APPROVE)
        }

        val result = controller.processCommand(objId, "Trap Chair", "@sit=force")

        assertTrue(promptReceived)
        assertEquals("sit", requestedAction)
        assertEquals(RLVResult.Success, result)
    }

    @Test
    fun `Tier 2 force command returns failure when denied by user`() {
        val objId = UUID.randomUUID()

        controller.setPromptHandler { _, _, _, _, _, onDecision ->
            onDecision(RLVDecision.DENY)
        }

        val result = controller.processCommand(objId, "Control Chair", "@sit=force")

        assertEquals(RLVResult.Failed, result)
    }

    @Test
    fun `Tier 2 force command with Always Allow for Session bypasses subsequent prompts`() {
        var promptCount = 0
        val objId = UUID.randomUUID()

        controller.setPromptHandler { _, _, _, _, _, onDecision ->
            promptCount++
            onDecision(RLVDecision.ALWAYS_ALLOW_SESSION)
        }

        val result1 = controller.processCommand(objId, "Wardrobe", "@remoutfit=force")
        assertEquals(RLVResult.Success, result1)
        assertEquals(1, promptCount)
        assertTrue(controller.isSessionTrusted(objId))

        // Second command from same object UUID executes without prompting
        val result2 = controller.processCommand(objId, "Wardrobe", "@remoutfit=force")
        assertEquals(RLVResult.Success, result2)
        assertEquals(1, promptCount)
    }

    @Test
    fun `clearSessionTrust resets pre-approved session authorizations`() {
        val objId = UUID.randomUUID()

        controller.setPromptHandler { _, _, _, _, _, onDecision ->
            onDecision(RLVDecision.ALWAYS_ALLOW_SESSION)
        }

        controller.processCommand(objId, "Wardrobe", "@remoutfit=force")
        assertTrue(controller.isSessionTrusted(objId))

        controller.clearSessionTrust()
        assertFalse(controller.isSessionTrusted(objId))
    }

    @Test
    fun `getActiveRestrictionsGroupedByObject groups active restrictions by issuing object UUID`() {
        val obj1 = UUID.randomUUID()
        val obj2 = UUID.randomUUID()

        controller.processCommand(obj1, "Collar", "@detach=n,@sendchat=n")
        controller.processCommand(obj2, "Shackles", "@tploc=n")

        val grouped = controller.getActiveRestrictionsGroupedByObject()

        assertEquals(2, grouped.size)
        assertTrue(grouped.containsKey(obj1))
        assertTrue(grouped.containsKey(obj2))

        val obj1Restrictions = grouped[obj1]?.map { it.command } ?: emptyList()
        assertTrue(obj1Restrictions.contains("detach"))
        assertTrue(obj1Restrictions.contains("sendchat"))

        val obj2Restrictions = grouped[obj2]?.map { it.command } ?: emptyList()
        assertTrue(obj2Restrictions.contains("tploc"))
    }

    @Test
    fun `clearRestrictions clears active restrictions for specific object`() {
        val obj1 = UUID.randomUUID()
        val obj2 = UUID.randomUUID()

        controller.processCommand(obj1, "Collar", "@detach=n")
        controller.processCommand(obj2, "Shackles", "@tploc=n")

        assertTrue(controller.isRestricted("detach"))
        assertTrue(controller.isRestricted("tploc"))

        controller.clearRestrictions(obj1)

        assertFalse(controller.isRestricted("detach"))
        assertTrue(controller.isRestricted("tploc"))
    }
}
