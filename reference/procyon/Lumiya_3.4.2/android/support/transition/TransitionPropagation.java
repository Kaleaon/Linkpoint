// 
// Decompiled by Procyon v0.6.0
// 

package android.support.transition;

import android.view.ViewGroup;

public abstract class TransitionPropagation
{
    public abstract void captureValues(final TransitionValues p0);
    
    public abstract String[] getPropagationProperties();
    
    public abstract long getStartDelay(final ViewGroup p0, final Transition p1, final TransitionValues p2, final TransitionValues p3);
}
