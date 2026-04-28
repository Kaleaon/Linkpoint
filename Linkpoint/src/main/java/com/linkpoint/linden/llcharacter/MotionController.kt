package com.linkpoint.linden.llcharacter

import com.linkpoint.linden.llcommon.LLUUID

class MotionController {

    enum class UpdateType { NORMAL, HIDDEN, FORCE }

    private val allMotions: MutableMap<LLUUID, Motion> = mutableMapOf()
    private val loadingMotions: MutableSet<Motion> = mutableSetOf()
    private val loadedMotions: MutableSet<Motion> = mutableSetOf()
    private val activeMotions: ArrayDeque<Motion> = ArrayDeque()
    private val deprecatedMotions: MutableSet<Motion> = mutableSetOf()

    var isSelf: Boolean = false
    var character: Character? = null

    var timeFactor: Float = 1f
    var timeStep: Float = 0f
    var animTime: Float = 0f

    private var prevTimerElapsed: Float = 0f
    private var lastTime: Float = 0f
    private var hasRunOnce: Boolean = false
    private var paused: Boolean = false
    private var pausedFrame: Int = 0
    private var timeStepCount: Int = 0
    private var lastInterp: Float = 0f

    val isPaused: Boolean get() = paused
    val pausedFrameNum: Int get() = pausedFrame

    private val motionRegistry: MutableMap<LLUUID, (LLUUID) -> Motion> = mutableMapOf()

    fun registerMotion(id: LLUUID, factory: (LLUUID) -> Motion): Boolean {
        motionRegistry[id] = factory
        return true
    }

    fun createMotion(id: LLUUID): Motion? {
        val existing = allMotions[id]
        if (existing != null) return existing
        val factory = motionRegistry[id] ?: return null
        val motion = factory(id)
        allMotions[id] = motion
        return motion
    }

    fun removeMotion(id: LLUUID) {
        val motion = allMotions.remove(id) ?: return
        activeMotions.remove(motion)
        loadingMotions.remove(motion)
        loadedMotions.remove(motion)
        deprecatedMotions.remove(motion)
    }

    fun addMotion(motion: Motion) {
        allMotions[motion.id] = motion
        loadedMotions.add(motion)
    }

    fun startMotion(id: LLUUID, startOffset: Float = 0f): Boolean {
        val motion = createMotion(id) ?: return false
        return activateMotionInstance(motion, animTime + startOffset)
    }

    fun stopMotion(id: LLUUID, stopImmediate: Boolean = false): Boolean {
        val motion = allMotions[id] ?: return false
        return stopMotionInstance(motion, stopImmediate)
    }

    fun updateMotions(dt: Float, forceUpdate: Boolean = false) {
        if (paused && !forceUpdate) return

        val scaledDt = dt * timeFactor
        animTime += scaledDt

        updateLoadingMotions()
        updateActiveMotions(scaledDt)
        deactivateStoppedMotions()
        purgeExcessMotions()
    }

    fun updateMotionsMinimal() {
        updateLoadingMotions()
    }

    private fun updateLoadingMotions() {
        val iter = loadingMotions.iterator()
        while (iter.hasNext()) {
            val motion = iter.next()
            if (motion.isLoaded()) {
                iter.remove()
                loadedMotions.add(motion)
            }
        }
    }

    private fun updateActiveMotions(dt: Float) {
        val iter = activeMotions.iterator()
        while (iter.hasNext()) {
            val motion = iter.next()
            if (!motion.onUpdate(animTime, ByteArray(0))) {
                motion.deactivate()
            }
        }
    }

    private fun activateMotionInstance(motion: Motion, time: Float): Boolean {
        if (motion in activeMotions) return true
        if (!motion.onActivate()) return false
        motion.activate(time)
        activeMotions.add(motion)
        return true
    }

    private fun stopMotionInstance(motion: Motion, stopImmediate: Boolean): Boolean {
        if (stopImmediate) {
            motion.setStopped(true)
            deactivateMotionInstance(motion)
        } else {
            motion.setStopped(true)
        }
        return true
    }

    private fun deactivateMotionInstance(motion: Motion): Boolean {
        motion.onDeactivate()
        motion.deactivate()
        activeMotions.remove(motion)
        return true
    }

    private fun deactivateStoppedMotions() {
        val iter = activeMotions.iterator()
        while (iter.hasNext()) {
            val motion = iter.next()
            if (motion.isStopped() && motion.getEaseOutDuration() <= 0f) {
                motion.onDeactivate()
                motion.deactivate()
                iter.remove()
            }
        }
    }

    private fun purgeExcessMotions() {
        val toRemove = allMotions.values.filter { motion ->
            !motion.isActive() &&
            motion !in loadingMotions &&
            motion !in loadedMotions &&
            motion !in deprecatedMotions
        }
        toRemove.forEach { allMotions.remove(it.id) }
    }

    fun deactivateAllMotions() {
        activeMotions.toList().forEach { deactivateMotionInstance(it) }
        activeMotions.clear()
    }

    fun flushAllMotions() {
        deactivateAllMotions()
        allMotions.clear()
        loadingMotions.clear()
        loadedMotions.clear()
        deprecatedMotions.clear()
    }

    fun pauseAllMotions() {
        paused = true
    }

    fun unpauseAllMotions() {
        paused = false
    }

    fun findMotion(id: LLUUID): Motion? = allMotions[id]

    fun isMotionActive(motion: Motion): Boolean = motion in activeMotions

    fun isMotionLoading(motion: Motion): Boolean = motion in loadingMotions

    fun getActiveMotions(): List<Motion> = activeMotions.toList()

    fun getMotionCounts(): MotionCounts = MotionCounts(
        numMotions = allMotions.size,
        numLoading = loadingMotions.size,
        numLoaded = loadedMotions.size,
        numActive = activeMotions.size,
        numDeprecated = deprecatedMotions.size
    )

    data class MotionCounts(
        val numMotions: Int,
        val numLoading: Int,
        val numLoaded: Int,
        val numActive: Int,
        val numDeprecated: Int
    )

    companion object {
        var currentTimeFactor: Float = 1f
    }
}

abstract class Motion(val id: LLUUID) {
    var name: String = ""
    @set:JvmName("setStoppedProp")
    protected var stopped: Boolean = false
    protected var active: Boolean = false
    var activationTimestamp: Float = 0f
    var stopTimestamp: Float = 0f
    var residualWeight: Float = 0f
    var fadeWeight: Float = 1f

    abstract fun getLoop(): Boolean
    abstract fun getDuration(): Float
    abstract fun getEaseInDuration(): Float
    abstract fun getEaseOutDuration(): Float
    abstract fun getPriority(): JointPriority
    abstract fun getBlendType(): MotionController.UpdateType
    abstract fun getMinPixelArea(): Float
    abstract fun onInitialize(character: Character): MotionController.UpdateType
    abstract fun onUpdate(time: Float, jointMask: ByteArray): Boolean
    abstract fun onDeactivate()
    abstract fun onActivate(): Boolean

    fun isStopped(): Boolean = stopped
    fun isActive(): Boolean = active
    fun isLoaded(): Boolean = true

    fun setStopped(s: Boolean) { stopped = s }

    fun activate(time: Float) {
        activationTimestamp = time
        active = true
    }

    fun deactivate() {
        active = false
    }

    fun setStopTime(time: Float) {
        stopTimestamp = time
        stopped = true
    }
}
