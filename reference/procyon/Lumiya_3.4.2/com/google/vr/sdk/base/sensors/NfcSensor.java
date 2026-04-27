// 
// Decompiled by Procyon v0.6.0
// 

package com.google.vr.sdk.base.sensors;

import android.app.PendingIntent;
import android.app.Activity;
import android.nfc.FormatException;
import android.nfc.TagLostException;
import android.os.Handler;
import java.util.Iterator;
import android.net.Uri;
import com.google.vr.sdk.base.GvrViewerParams;
import android.nfc.NdefRecord;
import android.nfc.NdefMessage;
import java.io.IOException;
import android.util.Log;
import android.content.Intent;
import com.google.vr.sdk.base.PermissionUtils;
import java.util.ArrayList;
import android.content.IntentFilter;
import java.util.Timer;
import android.content.BroadcastReceiver;
import android.nfc.NfcAdapter;
import java.util.List;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.content.Context;

public class NfcSensor
{
    private static final int MAX_CONNECTION_FAILURES = 1;
    private static final long NFC_POLLING_INTERVAL_MS = 250L;
    private static final String TAG = "NfcSensor";
    private static NfcSensor sInstance;
    private final Context context;
    private Ndef currentNdef;
    private Tag currentTag;
    private boolean currentTagIsCardboard;
    private final List<ListenerHelper> listeners;
    private final NfcAdapter nfcAdapter;
    private BroadcastReceiver nfcBroadcastReceiver;
    private Timer nfcDisconnectTimer;
    private IntentFilter[] nfcIntentFilters;
    private int tagConnectionFailures;
    private final Object tagLock;
    
    private NfcSensor(final Context context) {
        this.context = context.getApplicationContext();
        this.listeners = new ArrayList<ListenerHelper>();
        this.tagLock = new Object();
        if (!PermissionUtils.hasPermission(context, "android.permission.NFC")) {
            this.nfcAdapter = null;
        }
        else {
            this.nfcAdapter = NfcAdapter.getDefaultAdapter(context);
        }
        if (this.nfcAdapter != null) {
            this.nfcBroadcastReceiver = new BroadcastReceiver() {
                public void onReceive(final Context context, final Intent intent) {
                    NfcSensor.this.onNfcIntent(intent);
                }
            };
            return;
        }
        Log.w("NfcSensor", "NFC is not supported on this phone or application doesn't have NFC permission.");
    }
    
    private void closeCurrentNfcTag() {
        Label_0037: {
            if (this.nfcDisconnectTimer != null) {
                break Label_0037;
            }
        Label_0021_Outer:
            while (true) {
                if (this.currentNdef == null) {
                    return;
                }
                while (true) {
                    try {
                        this.currentNdef.close();
                        this.currentTag = null;
                        this.currentNdef = null;
                        this.currentTagIsCardboard = false;
                        return;
                        this.nfcDisconnectTimer.cancel();
                        continue Label_0021_Outer;
                    }
                    catch (final IOException ex) {
                        Log.w("NfcSensor", ex.toString());
                        continue;
                    }
                    break;
                }
                break;
            }
        }
    }
    
    public static NfcSensor getInstance(final Context context) {
        if (NfcSensor.sInstance == null) {
            NfcSensor.sInstance = new NfcSensor(context);
        }
        return NfcSensor.sInstance;
    }
    
    private boolean isCardboardNdefMessage(final NdefMessage ndefMessage) {
        if (ndefMessage != null) {
            final NdefRecord[] records = ndefMessage.getRecords();
            for (int length = records.length, i = 0; i < length; ++i) {
                if (this.isCardboardNdefRecord(records[i])) {
                    return true;
                }
            }
            return false;
        }
        return false;
    }
    
    private boolean isCardboardNdefRecord(final NdefRecord ndefRecord) {
        boolean b = false;
        if (ndefRecord != null) {
            final Uri uri = ndefRecord.toUri();
            if (uri != null && GvrViewerParams.isGvrUri(uri)) {
                b = true;
            }
            return b;
        }
        return false;
    }
    
