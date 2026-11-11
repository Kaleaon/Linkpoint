package com.linkpoint.linden.llcharacter

import com.linkpoint.linden.llmath.Matrix4
import com.linkpoint.linden.llmath.Quaternion
import com.linkpoint.linden.llmath.Vector3

open class LLJoint(val name: String, parent: LLJoint? = null) {
    var parent: LLJoint? = parent
        private set

    private val _children: MutableList<LLJoint> = mutableListOf()
    val children: List<LLJoint> get() = _children

    @set:JvmName("setLocalPositionProp")
    var localPosition: Vector3 = Vector3.ZERO
    @set:JvmName("setLocalRotationProp")
    var localRotation: Quaternion = Quaternion.DEFAULT
    @set:JvmName("setLocalScaleProp")
    var localScale: Vector3 = Vector3(1f, 1f, 1f)

    private var worldTransformDirty: Boolean = true
    private var _worldPosition: Vector3 = Vector3.ZERO
    private var _worldRotation: Quaternion = Quaternion.DEFAULT

    init { parent?.addChild(this) }

    fun addChild(child: LLJoint) {
        if (child !in _children) {
            child.parent?.removeChild(child)
            child.parent = this
            _children.add(child)
        }
    }

    fun removeChild(child: LLJoint) {
        if (_children.remove(child)) child.parent = null
    }

    fun setLocalPosition(pos: Vector3) { localPosition = pos; markDirty() }
    fun setLocalRotation(rot: Quaternion) { localRotation = rot; markDirty() }
    fun setLocalScale(scale: Vector3) { localScale = scale; markDirty() }

    private fun markDirty() {
        worldTransformDirty = true
        _children.forEach { it.markDirty() }
    }

    fun getWorldPosition(): Vector3 {
        if (worldTransformDirty) updateWorldTransform()
        return _worldPosition
    }

    fun getWorldRotation(): Quaternion {
        if (worldTransformDirty) updateWorldTransform()
        return _worldRotation
    }

    fun updateWorldTransform() {
        val p = parent
        if (p == null) {
            _worldPosition = localPosition
            _worldRotation = localRotation
        } else {
            _worldRotation = p.getWorldRotation() * localRotation
            _worldPosition = p.getWorldPosition() + p.getWorldRotation() * localPosition
        }
        worldTransformDirty = false
    }

    fun findChild(childName: String): LLJoint? {
        if (name == childName) return this
        return _children.firstNotNullOfOrNull { it.findChild(childName) }
    }

    fun getAllJoints(): List<LLJoint> {
        val result = mutableListOf<LLJoint>(this)
        _children.forEach { result.addAll(it.getAllJoints()) }
        return result
    }
}
