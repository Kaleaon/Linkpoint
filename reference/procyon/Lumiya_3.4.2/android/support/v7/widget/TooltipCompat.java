// 
// Decompiled by Procyon v0.6.0
// 

package android.support.v7.widget;

import android.annotation.TargetApi;
import android.support.annotation.Nullable;
import android.support.annotation.NonNull;
import android.view.View;
import android.os.Build$VERSION;

public class TooltipCompat
{
    private static final ViewCompatImpl IMPL;
    
    static {
        if (Build$VERSION.SDK_INT < 26) {
            IMPL = (ViewCompatImpl)new BaseViewCompatImpl();
        }
        else {
            IMPL = (ViewCompatImpl)new Api26ViewCompatImpl();
        }
    }
    
    private TooltipCompat() {
    }
    
    public static void setTooltipText(@NonNull final View view, @Nullable final CharSequence charSequence) {
        TooltipCompat.IMPL.setTooltipText(view, charSequence);
    }
    
    @TargetApi(26)
    private static class Api26ViewCompatImpl implements ViewCompatImpl
    {
        @Override
        public void setTooltipText(@NonNull final View view, @Nullable final CharSequence tooltipText) {
            view.setTooltipText(tooltipText);
        }
    }
    
    private interface ViewCompatImpl
    {
        void setTooltipText(@NonNull final View p0, @Nullable final CharSequence p1);
    }
    
    private static class BaseViewCompatImpl implements ViewCompatImpl
    {
        @Override
        public void setTooltipText(@NonNull final View view, @Nullable final CharSequence charSequence) {
            TooltipCompatHandler.setTooltipText(view, charSequence);
        }
    }
}
