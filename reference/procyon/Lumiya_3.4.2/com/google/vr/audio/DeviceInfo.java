// 
// Decompiled by Procyon v0.6.0
// 

package com.google.vr.audio;

import android.content.IntentFilter;
import android.media.AudioManager;
import android.content.Intent;
import android.content.BroadcastReceiver;
import android.content.Context;
import com.google.vr.cardboard.annotations.UsedByNative;

@UsedByNative
public class DeviceInfo
{
    private static final String TAG = "DeviceInfo";
    private final Context context;
    private final BroadcastReceiver headphoneStateReceiver;
    private final long nativeObject;
    
    private DeviceInfo(final long nativeObject, final Context context) {
        this.nativeObject = nativeObject;
        this.context = context;
        this.headphoneStateReceiver = new BroadcastReceiver() {
            public void onReceive(final Context context, final Intent intent) {
                if (intent.getAction().equals("android.intent.action.HEADSET_PLUG")) {
                    switch (intent.getIntExtra("state", -1)) {
                        default: {
                            DeviceInfo.this.nativeUpdateHeadphoneStateChange(DeviceInfo.this.nativeObject, 0);
                            break;
                        }
                        case 0: {
                            DeviceInfo.this.nativeUpdateHeadphoneStateChange(DeviceInfo.this.nativeObject, 2);
                            return;
                        }
                        case 1: {
                            DeviceInfo.this.nativeUpdateHeadphoneStateChange(DeviceInfo.this.nativeObject, 1);
                        }
                    }
                }
            }
        };
    }
    
    @UsedByNative
    private static DeviceInfo createDeviceInfo(final long n, final Context context) {
        return new DeviceInfo(n, context);
    }
    
    @UsedByNative
    private boolean isHeadphonePluggedIn() {
        return ((AudioManager)this.context.getSystemService("audio")).isWiredHeadsetOn();
    }
    
    private native void nativeUpdateHeadphoneStateChange(final long p0, final int p1);
    
    @UsedByNative
    private void registerHandlers() {
        this.context.registerReceiver(this.headphoneStateReceiver, new IntentFilter("android.intent.action.HEADSET_PLUG"));
    }
    
    @UsedByNative
    private void unregisterHandlers() {
        this.context.unregisterReceiver(this.headphoneStateReceiver);
    }
    
    private abstract static class HeadphoneState
    {
        public static final int PLUGGEDIN = 1;
        public static final int UNKNOWN = 0;
        public static final int UNPLUGGED = 2;
    }
}
