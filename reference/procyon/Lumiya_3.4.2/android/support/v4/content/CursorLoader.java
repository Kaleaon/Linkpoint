// 
// Decompiled by Procyon v0.6.0
// 

package android.support.v4.content;

import java.util.Arrays;
import java.io.PrintWriter;
import java.io.FileDescriptor;
import android.content.Context;
import android.net.Uri;
import android.support.v4.os.CancellationSignal;
import android.database.Cursor;

public class CursorLoader extends AsyncTaskLoader<Cursor>
{
    CancellationSignal mCancellationSignal;
    Cursor mCursor;
    final ForceLoadContentObserver mObserver;
    String[] mProjection;
    String mSelection;
    String[] mSelectionArgs;
    String mSortOrder;
    Uri mUri;
    
    public CursorLoader(final Context context) {
        super(context);
        this.mObserver = new ForceLoadContentObserver();
    }
    
    public CursorLoader(final Context context, final Uri mUri, final String[] mProjection, final String mSelection, final String[] mSelectionArgs, final String mSortOrder) {
        super(context);
        this.mObserver = new ForceLoadContentObserver();
        this.mUri = mUri;
        this.mProjection = mProjection;
        this.mSelection = mSelection;
        this.mSelectionArgs = mSelectionArgs;
        this.mSortOrder = mSortOrder;
    }
    
    @Override
    public void cancelLoadInBackground() {
        super.cancelLoadInBackground();
        synchronized (this) {
            if (this.mCancellationSignal != null) {
                this.mCancellationSignal.cancel();
            }
        }
    }
    
    @Override
    public void deliverResult(final Cursor mCursor) {
        if (!this.isReset()) {
            final Cursor mCursor2 = this.mCursor;
            this.mCursor = mCursor;
            if (this.isStarted()) {
                super.deliverResult(mCursor);
            }
            if (mCursor2 != null && mCursor2 != mCursor && !mCursor2.isClosed()) {
                mCursor2.close();
            }
            return;
        }
        if (mCursor != null) {
            mCursor.close();
        }
    }
    
    @Override
    public void dump(final String s, final FileDescriptor fileDescriptor, final PrintWriter printWriter, final String[] array) {
        super.dump(s, fileDescriptor, printWriter, array);
        printWriter.print(s);
        printWriter.print("mUri=");
        printWriter.println(this.mUri);
        printWriter.print(s);
        printWriter.print("mProjection=");
        printWriter.println(Arrays.toString(this.mProjection));
        printWriter.print(s);
        printWriter.print("mSelection=");
        printWriter.println(this.mSelection);
        printWriter.print(s);
        printWriter.print("mSelectionArgs=");
        printWriter.println(Arrays.toString(this.mSelectionArgs));
        printWriter.print(s);
        printWriter.print("mSortOrder=");
        printWriter.println(this.mSortOrder);
        printWriter.print(s);
        printWriter.print("mCursor=");
        printWriter.println(this.mCursor);
        printWriter.print(s);
        printWriter.print("mContentChanged=");
        printWriter.println(this.mContentChanged);
    }
    
    public String[] getProjection() {
        return this.mProjection;
    }
    
    public String getSelection() {
        return this.mSelection;
    }
    
    public String[] getSelectionArgs() {
        return this.mSelectionArgs;
    }
    
    public String getSortOrder() {
        return this.mSortOrder;
    }
    
    public Uri getUri() {
        return this.mUri;
    }
    
