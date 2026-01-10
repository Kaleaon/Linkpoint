package com.linkpoint.ui.settings

import android.content.Context
import android.content.res.TypedArray
import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri
import androidx.preference.Preference
import android.util.AttributeSet
import com.google.common.base.Objects
import com.linkpoint.R
import com.linkpoint.ui.media.NotificationSounds

class RingtonePreference : Preference {
    private Int defaultRawResource = 0

    RingtonePreference(Context context) {
        super(context)
    }

    RingtonePreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet)
        applyAttributes(context, attributeSet, 0, 0)
    }

    RingtonePreference(Context context, AttributeSet attributeSet, Int i) {
        super(context, attributeSet, i)
        applyAttributes(context, attributeSet, i, 0)
    }

    RingtonePreference(Context context, AttributeSet attributeSet, Int i, Int i2) {
        super(context, attributeSet, i, i2)
        applyAttributes(context, attributeSet, i, i2)
    }

    private Unit applyAttributes(Context context, AttributeSet attributeSet, Int i, Int i2) {
        TypedArray obtainStyledAttributes = context.getTheme().obtainStyledAttributes(attributeSet, R.styleable.RingtonePreference, i, i2)
        try {
            this.defaultRawResource = obtainStyledAttributes.getResourceId(0, this.defaultRawResource)
        } finally {
            obtainStyledAttributes.recycle()
        }
    }

    /* access modifiers changed from: package-private */
    fun getDefaultRawResource(): Int {
        return this.defaultRawResource
    }

    fun getSummary(): CharSequence {
        var string: String = getSharedPreferences().getString(getKey(), (String) null)
        if (string == null) {
            return "Default"
        }
        Uri parse = Uri.parse(string)
        if (Objects.equal(NotificationSounds.getResourceUri(this.defaultRawResource), parse)) {
            return "Default"
        }
        if (string.isEmpty()) {
            return "Silent"
        }
        Ringtone ringtone = RingtoneManager.getRingtone(getContext(), parse)
        return ringtone != null ? ringtone.getTitle(getContext()) : "No sound selected"
    }
}
