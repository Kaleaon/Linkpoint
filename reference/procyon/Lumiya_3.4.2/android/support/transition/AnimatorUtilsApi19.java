// 
// Decompiled by Procyon v0.6.0
// 

package android.support.transition;

import android.animation.Animator$AnimatorPauseListener;
import android.animation.AnimatorListenerAdapter;
import android.support.annotation.NonNull;
import android.animation.Animator;
import android.support.annotation.RequiresApi;

@RequiresApi(19)
class AnimatorUtilsApi19 implements AnimatorUtilsImpl
{
    @Override
    public void addPauseListener(@NonNull final Animator animator, @NonNull final AnimatorListenerAdapter animatorListenerAdapter) {
        animator.addPauseListener((Animator$AnimatorPauseListener)animatorListenerAdapter);
    }
    
    @Override
    public void pause(@NonNull final Animator animator) {
        animator.pause();
    }
    
    @Override
    public void resume(@NonNull final Animator animator) {
        animator.resume();
    }
}
