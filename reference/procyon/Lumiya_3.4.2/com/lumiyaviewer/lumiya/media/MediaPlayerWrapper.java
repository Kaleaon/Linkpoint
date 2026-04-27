// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.media;

import com.lumiyaviewer.lumiya.Debug;
import android.media.MediaPlayer;
import android.media.MediaPlayer$OnPreparedListener;
import android.media.MediaPlayer$OnInfoListener;
import android.media.MediaPlayer$OnErrorListener;

public class MediaPlayerWrapper implements Runnable, MediaPlayer$OnErrorListener, MediaPlayer$OnInfoListener, MediaPlayer$OnPreparedListener
{
    private MediaPlayer mediaPlayer;
    private volatile String mediaURL;
    private volatile boolean mustExit;
    private volatile boolean mustPlay;
    private Thread workingThread;
    
    public MediaPlayerWrapper() {
        this.mediaPlayer = null;
        this.workingThread = null;
        this.mustPlay = false;
        this.mustExit = false;
        this.mediaURL = "";
    }
    
    public boolean onError(final MediaPlayer mediaPlayer, final int i, final int j) {
        Debug.Log("MediaPlayerWrapper: onError: what = " + i + ", extra = " + j);
        return false;
    }
    
    public boolean onInfo(final MediaPlayer mediaPlayer, final int i, final int j) {
        Debug.Log("MediaPlayerWrapper: onInfo: what = " + i + ", extra = " + j);
        return false;
    }
    
    public void onPrepared(final MediaPlayer mediaPlayer) {
        Debug.Log("MediaPlayerWrapper: prepared, starting playback");
        mediaPlayer.start();
    }
    
    public void play(String mediaURL) {
        synchronized (this) {
            if (this.mustExit) {
                return;
            }
            this.mustPlay = true;
            this.mustExit = false;
            final String trim = mediaURL.trim();
            if (trim.toLowerCase().startsWith("http://")) {
                mediaURL = "http://" + trim.substring(7);
            }
            else {
                mediaURL = trim;
                if (trim.toLowerCase().startsWith("https://")) {
                    mediaURL = "https://" + trim.substring(8);
                }
            }
            this.mediaURL = mediaURL;
            if (this.workingThread == null) {
                (this.workingThread = new Thread(this)).start();
            }
            this.notify();
        }
    }
    
    public void release() {
        synchronized (this) {
            this.mustPlay = false;
            this.mustExit = true;
            this.mediaURL = "";
            this.workingThread = null;
            this.notify();
        }
    }
    
