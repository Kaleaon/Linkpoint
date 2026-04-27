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
import java.util.Iterator;
import java.util.List;
import java.util.Timer;

public class NfcSensor {
    private static final int MAX_CONNECTION_FAILURES = 1;
    private static final long NFC_POLLING_INTERVAL_MS = 250;
    private static final String TAG = "NfcSensor";
    private static NfcSensor sInstance;
    private final Context context;
    /* access modifiers changed from: private */
    public Ndef currentNdef;
    private Tag currentTag;
    private boolean currentTagIsCardboard;
    private final List<ListenerHelper> listeners = new ArrayList();
    private final NfcAdapter nfcAdapter;
    private BroadcastReceiver nfcBroadcastReceiver;
    private Timer nfcDisconnectTimer;
    private IntentFilter[] nfcIntentFilters;
    /* access modifiers changed from: private */
    public int tagConnectionFailures;
    /* access modifiers changed from: private */
    public final Object tagLock = new Object();

    private static class ListenerHelper implements OnCardboardNfcListener {
        private Handler handler;
        /* access modifiers changed from: private */
        public OnCardboardNfcListener listener;

        public ListenerHelper(OnCardboardNfcListener onCardboardNfcListener, Handler handler2) {
            this.listener = onCardboardNfcListener;
            this.handler = handler2;
        }

        public OnCardboardNfcListener getListener() {
            return this.listener;
        }

        public void onInsertedIntoGvrViewer(final GvrViewerParams gvrViewerParams) {
            this.handler.post(new Runnable() {
                public void run() {
                    ListenerHelper.this.listener.onInsertedIntoGvrViewer(gvrViewerParams);
                }
            });
        }

        public void onRemovedFromGvrViewer() {
            this.handler.post(new Runnable() {
                public void run() {
                    ListenerHelper.this.listener.onRemovedFromGvrViewer();
                }
            });
        }
    }

    public interface OnCardboardNfcListener {
        void onInsertedIntoGvrViewer(GvrViewerParams gvrViewerParams);

        void onRemovedFromGvrViewer();
    }

