// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.ui.settings;

import android.content.res.TypedArray;
import com.lumiyaviewer.lumiya.ui.notify.NotificationChannels;
import com.lumiyaviewer.lumiya.R;
import android.annotation.TargetApi;
import android.util.AttributeSet;
import android.content.Context;
import android.support.v7.preference.Preference;

public class PreferenceSubPage extends Preference
{
    private NotificationType notificationType;
    private boolean pageNotificationDetails;
    private int pageResource;
    
    public PreferenceSubPage(final Context context) {
        super(context);
        this.pageResource = 0;
        this.pageNotificationDetails = false;
        this.notificationType = null;
    }
    
    public PreferenceSubPage(final Context context, final AttributeSet set) {
        super(context, set);
        this.pageResource = 0;
        this.pageNotificationDetails = false;
        this.notificationType = null;
        this.applyAttributes(context, set, 0, 0);
    }
    
    public PreferenceSubPage(final Context context, final AttributeSet set, final int n) {
        super(context, set, n);
        this.pageResource = 0;
        this.pageNotificationDetails = false;
        this.notificationType = null;
        this.applyAttributes(context, set, n, 0);
    }
    
    @TargetApi(21)
    public PreferenceSubPage(final Context context, final AttributeSet set, final int n, final int n2) {
        super(context, set, n, n2);
        this.pageResource = 0;
        this.pageNotificationDetails = false;
        this.notificationType = null;
        this.applyAttributes(context, set, n, n2);
    }
    
    private void applyAttributes(Context obtainStyledAttributes, final AttributeSet set, final int n, final int n2) {
        obtainStyledAttributes = (Context)obtainStyledAttributes.getTheme().obtainStyledAttributes(set, R.styleable.PreferenceSubPage, n, n2);
        try {
            this.pageResource = ((TypedArray)obtainStyledAttributes).getResourceId(0, this.pageResource);
            this.pageNotificationDetails = ((TypedArray)obtainStyledAttributes).getBoolean(1, this.pageNotificationDetails);
            final String string = ((TypedArray)obtainStyledAttributes).getString(2);
            if (string == null) {
                return;
            }
            try {
                this.notificationType = NotificationType.valueOf(string);
            }
            catch (final Exception ex) {
                this.notificationType = null;
            }
        }
        finally {
            ((TypedArray)obtainStyledAttributes).recycle();
        }
    }
    
    NotificationType getNotificationType() {
        if (this.pageNotificationDetails) {
            return this.notificationType;
        }
        return null;
    }
    
    int getPageResource() {
        return this.pageResource;
    }
    
    @Override
    public CharSequence getSummary() {
        if (!this.pageNotificationDetails || this.notificationType == null) {
            return super.getSummary();
        }
        final NotificationChannels instance = NotificationChannels.getInstance();
        if (!instance.areNotificationsSystemControlled()) {
            final NotificationSettings notificationSettings = new NotificationSettings(this.notificationType);
            notificationSettings.Load(this.getSharedPreferences());
            return notificationSettings.getSummary(this.getContext());
        }
        final String notificationSummary = instance.getNotificationSummary(this.getContext(), instance.getChannelByType(this.notificationType));
        if (notificationSummary != null) {
            return notificationSummary;
        }
        return super.getSummary();
    }
}
