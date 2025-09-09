package android.support.v4.content;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import java.util.ArrayList;
import java.util.HashMap;

public final class LocalBroadcastManager {
    private static final boolean DEBUG = false;
    static final int MSG_EXEC_PENDING_BROADCASTS = 1;
    private static final String TAG = "LocalBroadcastManager";
    private static LocalBroadcastManager mInstance;
    private static final Object mLock = new Object();
    private final HashMap<String, ArrayList<ReceiverRecord>> mActions = new HashMap<>();
    private final Context mAppContext;
    private final Handler mHandler;
    private final ArrayList<BroadcastRecord> mPendingBroadcasts = new ArrayList<>();
    private final HashMap<BroadcastReceiver, ArrayList<ReceiverRecord>> mReceivers = new HashMap<>();

    private static final class BroadcastRecord {
        final Intent intent;
        final ArrayList<ReceiverRecord> receivers;

        BroadcastRecord(Intent intent2, ArrayList<ReceiverRecord> arrayList) {
            this.intent = intent2;
            this.receivers = arrayList;
        }
    }

    private static final class ReceiverRecord {
        boolean broadcasting;
        boolean dead;
        final IntentFilter filter;
        final BroadcastReceiver receiver;

        ReceiverRecord(IntentFilter intentFilter, BroadcastReceiver broadcastReceiver) {
            this.filter = intentFilter;
            this.receiver = broadcastReceiver;
        }

        public String toString() {
            StringBuilder sb = new StringBuilder(128);
            sb.append("Receiver{");
            sb.append(this.receiver);
            sb.append(" filter=");
            sb.append(this.filter);
            if (this.dead) {
                sb.append(" DEAD");
            }
            sb.append("}");
            return sb.toString();
        }
    }

