package com.google.vr.sdk.base.sensors;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.TagLostException;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.os.Handler;
import android.util.Log;
import com.google.vr.sdk.base.GvrViewerParams;
import com.google.vr.sdk.base.PermissionUtils;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
/* loaded from: classes.dex */
public class NfcSensor {
    private static final int MAX_CONNECTION_FAILURES = 1;
    private static final long NFC_POLLING_INTERVAL_MS = 250;
    private static final String TAG = "NfcSensor";
    private static NfcSensor sInstance;
    private final Context context;
    private Ndef currentNdef;
    private Tag currentTag;
    private boolean currentTagIsCardboard;
    private final NfcAdapter nfcAdapter;
    private BroadcastReceiver nfcBroadcastReceiver;
    private Timer nfcDisconnectTimer;
    private IntentFilter[] nfcIntentFilters;
    private int tagConnectionFailures;
    private final List<ListenerHelper> listeners = new ArrayList();
    private final Object tagLock = new Object();

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class ListenerHelper implements OnCardboardNfcListener {
        private Handler handler;
        private OnCardboardNfcListener listener;

        public ListenerHelper(OnCardboardNfcListener onCardboardNfcListener, Handler handler) {
            this.listener = onCardboardNfcListener;
            this.handler = handler;
        }

        public OnCardboardNfcListener getListener() {
            return this.listener;
        }

        @Override // com.google.vr.sdk.base.sensors.NfcSensor.OnCardboardNfcListener
        public void onInsertedIntoGvrViewer(final GvrViewerParams gvrViewerParams) {
            this.handler.post(new Runnable() { // from class: com.google.vr.sdk.base.sensors.NfcSensor.ListenerHelper.1
                @Override // java.lang.Runnable
                public void run() {
                    ListenerHelper.this.listener.onInsertedIntoGvrViewer(gvrViewerParams);
                }
            });
        }

        @Override // com.google.vr.sdk.base.sensors.NfcSensor.OnCardboardNfcListener
        public void onRemovedFromGvrViewer() {
            this.handler.post(new Runnable() { // from class: com.google.vr.sdk.base.sensors.NfcSensor.ListenerHelper.2
                @Override // java.lang.Runnable
                public void run() {
                    ListenerHelper.this.listener.onRemovedFromGvrViewer();
                }
            });
        }
    }

    /* loaded from: classes.dex */
    public interface OnCardboardNfcListener {
        void onInsertedIntoGvrViewer(GvrViewerParams gvrViewerParams);

        void onRemovedFromGvrViewer();
    }

    private NfcSensor(Context context) {
        this.context = context.getApplicationContext();
        if (PermissionUtils.hasPermission(context, "android.permission.NFC")) {
            this.nfcAdapter = NfcAdapter.getDefaultAdapter(context);
        } else {
            this.nfcAdapter = null;
        }
        if (this.nfcAdapter != null) {
            this.nfcBroadcastReceiver = new BroadcastReceiver() { // from class: com.google.vr.sdk.base.sensors.NfcSensor.1
                @Override // android.content.BroadcastReceiver
                public void onReceive(Context context2, Intent intent) {
                    NfcSensor.this.onNfcIntent(intent);
                }
            };
        } else {
            Log.w(TAG, "NFC is not supported on this phone or application doesn't have NFC permission.");
        }
    }

