// 
// Decompiled by Procyon v0.6.0
// 

package com.google.vr.cardboard;

import android.content.ContentProviderClient;
import android.content.pm.ProviderInfo;
import java.util.Iterator;
import android.content.pm.ResolveInfo;
import java.util.ArrayList;
import android.content.Intent;
import android.os.Build$VERSION;
import java.util.List;
import android.content.Context;

public final class VrParamsProviderFactory
{
    private static final boolean DEBUG = false;
    private static final String TAG;
    private static VrParamsProvider providerForTesting;
    
    static {
        TAG = VrParamsProviderFactory.class.getSimpleName();
    }
    
    public static VrParamsProvider create(final Context context) {
        if (VrParamsProviderFactory.providerForTesting != null) {
            return VrParamsProviderFactory.providerForTesting;
        }
        final ContentProviderClientHandle tryToGetContentProviderClientHandle = tryToGetContentProviderClientHandle(context);
        if (tryToGetContentProviderClientHandle == null) {
            return new LegacyVrParamsProvider();
        }
        return new ContentProviderVrParamsProvider(tryToGetContentProviderClientHandle.client, tryToGetContentProviderClientHandle.authority);
    }
    
    private static List<String> getValidContentProviderAuthorities(final Context context) {
        if (Build$VERSION.SDK_INT < 19) {
            return null;
        }
        final List queryIntentContentProviders = context.getPackageManager().queryIntentContentProviders(new Intent("android.content.action.VR_SETTINGS_PROVIDER"), 0);
        if (queryIntentContentProviders != null && !queryIntentContentProviders.isEmpty()) {
            final ArrayList list = new ArrayList();
            final Iterator iterator = queryIntentContentProviders.iterator();
            while (iterator.hasNext()) {
                final ProviderInfo providerInfo = ((ResolveInfo)iterator.next()).providerInfo;
                if (PackageUtils.isGooglePackage(providerInfo.packageName)) {
                    list.add(providerInfo.authority);
                }
            }
            return list;
        }
        return null;
    }
    
    public static boolean isContentProviderAvailable(final Context context) {
        if (VrParamsProviderFactory.providerForTesting != null && VrParamsProviderFactory.providerForTesting instanceof ContentProviderVrParamsProvider) {
            return true;
        }
        final List<String> validContentProviderAuthorities = getValidContentProviderAuthorities(context);
        return validContentProviderAuthorities != null && !validContentProviderAuthorities.isEmpty();
    }
    
    public static void setProviderForTesting(final VrParamsProvider providerForTesting) {
        VrParamsProviderFactory.providerForTesting = providerForTesting;
    }
    
    public static ContentProviderClientHandle tryToGetContentProviderClientHandle(final Context context) {
        final List<String> validContentProviderAuthorities = getValidContentProviderAuthorities(context);
        if (validContentProviderAuthorities != null) {
            for (final String s : validContentProviderAuthorities) {
                final ContentProviderClient acquireContentProviderClient = context.getContentResolver().acquireContentProviderClient(s);
                if (acquireContentProviderClient != null) {
                    return new ContentProviderClientHandle(acquireContentProviderClient, s);
                }
            }
        }
        return null;
    }
    
    public static class ContentProviderClientHandle
    {
        public final String authority;
        public final ContentProviderClient client;
        
        ContentProviderClientHandle(final ContentProviderClient client, final String authority) {
            this.client = client;
            this.authority = authority;
        }
    }
}
