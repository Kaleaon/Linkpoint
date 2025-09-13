package com.lumiyaviewer.lumiya.ui.settings;

import android.content.Context;
import android.support.v7.preference.Preference;
import android.util.AttributeSet;
import com.google.common.base.Strings;
import com.lumiyaviewer.lumiya.R;

public class CacheLocationPreference extends Preference {
    public CacheLocationPreference(Context context) {
        super(context);
    }

    public CacheLocationPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public CacheLocationPreference(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    public CacheLocationPreference(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
    }

    public static String makeDisplayableCacheLocation(String str) {
        int indexOf = str.indexOf("/Android");
        if (indexOf >= 0) {
            str = str.substring(0, indexOf);
        }
        int indexOf2 = str.indexOf("/com.lumiyaviewer.lumiya");
        return indexOf2 >= 0 ? str.substring(0, indexOf2) : str;
    }

    public CharSequence getSummary() {
        String persistedString = getPersistedString((String) null);
        return Strings.isNullOrEmpty(persistedString) ? getContext().getString(R.string.default_cache_location) : makeDisplayableCacheLocation(persistedString);
    }
}
