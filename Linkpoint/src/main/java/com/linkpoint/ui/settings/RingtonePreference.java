package com.lumiyaviewer.lumiya.ui.settings;

import android.content.Context;
import android.content.res.TypedArray;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v7.preference.Preference;
import android.util.AttributeSet;
import com.google.common.base.Objects;
import com.lumiyaviewer.lumiya.R;
import com.lumiyaviewer.lumiya.ui.media.NotificationSounds;

public class RingtonePreference extends Preference {
    private int defaultRawResource = 0;

    public RingtonePreference(Context context) {
        super(context);
    }

    public RingtonePreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        applyAttributes(context, attributeSet, 0, 0);
    }

    public RingtonePreference(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        applyAttributes(context, attributeSet, i, 0);
    }

    public RingtonePreference(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        applyAttributes(context, attributeSet, i, i2);
    }

    private void applyAttributes(Context context, AttributeSet attributeSet, int i, int i2) {
        TypedArray obtainStyledAttributes = context.getTheme().obtainStyledAttributes(attributeSet, R.styleable.RingtonePreference, i, i2);
        try {
            this.defaultRawResource = obtainStyledAttributes.getResourceId(0, this.defaultRawResource);
        } finally {
            obtainStyledAttributes.recycle();
        }
    }

    /* access modifiers changed from: package-private */
    public int getDefaultRawResource() {
        return this.defaultRawResource;
    }

    public CharSequence getSummary() {
        String string = getSharedPreferences().getString(getKey(), (String) null);
        if (string == null) {
            return "Default";
        }
        Uri parse = Uri.parse(string);
        if (Objects.equal(NotificationSounds.getResourceUri(this.defaultRawResource), parse)) {
            return "Default";
        }
        if (string.isEmpty()) {
            return "Silent";
        }
        Ringtone ringtone = RingtoneManager.getRingtone(getContext(), parse);
        return ringtone != null ? ringtone.getTitle(getContext()) : "No sound selected";
    }
}
