// 
// Decompiled by Procyon v0.6.0
// 

package android.support.v4.content;

import java.util.Set;
import android.net.Uri;
import android.util.Log;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Message;
import android.os.Looper;
import android.content.BroadcastReceiver;
import android.os.Handler;
import android.content.Context;
import java.util.ArrayList;
import java.util.HashMap;

public final class LocalBroadcastManager
{
    private static final boolean DEBUG = false;
    static final int MSG_EXEC_PENDING_BROADCASTS = 1;
    private static final String TAG = "LocalBroadcastManager";
    private static LocalBroadcastManager mInstance;
    private static final Object mLock;
    private final HashMap<String, ArrayList<ReceiverRecord>> mActions;
    private final Context mAppContext;
    private final Handler mHandler;
    private final ArrayList<BroadcastRecord> mPendingBroadcasts;
    private final HashMap<BroadcastReceiver, ArrayList<ReceiverRecord>> mReceivers;
    
    static {
        mLock = new Object();
    }
    
    private LocalBroadcastManager(final Context mAppContext) {
        this.mReceivers = new HashMap<BroadcastReceiver, ArrayList<ReceiverRecord>>();
        this.mActions = new HashMap<String, ArrayList<ReceiverRecord>>();
        this.mPendingBroadcasts = new ArrayList<BroadcastRecord>();
        this.mAppContext = mAppContext;
        this.mHandler = new Handler(mAppContext.getMainLooper()) {
            public void handleMessage(final Message message) {
                switch (message.what) {
                    default: {
                        super.handleMessage(message);
                        break;
                    }
                    case 1: {
                        LocalBroadcastManager.this.executePendingBroadcasts();
                        break;
                    }
                }
            }
        };
    }
    
