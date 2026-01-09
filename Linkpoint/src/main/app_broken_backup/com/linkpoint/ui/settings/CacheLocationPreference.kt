package com.linkpoint.ui.settings

import android.content.Context
import androidx.preference.Preference
import android.util.AttributeSet
import com.google.common.base.Strings
import com.linkpoint.R

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

    fun makeDisplayableCacheLocation(String str): String {
        var indexOf: Int = str.indexOf("/Android")
        if (indexOf >= 0) {
            str = str.substring(0, indexOf)
        }
        var indexOf2: Int = str.indexOf("/com.linkpoint")
        return indexOf2 >= 0 ? str.substring(0, indexOf2) : str
    }

    fun getSummary(): CharSequence {
        var persistedString: String = getPersistedString((String) null)
        return Strings.isNullOrEmpty(persistedString) ? getContext().getString(R.string.default_cache_location) : makeDisplayableCacheLocation(persistedString)
    }
}
