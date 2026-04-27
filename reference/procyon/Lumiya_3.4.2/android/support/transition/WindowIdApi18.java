// 
// Decompiled by Procyon v0.6.0
// 

package android.support.transition;

import android.support.annotation.NonNull;
import android.view.View;
import android.view.WindowId;
import android.support.annotation.RequiresApi;

@RequiresApi(18)
class WindowIdApi18 implements WindowIdImpl
{
    private final WindowId mWindowId;
    
    WindowIdApi18(@NonNull final View view) {
        this.mWindowId = view.getWindowId();
    }
    
    @Override
    public boolean equals(final Object o) {
        boolean b = false;
        if (o instanceof WindowIdApi18 && ((WindowIdApi18)o).mWindowId.equals((Object)this.mWindowId)) {
            b = true;
        }
        return b;
    }
    
    @Override
    public int hashCode() {
        return this.mWindowId.hashCode();
    }
}