    @Override
    public Cursor loadInBackground() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     1: monitorenter   
        //     2: aload_0        
        //     3: invokevirtual   android/support/v4/content/CursorLoader.isLoadInBackgroundCanceled:()Z
        //     6: ifne            74
        //     9: new             Landroid/support/v4/os/CancellationSignal;
        //    12: astore_1       
        //    13: aload_1        
        //    14: invokespecial   android/support/v4/os/CancellationSignal.<init>:()V
        //    17: aload_0        
        //    18: aload_1        
        //    19: putfield        android/support/v4/content/CursorLoader.mCancellationSignal:Landroid/support/v4/os/CancellationSignal;
        //    22: aload_0        
        //    23: monitorexit    
        //    24: aload_0        
        //    25: invokevirtual   android/support/v4/content/CursorLoader.getContext:()Landroid/content/Context;
        //    28: invokevirtual   android/content/Context.getContentResolver:()Landroid/content/ContentResolver;
        //    31: aload_0        
        //    32: getfield        android/support/v4/content/CursorLoader.mUri:Landroid/net/Uri;
        //    35: aload_0        
        //    36: getfield        android/support/v4/content/CursorLoader.mProjection:[Ljava/lang/String;
        //    39: aload_0        
        //    40: getfield        android/support/v4/content/CursorLoader.mSelection:Ljava/lang/String;
        //    43: aload_0        
        //    44: getfield        android/support/v4/content/CursorLoader.mSelectionArgs:[Ljava/lang/String;
        //    47: aload_0        
        //    48: getfield        android/support/v4/content/CursorLoader.mSortOrder:Ljava/lang/String;
        //    51: aload_0        
        //    52: getfield        android/support/v4/content/CursorLoader.mCancellationSignal:Landroid/support/v4/os/CancellationSignal;
        //    55: invokestatic    android/support/v4/content/ContentResolverCompat.query:(Landroid/content/ContentResolver;Landroid/net/Uri;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;Landroid/support/v4/os/CancellationSignal;)Landroid/database/Cursor;
        //    58: astore_2       
        //    59: aload_2        
        //    60: ifnonnull       89
        //    63: aload_0        
        //    64: monitorenter   
        //    65: aload_0        
        //    66: aconst_null    
        //    67: putfield        android/support/v4/content/CursorLoader.mCancellationSignal:Landroid/support/v4/os/CancellationSignal;
        //    70: aload_0        
        //    71: monitorexit    
        //    72: aload_2        
        //    73: areturn        
        //    74: new             Landroid/support/v4/os/OperationCanceledException;
        //    77: astore_1       
        //    78: aload_1        
        //    79: invokespecial   android/support/v4/os/OperationCanceledException.<init>:()V
        //    82: aload_1        
        //    83: athrow         
        //    84: astore_1       
        //    85: aload_0        
        //    86: monitorexit    
        //    87: aload_1        
        //    88: athrow         
        //    89: aload_2        
        //    90: invokeinterface android/database/Cursor.getCount:()I
        //    95: pop            
        //    96: aload_2        
        //    97: aload_0        
        //    98: getfield        android/support/v4/content/CursorLoader.mObserver:Landroid/support/v4/content/Loader$ForceLoadContentObserver;
        //   101: invokeinterface android/database/Cursor.registerContentObserver:(Landroid/database/ContentObserver;)V
        //   106: goto            63
        //   109: astore_1       
        //   110: aload_2        
        //   111: invokeinterface android/database/Cursor.close:()V
        //   116: aload_1        
        //   117: athrow         
        //   118: astore_1       
        //   119: aload_0        
        //   120: monitorenter   
        //   121: aload_0        
        //   122: aconst_null    
        //   123: putfield        android/support/v4/content/CursorLoader.mCancellationSignal:Landroid/support/v4/os/CancellationSignal;
        //   126: aload_0        
        //   127: monitorexit    
        //   128: aload_1        
        //   129: athrow         
        //   130: astore_1       
        //   131: aload_0        
        //   132: monitorexit    
        //   133: aload_1        
        //   134: athrow         
        //   135: astore_1       
        //   136: aload_0        
        //   137: monitorexit    
        //   138: aload_1        
        //   139: athrow         
        //    Exceptions:
        //  Try           Handler
        //  Start  End    Start  End    Type                        
        //  -----  -----  -----  -----  ----------------------------
        //  2      24     84     89     Any
        //  24     59     118    140    Any
        //  65     72     130    135    Any
        //  74     84     84     89     Any
        //  85     87     84     89     Any
        //  89     106    109    118    Ljava/lang/RuntimeException;
        //  89     106    118    140    Any
        //  110    118    118    140    Any
        //  121    128    135    140    Any
        //  131    133    130    135    Any
        //  136    138    135    140    Any
        // 
        // The error that occurred was:
        // 
        // java.lang.IndexOutOfBoundsException: Index 86 out of bounds for length 86
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
    
    @Override
    public void onCanceled(final Cursor cursor) {
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
    }
    
    @Override
    protected void onReset() {
        super.onReset();
        this.onStopLoading();
        if (this.mCursor != null && !this.mCursor.isClosed()) {
            this.mCursor.close();
        }
        this.mCursor = null;
    }
    
    @Override
    protected void onStartLoading() {
        if (this.mCursor != null) {
            this.deliverResult(this.mCursor);
        }
        if (this.takeContentChanged() || this.mCursor == null) {
            this.forceLoad();
        }
    }
    
    @Override
    protected void onStopLoading() {
        this.cancelLoad();
    }
    
    public void setProjection(final String[] mProjection) {
        this.mProjection = mProjection;
    }
    
    public void setSelection(final String mSelection) {
        this.mSelection = mSelection;
    }
    
    public void setSelectionArgs(final String[] mSelectionArgs) {
        this.mSelectionArgs = mSelectionArgs;
    }
    
    public void setSortOrder(final String mSortOrder) {
        this.mSortOrder = mSortOrder;
    }
    
    public void setUri(final Uri mUri) {
        this.mUri = mUri;
    }
}
