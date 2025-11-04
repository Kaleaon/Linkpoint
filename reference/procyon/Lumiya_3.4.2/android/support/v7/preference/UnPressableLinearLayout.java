// 
// Decompiled by Procyon v0.6.0
// 

package android.support.v7.preference;

import android.util.AttributeSet;
import android.content.Context;
import android.support.annotation.RestrictTo;
import android.widget.LinearLayout;

@RestrictTo({ RestrictTo.Scope.LIBRARY_GROUP })
public class UnPressableLinearLayout extends LinearLayout
{
    public UnPressableLinearLayout(final Context context) {
        this(context, null);
    }
    
    public UnPressableLinearLayout(final Context context, final AttributeSet set) {
        super(context, set);
    }
    
    protected void dispatchSetPressed(final boolean b) {
    }
}
