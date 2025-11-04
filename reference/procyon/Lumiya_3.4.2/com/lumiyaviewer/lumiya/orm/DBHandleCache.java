// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.orm;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import com.lumiyaviewer.lumiya.Debug;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.PhantomReference;
import java.util.Map;

public class DBHandleCache
{
    private final Map<String, DBOpenRef> fileMap;
    private final Map<PhantomReference<DBHandle>, DBOpenRef> refMap;
    private final ReferenceQueue<DBHandle> refQueue;
    
    private DBHandleCache() {
        this.refQueue = new ReferenceQueue<DBHandle>();
        this.refMap = new IdentityHashMap<PhantomReference<DBHandle>, DBOpenRef>();
        this.fileMap = new HashMap<String, DBOpenRef>();
        Debug.Printf("DBHandleCache: Initialized.", new Object[0]);
    }
    
    public static DBHandleCache getInstance() {
        return InstanceHolder.Instance;
    }
    
    public void Cleanup() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     1: monitorenter   
        //     2: aload_0        
        //     3: getfield        com/lumiyaviewer/lumiya/orm/DBHandleCache.refQueue:Ljava/lang/ref/ReferenceQueue;
        //     6: invokevirtual   java/lang/ref/ReferenceQueue.poll:()Ljava/lang/ref/Reference;
        //     9: astore_1       
        //    10: aload_1        
        //    11: ifnull          100
        //    14: aload_0        
        //    15: getfield        com/lumiyaviewer/lumiya/orm/DBHandleCache.refMap:Ljava/util/Map;
        //    18: aload_1        
        //    19: invokeinterface java/util/Map.remove:(Ljava/lang/Object;)Ljava/lang/Object;
        //    24: checkcast       Lcom/lumiyaviewer/lumiya/orm/DBHandleCache$DBOpenRef;
        //    27: astore_2       
        //    28: aload_2        
        //    29: ifnull          2
        //    32: aload_2        
        //    33: invokevirtual   com/lumiyaviewer/lumiya/orm/DBHandleCache$DBOpenRef.releaseReference:()I
        //    36: ifgt            2
        //    39: aload_2        
        //    40: invokevirtual   com/lumiyaviewer/lumiya/orm/DBHandleCache$DBOpenRef.getFileName:()Ljava/lang/String;
        //    43: astore_1       
        //    44: ldc             "DBHandle: Closing db '%s'"
        //    46: iconst_1       
        //    47: anewarray       Ljava/lang/Object;
        //    50: dup            
        //    51: iconst_0       
        //    52: aload_1        
        //    53: aastore        
        //    54: invokestatic    com/lumiyaviewer/lumiya/Debug.Printf:(Ljava/lang/String;[Ljava/lang/Object;)V
        //    57: aload_2        
        //    58: invokevirtual   com/lumiyaviewer/lumiya/orm/DBHandleCache$DBOpenRef.getDB:()Landroid/database/sqlite/SQLiteDatabase;
        //    61: astore_2       
        //    62: aload_2        
        //    63: invokevirtual   android/database/sqlite/SQLiteDatabase.isOpen:()Z
        //    66: ifeq            73
        //    69: aload_2        
        //    70: invokevirtual   android/database/sqlite/SQLiteDatabase.close:()V
        //    73: aload_0        
        //    74: getfield        com/lumiyaviewer/lumiya/orm/DBHandleCache.fileMap:Ljava/util/Map;
        //    77: aload_1        
        //    78: invokeinterface java/util/Map.remove:(Ljava/lang/Object;)Ljava/lang/Object;
        //    83: pop            
        //    84: goto            2
        //    87: astore_1       
        //    88: aload_0        
        //    89: monitorexit    
        //    90: aload_1        
        //    91: athrow         
        //    92: astore_2       
        //    93: aload_2        
        //    94: invokestatic    com/lumiyaviewer/lumiya/Debug.Warning:(Ljava/lang/Throwable;)V
        //    97: goto            73
        //   100: aload_0        
        //   101: monitorexit    
        //   102: return         
        //    Exceptions:
        //  Try           Handler
        //  Start  End    Start  End    Type                                     
        //  -----  -----  -----  -----  -----------------------------------------
        //  2      10     87     92     Any
        //  14     28     87     92     Any
        //  32     57     87     92     Any
        //  57     73     92     100    Landroid/database/sqlite/SQLiteException;
        //  57     73     87     92     Any
        //  73     84     87     92     Any
        //  93     97     87     92     Any
        // 
        // The error that occurred was:
        // 
        // java.lang.IllegalStateException: Expression is linked from several locations: Label_0073:
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
    
    public DBHandle OpenDB(final String s, final DBOpenHelper dbOpenHelper) throws SQLiteException {
        synchronized (this) {
            DBOpenRef dbOpenRef;
            if ((dbOpenRef = this.fileMap.get(s)) == null) {
                Debug.Printf("DBHandle: Opening db '%s'", s);
                dbOpenRef = new DBOpenRef(s, dbOpenHelper.openOrCreateDatabase(s));
                this.fileMap.put(s, dbOpenRef);
            }
            final DBHandle referent = new DBHandle(dbOpenRef.getDB());
            dbOpenRef.acquireReference();
            this.refMap.put(new PhantomReference<DBHandle>(referent, (ReferenceQueue<? super Object>)this.refQueue), dbOpenRef);
            return referent;
        }
    }
    
    public boolean hasOpenHandles() {
        synchronized (this) {
            final boolean b = this.fileMap.isEmpty() && this.refMap.isEmpty();
            monitorexit(this);
            return b ^ true;
        }
    }
    
    public interface DBOpenHelper
    {
        SQLiteDatabase openOrCreateDatabase(final String p0) throws SQLiteException;
    }
    
    private static class DBOpenRef
    {
        private final String fileName;
        private int handleCount;
        private final SQLiteDatabase sqliteDB;
        
        public DBOpenRef(final String fileName, final SQLiteDatabase sqliteDB) {
            this.fileName = fileName;
            this.sqliteDB = sqliteDB;
            this.handleCount = 0;
        }
        
        public final void acquireReference() {
            ++this.handleCount;
        }
        
        public final SQLiteDatabase getDB() {
            return this.sqliteDB;
        }
        
        public final String getFileName() {
            return this.fileName;
        }
        
        public final int releaseReference() {
            return --this.handleCount;
        }
    }
    
    private static class InstanceHolder
    {
        private static final DBHandleCache Instance;
        
        static {
            Instance = new DBHandleCache(null);
        }
    }
}
