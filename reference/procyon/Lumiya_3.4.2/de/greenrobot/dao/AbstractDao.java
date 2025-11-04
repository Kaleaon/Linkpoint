// 
// Decompiled by Procyon v0.6.0
// 

package de.greenrobot.dao;

import java.util.Collection;
import de.greenrobot.dao.query.Query;
import de.greenrobot.dao.query.QueryBuilder;
import android.database.CursorWindow;
import de.greenrobot.dao.internal.FastCursor;
import android.database.CrossProcessCursor;
import android.database.Cursor;
import java.util.Arrays;
import android.database.DatabaseUtils;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import android.database.sqlite.SQLiteStatement;
import de.greenrobot.dao.internal.TableStatements;
import de.greenrobot.dao.identityscope.IdentityScopeLong;
import de.greenrobot.dao.identityscope.IdentityScope;
import android.database.sqlite.SQLiteDatabase;
import de.greenrobot.dao.internal.DaoConfig;

public abstract class AbstractDao<T, K>
{
    protected final DaoConfig config;
    protected final SQLiteDatabase db;
    protected IdentityScope<K, T> identityScope;
    protected IdentityScopeLong<T> identityScopeLong;
    protected final int pkOrdinal;
    protected final AbstractDaoSession session;
    protected TableStatements statements;
    
    public AbstractDao(final DaoConfig daoConfig) {
        this(daoConfig, null);
    }
    
    public AbstractDao(final DaoConfig config, final AbstractDaoSession session) {
        this.config = config;
        this.session = session;
        this.db = config.db;
        this.identityScope = (IdentityScope<K, T>)config.getIdentityScope();
        if (this.identityScope instanceof IdentityScopeLong) {
            this.identityScopeLong = (IdentityScopeLong)this.identityScope;
        }
        this.statements = config.statements;
        int ordinal;
        if (config.pkProperty == null) {
            ordinal = -1;
        }
        else {
            ordinal = config.pkProperty.ordinal;
        }
        this.pkOrdinal = ordinal;
    }
    
    private void deleteByKeyInsideSynchronized(final K k, final SQLiteStatement sqLiteStatement) {
        if (!(k instanceof Long)) {
            if (k == null) {
                throw new DaoException("Cannot delete entity, key is null");
            }
            sqLiteStatement.bindString(1, k.toString());
        }
        else {
            sqLiteStatement.bindLong(1, (long)k);
        }
        sqLiteStatement.execute();
    }
    
    private void deleteInTxInternal(final Iterable<T> iterable, Iterable<K> next) {
        while (true) {
            List list = null;
            this.assertSinglePk();
            final SQLiteStatement deleteStatement = this.statements.getDeleteStatement();
            this.db.beginTransaction();
        Label_0047_Outer:
            while (true) {
                final Iterable iterable2;
                Iterator iterator;
                K keyVerified;
                Iterator<K> iterator2;
                Label_0165_Outer:Label_0174_Outer:Block_13_Outer:
                while (true) {
                    try {
                        synchronized (deleteStatement) {
                            if (this.identityScope != null) {
                                this.identityScope.lock();
                                list = new ArrayList();
                            }
                            if (iterable != null) {
                                break Label_0047_Outer;
                            }
                            if (next != null) {
                                break Label_0165_Outer;
                            }
                            Label_0040: {
                                if (this.identityScope != null) {
                                    break Label_0165_Outer;
                                }
                            }
                            monitorexit(deleteStatement);
                            this.db.setTransactionSuccessful();
                            if (list == null) {
                                return;
                            }
                            break Label_0047_Outer;
                        }
                    }
                    finally {
                        this.db.endTransaction();
                    }
                    try {
                        iterator = iterable2.iterator();
                        while (iterator.hasNext()) {
                            keyVerified = this.getKeyVerified(iterator.next());
                            this.deleteByKeyInsideSynchronized(keyVerified, deleteStatement);
                            if (list != null) {
                                list.add(keyVerified);
                            }
                        }
                        continue Label_0047_Outer;
                    }
                    finally {
                        while (true) {
                            if (this.identityScope == null) {
                                break Label_0165;
                            }
                            this.identityScope.unlock();
                            break Label_0165;
                            continue Label_0174_Outer;
                        }
                        this.identityScope.unlock();
                        continue Label_0165_Outer;
                        while (true) {
                        Block_12:
                            while (true) {
                                iftrue(Label_0040:)(!iterator2.hasNext());
                                break Block_12;
                                iterator2 = ((Iterable<K>)next).iterator();
                                continue Block_13_Outer;
                                list.add(next);
                                continue Block_13_Outer;
                            }
                            next = (K)iterator2.next();
                            this.deleteByKeyInsideSynchronized(next, deleteStatement);
                            iftrue(Label_0174:)(list == null);
                            continue;
                        }
                    }
                    break;
                }
                break;
            }
            if (this.identityScope != null) {
                this.identityScope.remove(list);
            }
        }
    }
    