    private NfcSensor(Context context2) {
        this.context = context2.getApplicationContext();
        if (!PermissionUtils.hasPermission(context2, "android.permission.NFC")) {
            this.nfcAdapter = null;
        } else {
            this.nfcAdapter = NfcAdapter.getDefaultAdapter(context2);
        }
        if (this.nfcAdapter != null) {
            this.nfcBroadcastReceiver = new BroadcastReceiver() {
                public void onReceive(Context context, Intent intent) {
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

    /* access modifiers changed from: private */
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

    public static NfcSensor getInstance(Context context2) {
        if (sInstance == null) {
            sInstance = new NfcSensor(context2);
        }
        return sInstance;
    }

    private boolean isCardboardNdefMessage(NdefMessage ndefMessage) {
        if (ndefMessage == null) {
            return false;
        }
        for (NdefRecord isCardboardNdefRecord : ndefMessage.getRecords()) {
            if (isCardboardNdefRecord(isCardboardNdefRecord)) {
                return true;
            }
        }
        return false;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:2:0x0003, code lost:
        r1 = r3.toUri();
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private boolean isCardboardNdefRecord(android.nfc.NdefRecord r3) {
        /*
            r2 = this;
            r0 = 0
            if (r3 == 0) goto L_0x000a
            android.net.Uri r1 = r3.toUri()
            if (r1 != 0) goto L_0x000b
        L_0x0009:
            return r0
        L_0x000a:
            return r0
        L_0x000b:
            boolean r1 = com.google.vr.sdk.base.GvrViewerParams.isGvrUri(r1)
            if (r1 == 0) goto L_0x0009
            r0 = 1
            goto L_0x0009
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.vr.sdk.base.sensors.NfcSensor.isCardboardNdefRecord(android.nfc.NdefRecord):boolean");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:17:0x0036, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:21:0x003b, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void onNewNfcTag(android.nfc.Tag r8) {
        /*
            r7 = this;
            r0 = 0
            if (r8 == 0) goto L_0x0037
            java.lang.Object r6 = r7.tagLock
            monitor-enter(r6)
            android.nfc.Tag r1 = r7.currentTag     // Catch:{ all -> 0x0040 }
            android.nfc.tech.Ndef r2 = r7.currentNdef     // Catch:{ all -> 0x0040 }
            boolean r3 = r7.currentTagIsCardboard     // Catch:{ all -> 0x0040 }
            r7.closeCurrentNfcTag()     // Catch:{ all -> 0x0040 }
            r7.currentTag = r8     // Catch:{ all -> 0x0040 }
            android.nfc.tech.Ndef r4 = android.nfc.tech.Ndef.get(r8)     // Catch:{ all -> 0x0040 }
            r7.currentNdef = r4     // Catch:{ all -> 0x0040 }
            android.nfc.tech.Ndef r4 = r7.currentNdef     // Catch:{ all -> 0x0040 }
            if (r4 == 0) goto L_0x0038
            if (r2 != 0) goto L_0x0043
            r1 = r0
        L_0x001e:
            android.nfc.tech.Ndef r0 = r7.currentNdef     // Catch:{ Exception -> 0x0064 }
            r0.connect()     // Catch:{ Exception -> 0x0064 }
            android.nfc.tech.Ndef r0 = r7.currentNdef     // Catch:{ Exception -> 0x0064 }
            android.nfc.NdefMessage r2 = r0.getCachedNdefMessage()     // Catch:{ Exception -> 0x0064 }
            boolean r0 = r7.isCardboardNdefMessage(r2)     // Catch:{ all -> 0x0040 }
            r7.currentTagIsCardboard = r0     // Catch:{ all -> 0x0040 }
            if (r1 == 0) goto L_0x0090
        L_0x0031:
            boolean r0 = r7.currentTagIsCardboard     // Catch:{ all -> 0x0040 }
            if (r0 != 0) goto L_0x00b6
        L_0x0035:
            monitor-exit(r6)     // Catch:{ all -> 0x0040 }
            return
        L_0x0037:
            return
        L_0x0038:
            if (r3 != 0) goto L_0x003c
        L_0x003a:
            monitor-exit(r6)     // Catch:{ all -> 0x0040 }
            return
        L_0x003c:
            r7.sendDisconnectionEvent()     // Catch:{ all -> 0x0040 }
            goto L_0x003a
        L_0x0040:
            r0 = move-exception
            monitor-exit(r6)     // Catch:{ all -> 0x0040 }
            throw r0
        L_0x0043:
            android.nfc.Tag r2 = r7.currentTag     // Catch:{ all -> 0x0040 }
            byte[] r2 = r2.getId()     // Catch:{ all -> 0x0040 }
            byte[] r1 = r1.getId()     // Catch:{ all -> 0x0040 }
            if (r2 != 0) goto L_0x0053
        L_0x004f:
            if (r0 == 0) goto L_0x005d
        L_0x0051:
            r1 = r0
            goto L_0x001e
        L_0x0053:
            if (r1 == 0) goto L_0x004f
            boolean r1 = java.util.Arrays.equals(r2, r1)     // Catch:{ all -> 0x0040 }
            if (r1 == 0) goto L_0x004f
            r0 = 1
            goto L_0x004f
        L_0x005d:
            if (r3 == 0) goto L_0x0051
            r7.sendDisconnectionEvent()     // Catch:{ all -> 0x0040 }
            r1 = r0
            goto L_0x001e
        L_0x0064:
            r0 = move-exception
            java.lang.String r0 = r0.toString()     // Catch:{ all -> 0x0040 }
            java.lang.String r0 = java.lang.String.valueOf(r0)     // Catch:{ all -> 0x0040 }
            int r2 = r0.length()     // Catch:{ all -> 0x0040 }
            java.lang.String r4 = "NfcSensor"
            java.lang.String r5 = "Error reading NFC tag: "
            if (r2 != 0) goto L_0x0085
            java.lang.String r0 = new java.lang.String     // Catch:{ all -> 0x0040 }
            r0.<init>(r5)     // Catch:{ all -> 0x0040 }
        L_0x007e:
            android.util.Log.e(r4, r0)     // Catch:{ all -> 0x0040 }
            if (r1 != 0) goto L_0x008a
        L_0x0083:
            monitor-exit(r6)     // Catch:{ all -> 0x0040 }
            return
        L_0x0085:
            java.lang.String r0 = r5.concat(r0)     // Catch:{ all -> 0x0040 }
            goto L_0x007e
        L_0x008a:
            if (r3 == 0) goto L_0x0083
            r7.sendDisconnectionEvent()     // Catch:{ all -> 0x0040 }
            goto L_0x0083
        L_0x0090:
            boolean r0 = r7.currentTagIsCardboard     // Catch:{ all -> 0x0040 }
            if (r0 == 0) goto L_0x0031
            java.util.List<com.google.vr.sdk.base.sensors.NfcSensor$ListenerHelper> r1 = r7.listeners     // Catch:{ all -> 0x0040 }
            monitor-enter(r1)     // Catch:{ all -> 0x0040 }
            java.util.List<com.google.vr.sdk.base.sensors.NfcSensor$ListenerHelper> r0 = r7.listeners     // Catch:{ all -> 0x00a5 }
            java.util.Iterator r3 = r0.iterator()     // Catch:{ all -> 0x00a5 }
        L_0x009d:
            boolean r0 = r3.hasNext()     // Catch:{ all -> 0x00a5 }
            if (r0 != 0) goto L_0x00a8
            monitor-exit(r1)     // Catch:{ all -> 0x00a5 }
            goto L_0x0031
        L_0x00a5:
            r0 = move-exception
            monitor-exit(r1)     // Catch:{ all -> 0x00a5 }
            throw r0     // Catch:{ all -> 0x0040 }
        L_0x00a8:
            java.lang.Object r0 = r3.next()     // Catch:{ all -> 0x00a5 }
            com.google.vr.sdk.base.sensors.NfcSensor$ListenerHelper r0 = (com.google.vr.sdk.base.sensors.NfcSensor.ListenerHelper) r0     // Catch:{ all -> 0x00a5 }
            com.google.vr.sdk.base.GvrViewerParams r4 = com.google.vr.sdk.base.GvrViewerParams.createFromNfcContents(r2)     // Catch:{ all -> 0x00a5 }
            r0.onInsertedIntoGvrViewer(r4)     // Catch:{ all -> 0x00a5 }
            goto L_0x009d
        L_0x00b6:
            r0 = 0
            r7.tagConnectionFailures = r0     // Catch:{ all -> 0x0040 }
            java.util.Timer r0 = new java.util.Timer     // Catch:{ all -> 0x0040 }
            java.lang.String r1 = "NFC disconnect timer"
            r0.<init>(r1)     // Catch:{ all -> 0x0040 }
            r7.nfcDisconnectTimer = r0     // Catch:{ all -> 0x0040 }
            java.util.Timer r0 = r7.nfcDisconnectTimer     // Catch:{ all -> 0x0040 }
            com.google.vr.sdk.base.sensors.NfcSensor$2 r1 = new com.google.vr.sdk.base.sensors.NfcSensor$2     // Catch:{ all -> 0x0040 }
            r1.<init>()     // Catch:{ all -> 0x0040 }
            r2 = 250(0xfa, double:1.235E-321)
            r4 = 250(0xfa, double:1.235E-321)
            r0.schedule(r1, r2, r4)     // Catch:{ all -> 0x0040 }
            goto L_0x0035
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.vr.sdk.base.sensors.NfcSensor.onNewNfcTag(android.nfc.Tag):void");
    }

    /* access modifiers changed from: private */
    public void sendDisconnectionEvent() {
        synchronized (this.listeners) {
            for (ListenerHelper onRemovedFromGvrViewer : this.listeners) {
                onRemovedFromGvrViewer.onRemovedFromGvrViewer();
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
                for (ListenerHelper listener : this.listeners) {
                    if (listener.getListener() == onCardboardNfcListener) {
                        return;
                    }
                }
                this.listeners.add(new ListenerHelper(onCardboardNfcListener, new Handler()));
            }
        }
    }

    public NdefMessage getCurrentTagContents() throws TagLostException, IOException, FormatException {
        NdefMessage ndefMessage = null;
        synchronized (this.tagLock) {
            if (this.currentNdef != null) {
                ndefMessage = this.currentNdef.getNdefMessage();
            }
        }
        return ndefMessage;
    }

    public int getTagCapacity() {
        int maxSize;
        synchronized (this.tagLock) {
            if (this.currentNdef != null) {
                maxSize = this.currentNdef.getMaxSize();
            } else {
                throw new IllegalStateException("No NFC tag");
            }
        }
        return maxSize;
    }

    public NdefMessage getTagContents() {
        NdefMessage ndefMessage = null;
        synchronized (this.tagLock) {
            if (this.currentNdef != null) {
                ndefMessage = this.currentNdef.getCachedNdefMessage();
            }
        }
        return ndefMessage;
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
            this.nfcAdapter.enableForegroundDispatch(activity, PendingIntent.getBroadcast(this.context, 0, intent, 0), this.nfcIntentFilters, (String[][]) null);
        }
    }

    public void removeOnCardboardNfcListener(OnCardboardNfcListener onCardboardNfcListener) {
        if (onCardboardNfcListener != null) {
            synchronized (this.listeners) {
                Iterator<ListenerHelper> it = this.listeners.iterator();
                while (true) {
                    if (it.hasNext()) {
                        ListenerHelper next = it.next();
                        if (next.getListener() == onCardboardNfcListener) {
                            this.listeners.remove(next);
                            break;
                        }
                    } else {
                        break;
                    }
                }
                if (this.nfcBroadcastReceiver != null) {
                    if (this.listeners.isEmpty()) {
                        this.context.unregisterReceiver(this.nfcBroadcastReceiver);
                    }
                }
            }
        }
    }

    public void writeUri(Uri uri) throws TagLostException, IOException, IllegalArgumentException {
        NdefMessage tagContents;
        NdefMessage ndefMessage = null;
        synchronized (this.tagLock) {
            if (this.currentTag != null) {
                NdefRecord createUri = NdefRecord.createUri(uri);
                try {
                    tagContents = getCurrentTagContents();
                } catch (FormatException e) {
                    String valueOf = String.valueOf(e.toString());
                    throw new RuntimeException(valueOf.length() == 0 ? new String("Internal error when writing to NFC tag: ") : "Internal error when writing to NFC tag: ".concat(valueOf));
                } catch (FormatException e2) {
                    String valueOf2 = String.valueOf(e2.toString());
                    throw new RuntimeException(valueOf2.length() == 0 ? new String("Internal error when writing to NFC tag: ") : "Internal error when writing to NFC tag: ".concat(valueOf2));
                } catch (Exception e3) {
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
                    if (ndefFormatable != null) {
                        Log.w(TAG, "Ndef technology not available. Falling back to NdefFormattable.");
                        ndefFormatable.connect();
                        ndefFormatable.format(ndefMessage);
                        ndefFormatable.close();
                    } else {
                        throw new IOException("Could not find a writable technology for the NFC tag");
                    }
                } else {
                    if (!this.currentNdef.isConnected()) {
                        this.currentNdef.connect();
                    }
                    if (this.currentNdef.getMaxSize() >= ndefMessage.getByteArrayLength()) {
                        this.currentNdef.writeNdefMessage(ndefMessage);
                    } else {
                        throw new IllegalArgumentException(new StringBuilder(82).append("Not enough capacity in NFC tag. Capacity: ").append(this.currentNdef.getMaxSize()).append(" bytes, ").append(ndefMessage.getByteArrayLength()).append(" required.").toString());
                    }
                }
                onNewNfcTag(this.currentTag);
            } else {
                throw new IllegalStateException("No NFC tag found");
            }
        }
    }
}
