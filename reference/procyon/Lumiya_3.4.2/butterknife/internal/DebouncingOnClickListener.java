// 
// Decompiled by Procyon v0.6.0
// 

package butterknife.internal;

import android.view.View;
import android.view.View$OnClickListener;

public abstract class DebouncingOnClickListener implements View$OnClickListener
{
    private static final Runnable ENABLE_AGAIN;
    static boolean enabled;
    
    static {
        DebouncingOnClickListener.enabled = true;
        ENABLE_AGAIN = new Runnable() {
            @Override
            public void run() {
                DebouncingOnClickListener.enabled = true;
            }
        };
    }
    
    public abstract void doClick(final View p0);
    
    public final void onClick(final View view) {
        if (DebouncingOnClickListener.enabled) {
            DebouncingOnClickListener.enabled = false;
            view.post(DebouncingOnClickListener.ENABLE_AGAIN);
            this.doClick(view);
        }
    }
}
