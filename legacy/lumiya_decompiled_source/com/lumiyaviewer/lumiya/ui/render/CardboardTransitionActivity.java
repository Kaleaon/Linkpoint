package com.lumiyaviewer.lumiya.ui.render;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.R;
import com.lumiyaviewer.lumiya.render.TextureMemoryTracker;
import com.lumiyaviewer.lumiya.ui.common.ActivityUtils;
import java.util.UUID;
/* loaded from: classes.dex */
public class CardboardTransitionActivity extends AppCompatActivity {
    private static final int MAX_WAIT_ATTEMPTS = 15;
    private static final long WAIT_INTERVAL = 250;
    private Handler handler = new Handler();
    private int waitAttempts = 0;

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: tryToStartCardboard */
    public void m796xf8bcab63() {
        if (this.waitAttempts >= 15 || (!TextureMemoryTracker.hasActiveRenderer())) {
            this.handler.postDelayed(new Runnable() { // from class: com.lumiyaviewer.lumiya.ui.render.-$Lambda$4MERJxt3ZMMK7daj1OhYLtxY69Y
                private final /* synthetic */ void $m$0() {
                    ((CardboardTransitionActivity) this).m797x33a6fc46();
                }

                @Override // java.lang.Runnable
                public final void run() {
                    $m$0();
                }
            }, WAIT_INTERVAL);
            return;
        }
        Debug.Printf("Cardboard: EGL renderer still active.", new Object[0]);
        this.waitAttempts++;
        this.handler.postDelayed(new Runnable() { // from class: com.lumiyaviewer.lumiya.ui.render.-$Lambda$4MERJxt3ZMMK7daj1OhYLtxY69Y.1
            private final /* synthetic */ void $m$0() {
                ((CardboardTransitionActivity) this).m796xf8bcab63();
            }

            @Override // java.lang.Runnable
            public final void run() {
                $m$0();
            }
        }, WAIT_INTERVAL);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_ui_render_CardboardTransitionActivity_1411  reason: not valid java name */
    public /* synthetic */ void m797x33a6fc46() {
        UUID activeAgentID = ActivityUtils.getActiveAgentID(getIntent());
        Intent intent = new Intent(this, CardboardActivity.class);
        ActivityUtils.setActiveAgentID(intent, activeAgentID);
        intent.addFlags(16777216);
        startActivity(intent);
        finish();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.support.v7.app.AppCompatActivity, android.support.v4.app.FragmentActivity, android.support.v4.app.SupportActivity, android.app.Activity
    public void onCreate(@Nullable Bundle bundle) {
        super.onCreate(bundle);
        requestWindowFeature(1);
        getWindow().setFlags(1024, 1024);
        setContentView(R.layout.cardboard_transition_layout);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.support.v4.app.FragmentActivity, android.app.Activity
    public void onResume() {
        super.onResume();
        m796xf8bcab63();
    }
}
