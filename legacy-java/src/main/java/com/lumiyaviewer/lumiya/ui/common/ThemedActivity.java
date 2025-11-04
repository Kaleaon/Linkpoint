package com.lumiyaviewer.lumiya.ui.common;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.GlobalOptions;
import com.lumiyaviewer.lumiya.eventbus.EventBus;
import com.lumiyaviewer.lumiya.eventbus.EventHandler;
import com.lumiyaviewer.lumiya.ui.ThemeMapper;
import com.lumiyaviewer.lumiya.ui.settings.ThemeChangedEvent;

/* loaded from: classes.dex */
public class ThemedActivity extends AppCompatActivity {
    private int selectedThemeId = -1;

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean isLightTheme() {
        return this.selectedThemeId != 2131427371;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* JADX WARN: Multi-variable type inference failed */
    @Override // android.support.v7.app.AppCompatActivity, android.support.v4.app.FragmentActivity, android.support.v4.app.SupportActivity, android.app.Activity
    public void onCreate(@Nullable Bundle bundle) {
        this.selectedThemeId = GlobalOptions.getInstance().getThemeResourceId();
        Debug.Printf("Theme: activity theme 0x%x", Integer.valueOf(this.selectedThemeId));
        int i = this.selectedThemeId;
        setTheme(this instanceof ThemeMapper ? ((ThemeMapper) this).mapThemeResourceId(i) : i);
        super.onCreate(bundle);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.support.v4.app.FragmentActivity, android.app.Activity
    public void onResume() {
        super.onResume();
        int themeResourceId = GlobalOptions.getInstance().getThemeResourceId();
        Debug.Printf("Theme: resume, new activity theme 0x%x", Integer.valueOf(themeResourceId));
        if (this.selectedThemeId == themeResourceId || this.selectedThemeId == -1) {
            return;
        }
        onThemeChangedEvent(new ThemeChangedEvent(themeResourceId));
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.support.v7.app.AppCompatActivity, android.support.v4.app.FragmentActivity, android.app.Activity
    public void onStart() {
        super.onStart();
        EventBus.getInstance().subscribe((Activity) this);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.support.v7.app.AppCompatActivity, android.support.v4.app.FragmentActivity, android.app.Activity
    public void onStop() {
        EventBus.getInstance().unsubscribeActivity(this);
        super.onStop();
    }

    @EventHandler
    public void onThemeChangedEvent(ThemeChangedEvent themeChangedEvent) {
        Debug.Printf("Theme: old theme id 0x%x, new theme id 0x%x", Integer.valueOf(this.selectedThemeId), Integer.valueOf(themeChangedEvent.themeResourceId));
        if (Build.VERSION.SDK_INT >= 11) {
            recreate();
            return;
        }
        Intent intent = new Intent(getIntent());
        finish();
        startActivity(intent);
    }
}
