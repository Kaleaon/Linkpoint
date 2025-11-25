package com.lumiyaviewer.lumiya.render

class TextureMemoryTracker {
    companion object {
        fun canAllocateMemory(size: Int): Boolean = true
        fun stall() {}
        fun setMemoryLimit(bytes: Int) {}
        fun allocBufferMemory(size: Int) {}
        fun releaseBufferMemory(size: Int) {}
    }
}