    private long executeInsert(final T t, final SQLiteStatement sqLiteStatement) {
        Label_0055: {
            if (this.db.isDbLockedByCurrentThread()) {
                break Label_0055;
            }
            this.db.beginTransaction();
            try {
                synchronized (sqLiteStatement) {
                    this.bindValues(sqLiteStatement, t);
                    long n = sqLiteStatement.executeInsert();
                    monitorexit(sqLiteStatement);
                    this.db.setTransactionSuccessful();
                    this.db.endTransaction();
                    this.updateKeyAfterInsertAndAttach(t, n, true);
                    return n;
                    synchronized (sqLiteStatement) {
                        this.bindValues(sqLiteStatement, t);
                        n = sqLiteStatement.executeInsert();
                    }
                }
            }
            finally {
                this.db.endTransaction();
            }
        }
    }
    
    private void executeInsertInTx(final SQLiteStatement p0, final Iterable<T> p1, final boolean p2) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     1: getfield        de/greenrobot/dao/AbstractDao.db:Landroid/database/sqlite/SQLiteDatabase;
        //     4: invokevirtual   android/database/sqlite/SQLiteDatabase.beginTransaction:()V
        //     7: aload_1        
        //     8: monitorenter   
        //     9: aload_0        
        //    10: getfield        de/greenrobot/dao/AbstractDao.identityScope:Lde/greenrobot/dao/identityscope/IdentityScope;
        //    13: astore          4
        //    15: aload           4
        //    17: ifnonnull       66
        //    20: aload_2        
        //    21: invokeinterface java/lang/Iterable.iterator:()Ljava/util/Iterator;
        //    26: astore          4
        //    28: aload           4
        //    30: invokeinterface java/util/Iterator.hasNext:()Z
        //    35: istore          5
        //    37: iload           5
        //    39: ifne            93
        //    42: aload_0        
        //    43: getfield        de/greenrobot/dao/AbstractDao.identityScope:Lde/greenrobot/dao/identityscope/IdentityScope;
        //    46: ifnonnull       141
        //    49: aload_1        
        //    50: monitorexit    
        //    51: aload_0        
        //    52: getfield        de/greenrobot/dao/AbstractDao.db:Landroid/database/sqlite/SQLiteDatabase;
        //    55: invokevirtual   android/database/sqlite/SQLiteDatabase.setTransactionSuccessful:()V
        //    58: aload_0        
        //    59: getfield        de/greenrobot/dao/AbstractDao.db:Landroid/database/sqlite/SQLiteDatabase;
        //    62: invokevirtual   android/database/sqlite/SQLiteDatabase.endTransaction:()V
        //    65: return         
        //    66: aload_0        
        //    67: getfield        de/greenrobot/dao/AbstractDao.identityScope:Lde/greenrobot/dao/identityscope/IdentityScope;
        //    70: invokeinterface de/greenrobot/dao/identityscope/IdentityScope.lock:()V
        //    75: goto            20
        //    78: astore_2       
        //    79: aload_1        
        //    80: monitorexit    
        //    81: aload_2        
        //    82: athrow         
        //    83: astore_1       
        //    84: aload_0        
        //    85: getfield        de/greenrobot/dao/AbstractDao.db:Landroid/database/sqlite/SQLiteDatabase;
        //    88: invokevirtual   android/database/sqlite/SQLiteDatabase.endTransaction:()V
        //    91: aload_1        
        //    92: athrow         
        //    93: aload           4
        //    95: invokeinterface java/util/Iterator.next:()Ljava/lang/Object;
        //   100: astore_2       
        //   101: aload_0        
        //   102: aload_1        
        //   103: aload_2        
        //   104: invokevirtual   de/greenrobot/dao/AbstractDao.bindValues:(Landroid/database/sqlite/SQLiteStatement;Ljava/lang/Object;)V
        //   107: iload_3        
        //   108: ifne            128
        //   111: aload_1        
        //   112: invokevirtual   android/database/sqlite/SQLiteStatement.execute:()V
        //   115: goto            28
        //   118: astore_2       
        //   119: aload_0        
        //   120: getfield        de/greenrobot/dao/AbstractDao.identityScope:Lde/greenrobot/dao/identityscope/IdentityScope;
        //   123: ifnonnull       153
        //   126: aload_2        
        //   127: athrow         
        //   128: aload_0        
        //   129: aload_2        
        //   130: aload_1        
        //   131: invokevirtual   android/database/sqlite/SQLiteStatement.executeInsert:()J
        //   134: iconst_0       
        //   135: invokevirtual   de/greenrobot/dao/AbstractDao.updateKeyAfterInsertAndAttach:(Ljava/lang/Object;JZ)V
        //   138: goto            28
        //   141: aload_0        
        //   142: getfield        de/greenrobot/dao/AbstractDao.identityScope:Lde/greenrobot/dao/identityscope/IdentityScope;
        //   145: invokeinterface de/greenrobot/dao/identityscope/IdentityScope.unlock:()V
        //   150: goto            49
        //   153: aload_0        
        //   154: getfield        de/greenrobot/dao/AbstractDao.identityScope:Lde/greenrobot/dao/identityscope/IdentityScope;
        //   157: invokeinterface de/greenrobot/dao/identityscope/IdentityScope.unlock:()V
        //   162: goto            126
        //    Signature:
        //  (Landroid/database/sqlite/SQLiteStatement;Ljava/lang/Iterable<TT;>;Z)V
        //    Exceptions:
        //  Try           Handler
        //  Start  End    Start  End    Type
        //  -----  -----  -----  -----  ----
        //  7      9      83     93     Any
        //  9      15     78     83     Any
        //  20     28     118    165    Any
        //  28     37     118    165    Any
        //  42     49     78     83     Any
        //  49     51     78     83     Any
        //  51     58     83     93     Any
        //  66     75     78     83     Any
        //  79     81     78     83     Any
        //  81     83     83     93     Any
        //  93     107    118    165    Any
        //  111    115    118    165    Any
        //  119    126    78     83     Any
        //  126    128    78     83     Any
        //  128    138    118    165    Any
        //  141    150    78     83     Any
        //  153    162    78     83     Any
        // 
        // The error that occurred was:
        // 
        // java.lang.IllegalStateException: Expression is linked from several locations: Label_0020:
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
    
