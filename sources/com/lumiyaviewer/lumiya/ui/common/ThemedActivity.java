// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.common;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.GlobalOptions;
import com.lumiyaviewer.lumiya.eventbus.EventBus;
import com.lumiyaviewer.lumiya.ui.ThemeMapper;
import com.lumiyaviewer.lumiya.ui.settings.ThemeChangedEvent;

public class ThemedActivity extends AppCompatActivity
{

    private int selectedThemeId;

    public ThemedActivity()
    {
        selectedThemeId = -1;
    }

    protected boolean isLightTheme()
    {
        return selectedThemeId != 0x7f0b002b;
    }

    protected void onCreate(Bundle bundle)
    {
        selectedThemeId = GlobalOptions.getInstance().getThemeResourceId();
        Debug.Printf("Theme: activity theme 0x%x", new Object[] {
            Integer.valueOf(selectedThemeId)
        });
        int i = selectedThemeId;
        if (this instanceof ThemeMapper)
        {
            i = ((ThemeMapper)this).mapThemeResourceId(i);
        }
        setTheme(i);
        super.onCreate(bundle);
    }

    protected void onResume()
    {
        super.onResume();
        int i = GlobalOptions.getInstance().getThemeResourceId();
        Debug.Printf("Theme: resume, new activity theme 0x%x", new Object[] {
            Integer.valueOf(i)
        });
        if (selectedThemeId != i && selectedThemeId != -1)
        {
            onThemeChangedEvent(new ThemeChangedEvent(i));
        }
    }

    protected void onStart()
    {
        super.onStart();
        EventBus.getInstance().subscribe(this);
    }

    protected void onStop()
    {
        EventBus.getInstance().unsubscribeActivity(this);
        super.onStop();
    }

    public void onThemeChangedEvent(ThemeChangedEvent themechangedevent)
    {
        Debug.Printf("Theme: old theme id 0x%x, new theme id 0x%x", new Object[] {
            Integer.valueOf(selectedThemeId), Integer.valueOf(themechangedevent.themeResourceId)
        });
        if (android.os.Build.VERSION.SDK_INT >= 11)
        {
            recreate();
            return;
        } else
        {
            themechangedevent = new Intent(getIntent());
            finish();
            startActivity(themechangedevent);
            return;
        }
    }
}
