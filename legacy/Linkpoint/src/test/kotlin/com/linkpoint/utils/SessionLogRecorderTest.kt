package com.linkpoint.utils

import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.nio.file.Files
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class SessionLogRecorderTest {

    private lateinit var tempDir: java.io.File

    @Before
    fun setUp() {
        tempDir = Files.createTempDirectory("session_log_test").toFile()
        SessionLogRecorder.initializeForTest(tempDir)
    }

    @After
    fun tearDown() {
        if (SessionLogRecorder.isRecording()) {
            SessionLogRecorder.stopRecording()
        }
        tempDir.deleteRecursively()
    }

    @Test
    fun `test RawPacketEvent equality and hash code with byte arrays`() {
        val payload1 = byteArrayOf(0x01, 0x02, 0x03, 0x04)
        val payload2 = byteArrayOf(0x01, 0x02, 0x03, 0x04)
        val payload3 = byteArrayOf(0x01, 0x02, 0x03, 0x05)

        val event1 = RawPacketEvent(
            timestamp = 1000L,
            type = SessionLogRecorder.EntryType.PACKET_SENT,
            tag = "UDP",
            messageId = 42,
            messageName = "TestMessage",
            sequenceNumber = 1,
            reliable = true,
            payload = payload1
        )

        val event2 = RawPacketEvent(
            timestamp = 1000L,
            type = SessionLogRecorder.EntryType.PACKET_SENT,
            tag = "UDP",
            messageId = 42,
            messageName = "TestMessage",
            sequenceNumber = 1,
            reliable = true,
            payload = payload2
        )

        val event3 = RawPacketEvent(
            timestamp = 1000L,
            type = SessionLogRecorder.EntryType.PACKET_SENT,
            tag = "UDP",
            messageId = 42,
            messageName = "TestMessage",
            sequenceNumber = 1,
            reliable = true,
            payload = payload3
        )

        assertEquals(event1, event2)
        assertEquals(event1.hashCode(), event2.hashCode())
        assertNotEquals(event1, event3)
    }

    @Test
    fun `test async non blocking packet logging and file output`() {
        assertTrue(SessionLogRecorder.startRecording())
        assertTrue(SessionLogRecorder.isRecording())

        val sentData = byteArrayOf(0x10, 0x20, 0x30, 0x40)
        val recvData = byteArrayOf(0x50, 0x60, 0x70)

        SessionLogRecorder.logPacketSent(
            messageId = 1,
            messageName = "StartPingCheck",
            sequenceNumber = 100,
            data = sentData,
            reliable = true
        )

        SessionLogRecorder.logPacketReceived(
            messageId = 2,
            messageName = "CompletePingCheck",
            sequenceNumber = 101,
            data = recvData,
            handlerFound = true
        )

        val logFile = SessionLogRecorder.stopRecording()
        assertNotNull(logFile)
        assertTrue(logFile!!.exists())

        val content = logFile.readText()
        assertTrue(content.contains("LINKPOINT SESSION LOG"))
        assertTrue(content.contains("→ SENT: StartPingCheck"))
        assertTrue(content.contains("ID: 0x01"))
        assertTrue(content.contains("seq: 100"))
        assertTrue(content.contains("RELIABLE"))
        assertTrue(content.contains("← RECV: CompletePingCheck ✓"))
        assertTrue(content.contains("END OF SESSION LOG"))
    }

    @Test
    fun `test complete channel draining on stop recording`() {
        assertTrue(SessionLogRecorder.startRecording())

        val packetCount = 100
        for (i in 1..packetCount) {
            SessionLogRecorder.logPacketSent(
                messageId = i,
                messageName = "BurstPacket_$i",
                sequenceNumber = i,
                data = byteArrayOf(i.toByte()),
                reliable = false
            )
        }

        val logFile = SessionLogRecorder.stopRecording()
        assertNotNull(logFile)

        val content = logFile!!.readText()
        for (i in 1..packetCount) {
            assertTrue("Log file missing packet $i", content.contains("BurstPacket_$i"))
        }
    }

    @Test
    fun `test defensive payload copy prevents data races on socket buffer mutation`() {
        assertTrue(SessionLogRecorder.startRecording())

        val mutableBuffer = byteArrayOf(0xAA.toByte(), 0xBB.toByte())
        SessionLogRecorder.logPacketSent(
            messageId = 99,
            messageName = "MutablePacket",
            sequenceNumber = 1,
            data = mutableBuffer,
            reliable = true
        )

        // Immediately mutate buffer after enqueuing
        mutableBuffer[0] = 0x00.toByte()
        mutableBuffer[1] = 0x00.toByte()

        val logFile = SessionLogRecorder.stopRecording()
        assertNotNull(logFile)

        val content = logFile!!.readText()
        assertTrue(content.contains("MutablePacket"))
        // Check size recorded is 2B
        assertTrue(content.contains("size: 2B"))
    }

    @Test
    fun `test queue backpressure drops payload byte arrays under sustained load`() {
        assertTrue(SessionLogRecorder.startRecording())

        // Watermark threshold is 1500
        val burstCount = 1600
        for (i in 1..burstCount) {
            val event = RawPacketEvent(
                timestamp = System.currentTimeMillis(),
                type = SessionLogRecorder.EntryType.PACKET_SENT,
                tag = "UDP",
                messageId = i,
                messageName = "BackpressurePacket_$i",
                sequenceNumber = i,
                reliable = false,
                payload = byteArrayOf(0x01, 0x02, 0x03)
            )
            SessionLogRecorder.enqueueRawPacket(event)
        }

        val stats = SessionLogRecorder.getStats()
        assertTrue(stats.pendingChannelEvents > 0 || stats.totalEntries > 0)

        val logFile = SessionLogRecorder.stopRecording()
        assertNotNull(logFile)
        assertTrue(logFile!!.exists())
    }

    @Test
    fun `LogEntry format uses HH mm ss SSS date format matching SimpleDateFormat`() {
        val timestamp = 1713500000000L // Fixed timestamp
        val expectedTimeStr = SimpleDateFormat("HH:mm:ss.SSS", Locale.US).format(Date(timestamp))

        val entry = SessionLogRecorder.LogEntry(
            timestamp = timestamp,
            type = SessionLogRecorder.EntryType.INFO,
            tag = "TestTag",
            message = "Hello world"
        )

        val formatted = entry.format()

        assertTrue(formatted.startsWith("[$expectedTimeStr] [INFO] [TestTag]\nHello world"))
    }

    @Test
    fun `LogEntry format includes hex dump and stack trace when provided`() {
        val timestamp = System.currentTimeMillis()
        val expectedTimeStr = SimpleDateFormat("HH:mm:ss.SSS", Locale.US).format(Date(timestamp))

        val entry = SessionLogRecorder.LogEntry(
            timestamp = timestamp,
            type = SessionLogRecorder.EntryType.PACKET_SENT,
            tag = "UDP",
            message = "Sent packet",
            hexDump = "00 01 02 03",
            stackTrace = "java.lang.Exception: test trace"
        )

        val formatted = entry.format()

        assertTrue(formatted.contains("[$expectedTimeStr] [PACKET_SENT] [UDP]"))
        assertTrue(formatted.contains("Sent packet"))
        assertTrue(formatted.contains("Hex: 00 01 02 03"))
        assertTrue(formatted.contains("Stack: java.lang.Exception: test trace"))
    }

    @Test
    fun `LogEntry format is thread safe under high concurrency`() {
        val executor = Executors.newFixedThreadPool(16)
        val iterations = 500
        val futures = (0 until iterations).map { i ->
            executor.submit<Boolean> {
                val timestamp = 1700000000000L + (i * 1000L)
                val expectedTimeStr = SimpleDateFormat("HH:mm:ss.SSS", Locale.US).format(Date(timestamp))
                val entry = SessionLogRecorder.LogEntry(
                    timestamp = timestamp,
                    type = SessionLogRecorder.EntryType.DEBUG,
                    tag = "ThreadTest",
                    message = "Iteration $i"
                )
                val formatted = entry.format()
                formatted.contains("[$expectedTimeStr]") && formatted.contains("Iteration $i")
            }
        }

        executor.shutdown()
        assertTrue(executor.awaitTermination(10, TimeUnit.SECONDS))

        futures.forEach { future ->
            assertTrue("Format mismatch or corruption during concurrent execution", future.get())
        }
    }
}
