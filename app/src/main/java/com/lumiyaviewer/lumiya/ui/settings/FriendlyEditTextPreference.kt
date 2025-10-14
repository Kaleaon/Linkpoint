package com.lumiyaviewer.lumiya.ui.settings

import android.content.Context
import android.support.v7.preference.EditTextPreference
import android.text.TextUtils
import android.util.AttributeSet

class FriendlyEditTextPreference : EditTextPreference {
    FriendlyEditTextPreference(Context context) {
        super(context)
    }

    FriendlyEditTextPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet)
    }

    FriendlyEditTextPreference(Context context, AttributeSet attributeSet, Int i) {
        super(context, attributeSet, i)
    }

    CharSequence getSummary() {
        CharSequence summary
        String text = getText()
        if (TextUtils.isEmpty(text) || (summary = super.getSummary()) == null) {
            return null
        }
        return String.format(summary.toString(), Any[]{text})
    }
}
