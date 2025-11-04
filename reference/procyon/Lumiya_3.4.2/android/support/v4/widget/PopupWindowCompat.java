// 
// Decompiled by Procyon v0.6.0
// 

package android.support.v4.widget;

import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewCompat;
import java.lang.reflect.Method;
import android.util.Log;
import java.lang.reflect.Field;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.widget.PopupWindow;
import android.os.Build$VERSION;

public final class PopupWindowCompat
{
    static final PopupWindowCompatBaseImpl IMPL;
    
    static {
        if (Build$VERSION.SDK_INT < 23) {
            if (Build$VERSION.SDK_INT < 21) {
                if (Build$VERSION.SDK_INT < 19) {
                    IMPL = new PopupWindowCompatBaseImpl();
                }
                else {
                    IMPL = (PopupWindowCompatBaseImpl)new PopupWindowCompatApi19Impl();
                }
            }
            else {
                IMPL = (PopupWindowCompatBaseImpl)new PopupWindowCompatApi21Impl();
            }
        }
        else {
            IMPL = (PopupWindowCompatBaseImpl)new PopupWindowCompatApi23Impl();
        }
    }
    
    private PopupWindowCompat() {
    }
    
    public static boolean getOverlapAnchor(final PopupWindow popupWindow) {
        return PopupWindowCompat.IMPL.getOverlapAnchor(popupWindow);
    }
    
    public static int getWindowLayoutType(final PopupWindow popupWindow) {
        return PopupWindowCompat.IMPL.getWindowLayoutType(popupWindow);
    }
    
    public static void setOverlapAnchor(final PopupWindow popupWindow, final boolean b) {
        PopupWindowCompat.IMPL.setOverlapAnchor(popupWindow, b);
    }
    
    public static void setWindowLayoutType(final PopupWindow popupWindow, final int n) {
        PopupWindowCompat.IMPL.setWindowLayoutType(popupWindow, n);
    }
    
    public static void showAsDropDown(final PopupWindow popupWindow, final View view, final int n, final int n2, final int n3) {
        PopupWindowCompat.IMPL.showAsDropDown(popupWindow, view, n, n2, n3);
    }
    
    @RequiresApi(19)
    static class PopupWindowCompatApi19Impl extends PopupWindowCompatBaseImpl
    {
        @Override
        public void showAsDropDown(final PopupWindow popupWindow, final View view, final int n, final int n2, final int n3) {
            popupWindow.showAsDropDown(view, n, n2, n3);
        }
    }
    
    @RequiresApi(21)
    static class PopupWindowCompatApi21Impl extends PopupWindowCompatApi19Impl
    {
        private static final String TAG = "PopupWindowCompatApi21";
        private static Field sOverlapAnchorField;
        
        static {
            try {
                (PopupWindowCompatApi21Impl.sOverlapAnchorField = PopupWindow.class.getDeclaredField("mOverlapAnchor")).setAccessible(true);
            }
            catch (final NoSuchFieldException ex) {
                Log.i("PopupWindowCompatApi21", "Could not fetch mOverlapAnchor field from PopupWindow", (Throwable)ex);
            }
        }
        
        @Override
        public boolean getOverlapAnchor(final PopupWindow obj) {
            if (PopupWindowCompatApi21Impl.sOverlapAnchorField != null) {
                try {
                    return (boolean)PopupWindowCompatApi21Impl.sOverlapAnchorField.get(obj);
                }
                catch (final IllegalAccessException ex) {
                    Log.i("PopupWindowCompatApi21", "Could not get overlap anchor field in PopupWindow", (Throwable)ex);
                }
            }
            return false;
        }
        
        @Override
        public void setOverlapAnchor(final PopupWindow obj, final boolean b) {
            if (PopupWindowCompatApi21Impl.sOverlapAnchorField != null) {
                try {
                    PopupWindowCompatApi21Impl.sOverlapAnchorField.set(obj, b);
                }
                catch (final IllegalAccessException ex) {
                    Log.i("PopupWindowCompatApi21", "Could not set overlap anchor field in PopupWindow", (Throwable)ex);
                }
            }
        }
    }
    
