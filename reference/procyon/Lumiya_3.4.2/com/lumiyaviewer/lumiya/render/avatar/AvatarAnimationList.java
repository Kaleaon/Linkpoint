// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.render.avatar;

import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import com.lumiyaviewer.lumiya.slproto.types.LLQuaternion;
import java.util.Iterator;
import java.util.List;
import java.util.Collections;
import java.util.ArrayList;
import java.util.Collection;
import javax.annotation.Nonnull;
import com.google.common.collect.ImmutableList;

class AvatarAnimationList
{
    @Nonnull
    private final ImmutableList<AvatarRunningAnimation> animations;
    @Nonnull
    private final ImmutableList<AvatarRunningSequence> sequences;
    
    AvatarAnimationList(final Collection<AvatarAnimationState> collection) {
        final ArrayList list = new ArrayList<AvatarRunningAnimation>(collection.size());
        final ImmutableList.Builder<AvatarRunningSequence> builder = ImmutableList.builder();
        final Iterator<Object> iterator = collection.iterator();
        while (iterator.hasNext()) {
            iterator.next().getRunningAnimations(builder, (Collection<AvatarRunningAnimation>)list);
        }
        Collections.sort((List<Comparable>)list);
        this.sequences = builder.build();
        this.animations = ImmutableList.copyOf((Collection<? extends AvatarRunningAnimation>)list);
    }
    
    void animate(final AvatarSkeleton avatarSkeleton, final float[] array, final float[] array2, final LLQuaternion[] array3, final LLVector3[] array4) {
        final Iterator<Object> iterator = this.animations.iterator();
        while (iterator.hasNext()) {
            iterator.next().animate(avatarSkeleton, array, array2, array3, array4);
        }
        for (int length = array.length, i = 0; i < length; ++i) {
            final float n = 1.0f - array[i];
            if (n > 0.01f && n < 1.0f) {
                final float n2 = 1.0f / n;
                final LLQuaternion llQuaternion = array3[i];
                llQuaternion.x *= n2;
                final LLQuaternion llQuaternion2 = array3[i];
                llQuaternion2.y *= n2;
                final LLQuaternion llQuaternion3 = array3[i];
                llQuaternion3.z *= n2;
                final LLQuaternion llQuaternion4 = array3[i];
                llQuaternion4.w *= n2;
            }
        }
    }
    
    boolean needAnimate(final long n) {
        final Iterator<Object> iterator = this.sequences.iterator();
        boolean b = false;
        while (iterator.hasNext()) {
            b |= iterator.next().needAnimate(n);
        }
        return b;
    }
}
