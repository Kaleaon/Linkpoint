package com.linkpoint.avatar

import android.content.Context
import android.util.Log
import com.linkpoint.protocol.types.LLVector3
import org.w3c.dom.Element
import java.util.UUID
import javax.xml.parsers.DocumentBuilderFactory

/**
 * Avatar Attention System - Controls where avatars look.
 * 
 * Based on Second Life's attention system where various events compete for
 * avatar attention, controlling head/eye tracking. Events with higher priority
 * take precedence, and attention persists until timeout or higher priority event.
 * 
 * Attention Types:
 * - IDLE: Tracks default focus point or mouse movement
 * - AUTO_LISTEN: Tracks nearby chat sources
 * - FREELOOK: Tracks target objects and mouse in 3rd person
 * - RESPOND: Tracks beginning of typing
 * - HOVER: Tracks hovered objects when tooltips enabled
 * - CONVERSATION: Tracks clicked avatars/objects
 * - SELECT: Tracks grabbed/moved objects
 * - FOCUS: Freezes during customization or explicit focus
 * - MOUSELOOK: Tracks center of view in mouselook mode
 */
class AvatarAttentionSystem(context: Context?) {
    
    companion object {
        private const val TAG = "AvatarAttention"
    }
    
    /**
     * Attention types with ascending priority order
     */
    enum class AttentionType(val priority: Float) {
        IDLE(1.0f),
        FREELOOK(2.0f),
        AUTO_LISTEN(3.0f),
        RESPOND(3.0f),
        HOVER(4.0f),
        CONVERSATION(5.0f),
        SELECT(6.0f),
        FOCUS(6.0f),
        MOUSELOOK(7.0f)
    }
    
    /**
     * Gender-specific attention parameters
     */
    enum class Gender {
        MASCULINE, FEMININE
    }
    
    /**
     * Attention parameter configuration
     */
    data class AttentionParam(
        val type: AttentionType,
        val priority: Float,
        val timeout: Float  // Negative = never timeout
    )
    
    /**
     * Current attention state for an avatar
     */
    data class AttentionState(
        var type: AttentionType = AttentionType.IDLE,
        var targetPosition: LLVector3? = null,
        var targetId: UUID? = null,
        var startTime: Long = System.currentTimeMillis(),
        var gender: Gender = Gender.MASCULINE
    ) {
        val elapsedSeconds: Float
            get() = (System.currentTimeMillis() - startTime) / 1000f
    }
    
    // Gender-specific attention parameters
    private val masculineParams = mutableMapOf<AttentionType, AttentionParam>()
    private val feminineParams = mutableMapOf<AttentionType, AttentionParam>()
    
    // Default parameters (used if config loading fails)
    private val defaultParams = mapOf(
        AttentionType.IDLE to AttentionParam(AttentionType.IDLE, 1.0f, 3.0f),
        AttentionType.AUTO_LISTEN to AttentionParam(AttentionType.AUTO_LISTEN, 3.0f, 4.0f),
        AttentionType.FREELOOK to AttentionParam(AttentionType.FREELOOK, 2.0f, 2.0f),
        AttentionType.RESPOND to AttentionParam(AttentionType.RESPOND, 3.0f, 4.0f),
        AttentionType.HOVER to AttentionParam(AttentionType.HOVER, 4.0f, 1.0f),
        AttentionType.CONVERSATION to AttentionParam(AttentionType.CONVERSATION, 5.0f, -1f),
        AttentionType.SELECT to AttentionParam(AttentionType.SELECT, 6.0f, -1f),
        AttentionType.FOCUS to AttentionParam(AttentionType.FOCUS, 6.0f, -1f),
        AttentionType.MOUSELOOK to AttentionParam(AttentionType.MOUSELOOK, 7.0f, -1f)
    )
    
    init {
        if (context != null) {
            loadAttentionConfig(context)
        } else {
            useDefaultParams()
        }
    }
    