    protected void assertSinglePk() {
        if (this.config.pkColumns.length == 1) {
            return;
        }
        throw new DaoException(this + " (" + this.config.tablename + ") does not have a single-column primary key");
    }
    
    protected void attachEntity(final T t) {
    }
    
    protected final void attachEntity(final K k, final T t, final boolean b) {
        this.attachEntity(t);
        if (this.identityScope != null && k != null) {
            if (!b) {
                this.identityScope.putNoLock(k, t);
            }
            else {
                this.identityScope.put(k, t);
            }
        }
    }
    
    protected abstract void bindValues(final SQLiteStatement p0, final T p1);
    
    public long count() {
        return DatabaseUtils.queryNumEntries(this.db, '\'' + this.config.tablename + '\'');
    }
    
    public void delete(final T t) {
        this.assertSinglePk();
        this.deleteByKey(this.getKeyVerified(t));
    }
    
    public void deleteAll() {
        this.db.execSQL("DELETE FROM '" + this.config.tablename + "'");
        if (this.identityScope != null) {
            this.identityScope.clear();
        }
    }
    
    public void deleteByKey(final K k) {
        this.assertSinglePk();
        final SQLiteStatement deleteStatement = this.statements.getDeleteStatement();
        Label_0061: {
            if (this.db.isDbLockedByCurrentThread()) {
                break Label_0061;
            }
            while (true) {
                this.db.beginTransaction();
                Label_0094: {
                    try {
                        synchronized (deleteStatement) {
                            this.deleteByKeyInsideSynchronized(k, deleteStatement);
                            monitorexit(deleteStatement);
                            this.db.setTransactionSuccessful();
                            this.db.endTransaction();
                            if (this.identityScope == null) {
                                return;
                            }
                            break Label_0094;
                            synchronized (deleteStatement) {
                                this.deleteByKeyInsideSynchronized(k, deleteStatement);
                            }
                        }
                    }
                    finally {
                        this.db.endTransaction();
                    }
                }
                final K i;
                this.identityScope.remove(i);
            }
        }
    }
    
