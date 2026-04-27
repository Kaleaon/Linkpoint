// 
// Decompiled by Procyon v0.6.0
// 

package android.support.transition;

import java.util.Iterator;
import java.util.HashMap;
import android.view.View;
import java.util.Map;
import java.util.ArrayList;

public class TransitionValues
{
    final ArrayList<Transition> mTargetedTransitions;
    public final Map<String, Object> values;
    public View view;
    
    public TransitionValues() {
        this.values = new HashMap<String, Object>();
        this.mTargetedTransitions = new ArrayList<Transition>();
    }
    
    @Override
    public boolean equals(final Object o) {
        return o instanceof TransitionValues && this.view == ((TransitionValues)o).view && this.values.equals(((TransitionValues)o).values);
    }
    
    @Override
    public int hashCode() {
        return this.view.hashCode() * 31 + this.values.hashCode();
    }
    
    @Override
    public String toString() {
        String str = "TransitionValues@" + Integer.toHexString(this.hashCode()) + ":\n" + "    view = " + this.view + "\n" + "    values:";
        for (final String str2 : this.values.keySet()) {
            str = str + "    " + str2 + ": " + this.values.get(str2) + "\n";
        }
        return str;
    }
}
