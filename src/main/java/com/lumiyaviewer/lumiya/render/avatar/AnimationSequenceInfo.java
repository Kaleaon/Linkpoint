package com.lumiyaviewer.lumiya.render.avatar;

import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class AnimationSequenceInfo {
    protected static final int INVALID_SEQUENCE_ID = 0;
    protected static final long INVALID_TIMESTAMP = -1;
    public static final long MAX_ANIMATION_LENGTH = 60000;
    @Nonnull
    protected final UUID animationID;
    protected final boolean dontEaseIn;
    protected final long runningSince;
    protected final int sequenceID;
    protected final long stoppingEasingOutSince;
    protected final long stoppingRunningSince;
    protected final int stoppingSequenceID;

    private AnimationSequenceInfo(@Nonnull UUID uuid, int i, long j, int i2, long j2, long j3, boolean z) {
        this.animationID = uuid;
        this.sequenceID = i;
        this.runningSince = j;
        this.stoppingSequenceID = i2;
        this.stoppingRunningSince = j2;
        this.stoppingEasingOutSince = j3;
        this.dontEaseIn = z;
    }

    @Nonnull
    public static AnimationSequenceInfo newSequence(@Nonnull UUID uuid, long j, int i) {
        return new AnimationSequenceInfo(uuid, i, j, 0, -1, -1, false);
    }

    @Nonnull
    public static AnimationSequenceInfo restartSequence(long j, int i, @Nonnull AnimationSequenceInfo animationSequenceInfo) {
        if (animationSequenceInfo.sequenceID != 0) {
            return new AnimationSequenceInfo(animationSequenceInfo.animationID, i, j, animationSequenceInfo.sequenceID, animationSequenceInfo.runningSince, j, true);
        }
        return new AnimationSequenceInfo(animationSequenceInfo.animationID, i, j, animationSequenceInfo.stoppingSequenceID, animationSequenceInfo.stoppingRunningSince, animationSequenceInfo.stoppingRunningSince, true);
    }

    @Nullable
    public static AnimationSequenceInfo stopSequence(long j, @Nonnull AnimationSequenceInfo animationSequenceInfo) {
        if (animationSequenceInfo.sequenceID == 0) {
            return null;
        }
        return new AnimationSequenceInfo(animationSequenceInfo.animationID, 0, -1, animationSequenceInfo.sequenceID, animationSequenceInfo.runningSince, j, animationSequenceInfo.dontEaseIn);
    }

    public boolean hasStopped(long j) {
        return this.sequenceID == 0 && (this.stoppingSequenceID == 0 || j >= this.stoppingEasingOutSince + MAX_ANIMATION_LENGTH);
    }
}
