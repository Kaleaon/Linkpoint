// 
// Decompiled by Procyon v0.6.0
// 

package android.support.transition;

import android.util.AttributeSet;
import android.content.Context;

public class AutoTransition extends TransitionSet
{
    public AutoTransition() {
        this.init();
    }
    
    public AutoTransition(final Context context, final AttributeSet set) {
        super(context, set);
        this.init();
    }
    
    private void init() {
        this.setOrdering(1);
        this.addTransition(new Fade(2)).addTransition(new ChangeBounds()).addTransition(new Fade(1));
    }
}
