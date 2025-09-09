package com.lumiyaviewer.lumiya.media;

import android.content.Context;
import android.media.AudioManager;
import android.os.Handler;
import com.lumiyaviewer.lumiya.Debug;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class AudioManagerWrapper implements InvocationHandler {
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

    public AudioManagerWrapper(Context context) {
        Class cls;
        this.audioManager = (AudioManager) context.getSystemService("audio");
        try {
            Class[] declaredClasses = this.audioManager.getClass().getDeclaredClasses();
            int length = declaredClasses.length;
            int i = 0;
            while (true) {
                if (i >= length) {
                    cls = null;
                    break;
                }
                cls = declaredClasses[i];
                if (cls.getSimpleName().equals("OnAudioFocusChangeListener")) {
                    break;
                }
                i++;
            }
            if (cls == null) {
                throw new Exception("Failed to get OnAudioFocusChangeListener interface");
            }
            mRequestAudioFocus = AudioManager.class.getMethod("requestAudioFocus", new Class[]{cls, Integer.TYPE, Integer.TYPE});
            mAbandonAudioFocus = AudioManager.class.getMethod("abandonAudioFocus", new Class[]{cls});
            this.audioFocusHandler = Proxy.newProxyInstance(cls.getClassLoader(), new Class[]{cls}, this);
            this.hasAudioFocusAPI = true;
            Debug.Log("AudioManagerWrapper: has audio focus api = " + this.hasAudioFocusAPI);
        } catch (Exception e) {
            this.hasAudioFocusAPI = false;
            Debug.Log("AudioManagerWrapper: audio focus api not found");
            e.printStackTrace();
        }
    }

    private void onAudioFocusChange(int i) {
        if (this.mHandler != null) {
            this.mHandler.sendMessage(this.mHandler.obtainMessage(this.msgCode, i, 0));
        }
    }

    public void abandonAudioFocus() {
        Debug.Log("AudioManagerWrapper: abandoning audio focus");
        if (this.hasAudioFocusAPI) {
            try {
                mAbandonAudioFocus.invoke(this.audioManager, new Object[]{this.audioFocusHandler});
            } catch (Exception e) {
            }
        }
    }

    public Object invoke(Object obj, Method method, Object[] objArr) throws Throwable {
        try {
            if (method.getName().equalsIgnoreCase("onAudioFocusChange") && objArr.length >= 1 && (objArr[0] instanceof Integer)) {
                onAudioFocusChange(objArr[0].intValue());
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    public boolean requestAudioFocus() {
        Debug.Log("AudioManagerWrapper: requesting audio focus");
        if (!this.hasAudioFocusAPI) {
            return true;
        }
        try {
            return ((Integer) mRequestAudioFocus.invoke(this.audioManager, new Object[]{this.audioFocusHandler, 3, 1})).intValue() == 1;
        } catch (Exception e) {
            return true;
        }
    }

    public void setHandler(Handler handler, int i) {
        this.mHandler = handler;
        this.msgCode = i;
    }
}
