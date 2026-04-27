// 
// Decompiled by Procyon v0.6.0
// 

package com.google.vr.sdk.base;

import android.content.Context;

public class ImplementationSelector
{
    public static CardboardViewApi createCardboardViewApi(final Context context) {
        return new CardboardViewNativeImpl(context);
    }
}
