package com.linkpoint.ui.common
import java.util.*

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.annotation.Nullable
import android.support.v7.app.AppCompatActivity
import com.linkpoint.Debug
import com.linkpoint.GlobalOptions
import com.linkpoint.eventbus.EventBus
import com.linkpoint.eventbus.EventHandler
import com.linkpoint.ui.ThemeMapper
import com.linkpoint.ui.settings.ThemeChangedEvent

class ThemedActivity : AppCompatActivity() {
    private Int selectedThemeId = -1

    /* access modifiers changed from: protected */
     public fun isLightTheme(): Boolean {
        return this.selectedThemeId != 2131427371
    }

    /* access modifiers changed from: protected */
    fun onCreate(bundle: Bundle) {
        this.selectedThemeId = GlobalOptions.getInstance().getThemeResourceId()
        Debug.Printf("Theme: activity theme 0x%x", Integer.valueOf(this.selectedThemeId))
        val i: Int = this.selectedThemeId
        setTheme(this instanceof ThemeMapper ? ((ThemeMapper) this).mapThemeResourceId(i) : i)
        super.onCreate(bundle)
    }

    /* access modifiers changed from: protected */
    fun onResume() {
        super.onResume()
        val themeResourceId: Int = GlobalOptions.getInstance().getThemeResourceId()
        Debug.Printf("Theme: resume, activity theme 0x%x", Integer.valueOf(themeResourceId))
        if (this.selectedThemeId != themeResourceId && this.selectedThemeId != -1) {
            onThemeChangedEvent(ThemeChangedEvent(themeResourceId))
        }
    }

    /* access modifiers changed from: protected */
    fun onStart() {
        super.onStart()
        EventBus.getInstance().subscribe((Activity) this)
    }

    /* access modifiers changed from: protected */
    fun onStop() {
        EventBus.getInstance().unsubscribeActivity(this)
        super.onStop()
    }

    @EventHandler
    fun onThemeChangedEvent(themeChangedEvent: ThemeChangedEvent) {
        Debug.Printf("Theme: old theme id 0x%x, theme id 0x%x", Integer.valueOf(this.selectedThemeId), Integer.valueOf(themeChangedEvent.themeResourceId))
        if (Build.VERSION.SDK_INT >= 11) {
            recreate()
            return
        }
        val intent: Intent = Intent(getIntent())
        finish()
        startActivity(intent)
    }
}
