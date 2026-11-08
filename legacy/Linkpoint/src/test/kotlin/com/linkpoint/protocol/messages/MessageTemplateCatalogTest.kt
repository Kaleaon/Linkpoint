package com.linkpoint.protocol.messages

import com.linkpoint.linden.llmessage.Deprecation
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class MessageTemplateCatalogTest {

    @Test
    fun testRuntimeTemplateLookup() {
        val agentAlert = MessageTemplateCatalog["AgentAlertMessage"]
        assertNotNull("AgentAlertMessage should be present in catalog", agentAlert)
        assertEquals("AgentAlertMessage", agentAlert?.name)
        assertEquals(Deprecation.NOT_DEPRECATED, agentAlert?.deprecation)
        assertTrue(MessageTemplateCatalog.contains("AgentAlertMessage"))

        val nonExistent = MessageTemplateCatalog["NonExistentFakeMessage"]
        assertNull("Non-existent message should return null", nonExistent)
        assertFalse(MessageTemplateCatalog.contains("NonExistentFakeMessage"))
    }

    @Test
    fun testDeprecatedMessagesLookup() {
        val deprecatedMessage = MessageTemplateCatalog["AgentDropGroup"]
        assertNotNull("AgentDropGroup should be registered", deprecatedMessage)
        assertEquals("AgentDropGroup", deprecatedMessage?.name)
        assertEquals(Deprecation.DEPRECATED, deprecatedMessage?.deprecation)

        assertTrue(MessageTemplateCatalog.deprecatedNames.contains("AgentDropGroup"))
        assertFalse(MessageTemplateCatalog.activeNames.contains("AgentDropGroup"))
    }

    @Test
    fun testCatalogSizesAndPartitioning() {
        assertTrue(MessageTemplateCatalog.size > 0)
        assertEquals(MessageTemplateCatalog.size, MessageTemplateCatalog.allNames.size)

        val active = MessageTemplateCatalog.activeNames
        val deprecated = MessageTemplateCatalog.deprecatedNames

        // Disjoint sets
        assertTrue("Active and deprecated sets should be disjoint", active.intersect(deprecated).isEmpty())

        // Union equals allNames
        assertEquals("Union of active and deprecated names must match allNames", MessageTemplateCatalog.allNames, active + deprecated)
    }

    @Test
    fun testProtocolClassificationPreserved() {
        assertTrue(MessageTemplateCatalog.supportedParserOrWriterMessages.isNotEmpty())
        assertTrue(MessageTemplateCatalog.declaredOnlyMessagesWithRationale.isNotEmpty())
        assertTrue(MessageTemplateCatalog.deprecatedMessagesWithRationale.isNotEmpty())
    }
}
