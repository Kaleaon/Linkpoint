package com.lumiyaviewer.lumiya.ui.settings;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.preference.Preference;
import android.util.AttributeSet;
import com.lumiyaviewer.lumiya.R;
import com.lumiyaviewer.lumiya.ui.notify.NotificationChannels;

public class PreferenceSubPage extends Preference {
    private NotificationType notificationType = null;
    private boolean pageNotificationDetails = false;
    private int pageResource = 0;

    public PreferenceSubPage(Context context) {
        super(context);
    }

    public PreferenceSubPage(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        applyAttributes(context, attributeSet, 0, 0);
    }

    public PreferenceSubPage(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        applyAttributes(context, attributeSet, i, 0);
    }

    @TargetApi(21)
    public PreferenceSubPage(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        applyAttributes(context, attributeSet, i, i2);
    }

    private void applyAttributes(Context context, AttributeSet attributeSet, int i, int i2) {
        TypedArray obtainStyledAttributes = context.getTheme().obtainStyledAttributes(attributeSet, R.styleable.PreferenceSubPage, i, i2);
        try {
            this.pageResource = obtainStyledAttributes.getResourceId(0, this.pageResource);
            this.pageNotificationDetails = obtainStyledAttributes.getBoolean(1, this.pageNotificationDetails);
            String string = obtainStyledAttributes.getString(2);
            if (string != null) {
                this.notificationType = NotificationType.valueOf(string);
            }
        } catch (Exception e) {
            this.notificationType = null;
        } catch (Throwable th) {
            obtainStyledAttributes.recycle();
            throw th;
        }
        obtainStyledAttributes.recycle();
    }

    /* access modifiers changed from: package-private */
    public NotificationType getNotificationType() {
        if (this.pageNotificationDetails) {
            return this.notificationType;
        }
        return null;
    }

    /* access modifiers changed from: package-private */
    public int getPageResource() {
        return this.pageResource;
    }

    public CharSequence getSummary() {
        if (!this.pageNotificationDetails || this.notificationType == null) {
            return super.getSummary();
        }
        NotificationChannels instance = NotificationChannels.getInstance();
        if (instance.areNotificationsSystemControlled()) {
            String notificationSummary = instance.getNotificationSummary(getContext(), instance.getChannelByType(this.notificationType));
            return notificationSummary != null ? notificationSummary : super.getSummary();
        }
        NotificationSettings notificationSettings = new NotificationSettings(this.notificationType);
        notificationSettings.Load(getSharedPreferences());
        return notificationSettings.getSummary(getContext());
    }
}
