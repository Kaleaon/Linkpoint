package com.linkpoint.ui.settings

import android.content.Context
import android.content.SharedPreferences
import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri
import android.support.v4.view.ViewCompat
import com.google.common.base.Objects
import com.google.common.base.Strings
import com.linkpoint.R
import com.linkpoint.ui.media.NotificationSounds
import com.linkpoint.utils.LEDAction

class NotificationSettings {
    private LEDAction blinkAction = LEDAction.None
    private String blinkColor = "red"
    private Boolean notificationEnabled = false
    private String ringtone = ""
    private Boolean soundEnabled = false
    private NotificationType type

    public NotificationSettings(NotificationType notificationType) {
        this.type = notificationType
    }

     private fun getPrefColor(str: String): Int {
        if (str.length() != 6) {
            return 0
        }
        try {
            return Integer.parseInt(str, 16) | ViewCompat.MEASURED_STATE_MASK
        } catch (NumberFormatException e) {
            e.printStackTrace()
            return 0
        }
    }

     private fun getPreferenceValueName(context: Context, str: String, i: Int, i2: Int): String {
        val stringArray: Array<String> = context.getResources().getStringArray(i)
        val stringArray2: Array<String> = context.getResources().getStringArray(i2)
        for (Int i3 = 0; i3 < stringArray.length; i3++) {
            if (stringArray[i3].equals(str)) {
                return stringArray2[i3]
            }
        }
        return ""
    }

    fun Load(sharedPreferences: SharedPreferences) {
        this.notificationEnabled = sharedPreferences.getBoolean(this.type.getEnableKey(), true)
        this.soundEnabled = sharedPreferences.getBoolean(this.type.getPlaySoundKey(), true)
        val notificationSounds: NotificationSounds = NotificationSounds.defaultSounds.get(this.type)
        this.ringtone = sharedPreferences.getString(this.type.getRingtoneKey(), notificationSounds != null ? notificationSounds.getUri().toString() : null)
        this.blinkAction = LEDAction.getByPreferenceString(sharedPreferences.getString(this.type.getBlinkKey(), "none"))
        this.blinkColor = sharedPreferences.getString(this.type.getBlinkColorKey(), "FF0000")
    }

     public fun getLEDAction(): LEDAction {
        return this.blinkAction
    }

     public fun getLEDColor(): Int {
        return getPrefColor(this.blinkColor)
    }

     public fun getRingtone(): String {
        if (this.soundEnabled) {
            return this.ringtone
        }
        return null
    }

    /* access modifiers changed from: package-private */
     public fun getSummary(context: Context): String {
        String str
        if (this.ringtone != null) {
            val parse: Uri = Uri.parse(this.ringtone)
            val notificationSounds: NotificationSounds = NotificationSounds.defaultSounds.get(this.type)
            if (Objects.equal(notificationSounds != null ? notificationSounds.getUri() : null, parse)) {
                str = "Default"
            } else if (this.ringtone.isEmpty()) {
                str = "Silent"
            } else {
                val ringtone2: Ringtone = RingtoneManager.getRingtone(context, parse)
                str = ringtone2 != null ? ringtone2.getTitle(context) : "No sound selected"
            }
        } else {
            str = "Default"
        }
        val preferenceValueName: String = getPreferenceValueName(context, this.blinkColor, R.array.pref_led_color_values, R.array.pref_led_color)
        if (!this.notificationEnabled) {
            return "Do nothing"
        }
        val str2: String = this.soundEnabled ? "Notify" + ", play sound (" + str + ")" : "Notify"
        if (this.blinkAction == LEDAction.None) {
            return str2
        }
        return str2 + ", blink " + (!Strings.isNullOrEmpty(preferenceValueName) ? preferenceValueName.toLowerCase() + " " : "") + "LED"
    }

     public fun isEnabled(): Boolean {
        return this.notificationEnabled
    }
}