    private void onNewNfcTag(final Tag p0) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     1: istore_2       
        //     2: aload_1        
        //     3: ifnull          106
        //     6: aload_0        
        //     7: getfield        com/google/vr/sdk/base/sensors/NfcSensor.tagLock:Ljava/lang/Object;
        //    10: astore_3       
        //    11: aload_3        
        //    12: monitorenter   
        //    13: aload_0        
        //    14: getfield        com/google/vr/sdk/base/sensors/NfcSensor.currentTag:Landroid/nfc/Tag;
        //    17: astore          4
        //    19: aload_0        
        //    20: getfield        com/google/vr/sdk/base/sensors/NfcSensor.currentNdef:Landroid/nfc/tech/Ndef;
        //    23: astore          5
        //    25: aload_0        
        //    26: getfield        com/google/vr/sdk/base/sensors/NfcSensor.currentTagIsCardboard:Z
        //    29: istore          6
        //    31: aload_0        
        //    32: invokespecial   com/google/vr/sdk/base/sensors/NfcSensor.closeCurrentNfcTag:()V
        //    35: aload_0        
        //    36: aload_1        
        //    37: putfield        com/google/vr/sdk/base/sensors/NfcSensor.currentTag:Landroid/nfc/Tag;
        //    40: aload_0        
        //    41: aload_1        
        //    42: invokestatic    android/nfc/tech/Ndef.get:(Landroid/nfc/Tag;)Landroid/nfc/tech/Ndef;
        //    45: putfield        com/google/vr/sdk/base/sensors/NfcSensor.currentNdef:Landroid/nfc/tech/Ndef;
        //    48: aload_0        
        //    49: getfield        com/google/vr/sdk/base/sensors/NfcSensor.currentNdef:Landroid/nfc/tech/Ndef;
        //    52: astore_1       
        //    53: aload_1        
        //    54: ifnull          107
        //    57: aload           5
        //    59: ifnonnull       127
        //    62: iconst_0       
        //    63: istore          7
        //    65: aload_0        
        //    66: getfield        com/google/vr/sdk/base/sensors/NfcSensor.currentNdef:Landroid/nfc/tech/Ndef;
        //    69: invokevirtual   android/nfc/tech/Ndef.connect:()V
        //    72: aload_0        
        //    73: getfield        com/google/vr/sdk/base/sensors/NfcSensor.currentNdef:Landroid/nfc/tech/Ndef;
        //    76: invokevirtual   android/nfc/tech/Ndef.getCachedNdefMessage:()Landroid/nfc/NdefMessage;
        //    79: astore          5
        //    81: aload_0        
        //    82: aload_0        
        //    83: aload           5
        //    85: invokespecial   com/google/vr/sdk/base/sensors/NfcSensor.isCardboardNdefMessage:(Landroid/nfc/NdefMessage;)Z
        //    88: putfield        com/google/vr/sdk/base/sensors/NfcSensor.currentTagIsCardboard:Z
        //    91: iload           7
        //    93: ifeq            258
        //    96: aload_0        
        //    97: getfield        com/google/vr/sdk/base/sensors/NfcSensor.currentTagIsCardboard:Z
        //   100: ifne            326
        //   103: aload_3        
        //   104: monitorexit    
        //   105: return         
        //   106: return         
        //   107: iload           6
        //   109: ifne            115
        //   112: aload_3        
        //   113: monitorexit    
        //   114: return         
        //   115: aload_0        
        //   116: invokespecial   com/google/vr/sdk/base/sensors/NfcSensor.sendDisconnectionEvent:()V
        //   119: goto            112
        //   122: astore_1       
        //   123: aload_3        
        //   124: monitorexit    
        //   125: aload_1        
        //   126: athrow         
        //   127: aload_0        
        //   128: getfield        com/google/vr/sdk/base/sensors/NfcSensor.currentTag:Landroid/nfc/Tag;
        //   131: invokevirtual   android/nfc/Tag.getId:()[B
        //   134: astore_1       
        //   135: aload           4
        //   137: invokevirtual   android/nfc/Tag.getId:()[B
        //   140: astore          4
        //   142: aload_1        
        //   143: ifnonnull       157
        //   146: iload_2        
        //   147: istore          7
        //   149: iload           7
        //   151: ifeq            183
        //   154: goto            65
        //   157: iload_2        
        //   158: istore          7
        //   160: aload           4
        //   162: ifnull          149
        //   165: iload_2        
        //   166: istore          7
        //   168: aload_1        
        //   169: aload           4
        //   171: invokestatic    java/util/Arrays.equals:([B[B)Z
        //   174: ifeq            149
        //   177: iconst_1       
        //   178: istore          7
        //   180: goto            149
        //   183: iload           6
        //   185: ifeq            154
        //   188: aload_0        
        //   189: invokespecial   com/google/vr/sdk/base/sensors/NfcSensor.sendDisconnectionEvent:()V
        //   192: goto            65
        //   195: astore_1       
        //   196: aload_1        
        //   197: invokevirtual   java/lang/Exception.toString:()Ljava/lang/String;
        //   200: invokestatic    java/lang/String.valueOf:(Ljava/lang/Object;)Ljava/lang/String;
        //   203: astore_1       
        //   204: aload_1        
        //   205: invokevirtual   java/lang/String.length:()I
        //   208: ifne            236
        //   211: new             Ljava/lang/String;
        //   214: astore_1       
        //   215: aload_1        
        //   216: ldc             "Error reading NFC tag: "
        //   218: invokespecial   java/lang/String.<init>:(Ljava/lang/String;)V
        //   221: ldc             "NfcSensor"
        //   223: aload_1        
        //   224: invokestatic    android/util/Log.e:(Ljava/lang/String;Ljava/lang/String;)I
        //   227: pop            
        //   228: iload           7
        //   230: ifne            246
        //   233: aload_3        
        //   234: monitorexit    
        //   235: return         
        //   236: ldc             "Error reading NFC tag: "
        //   238: aload_1        
        //   239: invokevirtual   java/lang/String.concat:(Ljava/lang/String;)Ljava/lang/String;
        //   242: astore_1       
        //   243: goto            221
        //   246: iload           6
        //   248: ifeq            233
        //   251: aload_0        
        //   252: invokespecial   com/google/vr/sdk/base/sensors/NfcSensor.sendDisconnectionEvent:()V
        //   255: goto            233
        //   258: aload_0        
        //   259: getfield        com/google/vr/sdk/base/sensors/NfcSensor.currentTagIsCardboard:Z
        //   262: ifeq            96
        //   265: aload_0        
        //   266: getfield        com/google/vr/sdk/base/sensors/NfcSensor.listeners:Ljava/util/List;
        //   269: astore_1       
        //   270: aload_1        
        //   271: monitorenter   
        //   272: aload_0        
        //   273: getfield        com/google/vr/sdk/base/sensors/NfcSensor.listeners:Ljava/util/List;
        //   276: invokeinterface java/util/List.iterator:()Ljava/util/Iterator;
        //   281: astore          4
        //   283: aload           4
        //   285: invokeinterface java/util/Iterator.hasNext:()Z
        //   290: ifne            305
        //   293: aload_1        
        //   294: monitorexit    
        //   295: goto            96
        //   298: astore          4
        //   300: aload_1        
        //   301: monitorexit    
        //   302: aload           4
        //   304: athrow         
        //   305: aload           4
        //   307: invokeinterface java/util/Iterator.next:()Ljava/lang/Object;
        //   312: checkcast       Lcom/google/vr/sdk/base/sensors/NfcSensor$ListenerHelper;
        //   315: aload           5
        //   317: invokestatic    com/google/vr/sdk/base/GvrViewerParams.createFromNfcContents:(Landroid/nfc/NdefMessage;)Lcom/google/vr/sdk/base/GvrViewerParams;
        //   320: invokevirtual   com/google/vr/sdk/base/sensors/NfcSensor$ListenerHelper.onInsertedIntoGvrViewer:(Lcom/google/vr/sdk/base/GvrViewerParams;)V
        //   323: goto            283
        //   326: aload_0        
        //   327: iconst_0       
        //   328: putfield        com/google/vr/sdk/base/sensors/NfcSensor.tagConnectionFailures:I
        //   331: new             Ljava/util/Timer;
        //   334: astore_1       
        //   335: aload_1        
        //   336: ldc             "NFC disconnect timer"
        //   338: invokespecial   java/util/Timer.<init>:(Ljava/lang/String;)V
        //   341: aload_0        
        //   342: aload_1        
        //   343: putfield        com/google/vr/sdk/base/sensors/NfcSensor.nfcDisconnectTimer:Ljava/util/Timer;
        //   346: aload_0        
        //   347: getfield        com/google/vr/sdk/base/sensors/NfcSensor.nfcDisconnectTimer:Ljava/util/Timer;
        //   350: astore_1       
        //   351: new             Lcom/google/vr/sdk/base/sensors/NfcSensor$2;
        //   354: astore          4
        //   356: aload           4
        //   358: aload_0        
        //   359: invokespecial   com/google/vr/sdk/base/sensors/NfcSensor$2.<init>:(Lcom/google/vr/sdk/base/sensors/NfcSensor;)V
        //   362: aload_1        
        //   363: aload           4
        //   365: ldc2_w          250
        //   368: ldc2_w          250
        //   371: invokevirtual   java/util/Timer.schedule:(Ljava/util/TimerTask;JJ)V
        //   374: goto            103
        //    Exceptions:
        //  Try           Handler
        //  Start  End    Start  End    Type                 
        //  -----  -----  -----  -----  ---------------------
        //  13     53     122    127    Any
        //  65     81     195    258    Ljava/lang/Exception;
        //  65     81     122    127    Any
        //  81     91     122    127    Any
        //  96     103    122    127    Any
        //  103    105    122    127    Any
        //  112    114    122    127    Any
        //  115    119    122    127    Any
        //  123    125    122    127    Any
        //  127    142    122    127    Any
        //  168    177    122    127    Any
        //  188    192    122    127    Any
        //  196    221    122    127    Any
        //  221    228    122    127    Any
        //  233    235    122    127    Any
        //  236    243    122    127    Any
        //  251    255    122    127    Any
        //  258    272    122    127    Any
        //  272    283    298    305    Any
        //  283    295    298    305    Any
        //  300    302    298    305    Any
        //  302    305    122    127    Any
        //  305    323    298    305    Any
        //  326    374    122    127    Any
        // 
        // The error that occurred was:
        // 
        // java.lang.IllegalStateException: Expression is linked from several locations: Label_0065:
        //     at com.strobel.decompiler.ast.Error.expressionLinkedFromMultipleLocations(Error.java:27)
        //     at com.strobel.decompiler.ast.AstOptimizer.mergeDisparateObjectInitializations(AstOptimizer.java:2604)
        //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:235)
        //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:42)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:206)
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
    
