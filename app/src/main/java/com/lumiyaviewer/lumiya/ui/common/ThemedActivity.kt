package com.lumiyaviewer.lumiya.ui.common
import java.util.*

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.annotation.Nullable
import android.support.v7.app.AppCompatActivity
import com.lumiyaviewer.lumiya.Debug
import com.lumiyaviewer.lumiya.GlobalOptions
import com.lumiyaviewer.lumiya.eventbus.EventBus
import com.lumiyaviewer.lumiya.eventbus.EventHandler
import com.lumiyaviewer.lumiya.ui.ThemeMapper
import com.lumiyaviewer.lumiya.ui.settings.ThemeChangedEvent

class ThemedActivity : AppCompatActivity {
    private Int selectedThemeId = -1

    /* access modifiers changed from: protected */
    Boolean isLightTheme() {
        return this.selectedThemeId != 2131427371
    }

    /* access modifiers changed from: protected */
    Unit onCreate(@Nullable Bundle bundle) {
        this.selectedThemeId = GlobalOptions.getInstance().getThemeResourceId()
        Debug.Printf("Theme: activity theme 0x%x", Int.valueOf(this.selectedThemeId))
        Int i = this.selectedThemeId
        setTheme(this instanceof ThemeMapper ? ((ThemeMapper) this).mapThemeResourceId(i) : i)
        super.onCreate(bundle)
    }

    /* access modifiers changed from: protected */
    Unit onResume() {
        super.onResume()
        Int themeResourceId = GlobalOptions.getInstance().getThemeResourceId()
        Debug.Printf("Theme: resume, activity theme 0x%x", Int.valueOf(themeResourceId))
        if (this.selectedThemeId != themeResourceId && this.selectedThemeId != -1) {
            onThemeChangedEvent(ThemeChangedEvent(themeResourceId))
        }
    }

    /* access modifiers changed from: protected */
    Unit onStart() {
        super.onStart()
        EventBus.getInstance().subscribe((Activity) this)
    }

    /* access modifiers changed from: protected */
    Unit onStop() {
        EventBus.getInstance().unsubscribeActivity(this)
        super.onStop()
    }

    @EventHandler
    Unit onThemeChangedEvent(ThemeChangedEvent themeChangedEvent) {
        Debug.Printf("Theme: old theme id 0x%x, theme id 0x%x", Int.valueOf(this.selectedThemeId), Int.valueOf(themeChangedEvent.themeResourceId))
        if (Build.VERSION.SDK_INT >= 11) {
            recreate()
            return
        }
        Intent intent = Intent(getIntent())
        finish()
        startActivity(intent)
    }
}