    private void executePendingBroadcasts() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     1: getfield        android/support/v4/content/LocalBroadcastManager.mReceivers:Ljava/util/HashMap;
        //     4: astore_1       
        //     5: aload_1        
        //     6: monitorenter   
        //     7: aload_0        
        //     8: getfield        android/support/v4/content/LocalBroadcastManager.mPendingBroadcasts:Ljava/util/ArrayList;
        //    11: invokevirtual   java/util/ArrayList.size:()I
        //    14: istore_2       
        //    15: iload_2        
        //    16: ifle            81
        //    19: iload_2        
        //    20: anewarray       Landroid/support/v4/content/LocalBroadcastManager$BroadcastRecord;
        //    23: astore_3       
        //    24: aload_0        
        //    25: getfield        android/support/v4/content/LocalBroadcastManager.mPendingBroadcasts:Ljava/util/ArrayList;
        //    28: aload_3        
        //    29: invokevirtual   java/util/ArrayList.toArray:([Ljava/lang/Object;)[Ljava/lang/Object;
        //    32: pop            
        //    33: aload_0        
        //    34: getfield        android/support/v4/content/LocalBroadcastManager.mPendingBroadcasts:Ljava/util/ArrayList;
        //    37: invokevirtual   java/util/ArrayList.clear:()V
        //    40: aload_1        
        //    41: monitorexit    
        //    42: iconst_0       
        //    43: istore_2       
        //    44: iload_2        
        //    45: aload_3        
        //    46: arraylength    
        //    47: if_icmpge       0
        //    50: aload_3        
        //    51: iload_2        
        //    52: aaload         
        //    53: astore          4
        //    55: aload           4
        //    57: getfield        android/support/v4/content/LocalBroadcastManager$BroadcastRecord.receivers:Ljava/util/ArrayList;
        //    60: invokevirtual   java/util/ArrayList.size:()I
        //    63: istore          5
        //    65: iconst_0       
        //    66: istore          6
        //    68: iload           6
        //    70: iload           5
        //    72: if_icmplt       89
        //    75: iinc            2, 1
        //    78: goto            44
        //    81: aload_1        
        //    82: monitorexit    
        //    83: return         
        //    84: astore_3       
        //    85: aload_1        
        //    86: monitorexit    
        //    87: aload_3        
        //    88: athrow         
        //    89: aload           4
        //    91: getfield        android/support/v4/content/LocalBroadcastManager$BroadcastRecord.receivers:Ljava/util/ArrayList;
        //    94: iload           6
        //    96: invokevirtual   java/util/ArrayList.get:(I)Ljava/lang/Object;
        //    99: checkcast       Landroid/support/v4/content/LocalBroadcastManager$ReceiverRecord;
        //   102: astore_1       
        //   103: aload_1        
        //   104: getfield        android/support/v4/content/LocalBroadcastManager$ReceiverRecord.dead:Z
        //   107: ifeq            116
        //   110: iinc            6, 1
        //   113: goto            68
        //   116: aload_1        
        //   117: getfield        android/support/v4/content/LocalBroadcastManager$ReceiverRecord.receiver:Landroid/content/BroadcastReceiver;
        //   120: aload_0        
        //   121: getfield        android/support/v4/content/LocalBroadcastManager.mAppContext:Landroid/content/Context;
        //   124: aload           4
        //   126: getfield        android/support/v4/content/LocalBroadcastManager$BroadcastRecord.intent:Landroid/content/Intent;
        //   129: invokevirtual   android/content/BroadcastReceiver.onReceive:(Landroid/content/Context;Landroid/content/Intent;)V
        //   132: goto            110
        //    Exceptions:
        //  Try           Handler
        //  Start  End    Start  End    Type
        //  -----  -----  -----  -----  ----
        //  7      15     84     89     Any
        //  19     42     84     89     Any
        //  81     83     84     89     Any
        //  85     87     84     89     Any
        // 
        // The error that occurred was:
        // 
        // java.lang.NullPointerException: Cannot invoke "com.strobel.assembler.metadata.TypeReference.getSimpleType()" because "type" is null
        //     at com.strobel.assembler.ir.StackMappingVisitor.push(StackMappingVisitor.java:290)
        //     at com.strobel.assembler.ir.StackMappingVisitor$InstructionAnalyzer.execute(StackMappingVisitor.java:837)
        //     at com.strobel.assembler.ir.StackMappingVisitor$InstructionAnalyzer.visit(StackMappingVisitor.java:398)
        //     at com.strobel.decompiler.ast.AstBuilder.performStackAnalysis(AstBuilder.java:2086)
        //     at com.strobel.decompiler.ast.AstBuilder.build(AstBuilder.java:108)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:203)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:93)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethodBody(AstBuilder.java:868)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethod(AstBuilder.java:761)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:638)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeCore(AstBuilder.java:605)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeNoCache(AstBuilder.java:195)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createType(AstBuilder.java:162)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addType(AstBuilder.java:137)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.buildAst(JavaLanguage.java:71)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.decompileType(JavaLanguage.java:59)
        //     at com.strobel.decompiler.DecompilerDriver.decompileType(DecompilerDriver.java:333)
        //     at com.strobel.decompiler.DecompilerDriver.decompileJar(DecompilerDriver.java:254)
        //     at com.strobel.decompiler.DecompilerDriver.main(DecompilerDriver.java:144)
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public static LocalBroadcastManager getInstance(final Context context) {
        synchronized (LocalBroadcastManager.mLock) {
            if (LocalBroadcastManager.mInstance == null) {
                LocalBroadcastManager.mInstance = new LocalBroadcastManager(context.getApplicationContext());
            }
            return LocalBroadcastManager.mInstance;
        }
    }
    
    public void registerReceiver(final BroadcastReceiver broadcastReceiver, final IntentFilter intentFilter) {
        while (true) {
            while (true) {
                final ReceiverRecord receiverRecord;
                int n;
                synchronized (this.mReceivers) {
                    receiverRecord = new ReceiverRecord(intentFilter, broadcastReceiver);
                    final ArrayList list = this.mReceivers.get(broadcastReceiver);
                    ArrayList<ReceiverRecord> list2;
                    if (list != null) {
                        list2 = list;
                    }
                    else {
                        final ArrayList<ReceiverRecord> value = new ArrayList<ReceiverRecord>(1);
                        this.mReceivers.put(broadcastReceiver, value);
                        list2 = value;
                    }
                    list2.add(receiverRecord);
                    n = 0;
                    if (n >= intentFilter.countActions()) {
                        return;
                    }
                }
                final String action = intentFilter.getAction(n);
                ArrayList<ReceiverRecord> value2 = this.mActions.get(action);
                if (value2 == null) {
                    value2 = new ArrayList<ReceiverRecord>(1);
                    this.mActions.put(action, value2);
                }
                value2.add(receiverRecord);
                ++n;
                continue;
            }
        }
    }
    
