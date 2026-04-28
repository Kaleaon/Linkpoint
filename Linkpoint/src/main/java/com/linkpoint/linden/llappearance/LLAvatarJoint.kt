package com.linkpoint.linden.llappearance

import com.linkpoint.linden.llcharacter.LLJoint

enum class AvatarJointLOD { HIGHEST, HIGH, MEDIUM, LOW, LOWEST }

class LLAvatarJoint(
    name: String,
    parent: LLJoint? = null,
) : LLJoint(name, parent) {

    var lod: AvatarJointLOD = AvatarJointLOD.HIGH
    var minPixelArea: Float = 0f
    var isVisible: Boolean = true

    private val meshes: MutableList<LLPolyMesh> = mutableListOf()

    fun addMesh(mesh: LLPolyMesh) {
        meshes.add(mesh)
    }

    fun getMeshes(): List<LLPolyMesh> = meshes.toList()

    fun getMesh(name: String): LLPolyMesh? = meshes.firstOrNull { it.meshName == name }

    fun setLOD(pixelArea: Float) {
        lod = when {
            pixelArea >= 10000f -> AvatarJointLOD.HIGHEST
            pixelArea >= 2000f  -> AvatarJointLOD.HIGH
            pixelArea >= 500f   -> AvatarJointLOD.MEDIUM
            pixelArea >= 100f   -> AvatarJointLOD.LOW
            else                -> AvatarJointLOD.LOWEST
        }
    }

    fun render(pixelArea: Float) {
        setLOD(pixelArea)
        if (!isVisible) return
        updateWorldTransform()
        // Actual GL rendering is a no-op in JVM context
    }

    override fun toString(): String =
        "LLAvatarJoint($name, lod=$lod, meshes=${meshes.size}, visible=$isVisible)"
}
