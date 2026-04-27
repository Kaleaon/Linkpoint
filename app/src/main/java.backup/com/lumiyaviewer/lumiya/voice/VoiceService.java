/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  android.annotation.SuppressLint
 *  android.app.Service
 *  android.content.BroadcastReceiver
 *  android.content.Context
 *  android.content.Intent
 *  android.content.IntentFilter
 *  android.content.pm.PackageInfo
 *  android.content.pm.PackageManager$NameNotFoundException
 *  android.media.AudioManager
 *  android.os.Build$VERSION
 *  android.os.Handler
 *  android.os.IBinder
 *  android.os.Message
 *  android.os.Messenger
 *  android.os.Parcelable
 */
package com.lumiyaviewer.lumiya.voice;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.Parcelable;
import android.support.v4.content.ContextCompat;
import com.lumiyaviewer.lumiya.voice.AudioStreamVolumeObserver;
import com.lumiyaviewer.lumiya.voice.Debug;
import com.lumiyaviewer.lumiya.voice.VivoxController;
import com.lumiyaviewer.lumiya.voice.VoicePermissionRequestActivity;
import com.lumiyaviewer.lumiya.voice.common.VoicePluginMessageType;
import com.lumiyaviewer.lumiya.voice.common.VoicePluginMessenger;
import com.lumiyaviewer.lumiya.voice.common.messages.VoiceAcceptCall;
import com.lumiyaviewer.lumiya.voice.common.messages.VoiceAudioProperties;
import com.lumiyaviewer.lumiya.voice.common.messages.VoiceConnectChannel;
import com.lumiyaviewer.lumiya.voice.common.messages.VoiceEnableMic;
import com.lumiyaviewer.lumiya.voice.common.messages.VoiceInitialize;
import com.lumiyaviewer.lumiya.voice.common.messages.VoiceInitializeReply;
import com.lumiyaviewer.lumiya.voice.common.messages.VoiceLogin;
import com.lumiyaviewer.lumiya.voice.common.messages.VoiceRejectCall;
import com.lumiyaviewer.lumiya.voice.common.messages.VoiceSet3DPosition;
import com.lumiyaviewer.lumiya.voice.common.messages.VoiceSetAudioProperties;
import com.lumiyaviewer.lumiya.voice.common.messages.VoiceTerminateCall;
import com.lumiyaviewer.lumiya.voice.common.model.VoiceBluetoothState;
import java.util.concurrent.atomic.AtomicInteger;
import javax.annotation.Nullable;