    private void sendDisconnectionEvent() {
        synchronized (this.listeners) {
            final Iterator<ListenerHelper> iterator = this.listeners.iterator();
            while (iterator.hasNext()) {
                iterator.next().onRemovedFromGvrViewer();
            }
        }
    }
    
    public void addOnCardboardNfcListener(final OnCardboardNfcListener onCardboardNfcListener) {
        if (onCardboardNfcListener != null) {
            while (true) {
                while (true) {
                    Object iterator = null;
                    Label_0140: {
                        synchronized (this.listeners) {
                            if (this.listeners.isEmpty()) {
                                iterator = new IntentFilter("android.nfc.action.NDEF_DISCOVERED");
                                ((IntentFilter)iterator).addAction("android.nfc.action.TECH_DISCOVERED");
                                ((IntentFilter)iterator).addAction("android.nfc.action.TAG_DISCOVERED");
                                this.nfcIntentFilters = new IntentFilter[] { (IntentFilter)iterator };
                                this.context.registerReceiver(this.nfcBroadcastReceiver, (IntentFilter)iterator);
                            }
                            iterator = this.listeners.iterator();
                            if (!((Iterator)iterator).hasNext()) {
                                final List<ListenerHelper> listeners = this.listeners;
                                iterator = new ListenerHelper(onCardboardNfcListener, new Handler());
                                listeners.add((ListenerHelper)iterator);
                                return;
                            }
                            break Label_0140;
                        }
                    }
                    if (((ListenerHelper)((Iterator)iterator).next()).getListener() == onCardboardNfcListener) {
                        break;
                    }
                    continue;
                }
            }
            final List list;
            monitorexit(list);
        }
    }
    
