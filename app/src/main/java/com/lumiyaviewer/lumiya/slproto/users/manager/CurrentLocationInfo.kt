package com.lumiyaviewer.lumiya.slproto.users.manager

import com.lumiyaviewer.lumiya.slproto.users.ParcelData
import com.lumiyaviewer.lumiya.voice.common.model.VoiceChannelInfo

data class CurrentLocationInfo(
    val parcelData: ParcelData?,
    val inChatRangeUsers: Int,
    val nearbyUsers: Int,
    val parcelVoiceChannel: VoiceChannelInfo?,
) {
    companion object {
        @JvmStatic
        fun create(
            parcelData: ParcelData?,
            i: Int,
            i2: Int,
            voiceChannelInfo: VoiceChannelInfo?,
        ): CurrentLocationInfo {
            return CurrentLocationInfo(parcelData, i, i2, voiceChannelInfo)
        }
    }
}