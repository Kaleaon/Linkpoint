package com.linkpoint.render.glres

/**
 * Modern Kotlin GLCleanable interface
 * Interface for OpenGL resources that need cleanup
 */
interface GLCleanable {
    /**
     * Cleans up OpenGL resources
     */
    fun GLCleanup()
}
