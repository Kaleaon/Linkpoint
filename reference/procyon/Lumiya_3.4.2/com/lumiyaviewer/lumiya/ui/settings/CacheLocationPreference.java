// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.ui.settings;

import com.google.common.base.Strings;
import android.util.AttributeSet;
import android.content.Context;
import android.support.v7.preference.Preference;

public class CacheLocationPreference extends Preference
{
    public CacheLocationPreference(final Context context) {
        super(context);
    }
    
    public CacheLocationPreference(final Context context, final AttributeSet set) {
        super(context, set);
    }
    
    public CacheLocationPreference(final Context context, final AttributeSet set, final int n) {
        super(context, set, n);
    }
    
    public CacheLocationPreference(final Context context, final AttributeSet set, final int n, final int n2) {
        super(context, set, n, n2);
    }
    
    public static String makeDisplayableCacheLocation(String substring) {
        final int index = substring.indexOf("/Android");
        String substring2 = substring;
        if (index >= 0) {
            substring2 = substring.substring(0, index);
        }
        final int index2 = substring2.indexOf("/com.lumiyaviewer.lumiya");
        substring = substring2;
        if (index2 >= 0) {
            substring = substring2.substring(0, index2);
        }
        return substring;
    }
    
    @Override
    public CharSequence getSummary() {
        final String persistedString = this.getPersistedString(null);
        if (Strings.isNullOrEmpty(persistedString)) {
            return this.getContext().getString(2131296470);
        }
        return makeDisplayableCacheLocation(persistedString);
    }
}
