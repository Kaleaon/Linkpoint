// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.ui.render;

import android.support.annotation.Nullable;
import android.os.Bundle;
import java.util.UUID;
import android.content.Context;
import android.content.Intent;
import com.lumiyaviewer.lumiya.ui.common.ActivityUtils;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.render.TextureMemoryTracker;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

public class CardboardTransitionActivity extends AppCompatActivity
{
    private static final int MAX_WAIT_ATTEMPTS = 15;
    private static final long WAIT_INTERVAL = 250L;
    private Handler handler;
    private int waitAttempts;
    
    public CardboardTransitionActivity() {
        this.handler = new Handler();
        this.waitAttempts = 0;
    }
    
    private void tryToStartCardboard() {
        if (this.waitAttempts >= 15 || (TextureMemoryTracker.hasActiveRenderer() ^ true)) {
            this.handler.postDelayed((Runnable)new _$Lambda$4MERJxt3ZMMK7daj1OhYLtxY69Y(this), 250L);
        }
        else {
            Debug.Printf("Cardboard: EGL renderer still active.", new Object[0]);
            ++this.waitAttempts;
            this.handler.postDelayed((Runnable)new _$Lambda$4MERJxt3ZMMK7daj1OhYLtxY69Y$1(this), 250L);
        }
    }
    
    @Override
    protected void onCreate(@Nullable final Bundle bundle) {
        super.onCreate(bundle);
        this.requestWindowFeature(1);
        this.getWindow().setFlags(1024, 1024);
        this.setContentView(2130968611);
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        this.tryToStartCardboard();
    }
}
