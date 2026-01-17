package com.linkpoint.rlv

import android.util.Log
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

/**
 * RLV (Restrained Love Viewer) Controller - Handles RLV/RLVa commands.
 * 
 * Based on Lumiya's RLVController.java
 * 
 * RLV is an API that allows in-world objects to restrict viewer behavior.
 * Common uses: roleplay, BDSM content, furniture systems, combat systems.
 * 
 * Commands format: @command[:option]=y/n/add/rem/force
 * Examples:
 *   @unsit=n          - Prevent standing up
 *   @tploc=n          - Prevent teleporting to location
 *   @sendchat=n       - Prevent chat
 *   @sit:<uuid>=force - Force sit on object
 */
class RLVController {
    
    companion object {
        private const val TAG = "RLVController"
        
        // RLV version info
        const val RLV_VERSION = "3.4.3"
        const val RLVA_VERSION = "2.4"
        const val VIEWER_NAME = "Linkpoint"
        
        // RLV command prefixes
        const val RLV_CMD_PREFIX = "@"
        const val RLV_REPLY_CHANNEL = -1812221819
        
        // Restriction categories
        const val CAT_MOVEMENT = "movement"
        const val CAT_CHAT = "chat"
        const val CAT_INVENTORY = "inventory"
        const val CAT_CAMERA = "camera"
        const val CAT_APPEARANCE = "appearance"
        const val CAT_TELEPORT = "teleport"
        const val CAT_INTERACTION = "interaction"
    }
    
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    
    // RLV enabled state
    private val _enabled = MutableStateFlow(true)
    val enabled: StateFlow<Boolean> = _enabled
    
    // Active restrictions by command name
    private val restrictions = ConcurrentHashMap<String, RLVRestriction>()
    
    // Exceptions (allowed items within restrictions)
    private val exceptions = ConcurrentHashMap<String, MutableSet<String>>()
    
    // Behavior callbacks
    private val behaviorCallbacks = mutableListOf<RLVBehaviorCallback>()
    
    /**
     * Enable/disable RLV processing.
     */
    fun setEnabled(enabled: Boolean) {
        _enabled.value = enabled
        if (!enabled) {
            clearAllRestrictions()
        }
        Log.i(TAG, "RLV ${if (enabled) "enabled" else "disabled"}")
    }
    
    /**
     * Process RLV command from chat/script.
     */
    fun processCommand(objectId: UUID, objectName: String, command: String): RLVResult {
        if (!_enabled.value) {
            return RLVResult.Disabled
        }
        
        if (!command.startsWith(RLV_CMD_PREFIX)) {
            return RLVResult.NotRLV
        }
        
        val cmdStr = command.substring(1) // Remove @
        val commands = cmdStr.split(",")
        
        var result = RLVResult.Success
        for (cmd in commands) {
            val singleResult = processSingleCommand(objectId, objectName, cmd.trim())
            if (singleResult != RLVResult.Success) {
                result = singleResult
            }
        }
        
        return result
    }
    
    private fun processSingleCommand(objectId: UUID, objectName: String, command: String): RLVResult {
        // Parse command: name[:option]=value
        val equalsIndex = command.lastIndexOf('=')
        if (equalsIndex < 0) {
            return RLVResult.InvalidFormat
        }
        
        val cmdPart = command.substring(0, equalsIndex)
        val value = command.substring(equalsIndex + 1).lowercase()
        
        // Parse command name and option
        val colonIndex = cmdPart.indexOf(':')
        val cmdName = if (colonIndex >= 0) cmdPart.substring(0, colonIndex) else cmdPart
        val option = if (colonIndex >= 0) cmdPart.substring(colonIndex + 1) else null
        
        Log.d(TAG, "RLV: cmd=$cmdName option=$option value=$value from $objectName")
        
        return when (value) {
            "y" -> removeRestriction(cmdName, objectId, option)
            "n" -> addRestriction(cmdName, objectId, option)
            "add" -> addException(cmdName, option)
            "rem" -> removeException(cmdName, option)
            "force" -> executeForceCommand(cmdName, option, objectId)
            else -> handleReplyCommand(cmdName, option, value)
        }
    }
    
