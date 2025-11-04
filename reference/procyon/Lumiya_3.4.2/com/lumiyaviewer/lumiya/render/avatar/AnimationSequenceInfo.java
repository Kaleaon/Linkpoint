// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.render.avatar;

import javax.annotation.Nullable;
import javax.annotation.Nonnull;
import java.util.UUID;

public class AnimationSequenceInfo
{
    protected static final int INVALID_SEQUENCE_ID = 0;
    protected static final long INVALID_TIMESTAMP = -1L;
    public static final long MAX_ANIMATION_LENGTH = 60000L;
    @Nonnull
    protected final UUID animationID;
    protected final boolean dontEaseIn;
    protected final long runningSince;
    protected final int sequenceID;
    protected final long stoppingEasingOutSince;
    protected final long stoppingRunningSince;
    protected final int stoppingSequenceID;
    
    private AnimationSequenceInfo(@Nonnull final UUID animationID, final int sequenceID, final long runningSince, final int stoppingSequenceID, final long stoppingRunningSince, final long stoppingEasingOutSince, final boolean dontEaseIn) {
        this.animationID = animationID;
        this.sequenceID = sequenceID;
        this.runningSince = runningSince;
        this.stoppingSequenceID = stoppingSequenceID;
        this.stoppingRunningSince = stoppingRunningSince;
        this.stoppingEasingOutSince = stoppingEasingOutSince;
        this.dontEaseIn = dontEaseIn;
    }
    
    @Nonnull
    public static AnimationSequenceInfo newSequence(@Nonnull final UUID uuid, final long n, final int n2) {
        return new AnimationSequenceInfo(uuid, n2, n, 0, -1L, -1L, false);
    }
    
    @Nonnull
    public static AnimationSequenceInfo restartSequence(final long n, final int n2, @Nonnull final AnimationSequenceInfo animationSequenceInfo) {
        if (animationSequenceInfo.sequenceID != 0) {
            return new AnimationSequenceInfo(animationSequenceInfo.animationID, n2, n, animationSequenceInfo.sequenceID, animationSequenceInfo.runningSince, n, true);
        }
        return new AnimationSequenceInfo(animationSequenceInfo.animationID, n2, n, animationSequenceInfo.stoppingSequenceID, animationSequenceInfo.stoppingRunningSince, animationSequenceInfo.stoppingRunningSince, true);
    }
    
    @Nullable
    public static AnimationSequenceInfo stopSequence(final long n, @Nonnull final AnimationSequenceInfo animationSequenceInfo) {
        if (animationSequenceInfo.sequenceID != 0) {
            return new AnimationSequenceInfo(animationSequenceInfo.animationID, 0, -1L, animationSequenceInfo.sequenceID, animationSequenceInfo.runningSince, n, animationSequenceInfo.dontEaseIn);
        }
        return null;
    }
    
    public boolean hasStopped(final long n) {
        final boolean b = true;
        if (this.sequenceID != 0) {
            return false;
        }
        boolean b2 = b;
        if (this.stoppingSequenceID != 0) {
            if (n < this.stoppingEasingOutSince + 60000L) {
                return false;
            }
            b2 = b;
        }
        return b2;
        b2 = false;
        return b2;
    }
}
