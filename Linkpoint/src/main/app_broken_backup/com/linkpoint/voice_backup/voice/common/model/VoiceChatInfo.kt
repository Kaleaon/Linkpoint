package com.linkpoint.voice.common.model

import android.os.Bundle
import com.google.common.collect.Interner
import com.google.common.collect.Interners
import java.util.UUID
import javax.annotation.Nonnull
import javax.annotation.Nullable

class VoiceChatInfo private constructor(
    val state: VoiceChatState,
    val previousState: VoiceChatState,
    val numActiveSpeakers: Int,
    val activeSpeakerID: UUID?,
    val isConference: Boolean,
    val localMicActive: Boolean
) {
    private constructor(bundle: Bundle) : this(
        VoiceChatState.valueOf(bundle.getString("state") ?: "None"),
        VoiceChatState.valueOf(bundle.getString("previousState") ?: "None"),
        bundle.getInt("numActiveSpeakers"),
        bundle.getString("activeSpeakerID")?.let { UUID.fromString(it) },
        bundle.getBoolean("isConference"),
        bundle.getBoolean("localMicActive")
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        
        val that = other as VoiceChatInfo
        if (numActiveSpeakers != that.numActiveSpeakers) return false
        if (isConference != that.isConference) return false
        if (localMicActive != that.localMicActive) return false
        if (state != that.state) return false
        if (previousState != that.previousState) return false
        return if (activeSpeakerID != null) {
            activeSpeakerID == that.activeSpeakerID
        } else {
            that.activeSpeakerID == null
        }
    }

    override fun hashCode(): Int {
        var result = state.hashCode()
        result = 31 * result + previousState.hashCode()
        result = 31 * result + numActiveSpeakers
        result = 31 * result + (activeSpeakerID?.hashCode() ?: 0)
        result = 31 * result + if (isConference) 1 else 0
        result = 31 * result + if (localMicActive) 1 else 0
        return result
    }

    fun toBundle(): Bundle {
        val bundle = Bundle()
        bundle.putString("state", state.toString())
        bundle.putString("previousState", previousState.toString())
        bundle.putInt("numActiveSpeakers", numActiveSpeakers)
        bundle.putString("activeSpeakerID", activeSpeakerID?.toString())
        bundle.putBoolean("isConference", isConference)
        bundle.putBoolean("localMicActive", localMicActive)
        return bundle
    }

    override fun toString(): String {
        return "VoiceChatInfo{" +
                "state=$state" +
                ", previousState=$previousState" +
                ", numActiveSpeakers=$numActiveSpeakers" +
                ", activeSpeakerID=$activeSpeakerID" +
                ", isConference=$isConference" +
                ", localMicActive=$localMicActive" +
                '}'
    }

    enum class VoiceChatState {
        None,
        Ringing,
        Connecting,
        Active
    }

    companion object {
        private val interner: Interner<VoiceChatInfo> = Interners.newWeakInterner()
        private val emptyChatState: VoiceChatInfo = interner.intern(
            VoiceChatInfo(VoiceChatState.None, VoiceChatState.None, 0, null, false, false)
        )

        @JvmStatic
        fun create(bundle: Bundle): VoiceChatInfo {
            return interner.intern(VoiceChatInfo(bundle))
        }

        @JvmStatic
        fun create(
            state: VoiceChatState,
            previousState: VoiceChatState,
            numActiveSpeakers: Int,
            activeSpeakerID: UUID?,
            isConference: Boolean,
            localMicActive: Boolean
        ): VoiceChatInfo {
            return interner.intern(
                VoiceChatInfo(state, previousState, numActiveSpeakers, activeSpeakerID, isConference, localMicActive)
            )
        }

        @JvmStatic
        fun empty(): VoiceChatInfo {
            return emptyChatState
        }
    }
}