    /**
     * Add a restriction.
     */
    private fun addRestriction(command: String, objectId: UUID, option: String?): RLVResult {
        val restriction = RLVRestriction(
            command = command,
            objectId = objectId,
            option = option,
            timestamp = System.currentTimeMillis()
        )
        
        val key = if (option != null) "$command:$option" else command
        restrictions[key] = restriction
        
        notifyBehaviorChange(command, true)
        Log.i(TAG, "Added restriction: $key")
        
        return RLVResult.Success
    }
    
    /**
     * Remove a restriction.
     */
    private fun removeRestriction(command: String, objectId: UUID, option: String?): RLVResult {
        val key = if (option != null) "$command:$option" else command
        
        // Only remove if same object or clear all for this command
        val existing = restrictions[key]
        if (existing != null && (existing.objectId == objectId || option == null)) {
            restrictions.remove(key)
            notifyBehaviorChange(command, false)
            Log.i(TAG, "Removed restriction: $key")
        }
        
        return RLVResult.Success
    }
    
    /**
     * Add an exception to a restriction.
     */
    private fun addException(command: String, exception: String?): RLVResult {
        if (exception == null) return RLVResult.InvalidFormat
        
        exceptions.getOrPut(command) { mutableSetOf() }.add(exception)
        Log.d(TAG, "Added exception: $command -> $exception")
        
        return RLVResult.Success
    }
    
    /**
     * Remove an exception.
     */
    private fun removeException(command: String, exception: String?): RLVResult {
        if (exception == null) return RLVResult.InvalidFormat
        
        exceptions[command]?.remove(exception)
        Log.d(TAG, "Removed exception: $command -> $exception")
        
        return RLVResult.Success
    }
    
    /**
     * Execute a force command (immediate action).
     */
    private fun executeForceCommand(command: String, option: String?, objectId: UUID): RLVResult {
        Log.i(TAG, "Force command: $command option=$option")
        
        return when (command) {
            "sit" -> forceSit(option, objectId)
            "unsit" -> forceUnsit()
            "tpto" -> forceTeleport(option)
            "attach" -> forceAttach(option)
            "detach" -> forceDetach(option)
            "remoutfit" -> forceRemoveOutfit(option)
            else -> RLVResult.UnknownCommand
        }
    }
    
    /**
     * Handle reply/query commands.
     */
    private fun handleReplyCommand(command: String, option: String?, replyChannel: String): RLVResult {
        val channel = replyChannel.toIntOrNull() ?: return RLVResult.InvalidFormat
        
        val reply = when (command) {
            "version" -> RLV_VERSION
            "versionnew" -> RLV_VERSION
            "versionnum" -> "3040300"
            "getoutfit" -> getOutfitInfo(option)
            "getattach" -> getAttachInfo(option)
            "getstatus" -> getStatus(option)
            "getstatusall" -> getStatusAll()
            else -> return RLVResult.UnknownCommand
        }
        
        // TODO: Send reply to chat channel
        Log.d(TAG, "RLV reply on $channel: $reply")
        
        return RLVResult.Success
    }
    
    // Force command implementations
    
    private fun forceSit(target: String?, objectId: UUID): RLVResult {
        // TODO: Implement sit on target UUID
        Log.d(TAG, "Force sit on: $target")
        return RLVResult.Success
    }
    
    private fun forceUnsit(): RLVResult {
        // TODO: Implement stand up
        Log.d(TAG, "Force unsit")
        return RLVResult.Success
    }
    
    private fun forceTeleport(coords: String?): RLVResult {
        // Format: regionname/x/y/z
        Log.d(TAG, "Force teleport to: $coords")
        return RLVResult.Success
    }
    
    private fun forceAttach(target: String?): RLVResult {
        Log.d(TAG, "Force attach: $target")
        return RLVResult.Success
    }
    
