package com.linkpoint.linden.llrender

enum class GLCapability(val glConst: UInt) {
    BLEND(RenderConstants.GL_BLEND),
    DEPTH_TEST(RenderConstants.GL_DEPTH_TEST),
    STENCIL_TEST(RenderConstants.GL_STENCIL_TEST),
    CULL_FACE(RenderConstants.GL_CULL_FACE),
    SCISSOR_TEST(RenderConstants.GL_SCISSOR_TEST),
    ALPHA_TEST(RenderConstants.GL_ALPHA_TEST),
}

object LLGLState {

    private val enabled: MutableMap<GLCapability, Boolean> = mutableMapOf()
    private val dirty: MutableSet<GLCapability> = mutableSetOf()

    fun enable(cap: GLCapability) {
        if (enabled[cap] != true) {
            enabled[cap] = true
            dirty.add(cap)
        }
    }

    fun disable(cap: GLCapability) {
        if (enabled[cap] != false) {
            enabled[cap] = false
            dirty.add(cap)
        }
    }

    fun isEnabled(cap: GLCapability): Boolean = enabled[cap] ?: false

    fun isDirty(cap: GLCapability): Boolean = cap in dirty

    fun clearDirty() = dirty.clear()

    fun clearDirty(cap: GLCapability) { dirty.remove(cap) }

    fun reset() {
        enabled.clear()
        dirty.clear()
    }

    fun getEnabledSet(): Set<GLCapability> = enabled.filterValues { it }.keys.toSet()
}
