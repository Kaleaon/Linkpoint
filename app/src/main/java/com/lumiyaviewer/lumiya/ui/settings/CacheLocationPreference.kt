package com.lumiyaviewer.lumiya.ui.settings

import android.content.Context
import android.util.AttributeSet
import androidx.preference.Preference
import com.google.common.base.Strings
import com.lumiyaviewer.lumiya.R

class CacheLocationPreference : Preference {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : 
        super(context, attrs, defStyleAttr, defStyleRes)

    override fun getSummary(): CharSequence {
        val persistedString = getPersistedString(null)
        return if (Strings.isNullOrEmpty(persistedString)) {
            context.getString(R.string.default_cache_location)
        } else {
            makeDisplayableCacheLocation(persistedString!!)
        }
    }

    companion object {
        @JvmStatic
        fun makeDisplayableCacheLocation(path: String): String {
            var result = path
            
            val androidIndex = result.indexOf("/Android")
            if (androidIndex >= 0) {
                result = result.substring(0, androidIndex)
            }
            
            val packageIndex = result.indexOf("/com.lumiyaviewer.lumiya")
            if (packageIndex >= 0) {
                result = result.substring(0, packageIndex)
            }
            
            return result
        }
    }
}