    public NdefMessage getCurrentTagContents() throws TagLostException, IOException, FormatException {
        NdefMessage ndefMessage = null;
        synchronized (this.tagLock) {
            if (this.currentNdef != null) {
                ndefMessage = this.currentNdef.getNdefMessage();
            }
            return ndefMessage;
        }
    }
    
    public int getTagCapacity() {
        synchronized (this.tagLock) {
            if (this.currentNdef != null) {
                return this.currentNdef.getMaxSize();
            }
            throw new IllegalStateException("No NFC tag");
        }
    }
    
    public NdefMessage getTagContents() {
        NdefMessage cachedNdefMessage = null;
        synchronized (this.tagLock) {
            if (this.currentNdef != null) {
                cachedNdefMessage = this.currentNdef.getCachedNdefMessage();
            }
            return cachedNdefMessage;
        }
    }
    
    public boolean isDeviceInCardboard() {
        synchronized (this.tagLock) {
            return this.currentTagIsCardboard;
        }
    }
    
    public boolean isNfcEnabled() {
        boolean b = false;
        if (this.isNfcSupported() && this.nfcAdapter.isEnabled()) {
            b = true;
        }
        return b;
    }
    
    public boolean isNfcSupported() {
        return this.nfcAdapter != null;
    }
    
    public void onNfcIntent(final Intent intent) {
        if (this.isNfcEnabled() && intent != null && this.nfcIntentFilters[0].matchAction(intent.getAction())) {
            this.onNewNfcTag((Tag)intent.getParcelableExtra("android.nfc.extra.TAG"));
        }
    }
    