    public void deleteByKeyInTx(final Iterable<K> iterable) {
        this.deleteInTxInternal(null, iterable);
    }
    
    public void deleteByKeyInTx(final K... a) {
        this.deleteInTxInternal(null, Arrays.asList(a));
    }
    
    public void deleteInTx(final Iterable<T> iterable) {
        this.deleteInTxInternal(iterable, null);
    }
    
    public void deleteInTx(final T... a) {
        this.deleteInTxInternal(Arrays.asList(a), null);
    }
    
    public boolean detach(final T t) {
        return this.identityScope != null && this.identityScope.detach(this.getKeyVerified(t), t);
    }
    
    public String[] getAllColumns() {
        return this.config.allColumns;
    }
    
    public SQLiteDatabase getDatabase() {
        return this.db;
    }
    
    protected abstract K getKey(final T p0);
    
    protected K getKeyVerified(final T t) {
        final K key = this.getKey(t);
        if (key != null) {
            return key;
        }
        if (t != null) {
            throw new DaoException("Entity has no key");
        }
        throw new NullPointerException("Entity may not be null");
    }
    
    public String[] getNonPkColumns() {
        return this.config.nonPkColumns;
    }
    
    public String[] getPkColumns() {
        return this.config.pkColumns;
    }
    
    public Property getPkProperty() {
        return this.config.pkProperty;
    }
    
    public Property[] getProperties() {
        return this.config.properties;
    }
    
    public AbstractDaoSession getSession() {
        return this.session;
    }
    
    TableStatements getStatements() {
        return this.config.statements;
    }
    
    public String getTablename() {
        return this.config.tablename;
    }
    
    public long insert(final T t) {
        return this.executeInsert(t, this.statements.getInsertStatement());
    }
    
    public void insertInTx(final Iterable<T> iterable) {
        this.insertInTx(iterable, this.isEntityUpdateable());
    }
    
    public void insertInTx(final Iterable<T> iterable, final boolean b) {
        this.executeInsertInTx(this.statements.getInsertStatement(), iterable, b);
    }
    
    public void insertInTx(final T... a) {
        this.insertInTx(Arrays.asList(a), this.isEntityUpdateable());
    }
    
    public long insertOrReplace(final T t) {
        return this.executeInsert(t, this.statements.getInsertOrReplaceStatement());
    }
    
    public void insertOrReplaceInTx(final Iterable<T> iterable) {
        this.insertOrReplaceInTx(iterable, this.isEntityUpdateable());
    }
    
    public void insertOrReplaceInTx(final Iterable<T> iterable, final boolean b) {
        this.executeInsertInTx(this.statements.getInsertOrReplaceStatement(), iterable, b);
    }
    
    public void insertOrReplaceInTx(final T... a) {
        this.insertOrReplaceInTx(Arrays.asList(a), this.isEntityUpdateable());
    }
    
