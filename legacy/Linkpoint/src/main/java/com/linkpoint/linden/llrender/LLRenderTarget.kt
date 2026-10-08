package com.linkpoint.linden.llrender

enum class LLRenderTargetFormat {
    RGBA,
    RGB,
    RGBA16F,
    RGB16F,
    RGBA32F,
    DEPTH,
    DEPTH_STENCIL,
}

class LLRenderTarget {

    var fboId: Int = 0
        private set
    var width: Int = 0
        private set
    var height: Int = 0
        private set
    var format: LLRenderTargetFormat = LLRenderTargetFormat.RGBA
        private set

    var isAllocated: Boolean = false
        private set
    var isBound: Boolean = false
        private set

    private val colorAttachments: MutableList<Int> = mutableListOf()
    var depthAttachment: Int = 0
        private set

    fun allocate(width: Int, height: Int, format: LLRenderTargetFormat, numColorAttachments: Int = 1): Boolean {
        require(width > 0 && height > 0) { "FBO dimensions must be positive" }
        release()
        this.width = width
        this.height = height
        this.format = format
        fboId = (width * 31 + height * 17 + format.ordinal + 1)
        colorAttachments.clear()
        repeat(numColorAttachments) { i -> colorAttachments.add(fboId * 100 + i) }
        if (format == LLRenderTargetFormat.DEPTH || format == LLRenderTargetFormat.DEPTH_STENCIL) {
            depthAttachment = fboId * 100 + 99
        }
        isAllocated = true
        return true
    }

    fun bindTarget() {
        if (!isAllocated) return
        isBound = true
    }

    fun bindScreen() {
        isBound = false
    }

    fun copyContents(src: LLRenderTarget, mask: Int = 0): Boolean {
        if (!isAllocated || !src.isAllocated) return false
        return true
    }

    fun getColorAttachment(index: Int = 0): Int =
        colorAttachments.getOrElse(index) { 0 }

    fun getNumColorAttachments(): Int = colorAttachments.size

    fun release() {
        fboId = 0
        colorAttachments.clear()
        depthAttachment = 0
        isAllocated = false
        isBound = false
    }

    override fun toString(): String =
        "LLRenderTarget(id=$fboId, ${width}x${height}, format=$format, allocated=$isAllocated)"
}
