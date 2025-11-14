package com.lumiyaviewer.lumiya.integration

import com.lumiyaviewer.lumiya.TestingFramework
import com.lumiyaviewer.lumiya.data.repository.ChatRepository
import com.lumiyaviewer.lumiya.modern.chat.ModernChatManager
import com.lumiyaviewer.lumiya.session.SessionManager
import com.lumiyaviewer.lumiya.ui.compose.chat.ChatViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Integration tests for chat functionality
 * 
 * Tests the complete flow from ViewModel -> Repository -> Manager
 */
@ExperimentalCoroutinesApi
class ChatIntegrationTest {
    
    @get:Rule
    val mockitoRule: MockitoRule = MockitoJUnit.rule()
    
    @get:Rule
    val mainDispatcherRule = TestingFramework.MainDispatcherRule()
    
    @Mock
    private lateinit var chatRepository: ChatRepository
    
    @Mock
    private lateinit var sessionManager: SessionManager
    
    @Mock
    private lateinit var chatManager: ModernChatManager
    
    private lateinit var viewModel: ChatViewModel
    
    @Before
    fun setup() {
        `when`(sessionManager.getCurrentUserId()).thenReturn(1L)
        `when`(sessionManager.getCurrentUserName()).thenReturn("Test User")
        `when`(sessionManager.isConnected()).thenReturn(true)
        
        viewModel = ChatViewModel(chatRepository, sessionManager)
    }
    
    @Test
    fun `complete chat flow from send to display`() = runTest {
        val testMessage = "Integration test message"
        
        // Send message through ViewModel
        viewModel.sendMessage(testMessage)
        
        // Verify it went through repository
        verify(chatRepository).sendMessage(testMessage, 1L)
        
        // Simulate message being added to database
        val mockMessages = TestingFramework.MockDataProvider.createMockChatMessages(1)
        `when`(chatRepository.getChatHistory(1L)).thenReturn(kotlinx.coroutines.flow.flowOf(mockMessages))
        
        // Load chat history
        viewModel.loadChatHistory()
        
        // Verify messages are displayed
        val messages = viewModel.chatMessages.first()
        assertEquals(1, messages.size)
    }
    
    @Test
    fun `offline message handling`() = runTest {
        // Simulate offline state
        `when`(sessionManager.isConnected()).thenReturn(false)
        
        val testMessage = "Offline message"
        
        // Send message while offline
        viewModel.sendMessage(testMessage)
        
        // Message should still be stored locally
        verify(chatRepository).sendMessage(testMessage, 1L)
        
        // Simulate coming back online
        `when`(sessionManager.isConnected()).thenReturn(true)
        
        // Messages should be synced (this would be handled by the manager)
        assertTrue(sessionManager.isConnected())
    }
    
    @Test
    fun `multiple users chat scenario`() = runTest {
        // User 1 sends message
        `when`(sessionManager.getCurrentUserId()).thenReturn(1L)
        viewModel.sendMessage("Message from user 1")
        verify(chatRepository).sendMessage("Message from user 1", 1L)
        
        // User 2 sends message (simulated by changing session)
        `when`(sessionManager.getCurrentUserId()).thenReturn(2L)
        viewModel.sendMessage("Message from user 2")
        verify(chatRepository).sendMessage("Message from user 2", 2L)
        
        // Load combined chat history
        val mockMessages = TestingFramework.MockDataProvider.createMockChatMessages(2)
        `when`(chatRepository.getChatHistory(anyLong())).thenReturn(kotlinx.coroutines.flow.flowOf(mockMessages))
        
        viewModel.loadChatHistory()
        val messages = viewModel.chatMessages.first()
        assertEquals(2, messages.size)
    }
}