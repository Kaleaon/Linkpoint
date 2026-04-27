package com.linkpoint.ui.settings

import android.content.Context
import android.content.res.TypedArray
import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri
import android.support.v7.preference.Preference
import android.util.AttributeSet
import com.google.common.base.Objects
import com.linkpoint.R
import com.linkpoint.ui.media.NotificationSounds

/**
 * Custom preference for ringtone selection with default resource support
 * Modernized Kotlin implementation
 */
class RingtonePreference : Preference {
    private var defaultRawResource = 0

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        applyAttributes(context, attrs, 0, 0)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : 
        super(context, attrs, defStyleAttr) {
        applyAttributes(context, attrs, defStyleAttr, 0)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : 
        super(context, attrs, defStyleAttr, defStyleRes) {
        applyAttributes(context, attrs, defStyleAttr, defStyleRes)
    }

    private fun applyAttributes(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.RingtonePreference,
            defStyleAttr,
            defStyleRes
        ).use { typedArray ->
            defaultRawResource = typedArray.getResourceId(0, defaultRawResource)
        }
    }

    internal fun getDefaultRawResource(): Int = defaultRawResource

    override fun getSummary(): CharSequence {
        val savedValue = sharedPreferences?.getString(key, null)
        
        return when {
            savedValue == null -> "Default"
            savedValue.isEmpty() -> "Silent"
            else -> {
                val uri = Uri.parse(savedValue)
                when {
                    Objects.equal(NotificationSounds.getResourceUri(defaultRawResource), uri) -> "Default"
                    else -> {
                        val ringtone = RingtoneManager.getRingtone(context, uri)
                        ringtone?.getTitle(context) ?: "No sound selected"
                    }
                }
            }
        }
    }
}
