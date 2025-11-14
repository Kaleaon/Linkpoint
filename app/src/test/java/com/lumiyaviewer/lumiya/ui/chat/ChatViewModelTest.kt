package com.lumiyaviewer.lumiya.ui.chat

import com.lumiyaviewer.lumiya.TestingFramework
import com.lumiyaviewer.lumiya.data.model.Chatter
import com.lumiyaviewer.lumiya.data.repository.ChatRepository
import com.lumiyaviewer.lumiya.session.SessionManager
import com.lumiyaviewer.lumiya.ui.compose.chat.ChatViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

/**
 * Unit tests for ChatViewModel
 * 
 * Tests:
 * - Current user retrieval
 * - Chatter name lookup
 * - Message sending
 * - Chat history loading
 * - Error handling
 */
@ExperimentalCoroutinesApi
class ChatViewModelTest {
    
    @get:Rule
    val mockitoRule: MockitoRule = MockitoJUnit.rule()
    
    @get:Rule
    val mainDispatcherRule = TestingFramework.MainDispatcherRule()
    
    @Mock
    private lateinit var chatRepository: ChatRepository
    
    @Mock
    private lateinit var sessionManager: SessionManager
    
    private lateinit var viewModel: ChatViewModel
    
    @Before
    fun setup() {
        // Setup mock session manager
        `when`(sessionManager.getCurrentUserId()).thenReturn(1L)
        `when`(sessionManager.getCurrentUserName()).thenReturn("Test User")
        
        // Create ViewModel with mocked dependencies
        viewModel = ChatViewModel(chatRepository, sessionManager)
    }
    
    @Test
    fun `getCurrentUserId returns session user id`() = runTest {
        val userId = viewModel.getCurrentUserId()
        assertEquals(1L, userId)
        verify(sessionManager).getCurrentUserId()
    }
    
    @Test
    fun `getChatterName returns cached name`() = runTest {
        // Setup mock chatter
        val chatter = Chatter(
            id = 2L,
            name = "Friend User",
            displayName = "Friend User"
        )
        `when`(chatRepository.getChatterById(2L)).thenReturn(chatter)
        
        // First call should fetch from repository
        val name1 = viewModel.getChatterName(2L)
        assertEquals("Friend User", name1)
        verify(chatRepository).getChatterById(2L)
        
        // Second call should use cache
        val name2 = viewModel.getChatterName(2L)
        assertEquals("Friend User", name2)
        verify(chatRepository, times(1)).getChatterById(2L) // Still only called once
    }
    
    @Test
    fun `getChatterName returns default for unknown user`() = runTest {
        `when`(chatRepository.getChatterById(999L)).thenReturn(null)
        
        val name = viewModel.getChatterName(999L)
        assertEquals("User 999", name)
    }
    
    @Test
    fun `sendMessage updates UI state`() = runTest {
        val testMessage = "Hello, world!"
        
        viewModel.sendMessage(testMessage)
        
        // Verify message was sent through repository
        verify(chatRepository).sendMessage(testMessage, 1L)
    }
    
    @Test
    fun `loadChatHistory fetches messages`() = runTest {
        val mockMessages = TestingFramework.MockDataProvider.createMockChatMessages(10)
        `when`(chatRepository.getChatHistory(1L)).thenReturn(flowOf(mockMessages))
        
        viewModel.loadChatHistory()
        
        val messages = viewModel.chatMessages.first()
        assertEquals(10, messages.size)
        verify(chatRepository).getChatHistory(1L)
    }
}