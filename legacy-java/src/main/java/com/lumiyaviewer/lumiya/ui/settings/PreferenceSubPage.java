package com.lumiyaviewer.lumiya.ui.settings;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.preference.Preference;
import android.util.AttributeSet;
import com.lumiyaviewer.lumiya.R;
import com.lumiyaviewer.lumiya.ui.notify.NotificationChannels;

/* loaded from: classes.dex */
public class PreferenceSubPage extends Preference {
    private NotificationType notificationType;
    private boolean pageNotificationDetails;
    private int pageResource;

    public PreferenceSubPage(Context context) {
        super(context);
        this.pageResource = 0;
        this.pageNotificationDetails = false;
        this.notificationType = null;
    }

    public PreferenceSubPage(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.pageResource = 0;
        this.pageNotificationDetails = false;
        this.notificationType = null;
        applyAttributes(context, attributeSet, 0, 0);
    }

    public PreferenceSubPage(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.pageResource = 0;
        this.pageNotificationDetails = false;
        this.notificationType = null;
        applyAttributes(context, attributeSet, i, 0);
    }

    @TargetApi(21)
    public PreferenceSubPage(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        this.pageResource = 0;
        this.pageNotificationDetails = false;
        this.notificationType = null;
        applyAttributes(context, attributeSet, i, i2);
    }

    private void applyAttributes(Context context, AttributeSet attributeSet, int i, int i2) {
        TypedArray obtainStyledAttributes = context.getTheme().obtainStyledAttributes(attributeSet, R.styleable.PreferenceSubPage, i, i2);
        try {
            this.pageResource = obtainStyledAttributes.getResourceId(0, this.pageResource);
            this.pageNotificationDetails = obtainStyledAttributes.getBoolean(1, this.pageNotificationDetails);
            String string = obtainStyledAttributes.getString(2);
            if (string != null) {
                try {
                    this.notificationType = NotificationType.valueOf(string);
                } catch (Exception e) {
                    this.notificationType = null;
                }
            }
        } finally {
            obtainStyledAttributes.recycle();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public NotificationType getNotificationType() {
        if (this.pageNotificationDetails) {
            return this.notificationType;
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getPageResource() {
        return this.pageResource;
    }

    @Override // android.support.v7.preference.Preference
    public CharSequence getSummary() {
        if (!this.pageNotificationDetails || this.notificationType == null) {
            return super.getSummary();
        }
        NotificationChannels notificationChannels = NotificationChannels.getInstance();
        if (notificationChannels.areNotificationsSystemControlled()) {
            String notificationSummary = notificationChannels.getNotificationSummary(getContext(), notificationChannels.getChannelByType(this.notificationType));
            return notificationSummary != null ? notificationSummary : super.getSummary();
        }
        NotificationSettings notificationSettings = new NotificationSettings(this.notificationType);
        notificationSettings.Load(getSharedPreferences());
        return notificationSettings.getSummary(getContext());
    }
}
