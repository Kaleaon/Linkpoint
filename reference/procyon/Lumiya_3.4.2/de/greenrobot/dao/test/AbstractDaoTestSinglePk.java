// 
// Decompiled by Procyon v0.6.0
// 

package de.greenrobot.dao.test;

import java.util.List;
import android.database.SQLException;
import java.util.Iterator;
import java.util.ArrayList;
import android.database.DatabaseUtils;
import de.greenrobot.dao.internal.SqlUtils;
import android.database.Cursor;
import java.util.HashSet;
import java.util.Set;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.AbstractDao;

public abstract class AbstractDaoTestSinglePk<D extends AbstractDao<T, K>, T, K> extends AbstractDaoTest<D, T, K>
{
    private Property pkColumn;
    protected Set<K> usedPks;
    
    public AbstractDaoTestSinglePk(final Class<D> clazz) {
        super(clazz);
        this.usedPks = new HashSet<K>();
    }
    
    protected abstract T createEntity(final K p0);
    
    protected T createEntityWithRandomPk() {
        return this.createEntity(this.nextPk());
    }
    
    protected abstract K createRandomPk();
    
    protected K nextPk() {
        for (int i = 0; i < 100000; ++i) {
            final K randomPk = this.createRandomPk();
            if (this.usedPks.add(randomPk)) {
                return randomPk;
            }
        }
        throw new IllegalStateException("Could not find a new PK");
    }
    
    protected Cursor queryWithDummyColumnsInFront(final int n, final String str, final K k) {
        final int n2 = 0;
        final StringBuilder sb = new StringBuilder("SELECT ");
        for (int i = 0; i < n; ++i) {
            sb.append(str).append(",");
        }
        SqlUtils.appendColumns(sb, "T", this.dao.getAllColumns()).append(" FROM ");
        sb.append(this.dao.getTablename()).append(" T");
        if (k != null) {
            sb.append(" WHERE ");
            assertEquals(1, this.dao.getPkColumns().length);
            sb.append(this.dao.getPkColumns()[0]).append("=");
            DatabaseUtils.appendValueToSql(sb, (Object)k);
        }
        final Cursor rawQuery = this.db.rawQuery(sb.toString(), (String[])null);
        assertTrue(rawQuery.moveToFirst());
        int n3 = n2;
        while (true) {
            Label_0197: {
                if (n3 >= n) {
                    if (k == null) {
                        break;
                    }
                    break Label_0197;
                }
                try {
                    assertEquals(str, rawQuery.getString(n3));
                    ++n3;
                    continue;
                    assertEquals(1, rawQuery.getCount());
                    break;
                }
                catch (final RuntimeException ex) {
                    rawQuery.close();
                    throw ex;
                }
            }
        }
        return rawQuery;
    }
    
    protected void runLoadPkTest(final int n) {
        final K nextPk = this.nextPk();
        ((AbstractDao<T, K>)this.dao).insert(this.createEntity(nextPk));
        final Cursor queryWithDummyColumnsInFront = this.queryWithDummyColumnsInFront(n, "42", nextPk);
        try {
            assertEquals((Object)nextPk, (Object)this.daoAccess.readKey(queryWithDummyColumnsInFront, n));
        }
        finally {
            queryWithDummyColumnsInFront.close();
        }
    }
    
    @Override
    protected void setUp() throws Exception {
        int i = 0;
        super.setUp();
        for (Property[] properties = this.daoAccess.getProperties(); i < properties.length; ++i) {
            final Property pkColumn = properties[i];
            if (pkColumn.primaryKey) {
                if (this.pkColumn != null) {
                    throw new RuntimeException("Test does not work with multiple PK columns");
                }
                this.pkColumn = pkColumn;
            }
        }
        if (this.pkColumn != null) {
            return;
        }
        throw new RuntimeException("Test does not work without a PK column");
    }
    
