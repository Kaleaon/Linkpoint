package com.linkpoint.ui.settings

import android.content.Context
import android.content.SharedPreferences
import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri
import androidx.core.view.ViewCompat
import com.google.common.base.Objects
import com.google.common.base.Strings
import com.linkpoint.R
import com.linkpoint.ui.media.NotificationSounds
import com.linkpoint.utils.LEDAction

class NotificationSettings {
    private LEDAction blinkAction = LEDAction.None
    private val blinkColor: String = "red"
    private Boolean notificationEnabled = false
    private val ringtone: String = ""
    private Boolean soundEnabled = false
    private NotificationType type

    NotificationSettings(NotificationType notificationType) {
        this.type = notificationType
    }

    private Int getPrefColor(String str) {
        if (str.size() != 6) {
            return 0
        }
        try {
            return Int.parseInt(str, 16) | ViewCompat.MEASURED_STATE_MASK
        } catch (NumberFormatException e) {
            e.printStackTrace()
            return 0
        }
    }

    private String getPreferenceValueName(Context context, String str, Int i, Int i2) {
        Array<String> stringArray = context.getResources().getStringArray(i)
        Array<String> stringArray2 = context.getResources().getStringArray(i2)
        for (i3 in 0 until stringArray.size) {
            if (stringArray[i3].equals(str)) {
                return stringArray2[i3]
            }
        }
        return ""
    }

    fun Load(SharedPreferences sharedPreferences)  {
        this.notificationEnabled = sharedPreferences.getBoolean(this.type.getEnableKey(), true)
        this.soundEnabled = sharedPreferences.getBoolean(this.type.getPlaySoundKey(), true)
        NotificationSounds notificationSounds = NotificationSounds.defaultSounds.get(this.type)
        this.ringtone = sharedPreferences.getString(this.type.getRingtoneKey(), notificationSounds != null ? notificationSounds.getUri().toString() : null)
        this.blinkAction = LEDAction.getByPreferenceString(sharedPreferences.getString(this.type.getBlinkKey(), "none"))
        this.blinkColor = sharedPreferences.getString(this.type.getBlinkColorKey(), "FF0000")
    }

    fun getLEDAction(): LEDAction {
        return this.blinkAction
    }

    fun getLEDColor(): Int {
        return getPrefColor(this.blinkColor)
    }

    fun getRingtone(): String {
        if (this.soundEnabled) {
            return this.ringtone
        }
        return null
    }

    /* access modifiers changed from: package-private */
    fun getSummary(Context context): String {
        String str
        if (this.ringtone != null) {
            Uri parse = Uri.parse(this.ringtone)
            NotificationSounds notificationSounds = NotificationSounds.defaultSounds.get(this.type)
            if (Objects.equal(notificationSounds != null ? notificationSounds.getUri() : null, parse)) {
                str = "Default"
            } else if (this.ringtone.isEmpty()) {
                str = "Silent"
            } else {
                Ringtone ringtone2 = RingtoneManager.getRingtone(context, parse)
                str = ringtone2 != null ? ringtone2.getTitle(context) : "No sound selected"
            }
        } else {
            str = "Default"
        }
        var preferenceValueName: String = getPreferenceValueName(context, this.blinkColor, R.array.pref_led_color_values, R.array.pref_led_color)
        if (!this.notificationEnabled) {
            return "Do nothing"
        }
        var str2: String = this.soundEnabled ? "Notify" + ", play sound (" + str + ")" : "Notify"
        if (this.blinkAction == LEDAction.None) {
            return str2
        }
        return str2 + ", blink " + (!Strings.isNullOrEmpty(preferenceValueName) ? preferenceValueName.toLowerCase() + " " : "") + "LED"
    }

    fun isEnabled(): Boolean {
        return this.notificationEnabled
    }
}