    private fun forceDetach(target: String?): RLVResult {
        Log.d(TAG, "Force detach: $target")
        return RLVResult.Success
    }
    
    private fun forceRemoveOutfit(layer: String?): RLVResult {
        Log.d(TAG, "Force remove outfit layer: $layer")
        return RLVResult.Success
    }
    
    // Query implementations
    
    private fun getOutfitInfo(layer: String?): String {
        // Return worn items info
        return ""
    }
    
    private fun getAttachInfo(point: String?): String {
        // Return attachment info
        return ""
    }
    
    private fun getStatus(filter: String?): String {
        return restrictions.keys.joinToString("/")
    }
    
    private fun getStatusAll(): String {
        return restrictions.entries.joinToString("/") { "${it.key}:${it.value.objectId}" }
    }
    
    // Restriction checking
    
    /**
     * Check if a behavior is restricted.
     */
    fun isRestricted(command: String, target: String? = null): Boolean {
        if (!_enabled.value) return false
        
        // Check direct restriction
        if (restrictions.containsKey(command)) {
            // Check for exception
            if (target != null && exceptions[command]?.contains(target) == true) {
                return false
            }
            return true
        }
        
        return false
    }
    
    /**
     * Check common restrictions.
     */
    fun canChat(): Boolean = !isRestricted("sendchat") && !isRestricted("chatshout")
    fun canIM(): Boolean = !isRestricted("sendim")
    fun canTeleport(): Boolean = !isRestricted("tploc") && !isRestricted("tplm")
    fun canStand(): Boolean = !isRestricted("unsit")
    fun canSit(): Boolean = !isRestricted("sit")
    fun canFly(): Boolean = !isRestricted("fly")
    fun canTouchWorld(): Boolean = !isRestricted("touchworld")
    fun canTouchAttach(): Boolean = !isRestricted("touchattach")
    fun canAcceptTp(): Boolean = !isRestricted("accepttp")
    fun canSeeNames(): Boolean = !isRestricted("shownames")
    fun canSeeLocation(): Boolean = !isRestricted("showloc")
    fun canEditAppearance(): Boolean = !isRestricted("editappearance")
    
    /**
     * Get all active restrictions.
     */
    fun getActiveRestrictions(): List<RLVRestriction> = restrictions.values.toList()
    
    /**
     * Clear all restrictions (e.g., on detach).
     */
    fun clearRestrictions(objectId: UUID) {
        val toRemove = restrictions.filter { it.value.objectId == objectId }.keys
        toRemove.forEach { 
            restrictions.remove(it)
            notifyBehaviorChange(it.substringBefore(':'), false)
        }
        Log.i(TAG, "Cleared ${toRemove.size} restrictions from $objectId")
    }
    
    /**
     * Clear all restrictions.
     */
    fun clearAllRestrictions() {
        restrictions.clear()
        exceptions.clear()
        Log.i(TAG, "Cleared all RLV restrictions")
    }
    
    /**
     * Register behavior change callback.
     */
    fun registerBehaviorCallback(callback: RLVBehaviorCallback) {
        behaviorCallbacks.add(callback)
    }
    
    private fun notifyBehaviorChange(command: String, restricted: Boolean) {
        behaviorCallbacks.forEach { it.onBehaviorChanged(command, restricted) }
    }
    
    fun shutdown() {
        scope.cancel()
        clearAllRestrictions()
    }
}

/**
 * RLV restriction data.
 */
data class RLVRestriction(
    val command: String,
    val objectId: UUID,
    val option: String?,
    val timestamp: Long
)

/**
 * RLV command result.
 */
enum class RLVResult {
    Success,
    Disabled,
    NotRLV,
    InvalidFormat,
    UnknownCommand,
    Failed
}

/**
 * Callback for RLV behavior changes.
 */
fun interface RLVBehaviorCallback {
    fun onBehaviorChanged(command: String, restricted: Boolean)
}
