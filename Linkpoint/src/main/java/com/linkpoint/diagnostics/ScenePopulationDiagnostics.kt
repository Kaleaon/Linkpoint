package com.linkpoint.diagnostics

import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicLong

object ScenePopulationDiagnostics {
    enum class EntityType { OBJECT, AVATAR }

    data class StageCounters(
        val packetReceived: Long,
        val parsed: Long,
        val managerApplied: Long,
        val sceneInserted: Long,
        val rendererSubmitted: Long,
        val dropped: Long
    )

    data class Snapshot(
        val handshakeComplete: Boolean,
        val objectCounters: StageCounters,
        val avatarCounters: StageCounters
    )

    data class SmokeCheckResult(val passed: Boolean, val reasons: List<String>)

    private class CounterSet {
        val packetReceived = AtomicLong(0)
        val parsed = AtomicLong(0)
        val managerApplied = AtomicLong(0)
        val sceneInserted = AtomicLong(0)
        val rendererSubmitted = AtomicLong(0)
        val dropped = AtomicLong(0)

        fun snapshot() = StageCounters(
            packetReceived = packetReceived.get(),
            parsed = parsed.get(),
            managerApplied = managerApplied.get(),
            sceneInserted = sceneInserted.get(),
            rendererSubmitted = rendererSubmitted.get(),
            dropped = dropped.get()
        )
    }

    private val handshakeComplete = AtomicBoolean(false)
    private val objectCounters = CounterSet()
    private val avatarCounters = CounterSet()

    fun markRegionHandshakeComplete() { handshakeComplete.set(true) }
    fun markPacketReceived(type: EntityType, count: Int = 1) { counter(type).packetReceived.addAndGet(count.toLong()) }
    fun markParsed(type: EntityType, count: Int = 1) { counter(type).parsed.addAndGet(count.toLong()) }

    fun markManagerApplied(type: EntityType, success: Boolean) {
        if (success) counter(type).managerApplied.incrementAndGet() else counter(type).dropped.incrementAndGet()
    }

    fun markSceneInserted(type: EntityType, success: Boolean) {
        if (success) counter(type).sceneInserted.incrementAndGet() else counter(type).dropped.incrementAndGet()
    }

    fun markRendererSubmitted(type: EntityType, success: Boolean) {
        if (success) counter(type).rendererSubmitted.incrementAndGet() else counter(type).dropped.incrementAndGet()
    }

    fun snapshot() = Snapshot(
        handshakeComplete = handshakeComplete.get(),
        objectCounters = objectCounters.snapshot(),
        avatarCounters = avatarCounters.snapshot()
    )

    fun runSmokeCheck(totalObjectsInScene: Int, totalAvatarsInScene: Int): SmokeCheckResult {
        val reasons = mutableListOf<String>()
        val snap = snapshot()

        if (!snap.handshakeComplete) reasons += "Region handshake has not completed"
        if (snap.objectCounters.packetReceived > 0 && totalObjectsInScene <= 0) reasons += "Object packets received but scene object count is zero"
        if (snap.avatarCounters.packetReceived > 0 && totalAvatarsInScene <= 0) reasons += "Avatar packets received but scene avatar count is zero"

        return SmokeCheckResult(reasons.isEmpty(), reasons)
    }

    fun reset() {
        handshakeComplete.set(false)
        resetCounter(objectCounters)
        resetCounter(avatarCounters)
    }

    private fun counter(type: EntityType) = when (type) {
        EntityType.OBJECT -> objectCounters
        EntityType.AVATAR -> avatarCounters
    }

    private fun resetCounter(counter: CounterSet) {
        counter.packetReceived.set(0)
        counter.parsed.set(0)
        counter.managerApplied.set(0)
        counter.sceneInserted.set(0)
        counter.rendererSubmitted.set(0)
        counter.dropped.set(0)
    }
}
