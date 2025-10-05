package com.linkpoint.slproto.users.manager

import com.linkpoint.slproto.users.ParcelData
import com.linkpoint.voice.common.model.VoiceChannelInfo

abstract class CurrentLocationInfo {
    abstract fun inChatRangeUsers(): Int
    abstract fun nearbyUsers(): Int
    abstract fun parcelData(): ParcelData?
    abstract fun parcelVoiceChannel(): VoiceChannelInfo?

    companion object {
        @JvmStatic
        fun create(
            parcelData: ParcelData?,
            nearbyUsers: Int,
            inChatRangeUsers: Int,
            voiceChannelInfo: VoiceChannelInfo?
        ): CurrentLocationInfo {
            return AutoValue_CurrentLocationInfo(parcelData, nearbyUsers, inChatRangeUsers, voiceChannelInfo)
        }
    }
}