// 
// Decompiled by Procyon v0.6.0
// 

package android.support.v4.app;

import java.util.HashMap;
import android.content.ClipDescription;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;
import android.content.ClipData;
import android.os.Bundle;
import android.net.Uri;
import java.util.Map;
import android.content.Intent;
import android.support.annotation.RequiresApi;

@RequiresApi(16)
class RemoteInputCompatJellybean
{
    private static final String EXTRA_DATA_TYPE_RESULTS_DATA = "android.remoteinput.dataTypeResultsData";
    private static final String KEY_ALLOWED_DATA_TYPES = "allowedDataTypes";
    private static final String KEY_ALLOW_FREE_FORM_INPUT = "allowFreeFormInput";
    private static final String KEY_CHOICES = "choices";
    private static final String KEY_EXTRAS = "extras";
    private static final String KEY_LABEL = "label";
    private static final String KEY_RESULT_KEY = "resultKey";
    
    public static void addDataResultToIntent(final RemoteInput remoteInput, final Intent intent, final Map<String, Uri> map) {
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
        int i = 0;
        Intent clipDataIntentFromIntent = getClipDataIntentFromIntent(intent);
        if (clipDataIntentFromIntent == null) {
            clipDataIntentFromIntent = new Intent();
        }
        Bundle bundleExtra = clipDataIntentFromIntent.getBundleExtra("android.remoteinput.resultsData");
        if (bundleExtra == null) {
            bundleExtra = new Bundle();
        }
        while (i < array.length) {
            final RemoteInputCompatBase.RemoteInput remoteInput = array[i];
            final Object value = bundle.get(remoteInput.getResultKey());
            if (value instanceof CharSequence) {
                bundleExtra.putCharSequence(remoteInput.getResultKey(), (CharSequence)value);
            }
            ++i;
        }
        clipDataIntentFromIntent.putExtra("android.remoteinput.resultsData", bundleExtra);
        intent.setClipData(ClipData.newIntent((CharSequence)"android.remoteinput.results", clipDataIntentFromIntent));
    }
    
    static RemoteInputCompatBase.RemoteInput fromBundle(final Bundle bundle, final RemoteInputCompatBase.RemoteInput.Factory factory) {
        final ArrayList stringArrayList = bundle.getStringArrayList("allowedDataTypes");
        final HashSet set = new HashSet();
        if (stringArrayList != null) {
            final Iterator iterator = stringArrayList.iterator();
            while (iterator.hasNext()) {
                set.add(iterator.next());
            }
        }
        return factory.build(bundle.getString("resultKey"), bundle.getCharSequence("label"), bundle.getCharSequenceArray("choices"), bundle.getBoolean("allowFreeFormInput"), bundle.getBundle("extras"), set);
    }
    
    static RemoteInputCompatBase.RemoteInput[] fromBundleArray(final Bundle[] array, final RemoteInputCompatBase.RemoteInput.Factory factory) {
        if (array != null) {
            final RemoteInputCompatBase.RemoteInput[] array2 = factory.newArray(array.length);
            for (int i = 0; i < array.length; ++i) {
                array2[i] = fromBundle(array[i], factory);
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
    
    static Bundle getResultsFromIntent(Intent clipDataIntentFromIntent) {
        clipDataIntentFromIntent = getClipDataIntentFromIntent(clipDataIntentFromIntent);
        if (clipDataIntentFromIntent != null) {
            return (Bundle)clipDataIntentFromIntent.getExtras().getParcelable("android.remoteinput.resultsData");
        }
        return null;
    }
    
    static Bundle toBundle(final RemoteInputCompatBase.RemoteInput remoteInput) {
        final Bundle bundle = new Bundle();
        bundle.putString("resultKey", remoteInput.getResultKey());
        bundle.putCharSequence("label", remoteInput.getLabel());
        bundle.putCharSequenceArray("choices", remoteInput.getChoices());
        bundle.putBoolean("allowFreeFormInput", remoteInput.getAllowFreeFormInput());
        bundle.putBundle("extras", remoteInput.getExtras());
        final Set<String> allowedDataTypes = remoteInput.getAllowedDataTypes();
        if (allowedDataTypes != null && !allowedDataTypes.isEmpty()) {
            final ArrayList list = new ArrayList<String>(allowedDataTypes.size());
            final Iterator iterator = allowedDataTypes.iterator();
            while (iterator.hasNext()) {
                list.add((String)iterator.next());
            }
            bundle.putStringArrayList("allowedDataTypes", list);
        }
        return bundle;
    }
    
    static Bundle[] toBundleArray(final RemoteInputCompatBase.RemoteInput[] array) {
        if (array != null) {
            final Bundle[] array2 = new Bundle[array.length];
            for (int i = 0; i < array.length; ++i) {
                array2[i] = toBundle(array[i]);
            }
            return array2;
        }
        return null;
    }
}
