// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.render.avatar;

import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import com.lumiyaviewer.lumiya.slproto.types.LLQuaternion;
import javax.annotation.Nonnull;

class AvatarRunningAnimation implements Comparable<AvatarRunningAnimation>
{
    @Nonnull
    private final AnimationData.AnimationJointSet jointSet;
    @Nonnull
    private final AvatarRunningSequence sequence;
    
    AvatarRunningAnimation(@Nonnull final AvatarRunningSequence sequence, @Nonnull final AnimationData.AnimationJointSet jointSet) {
        this.sequence = sequence;
        this.jointSet = jointSet;
    }
    
    void animate(final AvatarSkeleton avatarSkeleton, final float[] array, final float[] array2, final LLQuaternion[] array3, final LLVector3[] array4) {
        this.jointSet.animate(avatarSkeleton, this.sequence, array, array2, array3, array4);
    }
    
    @Override
    public int compareTo(@Nonnull final AvatarRunningAnimation avatarRunningAnimation) {
        final int n = avatarRunningAnimation.jointSet.getPriority() - this.jointSet.getPriority();
        if (n != 0) {
            return n;
        }
        final int n2 = avatarRunningAnimation.sequence.sequenceID - this.sequence.sequenceID;
        if (n2 != 0) {
            return n2;
        }
        return 0;
    }
}