    public void onPause(final Activity activity) {
        if (this.isNfcEnabled()) {
            this.nfcAdapter.disableForegroundDispatch(activity);
        }
    }
    
    public void onResume(final Activity activity) {
        if (this.isNfcEnabled()) {
            final Intent intent = new Intent("android.nfc.action.NDEF_DISCOVERED");
            intent.setPackage(activity.getPackageName());
            this.nfcAdapter.enableForegroundDispatch(activity, PendingIntent.getBroadcast(this.context, 0, intent, 0), this.nfcIntentFilters, (String[][])null);
        }
    }
    
    public void removeOnCardboardNfcListener(final OnCardboardNfcListener onCardboardNfcListener) {
        if (onCardboardNfcListener == null) {
            return;
        }
        while (true) {
            Label_0081: {
                synchronized (this.listeners) {
                    for (final ListenerHelper listenerHelper : this.listeners) {
                        if (listenerHelper.getListener() == onCardboardNfcListener) {
                            this.listeners.remove(listenerHelper);
                            break;
                        }
                    }
                    if (this.nfcBroadcastReceiver == null) {
                        return;
                    }
                    break Label_0081;
                }
            }
            if (this.listeners.isEmpty()) {
                this.context.unregisterReceiver(this.nfcBroadcastReceiver);
            }
        }
    }
    
