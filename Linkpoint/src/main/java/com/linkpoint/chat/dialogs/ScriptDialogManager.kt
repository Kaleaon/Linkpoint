package com.linkpoint.chat.dialogs

import android.util.Log
import com.linkpoint.protocol.messages.MessageIds
import com.linkpoint.protocol.messages.UDPConnectionFixed
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

/**
 * Script Dialog Manager - Handles LSL script dialogs, text input boxes, and permission requests.
 * 
 * Based on the reference viewer's SLChatScriptDialog.java, SLChatTextBoxDialog.java, etc.
 * 
 * Dialog types:
 * - Script Dialog: Multi-button menu from llDialog()
 * - Text Box: Text input from llTextBox()
 * - Permission Request: Permission request from llRequestPermissions()
 * - Load URL: URL open request from llLoadURL()
 */
class ScriptDialogManager(
    private val udpConnection: UDPConnectionFixed,
    private val agentId: UUID
) {
    companion object {
        private const val TAG = "ScriptDialogManager"

        // Permission flags
        const val PERM_DEBIT = 0x0002
        const val PERM_TAKE_CONTROLS = 0x0004
        const val PERM_REMAP_CONTROLS = 0x0008
        const val PERM_TRIGGER_ANIMATION = 0x0010
        const val PERM_ATTACH = 0x0020
        const val PERM_RELEASE_OWNERSHIP = 0x0040
        const val PERM_CHANGE_LINKS = 0x0080
        const val PERM_CHANGE_JOINTS = 0x0100
        const val PERM_CHANGE_PERMISSIONS = 0x0200
        const val PERM_TRACK_CAMERA = 0x0400
        const val PERM_CONTROL_CAMERA = 0x0800
        const val PERM_TELEPORT = 0x1000
        const val PERM_EXPERIENCE = 0x2000
        const val PERM_SILENT_ESTATE_MANAGEMENT = 0x4000
        const val PERM_OVERRIDE_ANIMATIONS = 0x8000
        const val PERM_RETURN_OBJECTS = 0x10000
        
        // Max buttons in script dialog
        const val MAX_DIALOG_BUTTONS = 12
        
        // Dialog timeout (2 minutes)
        const val DIALOG_TIMEOUT_MS = 120_000L
    }
    
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    
    // Active dialogs
    private val activeDialogs = ConcurrentHashMap<Int, ScriptDialog>()
    private val activePermissionRequests = ConcurrentHashMap<UUID, PermissionRequest>()
    private val activeLoadUrls = ConcurrentHashMap<UUID, LoadUrlRequest>()
    
    // Dialog events
    private val _dialogEvents = MutableSharedFlow<DialogEvent>(replay = 0, extraBufferCapacity = 16)
    val dialogEvents: SharedFlow<DialogEvent> = _dialogEvents
    
    // Channel ID counter
    private var lastChannel = 0
    
    /**
     * Handle ScriptDialog message from server.
     */
    fun handleScriptDialog(payload: ByteArray) {
        try {
            val buffer = ByteBuffer.wrap(payload).order(ByteOrder.LITTLE_ENDIAN)
            
            // Data block
            val objectId = readUUID(buffer)
            
            // Read first name
            val firstNameLen = buffer.get().toInt() and 0xFF
            val firstNameBytes = ByteArray(firstNameLen)
            buffer.get(firstNameBytes)
            val firstName = String(firstNameBytes, Charsets.UTF_8).trim('\u0000')
            
            // Read last name
            val lastNameLen = buffer.get().toInt() and 0xFF
            val lastNameBytes = ByteArray(lastNameLen)
            buffer.get(lastNameBytes)
            val lastName = String(lastNameBytes, Charsets.UTF_8).trim('\u0000')
            
            // Read object name
            val objectNameLen = buffer.get().toInt() and 0xFF
            val objectNameBytes = ByteArray(objectNameLen)
            buffer.get(objectNameBytes)
            val objectName = String(objectNameBytes, Charsets.UTF_8).trim('\u0000')
            
            // Read message
            val messageLen = buffer.short.toInt() and 0xFFFF
            val messageBytes = ByteArray(messageLen)
            buffer.get(messageBytes)
            val message = String(messageBytes, Charsets.UTF_8).trim('\u0000')
            
            // Chat channel
            val chatChannel = buffer.int
            
            // Image ID
            val imageId = readUUID(buffer)
            
            // Read buttons (variable count)
            val buttons = mutableListOf<String>()
            while (buffer.remaining() > 0) {
                val buttonLen = buffer.get().toInt() and 0xFF
                if (buttonLen == 0) break
                val buttonBytes = ByteArray(buttonLen)
                buffer.get(buttonBytes)
                buttons.add(String(buttonBytes, Charsets.UTF_8).trim('\u0000'))
                if (buttons.size >= MAX_DIALOG_BUTTONS) break
            }
            
            val dialog = ScriptDialog(
                dialogId = chatChannel, // Use channel as dialog ID
                objectId = objectId,
                objectName = objectName,
                ownerName = "$firstName $lastName".trim(),
                message = message,
                buttons = buttons,
                imageId = if (imageId != UUID(0, 0)) imageId else null,
                chatChannel = chatChannel,
                timestamp = System.currentTimeMillis()
            )
            
            activeDialogs[chatChannel] = dialog
            lastChannel = chatChannel
            
            Log.d(TAG, "ScriptDialog received: ${dialog.objectName} - ${dialog.message}")
            
            scope.launch {
                _dialogEvents.emit(DialogEvent.DialogReceived(dialog))
            }
            
            // Schedule timeout
            scope.launch {
                delay(DIALOG_TIMEOUT_MS)
                activeDialogs.remove(chatChannel)
            }
            
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing ScriptDialog", e)
        }
    }
    
    /**
     * Handle ScriptQuestion (permission request) message.
     */
    fun handleScriptQuestion(payload: ByteArray) {
        try {
            val buffer = ByteBuffer.wrap(payload).order(ByteOrder.LITTLE_ENDIAN)
            
            // Data block
            val taskId = readUUID(buffer)
            val itemId = readUUID(buffer)
            
            // Read object name
            val objectNameLen = buffer.get().toInt() and 0xFF
            val objectNameBytes = ByteArray(objectNameLen)
            buffer.get(objectNameBytes)
            val objectName = String(objectNameBytes, Charsets.UTF_8).trim('\u0000')
            
            // Read owner name
            val ownerNameLen = buffer.get().toInt() and 0xFF
            val ownerNameBytes = ByteArray(ownerNameLen)
            buffer.get(ownerNameBytes)
            val ownerName = String(ownerNameBytes, Charsets.UTF_8).trim('\u0000')
            
            // Permissions requested
            val permissions = buffer.int
            
            val request = PermissionRequest(
                taskId = taskId,
                itemId = itemId,
                objectName = objectName,
                ownerName = ownerName,
                permissions = permissions,
                timestamp = System.currentTimeMillis()
            )
            
            activePermissionRequests[taskId] = request
            
            Log.d(TAG, "PermissionRequest received: ${request.objectName} - permissions=0x${permissions.toString(16)}")
            
            scope.launch {
                _dialogEvents.emit(DialogEvent.PermissionRequested(request))
            }
            
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing ScriptQuestion", e)
        }
    }
    
    /**
     * Handle LoadURL message.
     */
    fun handleLoadUrl(payload: ByteArray) {
        try {
            val buffer = ByteBuffer.wrap(payload).order(ByteOrder.LITTLE_ENDIAN)
            
            // Data block
            val objectId = readUUID(buffer)
            val ownerId = readUUID(buffer)
            
            val ownerIsGroup = buffer.get() != 0.toByte()
            
            // Read message
            val messageLen = buffer.short.toInt() and 0xFFFF
            val messageBytes = ByteArray(messageLen)
            buffer.get(messageBytes)
            val message = String(messageBytes, Charsets.UTF_8).trim('\u0000')
            
            // Read URL
            val urlLen = buffer.short.toInt() and 0xFFFF
            val urlBytes = ByteArray(urlLen)
            buffer.get(urlBytes)
            val url = String(urlBytes, Charsets.UTF_8).trim('\u0000')
            
            val request = LoadUrlRequest(
                objectId = objectId,
                ownerId = ownerId,
                ownerIsGroup = ownerIsGroup,
                message = message,
                url = url,
                timestamp = System.currentTimeMillis()
            )
            
            activeLoadUrls[objectId] = request
            
            Log.d(TAG, "LoadURL received: $message -> $url")
            
            scope.launch {
                _dialogEvents.emit(DialogEvent.UrlLoadRequested(request))
            }
            
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing LoadURL", e)
        }
    }
    
    /**
     * Send dialog reply (button click).
     */
    suspend fun replyToDialog(dialog: ScriptDialog, buttonText: String) {
        try {
            val buttonBytes = buttonText.toByteArray(Charsets.UTF_8)
            val payload = ByteBuffer.allocate(36 + buttonBytes.size).order(ByteOrder.LITTLE_ENDIAN)
            
            // AgentData
            writeUUID(payload, agentId)
            writeUUID(payload, udpConnection.getSessionId())
            
            // Data
            writeUUID(payload, dialog.objectId)
            payload.putInt(dialog.chatChannel)
            payload.putInt(-1) // ButtonIndex (we send text instead)
            payload.put(buttonBytes.size.toByte())
            payload.put(buttonBytes)
            
            udpConnection.sendPacket(MessageIds.SCRIPT_DIALOG_REPLY, payload.array().copyOf(payload.position()), reliable = true)
            
            activeDialogs.remove(dialog.chatChannel)
            Log.d(TAG, "Sent dialog reply: $buttonText to channel ${dialog.chatChannel}")
            
        } catch (e: Exception) {
            Log.e(TAG, "Failed to send dialog reply", e)
        }
    }
    
    /**
     * Ignore a dialog (don't respond).
     */
    fun ignoreDialog(dialog: ScriptDialog) {
        activeDialogs.remove(dialog.chatChannel)
    }
    
    /**
     * Grant permissions to a script.
     */
    suspend fun grantPermissions(request: PermissionRequest) {
        try {
            val payload = ByteBuffer.allocate(56).order(ByteOrder.LITTLE_ENDIAN)
            
            // AgentData
            writeUUID(payload, agentId)
            writeUUID(payload, udpConnection.getSessionId())
            
            // Data
            writeUUID(payload, request.taskId)
            writeUUID(payload, request.itemId)
            payload.putInt(request.permissions)
            
            udpConnection.sendPacket(MessageIds.SCRIPT_ANSWER_YES, payload.array().copyOf(payload.position()), reliable = true)
            
            activePermissionRequests.remove(request.taskId)
            Log.d(TAG, "Granted permissions to ${request.objectName}: 0x${request.permissions.toString(16)}")
            
        } catch (e: Exception) {
            Log.e(TAG, "Failed to grant permissions", e)
        }
    }
    
    /**
     * Deny permissions to a script.
     */
    fun denyPermissions(request: PermissionRequest) {
        // Just remove from active requests - no message needed
        activePermissionRequests.remove(request.taskId)
        Log.d(TAG, "Denied permissions to ${request.objectName}")
    }
    
    /**
     * Get human-readable permission descriptions.
     */
    fun getPermissionDescriptions(permissions: Int): List<String> {
        val descriptions = mutableListOf<String>()
        
        if (permissions and PERM_DEBIT != 0) descriptions.add("Take L$ from you")
        if (permissions and PERM_TAKE_CONTROLS != 0) descriptions.add("Take movement controls")
        if (permissions and PERM_TRIGGER_ANIMATION != 0) descriptions.add("Animate your avatar")
        if (permissions and PERM_ATTACH != 0) descriptions.add("Attach to you")
        if (permissions and PERM_TRACK_CAMERA != 0) descriptions.add("Track your camera")
        if (permissions and PERM_CONTROL_CAMERA != 0) descriptions.add("Control your camera")
        if (permissions and PERM_TELEPORT != 0) descriptions.add("Teleport you")
        if (permissions and PERM_OVERRIDE_ANIMATIONS != 0) descriptions.add("Override animations")
        
        return descriptions
    }
    
    private fun readUUID(buffer: ByteBuffer): UUID {
        val msb = buffer.long
        val lsb = buffer.long
        return UUID(msb, lsb)
    }
    
    private fun writeUUID(buffer: ByteBuffer, uuid: UUID) {
        buffer.putLong(uuid.mostSignificantBits)
        buffer.putLong(uuid.leastSignificantBits)
    }
    
    /**
     * Get active dialog count.
     */
    fun getActiveDialogCount(): Int = activeDialogs.size
    
    // ==================== ALERT MESSAGE HANDLERS ====================
    
    /**
     * Show a system-wide alert message (AlertMessage).
     * These are typically important server notifications.
     */
    fun showSystemAlert(message: String) {
        Log.w(TAG, "System Alert: $message")
        scope.launch {
            _dialogEvents.emit(DialogEvent.SystemAlert(message, modal = false))
        }
    }
    
    /**
     * Show an agent-specific alert message (AgentAlertMessage).
     * These may be modal (blocking) or non-modal.
     */
    fun showAgentAlert(message: String, modal: Boolean) {
        Log.w(TAG, "Agent Alert (modal=$modal): $message")
        scope.launch {
            _dialogEvents.emit(DialogEvent.SystemAlert(message, modal = modal))
        }
    }
    
    /**
     * Shutdown the manager.
     */
    fun shutdown() {
        scope.cancel()
        activeDialogs.clear()
        activePermissionRequests.clear()
        activeLoadUrls.clear()
    }
}

/**
 * Script dialog data.
 */
data class ScriptDialog(
    val dialogId: Int,
    val objectId: UUID,
    val objectName: String,
    val ownerName: String,
    val message: String,
    val buttons: List<String>,
    val imageId: UUID?,
    val chatChannel: Int,
    val timestamp: Long
)

/**
 * Permission request data.
 */
data class PermissionRequest(
    val taskId: UUID,
    val itemId: UUID,
    val objectName: String,
    val ownerName: String,
    val permissions: Int,
    val timestamp: Long
)

/**
 * Load URL request data.
 */
data class LoadUrlRequest(
    val objectId: UUID,
    val ownerId: UUID,
    val ownerIsGroup: Boolean,
    val message: String,
    val url: String,
    val timestamp: Long
)

/**
 * Dialog events.
 */
sealed class DialogEvent {
    data class DialogReceived(val dialog: ScriptDialog) : DialogEvent()
    data class PermissionRequested(val request: PermissionRequest) : DialogEvent()
    data class UrlLoadRequested(val request: LoadUrlRequest) : DialogEvent()
    data class SystemAlert(val message: String, val modal: Boolean) : DialogEvent()
}
