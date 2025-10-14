package com.lumiyaviewer.lumiya.voice.common.model

import android.os.Bundle
import com.google.common.collect.Interners
import java.util.*

/**
 * Modern Kotlin VoiceChatInfo data class
 * Represents the current state of a voice chat session
 */
data class VoiceChatInfo private constructor(
    val state: VoiceChatState,
    val previousState: VoiceChatState,
    val numActiveSpeakers: Int,
    val activeSpeakerID: UUID?,
    val isConference: Boolean,
    val localMicActive: Boolean,
) {
    /**
     * Voice chat state enum
     */
    enum class VoiceChatState {
        None,
        Ringing,
        Connecting,
        Active,
    }

    companion object {
        // Interner for memory efficiency (reuses identical instances)
        private val interner = Interners.newWeakInterner<VoiceChatInfo>()

        // Empty/default chat state
        private val emptyChatState =
            interner.intern(
                VoiceChatInfo(
                    state = VoiceChatState.None,
                    previousState = VoiceChatState.None,
                    numActiveSpeakers = 0,
                    activeSpeakerID = null,
                    isConference = false,
                    localMicActive = false,
                ),
            )

        /**
         * Creates a VoiceChatInfo instance (interned for memory efficiency)
         */
        @JvmStatic
        fun create(
            state: VoiceChatState,
            previousState: VoiceChatState,
            numActiveSpeakers: Int,
            activeSpeakerID: UUID?,
            isConference: Boolean,
            localMicActive: Boolean,
        ): VoiceChatInfo {
            return interner.intern(
                VoiceChatInfo(
                    state,
                    previousState,
                    numActiveSpeakers,
                    activeSpeakerID,
                    isConference,
                    localMicActive,
                ),
            )
        }

        /**
         * Creates a VoiceChatInfo from a Bundle
         */
        @JvmStatic
        fun create(bundle: Bundle): VoiceChatInfo {
            val state = VoiceChatState.valueOf(bundle.getString("state") ?: "None")
            val previousState = VoiceChatState.valueOf(bundle.getString("previousState") ?: "None")
            val numActiveSpeakers = bundle.getInt("numActiveSpeakers")
            val activeSpeakerID = bundle.getString("activeSpeakerID")?.let { UUID.fromString(it) }
            val isConference = bundle.getBoolean("isConference")
            val localMicActive = bundle.getBoolean("localMicActive")

            return interner.intern(
                VoiceChatInfo(
                    state,
                    previousState,
                    numActiveSpeakers,
                    activeSpeakerID,
                    isConference,
                    localMicActive,
                ),
            )
        }

        /**
         * Returns an empty/default chat state
         */
        @JvmStatic
        fun empty(): VoiceChatInfo = emptyChatState
    }

    /**
     * Converts this chat info to a Bundle
     */
    fun toBundle(): Bundle =
        Bundle().apply {
            putString("state", state.toString())
            putString("previousState", previousState.toString())
            putInt("numActiveSpeakers", numActiveSpeakers)
            putString("activeSpeakerID", activeSpeakerID?.toString())
            putBoolean("isConference", isConference)
            putBoolean("localMicActive", localMicActive)
        }

    /**
     * String representation
     */
    override fun toString(): String {
        return "VoiceChatInfo{state=$state, previousState=$previousState, " +
            "numActiveSpeakers=$numActiveSpeakers, activeSpeakerID=$activeSpeakerID, " +
            "isConference=$isConference, localMicActive=$localMicActive}"
    }
}
