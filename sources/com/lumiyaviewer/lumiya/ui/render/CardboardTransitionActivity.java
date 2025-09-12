// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.render;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.render.TextureMemoryTracker;
import com.lumiyaviewer.lumiya.ui.common.ActivityUtils;

// Referenced classes of package com.lumiyaviewer.lumiya.ui.render:
//            CardboardActivity

public class CardboardTransitionActivity extends AppCompatActivity
{

    private static final int MAX_WAIT_ATTEMPTS = 15;
    private static final long WAIT_INTERVAL = 250L;
    private Handler handler;
    private int waitAttempts;

    public CardboardTransitionActivity()
    {
        handler = new Handler();
        waitAttempts = 0;
    }

    private void tryToStartCardboard()
    {
        if (waitAttempts >= 15 || TextureMemoryTracker.hasActiveRenderer() ^ true)
        {
            handler.postDelayed(new _2D_.Lambda._cls4MERJxt3ZMMK7daj1OhYLtxY69Y(this), 250L);
            return;
        } else
        {
            Debug.Printf("Cardboard: EGL renderer still active.", new Object[0]);
            waitAttempts = waitAttempts + 1;
            handler.postDelayed(new _2D_.Lambda._cls4MERJxt3ZMMK7daj1OhYLtxY69Y._cls1(this), 250L);
            return;
        }
    }

    void _2D_com_lumiyaviewer_lumiya_ui_render_CardboardTransitionActivity_2D_mthref_2D_0()
    {
        tryToStartCardboard();
    }

    void lambda$_2D_com_lumiyaviewer_lumiya_ui_render_CardboardTransitionActivity_1411()
    {
        java.util.UUID uuid = ActivityUtils.getActiveAgentID(getIntent());
        Intent intent = new Intent(this, com/lumiyaviewer/lumiya/ui/render/CardboardActivity);
        ActivityUtils.setActiveAgentID(intent, uuid);
        intent.addFlags(0x1000000);
        startActivity(intent);
        finish();
    }

    protected void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);
        requestWindowFeature(1);
        getWindow().setFlags(1024, 1024);
        setContentView(0x7f040023);
    }

    protected void onResume()
    {
        super.onResume();
        tryToStartCardboard();
    }
}
