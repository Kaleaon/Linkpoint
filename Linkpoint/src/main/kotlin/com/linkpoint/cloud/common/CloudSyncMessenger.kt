package com.linkpoint.cloud.common

import android.os.Bundle
import android.os.Message
import android.os.Messenger
import android.os.RemoteException

object CloudSyncMessenger {
    @JvmStatic
    fun sendMessage(messenger: Messenger?, messageType: MessageType, bundleable: Bundleable, replyMessenger: Messenger?): Boolean {
        if (messenger == null) {
            return false
        }
        
        val bundle = Bundle().apply {
            putString("messageType", messageType.toString())
            putBundle("message", bundleable.toBundle())
        }
        
        val message = Message.obtain(null, 100, bundle).apply {
            replyTo = replyMessenger
        }
        
        return try {
            messenger.send(message)
            true
        } catch (e: RemoteException) {
            false
        }
    }
}