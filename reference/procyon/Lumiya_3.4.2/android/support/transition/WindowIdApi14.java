// 
// Decompiled by Procyon v0.6.0
// 

package android.support.transition;

import android.os.IBinder;
import android.support.annotation.RequiresApi;

@RequiresApi(14)
class WindowIdApi14 implements WindowIdImpl
{
    private final IBinder mToken;
    
    WindowIdApi14(final IBinder mToken) {
        this.mToken = mToken;
    }
    
    @Override
    public boolean equals(final Object o) {
        boolean b = false;
        if (o instanceof WindowIdApi14 && ((WindowIdApi14)o).mToken.equals(this.mToken)) {
            b = true;
        }
        return b;
    }
    
    @Override
    public int hashCode() {
        return this.mToken.hashCode();
    }
}