public class VoiceService
extends Service {
    public static final int MESSAGE_PERMISSION_RESULTS = 300;
    private static final int REQUIRED_APP_VERSION = 60;
    public static final int STREAM_TYPE_BLUETOOTH = 6;
    private static VoiceService serviceInstance = null;
    private AudioManager audioManager = null;
    private AudioStreamVolumeObserver audioStreamVolumeObserver = null;
    private final AudioStreamVolumeObserver.OnAudioStreamVolumeChangedListener audioVolumeChangeListener;
    private boolean bluetoothReceiverRegistered = false;
    private final BroadcastReceiver bluetoothScoIntentReceiver;
    private final AtomicInteger bluetoothScoState;
    private boolean isServiceBound = false;
    private final Messenger mMessenger;
    private final Handler mainThreadHandler = new Handler();
    private Messenger toAppMessenger = null;
    private VivoxController vivoxController = null;

    public VoiceService() {
        this.bluetoothScoState = new AtomicInteger(-1);
        this.mMessenger = new Messenger((Handler)new IncomingHandler(this));
        this.bluetoothScoIntentReceiver = new BroadcastReceiver(this){
            final VoiceService this$0;
            {
                this.this$0 = voiceService;
            }

            public void onReceive(Context context, Intent intent) {
                this.this$0.handleBluetoothStateIntent(intent);
            }
        };
        this.audioVolumeChangeListener = new AudioStreamVolumeObserver.OnAudioStreamVolumeChangedListener(this){
            final VoiceService this$0;
            {
                this.this$0 = voiceService;
            }

            @Override
            public void onAudioStreamVolumeChanged(int n, int n2) {
                Debug.Printf("Voice: audio volume changed: %d", n2);
                this.this$0.updateAudioProperties();
            }
        };
    }

    static /* synthetic */ void access$000(VoiceService voiceService, VoiceInitialize voiceInitialize, Messenger messenger) {
        voiceService.onVoiceInitialize(voiceInitialize, messenger);
    }

    static /* synthetic */ void access$100(VoiceService voiceService, VoiceLogin voiceLogin, Messenger messenger) {
        voiceService.onVoiceLogin(voiceLogin, messenger);
    }

    static /* synthetic */ void access$1000(VoiceService voiceService, Messenger messenger, boolean bl) {
        voiceService.onVoicePermissionResults(messenger, bl);
    }

    static /* synthetic */ void access$200(VoiceService voiceService, VoiceConnectChannel voiceConnectChannel, Messenger messenger) {
        voiceService.onVoiceConnectChannel(voiceConnectChannel, messenger);
    }

    static /* synthetic */ void access$300(VoiceService voiceService, VoiceSet3DPosition voiceSet3DPosition, Messenger messenger) {
        voiceService.onVoiceSet3DPosition(voiceSet3DPosition, messenger);
    }

    static /* synthetic */ void access$400(VoiceService voiceService, VoiceRejectCall voiceRejectCall, Messenger messenger) {
        voiceService.onVoiceRejectCall(voiceRejectCall, messenger);
    }

    static /* synthetic */ void access$500(VoiceService voiceService, VoiceAcceptCall voiceAcceptCall, Messenger messenger) {
        voiceService.onVoiceAcceptCall(voiceAcceptCall, messenger);
    }

    static /* synthetic */ void access$600(VoiceService voiceService, VoiceTerminateCall voiceTerminateCall, Messenger messenger) {
        voiceService.onVoiceTerminateCall(voiceTerminateCall, messenger);
    }

    static /* synthetic */ void access$700(VoiceService voiceService, VoiceEnableMic voiceEnableMic) {
        voiceService.onVoiceEnableMic(voiceEnableMic);
    }

    static /* synthetic */ void access$800(VoiceService voiceService, Messenger messenger) {
        voiceService.onVoiceLogout(messenger);
    }

    static /* synthetic */ void access$900(VoiceService voiceService, VoiceSetAudioProperties voiceSetAudioProperties) {
        voiceService.onVoiceSetAudioProperties(voiceSetAudioProperties);
    }

    @Nullable
    static VoiceService getServiceInstance() {
        return serviceInstance;
    }

    private void handleBluetoothStateIntent(Intent intent) {
        int n = intent.getIntExtra("android.media.extra.SCO_AUDIO_STATE", 0);
        this.bluetoothScoState.set(n);
        if (this.vivoxController != null) {
            this.vivoxController.onBluetoothScoStateChanged(n);
        }
        this.updateAudioProperties();
    }

    private void onVoiceAcceptCall(VoiceAcceptCall voiceAcceptCall, Messenger messenger) {
        if (this.vivoxController != null) {
            this.vivoxController.AcceptCall(voiceAcceptCall);
        }
    }

    private void onVoiceConnectChannel(VoiceConnectChannel voiceConnectChannel, Messenger messenger) {
        if (this.vivoxController != null) {
            this.vivoxController.ConnectChannel(voiceConnectChannel.voiceChannelInfo, voiceConnectChannel.channelCredentials, messenger);
        }
    }

    private void onVoiceEnableMic(VoiceEnableMic voiceEnableMic) {
        if (this.vivoxController != null) {
            this.vivoxController.EnableVoiceMic(voiceEnableMic);
        }
    }

    /*
     * WARNING - Removed back jump from a try to a catch block - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private void onVoiceInitialize(VoiceInitialize object, Messenger messenger) {
        try {
            PackageInfo packageInfo = this.getPackageManager().getPackageInfo(this.getPackageName(), 0);
            if (((VoiceInitialize)object).appVersionCode < 60) {
                object = VoicePluginMessageType.VoiceInitializeReply;
                VoiceInitializeReply voiceInitializeReply = new VoiceInitializeReply(packageInfo.versionCode, this.getString(2131099681), false);
                VoicePluginMessenger.sendMessage(messenger, (VoicePluginMessageType)((Object)object), voiceInitializeReply, null);
                return;
            }
            if (ContextCompat.checkSelfPermission((Context)this, "android.permission.RECORD_AUDIO") != 0) {
                packageInfo = new Intent((Context)this, VoicePermissionRequestActivity.class);
                packageInfo.addFlags(0x50000000);
                packageInfo.putExtra("voiceInitMessage", ((VoiceInitialize)object).toBundle());
                packageInfo.putExtra("voiceInitReplyTo", (Parcelable)messenger);
                this.startActivity((Intent)packageInfo);
                return;
            }
        }
        catch (PackageManager.NameNotFoundException nameNotFoundException) {
            Debug.Warning(nameNotFoundException);
            return;
        }
        {
            this.onVoicePermissionResults(messenger, true);
            return;
        }
    }

    private void onVoiceLogin(VoiceLogin voiceLogin, Messenger messenger) {
        if (this.vivoxController != null) {
            this.vivoxController.Login(voiceLogin.voiceLoginInfo, messenger);
        }
    }

    private void onVoiceLogout(Messenger messenger) {
        if (this.vivoxController != null) {
            this.vivoxController.Logout(messenger);
        }
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private void onVoicePermissionResults(Messenger messenger, boolean bl) {
        try {
            String string2;
            PackageInfo packageInfo = this.getPackageManager().getPackageInfo(this.getPackageName(), 0);
            Object object = null;
            if (bl) {
                string2 = object;
                if (this.vivoxController == null) {
                    Debug.Printf("Voice: Creating new Vivox controller", new Object[0]);
                    try {
                        this.vivoxController = VivoxController.getInstance((Context)this, this.mainThreadHandler, this.mMessenger);
                        string2 = object;
                    }
                    catch (VivoxController.VivoxInitException vivoxInitException) {
                        Debug.Warning(vivoxInitException);
                        string2 = vivoxInitException.getMessage();
                    }
                }
            } else {
                string2 = this.getString(2131099683);
            }
            object = VoicePluginMessageType.VoiceInitializeReply;
            VoiceInitializeReply voiceInitializeReply = new VoiceInitializeReply(packageInfo.versionCode, string2, true);
            VoicePluginMessenger.sendMessage(messenger, (VoicePluginMessageType)((Object)object), voiceInitializeReply, null);
            if (string2 != null) return;
            this.toAppMessenger = messenger;
            this.registerForBluetoothScoIntentBroadcast();
            this.updateAudioProperties();
            return;
        }
        catch (PackageManager.NameNotFoundException nameNotFoundException) {
            Debug.Warning(nameNotFoundException);
            return;
        }
    }

    private void onVoiceRejectCall(VoiceRejectCall voiceRejectCall, Messenger messenger) {
        if (this.vivoxController != null) {
            this.vivoxController.RejectCall(voiceRejectCall);
        }
    }

    private void onVoiceSet3DPosition(VoiceSet3DPosition voiceSet3DPosition, Messenger messenger) {
        if (this.vivoxController != null) {
            this.vivoxController.Set3DPosition(voiceSet3DPosition);
        }
    }

    private void onVoiceSetAudioProperties(VoiceSetAudioProperties voiceSetAudioProperties) {
        if (this.vivoxController != null) {
            this.vivoxController.SetAudioProperties(voiceSetAudioProperties);
        }
    }

    private void onVoiceTerminateCall(VoiceTerminateCall voiceTerminateCall, Messenger messenger) {
        if (this.vivoxController != null) {
            this.vivoxController.TerminateCall(voiceTerminateCall);
        }
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private void registerForBluetoothScoIntentBroadcast() {
        if (Build.VERSION.SDK_INT < 14) return;
        if (this.bluetoothReceiverRegistered) return;
        try {
            this.bluetoothReceiverRegistered = true;
            IntentFilter intentFilter = new IntentFilter("android.media.ACTION_SCO_AUDIO_STATE_UPDATED");
            intentFilter = this.registerReceiver(this.bluetoothScoIntentReceiver, intentFilter);
            if (intentFilter == null) return;
            this.handleBluetoothStateIntent((Intent)intentFilter);
            return;
        }
        catch (Exception exception) {
            Debug.Warning(exception);
            return;
        }
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    void listenForVolumeChanges(boolean bl) {
        block4: {
            block3: {
                if (this.audioStreamVolumeObserver == null) break block3;
                if (!bl) break block4;
                AudioStreamVolumeObserver audioStreamVolumeObserver = this.audioStreamVolumeObserver;
                AudioStreamVolumeObserver.OnAudioStreamVolumeChangedListener onAudioStreamVolumeChangedListener = this.audioVolumeChangeListener;
                audioStreamVolumeObserver.start(new int[]{0, 6}, onAudioStreamVolumeChangedListener);
                this.updateAudioProperties();
            }
            return;
        }
        this.audioStreamVolumeObserver.stop();
    }

    public IBinder onBind(Intent intent) {
        this.isServiceBound = true;
        return this.mMessenger.getBinder();
    }

    public void onCreate() {
        super.onCreate();
        serviceInstance = this;
        this.audioManager = (AudioManager)this.getSystemService("audio");
        this.audioStreamVolumeObserver = new AudioStreamVolumeObserver((Context)this);
    }

    public void onDestroy() {
        if (this.vivoxController != null) {
            this.vivoxController.setIncomingMessenger(null);
        }
        if (this.bluetoothReceiverRegistered) {
            this.bluetoothReceiverRegistered = false;
            this.unregisterReceiver(this.bluetoothScoIntentReceiver);
        }
        if (this.audioStreamVolumeObserver != null) {
            this.audioStreamVolumeObserver.stop();
            this.audioStreamVolumeObserver = null;
        }
        this.toAppMessenger = null;
        this.audioManager = null;
        serviceInstance = null;
        super.onDestroy();
    }

    public int onStartCommand(Intent intent, int n, int n2) {
        return 2;
    }

    public boolean onUnbind(Intent intent) {
        Debug.Printf("Voice: service is unbound", new Object[0]);
        this.isServiceBound = false;
        if (this.vivoxController != null) {
            this.vivoxController.Logout(null);
        }
        return super.onUnbind(intent);
    }

    /*
     * Enabled aggressive block sorting
     */
    public void setVolume(float f) {
        if (this.audioManager != null) {
            int n = this.audioManager.isBluetoothScoOn() ? 6 : 0;
            int n2 = Math.round((float)this.audioManager.getStreamMaxVolume(n) * f);
            this.audioManager.setStreamVolume(n, n2, 0);
        }
    }

    /*
     * Enabled aggressive block sorting
     */
    void updateAudioProperties() {
        Messenger messenger;
        if (this.vivoxController != null && this.audioManager != null && (messenger = this.toAppMessenger) != null) {
            VoiceBluetoothState voiceBluetoothState;
            boolean bl = this.audioManager.isBluetoothScoOn();
            int n = bl ? 6 : 0;
            int n2 = this.audioManager.getStreamVolume(n);
            n = this.audioManager.getStreamMaxVolume(n);
            float f = (float)n2 / (float)n;
            boolean bl2 = this.audioManager.isSpeakerphoneOn();
            if (bl) {
                voiceBluetoothState = VoiceBluetoothState.Active;
            } else {
                switch (this.bluetoothScoState.get()) {
                    default: {
                        voiceBluetoothState = VoiceBluetoothState.Error;
                        break;
                    }
                    case 1: {
                        voiceBluetoothState = VoiceBluetoothState.Connected;
                        break;
                    }
                    case 2: {
                        voiceBluetoothState = VoiceBluetoothState.Connecting;
                        break;
                    }
                    case 0: {
                        voiceBluetoothState = VoiceBluetoothState.Disconnected;
                    }
                }
            }
            VoicePluginMessenger.sendMessage(messenger, VoicePluginMessageType.VoiceAudioProperties, new VoiceAudioProperties(f, bl2, voiceBluetoothState), null);
        }
    }

    @SuppressLint(value={"HandlerLeak"})
    private class IncomingHandler
    extends Handler {
        final VoiceService this$0;

        private IncomingHandler(VoiceService voiceService) {
            this.this$0 = voiceService;
        }

        /*
         * Exception decompiling
         */
        public void handleMessage(Message var1_1) {
            /*
             * This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
             * 
             * org.benf.cfr.reader.util.ConfusedCFRException: Back jump on a try block [egrp 1[TRYBLOCK] [2 : 181->469)] java.lang.Exception
             *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op02WithProcessedDataAndRefs.insertExceptionBlocks(Op02WithProcessedDataAndRefs.java:2283)
             *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:415)
             *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisOrWrapFail(CodeAnalyser.java:278)
             *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysis(CodeAnalyser.java:201)
             *     at org.benf.cfr.reader.entities.attributes.AttributeCode.analyse(AttributeCode.java:94)
             *     at org.benf.cfr.reader.entities.Method.analyse(Method.java:531)
             *     at org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:1055)
             *     at org.benf.cfr.reader.entities.ClassFile.analyseInnerClassesPass1(ClassFile.java:923)
             *     at org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:1035)
             *     at org.benf.cfr.reader.entities.ClassFile.analyseTop(ClassFile.java:942)
             *     at org.benf.cfr.reader.Driver.doJarVersionTypes(Driver.java:257)
             *     at org.benf.cfr.reader.Driver.doJar(Driver.java:139)
             *     at org.benf.cfr.reader.CfrDriverImpl.analyse(CfrDriverImpl.java:76)
             *     at org.benf.cfr.reader.Main.main(Main.java:54)
             */
            throw new IllegalStateException("Decompilation failed");
        }
    }
}

