// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.ui.settings;

import android.text.TextUtils;
import android.util.AttributeSet;
import android.content.Context;
import android.support.v7.preference.EditTextPreference;

public class FriendlyEditTextPreference extends EditTextPreference
{
    public FriendlyEditTextPreference(final Context context) {
        super(context);
    }
    
    public FriendlyEditTextPreference(final Context context, final AttributeSet set) {
        super(context, set);
    }
    
    public FriendlyEditTextPreference(final Context context, final AttributeSet set, final int n) {
        super(context, set, n);
    }
    
    @Override
    public CharSequence getSummary() {
        final String text = this.getText();
        if (TextUtils.isEmpty((CharSequence)text)) {
            return null;
        }
        final CharSequence summary = super.getSummary();
        if (summary != null) {
            return String.format(summary.toString(), text);
        }
        return null;
    }
}
