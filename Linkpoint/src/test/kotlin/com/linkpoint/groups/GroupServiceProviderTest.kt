package com.linkpoint.groups

import android.util.Log
import com.linkpoint.groups.provider.DefaultLlsdUdpGroupProvider
import com.linkpoint.groups.provider.FlotsamXmlRpcGroupClient
import com.linkpoint.groups.provider.GroupServiceProvider
import com.linkpoint.groups.provider.GroupServiceProviderFactory
import com.linkpoint.groups.provider.SimianRestGroupClient
import com.linkpoint.protocol.auth.LoginResponseParser
import com.linkpoint.protocol.capabilities.CapabilityManager
import com.linkpoint.protocol.messages.UDPConnectionFixed
import com.linkpoint.protocol.messages.ids.MessageIdRegistry
import kotlinx.coroutines.runBlocking
import org.junit.AfterClass
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.BeforeClass
import org.junit.Test
import org.mockito.MockedStatic
import org.mockito.Mockito
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.util.UUID

class GroupServiceProviderTest {

    companion object {
        private var mockedLog: MockedStatic<Log>? = null

        @JvmStatic
        @BeforeClass
        fun setUpClass() {
            mockedLog = Mockito.mockStatic(Log::class.java)
        }

        @JvmStatic
        @AfterClass
        fun tearDownClass() {
            mockedLog?.close()
        }
    }

    // ------------------------------------------------------------------------
    // 1. LoginResponseParser extraction of GroupServerURI
    // ------------------------------------------------------------------------

    @Test
    fun `LoginResponseParser extracts group_server_uri from login XML`() {
        val xml = """
            <?xml version="1.0"?>
            <methodResponse>
              <params>
                <param>
                  <value>
                    <struct>
                      <member>
                        <name>group_server_uri</name>
                        <value><string>http://grid.example.com:8002/groups.php</string></value>
                      </member>
                      <member>
                        <name>agent_access_max</name>
                        <value><string>PG</string></value>
                      </member>
                    </struct>
                  </value>
                </param>
              </params>
            </methodResponse>
        """.trimIndent()

        val parsed = LoginResponseParser.parse(xml)
        assertEquals("http://grid.example.com:8002/groups.php", parsed.groupServerUri)
    }

    @Test
    fun `LoginResponseParser extracts GroupServerURI variant from login XML`() {
        val xml = """
            <?xml version="1.0"?>
            <methodResponse>
              <params>
                <param>
                  <value>
                    <struct>
                      <member>
                        <name>GroupServerURI</name>
                        <value><string>http://simian.example.org/grid/groups/</string></value>
                      </member>
                    </struct>
                  </value>
                </param>
              </params>
            </methodResponse>
        """.trimIndent()

        val parsed = LoginResponseParser.parse(xml)
        assertEquals("http://simian.example.org/grid/groups/", parsed.groupServerUri)
    }

    @Test
    fun `LoginResponseParser returns null groupServerUri when tag absent`() {
        val xml = """
            <?xml version="1.0"?>
            <methodResponse>
              <params>
                <param>
                  <value>
                    <struct>
                      <member>
                        <name>agent_access_max</name>
                        <value><string>PG</string></value>
                      </member>
                    </struct>
                  </value>
                </param>
              </params>
            </methodResponse>
        """.trimIndent()

        val parsed = LoginResponseParser.parse(xml)
        assertNull(parsed.groupServerUri)
    }

    // ------------------------------------------------------------------------
    // 2. FlotsamXmlRpcGroupClient URL validation & parsing
    // ------------------------------------------------------------------------

    @Test
    fun `FlotsamXmlRpcGroupClient validates URI correctly`() {
        val validClient = FlotsamXmlRpcGroupClient(
            groupServerUri = "http://grid.example.com:8002/groups.php",
            agentId = UUID.randomUUID(),
            sessionId = UUID.randomUUID()
        )
        assertTrue(validClient.isAvailable)

        val invalidClient = FlotsamXmlRpcGroupClient(
            groupServerUri = "not-a-valid-url",
            agentId = UUID.randomUUID(),
            sessionId = UUID.randomUUID()
        )
        assertFalse(invalidClient.isAvailable)

        val emptyClient = FlotsamXmlRpcGroupClient(
            groupServerUri = "",
            agentId = UUID.randomUUID(),
            sessionId = UUID.randomUUID()
        )
        assertFalse(emptyClient.isAvailable)
    }

