// 
// Decompiled by Procyon v0.6.0
// 

package android.support.v4.text;

import java.lang.reflect.InvocationTargetException;
import java.util.Locale;
import android.util.Log;
import java.lang.reflect.Method;
import android.support.annotation.RequiresApi;

@RequiresApi(14)
class ICUCompatIcs
{
    private static final String TAG = "ICUCompatIcs";
    private static Method sAddLikelySubtagsMethod;
    private static Method sGetScriptMethod;
    
    static {
        try {
            final Class<?> forName = Class.forName("libcore.icu.ICU");
            if (forName != null) {
                ICUCompatIcs.sGetScriptMethod = forName.getMethod("getScript", String.class);
                ICUCompatIcs.sAddLikelySubtagsMethod = forName.getMethod("addLikelySubtags", String.class);
            }
        }
        catch (final Exception ex) {
            ICUCompatIcs.sGetScriptMethod = null;
            ICUCompatIcs.sAddLikelySubtagsMethod = null;
            Log.w("ICUCompatIcs", (Throwable)ex);
        }
    }
    
    private static String addLikelySubtags(Locale string) {
        string = (Locale)string.toString();
        try {
            if (ICUCompatIcs.sAddLikelySubtagsMethod == null) {
                return (String)string;
            }
            return (String)ICUCompatIcs.sAddLikelySubtagsMethod.invoke(null, string);
        }
        catch (final IllegalAccessException ex) {
            Log.w("ICUCompatIcs", (Throwable)ex);
            return (String)string;
        }
        catch (final InvocationTargetException ex2) {
            Log.w("ICUCompatIcs", (Throwable)ex2);
            return (String)string;
        }
    }
    
    private static String getScript(String s) {
        try {
            if (ICUCompatIcs.sGetScriptMethod == null) {
                return null;
            }
            s = (String)ICUCompatIcs.sGetScriptMethod.invoke(null, s);
            return s;
        }
        catch (final IllegalAccessException ex) {
            Log.w("ICUCompatIcs", (Throwable)ex);
            return null;
        }
        catch (final InvocationTargetException ex2) {
            Log.w("ICUCompatIcs", (Throwable)ex2);
            return null;
        }
    }
    
    public static String maximizeAndGetScript(final Locale locale) {
        final String addLikelySubtags = addLikelySubtags(locale);
        if (addLikelySubtags == null) {
            return null;
        }
        return getScript(addLikelySubtags);
    }
}
