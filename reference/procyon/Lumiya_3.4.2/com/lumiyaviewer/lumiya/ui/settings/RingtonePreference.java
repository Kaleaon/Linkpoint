// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.ui.settings;

import android.content.res.TypedArray;
import android.media.Ringtone;
import android.media.RingtoneManager;
import com.google.common.base.Objects;
import com.lumiyaviewer.lumiya.ui.media.NotificationSounds;
import android.net.Uri;
import com.lumiyaviewer.lumiya.R;
import android.util.AttributeSet;
import android.content.Context;
import android.support.v7.preference.Preference;

public class RingtonePreference extends Preference
{
    private int defaultRawResource;
    
    public RingtonePreference(final Context context) {
        super(context);
        this.defaultRawResource = 0;
    }
    
    public RingtonePreference(final Context context, final AttributeSet set) {
        super(context, set);
        this.applyAttributes(context, set, this.defaultRawResource = 0, 0);
    }
    
    public RingtonePreference(final Context context, final AttributeSet set, final int n) {
        super(context, set, n);
        this.applyAttributes(context, set, n, this.defaultRawResource = 0);
    }
    
    public RingtonePreference(final Context context, final AttributeSet set, final int n, final int n2) {
        super(context, set, n, n2);
        this.defaultRawResource = 0;
        this.applyAttributes(context, set, n, n2);
    }
    
    private void applyAttributes(Context obtainStyledAttributes, final AttributeSet set, final int n, final int n2) {
        obtainStyledAttributes = (Context)obtainStyledAttributes.getTheme().obtainStyledAttributes(set, R.styleable.RingtonePreference, n, n2);
        try {
            this.defaultRawResource = ((TypedArray)obtainStyledAttributes).getResourceId(0, this.defaultRawResource);
        }
        finally {
            ((TypedArray)obtainStyledAttributes).recycle();
        }
    }
    
    int getDefaultRawResource() {
        return this.defaultRawResource;
    }
    
    @Override
    public CharSequence getSummary() {
        String title = "No sound selected";
        final String string = this.getSharedPreferences().getString(this.getKey(), (String)null);
        if (string != null) {
            final Uri parse = Uri.parse(string);
            if (Objects.equal(NotificationSounds.getResourceUri(this.defaultRawResource), parse)) {
                title = "Default";
            }
            else if (string.isEmpty()) {
                title = "Silent";
            }
            else {
                final Ringtone ringtone = RingtoneManager.getRingtone(this.getContext(), parse);
                if (ringtone != null) {
                    title = ringtone.getTitle(this.getContext());
                }
            }
        }
        else {
            title = "Default";
        }
        return title;
    }
}