    @Test
    fun `FlotsamXmlRpcGroupClient parses member list XML response`() {
        val client = FlotsamXmlRpcGroupClient(
            groupServerUri = "http://grid.example.com/groups.php",
            agentId = UUID.randomUUID(),
            sessionId = UUID.randomUUID()
        )

        val xmlResponse = """
            <?xml version="1.0"?>
            <methodResponse>
              <params>
                <param>
                  <value>
                    <array>
                      <data>
                        <value>
                          <struct>
                            <member><name>AgentID</name><value><string>11111111-1111-1111-1111-111111111111</string></value></member>
                            <member><name>Title</name><value><string>Officer</string></value></member>
                            <member><name>IsOwner</name><value><string>1</string></value></member>
                            <member><name>AgentPowers</name><value><string>18446744073709551615</string></value></member>
                          </struct>
                        </value>
                        <value>
                          <struct>
                            <member><name>AgentID</name><value><string>22222222-2222-2222-2222-222222222222</string></value></member>
                            <member><name>Title</name><value><string>Member</string></value></member>
                            <member><name>IsOwner</name><value><string>0</string></value></member>
                            <member><name>AgentPowers</name><value><string>0</string></value></member>
                          </struct>
                        </value>
                      </data>
                    </array>
                  </value>
                </param>
              </params>
            </methodResponse>
        """.trimIndent()

        val members = client.parseMembersResponse(xmlResponse)
        assertEquals(2, members.size)

        val member1 = members[0]
        assertEquals(UUID.fromString("11111111-1111-1111-1111-111111111111"), member1.agentId)
        assertEquals("Officer", member1.title)
        assertTrue(member1.isOwner)

        val member2 = members[1]
        assertEquals(UUID.fromString("22222222-2222-2222-2222-222222222222"), member2.agentId)
        assertEquals("Member", member2.title)
        assertFalse(member2.isOwner)
    }

    // ------------------------------------------------------------------------
    // 3. SimianRestGroupClient URL validation & parsing
    // ------------------------------------------------------------------------

    @Test
    fun `SimianRestGroupClient validates URI correctly`() {
        val validClient = SimianRestGroupClient(
            groupServerUri = "http://simian.example.org/grid/",
            agentId = UUID.randomUUID(),
            sessionId = UUID.randomUUID()
        )
        assertTrue(validClient.isAvailable)

        val invalidClient = SimianRestGroupClient(
            groupServerUri = "ftp://invalid-scheme.com",
            agentId = UUID.randomUUID(),
            sessionId = UUID.randomUUID()
        )
        assertFalse(invalidClient.isAvailable)
    }

    @Test
    fun `SimianRestGroupClient parses Simian JSON response for members`() {
        val client = SimianRestGroupClient(
            groupServerUri = "http://simian.example.org/grid/",
            agentId = UUID.randomUUID(),
            sessionId = UUID.randomUUID()
        )

        val jsonResponse = """
            {
              "Success": true,
              "Members": [
                {
                  "AgentID": "33333333-3333-3333-3333-333333333333",
                  "Title": "Founder",
                  "IsOwner": true,
                  "AgentPowers": "12345"
                }
              ]
            }
        """.trimIndent()

        val members = client.parseMembersResponse(jsonResponse)
        assertEquals(1, members.size)
        assertEquals(UUID.fromString("33333333-3333-3333-3333-333333333333"), members[0].agentId)
        assertEquals("", members[0].title)
        assertFalse(members[0].isOwner)
    }

    // ------------------------------------------------------------------------
    // 4. GroupServiceProviderFactory selection logic
    // ------------------------------------------------------------------------

