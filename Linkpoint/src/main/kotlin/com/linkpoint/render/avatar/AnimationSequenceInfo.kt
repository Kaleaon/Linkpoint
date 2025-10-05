package com.linkpoint.render.avatar

import java.util.UUID
import javax.annotation.Nonnull
import javax.annotation.Nullable

class AnimationSequenceInfo {
    protected const val INVALID_SEQUENCE_ID: Int = 0
    protected const val INVALID_TIMESTAMP: Long = -1
    const val MAX_ANIMATION_LENGTH: Long = 60000
    protected val UUID animationID
    protected val Boolean dontEaseIn
    protected val Long runningSince
    protected val Int sequenceID
    protected val Long stoppingEasingOutSince
    protected val Long stoppingRunningSince
    protected val Int stoppingSequenceID

    private AnimationSequenceInfo(UUID uuid, Int i, Long j, Int i2, Long j2, Long j3, Boolean z) {
        this.animationID = uuid
        this.sequenceID = i
        this.runningSince = j
        this.stoppingSequenceID = i2
        this.stoppingRunningSince = j2
        this.stoppingEasingOutSince = j3
        this.dontEaseIn = z
    }

    @JvmStatic
    AnimationSequenceInfo newSequence(UUID uuid, Long j, Int i) {
        return AnimationSequenceInfo(uuid, i, j, 0, -1, -1, false)
    }

    @JvmStatic
    AnimationSequenceInfo restartSequence(Long j, Int i, AnimationSequenceInfo animationSequenceInfo) {
        if (animationSequenceInfo.sequenceID != 0) {
            return AnimationSequenceInfo(animationSequenceInfo.animationID, i, j, animationSequenceInfo.sequenceID, animationSequenceInfo.runningSince, j, true)
        }
        return AnimationSequenceInfo(animationSequenceInfo.animationID, i, j, animationSequenceInfo.stoppingSequenceID, animationSequenceInfo.stoppingRunningSince, animationSequenceInfo.stoppingRunningSince, true)
    }

    @JvmStatic
    AnimationSequenceInfo stopSequence(Long j, AnimationSequenceInfo animationSequenceInfo) {
        if (animationSequenceInfo.sequenceID == 0) {
            return null
        }
        return AnimationSequenceInfo(animationSequenceInfo.animationID, 0, -1, animationSequenceInfo.sequenceID, animationSequenceInfo.runningSince, j, animationSequenceInfo.dontEaseIn)
    }

    public Boolean hasStopped(Long j) {
        return this.sequenceID == 0 && (this.stoppingSequenceID == 0 || j >= this.stoppingEasingOutSince + MAX_ANIMATION_LENGTH)
    }
}
