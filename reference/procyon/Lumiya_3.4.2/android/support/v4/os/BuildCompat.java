// 
// Decompiled by Procyon v0.6.0
// 

package android.support.v4.os;

import android.os.Build$VERSION;

public class BuildCompat
{
    private BuildCompat() {
    }
    
    @Deprecated
    public static boolean isAtLeastN() {
        return Build$VERSION.SDK_INT >= 24;
    }
    
    @Deprecated
    public static boolean isAtLeastNMR1() {
        return Build$VERSION.SDK_INT >= 25;
    }
    
    public static boolean isAtLeastO() {
        return Build$VERSION.SDK_INT >= 26;
    }
    
    public static boolean isAtLeastOMR1() {
        boolean b = false;
        if (Build$VERSION.CODENAME.startsWith("OMR") || isAtLeastP()) {
            b = true;
        }
        return b;
    }
    
    public static boolean isAtLeastP() {
        return Build$VERSION.CODENAME.equals("P");
    }
}