    @Override
    public void run() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     2: invokestatic    com/lumiyaviewer/lumiya/Debug.Log:(Ljava/lang/String;)V
        //     5: aload_0        
        //     6: getfield        com/lumiyaviewer/lumiya/media/MediaPlayerWrapper.mustExit:Z
        //     9: ifne            257
        //    12: aload_0        
        //    13: getfield        com/lumiyaviewer/lumiya/media/MediaPlayerWrapper.mustPlay:Z
        //    16: ifeq            223
        //    19: new             Ljava/lang/StringBuilder;
        //    22: dup            
        //    23: invokespecial   java/lang/StringBuilder.<init>:()V
        //    26: ldc             "MediaPlayerWrapper: working thread must play, URL = "
        //    28: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //    31: aload_0        
        //    32: getfield        com/lumiyaviewer/lumiya/media/MediaPlayerWrapper.mediaURL:Ljava/lang/String;
        //    35: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //    38: invokevirtual   java/lang/StringBuilder.toString:()Ljava/lang/String;
        //    41: invokestatic    com/lumiyaviewer/lumiya/Debug.Log:(Ljava/lang/String;)V
        //    44: aload_0        
        //    45: getfield        com/lumiyaviewer/lumiya/media/MediaPlayerWrapper.mediaPlayer:Landroid/media/MediaPlayer;
        //    48: ifnull          63
        //    51: aload_0        
        //    52: getfield        com/lumiyaviewer/lumiya/media/MediaPlayerWrapper.mediaPlayer:Landroid/media/MediaPlayer;
        //    55: invokevirtual   android/media/MediaPlayer.release:()V
        //    58: aload_0        
        //    59: aconst_null    
        //    60: putfield        com/lumiyaviewer/lumiya/media/MediaPlayerWrapper.mediaPlayer:Landroid/media/MediaPlayer;
        //    63: aload_0        
        //    64: new             Landroid/media/MediaPlayer;
        //    67: dup            
        //    68: invokespecial   android/media/MediaPlayer.<init>:()V
        //    71: putfield        com/lumiyaviewer/lumiya/media/MediaPlayerWrapper.mediaPlayer:Landroid/media/MediaPlayer;
        //    74: aload_0        
        //    75: getfield        com/lumiyaviewer/lumiya/media/MediaPlayerWrapper.mediaPlayer:Landroid/media/MediaPlayer;
        //    78: iconst_3       
        //    79: invokevirtual   android/media/MediaPlayer.setAudioStreamType:(I)V
        //    82: aload_0        
        //    83: getfield        com/lumiyaviewer/lumiya/media/MediaPlayerWrapper.mediaPlayer:Landroid/media/MediaPlayer;
        //    86: aload_0        
        //    87: invokevirtual   android/media/MediaPlayer.setOnErrorListener:(Landroid/media/MediaPlayer$OnErrorListener;)V
        //    90: aload_0        
        //    91: getfield        com/lumiyaviewer/lumiya/media/MediaPlayerWrapper.mediaPlayer:Landroid/media/MediaPlayer;
        //    94: aload_0        
        //    95: invokevirtual   android/media/MediaPlayer.setOnInfoListener:(Landroid/media/MediaPlayer$OnInfoListener;)V
        //    98: aload_0        
        //    99: getfield        com/lumiyaviewer/lumiya/media/MediaPlayerWrapper.mediaPlayer:Landroid/media/MediaPlayer;
        //   102: aload_0        
        //   103: invokevirtual   android/media/MediaPlayer.setOnPreparedListener:(Landroid/media/MediaPlayer$OnPreparedListener;)V
        //   106: aload_0        
        //   107: getfield        com/lumiyaviewer/lumiya/media/MediaPlayerWrapper.mediaURL:Ljava/lang/String;
        //   110: astore_1       
        //   111: aload_0        
        //   112: getfield        com/lumiyaviewer/lumiya/media/MediaPlayerWrapper.mediaPlayer:Landroid/media/MediaPlayer;
        //   115: aload_1        
        //   116: invokevirtual   android/media/MediaPlayer.setDataSource:(Ljava/lang/String;)V
        //   119: aload_0        
        //   120: getfield        com/lumiyaviewer/lumiya/media/MediaPlayerWrapper.mediaPlayer:Landroid/media/MediaPlayer;
        //   123: invokevirtual   android/media/MediaPlayer.prepareAsync:()V
        //   126: ldc             "MediaPlayerWrapper: working thread waiting"
        //   128: invokestatic    com/lumiyaviewer/lumiya/Debug.Log:(Ljava/lang/String;)V
        //   131: aload_0        
        //   132: monitorenter   
        //   133: aload_0        
        //   134: invokevirtual   com/lumiyaviewer/lumiya/media/MediaPlayerWrapper.wait:()V
        //   137: aload_0        
        //   138: monitorexit    
        //   139: ldc             "MediaPlayerWrapper: working thread wake up"
        //   141: invokestatic    com/lumiyaviewer/lumiya/Debug.Log:(Ljava/lang/String;)V
        //   144: goto            5
        //   147: astore_1       
        //   148: new             Ljava/lang/StringBuilder;
        //   151: dup            
        //   152: invokespecial   java/lang/StringBuilder.<init>:()V
        //   155: ldc             "MediaPlayerWrapper: Failed to set data source to "
        //   157: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   160: aload_0        
        //   161: getfield        com/lumiyaviewer/lumiya/media/MediaPlayerWrapper.mediaURL:Ljava/lang/String;
        //   164: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   167: invokevirtual   java/lang/StringBuilder.toString:()Ljava/lang/String;
        //   170: invokestatic    com/lumiyaviewer/lumiya/Debug.Log:(Ljava/lang/String;)V
        //   173: aload_1        
        //   174: invokevirtual   java/lang/Exception.printStackTrace:()V
        //   177: aload_0        
        //   178: iconst_0       
        //   179: putfield        com/lumiyaviewer/lumiya/media/MediaPlayerWrapper.mustPlay:Z
        //   182: goto            119
        //   185: astore_1       
        //   186: new             Ljava/lang/StringBuilder;
        //   189: dup            
        //   190: invokespecial   java/lang/StringBuilder.<init>:()V
        //   193: ldc             "MediaPlayerWrapper: PrepareAsync exception while playing "
        //   195: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   198: aload_0        
        //   199: getfield        com/lumiyaviewer/lumiya/media/MediaPlayerWrapper.mediaURL:Ljava/lang/String;
        //   202: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   205: invokevirtual   java/lang/StringBuilder.toString:()Ljava/lang/String;
        //   208: invokestatic    com/lumiyaviewer/lumiya/Debug.Log:(Ljava/lang/String;)V
        //   211: aload_1        
        //   212: invokevirtual   java/lang/Exception.printStackTrace:()V
        //   215: aload_0        
        //   216: iconst_0       
        //   217: putfield        com/lumiyaviewer/lumiya/media/MediaPlayerWrapper.mustPlay:Z
        //   220: goto            126
        //   223: ldc             "MediaPlayerWrapper: working thread must stop playing"
        //   225: invokestatic    com/lumiyaviewer/lumiya/Debug.Log:(Ljava/lang/String;)V
        //   228: aload_0        
        //   229: getfield        com/lumiyaviewer/lumiya/media/MediaPlayerWrapper.mediaPlayer:Landroid/media/MediaPlayer;
        //   232: ifnull          126
        //   235: aload_0        
        //   236: getfield        com/lumiyaviewer/lumiya/media/MediaPlayerWrapper.mediaPlayer:Landroid/media/MediaPlayer;
        //   239: invokevirtual   android/media/MediaPlayer.release:()V
        //   242: aload_0        
        //   243: aconst_null    
        //   244: putfield        com/lumiyaviewer/lumiya/media/MediaPlayerWrapper.mediaPlayer:Landroid/media/MediaPlayer;
        //   247: goto            126
        //   250: astore_1       
        //   251: aload_1        
        //   252: invokevirtual   java/lang/InterruptedException.printStackTrace:()V
        //   255: aload_0        
        //   256: monitorexit    
        //   257: ldc             "MediaPlayerWrapper: working thread exiting"
        //   259: invokestatic    com/lumiyaviewer/lumiya/Debug.Log:(Ljava/lang/String;)V
        //   262: aload_0        
        //   263: getfield        com/lumiyaviewer/lumiya/media/MediaPlayerWrapper.mediaPlayer:Landroid/media/MediaPlayer;
        //   266: ifnull          281
        //   269: aload_0        
        //   270: getfield        com/lumiyaviewer/lumiya/media/MediaPlayerWrapper.mediaPlayer:Landroid/media/MediaPlayer;
        //   273: invokevirtual   android/media/MediaPlayer.release:()V
        //   276: aload_0        
        //   277: aconst_null    
        //   278: putfield        com/lumiyaviewer/lumiya/media/MediaPlayerWrapper.mediaPlayer:Landroid/media/MediaPlayer;
        //   281: return         
        //   282: astore_1       
        //   283: aload_0        
        //   284: monitorexit    
        //   285: aload_1        
        //   286: athrow         
        //    Exceptions:
        //  Try           Handler
        //  Start  End    Start  End    Type                            
        //  -----  -----  -----  -----  --------------------------------
        //  111    119    147    185    Ljava/lang/Exception;
        //  119    126    185    223    Ljava/lang/Exception;
        //  133    137    250    257    Ljava/lang/InterruptedException;
        //  133    137    282    287    Any
        //  251    255    282    287    Any
        // 
        // The error that occurred was:
        // 
        // java.lang.IndexOutOfBoundsException: Index 137 out of bounds for length 137
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
    
    public void stop() {
        synchronized (this) {
            if (this.mustExit) {
                return;
            }
            this.mustPlay = false;
            this.mediaURL = "";
            this.notify();
        }
    }
}
