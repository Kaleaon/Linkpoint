// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.render.avatar;

import com.google.common.collect.ImmutableList;
import com.lumiyaviewer.lumiya.slproto.types.LLQuaternion;
import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

// Referenced classes of package com.lumiyaviewer.lumiya.render.avatar:
//            AvatarAnimationState, AvatarRunningAnimation, AvatarRunningSequence, AvatarSkeleton

class AvatarAnimationList
{

    private final ImmutableList animations;
    private final ImmutableList sequences;

    AvatarAnimationList(Collection collection)
    {
        ArrayList arraylist = new ArrayList(collection.size());
        com.google.common.collect.ImmutableList.Builder builder = ImmutableList.builder();
        for (collection = collection.iterator(); collection.hasNext(); ((AvatarAnimationState)collection.next()).getRunningAnimations(builder, arraylist)) { }
        Collections.sort(arraylist);
        sequences = builder.build();
        animations = ImmutableList.copyOf(arraylist);
    }

    void animate(AvatarSkeleton avatarskeleton, float af[], float af1[], LLQuaternion allquaternion[], LLVector3 allvector3[])
    {
        for (Iterator iterator = animations.iterator(); iterator.hasNext(); ((AvatarRunningAnimation)iterator.next()).animate(avatarskeleton, af, af1, allquaternion, allvector3)) { }
        int j = af.length;
        for (int i = 0; i < j; i++)
        {
            float f = 1.0F - af[i];
            if (f > 0.01F && f < 1.0F)
            {
                f = 1.0F / f;
                avatarskeleton = allquaternion[i];
                avatarskeleton.x = ((LLQuaternion) (avatarskeleton)).x * f;
                avatarskeleton = allquaternion[i];
                avatarskeleton.y = ((LLQuaternion) (avatarskeleton)).y * f;
                avatarskeleton = allquaternion[i];
                avatarskeleton.z = ((LLQuaternion) (avatarskeleton)).z * f;
                avatarskeleton = allquaternion[i];
                avatarskeleton.w = f * ((LLQuaternion) (avatarskeleton)).w;
            }
        }

    }

    boolean needAnimate(long l)
    {
        Iterator iterator = sequences.iterator();
        boolean flag;
        for (flag = false; iterator.hasNext(); flag = ((AvatarRunningSequence)iterator.next()).needAnimate(l) | flag) { }
        return flag;
    }
}
