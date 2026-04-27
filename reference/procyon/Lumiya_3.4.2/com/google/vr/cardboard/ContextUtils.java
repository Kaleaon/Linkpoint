// 
// Decompiled by Procyon v0.6.0
// 

package com.google.vr.cardboard;

import android.content.ContextWrapper;
import android.app.Activity;
import android.content.Context;

public class ContextUtils
{
    public static boolean canGetActivity(final Context context) {
        return getActivity(context) != null;
    }
    
    public static Activity getActivity(Context baseContext) {
        while (baseContext != null) {
            if (baseContext instanceof Activity) {
                return (Activity)baseContext;
            }
            if (!(baseContext instanceof ContextWrapper)) {
                return null;
            }
            baseContext = ((ContextWrapper)baseContext).getBaseContext();
        }
        return null;
    }
}
