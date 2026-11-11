package com.linkpoint.inventory

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.util.UUID
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicLong

class GestureTriggerIntegrationTest {

    private lateinit var engine: GestureTriggerEngine
    private lateinit var adapter: InventoryTreeAdapter

    @Before
    fun setUp() {
        engine = GestureTriggerEngine()
        adapter = InventoryTreeAdapter(null, engine)
    }

    @Test
    fun `test shortcut registration API exposes active gesture binding`() {
        val gestureId = UUID.randomUUID().toString()
        val registered = engine.registerShortcut(0, gestureId, "Wave Gesture", "/wave")

        assertTrue("Shortcut registration should succeed", registered)
        assertEquals(1, engine.shortcutCount)
        assertTrue("Engine should contain shortcut slot 0", engine.hasShortcut(0))

        val shortcut = engine.getShortcut(0)
        assertNotNull(shortcut)
        assertEquals(0, shortcut.slot)
        assertEquals(gestureId, shortcut.gestureId)
        assertEquals("Wave Gesture", shortcut.name)
        assertEquals("/wave", shortcut.triggerCommand)
    }

    @Test
    fun `test quick action tap triggers gesture within 1 second`() {
        val gestureId = UUID.randomUUID().toString()
        adapter.addFavoriteGestureShortcut(0, gestureId, "Dance")

        val triggered = AtomicBoolean(false)
        val executionDurationMs = AtomicLong(0L)

        engine.setOnGestureTriggeredListener { slot, id, timeMs ->
            if (slot == 0 && id == gestureId) {
                triggered.set(true)
                executionDurationMs.set(timeMs)
            }
        }

        val startTime = System.currentTimeMillis()
        adapter.handleShortcutTap(0)
        val elapsed = System.currentTimeMillis() - startTime

        assertTrue("Shortcut should be triggered successfully", triggered.get())
        assertTrue("Execution speed should be under 1000 ms (was $elapsed ms)", elapsed < 1000)
    }

    @Test
    fun `test collapsible HUD quick action bar toggle`() {
        assertFalse("HUD bar should start expanded by default", adapter.isBarCollapsed)

        adapter.toggleBarCollapsed()
        assertTrue("HUD bar should be collapsed after toggle", adapter.isBarCollapsed)

        adapter.setBarCollapsed(false)
        assertFalse("HUD bar should be expanded when set to false", adapter.isBarCollapsed)
    }

    @Test
    fun `test HUD single touch responsiveness under active render cycles`() {
        val gestureId = UUID.randomUUID().toString()
        adapter.addFavoriteGestureShortcut(1, gestureId, "Clap")

        val latch = CountDownLatch(1)
        val triggered = AtomicBoolean(false)

        adapter.setOnShortcutClickListener { slot, id ->
            if (slot == 1 && id == gestureId) {
                triggered.set(true)
                latch.countDown()
            }
        }

        // Simulate active render cycle on background thread
        val renderCycleRunning = AtomicBoolean(true)
        val renderThread = Thread {
            var frame = 0
            while (renderCycleRunning.get()) {
                frame++
                Thread.sleep(16) // ~60 FPS render cycle
            }
        }
        renderThread.start()

        try {
            val tapStartTime = System.nanoTime()
            adapter.handleShortcutTap(1)
            val completedInTime = latch.await(1, TimeUnit.SECONDS)
            val tapLatencyMs = (System.nanoTime() - tapStartTime) / 1_000_000

            assertTrue("Touch tap should be handled within 1s under active render loop", completedInTime)
            assertTrue("Single-touch HUD tap should respond under 1000 ms (was ${tapLatencyMs}ms)", tapLatencyMs < 1000)
            assertTrue("Registered listener should receive shortcut tap", triggered.get())
        } finally {
            renderCycleRunning.set(false)
            renderThread.join(1000)
        }
    }

    @Test
    fun `test missing asset cache during region teleport renders empty slots without blocking UI`() {
        val gestureId = UUID.randomUUID().toString()
        adapter.addFavoriteGestureShortcut(2, gestureId, "Laugh")

        // Configure cache provider that simulates uncached assets
        adapter.setAssetCacheProvider { false }

        // Start region teleport
        adapter.onRegionTeleportStarted()

        val shortcuts = adapter.favoriteGestureShortcuts
        assertNotNull(shortcuts)
        assertEquals(8, shortcuts.size)

        // Slot 2 should show uncached/empty ready state during teleport without throwing or blocking
        val slot2 = shortcuts.first { it.slot == 2 }
        assertFalse("Slot 2 should report cache not ready during region teleport", slot2.isCacheReady)

        // Tapping slot with missing cache should safely handle empty/uncached without crashing
        adapter.handleShortcutTap(2)

        adapter.onRegionTeleportCompleted()
    }

    @Test
    fun `test blast radius - existing deep inventory folder navigation remains operational`() {
        val folder1 = adapter.addNode("folder1", "root", "Gestures Folder", true, null, 1)
        val folder2 = adapter.addNode("folder2", "folder1", "Dances Subfolder", true, null, 1)
        val item1 = adapter.addNode("item1", "folder2", "Salsa Dance", false, UUID.randomUUID().toString(), 20)

        assertNotNull(folder1)
        assertNotNull(folder2)
        assertNotNull(item1)

        // Default: root is expanded, child folders not expanded yet
        assertEquals("Root children visible count check", 1, adapter.itemCount)

        // Expand deep folder hierarchy
        adapter.expandFolder("folder1")
        assertEquals(2, adapter.itemCount)

        adapter.expandFolder("folder2")
        assertEquals(3, adapter.itemCount)

        assertEquals("Salsa Dance", adapter.getItemAt(2).name)

        // Collapse folder
        adapter.collapseFolder("folder1")
        assertEquals(1, adapter.itemCount)
    }

    @Test
    fun `test slash command trigger processing`() {
        val gestureId = UUID.randomUUID().toString()
        engine.registerShortcut(3, gestureId, "Bow Gesture", "/bow")

        val triggeredId = AtomicBoolean(false)
        engine.setOnGestureTriggeredListener { _, id, _ ->
            if (id == gestureId) {
                triggeredId.set(true)
            }
        }

        val processed = engine.processSlashCommand("/bow")
        assertTrue("Slash command /bow should be processed successfully", processed)
        assertTrue("Trigger listener should be called for slash command", triggeredId.get())
    }
}