    public boolean sendBroadcast(final Intent obj) {
        ArrayList<ReceiverRecord> list;
        while (true) {
            final String action;
            final String resolveTypeIfNeeded;
            final Uri data;
            final String scheme;
            final Set categories;
            boolean b;
            final ArrayList obj2;
            synchronized (this.mReceivers) {
                action = obj.getAction();
                resolveTypeIfNeeded = obj.resolveTypeIfNeeded(this.mAppContext.getContentResolver());
                data = obj.getData();
                scheme = obj.getScheme();
                categories = obj.getCategories();
                if ((obj.getFlags() & 0x8) == 0x0) {
                    b = false;
                }
                else {
                    b = true;
                }
                if (b) {
                    Log.v("LocalBroadcastManager", "Resolving type " + resolveTypeIfNeeded + " scheme " + scheme + " of intent " + obj);
                }
                obj2 = this.mActions.get(obj.getAction());
                if (obj2 == null) {
                    return false;
                }
            }
            if (b) {
                Log.v("LocalBroadcastManager", "Action list: " + obj2);
            }
            list = null;
            for (int i = 0; i < obj2.size(); ++i) {
                final ReceiverRecord e = (ReceiverRecord)obj2.get(i);
                if (b) {
                    Log.v("LocalBroadcastManager", "Matching against filter " + e.filter);
                }
                if (!e.broadcasting) {
                    final int match = e.filter.match(action, resolveTypeIfNeeded, scheme, data, categories, "LocalBroadcastManager");
                    if (match < 0) {
                        if (b) {
                            String str = null;
                            switch (match) {
                                default: {
                                    str = "unknown reason";
                                    break;
                                }
                                case -3: {
                                    str = "action";
                                    break;
                                }
                                case -4: {
                                    str = "category";
                                    break;
                                }
                                case -2: {
                                    str = "data";
                                    break;
                                }
                                case -1: {
                                    str = "type";
                                    break;
                                }
                            }
                            Log.v("LocalBroadcastManager", "  Filter did not match: " + str);
                        }
                    }
                    else {
                        if (b) {
                            Log.v("LocalBroadcastManager", "  Filter matched!  match=0x" + Integer.toHexString(match));
                        }
                        if (list == null) {
                            list = new ArrayList<ReceiverRecord>();
                        }
                        list.add(e);
                        e.broadcasting = true;
                    }
                }
                else if (b) {
                    Log.v("LocalBroadcastManager", "  Filter's target already added");
                }
            }
            if (list != null) {
                break;
            }
            return false;
        }
        for (int j = 0; j < list.size(); ++j) {
            list.get(j).broadcasting = false;
        }
        final Intent intent;
        this.mPendingBroadcasts.add(new BroadcastRecord(intent, list));
        if (!this.mHandler.hasMessages(1)) {
            this.mHandler.sendEmptyMessage(1);
        }
        final HashMap hashMap;
        monitorexit(hashMap);
        return true;
    }
    
    public void sendBroadcastSync(final Intent intent) {
        if (this.sendBroadcast(intent)) {
            this.executePendingBroadcasts();
        }
    }
    
    public void unregisterReceiver(final BroadcastReceiver key) {
        while (true) {
            while (true) {
                ArrayList list2 = null;
                int n = 0;
                Label_0158: {
                    synchronized (this.mReceivers) {
                        final ArrayList list = this.mReceivers.remove(key);
                        if (list != null) {
                            for (int i = list.size() - 1; i >= 0; --i) {
                                final ReceiverRecord receiverRecord = (ReceiverRecord)list.get(i);
                                receiverRecord.dead = true;
                                for (int j = 0; j < receiverRecord.filter.countActions(); ++j) {
                                    final String action = receiverRecord.filter.getAction(j);
                                    list2 = this.mActions.get(action);
                                    if (list2 != null) {
                                        n = list2.size() - 1;
                                        if (n >= 0) {
                                            break Label_0158;
                                        }
                                        if (list2.size() <= 0) {
                                            this.mActions.remove(action);
                                        }
                                    }
                                }
                            }
                        }
                        return;
                    }
                }
                final ReceiverRecord receiverRecord2 = (ReceiverRecord)list2.get(n);
                final Throwable t;
                if (receiverRecord2.receiver == t) {
                    receiverRecord2.dead = true;
                    list2.remove(n);
                }
                --n;
                continue;
            }
        }
    }
    
    private static final class BroadcastRecord
    {
        final Intent intent;
        final ArrayList<ReceiverRecord> receivers;
        
        BroadcastRecord(final Intent intent, final ArrayList<ReceiverRecord> receivers) {
            this.intent = intent;
            this.receivers = receivers;
        }
    }
    
    private static final class ReceiverRecord
    {
        boolean broadcasting;
        boolean dead;
        final IntentFilter filter;
        final BroadcastReceiver receiver;
        
        ReceiverRecord(final IntentFilter filter, final BroadcastReceiver receiver) {
            this.filter = filter;
            this.receiver = receiver;
        }
        
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder(128);
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
}
