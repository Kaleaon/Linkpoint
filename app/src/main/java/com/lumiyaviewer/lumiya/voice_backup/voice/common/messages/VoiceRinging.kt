package com.lumiyaviewer.lumiya.voice.common.messages

import android.net.Uri
import android.os.Bundle
import com.lumiyaviewer.lumiya.voice.common.VoicePluginMessage
import com.lumiyaviewer.lumiya.voice.common.model.VoiceChannelInfo
import java.util.UUID
import javax.annotation.Nonnull
import javax.annotation.Nullable

class VoiceRinging(
    val sessionHandle: String?,
    val voiceChannelInfo: VoiceChannelInfo,
    val agentUUID: UUID?
) : VoicePluginMessage {
    
    constructor(uri: Uri) : this(
        uri.getQueryParameter("sessionHandle"),
        VoiceChannelInfo(uri),
        uri.getQueryParameter("agentUUID")?.let { UUID.fromString(it) }
    )

    constructor(bundle: Bundle) : this(
        bundle.getString("sessionHandle"),
        VoiceChannelInfo(bundle.getBundle("voiceChannelInfo")!!),
        bundle.getString("agentUUID")?.let { UUID.fromString(it) }
    )

    override fun toBundle(): Bundle {
        val bundle = Bundle()
        bundle.putString("sessionHandle", sessionHandle)
        bundle.putBundle("voiceChannelInfo", voiceChannelInfo.toBundle())
        bundle.putString("agentUUID", agentUUID?.toString())
        return bundle
    }

    fun toUri(): Uri {
        val builder = Uri.Builder()
            .scheme("com.linkpoint")
            .authority("voice")
            .appendQueryParameter("sessionHandle", sessionHandle)
        
        agentUUID?.let {
            builder.appendQueryParameter("agentUUID", it.toString())
        }
        
        voiceChannelInfo.appendToUri(builder)
        return builder.build()
    }
}