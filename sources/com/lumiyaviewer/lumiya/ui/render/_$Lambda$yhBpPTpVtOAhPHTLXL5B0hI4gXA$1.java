/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  android.view.MotionEvent
 *  android.view.View
 *  android.view.View$OnHoverListener
 */
package com.lumiyaviewer.lumiya.ui.render;

import android.view.MotionEvent;
import android.view.View;
import com.lumiyaviewer.lumiya.ui.render.CardboardActivity;

/*
 * Illegal identifiers - consider using --renameillegalidents true
 */
final class _$Lambda$yhBpPTpVtOAhPHTLXL5B0hI4gXA$1
implements View.OnHoverListener {
    private final Object -$f0;

    private final /* synthetic */ boolean $m$0(View view, MotionEvent motionEvent) {
        return ((CardboardActivity)this.-$f0).lambda$-com_lumiyaviewer_lumiya_ui_render_CardboardActivity_28338(view, motionEvent);
    }

    public /* synthetic */ _$Lambda$yhBpPTpVtOAhPHTLXL5B0hI4gXA$1(Object object) {
        this.-$f0 = object;
    }

    public final boolean onHover(View view, MotionEvent motionEvent) {
        return this.$m$0(view, motionEvent);
    }
}
