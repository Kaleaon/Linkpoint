package com.lumiyaviewer.lumiya.cloud.common

import android.os.Bundle
import android.os.Message
import android.os.Messenger
import android.os.RemoteException

/**
 * Modern Kotlin CloudSyncMessenger utility
 * Handles sending messages for cloud synchronization
 */
object CloudSyncMessenger {
    
    /**
     * Sends a message to a cloud sync service
     * @param messenger The target messenger
     * @param messageType The type of message to send
     * @param bundleable The message content
     * @param replyTo Optional messenger for replies
     * @return true if message was sent successfully, false otherwise
     */
    @JvmStatic
    fun sendMessage(
        messenger: Messenger?,
        messageType: MessageType,
        bundleable: Bundleable,
        replyTo: Messenger? = null
    ): Boolean {
        if (messenger == null) return false
        
        return try {
            val bundle = Bundle().apply {
                putString("messageType", messageType.toString())
                putBundle("message", bundleable.toBundle())
            }
            
            val message = Message.obtain(null, MessageType.CLOUD_PLUGIN_MESSAGE, bundle).apply {
                this.replyTo = replyTo
            }
            
            messenger.send(message)
            true
        } catch (e: RemoteException) {
            false
        }
    }
}