package com.linkpoint.ui.settings

import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import androidx.preference.EditTextPreference

class FriendlyEditTextPreference : EditTextPreference {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun getSummary(): CharSequence? {
        val text = text
        val summary = super.getSummary()
        
        return if (TextUtils.isEmpty(text) || summary == null) {
            null
        } else {
            try {
                String.format(summary.toString(), text)
            } catch (e: java.util.IllegalFormatException) {
                summary
            }
        }
    }
}
