package com.linkpoint.linden.llrender

import com.linkpoint.linden.llprimitive.VolumeFace

enum class DrawPoolType {
    SIMPLE,
    ALPHA,
    FULLBRIGHT,
    TERRAIN,
    SKY,
    WATER,
    GLOW,
    BUMP,
    MATERIALS,
    AVATAR,
    CONTROL,
    WL_SKY,
    TREE,
    GRASS,
    INVISIBLE,
    VOIDWATER,
    GROUND,
    PUPPET,
}

data class DrawFaceEntry(
    val face: VolumeFace,
    val materialId: Int,
    val distance: Float,
)

abstract class LLDrawPool(val poolType: DrawPoolType) {

    protected val faces: MutableList<DrawFaceEntry> = mutableListOf()
    var isDirty: Boolean = false
        protected set

    abstract fun prerender()
    abstract fun render(pass: Int = 0)
    abstract fun postrender()

    open fun beginRenderPass(pass: Int) {}
    open fun endRenderPass(pass: Int) {}

    fun addFace(face: VolumeFace, materialId: Int, distance: Float = 0f) {
        faces.add(DrawFaceEntry(face, materialId, distance))
        isDirty = true
    }

    fun removeFace(face: VolumeFace) {
        faces.removeIf { it.face === face }
        isDirty = true
    }

    fun clearFaces() {
        faces.clear()
        isDirty = true
    }

    fun sortByDistance(frontToBack: Boolean = true) {
        if (frontToBack) {
            faces.sortBy { it.distance }
        } else {
            faces.sortByDescending { it.distance }
        }
    }

    fun sortByMaterial() {
        faces.sortBy { it.materialId }
    }

    fun getFaceCount(): Int = faces.size

    fun getFaceList(): List<DrawFaceEntry> = faces.toList()
}

class LLSimpleDrawPool : LLDrawPool(DrawPoolType.SIMPLE) {
    override fun prerender() { isDirty = false }
    override fun render(pass: Int) { sortByMaterial() }
    override fun postrender() {}
}

class LLAlphaDrawPool : LLDrawPool(DrawPoolType.ALPHA) {
    override fun prerender() {
        isDirty = false
        LLGLState.enable(GLCapability.BLEND)
    }
    override fun render(pass: Int) { sortByDistance(frontToBack = false) }
    override fun postrender() {
        LLGLState.disable(GLCapability.BLEND)
    }
}

object LLDrawPoolMgr {

    private val pools: MutableMap<DrawPoolType, LLDrawPool> = mutableMapOf()

    init {
        pools[DrawPoolType.SIMPLE] = LLSimpleDrawPool()
        pools[DrawPoolType.ALPHA] = LLAlphaDrawPool()
    }

    fun getPool(type: DrawPoolType): LLDrawPool? = pools[type]

    fun addPool(pool: LLDrawPool) { pools[pool.poolType] = pool }

    fun renderAll() {
        pools.values.forEach { pool ->
            pool.prerender()
            pool.render()
            pool.postrender()
        }
    }

    fun clearAll() {
        pools.values.forEach { it.clearFaces() }
    }

    fun getPoolCount(): Int = pools.size
}
