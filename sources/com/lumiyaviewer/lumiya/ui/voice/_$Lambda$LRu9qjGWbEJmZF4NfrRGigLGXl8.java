/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  android.view.MotionEvent
 *  android.view.View
 *  android.view.View$OnHoverListener
 */
package com.lumiyaviewer.lumiya.ui.voice;

import android.view.MotionEvent;
import android.view.View;
import com.lumiyaviewer.lumiya.ui.render.OnHoverListenerCompat;
import com.lumiyaviewer.lumiya.ui.voice.VoiceStatusView;

/*
 * Illegal identifiers - consider using --renameillegalidents true
 */
final class _$Lambda$LRu9qjGWbEJmZF4NfrRGigLGXl8
implements View.OnHoverListener {
    private final Object -$f0;

    private final /* synthetic */ boolean $m$0(View view, MotionEvent motionEvent) {
        return VoiceStatusView.lambda$-com_lumiyaviewer_lumiya_ui_voice_VoiceStatusView_6407((OnHoverListenerCompat)this.-$f0, view, motionEvent);
    }

    public /* synthetic */ _$Lambda$LRu9qjGWbEJmZF4NfrRGigLGXl8(Object object) {
        this.-$f0 = object;
    }

    public final boolean onHover(View view, MotionEvent motionEvent) {
        return this.$m$0(view, motionEvent);
    }
}
