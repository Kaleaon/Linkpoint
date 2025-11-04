// 
// Decompiled by Procyon v0.6.0
// 

package android.support.v4.hardware.display;

import android.hardware.display.DisplayManager;
import android.support.annotation.RequiresApi;
import android.view.WindowManager;
import android.view.Display;
import android.os.Build$VERSION;
import android.content.Context;
import java.util.WeakHashMap;

public abstract class DisplayManagerCompat
{
    public static final String DISPLAY_CATEGORY_PRESENTATION = "android.hardware.display.category.PRESENTATION";
    private static final WeakHashMap<Context, DisplayManagerCompat> sInstances;
    
    static {
        sInstances = new WeakHashMap<Context, DisplayManagerCompat>();
    }
    
    DisplayManagerCompat() {
    }
    
    public static DisplayManagerCompat getInstance(final Context context) {
        while (true) {
            while (true) {
                Label_0059: {
                    synchronized (DisplayManagerCompat.sInstances) {
                        DisplayManagerCompat value = DisplayManagerCompat.sInstances.get(context);
                        if (value == null) {
                            if (Build$VERSION.SDK_INT >= 17) {
                                break Label_0059;
                            }
                            value = new DisplayManagerCompatApi14Impl(context);
                            DisplayManagerCompat.sInstances.put(context, value);
                        }
                        return value;
                    }
                }
                DisplayManagerCompat value = new DisplayManagerCompatApi17Impl(context);
                continue;
            }
        }
    }
    
    public abstract Display getDisplay(final int p0);
    
    public abstract Display[] getDisplays();
    
    public abstract Display[] getDisplays(final String p0);
    
    private static class DisplayManagerCompatApi14Impl extends DisplayManagerCompat
    {
        private final WindowManager mWindowManager;
        
        DisplayManagerCompatApi14Impl(final Context context) {
            this.mWindowManager = (WindowManager)context.getSystemService("window");
        }
        
        @Override
        public Display getDisplay(final int n) {
            final Display defaultDisplay = this.mWindowManager.getDefaultDisplay();
            if (defaultDisplay.getDisplayId() != n) {
                return null;
            }
            return defaultDisplay;
        }
        
        @Override
        public Display[] getDisplays() {
            return new Display[] { this.mWindowManager.getDefaultDisplay() };
        }
        
        @Override
        public Display[] getDisplays(final String s) {
            Display[] displays;
            if (s != null) {
                displays = new Display[0];
            }
            else {
                displays = this.getDisplays();
            }
            return displays;
        }
    }
    
    @RequiresApi(17)
    private static class DisplayManagerCompatApi17Impl extends DisplayManagerCompat
    {
        private final DisplayManager mDisplayManager;
        
        DisplayManagerCompatApi17Impl(final Context context) {
            this.mDisplayManager = (DisplayManager)context.getSystemService("display");
        }
        
        @Override
        public Display getDisplay(final int n) {
            return this.mDisplayManager.getDisplay(n);
        }
        
        @Override
        public Display[] getDisplays() {
            return this.mDisplayManager.getDisplays();
        }
        
        @Override
        public Display[] getDisplays(final String s) {
            return this.mDisplayManager.getDisplays(s);
        }
    }
}
