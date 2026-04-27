// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.ui.settings;

import android.media.Ringtone;
import com.google.common.base.Strings;
import android.media.RingtoneManager;
import com.google.common.base.Objects;
import android.net.Uri;
import com.lumiyaviewer.lumiya.ui.media.NotificationSounds;
import android.content.SharedPreferences;
import android.content.Context;
import com.lumiyaviewer.lumiya.utils.LEDAction;

public class NotificationSettings
{
    private LEDAction blinkAction;
    private String blinkColor;
    private boolean notificationEnabled;
    private String ringtone;
    private boolean soundEnabled;
    private NotificationType type;
    
    public NotificationSettings(final NotificationType type) {
        this.type = type;
        this.notificationEnabled = false;
        this.soundEnabled = false;
        this.ringtone = "";
        this.blinkAction = LEDAction.None;
        this.blinkColor = "red";
    }
    
    private int getPrefColor(final String s) {
        if (s.length() != 6) {
            return 0;
        }
        try {
            return Integer.parseInt(s, 16) | 0xFF000000;
        }
        catch (final NumberFormatException ex) {
            ex.printStackTrace();
            return 0;
        }
    }
    
    private String getPreferenceValueName(final Context context, final String anObject, int i, final int n) {
        final String[] stringArray = context.getResources().getStringArray(i);
        final String[] stringArray2 = context.getResources().getStringArray(n);
        String s;
        for (i = 0; i < stringArray.length; ++i) {
            if (stringArray[i].equals(anObject)) {
                s = stringArray2[i];
                return s;
            }
        }
        s = "";
        return s;
    }
    
    public void Load(final SharedPreferences sharedPreferences) {
        this.notificationEnabled = sharedPreferences.getBoolean(this.type.getEnableKey(), true);
        this.soundEnabled = sharedPreferences.getBoolean(this.type.getPlaySoundKey(), true);
        final NotificationSounds notificationSounds = NotificationSounds.defaultSounds.get(this.type);
        final String ringtoneKey = this.type.getRingtoneKey();
        String string;
        if (notificationSounds != null) {
            string = notificationSounds.getUri().toString();
        }
        else {
            string = null;
        }
        this.ringtone = sharedPreferences.getString(ringtoneKey, string);
        this.blinkAction = LEDAction.getByPreferenceString(sharedPreferences.getString(this.type.getBlinkKey(), "none"));
        this.blinkColor = sharedPreferences.getString(this.type.getBlinkColorKey(), "FF0000");
    }
    
    public LEDAction getLEDAction() {
        return this.blinkAction;
    }
    
    public int getLEDColor() {
        return this.getPrefColor(this.blinkColor);
    }
    
    public String getRingtone() {
        if (this.soundEnabled) {
            return this.ringtone;
        }
        return null;
    }
    
    String getSummary(final Context context) {
        String title;
        if (this.ringtone != null) {
            final Uri parse = Uri.parse(this.ringtone);
            final NotificationSounds notificationSounds = NotificationSounds.defaultSounds.get(this.type);
            Uri uri;
            if (notificationSounds != null) {
                uri = notificationSounds.getUri();
            }
            else {
                uri = null;
            }
            if (Objects.equal(uri, parse)) {
                title = "Default";
            }
            else if (this.ringtone.isEmpty()) {
                title = "Silent";
            }
            else {
                final Ringtone ringtone = RingtoneManager.getRingtone(context, parse);
                if (ringtone != null) {
                    title = ringtone.getTitle(context);
                }
                else {
                    title = "No sound selected";
                }
            }
        }
        else {
            title = "Default";
        }
        final String preferenceValueName = this.getPreferenceValueName(context, this.blinkColor, 2131689490, 2131689489);
        String string2;
        if (this.notificationEnabled) {
            String string;
            if (this.soundEnabled) {
                string = "Notify" + ", play sound (" + title + ")";
            }
            else {
                string = "Notify";
            }
            string2 = string;
            if (this.blinkAction != LEDAction.None) {
                final StringBuilder append = new StringBuilder().append(string).append(", blink ");
                String string3;
                if (!Strings.isNullOrEmpty(preferenceValueName)) {
                    string3 = preferenceValueName.toLowerCase() + " ";
                }
                else {
                    string3 = "";
                }
                string2 = append.append(string3).append("LED").toString();
            }
        }
        else {
            string2 = "Do nothing";
        }
        return string2;
    }
    
    public boolean isEnabled() {
        return this.notificationEnabled;
    }
}
