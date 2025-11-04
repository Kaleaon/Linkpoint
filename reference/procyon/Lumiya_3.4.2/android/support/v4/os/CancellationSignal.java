// 
// Decompiled by Procyon v0.6.0
// 

package android.support.v4.os;

import android.os.Build$VERSION;

public final class CancellationSignal
{
    private boolean mCancelInProgress;
    private Object mCancellationSignalObj;
    private boolean mIsCanceled;
    private OnCancelListener mOnCancelListener;
    
    private void waitForCancelFinishedLocked() {
        while (this.mCancelInProgress) {
            try {
                this.wait();
            }
            catch (final InterruptedException ex) {}
        }
    }
    
    public void cancel() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     1: monitorenter   
        //     2: aload_0        
        //     3: getfield        android/support/v4/os/CancellationSignal.mIsCanceled:Z
        //     6: ifne            53
        //     9: aload_0        
        //    10: iconst_1       
        //    11: putfield        android/support/v4/os/CancellationSignal.mIsCanceled:Z
        //    14: aload_0        
        //    15: iconst_1       
        //    16: putfield        android/support/v4/os/CancellationSignal.mCancelInProgress:Z
        //    19: aload_0        
        //    20: getfield        android/support/v4/os/CancellationSignal.mOnCancelListener:Landroid/support/v4/os/CancellationSignal$OnCancelListener;
        //    23: astore_1       
        //    24: aload_0        
        //    25: getfield        android/support/v4/os/CancellationSignal.mCancellationSignalObj:Ljava/lang/Object;
        //    28: astore_2       
        //    29: aload_0        
        //    30: monitorexit    
        //    31: aload_1        
        //    32: ifnonnull       61
        //    35: aload_2        
        //    36: ifnonnull       86
        //    39: aload_0        
        //    40: monitorenter   
        //    41: aload_0        
        //    42: iconst_0       
        //    43: putfield        android/support/v4/os/CancellationSignal.mCancelInProgress:Z
        //    46: aload_0        
        //    47: invokevirtual   java/lang/Object.notifyAll:()V
        //    50: aload_0        
        //    51: monitorexit    
        //    52: return         
        //    53: aload_0        
        //    54: monitorexit    
        //    55: return         
        //    56: astore_1       
        //    57: aload_0        
        //    58: monitorexit    
        //    59: aload_1        
        //    60: athrow         
        //    61: aload_1        
        //    62: invokeinterface android/support/v4/os/CancellationSignal$OnCancelListener.onCancel:()V
        //    67: goto            35
        //    70: astore_1       
        //    71: aload_0        
        //    72: monitorenter   
        //    73: aload_0        
        //    74: iconst_0       
        //    75: putfield        android/support/v4/os/CancellationSignal.mCancelInProgress:Z
        //    78: aload_0        
        //    79: invokevirtual   java/lang/Object.notifyAll:()V
        //    82: aload_0        
        //    83: monitorexit    
        //    84: aload_1        
        //    85: athrow         
        //    86: getstatic       android/os/Build$VERSION.SDK_INT:I
        //    89: bipush          16
        //    91: if_icmplt       39
        //    94: aload_2        
        //    95: checkcast       Landroid/os/CancellationSignal;
        //    98: invokevirtual   android/os/CancellationSignal.cancel:()V
        //   101: goto            39
        //   104: astore_1       
        //   105: aload_0        
        //   106: monitorexit    
        //   107: aload_1        
        //   108: athrow         
        //   109: astore_1       
        //   110: aload_0        
        //   111: monitorexit    
        //   112: aload_1        
        //   113: athrow         
        //    Exceptions:
        //  Try           Handler
        //  Start  End    Start  End    Type
        //  -----  -----  -----  -----  ----
        //  2      31     56     61     Any
        //  41     52     104    109    Any
        //  53     55     56     61     Any
        //  57     59     56     61     Any
        //  61     67     70     114    Any
        //  73     84     109    114    Any
        //  86     101    70     114    Any
        //  105    107    104    109    Any
        //  110    112    109    114    Any
        // 
        // The error that occurred was:
        // 
        // java.lang.IllegalStateException: Expression is linked from several locations: Label_0053:
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
    
    public Object getCancellationSignalObject() {
        Label_0026: {
            if (Build$VERSION.SDK_INT < 16) {
                break Label_0026;
            }
            synchronized (this) {
                if (this.mCancellationSignalObj == null) {
                    this.mCancellationSignalObj = new android.os.CancellationSignal();
                    if (this.mIsCanceled) {
                        ((android.os.CancellationSignal)this.mCancellationSignalObj).cancel();
                    }
                }
                return this.mCancellationSignalObj;
                return null;
            }
        }
    }
    
    public boolean isCanceled() {
        synchronized (this) {
            return this.mIsCanceled;
        }
    }
    
    public void setOnCancelListener(final OnCancelListener mOnCancelListener) {
        synchronized (this) {
            this.waitForCancelFinishedLocked();
            if (this.mOnCancelListener == mOnCancelListener) {
                return;
            }
            this.mOnCancelListener = mOnCancelListener;
            if (this.mIsCanceled && mOnCancelListener != null) {
                monitorexit(this);
                mOnCancelListener.onCancel();
            }
        }
    }
    
    public void throwIfCanceled() {
        if (!this.isCanceled()) {
            return;
        }
        throw new OperationCanceledException();
    }
    
    public interface OnCancelListener
    {
        void onCancel();
    }
}
