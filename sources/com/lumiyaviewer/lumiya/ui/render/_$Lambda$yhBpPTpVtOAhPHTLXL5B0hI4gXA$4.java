/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  android.view.MotionEvent
 *  android.view.View
 *  android.view.View$OnTouchListener
 */
package com.lumiyaviewer.lumiya.ui.render;

import android.view.MotionEvent;
import android.view.View;
import com.lumiyaviewer.lumiya.ui.render.CardboardActivity;

/*
 * Illegal identifiers - consider using --renameillegalidents true
 */
final class _$Lambda$yhBpPTpVtOAhPHTLXL5B0hI4gXA$4
implements View.OnTouchListener {
    private final Object -$f0;

    private final /* synthetic */ boolean $m$0(View view, MotionEvent motionEvent) {
        return ((CardboardActivity)this.-$f0).-com_lumiyaviewer_lumiya_ui_render_CardboardActivity-mthref-7(view, motionEvent);
    }

    public /* synthetic */ _$Lambda$yhBpPTpVtOAhPHTLXL5B0hI4gXA$4(Object object) {
        this.-$f0 = object;
    }

    public final boolean onTouch(View view, MotionEvent motionEvent) {
        return this.$m$0(view, motionEvent);
    }
}
