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

public class ThemedActivity extends AppCompatActivity {
    private int selectedThemeId = -1;

    /* access modifiers changed from: protected */
    public boolean isLightTheme() {
        return this.selectedThemeId != 2131427371;
    }

    /* access modifiers changed from: protected */
    public void onCreate(@Nullable Bundle bundle) {
        this.selectedThemeId = GlobalOptions.getInstance().getThemeResourceId();
        Debug.Printf("Theme: activity theme 0x%x", Integer.valueOf(this.selectedThemeId));
        int i = this.selectedThemeId;
        setTheme(this instanceof ThemeMapper ? ((ThemeMapper) this).mapThemeResourceId(i) : i);
        super.onCreate(bundle);
    }

    /* access modifiers changed from: protected */
    public void onResume() {
        super.onResume();
        int themeResourceId = GlobalOptions.getInstance().getThemeResourceId();
        Debug.Printf("Theme: resume, new activity theme 0x%x", Integer.valueOf(themeResourceId));
        if (this.selectedThemeId != themeResourceId && this.selectedThemeId != -1) {
            onThemeChangedEvent(new ThemeChangedEvent(themeResourceId));
        }
    }

    /* access modifiers changed from: protected */
    public void onStart() {
        super.onStart();
        EventBus.getInstance().subscribe((Activity) this);
    }

    /* access modifiers changed from: protected */
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