    public long insertWithoutSettingPk(final T t) {
        final SQLiteStatement insertStatement = this.statements.getInsertStatement();
        Label_0056: {
            if (this.db.isDbLockedByCurrentThread()) {
                break Label_0056;
            }
            this.db.beginTransaction();
            try {
                synchronized (insertStatement) {
                    this.bindValues(insertStatement, t);
                    long n = insertStatement.executeInsert();
                    monitorexit(insertStatement);
                    this.db.setTransactionSuccessful();
                    return n;
                    synchronized (insertStatement) {
                        this.bindValues(insertStatement, t);
                        n = insertStatement.executeInsert();
                    }
                }
            }
            finally {
                this.db.endTransaction();
            }
        }
    }
    
    protected abstract boolean isEntityUpdateable();
    
    public T load(final K k) {
        this.assertSinglePk();
        if (k != null) {
            if (this.identityScope != null) {
                final T value = this.identityScope.get(k);
                if (value != null) {
                    return value;
                }
            }
            return this.loadUniqueAndCloseCursor(this.db.rawQuery(this.statements.getSelectByKey(), new String[] { k.toString() }));
        }
        return null;
    }
    
    public List<T> loadAll() {
        return this.loadAllAndCloseCursor(this.db.rawQuery(this.statements.getSelectAll(), (String[])null));
    }
    
    protected List<T> loadAllAndCloseCursor(final Cursor cursor) {
        try {
            return this.loadAllFromCursor(cursor);
        }
        finally {
            cursor.close();
        }
    }
    
    protected List<T> loadAllFromCursor(final Cursor cursor) {
        final int count = cursor.getCount();
        final ArrayList list = new ArrayList(count);
        Object o;
        if (!(cursor instanceof CrossProcessCursor)) {
            o = cursor;
        }
        else {
            final CursorWindow window = ((CrossProcessCursor)cursor).getWindow();
            o = cursor;
            if (window != null) {
                if (window.getNumRows() != count) {
                    DaoLog.d("Window vs. result size: " + window.getNumRows() + "/" + count);
                    o = cursor;
                }
                else {
                    o = new FastCursor(window);
                }
            }
        }
        if (((Cursor)o).moveToFirst()) {
            Label_0179: {
                if (this.identityScope != null) {
                    break Label_0179;
                }
                try {
                    while (true) {
                        do {
                            list.add((Object)this.loadCurrent((Cursor)o, 0, false));
                        } while (((Cursor)o).moveToNext());
                        if (this.identityScope != null) {
                            this.identityScope.unlock();
                            return (List<T>)list;
                        }
                        return (List<T>)list;
                        this.identityScope.lock();
                        this.identityScope.reserveRoom(count);
                        continue;
                    }
                }
                finally {
                    while (true) {
                        if (this.identityScope == null) {
                            break Label_0209;
                        }
                        this.identityScope.unlock();
                        break Label_0209;
                        continue;
                    }
                }
            }
        }
        return (List<T>)list;
    }
    
    public T loadByRowId(final long i) {
        return this.loadUniqueAndCloseCursor(this.db.rawQuery(this.statements.getSelectByRowId(), new String[] { Long.toString(i) }));
    }
    
    protected final T loadCurrent(final Cursor cursor, final int n, final boolean b) {
        if (this.identityScopeLong == null) {
            if (this.identityScope == null) {
                if (n != 0 && this.readKey(cursor, n) == null) {
                    return null;
                }
                final T entity = this.readEntity(cursor, n);
                this.attachEntity(entity);
                return entity;
            }
            else {
                final K key = this.readKey(cursor, n);
                if (n != 0 && key == null) {
                    return null;
                }
                T t;
                if (!b) {
                    t = this.identityScope.getNoLock(key);
                }
                else {
                    t = this.identityScope.get(key);
                }
                if (t == null) {
                    final T entity2 = this.readEntity(cursor, n);
                    this.attachEntity(key, entity2, b);
                    return entity2;
                }
                return t;
            }
        }
        else {
            if (n != 0 && cursor.isNull(this.pkOrdinal + n)) {
                return null;
            }
            final long long1 = cursor.getLong(this.pkOrdinal + n);
            T t2;
            if (!b) {
                t2 = this.identityScopeLong.get2NoLock(long1);
            }
            else {
                t2 = this.identityScopeLong.get2(long1);
            }
            if (t2 == null) {
                final T entity3 = this.readEntity(cursor, n);
                this.attachEntity(entity3);
                if (!b) {
                    this.identityScopeLong.put2NoLock(long1, entity3);
                }
                else {
                    this.identityScopeLong.put2(long1, entity3);
                }
                return entity3;
            }
            return t2;
        }
    }
    
