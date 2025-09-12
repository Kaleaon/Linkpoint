// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.media;

import android.content.Context;
import android.media.AudioManager;
import android.os.Handler;
import com.lumiyaviewer.lumiya.Debug;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class AudioManagerWrapper
    implements InvocationHandler
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

    public AudioManagerWrapper(Context context)
    {
        audioManager = (AudioManager)context.getSystemService("audio");
        Class aclass[];
        int j;
        aclass = audioManager.getClass().getDeclaredClasses();
        j = aclass.length;
        int i = 0;
_L5:
        if (i >= j)
        {
            break MISSING_BLOCK_LABEL_194;
        }
        context = aclass[i];
        if (!context.getSimpleName().equals("OnAudioFocusChangeListener")) goto _L2; else goto _L1
_L1:
        if (context != null) goto _L4; else goto _L3
_L3:
        try
        {
            throw new Exception("Failed to get OnAudioFocusChangeListener interface");
        }
        // Misplaced declaration of an exception variable
        catch (Context context)
        {
            hasAudioFocusAPI = false;
        }
        Debug.Log("AudioManagerWrapper: audio focus api not found");
        context.printStackTrace();
_L6:
        Debug.Log((new StringBuilder()).append("AudioManagerWrapper: has audio focus api = ").append(hasAudioFocusAPI).toString());
        return;
_L2:
        i++;
          goto _L5
_L4:
        mRequestAudioFocus = android/media/AudioManager.getMethod("requestAudioFocus", new Class[] {
            context, Integer.TYPE, Integer.TYPE
        });
        mAbandonAudioFocus = android/media/AudioManager.getMethod("abandonAudioFocus", new Class[] {
            context
        });
        audioFocusHandler = Proxy.newProxyInstance(context.getClassLoader(), new Class[] {
            context
        }, this);
        hasAudioFocusAPI = true;
          goto _L6
        context = null;
          goto _L1
    }

    private void onAudioFocusChange(int i)
    {
        if (mHandler != null)
        {
            android.os.Message message = mHandler.obtainMessage(msgCode, i, 0);
            mHandler.sendMessage(message);
        }
    }

    public void abandonAudioFocus()
    {
        Debug.Log("AudioManagerWrapper: abandoning audio focus");
        if (!hasAudioFocusAPI)
        {
            break MISSING_BLOCK_LABEL_34;
        }
        mAbandonAudioFocus.invoke(audioManager, new Object[] {
            audioFocusHandler
        });
        return;
        Exception exception;
        exception;
    }

    public Object invoke(Object obj, Method method, Object aobj[])
        throws Throwable
    {
        try
        {
            if (method.getName().equalsIgnoreCase("onAudioFocusChange") && aobj.length >= 1 && (aobj[0] instanceof Integer))
            {
                onAudioFocusChange(((Integer)aobj[0]).intValue());
            }
        }
        // Misplaced declaration of an exception variable
        catch (Object obj)
        {
            return null;
        }
        return null;
    }

    public boolean requestAudioFocus()
    {
        Debug.Log("AudioManagerWrapper: requesting audio focus");
        if (hasAudioFocusAPI)
        {
            int i;
            try
            {
                i = ((Integer)mRequestAudioFocus.invoke(audioManager, new Object[] {
                    audioFocusHandler, Integer.valueOf(3), Integer.valueOf(1)
                })).intValue();
            }
            catch (Exception exception)
            {
                return true;
            }
            return i == 1;
        } else
        {
            return true;
        }
    }

    public void setHandler(Handler handler, int i)
    {
        mHandler = handler;
        msgCode = i;
    }
}