    @RequiresApi(23)
    static class PopupWindowCompatApi23Impl extends PopupWindowCompatApi21Impl
    {
        @Override
        public boolean getOverlapAnchor(final PopupWindow popupWindow) {
            return popupWindow.getOverlapAnchor();
        }
        
        @Override
        public int getWindowLayoutType(final PopupWindow popupWindow) {
            return popupWindow.getWindowLayoutType();
        }
        
        @Override
        public void setOverlapAnchor(final PopupWindow popupWindow, final boolean overlapAnchor) {
            popupWindow.setOverlapAnchor(overlapAnchor);
        }
        
        @Override
        public void setWindowLayoutType(final PopupWindow popupWindow, final int windowLayoutType) {
            popupWindow.setWindowLayoutType(windowLayoutType);
        }
    }
    
    static class PopupWindowCompatBaseImpl
    {
        private static Method sGetWindowLayoutTypeMethod;
        private static boolean sGetWindowLayoutTypeMethodAttempted;
        private static Method sSetWindowLayoutTypeMethod;
        private static boolean sSetWindowLayoutTypeMethodAttempted;
        
        public boolean getOverlapAnchor(final PopupWindow popupWindow) {
            return false;
        }
        
        public int getWindowLayoutType(final PopupWindow obj) {
            Label_0014: {
                if (!PopupWindowCompatBaseImpl.sGetWindowLayoutTypeMethodAttempted) {
                    break Label_0014;
                }
            Label_0035_Outer:
                while (true) {
                    Label_0042: {
                        if (PopupWindowCompatBaseImpl.sGetWindowLayoutTypeMethod != null) {
                            break Label_0042;
                        }
                        return 0;
                        while (true) {
                            try {
                                (PopupWindowCompatBaseImpl.sGetWindowLayoutTypeMethod = PopupWindow.class.getDeclaredMethod("getWindowLayoutType", (Class<?>[])new Class[0])).setAccessible(true);
                                PopupWindowCompatBaseImpl.sGetWindowLayoutTypeMethodAttempted = true;
                                continue Label_0035_Outer;
                                try {
                                    return (int)PopupWindowCompatBaseImpl.sGetWindowLayoutTypeMethod.invoke(obj, new Object[0]);
                                }
                                catch (final Exception obj) {}
                            }
                            catch (final Exception ex) {
                                continue;
                            }
                            break;
                        }
                    }
                    break;
                }
            }
        }
        
        public void setOverlapAnchor(final PopupWindow popupWindow, final boolean b) {
        }
        
        public void setWindowLayoutType(final PopupWindow obj, final int i) {
            Label_0013: {
                if (!PopupWindowCompatBaseImpl.sSetWindowLayoutTypeMethodAttempted) {
                    break Label_0013;
                }
            Label_0040_Outer:
                while (true) {
                    Label_0047: {
                        if (PopupWindowCompatBaseImpl.sSetWindowLayoutTypeMethod != null) {
                            break Label_0047;
                        }
                        return;
                        while (true) {
                            try {
                                (PopupWindowCompatBaseImpl.sSetWindowLayoutTypeMethod = PopupWindow.class.getDeclaredMethod("setWindowLayoutType", Integer.TYPE)).setAccessible(true);
                                PopupWindowCompatBaseImpl.sSetWindowLayoutTypeMethodAttempted = true;
                                continue Label_0040_Outer;
                                try {
                                    PopupWindowCompatBaseImpl.sSetWindowLayoutTypeMethod.invoke(obj, i);
                                }
                                catch (final Exception obj) {}
                            }
                            catch (final Exception ex) {
                                continue;
                            }
                            break;
                        }
                    }
                    break;
                }
            }
        }
        
        public void showAsDropDown(final PopupWindow popupWindow, final View view, int n, final int n2, final int n3) {
            if ((GravityCompat.getAbsoluteGravity(n3, ViewCompat.getLayoutDirection(view)) & 0x7) == 0x5) {
                n -= popupWindow.getWidth() - view.getWidth();
            }
            popupWindow.showAsDropDown(view, n, n2);
        }
    }
}
