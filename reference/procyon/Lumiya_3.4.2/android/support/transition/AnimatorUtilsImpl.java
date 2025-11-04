// 
// Decompiled by Procyon v0.6.0
// 

package android.support.transition;

import android.animation.AnimatorListenerAdapter;
import android.support.annotation.NonNull;
import android.animation.Animator;

interface AnimatorUtilsImpl
{
    void addPauseListener(@NonNull final Animator p0, @NonNull final AnimatorListenerAdapter p1);
    
    void pause(@NonNull final Animator p0);
    
    void resume(@NonNull final Animator p0);
}
