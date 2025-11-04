// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.media;

import java.lang.reflect.Proxy;
import com.lumiyaviewer.lumiya.Debug;
import android.content.Context;
import android.os.Handler;
import android.media.AudioManager;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationHandler;

public class AudioManagerWrapper implements InvocationHandler
{
    public static final int AUDIOFOCUS_GAIN = 1;
    public static final int AUDIOFOCUS_GAIN_TRANSIENT = 2;
    public static final int AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK = 3;
    public static final int AUDIOFOCUS_LOSS = -1;
    public static final int AUDIOFOCUS_LOSS_TRANSIENT = -2;
    public static final int AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK = -3;
    public static final int AUDIOFOCUS_REQUEST_FAILED = 0;
    public static final int AUDIOFOCUS_REQUEST_GRANTED = 1;
    private static Method mAbandonAudioFocus;
    private static Method mRequestAudioFocus;
    private Object audioFocusHandler;
    private AudioManager audioManager;
    private boolean hasAudioFocusAPI;
    private Handler mHandler;
    private int msgCode;
    
    public AudioManagerWrapper(final Context context) {
        while (true) {
            Class clazz = null;
            Label_0120: {
            Label_0057_Outer:
                while (true) {
                    this.audioManager = (AudioManager)context.getSystemService("audio");
                    while (true) {
                    Label_0196:
                        while (true) {
                            int n = 0;
                            Label_0114: {
                                try {
                                    final Class[] declaredClasses = this.audioManager.getClass().getDeclaredClasses();
                                    final int length = declaredClasses.length;
                                    n = 0;
                                    if (n >= length) {
                                        break Label_0196;
                                    }
                                    clazz = declaredClasses[n];
                                    if (!clazz.getSimpleName().equals("OnAudioFocusChangeListener")) {
                                        break Label_0114;
                                    }
                                    if (clazz == null) {
                                        throw new Exception("Failed to get OnAudioFocusChangeListener interface");
                                    }
                                    break Label_0120;
                                }
                                catch (final Exception ex) {
                                    this.hasAudioFocusAPI = false;
                                    Debug.Log("AudioManagerWrapper: audio focus api not found");
                                    ex.printStackTrace();
                                }
                                break;
                            }
                            ++n;
                            continue Label_0057_Outer;
                        }
                        clazz = null;
                        continue;
                    }
                }
                Debug.Log("AudioManagerWrapper: has audio focus api = " + this.hasAudioFocusAPI);
                return;
            }
            AudioManagerWrapper.mRequestAudioFocus = AudioManager.class.getMethod("requestAudioFocus", clazz, Integer.TYPE, Integer.TYPE);
            AudioManagerWrapper.mAbandonAudioFocus = AudioManager.class.getMethod("abandonAudioFocus", clazz);
            this.audioFocusHandler = Proxy.newProxyInstance(clazz.getClassLoader(), new Class[] { clazz }, this);
            this.hasAudioFocusAPI = true;
            continue;
        }
    }
    
    private void onAudioFocusChange(final int n) {
        if (this.mHandler != null) {
            this.mHandler.sendMessage(this.mHandler.obtainMessage(this.msgCode, n, 0));
        }
    }
    
    public void abandonAudioFocus() {
        Debug.Log("AudioManagerWrapper: abandoning audio focus");
        if (!this.hasAudioFocusAPI) {
            return;
        }
        try {
            AudioManagerWrapper.mAbandonAudioFocus.invoke(this.audioManager, this.audioFocusHandler);
        }
        catch (final Exception ex) {}
    }
    
    @Override
    public Object invoke(final Object o, final Method method, final Object[] array) throws Throwable {
        try {
            if (method.getName().equalsIgnoreCase("onAudioFocusChange") && array.length >= 1 && array[0] instanceof Integer) {
                this.onAudioFocusChange((int)array[0]);
            }
            return null;
        }
        catch (final Exception ex) {
            return null;
        }
    }
    
    public boolean requestAudioFocus() {
        Debug.Log("AudioManagerWrapper: requesting audio focus");
        if (this.hasAudioFocusAPI) {
            try {
                return (int)AudioManagerWrapper.mRequestAudioFocus.invoke(this.audioManager, this.audioFocusHandler, 3, 1) == 1;
            }
            catch (final Exception ex) {
                return true;
            }
        }
        return true;
    }
    
    public void setHandler(final Handler mHandler, final int msgCode) {
        this.mHandler = mHandler;
        this.msgCode = msgCode;
    }
}
