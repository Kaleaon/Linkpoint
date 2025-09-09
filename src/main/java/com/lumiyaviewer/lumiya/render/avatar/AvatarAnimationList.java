package com.lumiyaviewer.lumiya.render.avatar;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.lumiyaviewer.lumiya.slproto.types.LLQuaternion;
import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import javax.annotation.Nonnull;

class AvatarAnimationList {
    @Nonnull
    private final ImmutableList<AvatarRunningAnimation> animations;
    @Nonnull
    private final ImmutableList<AvatarRunningSequence> sequences;

    AvatarAnimationList(Collection<AvatarAnimationState> collection) {
        Collection arrayList = new ArrayList(collection.size());
        Builder builder = ImmutableList.builder();
        for (AvatarAnimationState runningAnimations : collection) {
            runningAnimations.getRunningAnimations(builder, arrayList);
        }
        Collections.sort(arrayList);
        this.sequences = builder.build();
        this.animations = ImmutableList.copyOf(arrayList);
    }

    void animate(AvatarSkeleton avatarSkeleton, float[] fArr, float[] fArr2, LLQuaternion[] lLQuaternionArr, LLVector3[] lLVector3Arr) {
        for (AvatarRunningAnimation animate : this.animations) {
            animate.animate(avatarSkeleton, fArr, fArr2, lLQuaternionArr, lLVector3Arr);
        }
        int length = fArr.length;
        for (int i = 0; i < length; i++) {
            float f = 1.0f - fArr[i];
            if (f > 0.01f && f < 1.0f) {
                f = 1.0f / f;
                LLQuaternion lLQuaternion = lLQuaternionArr[i];
                lLQuaternion.x *= f;
                lLQuaternion = lLQuaternionArr[i];
                lLQuaternion.y *= f;
                lLQuaternion = lLQuaternionArr[i];
                lLQuaternion.z *= f;
                lLQuaternion = lLQuaternionArr[i];
                lLQuaternion.w = f * lLQuaternion.w;
            }
        }
    }

    boolean needAnimate(long j) {
        boolean z = false;
        Iterator it = this.sequences.iterator();
        while (true) {
            boolean z2 = z;
            if (!it.hasNext()) {
                return z2;
            }
            z = ((AvatarRunningSequence) it.next()).needAnimate(j) | z2;
        }
    }
}
