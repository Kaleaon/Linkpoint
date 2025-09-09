package com.lumiyaviewer.lumiya.render.avatar;

import com.lumiyaviewer.lumiya.render.avatar.AnimationData;
import com.lumiyaviewer.lumiya.slproto.types.LLQuaternion;
import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import javax.annotation.Nonnull;

class AvatarRunningAnimation implements Comparable<AvatarRunningAnimation> {
    @Nonnull
    private final AnimationData.AnimationJointSet jointSet;
    @Nonnull
    private final AvatarRunningSequence sequence;

    AvatarRunningAnimation(@Nonnull AvatarRunningSequence avatarRunningSequence, @Nonnull AnimationData.AnimationJointSet animationJointSet) {
        this.sequence = avatarRunningSequence;
        this.jointSet = animationJointSet;
    }

    /* access modifiers changed from: package-private */
    public void animate(AvatarSkeleton avatarSkeleton, float[] fArr, float[] fArr2, LLQuaternion[] lLQuaternionArr, LLVector3[] lLVector3Arr) {
        this.jointSet.animate(avatarSkeleton, this.sequence, fArr, fArr2, lLQuaternionArr, lLVector3Arr);
    }

    public int compareTo(@Nonnull AvatarRunningAnimation avatarRunningAnimation) {
        int priority = avatarRunningAnimation.jointSet.getPriority() - this.jointSet.getPriority();
        if (priority != 0) {
            return priority;
        }
        int i = avatarRunningAnimation.sequence.sequenceID - this.sequence.sequenceID;
        if (i != 0) {
            return i;
        }
        return 0;
    }
}