    public void testCount() {
        this.dao.deleteAll();
        assertEquals(0L, this.dao.count());
        ((AbstractDao<T, K>)this.dao).insert(this.createEntityWithRandomPk());
        assertEquals(1L, this.dao.count());
        ((AbstractDao<T, K>)this.dao).insert(this.createEntityWithRandomPk());
        assertEquals(2L, this.dao.count());
    }
    
    public void testDelete() {
        final K nextPk = this.nextPk();
        ((AbstractDao<T, K>)this.dao).deleteByKey(nextPk);
        ((AbstractDao<T, K>)this.dao).insert(this.createEntity(nextPk));
        assertNotNull(((AbstractDao<Object, K>)this.dao).load(nextPk));
        ((AbstractDao<T, K>)this.dao).deleteByKey(nextPk);
        assertNull(((AbstractDao<Object, K>)this.dao).load(nextPk));
    }
    
    public void testDeleteAll() {
        int i = 0;
        final ArrayList list = new ArrayList();
        while (i < 10) {
            list.add(this.createEntityWithRandomPk());
            ++i;
        }
        ((AbstractDao<T, K>)this.dao).insertInTx(list);
        this.dao.deleteAll();
        assertEquals(0L, this.dao.count());
        final Iterator iterator = list.iterator();
        while (iterator.hasNext()) {
            final K key = this.daoAccess.getKey((T)iterator.next());
            assertNotNull((Object)key);
            assertNull(((AbstractDao<Object, K>)this.dao).load(key));
        }
    }
    
    public void testDeleteByKeyInTx() {
        final ArrayList list = new ArrayList();
        for (int i = 0; i < 10; ++i) {
            list.add(this.createEntityWithRandomPk());
        }
        ((AbstractDao<T, K>)this.dao).insertInTx(list);
        final ArrayList list2 = new ArrayList();
        list2.add(this.daoAccess.getKey((T)list.get(0)));
        list2.add(this.daoAccess.getKey((T)list.get(3)));
        list2.add(this.daoAccess.getKey((T)list.get(4)));
        list2.add(this.daoAccess.getKey((T)list.get(8)));
        ((AbstractDao<T, K>)this.dao).deleteByKeyInTx(list2);
        assertEquals((long)(list.size() - list2.size()), this.dao.count());
        for (final Object next : list2) {
            assertNotNull(next);
            assertNull(((AbstractDao<Object, K>)this.dao).load((K)next));
        }
    }
    
    public void testDeleteInTx() {
        final ArrayList list = new ArrayList();
        for (int i = 0; i < 10; ++i) {
            list.add(this.createEntityWithRandomPk());
        }
        ((AbstractDao<T, K>)this.dao).insertInTx(list);
        final ArrayList list2 = new ArrayList();
        list2.add(list.get(0));
        list2.add(list.get(3));
        list2.add(list.get(4));
        list2.add(list.get(8));
        ((AbstractDao<T, K>)this.dao).deleteInTx(list2);
        assertEquals((long)(list.size() - list2.size()), this.dao.count());
        final Iterator iterator = list2.iterator();
        while (iterator.hasNext()) {
            final K key = this.daoAccess.getKey((T)iterator.next());
            assertNotNull((Object)key);
            assertNull(((AbstractDao<Object, K>)this.dao).load(key));
        }
    }
    
    public void testInsertAndLoad() {
        final K nextPk = this.nextPk();
        final T entity = this.createEntity(nextPk);
        ((AbstractDao<T, K>)this.dao).insert(entity);
        assertEquals((Object)nextPk, (Object)this.daoAccess.getKey(entity));
        final Object load = ((AbstractDao<Object, K>)this.dao).load(nextPk);
        assertNotNull(load);
        assertEquals((Object)this.daoAccess.getKey(entity), (Object)this.daoAccess.getKey((T)load));
    }
    
    public void testInsertInTx() {
        this.dao.deleteAll();
        final ArrayList list = new ArrayList();
        for (int i = 0; i < 20; ++i) {
            list.add(this.createEntityWithRandomPk());
        }
        ((AbstractDao<T, K>)this.dao).insertInTx(list);
        assertEquals((long)list.size(), this.dao.count());
    }
    
