// 
// Decompiled by Procyon v0.6.0
// 

package android.support.v7.preference.internal;

import java.util.Set;
import android.util.AttributeSet;
import android.content.Context;
import android.support.annotation.RestrictTo;
import android.support.v7.preference.DialogPreference;

@RestrictTo({ RestrictTo.Scope.LIBRARY_GROUP })
public abstract class AbstractMultiSelectListPreference extends DialogPreference
{
    public AbstractMultiSelectListPreference(final Context context) {
        super(context);
    }
    
    public AbstractMultiSelectListPreference(final Context context, final AttributeSet set) {
        super(context, set);
    }
    
    public AbstractMultiSelectListPreference(final Context context, final AttributeSet set, final int n) {
        super(context, set, n);
    }
    
    public AbstractMultiSelectListPreference(final Context context, final AttributeSet set, final int n, final int n2) {
        super(context, set, n, n2);
    }
    
    public abstract CharSequence[] getEntries();
    
    public abstract CharSequence[] getEntryValues();
    
    public abstract Set<String> getValues();
    
    public abstract void setValues(final Set<String> p0);
}
