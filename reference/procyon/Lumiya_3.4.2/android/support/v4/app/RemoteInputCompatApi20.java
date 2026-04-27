// 
// Decompiled by Procyon v0.6.0
// 

package android.support.v4.app;

import java.util.Set;
import java.util.HashMap;
import android.content.ClipDescription;
import android.app.RemoteInput$Builder;
import android.app.RemoteInput;
import java.util.Iterator;
import android.content.ClipData;
import android.os.Bundle;
import android.net.Uri;
import java.util.Map;
import android.content.Intent;
import android.support.annotation.RequiresApi;

@RequiresApi(20)
class RemoteInputCompatApi20
{
    private static final String EXTRA_DATA_TYPE_RESULTS_DATA = "android.remoteinput.dataTypeResultsData";
    
    public static void addDataResultToIntent(final RemoteInputCompatBase.RemoteInput remoteInput, final Intent intent, final Map<String, Uri> map) {
        Intent clipDataIntentFromIntent = getClipDataIntentFromIntent(intent);
        if (clipDataIntentFromIntent == null) {
            clipDataIntentFromIntent = new Intent();
        }
        for (final Map.Entry<String, V> entry : map.entrySet()) {
            final String s = entry.getKey();
            final Uri uri = (Uri)entry.getValue();
            if (s != null) {
                Bundle bundleExtra = clipDataIntentFromIntent.getBundleExtra(getExtraResultsKeyForData(s));
                if (bundleExtra == null) {
                    bundleExtra = new Bundle();
                }
                bundleExtra.putString(remoteInput.getResultKey(), uri.toString());
                clipDataIntentFromIntent.putExtra(getExtraResultsKeyForData(s), bundleExtra);
            }
        }
        intent.setClipData(ClipData.newIntent((CharSequence)"android.remoteinput.results", clipDataIntentFromIntent));
    }
    
    static void addResultsToIntent(final RemoteInputCompatBase.RemoteInput[] array, final Intent intent, final Bundle bundle) {
        final Bundle resultsFromIntent = getResultsFromIntent(intent);
        Bundle bundle2 = bundle;
        if (resultsFromIntent != null) {
            resultsFromIntent.putAll(bundle);
            bundle2 = resultsFromIntent;
        }
        for (final RemoteInputCompatBase.RemoteInput remoteInput : array) {
            final Map<String, Uri> dataResultsFromIntent = getDataResultsFromIntent(intent, remoteInput.getResultKey());
            RemoteInput.addResultsToIntent(fromCompat(new RemoteInputCompatBase.RemoteInput[] { remoteInput }), intent, bundle2);
            if (dataResultsFromIntent != null) {
                addDataResultToIntent(remoteInput, intent, dataResultsFromIntent);
            }
        }
    }
    
    static RemoteInput[] fromCompat(final RemoteInputCompatBase.RemoteInput[] array) {
        if (array != null) {
            final RemoteInput[] array2 = new RemoteInput[array.length];
            for (int i = 0; i < array.length; ++i) {
                final RemoteInputCompatBase.RemoteInput remoteInput = array[i];
                array2[i] = new RemoteInput$Builder(remoteInput.getResultKey()).setLabel(remoteInput.getLabel()).setChoices(remoteInput.getChoices()).setAllowFreeFormInput(remoteInput.getAllowFreeFormInput()).addExtras(remoteInput.getExtras()).build();
            }
            return array2;
        }
        return null;
    }
    
    private static Intent getClipDataIntentFromIntent(final Intent intent) {
        final ClipData clipData = intent.getClipData();
        if (clipData == null) {
            return null;
        }
        final ClipDescription description = clipData.getDescription();
        if (!description.hasMimeType("text/vnd.android.intent")) {
            return null;
        }
        if (description.getLabel().equals("android.remoteinput.results")) {
            return clipData.getItemAt(0).getIntent();
        }
        return null;
    }
    
    static Map<String, Uri> getDataResultsFromIntent(final Intent intent, final String s) {
        final Intent clipDataIntentFromIntent = getClipDataIntentFromIntent(intent);
        if (clipDataIntentFromIntent != null) {
            HashMap hashMap = new HashMap();
            for (final String s2 : clipDataIntentFromIntent.getExtras().keySet()) {
                if (s2.startsWith("android.remoteinput.dataTypeResultsData")) {
                    final String substring = s2.substring("android.remoteinput.dataTypeResultsData".length());
                    if (substring == null || substring.isEmpty()) {
                        continue;
                    }
                    final String string = clipDataIntentFromIntent.getBundleExtra(s2).getString(s);
                    if (string == null || string.isEmpty()) {
                        continue;
                    }
                    hashMap.put(substring, Uri.parse(string));
                }
            }
            if (hashMap.isEmpty()) {
                hashMap = null;
            }
            return hashMap;
        }
        return null;
    }
    
    private static String getExtraResultsKeyForData(final String str) {
        return "android.remoteinput.dataTypeResultsData" + str;
    }
    
    static Bundle getResultsFromIntent(final Intent intent) {
        return RemoteInput.getResultsFromIntent(intent);
    }
    
    static RemoteInputCompatBase.RemoteInput[] toCompat(final RemoteInput[] array, final RemoteInputCompatBase.RemoteInput.Factory factory) {
        if (array != null) {
            final RemoteInputCompatBase.RemoteInput[] array2 = factory.newArray(array.length);
            for (int i = 0; i < array.length; ++i) {
                final RemoteInput remoteInput = array[i];
                array2[i] = factory.build(remoteInput.getResultKey(), remoteInput.getLabel(), remoteInput.getChoices(), remoteInput.getAllowFreeFormInput(), remoteInput.getExtras(), null);
            }
            return array2;
        }
        return null;
    }
}
