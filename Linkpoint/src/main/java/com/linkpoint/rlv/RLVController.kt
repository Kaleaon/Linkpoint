package com.linkpoint.rlv

import android.util.Log
import com.linkpoint.objects.SitManager
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

private object SafeLog {
    fun d(tag: String, msg: String) { try { Log.d(tag, msg) } catch (t: Throwable) {} }
    fun i(tag: String, msg: String) { try { Log.i(tag, msg) } catch (t: Throwable) {} }
    fun w(tag: String, msg: String) { try { Log.w(tag, msg) } catch (t: Throwable) {} }
    fun e(tag: String, msg: String, tr: Throwable? = null) { try { Log.e(tag, msg, tr) } catch (t: Throwable) {} }
}

/**
 * RLV (Restrained Love Viewer) Controller - Handles RLV/RLVa commands.
 *
 * Based on the reference viewer's RLVController.java
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
class RLVController(
    private val chatManager: (() -> com.linkpoint.chat.ChatManager?)? = null,
    private val sitManager: (() -> SitManager?)? = null,
    private val outfitManager: (() -> com.linkpoint.inventory.OutfitManager?)? = null,
    private val teleportManager: (() -> com.linkpoint.teleport.TeleportManager?)? = null
) {

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

    // Session trust list for Tier 2 forced actions
    private val sessionTrust = ConcurrentHashMap<UUID, Boolean>()

    // Behavior callbacks
    private val behaviorCallbacks = mutableListOf<RLVBehaviorCallback>()

    // Notification listeners for Tier 1 soft restriction toasts
    private val notificationListeners = mutableListOf<RLVNotificationListener>()

    // Prompt handler for Tier 2 forced physical action prompts
    private var promptHandler: RLVPromptHandler? = null

    /**
     * Enable/disable RLV processing.
     */
    fun setEnabled(enabled: Boolean) {
        _enabled.value = enabled
        if (!enabled) {
            clearAllRestrictions()
        }
        SafeLog.i(TAG, "RLV ${if (enabled) "enabled" else "disabled"}")
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

    private fun processSingleCommand(objectId: UUID, objectName: String, rawCommand: String): RLVResult {
        val command = rawCommand.trim().removePrefix("@")
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

        SafeLog.d(TAG, "RLV: cmd=$cmdName option=$option value=$value from $objectName")

        return when (value) {
            "y" -> removeRestriction(cmdName, objectId, objectName, option)
            "n" -> addRestriction(cmdName, objectId, objectName, option)
            "add" -> addException(cmdName, option)
            "rem" -> removeException(cmdName, option)
            "force" -> executeForceCommand(cmdName, option, objectId, objectName)
            else -> handleReplyCommand(cmdName, option, value)
        }
    }

    /**
     * Add a restriction (Tier 1 soft restriction).
     */
    private fun addRestriction(command: String, objectId: UUID, objectName: String, option: String?): RLVResult {
        val restriction = RLVRestriction(
            command = command,
            objectId = objectId,
            objectName = objectName,
            option = option,
            timestamp = System.currentTimeMillis()
        )

        val key = if (option != null) "$command:$option" else command
        restrictions[key] = restriction

        notifyBehaviorChange(command, true)
        notifyNotificationListeners(objectId, objectName, command, option, true)
        SafeLog.i(TAG, "Added restriction: $key from $objectName ($objectId)")

        return RLVResult.Success
    }

    /**
     * Remove a restriction.
     */
    private fun removeRestriction(command: String, objectId: UUID, objectName: String, option: String?): RLVResult {
        val key = if (option != null) "$command:$option" else command

        // Only remove if same object or clear all for this command
        val existing = restrictions[key]
        if (existing != null && (existing.objectId == objectId || option == null)) {
            restrictions.remove(key)
            notifyBehaviorChange(command, false)
            notifyNotificationListeners(objectId, objectName, command, option, false)
            SafeLog.i(TAG, "Removed restriction: $key from $objectName ($objectId)")
        }

        return RLVResult.Success
    }

    /**
     * Add an exception to a restriction.
     */
    private fun addException(command: String, exception: String?): RLVResult {
        if (exception == null) return RLVResult.InvalidFormat

        exceptions.getOrPut(command) { mutableSetOf() }.add(exception)
        SafeLog.d(TAG, "Added exception: $command -> $exception")

        return RLVResult.Success
    }

    /**
     * Remove an exception.
     */
    private fun removeException(command: String, exception: String?): RLVResult {
        if (exception == null) return RLVResult.InvalidFormat

        exceptions[command]?.remove(exception)
        SafeLog.d(TAG, "Removed exception: $command -> $exception")

        return RLVResult.Success
    }

    /**
     * Execute a Tier 2 force command (intercepted for user approval if not in session trust list).
     */
    private fun executeForceCommand(command: String, option: String?, objectId: UUID, objectName: String): RLVResult {
        SafeLog.i(TAG, "Force command: $command option=$option from $objectName ($objectId)")

        val isTrusted = sessionTrust[objectId] == true
        if (!isTrusted) {
            val handler = promptHandler
            if (handler == null) {
                SafeLog.w(TAG, "Tier 2 forced command $command denied - no prompt handler registered")
                sendDenialReply(command, option, objectName)
                return RLVResult.Failed
            }

            val deferred = CompletableDeferred<RLVDecision>()
            val promptId = "${objectId}_${command}_${System.currentTimeMillis()}"

            try {
                handler.onRequestApproval(promptId, objectId, objectName, command, option) { decision ->
                    deferred.complete(decision)
                }
            } catch (e: Exception) {
                SafeLog.e(TAG, "Error invoking RLVPromptHandler", e)
                sendDenialReply(command, option, objectName)
                return RLVResult.Failed
            }

            val decision = runBlocking {
                withTimeoutOrNull(30_000L) {
                    deferred.await()
                }
            } ?: RLVDecision.DENY

            when (decision) {
                RLVDecision.APPROVE -> {
                    SafeLog.i(TAG, "Tier 2 forced command $command approved by user")
                }
                RLVDecision.ALWAYS_ALLOW_SESSION -> {
                    sessionTrust[objectId] = true
                    SafeLog.i(TAG, "Tier 2 forced command $command always allowed for session for $objectId")
                }
                RLVDecision.DENY -> {
                    SafeLog.w(TAG, "Tier 2 forced command $command denied or timed out for $objectId")
                    sendDenialReply(command, option, objectName)
                    return RLVResult.Failed
                }
            }
        }

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

    private fun sendDenialReply(command: String, option: String?, objectName: String) {
        val replyMsg = "Denied forced action: @$command${if (option != null) ":$option" else ""}=force from $objectName"
        chatManager?.invoke()?.let { manager ->
            try {
                manager.sendChat(replyMsg, channel = RLV_REPLY_CHANNEL)
                SafeLog.d(TAG, "Sent RLV denial reply on channel $RLV_REPLY_CHANNEL: $replyMsg")
            } catch (e: Exception) {
                SafeLog.e(TAG, "Failed to send RLV denial reply", e)
            }
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

        // Send reply to chat channel
        chatManager?.invoke()?.let { manager ->
            try {
                // ChatManager.sendChat handles its own coroutine/threading
                manager.sendChat(reply, channel = channel)
                SafeLog.d(TAG, "RLV reply sent on channel $channel: $reply")
            } catch (e: Exception) {
                SafeLog.e(TAG, "Failed to send RLV reply on channel $channel", e)
            }
        } ?: run {
            SafeLog.w(TAG, "RLV reply not sent - ChatManager unavailable. Reply: $reply on channel $channel")
        }

        return RLVResult.Success
    }

    // Force command implementations

    private fun forceSit(target: String?, objectId: UUID): RLVResult {
        SafeLog.d(TAG, "Force sit on target: $target, objectId: $objectId")
        val targetUUID = if (target.isNullOrBlank()) {
            objectId
        } else {
            try {
                UUID.fromString(target)
            } catch (e: IllegalArgumentException) {
                SafeLog.w(TAG, "Invalid UUID for force sit: $target")
                return RLVResult.InvalidFormat
            }
        }

        sitManager?.invoke()?.sitOnObject(targetUUID)
        SafeLog.i(TAG, "Force sit executed on object $targetUUID")
        return RLVResult.Success
    }

    private fun forceUnsit(): RLVResult {
        SafeLog.d(TAG, "Force unsit")
        sitManager?.invoke()?.standUp()
        SafeLog.i(TAG, "Force unsit executed")
        return RLVResult.Success
    }

    private fun forceTeleport(coords: String?): RLVResult {
        SafeLog.d(TAG, "Force teleport to: $coords")
        if (!coords.isNullOrBlank()) {
            val parts = coords.split("/")
            if (parts.size >= 4) {
                val region = parts[0]
                val x = parts[1].toFloatOrNull() ?: 128f
                val y = parts[2].toFloatOrNull() ?: 128f
                val z = parts[3].toFloatOrNull() ?: 25f
                scope.launch { teleportManager?.invoke()?.teleportToLocation(region, x, y, z) }
            }
        }
        return RLVResult.Success
    }

    private fun forceAttach(target: String?): RLVResult {
        SafeLog.d(TAG, "Force attach: $target")
        if (!target.isNullOrBlank()) {
            val uuid = runCatching { UUID.fromString(target) }.getOrNull()
            if (uuid != null) {
                outfitManager?.invoke()?.let {
                    scope.launch { it.wearItem(uuid, replace = false) }
                }
            }
        }
        return RLVResult.Success
    }

    private fun forceDetach(target: String?): RLVResult {
        SafeLog.d(TAG, "Force detach: $target")
        if (!target.isNullOrBlank()) {
            val uuid = runCatching { UUID.fromString(target) }.getOrNull()
            if (uuid != null) {
                outfitManager?.invoke()?.let {
                    scope.launch { it.detachItem(uuid) }
                }
            }
        }
        return RLVResult.Success
    }

    private fun forceRemoveOutfit(layer: String?): RLVResult {
        SafeLog.d(TAG, "Force remove outfit layer: $layer")
        return RLVResult.Success
    }

    // Query implementations

    private fun getOutfitInfo(layer: String?): String {
        val worn = outfitManager?.invoke()?.getWornItems() ?: emptyList()
        return worn.joinToString(",") { it.toString() }
    }

    private fun getAttachInfo(point: String?): String {
        val pt = point?.toIntOrNull() ?: return ""
        val item = outfitManager?.invoke()?.getAttachmentAt(pt)
        return item?.toString() ?: ""
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
     * Get active restrictions grouped by object UUID.
     */
    fun getActiveRestrictionsGroupedByObject(): Map<UUID, List<RLVRestriction>> {
        return restrictions.values.groupBy { it.objectId }
    }

    /**
     * Clear session trust authorizations (resets on teleport or logout).
     */
    fun clearSessionTrust() {
        sessionTrust.clear()
        SafeLog.i(TAG, "Cleared RLV session trust list")
    }

    /**
     * Check if an object UUID is in the session trust list.
     */
    fun isSessionTrusted(objectId: UUID): Boolean {
        return sessionTrust[objectId] == true
    }

    /**
     * Register prompt handler for interactive confirmation prompts.
     */
    fun setPromptHandler(handler: RLVPromptHandler?) {
        this.promptHandler = handler
    }

    /**
     * Register notification listener for real-time Tier 1 soft restriction toasts.
     */
    fun registerNotificationListener(listener: RLVNotificationListener) {
        notificationListeners.add(listener)
    }

    /**
     * Unregister notification listener.
     */
    fun unregisterNotificationListener(listener: RLVNotificationListener) {
        notificationListeners.remove(listener)
    }

    private fun notifyNotificationListeners(
        objectId: UUID,
        objectName: String,
        command: String,
        option: String?,
        restricted: Boolean
    ) {
        notificationListeners.forEach { listener ->
            listener.onRestrictionChanged(objectId, objectName, command, option, restricted)
        }
    }

    /**
     * Clear all restrictions (e.g., on detach).
     */
    fun clearRestrictions(objectId: UUID) {
        val toRemove = restrictions.filter { it.value.objectId == objectId }.keys
        toRemove.forEach {
            restrictions.remove(it)
            notifyBehaviorChange(it.substringBefore(':'), false)
        }
        SafeLog.i(TAG, "Cleared ${toRemove.size} restrictions from $objectId")
    }

    /**
     * Clear all restrictions.
     */
    fun clearAllRestrictions() {
        restrictions.clear()
        exceptions.clear()
        SafeLog.i(TAG, "Cleared all RLV restrictions")
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
        clearSessionTrust()
    }
}

/**
 * RLV decision choices for confirmation prompts.
 */
enum class RLVDecision {
    APPROVE,
    DENY,
    ALWAYS_ALLOW_SESSION
}

/**
 * Interface for intercepting Tier 2 forced commands and displaying interactive confirmation prompts.
 */
fun interface RLVPromptHandler {
    fun onRequestApproval(
        promptId: String,
        objectId: UUID,
        objectName: String,
        action: String,
        option: String?,
        onDecision: (RLVDecision) -> Unit
    )
}

/**
 * Interface for real-time notification toasts for Tier 1 soft restrictions.
 */
fun interface RLVNotificationListener {
    fun onRestrictionChanged(
        objectId: UUID,
        objectName: String,
        command: String,
        option: String?,
        restricted: Boolean
    )
}

/**
 * RLV restriction data.
 */
data class RLVRestriction(
    val command: String,
    val objectId: UUID,
    val objectName: String = "Unknown Object",
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
