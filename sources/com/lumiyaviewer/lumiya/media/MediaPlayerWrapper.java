// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.media;

import android.media.MediaPlayer;
import com.lumiyaviewer.lumiya.Debug;

public class MediaPlayerWrapper
    implements Runnable, android.media.MediaPlayer.OnErrorListener, android.media.MediaPlayer.OnInfoListener, android.media.MediaPlayer.OnPreparedListener
{

    private MediaPlayer mediaPlayer;
    private volatile String mediaURL;
    private volatile boolean mustExit;
    private volatile boolean mustPlay;
    private Thread workingThread;

    public MediaPlayerWrapper()
    {
        mediaPlayer = null;
        workingThread = null;
        mustPlay = false;
        mustExit = false;
        mediaURL = "";
    }

    public boolean onError(MediaPlayer mediaplayer, int i, int j)
    {
        Debug.Log((new StringBuilder()).append("MediaPlayerWrapper: onError: what = ").append(i).append(", extra = ").append(j).toString());
        return false;
    }

    public boolean onInfo(MediaPlayer mediaplayer, int i, int j)
    {
        Debug.Log((new StringBuilder()).append("MediaPlayerWrapper: onInfo: what = ").append(i).append(", extra = ").append(j).toString());
        return false;
    }

    public void onPrepared(MediaPlayer mediaplayer)
    {
        Debug.Log("MediaPlayerWrapper: prepared, starting playback");
        mediaplayer.start();
    }

    public void play(String s)
    {
        this;
        JVM INSTR monitorenter ;
        boolean flag = mustExit;
        if (!flag)
        {
            break MISSING_BLOCK_LABEL_14;
        }
        this;
        JVM INSTR monitorexit ;
        return;
        String s1;
        mustPlay = true;
        mustExit = false;
        s1 = s.trim();
        if (!s1.toLowerCase().startsWith("http://")) goto _L2; else goto _L1
_L1:
        s = (new StringBuilder()).append("http://").append(s1.substring(7)).toString();
_L4:
        mediaURL = s;
        if (workingThread == null)
        {
            workingThread = new Thread(this);
            workingThread.start();
        }
        notify();
        this;
        JVM INSTR monitorexit ;
        return;
_L2:
        s = s1;
        if (!s1.toLowerCase().startsWith("https://")) goto _L4; else goto _L3
_L3:
        s = (new StringBuilder()).append("https://").append(s1.substring(8)).toString();
          goto _L4
        s;
        throw s;
    }

    public void release()
    {
        this;
        JVM INSTR monitorenter ;
        mustPlay = false;
        mustExit = true;
        mediaURL = "";
        workingThread = null;
        notify();
        this;
        JVM INSTR monitorexit ;
        return;
        Exception exception;
        exception;
        throw exception;
    }

    public void run()
    {
        Debug.Log("MediaPlayerWrapper: working thread started");
_L8:
        if (mustExit) goto _L2; else goto _L1
_L1:
        if (!mustPlay) goto _L4; else goto _L3
_L3:
        Debug.Log((new StringBuilder()).append("MediaPlayerWrapper: working thread must play, URL = ").append(mediaURL).toString());
        if (mediaPlayer != null)
        {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(3);
        mediaPlayer.setOnErrorListener(this);
        mediaPlayer.setOnInfoListener(this);
        mediaPlayer.setOnPreparedListener(this);
        String s = mediaURL;
        try
        {
            mediaPlayer.setDataSource(s);
        }
        catch (Exception exception)
        {
            Debug.Log((new StringBuilder()).append("MediaPlayerWrapper: Failed to set data source to ").append(mediaURL).toString());
            exception.printStackTrace();
            mustPlay = false;
        }
        try
        {
            mediaPlayer.prepareAsync();
        }
        catch (Exception exception1)
        {
            Debug.Log((new StringBuilder()).append("MediaPlayerWrapper: PrepareAsync exception while playing ").append(mediaURL).toString());
            exception1.printStackTrace();
            mustPlay = false;
        }
_L6:
        Debug.Log("MediaPlayerWrapper: working thread waiting");
        this;
        JVM INSTR monitorenter ;
        wait();
        this;
        JVM INSTR monitorexit ;
        Debug.Log("MediaPlayerWrapper: working thread wake up");
        continue; /* Loop/switch isn't completed */
_L4:
        Debug.Log("MediaPlayerWrapper: working thread must stop playing");
        if (mediaPlayer != null)
        {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        if (true) goto _L6; else goto _L5
_L5:
        Object obj;
        obj;
        ((InterruptedException) (obj)).printStackTrace();
        this;
        JVM INSTR monitorexit ;
_L2:
        Debug.Log("MediaPlayerWrapper: working thread exiting");
        if (mediaPlayer != null)
        {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        return;
        obj;
        throw obj;
        if (true) goto _L8; else goto _L7
_L7:
    }

    public void stop()
    {
        this;
        JVM INSTR monitorenter ;
        boolean flag = mustExit;
        if (!flag)
        {
            break MISSING_BLOCK_LABEL_14;
        }
        this;
        JVM INSTR monitorexit ;
        return;
        mustPlay = false;
        mediaURL = "";
        notify();
        this;
        JVM INSTR monitorexit ;
        return;
        Exception exception;
        exception;
        throw exception;
    }
}
