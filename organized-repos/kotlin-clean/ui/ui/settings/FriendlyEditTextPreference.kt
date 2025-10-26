package com.linkpoint.ui.settings

import android.content.Context
import android.support.v7.preference.EditTextPreference
import android.text.TextUtils
import android.util.AttributeSet

class FriendlyEditTextPreference : EditTextPreference() {
    public FriendlyEditTextPreference(Context context) {
        super(context)
    }

    public FriendlyEditTextPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet)
    }

    public FriendlyEditTextPreference(Context context, AttributeSet attributeSet, Int i) {
        super(context, attributeSet, i)
    }

    public CharSequence getSummary() {
        CharSequence summary
        String text = getText()
        if (TextUtils.isEmpty(text) || (summary = super.getSummary()) == null) {
            return null
        }
        return String.format(summary.toString(), Array<Any>{text})
    }
}
