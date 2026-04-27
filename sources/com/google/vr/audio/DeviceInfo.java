package com.google.vr.audio;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import com.google.vr.cardboard.annotations.UsedByNative;

@UsedByNative
public class DeviceInfo {
    private static final String TAG = "DeviceInfo";
    private final Context context;
    private final BroadcastReceiver headphoneStateReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("android.intent.action.HEADSET_PLUG")) {
                switch (intent.getIntExtra("state", -1)) {
                    case 0:
                        DeviceInfo.this.nativeUpdateHeadphoneStateChange(DeviceInfo.this.nativeObject, 2);
                        return;
                    case 1:
                        DeviceInfo.this.nativeUpdateHeadphoneStateChange(DeviceInfo.this.nativeObject, 1);
                        return;
                    default:
                        DeviceInfo.this.nativeUpdateHeadphoneStateChange(DeviceInfo.this.nativeObject, 0);
                        return;
                }
            }
        }
    };
    /* access modifiers changed from: private */
    public final long nativeObject;

    private static abstract class HeadphoneState {
        public static final int PLUGGEDIN = 1;
        public static final int UNKNOWN = 0;
        public static final int UNPLUGGED = 2;

        private HeadphoneState() {
        }
    }

    private DeviceInfo(long j, Context context2) {
        this.nativeObject = j;
        this.context = context2;
    }

    @UsedByNative
    private static DeviceInfo createDeviceInfo(long j, Context context2) {
        return new DeviceInfo(j, context2);
    }

    @UsedByNative
    private boolean isHeadphonePluggedIn() {
        return ((AudioManager) this.context.getSystemService("audio")).isWiredHeadsetOn();
    }

    /* access modifiers changed from: private */
    public native void nativeUpdateHeadphoneStateChange(long j, int i);

    @UsedByNative
    private void registerHandlers() {
        this.context.registerReceiver(this.headphoneStateReceiver, new IntentFilter("android.intent.action.HEADSET_PLUG"));
    }

    @UsedByNative
    private void unregisterHandlers() {
        this.context.unregisterReceiver(this.headphoneStateReceiver);
    }
}
