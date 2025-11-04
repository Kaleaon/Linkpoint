// 
// Decompiled by Procyon v0.6.0
// 

package com.google.vr.cardboard;

import android.app.PendingIntent;
import android.nfc.Tag;
import android.util.Log;
import android.content.Intent;
import android.app.Activity;
import android.content.IntentFilter;
import android.content.BroadcastReceiver;
import android.nfc.NfcAdapter;
import android.content.Context;

public class NFCUtils
{
    private static final String TAG;
    Context context;
    NfcAdapter nfcAdapter;
    BroadcastReceiver nfcBroadcastReceiver;
    IntentFilter[] nfcIntentFilters;
    
    static {
        TAG = NFCUtils.class.getSimpleName();
    }
    
    private IntentFilter createNfcIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.nfc.action.NDEF_DISCOVERED");
        intentFilter.addAction("android.nfc.action.TECH_DISCOVERED");
        intentFilter.addAction("android.nfc.action.TAG_DISCOVERED");
        return intentFilter;
    }
    
    protected boolean isNFCEnabled() {
        return this.nfcAdapter != null && this.nfcAdapter.isEnabled();
    }
    
    public void onCreate(final Activity activity) {
        this.context = activity.getApplicationContext();
        this.nfcAdapter = NfcAdapter.getDefaultAdapter(this.context);
        this.nfcBroadcastReceiver = new BroadcastReceiver() {
            public void onReceive(final Context context, final Intent intent) {
                Log.i(NFCUtils.TAG, "Got an NFC tag!");
                NFCUtils.this.onNFCTagDetected((Tag)intent.getParcelableExtra("android.nfc.extra.TAG"));
            }
        };
        final IntentFilter nfcIntentFilter = this.createNfcIntentFilter();
        nfcIntentFilter.addDataScheme("cardboard");
        final IntentFilter nfcIntentFilter2 = this.createNfcIntentFilter();
        nfcIntentFilter2.addDataScheme("http");
        nfcIntentFilter2.addDataAuthority("goo.gl", (String)null);
        final IntentFilter nfcIntentFilter3 = this.createNfcIntentFilter();
        nfcIntentFilter3.addDataScheme("http");
        nfcIntentFilter3.addDataAuthority("google.com", (String)null);
        nfcIntentFilter3.addDataPath("/cardboard/cfg.*", 2);
        this.nfcIntentFilters = new IntentFilter[] { nfcIntentFilter, nfcIntentFilter2, nfcIntentFilter3 };
    }
    
    protected void onNFCTagDetected(final Tag tag) {
    }
    
    public void onPause(final Activity activity) {
        if (this.isNFCEnabled()) {
            this.nfcAdapter.disableForegroundDispatch(activity);
        }
        activity.unregisterReceiver(this.nfcBroadcastReceiver);
    }
    
    public void onResume(final Activity activity) {
        activity.registerReceiver(this.nfcBroadcastReceiver, this.createNfcIntentFilter());
        final Intent intent = new Intent("android.nfc.action.NDEF_DISCOVERED");
        intent.setPackage(activity.getPackageName());
        final PendingIntent broadcast = PendingIntent.getBroadcast(this.context, 0, intent, 0);
        if (this.isNFCEnabled()) {
            this.nfcAdapter.enableForegroundDispatch(activity, broadcast, this.nfcIntentFilters, (String[][])null);
        }
    }
}
