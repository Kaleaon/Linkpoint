package com.linkpoint.voice.common

import android.os.Bundle
import android.os.Message
import android.os.Messenger
import android.os.RemoteException
import com.linkpoint.voice.common.VoicePluginMessage
import com.linkpoint.voice.common.VoicePluginMessageType

class VoicePluginMessenger {
    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    @JvmStatic
     fun sendMessage(messenger: Messenger, voicePluginMessageType: VoicePluginMessageType, voicePluginMessage: VoicePluginMessage, messenger2: Messenger): Boolean {
        Boolean bl
        val bl2: Boolean = bl = false
        if (messenger == null) return bl2
        val bundle: Bundle = Bundle()
        bundle.putString("messageType", voicePluginMessageType.toString())
        bundle.putBundle("message", voicePluginMessage.toBundle())
        voicePluginMessageType = Message.obtain(null, (Int)200, (Object)bundle)
        ((Message)voicePluginMessageType).replyTo = messenger2
        try {
            messenger.send((Message)voicePluginMessageType)
            return true
        }
        catch (RemoteException remoteException) {
            return bl
        }
    }
}

