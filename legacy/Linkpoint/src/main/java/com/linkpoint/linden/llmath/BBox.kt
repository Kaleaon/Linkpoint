package com.linkpoint.linden.llmath

import kotlin.math.*

class BBox {
    private var minLocal: Vector3 = Vector3()
    private var maxLocal: Vector3 = Vector3()
    private var posAgent: Vector3 = Vector3()
    private var rotation: Quaternion = Quaternion()
    private var empty: Boolean = true

    constructor()

    constructor(posAgent: Vector3, rot: Quaternion, minLocal: Vector3, maxLocal: Vector3) {
        this.posAgent = Vector3(posAgent.x, posAgent.y, posAgent.z)
        this.rotation = Quaternion(rot.x, rot.y, rot.z, rot.w)
        this.minLocal = Vector3(minLocal.x, minLocal.y, minLocal.z)
        this.maxLocal = Vector3(maxLocal.x, maxLocal.y, maxLocal.z)
        this.empty = true
    }

    fun getPositionAgent(): Vector3 = posAgent
    fun getRotation(): Quaternion = rotation

    fun getMinLocal(): Vector3 = minLocal
    fun setMinLocal(min: Vector3) { minLocal = min }
    fun getMaxLocal(): Vector3 = maxLocal
    fun setMaxLocal(max: Vector3) { maxLocal = max }

    fun getMinAgent(): Vector3 = localToAgent(minLocal)
    fun getMaxAgent(): Vector3 = localToAgent(maxLocal)

    fun getCenterLocal(): Vector3 = (maxLocal - minLocal) * 0.5f + minLocal
    fun getCenterAgent(): Vector3 = localToAgent(getCenterLocal())
    fun getExtentLocal(): Vector3 = maxLocal - minLocal

    fun isEmpty(): Boolean = empty

    fun containsPointLocal(p: Vector3): Boolean =
        p.x >= minLocal.x && p.x <= maxLocal.x &&
        p.y >= minLocal.y && p.y <= maxLocal.y &&
        p.z >= minLocal.z && p.z <= maxLocal.z

    fun containsPointAgent(p: Vector3): Boolean = containsPointLocal(agentToLocal(p))

    fun addPointLocal(p: Vector3) {
        if (empty) {
            minLocal.set(p); maxLocal.set(p); empty = false
        } else {
            if (p.x < minLocal.x) minLocal.x = p.x
            if (p.y < minLocal.y) minLocal.y = p.y
            if (p.z < minLocal.z) minLocal.z = p.z
            if (p.x > maxLocal.x) maxLocal.x = p.x
            if (p.y > maxLocal.y) maxLocal.y = p.y
            if (p.z > maxLocal.z) maxLocal.z = p.z
        }
    }

    fun addPointAgent(p: Vector3) {
        addPointLocal(agentToLocal(p))
    }

    fun addBBoxLocal(b: BBox) {
        addPointLocal(b.minLocal)
        addPointLocal(b.maxLocal)
    }

    fun addBBoxAgent(b: BBox) {
        if (empty) {
            posAgent.set(b.posAgent)
            rotation = Quaternion(b.rotation.x, b.rotation.y, b.rotation.z, b.rotation.w)
            minLocal.setZero()
            maxLocal.setZero()
        }
        val bMin = b.minLocal; val bMax = b.maxLocal
        val rotMat = Matrix4(b.rotation)
        val invRotMat = Matrix4(rotation.conjugated())
        val translation = b.posAgent - posAgent
        val vertices = arrayOf(
            Vector3(bMin.x, bMin.y, bMin.z), Vector3(bMin.x, bMin.y, bMax.z),
            Vector3(bMin.x, bMax.y, bMin.z), Vector3(bMin.x, bMax.y, bMax.z),
            Vector3(bMax.x, bMin.y, bMin.z), Vector3(bMax.x, bMin.y, bMax.z),
            Vector3(bMax.x, bMax.y, bMin.z), Vector3(bMax.x, bMax.y, bMax.z)
        )
        for (v in vertices) {
            val agentV = rotMat.rotateVector(v) + translation
            val localV = invRotMat.rotateVector(agentV)
            addPointLocal(localV)
        }
    }

    fun expand(delta: Float) {
        minLocal.x -= delta; minLocal.y -= delta; minLocal.z -= delta
        maxLocal.x += delta; maxLocal.y += delta; maxLocal.z += delta
    }

    fun localToAgent(v: Vector3): Vector3 {
        val m = Matrix4(rotation)
        return m.rotateVector(v) + posAgent
    }

    fun agentToLocal(v: Vector3): Vector3 {
        val m = Matrix4(rotation.conjugated())
        return m.rotateVector(v - posAgent)
    }

    fun localToAgentBasis(v: Vector3): Vector3 = Matrix4(rotation).rotateVector(v)
    fun agentToLocalBasis(v: Vector3): Vector3 = Matrix4(rotation.conjugated()).rotateVector(v)

    fun getAxisAligned(): BBox {
        val aligned = BBox(posAgent, Quaternion(), Vector3(), Vector3())
        aligned.addPointAgent(posAgent)
        aligned.addBBoxAgent(this)
        return aligned
    }
}

class BBoxLocal {
    var min: Vector3 = Vector3()
        private set
    var max: Vector3 = Vector3()
        private set

    constructor()
    constructor(min: Vector3, max: Vector3) {
        this.min = Vector3(min.x, min.y, min.z)
        this.max = Vector3(max.x, max.y, max.z)
    }

    fun setMin(v: Vector3) { min = v }
    fun setMax(v: Vector3) { max = v }

    fun getCenter(): Vector3 = (max - min) * 0.5f + min
    fun getExtent(): Vector3 = max - min

    fun addPoint(p: Vector3) {
        if (p.x < min.x) min.x = p.x
        if (p.y < min.y) min.y = p.y
        if (p.z < min.z) min.z = p.z
        if (p.x > max.x) max.x = p.x
        if (p.y > max.y) max.y = p.y
        if (p.z > max.z) max.z = p.z
    }

    fun addBBox(b: BBoxLocal) {
        addPoint(b.min)
        addPoint(b.max)
    }

    fun expand(delta: Float) {
        min.x -= delta; min.y -= delta; min.z -= delta
        max.x += delta; max.y += delta; max.z += delta
    }

    fun transform(m: Matrix4): BBoxLocal = BBoxLocal(m * min, m * max)
}
