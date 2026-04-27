// 
// Decompiled by Procyon v0.6.0
// 

package android.support.v7.util;

public interface ListUpdateCallback
{
    void onChanged(final int p0, final int p1, final Object p2);
    
    void onInserted(final int p0, final int p1);
    
    void onMoved(final int p0, final int p1);
    
    void onRemoved(final int p0, final int p1);
}