    public void writeUri(final Uri p0) throws TagLostException, IOException, IllegalArgumentException {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     1: astore_2       
        //     2: iconst_0       
        //     3: istore_3       
        //     4: aload_0        
        //     5: getfield        com/google/vr/sdk/base/sensors/NfcSensor.tagLock:Ljava/lang/Object;
        //     8: astore          4
        //    10: aload           4
        //    12: monitorenter   
        //    13: aload_0        
        //    14: getfield        com/google/vr/sdk/base/sensors/NfcSensor.currentTag:Landroid/nfc/Tag;
        //    17: ifnull          94
        //    20: aload_1        
        //    21: invokestatic    android/nfc/NdefRecord.createUri:(Landroid/net/Uri;)Landroid/nfc/NdefRecord;
        //    24: astore          5
        //    26: aload_0        
        //    27: invokevirtual   com/google/vr/sdk/base/sensors/NfcSensor.getCurrentTagContents:()Landroid/nfc/NdefMessage;
        //    30: astore_1       
        //    31: aload_1        
        //    32: ifnonnull       122
        //    35: aload_2        
        //    36: astore_1       
        //    37: aload_1        
        //    38: ifnull          230
        //    41: aload_0        
        //    42: getfield        com/google/vr/sdk/base/sensors/NfcSensor.currentNdef:Landroid/nfc/tech/Ndef;
        //    45: ifnonnull       250
        //    48: aload_0        
        //    49: getfield        com/google/vr/sdk/base/sensors/NfcSensor.currentTag:Landroid/nfc/Tag;
        //    52: invokestatic    android/nfc/tech/NdefFormatable.get:(Landroid/nfc/Tag;)Landroid/nfc/tech/NdefFormatable;
        //    55: astore_2       
        //    56: aload_2        
        //    57: ifnull          415
        //    60: ldc             "NfcSensor"
        //    62: ldc_w           "Ndef technology not available. Falling back to NdefFormattable."
        //    65: invokestatic    android/util/Log.w:(Ljava/lang/String;Ljava/lang/String;)I
        //    68: pop            
        //    69: aload_2        
        //    70: invokevirtual   android/nfc/tech/NdefFormatable.connect:()V
        //    73: aload_2        
        //    74: aload_1        
        //    75: invokevirtual   android/nfc/tech/NdefFormatable.format:(Landroid/nfc/NdefMessage;)V
        //    78: aload_2        
        //    79: invokevirtual   android/nfc/tech/NdefFormatable.close:()V
        //    82: aload_0        
        //    83: aload_0        
        //    84: getfield        com/google/vr/sdk/base/sensors/NfcSensor.currentTag:Landroid/nfc/Tag;
        //    87: invokespecial   com/google/vr/sdk/base/sensors/NfcSensor.onNewNfcTag:(Landroid/nfc/Tag;)V
        //    90: aload           4
        //    92: monitorexit    
        //    93: return         
        //    94: new             Ljava/lang/IllegalStateException;
        //    97: astore_1       
        //    98: aload_1        
        //    99: ldc_w           "No NFC tag found"
        //   102: invokespecial   java/lang/IllegalStateException.<init>:(Ljava/lang/String;)V
        //   105: aload_1        
        //   106: athrow         
        //   107: astore_1       
        //   108: aload           4
        //   110: monitorexit    
        //   111: aload_1        
        //   112: athrow         
        //   113: astore_1       
        //   114: aload_0        
        //   115: invokevirtual   com/google/vr/sdk/base/sensors/NfcSensor.getTagContents:()Landroid/nfc/NdefMessage;
        //   118: astore_1       
        //   119: goto            31
        //   122: new             Ljava/util/ArrayList;
        //   125: astore_2       
        //   126: aload_2        
        //   127: invokespecial   java/util/ArrayList.<init>:()V
        //   130: aload_1        
        //   131: invokevirtual   android/nfc/NdefMessage.getRecords:()[Landroid/nfc/NdefRecord;
        //   134: astore_1       
        //   135: aload_1        
        //   136: arraylength    
        //   137: istore          6
        //   139: iconst_0       
        //   140: istore          7
        //   142: iload_3        
        //   143: iload           6
        //   145: if_icmplt       173
        //   148: new             Landroid/nfc/NdefMessage;
        //   151: dup            
        //   152: aload_2        
        //   153: aload_2        
        //   154: invokevirtual   java/util/ArrayList.size:()I
        //   157: anewarray       Landroid/nfc/NdefRecord;
        //   160: invokevirtual   java/util/ArrayList.toArray:([Ljava/lang/Object;)[Ljava/lang/Object;
        //   163: checkcast       [Landroid/nfc/NdefRecord;
        //   166: invokespecial   android/nfc/NdefMessage.<init>:([Landroid/nfc/NdefRecord;)V
        //   169: astore_1       
        //   170: goto            37
        //   173: aload_1        
        //   174: iload_3        
        //   175: aaload         
        //   176: astore          8
        //   178: aload_0        
        //   179: aload           8
        //   181: invokespecial   com/google/vr/sdk/base/sensors/NfcSensor.isCardboardNdefRecord:(Landroid/nfc/NdefRecord;)Z
        //   184: ifne            208
        //   187: aload_2        
        //   188: aload           8
        //   190: invokevirtual   java/util/ArrayList.add:(Ljava/lang/Object;)Z
        //   193: pop            
        //   194: iload           7
        //   196: istore          9
        //   198: iinc            3, 1
        //   201: iload           9
        //   203: istore          7
        //   205: goto            142
        //   208: iload           7
        //   210: istore          9
        //   212: iload           7
        //   214: ifne            198
        //   217: aload_2        
        //   218: aload           5
        //   220: invokevirtual   java/util/ArrayList.add:(Ljava/lang/Object;)Z
        //   223: pop            
        //   224: iconst_1       
        //   225: istore          9
        //   227: goto            198
        //   230: new             Landroid/nfc/NdefMessage;
        //   233: dup            
        //   234: iconst_1       
        //   235: anewarray       Landroid/nfc/NdefRecord;
        //   238: dup            
        //   239: iconst_0       
        //   240: aload           5
        //   242: aastore        
        //   243: invokespecial   android/nfc/NdefMessage.<init>:([Landroid/nfc/NdefRecord;)V
        //   246: astore_1       
        //   247: goto            41
        //   250: aload_0        
        //   251: getfield        com/google/vr/sdk/base/sensors/NfcSensor.currentNdef:Landroid/nfc/tech/Ndef;
        //   254: invokevirtual   android/nfc/tech/Ndef.isConnected:()Z
        //   257: ifeq            329
        //   260: aload_0        
        //   261: getfield        com/google/vr/sdk/base/sensors/NfcSensor.currentNdef:Landroid/nfc/tech/Ndef;
        //   264: invokevirtual   android/nfc/tech/Ndef.getMaxSize:()I
        //   267: istore_3       
        //   268: aload_1        
        //   269: invokevirtual   android/nfc/NdefMessage.getByteArrayLength:()I
        //   272: istore          9
        //   274: iload_3        
        //   275: iload           9
        //   277: if_icmplt       339
        //   280: aload_0        
        //   281: getfield        com/google/vr/sdk/base/sensors/NfcSensor.currentNdef:Landroid/nfc/tech/Ndef;
        //   284: aload_1        
        //   285: invokevirtual   android/nfc/tech/Ndef.writeNdefMessage:(Landroid/nfc/NdefMessage;)V
        //   288: goto            82
        //   291: astore_1       
        //   292: new             Ljava/lang/RuntimeException;
        //   295: astore_2       
        //   296: aload_1        
        //   297: invokevirtual   android/nfc/FormatException.toString:()Ljava/lang/String;
        //   300: invokestatic    java/lang/String.valueOf:(Ljava/lang/Object;)Ljava/lang/String;
        //   303: astore_1       
        //   304: aload_1        
        //   305: invokevirtual   java/lang/String.length:()I
        //   308: ifne            404
        //   311: new             Ljava/lang/String;
        //   314: astore_1       
        //   315: aload_1        
        //   316: ldc_w           "Internal error when writing to NFC tag: "
        //   319: invokespecial   java/lang/String.<init>:(Ljava/lang/String;)V
        //   322: aload_2        
        //   323: aload_1        
        //   324: invokespecial   java/lang/RuntimeException.<init>:(Ljava/lang/String;)V
        //   327: aload_2        
        //   328: athrow         
        //   329: aload_0        
        //   330: getfield        com/google/vr/sdk/base/sensors/NfcSensor.currentNdef:Landroid/nfc/tech/Ndef;
        //   333: invokevirtual   android/nfc/tech/Ndef.connect:()V
        //   336: goto            260
        //   339: new             Ljava/lang/IllegalArgumentException;
        //   342: astore_2       
        //   343: aload_0        
        //   344: getfield        com/google/vr/sdk/base/sensors/NfcSensor.currentNdef:Landroid/nfc/tech/Ndef;
        //   347: invokevirtual   android/nfc/tech/Ndef.getMaxSize:()I
        //   350: istore          9
        //   352: aload_1        
        //   353: invokevirtual   android/nfc/NdefMessage.getByteArrayLength:()I
        //   356: istore_3       
        //   357: new             Ljava/lang/StringBuilder;
        //   360: astore_1       
        //   361: aload_1        
        //   362: bipush          82
        //   364: invokespecial   java/lang/StringBuilder.<init>:(I)V
        //   367: aload_2        
        //   368: aload_1        
        //   369: ldc_w           "Not enough capacity in NFC tag. Capacity: "
        //   372: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   375: iload           9
        //   377: invokevirtual   java/lang/StringBuilder.append:(I)Ljava/lang/StringBuilder;
        //   380: ldc_w           " bytes, "
        //   383: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   386: iload_3        
        //   387: invokevirtual   java/lang/StringBuilder.append:(I)Ljava/lang/StringBuilder;
        //   390: ldc_w           " required."
        //   393: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   396: invokevirtual   java/lang/StringBuilder.toString:()Ljava/lang/String;
        //   399: invokespecial   java/lang/IllegalArgumentException.<init>:(Ljava/lang/String;)V
        //   402: aload_2        
        //   403: athrow         
        //   404: ldc_w           "Internal error when writing to NFC tag: "
        //   407: aload_1        
        //   408: invokevirtual   java/lang/String.concat:(Ljava/lang/String;)Ljava/lang/String;
        //   411: astore_1       
        //   412: goto            322
        //   415: new             Ljava/io/IOException;
        //   418: astore_1       
        //   419: aload_1        
        //   420: ldc_w           "Could not find a writable technology for the NFC tag"
        //   423: invokespecial   java/io/IOException.<init>:(Ljava/lang/String;)V
        //   426: aload_1        
        //   427: athrow         
        //   428: astore_1       
        //   429: new             Ljava/lang/RuntimeException;
        //   432: astore_2       
        //   433: aload_1        
        //   434: invokevirtual   android/nfc/FormatException.toString:()Ljava/lang/String;
        //   437: invokestatic    java/lang/String.valueOf:(Ljava/lang/Object;)Ljava/lang/String;
        //   440: astore_1       
        //   441: aload_1        
        //   442: invokevirtual   java/lang/String.length:()I
        //   445: ifne            466
        //   448: new             Ljava/lang/String;
        //   451: astore_1       
        //   452: aload_1        
        //   453: ldc_w           "Internal error when writing to NFC tag: "
        //   456: invokespecial   java/lang/String.<init>:(Ljava/lang/String;)V
        //   459: aload_2        
        //   460: aload_1        
        //   461: invokespecial   java/lang/RuntimeException.<init>:(Ljava/lang/String;)V
        //   464: aload_2        
        //   465: athrow         
        //   466: ldc_w           "Internal error when writing to NFC tag: "
        //   469: aload_1        
        //   470: invokevirtual   java/lang/String.concat:(Ljava/lang/String;)Ljava/lang/String;
        //   473: astore_1       
        //   474: goto            459
        //    Exceptions:
        //  throws android.nfc.TagLostException
        //  throws java.io.IOException
        //  throws java.lang.IllegalArgumentException
        //    Exceptions:
        //  Try           Handler
        //  Start  End    Start  End    Type                         
        //  -----  -----  -----  -----  -----------------------------
        //  13     26     107    113    Any
        //  26     31     113    122    Ljava/lang/Exception;
        //  26     31     107    113    Any
        //  41     56     107    113    Any
        //  60     69     107    113    Any
        //  69     82     428    477    Landroid/nfc/FormatException;
        //  69     82     107    113    Any
        //  82     93     107    113    Any
        //  94     107    107    113    Any
        //  108    111    107    113    Any
        //  114    119    107    113    Any
        //  122    139    107    113    Any
        //  148    170    107    113    Any
        //  178    194    107    113    Any
        //  217    224    107    113    Any
        //  230    247    107    113    Any
        //  250    260    107    113    Any
        //  260    274    107    113    Any
        //  280    288    291    415    Landroid/nfc/FormatException;
        //  280    288    107    113    Any
        //  292    322    107    113    Any
        //  322    329    107    113    Any
        //  329    336    107    113    Any
        //  339    404    107    113    Any
        //  404    412    107    113    Any
        //  415    428    107    113    Any
        //  429    459    107    113    Any
        //  459    466    107    113    Any
        //  466    474    107    113    Any
        // 
        // The error that occurred was:
        // 
        // java.lang.IndexOutOfBoundsException: Index 244 out of bounds for length 244
        //     at java.base/jdk.internal.util.Preconditions.outOfBounds(Preconditions.java:100)
        //     at java.base/jdk.internal.util.Preconditions.outOfBoundsCheckIndex(Preconditions.java:106)
        //     at java.base/jdk.internal.util.Preconditions.checkIndex(Preconditions.java:302)
        //     at java.base/java.util.Objects.checkIndex(Objects.java:385)
        //     at java.base/java.util.ArrayList.get(ArrayList.java:427)
        //     at com.strobel.decompiler.ast.AstBuilder.convertToAst(AstBuilder.java:3362)
        //     at com.strobel.decompiler.ast.AstBuilder.build(AstBuilder.java:112)
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
    
    private static class ListenerHelper implements OnCardboardNfcListener
    {
        private Handler handler;
        private OnCardboardNfcListener listener;
        
        public ListenerHelper(final OnCardboardNfcListener listener, final Handler handler) {
            this.listener = listener;
            this.handler = handler;
        }
        
        public OnCardboardNfcListener getListener() {
            return this.listener;
        }
        
        @Override
        public void onInsertedIntoGvrViewer(final GvrViewerParams gvrViewerParams) {
            this.handler.post((Runnable)new Runnable() {
                @Override
                public void run() {
                    ListenerHelper.this.listener.onInsertedIntoGvrViewer(gvrViewerParams);
                }
            });
        }
        
        @Override
        public void onRemovedFromGvrViewer() {
            this.handler.post((Runnable)new Runnable() {
                @Override
                public void run() {
                    ListenerHelper.this.listener.onRemovedFromGvrViewer();
                }
            });
        }
    }
    
    public interface OnCardboardNfcListener
    {
        void onInsertedIntoGvrViewer(final GvrViewerParams p0);
        
        void onRemovedFromGvrViewer();
    }
}
