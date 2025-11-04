// 
// Decompiled by Procyon v0.6.0
// 

package android.support.transition;

import java.util.ArrayList;
import android.animation.Animator$AnimatorListener;
import android.animation.AnimatorListenerAdapter;
import android.support.annotation.NonNull;
import android.animation.Animator;
import android.support.annotation.RequiresApi;

@RequiresApi(14)
class AnimatorUtilsApi14 implements AnimatorUtilsImpl
{
    @Override
    public void addPauseListener(@NonNull final Animator animator, @NonNull final AnimatorListenerAdapter animatorListenerAdapter) {
    }
    
    @Override
    public void pause(@NonNull final Animator animator) {
        final ArrayList listeners = animator.getListeners();
        if (listeners != null) {
            for (int size = listeners.size(), i = 0; i < size; ++i) {
                final Animator$AnimatorListener animator$AnimatorListener = listeners.get(i);
                if (animator$AnimatorListener instanceof AnimatorPauseListenerCompat) {
                    ((AnimatorPauseListenerCompat)animator$AnimatorListener).onAnimationPause(animator);
                }
            }
        }
    }
    
    @Override
    public void resume(@NonNull final Animator animator) {
        final ArrayList listeners = animator.getListeners();
        if (listeners != null) {
            for (int size = listeners.size(), i = 0; i < size; ++i) {
                final Animator$AnimatorListener animator$AnimatorListener = listeners.get(i);
                if (animator$AnimatorListener instanceof AnimatorPauseListenerCompat) {
                    ((AnimatorPauseListenerCompat)animator$AnimatorListener).onAnimationResume(animator);
                }
            }
        }
    }
    
    interface AnimatorPauseListenerCompat
    {
        void onAnimationPause(final Animator p0);
        
        void onAnimationResume(final Animator p0);
    }
}
