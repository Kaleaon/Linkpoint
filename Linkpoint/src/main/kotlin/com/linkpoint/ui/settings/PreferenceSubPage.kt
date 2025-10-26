package com.linkpoint.ui.settings

import android.annotation.TargetApi
import android.content.Context
import android.content.res.TypedArray
import android.support.v7.preference.Preference
import android.util.AttributeSet
import com.linkpoint.R
import com.linkpoint.ui.notify.NotificationChannels

class PreferenceSubPage : Preference() {
    private NotificationType notificationType = null
    private Boolean pageNotificationDetails = false
    private Int pageResource = 0

    public PreferenceSubPage(Context context) {
        super(context)
    }

    public PreferenceSubPage(Context context, AttributeSet attributeSet) {
        super(context, attributeSet)
        applyAttributes(context, attributeSet, 0, 0)
    }

    public PreferenceSubPage(Context context, AttributeSet attributeSet, Int i) {
        super(context, attributeSet, i)
        applyAttributes(context, attributeSet, i, 0)
    }

    @TargetApi(21)
    public PreferenceSubPage(Context context, AttributeSet attributeSet, Int i, Int i2) {
        super(context, attributeSet, i, i2)
        applyAttributes(context, attributeSet, i, i2)
    }

     private fun applyAttributes(context: Context, attributeSet: AttributeSet, i: Int, i2: Int) {
        val obtainStyledAttributes: TypedArray = context.getTheme().obtainStyledAttributes(attributeSet, R.styleable.PreferenceSubPage, i, i2)
        try {
            this.pageResource = obtainStyledAttributes.getResourceId(0, this.pageResource)
            this.pageNotificationDetails = obtainStyledAttributes.getBoolean(1, this.pageNotificationDetails)
            val string: String = obtainStyledAttributes.getString(2)
            if (string != null) {
                this.notificationType = NotificationType.valueOf(string)
            }
        } catch (Exception e) {
            this.notificationType = null
        } catch (Throwable th) {
            obtainStyledAttributes.recycle()
            throw th
        }
        obtainStyledAttributes.recycle()
    }

    /* access modifiers changed from: package-private */
     public fun getNotificationType(): NotificationType {
        if (this.pageNotificationDetails) {
            return this.notificationType
        }
        return null
    }

    /* access modifiers changed from: package-private */
     public fun getPageResource(): Int {
        return this.pageResource
    }

     public fun getSummary(): CharSequence {
        if (!this.pageNotificationDetails || this.notificationType == null) {
            return super.getSummary()
        }
        val instance: NotificationChannels = NotificationChannels.getInstance()
        if (instance.areNotificationsSystemControlled()) {
            val notificationSummary: String = instance.getNotificationSummary(getContext(), instance.getChannelByType(this.notificationType))
            return notificationSummary != null ? notificationSummary : super.getSummary()
        }
        val notificationSettings: NotificationSettings = NotificationSettings(this.notificationType)
        notificationSettings.Load(getSharedPreferences())
        return notificationSettings.getSummary(getContext())
    }
}
