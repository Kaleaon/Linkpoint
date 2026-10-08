package com.linkpoint.notifications

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.linkpoint.protocol.capabilities.CapabilityManager
import com.linkpoint.protocol.llsd.LLSDMap
import com.linkpoint.protocol.llsd.LLSDString
import com.linkpoint.protocol.llsd.LLSDUUID
import com.linkpoint.teleport.TeleportLure
import com.linkpoint.teleport.TeleportManager
import com.linkpoint.teleport.TeleportResult
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.robolectric.RobolectricTestRunner
import java.util.UUID

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
class NotificationManagerTest {

    private lateinit var context: Context
    private lateinit var capabilityManager: CapabilityManager
    private lateinit var notificationManager: NotificationManager
    private lateinit var mockTeleportManager: TeleportManager

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        capabilityManager = CapabilityManager()
        notificationManager = NotificationManager(context, capabilityManager)

        mockTeleportManager = mock {
            on { teleportEvents } doReturn MutableSharedFlow()
        }
        notificationManager.teleportManager = mockTeleportManager
    }

    @Test
    fun testCapabilityRegistrationRegistersTeleportOfferRequest() {
        val registeredTypes = capabilityManager.getRegisteredEventTypes()
        assertTrue(registeredTypes.contains("TeleportOfferRequest"))
    }

    @Test
    fun testHandleTeleportOfferUpdatesActiveLureAndCreatesNotification() {
        val senderId = UUID.randomUUID()
        val lureId = UUID.randomUUID()
        val body = LLSDMap().apply {
            this["from_id"] = LLSDUUID(senderId)
            this["from_name"] = LLSDString("Test Sender")
            this["region_name"] = LLSDString("Test Region")
            this["message"] = LLSDString("Come visit!")
            this["lure_id"] = LLSDUUID(lureId)
        }

        notificationManager.onEvent("TeleportOfferRequest", body)

        val activeLure = notificationManager.activeLure.value
        assertNotNull(activeLure)
        assertEquals(lureId, activeLure?.lureId)
        assertEquals(senderId, activeLure?.senderId)
        assertEquals("Test Sender", activeLure?.senderName)
        assertEquals("Test Region", activeLure?.regionName)
        assertEquals("Come visit!", activeLure?.message)

        val notifications = notificationManager.getAllNotifications()
        assertEquals(1, notifications.size)
        assertEquals(NotificationType.TELEPORT_OFFER, notifications[0].type)
        assertEquals("Teleport Offer from Test Sender", notifications[0].title)
    }

    @Test
    fun testAcceptLureDelegatesToTeleportManagerAndClearsActiveLure() = runTest {
        val lure = TeleportLure(
            lureId = UUID.randomUUID(),
            senderId = UUID.randomUUID(),
            senderName = "Test Sender",
            regionName = "Test Region",
            message = "Come visit!"
        )

        val body = LLSDMap().apply {
            this["from_id"] = LLSDUUID(lure.senderId)
            this["from_name"] = LLSDString(lure.senderName)
            this["region_name"] = LLSDString(lure.regionName)
            this["message"] = LLSDString(lure.message)
            this["lure_id"] = LLSDUUID(lure.lureId)
        }
        notificationManager.onEvent("TeleportOfferRequest", body)
        assertNotNull(notificationManager.activeLure.value)

        whenever(mockTeleportManager.acceptTeleportLure(any())).thenReturn(TeleportResult.Pending)

        val result = notificationManager.acceptLure(lure)

        assertEquals(TeleportResult.Pending, result)
        assertNull(notificationManager.activeLure.value)
        verify(mockTeleportManager).acceptTeleportLure(lure)
    }

    @Test
    fun testDeclineLureDelegatesToTeleportManagerAndClearsActiveLure() {
        val lure = TeleportLure(
            lureId = UUID.randomUUID(),
            senderId = UUID.randomUUID(),
            senderName = "Test Sender",
            regionName = "Test Region",
            message = "Come visit!"
        )

        val body = LLSDMap().apply {
            this["from_id"] = LLSDUUID(lure.senderId)
            this["from_name"] = LLSDString(lure.senderName)
            this["region_name"] = LLSDString(lure.regionName)
            this["message"] = LLSDString(lure.message)
            this["lure_id"] = LLSDUUID(lure.lureId)
        }
        notificationManager.onEvent("TeleportOfferRequest", body)
        assertNotNull(notificationManager.activeLure.value)

        notificationManager.declineLure(lure)

        assertNull(notificationManager.activeLure.value)
        verify(mockTeleportManager).declineTeleportLure(lure)
    }

    @Test
    fun testDeclineLureWhenTeleportManagerIsNullDoesNotThrow() {
        notificationManager.teleportManager = null
        val lure = TeleportLure(
            lureId = UUID.randomUUID(),
            senderId = UUID.randomUUID(),
            senderName = "Test Sender",
            regionName = "Test Region",
            message = "Come visit!"
        )
        notificationManager.declineLure(lure)
        assertNull(notificationManager.activeLure.value)
    }

    @Test
    fun testClearAllAndShutdownResetsActiveLure() {
        val body = LLSDMap().apply {
            this["from_id"] = LLSDUUID(UUID.randomUUID())
            this["from_name"] = LLSDString("Test Sender")
            this["region_name"] = LLSDString("Test Region")
            this["message"] = LLSDString("Come visit!")
        }
        notificationManager.onEvent("TeleportOfferRequest", body)
        assertNotNull(notificationManager.activeLure.value)

        notificationManager.clearAll()
        assertNull(notificationManager.activeLure.value)

        notificationManager.onEvent("TeleportOfferRequest", body)
        assertNotNull(notificationManager.activeLure.value)

        notificationManager.shutdown()
        assertNull(notificationManager.activeLure.value)
    }

    @Test
    fun testDeclineLureWithRealTeleportManagerTransmitsUdpPacket() {
        val mockUdpConnection = mock<com.linkpoint.protocol.messages.UDPConnectionFixed> {
            on { getSessionId() } doReturn UUID.randomUUID()
        }
        val realTeleportManager = TeleportManager(mockUdpConnection, capabilityManager, UUID.randomUUID())
        notificationManager.teleportManager = realTeleportManager

        val lure = TeleportLure(
            lureId = UUID.randomUUID(),
            senderId = UUID.randomUUID(),
            senderName = "Test Sender",
            regionName = "Test Region",
            message = "Come visit!"
        )

        notificationManager.declineLure(lure)

        verify(mockUdpConnection).sendPacket(
            org.mockito.kotlin.eq(com.linkpoint.protocol.messages.ids.MessageIdRegistry.IMPROVED_INSTANT_MESSAGE),
            org.mockito.kotlin.any(),
            org.mockito.kotlin.eq(true),
            org.mockito.kotlin.eq(false),
            org.mockito.kotlin.anyOrNull()
        )
    }

    @Test
    fun testAcceptLureWithRealTeleportManagerTransmitsUdpPacket() = runTest {
        val mockUdpConnection = mock<com.linkpoint.protocol.messages.UDPConnectionFixed> {
            on { getSessionId() } doReturn UUID.randomUUID()
        }
        val realTeleportManager = TeleportManager(mockUdpConnection, capabilityManager, UUID.randomUUID())
        notificationManager.teleportManager = realTeleportManager

        val lure = TeleportLure(
            lureId = UUID.randomUUID(),
            senderId = UUID.randomUUID(),
            senderName = "Test Sender",
            regionName = "Test Region",
            message = "Come visit!"
        )

        notificationManager.acceptLure(lure)

        verify(mockUdpConnection).sendPacket(
            org.mockito.kotlin.eq(com.linkpoint.protocol.messages.ids.MessageIdRegistry.TELEPORT_LURE_REQUEST),
            org.mockito.kotlin.any(),
            org.mockito.kotlin.eq(true),
            org.mockito.kotlin.eq(false),
            org.mockito.kotlin.anyOrNull()
        )
    }
}
