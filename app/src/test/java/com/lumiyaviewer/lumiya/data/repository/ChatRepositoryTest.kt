package com.lumiyaviewer.lumiya.data.repository

import com.lumiyaviewer.lumiya.TestingFramework
import com.lumiyaviewer.lumiya.dao.ChatterDao
import com.lumiyaviewer.lumiya.dao.MessageDao
import com.lumiyaviewer.lumiya.data.model.Chatter
import com.lumiyaviewer.lumiya.data.model.Message
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
import kotlin.test.assertNull

/**
 * Unit tests for ChatRepository
 * 
 * Tests:
 * - Message retrieval
 * - Chatter lookup
 * - Message insertion
 * - Database operations
 */
@ExperimentalCoroutinesApi
class ChatRepositoryTest {
    
    @get:Rule
    val mockitoRule: MockitoRule = MockitoJUnit.rule()
    
    @get:Rule
    val mainDispatcherRule = TestingFramework.MainDispatcherRule()
    
    @Mock
    private lateinit var messageDao: MessageDao
    
    @Mock
    private lateinit var chatterDao: ChatterDao
    
    private lateinit var repository: ChatRepository
    
    @Before
    fun setup() {
        repository = ChatRepository(messageDao, chatterDao)
    }
    
    @Test
    fun `getChatHistory returns messages from dao`() = runTest {
        val mockMessages = TestingFramework.MockDataProvider.createMockChatMessages(5)
        `when`(messageDao.getMessagesForChatter(1L)).thenReturn(flowOf(mockMessages))
        
        val messages = repository.getChatHistory(1L).first()
        
        assertEquals(5, messages.size)
        verify(messageDao).getMessagesForChatter(1L)
    }
    
    @Test
    fun `getChatterById returns chatter when exists`() = runTest {
        val mockChatter = Chatter(
            id = 1L,
            name = "Test User",
            displayName = "Test User"
        )
        `when`(chatterDao.getChatterById(1L)).thenReturn(mockChatter)
        
        val chatter = repository.getChatterById(1L)
        
        assertNotNull(chatter)
        assertEquals("Test User", chatter.name)
        verify(chatterDao).getChatterById(1L)
    }
    
    @Test
    fun `getChatterById returns null when not exists`() = runTest {
        `when`(chatterDao.getChatterById(999L)).thenReturn(null)
        
        val chatter = repository.getChatterById(999L)
        
        assertNull(chatter)
        verify(chatterDao).getChatterById(999L)
    }
    
    @Test
    fun `sendMessage inserts message to dao`() = runTest {
        val message = Message(
            id = 0,
            chatterId = 1L,
            content = "Test message",
            timestamp = System.currentTimeMillis(),
            isFromMe = true
        )
        
        repository.sendMessage("Test message", 1L)
        
        verify(messageDao).insert(any(Message::class.java))
    }
    
    @Test
    fun `getAllChatters returns all chatters`() = runTest {
        val mockChatters = listOf(
            Chatter(1L, "User 1", "User 1"),
            Chatter(2L, "User 2", "User 2"),
            Chatter(3L, "User 3", "User 3")
        )
        `when`(chatterDao.getAllChatters()).thenReturn(flowOf(mockChatters))
        
        val chatters = repository.getAllChatters().first()
        
        assertEquals(3, chatters.size)
        verify(chatterDao).getAllChatters()
    }
}