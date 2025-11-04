// 
// Decompiled by Procyon v0.6.0
// 

package butterknife;

import android.support.annotation.UiThread;

public interface Unbinder
{
    public static final Unbinder EMPTY = new Unbinder() {
        @Override
        public void unbind() {
        }
    };
    
    @UiThread
    void unbind();
}
