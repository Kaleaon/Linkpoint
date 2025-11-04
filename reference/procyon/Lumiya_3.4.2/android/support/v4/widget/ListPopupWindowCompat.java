// 
// Decompiled by Procyon v0.6.0
// 

package android.support.v4.widget;

import android.os.Build$VERSION;
import android.view.View$OnTouchListener;
import android.view.View;
import android.widget.ListPopupWindow;

public final class ListPopupWindowCompat
{
    private ListPopupWindowCompat() {
    }
    
    public static View$OnTouchListener createDragToOpenListener(final ListPopupWindow listPopupWindow, final View view) {
        if (Build$VERSION.SDK_INT < 19) {
            return null;
        }
        return listPopupWindow.createDragToOpenListener(view);
    }
    
    @Deprecated
    public static View$OnTouchListener createDragToOpenListener(final Object o, final View view) {
        return createDragToOpenListener((ListPopupWindow)o, view);
    }
}