    static /* synthetic */ int access$204(NfcSensor nfcSensor) {
        int i = nfcSensor.tagConnectionFailures + 1;
        nfcSensor.tagConnectionFailures = i;
        return i;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void closeCurrentNfcTag() {
        if (this.nfcDisconnectTimer != null) {
            this.nfcDisconnectTimer.cancel();
        }
        if (this.currentNdef != null) {
            try {
                this.currentNdef.close();
            } catch (IOException e) {
                Log.w(TAG, e.toString());
            }
            this.currentTag = null;
            this.currentNdef = null;
            this.currentTagIsCardboard = false;
        }
    }

    public static NfcSensor getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new NfcSensor(context);
        }
        return sInstance;
    }

    private boolean isCardboardNdefMessage(NdefMessage ndefMessage) {
        if (ndefMessage != null) {
            for (NdefRecord ndefRecord : ndefMessage.getRecords()) {
                if (isCardboardNdefRecord(ndefRecord)) {
                    return true;
                }
            }
            return false;
        }
        return false;
    }

    private boolean isCardboardNdefRecord(NdefRecord ndefRecord) {
        Uri uri;
        return (ndefRecord == null || (uri = ndefRecord.toUri()) == null || !GvrViewerParams.isGvrUri(uri)) ? false : true;
    }

    private void onNewNfcTag(Tag tag) {
        boolean z;
        boolean z2 = false;
        if (tag != null) {
            synchronized (this.tagLock) {
                Tag tag2 = this.currentTag;
                Ndef ndef = this.currentNdef;
                boolean z3 = this.currentTagIsCardboard;
                closeCurrentNfcTag();
                this.currentTag = tag;
                this.currentNdef = Ndef.get(tag);
                if (this.currentNdef == null) {
                    if (z3) {
                        sendDisconnectionEvent();
                    }
                    return;
                }
                if (ndef == null) {
                    z = false;
                } else {
                    byte[] id = this.currentTag.getId();
                    byte[] id2 = tag2.getId();
                    if (id != null && id2 != null && Arrays.equals(id, id2)) {
                        z2 = true;
                    }
                    if (!z2 && z3) {
                        sendDisconnectionEvent();
                        z = z2;
                    } else {
                        z = z2;
                    }
                }
                try {
                    this.currentNdef.connect();
                    NdefMessage cachedNdefMessage = this.currentNdef.getCachedNdefMessage();
                    this.currentTagIsCardboard = isCardboardNdefMessage(cachedNdefMessage);
                    if (!z && this.currentTagIsCardboard) {
                        synchronized (this.listeners) {
                            for (ListenerHelper listenerHelper : this.listeners) {
                                listenerHelper.onInsertedIntoGvrViewer(GvrViewerParams.createFromNfcContents(cachedNdefMessage));
                            }
                        }
                    }
                    if (this.currentTagIsCardboard) {
                        this.tagConnectionFailures = 0;
                        this.nfcDisconnectTimer = new Timer("NFC disconnect timer");
                        this.nfcDisconnectTimer.schedule(new TimerTask() { // from class: com.google.vr.sdk.base.sensors.NfcSensor.2
                            @Override // java.util.TimerTask, java.lang.Runnable
                            public void run() {
                                synchronized (NfcSensor.this.tagLock) {
                                    if (!NfcSensor.this.currentNdef.isConnected()) {
                                        NfcSensor.access$204(NfcSensor.this);
                                        if (NfcSensor.this.tagConnectionFailures > 1) {
                                            NfcSensor.this.closeCurrentNfcTag();
                                            NfcSensor.this.sendDisconnectionEvent();
                                        }
                                    }
                                }
                            }
                        }, NFC_POLLING_INTERVAL_MS, NFC_POLLING_INTERVAL_MS);
                    }
                } catch (Exception e) {
                    String valueOf = String.valueOf(e.toString());
                    Log.e(TAG, valueOf.length() == 0 ? new String("Error reading NFC tag: ") : "Error reading NFC tag: ".concat(valueOf));
                    if (z && z3) {
                        sendDisconnectionEvent();
                    }
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void sendDisconnectionEvent() {
        synchronized (this.listeners) {
            for (ListenerHelper listenerHelper : this.listeners) {
                listenerHelper.onRemovedFromGvrViewer();
            }
        }
    }

    public void addOnCardboardNfcListener(OnCardboardNfcListener onCardboardNfcListener) {
        if (onCardboardNfcListener != null) {
            synchronized (this.listeners) {
                if (this.listeners.isEmpty()) {
                    IntentFilter intentFilter = new IntentFilter("android.nfc.action.NDEF_DISCOVERED");
                    intentFilter.addAction("android.nfc.action.TECH_DISCOVERED");
                    intentFilter.addAction("android.nfc.action.TAG_DISCOVERED");
                    this.nfcIntentFilters = new IntentFilter[]{intentFilter};
                    this.context.registerReceiver(this.nfcBroadcastReceiver, intentFilter);
                }
                for (ListenerHelper listenerHelper : this.listeners) {
                    if (listenerHelper.getListener() == onCardboardNfcListener) {
                        return;
                    }
                }
                this.listeners.add(new ListenerHelper(onCardboardNfcListener, new Handler()));
            }
        }
    }

    public NdefMessage getCurrentTagContents() throws TagLostException, IOException, FormatException {
        NdefMessage ndefMessage;
        synchronized (this.tagLock) {
            ndefMessage = this.currentNdef != null ? this.currentNdef.getNdefMessage() : null;
        }
        return ndefMessage;
    }

    public int getTagCapacity() {
        int maxSize;
        synchronized (this.tagLock) {
            if (this.currentNdef == null) {
                throw new IllegalStateException("No NFC tag");
            }
            maxSize = this.currentNdef.getMaxSize();
        }
        return maxSize;
    }

    public NdefMessage getTagContents() {
        NdefMessage cachedNdefMessage;
        synchronized (this.tagLock) {
            cachedNdefMessage = this.currentNdef != null ? this.currentNdef.getCachedNdefMessage() : null;
        }
        return cachedNdefMessage;
    }

    public boolean isDeviceInCardboard() {
        boolean z;
        synchronized (this.tagLock) {
            z = this.currentTagIsCardboard;
        }
        return z;
    }

    public boolean isNfcEnabled() {
        return isNfcSupported() && this.nfcAdapter.isEnabled();
    }

    public boolean isNfcSupported() {
        return this.nfcAdapter != null;
    }

    public void onNfcIntent(Intent intent) {
        if (isNfcEnabled() && intent != null && this.nfcIntentFilters[0].matchAction(intent.getAction())) {
            onNewNfcTag((Tag) intent.getParcelableExtra("android.nfc.extra.TAG"));
        }
    }

    public void onPause(Activity activity) {
        if (isNfcEnabled()) {
            this.nfcAdapter.disableForegroundDispatch(activity);
        }
    }

    public void onResume(Activity activity) {
        if (isNfcEnabled()) {
            Intent intent = new Intent("android.nfc.action.NDEF_DISCOVERED");
            intent.setPackage(activity.getPackageName());
            this.nfcAdapter.enableForegroundDispatch(activity, PendingIntent.getBroadcast(this.context, 0, intent, 0), this.nfcIntentFilters, null);
        }
    }

    public void removeOnCardboardNfcListener(OnCardboardNfcListener onCardboardNfcListener) {
        if (onCardboardNfcListener != null) {
            synchronized (this.listeners) {
                Iterator<ListenerHelper> it = this.listeners.iterator();
                while (true) {
                    if (!it.hasNext()) {
                        break;
                    }
                    ListenerHelper next = it.next();
                    if (next.getListener() == onCardboardNfcListener) {
                        this.listeners.remove(next);
                        break;
                    }
                }
                if (this.nfcBroadcastReceiver != null && this.listeners.isEmpty()) {
                    this.context.unregisterReceiver(this.nfcBroadcastReceiver);
                }
            }
        }
    }

    public void writeUri(Uri uri) throws TagLostException, IOException, IllegalArgumentException {
        NdefMessage tagContents;
        NdefRecord[] records;
        NdefMessage ndefMessage = null;
        synchronized (this.tagLock) {
            if (this.currentTag == null) {
                throw new IllegalStateException("No NFC tag found");
            }
            NdefRecord createUri = NdefRecord.createUri(uri);
            try {
                tagContents = getCurrentTagContents();
            } catch (Exception e) {
                tagContents = getTagContents();
            }
            if (tagContents != null) {
                ArrayList arrayList = new ArrayList();
                boolean z = false;
                for (NdefRecord ndefRecord : tagContents.getRecords()) {
                    if (!isCardboardNdefRecord(ndefRecord)) {
                        arrayList.add(ndefRecord);
                    } else if (!z) {
                        arrayList.add(createUri);
                        z = true;
                    }
                }
                ndefMessage = new NdefMessage((NdefRecord[]) arrayList.toArray(new NdefRecord[arrayList.size()]));
            }
            if (ndefMessage == null) {
                ndefMessage = new NdefMessage(new NdefRecord[]{createUri});
            }
            if (this.currentNdef == null) {
                NdefFormatable ndefFormatable = NdefFormatable.get(this.currentTag);
                if (ndefFormatable == null) {
                    throw new IOException("Could not find a writable technology for the NFC tag");
                }
                Log.w(TAG, "Ndef technology not available. Falling back to NdefFormattable.");
                try {
                    ndefFormatable.connect();
                    ndefFormatable.format(ndefMessage);
                    ndefFormatable.close();
                    onNewNfcTag(this.currentTag);
                } catch (FormatException e2) {
                    String valueOf = String.valueOf(e2.toString());
                    throw new RuntimeException(valueOf.length() == 0 ? new String("Internal error when writing to NFC tag: ") : "Internal error when writing to NFC tag: ".concat(valueOf));
                }
            } else {
                if (!this.currentNdef.isConnected()) {
                    this.currentNdef.connect();
                }
                if (this.currentNdef.getMaxSize() < ndefMessage.getByteArrayLength()) {
                    throw new IllegalArgumentException(new StringBuilder(82).append("Not enough capacity in NFC tag. Capacity: ").append(this.currentNdef.getMaxSize()).append(" bytes, ").append(ndefMessage.getByteArrayLength()).append(" required.").toString());
                }
                try {
                    this.currentNdef.writeNdefMessage(ndefMessage);
                    onNewNfcTag(this.currentTag);
                } catch (FormatException e3) {
                    String valueOf2 = String.valueOf(e3.toString());
                    throw new RuntimeException(valueOf2.length() == 0 ? new String("Internal error when writing to NFC tag: ") : "Internal error when writing to NFC tag: ".concat(valueOf2));
                }
            }
        }
    }
}
