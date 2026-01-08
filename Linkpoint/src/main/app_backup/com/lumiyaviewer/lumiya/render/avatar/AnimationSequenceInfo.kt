package com.lumiyaviewer.lumiya.render.avatar

import java.util.UUID

open class AnimationSequenceInfo protected constructor(
    val animationID: UUID,
    val sequenceID: Int,
    val runningSince: Long,
    val stoppingSequenceID: Int,
    val stoppingRunningSince: Long,
    val stoppingEasingOutSince: Long,
    val dontEaseIn: Boolean,
) {
    fun hasStopped(currentTime: Long): Boolean {
        return sequenceID == INVALID_SEQUENCE_ID &&
            (
                stoppingSequenceID == INVALID_SEQUENCE_ID ||
                    currentTime >= stoppingEasingOutSince + MAX_ANIMATION_LENGTH
            )
    }

    companion object {
        const val INVALID_SEQUENCE_ID = 0
        const val INVALID_TIMESTAMP = -1L
        const val MAX_ANIMATION_LENGTH = 60000L

        @JvmStatic
        fun newSequence(
            animationID: UUID,
            currentTime: Long,
            sequenceID: Int,
        ): AnimationSequenceInfo {
            return AnimationSequenceInfo(
                animationID,
                sequenceID,
                currentTime,
                INVALID_SEQUENCE_ID,
                INVALID_TIMESTAMP,
                INVALID_TIMESTAMP,
                false,
            )
        }

        @JvmStatic
        fun restartSequence(
            currentTime: Long,
            newSequenceID: Int,
            previousInfo: AnimationSequenceInfo,
        ): AnimationSequenceInfo {
            return if (previousInfo.sequenceID != INVALID_SEQUENCE_ID) {
                AnimationSequenceInfo(
                    previousInfo.animationID,
                    newSequenceID,
                    currentTime,
                    previousInfo.sequenceID,
                    previousInfo.runningSince,
                    currentTime,
                    true,
                )
            } else {
                AnimationSequenceInfo(
                    previousInfo.animationID,
                    newSequenceID,
                    currentTime,
                    previousInfo.stoppingSequenceID,
                    previousInfo.stoppingRunningSince,
                    previousInfo.stoppingRunningSince,
                    true,
                )
            }
        }

        @JvmStatic
        fun stopSequence(
            currentTime: Long,
            previousInfo: AnimationSequenceInfo,
        ): AnimationSequenceInfo? {
            if (previousInfo.sequenceID == INVALID_SEQUENCE_ID) {
                return null
            }
            return AnimationSequenceInfo(
                previousInfo.animationID,
                INVALID_SEQUENCE_ID,
                INVALID_TIMESTAMP,
                previousInfo.sequenceID,
                previousInfo.runningSince,
                currentTime,
                previousInfo.dontEaseIn,
            )
        }
    }
}
