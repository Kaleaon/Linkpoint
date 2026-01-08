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
        replyTo: Messenger? = null,
    ): Boolean {
        if (messenger == null) return false

        return try {
            val bundle =
                Bundle().apply {
                    putString("messageType", messageType.toString())
                    putBundle("message", bundleable.toBundle())
                }

            val message =
                Message.obtain(null, messageType.ordinal, bundle).apply {
                    this.replyTo = replyTo
                }

            messenger.send(message)
            true
        } catch (e: RemoteException) {
            false
        }
    }

    inline fun <reified T : Bundleable> extractMessage(msg: Message): T? {
        val bundle = msg.obj as? Bundle ?: return null
        bundle.classLoader = T::class.java.classLoader
        val messageBundle = bundle.getBundle("message") ?: return null
        
        return try {
            T::class.java.getConstructor(Bundle::class.java).newInstance(messageBundle)
        } catch (e: Exception) {
            null
        }
    }
}