    protected final <O> O loadCurrentOther(final AbstractDao<O, ?> abstractDao, final Cursor cursor, final int n) {
        return abstractDao.loadCurrent(cursor, n, true);
    }
    
    protected T loadUnique(final Cursor cursor) {
        if (!cursor.moveToFirst()) {
            return null;
        }
        if (cursor.isLast()) {
            return this.loadCurrent(cursor, 0, true);
        }
        throw new DaoException("Expected unique result, but count was " + cursor.getCount());
    }
    
    protected T loadUniqueAndCloseCursor(final Cursor cursor) {
        try {
            return this.loadUnique(cursor);
        }
        finally {
            cursor.close();
        }
    }
    
    public QueryBuilder<T> queryBuilder() {
        return QueryBuilder.internalCreate((AbstractDao<T, ?>)this);
    }
    
    public List<T> queryRaw(final String str, final String... array) {
        return this.loadAllAndCloseCursor(this.db.rawQuery(this.statements.getSelectAll() + str, array));
    }
    
    public Query<T> queryRawCreate(final String s, final Object... a) {
        return this.queryRawCreateListArgs(s, Arrays.asList(a));
    }
    
    public Query<T> queryRawCreateListArgs(final String str, final Collection<Object> collection) {
        return Query.internalCreate((AbstractDao<T, ?>)this, this.statements.getSelectAll() + str, collection.toArray());
    }
    
    protected abstract T readEntity(final Cursor p0, final int p1);
    
    protected abstract void readEntity(final Cursor p0, final T p1, final int p2);
    
    protected abstract K readKey(final Cursor p0, final int p1);
    
    public void refresh(final T t) {
        this.assertSinglePk();
        final K keyVerified = this.getKeyVerified(t);
        final Cursor rawQuery = this.db.rawQuery(this.statements.getSelectByKey(), new String[] { keyVerified.toString() });
        try {
            if (!rawQuery.moveToFirst()) {
                throw new DaoException("Entity does not exist in the database anymore: " + t.getClass() + " with key " + keyVerified);
            }
            if (rawQuery.isLast()) {
                this.readEntity(rawQuery, t, 0);
                this.attachEntity(keyVerified, t, true);
                rawQuery.close();
                return;
            }
        }
        finally {
            rawQuery.close();
        }
        new DaoException("Expected unique result, but count was " + rawQuery.getCount());
    }
    
    public void update(final T t) {
        this.assertSinglePk();
        final SQLiteStatement updateStatement = this.statements.getUpdateStatement();
        Label_0055: {
            if (this.db.isDbLockedByCurrentThread()) {
                break Label_0055;
            }
            this.db.beginTransaction();
            try {
                synchronized (updateStatement) {
                    this.updateInsideSynchronized(t, updateStatement, true);
                    monitorexit(updateStatement);
                    this.db.setTransactionSuccessful();
                    return;
                    synchronized (updateStatement) {
                        this.updateInsideSynchronized(t, updateStatement, true);
                    }
                }
            }
            finally {
                this.db.endTransaction();
            }
        }
    }
    
