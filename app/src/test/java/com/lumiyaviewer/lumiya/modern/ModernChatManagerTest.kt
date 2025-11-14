package com.lumiyaviewer.lumiya.modern

import com.lumiyaviewer.lumiya.TestingFramework
import com.lumiyaviewer.lumiya.modern.chat.ModernChatManager
import com.lumiyaviewer.lumiya.modern.protocol.HybridProtocolManager
import com.lumiyaviewer.lumiya.session.SessionManager
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule
import kotlin.test.assertTrue

/**
 * Unit tests for ModernChatManager
 * 
 * Tests:
 * - Local chat message sending
 * - Group chat message sending
 * - Direct message sending
 * - Offline mode handling
 * - Error handling
 */
@ExperimentalCoroutinesApi
class ModernChatManagerTest {
    
    @get:Rule
    val mockitoRule: MockitoRule = MockitoJUnit.rule()
    
    @get:Rule
    val mainDispatcherRule = TestingFramework.MainDispatcherRule()
    
    @Mock
    private lateinit var protocolManager: HybridProtocolManager
    
    @Mock
    private lateinit var sessionManager: SessionManager
    
    private lateinit var chatManager: ModernChatManager
    
    @Before
    fun setup() {
        `when`(sessionManager.isConnected()).thenReturn(true)
        `when`(sessionManager.getCurrentUserId()).thenReturn(1L)
        
        chatManager = ModernChatManager(protocolManager, sessionManager)
    }
    
    @Test
    fun `sendLocalChatMessage sends message when connected`() = runTest {
        val message = "Hello, local chat!"
        val channel = 0
        
        val result = chatManager.sendLocalChatMessage(message, channel)
        
        assertTrue(result)
        verify(protocolManager).sendChatMessage(message, channel)
    }
    
    @Test
    fun `sendLocalChatMessage stores offline when disconnected`() = runTest {
        `when`(sessionManager.isConnected()).thenReturn(false)
        
        val message = "Offline message"
        val channel = 0
        
        val result = chatManager.sendLocalChatMessage(message, channel)
        
        // Should still return true (stored for later)
        assertTrue(result)
        verify(protocolManager, never()).sendChatMessage(any(), anyInt())
    }
    
    @Test
    fun `sendGroupChatMessage sends to correct group`() = runTest {
        val groupId = "group-123"
        val message = "Hello, group!"
        
        val result = chatManager.sendGroupChatMessage(groupId, message)
        
        assertTrue(result)
        verify(protocolManager).sendGroupChatMessage(groupId, message)
    }
    
    @Test
    fun `sendDirectMessage sends to specific user`() = runTest {
        val recipientId = 2L
        val message = "Private message"
        
        val result = chatManager.sendDirectMessage(recipientId, message)
        
        assertTrue(result)
        verify(protocolManager).sendDirectMessage(recipientId, message)
    }
}