    public void testInsertOrReplaceInTx() {
        int i = 0;
        this.dao.deleteAll();
        final ArrayList list = new ArrayList();
        final ArrayList list2 = new ArrayList();
        while (i < 20) {
            final Object entityWithRandomPk = this.createEntityWithRandomPk();
            if (i % 2 == 0) {
                list.add(entityWithRandomPk);
            }
            list2.add(entityWithRandomPk);
            ++i;
        }
        ((AbstractDao<T, K>)this.dao).insertOrReplaceInTx(list);
        ((AbstractDao<T, K>)this.dao).insertOrReplaceInTx(list2);
        assertEquals((long)list2.size(), this.dao.count());
    }
    
    public void testInsertOrReplaceTwice() {
        final T entityWithRandomPk = this.createEntityWithRandomPk();
        final long insert = ((AbstractDao<T, K>)this.dao).insert(entityWithRandomPk);
        final long insertOrReplace = ((AbstractDao<T, K>)this.dao).insertOrReplace(entityWithRandomPk);
        if (this.dao.getPkProperty().type == Long.class) {
            assertEquals(insert, insertOrReplace);
        }
    }
    
    public void testInsertTwice() {
        final T entity = this.createEntity(this.nextPk());
        ((AbstractDao<T, K>)this.dao).insert(entity);
        try {
            ((AbstractDao<T, K>)this.dao).insert(entity);
            fail("Inserting twice should not work");
        }
        catch (final SQLException ex) {}
    }
    
    public void testLoadAll() {
        this.dao.deleteAll();
        final ArrayList list = new ArrayList();
        for (int i = 0; i < 15; ++i) {
            list.add(this.createEntity(this.nextPk()));
        }
        ((AbstractDao<T, K>)this.dao).insertInTx(list);
        assertEquals(list.size(), ((AbstractDao<T, K>)this.dao).loadAll().size());
    }
    
    public void testLoadPk() {
        this.runLoadPkTest(0);
    }
    
    public void testLoadPkWithOffset() {
        this.runLoadPkTest(10);
    }
    
    public void testQuery() {
        ((AbstractDao<T, K>)this.dao).insert(this.createEntityWithRandomPk());
        final K nextPk = this.nextPk();
        ((AbstractDao<T, K>)this.dao).insert(this.createEntity(nextPk));
        ((AbstractDao<T, K>)this.dao).insert(this.createEntityWithRandomPk());
        final List<T> queryRaw = this.dao.queryRaw("WHERE " + this.dao.getPkColumns()[0] + "=?", nextPk.toString());
        assertEquals(1, queryRaw.size());
        assertEquals((Object)nextPk, (Object)this.daoAccess.getKey((T)queryRaw.get(0)));
    }
    
    public void testReadWithOffset() {
        final K nextPk = this.nextPk();
        ((AbstractDao<T, K>)this.dao).insert(this.createEntity(nextPk));
        final Cursor queryWithDummyColumnsInFront = this.queryWithDummyColumnsInFront(5, "42", nextPk);
        try {
            assertEquals((Object)nextPk, (Object)this.daoAccess.getKey(this.daoAccess.readEntity(queryWithDummyColumnsInFront, 5)));
        }
        finally {
            queryWithDummyColumnsInFront.close();
        }
    }
    
    public void testRowId() {
        assertTrue(((AbstractDao<T, K>)this.dao).insert(this.createEntityWithRandomPk()) != ((AbstractDao<T, K>)this.dao).insert(this.createEntityWithRandomPk()));
    }
    
    public void testUpdate() {
        this.dao.deleteAll();
        final T entityWithRandomPk = this.createEntityWithRandomPk();
        ((AbstractDao<T, K>)this.dao).insert(entityWithRandomPk);
        ((AbstractDao<T, K>)this.dao).update(entityWithRandomPk);
        assertEquals(1L, this.dao.count());
    }
}
