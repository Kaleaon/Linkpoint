package com.lumiyaviewer.lumiya.ui.settings

import android.content.Context
import android.support.v7.preference.Preference
import android.util.AttributeSet
import com.google.common.base.Strings
import com.lumiyaviewer.lumiya.R

class CacheLocationPreference : Preference {
    CacheLocationPreference(Context context) {
        super(context)
    }

    CacheLocationPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet)
    }

    CacheLocationPreference(Context context, AttributeSet attributeSet, Int i) {
        super(context, attributeSet, i)
    }

    CacheLocationPreference(Context context, AttributeSet attributeSet, Int i, Int i2) {
        super(context, attributeSet, i, i2)
    }

    String makeDisplayableCacheLocation(String str) {
        Int indexOf = str.indexOf("/Android")
        if (indexOf >= 0) {
            str = str.substring(0, indexOf)
        }
        Int indexOf2 = str.indexOf("/com.lumiyaviewer.lumiya")
        return indexOf2 >= 0 ? str.substring(0, indexOf2) : str
    }

    CharSequence getSummary() {
        String persistedString = getPersistedString((String) null)
        return Strings.isNullOrEmpty(persistedString) ? getContext().getString(R.string.default_cache_location) : makeDisplayableCacheLocation(persistedString)
    }
}
