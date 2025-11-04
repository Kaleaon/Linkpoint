// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.ui.common;

import com.lumiyaviewer.lumiya.eventbus.EventHandler;
import android.content.Intent;
import android.os.Build$VERSION;
import android.app.Activity;
import com.lumiyaviewer.lumiya.eventbus.EventBus;
import com.lumiyaviewer.lumiya.ui.settings.ThemeChangedEvent;
import com.lumiyaviewer.lumiya.ui.ThemeMapper;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.GlobalOptions;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class ThemedActivity extends AppCompatActivity
{
    private int selectedThemeId;
    
    public ThemedActivity() {
        this.selectedThemeId = -1;
    }
    
    protected boolean isLightTheme() {
        return this.selectedThemeId != 2131427371;
    }
    
    @Override
    protected void onCreate(@Nullable final Bundle bundle) {
        this.selectedThemeId = GlobalOptions.getInstance().getThemeResourceId();
        Debug.Printf("Theme: activity theme 0x%x", this.selectedThemeId);
        int theme = this.selectedThemeId;
        if (this instanceof ThemeMapper) {
            theme = ((ThemeMapper)this).mapThemeResourceId(theme);
        }
        this.setTheme(theme);
        super.onCreate(bundle);
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        final int themeResourceId = GlobalOptions.getInstance().getThemeResourceId();
        Debug.Printf("Theme: resume, new activity theme 0x%x", themeResourceId);
        if (this.selectedThemeId != themeResourceId && this.selectedThemeId != -1) {
            this.onThemeChangedEvent(new ThemeChangedEvent(themeResourceId));
        }
    }
    
    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getInstance().subscribe(this);
    }
    
    @Override
    protected void onStop() {
        EventBus.getInstance().unsubscribeActivity(this);
        super.onStop();
    }
    
    @EventHandler
    public void onThemeChangedEvent(final ThemeChangedEvent themeChangedEvent) {
        Debug.Printf("Theme: old theme id 0x%x, new theme id 0x%x", this.selectedThemeId, themeChangedEvent.themeResourceId);
        if (Build$VERSION.SDK_INT >= 11) {
            this.recreate();
        }
        else {
            final Intent intent = new Intent(this.getIntent());
            this.finish();
            this.startActivity(intent);
        }
    }
}
