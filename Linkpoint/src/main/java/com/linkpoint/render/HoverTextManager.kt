package com.linkpoint.render

import android.graphics.Color
import android.util.Log
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

/**
 * Hover Text Manager - Manages floating text above objects.
 * 
 * Based on Lumiya's HoverText.java and DrawableHoverText.java
 * 
 * Hover text is set by scripts using llSetText() and appears
 * floating above objects.
 */
class HoverTextManager {
    
    companion object {
        private const val TAG = "HoverTextManager"
        
        // Maximum text lines
        const val MAX_TEXT_LINES = 10
        
        // Maximum characters per line
        const val MAX_LINE_LENGTH = 255
        
        // Default hover height (above object center)
        const val DEFAULT_HOVER_HEIGHT = 0.5f
    }
    
    // Hover texts by local ID
    private val hoverTexts = ConcurrentHashMap<Int, HoverText>()
    
    // Hover texts by object UUID
    private val hoverTextsByUUID = ConcurrentHashMap<UUID, HoverText>()
    
    /**
     * Set or update hover text for an object.
     */
    fun setHoverText(
        localId: Int,
        objectId: UUID,
        text: String,
        color: FloatArray = floatArrayOf(1f, 1f, 1f),
        alpha: Float = 1f
    ) {
        if (text.isEmpty()) {
            removeHoverText(localId)
            return
        }
        
        val hoverText = HoverText(
            localId = localId,
            objectId = objectId,
            text = sanitizeText(text),
            color = color,
            alpha = alpha.coerceIn(0f, 1f)
        )
        
        hoverTexts[localId] = hoverText
        hoverTextsByUUID[objectId] = hoverText
        
        Log.v(TAG, "Set hover text for object $localId: ${text.take(50)}...")
    }
    
    /**
     * Set hover text from object update data.
     */
    fun setHoverTextFromObjectData(
        localId: Int,
        objectId: UUID,
        textData: ByteArray
    ) {
        if (textData.isEmpty()) {
            removeHoverText(localId)
            return
        }
        
        try {
            // Format: text (null-terminated), then 4 bytes RGBA color
            val nullIndex = textData.indexOfFirst { it == 0.toByte() }
            if (nullIndex < 0) return
            
            val text = String(textData, 0, nullIndex, Charsets.UTF_8)
            
            // Parse color if present (after null terminator)
            val color = if (textData.size >= nullIndex + 5) {
                floatArrayOf(
                    (textData[nullIndex + 1].toInt() and 0xFF) / 255f,
                    (textData[nullIndex + 2].toInt() and 0xFF) / 255f,
                    (textData[nullIndex + 3].toInt() and 0xFF) / 255f
                )
            } else {
                floatArrayOf(1f, 1f, 1f)
            }
            
            val alpha = if (textData.size >= nullIndex + 5) {
                (textData[nullIndex + 4].toInt() and 0xFF) / 255f
            } else {
                1f
            }
            
            setHoverText(localId, objectId, text, color, alpha)
            
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing hover text data", e)
        }
    }
    
    /**
     * Remove hover text for an object.
     */
    fun removeHoverText(localId: Int) {
        val removed = hoverTexts.remove(localId)
        if (removed != null) {
            hoverTextsByUUID.remove(removed.objectId)
        }
    }
    
    /**
     * Get hover text for an object by local ID.
     */
    fun getHoverText(localId: Int): HoverText? = hoverTexts[localId]
    
    /**
     * Get hover text for an object by UUID.
     */
    fun getHoverTextByUUID(objectId: UUID): HoverText? = hoverTextsByUUID[objectId]
    
    /**
     * Get all hover texts.
     */
    fun getAllHoverTexts(): List<HoverText> = hoverTexts.values.toList()
    
    /**
     * Get hover texts within a certain distance from a position.
     * This is used for rendering optimization.
     */
    fun getVisibleHoverTexts(
        cameraX: Float,
        cameraY: Float,
        cameraZ: Float,
        maxDistance: Float
    ): List<HoverText> {
        // In a real implementation, this would filter by position
        // For now, return all (filtering would require object positions)
        return hoverTexts.values.filter { it.alpha > 0.01f }.toList()
    }
    
    /**
     * Sanitize text for display.
     */
    private fun sanitizeText(text: String): String {
        // Split into lines and limit
        val lines = text.lines().take(MAX_TEXT_LINES)
        
        // Truncate each line
        return lines.map { line ->
            if (line.length > MAX_LINE_LENGTH) {
                line.take(MAX_LINE_LENGTH - 3) + "..."
            } else {
                line
            }
        }.joinToString("\n")
    }
    
    /**
     * Clear all hover texts.
     */
    fun clear() {
        hoverTexts.clear()
        hoverTextsByUUID.clear()
    }
    
    /**
     * Get total hover text count.
     */
    fun getCount(): Int = hoverTexts.size
}

/**
 * Hover text data.
 */
data class HoverText(
    val localId: Int,
    val objectId: UUID,
    val text: String,
    val color: FloatArray,
    val alpha: Float
) {
    /**
     * Get Android color int for rendering.
     */
    fun getColorInt(): Int {
        return Color.argb(
            (alpha * 255).toInt(),
            (color[0] * 255).toInt(),
            (color[1] * 255).toInt(),
            (color[2] * 255).toInt()
        )
    }
    
    /**
     * Get text lines for multi-line rendering.
     */
    fun getLines(): List<String> = text.lines()
    
    /**
     * Check if visible (alpha > 0).
     */
    fun isVisible(): Boolean = alpha > 0.01f
    
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is HoverText) return false
        return localId == other.localId && 
               objectId == other.objectId && 
               text == other.text &&
               color.contentEquals(other.color) &&
               alpha == other.alpha
    }
    
    override fun hashCode(): Int {
        var result = localId
        result = 31 * result + objectId.hashCode()
        result = 31 * result + text.hashCode()
        result = 31 * result + color.contentHashCode()
        result = 31 * result + alpha.hashCode()
        return result
    }
}
