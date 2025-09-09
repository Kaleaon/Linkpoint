package com.google.vr.sdk.base;

import android.content.Context;

public class ImplementationSelector {
    public static CardboardViewApi createCardboardViewApi(Context context) {
        return new CardboardViewNativeImpl(context);
    }
}
