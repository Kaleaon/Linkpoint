package com.linkpoint.inventory

import android.util.Log
import com.linkpoint.assets.AnimationManager
import com.linkpoint.assets.AssetCache
import com.linkpoint.assets.AssetType
import com.linkpoint.assets.SoundManager
import com.linkpoint.protocol.types.LLVector3
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

/**
 * Manages gestures and their playback
 */
class GestureManager(
    private val cache: AssetCache,
    private val animationManager: AnimationManager,
    private val soundManager: SoundManager,
    private val chatCallback: (String) -> Unit
) {
    companion object {
        private const val TAG = "GestureManager"
        
        // Step types
        const val STEP_ANIMATION = 0
        const val STEP_SOUND = 1
        const val STEP_CHAT = 2
        const val STEP_WAIT = 3
        const val STEP_EOF = 4
    }
    
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    
    // Active gestures
    private val activeGestures = ConcurrentHashMap<UUID, GestureData>()
    private val playingGestures = ConcurrentHashMap<UUID, Job>()
    
    // Trigger mappings
    private val triggerMap = ConcurrentHashMap<String, UUID>()
    
    private val _activeGesturesList = MutableStateFlow<List<UUID>>(emptyList())
    val activeGesturesList: StateFlow<List<UUID>> = _activeGesturesList
    
    /**
     * Activate a gesture
     */
    suspend fun activateGesture(assetId: UUID): Boolean {
        val data = cache.get(assetId, AssetType.GESTURE) ?: return false
        val gesture = parseGesture(assetId, data) ?: return false
        
        activeGestures[assetId] = gesture
        
        // Register trigger
        if (gesture.trigger.isNotEmpty()) {
            triggerMap[gesture.trigger.lowercase()] = assetId
        }
        
        updateActiveList()
        return true
    }
    
    /**
     * Deactivate a gesture
     */
    fun deactivateGesture(assetId: UUID) {
        activeGestures.remove(assetId)?.let { gesture ->
            triggerMap.remove(gesture.trigger.lowercase())
        }
        stopGesture(assetId)
        updateActiveList()
    }
    
    /**
     * Check if text triggers a gesture
     */
    fun checkTrigger(text: String): UUID? {
        val lowerText = text.lowercase()
        return triggerMap[lowerText]
    }
    
    /**
     * Play a gesture
     */
    fun playGesture(assetId: UUID, position: LLVector3 = LLVector3.zero()) {
        val gesture = activeGestures[assetId] ?: return
        
        // Cancel if already playing
        playingGestures[assetId]?.cancel()
        
        playingGestures[assetId] = scope.launch {
            var stepIndex = 0
            
            while (stepIndex < gesture.steps.size && isActive) {
                val step = gesture.steps[stepIndex]
                
                when (step.type) {
                    STEP_ANIMATION -> {
                        val animId = UUID.fromString(step.id)
                        animationManager.getAnimation(animId)
                        // Would start animation on avatar
                    }
                    
                    STEP_SOUND -> {
                        val soundId = UUID.fromString(step.id)
                        soundManager.playSound(soundId, position)
                    }
                    
                    STEP_CHAT -> {
                        chatCallback(step.text)
                    }
                    
                    STEP_WAIT -> {
                        delay((step.waitTime * 1000).toLong())
                    }
                }
                
                stepIndex++
            }
            
            playingGestures.remove(assetId)
        }
    }
    
    /**
     * Stop a playing gesture
     */
    fun stopGesture(assetId: UUID) {
        playingGestures[assetId]?.cancel()
        playingGestures.remove(assetId)
    }
    
    /**
     * Stop all gestures
     */
    fun stopAllGestures() {
        playingGestures.values.forEach { it.cancel() }
        playingGestures.clear()
    }
    
    private fun parseGesture(assetId: UUID, data: ByteArray): GestureData? {
        try {
            val content = String(data, Charsets.UTF_8)
            val lines = content.lines()
            
            var version = 0
            var trigger = ""
            var key = 0
            var mask = 0
            var replaceText = ""
            val steps = mutableListOf<GestureStep>()
            
            var lineIndex = 0
            
            // Parse header
            while (lineIndex < lines.size) {
                val line = lines[lineIndex].trim()
                lineIndex++
                
                if (line.startsWith("version")) {
                    version = line.substringAfter("version").trim().toIntOrNull() ?: 2
                } else if (line.startsWith("trigger")) {
                    trigger = lines[lineIndex].trim()
                    lineIndex++
                } else if (line.startsWith("key")) {
                    key = lines[lineIndex].trim().toIntOrNull() ?: 0
                    lineIndex++
                } else if (line.startsWith("mask")) {
                    mask = lines[lineIndex].trim().toIntOrNull() ?: 0
                    lineIndex++
                } else if (line.startsWith("replace")) {
                    replaceText = lines[lineIndex].trim()
                    lineIndex++
                } else if (line.startsWith("steps")) {
                    // Parse steps
                    val stepCount = lines[lineIndex].trim().toIntOrNull() ?: 0
                    lineIndex++
                    
                    for (i in 0 until stepCount) {
                        if (lineIndex >= lines.size) break
                        
                        val stepType = lines[lineIndex].trim().toIntOrNull() ?: continue
                        lineIndex++
                        
                        when (stepType) {
                            STEP_ANIMATION -> {
                                val animName = lines[lineIndex].trim()
                                lineIndex++
                                val animId = lines[lineIndex].trim()
                                lineIndex++
                                val flags = lines[lineIndex].trim().toIntOrNull() ?: 0
                                lineIndex++
                                
                                steps.add(GestureStep(
                                    type = STEP_ANIMATION,
                                    id = animId,
                                    text = animName,
                                    flags = flags
                                ))
                            }
                            
                            STEP_SOUND -> {
                                val soundName = lines[lineIndex].trim()
                                lineIndex++
                                val soundId = lines[lineIndex].trim()
                                lineIndex++
                                val flags = lines[lineIndex].trim().toIntOrNull() ?: 0
                                lineIndex++
                                
                                steps.add(GestureStep(
                                    type = STEP_SOUND,
                                    id = soundId,
                                    text = soundName,
                                    flags = flags
                                ))
                            }
                            
                            STEP_CHAT -> {
                                val chatText = lines[lineIndex].trim()
                                lineIndex++
                                val flags = lines[lineIndex].trim().toIntOrNull() ?: 0
                                lineIndex++
                                
                                steps.add(GestureStep(
                                    type = STEP_CHAT,
                                    text = chatText,
                                    flags = flags
                                ))
                            }
                            
                            STEP_WAIT -> {
                                val waitTime = lines[lineIndex].trim().toFloatOrNull() ?: 0f
                                lineIndex++
                                val flags = lines[lineIndex].trim().toIntOrNull() ?: 0
                                lineIndex++
                                
                                steps.add(GestureStep(
                                    type = STEP_WAIT,
                                    waitTime = waitTime,
                                    flags = flags
                                ))
                            }
                            
                            STEP_EOF -> break
                        }
                    }
                    break
                }
            }
            
            return GestureData(
                assetId = assetId,
                trigger = trigger,
                key = key,
                mask = mask,
                replaceText = replaceText,
                steps = steps
            )
        } catch (e: Exception) {
            Log.e(TAG, "Failed to parse gesture", e)
            return null
        }
    }
    
    private fun updateActiveList() {
        _activeGesturesList.value = activeGestures.keys.toList()
    }
    
    fun shutdown() {
        scope.cancel()
        stopAllGestures()
    }
}

data class GestureData(
    val assetId: UUID,
    val trigger: String,
    val key: Int,
    val mask: Int,
    val replaceText: String,
    val steps: List<GestureStep>
)

data class GestureStep(
    val type: Int,
    val id: String = "",
    val text: String = "",
    val waitTime: Float = 0f,
    val flags: Int = 0
)
