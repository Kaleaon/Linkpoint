package com.linkpoint.linden.llaudio

import com.linkpoint.linden.llmath.Vector3

class LLListener {
    @set:JvmName("setPositionProp")
    var position: Vector3 = Vector3.ZERO
    @set:JvmName("setVelocityProp")
    var velocity: Vector3 = Vector3.ZERO
    var atDir: Vector3 = Vector3(0f, 1f, 0f)
    var upDir: Vector3 = Vector3(0f, 0f, 1f)

    private var pendingPosition: Vector3? = null
    private var pendingVelocity: Vector3? = null
    private var pendingAt: Vector3? = null
    private var pendingUp: Vector3? = null

    fun setPosition(pos: Vector3) { pendingPosition = pos }
    fun setVelocity(vel: Vector3) { pendingVelocity = vel }
    fun setOrientation(at: Vector3, up: Vector3) { pendingAt = at; pendingUp = up }

    fun commitDeferredChanges() {
        pendingPosition?.let { position = it; pendingPosition = null }
        pendingVelocity?.let { velocity = it; pendingVelocity = null }
        pendingAt?.let { it.normalize(); atDir = it; pendingAt = null }
        pendingUp?.let { it.normalize(); upDir = it; pendingUp = null }
    }

}