    private fun loadAttentionConfig(context: Context) {
        try {
            context.assets.open("avatar/attentions.xml").use { inputStream ->
                val factory = DocumentBuilderFactory.newInstance()
                val builder = factory.newDocumentBuilder()
                val doc = builder.parse(inputStream)
                
                val genderElements = doc.getElementsByTagName("gender")
                for (i in 0 until genderElements.length) {
                    val genderElement = genderElements.item(i) as Element
                    val genderName = genderElement.getAttribute("name")
                    val params = if (genderName.equals("Masculine", ignoreCase = true)) {
                        masculineParams
                    } else {
                        feminineParams
                    }
                    
                    val paramElements = genderElement.getElementsByTagName("param")
                    for (j in 0 until paramElements.length) {
                        val paramElement = paramElements.item(j) as Element
                        val attentionName = paramElement.getAttribute("attention").uppercase()
                        val priority = paramElement.getAttribute("priority").toFloatOrNull() ?: 1.0f
                        val timeout = paramElement.getAttribute("timeout").toFloatOrNull() ?: 3.0f
                        
                        try {
                            val type = AttentionType.valueOf(attentionName)
                            params[type] = AttentionParam(type, priority, timeout)
                        } catch (e: IllegalArgumentException) {
                            Log.w(TAG, "Unknown attention type: $attentionName")
                        }
                    }
                }
                
                Log.i(TAG, "Loaded attention config with ${masculineParams.size} masculine and ${feminineParams.size} feminine params")
            }
        } catch (e: Exception) {
            Log.w(TAG, "Failed to load attention config, using defaults", e)
            useDefaultParams()
        }
    }
    
    private fun useDefaultParams() {
        masculineParams.putAll(defaultParams)
        feminineParams.putAll(defaultParams)
    }
    
    /**
     * Get attention parameters for a specific gender
     */
    fun getParams(gender: Gender): Map<AttentionType, AttentionParam> {
        return when (gender) {
            Gender.MASCULINE -> masculineParams.ifEmpty { defaultParams }
            Gender.FEMININE -> feminineParams.ifEmpty { defaultParams }
        }
    }
    
    /**
     * Check if a new attention event should take over from current attention
     */
    fun shouldChangeAttention(
        current: AttentionState,
        newType: AttentionType
    ): Boolean {
        val params = getParams(current.gender)
        val currentParam = params[current.type] 
            ?: defaultParams[current.type] 
            ?: throw IllegalStateException("Default parameter for ${current.type} not found")
        val newParam = params[newType] 
            ?: defaultParams[newType] 
            ?: throw IllegalStateException("Default parameter for ${newType} not found")
        
        // Higher or equal priority takes over
        if (newParam.priority >= currentParam.priority) {
            return true
        }
        
        // Check if current attention has timed out
        if (currentParam.timeout > 0 && current.elapsedSeconds >= currentParam.timeout) {
            return true
        }
        
        return false
    }
    
    /**
     * Update attention state based on a new event
     */
    fun updateAttention(
        current: AttentionState,
        newType: AttentionType,
        targetPosition: LLVector3? = null,
        targetId: UUID? = null
    ): Boolean {
        if (shouldChangeAttention(current, newType)) {
            current.type = newType
            current.targetPosition = targetPosition
            current.targetId = targetId
            current.startTime = System.currentTimeMillis()
            return true
        }
        return false
    }
    
    /**
     * Check if attention has timed out and should return to idle
     */
    fun checkTimeout(state: AttentionState): Boolean {
        val params = getParams(state.gender)
        val param = params[state.type] 
            ?: defaultParams[state.type] 
            ?: throw IllegalStateException("Default parameter for ${state.type} not found")
        
        if (param.timeout > 0 && state.elapsedSeconds >= param.timeout) {
            state.type = AttentionType.IDLE
            state.targetPosition = null
            state.targetId = null
            state.startTime = System.currentTimeMillis()
            return true
        }
        return false
    }
    
    /**
     * Calculate head rotation to look at target
     * Returns rotation angles (pitch, yaw) in radians
     */
    fun calculateLookAtRotation(
        headPosition: LLVector3,
        targetPosition: LLVector3,
        maxPitch: Float = Math.toRadians(30.0).toFloat(),
        maxYaw: Float = Math.toRadians(60.0).toFloat()
    ): Pair<Float, Float> {
        val direction = LLVector3(
            targetPosition.x - headPosition.x,
            targetPosition.y - headPosition.y,
            targetPosition.z - headPosition.z
        )
        
        val horizontalDist = kotlin.math.hypot(direction.x, direction.y)
        
        // Calculate pitch (up/down)
        var pitch = kotlin.math.atan2(direction.z, horizontalDist)
        pitch = pitch.coerceIn(-maxPitch, maxPitch)
        
        // Calculate yaw (left/right)
        var yaw = kotlin.math.atan2(direction.y, direction.x)
        yaw = yaw.coerceIn(-maxYaw, maxYaw)
        
        return Pair(pitch, yaw)
    }
}