    private LocalBroadcastManager(Context context) {
        this.mAppContext = context;
        this.mHandler = new Handler(context.getMainLooper()) {
            public void handleMessage(Message message) {
                switch (message.what) {
                    case 1:
                        LocalBroadcastManager.this.executePendingBroadcasts();
                        return;
                    default:
                        super.handleMessage(message);
                        return;
                }
            }
        };
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:10:0x001b, code lost:
        if (r1 >= r4.length) goto L_0x0001;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:11:0x001d, code lost:
        r5 = r4[r1];
        r6 = r5.receivers.size();
        r3 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:12:0x0026, code lost:
        if (r3 < r6) goto L_0x0031;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:13:0x0028, code lost:
        r1 = r1 + 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:20:0x0031, code lost:
        r0 = r5.receivers.get(r3);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:21:0x003b, code lost:
        if (r0.dead == false) goto L_0x0041;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:22:0x003d, code lost:
        r3 = r3 + 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:23:0x0041, code lost:
        r0.receiver.onReceive(r9.mAppContext, r5.intent);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:8:0x0019, code lost:
        r1 = 0;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void executePendingBroadcasts() {
        /*
            r9 = this;
            r2 = 0
        L_0x0001:
            java.util.HashMap<android.content.BroadcastReceiver, java.util.ArrayList<android.support.v4.content.LocalBroadcastManager$ReceiverRecord>> r1 = r9.mReceivers
            monitor-enter(r1)
            java.util.ArrayList<android.support.v4.content.LocalBroadcastManager$BroadcastRecord> r0 = r9.mPendingBroadcasts     // Catch:{ all -> 0x002e }
            int r0 = r0.size()     // Catch:{ all -> 0x002e }
            if (r0 <= 0) goto L_0x002c
            android.support.v4.content.LocalBroadcastManager$BroadcastRecord[] r4 = new android.support.v4.content.LocalBroadcastManager.BroadcastRecord[r0]     // Catch:{ all -> 0x002e }
            java.util.ArrayList<android.support.v4.content.LocalBroadcastManager$BroadcastRecord> r0 = r9.mPendingBroadcasts     // Catch:{ all -> 0x002e }
            r0.toArray(r4)     // Catch:{ all -> 0x002e }
            java.util.ArrayList<android.support.v4.content.LocalBroadcastManager$BroadcastRecord> r0 = r9.mPendingBroadcasts     // Catch:{ all -> 0x002e }
            r0.clear()     // Catch:{ all -> 0x002e }
            monitor-exit(r1)     // Catch:{ all -> 0x002e }
            r1 = r2
        L_0x001a:
            int r0 = r4.length
            if (r1 >= r0) goto L_0x0001
            r5 = r4[r1]
            java.util.ArrayList<android.support.v4.content.LocalBroadcastManager$ReceiverRecord> r0 = r5.receivers
            int r6 = r0.size()
            r3 = r2
        L_0x0026:
            if (r3 < r6) goto L_0x0031
            int r0 = r1 + 1
            r1 = r0
            goto L_0x001a
        L_0x002c:
            monitor-exit(r1)     // Catch:{ all -> 0x002e }
            return
        L_0x002e:
            r0 = move-exception
            monitor-exit(r1)     // Catch:{ all -> 0x002e }
            throw r0
        L_0x0031:
            java.util.ArrayList<android.support.v4.content.LocalBroadcastManager$ReceiverRecord> r0 = r5.receivers
            java.lang.Object r0 = r0.get(r3)
            android.support.v4.content.LocalBroadcastManager$ReceiverRecord r0 = (android.support.v4.content.LocalBroadcastManager.ReceiverRecord) r0
            boolean r7 = r0.dead
            if (r7 == 0) goto L_0x0041
        L_0x003d:
            int r0 = r3 + 1
            r3 = r0
            goto L_0x0026
        L_0x0041:
            android.content.BroadcastReceiver r0 = r0.receiver
            android.content.Context r7 = r9.mAppContext
            android.content.Intent r8 = r5.intent
            r0.onReceive(r7, r8)
            goto L_0x003d
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.v4.content.LocalBroadcastManager.executePendingBroadcasts():void");
    }

    public static LocalBroadcastManager getInstance(Context context) {
        LocalBroadcastManager localBroadcastManager;
        synchronized (mLock) {
            if (mInstance == null) {
                mInstance = new LocalBroadcastManager(context.getApplicationContext());
            }
            localBroadcastManager = mInstance;
        }
        return localBroadcastManager;
    }

    public void registerReceiver(BroadcastReceiver broadcastReceiver, IntentFilter intentFilter) {
        synchronized (this.mReceivers) {
            ReceiverRecord receiverRecord = new ReceiverRecord(intentFilter, broadcastReceiver);
            ArrayList arrayList = this.mReceivers.get(broadcastReceiver);
            if (arrayList == null) {
                arrayList = new ArrayList(1);
                this.mReceivers.put(broadcastReceiver, arrayList);
            }
            arrayList.add(receiverRecord);
            for (int i = 0; i < intentFilter.countActions(); i++) {
                String action = intentFilter.getAction(i);
                ArrayList arrayList2 = this.mActions.get(action);
                if (arrayList2 == null) {
                    arrayList2 = new ArrayList(1);
                    this.mActions.put(action, arrayList2);
                }
                arrayList2.add(receiverRecord);
            }
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:10:0x003c, code lost:
        return false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:28:0x00a2, code lost:
        return true;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean sendBroadcast(android.content.Intent r16) {
        /*
            r15 = this;
            java.util.HashMap<android.content.BroadcastReceiver, java.util.ArrayList<android.support.v4.content.LocalBroadcastManager$ReceiverRecord>> r13 = r15.mReceivers
            monitor-enter(r13)
            java.lang.String r2 = r16.getAction()     // Catch:{ all -> 0x0074 }
            android.content.Context r1 = r15.mAppContext     // Catch:{ all -> 0x0074 }
            android.content.ContentResolver r1 = r1.getContentResolver()     // Catch:{ all -> 0x0074 }
            r0 = r16
            java.lang.String r3 = r0.resolveTypeIfNeeded(r1)     // Catch:{ all -> 0x0074 }
            android.net.Uri r5 = r16.getData()     // Catch:{ all -> 0x0074 }
            java.lang.String r4 = r16.getScheme()     // Catch:{ all -> 0x0074 }
            java.util.Set r6 = r16.getCategories()     // Catch:{ all -> 0x0074 }
            int r1 = r16.getFlags()     // Catch:{ all -> 0x0074 }
            r1 = r1 & 8
            if (r1 != 0) goto L_0x003e
            r1 = 0
            r10 = r1
        L_0x0029:
            if (r10 != 0) goto L_0x0041
        L_0x002b:
            java.util.HashMap<java.lang.String, java.util.ArrayList<android.support.v4.content.LocalBroadcastManager$ReceiverRecord>> r1 = r15.mActions     // Catch:{ all -> 0x0074 }
            java.lang.String r7 = r16.getAction()     // Catch:{ all -> 0x0074 }
            java.lang.Object r1 = r1.get(r7)     // Catch:{ all -> 0x0074 }
            r0 = r1
            java.util.ArrayList r0 = (java.util.ArrayList) r0     // Catch:{ all -> 0x0074 }
            r8 = r0
            if (r8 != 0) goto L_0x0077
        L_0x003b:
            monitor-exit(r13)     // Catch:{ all -> 0x0074 }
            r1 = 0
            return r1
        L_0x003e:
            r1 = 1
            r10 = r1
            goto L_0x0029
        L_0x0041:
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ all -> 0x0074 }
            r1.<init>()     // Catch:{ all -> 0x0074 }
            java.lang.String r7 = "Resolving type "
            java.lang.StringBuilder r1 = r1.append(r7)     // Catch:{ all -> 0x0074 }
            java.lang.StringBuilder r1 = r1.append(r3)     // Catch:{ all -> 0x0074 }
            java.lang.String r7 = " scheme "
            java.lang.StringBuilder r1 = r1.append(r7)     // Catch:{ all -> 0x0074 }
            java.lang.StringBuilder r1 = r1.append(r4)     // Catch:{ all -> 0x0074 }
            java.lang.String r7 = " of intent "
            java.lang.StringBuilder r1 = r1.append(r7)     // Catch:{ all -> 0x0074 }
            r0 = r16
            java.lang.StringBuilder r1 = r1.append(r0)     // Catch:{ all -> 0x0074 }
            java.lang.String r1 = r1.toString()     // Catch:{ all -> 0x0074 }
            java.lang.String r7 = "LocalBroadcastManager"
            android.util.Log.v(r7, r1)     // Catch:{ all -> 0x0074 }
            goto L_0x002b
        L_0x0074:
            r1 = move-exception
            monitor-exit(r13)     // Catch:{ all -> 0x0074 }
            throw r1
        L_0x0077:
            if (r10 != 0) goto L_0x00a4
        L_0x0079:
            r11 = 0
            r1 = 0
            r12 = r1
        L_0x007c:
            int r1 = r8.size()     // Catch:{ all -> 0x0074 }
            if (r12 < r1) goto L_0x00bf
            if (r11 == 0) goto L_0x003b
            r1 = 0
            r2 = r1
        L_0x0086:
            int r1 = r11.size()     // Catch:{ all -> 0x0074 }
            if (r2 < r1) goto L_0x0170
            java.util.ArrayList<android.support.v4.content.LocalBroadcastManager$BroadcastRecord> r1 = r15.mPendingBroadcasts     // Catch:{ all -> 0x0074 }
            android.support.v4.content.LocalBroadcastManager$BroadcastRecord r2 = new android.support.v4.content.LocalBroadcastManager$BroadcastRecord     // Catch:{ all -> 0x0074 }
            r0 = r16
            r2.<init>(r0, r11)     // Catch:{ all -> 0x0074 }
            r1.add(r2)     // Catch:{ all -> 0x0074 }
            android.os.Handler r1 = r15.mHandler     // Catch:{ all -> 0x0074 }
            r2 = 1
            boolean r1 = r1.hasMessages(r2)     // Catch:{ all -> 0x0074 }
            if (r1 == 0) goto L_0x017e
        L_0x00a1:
            monitor-exit(r13)     // Catch:{ all -> 0x0074 }
            r1 = 1
            return r1
        L_0x00a4:
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ all -> 0x0074 }
            r1.<init>()     // Catch:{ all -> 0x0074 }
            java.lang.String r7 = "Action list: "
            java.lang.StringBuilder r1 = r1.append(r7)     // Catch:{ all -> 0x0074 }
            java.lang.StringBuilder r1 = r1.append(r8)     // Catch:{ all -> 0x0074 }
            java.lang.String r1 = r1.toString()     // Catch:{ all -> 0x0074 }
            java.lang.String r7 = "LocalBroadcastManager"
            android.util.Log.v(r7, r1)     // Catch:{ all -> 0x0074 }
            goto L_0x0079
        L_0x00bf:
            java.lang.Object r1 = r8.get(r12)     // Catch:{ all -> 0x0074 }
            r0 = r1
            android.support.v4.content.LocalBroadcastManager$ReceiverRecord r0 = (android.support.v4.content.LocalBroadcastManager.ReceiverRecord) r0     // Catch:{ all -> 0x0074 }
            r9 = r0
            if (r10 != 0) goto L_0x00e0
        L_0x00c9:
            boolean r1 = r9.broadcasting     // Catch:{ all -> 0x0074 }
            if (r1 != 0) goto L_0x00fd
            android.content.IntentFilter r1 = r9.filter     // Catch:{ all -> 0x0074 }
            java.lang.String r7 = "LocalBroadcastManager"
            int r1 = r1.match(r2, r3, r4, r5, r6, r7)     // Catch:{ all -> 0x0074 }
            if (r1 >= 0) goto L_0x010c
            if (r10 != 0) goto L_0x013d
            r1 = r11
        L_0x00db:
            int r7 = r12 + 1
            r12 = r7
            r11 = r1
            goto L_0x007c
        L_0x00e0:
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ all -> 0x0074 }
            r1.<init>()     // Catch:{ all -> 0x0074 }
            java.lang.String r7 = "Matching against filter "
            java.lang.StringBuilder r1 = r1.append(r7)     // Catch:{ all -> 0x0074 }
            android.content.IntentFilter r7 = r9.filter     // Catch:{ all -> 0x0074 }
            java.lang.StringBuilder r1 = r1.append(r7)     // Catch:{ all -> 0x0074 }
            java.lang.String r1 = r1.toString()     // Catch:{ all -> 0x0074 }
            java.lang.String r7 = "LocalBroadcastManager"
            android.util.Log.v(r7, r1)     // Catch:{ all -> 0x0074 }
            goto L_0x00c9
        L_0x00fd:
            if (r10 != 0) goto L_0x0101
            r1 = r11
            goto L_0x00db
        L_0x0101:
            java.lang.String r1 = "LocalBroadcastManager"
            java.lang.String r7 = "  Filter's target already added"
            android.util.Log.v(r1, r7)     // Catch:{ all -> 0x0074 }
            r1 = r11
            goto L_0x00db
        L_0x010c:
            if (r10 != 0) goto L_0x0118
        L_0x010e:
            if (r11 == 0) goto L_0x0137
            r1 = r11
        L_0x0111:
            r1.add(r9)     // Catch:{ all -> 0x0074 }
            r7 = 1
            r9.broadcasting = r7     // Catch:{ all -> 0x0074 }
            goto L_0x00db
        L_0x0118:
            java.lang.StringBuilder r7 = new java.lang.StringBuilder     // Catch:{ all -> 0x0074 }
            r7.<init>()     // Catch:{ all -> 0x0074 }
            java.lang.String r14 = "  Filter matched!  match=0x"
            java.lang.StringBuilder r7 = r7.append(r14)     // Catch:{ all -> 0x0074 }
            java.lang.String r14 = "LocalBroadcastManager"
            java.lang.String r1 = java.lang.Integer.toHexString(r1)     // Catch:{ all -> 0x0074 }
            java.lang.StringBuilder r1 = r7.append(r1)     // Catch:{ all -> 0x0074 }
            java.lang.String r1 = r1.toString()     // Catch:{ all -> 0x0074 }
            android.util.Log.v(r14, r1)     // Catch:{ all -> 0x0074 }
            goto L_0x010e
        L_0x0137:
            java.util.ArrayList r1 = new java.util.ArrayList     // Catch:{ all -> 0x0074 }
            r1.<init>()     // Catch:{ all -> 0x0074 }
            goto L_0x0111
        L_0x013d:
            switch(r1) {
                case -4: goto L_0x0164;
                case -3: goto L_0x0160;
                case -2: goto L_0x0168;
                case -1: goto L_0x016c;
                default: goto L_0x0140;
            }     // Catch:{ all -> 0x0074 }
        L_0x0140:
            java.lang.String r1 = "unknown reason"
        L_0x0143:
            java.lang.StringBuilder r7 = new java.lang.StringBuilder     // Catch:{ all -> 0x0074 }
            r7.<init>()     // Catch:{ all -> 0x0074 }
            java.lang.String r9 = "  Filter did not match: "
            java.lang.StringBuilder r7 = r7.append(r9)     // Catch:{ all -> 0x0074 }
            java.lang.StringBuilder r1 = r7.append(r1)     // Catch:{ all -> 0x0074 }
            java.lang.String r1 = r1.toString()     // Catch:{ all -> 0x0074 }
            java.lang.String r7 = "LocalBroadcastManager"
            android.util.Log.v(r7, r1)     // Catch:{ all -> 0x0074 }
            r1 = r11
            goto L_0x00db
        L_0x0160:
            java.lang.String r1 = "action"
            goto L_0x0143
        L_0x0164:
            java.lang.String r1 = "category"
            goto L_0x0143
        L_0x0168:
            java.lang.String r1 = "data"
            goto L_0x0143
        L_0x016c:
            java.lang.String r1 = "type"
            goto L_0x0143
        L_0x0170:
            java.lang.Object r1 = r11.get(r2)     // Catch:{ all -> 0x0074 }
            android.support.v4.content.LocalBroadcastManager$ReceiverRecord r1 = (android.support.v4.content.LocalBroadcastManager.ReceiverRecord) r1     // Catch:{ all -> 0x0074 }
            r3 = 0
            r1.broadcasting = r3     // Catch:{ all -> 0x0074 }
            int r1 = r2 + 1
            r2 = r1
            goto L_0x0086
        L_0x017e:
            android.os.Handler r1 = r15.mHandler     // Catch:{ all -> 0x0074 }
            r2 = 1
            r1.sendEmptyMessage(r2)     // Catch:{ all -> 0x0074 }
            goto L_0x00a1
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.v4.content.LocalBroadcastManager.sendBroadcast(android.content.Intent):boolean");
    }

    public void sendBroadcastSync(Intent intent) {
        if (sendBroadcast(intent)) {
            executePendingBroadcasts();
        }
    }

    public void unregisterReceiver(BroadcastReceiver broadcastReceiver) {
        synchronized (this.mReceivers) {
            ArrayList remove = this.mReceivers.remove(broadcastReceiver);
            if (remove != null) {
                for (int size = remove.size() - 1; size >= 0; size--) {
                    ReceiverRecord receiverRecord = (ReceiverRecord) remove.get(size);
                    receiverRecord.dead = true;
                    for (int i = 0; i < receiverRecord.filter.countActions(); i++) {
                        String action = receiverRecord.filter.getAction(i);
                        ArrayList arrayList = this.mActions.get(action);
                        if (arrayList != null) {
                            for (int size2 = arrayList.size() - 1; size2 >= 0; size2--) {
                                ReceiverRecord receiverRecord2 = (ReceiverRecord) arrayList.get(size2);
                                if (receiverRecord2.receiver == broadcastReceiver) {
                                    receiverRecord2.dead = true;
                                    arrayList.remove(size2);
                                }
                            }
                            if (arrayList.size() <= 0) {
                                this.mActions.remove(action);
                            }
                        }
                    }
                }
            }
        }
    }
}