    public void updateInTx(final Iterable<T> p0) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     1: getfield        de/greenrobot/dao/AbstractDao.statements:Lde/greenrobot/dao/internal/TableStatements;
        //     4: invokevirtual   de/greenrobot/dao/internal/TableStatements.getUpdateStatement:()Landroid/database/sqlite/SQLiteStatement;
        //     7: astore_2       
        //     8: aload_0        
        //     9: getfield        de/greenrobot/dao/AbstractDao.db:Landroid/database/sqlite/SQLiteDatabase;
        //    12: invokevirtual   android/database/sqlite/SQLiteDatabase.beginTransaction:()V
        //    15: aload_2        
        //    16: monitorenter   
        //    17: aload_0        
        //    18: getfield        de/greenrobot/dao/AbstractDao.identityScope:Lde/greenrobot/dao/identityscope/IdentityScope;
        //    21: astore_3       
        //    22: aload_3        
        //    23: ifnonnull       70
        //    26: aload_1        
        //    27: invokeinterface java/lang/Iterable.iterator:()Ljava/util/Iterator;
        //    32: astore_1       
        //    33: aload_1        
        //    34: invokeinterface java/util/Iterator.hasNext:()Z
        //    39: istore          4
        //    41: iload           4
        //    43: ifne            105
        //    46: aload_0        
        //    47: getfield        de/greenrobot/dao/AbstractDao.identityScope:Lde/greenrobot/dao/identityscope/IdentityScope;
        //    50: ifnonnull       140
        //    53: aload_2        
        //    54: monitorexit    
        //    55: aload_0        
        //    56: getfield        de/greenrobot/dao/AbstractDao.db:Landroid/database/sqlite/SQLiteDatabase;
        //    59: invokevirtual   android/database/sqlite/SQLiteDatabase.setTransactionSuccessful:()V
        //    62: aload_0        
        //    63: getfield        de/greenrobot/dao/AbstractDao.db:Landroid/database/sqlite/SQLiteDatabase;
        //    66: invokevirtual   android/database/sqlite/SQLiteDatabase.endTransaction:()V
        //    69: return         
        //    70: aload_0        
        //    71: getfield        de/greenrobot/dao/AbstractDao.identityScope:Lde/greenrobot/dao/identityscope/IdentityScope;
        //    74: invokeinterface de/greenrobot/dao/identityscope/IdentityScope.lock:()V
        //    79: goto            26
        //    82: astore_1       
        //    83: aload_2        
        //    84: monitorexit    
        //    85: aload_1        
        //    86: athrow         
        //    87: astore_2       
        //    88: aload_0        
        //    89: getfield        de/greenrobot/dao/AbstractDao.db:Landroid/database/sqlite/SQLiteDatabase;
        //    92: invokevirtual   android/database/sqlite/SQLiteDatabase.endTransaction:()V
        //    95: goto            69
        //    98: astore_1       
        //    99: aload_2        
        //   100: ifnonnull       181
        //   103: aload_1        
        //   104: athrow         
        //   105: aload_0        
        //   106: aload_1        
        //   107: invokeinterface java/util/Iterator.next:()Ljava/lang/Object;
        //   112: aload_2        
        //   113: iconst_0       
        //   114: invokevirtual   de/greenrobot/dao/AbstractDao.updateInsideSynchronized:(Ljava/lang/Object;Landroid/database/sqlite/SQLiteStatement;Z)V
        //   117: goto            33
        //   120: astore_1       
        //   121: aload_0        
        //   122: getfield        de/greenrobot/dao/AbstractDao.identityScope:Lde/greenrobot/dao/identityscope/IdentityScope;
        //   125: ifnonnull       152
        //   128: aload_1        
        //   129: athrow         
        //   130: astore_1       
        //   131: aload_0        
        //   132: getfield        de/greenrobot/dao/AbstractDao.db:Landroid/database/sqlite/SQLiteDatabase;
        //   135: invokevirtual   android/database/sqlite/SQLiteDatabase.endTransaction:()V
        //   138: aload_1        
        //   139: athrow         
        //   140: aload_0        
        //   141: getfield        de/greenrobot/dao/AbstractDao.identityScope:Lde/greenrobot/dao/identityscope/IdentityScope;
        //   144: invokeinterface de/greenrobot/dao/identityscope/IdentityScope.unlock:()V
        //   149: goto            53
        //   152: aload_0        
        //   153: getfield        de/greenrobot/dao/AbstractDao.identityScope:Lde/greenrobot/dao/identityscope/IdentityScope;
        //   156: invokeinterface de/greenrobot/dao/identityscope/IdentityScope.unlock:()V
        //   161: goto            128
        //   164: astore_1       
        //   165: iconst_0       
        //   166: ifne            171
        //   169: aload_1        
        //   170: athrow         
        //   171: ldc_w           "Could not end transaction (rethrowing initial exception)"
        //   174: aload_1        
        //   175: invokestatic    de/greenrobot/dao/DaoLog.w:(Ljava/lang/String;Ljava/lang/Throwable;)I
        //   178: pop            
        //   179: aconst_null    
        //   180: athrow         
        //   181: ldc_w           "Could not end transaction (rethrowing initial exception)"
        //   184: aload_1        
        //   185: invokestatic    de/greenrobot/dao/DaoLog.w:(Ljava/lang/String;Ljava/lang/Throwable;)I
        //   188: pop            
        //   189: aload_2        
        //   190: athrow         
        //   191: astore_1       
        //   192: iconst_0       
        //   193: ifne            198
        //   196: aload_1        
        //   197: athrow         
        //   198: ldc_w           "Could not end transaction (rethrowing initial exception)"
        //   201: aload_1        
        //   202: invokestatic    de/greenrobot/dao/DaoLog.w:(Ljava/lang/String;Ljava/lang/Throwable;)I
        //   205: pop            
        //   206: aconst_null    
        //   207: athrow         
        //    Signature:
        //  (Ljava/lang/Iterable<TT;>;)V
        //    Exceptions:
        //  Try           Handler
        //  Start  End    Start  End    Type                        
        //  -----  -----  -----  -----  ----------------------------
        //  15     17     87     105    Ljava/lang/RuntimeException;
        //  15     17     130    208    Any
        //  17     22     82     87     Any
        //  26     33     120    164    Any
        //  33     41     120    164    Any
        //  46     53     82     87     Any
        //  53     55     82     87     Any
        //  55     62     87     105    Ljava/lang/RuntimeException;
        //  55     62     130    208    Any
        //  62     69     164    181    Ljava/lang/RuntimeException;
        //  70     79     82     87     Any
        //  83     85     82     87     Any
        //  85     87     87     105    Ljava/lang/RuntimeException;
        //  85     87     130    208    Any
        //  88     95     98     105    Ljava/lang/RuntimeException;
        //  105    117    120    164    Any
        //  121    128    82     87     Any
        //  128    130    82     87     Any
        //  131    138    191    208    Ljava/lang/RuntimeException;
        //  140    149    82     87     Any
        //  152    161    82     87     Any
        // 
        // The error that occurred was:
        // 
        // java.lang.IndexOutOfBoundsException: Index 108 out of bounds for length 108
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
    
    public void updateInTx(final T... a) {
        this.updateInTx(Arrays.asList(a));
    }
    
    protected void updateInsideSynchronized(final T t, final SQLiteStatement sqLiteStatement, final boolean b) {
        this.bindValues(sqLiteStatement, t);
        final int n = this.config.allColumns.length + 1;
        final K key = this.getKey(t);
        if (!(key instanceof Long)) {
            if (key == null) {
                throw new DaoException("Cannot update entity without key - was it inserted before?");
            }
            sqLiteStatement.bindString(n, key.toString());
        }
        else {
            sqLiteStatement.bindLong(n, (long)key);
        }
        sqLiteStatement.execute();
        this.attachEntity(key, t, b);
    }
    
    protected abstract K updateKeyAfterInsert(final T p0, final long p1);
    
    protected void updateKeyAfterInsertAndAttach(final T t, final long n, final boolean b) {
        if (n != -1L) {
            this.attachEntity(this.updateKeyAfterInsert(t, n), t, b);
        }
        else {
            DaoLog.w("Could not insert row (executeInsert returned -1)");
        }
    }
}