    @Test
    fun `GroupServiceProviderFactory selects Flotsam provider for groups php URI`() {
        val provider = GroupServiceProviderFactory.createProvider(
            groupServerUri = "http://grid.example.com/groups.php",
            agentId = UUID.randomUUID(),
            sessionId = UUID.randomUUID()
        )
        assertEquals("FlotsamXmlRpc", provider.providerType)
        assertTrue(provider is FlotsamXmlRpcGroupClient)
    }

    @Test
    fun `GroupServiceProviderFactory selects Simian provider for simian or rest URI`() {
        val provider = GroupServiceProviderFactory.createProvider(
            groupServerUri = "http://grid.example.com/simian/grid/groups",
            agentId = UUID.randomUUID(),
            sessionId = UUID.randomUUID()
        )
        assertEquals("SimianRest", provider.providerType)
        assertTrue(provider is SimianRestGroupClient)
    }

    @Test
    fun `GroupServiceProviderFactory selects Default LLSD UDP provider for null or empty URI`() {
        val providerNull = GroupServiceProviderFactory.createProvider(
            groupServerUri = null,
            agentId = UUID.randomUUID(),
            sessionId = UUID.randomUUID()
        )
        assertEquals("DefaultLLSD", providerNull.providerType)
        assertTrue(providerNull is DefaultLlsdUdpGroupProvider)

        val providerEmpty = GroupServiceProviderFactory.createProvider(
            groupServerUri = "",
            agentId = UUID.randomUUID(),
            sessionId = UUID.randomUUID()
        )
        assertEquals("DefaultLLSD", providerEmpty.providerType)
        assertTrue(providerEmpty is DefaultLlsdUdpGroupProvider)
    }

    // ------------------------------------------------------------------------
    // 5. GroupsManager provider integration and UDP fallback
    // ------------------------------------------------------------------------

    @Test
    fun `GroupsManager updates provider when groupServerUri is set`() {
        val agentId = UUID.randomUUID()
        val sessionId = UUID.randomUUID()

        val udpConnection: UDPConnectionFixed = mock()
        whenever(udpConnection.getSessionId()).thenReturn(sessionId)

        val manager = GroupsManager(
            udpConnection = udpConnection,
            capabilityManager = CapabilityManager(),
            agentId = agentId,
            initialGroupServerUri = null
        )

        assertEquals("DefaultLLSD", manager.groupServiceProvider.providerType)

        manager.setGroupServerUri("http://grid.example.com/groups.php")
        assertEquals("http://grid.example.com/groups.php", manager.groupServerUri)
        assertEquals("FlotsamXmlRpc", manager.groupServiceProvider.providerType)

        manager.setGroupServerUri(null)
        assertNull(manager.groupServerUri)
        assertEquals("DefaultLLSD", manager.groupServiceProvider.providerType)
    }

    @Test
    fun `GroupsManager requestGroupMembers falls back to UDP when provider fails`() {
        runBlocking {
            val agentId = UUID.fromString("11111111-1111-1111-1111-111111111111")
            val sessionId = UUID.fromString("22222222-2222-2222-2222-222222222222")
            val groupId = UUID.fromString("33333333-3333-3333-3333-333333333333")

            val udpConnection: UDPConnectionFixed = mock()
            whenever(udpConnection.getSessionId()).thenReturn(sessionId)

            val failingProvider: GroupServiceProvider = mock()
            whenever(failingProvider.isAvailable).thenReturn(true)
            whenever(failingProvider.providerType).thenReturn("FlotsamXmlRpc")
            whenever(failingProvider.getGroupMembers(groupId)).thenReturn(Result.failure(RuntimeException("Network error")))

            val manager = GroupsManager(
                udpConnection = udpConnection,
                capabilityManager = CapabilityManager(),
                agentId = agentId,
                initialProvider = failingProvider
            )

            val result = manager.requestGroupMembers(groupId)
            assertTrue(result.isSuccess)

            // Verify fallback sent UDP GROUP_MEMBERS_REQUEST
            verify(udpConnection).sendPacket(
                eq(MessageIdRegistry.GROUP_MEMBERS_REQUEST),
                org.mockito.kotlin.any(),
                eq(true),
                eq(false),
                org.mockito.kotlin.anyOrNull()
            )
        }
    }
}
